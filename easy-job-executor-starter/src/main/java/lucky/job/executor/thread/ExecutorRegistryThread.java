package lucky.job.executor.thread;

import lucky.job.core.biz.AdminBiz;
import lucky.job.core.enums.RegistryConfig;
import lucky.job.core.model.RegistryParam;
import lucky.job.core.model.ReturnT;
import lucky.job.core.util.EasyJobThreadFactory;
import lucky.job.executor.excutor.EasyJobExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: luckylau
 * @Date: 2021/1/11 10:01
 * @Description:
 */
public class ExecutorRegistryThread {

    private static Logger logger = LoggerFactory.getLogger(ExecutorRegistryThread.class);

    private static ExecutorRegistryThread instance = new ExecutorRegistryThread();
    private ExecutorService registryThread;
    private volatile boolean toStop = false;

    public static ExecutorRegistryThread getInstance() {
        return instance;
    }

    public void start(final String appname, final String address) {

        // valid
        if (appname == null || appname.trim().length() == 0) {
            logger.warn(">>>>>>>>>>> easy-job, executor registry config fail, appname is null.");
            return;
        }
        if (EasyJobExecutor.getAdminBizList() == null) {
            logger.warn(">>>>>>>>>>> easy-job, executor registry config fail, adminAddresses is null.");
            return;
        }

        registryThread = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, new SynchronousQueue<>(), EasyJobThreadFactory.create("executorRegistry", true));
        registryThread.execute(() -> {
            // registry
            while (!toStop) {
                try {
                    RegistryParam registryParam = new RegistryParam(RegistryConfig.RegistryType.EXECUTOR.name(), appname, address);
                    for (AdminBiz adminBiz : EasyJobExecutor.getAdminBizList()) {
                        try {
                            ReturnT<String> registryResult = adminBiz.registry(registryParam);
                            if (registryResult != null && ReturnT.SUCCESS_CODE == registryResult.getCode()) {
                                registryResult = ReturnT.SUCCESS;
                                logger.debug(">>>>>>>>>>> easy-job registry success, registryParam:{}, registryResult:{}", new Object[]{registryParam, registryResult});
                                break;
                            } else {
                                logger.info(">>>>>>>>>>> easy-job registry fail, registryParam:{}, registryResult:{}", new Object[]{registryParam, registryResult});
                            }
                        } catch (Exception e) {
                            logger.info(">>>>>>>>>>> easy-job registry error, registryParam:{}", registryParam, e);
                        }

                    }
                } catch (Exception e) {
                    if (!toStop) {
                        logger.error(e.getMessage(), e);
                    }

                }

                try {
                    if (!toStop) {
                        TimeUnit.SECONDS.sleep(RegistryConfig.BEAT_TIMEOUT);
                    }
                } catch (InterruptedException e) {
                    logger.warn(">>>>>>>>>>> easy-job, executor registry thread interrupted, error msg:{}", e.getMessage());

                }
            }

            // registry remove
            try {
                RegistryParam registryParam = new RegistryParam(RegistryConfig.RegistryType.EXECUTOR.name(), appname, address);
                for (AdminBiz adminBiz : EasyJobExecutor.getAdminBizList()) {
                    try {
                        ReturnT<String> registryResult = adminBiz.registryRemove(registryParam);
                        if (registryResult != null && ReturnT.SUCCESS_CODE == registryResult.getCode()) {
                            registryResult = ReturnT.SUCCESS;
                            logger.info(">>>>>>>>>>> easy-job registry-remove success, registryParam:{}, registryResult:{}", new Object[]{registryParam, registryResult});
                            break;
                        } else {
                            logger.info(">>>>>>>>>>> easy-job registry-remove fail, registryParam:{}, registryResult:{}", new Object[]{registryParam, registryResult});
                        }
                    } catch (Exception e) {
                        if (!toStop) {
                            logger.info(">>>>>>>>>>> easy-job registry-remove error, registryParam:{}", registryParam, e);
                        }

                    }

                }
            } catch (Exception e) {
                if (!toStop) {
                    logger.error(e.getMessage(), e);
                }
            }
            logger.info(">>>>>>>>>>> easy-job, executor registry thread destroy.");
        });
    }

    public void toStop() {
        toStop = true;
        // interrupt and wait
        registryThread.shutdown();
    }
}
