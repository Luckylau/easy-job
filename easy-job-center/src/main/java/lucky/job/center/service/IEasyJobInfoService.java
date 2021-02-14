package lucky.job.center.service;

import com.baomidou.mybatisplus.extension.service.IService;
import lucky.job.center.entity.EasyJobInfo;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author luckylau
 * @since 2020-11-25
 */
public interface IEasyJobInfoService extends IService<EasyJobInfo> {

    int pageListCount(int offset,
                      int pagesize,
                      int jobGroup,
                      int triggerStatus,
                      String jobDesc,
                      String executorHandler,
                      String author);

}
