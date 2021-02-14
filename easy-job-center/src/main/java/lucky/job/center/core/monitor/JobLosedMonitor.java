package lucky.job.center.core.monitor;

import lombok.extern.slf4j.Slf4j;
import lucky.job.center.dao.EasyJobLogMapper;
import lucky.job.core.util.EasyJobThreadFactory;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: luckylau
 * @Date: 2020/11/25 16:24
 * @Description:
 */
@Slf4j
public class JobLosedMonitor extends AbstractMonitor {

    private final static int INTERNAL = 60;
    private ScheduledThreadPoolExecutor executor;
    private EasyJobLogMapper easyJobLogMapper;

    public JobLosedMonitor(EasyJobLogMapper easyJobLogMapper) {
        this.easyJobLogMapper = easyJobLogMapper;
    }

    @Override
    protected void start() {
        executor = new ScheduledThreadPoolExecutor(1, EasyJobThreadFactory.create("JobLosedMonitor", true));
        executor.scheduleWithFixedDelay(new JobLosed(), 0, INTERNAL, TimeUnit.SECONDS);
    }

    @Override
    protected void stop() {
        executor.shutdown();
    }

    private class JobLosed implements Runnable {

        @Override
        public void run() {
            log.info("JobLosedMonitor start");

        }
    }
}
