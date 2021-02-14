package lucky.job.center.service;

import com.baomidou.mybatisplus.extension.service.IService;
import lucky.job.center.entity.EasyJobGroup;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author luckylau
 * @since 2020-11-25
 */
public interface IEasyJobGroupService extends IService<EasyJobGroup> {

    List<EasyJobGroup> pageList(int offset, int pagesize, String appname, String title);

    int pageListCount(int offset, int pagesize, String appname, String title);

}
