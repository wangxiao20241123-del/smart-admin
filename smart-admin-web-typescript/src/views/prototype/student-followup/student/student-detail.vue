<!--
  * AI销售助手 - 客户360°详情页
  *
  * @Author:    wangxiao
  * @Date:      2025-10-06
  * @Copyright  子午线高科智能科技 2025
-->
<template>
  <div class="customer-detail">
    <a-page-header title="客户360°视图" @back="goBack">
      <template #extra>
        <a-space>
          <a-button type="primary" @click="contactCustomer">
            <MessageOutlined /> 联系客户
          </a-button>
          <a-button @click="getAiScript">
            <RobotOutlined /> AI话术
          </a-button>
          <a-button @click="setReminder">
            <BellOutlined /> 设置提醒
          </a-button>
        </a-space>
      </template>
    </a-page-header>

    <a-spin :spinning="loading">
      <a-row :gutter="16">
        <!-- 左侧：基本信息 + AI分析 -->
        <a-col :span="8">
          <!-- 基本信息卡片 -->
          <a-card title="基本信息" class="info-card" :bordered="false">
            <div class="customer-header">
              <a-avatar :size="80" :src="detail.avatar">
                {{ detail.customerName?.substring(0, 1) }}
              </a-avatar>
              <div class="header-info">
                <h2>{{ detail.customerName }}</h2>
                <a-space>
                  <a-tag v-if="detail.gender === 1" color="blue">男</a-tag>
                  <a-tag v-if="detail.gender === 2" color="pink">女</a-tag>
                  <span class="age">{{ detail.age }}岁</span>
                </a-space>
              </div>
            </div>

            <a-descriptions :column="1" size="small" class="detail-desc">
              <a-descriptions-item label="职业">{{ detail.occupation || '-' }}</a-descriptions-item>
              <a-descriptions-item label="手机">{{ detail.phone }}</a-descriptions-item>
              <a-descriptions-item label="微信号">{{ detail.wechatId || '-' }}</a-descriptions-item>
              <a-descriptions-item label="来源">{{ detail.sourceChannel }}</a-descriptions-item>
              <a-descriptions-item label="添加时间">{{ detail.addTime }}</a-descriptions-item>
              <a-descriptions-item label="训练营期数">第{{ detail.trainingCampPeriod }}期</a-descriptions-item>
            </a-descriptions>

            <div class="tags-section">
              <div class="section-title">客户标签</div>
              <a-space wrap>
                <a-tag color="processing">{{ detail.intentionLevel }}量</a-tag>
                <a-tag v-for="tag in detail.tags" :key="tag">{{ tag }}</a-tag>
                <a-button type="link" size="small">+ 添加标签</a-button>
              </a-space>
            </div>
          </a-card>

          <!-- AI意向分析卡片 -->
          <a-card title="AI意向分析" class="ai-analysis-card" :bordered="false">
            <div class="comprehensive-score">
              <div class="score-value" :class="getScoreClass(detail.comprehensiveScore)">
                {{ detail.comprehensiveScore }}
              </div>
              <div class="score-label">综合评分</div>
              <a-tag :color="getIntentionColor(detail.intentionLevel)" class="level-tag">
                {{ detail.intentionLevel }}量 ({{getIntentionRange(detail.intentionLevel)}})
              </a-tag>
            </div>

            <div class="score-dimensions">
              <div class="dimension-item">
                <div class="dimension-label">💰 价格维度</div>
                <a-progress
                  :percent="detail.priceScore"
                  :stroke-color="getScoreColor(detail.priceScore)"
                  :show-info="true"
                />
              </div>
              <div class="dimension-item">
                <div class="dimension-label">🎯 需求维度</div>
                <a-progress
                  :percent="detail.demandScore"
                  :stroke-color="getScoreColor(detail.demandScore)"
                  :show-info="true"
                />
              </div>
              <div class="dimension-item">
                <div class="dimension-label">✅ 共识维度</div>
                <a-progress
                  :percent="detail.consensusScore"
                  :stroke-color="getScoreColor(detail.consensusScore)"
                  :show-info="true"
                />
              </div>
              <div class="dimension-item">
                <div class="dimension-label">🤝 信任维度</div>
                <a-progress
                  :percent="detail.trustScore"
                  :stroke-color="getScoreColor(detail.trustScore)"
                  :show-info="true"
                />
              </div>
            </div>

            <div class="ai-suggestion">
              <div class="section-title">💡 AI分析建议</div>
              <div class="suggestion-content">{{ detail.aiAnalysis || '暂无分析建议' }}</div>
            </div>

            <div class="score-adjust">
              <a-button size="small" @click="showAdjustModal">手动调整评分(±5分)</a-button>
            </div>
          </a-card>
        </a-col>

        <!-- 右侧：训练营进度 + 行为轨迹 + 聊天记录 -->
        <a-col :span="16">
          <!-- 训练营进度 -->
          <a-card title="训练营进度追踪" class="progress-card" :bordered="false">
            <a-timeline>
              <a-timeline-item color="green">
                <template #dot>
                  <CheckCircleOutlined />
                </template>
                <div class="timeline-item">
                  <div class="item-title">✅ DAY0 (10/01) 入营</div>
                  <div class="item-content">填写问卷，购买力强+是决策人</div>
                </div>
              </a-timeline-item>

              <a-timeline-item color="green" v-if="detail.hasAppliedPlan">
                <template #dot>
                  <CheckCircleOutlined />
                </template>
                <div class="timeline-item">
                  <div class="item-title">✅ DAY1 (10/02) 申请规划卡片</div>
                  <div class="item-content">主动申请，沟通顺畅</div>
                </div>
              </a-timeline-item>

              <a-timeline-item color="blue" v-if="detail.hasPricing">
                <template #dot>
                  <DollarOutlined />
                </template>
                <div class="timeline-item">
                  <div class="item-title">💰 DAY1 (10/02) 规划报价沟通</div>
                  <div class="item-content">对4998元表示"有点贵"</div>
                </div>
              </a-timeline-item>

              <a-timeline-item color="orange" v-if="!detail.isDeal">
                <template #dot>
                  <ClockCircleOutlined />
                </template>
                <div class="timeline-item">
                  <div class="item-title">⏳ DAY3 (10/04) 待跟进</div>
                  <div class="item-content">今晚二次逼单关键节点</div>
                </div>
              </a-timeline-item>

              <a-timeline-item color="gold" v-if="detail.isDeal">
                <template #dot>
                  <TrophyOutlined />
                </template>
                <div class="timeline-item">
                  <div class="item-title">🎉 已成交</div>
                  <div class="item-content">成交金额: ¥{{ detail.dealAmount }}</div>
                  <div class="item-sub">成交时间: {{ detail.dealTime }}</div>
                </div>
              </a-timeline-item>
            </a-timeline>
          </a-card>

          <!-- 行为轨迹 -->
          <a-card title="行为轨迹" class="behavior-card" :bordered="false">
            <a-table
              :dataSource="behaviorData"
              :columns="behaviorColumns"
              :pagination="false"
              size="small"
              :scroll="{ y: 300 }"
            >
              <template #bodyCell="{ column, record }">
                <template v-if="column.dataIndex === 'behaviorType'">
                  <a-tag :color="getBehaviorColor(record.behaviorType)">
                    {{ $smartEnumPlugin.getDescByValue('BEHAVIOR_TYPE_ENUM', record.behaviorType) }}
                  </a-tag>
                </template>
              </template>
            </a-table>
          </a-card>

          <!-- 聊天记录检索 -->
          <a-card title="聊天记录检索" class="chat-card" :bordered="false">
            <div class="chat-search">
              <a-input-search
                v-model:value="chatKeyword"
                placeholder="搜索关键词..."
                enter-button="搜索"
                @search="searchChat"
                style="margin-bottom: 16px"
              />
            </div>

            <a-tabs>
              <a-tab-pane key="all" tab="全部">
                <div class="chat-list">
                  <div
                    v-for="chat in chatRecords"
                    :key="chat.recordId"
                    class="chat-item"
                    :class="{ 'chat-customer': chat.sender === 1, 'chat-sales': chat.sender === 2 }"
                  >
                    <div class="chat-time">{{ chat.chatTime }}</div>
                    <div class="chat-content">{{ chat.messageContent }}</div>
                    <a-tag v-if="chat.category" size="small" class="chat-category">
                      {{ $smartEnumPlugin.getDescByValue('CHAT_CATEGORY_ENUM', chat.category) }}
                    </a-tag>
                  </div>
                </div>
              </a-tab-pane>
              <a-tab-pane key="price" tab="价格相关 (3)"></a-tab-pane>
              <a-tab-pane key="content" tab="课程内容 (5)"></a-tab-pane>
              <a-tab-pane key="monetization" tab="副业变现 (8)"></a-tab-pane>
            </a-tabs>
          </a-card>
        </a-col>
      </a-row>
    </a-spin>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { message } from 'ant-design-vue';
import { studentApi } from '/@/api/prototype/student-followup/student-api';
import {
  MessageOutlined,
  RobotOutlined,
  BellOutlined,
  CheckCircleOutlined,
  DollarOutlined,
  ClockCircleOutlined,
  TrophyOutlined,
} from '@ant-design/icons-vue';

const router = useRouter();
const route = useRoute();

// ==================== 数据定义 ====================
const loading = ref(false);
const customerId = ref(route.query.id);
const chatKeyword = ref('');

const detail = ref({
  customerId: 0,
  customerName: '',
  gender: 0,
  age: 0,
  occupation: '',
  phone: '',
  wechatId: '',
  sourceChannel: '',
  addTime: '',
  trainingCampPeriod: 0,
  comprehensiveScore: 0,
  intentionLevel: 'S',
  priceScore: 0,
  demandScore: 0,
  consensusScore: 0,
  trustScore: 0,
  aiAnalysis: '',
  hasAppliedPlan: false,
  hasPricing: false,
  isDeal: false,
  dealAmount: 0,
  dealTime: '',
  tags: [],
});

const behaviorData = ref([]);
const chatRecords = ref([]);

const behaviorColumns = [
  {
    title: '时间',
    dataIndex: 'behaviorTime',
    width: 150,
  },
  {
    title: '行为类型',
    dataIndex: 'behaviorType',
    width: 150,
  },
  {
    title: '行为描述',
    dataIndex: 'behaviorDesc',
  },
];

// ==================== 生命周期 ====================
onMounted(() => {
  getDetail();
});

// ==================== 业务方法 ====================

async function getDetail() {
  try {
    loading.value = true;
    const res = await studentApi.detail(customerId.value);
    if (res.data) {
      detail.value = res.data;
      behaviorData.value = res.data.behaviorTimeline || [];
      chatRecords.value = res.data.recentChats || [];
    }
  } catch (error) {
    message.error('获取详情失败');
    // 模拟数据
    loadMockData();
  } finally {
    loading.value = false;
  }
}

function loadMockData() {
  detail.value = {
    customerId: 1,
    customerName: '张三',
    gender: 1,
    age: 32,
    occupation: '产品经理',
    phone: '138****0001',
    wechatId: 'zhangsan123',
    sourceChannel: '直播引流',
    addTime: '2025-10-01 14:30',
    trainingCampPeriod: 10,
    comprehensiveScore: 92,
    intentionLevel: 'S',
    priceScore: 75,
    demandScore: 95,
    consensusScore: 90,
    trustScore: 88,
    aiAnalysis:
      '张三是典型的S量学员，已在规划过程中完成报价沟通，讨价还价特征明显。建议策略：1. 强调限时优惠和名额紧张 2. 提供分期付款方案降低决策门槛 3. 分享相似学员成功案例增强信心',
    hasAppliedPlan: true,
    hasPricing: true,
    isDeal: false,
    tags: ['讨价还价', '执行力强', '有决策权'],
  };

  behaviorData.value = [
    {
      timelineId: 1,
      behaviorTime: '2025-10-03 22:15',
      behaviorType: 'CHAT',
      behaviorDesc: '发消息: "能不能4500拿下？"',
    },
    {
      timelineId: 2,
      behaviorTime: '2025-10-03 21:30',
      behaviorType: 'WATCH_LIVE',
      behaviorDesc: '观看DAY2直播 (全程参与，互动3次)',
    },
    {
      timelineId: 3,
      behaviorTime: '2025-10-02 22:30',
      behaviorType: 'PRICING',
      behaviorDesc: '1V1规划沟通 (时长28分钟)',
    },
    {
      timelineId: 4,
      behaviorTime: '2025-10-01 16:20',
      behaviorType: 'ENTER_CAMP',
      behaviorDesc: '填写问卷: "想通过AI副业月入5000+"',
    },
  ];

  chatRecords.value = [
    {
      recordId: 1,
      chatTime: '2025-10-03 22:15',
      sender: 1,
      messageType: 1,
      messageContent: '能不能4500拿下？',
      category: 'PRICE',
    },
    {
      recordId: 2,
      chatTime: '2025-10-03 22:18',
      sender: 2,
      messageType: 1,
      messageContent: '这个价格已经是限时特惠了，原价7499...',
      category: 'PRICE',
    },
    {
      recordId: 3,
      chatTime: '2025-10-03 22:20',
      sender: 1,
      messageType: 1,
      messageContent: '我再考虑考虑，明天给你答复',
      category: 'OTHER',
    },
  ];
}

function goBack() {
  router.back();
}

function contactCustomer() {
  message.info('跳转企微联系客户');
}

function getAiScript() {
  message.info('正在生成AI话术...');
}

function setReminder() {
  message.info('设置跟进提醒');
}

function showAdjustModal() {
  message.info('手动调整评分');
}

function searchChat() {
  message.info(`搜索关键词: ${chatKeyword.value}`);
}

// ==================== 辅助方法 ====================

function getScoreClass(score) {
  if (score >= 90) return 'score-s';
  if (score >= 80) return 'score-a';
  if (score >= 70) return 'score-b';
  return 'score-c';
}

function getIntentionColor(level) {
  const colorMap = {
    S: 'red',
    A: 'orange',
    B: 'gold',
    C: 'default',
  };
  return colorMap[level] || 'default';
}

function getIntentionRange(level) {
  const rangeMap = {
    S: '90-100分',
    A: '80-89分',
    B: '70-79分',
    C: '<70分',
  };
  return rangeMap[level] || '';
}

function getScoreColor(score) {
  if (score >= 90) return '#ff4d4f';
  if (score >= 80) return '#fa8c16';
  if (score >= 70) return '#fadb14';
  return '#d9d9d9';
}

function getBehaviorColor(type) {
  const colorMap = {
    ENTER_CAMP: 'green',
    APPLY_PLAN: 'blue',
    PRICING: 'orange',
    BARGAIN: 'red',
    DEAL: 'gold',
    WATCH_LIVE: 'purple',
    CHAT: 'default',
  };
  return colorMap[type] || 'default';
}
</script>

<style lang="less" scoped>
.customer-detail {
  padding: 16px;
  background: #f0f2f5;

  .info-card,
  .ai-analysis-card,
  .progress-card,
  .behavior-card,
  .chat-card {
    margin-bottom: 16px;
  }

  .customer-header {
    display: flex;
    align-items: center;
    gap: 16px;
    margin-bottom: 24px;
    padding-bottom: 16px;
    border-bottom: 1px solid #f0f0f0;

    .header-info {
      h2 {
        margin: 0 0 8px 0;
        font-size: 20px;
      }

      .age {
        color: #999;
        font-size: 14px;
      }
    }
  }

  .detail-desc {
    margin-bottom: 16px;
  }

  .tags-section {
    margin-top: 16px;
    padding-top: 16px;
    border-top: 1px solid #f0f0f0;

    .section-title {
      font-size: 14px;
      font-weight: 500;
      margin-bottom: 12px;
      color: #333;
    }
  }

  .comprehensive-score {
    text-align: center;
    padding: 24px 0;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 8px;
    color: white;
    margin-bottom: 24px;

    .score-value {
      font-size: 48px;
      font-weight: bold;
      line-height: 1;
    }

    .score-label {
      font-size: 14px;
      margin-top: 8px;
      opacity: 0.9;
    }

    .level-tag {
      margin-top: 12px;
      font-size: 14px;
    }
  }

  .score-dimensions {
    margin-bottom: 24px;

    .dimension-item {
      margin-bottom: 16px;

      .dimension-label {
        font-size: 13px;
        margin-bottom: 8px;
        color: #666;
      }
    }
  }

  .ai-suggestion {
    margin-bottom: 16px;
    padding: 16px;
    background: #f5f7fa;
    border-radius: 4px;

    .section-title {
      font-size: 14px;
      font-weight: 500;
      margin-bottom: 12px;
      color: #333;
    }

    .suggestion-content {
      font-size: 13px;
      line-height: 1.6;
      color: #666;
    }
  }

  .score-adjust {
    text-align: center;
  }

  .timeline-item {
    .item-title {
      font-weight: 500;
      margin-bottom: 4px;
    }

    .item-content {
      font-size: 13px;
      color: #666;
    }

    .item-sub {
      font-size: 12px;
      color: #999;
      margin-top: 4px;
    }
  }

  .chat-list {
    max-height: 400px;
    overflow-y: auto;

    .chat-item {
      margin-bottom: 16px;
      padding: 12px;
      border-radius: 4px;

      &.chat-customer {
        background: #e6f7ff;
        border-left: 3px solid #1890ff;
      }

      &.chat-sales {
        background: #f6ffed;
        border-left: 3px solid #52c41a;
      }

      .chat-time {
        font-size: 12px;
        color: #999;
        margin-bottom: 6px;
      }

      .chat-content {
        font-size: 14px;
        color: #333;
        margin-bottom: 6px;
      }

      .chat-category {
        font-size: 11px;
      }
    }
  }
}

.score-s {
  color: #ff4d4f;
}

.score-a {
  color: #fa8c16;
}

.score-b {
  color: #fadb14;
}

.score-c {
  color: #d9d9d9;
}
</style>
