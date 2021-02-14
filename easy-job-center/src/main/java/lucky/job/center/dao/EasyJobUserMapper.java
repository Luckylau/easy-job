package lucky.job.center.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lucky.job.center.entity.EasyJobUser;
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
public interface EasyJobUserMapper extends BaseMapper<EasyJobUser> {
    List<EasyJobUser> pageList(@Param("offset") int offset,
                               @Param("pagesize") int pagesize,
                               @Param("username") String username,
                               @Param("role") int role);

    int pageListCount(@Param("offset") int offset,
                      @Param("pagesize") int pagesize,
                      @Param("username") String username,
                      @Param("role") int role);
}
