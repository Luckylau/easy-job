package lucky.job.center.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lucky.job.center.dao.EasyJobUserMapper;
import lucky.job.center.entity.EasyJobUser;
import lucky.job.center.service.IEasyJobUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
public class EasyJobUserServiceImpl extends ServiceImpl<EasyJobUserMapper, EasyJobUser> implements IEasyJobUserService {

    @Autowired
    private EasyJobUserMapper easyJobUserMapper;

    @Override
    public List<EasyJobUser> pageList(int offset, int pagesize, String username, int role) {
        return easyJobUserMapper.pageList(offset, pagesize, username, role);
    }

    @Override
    public int pageListCount(int offset, int pagesize, String username, int role) {
        return easyJobUserMapper.pageListCount(offset, pagesize, username, role);
    }
}
