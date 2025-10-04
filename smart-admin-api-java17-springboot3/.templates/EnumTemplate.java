/*
 * [枚举名称]
 *
 * @Author:    wangxiao
 * @Date:      [日期]
 * @Copyright  子午线高科智能科技 2025
 */
package [package];

import net.lab1024.sa.base.common.enumeration.BaseEnum;

/**
 * [枚举名称]
 *
 * @Author:    wangxiao
 * @Date:      [日期]
 * @Copyright  子午线高科智能科技 2025
 */
public enum [EnumName]Enum implements BaseEnum {

    NORMAL(1, "正常"),
    DISABLED(0, "禁用");

    private final Integer value;
    private final String desc;

    [EnumName]Enum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}
