# SmartAdmin 项目开发规范

> **项目**: SmartAdmin (前后端分离架构)
> **作者**: wangxiao
> **企业**: 子午线高科智能科技
> **更新时间**: 2025-10-04

---

## 🎯 强制规则 - 写代码前必读

### 📖 必读文档

**写任何代码之前，你必须先阅读以下文档：**

**前端开发**:
- `smart-admin-web-typescript/CODING_STANDARDS.md` - 前端代码规范
- `smart-admin-web-typescript/DEV_GUIDE.md` - 前端开发指南
- `smart-admin-web-typescript/.templates/` - 前端代码模板

**后端开发**:
- `smart-admin-api-java17-springboot3/BACKEND_CODING_STANDARDS.md` - 后端代码规范
- `smart-admin-api-java17-springboot3/.templates/` - 后端代码模板

**前后端协作**:
- `COLLABORATION_STANDARDS.md` - 前后端协作规范

---

## 🔒 不可协商的标准

### 作者和版权信息（每个文件都要检查！）

```
/*
 * [功能描述]
 *
 * @Author:    wangxiao
 * @Date:      2025-10-04
 * @Copyright  子午线高科智能科技 2025
 */
```

⚠️ **严格要求**:
- 作者：`wangxiao`（无例外）
- 版权：`子午线高科智能科技 2025`（不要写"Since"，不要写其他公司名）
- 每个public方法都要加：`@author wangxiao`

---

## 🎨 前端强制规则

### 文件创建 - 四目录对应原则

**必须在这四个位置创建文件**:
```
api/[business|support|system]/[模块]/[模块]-api.ts
api/[business|support|system]/[模块]/[模块]-model.ts
constants/[business|support|system]/[模块]/[模块]-const.ts
views/[business|support|system]/[模块]/[模块]-xxx.vue
```

### SmartEnum 常量系统（强制使用）

**✅ 正确写法**:
```typescript
export const STATUS_ENUM: SmartEnum<number> = {
  ACTIVE: { value: 1, desc: '启用' },
  DISABLED: { value: 0, desc: '禁用' },
};
```

**❌ 错误写法**:
```typescript
export const STATUS_ACTIVE = 1;  // 禁止！必须用SmartEnum
```

### 模板使用

**必须使用** `smart-admin-web-typescript/.templates/` 下的模板：
1. 复制模板文件
2. 替换 `[占位符]`
3. 保持文件头注释格式

### API 定义规范

```typescript
export const moduleApi = {
  /**
   * 分页查询 @author wangxiao
   */
  queryPage: (param: QueryForm) => {
    return postRequest<ResponseModel<PageResultModel<VO>>>('/path/queryPage', param);
  },
};
```

---

## ☕ 后端强制规则

### 四层架构

```
Controller → Service → Manager(可选) → Dao
```

**返回值类型**:
- Controller: `ResponseDTO<T>`（强制）
- Service: `ResponseDTO<T>` 或业务对象
- Dao: `Entity` 或集合

### Controller 模板

```java
@RestController
public class ModuleController {

    /**
     * 分页查询 @author wangxiao
     */
    @Operation(summary = "分页查询 @author wangxiao")
    @PostMapping("/module/queryPage")
    @SaCheckPermission("module:query")
    public ResponseDTO<PageResult<ModuleVO>> queryPage(
            @RequestBody @Valid ModuleQueryForm form) {
        return service.queryPage(form);
    }
}
```

### Service 模板

```java
@Service
public class ModuleService {

    /**
     * 新增 @author wangxiao
     */
    @Transactional(rollbackFor = Exception.class)  // 必须指定rollbackFor
    public ResponseDTO<String> add(ModuleAddForm form) {
        // 实现代码
        return ResponseDTO.ok();
    }
}
```

### ErrorCode 错误码（禁止硬编码！）

**✅ 正确写法**:
```java
if (entity == null) {
    return ResponseDTO.error(BusinessErrorCode.NOT_EXIST);
}
```

**❌ 错误写法**:
```java
return ResponseDTO.error(50001, "数据不存在");  // 禁止硬编码！
```

### 模板使用

**必须使用** `smart-admin-api-java17-springboot3/.templates/` 下的模板：
- ControllerTemplate.java
- ServiceTemplate.java
- DaoTemplate.java
- EntityTemplate.java
- FormTemplate.java
- VOTemplate.java
- EnumTemplate.java

---

## 🤝 前后端协作规范

### API 路径约定

```
格式: /[business|system|support]/[模块]/[操作]

前端: /business/oa/enterprise/queryPage
后端: @PostMapping("/oa/enterprise/page/query")
```

### 数据对象同步（严格要求！）

**前后端必须保持一致**:
| 类型 | 必须同步 |
|------|----------|
| QueryForm | 字段名和类型 |
| AddForm | 字段名和类型 |
| UpdateForm | 字段名和类型 |
| VO | 字段名和类型 |

### 分页参数

**pageNum 从 1 开始**（不是0）:
```typescript
// 前端
pageNum: 1  // 第一页

// 后端
pageNum = 1  // 第一页
```

---

## ⚠️ 常见错误（必须避免）

### 前端
- ❌ 缺少 `@Author` 或 `@Copyright`
- ❌ 用普通常量而不是 SmartEnum
- ❌ 四个目录不对应
- ❌ API方法缺少 `@author wangxiao`
- ❌ 不使用模板

### 后端
- ❌ Controller 返回原始对象（必须返回 `ResponseDTO<T>`）
- ❌ `@Transactional` 不写 `rollbackFor`
- ❌ 缺少 `@Operation` 或 `@author wangxiao`
- ❌ 硬编码错误码和错误信息
- ❌ 不使用模板

---

## 🚦 开发工作流

### 开始前
1. ✅ 阅读相关的 `CODING_STANDARDS.md`
2. ✅ 查看现有代码的写法
3. ✅ 找到 `.templates/` 下的模板

### 开发中
1. ✅ 复制模板，替换占位符
2. ✅ 添加正确的文件头注释
3. ✅ 给方法加 `@author wangxiao`
4. ✅ 遵循命名规范

### 提交前
1. ✅ 检查所有 `@Author` 和 `@Copyright` 是否正确
2. ✅ 检查前后端 Model 是否同步
3. ✅ 运行 linter/type-check
4. ✅ 测试功能

---

## 💾 Serena 项目记忆

项目规范已保存在: `smartadmin_code_standards_solution`

命令:
- `/sc:load` - 加载项目上下文
- `/sc:save` - 保存会话进度

---

## 🎯 快速开始示例

### 前端新功能 - 客户管理

```
1. 创建文件:
   api/business/crm/customer/customer-api.ts
   api/business/crm/customer/customer-model.ts
   constants/business/crm/customer/customer-const.ts
   views/business/crm/customer/customer-list.vue

2. 使用 SmartEnum:
   export const CUSTOMER_STATUS_ENUM: SmartEnum<number> = {...}

3. 所有API方法加 @author wangxiao
```

### 后端新功能 - 订单管理

```
1. 创建文件:
   module/business/order/controller/OrderController.java
   module/business/order/service/OrderService.java
   module/business/order/dao/OrderDao.java
   module/business/order/domain/entity/OrderEntity.java
   module/business/order/domain/form/OrderQueryForm.java
   module/business/order/domain/vo/OrderVO.java

2. 使用 .templates/ 下的模板
3. Controller 返回 ResponseDTO<T>
4. 加 @Transactional(rollbackFor = Exception.class)
5. 使用 ErrorCode 枚举
```

---

**🔴 重要提醒**: 这个项目有非常严格的代码规范。写代码前一定要先看文档。一定要用模板。一定要加 `@author wangxiao`。
