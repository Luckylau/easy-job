package lucky.job.center.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lucky.job.center.dao.EasyJobInfoMapper;
import lucky.job.center.entity.EasyJobInfo;
import lucky.job.center.service.IEasyJobInfoService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class EasyJobInfoServiceImpl extends ServiceImpl<EasyJobInfoMapper, EasyJobInfo> implements IEasyJobInfoService {

    @Autowired
    private EasyJobInfoMapper easyJobInfoMapper;

    @Override
    public int pageListCount(int offset, int pagesize, int jobGroup, int triggerStatus, String jobDesc, String executorHandler, String author) {
        return easyJobInfoMapper.pageListCount(offset, pagesize, jobGroup, triggerStatus, jobDesc, executorHandler, author);
    }
}
