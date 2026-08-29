-- ============================================================
-- M2：运营工作台菜单
-- 日期：2026-08-18
-- ============================================================

-- 1) 一级菜单：运营工作台（与首页同级）
INSERT INTO `sys_permission` (
  `id`, `parent_id`, `name`, `url`, `component`, `component_name`, `redirect`,
  `menu_type`, `perms`, `perms_type`, `sort_no`, `always_show`, `icon`,
  `is_route`, `is_leaf`, `keep_alive`, `hidden`, `hide_tab`, `description`,
  `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `rule_flag`, `status`, `internal_or_external`
)
SELECT
  '2026081804000000001', NULL, '运营工作台', '/ops/workbench', 'ops/OpsWorkbench',
  NULL, NULL, 0, NULL, '1', 0.50, 0, 'dashboard',
  1, 1, 1, 0, 0, '待审岗/认证/异常单/提现聚合',
  'admin', NOW(), NULL, NULL, 0, 0, '1', 0
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_permission`
  WHERE `id` = '2026081804000000001'
     OR `component` = 'ops/OpsWorkbench'
     OR `url` = '/ops/workbench'
);

-- 2) 授权给 admin 角色
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `data_rule_ids`, `operate_date`, `operate_ip`)
SELECT
  '2026081804000000101',
  'f6817f48af4fb3af11b9e8bf182f618b',
  '2026081804000000001',
  NULL, NOW(), '127.0.0.1'
FROM DUAL
WHERE EXISTS (SELECT 1 FROM `sys_permission` WHERE `id` = '2026081804000000001')
  AND NOT EXISTS (
    SELECT 1 FROM `sys_role_permission`
    WHERE `role_id` = 'f6817f48af4fb3af11b9e8bf182f618b'
      AND `permission_id` = '2026081804000000001'
  );

SELECT '运营工作台菜单已就绪，请重新登录后台' AS message;
