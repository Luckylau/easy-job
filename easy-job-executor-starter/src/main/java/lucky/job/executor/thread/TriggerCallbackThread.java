package lucky.job.executor.thread;


import lucky.job.core.biz.AdminBiz;
import lucky.job.core.enums.RegistryConfig;
import lucky.job.core.log.EasyJobLogger;
import lucky.job.core.model.HandleCallbackParam;
import lucky.job.core.model.ReturnT;
import lucky.job.core.util.EasyJobFileAppender;
import lucky.job.core.util.EasyJobThreadFactory;
import lucky.job.core.util.JdkSerializeTool;
import lucky.job.executor.excutor.EasyJobExecutor;
import lucky.job.executor.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author: luckylau
 * @Date: 2021/1/6 19:08
 * @Description:
 */
public class TriggerCallbackThread {
    private static Logger logger = LoggerFactory.getLogger(TriggerCallbackThread.class);

    private static TriggerCallbackThread instance = new TriggerCallbackThread();
    private static String failCallbackFilePath = EasyJobFileAppender.getLogPath().concat(File.separator).concat("callbacklog").concat(File.separator);
    private static String failCallbackFileName = failCallbackFilePath.concat("xxl-job-callback-{x}").concat(".log");
    /**
     * job results callback queue
     */
    private LinkedBlockingQueue<HandleCallbackParam> callBackQueue = new LinkedBlockingQueue<>();
    /**
     * callback thread
     */
    private ExecutorService triggerCallbackThread;
    private ExecutorService triggerRetryCallbackThread;
    private volatile boolean toStop = false;

    public static TriggerCallbackThread getInstance() {
        return instance;
    }

    public static void pushCallBack(HandleCallbackParam callback) {
        getInstance().callBackQueue.add(callback);
        logger.debug(">>>>>>>>>>> easy-job, push callback request, logId:{}", callback.getLogId());
    }

    public void start() {

        // valid
        if (EasyJobExecutor.getAdminBizList() == null) {
            logger.warn(">>>>>>>>>>> easy-job, executor callback config fail, adminAddresses is null.");
            return;
        }

        // callback
        triggerCallbackThread = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, new SynchronousQueue<>(), EasyJobThreadFactory.create("triggerCallback", true));
        triggerCallbackThread.execute(() -> {
            // normal callback
            while (!toStop) {
                try {
                    HandleCallbackParam callback = getInstance().callBackQueue.take();
                    if (callback != null) {

                        // callback list param
                        List<HandleCallbackParam> callbackParamList = new ArrayList<>();
                        getInstance().callBackQueue.drainTo(callbackParamList);
                        callbackParamList.add(callback);

                        // callback, will retry if error
                        if (callbackParamList.size() > 0) {
                            doCallback(callbackParamList);
                        }
                    }
                } catch (Exception e) {
                    if (!toStop) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }

            // last callback
            try {
                List<HandleCallbackParam> callbackParamList = new ArrayList<HandleCallbackParam>();
                getInstance().callBackQueue.drainTo(callbackParamList);
                if (callbackParamList.size() > 0) {
                    doCallback(callbackParamList);
                }
            } catch (Exception e) {
                if (!toStop) {
                    logger.error(e.getMessage(), e);
                }
            }
            logger.info(">>>>>>>>>>> easy-job, executor callback thread destroy.");
        });


        // retry
        triggerRetryCallbackThread = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, new SynchronousQueue<>(), EasyJobThreadFactory.create("triggerRetryCallback", true));
        triggerRetryCallbackThread.execute(() -> {
            while (!toStop) {
                try {
                    retryFailCallbackFile();
                } catch (Exception e) {
                    if (!toStop) {
                        logger.error(e.getMessage(), e);
                    }

                }
                try {
                    TimeUnit.SECONDS.sleep(RegistryConfig.BEAT_TIMEOUT);
                } catch (InterruptedException e) {
                    if (!toStop) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
            logger.info(">>>>>>>>>>> easy-job, executor retry callback thread destroy.");
        });

    }

    public void toStop() {
        toStop = true;
        // stop callback, interrupt and wait
        triggerCallbackThread.shutdownNow();
        triggerRetryCallbackThread.shutdownNow();

    }


    // ---------------------- fail-callback file ----------------------

    /**
     * do callback, will retry if error
     *
     * @param callbackParamList
     */
    private void doCallback(List<HandleCallbackParam> callbackParamList) {
        boolean callbackRet = false;
        // callback, will retry if error
        for (AdminBiz adminBiz : EasyJobExecutor.getAdminBizList()) {
            try {
                ReturnT<String> callbackResult = adminBiz.callback(callbackParamList);
                if (callbackResult != null && ReturnT.SUCCESS_CODE == callbackResult.getCode()) {
                    callbackLog(callbackParamList, "<br>----------- xxl-job job callback finish.");
                    callbackRet = true;
                    break;
                } else {
                    callbackLog(callbackParamList, "<br>----------- xxl-job job callback fail, callbackResult:" + callbackResult);
                }
            } catch (Exception e) {
                callbackLog(callbackParamList, "<br>----------- xxl-job job callback error, errorMsg:" + e.getMessage());
            }
        }
        if (!callbackRet) {
            appendFailCallbackFile(callbackParamList);
        }
    }

    /**
     * callback log
     */
    private void callbackLog(List<HandleCallbackParam> callbackParamList, String logContent) {
        for (HandleCallbackParam callbackParam : callbackParamList) {
            String logFileName = EasyJobFileAppender.makeLogFileName(new Date(callbackParam.getLogDateTim()), callbackParam.getLogId());
            EasyJobFileAppender.contextHolder.set(logFileName);
            EasyJobLogger.log(logContent);
        }
    }

    private void appendFailCallbackFile(List<HandleCallbackParam> callbackParamList) {
        // valid
        if (callbackParamList == null || callbackParamList.size() == 0) {
            return;
        }

        // append file
        byte[] callbackParamList_bytes = JdkSerializeTool.serialize(callbackParamList);

        File callbackLogFile = new File(failCallbackFileName.replace("{x}", String.valueOf(System.currentTimeMillis())));
        if (callbackLogFile.exists()) {
            for (int i = 0; i < 100; i++) {
                callbackLogFile = new File(failCallbackFileName.replace("{x}", String.valueOf(System.currentTimeMillis()).concat("-").concat(String.valueOf(i))));
                if (!callbackLogFile.exists()) {
                    break;
                }
            }
        }
        FileUtil.writeFileContent(callbackLogFile, callbackParamList_bytes);
    }

    private void retryFailCallbackFile() {

        // valid
        File callbackLogPath = new File(failCallbackFilePath);
        if (!callbackLogPath.exists()) {
            return;
        }
        if (callbackLogPath.isFile()) {
            callbackLogPath.delete();
        }
        if (!(callbackLogPath.isDirectory() && callbackLogPath.list() != null && callbackLogPath.list().length > 0)) {
            return;
        }

        // load and clear file, retry
        for (File callbaclLogFile : callbackLogPath.listFiles()) {
            byte[] callbackParamList_bytes = FileUtil.readFileContent(callbaclLogFile);
            List<HandleCallbackParam> callbackParamList = (List<HandleCallbackParam>) JdkSerializeTool.deserialize(callbackParamList_bytes, List.class);

            callbaclLogFile.delete();
            doCallback(callbackParamList);
        }

    }

}
