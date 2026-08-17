package org.jeecg.modules.job.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.modules.job.base.entity.BaseConfig;
import org.jeecg.modules.job.base.mapper.BaseConfigMapper;
import org.jeecg.modules.job.base.service.IBaseConfigService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: 基础配置
 * @Author: qingkonglan
 * @Date:   2024-07-30
 * @Version: V1.0
 */
@Service
public class BaseConfigServiceImpl extends ServiceImpl<BaseConfigMapper, BaseConfig> implements IBaseConfigService {

    @Override
    public BaseConfig getConfigByCode(String code) {
        return this.getOne(new QueryWrapper<>(new BaseConfig().setConfigCode(code)));
    }

    @Override
    public List<BaseConfig> getConfigByGroupCode(String groupCode) {
        return this.list(new QueryWrapper<>(new BaseConfig().setGroupCode(groupCode)));
    }
}
