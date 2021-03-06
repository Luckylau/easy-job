package lucky.job.center.core;

import lucky.job.core.exception.LifeCycleException;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractLifeCycle implements LifeCycle {

    private final AtomicBoolean isStarted = new AtomicBoolean(false);

    @Override
    public void startup() throws LifeCycleException {
        if (isStarted.compareAndSet(false, true)) {
            return;
        }
        throw new LifeCycleException("this component has started");
    }

    @Override
    public void shutdown() throws LifeCycleException {
        if (isStarted.compareAndSet(true, false)) {
            return;
        }
        throw new LifeCycleException("this component has closed");
    }

    @Override
    public boolean isStarted() {
        return isStarted.get();
    }

    /**
     * ensure the component has been startup before providing service.
     */
    protected void ensureStarted() {
        if (!isStarted()) {
            throw new LifeCycleException(String.format(
                    "Component(%s) has not been started yet, please startup first!", getClass()
                            .getSimpleName()));
        }
    }
}