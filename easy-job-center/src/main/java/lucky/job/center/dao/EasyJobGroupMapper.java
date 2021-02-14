package lucky.job.center.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lucky.job.center.entity.EasyJobGroup;
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
public interface EasyJobGroupMapper extends BaseMapper<EasyJobGroup> {

    List<EasyJobGroup> pageList(@Param("offset") int offset,
                                @Param("pagesize") int pagesize,
                                @Param("appname") String appname,
                                @Param("title") String title);


    int pageListCount(@Param("offset") int offset,
                      @Param("pagesize") int pagesize,
                      @Param("appname") String appname,
                      @Param("title") String title);
}
