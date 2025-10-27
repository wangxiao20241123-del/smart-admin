SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ==========================================
-- 企业微信聊天记录功能
-- 作者: wangxiao
-- 日期: 2025-01-07
-- 版本: v3.29.0
-- ==========================================

-- ----------------------------
-- Table: t_wecom_chat_message
-- ----------------------------
CREATE TABLE IF NOT EXISTS `t_wecom_chat_message` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `msg_id` VARCHAR(128) NOT NULL COMMENT '消息唯一ID (企微msgid)',
  `seq` BIGINT NOT NULL COMMENT '消息序列号 (拉取时的seq)',

  `action` VARCHAR(20) NOT NULL COMMENT '消息动作: send=发送, recall=撤回, switch=切换企业日志',
  `from_user_id` VARCHAR(64) NOT NULL COMMENT '发送者ID (userid)',
  `msg_time` BIGINT NOT NULL COMMENT '消息时间戳 (毫秒)',
  `msg_type` VARCHAR(20) NOT NULL COMMENT '消息类型: text/image/voice/video/file/emotion/...',

  `to_list` TEXT COMMENT '接收者ID列表 (JSON数组字符串)',
  `room_id` VARCHAR(64) DEFAULT NULL COMMENT '群聊ID (单聊时为NULL)',

  `text_content` TEXT COMMENT '文本消息内容',

  `sdk_fileid` VARCHAR(200) COMMENT '企微SDK文件ID (用于下载)',
  `media_url` VARCHAR(500) COMMENT 'OSS存储的媒体文件URL',
  `filename` VARCHAR(200) COMMENT '原始文件名',
  `filesize` BIGINT COMMENT '文件大小 (字节)',
  `md5sum` VARCHAR(64) COMMENT '文件MD5',

  `raw_data` JSON COMMENT '原始消息完整JSON',

  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '入库时间',

  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_msg_id` (`msg_id`),
  KEY `idx_seq` (`seq`),
  KEY `idx_from_time` (`from_user_id`, `msg_time`),
  KEY `idx_room_time` (`room_id`, `msg_time`),
  KEY `idx_msg_time` (`msg_time`),
  KEY `idx_msg_type` (`msg_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='企业微信聊天记录表';

-- ----------------------------
-- Table: t_wecom_sync_progress
-- ----------------------------
CREATE TABLE IF NOT EXISTS `t_wecom_sync_progress` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `last_seq` BIGINT NOT NULL DEFAULT 0 COMMENT '最后同步的seq',
  `last_sync_time` DATETIME COMMENT '最后同步时间',
  `total_synced` BIGINT DEFAULT 0 COMMENT '累计同步消息数',
  `status` TINYINT DEFAULT 1 COMMENT '同步状态: 1=运行中 0=已暂停',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='同步进度表';

-- 初始化同步进度
INSERT INTO `t_wecom_sync_progress` (`last_seq`) VALUES (0)
ON DUPLICATE KEY UPDATE `last_seq` = `last_seq`;

-- ----------------------------
-- 菜单: 企微助手
-- ----------------------------
INSERT INTO `t_menu` VALUES (430, '企微助手', 1, 400, 'WechatOutlined', '/prototype/wecom', NULL, NULL, 0, 1, 1, 1, NULL, '2025-01-07 00:00:00', '2025-01-07 00:00:00', NULL);
INSERT INTO `t_menu` VALUES (431, '聊天记录', 2, 430, NULL, '/prototype/wecom/chat-record/list', '/prototype/wecom/chat-record-list.vue', 'WecomChatRecordList', 0, 1, 1, 1, NULL, '2025-01-07 00:00:00', '2025-01-07 00:00:00', NULL);
INSERT INTO `t_menu` VALUES (432, '查询', 3, 431, NULL, NULL, NULL, NULL, 0, 1, NULL, NULL, 'wecom:chat:query', '2025-01-07 00:00:00', '2025-01-07 00:00:00', NULL);
INSERT INTO `t_menu` VALUES (433, '导出', 3, 431, NULL, NULL, NULL, NULL, 0, 1, NULL, NULL, 'wecom:chat:export', '2025-01-07 00:00:00', '2025-01-07 00:00:00', NULL);

-- 角色授权 (超级管理员)
INSERT INTO `t_role_menu` (`role_id`, `menu_id`) VALUES (1, 430), (1, 431), (1, 432), (1, 433);

-- 验证SQL
SELECT menu_id, menu_name, parent_id FROM t_menu WHERE menu_id BETWEEN 430 AND 433;

SET FOREIGN_KEY_CHECKS = 1;
