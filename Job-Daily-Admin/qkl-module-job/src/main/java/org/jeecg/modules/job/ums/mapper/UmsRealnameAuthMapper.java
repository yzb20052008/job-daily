package org.jeecg.modules.job.ums.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.job.ums.entity.UmsRealnameAuth;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 实名认证
 * @Author: qingkonglan
 * @Date:   2024-07-30
 * @Version: V1.0
 */
public interface UmsRealnameAuthMapper extends BaseMapper<UmsRealnameAuth> {

    /**
     *  分页查询签到情况
     * @param page
     * @param params
     * @return
     */
    IPage<UmsRealnameAuth> getRealNameAuthList(IPage page, @Param("paramCondition")UmsRealnameAuth params);

}
