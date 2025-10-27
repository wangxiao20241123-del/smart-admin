<!--
  企业微信聊天会话列表（原型开发）

  @Author:    wangxiao
  @Date:      2025-10-07
  @Copyright  子午线高科智能科技 2025
-->
<template>
  <div class="chat-record-container">
    <!-- 查询表单区 -->
    <a-form class="smart-query-form" v-privilege="'wecom:chat:query'">
      <a-row class="smart-query-form-row">
        <a-form-item label="关键字搜索" class="smart-query-form-item">
          <a-input
            v-model:value="queryForm.keyword"
            placeholder="伙伴姓名、学员姓名、微信ID"
            style="width: 300px"
            allow-clear
          >
            <template #prefix><SearchOutlined /></template>
          </a-input>
        </a-form-item>

        <a-form-item label="时间范围" class="smart-query-form-item">
          <a-range-picker
            v-model:value="timeRange"
            :show-time="true"
            format="YYYY-MM-DD HH:mm:ss"
            style="width: 400px"
            :presets="timePresets"
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

    <!-- 数据表格区 -->
    <a-card size="small" :bordered="false" :hoverable="true">
      <!-- 操作按钮栏 -->
      <a-row class="smart-table-btn-block">
        <div class="smart-table-operate-block">
          <a-button @click="exportExcel" v-privilege="'wecom:chat:export'" type="primary" :disabled="selectedRowKeys.length === 0">
            <template #icon><FileExcelOutlined /></template>
            导出选中 ({{ selectedRowKeys.length }})
          </a-button>
          <a-button v-if="selectedRowKeys.length > 0" @click="clearSelection" size="small">
            清空选择
          </a-button>
        </div>
        <div class="smart-table-setting-block">
          <TableOperator
            v-model="columns"
            :tableId="TABLE_ID_CONST.PROTOTYPE.WECOM.CHAT_RECORD"
            :refresh="ajaxQuery"
          />
        </div>
      </a-row>

      <!-- 数据表格 -->
      <a-table
        :scroll="{ x: 1200 }"
        size="small"
        :dataSource="tableData"
        :columns="columns"
        :loading="tableLoading"
        :pagination="false"
        rowKey="id"
        bordered
        :row-selection="{
          selectedRowKeys: selectedRowKeys,
          onChange: onSelectChange,
        }"
      >
        <template #bodyCell="{ column, record, text }">
          <!-- 伙伴信息 -->
          <template v-if="column.dataIndex === 'partner'">
            <div class="user-cell">
              <a-avatar :src="record.partnerAvatar" :size="32">
                <template #icon><UserOutlined /></template>
              </a-avatar>
              <span class="user-name">{{ record.partnerRemark || record.partnerNickname }}</span>
            </div>
          </template>

          <!-- 学员信息 -->
          <template v-if="column.dataIndex === 'student'">
            <div class="user-cell">
              <a-avatar :src="record.studentAvatar" :size="32">
                <template #icon><UserOutlined /></template>
              </a-avatar>
              <span class="user-name">{{ record.studentRemark || record.studentNickname }}</span>
            </div>
          </template>

          <!-- 最新消息 -->
          <template v-if="column.dataIndex === 'lastMsg'">
            <div class="msg-cell">
              <div class="msg-content">{{ record.lastMsgContent || '暂无消息' }}</div>
              <div class="msg-time">{{ formatRelativeTime(record.lastMsgTime) }}</div>
            </div>
          </template>

          <!-- 会话时段 -->
          <template v-if="column.dataIndex === 'timeRange'">
            <span>{{ formatDateRange(record.firstMsgTime, record.latestMsgTime) }}</span>
          </template>

          <!-- 操作列 -->
          <template v-if="column.dataIndex === 'action'">
            <div class="smart-table-operate">
              <a-button @click="showDetail(record.id)" size="small" type="link">查看详情</a-button>
            </div>
          </template>
        </template>
      </a-table>

      <!-- 分页 -->
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
          :show-total="(total) => `共 ${total} 条记录`"
        />
      </div>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted, computed } from 'vue';
import { message } from 'ant-design-vue';
import {
  SearchOutlined,
  ReloadOutlined,
  FileExcelOutlined,
  UserOutlined,
} from '@ant-design/icons-vue';
import { SmartLoading } from '/@/components/framework/smart-loading';
import { smartSentry } from '/@/lib/smart-sentry';
import { PAGE_SIZE, PAGE_SIZE_OPTIONS } from '/@/constants/common-const';
import { TABLE_ID_CONST } from '/@/constants/support/table-id-const';
import TableOperator from '/@/components/support/table-operator/index.vue';
import { chatSessionApi } from '/@/api/prototype/wecom/chat-session-api';
import type { ChatSessionQueryForm, ChatSessionVO } from '/@/api/prototype/wecom/chat-session-model';
import dayjs, { Dayjs } from 'dayjs';
import relativeTime from 'dayjs/plugin/relativeTime';
import 'dayjs/locale/zh-cn';

// 配置 dayjs
dayjs.extend(relativeTime);
dayjs.locale('zh-cn');

// ========== 时间快捷选项 ==========
const timePresets = ref([
  { label: '今天', value: [dayjs().startOf('day'), dayjs()] as [Dayjs, Dayjs] },
  { label: '昨天', value: [dayjs().subtract(1, 'day').startOf('day'), dayjs().subtract(1, 'day').endOf('day')] as [Dayjs, Dayjs] },
  { label: '最近3天', value: [dayjs().subtract(3, 'day'), dayjs()] as [Dayjs, Dayjs] },
  { label: '最近7天', value: [dayjs().subtract(7, 'day'), dayjs()] as [Dayjs, Dayjs] },
  { label: '最近30天', value: [dayjs().subtract(30, 'day'), dayjs()] as [Dayjs, Dayjs] },
  { label: '本月', value: [dayjs().startOf('month'), dayjs()] as [Dayjs, Dayjs] },
]);

// ========== 表格列定义 ==========
const columns = ref([
  {
    title: '伙伴',
    dataIndex: 'partner',
    width: 150,
  },
  {
    title: '学员',
    dataIndex: 'student',
    width: 150,
  },
  {
    title: '最新消息',
    dataIndex: 'lastMsg',
    width: 300,
  },
  {
    title: '会话时段',
    dataIndex: 'timeRange',
    width: 180,
  },
  {
    title: '伙伴消息',
    dataIndex: 'partnerMsgCount',
    width: 100,
    align: 'center' as const,
    sorter: true,
  },
  {
    title: '学员消息',
    dataIndex: 'studentMsgCount',
    width: 100,
    align: 'center' as const,
    sorter: true,
  },
  {
    title: '总消息数',
    dataIndex: 'totalMsgCount',
    width: 100,
    align: 'center' as const,
    sorter: true,
  },
  {
    title: '操作',
    dataIndex: 'action',
    width: 120,
    fixed: 'right' as const,
  },
]);

// ========== 查询表单 ==========
const queryFormState: ChatSessionQueryForm = {
  keyword: undefined,
  partnerName: undefined,
  studentName: undefined,
  studentWechatId: undefined,
  startTime: undefined,
  endTime: undefined,
  sortField: undefined,
  sortOrder: 'desc',
  pageNum: 1,
  pageSize: 10,
};
const queryForm = reactive({ ...queryFormState });
const tableLoading = ref(false);
const tableData = ref<ChatSessionVO[]>([]);
const total = ref(0);
const timeRange = ref<[Dayjs, Dayjs]>();

// ========== 多选 ==========
const selectedRowKeys = ref<number[]>([]);

// ========== 查询方法 ==========
function onSearch() {
  queryForm.pageNum = 1;
  ajaxQuery();
}

function resetQuery() {
  Object.assign(queryForm, queryFormState);
  timeRange.value = undefined;
  ajaxQuery();
}

async function ajaxQuery() {
  try {
    // 处理时间范围
    if (timeRange.value && timeRange.value.length === 2) {
      queryForm.startTime = timeRange.value[0].format('YYYY-MM-DD HH:mm:ss');
      queryForm.endTime = timeRange.value[1].format('YYYY-MM-DD HH:mm:ss');
    } else {
      queryForm.startTime = undefined;
      queryForm.endTime = undefined;
    }

    // 关键字搜索拆分
    if (queryForm.keyword) {
      queryForm.partnerName = queryForm.keyword;
      queryForm.studentName = queryForm.keyword;
    } else {
      queryForm.partnerName = undefined;
      queryForm.studentName = undefined;
    }

    tableLoading.value = true;
    const res = await chatSessionApi.queryPage(queryForm);
    tableData.value = res.data.list;
    total.value = res.data.total;
  } catch (e) {
    smartSentry.captureError(e);
  } finally {
    tableLoading.value = false;
  }
}

// ========== 多选方法 ==========
function onSelectChange(keys: number[]) {
  selectedRowKeys.value = keys;
}

function clearSelection() {
  selectedRowKeys.value = [];
}

// ========== 导出 ==========
async function exportExcel() {
  if (selectedRowKeys.value.length === 0) {
    message.warning('请选择要导出的会话');
    return;
  }

  try {
    SmartLoading.show('正在导出...');

    // 获取选中的数据
    const selectedData = tableData.value.filter(item => selectedRowKeys.value.includes(item.id));

    message.success(`已选择 ${selectedData.length} 条会话，导出功能开发中`);

    // TODO: 调用导出API
    // await chatSessionApi.exportSessions({
    //   sessionIds: selectedRowKeys.value,
    // });
  } catch (e) {
    smartSentry.captureError(e);
  } finally {
    SmartLoading.hide();
  }
}

// ========== 详情 ==========
function showDetail(id: number) {
  message.info('详情功能待实现');
}

// ========== 工具方法 ==========
function formatTime(timestamp: string) {
  return dayjs(timestamp).format('YYYY-MM-DD HH:mm:ss');
}

function formatRelativeTime(timestamp: string) {
  const now = dayjs();
  const time = dayjs(timestamp);
  const diffHours = now.diff(time, 'hour');

  if (diffHours < 1) {
    return time.fromNow();
  } else if (diffHours < 24) {
    return `${diffHours}小时前`;
  } else {
    return time.format('YYYY-MM-DD HH:mm');
  }
}

function formatDateRange(startTime: string, endTime: string) {
  const start = dayjs(startTime);
  const end = dayjs(endTime);
  const diffDays = end.diff(start, 'day');

  if (diffDays === 0) {
    return start.format('YYYY-MM-DD');
  } else if (diffDays < 7) {
    return `${start.format('MM-DD')} ~ ${end.format('MM-DD')} (${diffDays + 1}天)`;
  } else {
    return `${start.format('YYYY-MM-DD')} ~ ${end.format('YYYY-MM-DD')}`;
  }
}

// ========== 初始化 ==========
onMounted(() => {
  ajaxQuery();
});
</script>

<style scoped lang="less">
.chat-record-container {
  // 表格样式
  .user-cell {
    display: flex;
    align-items: center;
    gap: 8px;

    .user-name {
      font-size: 14px;
      color: #262626;
    }
  }

  .msg-cell {
    .msg-content {
      font-size: 14px;
      color: #262626;
      margin-bottom: 4px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .msg-time {
      font-size: 12px;
      color: #8c8c8c;
    }
  }
}
</style>
