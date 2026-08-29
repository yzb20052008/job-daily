-- ============================================================
-- M2-W5：消息模板菜单
-- ============================================================

INSERT INTO `sys_permission` (
  `id`, `parent_id`, `name`, `url`, `component`, `component_name`, `redirect`,
  `menu_type`, `perms`, `perms_type`, `sort_no`, `always_show`, `icon`,
  `is_route`, `is_leaf`, `keep_alive`, `hidden`, `hide_tab`, `description`,
  `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `rule_flag`, `status`, `internal_or_external`
)
SELECT
  '2026081805002000001', '1585831107638308866', '消息模板', '/msg/bizMsgTemplateList', 'msg/BizMsgTemplateList',
  NULL, NULL, 1, NULL, '1', 8.00, 0, NULL,
  1, 1, 0, 0, 0, '站内信/微信订阅模板',
  'admin', NOW(), NULL, NULL, 0, 0, '1', 0
FROM DUAL
WHERE EXISTS (SELECT 1 FROM `sys_permission` WHERE `id` = '1585831107638308866')
  AND NOT EXISTS (
    SELECT 1 FROM `sys_permission`
    WHERE `id` = '2026081805002000001' OR `component` = 'msg/BizMsgTemplateList'
  );

INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `data_rule_ids`, `operate_date`, `operate_ip`)
SELECT
  '2026081805002000101',
  'f6817f48af4fb3af11b9e8bf182f618b',
  '2026081805002000001',
  NULL, NOW(), '127.0.0.1'
FROM DUAL
WHERE EXISTS (SELECT 1 FROM `sys_permission` WHERE `id` = '2026081805002000001')
  AND NOT EXISTS (
    SELECT 1 FROM `sys_role_permission`
    WHERE `role_id` = 'f6817f48af4fb3af11b9e8bf182f618b'
      AND `permission_id` = '2026081805002000001'
  );

SELECT '消息模板菜单已就绪' AS message;
