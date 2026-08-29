-- 余额充值单：支撑微信余额充值下单与回调入账
-- 执行库：job-daily（或现行业务库）

CREATE TABLE IF NOT EXISTS `ums_balance_recharge` (
  `id` varchar(36) NOT NULL,
  `user_id` varchar(36) DEFAULT NULL COMMENT '用户ID',
  `order_sn` varchar(64) DEFAULT NULL COMMENT '商户订单号',
  `money` decimal(14,2) DEFAULT NULL COMMENT '充值金额(元)',
  `pay_type` varchar(20) DEFAULT NULL COMMENT '支付方式',
  `recharge_status` varchar(10) DEFAULT '0' COMMENT '0未支付1成功2失败',
  `create_by` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_by` varchar(50) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `remark` varchar(500) DEFAULT NULL,
  `sys_org_code` varchar(64) DEFAULT NULL,
  `tenant_id` int(11) DEFAULT NULL,
  `del_flag` int(11) DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_balance_recharge_sn` (`order_sn`),
  KEY `idx_balance_recharge_user` (`user_id`),
  KEY `idx_balance_recharge_status` (`recharge_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='余额充值单';
