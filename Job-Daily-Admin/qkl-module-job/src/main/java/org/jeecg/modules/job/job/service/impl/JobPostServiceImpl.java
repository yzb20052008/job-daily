package org.jeecg.modules.job.job.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.job.constant.BizConstants;
import org.jeecg.modules.job.home.service.WechatApiService;
import org.jeecg.modules.job.job.entity.*;
import org.jeecg.modules.job.job.mapper.JobOrderMapper;
import org.jeecg.modules.job.job.mapper.JobPostMapper;
import org.jeecg.modules.job.job.service.*;
import org.jeecg.modules.job.job.vo.JobPostVo;
import org.jeecg.modules.job.ums.entity.UmsRealnameAuth;
import org.jeecg.modules.job.ums.service.IUmsRealnameAuthService;
import org.jeecg.modules.job.utils.JsonUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description: 招工信息
 * @Author: qingkonglan
 * @Date:   2024-07-30
 * @Version: V1.0
 */
@Service
public class JobPostServiceImpl extends ServiceImpl<JobPostMapper, JobPost> implements IJobPostService {

    @Resource
    private ISysBaseAPI sysBaseAPI;
    @Resource
    private IUmsRealnameAuthService realnameAuthService;
    @Resource
    private IJobCompanyService companyService;
    @Resource
    private IJobCollectService collectService;
    @Resource
    private IJobBrowseService jobBrowseService;
    @Resource
    private IJobPostContactService contactService;
    @Resource
    private JobOrderMapper orderMapper;
    @Resource
    private IJobEvaluateService evaluateService;
    @Resource
    private WechatApiService wechatApiService;
    @Resource
    private IJobEvaluateLogService evaluateLogService;

    @Override
    public boolean addPostInfo(JobPost post) {
        //敏感字校验
        boolean result = wechatApiService.checkText(JsonUtils.objectToJson(post));
        if (result==false){
            throw new RuntimeException("内容存在违规信息");
        }
        this.save(post);
        return true;
    }

    @Override
    public boolean updatePostInfo(JobPost post) {
        //敏感字校验
        boolean result = wechatApiService.checkText(JsonUtils.objectToJson(post));
        if (result==false){
            throw new RuntimeException("内容存在违规信息");
        }
        return this.updateById(post);
    }

    @Transactional
    @Override
    public boolean deletePostInfo(String postId) {
        //删除相关记录 1、收藏记录，2、浏览记录，3、订单记录、4、评价记录、5、联系记录
        this.removeById(postId);
        jobBrowseService.remove(new QueryWrapper<>(new JobBrowse().setDataId(postId)));
        collectService.remove(new QueryWrapper<>(new JobCollect().setDataId(postId)));
        orderMapper.delete(new QueryWrapper<>(new JobOrder().setPostId(postId)));
        contactService.remove(new QueryWrapper<>(new JobPostContact().setPostId(postId)));
        evaluateLogService.remove(new QueryWrapper<>(new JobEvaluateLog().setPostId(postId)));
        return true;
    }

    /**
     * 到期指定结束
     */
    @Override
    public void autoOfflinePost() {
        //查询即将到期
        LambdaQueryWrapper<JobPost> queryWrapper=new LambdaQueryWrapper();
        queryWrapper.eq(JobPost::getPostStatus,BizConstants.POST_STATUS_RUNNING);
        queryWrapper.le(JobPost::getCloseTime, DateUtils.formatDate(new Date(),"yyyy-MM-dd HH:mm:ss"));
        List<JobPost> list=this.list(queryWrapper);
        for (JobPost post:list){
            post.setPostStatus(BizConstants.POST_STATUS_STOP);
            this.updateById(post);
        }
    }

    @Override
    public JobPostVo getPostDetail(String id,String userId) {
        JobPost post=this.getById(id);
        LoginUser user=sysBaseAPI.getUserById(post.getUserId());
        JobPostVo postVo=new JobPostVo();
        BeanUtils.copyProperties(post, postVo);
        //老板信息
        if (post.getPostSource()==1){
            //平台发布
            postVo.setUserAvatar(user.getAvatar());
            postVo.setUserName(post.getName());
            postVo.setUserPhone(post.getPhone());
        }else{
            //老板发布
            postVo.setUserAvatar(user.getAvatar());
            postVo.setUserName(user.getNickname());
            if (oConvertUtils.isEmpty(post.getPhone())){
                postVo.setUserPhone(user.getPhone());
            }else{
                postVo.setUserPhone(post.getPhone());
            }
        }
        //评分
        BigDecimal score=evaluateService.getUserEvaluate(user.getId(),BizConstants.ROLE_CODE_COMPANY);//作为老板的评分
        postVo.setScore(score.toString());
        //是否实名认证
        UmsRealnameAuth auth=realnameAuthService.getRealNameAuth(post.getUserId());
        if (auth!=null && auth.getAuthStatus() == BizConstants.AUTH_STATUS_SUCCESS){
            postVo.setIfRealName(true);
        }else{
            postVo.setIfRealName(false);
        }
        //是否企业认证
        JobCompany companyAuth=companyService.getCompanyAuth(post.getUserId());
        if (companyAuth!=null && companyAuth.getAuthStatus() == BizConstants.AUTH_STATUS_SUCCESS){
            postVo.setIfCompanyAuth(true);
        }else{
            postVo.setIfCompanyAuth(false);
        }

        //判断用户是否登录
        if(oConvertUtils.isNotEmpty(userId)){
            //查询是否收藏
            JobCollect collect=collectService.getCollect(userId,id,"member");
            if (collect==null){
                postVo.setIfCollected(false);
            }else{
                postVo.setIfCollected(true);
            }
            //最近拨号记录
            JobPostContact contact=contactService.getLastContact(userId,id,BizConstants.ROLE_CODE_MEMBER);
            if (contact!=null){
                postVo.setIfCalled(true);
            }else{
                postVo.setIfCalled(false);
            }
            if (post.getPostSource()!=1){
                if (contact!=null && contact.getAgreeState()==BizConstants.AGREE_STATUS_DEFAULT){
                    postVo.setIfShowAgree(true);
                    postVo.setContactId(contact.getId());
                }else{
                    postVo.setIfShowAgree(false);
                }
            }
            //生成求职意向
            JobOrder order=orderMapper.selectOne(new LambdaQueryWrapper<JobOrder>().eq(JobOrder::getUserId,userId).eq(JobOrder::getPostId,postVo.getId()));
            if (order!=null){
                postVo.setIfApply(true);
            }
            //添加浏览记录
            jobBrowseService.addBrowse(userId,id,"member");
        }

        //添加浏览记录
        if(oConvertUtils.isNotEmpty(userId)){
            jobBrowseService.addBrowse(userId,id,"member");
        }
        post.setBrowseNumber(post.getBrowseNumber()+1);
        this.updateById(post);
        return postVo;
    }

    @Override
    public JobPostVo getPostDetail(String id) {
        JobPost post=this.getById(id);
        //老板信息
        LoginUser user=sysBaseAPI.getUserById(post.getUserId());
        JobPostVo postVo=new JobPostVo();
        BeanUtils.copyProperties(post, postVo);
        postVo.setUserAvatar(user.getAvatar());
        postVo.setUserName(user.getNickname());
        postVo.setUserPhone(user.getPhone());
        //评分
        BigDecimal score=evaluateService.getUserEvaluate(user.getId(),BizConstants.ROLE_CODE_COMPANY);//作为老板的评分
        postVo.setScore(score.toString());
        //是否实名认证
        UmsRealnameAuth auth=realnameAuthService.getRealNameAuth(post.getUserId());
        if (auth!=null && BizConstants.AUTH_STATUS_SUCCESS.equals(auth.getAuthStatus())){
            postVo.setIfRealName(true);
        }else{
            postVo.setIfRealName(false);
        }
        //是否企业认证
        JobCompany companyAuth=companyService.getCompanyAuth(post.getUserId());
        if (companyAuth!=null && BizConstants.AUTH_STATUS_SUCCESS.equals(companyAuth.getAuthStatus())){
            postVo.setIfCompanyAuth(true);
        }else{
            postVo.setIfCompanyAuth(false);
        }
        return postVo;
    }

    @Override
    public boolean updatePostStatus(String id, String postStatus) {
        JobPost post=new JobPost();
        post.setId(id);
        post.setPostStatus(postStatus);
        return this.updateById(post);
    }

    @Override
    public IPage<JobPost> getPostList(Page<JobPost> page, JobPost params) {
        IPage<JobPost> pageInfo= this.page(page,new QueryWrapper<>(params));
        pageInfo.getRecords().forEach(item->{
        });
        return pageInfo;
    }

    @Override
    public IPage<JobPost> getMyPostList(Page<JobPost> page, JobPost params) {
        LambdaQueryWrapper<JobPost> queryWrapper=new LambdaQueryWrapper<>(params);
        if (params.getStatus()!=null){
            if (params.getStatus()==1){
                //招工状态：1-待审核，2-招工中，3-发布失败，4-已停招，5-已取消，6-已招满
                queryWrapper.eq(JobPost::getPostStatus,"2");
            }else if (params.getStatus()==2){
//                queryWrapper.in(JobPost::getPostStatus,"4,6");
                queryWrapper.eq(JobPost::getPostStatus,"6");
            }else if (params.getStatus()==3){
//                queryWrapper.eq(JobPost::getPostStatus,"5");
                queryWrapper.in(JobPost::getPostStatus,"4,5");
            }
        }
        queryWrapper.orderByDesc(JobPost::getCreateTime);
        IPage<JobPost> pageInfo= this.page(page,queryWrapper);
        pageInfo.getRecords().forEach(item->{
            //接单人数
            long orderCount=orderMapper.selectCount(new QueryWrapper<>(new JobOrder().setPostId(item.getId())));
            item.setOrderCount(orderCount);
            //结算人数
            long payCount=orderMapper.selectCount(new QueryWrapper<>(new JobOrder().setPostId(item.getId()).setPayStatus("1")));
            item.setSettlementCount(payCount);

        });
        return pageInfo;
    }

    @Override
    public IPage<Map<String, Object>> getPostMapList(Page<JobPost> page, JobPost params) {
        return this.baseMapper.getPostMapList(page,params);
    }

    @Override
    public IPage<Map<String, Object>> getPostMapListForAdmin(Page<JobPost> page, JobPost params) {
        return this.baseMapper.getPostMapListForAdmin(page,params);
    }
}
