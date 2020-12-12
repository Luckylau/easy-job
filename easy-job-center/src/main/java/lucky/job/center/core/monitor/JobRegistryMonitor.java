package lucky.job.center.core.monitor;

import lombok.extern.slf4j.Slf4j;
import lucky.job.center.dao.EasyJobGroupMapper;
import lucky.job.center.dao.EasyJobRegistryMapper;
import lucky.job.center.util.EasyJobThreadFactory;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: luckylau
 * @Date: 2020/11/20 19:38
 * @Description:
 */
@Slf4j
public class JobRegistryMonitor extends AbstractMonitor {

    private final static int BEAT_INTERNAL = 30;
    private ScheduledThreadPoolExecutor executor;
    private EasyJobGroupMapper easyJobGroupMapper;

    private EasyJobRegistryMapper easyJobRegistryMapper;

    public JobRegistryMonitor(EasyJobGroupMapper easyJobGroupMapper, EasyJobRegistryMapper easyJobRegistryMapper) {
        this.easyJobGroupMapper = easyJobGroupMapper;
        this.easyJobRegistryMapper = easyJobRegistryMapper;
    }

    @Override
    protected void start() {
        executor = new ScheduledThreadPoolExecutor(1, EasyJobThreadFactory.create("jobRegistryMonitor", true));
        executor.scheduleWithFixedDelay(new JobRegistry(), 0, BEAT_INTERNAL, TimeUnit.SECONDS);
    }

    @Override
    protected void stop() {
        executor.shutdown();
    }

    private class JobRegistry implements Runnable {

        @Override
        public void run() {
            //todo
            log.info("JobRegistryMonitor start");
        }
    }
}
