package org.jeecg.modules.job.cms.mapper;


import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.job.cms.entity.CmsFeedback;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 意见反馈
 * @Author: qingkonglan
 * @Date:   2022-08-21
 * @Version: V1.0
 */
public interface CmsFeedbackMapper extends BaseMapper<CmsFeedback> {

    /**
     * 分页查询意见反馈
     * @param page
     * @param params 参数信息
     * @return
     */
    IPage<CmsFeedback> getFeedbackList(IPage page, @Param("params") CmsFeedback params);

}
