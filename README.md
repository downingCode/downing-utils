# SaaS 
[![SonarQube](http://192.168.1.7:9000/api/project_badges/measure?project=com.cdoboe.cloud%3Asaas-cloud-cdoboe&metric=alert_status)](http://192.168.1.7:9000)

## 工程结构
```$xslt
SaaS
├── doc [文档]
├── saas-cloud-cdoboe-auth [鉴权服务]
├── saas-cloud-cdoboe-gateway [路由服务]
├── saas-cloud-cdoboe-common
        ├── saas-cloud-cdoboe-common-core [基础组件]
        ├── saas-cloud-cdoboe-common-launch [启动组件]
        ├── saas-cloud-cdoboe-common-log [日志组件]
        ├── saas-cloud-cdoboe-common-elasticsearch [es组件]
        └── saas-cloud-cdoboe-common-wxpay [wxpay组件]
├── saas-cloud-cdoboe-basis [基础服务]
├── saas-cloud-cdoboe-user [用户服务]
├── saas-cloud-cdoboe-goods [商品服务]
├── ...[业务服务]
```

## 架构

## 技术文档

- [SaaS系统搭建](/doc/SaaS系统搭建.md)

- [快速开发](/doc/快速开发.md)

- [路由映射](/doc/路由映射.md)

- [elasticsearch组件操作](/doc/elasticsearch.md)

- 业务文档。。。

- [部署操作](/doc/部署操作.md)

## 版本
| 版本 |版本号 | 说明 |
| ----- | --------- | ----------- |
| SaaS |1.0.0 | init |