/*
 * [模块名称]VO
 *
 * @Author:    wangxiao
 * @Date:      [日期]
 * @Copyright  子午线高科智能科技 2025
 */
package [package];

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * [模块名称]VO
 *
 * @Author:    wangxiao
 * @Date:      [日期]
 * @Copyright  子午线高科智能科技 2025
 */
@Data
public class [ModuleName]VO {

    @Schema(description = "[主键说明]")
    private Long [id];

    @Schema(description = "[字段说明]")
    private String [field];

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
