/*
 * AI销售助手 - 学员管理常量
 *
 * @Author:    wangxiao
 * @Date:      2025-10-06
 * @Copyright  子午线高科智能科技 2025
 */
import { SmartEnum } from '/@/types/smart-enum';

// 批次类型枚举
export const BATCH_TYPE_ENUM: SmartEnum<number> = {
  CURRENT_CAMP: {
    value: 1,
    desc: '本期营',
  },
  LONG_TERM_POOL: {
    value: 2,
    desc: '长期池',
  },
};

// 意向等级枚举
export const INTENTION_LEVEL_ENUM: SmartEnum<string> = {
  S_LEVEL: {
    value: 'S',
    desc: 'S量(90-100分)',
  },
  A_LEVEL: {
    value: 'A',
    desc: 'A量(80-89分)',
  },
  B_LEVEL: {
    value: 'B',
    desc: 'B量(70-79分)',
  },
  C_LEVEL: {
    value: 'C',
    desc: 'C量(<70分)',
  },
};

// 来源渠道枚举
export const SOURCE_CHANNEL_ENUM: SmartEnum<string> = {
  LIVE_STREAM: {
    value: 'LIVE',
    desc: '直播引流',
  },
  TRIAL_COURSE: {
    value: 'TRIAL',
    desc: '试听课',
  },
  ACTIVE_ADD: {
    value: 'ACTIVE',
    desc: '主动添加',
  },
  REFERRAL: {
    value: 'REFERRAL',
    desc: '转介绍',
  },
  OTHER: {
    value: 'OTHER',
    desc: '其他',
  },
};

// 训练营天数枚举
export const TRAINING_DAY_ENUM: SmartEnum<number> = {
  DAY_0: {
    value: 0,
    desc: 'DAY0-入营',
  },
  DAY_1: {
    value: 1,
    desc: 'DAY1-规划卡片',
  },
  DAY_2: {
    value: 2,
    desc: 'DAY2-首次秒杀',
  },
  DAY_3: {
    value: 3,
    desc: 'DAY3-二次逼单',
  },
  DAY_4: {
    value: 4,
    desc: 'DAY4-最后冲刺',
  },
  DAY_5: {
    value: 5,
    desc: 'DAY5-营期结束',
  },
};

// 未成交原因类型枚举
export const NOT_DEAL_REASON_ENUM: SmartEnum<number> = {
  PRICE_SENSITIVE: {
    value: 1,
    desc: '价格敏感',
  },
  DELAY_PURCHASE: {
    value: 2,
    desc: '延迟购买',
  },
  DOUBT_EFFECT: {
    value: 3,
    desc: '效果怀疑',
  },
  NEED_DISCUSS: {
    value: 4,
    desc: '需要商量',
  },
  NO_TIME: {
    value: 5,
    desc: '时间不合适',
  },
  CONTENT_MISMATCH: {
    value: 6,
    desc: '内容不匹配',
  },
  OTHER: {
    value: 99,
    desc: '其他原因',
  },
};

// 消息类型枚举
export const MESSAGE_TYPE_ENUM: SmartEnum<number> = {
  TEXT: {
    value: 1,
    desc: '文本',
  },
  IMAGE: {
    value: 2,
    desc: '图片',
  },
  FILE: {
    value: 3,
    desc: '文件',
  },
  VOICE: {
    value: 4,
    desc: '语音',
  },
  VIDEO: {
    value: 5,
    desc: '视频',
  },
};

// 聊天记录分类枚举
export const CHAT_CATEGORY_ENUM: SmartEnum<string> = {
  PRICE: {
    value: 'PRICE',
    desc: '价格相关',
  },
  CONTENT: {
    value: 'CONTENT',
    desc: '课程内容',
  },
  MONETIZATION: {
    value: 'MONETIZATION',
    desc: '副业变现',
  },
  TIME: {
    value: 'TIME',
    desc: '时间安排',
  },
  OTHER: {
    value: 'OTHER',
    desc: '其他',
  },
};

// 行为类型枚举
export const BEHAVIOR_TYPE_ENUM: SmartEnum<string> = {
  ENTER_CAMP: {
    value: 'ENTER_CAMP',
    desc: '入营',
  },
  APPLY_PLAN: {
    value: 'APPLY_PLAN',
    desc: '申请规划卡片',
  },
  PRICING: {
    value: 'PRICING',
    desc: '规划报价',
  },
  BARGAIN: {
    value: 'BARGAIN',
    desc: '讨价还价',
  },
  DEAL: {
    value: 'DEAL',
    desc: '成交',
  },
  WATCH_LIVE: {
    value: 'WATCH_LIVE',
    desc: '观看直播',
  },
  ATTEND_COURSE: {
    value: 'ATTEND_COURSE',
    desc: '参加试听课',
  },
  CHAT: {
    value: 'CHAT',
    desc: '沟通交流',
  },
};

// 批量操作类型枚举
export const BATCH_OPERATE_TYPE_ENUM: SmartEnum<number> = {
  ADD_TAG: {
    value: 1,
    desc: '批量添加标签',
  },
  TRANSFER_POOL: {
    value: 2,
    desc: '批量转池',
  },
  SET_REMINDER: {
    value: 3,
    desc: '批量设置提醒',
  },
  SEND_MESSAGE: {
    value: 4,
    desc: '批量群发',
  },
};

// 话术场景类型枚举
export const SCRIPT_SCENE_ENUM: SmartEnum<string> = {
  S_LEVEL_PRICE: {
    value: 'S_PRICE',
    desc: 'S量-价格敏感',
  },
  S_LEVEL_BARGAIN: {
    value: 'S_BARGAIN',
    desc: 'S量-讨价还价',
  },
  A_LEVEL_CONSIDER: {
    value: 'A_CONSIDER',
    desc: 'A量-要考虑',
  },
  LONGTERM_DELAY: {
    value: 'LONGTERM_DELAY',
    desc: '往期S量-延迟购买',
  },
  LONGTERM_FAMILY: {
    value: 'LONGTERM_FAMILY',
    desc: '往期A量-家人反对',
  },
  SILENT_ACTIVATE: {
    value: 'SILENT_ACTIVATE',
    desc: '沉默学员-激活',
  },
};

export default {
  BATCH_TYPE_ENUM,
  INTENTION_LEVEL_ENUM,
  SOURCE_CHANNEL_ENUM,
  TRAINING_DAY_ENUM,
  NOT_DEAL_REASON_ENUM,
  MESSAGE_TYPE_ENUM,
  CHAT_CATEGORY_ENUM,
  BEHAVIOR_TYPE_ENUM,
  BATCH_OPERATE_TYPE_ENUM,
  SCRIPT_SCENE_ENUM,
};
