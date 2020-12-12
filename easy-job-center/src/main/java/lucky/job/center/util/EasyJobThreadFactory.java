package lucky.job.center.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by luckylau on 2018/7/27
 */
public class EasyJobThreadFactory implements ThreadFactory {

    private static final ThreadGroup THREAD_GROUP = new ThreadGroup("EasyJob");
    private final AtomicLong threadNumber = new AtomicLong(1);
    private final String namePrefix;
    private final Boolean daemon;

    public EasyJobThreadFactory(String namePrefix, Boolean daemon) {
        this.daemon = daemon;
        this.namePrefix = namePrefix;
    }

    public static ThreadFactory create(String namePrefix, boolean daemon) {
        return new EasyJobThreadFactory(namePrefix, daemon);
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(THREAD_GROUP, r, THREAD_GROUP.getName() + "-" + namePrefix + "-" + threadNumber.getAndIncrement());
        thread.setDaemon(daemon);
        if (thread.getPriority() != Thread.NORM_PRIORITY) {
            thread.setPriority(Thread.NORM_PRIORITY);
        }
        return thread;
    }
}
