/*
 * 企业微信聊天记录 Model
 *
 * @Author:    wangxiao
 * @Date:      2025-01-07
 * @Copyright  子午线高科智能科技 2025
 */

import { PageParamModel, PageResultModel } from '/@/api/model/page-model';

// ==================== 查询表单 ====================

/**
 * 聊天记录查询表单
 */
export interface ChatRecordQueryForm extends PageParamModel {
  // 时间范围
  startTime?: string;
  endTime?: string;

  // 发送者
  fromUserId?: string;

  // 群聊ID
  roomId?: string;

  // 消息类型
  msgType?: string;

  // 关键词搜索
  keyword?: string;

  // 是否外部联系人
  isExternal?: boolean;

  // 会话类型: 1=单聊 2=群聊
  chatType?: number;
}

// ==================== 展示VO ====================

/**
 * 聊天记录VO
 */
export interface ChatRecordVO {
  // 主键
  id: number;

  // 消息ID
  msgId: string;

  // 消息序号
  seq: number;

  // 发送者信息
  fromUserId: string;
  fromUserName?: string;

  // 消息时间
  msgTime: string;

  // 消息类型
  msgType: string;
  msgTypeDesc?: string;

  // 文本内容
  textContent?: string;

  // 媒体文件信息
  mediaFilename?: string;
  mediaUrl?: string;
  mediaFilesize?: number;

  // 群聊信息
  roomId?: string;
  roomName?: string;

  // 接收者列表
  toList?: string;

  // 是否群聊
  isGroupChat?: boolean;

  // 创建时间
  createdAt?: string;
}

/**
 * 聊天记录详情VO
 */
export interface ChatRecordDetailVO extends ChatRecordVO {
  // 原始JSON数据
  rawData?: any;

  // 完整接收者信息
  toUserList?: Array<{
    userId: string;
    userName?: string;
  }>;
}

// ==================== 导出表单 ====================

/**
 * 聊天记录导出表单
 */
export interface ChatRecordExportForm {
  // 时间范围
  startTime?: string;
  endTime?: string;

  // 发送者
  fromUserId?: string;

  // 群聊ID
  roomId?: string;

  // 消息类型
  msgType?: string;

  // 最大导出数量
  maxCount?: number;
}
