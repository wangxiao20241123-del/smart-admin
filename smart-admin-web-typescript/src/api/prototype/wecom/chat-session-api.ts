/*
 * 企业微信聊天会话 API（原型开发）
 *
 * @Author:    wangxiao
 * @Date:      2025-10-07
 * @Copyright  子午线高科智能科技 2025
 */

import { postRequest } from '/@/lib/axios';
import { ResponseModel } from '/@/api/model/response-model';
import { PageResultModel } from '/@/api/model/page-model';
import { ChatSessionQueryForm, ChatSessionVO, ChatSessionExportForm } from './chat-session-model';
import { callApiWithFallback } from '/@/api/prototype/api-checker';

/**
 * 生成Mock会话数据 @author wangxiao
 */
const generateMockSessions = (pageNum: number, pageSize: number, sortField?: string, sortOrder?: string): ChatSessionVO[] => {
  const partnerNames = ['张老师', '李老师', '王老师', '赵老师', '刘老师'];
  const studentNames = ['小明', '小红', '小刚', '小美', '小华', '小强', '小丽', '小芳'];
  const avatars = [
    'https://api.dicebear.com/7.x/avataaars/svg?seed=1',
    'https://api.dicebear.com/7.x/avataaars/svg?seed=2',
    'https://api.dicebear.com/7.x/avataaars/svg?seed=3',
    'https://api.dicebear.com/7.x/avataaars/svg?seed=4',
    'https://api.dicebear.com/7.x/avataaars/svg?seed=5',
  ];

  const lastMessages = [
    '好的，我知道了',
    '明天见！',
    '感谢老师的指导',
    '这个问题我理解了',
    '今天的作业我已经完成',
    '周末愉快~',
    '收到，谢谢老师',
  ];

  // 生成全量数据（50条）
  const allSessions = Array.from({ length: 50 }, (_, i) => {
    const partnerIdx = i % partnerNames.length;
    const studentIdx = i % studentNames.length;

    const partnerMsgCount = 15 + Math.floor(Math.random() * 50);
    const studentMsgCount = 10 + Math.floor(Math.random() * 40);

    // 生成最近7天内的随机时间
    const daysAgo = Math.floor(Math.random() * 7);
    const hoursAgo = Math.floor(Math.random() * 24);
    const latestTime = new Date(Date.now() - daysAgo * 86400000 - hoursAgo * 3600000);
    const firstTime = new Date(latestTime.getTime() - (3 + Math.random() * 10) * 86400000);

    return {
      id: 1001 + i,
      sessionId: `session_${1001 + i}`,

      // 伙伴信息
      partnerAvatar: avatars[partnerIdx],
      partnerRemark: partnerNames[partnerIdx],
      partnerNickname: partnerNames[partnerIdx],
      partnerWechatId: `partner_${partnerIdx + 1}`,

      // 学员信息
      studentAvatar: avatars[studentIdx],
      studentRemark: studentNames[studentIdx] + '同学',
      studentNickname: studentNames[studentIdx],
      studentWechatId: `student_${studentIdx + 1}`,

      // 消息统计
      partnerMsgCount,
      studentMsgCount,
      totalMsgCount: partnerMsgCount + studentMsgCount,

      // 最后消息
      lastMsgContent: lastMessages[i % lastMessages.length],
      lastMsgTime: latestTime.toISOString().slice(0, 19).replace('T', ' '),

      // 时间范围
      firstMsgTime: firstTime.toISOString().slice(0, 19).replace('T', ' '),
      latestMsgTime: latestTime.toISOString().slice(0, 19).replace('T', ' '),
    };
  });

  // 排序处理
  if (sortField) {
    allSessions.sort((a, b) => {
      let aValue: any = a[sortField as keyof ChatSessionVO];
      let bValue: any = b[sortField as keyof ChatSessionVO];

      // 数字比较
      if (typeof aValue === 'number' && typeof bValue === 'number') {
        return sortOrder === 'desc' ? bValue - aValue : aValue - bValue;
      }

      // 字符串/时间比较
      if (sortOrder === 'desc') {
        return bValue > aValue ? 1 : -1;
      } else {
        return aValue > bValue ? 1 : -1;
      }
    });
  }

  // 分页处理
  const startIndex = (pageNum - 1) * pageSize;
  const endIndex = startIndex + pageSize;
  return allSessions.slice(startIndex, endIndex);
};

/**
 * 聊天会话API
 */
export const chatSessionApi = {
  /**
   * 分页查询聊天会话 @author wangxiao
   */
  queryPage: async (param: ChatSessionQueryForm) => {
    return callApiWithFallback(
      '/business/wecom/chatSession/queryPage',
      () => postRequest<ResponseModel<PageResultModel<ChatSessionVO>>>('/business/wecom/chatSession/queryPage', param),
      () => ({
        code: 0,
        msg: 'success',
        data: {
          list: generateMockSessions(
            param.pageNum || 1,
            param.pageSize || 10,
            param.sortField,
            param.sortOrder
          ),
          total: 50,
          pageNum: param.pageNum || 1,
          pageSize: param.pageSize || 10,
        },
        ok: true,
      })
    );
  },

  /**
   * 导出聊天会话 @author wangxiao
   */
  exportSessions: async (param: ChatSessionExportForm) => {
    return callApiWithFallback(
      '/business/wecom/chatSession/export',
      () => postRequest<Blob>('/business/wecom/chatSession/export', param, { responseType: 'blob' }),
      () => {
        // Mock导出：创建一个简单的CSV Blob
        const csvContent = 'ID,伙伴,学员,消息数\n1001,张老师,小明,65\n';
        return new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
      }
    );
  },
};
