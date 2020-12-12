package lucky.job.center.util;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import lucky.job.center.core.monitor.JobFailMonitor;
import lucky.job.center.core.monitor.JobLogReportMonitor;
import lucky.job.center.core.monitor.JobLosedMonitor;
import lucky.job.center.core.monitor.JobRegistryMonitor;

public class SpringInjector {
    private static final Object lock = new Object();
    private static volatile Injector s_injector;

    private static Injector getInjector() {
        if (s_injector == null) {
            synchronized (lock) {
                if (s_injector == null) {
                    try {
                        s_injector = Guice.createInjector(new SpringModule());
                    } catch (Throwable ex) {
                        throw ex;
                    }
                }
            }
        }

        return s_injector;
    }

    public static <T> T getInstance(Class<T> clazz) {
        try {
            return getInjector().getInstance(clazz);
        } catch (Throwable ex) {
            throw ex;
        }
    }

    private static class SpringModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(JobLogReportMonitor.class).in(Singleton.class);
            bind(JobFailMonitor.class).in(Singleton.class);
            bind(JobLosedMonitor.class).in(Singleton.class);
            bind(JobRegistryMonitor.class).in(Singleton.class);
        }
    }
}