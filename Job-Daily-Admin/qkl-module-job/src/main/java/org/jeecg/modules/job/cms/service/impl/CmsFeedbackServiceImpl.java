package org.jeecg.modules.job.cms.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.job.cms.entity.CmsFeedback;
import org.jeecg.modules.job.cms.mapper.CmsFeedbackMapper;
import org.jeecg.modules.job.cms.service.ICmsFeedbackService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 意见反馈
 * @Author: qingkonglan
 * @Date:   2022-08-21
 * @Version: V1.0
 */
@Service
public class CmsFeedbackServiceImpl extends ServiceImpl<CmsFeedbackMapper, CmsFeedback> implements ICmsFeedbackService {

    @Override
    public IPage<CmsFeedback> getFeedbackList(Page<CmsFeedback> page, CmsFeedback params) {
        return this.baseMapper.getFeedbackList(page,params);
    }
}
