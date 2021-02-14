package lucky.job.center.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lucky.job.center.dao.EasyJobRegistryMapper;
import lucky.job.center.entity.EasyJobRegistry;
import lucky.job.center.service.IEasyJobRegistryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author luckylau
 * @since 2020-11-25
 */
@Service
public class EasyJobRegistryServiceImpl extends ServiceImpl<EasyJobRegistryMapper, EasyJobRegistry> implements IEasyJobRegistryService {

    @Autowired
    private EasyJobRegistryMapper easyJobRegistryMapper;

    @Override
    public List<EasyJobRegistry> findAll(int timeout, Date nowTime) {
        return easyJobRegistryMapper.findAll(timeout, nowTime);
    }

    @Override
    public int registryUpdate(String registryGroup, String registryKey, String registryValue, Date updateTime) {
        return easyJobRegistryMapper.registryUpdate(registryGroup, registryKey, registryValue, updateTime);
    }

    @Override
    public int registrySave(String registryGroup, String registryKey, String registryValue, Date updateTime) {
        return easyJobRegistryMapper.registrySave(registryGroup, registryKey, registryValue, updateTime);
    }

    @Override
    public int registryDelete(String registryGroup, String registryKey, String registryValue) {
        return easyJobRegistryMapper.registryDelete(registryGroup, registryKey, registryValue);
    }
}
