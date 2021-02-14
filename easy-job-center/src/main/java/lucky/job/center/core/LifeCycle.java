package lucky.job.center.core;

import lucky.job.core.exception.LifeCycleException;

public interface LifeCycle {

    void startup() throws LifeCycleException;

    void shutdown() throws LifeCycleException;

    boolean isStarted();
}