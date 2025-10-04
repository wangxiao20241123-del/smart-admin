/*
 * [模块名称]实体
 *
 * @Author:    wangxiao
 * @Date:      [日期]
 * @Copyright  子午线高科智能科技 2025
 */
package [package];

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import net.lab1024.sa.base.common.domain.BaseEntity;

/**
 * [模块名称]实体
 *
 * @Author:    wangxiao
 * @Date:      [日期]
 * @Copyright  子午线高科智能科技 2025
 */
@Data
@TableName("t_[table_name]")
public class [ModuleName]Entity extends BaseEntity {

    /**
     * [主键说明]
     */
    @TableId(type = IdType.AUTO)
    private Long [id];

    /**
     * [字段说明]
     */
    private String [field];
}
