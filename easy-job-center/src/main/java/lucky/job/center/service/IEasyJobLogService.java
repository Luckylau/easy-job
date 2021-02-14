package lucky.job.center.service;

import com.baomidou.mybatisplus.extension.service.IService;
import lucky.job.center.entity.EasyJobLog;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author luckylau
 * @since 2020-11-25
 */
public interface IEasyJobLogService extends IService<EasyJobLog> {

    List<EasyJobLog> pageList(int offset, int pagesize, int jobGroup, int jobId, Date triggerTimeStart, Date triggerTimeEnd, int logStatus);

    int pageListCount(int offset, int pagesize, int jobGroup, int jobId, Date triggerTimeStart, Date triggerTimeEnd, int logStatus);

    List<Long> findClearLogIds(int jobGroup, int jobId, Date clearBeforeTime, int clearBeforeNum, int pagesize);

    int clearLog(List<Long> logIds);

    int updateHandleInfo(EasyJobLog xxlJobLog);

}
