-- 下线积分 AI 卡（GPT/AI 能力已从工程移除）
-- 执行库：job-daily（或现行业务库）

-- 禁用 AI 卡商品，避免 App 继续展示可购买
UPDATE `integral_goods`
SET `status` = 0,
    `remark` = CONCAT(IFNULL(`remark`, ''), ' [已下线-AI卡]')
WHERE `code` = 'ai'
  AND (`status` IS NULL OR `status` <> 0);

-- 用户额度表保留字段兼容历史数据；业务侧已不再读写 ai_num / used_ai_num
