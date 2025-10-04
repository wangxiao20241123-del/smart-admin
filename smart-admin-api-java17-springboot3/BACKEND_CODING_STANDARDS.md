# SmartAdmin后端代码规范标准

> **作者**: wangxiao
> **企业**: 子午线高科智能科技
> **更新时间**: 2025-10-04

---

## 📋 目录

1. [项目结构](#项目结构)
2. [包命名规范](#包命名规范)
3. [四层架构设计](#四层架构设计)
4. [类命名规范](#类命名规范)
5. [ResponseDTO响应规范](#responseDto响应规范)
6. [ErrorCode错误码规范](#errorcode错误码规范)
7. [注解规范](#注解规范)
8. [代码注释规范](#代码注释规范)
9. [数据库规范](#数据库规范)
10. [异常处理规范](#异常处理规范)
11. [最佳实践](#最佳实践)

---

## 项目结构

### 模块划分

```
smart-admin-api-java17-springboot3/
├── sa-base/              # 基础模块(框架、工具、公共组件)
│   ├── common/           # 公共组件
│   ├── config/           # 配置类
│   ├── constant/         # 常量定义
│   └── module/support/   # 支撑功能
├── sa-admin/             # 业务模块(具体业务实现)
│   ├── module/business/  # 业务功能
│   └── module/system/    # 系统功能
└── pom.xml
```

### 模块职责

| 模块 | 职责 | 示例 |
|------|------|------|
| **sa-base** | 框架基础、工具类、公共组件 | ResponseDTO、工具类、配置类 |
| **sa-admin** | 业务实现、系统功能 | Controller、Service、Dao |

---

## 包命名规范

### 业务分类

所有业务代码按照以下三类进行组织:

1. **business**: 核心业务功能
2. **system**: 系统管理功能
3. **support**: 支撑功能(非核心业务)

### 包结构示例

```java
// ✅ 正确: 业务功能
net.lab1024.sa.admin.module.business.oa.enterprise

// ✅ 正确: 系统功能
net.lab1024.sa.admin.module.system.employee

// ❌ 错误: 没有分类
net.lab1024.sa.admin.enterprise
```

---

## 四层架构设计

### 架构层次

```
Controller (控制层)
    ↓
Service (服务层)
    ↓
Manager (管理层, 可选)
    ↓
Dao (数据访问层)
```

### 各层职责

| 层级 | 职责 | 注解 | 返回值 |
|------|------|------|--------|
| **Controller** | 接口定义、参数校验、权限控制 | `@RestController` | `ResponseDTO<T>` |
| **Service** | 业务逻辑、事务控制 | `@Service` | `ResponseDTO<T>` 或业务对象 |
| **Manager** | 复杂业务编排、缓存管理 | `@Component` | 业务对象 |
| **Dao** | 数据库操作 | `@Mapper` | Entity 或 集合 |

### 代码示例

#### Controller层

```java
/*
 * 企业管理
 *
 * @Author:    wangxiao
 * @Date:      2025-10-04
 * @Copyright  子午线高科智能科技 2025
 */
@RestController
@Api(tags = {SwaggerTagConst.AdminBusiness.MANAGER_OA_ENTERPRISE})
public class EnterpriseController {

    @Resource
    private EnterpriseService enterpriseService;

    /**
     * 分页查询企业 @author wangxiao
     */
    @Operation(summary = "分页查询企业 @author wangxiao")
    @PostMapping("/oa/enterprise/page/query")
    @SaCheckPermission("oa:enterprise:query")
    public ResponseDTO<PageResult<EnterpriseVO>> queryByPage(
            @RequestBody @Valid EnterpriseQueryForm queryForm) {
        return enterpriseService.queryByPage(queryForm);
    }

    /**
     * 新增企业 @author wangxiao
     */
    @Operation(summary = "新增企业 @author wangxiao")
    @PostMapping("/oa/enterprise/add")
    @SaCheckPermission("oa:enterprise:add")
    public ResponseDTO<String> add(@RequestBody @Valid EnterpriseAddForm addForm) {
        return enterpriseService.add(addForm);
    }
}
```

#### Service层

```java
/*
 * 企业服务
 *
 * @Author:    wangxiao
 * @Date:      2025-10-04
 * @Copyright  子午线高科智能科技 2025
 */
@Service
public class EnterpriseService {

    @Resource
    private EnterpriseDao enterpriseDao;

    /**
     * 分页查询 @author wangxiao
     */
    public ResponseDTO<PageResult<EnterpriseVO>> queryByPage(EnterpriseQueryForm queryForm) {
        // 1. 构建查询条件
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);

        // 2. 查询数据
        List<EnterpriseVO> list = enterpriseDao.queryByPage(page, queryForm);

        // 3. 构建分页结果
        PageResult<EnterpriseVO> pageResult = SmartPageUtil.convert2PageResult(page, list);
        return ResponseDTO.ok(pageResult);
    }

    /**
     * 新增 @author wangxiao
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<String> add(EnterpriseAddForm addForm) {
        // 1. 数据校验
        // 2. 构建实体
        EnterpriseEntity entity = SmartBeanUtil.copy(addForm, EnterpriseEntity.class);
        // 3. 保存数据
        enterpriseDao.insert(entity);
        return ResponseDTO.ok();
    }
}
```

#### Dao层

```java
/*
 * 企业数据访问
 *
 * @Author:    wangxiao
 * @Date:      2025-10-04
 * @Copyright  子午线高科智能科技 2025
 */
@Mapper
public interface EnterpriseDao extends BaseMapper<EnterpriseEntity> {

    /**
     * 分页查询 @author wangxiao
     */
    List<EnterpriseVO> queryByPage(@Param("page") Page<?> page,
                                    @Param("queryForm") EnterpriseQueryForm queryForm);
}
```

---

## 类命名规范

### Domain对象命名

| 类型 | 后缀 | 用途 | 示例 |
|------|------|------|------|
| **Entity** | Entity | 数据库实体 | `EnterpriseEntity` |
| **QueryForm** | QueryForm | 查询表单 | `EnterpriseQueryForm` |
| **AddForm** | AddForm | 新增表单 | `EnterpriseAddForm` |
| **UpdateForm** | UpdateForm | 更新表单 | `EnterpriseUpdateForm` |
| **VO** | VO | 视图对象(返回前端) | `EnterpriseVO` |
| **DTO** | DTO | 数据传输对象 | `EnterpriseDTO` |

### 示例代码

```java
// ✅ 正确: Entity
@TableName("t_enterprise")
public class EnterpriseEntity extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long enterpriseId;
    private String enterpriseName;
}

// ✅ 正确: QueryForm
public class EnterpriseQueryForm extends PageParam {
    @Schema(description = "企业名称")
    private String enterpriseName;
}

// ✅ 正确: VO
public class EnterpriseVO {
    @Schema(description = "企业ID")
    private Long enterpriseId;
    @Schema(description = "企业名称")
    private String enterpriseName;
}
```

### 枚举类命名

```java
// ✅ 正确: Enum后缀
public enum EnterpriseTypeEnum implements BaseEnum {
    NORMAL(1, "正常企业"),
    DISABLED(0, "禁用企业");

    private final Integer value;
    private final String desc;
}
```

---

## ResponseDTO响应规范

### 统一响应结构

```java
public class ResponseDTO<T> {
    private Integer code;      // 响应码
    private String level;      // 日志级别
    private String msg;        // 响应消息
    private Boolean ok;        // 是否成功
    private T data;           // 响应数据
    private Integer dataType; // 数据类型
}
```

### 使用规范

```java
// ✅ 正确: 成功响应
return ResponseDTO.ok();
return ResponseDTO.ok(data);

// ✅ 正确: 业务错误
return ResponseDTO.error(UserErrorCode.DATA_NOT_EXIST);

// ✅ 正确: 系统错误
return ResponseDTO.error(SystemErrorCode.SYSTEM_ERROR);

// ❌ 错误: 直接返回业务对象
return enterprise; // Controller必须返回ResponseDTO
```

### Controller返回值规范

```java
// ✅ 所有Controller方法必须返回ResponseDTO
@PostMapping("/query")
public ResponseDTO<List<EnterpriseVO>> query() {
    return ResponseDTO.ok(list);
}

// ❌ 禁止直接返回业务对象
@PostMapping("/query")
public List<EnterpriseVO> query() {
    return list;
}
```

---

## ErrorCode错误码规范

### 错误码分类

| 分类 | 范围 | 用途 | 示例 |
|------|------|------|------|
| **UserErrorCode** | 30001-30999 | 用户侧错误 | 参数错误、权限不足 |
| **SystemErrorCode** | 10001-10999 | 系统错误 | 系统异常、服务不可用 |
| **BusinessErrorCode** | 50001-59999 | 业务错误 | 业务规则校验失败 |

### 定义规范

```java
/*
 * 业务错误码
 *
 * @Author:    wangxiao
 * @Date:      2025-10-04
 * @Copyright  子午线高科智能科技 2025
 */
public enum BusinessErrorCode implements ErrorCode {

    ENTERPRISE_NOT_EXIST(50001, "企业不存在"),
    ENTERPRISE_NAME_EXIST(50002, "企业名称已存在"),
    ENTERPRISE_DISABLED(50003, "企业已被禁用");

    private final int code;
    private final String msg;

    BusinessErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}
```

### 使用示例

```java
// ✅ 正确: 使用ErrorCode
if (enterprise == null) {
    return ResponseDTO.error(BusinessErrorCode.ENTERPRISE_NOT_EXIST);
}

// ❌ 错误: 硬编码错误信息
if (enterprise == null) {
    return ResponseDTO.error(50001, "企业不存在");
}
```

---

## 注解规范

### 必需注解清单

| 注解 | 使用位置 | 说明 |
|------|---------|------|
| `@Operation` | Controller方法 | API文档说明,必须包含@author |
| `@Schema` | DTO/VO字段 | 字段说明 |
| `@SaCheckPermission` | Controller方法 | 权限控制 |
| `@Valid` | Controller参数 | 参数校验 |
| `@Transactional` | Service方法 | 事务控制 |

### Controller注解示例

```java
/**
 * 企业管理控制器
 *
 * @Author:    wangxiao
 * @Date:      2025-10-04
 * @Copyright  子午线高科智能科技 2025
 */
@RestController
@Api(tags = {SwaggerTagConst.AdminBusiness.MANAGER_OA_ENTERPRISE})
public class EnterpriseController {

    /**
     * 新增企业 @author wangxiao
     */
    @Operation(summary = "新增企业 @author wangxiao")
    @PostMapping("/oa/enterprise/add")
    @SaCheckPermission("oa:enterprise:add")
    public ResponseDTO<String> add(@RequestBody @Valid EnterpriseAddForm addForm) {
        return enterpriseService.add(addForm);
    }
}
```

### DTO/VO注解示例

```java
public class EnterpriseVO {

    @Schema(description = "企业ID")
    private Long enterpriseId;

    @Schema(description = "企业名称")
    @NotBlank(message = "企业名称不能为空")
    @Length(max = 200, message = "企业名称最多200字符")
    private String enterpriseName;

    @Schema(description = "统一社会信用代码")
    @Length(max = 200, message = "统一社会信用代码最多200字符")
    private String unifiedSocialCreditCode;
}
```

---

## 代码注释规范

### 文件头注释

**强制要求**:每个Java文件必须包含完整的文件头注释。

```java
/*
 * [功能模块名称]
 *
 * @Author:    wangxiao
 * @Date:      2025-10-04
 * @Copyright  子午线高科智能科技 2025
 */
```

### 方法注释

**强制要求**:所有public方法必须包含`@author wangxiao`标识。

```java
/**
 * 分页查询企业 @author wangxiao
 */
public ResponseDTO<PageResult<EnterpriseVO>> queryByPage(EnterpriseQueryForm queryForm) {
    // 实现代码
}

/**
 * 新增企业 @author wangxiao
 */
@Transactional(rollbackFor = Exception.class)
public ResponseDTO<String> add(EnterpriseAddForm addForm) {
    // 实现代码
}
```

### 业务逻辑注释

对于复杂的业务逻辑,使用分步注释:

```java
public ResponseDTO<String> add(EnterpriseAddForm addForm) {
    // 1. 数据校验
    EnterpriseEntity existEntity = enterpriseDao.getByName(addForm.getEnterpriseName());
    if (existEntity != null) {
        return ResponseDTO.error(BusinessErrorCode.ENTERPRISE_NAME_EXIST);
    }

    // 2. 构建实体
    EnterpriseEntity entity = SmartBeanUtil.copy(addForm, EnterpriseEntity.class);
    entity.setDisabledFlag(Boolean.FALSE);

    // 3. 保存数据
    enterpriseDao.insert(entity);

    return ResponseDTO.ok();
}
```

---

## 数据库规范

### Entity规范

```java
/*
 * 企业实体
 *
 * @Author:    wangxiao
 * @Date:      2025-10-04
 * @Copyright  子午线高科智能科技 2025
 */
@Data
@TableName("t_enterprise")
public class EnterpriseEntity extends BaseEntity {

    /**
     * 企业ID
     */
    @TableId(type = IdType.AUTO)
    private Long enterpriseId;

    /**
     * 企业名称
     */
    private String enterpriseName;

    /**
     * 禁用状态
     */
    private Boolean disabledFlag;
}
```

### MyBatis-Plus分页

```java
// ✅ 正确: 使用SmartPageUtil
Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
List<EnterpriseVO> list = enterpriseDao.queryByPage(page, queryForm);
PageResult<EnterpriseVO> pageResult = SmartPageUtil.convert2PageResult(page, list);
```

### Mapper XML规范

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="net.lab1024.sa.admin.module.business.oa.enterprise.dao.EnterpriseDao">

    <!-- 分页查询 @author wangxiao -->
    <select id="queryByPage" resultType="net.lab1024.sa.admin.module.business.oa.enterprise.domain.vo.EnterpriseVO">
        SELECT
            enterprise_id,
            enterprise_name,
            unified_social_credit_code,
            disabled_flag
        FROM t_enterprise
        <where>
            <if test="queryForm.enterpriseName != null and queryForm.enterpriseName != ''">
                AND enterprise_name LIKE CONCAT('%', #{queryForm.enterpriseName}, '%')
            </if>
            <if test="queryForm.disabledFlag != null">
                AND disabled_flag = #{queryForm.disabledFlag}
            </if>
        </where>
        ORDER BY create_time DESC
    </select>

</mapper>
```

---

## 异常处理规范

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
    if (enterpriseDao.selectById(id) == null) {
        throw new RuntimeException("企业不存在");
    }
}
```

### 事务控制

```java
// ✅ 正确: 使用@Transactional,指定rollbackFor
@Transactional(rollbackFor = Exception.class)
public ResponseDTO<String> add(EnterpriseAddForm addForm) {
    // 业务逻辑
}

// ❌ 错误: 未指定rollbackFor
@Transactional
public ResponseDTO<String> add(EnterpriseAddForm addForm) {
    // 业务逻辑
}
```

---

## 最佳实践

### DO/DON'T清单

#### ✅ DO (应该这样做)

- 所有Controller方法返回`ResponseDTO<T>`
- 所有public方法包含`@author wangxiao`
- 使用`SmartBeanUtil.copy()`进行对象转换
- 使用`SmartPageUtil`处理分页
- ErrorCode枚举管理错误码
- `@Transactional`指定`rollbackFor = Exception.class`
- Entity继承`BaseEntity`获取公共字段
- 使用`@Schema`注解为字段添加说明

#### ❌ DON'T (不应该这样做)

- Controller直接返回业务对象
- 硬编码错误码和错误信息
- 使用魔法数字
- 缺少方法注释和@author标识
- 事务注解不指定rollbackFor
- 手动实现分页逻辑
- 直接使用BeanUtils.copyProperties()

### 代码质量检查

```bash
# Maven编译检查
mvn clean compile

# 代码格式化
mvn spotless:apply

# 静态代码检查
mvn checkstyle:check
```

---

## 工具推荐

### IDEA插件

- **Alibaba Java Coding Guidelines**: 阿里巴巴代码规范检查
- **SonarLint**: 代码质量检查
- **MyBatisX**: MyBatis-Plus增强
- **Lombok**: 简化Java代码

### 代码模板

参考项目中的 `.templates/` 目录获取标准化代码模板。

---

**持续改进**: 本文档会随项目发展持续更新,欢迎提出改进建议!
