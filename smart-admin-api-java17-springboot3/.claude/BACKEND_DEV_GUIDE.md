---
title: SmartAdmin后端开发实施指南
author: wangxiao
company: 子午线高科智能科技
date: 2025-10-07
---

# SmartAdmin后端开发实施指南

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

#### 表结构设计规范

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

**SQL脚本管理**: 将SQL脚本放置在 `数据库SQL脚本/mysql/sql-update-log/` 目录

### Step 3: 使用模板创建文件 (10分钟)

**文件创建清单**:

| 层级 | 文件命名 | 模板位置 | 关键注意点 |
|------|----------|----------|------------|
| Entity | `{模块}Entity.java` | `.templates/EntityTemplate.java` | `@TableName`, `@TableId`, `@TableLogic` |
| Form | `{模块}QueryForm.java`<br>`{模块}AddForm.java`<br>`{模块}UpdateForm.java` | `.templates/FormTemplate.java` | 继承 `PageParam`<br>`@Schema` 描述<br>`@Valid` 校验 |
| VO | `{模块}VO.java` | `.templates/VOTemplate.java` | 返回字段选择<br>`@Schema` 描述 |
| Dao | `{模块}Dao.java` | `.templates/DaoTemplate.java` | 继承 `BaseMapper`<br>`@Mapper` 注解 |
| Mapper XML | `{模块}Mapper.xml` | `.templates/MapperXMLTemplate.xml` | namespace 正确<br>SQL 参数绑定 |
| Service | `{模块}Service.java` | `.templates/ServiceTemplate.java` | `@Service` 注解<br>`@Transactional(rollbackFor = Exception.class)` |
| Controller | `{模块}Controller.java` | `.templates/ControllerTemplate.java` | 返回 `ResponseDTO<T>`<br>`@SaCheckPermission` |

**使用步骤**:
1. 复制对应模板文件
2. 替换所有 `{占位符}`
3. 按照用户偏好添加文件头注释和方法注释

**详细代码示例**: 请查看 `.templates/` 目录下的完整模板文件

### Step 4: 代码开发 (根据复杂度)

**强制规范** ⚠️:
- ✅ Controller必须返回 `ResponseDTO<T>`
- ✅ Service事务方法必须加 `@Transactional(rollbackFor = Exception.class)`
- ✅ 使用 `ErrorCode` 枚举，不要硬编码错误信息
- ✅ 参数校验使用 `@Valid` 注解

### Step 5: 自检与测试 (5分钟)

**编译测试**:
```bash
# 编译项目
mvn clean compile

# 启动项目
mvn spring-boot:run

# 访问Swagger文档
http://localhost:1024/doc.html
```

**快速检查**:
- [ ] Controller返回 `ResponseDTO<T>`
- [ ] Service事务方法有 `@Transactional(rollbackFor = Exception.class)`
- [ ] 使用ErrorCode枚举而非硬编码

---

## ✅ 开发检查清单

### 📝 开发前检查
- [ ] 阅读 [BACKEND_CODING_STANDARDS.md](./BACKEND_CODING_STANDARDS.md)
- [ ] 确认功能分类 (business/system/support)
- [ ] 规划完整的包结构
- [ ] 设计数据库表结构
- [ ] 准备代码模板

### 💻 开发中检查
**文件完整性**:
- [ ] Entity: `{模块}Entity.java`
- [ ] Form: `{模块}QueryForm/AddForm/UpdateForm.java`
- [ ] VO: `{模块}VO.java`
- [ ] Dao: `{模块}Dao.java` + Mapper XML
- [ ] Service: `{模块}Service.java`
- [ ] Controller: `{模块}Controller.java`

**代码规范**:
- [ ] Controller返回 `ResponseDTO<T>`
- [ ] Service事务方法有 `@Transactional(rollbackFor = Exception.class)`
- [ ] 使用ErrorCode枚举

### 🚦 开发后检查
- [ ] Maven编译通过 (`mvn clean compile`)
- [ ] 启动无错误 (`mvn spring-boot:run`)
- [ ] Swagger文档正确 (http://localhost:1024/doc.html)
- [ ] 接口功能测试通过
- [ ] 代码已提交到正确分支

---

## 📚 核心场景示例

### 场景1: 新增CRUD模块

**需求**: 新增"库存管理"功能

**步骤**:
1. 确定分类: `business/inventory`
2. 设计数据库表: `t_inventory`
3. 创建文件结构:
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
4. 使用 `.templates/` 模板填充内容
5. 测试接口

### 场景2: 定时任务开发

**需求**: 每天凌晨2点同步库存数据

**实现**:
```java
/*
 * 库存同步定时任务
 *
 * @Author:    wangxiao
 * @Date:      2025-10-07
 * @Copyright  子午线高科智能科技 2025
 */
@Slf4j
@Component
public class InventorySyncJob {

    @Resource
    private InventoryService inventoryService;

    /**
     * 同步库存数据 @author wangxiao
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

### 场景3: 权限控制

**需求**: 只有管理员能删除订单

**实现**:
```java
/**
 * 删除订单 @author wangxiao
 */
@Operation(summary = "删除订单 @author wangxiao")
@GetMapping("/order/delete/{orderId}")
@SaCheckPermission("order:delete")  // 权限码（在菜单管理中配置）
@SaCheckRole("admin")               // 角色检查
public ResponseDTO<String> delete(@PathVariable Long orderId) {
    return orderService.delete(orderId);
}
```

---

## 💡 关键最佳实践

### 1. 事务管理 ⚠️ 强制

所有涉及数据修改的Service方法必须加事务注解:

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

**要点**:
- 事务注解放在Service层，不要放在Controller或Dao
- 必须指定 `rollbackFor = Exception.class`

### 2. 异常处理

**使用ResponseDTO返回业务错误**:

```java
// ✅ 正确
public ResponseDTO<String> updateStatus(Long orderId) {
    OrderEntity entity = orderDao.selectById(orderId);
    if (entity == null) {
        return ResponseDTO.userErrorParam("订单不存在");
    }
    // 继续业务逻辑
}

// ❌ 错误: 不要抛出异常
public ResponseDTO<String> updateStatus(Long orderId) {
    if (entity == null) {
        throw new RuntimeException("订单不存在");  // 禁止
    }
}
```

### 3. 参数校验

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
List<OrderVO> orders = orderDao.queryListWithCustomer();  // 一次查询
```

**合理使用索引**:

```sql
-- 为常用查询条件添加索引
ALTER TABLE t_order ADD INDEX idx_customer_status (customer_id, status);

-- 避免在WHERE条件中对索引字段进行函数操作
-- ❌ 错误
SELECT * FROM t_order WHERE DATE(create_time) = '2025-10-07';

-- ✅ 正确
SELECT * FROM t_order
WHERE create_time >= '2025-10-07 00:00:00'
  AND create_time < '2025-10-08 00:00:00';
```

### 5. 日志规范

**日志级别正确使用**:

```java
@Slf4j
@Service
public class OrderService {

    public ResponseDTO<String> processOrder(OrderAddForm addForm) {
        // DEBUG: 详细调试信息
        log.debug("开始处理订单, 参数: {}", addForm);

        // INFO: 重要业务流程节点
        log.info("创建订单成功, 订单号: {}", orderNo);

        // WARN: 需要关注但不影响主流程
        log.warn("库存不足, 订单: {}, 当前库存: {}", orderNo, stock);

        // ERROR: 错误和异常
        log.error("订单处理失败, 订单号: {}", orderNo, e);

        return ResponseDTO.ok();
    }
}
```

---

## 🔧 工具命令

### Maven命令

```bash
# 编译项目
mvn clean compile

# 打包项目（跳过测试）
mvn clean package -DskipTests

# 运行项目
mvn spring-boot:run

# 指定环境运行
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 运行测试
mvn test
```

### 日志查看

```bash
# 查看实时日志
tail -f logs/smart-admin-api.log
```

### SQL调试

在 `application-dev.yml` 中开启SQL日志:

```yaml
mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

---

## 🆘 常见问题

### Q1: 如何快速创建符合规范的文件?

**A**: 使用 `.templates/` 目录下的模板文件，替换占位符即可。

### Q2: Controller必须返回ResponseDTO吗?

**A**: 是的，所有Controller方法必须返回 `ResponseDTO<T>`，这是强制规范。

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

**A**: 事务注解放在Service层，不要放在Controller或Dao层。

```java
// ✅ 正确: Service层
@Service
public class OrderService {
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<String> add(...) { }
}

// ❌ 错误: Controller层
@RestController
public class OrderController {
    @Transactional  // 禁止
    public ResponseDTO<String> add(...) { }
}
```

### Q4: 如何确保包结构正确?

**A**: 遵循三层分类原则：

```
module/
├── business/     # 业务功能（订单、库存、客户等）
├── system/       # 系统功能（员工、角色、菜单等）
└── support/      # 支撑功能（文件、日志、配置等）
```

### Q5: 团队新成员如何快速上手?

**A**:
1. 阅读 [BACKEND_CODING_STANDARDS.md](./BACKEND_CODING_STANDARDS.md)
2. 查看 `.templates/` 目录了解代码模板
3. 参考现有模块代码实现
4. 遵循本开发指南的5步流程
5. 使用Swagger文档测试接口 (http://localhost:1024/doc.html)

---

## 📖 参考资源

- [SmartAdmin官方文档](https://smartadmin.vip)
- [Spring Boot官方文档](https://spring.io/projects/spring-boot)
- [MyBatis-Plus官方文档](https://baomidou.com/)
- [Sa-Token官方文档](https://sa-token.cc/)

---

**持续改进**: 本文档会随项目发展持续更新，欢迎提出改进建议！
