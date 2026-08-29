-- ============================================================
-- M2-W5：权益开关 + 消息模板
-- 日期：2026-08-18
-- ============================================================

-- 1) VIP / 发岗权益开关（复用 base_config）
INSERT INTO `base_config` (`id`,`config_name`,`config_code`,`config_value`,`config_flag`,`group_code`,`remark`,`sort`,`status`,`create_by`,`create_time`,`del_flag`)
SELECT '2026081805000000001','VIP拨号免积分','vip_contact_free','1',1,'privilege','1=有效VIP联系免扣积分',10,'1','admin',NOW(),0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `base_config` WHERE `config_code`='vip_contact_free');

INSERT INTO `base_config` (`id`,`config_name`,`config_code`,`config_value`,`config_flag`,`group_code`,`remark`,`sort`,`status`,`create_by`,`create_time`,`del_flag`)
SELECT '2026081805000000002','发岗需企业VIP','vip_post_require','0',1,'privilege','1=发岗强制企业VIP；默认0兼容现网',11,'1','admin',NOW(),0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `base_config` WHERE `config_code`='vip_post_require');

-- 2) 消息模板表
CREATE TABLE IF NOT EXISTS `biz_msg_template` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `template_code` varchar(64) NOT NULL COMMENT '模板编码',
  `channel` varchar(16) NOT NULL COMMENT '通道 site/wx',
  `wx_template_id` varchar(128) DEFAULT NULL COMMENT '微信订阅模板ID',
  `title` varchar(200) DEFAULT NULL COMMENT '标题模板',
  `content` varchar(1000) DEFAULT NULL COMMENT '内容模板，支持{var}',
  `field_mapping` varchar(1000) DEFAULT NULL COMMENT '字段映射JSON',
  `status` varchar(3) DEFAULT '1' COMMENT '1启用0停用',
  `remark` varchar(500) DEFAULT NULL,
  `create_by` varchar(32) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_by` varchar(32) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `del_flag` int(11) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_template_code` (`template_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='业务消息模板';

-- 3) 种子：微信模板（沿用现网 TemplateId，可后台改）
INSERT INTO `biz_msg_template` (`id`,`template_code`,`channel`,`wx_template_id`,`title`,`content`,`status`,`remark`,`create_by`,`create_time`,`del_flag`)
SELECT '2026081805001000001','wx_auth','wx','rORH6Ct2fOi5JMGsZABamh5LrB2w0ZmhWIA03T0IS8I','认证结果通知',NULL,'1','微信订阅','admin',NOW(),0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `biz_msg_template` WHERE `template_code`='wx_auth');

INSERT INTO `biz_msg_template` (`id`,`template_code`,`channel`,`wx_template_id`,`title`,`content`,`status`,`remark`,`create_by`,`create_time`,`del_flag`)
SELECT '2026081805001000002','wx_withdraw','wx','ij_HvuqyZZ_lZnAlMpWv2zcUCm_Nwd4jTUlGPk1FEaM','提现进度提醒',NULL,'1','微信订阅','admin',NOW(),0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `biz_msg_template` WHERE `template_code`='wx_withdraw');

INSERT INTO `biz_msg_template` (`id`,`template_code`,`channel`,`wx_template_id`,`title`,`content`,`status`,`remark`,`create_by`,`create_time`,`del_flag`)
SELECT '2026081805001000003','wx_order_agree','wx','M1CpyBY5jSPs_UvB4eSpnHzgQeaLIR-Im-8gEb1n4v8','同意用工通知',NULL,'1','微信订阅','admin',NOW(),0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `biz_msg_template` WHERE `template_code`='wx_order_agree');

INSERT INTO `biz_msg_template` (`id`,`template_code`,`channel`,`wx_template_id`,`title`,`content`,`status`,`remark`,`create_by`,`create_time`,`del_flag`)
SELECT '2026081805001000004','wx_order_unagree','wx','85msHoY9z5lBVsRX5hIpIyGBRHfht_ac9GDqCDOGIhM','拒绝用工通知',NULL,'1','微信订阅','admin',NOW(),0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `biz_msg_template` WHERE `template_code`='wx_order_unagree');

INSERT INTO `biz_msg_template` (`id`,`template_code`,`channel`,`wx_template_id`,`title`,`content`,`status`,`remark`,`create_by`,`create_time`,`del_flag`)
SELECT '2026081805001000005','wx_apply_result','wx','dKD4VQ-E9kChwTRI5nsteBGRIM9WYOWuT9acAPwGGGc','录用结果通知',NULL,'1','微信订阅','admin',NOW(),0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `biz_msg_template` WHERE `template_code`='wx_apply_result');

INSERT INTO `biz_msg_template` (`id`,`template_code`,`channel`,`wx_template_id`,`title`,`content`,`status`,`remark`,`create_by`,`create_time`,`del_flag`)
SELECT '2026081805001000006','wx_new_order','wx','6wvo2jmjl9e5JrkcyTUBnTQZ_YYfVi75WXXr96mpL4E','接单任务通知',NULL,'1','微信订阅','admin',NOW(),0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `biz_msg_template` WHERE `template_code`='wx_new_order');

-- 4) 种子：站内信
INSERT INTO `biz_msg_template` (`id`,`template_code`,`channel`,`wx_template_id`,`title`,`content`,`status`,`remark`,`create_by`,`create_time`,`del_flag`)
SELECT '2026081805001000011','site_new_order_member','site',NULL,'生成订单','您已确认生成，请及时关注订单进展~~','1','站内信','admin',NOW(),0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `biz_msg_template` WHERE `template_code`='site_new_order_member');

INSERT INTO `biz_msg_template` (`id`,`template_code`,`channel`,`wx_template_id`,`title`,`content`,`status`,`remark`,`create_by`,`create_time`,`del_flag`)
SELECT '2026081805001000012','site_new_order_company','site',NULL,'新申请单','已有联系过您的工人提交了合作订单，请及时确认~~','1','站内信','admin',NOW(),0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `biz_msg_template` WHERE `template_code`='site_new_order_company');

INSERT INTO `biz_msg_template` (`id`,`template_code`,`channel`,`wx_template_id`,`title`,`content`,`status`,`remark`,`create_by`,`create_time`,`del_flag`)
SELECT '2026081805001000013','site_order_paid_member','site',NULL,'订单已结算','您的工作订单已完成线上结算，结算金额：{amount}元','1','站内信','admin',NOW(),0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `biz_msg_template` WHERE `template_code`='site_order_paid_member');

INSERT INTO `biz_msg_template` (`id`,`template_code`,`channel`,`wx_template_id`,`title`,`content`,`status`,`remark`,`create_by`,`create_time`,`del_flag`)
SELECT '2026081805001000014','site_order_paid_company','site',NULL,'工资结算成功','工人结算支付成功，结算金额：{amount}元','1','站内信','admin',NOW(),0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `biz_msg_template` WHERE `template_code`='site_order_paid_company');

SELECT 'M2-W5 规则与消息模板脚本完成' AS message;
