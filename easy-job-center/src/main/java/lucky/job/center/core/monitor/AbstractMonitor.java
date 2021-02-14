package lucky.job.center.core.monitor;

import lucky.job.center.core.AbstractLifeCycle;
import lucky.job.core.exception.LifeCycleException;

/**
 * @author: luckylau
 * @Date: 2020/11/25 16:13
 * @Description:
 */
public abstract class AbstractMonitor extends AbstractLifeCycle {


    @Override
    public void startup() throws LifeCycleException {
        super.startup();
        start();

    }

    @Override
    public void shutdown() throws LifeCycleException {
        super.shutdown();
        stop();

    }

    @Override
    public boolean isStarted() {
        return super.isStarted();
    }

    protected abstract void start();

    protected abstract void stop();
}
