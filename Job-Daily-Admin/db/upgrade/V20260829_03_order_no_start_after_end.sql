-- 待开工已过结束时间自动取消（开关，默认开启）
-- 历史脏数据由 OrderJob.autoFinishOrder 幂等清洗（含退积分/释名额/通知），勿裸 UPDATE order_status
-- 执行库：job-daily（或现行业务库）

INSERT INTO `base_config` (`id`,`config_name`,`config_code`,`config_value`,`config_flag`,`group_code`,`remark`,`sort`,`status`,`create_by`,`create_time`,`del_flag`)
SELECT LEFT(REPLACE(UUID(),'-',''), 32),'待开工过期自动取消','order_no_start_after_end','1',1,'job','1=开启：待开工且 end_time 已过则自动取消；0=关闭',12,'1','admin',NOW(),0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `base_config` WHERE `config_code`='order_no_start_after_end');

-- 预览逾期待开工（只读，便于验收；实际取消走定时任务）
-- SELECT id, order_sn, start_time, end_time FROM job_order
-- WHERE del_flag=0 AND order_status='1' AND end_time IS NOT NULL AND end_time<>'' AND end_time < NOW();
