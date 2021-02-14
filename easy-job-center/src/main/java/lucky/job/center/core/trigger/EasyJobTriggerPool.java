package lucky.job.center.core.trigger;

import lombok.extern.slf4j.Slf4j;
import lucky.job.center.entity.TriggerTypeEnum;
import lucky.job.core.util.EasyJobThreadFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: luckylau
 * @Date: 2020/11/30 20:13
 * @Description:
 */
@Slf4j
@Service
public class EasyJobTriggerPool implements DisposableBean {
    private ThreadPoolExecutor fastTriggerPool;
    private ThreadPoolExecutor slowTriggerPool;

    @Value("${easy.job.triggerPool.fast.max}")
    private int triggerPoolFastMax;

    @Value("${easy.job.triggerPool.slow.max}")
    private int triggerPoolSlowMax;
    @Autowired
    private EasyJobTrigger easyJobTrigger;

    /**
     * ms > min
     */
    private volatile long minTim = System.currentTimeMillis() / 60000;

    private volatile ConcurrentMap<Integer, AtomicInteger> jobTimeoutCountMap = new ConcurrentHashMap<>();


    @PostConstruct
    private void init() {
        fastTriggerPool = new ThreadPoolExecutor(10, triggerPoolFastMax, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1000), EasyJobThreadFactory.create("fastTriggerPool", false));
        slowTriggerPool = new ThreadPoolExecutor(10, triggerPoolSlowMax, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1000), EasyJobThreadFactory.create("slowTriggerPool", false));
    }

    @Override
    public void destroy() {
        fastTriggerPool.shutdownNow();
        slowTriggerPool.shutdownNow();
    }

    public void addTrigger(final int jobId,
                           final TriggerTypeEnum triggerType,
                           final int failRetryCount,
                           final String executorShardingParam,
                           final String executorParam,
                           final String addressList) {

        // choose thread pool
        ThreadPoolExecutor triggerPool = fastTriggerPool;
        AtomicInteger jobTimeoutCount = jobTimeoutCountMap.get(jobId);
        // job-timeout 10 times in 1 min
        if (jobTimeoutCount != null && jobTimeoutCount.get() > 10) {
            triggerPool = slowTriggerPool;
        }

        // trigger
        triggerPool.execute(() -> {

            long start = System.currentTimeMillis();

            try {
                // do trigger
                easyJobTrigger.trigger(jobId, triggerType, failRetryCount, executorShardingParam, executorParam, addressList);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {
                // check timeout-count-map
                long minTimNow = System.currentTimeMillis() / 60000;
                if (minTim != minTimNow) {
                    minTim = minTimNow;
                    jobTimeoutCountMap.clear();
                }

                // incr timeout-count-map
                long cost = System.currentTimeMillis() - start;
                // ob-timeout threshold 500ms
                if (cost > 500) {
                    AtomicInteger timeoutCount = jobTimeoutCountMap.putIfAbsent(jobId, new AtomicInteger(1));
                    if (timeoutCount != null) {
                        timeoutCount.incrementAndGet();
                    }
                }

            }

        });
    }

}
