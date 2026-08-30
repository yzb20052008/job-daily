# 数据库脚本

开源初始化**只保留一个文件**：

| 文件 | 说明 |
|------|------|
| `00_job_daily_clean.sql` | 干净全库（结构 + 权限/字典/工种/配置种子 + 代码依赖补充表；无用户隐私与业务流水） |

辅助脚本（可选）：

| 文件 | 说明 |
|------|------|
| `demo/_clean_job_daily_dump.py` | 从本地 `job-daily.sql`（gitignore）重新生成干净库 |

## 使用

```bash
mysql -uroot -p -e "CREATE DATABASE \`job-daily\` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;"
mysql -uroot -p --default-character-set=utf8mb4 job-daily < Job-Daily-Admin/db/00_job_daily_clean.sql
```

后台登录：`admin` / `123456`（上线后立即改密）。

在「基础配置」填写 `map_key`（腾讯地图 WebService Key，控制台勿开启签名校验），在「消息模板」填写微信订阅模板 ID。

