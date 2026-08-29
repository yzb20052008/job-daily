-- 基础配置：补齐按钮权限，并给「已有菜单权限」的角色自动挂上按钮权限
-- 现象：能打开 baseConfigList，编辑时报「没有权限」——通常只勾了菜单未勾按钮
-- 权限码须与 BaseConfigController @RequiresPermissions 一致：base:base_config:*

-- 1) 定位菜单（按路由，兼容菜单 id 被重建）
-- 2) 确保按钮权限存在且 perms 正确

SET @menu_id = (
  SELECT id FROM sys_permission
  WHERE url = '/base/baseConfigList' AND del_flag = 0
  ORDER BY create_time DESC LIMIT 1
);

-- 无菜单则跳过后续（需先在菜单管理中存在「基础配置」）
-- 菜单应标记为非叶子，否则角色授权树不展示按钮
UPDATE sys_permission
SET is_leaf = 0, update_time = NOW()
WHERE id = @menu_id AND (is_leaf IS NULL OR is_leaf = 1);

-- 按钮：添加
INSERT INTO sys_permission (
  id, parent_id, name, url, component, component_name, redirect, menu_type, perms, perms_type,
  sort_no, always_show, icon, is_route, is_leaf, keep_alive, hidden, hide_tab, description,
  create_by, create_time, del_flag, rule_flag, status, internal_or_external, platform
)
SELECT
  '2024073004162240121', @menu_id, '添加基础配置', NULL, NULL, NULL, NULL, 2, 'base:base_config:add', '1',
  NULL, 0, NULL, 0, 1, 0, 0, 0, NULL,
  'admin', NOW(), 0, 0, '1', 0, 'admin'
FROM DUAL
WHERE @menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_permission WHERE perms = 'base:base_config:add' AND del_flag = 0);

-- 若已存在但 parent 不对 / perms 被改坏，纠正
UPDATE sys_permission
SET parent_id = @menu_id, perms = 'base:base_config:add', menu_type = 2, status = '1', del_flag = 0
WHERE @menu_id IS NOT NULL AND (
  id = '2024073004162240121' OR perms = 'base:base_config:add'
);

-- 按钮：编辑
INSERT INTO sys_permission (
  id, parent_id, name, url, component, component_name, redirect, menu_type, perms, perms_type,
  sort_no, always_show, icon, is_route, is_leaf, keep_alive, hidden, hide_tab, description,
  create_by, create_time, del_flag, rule_flag, status, internal_or_external, platform
)
SELECT
  '2024073004162240122', @menu_id, '编辑基础配置', NULL, NULL, NULL, NULL, 2, 'base:base_config:edit', '1',
  NULL, 0, NULL, 0, 1, 0, 0, 0, NULL,
  'admin', NOW(), 0, 0, '1', 0, 'admin'
FROM DUAL
WHERE @menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_permission WHERE perms = 'base:base_config:edit' AND del_flag = 0);

UPDATE sys_permission
SET parent_id = @menu_id, perms = 'base:base_config:edit', menu_type = 2, status = '1', del_flag = 0
WHERE @menu_id IS NOT NULL AND (
  id = '2024073004162240122' OR perms = 'base:base_config:edit'
);

-- 按钮：删除
INSERT INTO sys_permission (
  id, parent_id, name, url, component, component_name, redirect, menu_type, perms, perms_type,
  sort_no, always_show, icon, is_route, is_leaf, keep_alive, hidden, hide_tab, description,
  create_by, create_time, del_flag, rule_flag, status, internal_or_external, platform
)
SELECT
  '2024073004162240123', @menu_id, '删除基础配置', NULL, NULL, NULL, NULL, 2, 'base:base_config:delete', '1',
  NULL, 0, NULL, 0, 1, 0, 0, 0, NULL,
  'admin', NOW(), 0, 0, '1', 0, 'admin'
FROM DUAL
WHERE @menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_permission WHERE perms = 'base:base_config:delete' AND del_flag = 0);

UPDATE sys_permission
SET parent_id = @menu_id, perms = 'base:base_config:delete', menu_type = 2, status = '1', del_flag = 0
WHERE @menu_id IS NOT NULL AND (
  id = '2024073004162240123' OR perms = 'base:base_config:delete'
);

-- 按钮：批量删除
INSERT INTO sys_permission (
  id, parent_id, name, url, component, component_name, redirect, menu_type, perms, perms_type,
  sort_no, always_show, icon, is_route, is_leaf, keep_alive, hidden, hide_tab, description,
  create_by, create_time, del_flag, rule_flag, status, internal_or_external, platform
)
SELECT
  '2024073004162240124', @menu_id, '批量删除基础配置', NULL, NULL, NULL, NULL, 2, 'base:base_config:deleteBatch', '1',
  NULL, 0, NULL, 0, 1, 0, 0, 0, NULL,
  'admin', NOW(), 0, 0, '1', 0, 'admin'
FROM DUAL
WHERE @menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_permission WHERE perms = 'base:base_config:deleteBatch' AND del_flag = 0);

UPDATE sys_permission
SET parent_id = @menu_id, perms = 'base:base_config:deleteBatch', menu_type = 2, status = '1', del_flag = 0
WHERE @menu_id IS NOT NULL AND (
  id = '2024073004162240124' OR perms = 'base:base_config:deleteBatch'
);

-- 按钮：导出
INSERT INTO sys_permission (
  id, parent_id, name, url, component, component_name, redirect, menu_type, perms, perms_type,
  sort_no, always_show, icon, is_route, is_leaf, keep_alive, hidden, hide_tab, description,
  create_by, create_time, del_flag, rule_flag, status, internal_or_external, platform
)
SELECT
  '2024073004162240125', @menu_id, '导出excel_基础配置', NULL, NULL, NULL, NULL, 2, 'base:base_config:exportXls', '1',
  NULL, 0, NULL, 0, 1, 0, 0, 0, NULL,
  'admin', NOW(), 0, 0, '1', 0, 'admin'
FROM DUAL
WHERE @menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_permission WHERE perms = 'base:base_config:exportXls' AND del_flag = 0);

UPDATE sys_permission
SET parent_id = @menu_id, perms = 'base:base_config:exportXls', menu_type = 2, status = '1', del_flag = 0
WHERE @menu_id IS NOT NULL AND (
  id = '2024073004162240125' OR perms = 'base:base_config:exportXls'
);

-- 按钮：导入
INSERT INTO sys_permission (
  id, parent_id, name, url, component, component_name, redirect, menu_type, perms, perms_type,
  sort_no, always_show, icon, is_route, is_leaf, keep_alive, hidden, hide_tab, description,
  create_by, create_time, del_flag, rule_flag, status, internal_or_external, platform
)
SELECT
  '2024073004162240126', @menu_id, '导入excel_基础配置', NULL, NULL, NULL, NULL, 2, 'base:base_config:importExcel', '1',
  NULL, 0, NULL, 0, 1, 0, 0, 0, NULL,
  'admin', NOW(), 0, 0, '1', 0, 'admin'
FROM DUAL
WHERE @menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_permission WHERE perms = 'base:base_config:importExcel' AND del_flag = 0);

UPDATE sys_permission
SET parent_id = @menu_id, perms = 'base:base_config:importExcel', menu_type = 2, status = '1', del_flag = 0
WHERE @menu_id IS NOT NULL AND (
  id = '2024073004162240126' OR perms = 'base:base_config:importExcel'
);

-- 3) 凡已授权「基础配置」菜单的角色，自动补齐其下全部按钮权限
INSERT INTO sys_role_permission (id, role_id, permission_id, data_rule_ids, operate_date, operate_ip)
SELECT
  REPLACE(UUID(), '-', ''),
  rp.role_id,
  btn.id,
  NULL,
  NOW(),
  '127.0.0.1'
FROM sys_role_permission rp
INNER JOIN sys_permission menu ON menu.id = rp.permission_id
  AND menu.del_flag = 0
  AND (menu.id = @menu_id OR menu.url = '/base/baseConfigList')
INNER JOIN sys_permission btn ON btn.parent_id = menu.id
  AND btn.menu_type = 2
  AND btn.del_flag = 0
  AND btn.status = '1'
WHERE @menu_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM sys_role_permission x
    WHERE x.role_id = rp.role_id AND x.permission_id = btn.id
  );
