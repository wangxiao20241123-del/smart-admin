# SmartAdmin 代码规范标准

> **版本**: v1.0
> **更新时间**: 2025-10-04
> **维护团队**: 1024创新实验室

---

## 📖 目录

1. [目录结构规范](#目录结构规范)
2. [命名规范](#命名规范)
3. [SmartEnum常量体系](#smartenum常量体系)
4. [API设计规范](#api设计规范)
5. [组件设计规范](#组件设计规范)
6. [Model设计规范](#model设计规范)
7. [代码注释规范](#代码注释规范)
8. [开发检查清单](#开发检查清单)

---

## 🏗️ 目录结构规范

### 核心分层原则

项目采用**业务(business)、支撑(support)、系统(system)**三层架构,贯穿所有目录:

```
src/
├── api/              # API层 - 按业务模块分类
│   ├── base-model/   # ✅ 基础模型(ResponseModel, PageParamModel等)
│   ├── business/     # ✅ 业务API (erp, oa等)
│   ├── support/      # ✅ 支撑API (文件、日志、配置等)
│   └── system/       # ✅ 系统API (登录、菜单、角色等)
├── components/       # 组件层 - 与API对应
│   ├── business/
│   ├── framework/    # 框架通用组件
│   ├── support/
│   └── system/
├── constants/        # 常量枚举层 - SmartEnum体系
│   ├── business/
│   ├── support/
│   └── system/
├── views/            # 视图层 - 与API、constants对应
│   ├── business/
│   ├── support/
│   └── system/
├── store/            # 状态管理 - Pinia
├── router/           # 路由配置
├── lib/              # 工具库
├── types/            # TypeScript类型定义
└── config/           # 配置文件
```

### 路径对应关系

**重要原则**: API、常量、组件、视图**四者路径必须对应**

✅ **正确示范**:
```
api/business/oa/enterprise-api.ts
constants/business/oa/enterprise-const.ts
components/business/oa/enterprise-select/index.vue
views/business/oa/enterprise/enterprise-list.vue
```

❌ **错误示范**:
```
api/enterprise-api.ts                    # ❌ 缺少业务分类
constants/enterprise.ts                  # ❌ 命名不符合规范
components/EnterpriseSelect.vue          # ❌ 路径不对应
views/oa/enterprise-page.vue             # ❌ 缺少business层级
```

---

## 📝 命名规范

### 文件命名

| 文件类型 | 命名格式 | 示例 |
|---------|---------|------|
| API文件 | `{模块名}-api.ts` | `login-api.ts`, `enterprise-api.ts` |
| 常量文件 | `{模块名}-const.ts` | `menu-const.ts`, `login-device-const.ts` |
| Model文件 | `{功能描述}-model.ts` | `response-model.ts`, `page-param-model.ts` |
| 页面组件 | `{功能描述}.vue` | `enterprise-list.vue`, `enterprise-detail.vue` |
| 子组件 | `{父组件名}-{功能}.vue` | `enterprise-bank-list.vue` |
| 模态框组件 | `{功能}-modal.vue` | `enterprise-operate-modal.vue` |

### 变量命名

```typescript
// ✅ 正确命名
export const loginApi = { ... }              // API对象用Api后缀
export const GENDER_ENUM = { ... }           // 枚举全大写+ENUM后缀
const enterpriseId = ref<number>()           // 变量驼峰,具体语义
let detailList = ref<EnterpriseVO[]>([])     // 列表用List后缀

// ❌ 错误命名
export const login = { ... }                 // ❌ 缺少Api标识
export const gender = { ... }                // ❌ 枚举未大写
const id = ref()                             // ❌ 命名不具体
let data = ref([])                           // ❌ 语义不明确
```

### 函数命名

**动词开头 + 具体操作对象**:

```typescript
// ✅ 正确命名
function getDetail() { }              // 获取详情
function queryList() { }              // 查询列表
function updateEnterprise() { }       // 更新企业
function deleteById() { }             // 删除
function showModal() { }              // 显示模态框
async function refreshData() { }      // 刷新数据

// ❌ 错误命名
function detail() { }                 // ❌ 缺少动词
function list() { }                   // ❌ 缺少动词
function update() { }                 // ❌ 对象不明确
function del() { }                    // ❌ 使用缩写
```

---

## 🎯 SmartEnum常量体系

### 核心设计

**杜绝魔法数字**,所有常量必须定义为SmartEnum:

```typescript
// 类型定义 (已在 types/smart-enum.d.ts)
export interface SmartEnum<T> {
  [key: string]: SmartEnumItem<T>;
}

interface SmartEnumItem<T> {
  value: T;      // 实际值
  desc: string;  // 描述文本
}
```

### 标准示范

```typescript
// constants/common-const.ts
import { SmartEnum } from '/@/types/smart-enum';

export const GENDER_ENUM: SmartEnum<number> = {
  UNKNOWN: {
    value: 0,
    desc: '未知',
  },
  MAN: {
    value: 1,
    desc: '男',
  },
  WOMAN: {
    value: 2,
    desc: '女',
  },
};

export const FLAG_NUMBER_ENUM: SmartEnum<number> = {
  TRUE: {
    value: 1,
    desc: '是',
  },
  FALSE: {
    value: 0,
    desc: '否',
  },
};
```

### 统一导出

所有常量通过 `constants/index.ts` 统一导出:

```typescript
// constants/index.ts
import menu from './system/menu-const';
import enterprise from './business/oa/enterprise-const';
import { GENDER_ENUM, FLAG_NUMBER_ENUM } from './common-const';

export default {
  GENDER_ENUM,
  FLAG_NUMBER_ENUM,
  ...menu,
  ...enterprise,
  // ...其他常量
};
```

### 使用方式

```vue
<script setup lang="ts">
import smartEnumPlugin from '/@/constants';

// 获取枚举描述
const genderDesc = smartEnumPlugin.getDescByValue('GENDER_ENUM', 1);  // "男"

// 获取枚举列表
const genderList = smartEnumPlugin.getValueDescList('GENDER_ENUM');
// [{ value: 0, desc: '未知' }, { value: 1, desc: '男' }, ...]
</script>
```

---

## 🔌 API设计规范

### 标准API文件结构

```typescript
/*
 *  模块功能说明
 *
 * @Author:    wangxiao
 * @Date:      2025-10-04 21:59:58
 * @Copyright  子午线高科智能科技 2025
 */
import { getRequest, postRequest } from '/@/lib/axios';

export const loginApi = {
  /**
   * 登录 @author 卓大
   */
  login: (param: LoginForm) => {
    return postRequest<ResponseModel<LoginVO>>('/login', param);
  },

  /**
   * 退出登录 @author 卓大
   */
  logout: () => {
    return getRequest('/login/logout');
  },

  /**
   * 获取登录信息 @author 卓大
   */
  getLoginInfo: () => {
    return getRequest<ResponseModel<LoginInfoVO>>('/login/getLoginInfo');
  },
};
```

### 关键原则

1. ✅ **统一命名**: `{模块}Api` 格式
2. ✅ **方法注释**: 每个方法包含功能说明和作者
3. ✅ **类型约束**: 使用TypeScript泛型定义返回类型
4. ✅ **工具函数**: 使用 `getRequest/postRequest` 而非直接axios
5. ✅ **RESTful风格**: URL命名清晰语义化

### 请求方法选择

| 操作类型 | HTTP方法 | 工具函数 |
|---------|---------|---------|
| 查询 | GET | `getRequest` |
| 新增 | POST | `postRequest` |
| 修改 | POST/PUT | `postRequest` |
| 删除 | POST/DELETE | `postRequest` |

---

## 🧩 组件设计规范

### 标准组件结构

```vue
<!--
  * 组件功能描述
  *
  * @Author:    1024创新实验室-主任:卓大
  * @Date:      2022-08-15 20:15:49
  * @Wechat:    zhuda1024
  * @Email:     lab1024@163.com
  * @Copyright  1024创新实验室 (https://1024lab.net),Since 2012
-->
<template>
  <div class="enterprise-detail">
    <!-- 模板内容 -->
  </div>
</template>

<script setup lang="ts">
  import { ref, onMounted, computed } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import { enterpriseApi } from '/@/api/business/oa/enterprise-api';
  import { SmartLoading } from '/@/components/framework/smart-loading';
  import { smartSentry } from '/@/lib/smart-sentry';

  // ==================== 1. Props/Emits定义 ====================
  const props = defineProps<{
    enterpriseId?: number;
  }>();

  const emit = defineEmits<{
    refresh: [];
  }>();

  // ==================== 2. 路由/参数获取 ====================
  const route = useRoute();
  const router = useRouter();

  // ==================== 3. 响应式数据定义 ====================
  let detail = ref<EnterpriseVO>({});
  let loading = ref(false);

  // ==================== 4. 计算属性 ====================
  const isEditable = computed(() => {
    return detail.value.status === 1;
  });

  // ==================== 5. 生命周期钩子 ====================
  onMounted(() => {
    getDetail();
  });

  // ==================== 6. 业务方法 ====================
  async function getDetail() {
    try {
      loading.value = true;
      let result = await enterpriseApi.detail(props.enterpriseId!);
      detail.value = result.data;
    } catch (error) {
      smartSentry.captureError(error);
    } finally {
      loading.value = false;
      SmartLoading.hide();
    }
  }

  function handleEdit() {
    // 编辑逻辑
  }
</script>

<style scoped lang="less">
.enterprise-detail {
  padding: 20px;
}
</style>
```

### 关键原则

1. ✅ **头部注释完整**: 作者、日期、联系方式
2. ✅ **setup语法糖**: 使用 `<script setup lang="ts">`
3. ✅ **代码分组**: Props→路由→数据→计算属性→生命周期→方法
4. ✅ **类型定义**: 使用TypeScript类型约束
5. ✅ **错误处理**: 统一使用 `smartSentry.captureError`
6. ✅ **加载状态**: 使用 `SmartLoading` 统一管理
7. ✅ **权限控制**: 使用 `v-if="$privilege('权限code')"`

---

## 📦 Model设计规范

### 统一基础模型

所有Model定义在 `api/base-model/` 目录:

```typescript
// response-model.ts - 统一响应模型
export interface ResponseModel<T> {
  code: number;      // 响应码
  data: T;           // 数据泛型
  msg?: string;      // 消息
  success: boolean;  // 成功标识
}

// page-param-model.ts - 分页参数模型
export interface PageParamModel {
  pageNum: number;              // 页码(不能为空)
  pageSize: number;             // 每页数量(不能为空)
  sortItemList?: Array<SortItemModel>;  // 排序字段集合
}

// page-result-model.ts - 分页结果模型
export interface PageResultModel<T> {
  total: number;      // 总数
  list: Array<T>;     // 数据列表
}
```

### 业务Model设计

```typescript
// api/business/oa/enterprise-model.ts
import { PageParamModel } from '/@/api/base-model/page-param-model';

// ========== 查询表单 ==========
export interface EnterpriseQueryForm extends PageParamModel {
  enterpriseName?: string;
  contact?: string;
  contactPhone?: string;
  // ...其他查询条件
}

// ========== 新增表单 ==========
export interface EnterpriseAddForm {
  enterpriseName: string;
  unifiedSocialCreditCode: string;
  contact: string;
  contactPhone: string;
  // ...其他字段
}

// ========== 更新表单 ==========
export interface EnterpriseUpdateForm extends EnterpriseAddForm {
  enterpriseId: number;  // 更新需要ID
}

// ========== 展示VO ==========
export interface EnterpriseVO {
  enterpriseId: number;
  enterpriseName: string;
  unifiedSocialCreditCode: string;
  contact: string;
  contactPhone: string;
  createTime?: string;
  createUserName?: string;
  // ...其他展示字段
}
```

### 关键原则

1. ✅ **继承基础模型**: 查询表单继承 `PageParamModel`
2. ✅ **Form/VO分离**: 表单(Form)和展示(VO)分开定义
3. ✅ **可选字段**: 使用 `?` 标记可选属性
4. ✅ **类型安全**: 避免使用 `any`,明确类型定义

---

## 💬 代码注释规范

### 文件头注释

```typescript
/*
 * 模块功能描述
 *
 * @Author:    1024创新实验室-主任:卓大
 * @Date:      2022-09-03 21:59:58
 * @Wechat:    zhuda1024
 * @Email:     lab1024@163.com
 * @Copyright  1024创新实验室 (https://1024lab.net),Since 2012
 */
```

### 函数注释

```typescript
/**
 * 登录 @author 卓大
 */
login: (param: LoginForm) => {
  return postRequest('/login', param);
},

/**
 * 查询企业列表
 * @param queryForm 查询条件
 * @returns 分页数据
 * @author 卓大
 */
async function queryList(queryForm: EnterpriseQueryForm) {
  // ...
}
```

### 复杂逻辑注释

```typescript
// ========== 数据权限处理 ==========
function handleDataScope() {
  // 1. 获取当前用户权限
  // 2. 过滤可见数据
  // 3. 返回处理结果
}
```

---

## ✅ 开发检查清单

### 新功能开发前

- [ ] 确认功能属于 business/support/system 哪个分类
- [ ] 规划好 API、常量、组件、视图的目录结构
- [ ] 检查是否有可复用的基础组件
- [ ] 确认权限点定义

### 开发中

- [ ] API文件命名: `{模块}-api.ts`
- [ ] 常量文件命名: `{模块}-const.ts`
- [ ] 所有常量定义为SmartEnum
- [ ] API方法包含完整注释
- [ ] 组件包含完整头部注释
- [ ] 使用TypeScript类型约束
- [ ] 错误统一用smartSentry处理

### 开发完成后

- [ ] ESLint检查通过
- [ ] TypeScript编译无错误
- [ ] 功能自测完成
- [ ] 代码已提交到正确的分支
- [ ] 更新相关文档

### Code Review要点

- [ ] 目录结构是否符合规范
- [ ] 命名是否规范统一
- [ ] 是否存在魔法数字
- [ ] 是否有重复代码
- [ ] 错误处理是否完善
- [ ] 权限控制是否正确

---

## 📚 参考资源

- [SmartAdmin官方文档](https://smartadmin.vip)
- [Vue3官方文档](https://cn.vuejs.org/)
- [Ant Design Vue文档](https://antdv.com/)
- [TypeScript文档](https://www.typescriptlang.org/zh/)

---

**文档维护**: 本文档由开发团队共同维护,如有疑问或建议请联系团队负责人。
