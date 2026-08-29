-- 启用并校准 OrderJob：待确认超时 + 待开工过期取消 + 待评价超时完结
-- status: 0=启动, -1=停止

UPDATE `sys_quartz_job`
SET `status` = 0,
    `cron_expression` = IFNULL(NULLIF(TRIM(`cron_expression`), ''), '0 0/5 * * * ?'),
    `description` = '订单自动清理：待确认超时取消 / 待开工过期取消 / 待评价超时完结',
    `update_by` = 'admin',
    `update_time` = NOW()
WHERE `job_class_name` = 'org.jeecg.modules.job.quartz.OrderJob';

INSERT INTO `sys_quartz_job` (
  `id`, `create_by`, `create_time`, `del_flag`, `update_by`, `update_time`,
  `job_class_name`, `cron_expression`, `parameter`, `description`, `status`
)
SELECT
  LEFT(REPLACE(UUID(),'-',''), 32), 'admin', NOW(), 0, NULL, NULL,
  'org.jeecg.modules.job.quartz.OrderJob',
  '0 0/5 * * * ?',
  NULL,
  '订单自动清理：待确认超时取消 / 待开工过期取消 / 待评价超时完结',
  0
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_quartz_job`
  WHERE `job_class_name` = 'org.jeecg.modules.job.quartz.OrderJob'
);

-- 开关（若未执行过 V20260829_03）
INSERT INTO `base_config` (`id`,`config_name`,`config_code`,`config_value`,`config_flag`,`group_code`,`remark`,`sort`,`status`,`create_by`,`create_time`,`del_flag`)
SELECT LEFT(REPLACE(UUID(),'-',''), 32),'待开工过期自动取消','order_no_start_after_end','1',1,'job','1=开启：待开工且 end_time 已过则自动取消；0=关闭',12,'1','admin',NOW(),0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `base_config` WHERE `config_code`='order_no_start_after_end');

-- 预览仍卡住的逾期待开工（执行清理前可先看）
-- SELECT id, order_sn, order_status, start_time, end_time
-- FROM job_order
-- WHERE del_flag=0 AND order_status='1' AND end_time IS NOT NULL AND end_time<>'' AND end_time < NOW();
