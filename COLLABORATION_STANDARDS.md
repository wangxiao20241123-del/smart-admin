# SmartAdmin前后端协作规范

> **作者**: wangxiao
> **企业**: 子午线高科智能科技
> **更新时间**: 2025-10-04

---

## 📋 目录

1. [接口规范](#接口规范)
2. [数据交互规范](#数据交互规范)
3. [错误处理协作](#错误处理协作)
4. [开发协作流程](#开发协作流程)
5. [联调规范](#联调规范)

---

## 接口规范

### API路径命名

**前后端统一规范**:

```
格式: /[分类]/[模块]/[操作]

分类: business | system | support
操作: add | update | delete | query | queryPage | detail
```

**示例**:

| 功能 | 前端API路径 | 后端Controller路径 |
|------|-----------|------------------|
| 企业分页查询 | `/business/oa/enterprise/queryPage` | `@PostMapping("/oa/enterprise/page/query")` |
| 新增企业 | `/business/oa/enterprise/add` | `@PostMapping("/oa/enterprise/add")` |
| 更新企业 | `/business/oa/enterprise/update` | `@PostMapping("/oa/enterprise/update")` |

### 请求方法约定

| 操作类型 | HTTP方法 | 说明 |
|---------|---------|------|
| 查询列表 | POST | 支持复杂查询条件 |
| 查询详情 | GET | RESTful风格,使用路径参数 |
| 新增 | POST | 请求体传递数据 |
| 更新 | POST | 请求体传递数据 |
| 删除 | GET | RESTful风格,使用路径参数 |

---

## 数据交互规范

### 统一响应结构

**后端定义** (Java):

```java
public class ResponseDTO<T> {
    private Integer code;      // 响应码
    private String msg;        // 响应消息
    private Boolean ok;        // 是否成功
    private T data;           // 响应数据
}
```

**前端定义** (TypeScript):

```typescript
export interface ResponseModel<T = any> {
  code: number;
  msg: string;
  ok: boolean;
  data: T;
}
```

### 数据对象映射

| 后端 | 前端 | 用途 |
|------|------|------|
| `QueryForm` | `QueryForm` | 查询条件 |
| `AddForm` | `AddForm` | 新增表单 |
| `UpdateForm` | `UpdateForm` | 更新表单 |
| `VO` | `VO` | 视图对象(响应数据) |

**示例映射**:

**后端** `EnterpriseQueryForm.java`:
```java
public class EnterpriseQueryForm extends PageParam {
    @Schema(description = "企业名称")
    private String enterpriseName;
}
```

**前端** `enterprise-model.ts`:
```typescript
export interface EnterpriseQueryForm extends PageParam {
  enterpriseName?: string; // 企业名称
}
```

### 分页参数

**后端** (Java):
```java
public class PageParam {
    private Integer pageNum = 1;   // 页码
    private Integer pageSize = 10; // 每页数量
}

public class PageResult<T> {
    private Long total;           // 总记录数
    private List<T> list;        // 数据列表
}
```

**前端** (TypeScript):
```typescript
export interface PageParam {
  pageNum: number;   // 页码,从1开始
  pageSize: number;  // 每页数量
}

export interface PageResultModel<T> {
  total: number;     // 总记录数
  list: T[];        // 数据列表
}
```

---

## 错误处理协作

### 错误码体系

**后端定义错误码范围**:

| 错误类型 | 错误码范围 | 前端处理方式 |
|---------|----------|------------|
| UserErrorCode | 30001-30999 | 提示用户,允许重试 |
| SystemErrorCode | 10001-10999 | 系统错误提示,联系管理员 |
| BusinessErrorCode | 50001-59999 | 业务规则提示 |

### 前端错误处理流程

```typescript
// 1. 后端返回错误
const response = await enterpriseApi.add(form);

// 2. 前端统一拦截处理
if (!response.ok) {
  // 根据错误码范围判断
  if (response.code >= 30001 && response.code <= 30999) {
    // 用户错误: 直接提示
    message.error(response.msg);
  } else if (response.code >= 10001 && response.code <= 10999) {
    // 系统错误: 通用提示
    message.error('系统异常,请联系管理员');
  } else {
    // 业务错误: 直接提示
    message.error(response.msg);
  }
  return;
}

// 3. 成功处理
message.success('操作成功');
```

### 后端错误返回示例

```java
// 业务错误
if (enterprise == null) {
    return ResponseDTO.error(BusinessErrorCode.ENTERPRISE_NOT_EXIST);
}

// 对应前端接收
{
  "code": 50001,
  "msg": "企业不存在",
  "ok": false,
  "data": null
}
```

---

## 开发协作流程

### 新功能开发流程

```
1. 需求评审
   ├─ 前后端共同参与
   ├─ 确定接口定义
   └─ 确定数据结构

2. 接口设计
   ├─ 后端: 定义API路径、请求参数、响应结构
   └─ 前端: 确认API定义,创建Model和API文件

3. 并行开发
   ├─ 后端: 实现Controller/Service/Dao
   └─ 前端: 实现页面/组件/API调用

4. 联调测试
   ├─ 后端: 提供Swagger文档
   ├─ 前端: 使用Postman/Swagger验证接口
   └─ 联调: 前后端集成测试

5. 代码审查
   ├─ 后端: 检查ResponseDTO、ErrorCode、注解
   └─ 前端: 检查Model定义、API调用、错误处理
```

### 接口变更流程

```
1. 发起变更
   └─ 后端: 提前通知前端接口变更

2. 更新文档
   ├─ 后端: 更新Swagger注解
   └─ 前端: 更新Model定义

3. 版本控制
   ├─ 重大变更: 新增版本号(/v2/...)
   └─ 兼容变更: 保持原接口,新增字段

4. 联调验证
   └─ 前后端共同验证变更影响
```

---

## 联调规范

### 环境配置

**后端环境**:
```yaml
# application-dev.yaml
server:
  port: 1024

# CORS配置
cors:
  allowed-origins: http://localhost:9527
```

**前端环境**:
```typescript
// .env.development
VITE_API_BASE_URL=http://localhost:1024
```

### Swagger文档

**后端提供**:
- Swagger UI地址: `http://localhost:1024/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:1024/v3/api-docs`

**前端使用**:
1. 访问Swagger UI查看接口文档
2. 使用"Try it out"测试接口
3. 确认请求参数和响应结构

### 联调检查清单

#### 后端检查

- [ ] Controller路径正确
- [ ] @Operation注解包含@author
- [ ] @SaCheckPermission权限码正确
- [ ] 返回ResponseDTO<T>
- [ ] ErrorCode定义完整
- [ ] Swagger文档可访问

#### 前端检查

- [ ] API路径与后端一致
- [ ] Model定义与后端VO/Form一致
- [ ] 分页参数正确(pageNum从1开始)
- [ ] 错误处理完整
- [ ] 权限控制(v-if="$privilege('code')")

### 常见问题

#### 1. 跨域问题

**问题**: 前端调用后端接口报CORS错误

**解决**:
- 后端检查CORS配置
- 确认`allowed-origins`包含前端地址

#### 2. 分页参数不一致

**问题**: 前端传pageNum=0,后端期望从1开始

**解决**:
- 统一约定: pageNum从1开始
- 前端使用PageParam基类
- 后端使用SmartPageUtil处理

#### 3. 枚举值不匹配

**问题**: 前端传字符串,后端期望数字

**解决**:
- 后端使用SmartEnum定义
- 前端使用SmartEnum对应的TypeScript定义
- 传递value值,不传desc

#### 4. 日期格式问题

**问题**: 前端传"2025-10-04",后端解析失败

**解决**:
- 统一使用ISO 8601格式: `2025-10-04T10:30:00`
- 后端使用LocalDateTime
- 前端使用dayjs处理

---

## 最佳实践

### DO/DON'T清单

#### ✅ DO (应该这样做)

**后端**:
- 所有接口返回ResponseDTO<T>
- 使用ErrorCode枚举管理错误
- Swagger注解完整
- @author标识完整

**前端**:
- API路径与后端一致
- Model定义与后端同步
- 统一错误处理
- 使用TypeScript类型约束

#### ❌ DON'T (不应该这样做)

**后端**:
- 直接返回Entity
- 硬编码错误信息
- 缺少Swagger文档
- 随意更改接口

**前端**:
- 使用any类型
- 缺少错误处理
- 直接硬编码接口路径
- 忽略分页参数

---

## 沟通协作

### 日常沟通

- **接口变更**: 提前1天通知
- **数据结构变更**: 提前2天通知
- **重大重构**: 提前1周评审

### 问题反馈

- **前端发现后端问题**: 提供请求参数、响应结果、错误截图
- **后端发现前端问题**: 提供接口调用日志、参数校验结果

### 文档维护

- **后端**: 保持Swagger文档与代码同步
- **前端**: 保持Model定义与后端VO/Form同步
- **共同**: 更新COLLABORATION_STANDARDS.md

---

**持续改进**: 本文档会随项目发展持续更新,欢迎提出改进建议!
