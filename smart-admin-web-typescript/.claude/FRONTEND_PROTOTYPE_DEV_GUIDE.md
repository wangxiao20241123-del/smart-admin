---
title: SmartAdmin 前端原型开发指南
author: wangxiao
company: 子午线高科智能科技
date: 2025-10-07
description: 适用场景：快速原型开发、功能演示、需求验证
---

# SmartAdmin 前端原型开发指南

## 📌 核心理念

**原型开发 = 快速验证，不是降低质量**

| 对比维度 | 标准开发 | 原型开发 |
|---------|---------|---------|
| 目标 | 生产级代码 | 快速验证需求 |
| 周期 | 按迭代计划 | **1-3天交付** |
| 后端依赖 | 需要API就绪 | **Mock数据，零依赖** |
| 测试 | 单元+集成测试 | 手工功能测试 |

---

## 🚀 快速开发流程

### Step 1: 创建目录结构 (10分钟)

**四目录对应原则**:
```
api/prototype/[模块]/[模块]-api.ts        # API + Mock降级
api/prototype/[模块]/[模块]-model.ts      # 数据模型
constants/prototype/[模块]/[模块]-const.ts # SmartEnum常量
views/prototype/[模块]/[模块]-xxx.vue      # 页面组件
router/prototype/[模块].ts                 # 路由配置
```

### Step 2: 智能API降级 (30分钟)

#### 推荐方案：智能检查工具

**工具**: `src/api/prototype/api-checker.ts`

**工作原理**:
1. 调用 `/v3/api-docs` 获取后端所有API
2. 检查目标API是否存在
3. 不存在或失败 → 自动降级到Mock数据

**标准用法**:
```typescript
import { callApiWithFallback } from '/@/api/prototype/api-checker';

export const chatRecordApi = {
  queryPage: async (param) => {
    return callApiWithFallback(
      '/business/wecom/chatRecord/queryPage',
      () => postRequest('/business/wecom/chatRecord/queryPage', param),
      () => ({
        code: 0,
        data: {
          list: generateMockList(param.pageNum, param.pageSize),
          total: 50,
        },
      })
    );
  },
};
```

#### 备选方案：Try-Catch降级

**简单场景适用**:
```typescript
export const customerApi = {
  queryPage: async (param) => {
    try {
      return await postRequest('/api/customer/query', param);
    } catch (error) {
      console.warn('API失败，使用Mock', error);
      return { code: 0, data: generateMockData(param) };
    }
  },
};
```

### Step 3: Mock数据生成

**真实感Mock数据模板**:
```typescript
const mockNames = ['张三', '李四', '王五', '赵六'];
const mockStatus = [1, 0];

const generateMockData = (pageNum: number, pageSize: number) => {
  const list = Array.from({ length: pageSize }, (_, i) => ({
    id: 1001 + (pageNum - 1) * pageSize + i,
    name: mockNames[i % mockNames.length],
    status: mockStatus[i % 2],
    score: Math.floor(60 + Math.random() * 40),
    createTime: new Date(Date.now() - Math.random() * 30 * 86400000)
      .toISOString()
      .slice(0, 19)
      .replace('T', ' '),
  }));

  return {
    list,
    total: 50,
    pageNum,
    pageSize,
  };
};
```

### Step 4: 常量与路由

**常量定义** - 使用SmartEnum:
```typescript
import { SmartEnum } from '/@/types/smart-enum';

export const STATUS_ENUM: SmartEnum<number> = {
  ACTIVE: { value: 1, desc: '启用' },
  DISABLED: { value: 0, desc: '禁用' },
};
```

**路由配置**:
```typescript
// router/prototype/wecom.ts
export const prototypeRouters = [
  {
    path: '/prototype/wecom',
    component: () => import('/@/layout/index.vue'),
    meta: { title: '企微助手（原型）' },
    children: [
      {
        path: '/prototype/wecom/chat-record/list',
        component: () => import('/@/views/prototype/wecom/chat-record-list.vue'),
        meta: { title: '聊天记录', keepAlive: true },
      },
    ],
  },
];

// router/routers.ts - 集成主路由
import { prototypeRouters } from './prototype/wecom';
export const routerArray = [...prototypeRouters, ...其他路由];
```

---

## ✅ 开发规范

### 必须遵守 ⚠️

- ✅ 枚举：使用SmartEnum，禁止魔法数字
- ✅ UI组件：100% Ant Design Vue
- ✅ 目录：必须在 `prototype/` 下
- ✅ API：必须有Mock降级机制
- ✅ 方法注释：清晰描述功能

### 可以简化 ✨

- ✅ 文件长度：500-800行可接受
- ✅ 组件抽取：使用<3次不强制抽取
- ✅ TypeScript：允许使用any（但应尽量避免）
- ✅ 测试：不强制单元测试，手工测试即可

### 严格禁止 ❌

- ❌ 自己实现UI组件（必须用Ant Design Vue）
- ❌ 硬编码后端URL
- ❌ 页面直接写业务逻辑（必须有API层）
- ❌ 缺少错误处理

---

## 💡 最佳实践

### 1. Mock数据要真实

```typescript
// ❌ 错误：假数据感太强
const mock = { name: 'test', phone: '12345678901' };

// ✅ 正确：贴近真实数据
const mock = {
  name: '张晓明',
  phone: '138****1234',
  createTime: '2025-10-05 14:32:15',
  wechatName: '奋斗的职场人',
};
```

### 2. 交互完整性

**加载状态、错误处理、成功反馈都要完整**:
```vue
<a-button type="primary" :loading="loading" @click="handleSubmit">
  提交
</a-button>

<script setup>
const handleSubmit = async () => {
  loading.value = true;
  try {
    await api.submit(data);
    message.success('提交成功');
    router.push('/list');
  } catch (error) {
    message.error('提交失败：' + error.message);
  } finally {
    loading.value = false;
  }
};
</script>
```

### 3. 注释标注原型性质

```typescript
/**
 * 原型开发：客户管理API
 * 注意：使用Mock降级，生产环境需移除Mock逻辑
 */
```

---

## 🔄 原型转生产

### 迁移检查清单

**代码清理**:
- [ ] 移除Mock数据生成函数
- [ ] 移除API降级逻辑（callApiWithFallback或try-catch）
- [ ] 补充完整TypeScript类型
- [ ] 拆分过长组件（>500行）

**功能补全**:
- [ ] 完整表单验证
- [ ] 完整错误处理
- [ ] 权限控制
- [ ] 单元测试

**目录迁移**:
- [ ] `prototype/` → `business/`
- [ ] 更新路由路径
- [ ] 更新菜单配置

### 迁移示例

```typescript
// 原型版本
export const api = {
  query: async (p) => {
    try {
      return await post('/api', p);
    } catch {
      return mockData(); // Mock降级
    }
  },
};

// 生产版本
export const api = {
  query: (p: QueryForm) => {
    return post<ResponseModel<PageResult<VO>>>('/business/api', p);
  },
};
```

---

## 🆘 常见问题

**Q: 原型和生产代码能混在一起吗？**
A: 不能！必须分离 `prototype/` vs `business/`

**Q: Mock降级会影响生产环境吗？**
A: 不影响。生产时移除Mock逻辑即可

**Q: 何时做原型 vs 生产代码？**
A:
- **原型** = 需求不明确、需快速演示、后端未就绪
- **生产** = 需求清晰、后端API已就绪

**Q: 原型能跳过Code Review吗？**
A: 可简化流程，但核心规范（文件头、SmartEnum、Ant Design Vue）必须检查

---

## 📚 参考案例

### 企微聊天记录原型

**代码位置**:
```
api/prototype/wecom/chat-record/
views/prototype/wecom/chat-record/
router/prototype/wecom.ts
```

**关键特性**:
- 智能API检查（`/v3/api-docs`）
- 自动Mock降级
- 完整CRUD交互

**开发时长**: 约4小时

---

## 📖 相关文档

- [前端代码规范](./FRONTEND_CODING_STANDARDS.md)
- [前端开发指南](./FRONTEND_DEV_GUIDE.md)

---

**核心原则**: 原型开发追求速度，但不牺牲核心质量。必须遵守规范、完整交互、可读代码。验证通过后，及时转为生产代码。