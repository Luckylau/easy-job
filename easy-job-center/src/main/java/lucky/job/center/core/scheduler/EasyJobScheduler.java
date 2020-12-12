package lucky.job.center.core.scheduler;

import lombok.extern.slf4j.Slf4j;
import lucky.job.center.core.lock.LockService;
import lucky.job.center.core.trigger.EasyJobTriggerPool;
import lucky.job.center.dao.EasyJobInfoMapper;
import lucky.job.center.entity.EasyJobInfo;
import lucky.job.center.entity.TriggerTypeEnum;
import lucky.job.center.util.EasyJobThreadFactory;
import lucky.job.center.util.NetUtil;
import lucky.job.core.cron.CronExpression;
import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author: luckylau
 * @Date: 2020/11/27 17:08
 * @Description:
 */
@Component
@Slf4j
public class EasyJobScheduler implements DisposableBean {

    public static final long PRE_READ_MS = 5000;
    private volatile static Map<Integer, List<Integer>> ringData = new ConcurrentHashMap<>();
    @Autowired
    private EasyJobInfoMapper easyJobInfoMapper;
    @Autowired
    private LockService lockService;
    @Value("${easy.job.triggerPool.fast.max}")
    private int triggerPoolFastMax;
    @Value("${easy.job.triggerPool.slow.max}")
    private int triggerPoolSlowMax;
    @Autowired
    private EasyJobTriggerPool easyJobTriggerPool;
    private ExecutorService scheduleThread;
    private ExecutorService ringThread;
    private volatile boolean scheduleThreadToStop = false;
    private volatile boolean ringThreadToStop = false;
    private String lockKey = NetUtil.getLocalIP();

    @PostConstruct
    private void init() {
        scheduleThread = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, new SynchronousQueue<>(), EasyJobThreadFactory.create("scheduleThread", true));
        scheduleThread.execute(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(5000 - System.currentTimeMillis() % 1000);
            } catch (InterruptedException e) {
                log.error("scheduleThread error", e);
            }
            log.info(">>>>>>>>>>>>>  easyJobScheduler success.");
            int preReadCount = (triggerPoolFastMax + triggerPoolSlowMax) * 20;
            while (!scheduleThreadToStop) {
                try {
                    lockService.acquireLock(lockKey);
                    long nowTime = System.currentTimeMillis();
                    List<EasyJobInfo> easyJobInfoList = easyJobInfoMapper.scheduleJobQuery(nowTime + PRE_READ_MS, preReadCount);
                    if (easyJobInfoList != null && !easyJobInfoList.isEmpty()) {
                        for (EasyJobInfo jobInfo : easyJobInfoList) {
                            if (jobInfo != null) {
                                if (nowTime > jobInfo.getTriggerNextTime() + PRE_READ_MS) {
                                    log.warn(">>>>>>>>>>> easy-job, schedule misfire, jobId = " + jobInfo.getId());
                                    refreshNextValidTime(jobInfo, new Date());
                                } else if (nowTime > jobInfo.getTriggerNextTime()) {
                                    easyJobTriggerPool.addTrigger(jobInfo.getId(), TriggerTypeEnum.CRON, -1, null, null, null);
                                    log.debug(">>>>>>>>>>> easy-job, schedule push trigger : jobId = " + jobInfo.getId());

                                    // 2、fresh next
                                    refreshNextValidTime(jobInfo, new Date());
                                    if (jobInfo.getTriggerStatus() == 1 && nowTime + PRE_READ_MS > jobInfo.getTriggerNextTime()) {

                                        // 1、make ring second
                                        int ringSecond = (int) ((jobInfo.getTriggerNextTime() / 1000) % 60);

                                        // 2、push time ring
                                        pushTimeRing(ringSecond, jobInfo.getId());

                                        // 3、fresh next
                                        refreshNextValidTime(jobInfo, new Date(jobInfo.getTriggerNextTime()));

                                    }

                                } else {
                                    int ringSecond = (int) ((jobInfo.getTriggerNextTime() / 1000) % 60);

                                    // 2、push time ring
                                    pushTimeRing(ringSecond, jobInfo.getId());

                                    // 3、fresh next
                                    refreshNextValidTime(jobInfo, new Date(jobInfo.getTriggerNextTime()));
                                }
                            }
                        }

                        for (EasyJobInfo easyJobInfo : easyJobInfoList) {
                            easyJobInfoMapper.updateById(easyJobInfo);
                        }


                    }


                    long cost = System.currentTimeMillis() - nowTime;
                    if (cost < 1000) {
                        try {
                            TimeUnit.MILLISECONDS.sleep(PRE_READ_MS - System.currentTimeMillis() % 1000);
                        } catch (InterruptedException ex) {
                            log.error("scheduleThread error", ex);
                        }
                    }

                } catch (PersistenceException e) {
                    log.warn("address: {} acquire Lock Failed", lockKey);
                    try {
                        TimeUnit.MILLISECONDS.sleep(1000 - System.currentTimeMillis() % 1000);
                    } catch (InterruptedException ex) {
                        log.error("scheduleThread error", ex);
                    }
                } catch (Exception e) {
                    log.error("scheduleThread error", e);
                } finally {
                    lockService.unLock(lockKey);
                }
            }


        });

        //时间轮
        ringThread = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, new SynchronousQueue<>(), EasyJobThreadFactory.create("ringThread", true));
        ringThread.execute(() -> {
            while (!ringThreadToStop) {

                try {
                    // second data
                    List<Integer> ringItemData = new ArrayList<>();
                    int nowSecond = Calendar.getInstance().get(Calendar.SECOND);   // 避免处理耗时太长，跨过刻度，向前校验一个刻度；
                    for (int i = 0; i < 2; i++) {
                        List<Integer> tmpData = ringData.remove((nowSecond + 60 - i) % 60);
                        if (tmpData != null) {
                            ringItemData.addAll(tmpData);
                        }
                    }

                    // ring trigger
                    log.debug(">>>>>>>>>>> easy-job, time-ring beat : " + nowSecond + " = " + ringItemData);
                    if (ringItemData.size() > 0) {
                        // do trigger
                        for (int jobId : ringItemData) {
                            // do trigger
                            easyJobTriggerPool.addTrigger(jobId, TriggerTypeEnum.CRON, -1, null, null, null);
                        }
                        // clear
                        ringItemData.clear();
                    }
                } catch (Exception e) {
                    if (!ringThreadToStop) {
                        log.error(">>>>>>>>>>> easy-job, JobScheduleHelper#ringThread error:{}", e);
                    }
                }

                // next second, align second
                try {
                    TimeUnit.MILLISECONDS.sleep(1000 - System.currentTimeMillis() % 1000);
                } catch (InterruptedException e) {
                    if (!ringThreadToStop) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
            log.info(">>>>>>>>>>> easy-job, JobScheduleHelper#ringThread stop");
        });
    }


    @Override
    public void destroy() {
        scheduleThreadToStop = true;
        ringThreadToStop = true;
    }

    private void refreshNextValidTime(EasyJobInfo jobInfo, Date fromTime) throws ParseException {
        Date nextValidTime = new CronExpression(jobInfo.getJobCron()).getNextValidTimeAfter(fromTime);
        if (nextValidTime != null) {
            jobInfo.setTriggerLastTime(jobInfo.getTriggerNextTime());
            jobInfo.setTriggerNextTime(nextValidTime.getTime());
        } else {
            jobInfo.setTriggerStatus(0);
            jobInfo.setTriggerLastTime(0L);
            jobInfo.setTriggerNextTime(0L);
        }
    }

    private void pushTimeRing(int ringSecond, int jobId) {
        // push async ring
        List<Integer> ringItemData = ringData.putIfAbsent(ringSecond, new ArrayList<>());
        if (ringItemData != null) {
            ringItemData.add(jobId);
        }
        log.debug(">>>>>>>>>>> easy-job, schedule push time-ring : " + ringSecond + " = " + ringItemData);
    }

}
