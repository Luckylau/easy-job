package lucky.job.center.core;

import lombok.extern.slf4j.Slf4j;
import lucky.job.center.exception.LifeCycleException;
import lucky.job.center.util.EasyJobThreadFactory;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: luckylau
 * @Date: 2020/11/20 19:38
 * @Description:
 */
@Slf4j
public class JobRegistryMonitor extends AbstractLifeCycle {

    private final static int BEAT_TIMEOUT = 30;
    private ScheduledThreadPoolExecutor executor;

    @Override
    public void startup() throws LifeCycleException {
        super.startup();
        start();
    }

    @Override
    public void shutdown() throws LifeCycleException {
        super.shutdown();
        executor.shutdown();
    }

    @Override
    public boolean isStarted() {
        return super.isStarted();
    }

    private void start() {
        executor = new ScheduledThreadPoolExecutor(1, EasyJobThreadFactory.create("jobRegistryMonitor", true));
        executor.scheduleWithFixedDelay(new JobRegistry(), 0, BEAT_TIMEOUT, TimeUnit.SECONDS);
    }

    private class JobRegistry implements Runnable {

        @Override
        public void run() {

        }
    }
}
