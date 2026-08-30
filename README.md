# 日结零工招工系统（小蓝零工 / Job-Daily）

[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-8+-orange.svg)](Job-Daily-Admin)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7-brightgreen.svg)](Job-Daily-Admin)
[![UniApp](https://img.shields.io/badge/UniApp-微信小程序-green.svg)](Job-Daily-Mobile)
[![Vue](https://img.shields.io/badge/Vue-2.7-42b983.svg)](Job-Daily-Web)

面向**日结 / 零工**场景的招工撮合开源系统（**不是**传统简历招聘站）。提供 **UniApp 移动端 + Vue 管理端 + Java 后端** 三端源码，覆盖：

**发岗 → 报名 → 确认 → 打卡 → 结算 → 评价 → 提现**

适合私有部署、区域运营商换皮、二次开发学习。英文简介：[README.en.md](README.en.md)

---

## 在线体验

| 渠道 | 说明 |
|------|------|
| 体验小程序 | 微信扫下方二维码（演示环境，以线上为准） |
| 管理后台 | [http://lg.qinkonglan.cn/](http://lg.qinkonglan.cn/) |
| 演示账号 | 用户名 `test`，密码 `test@123`（仅体验，可能定期重置） |
| 技术问题 | 请在代码托管平台提交 **Issue**（标明 Admin / Web / Mobile 与复现步骤） |

<p align="center">
  <img src="docs/开源/images/mobile-mp-qrcode.jpg" alt="体验小程序二维码" width="220" />
</p>

| 首页 | 职位详情 | 工人订单 | 管理端招工 |
|:----:|:----:|:----:|:----:|
| ![首页](docs/开源/images/mobile-home.jpg) | ![详情](docs/开源/images/mobile-post-detail.jpg) | ![订单](docs/开源/images/mobile-order-worker.jpg) | ![招工](docs/开源/images/admin-post-list.png) |

| 消息 | 我的 | 老板订单 | 基础配置 |
|:----:|:----:|:----:|:----:|
| ![消息](docs/开源/images/mobile-message.jpg) | ![我的](docs/开源/images/mobile-mine-worker.jpg) | ![老板订单](docs/开源/images/mobile-order-boss.jpg) | ![配置](docs/开源/images/admin-base-config.png) |

更多截图：[docs/开源/images/README.md](docs/开源/images/README.md)

---

## 为什么用本项目

| 能力 | 说明 |
|------|------|
| 双角色 | 工人 `member` / 老板 `company`，移动端可切换 |
| 用工闭环 | 发岗、报名、确认、打卡、结算、评价、提现主路径可跑通 |
| 资金能力 | 微信/支付宝结算回调、余额流水、提现审核与商家转账、对账任务 |
| 可运营 | 岗位/认证审核、运营工作台、CMS、消息、定时任务 |
| 可交付 | 干净全库 SQL 一键初始化；配置用 `*.example`，密钥不进仓库 |

**定位澄清**：侧重日结短单与线上留痕结算；不是长周期简历招聘、猎头 ATS 或纯信息黄页。

---

## 工程结构

```text
daily/
├── Job-Daily-Admin/     # 后端（JeecgBoot + qkl-module-job）
│   └── db/              # 00_job_daily_clean.sql 唯一初始化脚本
├── Job-Daily-Web/       # 管理端（Vue2 + Ant Design Vue）
├── Job-Daily-Mobile/    # 移动端（UniApp + uView，微信小程序为主）
├── docs/开源/           # 部署 / 架构 / 安全等展开说明
├── LICENSE / NOTICE / SECURITY.md / CONTRIBUTING.md
└── README.md            # 本文件
```

| 目录 | 职责 |
|------|------|
| `Job-Daily-Admin` | `/api/*` 业务、支付回调、Quartz、权限与字典 |
| `Job-Daily-Web` | 运营审核、用户/订单/配置、工作台 |
| `Job-Daily-Mobile` | C 端获客与完整用工动作 |

---

## 技术栈

| 端 | 技术 |
|----|------|
| 后端 | Java 8/11、Spring Boot 2.7、JeecgBoot 3.5.x、MyBatis-Plus、Shiro、Redis、MySQL 5.7+ |
| 管理端 | Vue 2.7、Ant Design Vue 1.x、Vue CLI |
| 移动端 | UniApp、uView 2.x、微信小程序（可扩展 App/H5） |

外部依赖（按需开通）：微信登录/支付/商家转账、支付宝（受端能力限制）、OSS、地图（Key 配后台 `base_config`）。

---

## 功能一览

### 账号与认证

登录注册（含微信）、工人/老板角色切换、实名认证、企业认证（后台审核）。

### 招工与订单

岗位发布/编辑/审核/下架、报名、老板确认或取消、接单人数防超招、待确认超时取消、上下班打卡（图片/距离/时间窗可配）、双方评价。

### 资金与财务

工资支付（微信为主；支付宝视端能力）、回调验额与幂等入账、工人余额与流水、提现申请冻结、后台审核与商家转账查单回写、日终对账、运营待办聚合。

### 运营与增长

公告/广告/资讯、站内消息、消息模板、积分/道具/签到、VIP、定时任务监控告警。

### 订单状态（用工单）

```text
0 待确认 → 1 待开工 → 2 工作中 → 3 待结算 → 4 待评价 → 5 已完成
                ↘ 6 取消（超时/双方取消等）
```

合法迁移由服务端状态机约束；客户端应走动作接口（确认/取消/打卡等），勿随意改状态。

### 用工主流程

```text
老板发岗（待审 → 招工中）
  → 工人报名（待确认）
  → 老板确认（待开工）/ 取消
  → 上班打卡（工作中）→ 下班打卡（待结算）
  → 老板支付（回调成功 → 待评价，工人余额增加）
  → 双方评价（已完成）
  → 工人提现（冻结 → 审核 → 转账 → 用户确认 → 成功）
```

---

## 快速开始

### 环境

| 组件 | 建议 |
|------|------|
| JDK | 8 或 11 |
| Maven | 3.6+ |
| MySQL | 5.7+ / 8.0（utf8mb4） |
| Redis | 5.0+ |
| Node.js | 14.x～16.x（管理端） |
| HBuilderX + 微信开发者工具 | 移动端 |

### 1. 数据库

创建库后导入唯一初始化脚本：

```bash
mysql -uroot -p --default-character-set=utf8mb4 job-daily < Job-Daily-Admin/db/00_job_daily_clean.sql
```

本地后台默认：`admin` / `123456`（**生产必须改密**）。脚本说明：[Job-Daily-Admin/db/README.md](Job-Daily-Admin/db/README.md)

### 2. 后端

```text
application-dev.yml.example  →  application-dev.yml
```

路径在 `Job-Daily-Admin/qkl-module-system/qkl-system-start/src/main/resources/`。填写数据源、Redis 等；确认 `spring.profiles.active=dev`。

IDE 运行主类：`org.jeecg.JeecgSystemApplication`（或 Maven 打包后启动）。端口以配置为准（常见 8080/8081）。

### 3. 管理端

```bash
cd Job-Daily-Web
cp .env.development.example .env.development   # Windows 可用 copy
npm install
npm run serve
```

用 `admin` / `123456` 登录；改权限后请重新登录刷新菜单。

### 4. 移动端

```text
Job-Daily-Mobile/config/baseUrl.js.example  →  baseUrl.js
```

填写后端地址（真机调试用局域网 IP，勿写死生产密钥）。HBuilderX 打开目录 → 运行到微信开发者工具；`manifest.json` 配置自有 AppId。地图 Key 走后台配置。

### 5. 最小冒烟

1. 后台审核通过一个岗位  
2. 工人报名 → 老板确认  
3. 上下班打卡  
4. 老板结算（无商户可先验证到「待结算」）  
5. 评价 / 提现（依赖支付与转账配置）

**配置约定**：真实密钥、域名放在各工程本地文件（与 `*.example` 同目录），已 gitignore；勿提交公开仓库。可选备份见 `private/README.md`。

---

## 配置要点（摘要）

| 项 | 位置 | 说明 |
|----|------|------|
| 数据源 / Redis | `application-*.yml` | 本地复制 example |
| 微信小程序 / 支付 | 后端 yml + `wxpay*.properties`（example） | 回调 URL 须公网 HTTPS |
| 支付宝 | 后端配置 | 微信小程序内支付宝能力受限 |
| OSS / 上传 | Jeecg `jeecg.oss.*` 等 | 勿在前端写死 OSS 密钥 |
| 移动端 API | `baseUrl.js` | 已忽略跟踪 |
| 地图 | 后台 `base_config.map_key` | 正式 Key 不要进仓库 |

生产部署检查项、HTTPS、回调与定时任务启用等，见 [docs/开源/05-部署指南.md](docs/开源/05-部署指南.md)、[06-配置说明.md](docs/开源/06-配置说明.md)。

---

## 架构关系（简图）

```text
┌─────────────────┐  ┌─────────────────┐  ┌──────────────────┐
│ Job-Daily-Mobile│  │  Job-Daily-Web  │  │ 微信 / 支付 / 地图 │
│   UniApp 小程序  │  │   Vue 管理端     │  │                  │
└────────┬────────┘  └────────┬────────┘  └────────┬─────────┘
         │ /api/*              │ /sys|job|ums…      │ 回调 / SDK
         └──────────┬─────────┴────────────────────┘
                    ▼
         ┌─────────────────────┐
         │   Job-Daily-Admin   │
         │ Jeecg + qkl-module-job │
         └──────────┬──────────┘
                    ▼
              MySQL + Redis
```

原则：**服务端为准**（订单状态、金额、提现）；支付/转账回调须幂等；管理端走权限码，移动端校验订单当事人。

---

## 常见问题（摘要）

| 问题 | 简答 |
|------|------|
| 和 BOSS 直聘一类产品区别？ | 本项目侧重日结短单、打卡与线上结算，不是长周期简历招聘。 |
| 没有支付商户能演示吗？ | 可演示到待结算；真实入账需配置支付或沙箱。 |
| 提现一直「待用户确认」？ | 新版商家转账常需用户在小程序确认收款，可查单回写。 |
| 可以商用 / 卖源码吗？ | Apache-2.0 允许使用与分发，须保留声明；勿虚假宣称「官方唯一」。 |
| Java 17？ | 面向 8/11；17+ 需自行验证。 |
| Redis 能不做？ | 不建议；登录与防重等依赖 Redis。 |

更多：[docs/开源/11-FAQ.md](docs/开源/11-FAQ.md)

---

## 开源协议

本项目以 **Apache License 2.0** 开源，见 [LICENSE](LICENSE)、[NOTICE](NOTICE)。

你可以：私有部署、学习、二次开发；基于本项目提供商业服务（部署、定制、运维）。

请注意：二次分发保留许可证与 NOTICE；**更换自有品牌**，避免与「小蓝零工」官方运营混淆；支付、劳务、个人信息等合规由**部署运营方**自行承担。

---

## 商业支持

开源版便于验证与自建。若需要可签约交付，可合作：

| 类型 | 说明 |
|------|------|
| 部署实施 | 环境、HTTPS、回调、菜单与任务、冒烟验收 |
| 品牌换皮 | Logo、名称、主题、小程序装修 |
| 支付联调 | 商户、转账、对账、异常预案 |
| 功能定制 | 工种规则、城市运营、报表、对接 ERP/HR |
| 运维托管 | 监控、备份、应急（另签 SLA） |

- 官网：[https://www.qingkonglan.com/](https://www.qingkonglan.com/)
- 邮箱：`941060982@qq.com`
- 商务沟通也可 Issue 标题加 `[商务]`

详情与边界：[docs/开源/10-商业支持.md](docs/开源/10-商业支持.md)

---

## 贡献与安全

- 缺陷 / 技术问题：提交 **Issue**（标明端与复现步骤）→ [CONTRIBUTING.md](CONTRIBUTING.md)
- 安全漏洞：[SECURITY.md](SECURITY.md)（勿在公开 Issue 贴密钥）

---

## 文档索引（展开阅读）

根 README 已覆盖体验、功能、流程与本地启动。下列文档供深入排查与上线：

| 文档 | 内容 |
|------|------|
| [开源文档索引](docs/开源/README.md) | 全套目录 |
| [产品介绍](docs/开源/01-产品介绍.md) | 范围与非目标 |
| [架构说明](docs/开源/02-架构说明.md) | 模块与依赖 |
| [功能清单](docs/开源/03-功能清单.md) | 端覆盖矩阵 |
| [快速开始](docs/开源/04-快速开始.md) | 卡点排查补充 |
| [部署指南](docs/开源/05-部署指南.md) | 生产检查清单 |
| [配置说明](docs/开源/06-配置说明.md) | 微信/支付/OSS |
| [业务说明](docs/开源/07-业务说明.md) | 状态机与资金细节 |
| [安全合规](docs/开源/08-安全合规.md) | 上线安全 |
| [开源脱敏清单](docs/开源/13-开源脱敏清单.md) | 发布前清理 |

---

## 致谢

- 基于 [JeecgBoot](https://github.com/jeecgboot/jeecg-boot) 等优秀开源组件构建  
- 感谢所有提交 Issue / PR 的贡献者  

---

## 免责声明

软件按「现状」提供，不含任何明示或暗示担保。作者与贡献者不对因使用本软件导致的资金损失、数据损坏、监管处罚或商业纠纷承担责任。生产环境请完成充分测试、安全加固与合法资质申请后再上线。
