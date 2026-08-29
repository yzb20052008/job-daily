# 日结零工招工系统（小蓝零工 / Job-Daily）

[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-8+-orange.svg)](Job-Daily-Admin)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7-brightgreen.svg)](Job-Daily-Admin)
[![UniApp](https://img.shields.io/badge/UniApp-微信小程序-green.svg)](Job-Daily-Mobile)
[![Vue](https://img.shields.io/badge/Vue-2.7-42b983.svg)](Job-Daily-Web)

面向**日结 / 零工**场景的招工撮合开源系统（非传统简历招聘站）。提供 **UniApp 移动端 + Vue 管理端 + Java 后端** 三端源码，覆盖发岗、报名、打卡、工资结算、评价与提现等用工闭环，支持私有部署与二次开发。

> 开源文档入口：**[docs/开源/README.md](docs/开源/README.md)**  
> 英文简介：[README.en.md](README.en.md)

---

## 为什么用本项目

| 能力 | 说明 |
|------|------|
| 双角色 | 工人（member）/ 老板（company），移动端可切换 |
| 用工闭环 | 发岗 → 报名 → 确认 → 打卡 → 结算 → 评价 → 提现 |
| 资金能力 | 微信/支付宝结算回调、余额、提现审核与商家转账、对账任务 |
| 可运营 | 岗位/认证审核、运营工作台、消息模板、定时任务告警 |
| 可交付 | 数据库 baseline / upgrade / 菜单 / Quartz / Demo 种子脚本 |

---

## 工程结构

```text
daily/
├── Job-Daily-Admin/     # 后端（JeecgBoot + qkl-module-job）
├── Job-Daily-Web/       # 管理端（Vue2 + Ant Design Vue）
├── Job-Daily-Mobile/    # 移动端（UniApp + uView）
├── docs/                # 产品改造说明 + 开源正式文档
├── LICENSE              # Apache License 2.0
├── NOTICE               # 第三方与免责声明
├── SECURITY.md          # 安全政策
└── CONTRIBUTING.md      # 贡献入口
```

| 目录 | 说明 |
|------|------|
| `Job-Daily-Admin` | API、业务域、支付、Quartz、SQL（`db/`） |
| `Job-Daily-Web` | 运营后台 |
| `Job-Daily-Mobile` | 微信小程序为主，可扩展 App/H5 |
| `docs/开源/` | **开源正式文档（部署/架构/功能/安全/FAQ）** |
| `docs/` 根下其余 | 历史改造与里程碑说明（M1/M2 等） |

---

## 技术栈

| 端 | 技术 |
|----|------|
| 后端 | Java 8+、Spring Boot 2.7、JeecgBoot 3.5.x、MyBatis-Plus、Shiro、Redis、MySQL 5.7+ |
| 管理端 | Vue 2.7、Ant Design Vue 1.x、Vue CLI |
| 移动端 | UniApp、uView、微信小程序 |

---

## 快速开始

1. 准备：JDK 8+、Maven、MySQL 5.7+、Redis、Node.js 14+（管理端）、HBuilderX（移动端）
2. 数据库：按 [Job-Daily-Admin/db/README.md](Job-Daily-Admin/db/README.md) 执行脚本
3. 后端配置：参考 `application-dev.yml.example`，**勿提交真实密钥**
4. 启动后端主类：`JeecgSystemApplication`
5. 管理端：`cd Job-Daily-Web && npm i && npm run serve`
6. 移动端：配置 `Job-Daily-Mobile/config/baseUrl.js` 后用 HBuilderX 运行到微信开发者工具

逐步说明见：[docs/开源/04-快速开始.md](docs/开源/04-快速开始.md)  
生产部署见：[docs/开源/05-部署指南.md](docs/开源/05-部署指南.md)

---

## 文档导航

| 文档 | 内容 |
|------|------|
| [开源文档索引](docs/开源/README.md) | 全套开源文档目录 |
| [产品介绍](docs/开源/01-产品介绍.md) | 定位、角色、边界 |
| [架构说明](docs/开源/02-架构说明.md) | 模块与调用关系 |
| [功能清单](docs/开源/03-功能清单.md) | 功能对照表 |
| [配置说明](docs/开源/06-配置说明.md) | 微信/支付/OSS 等 |
| [业务说明](docs/开源/07-业务说明.md) | 订单状态机与资金流 |
| [安全合规](docs/开源/08-安全合规.md) | 上线安全清单 |
| [商业支持](docs/开源/10-商业支持.md) | 定制/部署/升级合作 |
| [FAQ](docs/开源/11-FAQ.md) | 常见问题 |

---

## 演示与截图

建议自行部署后体验。可将截图放在 `docs/开源/images/`（相对路径引用），避免大量外链图片影响托管平台合规扫描。

可选：执行 `Job-Daily-Admin/db/demo/` 下种子脚本生成体验岗位数据（**仅测试库**）。

---

## 开源协议

本项目以 **Apache License 2.0** 开源，详见 [LICENSE](LICENSE) 与 [NOTICE](NOTICE)。

在遵守许可证的前提下，你可以：

- 私有部署、学习、二次开发
- 基于本项目提供商业服务（部署、定制、运维）

请注意：

- 二次分发请保留许可证与 NOTICE
- **更换自有品牌**，避免与「小蓝零工」官方运营混淆
- 支付、劳务、个人信息等合规责任由**部署运营方**自行承担

---

## 商业支持

开源版便于验证与自建。若需要品牌换皮、专属部署、资金增强、区域运营定制等，请阅读：

→ [docs/开源/10-商业支持.md](docs/开源/10-商业支持.md)

合作沟通请通过代码托管平台 Issue（标注 `[商务]`）或文档中预留的官方渠道，**请勿在 README 堆砌私人联系方式**。

---

## 贡献与安全

- 贡献：[CONTRIBUTING.md](CONTRIBUTING.md)
- 安全漏洞：[SECURITY.md](SECURITY.md)

---

## 致谢

- 基于 [JeecgBoot](https://github.com/jeecgboot/jeecg-boot) 等优秀开源组件构建
- 感谢所有提交 Issue / PR 的贡献者

---

## 免责声明

软件按「现状」提供，不含任何明示或暗示担保。作者与贡献者不对因使用本软件导致的资金损失、数据损坏、监管处罚或商业纠纷承担责任。生产环境请完成充分测试、安全加固与合法资质申请后再上线。
