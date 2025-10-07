<!--
  [模块]列表

  @Author:    wangxiao
  @Date:      [日期]
  @Copyright  子午线高科智能科技 2025
-->
<template>
  <!-- 1️⃣ 查询表单区 -->
  <a-form class="smart-query-form" v-privilege="'[模块]:query'">
    <a-row class="smart-query-form-row">
      <a-form-item label="关键字" class="smart-query-form-item">
        <a-input
          v-model:value="queryForm.keywords"
          placeholder="请输入关键字"
          style="width: 300px"
        />
      </a-form-item>

      <a-form-item label="创建时间" class="smart-query-form-item">
        <a-range-picker
          v-model:value="searchDate"
          :presets="defaultTimeRanges"
          @change="dateChange"
        />
      </a-form-item>

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

  <!-- 2️⃣ 数据表格区 -->
  <a-card size="small" :bordered="false" :hoverable="true">
    <!-- 2.1 操作按钮栏 -->
    <a-row class="smart-table-btn-block">
      <div class="smart-table-operate-block">
        <a-button @click="add()" v-privilege="'[模块]:add'" type="primary">
          <template #icon><PlusOutlined /></template>
          新建
        </a-button>
        <a-button @click="exportExcel()" v-privilege="'[模块]:export'" type="primary">
          <template #icon><FileExcelOutlined /></template>
          导出
        </a-button>
      </div>
      <div class="smart-table-setting-block">
        <TableOperator
          v-model="columns"
          :tableId="TABLE_ID_CONST.[分类].[模块]"
          :refresh="ajaxQuery"
        />
      </div>
    </a-row>

    <!-- 2.2 数据表格 -->
    <a-table
      :scroll="{ x: 1300 }"
      size="small"
      :dataSource="tableData"
      :columns="columns"
      rowKey="[主键]"
      :pagination="false"
      :loading="tableLoading"
      bordered
    >
      <template #bodyCell="{ column, record, text }">
        <!-- 枚举值示例 -->
        <template v-if="column.dataIndex === 'status'">
          <span>{{ $smartEnumPlugin.getDescByValue('STATUS_ENUM', text) }}</span>
        </template>

        <!-- 操作列 -->
        <template v-if="column.dataIndex === 'action'">
          <div class="smart-table-operate">
            <a-button
              @click="update(record.[主键])"
              v-privilege="'[模块]:update'"
              size="small"
              type="link"
            >编辑</a-button>
            <a-button
              @click="confirmDelete(record.[主键])"
              v-privilege="'[模块]:delete'"
              size="small"
              danger
              type="link"
            >删除</a-button>
          </div>
        </template>
      </template>
    </a-table>

    <!-- 2.3 分页组件 -->
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

    <!-- 操作弹窗 -->
    <[模块]Operate ref="operateRef" @refresh="ajaxQuery" />
  </a-card>
</template>

<script setup lang="ts">
  import { reactive, ref, onMounted } from 'vue';
  import { message, Modal } from 'ant-design-vue';
  import { useRouter } from 'vue-router';
  import { SmartLoading } from '/@/components/framework/smart-loading';
  import { smartSentry } from '/@/lib/smart-sentry';
  import { defaultTimeRanges } from '/@/lib/default-time-ranges';
  import { PAGE_SIZE, PAGE_SIZE_OPTIONS } from '/@/constants/common-const';
  import { TABLE_ID_CONST } from '/@/constants/support/table-id-const';
  import TableOperator from '/@/components/support/table-operator/index.vue';
  import { [模块]Api } from '/@/api/[分类]/[模块]/[模块]-api';
  import type { [模块]QueryForm, [模块]VO } from '/@/api/[分类]/[模块]/[模块]-model';
  import [模块]Operate from './components/[模块]-operate-modal.vue';

  // ========== 表格列定义 ==========
  const columns = ref([
    {
      title: '[列名]',
      dataIndex: '[字段]',
      width: 150,
      ellipsis: true,
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 100,
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      width: 150,
    },
    {
      title: '操作',
      dataIndex: 'action',
      width: 120,
    },
  ]);

  // ========== 查询表单 ==========
  const queryFormState: [模块]QueryForm = {
    keywords: '',
    startTime: undefined,
    endTime: undefined,
    pageNum: 1,
    pageSize: PAGE_SIZE,
  };

  const queryForm = reactive({ ...queryFormState });
  const tableLoading = ref(false);
  const tableData = ref<[模块]VO[]>([]);
  const total = ref(0);
  const searchDate = ref();

  // ========== 查询方法 ==========
  function dateChange(dates: any, dateStrings: string[]) {
    queryForm.startTime = dateStrings[0];
    queryForm.endTime = dateStrings[1];
  }

  function onSearch() {
    queryForm.pageNum = 1;
    ajaxQuery();
  }

  function resetQuery() {
    searchDate.value = undefined;
    Object.assign(queryForm, queryFormState);
    ajaxQuery();
  }

  async function ajaxQuery() {
    try {
      tableLoading.value = true;
      const res = await [模块]Api.queryPage(queryForm);
      tableData.value = res.data.list;
      total.value = res.data.total;
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      tableLoading.value = false;
    }
  }

  // ========== 导出 ==========
  async function exportExcel() {
    try {
      SmartLoading.show();
      await [模块]Api.exportExcel(queryForm);
      message.success('导出成功');
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      SmartLoading.hide();
    }
  }

  // ========== 删除 ==========
  function confirmDelete(id: number) {
    Modal.confirm({
      title: '确定要删除吗？',
      content: '删除后，该信息将不可恢复',
      okText: '删除',
      okType: 'danger',
      onOk() {
        del(id);
      },
      cancelText: '取消',
    });
  }

  async function del(id: number) {
    try {
      SmartLoading.show();
      await [模块]Api.delete(id);
      message.success('删除成功');
      ajaxQuery();
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      SmartLoading.hide();
    }
  }

  // ========== 增加、修改 ==========
  const router = useRouter();
  const operateRef = ref();

  function add() {
    operateRef.value.showModal();
  }

  function update(id: number) {
    operateRef.value.showModal(id);
  }

  // ========== 初始化 ==========
  onMounted(ajaxQuery);
</script>

<style scoped lang="less">
  // 自定义样式
</style>
