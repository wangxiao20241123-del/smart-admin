# SmartAdmin 开发快速参考

## 项目信息
- 作者: wangxiao
- 企业: 子午线高科智能科技
- 版权格式: `@Copyright 子午线高科智能科技 2025` (无"Since")

## 📖 规范文档位置

### 前端
- **代码规范**: `smart-admin-web-typescript/CODING_STANDARDS.md`
- **开发指南**: `smart-admin-web-typescript/DEV_GUIDE.md`
- **代码模板**: `smart-admin-web-typescript/.templates/`

### 后端
- **代码规范**: `smart-admin-api-java17-springboot3/BACKEND_CODING_STANDARDS.md`
- **代码模板**: `smart-admin-api-java17-springboot3/.templates/`

### 协作
- **协作规范**: `COLLABORATION_STANDARDS.md`

### 项目配置
- **Claude配置**: `.claude/CLAUDE.md` (项目级，自动读取)

## ⚡ 快速规则

### 文件头注释（强制）
```
@Author:    wangxiao
@Copyright  子午线高科智能科技 2025
```

### 方法注释（强制）
```
@author wangxiao
```

### 前端关键点
- 四目录对应: api ↔ constants ↔ components ↔ views
- 必用 SmartEnum (禁止普通常量)
- 必用模板创建文件

### 后端关键点
- Controller 必返回 `ResponseDTO<T>`
- `@Transactional(rollbackFor = Exception.class)`
- 必用 ErrorCode 枚举 (禁止硬编码)
- 必用模板创建文件

## 🎯 开发流程
1. 读对应的 CODING_STANDARDS.md
2. 复制 .templates/ 下的模板
3. 替换占位符
4. 检查 @Author/@Copyright/@author

## 💡 记住
开发任何功能前，先看对应的文档，别凭感觉写代码！
