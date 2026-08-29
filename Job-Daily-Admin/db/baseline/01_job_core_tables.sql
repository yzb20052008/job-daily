-- ============================================================
-- 小蓝零工核心业务表 DDL（按现行 Java 实体生成）
-- 用途：新环境基线 / 与旧 qkl-job.sql 代差对齐
-- 注意：IF NOT EXISTS，可重复执行；不含 Jeecg 系统表
-- 日期：2026-08-18
-- ============================================================

SET NAMES utf8mb4;

-- ----------------------------
-- 招工信息
-- ----------------------------
CREATE TABLE IF NOT EXISTS `job_post` (
  `id` varchar(36) NOT NULL COMMENT '主键',
  `user_id` varchar(36) DEFAULT NULL COMMENT '发布人用户ID',
  `title` varchar(200) DEFAULT NULL COMMENT '标题',
  `settlement_type` int(11) DEFAULT NULL COMMENT '结算方式',
  `post_source` int(11) DEFAULT NULL COMMENT '职位来源',
  `type_ids` varchar(500) DEFAULT NULL COMMENT '工种ID',
  `type_codes` varchar(500) DEFAULT NULL COMMENT '工种编码',
  `type_names` varchar(500) DEFAULT NULL COMMENT '工种名称',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `close_time` datetime DEFAULT NULL COMMENT '截止时间',
  `sex_require` varchar(50) DEFAULT NULL COMMENT '性别要求',
  `age_require` varchar(50) DEFAULT NULL COMMENT '年龄要求',
  `recruits_number` int(11) DEFAULT NULL COMMENT '招聘人数',
  `address_name` varchar(200) DEFAULT NULL COMMENT '地址名称',
  `address_house` varchar(100) DEFAULT NULL COMMENT '门牌号',
  `address` varchar(500) DEFAULT NULL COMMENT '工作地点',
  `longitude` varchar(50) DEFAULT NULL COMMENT '经度',
  `latitude` varchar(50) DEFAULT NULL COMMENT '纬度',
  `phone` varchar(30) DEFAULT NULL COMMENT '联系电话',
  `name` varchar(50) DEFAULT NULL COMMENT '联系人',
  `descr` text COMMENT '职位描述',
  `pricing_mode` varchar(50) DEFAULT NULL COMMENT '计价方式',
  `salary` varchar(50) DEFAULT NULL COMMENT '薪资',
  `salary_unit` varchar(50) DEFAULT NULL COMMENT '薪资单位',
  `job_requires` text COMMENT '招工要求',
  `video_url` varchar(500) DEFAULT NULL COMMENT '工作视频',
  `img_url` varchar(1000) DEFAULT NULL COMMENT '工作图片',
  `if_call` int(11) DEFAULT NULL COMMENT '是否需电话沟通',
  `browse_number` int(11) DEFAULT 0 COMMENT '浏览量',
  `if_topping` int(11) DEFAULT 0 COMMENT '是否置顶',
  `if_bold` int(11) DEFAULT 0 COMMENT '是否加粗',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `sort` int(11) DEFAULT NULL COMMENT '排序',
  `post_status` varchar(10) DEFAULT NULL COMMENT '招工状态',
  `create_by` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_by` varchar(50) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `sys_org_code` varchar(64) DEFAULT NULL,
  `tenant_id` int(11) DEFAULT NULL,
  `del_flag` int(11) DEFAULT 0,
  `city` varchar(50) DEFAULT NULL,
  `city_code` varchar(50) DEFAULT NULL,
  `p_city` varchar(50) DEFAULT NULL,
  `p_city_code` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_job_post_user` (`user_id`),
  KEY `idx_job_post_status` (`post_status`),
  KEY `idx_job_post_city` (`city_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='招工信息';

-- ----------------------------
-- 用工订单
-- ----------------------------
CREATE TABLE IF NOT EXISTS `job_order` (
  `id` varchar(36) NOT NULL COMMENT '主键',
  `order_sn` varchar(64) DEFAULT NULL COMMENT '商户订单号',
  `user_id` varchar(36) DEFAULT NULL COMMENT '工人用户ID',
  `post_user_id` varchar(36) DEFAULT NULL COMMENT '老板用户ID',
  `post_id` varchar(36) DEFAULT NULL COMMENT '招工ID',
  `if_called` int(11) DEFAULT NULL COMMENT '是否电话联系',
  `start_time` datetime DEFAULT NULL COMMENT '开始工作时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束工作时间',
  `unit_price` decimal(12,2) DEFAULT NULL COMMENT '单价',
  `duration` int(11) DEFAULT NULL COMMENT '时长小时',
  `payable_amount` decimal(12,2) DEFAULT NULL COMMENT '应付金额',
  `amount` decimal(12,2) DEFAULT NULL COMMENT '实付金额',
  `order_status` varchar(10) DEFAULT NULL COMMENT '订单状态',
  `user_evaluate` int(11) DEFAULT 0 COMMENT '工人评价状态',
  `company_evaluate` int(11) DEFAULT 0 COMMENT '老板评价状态',
  `pay_type` varchar(30) DEFAULT NULL COMMENT '支付方式',
  `pay_status` varchar(10) DEFAULT NULL COMMENT '结算状态',
  `remark` varchar(500) DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `create_by` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `ensure_time` datetime DEFAULT NULL COMMENT '确认截止时间',
  `update_by` varchar(50) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `sys_org_code` varchar(64) DEFAULT NULL,
  `tenant_id` int(11) DEFAULT NULL,
  `del_flag` int(11) DEFAULT 0,
  `city` varchar(50) DEFAULT NULL,
  `city_code` varchar(50) DEFAULT NULL,
  `p_city` varchar(50) DEFAULT NULL,
  `p_city_code` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_job_order_sn` (`order_sn`),
  KEY `idx_job_order_user` (`user_id`),
  KEY `idx_job_order_post_user` (`post_user_id`),
  KEY `idx_job_order_post` (`post_id`),
  KEY `idx_job_order_status` (`order_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用工订单';

-- ----------------------------
-- 打卡记录
-- ----------------------------
CREATE TABLE IF NOT EXISTS `job_order_clock` (
  `id` varchar(36) NOT NULL,
  `order_id` varchar(36) DEFAULT NULL COMMENT '订单ID',
  `clock_type` int(11) DEFAULT NULL COMMENT '1上班2下班',
  `address` varchar(500) DEFAULT NULL,
  `longitude` varchar(50) DEFAULT NULL,
  `latitude` varchar(50) DEFAULT NULL,
  `images` varchar(1000) DEFAULT NULL,
  `create_by` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_by` varchar(50) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `sys_org_code` varchar(64) DEFAULT NULL,
  `tenant_id` int(11) DEFAULT NULL,
  `del_flag` int(11) DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_clock_order` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单打卡';

-- ----------------------------
-- 订单日志
-- ----------------------------
CREATE TABLE IF NOT EXISTS `job_order_log` (
  `id` varchar(36) NOT NULL,
  `order_id` varchar(36) DEFAULT NULL,
  `order_status` varchar(10) DEFAULT NULL,
  `note` varchar(500) DEFAULT NULL,
  `imgs` varchar(1000) DEFAULT NULL,
  `create_by` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_by` varchar(50) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `sys_org_code` varchar(64) DEFAULT NULL,
  `tenant_id` int(11) DEFAULT NULL,
  `del_flag` int(11) DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_order_log_order` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单状态日志';

-- ----------------------------
-- 简历主表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `job_resume` (
  `id` varchar(36) NOT NULL,
  `user_id` varchar(36) DEFAULT NULL,
  `name` varchar(50) DEFAULT NULL,
  `sex` varchar(10) DEFAULT NULL,
  `birthday` date DEFAULT NULL,
  `phone` varchar(30) DEFAULT NULL,
  `avatar` varchar(500) DEFAULT NULL,
  `intro` text,
  `status` int(11) DEFAULT NULL,
  `create_by` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_by` varchar(50) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `sys_org_code` varchar(64) DEFAULT NULL,
  `tenant_id` int(11) DEFAULT NULL,
  `del_flag` int(11) DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_resume_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='简历';

-- ----------------------------
-- 企业认证
-- ----------------------------
CREATE TABLE IF NOT EXISTS `job_company` (
  `id` varchar(36) NOT NULL,
  `user_id` varchar(36) DEFAULT NULL,
  `company_name` varchar(200) DEFAULT NULL,
  `license_no` varchar(100) DEFAULT NULL,
  `license_img` varchar(500) DEFAULT NULL,
  `legal_person` varchar(50) DEFAULT NULL,
  `auth_status` int(11) DEFAULT NULL,
  `reason` varchar(500) DEFAULT NULL,
  `create_by` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_by` varchar(50) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `sys_org_code` varchar(64) DEFAULT NULL,
  `tenant_id` int(11) DEFAULT NULL,
  `del_flag` int(11) DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_company_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='企业信息';

-- ----------------------------
-- 账户（与实体对齐）
-- ----------------------------
CREATE TABLE IF NOT EXISTS `ums_account` (
  `id` varchar(36) NOT NULL,
  `user_id` varchar(36) DEFAULT NULL,
  `balance_frozen` decimal(14,2) DEFAULT 0.00,
  `balance_withdraw` decimal(14,2) DEFAULT 0.00,
  `balance` decimal(14,2) DEFAULT 0.00,
  `total_recharge` decimal(14,2) DEFAULT 0.00,
  `total_withdraw` decimal(14,2) DEFAULT 0.00,
  `total_consume` decimal(14,2) DEFAULT 0.00,
  `integral` int(11) DEFAULT 0,
  `total_integral` int(11) DEFAULT 0,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `create_by` varchar(50) DEFAULT NULL,
  `update_by` varchar(50) DEFAULT NULL,
  `remark` varchar(500) DEFAULT NULL,
  `sys_org_code` varchar(64) DEFAULT NULL,
  `tenant_id` int(11) DEFAULT NULL,
  `del_flag` int(11) DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ums_account_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员账户';

-- ----------------------------
-- 提现（现行字段，含转账单号）
-- ----------------------------
CREATE TABLE IF NOT EXISTS `ums_withdraw` (
  `id` varchar(36) NOT NULL,
  `user_id` varchar(36) DEFAULT NULL COMMENT '用户ID',
  `out_bill_no` varchar(64) DEFAULT NULL COMMENT '商户转账单号',
  `money` decimal(14,2) DEFAULT NULL,
  `last_balance` decimal(14,2) DEFAULT NULL,
  `balance` decimal(14,2) DEFAULT NULL,
  `withdraw_status` int(11) DEFAULT 0 COMMENT '0待审1通过2失败',
  `reason` varchar(500) DEFAULT NULL,
  `account_type` int(11) DEFAULT NULL,
  `withdraw_account` varchar(100) DEFAULT NULL,
  `withdraw_name` varchar(100) DEFAULT NULL,
  `bank_name` varchar(100) DEFAULT NULL,
  `bank_branch_name` varchar(200) DEFAULT NULL,
  `transfer_status` varchar(50) DEFAULT NULL COMMENT '微信转账状态',
  `package_info` varchar(1000) DEFAULT NULL COMMENT '确认收款package',
  `create_by` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_by` varchar(50) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `remark` varchar(500) DEFAULT NULL,
  `sys_org_code` varchar(64) DEFAULT NULL,
  `tenant_id` int(11) DEFAULT NULL,
  `del_flag` int(11) DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_withdraw_out_bill` (`out_bill_no`),
  KEY `idx_withdraw_user` (`user_id`),
  KEY `idx_withdraw_status` (`withdraw_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户提现';

-- ----------------------------
-- 余额充值单
-- ----------------------------
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
