<!--
  * 学员跟进 - 学员列表(双池视图)
  *
  * @Author:    wangxiao
  * @Date:      2025-10-06
  * @Copyright  子午线高科智能科技 2025
-->
<template>
  <!-- Tab切换 -->
  <a-tabs v-model:activeKey="activeTab" @change="onTabChange">
    <!-- 本期营Tab -->
    <a-tab-pane key="current" tab="本期营">
      <template #tab>
        <span>
          <UserOutlined />
          本期营 (120人)
        </span>
      </template>

      <!-- 查询表单 -->
      <a-form class="smart-query-form">
            <a-row class="smart-query-form-row">
              <a-form-item label="关键字" class="smart-query-form-item">
                <a-input
                  style="width: 200px"
                  v-model:value="queryForm.studentName"
                  placeholder="姓名/手机号"
                  allow-clear
                />
              </a-form-item>

              <a-form-item label="意向等级" class="smart-query-form-item">
                <a-select
                  style="width: 150px"
                  v-model:value="queryForm.intentionLevel"
                  placeholder="全部"
                  allow-clear
                >
                  <a-select-option value="S">S量 (90-100分)</a-select-option>
                  <a-select-option value="A">A量 (80-89分)</a-select-option>
                  <a-select-option value="B">B量 (70-79分)</a-select-option>
                </a-select>
              </a-form-item>

              <a-form-item label="关键节点" class="smart-query-form-item">
                <a-checkbox-group v-model:value="filterNodes">
                  <a-checkbox value="appliedPlan">已申请规划</a-checkbox>
                  <a-checkbox value="pricing">已规划报价</a-checkbox>
                  <a-checkbox value="deal">已成交</a-checkbox>
                </a-checkbox-group>
              </a-form-item>

              <a-form-item class="smart-query-form-item smart-margin-left10">
                <a-button-group>
                  <a-button type="primary" @click="onSearch">
                    <template #icon>
                      <SearchOutlined />
                    </template>
                    查询
                  </a-button>
                  <a-button @click="resetQuery">
                    <template #icon>
                      <ReloadOutlined />
                    </template>
                    重置
                  </a-button>
                </a-button-group>
              </a-form-item>
            </a-row>
      </a-form>

      <!-- 卡片列表容器 -->
      <a-card size="small" :bordered="false" :hoverable="true">
        <!-- 学员卡片列表 - 使用 List + Grid 标准方案 -->
          <a-list
            :grid="{ gutter: 16, xs: 1, sm: 2, md: 2, lg: 3, xl: 4, xxl: 5 }"
            :data-source="tableData"
            :loading="tableLoading"
            :locale="{ emptyText: '暂无学员数据' }"
          >
            <template #renderItem="{ item: student }">
              <a-list-item>
                <a-card
                  hoverable
                  class="student-card-v3"
                  :class="{'high-priority': student.intentionLevel === 'S'}"
                  @click="viewDetail(student.studentId)"
                  :body-style="{ padding: '16px' }"
                >
                  <!-- 头部:头像 + 基本信息 + 右侧分数 -->
                  <div class="card-header">
                    <a-list-item-meta>
                      <template #avatar>
                        <a-avatar :size="56" :src="student.avatar">
                          {{ student.studentName?.substring(0, 1) }}
                        </a-avatar>
                      </template>
                      <template #title>
                        {{ student.studentName || '微信用户' }}
                      </template>
                      <template #description>
                        {{ getBackgroundSummary(student) }}
                      </template>
                    </a-list-item-meta>

                    <!-- 右侧分数徽章 -->
                    <div class="score-badge">
                      <div class="score-number" :style="{ color: getLevelColor(student.intentionLevel) }">
                        {{ student.comprehensiveScore }}
                      </div>
                      <div class="score-level" :style="{ color: getLevelColor(student.intentionLevel) }">
                        {{ student.intentionLevel }}级
                      </div>
                    </div>
                  </div>

                  <a-divider style="margin: 12px 0" />

                  <!-- 学员背景描述 -->
                  <div class="customer-background">
                    {{ getStudentBackground(student) }}
                  </div>

                  <!-- 为什么重要 -->
                  <div class="priority-reason">
                    {{ getPriorityReason(student) }}
                  </div>

                  <!-- 关键标签 -->
                  <div class="action-tags">
                    <a-tag v-if="student.hasAppliedPlan" color="success">已申请规划</a-tag>
                    <a-tag v-if="student.hasPricing" color="orange">已报价</a-tag>
                    <a-tag v-if="student.isDeal" color="gold">已成交</a-tag>
                  </div>
                </a-card>
              </a-list-item>
            </template>
        </a-list>

        <div class="smart-query-table-page">
          <a-pagination
            showSizeChanger
            showQuickJumper
            :current="queryForm.pageNum"
            :pageSize="queryForm.pageSize"
            :total="total"
            :pageSizeOptions="['10', '20', '50', '100']"
            @change="onPageChange"
            @showSizeChange="onPageSizeChange"
            :showTotal="(total) => `共 ${total} 条`"
          />
        </div>
      </a-card>
    </a-tab-pane>

    <!-- 长期池Tab -->
    <a-tab-pane key="longterm" tab="长期池">
      <template #tab>
        <span>
          <DatabaseOutlined />
          长期池 (1140人)
        </span>
      </template>

      <!-- 长期池筛选 -->
      <a-form class="smart-query-form">
            <a-row class="smart-query-form-row">
              <a-form-item label="潜力筛选" class="smart-query-form-item">
                <a-select
                  style="width: 200px"
                  v-model:value="longTermFilter.potentialLevel"
                  placeholder="全部"
                  allow-clear
                >
                  <a-select-option value="high">往期S/A量 - 高潜力</a-select-option>
                  <a-select-option value="medium">往期B量 - 中潜力</a-select-option>
                  <a-select-option value="silent">沉默学员 (30天+)</a-select-option>
                </a-select>
              </a-form-item>

              <a-form-item label="未成交原因" class="smart-query-form-item">
                <a-select
                  style="width: 150px"
                  v-model:value="longTermFilter.notDealReason"
                  placeholder="全部"
                  allow-clear
                >
                  <a-select-option value="1">价格敏感</a-select-option>
                  <a-select-option value="2">延迟购买</a-select-option>
                  <a-select-option value="3">效果怀疑</a-select-option>
                  <a-select-option value="4">需要商量</a-select-option>
                </a-select>
              </a-form-item>

              <a-form-item class="smart-query-form-item smart-margin-left10">
                <a-button type="primary" @click="searchLongTerm">
                  <template #icon>
                    <SearchOutlined />
                  </template>
                  筛选激活
                </a-button>
                <a-button style="margin-left: 8px" @click="resetLongTermFilter">
                  <template #icon>
                    <ReloadOutlined />
                  </template>
                  重置
                </a-button>
              </a-form-item>
            </a-row>
      </a-form>

      <!-- 卡片列表容器 -->
      <a-card size="small" :bordered="false" :hoverable="true">
        <!-- 长期池卡片列表 - 使用 List + Grid 标准方案 -->
          <a-list
            :grid="{ gutter: 16, xs: 1, sm: 2, md: 2, lg: 3, xl: 4, xxl: 5 }"
            :data-source="longTermData"
            :loading="longTermLoading"
            :locale="{ emptyText: '暂无长期池学员数据' }"
          >
            <template #renderItem="{ item: student }">
              <a-list-item>
                <a-card
                  hoverable
                  class="student-card-v3 longterm-card"
                  @click="viewDetail(student.studentId)"
                  :body-style="{ padding: '16px' }"
                >
                  <!-- 头部:头像 + 基本信息 + 右侧分数 -->
                  <div class="card-header">
                    <a-list-item-meta>
                      <template #avatar>
                        <a-avatar :size="56" :src="student.avatar">
                          {{ student.studentName?.substring(0, 1) }}
                        </a-avatar>
                      </template>
                      <template #title>
                        {{ student.studentName || '微信用户' }}
                      </template>
                      <template #description>
                        {{ getLongTermBackground(student) }}
                      </template>
                    </a-list-item-meta>

                    <!-- 右侧分数徽章 -->
                    <div class="score-badge">
                      <div class="score-number" style="color: #faad14">
                        {{ student.comprehensiveScore }}
                      </div>
                      <div class="score-level" style="color: #faad14">
                        往期{{ student.intentionLevel }}级
                      </div>
                    </div>
                  </div>

                  <a-divider style="margin: 12px 0" />

                  <!-- 学员背景描述 -->
                  <div class="customer-background">
                    {{ getLongTermStudentBackground(student) }}
                  </div>

                  <!-- 为什么重要 -->
                  <div class="priority-reason">
                    {{ getLongTermReason(student) }}
                  </div>

                  <!-- 未成交原因标签 -->
                  <div class="action-tags">
                    <a-tag color="warning">
                      {{ getNotDealReasonText(student.notDealReasonType) }}
                    </a-tag>
                    <a-tag color="default">沉默{{ student.silentDays }}天</a-tag>
                  </div>
                </a-card>
              </a-list-item>
            </template>
        </a-list>

        <div class="smart-query-table-page">
          <a-pagination
            showSizeChanger
            showQuickJumper
            :current="longTermQuery.pageNum"
            :pageSize="longTermQuery.pageSize"
            :total="longTermTotal"
            :pageSizeOptions="['10', '20', '50', '100']"
            @change="onLongTermPageChange"
            @showSizeChange="onLongTermPageSizeChange"
            :showTotal="(total) => `共 ${total} 条`"
          />
        </div>
      </a-card>
    </a-tab-pane>
  </a-tabs>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { message } from 'ant-design-vue';
import { studentApi } from '/@/api/prototype/student-followup/student-api';
import {
  UserOutlined,
  DatabaseOutlined,
  SearchOutlined,
  ReloadOutlined,
  CheckCircleOutlined,
  DollarOutlined,
  TrophyOutlined,
  ClockCircleOutlined,
  PlayCircleOutlined,
  MessageOutlined,
  RobotOutlined,
  FieldTimeOutlined,
  ThunderboltOutlined,
} from '@ant-design/icons-vue';

const router = useRouter();

// ==================== 数据定义 ====================
const activeTab = ref('current');
const tableLoading = ref(false);
const longTermLoading = ref(false);

// 本期营查询条件
const queryForm = reactive({
  pageNum: 1,
  pageSize: 20,
  studentName: '',
  intentionLevel: undefined,
  batchType: 1, // 本期营
  trainingCampPeriod: 10, // 第10期
});

const filterNodes = ref([]);
const tableData = ref([]);
const total = ref(0);

// 长期池查询条件
const longTermQuery = reactive({
  pageNum: 1,
  pageSize: 20,
  batchType: 2, // 长期池
});

const longTermFilter = reactive({
  potentialLevel: undefined,
  notDealReason: undefined,
});

const longTermData = ref([]);
const longTermTotal = ref(0);

// ==================== 表格列定义 ====================
const currentColumns = [
  {
    title: '学员信息',
    dataIndex: 'customerInfo',
    width: 250,
    fixed: 'left',
  },
  {
    title: '意向分级',
    dataIndex: 'intentionLevel',
    width: 120,
    align: 'center',
  },
  {
    title: '综合评分',
    dataIndex: 'comprehensiveScore',
    width: 100,
    align: 'center',
    sorter: (a, b) => a.comprehensiveScore - b.comprehensiveScore,
  },
  {
    title: '关键节点',
    dataIndex: 'keyNodes',
    width: 250,
  },
  {
    title: '最近动态',
    dataIndex: 'lastActivity',
    width: 300,
  },
  {
    title: '直播观看',
    dataIndex: 'liveWatchCount',
    width: 100,
    align: 'center',
  },
  {
    title: '操作',
    dataIndex: 'action',
    width: 250,
    fixed: 'right',
  },
];

const longTermColumns = [
  {
    title: '学员信息',
    dataIndex: 'customerInfo',
    width: 250,
    fixed: 'left',
  },
  {
    title: '历史意向',
    dataIndex: 'historicalLevel',
    width: 150,
    align: 'center',
  },
  {
    title: '未成交原因',
    dataIndex: 'notDealReason',
    width: 150,
  },
  {
    title: '最后联系',
    dataIndex: 'lastContactTime',
    width: 150,
  },
  {
    title: '沉默天数',
    dataIndex: 'silentDays',
    width: 100,
    align: 'center',
    sorter: (a, b) => a.silentDays - b.silentDays,
  },
  {
    title: '操作',
    dataIndex: 'action',
    width: 200,
    fixed: 'right',
  },
];

// ==================== 生命周期 ====================
onMounted(() => {
  queryList();
});

// ==================== 业务方法 ====================

// 查询本期营列表
async function queryList() {
  try {
    tableLoading.value = true;
    const params = {
      ...queryForm,
      hasAppliedPlan: filterNodes.value.includes('appliedPlan') ? true : undefined,
      hasPricing: filterNodes.value.includes('pricing') ? true : undefined,
      isDeal: filterNodes.value.includes('deal') ? true : undefined,
    };
    const res = await studentApi.queryPage(params);
    if (res.data) {
      tableData.value = generateMockData(res.data.list || []);
      total.value = res.data.total || 120;
    }
  } catch (error) {
    message.error('查询失败');
    // 生成模拟数据用于演示
    tableData.value = generateMockData([]);
    total.value = 120;
  } finally {
    tableLoading.value = false;
  }
}

// 查询长期池列表
async function queryLongTermList() {
  try {
    longTermLoading.value = true;
    const params = {
      ...longTermQuery,
      ...longTermFilter,
    };
    const res = await studentApi.queryPage(params);
    if (res.data) {
      longTermData.value = generateMockLongTermData(res.data.list || []);
      longTermTotal.value = res.data.total || 1140;
    }
  } catch (error) {
    message.error('查询失败');
    longTermData.value = generateMockLongTermData([]);
    longTermTotal.value = 1140;
  } finally {
    longTermLoading.value = false;
  }
}

// Tab切换
function onTabChange(key) {
  activeTab.value = key;
  if (key === 'longterm') {
    queryLongTermList();
  } else {
    queryList();
  }
}

// 搜索
function onSearch() {
  queryForm.pageNum = 1;
  queryList();
}

// 重置查询
function resetQuery() {
  queryForm.studentName = '';
  queryForm.intentionLevel = undefined;
  filterNodes.value = [];
  queryForm.pageNum = 1;
  queryList();
}

// 长期池筛选
function searchLongTerm() {
  longTermQuery.pageNum = 1;
  queryLongTermList();
}

// 重置长期池筛选
function resetLongTermFilter() {
  longTermFilter.potentialLevel = undefined;
  longTermFilter.notDealReason = undefined;
  longTermQuery.pageNum = 1;
  queryLongTermList();
}

// 分页变化
function onPageChange(pageNum, pageSize) {
  queryForm.pageNum = pageNum;
  queryForm.pageSize = pageSize;
  queryList();
}

function onPageSizeChange(current, size) {
  queryForm.pageNum = 1;
  queryForm.pageSize = size;
  queryList();
}

function onLongTermPageChange(pageNum, pageSize) {
  longTermQuery.pageNum = pageNum;
  longTermQuery.pageSize = pageSize;
  queryLongTermList();
}

function onLongTermPageSizeChange(current, size) {
  longTermQuery.pageNum = 1;
  longTermQuery.pageSize = size;
  queryLongTermList();
}

// 查看详情
function viewDetail(studentId) {
  router.push({
    path: '/prototype/student-followup/student/detail',
    query: { id: studentId },
  });
}

// 联系学员
function contactStudent(record) {
  message.info(`跳转企微联系: ${record.studentName}`);
}

// 获取AI话术
function getAiScript(record) {
  message.info(`正在为 ${record.studentName} 生成AI话术...`);
}

// 激活学员
function activateStudent(record) {
  message.info(`生成激活话术: ${record.studentName}`);
}

// ==================== 辅助方法 ====================

// 获取意向等级颜色
function getIntentionColor(level) {
  const colorMap = {
    S: 'red',
    A: 'orange',
    B: 'gold',
    C: 'default',
  };
  return colorMap[level] || 'default';
}

/**
 * 获取等级对应的颜色值 @author wangxiao
 */
function getLevelColor(level) {
  const colorMap = {
    S: '#ff4d4f',
    A: '#fa8c16',
    B: '#fadb14',
    C: '#d9d9d9',
  };
  return colorMap[level] || '#d9d9d9';
}

/**
 * 获取等级Tag颜色 @author wangxiao
 */
function getLevelTagColor(level) {
  const colorMap = {
    S: 'error',
    A: 'orange',
    B: 'warning',
    C: 'default',
  };
  return colorMap[level] || 'default';
}

// 格式化时间
function formatTime(time) {
  if (!time) return '-';
  return time;
}

// 获取未成交原因文本
function getNotDealReasonText(type) {
  const reasonMap = {
    1: '价格敏感',
    2: '延迟购买',
    3: '效果怀疑',
    4: '需要商量',
    5: '时间不合适',
    6: '内容不匹配',
  };
  return reasonMap[type] || '其他';
}

/**
 * 智能生成优先级原因 @author wangxiao
 * 根据学员行为数据，生成一句话说明"为什么重要"
 */
function getPriorityReason(student) {
  // S量学员的原因
  if (student.intentionLevel === 'S') {
    if (student.isDeal) {
      return '已成交学员，维护好关系';
    }
    if (student.hasPricing) {
      return '已报价待成交，今晚临门一脚';
    }
    if (student.hasAppliedPlan) {
      return '已申请规划，立即跟进报价';
    }
    if (student.liveWatchCount >= 3) {
      return `观看${student.liveWatchCount}次直播，购买意向强`;
    }
    return '高意向学员，优先跟进';
  }

  // A量学员的原因
  if (student.intentionLevel === 'A') {
    if (student.hasAppliedPlan) {
      return '已申请规划，持续培育中';
    }
    if (student.liveWatchCount >= 2) {
      return `观看${student.liveWatchCount}次直播，需深入沟通`;
    }
    if (student.totalMessageCount >= 10) {
      return '多次互动，有一定意向';
    }
    return '中等意向，待深入沟通';
  }

  // B量学员的原因
  if (student.intentionLevel === 'B') {
    if (student.liveWatchCount >= 1) {
      return '观看过直播，待激活';
    }
    return '观望中，需要激活引导';
  }

  return '待评估';
}

/**
 * 获取时间描述文本 @author wangxiao
 */
function getTimeText(student) {
  if (student.isDeal) {
    return `成交于 ${student.dealTime || '近期'}`;
  }

  const lastTime = student.lastContactTime;
  if (!lastTime) {
    return '暂无互动';
  }

  // 这里可以做更智能的时间计算
  // 简化处理，显示最后联系时间
  const now = new Date();
  const contactTime = new Date(lastTime);
  const diffHours = Math.floor((now - contactTime) / (1000 * 60 * 60));

  if (diffHours < 1) {
    return '刚刚互动';
  }
  if (diffHours < 24) {
    return `${diffHours}小时前互动`;
  }
  const diffDays = Math.floor(diffHours / 24);
  if (diffDays < 7) {
    return `${diffDays}天前互动`;
  }
  return lastTime;
}

/**
 * 获取学员背景概括 - 人物画像 @author wangxiao
 * 显示学员的基本特征:职业、年龄、性别等
 */
function getBackgroundSummary(student) {
  const parts = [];

  // 职业
  if (student.occupation) {
    parts.push(student.occupation);
  }

  // 年龄
  if (student.age) {
    parts.push(`${student.age}岁`);
  }

  // 性别
  const genderMap = { 1: '男', 2: '女' };
  if (student.gender && genderMap[student.gender]) {
    parts.push(genderMap[student.gender]);
  }

  // 如果没有信息,显示默认
  if (parts.length === 0) {
    return '新学员';
  }

  return parts.join(' · ');
}

/**
 * 获取学员背景描述 @author wangxiao
 * 描述学员的学习目的、需求、痛点等
 */
function getStudentBackground(student) {
  // 这里应该从学员数据中获取背景信息
  // 目前使用模拟数据,实际应该从 student.backgroundDescription 或类似字段获取

  // 根据职业和意向等级生成合理的背景描述
  const backgrounds = {
    '程序员_S': '想系统学习AI Agent开发,提升技术能力,同时希望能接一些外包项目增加收入',
    '程序员_A': '对AI技术感兴趣,想了解Agent开发的实战应用,考虑未来转型方向',
    '程序员_B': '好奇AI Agent技术,想初步了解是否适合自己学习',
    '产品经理_S': '公司正在规划AI产品,需要快速掌握Agent技术与产品团队协作',
    '产品经理_A': '想了解AI Agent产品设计方法,提升产品竞争力',
    '产品经理_B': '对AI产品感兴趣,想探索新的产品方向',
    '运营专员_S': '想通过AI工具提升工作效率,同时探索AI运营的新方向',
    '运营专员_A': '希望学习AI工具应用,优化现有运营流程',
    '运营专员_B': '了解AI在运营领域的应用场景',
    '设计师_S': '想学习AI辅助设计工具,提升设计效率并探索AI设计方向',
    '设计师_A': '对AI设计工具感兴趣,想了解如何应用到工作中',
    '设计师_B': '了解AI对设计行业的影响',
    '销售经理_S': '想用AI提升销售效率,同时学习AI销售工具的开发',
    '销售经理_A': '希望了解AI在销售领域的应用,提升业绩',
    '销售经理_B': '初步了解AI销售工具的可能性',
    'default_S': '学生,想通过学习Agent找一份工作,同时自己也想开发需求',
    'default_A': '对AI技术感兴趣,想系统学习并应用到实际工作中',
    'default_B': '想了解AI技术,探索学习方向',
  };

  const key = `${student.occupation || 'default'}_${student.intentionLevel}`;
  return backgrounds[key] || backgrounds[`default_${student.intentionLevel}`] || '对AI Agent技术感兴趣,想深入学习';
}

/**
 * 获取长期池学员背景概括 @author wangxiao
 */
function getLongTermBackground(student) {
  const parts = [];

  // 期数
  parts.push(`第${student.trainingCampPeriod}期学员`);

  // 往期等级
  parts.push(`往期${student.intentionLevel}级意向`);

  return parts.join(' · ');
}

/**
 * 获取长期池学员背景描述 @author wangxiao
 */
function getLongTermStudentBackground(student) {
  const reason = getNotDealReasonText(student.notDealReasonType);

  const backgrounds = {
    '价格敏感': '当时觉得价格偏高,想等优惠活动或攒够预算再报名',
    '延迟购买': '当时工作较忙,计划过段时间再系统学习',
    '效果怀疑': '对学习效果有顾虑,想先看看其他学员的成果',
    '需要商量': '需要和家人商量或申请公司培训预算',
    '时间不合适': '上课时间与工作时间冲突,无法参加直播',
    '内容不匹配': '课程内容与当时的需求不太匹配',
  };

  return backgrounds[reason] || `因${reason}未成交,可考虑重新激活`;
}

/**
 * 获取长期池学员的优先级原因 @author wangxiao
 */
function getLongTermReason(student) {
  const reason = getNotDealReasonText(student.notDealReasonType);

  // 根据历史意向等级和沉默天数生成原因
  if (student.intentionLevel === 'S' || student.intentionLevel === 'A') {
    if (student.silentDays < 30) {
      return `往期高意向，${reason}，值得重新激活`;
    }
    if (student.silentDays < 60) {
      return `往期高潜力，${reason}，尝试激活`;
    }
    return `往期${student.intentionLevel}量学员，${reason}`;
  }

  // B量学员
  if (student.silentDays < 30) {
    return `${reason}，可适当培育`;
  }
  return `观望学员，${reason}，低优先级`;
}

// ==================== 模拟数据生成 ====================

function generateMockData(data) {
  if (data.length > 0) return data;

  // 生成模拟数据用于演示
  const mockData = [];
  const names = ['张三', '李四', '王五', '赵六', '钱七', '孙八', '周九', '吴十'];
  const levels = ['S', 'S', 'A', 'A', 'A', 'B', 'B', 'B'];
  const occupations = ['产品经理', '程序员', '运营专员', '设计师', '销售经理'];

  for (let i = 0; i < 20; i++) {
    mockData.push({
      studentId: i + 1,
      studentName: names[i % names.length],
      gender: i % 2 === 0 ? 1 : 2,
      age: 25 + i,
      occupation: occupations[i % occupations.length],
      phone: `138****${String(i).padStart(4, '0')}`,
      intentionLevel: levels[i % levels.length],
      comprehensiveScore: 90 - i * 2,
      hasAppliedPlan: i % 3 !== 0,
      hasPricing: i % 4 === 0,
      isDeal: i % 10 === 0,
      lastContactTime: '2025-10-06 10:30',
      lastContactContent: '询问课程内容和价格',
      liveWatchCount: 3,
      totalMessageCount: 15,
    });
  }

  return mockData;
}

function generateMockLongTermData(data) {
  if (data.length > 0) return data;

  const mockData = [];
  const names = ['李四', '王五', '赵六', '钱七'];

  for (let i = 0; i < 10; i++) {
    mockData.push({
      studentId: 100 + i,
      studentName: names[i % names.length],
      phone: `138****${String(i).padStart(4, '0')}`,
      trainingCampPeriod: 9 - i,
      intentionLevel: i % 2 === 0 ? 'S' : 'A',
      comprehensiveScore: 85 - i,
      notDealReasonType: (i % 4) + 1,
      lastContactTime: `2025-09-${String(20 - i).padStart(2, '0')}`,
      silentDays: 30 + i * 5,
    });
  }

  return mockData;
}
</script>

<style lang="less" scoped>
// 使用 List.Item 响应式布局的卡片设计
.student-card-v3 {
      border-radius: 12px;
      border: 1px solid #f0f0f0;
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      overflow: hidden;
      box-shadow: 0 1px 2px rgba(0, 0, 0, 0.03), 0 1px 6px -1px rgba(0, 0, 0, 0.02);

      &:hover {
        transform: translateY(-4px);
        box-shadow: 0 12px 24px -6px rgba(0, 0, 0, 0.12), 0 0 0 1px rgba(0, 0, 0, 0.04);
        border-color: #1890ff;
      }

      // S量学员特殊样式
      &.high-priority {
        border-color: #ff4d4f;
        background: linear-gradient(135deg, #fff1f0 0%, #ffffff 100%);

        &:hover {
          border-color: #ff4d4f;
          box-shadow: 0 12px 24px -6px rgba(255, 77, 79, 0.2), 0 0 0 1px rgba(255, 77, 79, 0.1);
        }
      }

      // 长期池卡片
      &.longterm-card {
        border-color: #ffe7ba;
        background: linear-gradient(135deg, #fffbf0 0%, #ffffff 100%);

        &:hover {
          border-color: #faad14;
          box-shadow: 0 12px 24px -6px rgba(250, 173, 20, 0.15), 0 0 0 1px rgba(250, 173, 20, 0.08);
        }
      }

      // 卡片头部布局
      .card-header {
        display: flex;
        align-items: flex-start;
        justify-content: space-between;
        gap: 12px;

        // 分数徽章 - 右侧固定
        .score-badge {
          display: flex;
          flex-direction: column;
          align-items: flex-end;
          min-width: 60px;
          flex-shrink: 0;

          .score-number {
            font-size: 26px;
            font-weight: bold;
            line-height: 1;
            margin-bottom: 4px;
          }

          .score-level {
            font-size: 12px;
            font-weight: 500;
            white-space: nowrap;
          }
        }

        // List.Item.Meta 布局
        .ant-list-item-meta {
          flex: 1;
          min-width: 0;

          .ant-list-item-meta-avatar {
            margin-right: 12px;
          }

          .ant-list-item-meta-content {
            .ant-list-item-meta-title {
              margin-bottom: 6px;
              font-size: 16px;
              font-weight: 600;
              color: #1a1a1a;
              line-height: 1.4;
            }

            .ant-list-item-meta-description {
              font-size: 13px;
              color: #8c8c8c;
              line-height: 1.5;
            }
          }
        }
      }

      // 学员背景描述区域
      .customer-background {
        font-size: 13px;
        line-height: 1.6;
        color: #666;
        margin-bottom: 12px;
        padding-bottom: 12px;
        border-bottom: 1px dashed #e8e8e8;
      }

      // 为什么重要区域 - 简洁样式
      .priority-reason {
        font-size: 14px;
        line-height: 1.6;
        color: #333;
        margin-bottom: 12px;
        font-weight: 500;
      }

      // 标签区域
      .action-tags {
        display: flex;
        flex-wrap: wrap;
        gap: 8px;
        min-height: 24px;

        .ant-tag {
          margin: 0;
          font-size: 12px;
          padding: 2px 8px;
          border-radius: 4px;
      }
    }
  }
</style>
