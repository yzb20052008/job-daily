# 数据库脚本说明

| 目录 | 用途 |
|------|------|
| `baseline/` | 现行核心业务表建表（新环境） |
| `upgrade/` | 增量升级（旧库对齐） |
| `quartz/` | 业务定时任务注册 |
| `menu/` | 后台菜单：`01` 对齐、`02` 工作台、`03` 消息模板、`04` 基础配置按钮权限补齐 |
| `demo/` | 业务数据清理 + 体验岗位种子（见目录内 README） |
| `qkl-job.sql` | 历史全量脚本（含旧 rms/wms，**勿作为现行唯一依据**） |

推荐执行顺序：

1. `baseline/01_job_core_tables.sql`（新库）或 `upgrade/*`（旧库）
2. `quartz/01_biz_jobs.sql`
3. `menu/01_align_job_menus.sql`
4. `menu/02_ops_workbench_menu.sql`（运营工作台）
5. `upgrade/V20260818_03_ums_balance_recharge.sql`（余额充值单，若需要）
6. `upgrade/V20260818_04_rule_msg_template.sql` + `menu/03_msg_template_menu.sql`（W5 规则/消息）
7. `upgrade/V20260829_01_job_alert_webhook.sql`（W6 任务告警配置位）
8. `upgrade/V20260829_02_p2_base_config.sql`（打卡半径/评价超时）
9. `upgrade/V20260829_03_order_no_start_after_end.sql`（待开工过期自动取消开关）
10. `upgrade/V20260829_04_map_config.sql`（腾讯地图 map_key / map_sk）
11. `menu/04_base_config_button_perms.sql`（基础配置编辑等按钮权限补齐）
12. 管理后台启动定时任务；重新登录刷新菜单

详见：

- 开源正式文档：`docs/开源/README.md`（快速开始 / 部署 / 配置）
- 改造里程碑：`docs/M1改造说明.md`、`docs/M2改造说明.md`、`docs/M1-P0P1补强说明.md`

增量脚本补充：`upgrade/V20260818_03_ums_balance_recharge.sql`（余额充值单）。
