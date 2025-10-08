---
title: SmartAdmin 前端开发指南
author: wangxiao
company: 子午线高科智能科技
date: 2025-10-07
---

# SmartAdmin 前端开发指南

## 📚 文档体系

1. **[编码规范](./FRONTEND_CODING_STANDARDS.md)** - 命名、结构、注释规范
2. **[列表页面规范](./LIST_PAGE_STANDARDS.md)** - 列表页面标准实现 ⭐
3. **[原型开发指南](./FRONTEND_PROTOTYPE_DEV_GUIDE.md)** - 快速原型开发
4. **[代码模板](../smart-admin-web-typescript/.templates/)** - 标准代码模板
5. **本文档** - 开发流程和最佳实践

---

## 🚀 标准开发流程

### Step 1: 需求分析 (5分钟)

**确定功能分类**:
- business - 业务功能
- support - 支撑功能
- system - 系统功能

**规划目录结构**（四目录对应）:
```
例：客户管理 (business/crm/customer)

api/business/crm/customer/customer-api.ts
api/business/crm/customer/customer-model.ts
constants/business/crm/customer/customer-const.ts
views/business/crm/customer/customer-list.vue
```

### Step 2: 使用模板创建文件 (10分钟)

#### 列表页面
```bash
# 复制 .templates/list-page-template.vue
# 替换占位符: [模块]、[分类]、[主键]、[日期]
```

**必读**: 查看 [列表页面规范](./LIST_PAGE_STANDARDS.md) 了解完整结构要求

#### API文件
```typescript
// 复制 .templates/api-template.ts
import { getRequest, postRequest } from '/@/lib/axios';

export const customerApi = {
  /**
   * 分页查询
   */
  queryPage: (param: CustomerQueryForm) => {
    return postRequest<ResponseModel<PageResultModel<CustomerVO>>>(
      '/business/crm/customer/queryPage',
      param
    );
  },
};
```

#### 常量文件
```typescript
// 复制 .templates/const-template.ts
import { SmartEnum } from '/@/types/smart-enum';

export const CUSTOMER_STATUS_ENUM: SmartEnum<number> = {
  ACTIVE: { value: 1, desc: '活跃' },
  INACTIVE: { value: 0, desc: '非活跃' },
};

export default { CUSTOMER_STATUS_ENUM };
```

#### Model 和组件
- Model: 复制 `.templates/model-template.ts`
- 组件: 复制 `.templates/component-template.vue`

### Step 3: 代码开发

**开发要点**:
- ✅ TypeScript 类型约束
- ✅ 错误处理用 `smartSentry.captureError`
- ✅ 加载状态用 `SmartLoading`
- ✅ 权限控制用 `v-if="$privilege('code')"`
- ✅ 杜绝魔法数字，用 SmartEnum

### Step 4: 自检 (5分钟)

**结构检查**:
- [ ] 四目录路径对应？
- [ ] 文件命名规范？
- [ ] 分类正确？

**代码检查**:
- [ ] 方法有注释？
- [ ] 无魔法数字？
- [ ] TypeScript类型完整？

**工具检查**:
```bash
npm run lint          # ESLint
npm run type-check    # TypeScript
```

### Step 5: 提交代码

```bash
git add .
git commit -m "feat: 新增客户管理功能"
git push
```

---

## ✅ 开发检查清单

### 开发前
- [ ] 阅读编码规范文档
- [ ] 确认功能分类
- [ ] 规划目录结构
- [ ] 准备代码模板

### 开发中
- [ ] 文件命名: `{模块}-api.ts`/`{模块}-const.ts`
- [ ] 常量用 SmartEnum
- [ ] API 方法有注释
- [ ] TypeScript 类型完整
- [ ] 错误用 smartSentry

### 开发后
- [ ] ESLint 通过
- [ ] TypeScript 无错
- [ ] 功能自测通过
- [ ] 代码已提交

---

## 💡 最佳实践

### 1. 100% 使用 Ant Design Vue ⚠️

**强制原则**: 能用官方组件，绝不自己实现。

**为什么**:
- ✅ 稳定性好、可维护性强
- ✅ 开发效率高、UI 统一
- ✅ 有文档支持、社区活跃

**示例**:
```vue
<!-- ❌ 错误 -->
<MyTable :data="list" />
<MyPagination :total="total" />

<!-- ✅ 正确 -->
<a-table :dataSource="list" :columns="columns" />
<a-pagination v-model:current="page" :total="total" />
```

**自定义组件前必须**:
1. 查阅 [Ant Design Vue 文档](https://antdv.com/)
2. 确认官方无对应组件
3. 确认 `components/framework/` 无可复用组件

### 2. 敢于质疑不合理需求

**遇到以下情况必须质疑**:
- 要求"自己开发"，但 Ant Design Vue 已有
- 要求"自定义样式"，但配置即可实现
- 要求"引入新 UI 库"，但已有提供

**质疑模板**:
> "Ant Design Vue 的 `a-xxx` 已提供此功能，使用官方组件有以下优势：稳定、可维护、高效、统一。是否有特殊业务需求无法满足？"

### 3. 最大化复用

**开发前检查**:
- `components/framework/` - 通用组件
- `components/support/` - 支撑组件
- 现有业务模块实现

**重复代码立即重构**:
- 业务逻辑 → 工具函数
- UI 组件 → 通用组件
- 常量 → SmartEnum

### 4. 技术选型合理

**引入新库前评估**:
- [ ] Ant Design Vue 是否已提供？
- [ ] 原生 JS/TS 能否实现？
- [ ] 现有依赖是否有类似？
- [ ] 包体积影响？
- [ ] 维护状态？

### 5. 保持一致

- 同模块文件用相同命名前缀
- API、常量、组件路径对应
- 代码风格与现有代码一致

---

## 🆘 常见问题

**Q: 如何快速创建规范文件？**
A: 使用 `.templates/` 模板，替换占位符

**Q: 魔法数字怎么处理？**
A: 定义 SmartEnum
```typescript
// ❌ if (status === 1)
// ✅ if (status === STATUS_ENUM.ACTIVE.value)
```

**Q: 如何确保路径对应？**
A: 规划时确保四目录一致
```
api/business/order/
constants/business/order/
components/business/order/
views/business/order/
```

**Q: 新人如何上手？**
A:
1. 读编码规范
2. 参考现有代码
3. 用代码模板
4. 遵循本指南

---

## 📝 开发日志规范

### 控制台日志

```typescript
// ✅ 正确
console.log('普通信息:', data);
console.info('操作成功:', result);
console.warn('警告:', msg);
console.error('错误:', error);

// ❌ 禁止
console.log('test');  // 提交前删除
console.log('用户:', user);  // 敏感信息
```

### 环境区分

```typescript
// 开发环境
if (import.meta.env.DEV) {
  console.log('调试:', data);
}

// 生产环境
if (import.meta.env.PROD) {
  // 使用日志服务 (Sentry 等)
}
```

---

## 📖 参考资源

- [Vue3 官方文档](https://cn.vuejs.org/)
- [Ant Design Vue](https://antdv.com/)
- [TypeScript 文档](https://www.typescriptlang.org/zh/)

---

**核心原则**: 规范优先、复用优先、效率优先、质量优先。
