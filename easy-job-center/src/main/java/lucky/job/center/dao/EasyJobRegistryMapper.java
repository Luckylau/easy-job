package lucky.job.center.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lucky.job.center.entity.EasyJobRegistry;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author luckylau
 * @since 2020-11-25
 */
public interface EasyJobRegistryMapper extends BaseMapper<EasyJobRegistry> {

    List<EasyJobRegistry> findAll(@Param("timeout") int timeout,
                                  @Param("nowTime") Date nowTime);

    int registryUpdate(@Param("registryGroup") String registryGroup,
                       @Param("registryKey") String registryKey,
                       @Param("registryValue") String registryValue,
                       @Param("updateTime") Date updateTime);

    int registrySave(@Param("registryGroup") String registryGroup,
                     @Param("registryKey") String registryKey,
                     @Param("registryValue") String registryValue,
                     @Param("updateTime") Date updateTime);

    int registryDelete(@Param("registryGroup") String registryGroup,
                       @Param("registryKey") String registryKey,
                       @Param("registryValue") String registryValue);


}
