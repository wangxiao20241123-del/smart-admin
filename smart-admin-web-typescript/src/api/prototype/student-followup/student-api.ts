/*
 * 学员跟进 - 学员管理API (原型版本 - 模拟数据)
 *
 * @Author:    wangxiao
 * @Date:      2025-10-06
 * @Copyright  子午线高科智能科技 2025
 */

import type {
  StudentQueryForm,
  StudentDetailVO,
  StudentAddForm,
  StudentUpdateForm,
  ScoreAdjustForm,
  BatchOperateForm,
  ScriptRecommendRequest,
} from './student-model';
import type { ResponseModel } from '/@/api/base-model/response-model';
import type { PageResultModel } from '/@/api/base-model/page-result-model';

// ==================== 模拟数据生成函数 ====================

function generateMockTrainingData() {
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

function generateMockLongTermData() {
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

// 模拟学员详情数据
const mockStudentDetails: any = {
  1: {
    studentId: 1,
    studentName: '张三',
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
    behaviorTimeline: [
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
    ],
    recentChats: [
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
    ],
  },
};

// ==================== API方法 ====================

export const studentApi = {
  /**
   * 分页查询学员列表 @author wangxiao
   */
  queryPage: (param: StudentQueryForm) => {
    return new Promise<ResponseModel<PageResultModel<any>>>((resolve) => {
      setTimeout(() => {
        const { poolType = 'training' } = param as any;
        const students =
          poolType === 'training'
            ? generateMockTrainingData()
            : generateMockLongTermData();

        resolve({
          code: 0,
          ok: true,
          msg: '操作成功',
          data: {
            list: students,
            total: students.length,
            pageNum: 1,
            pageSize: 10,
          },
        });
      }, 300);
    });
  },

  /**
   * 获取学员详情 @author wangxiao
   */
  detail: (studentId: number) => {
    return new Promise<ResponseModel<StudentDetailVO>>((resolve) => {
      setTimeout(() => {
        const detail = mockStudentDetails[studentId] || mockStudentDetails[1];
        resolve({
          code: 0,
          ok: true,
          msg: '操作成功',
          data: detail,
        });
      }, 200);
    });
  },

  /**
   * 新增学员 @author wangxiao
   */
  add: (param: StudentAddForm) => {
    return new Promise<ResponseModel<string>>((resolve) => {
      setTimeout(() => {
        resolve({
          code: 0,
          ok: true,
          msg: '新增成功',
          data: null,
        });
      }, 300);
    });
  },

  /**
   * 更新学员 @author wangxiao
   */
  update: (param: StudentUpdateForm) => {
    return new Promise<ResponseModel<string>>((resolve) => {
      setTimeout(() => {
        resolve({
          code: 0,
          ok: true,
          msg: '更新成功',
          data: null,
        });
      }, 300);
    });
  },

  /**
   * 删除学员 @author wangxiao
   */
  delete: (studentId: number) => {
    return new Promise<ResponseModel<string>>((resolve) => {
      setTimeout(() => {
        resolve({
          code: 0,
          ok: true,
          msg: '删除成功',
          data: null,
        });
      }, 200);
    });
  },

  /**
   * 手动调整AI评分 @author wangxiao
   */
  adjustScore: (param: ScoreAdjustForm) => {
    return new Promise<ResponseModel<string>>((resolve) => {
      setTimeout(() => {
        resolve({
          code: 0,
          ok: true,
          msg: '评分调整成功',
          data: null,
        });
      }, 300);
    });
  },

  /**
   * 批量操作 @author wangxiao
   */
  batchOperate: (param: BatchOperateForm) => {
    return new Promise<ResponseModel<string>>((resolve) => {
      setTimeout(() => {
        resolve({
          code: 0,
          ok: true,
          msg: '批量操作成功',
          data: null,
        });
      }, 400);
    });
  },

  /**
   * 获取AI话术推荐 @author wangxiao
   */
  getScriptRecommend: (param: ScriptRecommendRequest) => {
    return new Promise<ResponseModel<any>>((resolve) => {
      setTimeout(() => {
        resolve({
          code: 0,
          ok: true,
          msg: '操作成功',
          data: {
            scripts: [
              {
                scenario: '初次接触',
                content: '您好，我是XX课程的顾问小王...',
              },
              {
                scenario: '跟进咨询',
                content: '张总您好，上次您提到的问题...',
              },
              {
                scenario: '促单话术',
                content: '目前课程优惠活动即将结束...',
              },
            ],
          },
        });
      }, 300);
    });
  },

  /**
   * 获取训练营统计数据 @author wangxiao
   */
  getTrainingCampStat: (period: string) => {
    return new Promise<ResponseModel<any>>((resolve) => {
      setTimeout(() => {
        resolve({
          code: 0,
          ok: true,
          msg: '操作成功',
          data: {
            totalCount: 120,
            newCount: 120,
            dealCount: 3,
            dealRate: 2.5,
            avgScore: 75,
          },
        });
      }, 200);
    });
  },

  /**
   * 获取长期池统计数据 @author wangxiao
   */
  getLongTermPoolStat: () => {
    return new Promise<ResponseModel<any>>((resolve) => {
      setTimeout(() => {
        resolve({
          code: 0,
          ok: true,
          msg: '操作成功',
          data: {
            totalCount: 850,
            highIntentCount: 180,
            mediumIntentCount: 420,
            lowIntentCount: 250,
            avgScore: 62,
          },
        });
      }, 200);
    });
  },

  /**
   * 搜索聊天记录 @author wangxiao
   */
  searchChatRecords: (studentId: number, keyword: string) => {
    return new Promise<ResponseModel<any>>((resolve) => {
      setTimeout(() => {
        resolve({
          code: 0,
          ok: true,
          msg: '操作成功',
          data: {
            records: [
              {
                time: '2025-10-05 14:30',
                content: `包含"${keyword}"的聊天记录...`,
              },
            ],
          },
        });
      }, 200);
    });
  },

  /**
   * 触发AI评分分析 @author wangxiao
   */
  triggerAiAnalysis: (studentId: number) => {
    return new Promise<ResponseModel<any>>((resolve) => {
      setTimeout(() => {
        resolve({
          code: 0,
          ok: true,
          msg: 'AI分析完成',
          data: {
            newScore: 88,
            analysis: '学员最近互动频繁，成交意向提升',
          },
        });
      }, 1000);
    });
  },
};
