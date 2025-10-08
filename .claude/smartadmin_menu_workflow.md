---
title: SmartAdmin 菜单开发工作流程
author: wangxiao
company: 子午线高科智能科技
date: 2025-10-07
---

# SmartAdmin 菜单开发工作流程

## 🎯 核心原则

1. **开闭原则**: 主脚本不修改，用增量脚本扩展
2. **全量重建**: 改了任何SQL必须全部重新执行验证

---

## 📋 菜单ID分配规则

### ID段规划

| ID段 | 用途 | 示例模块 | 备注 |
|------|------|----------|------|
| 1-99 | 系统功能 | 登录、菜单、角色 | 核心系统 |
| 100-199 | 监控服务 | 日志、监控、定时任务 | 运维相关 |
| 200-299 | 业务功能 | OA、ERP、CRM | 核心业务 |
| 300-399 | 支撑功能 | 文件、配置、通知 | 辅助功能 |
| **400-499** | **原型功能** ⭐ | 学员跟进、企微助手 | **原型专用** |

### 原型功能ID详细分配

```
400-499: 原型功能专用
├── 400: 页面原型（一级目录）
├── 401-412: 学员跟进（已占用）
├── 413-429: 预留
├── 430-439: 企微助手（已占用）
└── 440-499: 其他原型功能
```

### 分配原则

1. ✅ 每个子功能预留20个ID（例如：430-439）
2. ✅ 检查已占用ID避免冲突
3. ✅ 同一模块的菜单ID必须连续
4. ✅ 不要跨段使用ID

---

## 🚀 开发流程

### Step 0: 创建前检查 ⚠️ 必读

**检查现有菜单**:
```bash
# 检查菜单是否已存在
docker exec -it smart-admin-mysql mysql -uroot -pSmartAdmin666 smart_admin_v3 -e "
SELECT menu_id, menu_name, parent_id FROM t_menu
WHERE menu_id BETWEEN 400 AND 499 OR menu_name LIKE '%原型%'
ORDER BY menu_id;
"
```

**检查结果处理**:
- ✅ 已存在同功能父菜单 → 使用现有菜单的 `menu_id` 作为 `parent_id`
- ❌ 不存在 → 可以创建新的父级菜单

### Step 1: 创建增量脚本

**命名**: `v[版本号]-[功能描述].sql`

**标准模板**:
```sql
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ==========================================
-- 功能描述
-- 作者: wangxiao
-- 日期: 2025-10-07
-- ==========================================

-- 插入菜单（确保parent_id存在）
INSERT INTO t_menu VALUES (430, '企微助手', 1, 400, ...);
INSERT INTO t_menu VALUES (431, '聊天记录', 2, 430, ...);

-- 角色授权（role_id=1为超级管理员）
INSERT INTO t_role_menu (role_id, menu_id) VALUES (1, 430), (1, 431);

-- 验证SQL
SELECT menu_id, menu_name, parent_id FROM t_menu WHERE menu_id >= 430;

SET FOREIGN_KEY_CHECKS = 1;
```

### Step 2: 全量重建数据库

**执行命令**:
```bash
# 1. 执行主脚本（自带DROP TABLE，会清空重建）
docker exec -i smart-admin-mysql mysql -uroot -pSmartAdmin666 smart_admin_v3 \
  < 数据库SQL脚本/mysql/smart_admin_v3.sql

# 2. 按版本顺序执行所有增量脚本
cd 数据库SQL脚本/mysql/sql-update-log
for f in v*.sql; do
  echo "执行: $f"
  docker exec -i smart-admin-mysql mysql -uroot -pSmartAdmin666 smart_admin_v3 < $f
done

# 3. 验证结果
docker exec -it smart-admin-mysql mysql -uroot -pSmartAdmin666 smart_admin_v3 -e "
SELECT COUNT(*) as total_menus FROM t_menu;
SELECT menu_id, menu_name FROM t_menu WHERE menu_id BETWEEN 400 AND 499;
"
```

### Step 3: 前端路由集成

**路由配置**: `src/router/prototype/[模块].ts`

```typescript
export const prototypeRouters = [
  {
    path: '/prototype/wecom',
    component: () => import('/@/layout/index.vue'),
    meta: { title: '企微助手' },
    children: [
      {
        path: '/prototype/wecom/chat-record/list',
        component: () => import('/@/views/prototype/wecom/chat-record-list.vue'),
        meta: { title: '聊天记录', keepAlive: true },
      },
    ],
  },
];
```

**集成主路由**: `src/router/routers.ts`
```typescript
import { prototypeRouters } from './prototype/wecom';
export const routerArray = [...prototypeRouters, ...其他路由];
```

---

## 🗄️ 菜单表核心字段

| 字段 | 类型 | 说明 | 示例值 |
|------|------|------|--------|
| `menu_id` | bigint | 菜单ID | 430 |
| `menu_name` | varchar | 菜单名称 | '企微助手' |
| `menu_type` | int | 类型 | 1=目录, 2=菜单, 3=功能点 |
| `parent_id` | bigint | 父菜单ID | 0=根菜单, 400=页面原型 |
| `path` | varchar | 前端路由 | '/prototype/wecom' |
| `component` | varchar | Vue组件路径 | '/prototype/wecom/chat-record-list.vue' |
| `api_perms` | varchar | 权限标识 | 'wecom:chat:query' |
| `visible_flag` | tinyint | 是否显示 | 1=显示, 0=隐藏 |
| `cache_flag` | tinyint | 是否缓存 | 1=缓存, 0=不缓存 |

---

## ⚠️ 常见错误与解决

### 错误1: 重复创建父级菜单

❌ **错误做法**:
```sql
-- 没检查就创建，导致重复
INSERT INTO t_menu VALUES (500, '页面原型', 1, 0, ...);
-- 实际上 menu_id=400 已经存在"页面原型"！
```

✅ **正确做法**:
```sql
-- 1. 先查询
SELECT menu_id FROM t_menu WHERE menu_name = '页面原型';
-- 结果：menu_id = 400 已存在

-- 2. 使用现有父级
INSERT INTO t_menu VALUES (430, '企微助手', 1, 400, ...);  -- parent_id=400
```

### 错误2: ID段分配错误

❌ **错误**: 原型功能使用500+的ID
✅ **正确**: 原型功能必须使用 400-499

### 错误3: parentId指向不存在的菜单

❌ **错误**: `INSERT INTO t_menu VALUES (431, '聊天记录', 2, 500, ...);`
✅ **正确**: 先确保 parent_id=430 存在

---

## 🔍 故障排查

### 菜单不显示？

1. 检查 `t_role_menu` 是否授权
2. 检查 `visible_flag` 是否为 1
3. 检查前端 `routers.ts` 是否注册路由

### 接口报"无权限"？

1. 检查 `@SaCheckPermission` 和数据库 `api_perms` 是否一致
2. 开发环境检查Mock用户 `administratorFlag = true`

### Duplicate entry错误？

- 主脚本自带DROP TABLE，直接重新执行主脚本即可

---

## 📝 开发检查清单

### 创建菜单前
- [ ] 查询现有菜单，避免重复创建父级
- [ ] 确认ID段分配（400-499原型功能）
- [ ] 检查已占用ID，避免冲突

### 创建脚本时
- [ ] 命名规范：`v[版本]-[功能].sql`
- [ ] parent_id 确保父菜单存在
- [ ] menu_id 连续且在正确ID段内
- [ ] 包含角色授权SQL
- [ ] 包含验证SQL

### 执行后
- [ ] 全量重建数据库（主脚本 + 所有增量）
- [ ] 验证菜单插入成功
- [ ] 前端路由配置正确
- [ ] 功能测试通过

---

## 📖 参考资源

- 数据库主脚本: `数据库SQL脚本/mysql/smart_admin_v3.sql`
- 增量脚本目录: `数据库SQL脚本/mysql/sql-update-log/`
- 前端路由目录: `smart-admin-web-typescript/src/router/`

---

**最佳实践**: 先检查、再规划、后执行，全量验证。每次修改SQL都要重建整个数据库确保一致性。