/*
 * 企业微信聊天记录常量
 *
 * @Author:    wangxiao
 * @Date:      2025-01-07
 * @Copyright  子午线高科智能科技 2025
 */

import { SmartEnum } from '/@/types/smart-enum';

/**
 * 消息类型枚举
 */
export const MSG_TYPE_ENUM: SmartEnum<string> = {
  TEXT: { value: 'text', desc: '文本消息' },
  IMAGE: { value: 'image', desc: '图片消息' },
  VOICE: { value: 'voice', desc: '语音消息' },
  VIDEO: { value: 'video', desc: '视频消息' },
  FILE: { value: 'file', desc: '文件消息' },
  EMOTION: { value: 'emotion', desc: '表情消息' },
  LINK: { value: 'link', desc: '链接消息' },
  WEAPP: { value: 'weapp', desc: '小程序消息' },
  LOCATION: { value: 'location', desc: '位置消息' },
  CARD: { value: 'card', desc: '名片消息' },
  REVOKE: { value: 'revoke', desc: '撤回消息' },
};

/**
 * 会话类型枚举
 */
export const CHAT_TYPE_ENUM: SmartEnum<number> = {
  SINGLE: { value: 1, desc: '单聊' },
  GROUP: { value: 2, desc: '群聊' },
};

/**
 * 是否外部联系人枚举
 */
export const IS_EXTERNAL_ENUM: SmartEnum<number> = {
  INTERNAL: { value: 0, desc: '内部' },
  EXTERNAL: { value: 1, desc: '外部' },
};

export default {
  MSG_TYPE_ENUM,
  CHAT_TYPE_ENUM,
  IS_EXTERNAL_ENUM,
};
