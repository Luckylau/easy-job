package lucky.job.center.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lucky.job.center.dao.EasyJobLogMapper;
import lucky.job.center.entity.EasyJobLog;
import lucky.job.center.service.IEasyJobLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
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
public class EasyJobLogServiceImpl extends ServiceImpl<EasyJobLogMapper, EasyJobLog> implements IEasyJobLogService {

    @Autowired
    private EasyJobLogMapper easyJobLogMapper;

    @Override
    public List<EasyJobLog> pageList(int offset, int pagesize, int jobGroup, int jobId, Date triggerTimeStart, Date triggerTimeEnd, int logStatus) {
        return easyJobLogMapper.pageList(offset, pagesize, jobGroup, jobId, triggerTimeStart, triggerTimeEnd, logStatus);
    }

    @Override
    public int pageListCount(int offset, int pagesize, int jobGroup, int jobId, Date triggerTimeStart, Date triggerTimeEnd, int logStatus) {
        return easyJobLogMapper.pageListCount(offset, pagesize, jobGroup, jobId, triggerTimeStart, triggerTimeEnd, logStatus);
    }

    @Override
    public List<Long> findClearLogIds(int jobGroup, int jobId, Date clearBeforeTime, int clearBeforeNum, int pagesize) {
        return easyJobLogMapper.findClearLogIds(jobGroup, jobId, clearBeforeTime, clearBeforeNum, pagesize);
    }

    @Override
    public int clearLog(List<Long> logIds) {
        return easyJobLogMapper.clearLog(logIds);
    }

    @Override
    public int updateHandleInfo(EasyJobLog easyJobLog) {
        return easyJobLogMapper.updateHandleInfo(easyJobLog);
    }
}
