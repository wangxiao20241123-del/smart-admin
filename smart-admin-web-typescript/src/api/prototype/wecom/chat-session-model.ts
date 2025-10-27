/*
 * 企业微信聊天会话 Model
 *
 * @Author:    wangxiao
 * @Date:      2025-10-07
 * @Copyright  子午线高科智能科技 2025
 */

import { PageParamModel } from '/@/api/model/page-model';

// ==================== 查询表单 ====================

/**
 * 聊天会话查询表单
 */
export interface ChatSessionQueryForm extends PageParamModel {
  // 关键字搜索
  keyword?: string;

  // 时间范围
  startTime?: string;
  endTime?: string;

  // 伙伴姓名/备注
  partnerName?: string;

  // 学员姓名/备注
  studentName?: string;

  // 学员微信ID
  studentWechatId?: string;

  // 消息数范围
  minMsgCount?: number;
  maxMsgCount?: number;

  // 排序字段
  sortField?: 'partnerMsgCount' | 'studentMsgCount' | 'totalMsgCount' | 'lastMsgTime';
  sortOrder?: 'asc' | 'desc';
}

// ==================== 展示VO ====================

/**
 * 聊天会话VO
 */
export interface ChatSessionVO {
  // 主键
  id: number;

  // 伙伴信息
  partnerAvatar: string;         // 伙伴头像
  partnerRemark: string;         // 伙伴备注
  partnerNickname: string;       // 伙伴昵称
  partnerWechatId: string;       // 伙伴微信ID

  // 学员信息
  studentAvatar: string;         // 学员头像
  studentRemark: string;         // 学员备注
  studentNickname: string;       // 学员昵称
  studentWechatId: string;       // 学员微信ID

  // 消息统计
  partnerMsgCount: number;       // 伙伴消息数
  studentMsgCount: number;       // 学员消息数
  totalMsgCount: number;         // 总消息数

  // 最后消息
  lastMsgContent: string;        // 最后消息内容
  lastMsgTime: string;           // 最后消息时间

  // 时间范围
  firstMsgTime: string;          // 最早时间
  latestMsgTime: string;         // 最后时间

  // 会话标识
  sessionId: string;             // 会话ID
}

/**
 * 聊天会话详情VO
 */
export interface ChatSessionDetailVO extends ChatSessionVO {
  // 消息列表
  messages?: Array<{
    msgId: string;
    fromType: 'partner' | 'student';  // 发送者类型
    msgType: string;                   // 消息类型
    content: string;                   // 消息内容
    msgTime: string;                   // 消息时间
  }>;
}

// ==================== 导出表单 ====================

/**
 * 聊天会话导出表单
 */
export interface ChatSessionExportForm {
  // 时间范围
  startTime?: string;
  endTime?: string;

  // 伙伴姓名
  partnerName?: string;

  // 学员姓名
  studentName?: string;

  // 最大导出数量
  maxCount?: number;
}
