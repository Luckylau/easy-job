package lucky.job.center.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lucky.job.center.dao.EasyJobLockMapper;
import lucky.job.center.entity.EasyJobLock;
import lucky.job.center.service.IEasyJobLockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author luckylau
 * @since 2020-11-25
 */
@Service
public class EasyJobLockServiceImpl implements IEasyJobLockService {

    @Autowired
    private EasyJobLockMapper easyJobLockMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void save(EasyJobLock easyJobLock) {
        easyJobLock.insert();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(String address) {
        QueryWrapper<EasyJobLock> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("address", address);
        easyJobLockMapper.delete(queryWrapper);

    }
}
