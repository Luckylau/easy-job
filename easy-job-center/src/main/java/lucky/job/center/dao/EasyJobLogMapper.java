package lucky.job.center.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lucky.job.center.entity.EasyJobLog;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author luckylau
 * @since 2020-11-25
 */
public interface EasyJobLogMapper extends BaseMapper<EasyJobLog> {

    /**
     * @param offset
     * @param pagesize
     * @param jobGroup
     * @param jobId
     * @param triggerTimeStart
     * @param triggerTimeEnd
     * @param logStatus
     * @return
     */
    List<EasyJobLog> pageList(@Param("offset") int offset,
                              @Param("pagesize") int pagesize,
                              @Param("jobGroup") int jobGroup,
                              @Param("jobId") int jobId,
                              @Param("triggerTimeStart") Date triggerTimeStart,
                              @Param("triggerTimeEnd") Date triggerTimeEnd,
                              @Param("logStatus") int logStatus);


    int pageListCount(@Param("offset") int offset,
                      @Param("pagesize") int pagesize,
                      @Param("jobGroup") int jobGroup,
                      @Param("jobId") int jobId,
                      @Param("triggerTimeStart") Date triggerTimeStart,
                      @Param("triggerTimeEnd") Date triggerTimeEnd,
                      @Param("logStatus") int logStatus);

    List<Long> findClearLogIds(@Param("jobGroup") int jobGroup,
                               @Param("jobId") int jobId,
                               @Param("clearBeforeTime") Date clearBeforeTime,
                               @Param("clearBeforeNum") int clearBeforeNum,
                               @Param("pagesize") int pagesize);


    int clearLog(@Param("logIds") List<Long> logIds);

    int updateHandleInfo(EasyJobLog easyJobLog);
}
