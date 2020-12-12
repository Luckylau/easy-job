package lucky.job.center.core.monitor;

import lombok.extern.slf4j.Slf4j;
import lucky.job.center.dao.EasyJobInfoMapper;
import lucky.job.center.dao.EasyJobLogMapper;
import lucky.job.center.util.EasyJobThreadFactory;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: luckylau
 * @Date: 2020/11/25 16:03
 * @Description:
 */
@Slf4j
public class JobFailMonitor extends AbstractMonitor {

    private final static int INTERNAL = 10;
    private ScheduledThreadPoolExecutor executor;
    private EasyJobLogMapper easyJobLogMapper;

    private EasyJobInfoMapper easyJobInfoMapper;

    public JobFailMonitor(EasyJobLogMapper easyJobLogMapper, EasyJobInfoMapper easyJobInfoMapper) {
        this.easyJobLogMapper = easyJobLogMapper;
        this.easyJobInfoMapper = easyJobInfoMapper;
    }

    @Override
    protected void start() {
        executor = new ScheduledThreadPoolExecutor(1, EasyJobThreadFactory.create("jobRegistryMonitor", true));
        executor.scheduleWithFixedDelay(new JobFail(), 0, INTERNAL, TimeUnit.SECONDS);
    }

    @Override
    protected void stop() {
        executor.shutdown();
    }

    private class JobFail implements Runnable {

        @Override
        public void run() {
            //todo
            log.info("JobFailMonitor start");
        }
    }
}
