package lucky.job.center.core.lock;

import lucky.job.center.entity.EasyJobLock;
import lucky.job.center.service.IEasyJobLockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: luckylau
 * @Date: 2020/11/27 20:05
 * @Description:
 */
@Service
public class LockService {
    private static String LOCK_NAME = "schedule_lock";
    @Autowired
    private IEasyJobLockService easyJobLockService;

    /**
     * Ip:port
     *
     * @param address
     */
    public void acquireLock(String address) {
        EasyJobLock easyJobLock = new EasyJobLock();
        easyJobLock.setAddress(address);
        easyJobLock.setLockName(LOCK_NAME);
        easyJobLockService.save(easyJobLock);
    }

    public void unLock(String address) {
        easyJobLockService.delete(address);
    }
}
