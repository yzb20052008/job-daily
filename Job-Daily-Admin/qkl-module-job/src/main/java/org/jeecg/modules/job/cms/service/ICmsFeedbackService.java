package org.jeecg.modules.job.cms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.job.cms.entity.CmsFeedback;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 意见反馈
 * @Author: qingkonglan
 * @Date:   2022-08-21
 * @Version: V1.0
 */
public interface ICmsFeedbackService extends IService<CmsFeedback> {


    /**
     * 查询意见反馈列表
     * @param page
     * @param params 参数信息
     * @return
     */
    public IPage<CmsFeedback> getFeedbackList(Page<CmsFeedback> page, CmsFeedback params);

}
