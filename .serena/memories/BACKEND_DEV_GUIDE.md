# SmartAdmin后端开发实施指南

> **作者**: wangxiao
> **企业**: 子午线高科智能科技
> **更新时间**: 2025-10-06

---

## 📋 快速开始

### 必读文档
1. [代码规范标准](./BACKEND_CODING_STANDARDS.md) - 详细的编码规范
2. [代码模板](./.templates/) - 标准化代码模板
3. 本文档 - 实施指南和检查清单

---

## 🚀 新功能开发流程

### Step 1: 需求分析 (5分钟)

**确定功能分类**:
- [ ] business (业务功能)
- [ ] system (系统功能)
- [ ] support (支撑功能)

**规划包结构**:
```
例如: 订单管理功能 (business/order)

module/business/order/
├── controller/
│   └── OrderController.java
├── service/
│   └── OrderService.java
├── dao/
│   └── OrderDao.java
├── domain/
│   ├── entity/
│   │   └── OrderEntity.java
│   ├── form/
│   │   ├── OrderQueryForm.java
│   │   ├── OrderAddForm.java
│   │   └── OrderUpdateForm.java
│   └── vo/
│       └── OrderVO.java
└── constant/
    └── OrderStatusEnum.java
```

### Step 2: 数据库设计 (10分钟)

#### 2.1 表结构设计

遵循SmartAdmin数据库规范:
```sql
CREATE TABLE `t_order` (
  `order_id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(64) NOT NULL COMMENT '订单编号',
  `customer_id` bigint NOT NULL COMMENT '客户ID',
  `total_amount` decimal(10,2) NOT NULL COMMENT '订单总金额',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '订单状态(1-待支付 2-已支付 3-已取消)',
  `deleted_flag` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识(0-未删除 1-已删除)',
  `create_user_id` bigint NOT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user_id` bigint DEFAULT NULL COMMENT '更新人ID',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`order_id`),
  UNIQUE KEY `uk_order_no` (`order_no`)
) ENGINE=InnoDB COMMENT='订单表';
```

#### 2.2 SQL脚本管理

将SQL脚本放置在正确位置:
```
数据库SQL脚本/mysql/sql-update-log/
└── order-module.sql  # 新模块的SQL脚本
```

### Step 3: 使用模板创建文件 (10分钟)

#### 3.1 创建Entity (实体类)

复制 `.templates/EntityTemplate.java`:
```java
/*
 * 订单实体
 *
 * @Author:    wangxiao
 * @Date:      2025-10-06
 * @Copyright  子午线高科智能科技 2025
 */
package net.lab1024.sa.admin.module.business.order.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_order")
public class OrderEntity {

    /**
     * 订单ID
     */
    @TableId(type = IdType.AUTO)
    private Long orderId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 客户ID
     */
    private Long customerId;

    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;

    /**
     * 订单状态
     */
    private Integer status;

    /**
     * 删除标识
     */
    @TableLogic
    private Integer deletedFlag;

    /**
     * 创建人ID
     */
    private Long createUserId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新人ID
     */
    private Long updateUserId;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
```

#### 3.2 创建Form (表单对象)

复制 `.templates/FormTemplate.java`:
```java
/*
 * 订单查询表单
 *
 * @Author:    wangxiao
 * @Date:      2025-10-06
 * @Copyright  子午线高科智能科技 2025
 */
package net.lab1024.sa.admin.module.business.order.domain.form;

import net.lab1024.sa.base.common.domain.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "订单查询表单")
public class OrderQueryForm extends PageParam {

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "客户ID")
    private Long customerId;

    @Schema(description = "订单状态")
    private Integer status;
}
```

#### 3.3 创建VO (视图对象)

复制 `.templates/VOTemplate.java`:
```java
/*
 * 订单VO
 *
 * @Author:    wangxiao
 * @Date:      2025-10-06
 * @Copyright  子午线高科智能科技 2025
 */
package net.lab1024.sa.admin.module.business.order.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "订单VO")
public class OrderVO {

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "客户ID")
    private Long customerId;

    @Schema(description = "订单总金额")
    private BigDecimal totalAmount;

    @Schema(description = "订单状态")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
```

#### 3.4 创建Dao (数据访问层)

复制 `.templates/DaoTemplate.java`:
```java
/*
 * 订单Dao
 *
 * @Author:    wangxiao
 * @Date:      2025-10-06
 * @Copyright  子午线高科智能科技 2025
 */
package net.lab1024.sa.admin.module.business.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.admin.module.business.order.domain.entity.OrderEntity;
import net.lab1024.sa.admin.module.business.order.domain.form.OrderQueryForm;
import net.lab1024.sa.admin.module.business.order.domain.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {

    /**
     * 分页查询 @author wangxiao
     */
    List<OrderVO> queryPage(Page page, @Param("query") OrderQueryForm queryForm);
}
```

#### 3.5 创建Mapper XML

复制 `.templates/MapperXMLTemplate.xml`:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="net.lab1024.sa.admin.module.business.order.dao.OrderDao">

    <select id="queryPage" resultType="net.lab1024.sa.admin.module.business.order.domain.vo.OrderVO">
        SELECT
            order_id,
            order_no,
            customer_id,
            total_amount,
            status,
            create_time
        FROM t_order
        WHERE deleted_flag = 0
        <if test="query.orderNo != null and query.orderNo != ''">
            AND order_no = #{query.orderNo}
        </if>
        <if test="query.customerId != null">
            AND customer_id = #{query.customerId}
        </if>
        <if test="query.status != null">
            AND status = #{query.status}
        </if>
        ORDER BY create_time DESC
    </select>

</mapper>
```

#### 3.6 创建Service (服务层)

复制 `.templates/ServiceTemplate.java`:
```java
/*
 * 订单Service
 *
 * @Author:    wangxiao
 * @Date:      2025-10-06
 * @Copyright  子午线高科智能科技 2025
 */
package net.lab1024.sa.admin.module.business.order.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.module.business.order.dao.OrderDao;
import net.lab1024.sa.admin.module.business.order.domain.entity.OrderEntity;
import net.lab1024.sa.admin.module.business.order.domain.form.OrderAddForm;
import net.lab1024.sa.admin.module.business.order.domain.form.OrderQueryForm;
import net.lab1024.sa.admin.module.business.order.domain.vo.OrderVO;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class OrderService {

    @Resource
    private OrderDao orderDao;

    /**
     * 分页查询 @author wangxiao
     */
    public ResponseDTO<PageResult<OrderVO>> queryPage(OrderQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<OrderVO> list = orderDao.queryPage(page, queryForm);
        PageResult<OrderVO> pageResult = SmartPageUtil.convert2PageResult(page, list);
        return ResponseDTO.ok(pageResult);
    }

    /**
     * 新增订单 @author wangxiao
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<String> add(OrderAddForm addForm) {
        // 业务逻辑实现
        OrderEntity entity = new OrderEntity();
        BeanUtils.copyProperties(addForm, entity);
        orderDao.insert(entity);
        return ResponseDTO.ok();
    }

    /**
     * 更新订单 @author wangxiao
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<String> update(OrderUpdateForm updateForm) {
        // 业务逻辑实现
        OrderEntity entity = orderDao.selectById(updateForm.getOrderId());
        if (entity == null) {
            return ResponseDTO.userErrorParam("订单不存在");
        }
        BeanUtils.copyProperties(updateForm, entity);
        orderDao.updateById(entity);
        return ResponseDTO.ok();
    }

    /**
     * 删除订单 @author wangxiao
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<String> delete(Long orderId) {
        orderDao.deleteById(orderId);
        return ResponseDTO.ok();
    }
}
```

#### 3.7 创建Controller (控制层)

复制 `.templates/ControllerTemplate.java`:
```java
/*
 * 订单Controller
 *
 * @Author:    wangxiao
 * @Date:      2025-10-06
 * @Copyright  子午线高科智能科技 2025
 */
package net.lab1024.sa.admin.module.business.order.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.lab1024.sa.admin.module.business.order.domain.form.OrderAddForm;
import net.lab1024.sa.admin.module.business.order.domain.form.OrderQueryForm;
import net.lab1024.sa.admin.module.business.order.domain.form.OrderUpdateForm;
import net.lab1024.sa.admin.module.business.order.domain.vo.OrderVO;
import net.lab1024.sa.admin.module.business.order.service.OrderService;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.ValidateList;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@Tag(name = "订单管理")
public class OrderController {

    @Resource
    private OrderService orderService;

    /**
     * 分页查询 @author wangxiao
     */
    @Operation(summary = "分页查询 @author wangxiao")
    @PostMapping("/order/queryPage")
    @SaCheckPermission("order:query")
    public ResponseDTO<PageResult<OrderVO>> queryPage(@RequestBody @Valid OrderQueryForm queryForm) {
        return orderService.queryPage(queryForm);
    }

    /**
     * 新增订单 @author wangxiao
     */
    @Operation(summary = "新增订单 @author wangxiao")
    @PostMapping("/order/add")
    @SaCheckPermission("order:add")
    public ResponseDTO<String> add(@RequestBody @Valid OrderAddForm addForm) {
        return orderService.add(addForm);
    }

    /**
     * 更新订单 @author wangxiao
     */
    @Operation(summary = "更新订单 @author wangxiao")
    @PostMapping("/order/update")
    @SaCheckPermission("order:update")
    public ResponseDTO<String> update(@RequestBody @Valid OrderUpdateForm updateForm) {
        return orderService.update(updateForm);
    }

    /**
     * 删除订单 @author wangxiao
     */
    @Operation(summary = "删除订单 @author wangxiao")
    @GetMapping("/order/delete/{orderId}")
    @SaCheckPermission("order:delete")
    public ResponseDTO<String> delete(@PathVariable Long orderId) {
        return orderService.delete(orderId);
    }
}
```

### Step 4: 代码开发 (根据复杂度)

**开发中注意**:
- ✅ Controller必须返回 `ResponseDTO<T>`
- ✅ Service事务方法必须加 `@Transactional(rollbackFor = Exception.class)`
- ✅ 使用ErrorCode枚举,不要硬编码错误信息
- ✅ 所有方法添加 `@author wangxiao` 注释
- ✅ 参数校验使用 `@Valid` 注解

### Step 5: 自检清单 (5分钟)

**代码规范检查**:
- [ ] 文件头注释完整? (作者、日期、Copyright)
- [ ] 方法注释包含 `@author wangxiao`?
- [ ] Controller返回值是 `ResponseDTO<T>`?
- [ ] Service事务方法有 `@Transactional(rollbackFor = Exception.class)`?
- [ ] 使用ErrorCode枚举而非硬编码?

**功能测试**:
```bash
# 启动项目
mvn spring-boot:run

# 访问Swagger文档
http://localhost:1024/doc.html
```

---

## ✅ 开发检查清单

### 开发前检查

- [ ] 阅读 [BACKEND_CODING_STANDARDS.md](./BACKEND_CODING_STANDARDS.md)
- [ ] 确认功能分类 (business/system/support)
- [ ] 规划好完整的包结构
- [ ] 设计好数据库表结构
- [ ] 准备好代码模板

### 开发中检查

- [ ] Entity文件: `{模块}Entity.java`
- [ ] Form文件: `{模块}QueryForm.java`, `{模块}AddForm.java`, `{模块}UpdateForm.java`
- [ ] VO文件: `{模块}VO.java`
- [ ] Dao文件: `{模块}Dao.java`
- [ ] Service文件: `{模块}Service.java`
- [ ] Controller文件: `{模块}Controller.java`
- [ ] 所有类包含完整头部注释
- [ ] 所有public方法包含 `@author wangxiao`
- [ ] Controller返回 `ResponseDTO<T>`
- [ ] Service事务方法有 `@Transactional(rollbackFor = Exception.class)`
- [ ] 使用ErrorCode枚举

### 开发后检查

- [ ] Maven编译通过 (`mvn clean compile`)
- [ ] 启动无错误 (`mvn spring-boot:run`)
- [ ] Swagger文档正确 (http://localhost:1024/doc.html)
- [ ] 接口测试通过
- [ ] 代码已提交到正确分支

---

## 📚 常见场景示例

### 场景1: 新增CRUD模块

**需求**: 新增"库存管理"功能

**步骤**:
1. 确定分类: `business/inventory`
2. 设计数据库表: `t_inventory`
3. 创建文件:
   ```
   module/business/inventory/
   ├── controller/InventoryController.java
   ├── service/InventoryService.java
   ├── dao/InventoryDao.java
   ├── domain/
   │   ├── entity/InventoryEntity.java
   │   ├── form/InventoryQueryForm.java
   │   └── vo/InventoryVO.java
   ```
4. 使用模板填充内容
5. 测试接口

### 场景2: 定时任务开发

**需求**: 每天凌晨2点同步库存数据

**步骤**:
```java
/*
 * 库存同步定时任务
 *
 * @Author:    wangxiao
 * @Date:      2025-10-06
 * @Copyright  子午线高科智能科技 2025
 */
package net.lab1024.sa.admin.module.business.inventory.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class InventorySyncJob {

    @Resource
    private InventoryService inventoryService;

    /**
     * 同步库存数据 @author wangxiao
     * 每天凌晨2点执行
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void syncInventory() {
        log.info("开始同步库存数据");
        try {
            inventoryService.syncInventoryData();
            log.info("库存数据同步成功");
        } catch (Exception e) {
            log.error("库存数据同步失败", e);
        }
    }
}
```

### 场景3: 文件上传下载

**需求**: 实现订单文件上传

**步骤**:
```java
@RestController
@Tag(name = "订单文件管理")
public class OrderFileController {

    @Resource
    private FileService fileService;

    /**
     * 上传订单文件 @author wangxiao
     */
    @Operation(summary = "上传订单文件 @author wangxiao")
    @PostMapping("/order/file/upload")
    @SaCheckPermission("order:file:upload")
    public ResponseDTO<FileUploadVO> upload(@RequestParam("file") MultipartFile file) {
        return fileService.upload(file, FileModuleEnum.ORDER);
    }

    /**
     * 下载订单文件 @author wangxiao
     */
    @Operation(summary = "下载订单文件 @author wangxiao")
    @GetMapping("/order/file/download/{fileId}")
    @SaCheckPermission("order:file:download")
    public void download(@PathVariable Long fileId, HttpServletResponse response) {
        fileService.download(fileId, response);
    }
}
```

### 场景4: 权限控制

**需求**: 只有管理员能删除订单

**步骤**:
```java
@RestController
public class OrderController {

    /**
     * 删除订单 @author wangxiao
     * 权限: 只有管理员可以删除
     */
    @Operation(summary = "删除订单 @author wangxiao")
    @GetMapping("/order/delete/{orderId}")
    @SaCheckPermission("order:delete")  // 在菜单管理中配置此权限码
    @SaCheckRole("admin")               // 检查角色
    public ResponseDTO<String> delete(@PathVariable Long orderId) {
        return orderService.delete(orderId);
    }
}
```

### 场景5: 数据导出

**需求**: 导出订单列表到Excel

**步骤**:
```java
@RestController
public class OrderController {

    @Resource
    private OrderService orderService;

    /**
     * 导出订单 @author wangxiao
     */
    @Operation(summary = "导出订单 @author wangxiao")
    @PostMapping("/order/export")
    @SaCheckPermission("order:export")
    public void export(@RequestBody @Valid OrderQueryForm queryForm, HttpServletResponse response) {
        orderService.export(queryForm, response);
    }
}
```

Service实现:
```java
public void export(OrderQueryForm queryForm, HttpServletResponse response) {
    // 查询数据
    List<OrderVO> list = orderDao.queryList(queryForm);

    // 导出Excel
    SmartExcelUtil.exportExcel(
        response,
        "订单列表.xlsx",
        OrderVO.class,
        list
    );
}
```

---

## 🔧 工具配置

### Maven命令

**编译项目**:
```bash
mvn clean compile
```

**打包项目**:
```bash
mvn clean package -DskipTests
```

**运行项目**:
```bash
mvn spring-boot:run
```

**指定环境运行**:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

**运行测试**:
```bash
mvn test
```

### 数据库工具

**查看Flyway迁移状态**:
```bash
mvn flyway:info
```

**执行数据库迁移**:
```bash
mvn flyway:migrate
```

### 日志查看

**开发环境日志位置**:
```
logs/smart-admin-api.log
```

**查看实时日志**:
```bash
tail -f logs/smart-admin-api.log
```

---

## 💡 最佳实践

### 1. 事务管理 ⚠️ 强制

**核心原则**: 所有涉及数据修改的Service方法必须加事务注解

```java
// ✅ 正确
@Transactional(rollbackFor = Exception.class)
public ResponseDTO<String> add(OrderAddForm addForm) {
    // 业务逻辑
}

// ❌ 错误: 缺少事务注解
public ResponseDTO<String> add(OrderAddForm addForm) {
    // 数据修改但没有事务保护
}
```

### 2. 异常处理 ⚠️ 重要

**统一异常处理**:
```java
// ✅ 正确: 使用ResponseDTO返回业务错误
public ResponseDTO<String> updateStatus(Long orderId) {
    OrderEntity entity = orderDao.selectById(orderId);
    if (entity == null) {
        return ResponseDTO.userErrorParam("订单不存在");
    }
    // 继续业务逻辑
}

// ❌ 错误: 抛出异常
public ResponseDTO<String> updateStatus(Long orderId) {
    OrderEntity entity = orderDao.selectById(orderId);
    if (entity == null) {
        throw new RuntimeException("订单不存在");  // 不要这样做
    }
}
```

### 3. 日志规范

**日志级别使用**:
```java
@Slf4j
@Service
public class OrderService {

    public ResponseDTO<String> processOrder(OrderAddForm addForm) {
        // DEBUG: 详细的调试信息
        log.debug("开始处理订单, 参数: {}", addForm);

        // INFO: 重要的业务流程节点
        log.info("创建订单成功, 订单号: {}", orderNo);

        // WARN: 需要关注但不影响主流程
        log.warn("库存不足, 订单: {}, 当前库存: {}", orderNo, stock);

        // ERROR: 错误和异常
        log.error("订单处理失败, 订单号: {}", orderNo, e);

        return ResponseDTO.ok();
    }
}
```

### 4. SQL优化

**避免N+1查询**:
```java
// ❌ 错误: N+1查询
List<OrderVO> orders = orderDao.queryList();
for (OrderVO order : orders) {
    CustomerVO customer = customerDao.selectById(order.getCustomerId());  // N次查询
    order.setCustomer(customer);
}

// ✅ 正确: 关联查询
List<OrderVO> orders = orderDao.queryListWithCustomer();  // 一次查询搞定
```

**合理使用索引**:
```sql
-- 为常用查询条件添加索引
ALTER TABLE t_order ADD INDEX idx_customer_status (customer_id, status);

-- 避免在WHERE条件中对索引字段进行函数操作
-- ❌ 错误
SELECT * FROM t_order WHERE DATE(create_time) = '2025-10-06';

-- ✅ 正确
SELECT * FROM t_order WHERE create_time >= '2025-10-06 00:00:00'
  AND create_time < '2025-10-07 00:00:00';
```

### 5. 批量操作

**大批量数据处理**:
```java
// ✅ 正确: 分批处理
@Transactional(rollbackFor = Exception.class)
public ResponseDTO<String> batchUpdate(List<Long> orderIds) {
    // 每批处理1000条
    int batchSize = 1000;
    for (int i = 0; i < orderIds.size(); i += batchSize) {
        List<Long> batch = orderIds.subList(i, Math.min(i + batchSize, orderIds.size()));
        orderDao.batchUpdate(batch);
    }
    return ResponseDTO.ok();
}
```

### 6. 缓存策略

**使用Redis缓存**:
```java
@Service
public class OrderService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private static final String CACHE_KEY = "order:detail:";

    /**
     * 获取订单详情(带缓存) @author wangxiao
     */
    public ResponseDTO<OrderVO> getDetail(Long orderId) {
        // 先查缓存
        String cacheKey = CACHE_KEY + orderId;
        OrderVO cached = (OrderVO) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return ResponseDTO.ok(cached);
        }

        // 查数据库
        OrderVO order = orderDao.selectById(orderId);

        // 写入缓存, 过期时间30分钟
        redisTemplate.opsForValue().set(cacheKey, order, 30, TimeUnit.MINUTES);

        return ResponseDTO.ok(order);
    }
}
```

### 7. 参数校验

**使用Validation注解**:
```java
@Data
@Schema(description = "订单新增表单")
public class OrderAddForm {

    @NotBlank(message = "订单编号不能为空")
    @Schema(description = "订单编号")
    private String orderNo;

    @NotNull(message = "客户ID不能为空")
    @Schema(description = "客户ID")
    private Long customerId;

    @NotNull(message = "订单金额不能为空")
    @Min(value = 0, message = "订单金额不能为负数")
    @Schema(description = "订单总金额")
    private BigDecimal totalAmount;
}
```

### 8. 敏感信息处理

**日志脱敏**:
```java
@Slf4j
@Service
public class OrderService {

    public ResponseDTO<String> processPayment(PaymentForm form) {
        // ❌ 错误: 直接打印敏感信息
        log.info("处理支付, 参数: {}", form);  // 可能包含银行卡号

        // ✅ 正确: 脱敏后打印
        log.info("处理支付, 订单号: {}, 金额: {}", form.getOrderNo(), form.getAmount());

        return ResponseDTO.ok();
    }
}
```

---

## 🆘 常见问题

### Q1: 如何快速创建符合规范的文件?

**A**: 使用 `.templates/` 目录下的模板文件,替换占位符即可。

### Q2: Controller必须返回ResponseDTO吗?

**A**: 是的,所有Controller方法必须返回 `ResponseDTO<T>`,这是SmartAdmin的强制规范。

```java
// ✅ 正确
public ResponseDTO<String> add(...) {
    return ResponseDTO.ok();
}

// ❌ 错误
public String add(...) {
    return "success";
}
```

### Q3: 事务注解应该放在哪一层?

**A**: 事务注解应该放在Service层,不要放在Controller或Dao层。

```java
// ✅ 正确: Service层加事务
@Service
public class OrderService {
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<String> add(...) { }
}

// ❌ 错误: Controller层加事务
@RestController
public class OrderController {
    @Transactional  // 不要这样做
    public ResponseDTO<String> add(...) { }
}
```

### Q4: 如何处理并发问题?

**A**: 使用乐观锁或分布式锁。

**乐观锁(推荐)**:
```java
@Data
@TableName("t_order")
public class OrderEntity {
    @Version  // MyBatis-Plus乐观锁注解
    private Integer version;
}
```

**分布式锁**:
```java
@Service
public class OrderService {

    @Resource
    private RedissonClient redissonClient;

    public ResponseDTO<String> processOrder(Long orderId) {
        RLock lock = redissonClient.getLock("order:lock:" + orderId);
        try {
            // 尝试加锁, 等待时间10秒, 锁过期时间30秒
            if (lock.tryLock(10, 30, TimeUnit.SECONDS)) {
                // 业务处理
                return ResponseDTO.ok();
            } else {
                return ResponseDTO.userErrorParam("订单正在处理中,请稍后再试");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseDTO.error("系统异常");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
```

### Q5: 如何确保包结构正确?

**A**: 遵循三层分类原则,参考现有代码实现:

```
module/
├── business/     # 业务功能 (订单、库存、客户等)
├── system/       # 系统功能 (员工、角色、菜单等)
└── support/      # 支撑功能 (文件、日志、配置等)
```

### Q6: 如何调试SQL语句?

**A**: 在 `application-dev.yml` 中开启SQL日志:

```yaml
mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl  # 控制台打印SQL
```

### Q7: 团队新成员如何快速上手?

**A**:
1. 阅读 [BACKEND_CODING_STANDARDS.md](./BACKEND_CODING_STANDARDS.md)
2. 查看 `.templates/` 目录了解代码模板
3. 参考现有模块代码实现
4. 遵循本开发指南
5. 使用Swagger文档测试接口

---

## 📖 参考资源

- [SmartAdmin官方文档](https://smartadmin.vip)
- [Spring Boot官方文档](https://spring.io/projects/spring-boot)
- [MyBatis-Plus官方文档](https://baomidou.com/)
- [Sa-Token官方文档](https://sa-token.cc/)

---

**持续改进**: 本文档会随项目发展持续更新,欢迎提出改进建议!
