package org.jeecg.modules.job.job.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.job.job.entity.JobBrowse;
import org.jeecg.modules.job.job.mapper.JobBrowseMapper;
import org.jeecg.modules.job.job.service.IJobBrowseService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.Map;

/**
 * @Description: 浏览记录
 * * @Author: qingkonglan
 * @Date: 2024-08-22
 * @Version: V1.0
 */
@Service
public class JobBrowseServiceImpl extends ServiceImpl<JobBrowseMapper, JobBrowse> implements IJobBrowseService {

    @Override
    public boolean addBrowse(String userId, String postId, String roleCode) {
        JobBrowse browse = new JobBrowse();
        browse.setUserId(userId);
        browse.setDataId(postId);
        browse.setRoleCode(roleCode);
        return this.save(browse);
    }

    @Override
    public boolean ifBrowsed(String userId, String postId, String roleCode) {
        long count = this.count(new QueryWrapper<>(new JobBrowse().setDataId(postId).setUserId(userId).setRoleCode(roleCode)));
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public IPage<Map<String, Object>> getBrowseList(Page<JobBrowse> page, JobBrowse params) {
        return null;
    }
}
