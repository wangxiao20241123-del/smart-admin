# SmartAdmin后端代码规范标准

> **作者**: wangxiao
> **企业**: 子午线高科智能科技
> **更新时间**: 2025-10-04

---

## 🚀 开发速查表

> **新人必看**: 写代码前先看这个表,90%的问题都在这里

### 核心规范(每个文件都要检查)

```java
/*
 * [功能模块名称]
 *
 * @Author:    wangxiao
 * @Date:      2025-10-04
 * @Copyright  子午线高科智能科技 2025
 */

/**
 * [方法说明] @author wangxiao
 */
```

⚠️ **强制要求**:
- 文件头: `@Author: wangxiao` + `@Copyright 子午线高科智能科技 2025`
- 方法注释: 所有public方法必须加 `@author wangxiao`

### 常见场景速查

| 场景 | ✅ 正确做法 | ❌ 错误做法 |
|------|-----------|-----------|
| **Controller返回值** | `ResponseDTO<T>` | 直接返回业务对象 |
| **错误处理** | `ResponseDTO.error(ErrorCode)` | 硬编码错误码/消息 |
| **事务控制** | `@Transactional(rollbackFor = Exception.class)` | `@Transactional` |
| **对象转换** | `SmartBeanUtil.copy()` | `BeanUtils.copyProperties()` |
| **分页查询** | `SmartPageUtil` | 手动分页逻辑 |
| **数据校验** | `@RequestBody @Valid Form` | 手动校验参数 |

### 快速决策树

#### 我应该返回什么类型?
```
在Controller?
├─ 是 → 必须返回 ResponseDTO<T>
└─ 否 → 在Service?
    ├─ 是 → ResponseDTO<T> 或业务对象都可以
    └─ 否 → 在Dao?
        └─ 是 → Entity 或集合
```

#### 我应该用哪个ErrorCode?
```
用户输入问题? → UserErrorCode (30001-30999)
业务规则不满足? → BusinessErrorCode (50001-59999)
系统内部错误? → SystemErrorCode (10001-10999)
```

#### 我应该放在哪个包?
```
核心业务功能? → module.business
系统管理功能? → module.system
支撑功能? → module.support
```

---

## 📋 目录

1. [项目结构](#项目结构)
2. [四层架构](#四层架构)
3. [类命名规范](#类命名规范)
4. [ResponseDTO规范](#responsedt规范)
5. [ErrorCode规范](#errorcode规范)
6. [注解规范](#注解规范)
7. [数据库规范](#数据库规范)
8. [异常处理](#异常处理)
9. [工具推荐](#工具推荐)

---

## 项目结构

### 模块划分

```
smart-admin-api-java17-springboot3/
├── sa-base/              # 基础模块(框架、工具、公共组件)
│   ├── common/           # 公共组件(ResponseDTO、工具类)
│   ├── config/           # 配置类
│   ├── constant/         # 常量定义
│   └── module/support/   # 支撑功能
├── sa-admin/             # 业务模块(具体业务实现)
│   ├── module/business/  # 核心业务功能
│   └── module/system/    # 系统管理功能
└── pom.xml
```

### 包结构规范

```java
// ✅ 正确: 业务功能
net.lab1024.sa.admin.module.business.oa.enterprise

// ✅ 正确: 系统功能
net.lab1024.sa.admin.module.system.employee

// ❌ 错误: 没有分类
net.lab1024.sa.admin.enterprise
```

💡 **使用模板**: 参考 `.templates/` 目录获取标准化代码模板

---

## 四层架构

### 架构层次与职责

| 层级 | 职责 | 注解 | 返回值 | 模板 |
|------|------|------|--------|------|
| **Controller** | 接口定义、参数校验、权限控制 | `@RestController` | `ResponseDTO<T>` | ControllerTemplate.java |
| **Service** | 业务逻辑、事务控制 | `@Service` | `ResponseDTO<T>` 或业务对象 | ServiceTemplate.java |
| **Manager** | 复杂业务编排、缓存管理(可选) | `@Component` | 业务对象 | - |
| **Dao** | 数据库操作 | `@Mapper` | Entity 或集合 | DaoTemplate.java |

### 代码模板(精简版)

#### Controller层
```java
@RestController
public class ModuleController {

    @Resource
    private ModuleService moduleService;

    /**
     * [操作说明] @author wangxiao
     */
    @Operation(summary = "[操作说明] @author wangxiao")
    @PostMapping("/path")
    @SaCheckPermission("module:action")
    public ResponseDTO<T> method(@RequestBody @Valid Form form) {
        return moduleService.method(form);
    }
}
```

#### Service层
```java
@Service
public class ModuleService {

    @Resource
    private ModuleDao moduleDao;

    /**
     * [操作说明] @author wangxiao
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<String> method(Form form) {
        // 1. 数据校验
        // 2. 业务逻辑
        // 3. 保存数据
        return ResponseDTO.ok();
    }
}
```

#### Dao层
```java
@Mapper
public interface ModuleDao extends BaseMapper<ModuleEntity> {

    /**
     * [操作说明] @author wangxiao
     */
    List<VO> queryByPage(@Param("page") Page<?> page,
                         @Param("queryForm") QueryForm queryForm);
}
```

💡 **使用模板**: 复制 `.templates/` 下的模板文件,替换 `[占位符]`

⚠️ **常见错误**:
- ❌ Controller返回业务对象而不是ResponseDTO
- ❌ @Transactional不指定rollbackFor
- ❌ 缺少@author wangxiao标识

---

## 类命名规范

### Domain对象命名

| 类型 | 后缀 | 用途 | 示例 | 模板 |
|------|------|------|------|------|
| **Entity** | Entity | 数据库实体 | `EnterpriseEntity` | EntityTemplate.java |
| **QueryForm** | QueryForm | 查询表单 | `EnterpriseQueryForm` | FormTemplate.java |
| **AddForm** | AddForm | 新增表单 | `EnterpriseAddForm` | FormTemplate.java |
| **UpdateForm** | UpdateForm | 更新表单 | `EnterpriseUpdateForm` | FormTemplate.java |
| **VO** | VO | 视图对象(返回前端) | `EnterpriseVO` | VOTemplate.java |
| **DTO** | DTO | 数据传输对象 | `EnterpriseDTO` | - |

### 枚举类命名

```java
// ✅ 正确: Enum后缀,实现BaseEnum
public enum EnterpriseTypeEnum implements BaseEnum {
    NORMAL(1, "正常企业"),
    DISABLED(0, "禁用企业");

    private final Integer value;
    private final String desc;
}
```

💡 **使用模板**: 参考 `.templates/EnumTemplate.java`

---

## ResponseDTO规范

### 统一响应结构

```java
public class ResponseDTO<T> {
    private Integer code;      // 响应码
    private String msg;        // 响应消息
    private Boolean ok;        // 是否成功
    private T data;           // 响应数据
}
```

### 使用规范

```java
// ✅ 成功响应
return ResponseDTO.ok();
return ResponseDTO.ok(data);

// ✅ 错误响应(使用ErrorCode枚举)
return ResponseDTO.error(BusinessErrorCode.NOT_EXIST);

// ❌ 禁止硬编码
return ResponseDTO.error(50001, "企业不存在");

// ❌ 禁止直接返回业务对象
return enterprise; // Controller必须返回ResponseDTO
```

⚠️ **强制要求**: Controller所有方法必须返回`ResponseDTO<T>`

---

## ErrorCode规范

### 错误码分类与决策

| 分类 | 范围 | 使用场景 | 示例 |
|------|------|----------|------|
| **UserErrorCode** | 30001-30999 | 用户输入错误、权限不足 | 参数格式错误、无权限 |
| **SystemErrorCode** | 10001-10999 | 系统内部错误 | 数据库连接失败、服务不可用 |
| **BusinessErrorCode** | 50001-59999 | 业务规则校验失败 | 企业不存在、名称重复 |

### 定义与使用

```java
// ✅ 正确: 定义ErrorCode枚举
public enum BusinessErrorCode implements ErrorCode {
    ENTERPRISE_NOT_EXIST(50001, "企业不存在"),
    ENTERPRISE_NAME_EXIST(50002, "企业名称已存在");

    private final int code;
    private final String msg;
}

// ✅ 正确: 使用ErrorCode
if (enterprise == null) {
    return ResponseDTO.error(BusinessErrorCode.ENTERPRISE_NOT_EXIST);
}

// ❌ 错误: 硬编码
return ResponseDTO.error(50001, "企业不存在");
```

⚠️ **禁止硬编码**: 所有错误都必须使用ErrorCode枚举

---

## 注解规范

### 必需注解清单

| 注解 | 使用位置 | 说明 | 示例 |
|------|---------|------|------|
| `@Operation` | Controller方法 | API文档,必须包含@author | `@Operation(summary = "新增 @author wangxiao")` |
| `@Schema` | DTO/VO字段 | 字段说明 | `@Schema(description = "企业名称")` |
| `@SaCheckPermission` | Controller方法 | 权限控制 | `@SaCheckPermission("oa:enterprise:add")` |
| `@Valid` | Controller参数 | 参数校验 | `@RequestBody @Valid Form` |
| `@Transactional` | Service方法 | 事务控制 | `@Transactional(rollbackFor = Exception.class)` |

### 完整示例

```java
/**
 * 新增企业 @author wangxiao
 */
@Operation(summary = "新增企业 @author wangxiao")
@PostMapping("/oa/enterprise/add")
@SaCheckPermission("oa:enterprise:add")
public ResponseDTO<String> add(@RequestBody @Valid EnterpriseAddForm addForm) {
    return enterpriseService.add(addForm);
}
```

⚠️ **常见错误**:
- ❌ @Operation缺少@author wangxiao
- ❌ @Transactional不指定rollbackFor
- ❌ Controller参数缺少@Valid

---

## 数据库规范

### Entity规范

```java
@Data
@TableName("t_enterprise")
public class EnterpriseEntity extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long enterpriseId;

    private String enterpriseName;

    private Boolean disabledFlag;
}
```

### MyBatis-Plus分页(使用工具类)

```java
// ✅ 正确: 使用SmartPageUtil
Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
List<VO> list = dao.queryByPage(page, queryForm);
PageResult<VO> pageResult = SmartPageUtil.convert2PageResult(page, list);

// ❌ 错误: 手动分页
Page<VO> page = new Page<>(pageNum, pageSize); // 不要手动创建
```

### Mapper XML规范

```xml
<!-- 分页查询 @author wangxiao -->
<select id="queryByPage" resultType="net.lab1024...VO">
    SELECT
        enterprise_id,
        enterprise_name
    FROM t_enterprise
    <where>
        <if test="queryForm.enterpriseName != null and queryForm.enterpriseName != ''">
            AND enterprise_name LIKE CONCAT('%', #{queryForm.enterpriseName}, '%')
        </if>
    </where>
    ORDER BY create_time DESC
</select>
```

⚠️ **常见错误**:
- ❌ Entity不继承BaseEntity
- ❌ 手动实现分页逻辑
- ❌ Mapper XML缺少@author标识

---

## 异常处理

### Service层异常处理

```java
// ✅ 正确: 业务异常返回ResponseDTO
public ResponseDTO<String> delete(Long id) {
    EnterpriseEntity entity = enterpriseDao.selectById(id);
    if (entity == null) {
        return ResponseDTO.error(BusinessErrorCode.ENTERPRISE_NOT_EXIST);
    }
    enterpriseDao.deleteById(id);
    return ResponseDTO.ok();
}

// ❌ 错误: 抛出运行时异常
public void delete(Long id) {
    if (dao.selectById(id) == null) {
        throw new RuntimeException("企业不存在"); // 不要这样做!
    }
}
```

### 事务控制

```java
// ✅ 正确: 指定rollbackFor
@Transactional(rollbackFor = Exception.class)
public ResponseDTO<String> add(Form form) {
    // 业务逻辑
}

// ❌ 错误: 未指定rollbackFor
@Transactional
public ResponseDTO<String> add(Form form) {
    // 默认只回滚RuntimeException,checked异常不回滚!
}
```

⚠️ **强制要求**: @Transactional必须指定`rollbackFor = Exception.class`

---

## 工具推荐

### IDEA插件

- **Alibaba Java Coding Guidelines**: 阿里巴巴代码规范检查
- **SonarLint**: 代码质量实时检查
- **MyBatisX**: MyBatis-Plus增强工具
- **Lombok**: 简化Java代码

### 代码质量检查

```bash
# Maven编译检查
mvn clean compile

# 代码格式化
mvn spotless:apply

# 静态代码检查
mvn checkstyle:check
```

### 代码模板位置

项目中的 `.templates/` 目录包含以下模板:
- `ControllerTemplate.java` - Controller层模板
- `ServiceTemplate.java` - Service层模板
- `DaoTemplate.java` - Dao层模板
- `EntityTemplate.java` - Entity模板
- `FormTemplate.java` - Form模板
- `VOTemplate.java` - VO模板
- `EnumTemplate.java` - Enum模板

---

## 📌 最后检查清单

提交代码前,确保:

- [ ] 文件头包含`@Author: wangxiao`和`@Copyright 子午线高科智能科技 2025`
- [ ] 所有public方法包含`@author wangxiao`
- [ ] Controller方法返回`ResponseDTO<T>`
- [ ] 使用ErrorCode枚举,没有硬编码错误信息
- [ ] @Transactional指定`rollbackFor = Exception.class`
- [ ] 使用SmartBeanUtil和SmartPageUtil工具类
- [ ] Controller方法包含@Operation、@SaCheckPermission、@Valid注解
- [ ] DTO/VO字段包含@Schema注解

---

**持续改进**: 本文档会随项目发展持续更新,欢迎提出改进建议!
