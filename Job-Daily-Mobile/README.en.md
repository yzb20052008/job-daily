# Job-Daily-Mobile (Xiaolan Gig Work — Mobile)

UniApp client for the **daily-wage / gig hiring** platform. Supports worker and employer roles: browse/post jobs, apply, clock-in, payroll, wallet withdrawal, and messaging.

> Monorepo overview: [root README](../README.md) · Docs (Chinese): [docs/开源](../docs/开源/README.md)

## Stack

- UniApp (Vue 2) + uView 2.x
- Vuex store
- Primary target: WeChat Mini Program (App / H5 optional)
- API: `Job-Daily-Admin` `/api/*`

## Quick start

1. Start the Java backend (`Job-Daily-Admin`)
2. Copy `config/baseUrl.js.example` → `config/baseUrl.js` and set API / share URLs  
   (`baseUrl.js` is gitignored — do not commit secrets or production hosts)
3. Open this folder in HBuilderX → Run to WeChat DevTools
4. Set Mini Program AppId in `manifest.json`

Map keys should come from admin `base_config.map_key`, not hard-coded in the repo.

## Layout

| Path | Role |
|------|------|
| `pages/index` | Splash, home (worker / boss) |
| `pages/job` | Post detail, publish, auth |
| `pages/order` | Orders, worker manage, settle, review |
| `pages/finance` | Wallet, withdraw |
| `pages/message` | Messages (role-scoped) |
| `pages/my` / `resume` / `user` | Profile, resume, account |
| `config/` | `api.js`, `request.js`, `login.js`, `pay.js`, `common.js` |

## Docs

- [Quick start](../docs/开源/04-快速开始.md)
- [Config](../docs/开源/06-配置说明.md)
- [Business flows](../docs/开源/07-业务说明.md)
- [Screenshots](../docs/开源/images/README.md)
- [Commercial support](../docs/开源/10-商业支持.md) · [Website](https://www.qingkonglan.com/)

## License

Apache License 2.0 — see [LICENSE](../LICENSE).
