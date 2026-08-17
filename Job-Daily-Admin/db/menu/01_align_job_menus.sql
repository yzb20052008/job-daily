-- ============================================================
-- 后台菜单对齐（针对已有 job-daily 库的增量版）
-- 说明：本地 job-daily 已存在「招工管理」「积分管理」菜单，本脚本不再重复插入
-- 仅做：停用失效旧菜单、取消隐藏运营菜单、补缺（按需）
-- 日期：2026-08-18
-- ============================================================

-- 1) 停用仍指向不存在页面的旧菜单（若有）
UPDATE `sys_permission`
SET `del_flag` = 1, `hidden` = 1, `update_by` = 'admin', `update_time` = NOW()
WHERE `del_flag` = 0
  AND (
    `component` LIKE 'rms/%'
    OR `component` LIKE 'wms/%'
    OR `url` LIKE '/rms/%'
    OR `url` LIKE '/wms/%'
  );

-- 2) 停用已无 Vue 页面的旧 ums 菜单
UPDATE `sys_permission`
SET `del_flag` = 1, `hidden` = 1, `update_by` = 'admin', `update_time` = NOW()
WHERE `del_flag` = 0
  AND `component` IN (
    'ums/UmsShareLogList',
    'ums/UmsShieldCompanyList',
    'ums/UmsChatPresetList',
    'ums/UmsCommunicateList',
    'ums/UmsRobotLogList',
    'ums/UmsBrowseList',
    'ums/UmsApplyLogList',
    'ums/UmsApplyReadList',
    'ums/UmsChatHistoryList'
  );

-- 3) 取消隐藏：签到 / VIP（页面存在）
UPDATE `sys_permission`
SET `hidden` = 0, `update_by` = 'admin', `update_time` = NOW()
WHERE `del_flag` = 0
  AND `hidden` = 1
  AND `component` IN ('ums/UmsSignList', 'ums/UmsVipList', 'ums/UmsVipOrdersList');

-- 4) 若缺少「用户VIP」菜单则补一条（挂在用户管理下）
INSERT INTO `sys_permission` (
  `id`, `parent_id`, `name`, `url`, `component`, `component_name`, `redirect`,
  `menu_type`, `perms`, `perms_type`, `sort_no`, `always_show`, `icon`,
  `is_route`, `is_leaf`, `keep_alive`, `hidden`, `hide_tab`, `description`,
  `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `rule_flag`, `status`, `internal_or_external`
)
SELECT
  '2026081803000000013', '1577192805142093826', '用户VIP', '/ums/umsUserVipList', 'ums/UmsUserVipList',
  NULL, NULL, 1, NULL, '1', 22.00, 0, NULL, 1, 1, 0, 0, 0, NULL,
  'admin', NOW(), NULL, NULL, 0, 0, '1', 0
FROM DUAL
WHERE EXISTS (SELECT 1 FROM `sys_permission` WHERE `id` = '1577192805142093826')
  AND NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `component` = 'ums/UmsUserVipList' OR `url` = '/ums/umsUserVipList');

SELECT '菜单增量对齐完成（未重复创建招工/积分一级菜单）' AS message;
