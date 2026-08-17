package org.jeecg.modules.job.job.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.job.job.entity.JobPost;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.job.job.vo.JobPostVo;
import org.quartz.Job;

import java.util.Map;

/**
 * @Description: 招工信息
 * @Author: qingkonglan
 * @Date:   2024-07-30
 * @Version: V1.0
 */
public interface IJobPostService extends IService<JobPost> {

    /**
     * 新增招工
     * @param post
     * @return
     */
    boolean addPostInfo(JobPost post);

    /**
     * 更新招工
     * @param post
     * @return
     */
    boolean updatePostInfo(JobPost post);

    /**
     * 删除招工信息
     * @param postId
     * @return
     */
    boolean deletePostInfo(String postId);



    /**
     * 自动下架超时任务
     */
    void autoOfflinePost();


    /**
     * 根据id查询招工详情
     * @param id
     * @return
     */
    JobPostVo getPostDetail(String id, String userId);


    /**
     * 根据id查询招工详情
     * @param id
     * @return
     */
    JobPostVo getPostDetail(String id);


    /**
     * 更新招工状态
     * @param id
     * @param postStatus
     * @return
     */
    boolean updatePostStatus(String id,String postStatus);

    /**
     * 分页查询列表
     * @param page
     * @param params 参数信息
     * @return
     */
    IPage<JobPost> getPostList(Page<JobPost> page, JobPost params);


    /**
     * 分页查询列表
     * @param page
     * @param params 参数信息
     * @return
     */
    IPage<JobPost> getMyPostList(Page<JobPost> page, JobPost params);


    /**
     * 分页查询列表
     * @param page
     * @param params 参数信息
     * @return
     */
    IPage<Map<String,Object>> getPostMapList(Page<JobPost> page, JobPost params);

    /**
     * 分页查询列表
     * @param page
     * @param params 参数信息
     * @return
     */
    IPage<Map<String,Object>> getPostMapListForAdmin(Page<JobPost> page, JobPost params);



}
