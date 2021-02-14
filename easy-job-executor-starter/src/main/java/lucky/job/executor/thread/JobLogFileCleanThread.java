package lucky.job.executor.thread;

import lucky.job.core.util.EasyJobFileAppender;
import lucky.job.core.util.EasyJobThreadFactory;
import lucky.job.executor.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: luckylau
 * @Date: 2021/1/6 19:20
 * @Description:
 */
public class JobLogFileCleanThread {

    private static Logger logger = LoggerFactory.getLogger(JobLogFileCleanThread.class);

    private static JobLogFileCleanThread instance = new JobLogFileCleanThread();
    private ExecutorService localThread;
    private volatile boolean toStop = false;

    public static JobLogFileCleanThread getInstance() {
        return instance;
    }

    public void start(final long logRetentionDays) {

        // limit min value
        if (logRetentionDays < 3) {
            return;
        }

        localThread = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, new SynchronousQueue<>(), EasyJobThreadFactory.create("jobLogFileClean", true));
        localThread.execute(() -> {
            while (!toStop) {
                try {
                    // clean log dir, over logRetentionDays
                    File[] childDirs = new File(EasyJobFileAppender.getLogPath()).listFiles();
                    if (childDirs != null && childDirs.length > 0) {

                        // today
                        Calendar todayCal = Calendar.getInstance();
                        todayCal.set(Calendar.HOUR_OF_DAY, 0);
                        todayCal.set(Calendar.MINUTE, 0);
                        todayCal.set(Calendar.SECOND, 0);
                        todayCal.set(Calendar.MILLISECOND, 0);

                        Date todayDate = todayCal.getTime();

                        for (File childFile : childDirs) {

                            // valid
                            if (!childFile.isDirectory()) {
                                continue;
                            }
                            if (childFile.getName().contains("-")) {
                                continue;
                            }

                            // file create date
                            Date logFileCreateDate = null;
                            try {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                logFileCreateDate = simpleDateFormat.parse(childFile.getName());
                            } catch (ParseException e) {
                                logger.error(e.getMessage(), e);
                            }
                            if (logFileCreateDate == null) {
                                continue;
                            }

                            if ((todayDate.getTime() - logFileCreateDate.getTime()) >= logRetentionDays * (24 * 60 * 60 * 1000)) {
                                FileUtil.deleteRecursively(childFile);
                            }

                        }
                    }

                } catch (Exception e) {
                    if (!toStop) {
                        logger.error(e.getMessage(), e);
                    }

                }

                try {
                    TimeUnit.DAYS.sleep(1);
                } catch (InterruptedException e) {
                    if (!toStop) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
            logger.info(">>>>>>>>>>> easy-job, executor JobLogFileCleanThread thread destory.");
        });

    }

    public void toStop() {
        toStop = true;

        if (localThread == null) {
            return;
        }
        localThread.shutdownNow();
    }
}
