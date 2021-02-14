package lucky.job.center.core.monitor;

import lombok.extern.slf4j.Slf4j;
import lucky.job.center.dao.EasyJobLogMapper;
import lucky.job.center.dao.EasyJobLogReportMapper;
import lucky.job.core.util.EasyJobThreadFactory;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: luckylau
 * @Date: 2020/11/25 17:01
 * @Description:
 */
@Slf4j
public class JobLogReportMonitor extends AbstractMonitor {

    private final static int INTERNAL = 1;

    private ScheduledThreadPoolExecutor executor;

    private EasyJobLogMapper easyJobLogMapper;

    private EasyJobLogReportMapper easyJobLogReportMapper;

    public JobLogReportMonitor(EasyJobLogMapper easyJobLogMapper, EasyJobLogReportMapper easyJobLogReportMapper) {
        this.easyJobLogMapper = easyJobLogMapper;
        this.easyJobLogReportMapper = easyJobLogReportMapper;
    }

    @Override
    protected void start() {
        executor = new ScheduledThreadPoolExecutor(1, EasyJobThreadFactory.create("JobLogReportMonitor", true));
        executor.scheduleWithFixedDelay(new JobLogReport(), 0, INTERNAL, TimeUnit.SECONDS);
    }

    @Override
    protected void stop() {
        executor.shutdown();
    }

    private class JobLogReport implements Runnable {

        @Override
        public void run() {
            log.info("JobLogReportMonitor start");

        }
    }
}
