package lucky.job.center.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lucky.job.center.entity.EasyJobInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author luckylau
 * @since 2020-11-25
 */
public interface EasyJobInfoMapper extends BaseMapper<EasyJobInfo> {

    /**
     * 下一时刻的任务
     *
     * @param maxNextTime
     * @param pageSize
     * @return
     */
    List<EasyJobInfo> scheduleJobQuery(@Param("maxNextTime") long maxNextTime, @Param("pageSize") int pageSize);
}
