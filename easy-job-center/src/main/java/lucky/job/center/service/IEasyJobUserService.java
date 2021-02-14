package lucky.job.center.service;

import com.baomidou.mybatisplus.extension.service.IService;
import lucky.job.center.entity.EasyJobUser;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author luckylau
 * @since 2020-11-25
 */
public interface IEasyJobUserService extends IService<EasyJobUser> {

    List<EasyJobUser> pageList(int offset, int pagesize, String username, int role);

    int pageListCount(int offset, int pagesize, String username, int role);

}
