/*
 * [模块名称]数据访问
 *
 * @Author:    wangxiao
 * @Date:      [日期]
 * @Copyright  子午线高科智能科技 2025
 */
package [package];

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * [模块名称]数据访问
 *
 * @Author:    wangxiao
 * @Date:      [日期]
 * @Copyright  子午线高科智能科技 2025
 */
@Mapper
public interface [ModuleName]Dao extends BaseMapper<[ModuleName]Entity> {

    /**
     * 分页查询 @author wangxiao
     */
    List<[ModuleName]VO> queryPage(@Param("page") Page<?> page,
                                    @Param("queryForm") [ModuleName]QueryForm queryForm);
}
