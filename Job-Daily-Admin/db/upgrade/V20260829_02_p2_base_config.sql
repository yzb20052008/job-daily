-- 打卡半径 / 评价超时配置（与后端 BizConstants 同源）
-- 执行库：job-daily（或现行业务库）

INSERT INTO `base_config` (`id`,`config_name`,`config_code`,`config_value`,`config_flag`,`group_code`,`remark`,`sort`,`status`,`create_by`,`create_time`,`del_flag`)
SELECT LEFT(REPLACE(UUID(),'-',''), 32),'打卡允许半径(米)','clock_range','2000',1,'job','前后端打卡围栏统一半径',10,'1','admin',NOW(),0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `base_config` WHERE `config_code`='clock_range');

INSERT INTO `base_config` (`id`,`config_name`,`config_code`,`config_value`,`config_flag`,`group_code`,`remark`,`sort`,`status`,`create_by`,`create_time`,`del_flag`)
SELECT LEFT(REPLACE(UUID(),'-',''), 32),'待评价超时完结(小时)','evaluate_timeout_hours','72',1,'job','待评价超过该小时数自动完结',11,'1','admin',NOW(),0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `base_config` WHERE `config_code`='evaluate_timeout_hours');
