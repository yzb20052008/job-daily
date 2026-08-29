-- 腾讯地图配置（WebService / JS Key + 服务端签名 SK）
-- 执行库：job-daily
-- 说明：map_key 可下发前端渲染地图；map_sk 仅服务端签名，勿暴露给客户端
-- 新环境请在后台「基础配置」填写真实值；已有 map_key 时本脚本不会覆盖

INSERT INTO `base_config` (`id`,`config_name`,`config_code`,`config_value`,`config_flag`,`group_code`,`remark`,`sort`,`status`,`create_by`,`create_time`,`del_flag`)
SELECT LEFT(REPLACE(UUID(),'-',''), 32),'腾讯地图Key','map_key','',1,'map','腾讯位置服务 Key，逆地理/搜索/JS 地图',20,'1','admin',NOW(),0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `base_config` WHERE `config_code`='map_key');

INSERT INTO `base_config` (`id`,`config_name`,`config_code`,`config_value`,`config_flag`,`group_code`,`remark`,`sort`,`status`,`create_by`,`create_time`,`del_flag`)
SELECT LEFT(REPLACE(UUID(),'-',''), 32),'腾讯地图SK','map_sk','',1,'map','腾讯 WebService 签名密钥，仅服务端使用',21,'1','admin',NOW(),0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `base_config` WHERE `config_code`='map_sk');
