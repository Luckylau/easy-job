package lucky.job.center.service;

import com.baomidou.mybatisplus.extension.service.IService;
import lucky.job.center.entity.EasyJobLogGlue;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author luckylau
 * @since 2020-11-25
 */
public interface IEasyJobLogGlueService extends IService<EasyJobLogGlue> {

    int removeOld(@Param("jobId") int jobId, @Param("limit") int limit);

}
