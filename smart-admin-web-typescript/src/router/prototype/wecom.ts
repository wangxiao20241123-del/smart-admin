/*
 * 企业微信原型路由
 *
 * @Author:    wangxiao
 * @Date:      2025-01-07
 * @Copyright  子午线高科智能科技 2025
 */

export const wecomPrototypeRouters = [
  {
    path: '/prototype/wecom',
    component: () => import('/@/layout/index.vue'),
    meta: {
      title: '企微助手',
      icon: 'WechatOutlined',
    },
    children: [
      {
        path: '/prototype/wecom/chat-record/list',
        name: 'WecomChatRecordList',
        component: () => import('/@/views/prototype/wecom/chat-record-list.vue'),
        meta: {
          title: '聊天记录',
          keepAlive: true,
        },
      },
    ],
  },
];
