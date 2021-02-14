package lucky.job.center.service;

import com.baomidou.mybatisplus.extension.service.IService;
import lucky.job.center.entity.EasyJobRegistry;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author luckylau
 * @since 2020-11-25
 */
public interface IEasyJobRegistryService extends IService<EasyJobRegistry> {

    List<EasyJobRegistry> findAll(int timeout, Date nowTime);

    int registryUpdate(String registryGroup,
                       String registryKey,
                       String registryValue,
                       Date updateTime);

    int registrySave(String registryGroup,
                     String registryKey,
                     String registryValue,
                     Date updateTime);

    int registryDelete(String registryGroup,
                       String registryKey,
                       String registryValue);



}
