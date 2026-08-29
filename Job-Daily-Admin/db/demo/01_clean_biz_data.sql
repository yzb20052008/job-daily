-- ============================================================
-- 清理系统业务流水数据（保留岗位/工种/企业/配置等支撑数据）
-- MySQL 5.7+
-- 执行库：job-daily（或现行业务库）
-- 警告：不可逆，执行前请备份；不会删除 job_post / job_types / job_company
--       / integral_goods / ums_pay_type / base_config / 系统表 / 用户表
-- ============================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1. 订单与履约
-- ----------------------------
TRUNCATE TABLE `job_order_clock`;
TRUNCATE TABLE `job_order_log`;
TRUNCATE TABLE `job_order`;
TRUNCATE TABLE `job_evaluate_log`;
TRUNCATE TABLE `job_evaluate`;
TRUNCATE TABLE `job_post_contact`;
TRUNCATE TABLE `job_browse`;
TRUNCATE TABLE `job_collect`;

-- ----------------------------
-- 2. 简历（工人侧业务资料，非岗位支撑）
-- ----------------------------
TRUNCATE TABLE `job_resume_cert`;
TRUNCATE TABLE `job_resume_expe`;
TRUNCATE TABLE `job_resume_intention`;
TRUNCATE TABLE `job_resume`;

-- ----------------------------
-- 3. 积分流水与效果（保留 integral_goods 商品目录）
-- ----------------------------
TRUNCATE TABLE `integral_goods_effect`;
TRUNCATE TABLE `integral_goods_order`;
TRUNCATE TABLE `integral_log`;
TRUNCATE TABLE `integral_recharge`;

-- ----------------------------
-- 4. 账户资金流水 / 提现 / VIP 订单（保留 ums_account 账户行、ums_pay_type）
-- ----------------------------
TRUNCATE TABLE `ums_account_records`;
TRUNCATE TABLE `ums_withdraw`;
TRUNCATE TABLE `ums_withdraw_account`;
TRUNCATE TABLE `ums_vip_orders`;
TRUNCATE TABLE `ums_referrer_log`;
TRUNCATE TABLE `ums_realname_auth`;
TRUNCATE TABLE `ums_vip`;

-- 余额充值单（若表不存在可忽略报错或注释本段）
-- TRUNCATE TABLE `ums_balance_recharge`;

-- 账户余额/积分归零（保留账户主键与用户绑定）
UPDATE `ums_account`
SET `balance` = 0.00,
    `balance_frozen` = 0.00,
    `balance_withdraw` = 0.00,
    `total_recharge` = 0.00,
    `total_withdraw` = 0.00,
    `total_consume` = 0.00,
    `integral` = 0,
    `total_integral` = 0,
    `update_time` = CURDATE();

-- ----------------------------
-- 5. 业务通知 / 反馈
--    cms_notice：只删私有站内信（if_public=0，订单/个人通知）
--    保留 if_public=1 的系统公告；CMS 文章、广告、关于我们不动
-- ----------------------------
DELETE nr FROM `cms_notice_read` nr
INNER JOIN `cms_notice` n ON nr.`notice_id` = n.`id`
WHERE IFNULL(n.`if_public`, 0) = 0;

DELETE FROM `cms_notice` WHERE IFNULL(`if_public`, 0) = 0;

TRUNCATE TABLE `cms_feedback`;

SET FOREIGN_KEY_CHECKS = 1;

-- 验收：
-- SELECT COUNT(*) FROM job_order;          -- 期望 0
-- SELECT COUNT(*) FROM job_post;           -- 岗位应仍在
-- SELECT COUNT(*) FROM job_types;          -- 工种应仍在
-- SELECT COUNT(*) FROM integral_goods;     -- 道具商品应仍在
-- SELECT COUNT(*) FROM cms_notice WHERE if_public=1;  -- 系统公告应仍在
-- SELECT COUNT(*) FROM cms_notice WHERE IFNULL(if_public,0)=0; -- 期望 0
