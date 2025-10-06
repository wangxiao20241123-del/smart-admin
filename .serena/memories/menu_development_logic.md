# AI销售助手菜单开发逻辑

## 菜单系统架构

### 数据库表结构
- **t_menu**: 菜单主表，存储所有菜单项（一级菜单、二级菜单、功能点）
- **t_role_menu**: 角色菜单关联表，控制角色对菜单的访问权限

### 菜单类型
1. **menu_type = 1**: 一级菜单（顶级导航）
2. **menu_type = 2**: 二级菜单（页面路由）
3. **menu_type = 3**: 功能点（按钮权限）

### 菜单ID规划
- **AI销售助手模块**: 使用 400-410 ID段
  - 400: 一级菜单 "AI销售助手"
  - 401: 二级菜单 "客户管理"
  - 402: 隐藏页面 "客户详情"
  - 403-410: 功能点权限（8个）

## AI销售助手菜单结构

### 一级菜单 (ID: 400)
```sql
menu_name: 'AI销售助手'
menu_type: 1
parent_id: 0
path: '/ai-sales'
icon: 'RobotOutlined'
visible_flag: 1  -- 显示在导航栏
```

### 二级菜单 - 客户管理 (ID: 401)
```sql
menu_name: '客户管理'
menu_type: 2
parent_id: 400
path: '/business/ai-sales/customer'
component: '/business/ai-sales/customer/customer-list.vue'
cache_flag: 1  -- 缓存页面
visible_flag: 1  -- 显示在侧边栏
```

### 隐藏页面 - 客户详情 (ID: 402)
```sql
menu_name: '客户详情'
menu_type: 2
parent_id: 400
path: '/business/ai-sales/customer/detail'
component: '/business/ai-sales/customer/customer-detail.vue'
visible_flag: 0  -- 不显示在侧边栏（通过路由跳转访问）
```

### 功能点权限 (ID: 403-410)
所有功能点的共同特征：
- `menu_type: 3`
- `parent_id: 401` (挂载在客户管理菜单下)
- `perms_type: 1` (前后端都需要校验)
- `context_menu_id: 401` (右键菜单关联)
- `visible_flag: 1`

具体权限列表：
1. **403 - 查询客户**: `ai-sales:customer:query`
2. **404 - 新增客户**: `ai-sales:customer:add`
3. **405 - 编辑客户**: `ai-sales:customer:update`
4. **406 - 删除客户**: `ai-sales:customer:delete`
5. **407 - 查看详情**: `ai-sales:customer:detail`
6. **408 - 调整评分**: `ai-sales:customer:adjustScore`
7. **409 - AI话术**: `ai-sales:customer:scriptRecommend`
8. **410 - 批量操作**: `ai-sales:customer:batchOperate`

## 菜单权限绑定

### 管理员角色授权
```sql
INSERT INTO t_role_menu (role_id, menu_id)
VALUES 
  (1, 400),  -- 一级菜单
  (1, 401),  -- 二级菜单
  (1, 402),  -- 隐藏页面
  (1, 403), (1, 404), (1, 405), (1, 406),  -- 基础CRUD
  (1, 407), (1, 408), (1, 409), (1, 410);  -- 高级功能
```

## 菜单API使用

### 查询菜单树
```
GET /menu/tree?onlyMenu=false
返回: ResponseDTO<List<MenuTreeNode>>
```

**参数说明**:
- `onlyMenu=false`: 返回所有类型（包括功能点）
- `onlyMenu=true`: 仅返回菜单类型（不包括功能点）

### 返回数据结构
```json
{
  "code": 0,
  "ok": true,
  "data": [
    {
      "menuId": 400,
      "menuName": "AI销售助手",
      "menuType": 1,
      "path": "/ai-sales",
      "icon": "RobotOutlined",
      "children": [
        {
          "menuId": 401,
          "menuName": "客户管理",
          "menuType": 2,
          "path": "/business/ai-sales/customer",
          "component": "/business/ai-sales/customer/customer-list.vue",
          "children": [
            {
              "menuId": 403,
              "menuName": "查询客户",
              "menuType": 3,
              "webPerms": "ai-sales:customer:query",
              "apiPerms": "ai-sales:customer:query"
            }
            // ... 其他功能点
          ]
        }
      ]
    }
  ]
}
```

## 前端路由配置

### 路由文件位置
`smart-admin-web-typescript/src/router/business/ai-sales.ts`

### 路由配置示例
```typescript
export const aiSalesRoutes = [
  {
    path: '/ai-sales',
    name: 'AiSales',
    component: () => import('@/layout/index.vue'),
    meta: { title: 'AI销售助手', icon: 'RobotOutlined' },
    children: [
      {
        path: '/business/ai-sales/customer',
        name: 'CustomerManagement',
        component: () => import('@/views/business/ai-sales/customer/customer-list.vue'),
        meta: { 
          title: '客户管理',
          requiresAuth: true,
          keepAlive: true 
        }
      },
      {
        path: '/business/ai-sales/customer/detail',
        name: 'CustomerDetail',
        component: () => import('@/views/business/ai-sales/customer/customer-detail.vue'),
        meta: { 
          title: '客户详情',
          requiresAuth: true,
          hidden: true  // 不在菜单显示
        }
      }
    ]
  }
];
```

## 数据库操作

### 连接信息
- **容器名**: `smart-admin-mysql`
- **数据库**: `smart_admin_v3`
- **用户名**: `root`
- **密码**: `SmartAdmin666`

### 执行SQL脚本
```bash
docker exec -i smart-admin-mysql mysql -uroot -pSmartAdmin666 smart_admin_v3 < menu.sql
```

### 查询菜单
```bash
docker exec smart-admin-mysql mysql -uroot -pSmartAdmin666 smart_admin_v3 \
  -e "SELECT menu_id, menu_name, menu_type, parent_id, visible_flag 
      FROM t_menu WHERE menu_id BETWEEN 400 AND 410 ORDER BY menu_id;"
```

## 常见问题

### 1. 菜单ID冲突
**错误**: `Duplicate entry '300' for key 't_menu.PRIMARY'`
**原因**: ID已被占用
**解决**: 更换ID段（如从300-310改为400-410）

### 2. 菜单不显示
**检查清单**:
1. ✅ 数据库中菜单是否插入成功
2. ✅ `visible_flag` 是否为 1
3. ✅ 角色是否有该菜单权限（t_role_menu）
4. ✅ 前端路由是否配置
5. ✅ 组件路径是否正确

### 3. 权限校验失败
**检查清单**:
1. ✅ 功能点权限码是否正确（web_perms, api_perms）
2. ✅ 后端Controller是否添加了 `@SaCheckPermission` 注解
3. ✅ 前端按钮是否使用了 `v-has` 指令
4. ✅ 角色是否有该功能点权限

## 开发流程总结

1. **规划菜单结构**: 确定一级菜单、二级菜单、功能点
2. **分配ID段**: 避免与现有菜单冲突
3. **编写SQL脚本**: 插入菜单和权限数据
4. **执行SQL**: 通过Docker或MySQL客户端执行
5. **验证数据**: 查询数据库确认插入成功
6. **测试API**: 调用菜单树API验证返回
7. **配置路由**: 前端添加对应路由配置
8. **开发页面**: 实现具体页面组件
9. **权限绑定**: 给角色分配菜单权限
10. **功能测试**: 验证菜单显示和权限控制
