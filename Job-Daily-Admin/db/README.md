# 数据库脚本说明

| 目录 | 用途 |
|------|------|
| `baseline/` | 现行核心业务表建表（新环境） |
| `upgrade/` | 增量升级（旧库对齐） |
| `quartz/` | 业务定时任务注册 |
| `menu/` | 后台菜单对齐（job/integral/ums，停用 rms/wms/bms） |
| `qkl-job.sql` | 历史全量脚本（含旧 rms/wms，**勿作为现行唯一依据**） |

推荐执行顺序：

1. `baseline/01_job_core_tables.sql`（新库）或 `upgrade/*`（旧库）
2. `quartz/01_biz_jobs.sql`
3. `menu/01_align_job_menus.sql`
4. 管理后台启动定时任务；重新登录刷新菜单

详见 `docs/M1改造说明.md`。
