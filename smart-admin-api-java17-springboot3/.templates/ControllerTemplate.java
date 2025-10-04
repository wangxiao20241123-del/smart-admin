/*
 * [模块名称]控制器
 *
 * @Author:    wangxiao
 * @Date:      [日期]
 * @Copyright  子午线高科智能科技 2025
 */
package [package];

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import org.springframework.web.bind.annotation.*;
import cn.dev33.satoken.annotation.SaCheckPermission;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * [模块名称]控制器
 *
 * @Author:    wangxiao
 * @Date:      [日期]
 * @Copyright  子午线高科智能科技 2025
 */
@RestController
@Tag(name = "[模块标签]")
public class [ModuleName]Controller {

    @Resource
    private [ModuleName]Service [moduleName]Service;

    /**
     * 分页查询 @author wangxiao
     */
    @Operation(summary = "分页查询 @author wangxiao")
    @PostMapping("/[module-path]/queryPage")
    @SaCheckPermission("[module:permission:query]")
    public ResponseDTO<PageResult<[ModuleName]VO>> queryPage(
            @RequestBody @Valid [ModuleName]QueryForm queryForm) {
        return [moduleName]Service.queryPage(queryForm);
    }

    /**
     * 新增 @author wangxiao
     */
    @Operation(summary = "新增 @author wangxiao")
    @PostMapping("/[module-path]/add")
    @SaCheckPermission("[module:permission:add]")
    public ResponseDTO<String> add(@RequestBody @Valid [ModuleName]AddForm addForm) {
        return [moduleName]Service.add(addForm);
    }

    /**
     * 更新 @author wangxiao
     */
    @Operation(summary = "更新 @author wangxiao")
    @PostMapping("/[module-path]/update")
    @SaCheckPermission("[module:permission:update]")
    public ResponseDTO<String> update(@RequestBody @Valid [ModuleName]UpdateForm updateForm) {
        return [moduleName]Service.update(updateForm);
    }

    /**
     * 删除 @author wangxiao
     */
    @Operation(summary = "删除 @author wangxiao")
    @GetMapping("/[module-path]/delete/{id}")
    @SaCheckPermission("[module:permission:delete]")
    public ResponseDTO<String> delete(@PathVariable Long id) {
        return [moduleName]Service.delete(id);
    }

    /**
     * 详情 @author wangxiao
     */
    @Operation(summary = "详情 @author wangxiao")
    @GetMapping("/[module-path]/detail/{id}")
    @SaCheckPermission("[module:permission:query]")
    public ResponseDTO<[ModuleName]VO> detail(@PathVariable Long id) {
        return [moduleName]Service.detail(id);
    }
}
