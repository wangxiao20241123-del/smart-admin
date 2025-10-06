/*
 * 学员跟进路由
 *
 * @Author:    wangxiao
 * @Date:      2025-10-06
 * @Copyright  子午线高科智能科技 2025
 */
import SmartLayout from '/@/layout/index.vue';

export const studentFollowupRouters = [
  {
    path: '/prototype/student-followup',
    name: 'PrototypeStudentFollowup',
    component: SmartLayout,
    meta: {
      title: '学员跟进',
      icon: 'TeamOutlined',
    },
    children: [
      {
        path: '/prototype/student-followup/student/list',
        name: 'PrototypeStudentList',
        component: () => import('/@/views/prototype/student-followup/student/student-list.vue'),
        meta: {
          title: '持续跟进',
          componentName: 'StudentList',
          keepAlive: true,
        },
      },
      {
        path: '/prototype/student-followup/student/detail/:id?',
        name: 'PrototypeStudentDetail',
        component: () => import('/@/views/prototype/student-followup/student/student-detail.vue'),
        meta: {
          title: '学员详情',
          componentName: 'StudentDetail',
          keepAlive: false,
          hideInMenu: true,
        },
      },
      {
        path: '/prototype/student-followup/dormant/awaken',
        name: 'PrototypeDormantAwaken',
        component: () => import('/@/views/prototype/student-followup/dormant/dormant-awaken.vue'),
        meta: {
          title: '沉睡唤醒',
          componentName: 'DormantAwaken',
          keepAlive: true,
        },
      },
    ],
  },
];
