SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ================================
-- 学员跟进菜单初始化脚本
-- 说明: 创建学员跟进菜单结构，包含持续跟进和沉睡唤醒两个子模块
-- 作者: wangxiao
-- 企业: 子午线高科智能科技
-- 日期: 2025-10-06
-- ================================

-- 1. 添加一级菜单: 页面原型
INSERT INTO `t_menu` (`menu_id`, `menu_name`, `menu_type`, `parent_id`, `sort`, `path`, `component`, `perms_type`, `api_perms`, `web_perms`, `icon`, `context_menu_id`, `frame_flag`, `frame_url`, `cache_flag`, `visible_flag`, `disabled_flag`, `deleted_flag`, `create_user_id`, `create_time`, `update_user_id`, `update_time`)
VALUES (400, '页面原型', 1, 0, 3, '/prototype', NULL, NULL, NULL, NULL, 'FileSearchOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW());

-- 2. 添加二级目录: 学员跟进
INSERT INTO `t_menu` (`menu_id`, `menu_name`, `menu_type`, `parent_id`, `sort`, `path`, `component`, `perms_type`, `api_perms`, `web_perms`, `icon`, `context_menu_id`, `frame_flag`, `frame_url`, `cache_flag`, `visible_flag`, `disabled_flag`, `deleted_flag`, `create_user_id`, `create_time`, `update_user_id`, `update_time`)
VALUES (401, '学员跟进', 1, 400, 1, '/prototype/student-followup', NULL, NULL, NULL, NULL, 'TeamOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW());

-- 3. 添加三级菜单: 持续跟进 (原本期学员列表)
INSERT INTO `t_menu` (`menu_id`, `menu_name`, `menu_type`, `parent_id`, `sort`, `path`, `component`, `perms_type`, `api_perms`, `web_perms`, `icon`, `context_menu_id`, `frame_flag`, `frame_url`, `cache_flag`, `visible_flag`, `disabled_flag`, `deleted_flag`, `create_user_id`, `create_time`, `update_user_id`, `update_time`)
VALUES (411, '持续跟进', 2, 401, 1, '/prototype/student-followup/continuous', '/prototype/student-followup/student/student-list.vue', NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, 1, 0, 0, 1, NOW(), 1, NOW());

-- 4. 添加三级菜单: 沉睡唤醒 (占位页面)
INSERT INTO `t_menu` (`menu_id`, `menu_name`, `menu_type`, `parent_id`, `sort`, `path`, `component`, `perms_type`, `api_perms`, `web_perms`, `icon`, `context_menu_id`, `frame_flag`, `frame_url`, `cache_flag`, `visible_flag`, `disabled_flag`, `deleted_flag`, `create_user_id`, `create_time`, `update_user_id`, `update_time`)
VALUES (412, '沉睡唤醒', 2, 401, 2, '/prototype/student-followup/dormant', '/prototype/student-followup/dormant/dormant-awaken.vue', NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, 1, 0, 0, 1, NOW(), 1, NOW());

-- 5. 添加隐藏菜单: 学员详情
INSERT INTO `t_menu` (`menu_id`, `menu_name`, `menu_type`, `parent_id`, `sort`, `path`, `component`, `perms_type`, `api_perms`, `web_perms`, `icon`, `context_menu_id`, `frame_flag`, `frame_url`, `cache_flag`, `visible_flag`, `disabled_flag`, `deleted_flag`, `create_user_id`, `create_time`, `update_user_id`, `update_time`)
VALUES (402, '学员详情', 2, 400, 2, '/prototype/student-followup/student/detail', '/prototype/student-followup/student/student-detail.vue', NULL, NULL, NULL, NULL, NULL, 0, NULL, 0, 0, 0, 0, 1, NOW(), 1, NOW());

-- 6. 添加功能点: 查询学员
INSERT INTO `t_menu` (`menu_id`, `menu_name`, `menu_type`, `parent_id`, `sort`, `path`, `component`, `perms_type`, `api_perms`, `web_perms`, `icon`, `context_menu_id`, `frame_flag`, `frame_url`, `cache_flag`, `visible_flag`, `disabled_flag`, `deleted_flag`, `create_user_id`, `create_time`, `update_user_id`, `update_time`)
VALUES (403, '查询学员', 3, 411, 1, NULL, NULL, 1, 'student-followup:student:query', 'student-followup:student:query', NULL, 411, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW());

-- 7. 添加功能点: 新增学员
INSERT INTO `t_menu` (`menu_id`, `menu_name`, `menu_type`, `parent_id`, `sort`, `path`, `component`, `perms_type`, `api_perms`, `web_perms`, `icon`, `context_menu_id`, `frame_flag`, `frame_url`, `cache_flag`, `visible_flag`, `disabled_flag`, `deleted_flag`, `create_user_id`, `create_time`, `update_user_id`, `update_time`)
VALUES (404, '新增学员', 3, 411, 2, NULL, NULL, 1, 'student-followup:student:add', 'student-followup:student:add', NULL, 411, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW());

-- 8. 添加功能点: 编辑学员
INSERT INTO `t_menu` (`menu_id`, `menu_name`, `menu_type`, `parent_id`, `sort`, `path`, `component`, `perms_type`, `api_perms`, `web_perms`, `icon`, `context_menu_id`, `frame_flag`, `frame_url`, `cache_flag`, `visible_flag`, `disabled_flag`, `deleted_flag`, `create_user_id`, `create_time`, `update_user_id`, `update_time`)
VALUES (405, '编辑学员', 3, 411, 3, NULL, NULL, 1, 'student-followup:student:update', 'student-followup:student:update', NULL, 411, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW());

-- 9. 添加功能点: 删除学员
INSERT INTO `t_menu` (`menu_id`, `menu_name`, `menu_type`, `parent_id`, `sort`, `path`, `component`, `perms_type`, `api_perms`, `web_perms`, `icon`, `context_menu_id`, `frame_flag`, `frame_url`, `cache_flag`, `visible_flag`, `disabled_flag`, `deleted_flag`, `create_user_id`, `create_time`, `update_user_id`, `update_time`)
VALUES (406, '删除学员', 3, 411, 4, NULL, NULL, 1, 'student-followup:student:delete', 'student-followup:student:delete', NULL, 411, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW());

-- 10. 添加功能点: 查看学员详情
INSERT INTO `t_menu` (`menu_id`, `menu_name`, `menu_type`, `parent_id`, `sort`, `path`, `component`, `perms_type`, `api_perms`, `web_perms`, `icon`, `context_menu_id`, `frame_flag`, `frame_url`, `cache_flag`, `visible_flag`, `disabled_flag`, `deleted_flag`, `create_user_id`, `create_time`, `update_user_id`, `update_time`)
VALUES (407, '查看详情', 3, 411, 5, NULL, NULL, 1, 'student-followup:student:detail', 'student-followup:student:detail', NULL, 411, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW());

-- 11. 添加功能点: 调整AI评分
INSERT INTO `t_menu` (`menu_id`, `menu_name`, `menu_type`, `parent_id`, `sort`, `path`, `component`, `perms_type`, `api_perms`, `web_perms`, `icon`, `context_menu_id`, `frame_flag`, `frame_url`, `cache_flag`, `visible_flag`, `disabled_flag`, `deleted_flag`, `create_user_id`, `create_time`, `update_user_id`, `update_time`)
VALUES (408, '调整评分', 3, 411, 6, NULL, NULL, 1, 'student-followup:student:adjustScore', 'student-followup:student:adjustScore', NULL, 411, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW());

-- 12. 添加功能点: 获取AI话术
INSERT INTO `t_menu` (`menu_id`, `menu_name`, `menu_type`, `parent_id`, `sort`, `path`, `component`, `perms_type`, `api_perms`, `web_perms`, `icon`, `context_menu_id`, `frame_flag`, `frame_url`, `cache_flag`, `visible_flag`, `disabled_flag`, `deleted_flag`, `create_user_id`, `create_time`, `update_user_id`, `update_time`)
VALUES (409, 'AI话术', 3, 411, 7, NULL, NULL, 1, 'student-followup:student:scriptRecommend', 'student-followup:student:scriptRecommend', NULL, 411, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW());

-- 13. 添加功能点: 批量操作
INSERT INTO `t_menu` (`menu_id`, `menu_name`, `menu_type`, `parent_id`, `sort`, `path`, `component`, `perms_type`, `api_perms`, `web_perms`, `icon`, `context_menu_id`, `frame_flag`, `frame_url`, `cache_flag`, `visible_flag`, `disabled_flag`, `deleted_flag`, `create_user_id`, `create_time`, `update_user_id`, `update_time`)
VALUES (410, '批量操作', 3, 411, 8, NULL, NULL, 1, 'student-followup:student:batchOperate', 'student-followup:student:batchOperate', NULL, 411, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW());

-- ================================
-- 菜单结构说明
-- ================================
-- menu_id:
--   400: 页面原型 (一级菜单 - 存放纯前端原型页面)
--   401: 学员跟进 (二级目录)
--   411: 持续跟进 (三级菜单 - 学员列表页)
--   412: 沉睡唤醒 (三级菜单 - 占位页)
--   402: 学员详情 (二级菜单 - 详情页, 隐藏)
--   403-410: 功能点 (四级菜单 - 按钮权限, 挂在411持续跟进下)
--
-- menu_type:
--   1: 目录
--   2: 菜单 (对应页面)
--   3: 功能点 (按钮权限)
--
-- parent_id:
--   0: 根级菜单
--   400: 挂在"页面原型"下 → 401学员跟进
--   401: 挂在"学员跟进"下 → 411持续跟进, 412沉睡唤醒
--   411: 挂在"持续跟进"下 → 403-410功能点
--
-- visible_flag:
--   1: 显示在菜单
--   0: 不显示(如详情页)
--
-- cache_flag:
--   1: 缓存页面
--   0: 不缓存
-- ================================

-- 为管理员角色(role_id=1)授予所有菜单权限
INSERT INTO `t_role_menu` (`role_id`, `menu_id`)
VALUES
  (1, 400), (1, 401), (1, 402), (1, 403), (1, 404),
  (1, 405), (1, 406), (1, 407), (1, 408), (1, 409),
  (1, 410), (1, 411), (1, 412);

-- 查看新增的菜单
SELECT menu_id, menu_name, menu_type, parent_id, path, component, visible_flag, icon
FROM t_menu
WHERE menu_id >= 400 AND menu_id <= 412
ORDER BY menu_id;

SET FOREIGN_KEY_CHECKS = 1;
