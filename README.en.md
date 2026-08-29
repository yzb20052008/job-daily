# Job-Daily (Xiaolan Gig Work) — Daily-Wage Hiring Platform

[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)

Open-source **daily-wage / gig hiring** platform (not a traditional resume job board). Includes **UniApp mini program**, **Vue admin**, and **Java backend**, covering posting, apply, clock-in, payroll, reviews, and withdrawals. Suitable for private deployment and secondary development.

Full Chinese documentation: **[docs/开源/README.md](docs/开源/README.md)**

## Repositories

| Path | Description |
|------|-------------|
| `Job-Daily-Admin` | Backend (JeecgBoot + `qkl-module-job`) |
| `Job-Daily-Web` | Admin UI (Vue2) |
| `Job-Daily-Mobile` | Mobile (UniApp) |
| `docs/开源/` | Official open-source docs (deploy, architecture, security) |

## Quick start

1. MySQL + Redis; run scripts under `Job-Daily-Admin/db/` (see `db/README.md`)
2. Configure backend via `application-*.yml.example` — **never commit secrets**
3. Start `JeecgSystemApplication`
4. Admin: `npm run serve` in `Job-Daily-Web`
5. Mobile: set `baseUrl` in `Job-Daily-Mobile/config/baseUrl.js`

## License

Apache License 2.0 — see [LICENSE](LICENSE) and [NOTICE](NOTICE).

Operators are responsible for payment merchant onboarding, data privacy, and labor compliance.

## Commercial support

See [docs/开源/10-商业支持.md](docs/开源/10-商业支持.md). Prefer platform Issues tagged `[商务]` for partnership inquiries.
