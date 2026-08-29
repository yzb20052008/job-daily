-- ============================================================
-- Quartz 业务任务注册（匹配 job-daily.sys_quartz_job 实际字段）
-- status: 0=启动, -1=停止；新任务默认停止，需后台手动启动
-- ============================================================

INSERT INTO `sys_quartz_job` (
  `id`, `create_by`, `create_time`, `del_flag`, `update_by`, `update_time`,
  `job_class_name`, `cron_expression`, `parameter`, `description`, `status`
)
SELECT
  LEFT(REPLACE(UUID(),'-',''), 32), 'admin', NOW(), 0, NULL, NULL,
  'org.jeecg.modules.job.quartz.FinanceReconJob',
  '0 30 1 * * ?',
  NULL,
  '资金日终对账：提现查单回写 + 结算卡单告警',
  -1
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_quartz_job`
  WHERE `job_class_name` = 'org.jeecg.modules.job.quartz.FinanceReconJob'
);

-- OrderJob / PostJob / GoodsJob 在 job-daily 中通常已存在，此处仅补缺
INSERT INTO `sys_quartz_job` (
  `id`, `create_by`, `create_time`, `del_flag`, `update_by`, `update_time`,
  `job_class_name`, `cron_expression`, `parameter`, `description`, `status`
)
SELECT
  LEFT(REPLACE(UUID(),'-',''), 32), 'admin', NOW(), 0, NULL, NULL,
  'org.jeecg.modules.job.quartz.OrderJob',
  '0 0/5 * * * ?',
  NULL,
  '待确认订单超时自动取消',
  0
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_quartz_job`
  WHERE `job_class_name` = 'org.jeecg.modules.job.quartz.OrderJob'
);

INSERT INTO `sys_quartz_job` (
  `id`, `create_by`, `create_time`, `del_flag`, `update_by`, `update_time`,
  `job_class_name`, `cron_expression`, `parameter`, `description`, `status`
)
SELECT
  LEFT(REPLACE(UUID(),'-',''), 32), 'admin', NOW(), 0, NULL, NULL,
  'org.jeecg.modules.job.quartz.PostJob',
  '0 0/10 * * * ?',
  NULL,
  '招工岗位超时自动下架',
  -1
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_quartz_job`
  WHERE `job_class_name` = 'org.jeecg.modules.job.quartz.PostJob'
);

SELECT job_class_name, cron_expression, status, description
FROM sys_quartz_job
WHERE job_class_name LIKE 'org.jeecg.modules.job.quartz.%';
