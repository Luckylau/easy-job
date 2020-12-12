package lucky.job.center.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lucky.job.center.dao.EasyJobUserMapper;
import lucky.job.center.entity.EasyJobUser;
import lucky.job.center.service.IEasyJobUserService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author luckylau
 * @since 2020-11-25
 */
@Service
public class EasyJobUserServiceImpl extends ServiceImpl<EasyJobUserMapper, EasyJobUser> implements IEasyJobUserService {

}
