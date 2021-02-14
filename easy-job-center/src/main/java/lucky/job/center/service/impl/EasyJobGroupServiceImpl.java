package lucky.job.center.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lucky.job.center.dao.EasyJobGroupMapper;
import lucky.job.center.entity.EasyJobGroup;
import lucky.job.center.service.IEasyJobGroupService;
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
public class EasyJobGroupServiceImpl extends ServiceImpl<EasyJobGroupMapper, EasyJobGroup> implements IEasyJobGroupService {
    @Autowired
    private EasyJobGroupMapper easyJobGroupMapper;

    @Override
    public List<EasyJobGroup> pageList(int offset, int pagesize, String appname, String title) {
        return easyJobGroupMapper.pageList(offset, pagesize, appname, title);
    }

    @Override
    public int pageListCount(int offset, int pagesize, String appname, String title) {
        return 0;
    }
}
