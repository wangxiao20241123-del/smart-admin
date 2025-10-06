# SmartAdmin 菜单开发工作流程

> **作者**: wangxiao  
> **企业**: 子午线高科智能科技  
> **更新**: 2025-10-06

---

## 🎯 核心原则

1. **开闭原则**: 主脚本不修改，用增量脚本扩展
2. **每次脚本修改必须全部重新执行**: 改了任何SQL都要重建整个数据库验证

---

## 📁 目录结构

```
数据库SQL脚本/mysql/
├── smart_admin_v3.sql              # 主脚本（自带DROP TABLE）
└── sql-update-log/                 # 增量脚本（按版本号顺序）
    ├── v3.15.0-xxx.sql
    ├── v3.28.2-student-followup.sql
    └── v3.29.0-xxx.sql
```

---

## 🚀 开发流程

### 1. 创建增量脚本

**命名**: `v[版本号]-[功能描述].sql`

**内容**:
```sql
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 说明、作者、日期注释

-- 插入菜单
INSERT INTO t_menu (...) VALUES (...);

-- 角色授权
INSERT INTO t_role_menu (role_id, menu_id) VALUES (1, xxx);

-- 验证SQL
SELECT menu_id, menu_name FROM t_menu WHERE menu_id >= xxx;

SET FOREIGN_KEY_CHECKS = 1;
```

### 2. 执行数据库脚本（完整重建）

**⚠️ 关键：每次修改SQL必须全部重新执行**

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

# 3. 验证
docker exec -it smart-admin-mysql mysql -uroot -pSmartAdmin666 smart_admin_v3 -e "
SELECT COUNT(*) FROM t_menu;
"
```

---

## 🗄️ 菜单表核心字段

| 字段 | 说明 | 示例 |
|------|------|------|
| `menu_type` | 1=目录, 2=菜单, 3=功能点 | 1 |
| `parent_id` | 父菜单ID | 0=根菜单 |
| `path` | 前端路由路径 | `/prototype/student-followup` |
| `component` | Vue组件路径 | `/prototype/.../list.vue` |
| `api_perms` | 后端权限标识 | `student-followup:student:query` |
| `visible_flag` | 是否显示 | 1=显示, 0=隐藏 |
| `cache_flag` | 是否缓存 | 1=缓存, 0=不缓存 |

---

## ❗ 重要规则

### 数据库脚本执行

1. **改了任何SQL → 必须全部重新执行**（主脚本 + 所有增量脚本）
2. **主脚本自带DROP TABLE** → 直接执行会清空重建
3. **主脚本不修改** → 新功能写增量脚本
4. **增量脚本按版本号顺序执行** → v3.15.0 → v3.28.2 → v3.29.0

### 开发环境Mock用户

- `AdminInterceptor.java` 开发环境自动创建管理员
- **必须设置**: `setUserType(UserTypeEnum.ADMIN_EMPLOYEE)`
- 拥有全部权限（`administratorFlag = true`）

### 权限标识

- 数据库 `api_perms`: `student-followup:student:query`
- Controller: `@SaCheckPermission("student-followup:student:query")`
- **必须完全一致**

---

## 🔍 常见问题

**Q: 菜单不显示？**
- 检查 `t_role_menu` 是否授权
- 检查 `visible_flag` 是否为 1
- 检查前端 `routers.ts` 是否注册

**Q: 接口报"无权限"？**
- 检查 `@SaCheckPermission` 和数据库 `api_perms` 是否一致
- 开发环境检查Mock用户是否有 `administratorFlag = true`

**Q: Duplicate entry错误？**
- 主脚本自带DROP TABLE，重新执行主脚本即可

---

## 📋 最近更新 (2025-10-06)

- ✅ 修复 AdminInterceptor Mock用户NullPointerException（缺少userType）
- ✅ 合并两个SQL脚本为 v3.28.2-student-followup.sql
- ✅ 完整重建数据库：主脚本 + 所有增量脚本
- ✅ 成功插入13条菜单（menu_id 400-412）
- ✅ 学员跟进模块：页面原型 → 学员跟进 → 持续跟进/沉睡唤醒