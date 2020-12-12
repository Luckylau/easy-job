package lucky.job.center.core;

import lucky.job.center.exception.LifeCycleException;

public interface LifeCycle {

    void startup() throws LifeCycleException;

    void shutdown() throws LifeCycleException;

    boolean isStarted();
}