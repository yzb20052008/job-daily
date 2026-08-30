# private/ — 可选本地备份（非主配置方式）

**推荐做法**：真实配置与模板放在工程对应目录旁：

| 模板（提交 Git） | 本地真实文件（gitignore） |
|------------------|---------------------------|
| `…/application-dev.yml.example` | `…/application-dev.yml` |
| `…/production/*.properties.example` | `…/production/*.properties` |
| `Job-Daily-Mobile/config/baseUrl.js.example` | `baseUrl.js` |
| `Job-Daily-Web/.env.*.example` | `.env.development` / `.env.production` |

本目录仅作**个人可选备份**（如全量 SQL dump），已被根 `.gitignore` 忽略（仅本 README 可提交）。  
**不要**把所有密钥集中堆在这里当唯一真相源，也勿推送到公开远程。

## 可选内容

| 路径 | 说明 |
|------|------|
| `sql-dumps/` | 历史全量库备份（含业务数据时勿公开） |
| `cert/` | 证书临时存放（正式应放部署机安全路径） |

## 开源发布

见 `docs/开源/13-开源脱敏清单.md`。
