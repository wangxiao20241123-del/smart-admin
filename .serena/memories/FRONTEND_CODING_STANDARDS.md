# SmartAdmin 前端编码规范

> **作者**: wangxiao | **企业**: 子午线高科智能科技 | **更新**: 2025-10-07

---

## 🏗️ 目录结构规范

### 三层架构原则

所有目录按 **business/support/system** 分类：

```
src/
├── api/              # API层
│   ├── business/     # 业务API (oa, erp等)
│   ├── support/      # 支撑API (文件、日志等)
│   └── system/       # 系统API (登录、菜单等)
├── constants/        # 常量枚举
├── components/       # 组件
├── views/            # 视图
└── router/           # 路由
```

### 四目录对应原则 ⚠️

**API、常量、组件、视图必须路径对应**

✅ 正确:
```
api/business/oa/enterprise/enterprise-api.ts
constants/business/oa/enterprise/enterprise-const.ts
components/business/oa/enterprise/enterprise-select.vue
views/business/oa/enterprise/enterprise-list.vue
```

❌ 错误:
```
api/enterprise-api.ts                # ❌ 缺少分类
constants/enterprise.ts              # ❌ 命名不规范
components/EnterpriseSelect.vue      # ❌ 路径不对应
```

---

## 📝 命名规范

### 统一命名表

| 类型 | 格式 | 示例 | 说明 |
|------|------|------|------|
| **文件** | | | |
| API | `{模块}-api.ts` | `login-api.ts` | 小写-连字符 |
| 常量 | `{模块}-const.ts` | `menu-const.ts` | 小写-连字符 |
| Model | `{功能}-model.ts` | `response-model.ts` | 小写-连字符 |
| 页面 | `{功能}.vue` | `enterprise-list.vue` | 小写-连字符 |
| 模态框 | `{功能}-modal.vue` | `user-form-modal.vue` | 小写-连字符 |
| **变量** | | | |
| API对象 | `{模块}Api` | `loginApi` | 驼峰+Api后缀 |
| 枚举 | `{名称}_ENUM` | `GENDER_ENUM` | 全大写+下划线 |
| 普通变量 | 驼峰 | `userId`, `userName` | 驼峰命名 |
| 列表 | `{对象}List` | `detailList`, `userList` | 驼峰+List后缀 |
| **函数** | | | |
| 获取数据 | `get{对象}` | `getDetail()` | 动词+对象 |
| 查询列表 | `query{对象}` | `queryList()` | 动词+对象 |
| 更新数据 | `update{对象}` | `updateUser()` | 动词+对象 |
| 删除数据 | `delete{对象}` | `deleteById()` | 动词+对象 |
| 显示界面 | `show{对象}` | `showModal()` | 动词+对象 |

---

## 🎯 SmartEnum 常量体系

### 核心原则：杜绝魔法数字

```typescript
// 定义
import { SmartEnum } from '/@/types/smart-enum';

export const GENDER_ENUM: SmartEnum<number> = {
  UNKNOWN: { value: 0, desc: '未知' },
  MAN: { value: 1, desc: '男' },
  WOMAN: { value: 2, desc: '女' },
};

export const STATUS_ENUM: SmartEnum<number> = {
  DISABLED: { value: 0, desc: '禁用' },
  ENABLED: { value: 1, desc: '启用' },
};
```

### 统一导出与使用

**导出** (`constants/index.ts`):
```typescript
import menu from './system/menu-const';
import enterprise from './business/oa/enterprise-const';

export default {
  ...menu,
  ...enterprise,
};
```

**使用**:
```vue
<script setup>
import smartEnumPlugin from '/@/constants';

// 获取描述
const desc = smartEnumPlugin.getDescByValue('GENDER_ENUM', 1);  // "男"

// 获取列表
const list = smartEnumPlugin.getValueDescList('GENDER_ENUM');
// [{ value: 0, desc: '未知' }, { value: 1, desc: '男' }, ...]
</script>
```

---

## 🔌 API 设计规范

### 标准结构

```typescript
/*
 * 模块功能说明
 *
 * @Author:    wangxiao
 * @Date:      2025-10-07
 * @Copyright  子午线高科智能科技 2025
 */
import { getRequest, postRequest } from '/@/lib/axios';

export const loginApi = {
  /**
   * 用户登录 @author wangxiao
   */
  login: (param: LoginForm) => {
    return postRequest<ResponseModel<LoginVO>>('/login', param);
  },

  /**
   * 退出登录 @author wangxiao
   */
  logout: () => {
    return getRequest('/login/logout');
  },
};
```

### 关键要求

| 要求 | 说明 | 检查点 |
|------|------|--------|
| ✅ 文件头 | 作者、日期、Copyright | `@Author: wangxiao` |
| ✅ 方法注释 | 包含 `@author wangxiao` | 所有API方法 |
| ✅ 类型约束 | TypeScript 泛型 | `ResponseModel<T>` |
| ✅ 工具函数 | `getRequest/postRequest` | 不直接用axios |
| ✅ RESTful | URL 语义化 | `/user/login` 不是 `/api1` |

### HTTP方法映射

| 操作 | HTTP | 工具函数 |
|------|------|----------|
| 查询 | GET | `getRequest` |
| 新增 | POST | `postRequest` |
| 修改 | POST/PUT | `postRequest` |
| 删除 | POST/DELETE | `postRequest` |

---

## 🧩 组件设计规范

### 标准结构

```vue
<!--
  组件功能描述

  @Author:    wangxiao
  @Date:      2025-10-07
  @Copyright  子午线高科智能科技 2025
-->
<template>
  <div class="user-detail">
    <!-- 内容 -->
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { userApi } from '/@/api/system/user-api';
import { SmartLoading } from '/@/components/framework/smart-loading';

// ========== Props/Emits ==========
const props = defineProps<{ userId: number }>();
const emit = defineEmits<{ refresh: [] }>();

// ========== 响应式数据 ==========
const detail = ref<UserVO>({});
const loading = ref(false);

// ========== 生命周期 ==========
onMounted(() => getDetail());

// ========== 业务方法 ==========
async function getDetail() {
  try {
    loading.value = true;
    const res = await userApi.detail(props.userId);
    detail.value = res.data;
  } catch (error) {
    smartSentry.captureError(error);
  } finally {
    loading.value = false;
    SmartLoading.hide();
  }
}
</script>

<style scoped lang="less">
.user-detail {
  padding: 20px;
}
</style>
```

### 代码分组顺序

1. Props/Emits 定义
2. 路由/参数获取
3. 响应式数据
4. 计算属性
5. 生命周期钩子
6. 业务方法

### 关键要求

| 要求 | 说明 |
|------|------|
| ✅ 头部注释完整 | 功能、作者、日期、Copyright |
| ✅ `<script setup lang="ts">` | 使用Composition API |
| ✅ TypeScript类型约束 | 所有ref、props、emits都有类型 |
| ✅ 错误处理 | 用 `smartSentry.captureError` |
| ✅ 加载状态 | 用 `SmartLoading` |
| ✅ 权限控制 | 用 `v-if="$privilege('code')"` |

---

## 📦 Model 设计规范

### 基础模型

```typescript
// response-model.ts
export interface ResponseModel<T> {
  code: number;
  data: T;
  msg?: string;
  ok: boolean;
}

// page-param-model.ts
export interface PageParamModel {
  pageNum: number;
  pageSize: number;
}

// page-result-model.ts
export interface PageResultModel<T> {
  total: number;
  list: Array<T>;
}
```

### 业务Model示例

```typescript
// api/business/user/user-model.ts

// 查询表单（必须继承PageParamModel）
export interface UserQueryForm extends PageParamModel {
  userName?: string;
  phone?: string;
}

// 新增表单
export interface UserAddForm {
  userName: string;
  phone: string;
}

// 更新表单（复用AddForm）
export interface UserUpdateForm extends UserAddForm {
  userId: number;
}

// 展示VO
export interface UserVO {
  userId: number;
  userName: string;
  phone: string;
  createTime?: string;
}
```

### 设计原则

1. ✅ 查询表单继承 `PageParamModel`
2. ✅ Form/VO 分离
3. ✅ 可选字段用 `?`
4. ✅ 避免 `any`

---

## 💬 代码注释规范

### 文件头注释

```typescript
/*
 * 模块功能描述
 *
 * @Author:    wangxiao
 * @Date:      2025-10-07
 * @Copyright  子午线高科智能科技 2025
 */
```

### 函数注释

```typescript
/**
 * 用户登录 @author wangxiao
 */
login: (param: LoginForm) => {
  return postRequest('/login', param);
},

/**
 * 查询用户列表
 * @param form 查询条件
 * @returns 分页数据
 * @author wangxiao
 */
async function queryList(form: UserQueryForm) {
  // ...
}
```

### 复杂逻辑注释

```typescript
// ========== 数据权限处理 ==========
function handleDataScope() {
  // 1. 获取用户权限
  // 2. 过滤数据
  // 3. 返回结果
}
```

---

## ✅ 开发检查清单

### 开发前
- [ ] 确认功能分类 (business/support/system)
- [ ] 规划四目录对应结构
- [ ] 检查可复用组件
- [ ] 确认权限点

### 开发中
- [ ] 文件命名规范
- [ ] 常量用 SmartEnum
- [ ] API 方法有注释
- [ ] 组件有头部注释
- [ ] TypeScript 类型完整
- [ ] 错误用 smartSentry

### 开发后
- [ ] ESLint 通过
- [ ] TypeScript 编译无错
- [ ] 功能自测完成
- [ ] 代码已提交

### Code Review
- [ ] 目录结构符合规范
- [ ] 命名统一规范
- [ ] 无魔法数字
- [ ] 无重复代码
- [ ] 错误处理完善
- [ ] 权限控制正确

---

## 📚 相关文档

- [前端开发指南](./FRONTEND_DEV_GUIDE.md)
- [前端原型开发指南](./FRONTEND_PROTOTYPE_DEV_GUIDE.md)
- [Vue3 官方文档](https://cn.vuejs.org/)
- [Ant Design Vue](https://antdv.com/)

---

**核心原则**: 统一规范、类型安全、清晰注释、可维护性优先。
