package org.jeecg.modules.job.base.service;

import org.jeecg.modules.job.base.entity.BaseConfig;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 基础配置
 * @Author: qingkonglan
 * @Date:   2024-07-30
 * @Version: V1.0
 */
public interface IBaseConfigService extends IService<BaseConfig> {

    /**
     * 根据编码查询配置参数
     * @param code
     * @return
     */
    BaseConfig getConfigByCode(String code);

    /**
     * 根据类型编码查询配置列表
     * @param groupCode
     * @return
     */
    List<BaseConfig> getConfigByGroupCode(String groupCode);
}
