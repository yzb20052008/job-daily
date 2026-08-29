-- ============================================================
-- M2-W6：定时任务告警 Webhook 配置位
-- ============================================================

INSERT INTO `base_config` (`id`,`config_name`,`config_code`,`config_value`,`config_flag`,`group_code`,`remark`,`sort`,`status`,`create_by`,`create_time`,`del_flag`)
SELECT '2026082906000000001','定时任务告警Webhook','job_alert_webhook','',1,'ops','企微/钉钉机器人地址，空则仅写日志',20,'1','admin',NOW(),0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `base_config` WHERE `config_code`='job_alert_webhook');

SELECT 'M2-W6 job_alert_webhook 配置就绪' AS message;
