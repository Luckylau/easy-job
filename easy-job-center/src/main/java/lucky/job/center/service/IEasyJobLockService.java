package lucky.job.center.service;

import lucky.job.center.entity.EasyJobLock;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author luckylau
 * @since 2020-11-25
 */
public interface IEasyJobLockService {

    void save(EasyJobLock easyJobLock);

    void delete(String address);

}
