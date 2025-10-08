# SmartAdmin 数据库配置信息

## 数据库连接信息

**配置文件位置**: `smart-admin-api-java17-springboot3/sa-base/src/main/resources/dev/sa-base.yaml`

**数据库配置**:
```yaml
Host: 127.0.0.1
Port: 3306
Database: smart_admin_v3
Username: root
Password: SmartAdmin666
Driver: com.p6spy.engine.spy.P6SpyDriver (使用P6Spy监控SQL)
实际驱动: MySQL JDBC Driver
```

**连接URL**:
```
jdbc:p6spy:mysql://127.0.0.1:3306/smart_admin_v3?autoReconnect=true&useServerPreparedStmts=false&rewriteBatchedStatements=true&characterEncoding=UTF-8&useSSL=false&allowMultiQueries=true&serverTimezone=Asia/Shanghai
```

**连接池配置**:
- initial-size: 2
- min-idle: 2
- max-active: 10
- max-wait: 60000ms