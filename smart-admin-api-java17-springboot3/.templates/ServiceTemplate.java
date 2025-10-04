/*
 * [模块名称]服务
 *
 * @Author:    wangxiao
 * @Date:      [日期]
 * @Copyright  子午线高科智能科技 2025
 */
package [package];

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * [模块名称]服务
 *
 * @Author:    wangxiao
 * @Date:      [日期]
 * @Copyright  子午线高科智能科技 2025
 */
@Service
public class [ModuleName]Service {

    @Resource
    private [ModuleName]Dao [moduleName]Dao;

    /**
     * 分页查询 @author wangxiao
     */
    public ResponseDTO<PageResult<[ModuleName]VO>> queryPage([ModuleName]QueryForm queryForm) {
        // 1. 构建查询条件
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);

        // 2. 查询数据
        List<[ModuleName]VO> list = [moduleName]Dao.queryPage(page, queryForm);

        // 3. 构建分页结果
        PageResult<[ModuleName]VO> pageResult = SmartPageUtil.convert2PageResult(page, list);
        return ResponseDTO.ok(pageResult);
    }

    /**
     * 新增 @author wangxiao
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<String> add([ModuleName]AddForm addForm) {
        // 1. 数据校验
        // TODO: 添加业务校验逻辑

        // 2. 构建实体
        [ModuleName]Entity entity = SmartBeanUtil.copy(addForm, [ModuleName]Entity.class);

        // 3. 保存数据
        [moduleName]Dao.insert(entity);

        return ResponseDTO.ok();
    }

    /**
     * 更新 @author wangxiao
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<String> update([ModuleName]UpdateForm updateForm) {
        // 1. 数据校验
        [ModuleName]Entity entity = [moduleName]Dao.selectById(updateForm.get[Id]());
        if (entity == null) {
            return ResponseDTO.error([BusinessErrorCode].NOT_EXIST);
        }

        // 2. 更新实体
        SmartBeanUtil.copyProperties(updateForm, entity);

        // 3. 保存数据
        [moduleName]Dao.updateById(entity);

        return ResponseDTO.ok();
    }

    /**
     * 删除 @author wangxiao
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<String> delete(Long id) {
        // 1. 数据校验
        [ModuleName]Entity entity = [moduleName]Dao.selectById(id);
        if (entity == null) {
            return ResponseDTO.error([BusinessErrorCode].NOT_EXIST);
        }

        // 2. 删除数据
        [moduleName]Dao.deleteById(id);

        return ResponseDTO.ok();
    }

    /**
     * 详情 @author wangxiao
     */
    public ResponseDTO<[ModuleName]VO> detail(Long id) {
        // 1. 查询数据
        [ModuleName]Entity entity = [moduleName]Dao.selectById(id);
        if (entity == null) {
            return ResponseDTO.error([BusinessErrorCode].NOT_EXIST);
        }

        // 2. 构建VO
        [ModuleName]VO vo = SmartBeanUtil.copy(entity, [ModuleName]VO.class);

        return ResponseDTO.ok(vo);
    }
}
