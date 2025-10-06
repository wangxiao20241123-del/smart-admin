# SmartAdmin 前端原型开发指南

> **作者**: wangxiao
> **企业**: 子午线高科智能科技
> **更新时间**: 2025-10-06
> **适用场景**: 快速原型开发、功能演示、需求验证

---

## 📌 本文档定位

本指南是 [FRONTEND_DEV_GUIDE.md](./FRONTEND_DEV_GUIDE.md) 的**补充文档**，专门针对**原型开发**场景。

**阅读顺序**:
1. 先阅读 [FRONTEND_CODING_STANDARDS.md](./FRONTEND_CODING_STANDARDS.md) - 了解代码规范
2. 再阅读 [FRONTEND_DEV_GUIDE.md](./FRONTEND_DEV_GUIDE.md) - 了解标准开发流程
3. 最后阅读本文档 - 了解原型开发的**差异化做法**

---

## 🎯 原型开发 vs 标准开发

### 核心差异

| 维度 | 标准开发 | 原型开发 |
|------|----------|----------|
| **目标** | 可维护的生产代码 | 快速验证想法和需求 |
| **周期** | 按迭代计划开发 | 1-3天快速交付 |
| **后端依赖** | 需要后端API就绪 | **不依赖后端**，使用Mock数据 |
| **代码质量** | 严格遵循所有规范 | 遵循核心规范，简化部分流程 |
| **测试** | 完整单元测试+集成测试 | 手工功能测试 |
| **可复用性** | 高度模块化复用 | 允许适度冗余 |

---

## 🚀 原型开发快速流程

### Step 1: 需求快速分析 (10分钟)

**明确原型目标**:
- [ ] 这是为了演示什么功能？
- [ ] 需要展示哪些核心交互？
- [ ] 有哪些关键数据需要模拟？

**规划最小化目录结构**:
```
原型开发只创建必要的文件：

✅ 必须创建:
api/prototype/[模块]/[模块]-api.ts        # API定义（含Mock）
api/prototype/[模块]/[模块]-model.ts      # 数据模型
constants/prototype/[模块]/[模块]-const.ts # 枚举常量
views/prototype/[模块]/[模块]-xxx.vue      # 页面组件

⚠️ 可选创建（按需）:
components/prototype/[模块]/xxx.vue        # 仅在真正需要复用时创建
router/prototype/[模块].ts                 # 路由配置（简化版）
```

**示例 - AI销售助手原型**:
```
api/prototype/ai-sales/customer/customer-api.ts
api/prototype/ai-sales/customer/customer-model.ts
constants/prototype/ai-sales/customer/customer-const.ts
views/prototype/ai-sales/customer/customer-list.vue
views/prototype/ai-sales/customer/customer-detail.vue
router/prototype/ai-sales.ts
```

### Step 2: Mock数据优先策略 (30分钟)

#### 2.1 API文件 - 内置Mock降级

```typescript
/*
 * AI销售-客户管理API
 *
 * @Author:    wangxiao
 * @Date:      2025-10-06
 * @Copyright  子午线高科智能科技 2025
 */
import { postRequest } from '/@/lib/axios';

// Mock数据生成函数
const generateMockCustomers = (pageNum: number, pageSize: number) => {
  const total = 50;
  const list = [];
  for (let i = 0; i < pageSize; i++) {
    const index = (pageNum - 1) * pageSize + i;
    if (index >= total) break;
    list.push({
      customerId: 1001 + index,
      customerName: `客户${index + 1}`,
      wechatName: `微信用户${index + 1}`,
      intentionLevel: ['S', 'A', 'B'][index % 3],
      aiScore: 75 + Math.random() * 25,
      // ... 其他字段
    });
  }
  return {
    code: 0,
    data: {
      list,
      total,
      pageNum,
      pageSize,
    },
    msg: 'success',
  };
};

export const customerApi = {
  /**
   * 分页查询 @author wangxiao
   */
  queryPage: async (param) => {
    try {
      // 优先调用真实API
      return await postRequest('/business/ai-sales/customer/queryPage', param);
    } catch (error) {
      // API失败时使用Mock数据
      console.warn('API调用失败，使用Mock数据', error);
      return new Promise((resolve) => {
        setTimeout(() => {
          resolve(generateMockCustomers(param.pageNum, param.pageSize));
        }, 500); // 模拟网络延迟
      });
    }
  },

  /**
   * 获取详情 @author wangxiao
   */
  detail: async (customerId) => {
    try {
      return await postRequest('/business/ai-sales/customer/detail', { customerId });
    } catch (error) {
      console.warn('API调用失败，使用Mock数据', error);
      return {
        code: 0,
        data: {
          customerId,
          customerName: '张三',
          wechatName: '奋斗的小张',
          intentionLevel: 'S',
          aiScore: 92.5,
          // ... 完整的mock数据
        },
        msg: 'success',
      };
    }
  },
};
```

**Mock数据降级的优势**:
- ✅ 不依赖后端进度，前端独立开发
- ✅ 演示时即使后端挂了也能正常展示
- ✅ 后端API就绪后，自动切换到真实数据
- ✅ 方便测试各种边界场景

#### 2.2 丰富的Mock数据生成

```typescript
// 生成真实感的Mock数据
const mockNames = ['张三', '李四', '王五', '赵六', '钱七'];
const mockWechatNames = ['奋斗的小张', '职场新人李', '斜杠青年王'];
const mockPhones = ['138****1234', '139****5678', '150****9012'];

const generateRealisticMockData = () => {
  return {
    customerId: 1000 + Math.floor(Math.random() * 1000),
    customerName: mockNames[Math.floor(Math.random() * mockNames.length)],
    wechatName: mockWechatNames[Math.floor(Math.random() * mockWechatNames.length)],
    phone: mockPhones[Math.floor(Math.random() * mockPhones.length)],
    intentionLevel: ['S', 'A', 'B'][Math.floor(Math.random() * 3)],
    aiScore: 60 + Math.random() * 40,
    priceScore: 60 + Math.random() * 40,
    demandScore: 60 + Math.random() * 40,
    consensusScore: 60 + Math.random() * 40,
    trustScore: 60 + Math.random() * 40,
    createTime: new Date(Date.now() - Math.random() * 30 * 24 * 60 * 60 * 1000).toISOString(),
  };
};
```

### Step 3: 简化的常量定义 (10分钟)

```typescript
/*
 * AI销售-客户管理常量
 *
 * @Author:    wangxiao
 * @Date:      2025-10-06
 * @Copyright  子午线高科智能科技 2025
 */
import { SmartEnum } from '/@/types/smart-enum';

// 原型开发：只定义必要的枚举
export const INTENTION_LEVEL_ENUM: SmartEnum<string> = {
  S: { value: 'S', desc: 'S量-高意向' },
  A: { value: 'A', desc: 'A量-中意向' },
  B: { value: 'B', desc: 'B量-低意向' },
};

export const CUSTOMER_POOL_ENUM: SmartEnum<string> = {
  CURRENT: { value: 'current', desc: '本期营' },
  LONG_TERM: { value: 'long_term', desc: '长期池' },
};

// 导出（保持一致性）
export default {
  INTENTION_LEVEL_ENUM,
  CUSTOMER_POOL_ENUM,
};
```

### Step 4: 组件开发 - 内联优先 (2-4小时)

**原型开发原则**:
- ⚠️ **不过度封装**: 单个页面功能允许写在一个文件里
- ⚠️ **不过度复用**: 除非真的会在3个以上地方用到，否则不抽组件
- ✅ **优先Ant Design Vue**: 100%使用官方组件

#### 4.1 页面结构 - 单文件组件

```vue
<!--
  AI销售-客户列表

  @Author:    wangxiao
  @Date:      2025-10-06
  @Copyright  子午线高科智能科技 2025
-->
<template>
  <div class="customer-list-container">
    <!-- 统计卡片 -->
    <a-card class="stat-card">
      <a-row :gutter="16">
        <a-col :span="6">
          <a-statistic title="本期总人数" :value="statData.totalCount" />
        </a-col>
        <!-- ... 更多统计 -->
      </a-row>
    </a-card>

    <!-- Tab切换 -->
    <a-tabs v-model:activeKey="activeTab" @change="handleTabChange">
      <a-tab-pane key="current" tab="本期营">
        <!-- 筛选区 -->
        <a-form layout="inline" :model="queryForm">
          <a-form-item label="意向等级">
            <a-select v-model:value="queryForm.intentionLevel" style="width: 120px">
              <a-select-option value="">全部</a-select-option>
              <a-select-option
                v-for="item in INTENTION_LEVEL_ENUM"
                :key="item.value"
                :value="item.value"
              >
                {{ item.desc }}
              </a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="handleSearch">查询</a-button>
          </a-form-item>
        </a-form>

        <!-- 表格 -->
        <a-table
          :dataSource="tableData"
          :columns="columns"
          :loading="tableLoading"
          :pagination="pagination"
          @change="handleTableChange"
          rowKey="customerId"
        >
          <!-- 自定义列 -->
          <template #intentionLevel="{ text }">
            <a-tag :color="getIntentionColor(text)">
              {{ INTENTION_LEVEL_ENUM[text]?.desc || text }}
            </a-tag>
          </template>
        </a-table>
      </a-tab-pane>
    </a-tabs>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { customerApi } from '/@/api/prototype/ai-sales/customer/customer-api';
import { INTENTION_LEVEL_ENUM } from '/@/constants/prototype/ai-sales/customer/customer-const';
import { message } from 'ant-design-vue';

// 状态定义
const activeTab = ref('current');
const tableLoading = ref(false);
const tableData = ref([]);
const statData = ref({
  totalCount: 0,
  sCount: 0,
  aCount: 0,
  bCount: 0,
});

const queryForm = reactive({
  intentionLevel: '',
  pageNum: 1,
  pageSize: 10,
});

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
});

// 表格列定义
const columns = [
  { title: '客户姓名', dataIndex: 'customerName', width: 120 },
  { title: '微信昵称', dataIndex: 'wechatName', width: 120 },
  {
    title: '意向等级',
    dataIndex: 'intentionLevel',
    width: 100,
    slots: { customRender: 'intentionLevel' },
  },
  { title: 'AI评分', dataIndex: 'aiScore', width: 100 },
  // ... 更多列
];

// 颜色映射
const getIntentionColor = (level) => {
  const colorMap = { S: 'red', A: 'orange', B: 'gold' };
  return colorMap[level] || 'default';
};

// 查询数据
const loadData = async () => {
  tableLoading.value = true;
  try {
    const res = await customerApi.queryPage({
      ...queryForm,
      poolType: activeTab.value,
    });

    if (res.code === 0) {
      tableData.value = res.data.list;
      pagination.total = res.data.total;
    }
  } catch (error) {
    message.error('加载失败');
  } finally {
    tableLoading.value = false;
  }
};

// 事件处理
const handleSearch = () => {
  queryForm.pageNum = 1;
  pagination.current = 1;
  loadData();
};

const handleTabChange = () => {
  queryForm.pageNum = 1;
  pagination.current = 1;
  loadData();
};

const handleTableChange = (pag) => {
  queryForm.pageNum = pag.current;
  queryForm.pageSize = pag.pageSize;
  pagination.current = pag.current;
  pagination.pageSize = pag.pageSize;
  loadData();
};

// 初始化
onMounted(() => {
  loadData();
});
</script>

<style scoped lang="less">
.customer-list-container {
  padding: 16px;

  .stat-card {
    margin-bottom: 16px;
  }
}
</style>
```

### Step 5: 路由配置 - 简化版 (5分钟)

```typescript
/*
 * AI销售助手原型路由
 *
 * @Author:    wangxiao
 * @Date:      2025-10-06
 * @Copyright  子午线高科智能科技 2025
 */

export const aiSalesPrototypeRouters = [
  {
    path: '/prototype/ai-sales',
    name: 'AISalesPrototype',
    redirect: '/prototype/ai-sales/customer',
    component: () => import('/@/layout/index.vue'),
    meta: {
      title: 'AI销售助手（原型）',
      icon: 'RobotOutlined',
    },
    children: [
      {
        path: '/prototype/ai-sales/customer',
        name: 'CustomerPrototype',
        component: () => import('/@/views/prototype/ai-sales/customer/customer-list.vue'),
        meta: {
          title: '客户管理',
          keepAlive: true,
        },
      },
      {
        path: '/prototype/ai-sales/customer/detail',
        name: 'CustomerDetailPrototype',
        component: () => import('/@/views/prototype/ai-sales/customer/customer-detail.vue'),
        meta: {
          title: '客户详情',
          hideInMenu: true,
        },
      },
    ],
  },
];
```

**集成到主路由**:
```typescript
// src/router/routers.ts
import { aiSalesPrototypeRouters } from './prototype/ai-sales';

export const routes = [
  // ... 其他路由
  ...aiSalesPrototypeRouters,
];
```

### Step 6: 快速测试 (15分钟)

**原型测试检查清单**:
- [ ] 页面能正常打开
- [ ] Mock数据能正常显示
- [ ] 基本交互功能正常（筛选、分页、Tab切换）
- [ ] 样式基本符合设计
- [ ] 核心流程能走通

**不需要的测试**:
- ❌ 不需要单元测试
- ❌ 不需要E2E测试
- ❌ 不需要覆盖所有边界场景

---

## ✅ 原型开发检查清单

### 开发前检查 (5分钟)

- [ ] 明确原型目标和核心功能
- [ ] 规划最小化文件结构（只创建必要文件）
- [ ] 准备Mock数据方案

### 开发中检查 (持续)

**必须遵守的规范** ⚠️:
- [ ] 文件头注释完整（作者、日期、Copyright）
- [ ] 方法注释包含 `@author wangxiao`
- [ ] 使用SmartEnum定义枚举
- [ ] 100% 使用 Ant Design Vue 组件
- [ ] API方法包含Mock数据降级

**可以简化的部分** ✅:
- [ ] 允许单个文件较长（500-800行可接受）
- [ ] 允许适度代码冗余（不强制抽取组件）
- [ ] 允许内联样式和逻辑
- [ ] 不强制TypeScript严格模式

### 开发后检查 (10分钟)

- [ ] ESLint无致命错误（警告可接受）
- [ ] 页面功能演示正常
- [ ] Mock数据展示合理
- [ ] 基本交互流畅

---

## 🎨 原型开发最佳实践

### 1. Mock数据要真实

```typescript
// ❌ 错误：假数据太假
const mockData = {
  customerName: 'test',
  phone: '12345678901',
  createTime: '2024-01-01',
};

// ✅ 正确：模拟真实场景
const mockData = {
  customerName: '张晓明',
  phone: '138****1234',
  createTime: '2025-10-05 14:32:15',
  wechatName: '奋斗的职场人',
  company: '上海某互联网公司',
  position: '产品经理',
};
```

### 2. 保持代码可读性

即使是原型，代码也要让其他人能看懂：

```typescript
// ✅ 好的代码组织
const loadCustomerData = async () => {
  tableLoading.value = true;
  try {
    const res = await customerApi.queryPage(queryForm);
    handleQuerySuccess(res);
  } catch (error) {
    handleQueryError(error);
  } finally {
    tableLoading.value = false;
  }
};
```

### 3. 关键交互要完整

原型虽然简化，但核心交互必须完整：

```vue
<!-- ✅ 完整的用户反馈 -->
<a-button
  type="primary"
  :loading="submitLoading"
  @click="handleSubmit"
>
  提交
</a-button>

<script setup>
const handleSubmit = async () => {
  submitLoading.value = true;
  try {
    await customerApi.add(formData);
    message.success('提交成功');
    router.push('/prototype/ai-sales/customer');
  } catch (error) {
    message.error('提交失败：' + error.message);
  } finally {
    submitLoading.value = false;
  }
};
</script>
```

### 4. 注释说明原型性质

```typescript
/**
 * 原型开发：客户管理API
 *
 * 注意：
 * 1. 当前使用Mock数据降级策略
 * 2. 后端API就绪后会自动切换到真实数据
 * 3. 生产环境需要移除Mock逻辑
 *
 * @author wangxiao
 */
export const customerApi = {
  // ...
};
```

---

## ⚠️ 原型开发注意事项

### 必须遵守的规范

即使是原型开发，以下规范**绝对不能放松**：

1. **文件头注释**: 必须包含作者、日期、Copyright
2. **方法注释**: 必须包含 `@author wangxiao`
3. **SmartEnum**: 枚举必须使用SmartEnum格式
4. **Ant Design Vue**: 必须100%使用官方组件
5. **目录结构**: 必须放在 `prototype/` 子目录下

### 可以简化的部分

以下方面在原型开发时可以适度放宽：

1. **代码长度**: 单文件500-800行可接受
2. **组件抽取**: 使用少于3次的不强制抽取
3. **TypeScript严格模式**: 允许any类型
4. **测试覆盖**: 不强制单元测试
5. **代码复用**: 允许适度冗余

### 禁止的做法

即使是原型，也**严格禁止**：

1. ❌ 自己实现UI组件（必须用Ant Design Vue）
2. ❌ 硬编码后端URL（必须走统一的axios封装）
3. ❌ 直接在页面写业务逻辑（至少要有API层）
4. ❌ 缺少错误处理（必须有try-catch和用户提示）

---

## 🔄 原型转生产的迁移路径

### 迁移清单

当原型验证通过，需要转为生产代码时：

**代码清理**:
- [ ] 移除Mock数据生成函数
- [ ] 移除API的try-catch降级逻辑
- [ ] 补充完整的TypeScript类型
- [ ] 拆分过长的组件（>500行）

**功能补全**:
- [ ] 补充完整的表单验证
- [ ] 补充完整的错误处理
- [ ] 补充完整的权限控制
- [ ] 补充单元测试

**目录迁移**:
- [ ] 从 `prototype/` 迁移到 `business/`
- [ ] 更新路由路径
- [ ] 更新菜单配置

### 迁移示例

```typescript
// 原型阶段
export const customerApi = {
  queryPage: async (param) => {
    try {
      return await postRequest('/api/customer/query', param);
    } catch (error) {
      // Mock降级
      return generateMockData();
    }
  },
};

// 生产阶段
export const customerApi = {
  /**
   * 分页查询客户 @author wangxiao
   */
  queryPage: (param: CustomerQueryForm) => {
    return postRequest<ResponseModel<PageResultModel<CustomerVO>>>(
      '/business/ai-sales/customer/queryPage',
      param
    );
  },
};
```

---

## 📚 参考案例

### 完整案例：AI销售助手原型

**实现文档**: `claudedocs/ai_sales_assistant_prototype_readme.md`

**代码位置**:
```
api/prototype/ai-sales/customer/
constants/prototype/ai-sales/customer/
views/prototype/ai-sales/customer/
router/prototype/ai-sales.ts
```

**关键特性**:
- Mock数据降级策略
- 双池视图（本期营 + 长期池）
- AI意向分析可视化
- 完整的CRUD交互

**开发时长**: 约6小时（含Mock数据准备）

**演示效果**: 无需后端即可完整演示所有功能

---

## 💡 常见问题

### Q1: 原型代码和生产代码能混在一起吗？

**A**: 不能！必须严格分离：
- 原型代码放 `prototype/` 目录
- 生产代码放 `business/` 或 `support/` 目录
- 路由路径也要区分（/prototype/ vs /business/）

### Q2: Mock数据降级会影响生产环境吗？

**A**: 不会，只要：
1. 生产环境确保后端API可用
2. 迁移到生产时移除Mock逻辑
3. 使用环境变量控制Mock开关（可选）

### Q3: 什么时候应该做原型，什么时候直接做生产代码？

**A**:
- **做原型**: 需求不明确、需要演示、需要快速验证
- **生产代码**: 需求清晰、后端已就绪、长期维护

### Q4: 原型开发可以跳过代码审查吗？

**A**:
- 可以简化审查流程
- 但核心规范（文件头、SmartEnum、Ant Design Vue）必须检查
- 代码可读性必须保证

---

## 📖 相关文档

- [前端代码规范](./FRONTEND_CODING_STANDARDS.md) - 必须遵守的规范
- [前端开发指南](./FRONTEND_DEV_GUIDE.md) - 标准开发流程

---

**最后提醒**:

原型开发的目的是**快速验证**，不是**降低质量**。核心规范必须遵守，关键交互必须完整，代码必须可读。原型验证通过后，要及时转为高质量的生产代码。
