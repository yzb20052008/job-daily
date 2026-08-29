# 演示 / 体验数据脚本

| 文件 | 用途 |
|------|------|
| `01_clean_biz_data.sql` | 清理业务流水（订单/打卡/评价/积分流水/提现等），**保留**岗位、工种、企业、道具商品、配置、用户 |
| `02_seed_demo_posts_100.sql` | 插入 100 条体验招工岗（全国主流城市，**赣州 10 条**） |
| `_gen_seed_posts.py` | 生成 `02` 的脚本，一般无需再跑 |

## 执行顺序（MySQL 5.7+）

```bash
# 可选：先备份
mysqldump -uroot -p job-daily > backup_before_demo.sql

# 1) 清业务流水（不删岗位）
mysql -uroot -p --default-character-set=utf8mb4 job-daily < Job-Daily-Admin/db/demo/01_clean_biz_data.sql

# 2) 灌体验岗位
mysql -uroot -p --default-character-set=utf8mb4 job-daily < Job-Daily-Admin/db/demo/02_seed_demo_posts_100.sql
```

## 说明

- 体验岗 `remark` 形如 `体验数据-demo-001`，可按此前缀删除重灌。
- 发布人默认 `user_id=5684103397994088016`（库内已有老板账号）；若环境无此用户，请改 SQL 中的 `user_id`。
- `01` 会将 `ums_account` 余额/积分归零，但**不删**账户行与 `sys_user`。
- `cms_notice` **只删私有站内信**（`if_public=0`），系统公告（`if_public=1`）保留。
