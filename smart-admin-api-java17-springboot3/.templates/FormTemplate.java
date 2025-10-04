/*
 * [模块名称][表单类型]
 *
 * @Author:    wangxiao
 * @Date:      [日期]
 * @Copyright  子午线高科智能科技 2025
 */
package [package];

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

/**
 * [模块名称][表单类型]
 *
 * @Author:    wangxiao
 * @Date:      [日期]
 * @Copyright  子午线高科智能科技 2025
 */
@Data
public class [ModuleName][FormType]Form {

    @Schema(description = "[字段说明]")
    @NotBlank(message = "[字段说明]不能为空")
    @Length(max = 200, message = "[字段说明]最多200字符")
    private String [field];

    @Schema(description = "[数字字段说明]")
    @NotNull(message = "[数字字段说明]不能为空")
    private Long [numberField];
}
