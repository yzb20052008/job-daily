# Job-Daily (Xiaolan Gig Work) — Daily-Wage Hiring Platform

[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)

Open-source **daily-wage / gig hiring** platform (not a traditional resume job board). Includes **UniApp mini program**, **Vue admin**, and **Java backend**.

**Post → apply → confirm → clock-in → payroll → review → withdraw**

Chinese README (full): **[README.md](README.md)** · Docs: [docs/开源](docs/开源/README.md)

---

## Online demo

| Item | Detail |
|------|--------|
| Mini Program | Scan QR in WeChat |
| Admin | [http://lg.qinkonglan.cn/](http://lg.qinkonglan.cn/) — `test` / `test@123` (demo only; may reset) |
| Support | Open a platform **Issue** (name the app: Admin / Web / Mobile) |

<p align="center">
  <img src="docs/开源/images/mobile-mp-qrcode.jpg" alt="Mini Program QR" width="200" />
</p>

| Home | Post detail | Worker orders | Admin posts |
|:----:|:----:|:----:|:----:|
| ![home](docs/开源/images/mobile-home.jpg) | ![post](docs/开源/images/mobile-post-detail.jpg) | ![orders](docs/开源/images/mobile-order-worker.jpg) | ![admin](docs/开源/images/admin-post-list.png) |

---

## Why this project

| Capability | Notes |
|------------|--------|
| Dual roles | Worker (`member`) / Employer (`company`) |
| Closed loop | Hiring through payroll and withdrawal |
| Money path | Pay callbacks, balance, withdraw audit, merchant transfer |
| Ops | Post/auth review, ops desk, CMS, jobs |
| Deliverable | Clean SQL init; secrets stay in local `*.yml` / `baseUrl.js` (gitignored) |

---

## Layout

| Path | Role |
|------|------|
| `Job-Daily-Admin` | JeecgBoot + `qkl-module-job`, `/api/*`, pay, Quartz |
| `Job-Daily-Web` | Vue 2 admin |
| `Job-Daily-Mobile` | UniApp (WeChat Mini Program first) |
| `Job-Daily-Admin/db/00_job_daily_clean.sql` | Sole DB bootstrap script |

**Stack:** Java 8/11, Spring Boot 2.7, JeecgBoot 3.5.x, Vue 2.7 + Ant Design Vue, UniApp + uView.

---

## Features (summary)

- Auth, role switch, real-name / company verification  
- Post, apply, confirm/cancel, headcount limits, timeout cancel, clock-in rules, mutual reviews  
- WeChat pay (Alipay where the client allows), idempotent credit, wallet, withdraw + transfer confirm  
- CMS, inbox, points/VIP, scheduled reconciliation  

**Order states:** `0 pending confirm → 1 pending start → 2 working → 3 pending pay → 4 pending review → 5 done` (also `6 cancelled`).

---

## Quick start

1. MySQL (utf8mb4) + Redis; import `Job-Daily-Admin/db/00_job_daily_clean.sql`  
2. Copy `application-dev.yml.example` → `application-dev.yml`; run `JeecgSystemApplication`  
3. Admin: copy `.env.development.example` → `.env.development`, then `npm i && npm run serve` (`admin` / `123456` — change in production)  
4. Mobile: copy `config/baseUrl.js.example` → `baseUrl.js`; run with HBuilderX → WeChat DevTools  

Smoke: approve a post → apply → confirm → clock → settle (or stop at pending pay without merchant) → review.

Never commit real keys or production hosts.

---

## Commercial support

Deploy, rebrand, payment integration, custom features, ops SLA.

- Site: [qingkonglan.com](https://www.qingkonglan.com/)  
- Email: `941060982@qq.com`  
- Issues tagged `[商务]` welcome  

Details (Chinese): [docs/开源/10-商业支持.md](docs/开源/10-商业支持.md)

---

## License & disclaimer

Apache License 2.0 — [LICENSE](LICENSE), [NOTICE](NOTICE).

Software is provided **as is**. Operators are responsible for payment onboarding, privacy, and labor compliance. No warranty for fund loss, data damage, or regulatory issues.
