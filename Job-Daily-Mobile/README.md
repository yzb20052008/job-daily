# Job-Daily-Mobile（小蓝零工 · 移动端）

日结零工招工系统的 **UniApp 移动端**，工人 / 老板双角色，覆盖找岗、发岗、报名、打卡、结算、钱包提现与消息。

> 仓库总览与完整文档：[仓库根 README](../README.md) · [开源文档](../docs/开源/README.md)

---

## 技术栈

| 项 | 说明 |
|----|------|
| 框架 | UniApp（Vue2） |
| UI | uView 2.x（`uni_modules/uview-ui`） |
| 状态 | Vuex（`store/`） |
| 主端 | 微信小程序（可扩展 App / H5 / 支付宝小程序） |
| 后端 | 对接 `Job-Daily-Admin` 的 `/api/*` |

---

## 功能概览

| 模块 | 目录 | 说明 |
|------|------|------|
| 首页 / 启动 | `pages/index` | 闪屏拉配置、附近岗位、工人/老板首页 |
| 岗位 | `pages/job` | 详情、发岗/改岗、认证、收藏、举报 |
| 订单 | `pages/order` | 订单列表/详情、工人管理、结算与评价 |
| 资金 | `pages/finance` | 钱包、收支、提现与记录 |
| 消息 | `pages/message` | 系统/订单/资金等消息（按角色隔离） |
| 我的 | `pages/my` | 资料、积分、名片等 |
| 简历 | `pages/resume` | 工人简历相关 |
| 登录账号 | `pages/login`、`pages/user` | 登录注册、协议、设置、关于 |
| 邀请 / VIP 等 | `pages/invite`、`pages/vip`、`pages/integral` | 运营增值能力 |

界面截图见：[docs/开源/images](../docs/开源/images/README.md)

### 在线体验

微信扫码体验小程序：

![体验小程序](../docs/开源/images/mobile-mp-qrcode.jpg)

管理后台：[http://lg.qinkonglan.cn/](http://lg.qinkonglan.cn/)（`test` / `test@123`）。技术问题请提交 **Issue**。

---

## 本地运行

### 前置条件

1. 已启动后端 `Job-Daily-Admin`（默认如 `http://127.0.0.1:8081`）
2. 安装 [HBuilderX](https://www.dcloud.io/hbuilderx.html) 与微信开发者工具
3. 自备微信小程序 AppId（`manifest.json`）

### 配置

```text
copy config/baseUrl.js.example  →  config/baseUrl.js
```

在 `baseUrl.js` 中填写：

- **开发**：本机或局域网后端地址（真机调试勿用 `127.0.0.1`，改用电脑局域网 IP）
- **生产**：HTTPS API 域名与分享落地页 `shareUrl`
- 地图 Key：后台 `base_config.map_key`（勿把正式 Key 写死进仓库）

`baseUrl.js` 已 gitignore，**不要提交真实域名**。说明见 [配置说明](../docs/开源/06-配置说明.md)。

### 运行步骤

1. HBuilderX 打开本目录 `Job-Daily-Mobile`
2. 运行 → 微信开发者工具
3. 在开发者工具中配置合法 AppId、关闭域名校验（仅本地调试）

逐步说明：[快速开始 · 启动移动端](../docs/开源/04-快速开始.md)

---

## 目录结构

```text
Job-Daily-Mobile/
├── config/
│   ├── baseUrl.js.example   # API 地址模板（复制为 baseUrl.js）
│   ├── api.js               # 接口路径
│   ├── request.js           # 请求封装（Token 等）
│   ├── login.js / pay.js    # 登录、支付
│   └── common.js            # 定位、时间格式、通用工具
├── pages/                   # 业务页面（见上表）
├── components/              # 业务组件
├── store/                   # Vuex
├── static/                  # 静态资源
├── uni_modules/             # uView 等
├── App.vue / main.js
├── pages.json               # 路由与 tabBar
└── manifest.json            # 应用名、AppId、各端能力
```

---

## 联调注意

| 点 | 说明 |
|----|------|
| 双角色 | `role_code`：工人 `member` / 老板 `company`，首页与订单视图不同 |
| 支付 | 微信小程序侧以微信支付为主；支付宝能力受端限制 |
| 定位 | 首页城市与打卡坐标；正式地图服务走后端代理 |
| 提现 | 商家转账可能需用户在小程序内确认收款 |
| 脱敏 | 发布前对照 [开源脱敏清单](../docs/开源/13-开源脱敏清单.md) |

业务状态机与资金流：[业务说明](../docs/开源/07-业务说明.md)

---

## 发布

1. 生产 `baseUrl` / `shareUrl` 改为自有 HTTPS 域名  
2. `manifest.json` 填写正式小程序 AppId  
3. HBuilderX → 发行 → 微信小程序 → 上传审核  

详见 [部署指南](../docs/开源/05-部署指南.md)

---

## 协议与商业支持

- 许可证：与仓库根目录一致，[Apache License 2.0](../LICENSE)
- 商业合作：[商业支持](../docs/开源/10-商业支持.md) · [官网](https://www.qingkonglan.com/)
