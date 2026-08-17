-- ============================================================
-- 升级脚本：旧 ums_withdraw（member_id）对齐现行实体
-- 可重复执行（先判断列是否存在）
-- 日期：2026-08-18
-- ============================================================

-- 1) member_id → user_id（若仍是旧列名）
SET @col_member := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'ums_withdraw' AND COLUMN_NAME = 'member_id'
);
SET @col_user := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'ums_withdraw' AND COLUMN_NAME = 'user_id'
);
SET @sql := IF(@col_member > 0 AND @col_user = 0,
  'ALTER TABLE `ums_withdraw` CHANGE COLUMN `member_id` `user_id` varchar(36) NULL COMMENT ''用户ID''',
  'SELECT ''skip rename member_id''');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2) 补充转账相关字段
SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='ums_withdraw' AND COLUMN_NAME='out_bill_no')=0,
  'ALTER TABLE `ums_withdraw` ADD COLUMN `out_bill_no` varchar(64) NULL COMMENT ''商户转账单号'' AFTER `user_id`',
  'SELECT ''skip out_bill_no''');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='ums_withdraw' AND COLUMN_NAME='transfer_status')=0,
  'ALTER TABLE `ums_withdraw` ADD COLUMN `transfer_status` varchar(50) NULL COMMENT ''微信转账状态'' AFTER `bank_branch_name`',
  'SELECT ''skip transfer_status''');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='ums_withdraw' AND COLUMN_NAME='package_info')=0,
  'ALTER TABLE `ums_withdraw` ADD COLUMN `package_info` varchar(1000) NULL COMMENT ''确认收款package'' AFTER `transfer_status`',
  'SELECT ''skip package_info''');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 3) 唯一索引（若不存在）
SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='ums_withdraw' AND INDEX_NAME='uk_withdraw_out_bill')=0,
  'ALTER TABLE `ums_withdraw` ADD UNIQUE KEY `uk_withdraw_out_bill` (`out_bill_no`)',
  'SELECT ''skip uk_withdraw_out_bill''');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
