/*
 * AI销售助手 - 学员数据模型
 *
 * @Author:    wangxiao
 * @Date:      2025-10-06
 * @Copyright  子午线高科智能科技 2025
 */
import { PageParamModel } from '/@/api/base-model/page-param-model';

// ========== 查询表单 ==========
export interface StudentQueryForm extends PageParamModel {
  studentName?: string;         // 学员姓名
  phone?: string;                // 手机号
  batchType?: number;            // 批次类型 1=本期营 2=长期池
  intentionLevel?: string;       // 意向等级 S/A/B
  trainingCampPeriod?: number;   // 训练营期数
  minScore?: number;             // 最小综合评分
  maxScore?: number;             // 最大综合评分
  hasAppliedPlan?: boolean;      // 是否已申请规划卡片
  hasPricing?: boolean;          // 是否已规划报价
  isDeal?: boolean;              // 是否已成交
  lastContactDays?: number;      // 最近联系天数
}

// ========== 学员展示VO ==========
export interface StudentVO {
  studentId: number;            // 学员ID
  studentName: string;          // 学员姓名
  gender: number;                // 性别 0=未知 1=男 2=女
  age?: number;                  // 年龄
  occupation?: string;           // 职业
  phone: string;                 // 手机号
  wechatId?: string;             // 微信号
  avatar?: string;               // 头像URL

  // 来源信息
  sourceChannel: string;         // 来源渠道
  addTime: string;               // 添加时间

  // 训练营信息
  trainingCampPeriod: number;    // 训练营期数
  currentDay: number;            // 当前训练营第几天 0-5
  batchType: number;             // 批次类型 1=本期营 2=长期池

  // AI意向分析
  comprehensiveScore: number;    // 综合评分 0-100
  intentionLevel: string;        // 意向等级 S/A/B
  priceScore: number;            // 价格维度评分
  demandScore: number;           // 需求维度评分
  consensusScore: number;        // 共识维度评分
  trustScore: number;            // 信任维度评分
  aiAnalysis?: string;           // AI分析建议

  // 关键节点
  hasAppliedPlan: boolean;       // 是否已申请规划卡片
  appliedPlanTime?: string;      // 申请规划卡片时间
  hasPricing: boolean;           // 是否已规划报价
  pricingTime?: string;          // 规划报价时间
  isDeal: boolean;               // 是否已成交
  dealTime?: string;             // 成交时间
  dealAmount?: number;           // 成交金额

  // 行为数据
  lastContactTime?: string;      // 最后联系时间
  lastContactContent?: string;   // 最后联系内容
  totalMessageCount: number;     // 总消息数
  liveWatchCount: number;        // 直播观看次数
  courseAttendRate: number;      // 试听课出勤率

  // 未成交原因(长期池)
  notDealReason?: string;        // 未成交原因
  notDealReasonType?: number;    // 未成交原因类型

  // 标签
  tags: string[];                // 学员标签

  // 系统字段
  createTime: string;            // 创建时间
  updateTime?: string;           // 更新时间
}

// ========== 学员360°详情VO ==========
export interface StudentDetailVO extends StudentVO {
  // 聊天记录摘要
  recentChats: ChatRecordVO[];

  // 行为轨迹
  behaviorTimeline: BehaviorTimelineVO[];

  // 直播观看记录
  liveRecords: LiveRecordVO[];

  // 试听课记录
  courseRecords: CourseRecordVO[];
}

// ========== 聊天记录VO ==========
export interface ChatRecordVO {
  recordId: number;              // 记录ID
  chatTime: string;              // 聊天时间
  messageType: number;           // 消息类型 1=文本 2=图片 3=文件
  messageContent: string;        // 消息内容
  sender: number;                // 发送者 1=学员 2=销售
  category?: string;             // 自动分类
}

// ========== 行为轨迹VO ==========
export interface BehaviorTimelineVO {
  timelineId: number;            // 时间线ID
  behaviorTime: string;          // 行为时间
  behaviorType: string;          // 行为类型
  behaviorDesc: string;          // 行为描述
  behaviorData?: any;            // 行为数据
}

// ========== 直播观看记录VO ==========
export interface LiveRecordVO {
  recordId: number;              // 记录ID
  liveName: string;              // 直播名称
  liveTime: string;              // 直播时间
  watchDuration: number;         // 观看时长(分钟)
  isFullAttend: boolean;         // 是否全程参与
  interactionCount: number;      // 互动次数
}

// ========== 试听课记录VO ==========
export interface CourseRecordVO {
  recordId: number;              // 记录ID
  courseName: string;            // 课程名称
  courseTime: string;            // 课程时间
  isAttend: boolean;             // 是否出勤
  completionRate: number;        // 完课率
}

// ========== 新增学员表单 ==========
export interface StudentAddForm {
  studentName: string;          // 学员姓名(必填)
  phone: string;                 // 手机号(必填)
  gender?: number;               // 性别
  age?: number;                  // 年龄
  occupation?: string;           // 职业
  wechatId?: string;             // 微信号
  sourceChannel: string;         // 来源渠道(必填)
  trainingCampPeriod: number;    // 训练营期数(必填)
}

// ========== 更新学员表单 ==========
export interface StudentUpdateForm extends StudentAddForm {
  studentId: number;            // 学员ID(必填)
}

// ========== AI评分调整表单 ==========
export interface ScoreAdjustForm {
  studentId: number;            // 学员ID
  scoreAdjust: number;           // 评分调整 -5到+5
  adjustReason: string;          // 调整原因
}

// ========== 批量操作表单 ==========
export interface BatchOperateForm {
  studentIds: number[];         // 学员ID列表
  operateType: number;           // 操作类型 1=批量标签 2=批量转池 3=批量提醒
  operateData?: any;             // 操作数据
}

// ========== 话术推荐请求 ==========
export interface ScriptRecommendRequest {
  studentId: number;            // 学员ID
  sceneType?: string;            // 场景类型
}

// ========== 话术推荐响应 ==========
export interface ScriptRecommendVO {
  scriptId: number;              // 话术ID
  scriptContent: string;         // 话术内容
  sceneType: string;             // 场景类型
  suitableFor: string;           // 适用对象
}

// ========== 训练营统计VO ==========
export interface TrainingCampStatVO {
  period: number;                // 期数
  currentDay: number;            // 当前天数
  totalStudents: number;         // 总学员数
  dealCount: number;             // 已成交数
  dealRate: number;              // 成交率
  sLevelCount: number;           // S量数量
  aLevelCount: number;           // A量数量
  bLevelCount: number;           // B量数量
  averageScore: number;          // 平均评分
}

// ========== 长期池统计VO ==========
export interface LongTermPoolStatVO {
  totalCount: number;            // 总数
  highValueCount: number;        // 高价值数(往期S/A量)
  silentCount: number;           // 沉默学员数(30天未联系)
  reactivatableCount: number;    // 可激活数
}
