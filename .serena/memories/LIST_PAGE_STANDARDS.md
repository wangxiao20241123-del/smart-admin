# SmartAdmin 列表页面标准规范

> **作者**: wangxiao | **企业**: 子午线高科智能科技 | **更新**: 2025-10-07

---

## 📋 标准结构

列表页面由 **3个核心部分** 组成，严格按以下顺序：

```vue
<template>
  <!-- 1️⃣ 查询表单区 (带权限控制) -->
  <a-form class="smart-query-form" v-privilege="'xxx:query'">
    ...
  </a-form>

  <!-- 2️⃣ 数据表格区 (包含操作栏、表格、分页) -->
  <a-card size="small" :bordered="false" :hoverable="true">
    <!-- 2.1 操作按钮栏 -->
    <a-row class="smart-table-btn-block">...</a-row>

    <!-- 2.2 数据表格 -->
    <a-table size="small" .../>

    <!-- 2.3 分页组件 -->
    <div class="smart-query-table-page">
      <a-pagination .../>
    </div>
  </a-card>
</template>
```

---

## 1️⃣ 查询表单区

### 强制要求

```vue
<!-- ✅ 正确 - 直接使用表单，添加权限控制 -->
<a-form class="smart-query-form" v-privilege="'oa:enterprise:query'">
  <a-row class="smart-query-form-row">
    <a-form-item label="关键字" class="smart-query-form-item">
      <a-input v-model:value="queryForm.keywords" placeholder="提示文字" />
    </a-form-item>

    <!-- 查询、重置按钮 -->
    <a-form-item class="smart-query-form-item smart-margin-left10">
      <a-button-group>
        <a-button type="primary" @click="onSearch">
          <template #icon><SearchOutlined /></template>
          查询
        </a-button>
        <a-button @click="resetQuery">
          <template #icon><ReloadOutlined /></template>
          重置
        </a-button>
      </a-button-group>
    </a-form-item>
  </a-row>
</a-form>

<!-- ❌ 错误 - 不要外层包裹 a-card -->
<a-card>
  <a-form class="smart-query-form">...</a-form>
</a-card>
```

### 关键点

| 规范 | 说明 |
|------|------|
| ✅ `v-privilege="'xxx:query'"` | 必须添加查询权限控制 |
| ✅ `class="smart-query-form"` | 使用统一样式类 |
| ✅ `class="smart-query-form-row"` | 行容器样式 |
| ✅ `class="smart-query-form-item"` | 表单项样式 |
| ❌ 外层不包 `a-card` | 查询区域不需要卡片 |

---

## 2️⃣ 数据表格区

### 2.1 操作按钮栏

```vue
<a-row class="smart-table-btn-block">
  <!-- 左侧：业务操作按钮 -->
  <div class="smart-table-operate-block">
    <a-button @click="add()" v-privilege="'xxx:add'" type="primary">
      <template #icon><PlusOutlined /></template>
      新建
    </a-button>
    <a-button @click="exportExcel()" v-privilege="'xxx:export'" type="primary">
      <template #icon><FileExcelOutlined /></template>
      导出
    </a-button>
  </div>

  <!-- 右侧：表格设置 -->
  <div class="smart-table-setting-block">
    <TableOperator
      v-model="columns"
      :tableId="TABLE_ID_CONST.BUSINESS.XXX"
      :refresh="ajaxQuery"
    />
  </div>
</a-row>
```

**强制要求**:
- ✅ 按钮必须添加 `v-privilege` 权限控制
- ✅ 必须包含 `TableOperator` 组件（表格列设置、刷新）
- ✅ 使用 `TABLE_ID_CONST` 定义表格ID

### 2.2 数据表格

```vue
<a-table
  :scroll="{ x: 1300 }"
  size="small"                    <!-- ✅ 必须加 size="small" -->
  :dataSource="tableData"
  :columns="columns"
  rowKey="xxxId"                  <!-- ✅ 主键字段 -->
  :pagination="false"              <!-- ✅ 表格内不分页 -->
  :loading="tableLoading"
  bordered
>
  <template #bodyCell="{ column, record, text }">
    <!-- 枚举值显示 -->
    <template v-if="column.dataIndex === 'type'">
      <span>{{ $smartEnumPlugin.getDescByValue('TYPE_ENUM', text) }}</span>
    </template>

    <!-- 操作列 -->
    <template v-if="column.dataIndex === 'action'">
      <div class="smart-table-operate">
        <a-button @click="update(record.id)" v-privilege="'xxx:update'" size="small" type="link">编辑</a-button>
        <a-button @click="del(record.id)" v-privilege="'xxx:delete'" size="small" danger type="link">删除</a-button>
      </div>
    </template>
  </template>
</a-table>
```

**强制要求**:
- ✅ `size="small"` - 紧凑显示
- ✅ `:pagination="false"` - 使用外部分页
- ✅ `rowKey="xxxId"` - 指定唯一键
- ✅ `bordered` - 显示边框
- ✅ 操作按钮带权限 `v-privilege`

### 2.3 分页组件

```vue
<div class="smart-query-table-page">
  <a-pagination
    showSizeChanger
    showQuickJumper
    show-less-items
    :pageSizeOptions="PAGE_SIZE_OPTIONS"
    :defaultPageSize="queryForm.pageSize"
    v-model:current="queryForm.pageNum"
    v-model:pageSize="queryForm.pageSize"
    :total="total"
    @change="ajaxQuery"
    :show-total="(total) => `共${total}条`"
  />
</div>
```

**强制要求**:
- ✅ 包裹在 `<div class="smart-query-table-page">` 中
- ✅ `showSizeChanger` - 显示每页条数选择器
- ✅ `showQuickJumper` - 显示快速跳转
- ✅ `:show-total` - 显示总数
- ✅ `@change="ajaxQuery"` - 页码变化时查询

---

## 📝 Script 部分标准

### 导入顺序

```typescript
import { reactive, ref, onMounted } from 'vue';
import { message, Modal } from 'ant-design-vue';
import { useRouter } from 'vue-router';
import { SmartLoading } from '/@/components/framework/smart-loading';
import { smartSentry } from '/@/lib/smart-sentry';
import { defaultTimeRanges } from '/@/lib/default-time-ranges';
import { PAGE_SIZE, PAGE_SIZE_OPTIONS } from '/@/constants/common-const';
import { TABLE_ID_CONST } from '/@/constants/support/table-id-const';
import TableOperator from '/@/components/support/table-operator/index.vue';
import { xxxApi } from '/@/api/xxx/xxx-api';
```

### 代码分区

```typescript
// ========== 表格列定义 ==========
const columns = ref([...]);

// ========== 查询表单 ==========
const queryFormState = { ... };
const queryForm = reactive({ ...queryFormState });
const tableLoading = ref(false);
const tableData = ref([]);
const total = ref(0);

// ========== 查询方法 ==========
function onSearch() {
  queryForm.pageNum = 1;
  ajaxQuery();
}

function resetQuery() {
  Object.assign(queryForm, queryFormState);
  ajaxQuery();
}

async function ajaxQuery() {
  try {
    tableLoading.value = true;
    const res = await xxxApi.queryPage(queryForm);
    tableData.value = res.data.list;
    total.value = res.data.total;
  } catch (e) {
    smartSentry.captureError(e);
  } finally {
    tableLoading.value = false;
  }
}

// ========== 增删改 ==========
function add() { ... }
function update(id) { ... }
function confirmDelete(id) { ... }

// ========== 初始化 ==========
onMounted(ajaxQuery);
```

---

## ✅ 检查清单

### 结构检查
- [ ] 查询表单直接使用，不包裹 `a-card`
- [ ] 查询表单有 `v-privilege` 权限
- [ ] 表格区域使用 `a-card` 包裹
- [ ] 包含操作按钮栏 `smart-table-btn-block`
- [ ] 包含 `TableOperator` 组件
- [ ] 分页组件在表格下方

### 表格检查
- [ ] `size="small"`
- [ ] `:pagination="false"`
- [ ] `rowKey` 指定唯一键
- [ ] 操作列按钮带 `v-privilege`

### 分页检查
- [ ] `showSizeChanger`
- [ ] `showQuickJumper`
- [ ] `:show-total`
- [ ] `@change="ajaxQuery"`

### 代码检查
- [ ] 使用 `smartSentry.captureError` 处理错误
- [ ] 使用 `SmartLoading` 显示加载
- [ ] 使用 `TABLE_ID_CONST` 定义表格ID
- [ ] 枚举值使用 `$smartEnumPlugin`

---

## 🚫 常见错误

| 错误 | 正确做法 |
|------|----------|
| ❌ 查询表单外包 `a-card` | ✅ 直接使用 `a-form` |
| ❌ 查询表单缺少 `v-privilege` | ✅ 添加 `v-privilege="'xxx:query'"` |
| ❌ 表格缺少 `size="small"` | ✅ 添加 `size="small"` |
| ❌ 表格内分页 `:pagination="true"` | ✅ 使用 `:pagination="false"` + 外部分页 |
| ❌ 缺少 `TableOperator` | ✅ 添加表格设置组件 |
| ❌ 操作按钮无权限控制 | ✅ 添加 `v-privilege` |
| ❌ 分页无 `@change` | ✅ 添加 `@change="ajaxQuery"` |

---

## 📖 参考示例

标准示例: `views/business/oa/enterprise/enterprise-list.vue`

---

**核心原则**: 结构统一、权限完整、功能完善、用户友好。
