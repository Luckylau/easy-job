package lucky.job.executor.excutor;

import lucky.job.core.biz.AdminBiz;
import lucky.job.core.handler.IJobHandler;
import lucky.job.core.util.EasyJobFileAppender;
import lucky.job.core.util.NetUtil;
import lucky.job.executor.biz.AdminBizClient;
import lucky.job.executor.server.EmbedServer;
import lucky.job.executor.thread.JobLogFileCleanThread;
import lucky.job.executor.thread.JobThread;
import lucky.job.executor.thread.TriggerCallbackThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author: luckylau
 * @Date: 2021/1/6 19:08
 * @Description:
 */
public class EasyJobExecutor {
    private static final Logger logger = LoggerFactory.getLogger(EasyJobExecutor.class);
    private static List<AdminBiz> adminBizList;
    // ---------------------- job handler repository ----------------------
    private static ConcurrentMap<String, IJobHandler> jobHandlerRepository = new ConcurrentHashMap<String, IJobHandler>();
    // ---------------------- job thread repository ----------------------
    private static ConcurrentMap<Integer, JobThread> jobThreadRepository = new ConcurrentHashMap<Integer, JobThread>();
    private String centerAddresses;
    private String centerAccessToken;
    private String appname;
    private String registerAddress;
    private String ip;
    private int port;
    private String logPath;
    private Long logRetentionDays;
    // ---------------------- executor-server (rpc provider) ----------------------
    private EmbedServer embedServer = null;

    public static List<AdminBiz> getAdminBizList() {
        return adminBizList;
    }

    public static IJobHandler registJobHandler(String name, IJobHandler jobHandler) {
        logger.info(">>>>>>>>>>> easy-job register jobhandler success, name:{}, jobHandler:{}", name, jobHandler);
        return jobHandlerRepository.put(name, jobHandler);
    }

    public static IJobHandler loadJobHandler(String name) {
        return jobHandlerRepository.get(name);
    }

    public static JobThread registJobThread(int jobId, IJobHandler handler, String removeOldReason) {
        JobThread newJobThread = new JobThread(jobId, handler);
        newJobThread.start();
        logger.info(">>>>>>>>>>> easy-job regist JobThread success, jobId:{}, handler:{}", new Object[]{jobId, handler});
        // putIfAbsent | oh my god, map's put method return the old value!!!
        JobThread oldJobThread = jobThreadRepository.put(jobId, newJobThread);
        if (oldJobThread != null) {
            oldJobThread.toStop(removeOldReason);
            oldJobThread.interrupt();
        }

        return newJobThread;
    }

    public static JobThread removeJobThread(int jobId, String removeOldReason) {
        JobThread oldJobThread = jobThreadRepository.remove(jobId);
        if (oldJobThread != null) {
            oldJobThread.toStop(removeOldReason);
            oldJobThread.interrupt();

            return oldJobThread;
        }
        return null;
    }

    public static JobThread loadJobThread(int jobId) {
        return jobThreadRepository.get(jobId);
    }

    public String getCenterAddresses() {
        return centerAddresses;
    }

    public void setCenterAddresses(String centerAddresses) {
        this.centerAddresses = centerAddresses;
    }

    public String getCenterAccessToken() {
        return centerAccessToken;
    }

    public void setCenterAccessToken(String centerAccessToken) {
        this.centerAccessToken = centerAccessToken;
    }

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public String getRegisterAddress() {
        return registerAddress;
    }

    public void setRegisterAddress(String registerAddress) {
        this.registerAddress = registerAddress;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getLogPath() {
        return logPath;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    public Long getLogRetentionDays() {
        return logRetentionDays;
    }

    public void setLogRetentionDays(Long logRetentionDays) {
        this.logRetentionDays = logRetentionDays;
    }

    public void start() throws Exception {

        // init logpath
        EasyJobFileAppender.initLogPath(logPath);

        // init invoker, admin-client
        initAdminBizList(centerAddresses, centerAccessToken);


        // init JobLogFileCleanThread
        JobLogFileCleanThread.getInstance().start(logRetentionDays);

        // init TriggerCallbackThread
        TriggerCallbackThread.getInstance().start();

        // init executor-server
        initEmbedServer(registerAddress, ip, port, appname, centerAccessToken);
    }

    public void destroy() {
        // destory executor-server
        stopEmbedServer();

        // destory jobThreadRepository
        if (jobThreadRepository.size() > 0) {
            for (Map.Entry<Integer, JobThread> item : jobThreadRepository.entrySet()) {
                JobThread oldJobThread = removeJobThread(item.getKey(), "web container destroy and kill the job.");
                // wait for job thread push result to callback queue
                if (oldJobThread != null) {
                    try {
                        oldJobThread.join();
                    } catch (InterruptedException e) {
                        logger.error(">>>>>>>>>>> easy-job, JobThread destroy(join) error, jobId:{}", item.getKey(), e);
                    }
                }
            }
            jobThreadRepository.clear();
        }
        jobHandlerRepository.clear();


        // destory JobLogFileCleanThread
        JobLogFileCleanThread.getInstance().toStop();

        // destory TriggerCallbackThread
        TriggerCallbackThread.getInstance().toStop();

    }

    private void initAdminBizList(String adminAddresses, String accessToken) throws Exception {
        if (adminAddresses != null && adminAddresses.trim().length() > 0) {
            for (String address : adminAddresses.trim().split(",")) {
                if (address != null && address.trim().length() > 0) {

                    AdminBiz adminBiz = new AdminBizClient(address.trim(), accessToken);

                    if (adminBizList == null) {
                        adminBizList = new ArrayList<AdminBiz>();
                    }
                    adminBizList.add(adminBiz);
                }
            }
        }
    }

    private void initEmbedServer(String address, String ip, int port, String appname, String accessToken) throws Exception {

        // fill ip port
        port = port > 0 ? port : NetUtil.findAvailablePort(9999);
        ip = (ip != null && ip.trim().length() > 0) ? ip : NetUtil.getLocalIP();

        // generate address
        if (address == null || address.trim().length() == 0) {
            // registry-address：default use address to registry , otherwise use ip:port if address is null
            String ipPortAddress = NetUtil.getIpPort(ip, port);
            address = "http://{ip_port}/".replace("{ip_port}", ipPortAddress);
        }

        // start
        embedServer = new EmbedServer();
        embedServer.start(address, port, appname, accessToken);
    }

    private void stopEmbedServer() {
        // stop provider factory
        try {
            embedServer.stop();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }


}
