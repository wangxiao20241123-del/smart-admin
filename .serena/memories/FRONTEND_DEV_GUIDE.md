# SmartAdmin开发实施指南

> **作者**: wangxiao
> **企业**: 子午线高科智能科技
> **更新时间**: 2025-10-04

---

## 📋 快速开始

### 必读文档
1. [代码规范标准](./FRONTEND_CODING_STANDARDS.md) - 详细的编码规范
2. [代码模板](./.templates/) - 标准化代码模板
3. 本文档 - 实施指南和检查清单

---

## 🚀 新功能开发流程

### Step 1: 需求分析 (5分钟)

**确定功能分类**:
- [ ] business (业务功能)
- [ ] support (支撑功能)
- [ ] system (系统功能)

**规划目录结构**:
```
例如: 客户管理功能 (business/crm/customer)

api/business/crm/customer-api.ts
api/business/crm/customer-model.ts
constants/business/crm/customer-const.ts
views/business/crm/customer/customer-list.vue
views/business/crm/customer/customer-detail.vue
components/business/crm/customer-select/index.vue
```

### Step 2: 使用模板创建文件 (10分钟)

#### 2.1 创建API文件

复制 `.templates/api-template.ts`:
```typescript
/*
 *  客户管理
 *
 * @Author:    wangxiao
 * @Date:      2025-10-04
 * @Copyright  子午线高科智能科技 2025
 */
import { getRequest, postRequest } from '/@/lib/axios';
import { ResponseModel } from '/@/api/base-model/response-model';
import { PageResultModel } from '/@/api/base-model/page-result-model';
import {
  CustomerQueryForm,
  CustomerAddForm,
  CustomerUpdateForm,
  CustomerVO,
} from './customer-model';

export const customerApi = {
  /**
   * 分页查询 @author wangxiao
   */
  queryPage: (param: CustomerQueryForm) => {
    return postRequest<ResponseModel<PageResultModel<CustomerVO>>>(
      '/business/crm/customer/queryPage',
      param
    );
  },

  // ... 其他方法
};
```

#### 2.2 创建Model文件

复制 `.templates/model-template.ts` 并填充字段。

#### 2.3 创建常量文件

复制 `.templates/const-template.ts`:
```typescript
/*
 * 客户管理常量
 *
 * @Author:    wangxiao
 * @Date:      2025-10-04
 * @Copyright  子午线高科智能科技 2025
 */
import { SmartEnum } from '/@/types/smart-enum';

export const CUSTOMER_STATUS_ENUM: SmartEnum<number> = {
  ACTIVE: { value: 1, desc: '活跃' },
  INACTIVE: { value: 0, desc: '非活跃' },
};

export default {
  CUSTOMER_STATUS_ENUM,
};
```

#### 2.4 创建组件文件

复制 `.templates/component-template.vue` 并按需修改。

### Step 3: 代码开发 (根据复杂度)

**开发中注意**:
- ✅ 使用TypeScript类型约束
- ✅ 错误处理用 `smartSentry.captureError`
- ✅ 加载状态用 `SmartLoading`
- ✅ 权限控制用 `v-if="$privilege('code')"`
- ✅ 避免魔法数字,使用SmartEnum

### Step 4: 自检清单 (5分钟)

**目录结构检查**:
- [ ] API、常量、组件、视图路径是否对应?
- [ ] 文件命名是否符合规范?
- [ ] 是否放在正确的分类下(business/support/system)?

**代码规范检查**:
- [ ] 文件头注释完整? (作者、日期、Copyright)
- [ ] 方法注释包含 `@author wangxiao`?
- [ ] 变量命名是否语义化?
- [ ] 是否有魔法数字?

**TypeScript检查**:
```bash
npm run type-check
```

**ESLint检查**:
```bash
npm run lint
```

### Step 5: 提交代码

```bash
git add .
git commit -m "feat: 新增客户管理功能"
git push
```

---

## ✅ 开发检查清单

### 开发前检查

- [ ] 阅读 [FRONTEND_CODING_STANDARDS.md](./FRONTEND_CODING_STANDARDS.md)
- [ ] 确认功能分类 (business/support/system)
- [ ] 规划好完整的目录结构
- [ ] 准备好代码模板

### 开发中检查

- [ ] API文件: `{模块}-api.ts`
- [ ] Model文件: `{模块}-model.ts`
- [ ] 常量文件: `{模块}-const.ts`
- [ ] 所有常量定义为SmartEnum
- [ ] API方法包含完整注释
- [ ] 组件包含完整头部注释
- [ ] 使用TypeScript类型约束
- [ ] 错误统一用smartSentry处理

### 开发后检查

- [ ] ESLint检查通过
- [ ] TypeScript编译无错误
- [ ] 功能自测完成
- [ ] 代码已提交到正确分支

---

## 📚 常见场景示例

### 场景1: 新增业务模块

**需求**: 新增"订单管理"功能

**步骤**:
1. 确定分类: `business/order`
2. 创建文件:
   ```
   api/business/order/order-api.ts
   api/business/order/order-model.ts
   constants/business/order/order-const.ts
   views/business/order/order-list.vue
   ```
3. 使用模板填充内容
4. 在 `constants/index.ts` 中导入常量
5. 在路由中配置页面

### 场景2: 新增枚举常量

**需求**: 新增订单状态枚举

**步骤**:
```typescript
// constants/business/order/order-const.ts
export const ORDER_STATUS_ENUM: SmartEnum<number> = {
  PENDING: { value: 1, desc: '待处理' },
  PROCESSING: { value: 2, desc: '处理中' },
  COMPLETED: { value: 3, desc: '已完成' },
  CANCELLED: { value: 0, desc: '已取消' },
};
```

### 场景3: 新增通用组件

**需求**: 新增客户选择组件

**步骤**:
1. 路径: `components/business/crm/customer-select/index.vue`
2. 使用component-template.vue模板
3. 实现选择逻辑
4. 导出使用

---

## 🔧 工具配置

### ESLint配置

项目已配置 `.eslintrc.cjs`,包含:
- Vue3规范检查
- TypeScript规范
- 命名规范检查

**运行检查**:
```bash
npm run lint
```

**自动修复**:
```bash
npm run lint:fix
```

### TypeScript配置

**类型检查**:
```bash
npm run type-check
```

---

## 💡 最佳实践

### 1. 优先使用 Ant Design Vue 组件 ⚠️ 强制

**核心原则**: 能用 Ant Design Vue 组件的，**必须使用**，不要自己实现

**为什么**:
- ✅ 稳定性好，经过大量项目验证
- ✅ 可维护性强，有官方文档和社区支持
- ✅ 开发效率高，无需从零实现
- ✅ UI 风格统一，用户体验一致

**反例**:
```vue
<!-- ❌ 错误：自己实现分页 -->
<MyPagination :total="total" @change="handlePageChange" />

<!-- ✅ 正确：使用 Ant Design Vue -->
<a-pagination v-model:current="current" :total="total" />
```

```vue
<!-- ❌ 错误：自己实现表格 -->
<MyTable :data="tableData" :columns="columns" />

<!-- ✅ 正确：使用 Ant Design Vue -->
<a-table :dataSource="tableData" :columns="columns" rowKey="id" />
```

**自己实现组件前必须做**:
1. 查阅 [Ant Design Vue 官方文档](https://antdv.com/)
2. 确认 Ant Design Vue 确实没有对应组件
3. 确认现有的 `components/framework/` 和 `components/support/` 没有可复用组件

### 2. 勇于质疑不合理需求 ⚠️ 重要

**如果遇到以下情况，必须质疑**:
- 需求要求"自己开发组件"，而 Ant Design Vue 已有现成组件
- 需求要求"自定义样式"，而 Ant Design Vue 通过配置就能实现
- 需求要求"引入新的UI库"，而 Ant Design Vue 已经提供

**质疑模板**:
> "这个需求要求自定义组件，但我发现 Ant Design Vue 的 `a-xxx` 组件已经提供了这个功能。使用官方组件有以下优势：
> 1. 稳定性更好，经过大量项目验证
> 2. 可维护性强，有官方文档和社区支持
> 3. 开发效率高，无需从零实现
> 4. UI 风格统一
>
> 请问有什么特殊需求是官方组件无法满足的吗？"

**敢于反驳**:
- 不要盲目接受"产品说要自定义"的需求
- 用技术角度说明使用标准组件的优势
- 只有在确实有特殊业务场景时，才考虑自定义

### 3. 最大化组件复用

开发前先检查是否有类似功能可以复用:
- 查看 `components/framework/` 下的通用组件
- 查看 `components/support/` 下的支撑组件
- 查看现有业务模块的实现
- 同一功能不要在多个地方重复实现

### 4. 技术选型合理性

**新增第三方库前，必须评估**:
- [ ] Ant Design Vue 是否已提供类似功能？
- [ ] 原生 JS/TS 能否实现？
- [ ] 现有依赖中是否有类似功能？
- [ ] 引入新库会增加多少包体积？
- [ ] 这个库的维护状态如何？

**避免技术栈碎片化**:
- 能用 Ant Design Vue + 原生实现的，不引入新依赖
- 不要为了一个小功能引入一个大库

### 5. 保持一致

- 同一业务模块的所有文件使用相同的命名前缀
- API、常量、组件路径保持对应关系
- 代码风格与现有代码保持一致

### 6. 及时重构

发现重复代码立即提取:
- 重复的业务逻辑 → 抽取到工具函数
- 重复的UI组件 → 抽取到通用组件
- 重复的常量 → 定义SmartEnum

---

## 🆘 常见问题

### Q1: 如何快速创建符合规范的文件?

**A**: 使用 `.templates/` 目录下的模板文件,替换占位符即可。

### Q2: 魔法数字应该如何处理?

**A**: 定义为SmartEnum常量:
```typescript
// ❌ 错误
if (status === 1) { }

// ✅ 正确
if (status === ORDER_STATUS_ENUM.PENDING.value) { }
```

### Q3: 如何确保API、常量、组件路径对应?

**A**: 开发前先规划好完整目录结构,确保四个目录路径一致:
```
api/business/order/
constants/business/order/
components/business/order/
views/business/order/
```

### Q4: 团队新成员如何快速上手?

**A**:
1. 阅读 [FRONTEND_CODING_STANDARDS.md](./FRONTEND_CODING_STANDARDS.md)
2. 参考现有代码实现
3. 使用代码模板
4. 遵循本开发指南

---

## 📖 参考资源

- [SmartAdmin官方文档](https://smartadmin.vip)
- [Vue3官方文档](https://cn.vuejs.org/)
- [Ant Design Vue文档](https://antdv.com/)
- [TypeScript文档](https://www.typescriptlang.org/zh/)

---

**持续改进**: 本文档会随项目发展持续更新,欢迎提出改进建议!
