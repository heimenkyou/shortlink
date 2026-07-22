# 开发环境配置

开发配置可能包含数据库、Redis 和管理令牌等敏感信息，因此真实的 `application-dev.yaml` 与 `shardingsphere-config-dev.yaml` 不提交到 Git。

首次拉取项目后，将各模块的模板复制为真实开发配置：

```powershell
Copy-Item gateway/src/main/resources/application-dev.example.yaml gateway/src/main/resources/application-dev.yaml
Copy-Item admin/src/main/resources/application-dev.example.yaml admin/src/main/resources/application-dev.yaml
Copy-Item admin/src/main/resources/shardingsphere-config-dev.example.yaml admin/src/main/resources/shardingsphere-config-dev.yaml
Copy-Item project/src/main/resources/application-dev.example.yaml project/src/main/resources/application-dev.yaml
Copy-Item project/src/main/resources/shardingsphere-config-dev.example.yaml project/src/main/resources/shardingsphere-config-dev.yaml
```

复制后按本地环境填写密码、地址和加密密钥。模板只维护配置结构和非敏感默认值；新增开发配置项时，应同步修改对应模板。
