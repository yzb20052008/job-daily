package org.jeecg.modules.job.job.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.job.job.entity.JobBrowse;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.job.job.entity.JobCollect;

import java.util.Map;

/**
 * @Description: 浏览记录
 * * @Author: qingkonglan
 * @Date:   2024-08-22
 * @Version: V1.0
 */
public interface IJobBrowseService extends IService<JobBrowse> {

    /**
     * 添加浏览记录
     * @param userId
     * @param postId
     * @param roleCode
     * @return
     */
    boolean addBrowse(String userId,String postId,String roleCode);

    /**
     * 查询是否浏览过
     * @param userId
     * @param postId
     * @param roleCode
     * @return
     */
    boolean ifBrowsed(String userId,String postId,String roleCode);



    /**
     * 分页查询浏览记录列表
     * @param page
     * @param params 参数信息
     * @return
     */
    IPage<Map<String,Object>> getBrowseList(Page<JobBrowse> page, JobBrowse params);

}
