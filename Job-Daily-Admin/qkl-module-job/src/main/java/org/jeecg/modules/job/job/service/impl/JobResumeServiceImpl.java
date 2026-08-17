package org.jeecg.modules.job.job.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.job.api.model.UserLocation;
import org.jeecg.modules.job.constant.BizConstants;
import org.jeecg.modules.job.home.service.WechatApiService;
import org.jeecg.modules.job.job.entity.*;
import org.jeecg.modules.job.job.mapper.JobResumeMapper;
import org.jeecg.modules.job.job.service.*;
import org.jeecg.modules.job.job.vo.JobPostVo;
import org.jeecg.modules.job.job.vo.JobResumeVo;
import org.jeecg.modules.job.ums.entity.UmsRealnameAuth;
import org.jeecg.modules.job.ums.service.IUmsRealnameAuthService;
import org.jeecg.modules.job.utils.JsonUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Description: 简历信息
 * @Author: qingkonglan
 * @Date:   2024-07-30
 * @Version: V1.0
 */
@Service
public class JobResumeServiceImpl extends ServiceImpl<JobResumeMapper, JobResume> implements IJobResumeService {

    @Resource
    private IJobResumeIntentionService intentionService;
    @Resource
    private IJobResumeCertService certService;
    @Resource
    private IJobResumeExpeService expService;
    @Resource
    private ISysBaseAPI sysBaseAPI;
    @Resource
    private IUmsRealnameAuthService realnameAuthService;
    @Resource
    private IJobCollectService collectService;
    @Resource
    private WechatApiService wechatApiService;

    @Override
    public boolean createDefaultResume(LoginUser sysUser, UserLocation userLocation) {
        JobResume resume=this.getResumeInfo(sysUser.getId());
        if (resume==null){
            resume=new JobResume();
            resume.setAvatar(sysUser.getAvatar());
            resume.setUserId(sysUser.getId());
            resume.setName(sysUser.getNickname());
            resume.setJobStatus("1");
            resume.setCity(userLocation.getCity());
            resume.setCityCode(userLocation.getCityCode());
            resume.setPCity(userLocation.getPCity());
            resume.setPCityCode(userLocation.getPCityCode());
            resume.setAddress(userLocation.getAddress());
            resume.setAddressName(userLocation.getAddress());
            resume.setAddressLat(userLocation.getLatitude());
            resume.setAddressLng(userLocation.getLongitude());
            resume.setPhone(sysUser.getPhone());
            resume.setWorkYear(5);//默认五年
            resume.setSex(1);
            resume.setPercentage(10);
            resume.setPersonalSkill("");
            this.save(resume);
            //添加期望工作
            JobResumeIntention intention=new JobResumeIntention();
            intention.setResumeId(resume.getId());
            intention.setUserId(sysUser.getId());
            intention.setCity(userLocation.getCity());
            intention.setWorkCity(userLocation.getCity());
            intention.setCityCode(userLocation.getCityCode());
            intention.setPCity(userLocation.getPCity());
            intention.setPCityCode(userLocation.getPCityCode());
            intention.setSalaryUnit("面议");
            intention.setExpectSalary("");
            intention.setTypeIds("1853604943580229633");
            intention.setTypeNames("普工");
            intentionService.updateResumeIntention(intention);
            return true;
        }
        return false;
    }

    @Override
    public void updateResumePercentage(String userId, int num) {
        //判断简历是否存在
        JobResume resume=this.getOne(new QueryWrapper<>(new JobResume().setUserId(userId)));
        if (resume==null){
            resume=new JobResume();
            LoginUser user=sysBaseAPI.getUserById(userId);
            resume.setUserId(userId);
            resume.setPhone(user.getPhone());
            resume.setName(user.getNickname());
            resume.setSex(user.getSex());
            resume.setPercentage(10);
            this.save(resume);
        }
        //更新完善度
        int newPercent=resume.getPercentage()+num;
        if (newPercent>100){
            newPercent=100;
        }
        resume.setPercentage(newPercent);
        this.updateById(resume);
    }

    @Override
    public boolean updateResume(JobResume resume) {
        //敏感字校验
        boolean ifPass = wechatApiService.checkText(JsonUtils.objectToJson(resume));
        if (ifPass==false){
            throw new RuntimeException("内容存在违规信息");
        }
        //查询是否存在
        JobResume result=this.getOne(new QueryWrapper<>(new JobResume().setUserId(resume.getUserId())));
        if (result!=null){
            resume.setId(result.getId());
            this.updateById(resume);
        }else{
            this.save(resume);
            //更新完善度
            this.updateResumePercentage(resume.getUserId(),30);
        }
        LoginUser user=new LoginUser();
        user.setId(resume.getUserId());
//        if(oConvertUtils.isNotEmpty(resume.getAvatar())){
            user.setAvatar(resume.getAvatar());
//        }
        if(oConvertUtils.isNotEmpty(resume.getName())){
            user.setNickname(resume.getName());
        }
        if(oConvertUtils.isNotEmpty(resume.getSex())){
            user.setSex(resume.getSex());
        }
        if(oConvertUtils.isNotEmpty(resume.getBirthday())){
            try{
                user.setBirthday(DateUtils.parseDate(resume.getBirthday(),"yyyy-MM-dd"));
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        sysBaseAPI.updateUserInfo(user);
        return true;
    }

    @Override
    public JobResumeVo getResumeInfo(String userId) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        JobResume resume=this.getOne(new QueryWrapper<>(new JobResume().setUserId(userId)));
        if (resume==null){
            return null;
        }
        JobResumeVo resumeVo=new JobResumeVo();
        BeanUtils.copyProperties(resume, resumeVo);
        //求职意向
        JobResumeIntention intention=intentionService.getResumeIntention(userId);
        resumeVo.setIntention(intention);
        //项目经验
        List<JobResumeExpe> expList=expService.getResumeExp(userId);
        resumeVo.setExpList(expList);
        //技能证书
        List<JobResumeCert> certList=certService.getResumeCert(userId);
        resumeVo.setCertList(certList);
        //是否实名认证
        UmsRealnameAuth auth=realnameAuthService.getRealNameAuth(userId);
        if (auth!=null && auth.getAuthStatus() == BizConstants.AUTH_STATUS_SUCCESS){
            resumeVo.setIfRealName(true);
        }else{
            resumeVo.setIfRealName(false);
        }
        //查询是否收藏
        JobCollect collect=collectService.getCollect(user.getId(),resume.getId(),"company");
        if (collect==null){
            resumeVo.setIfCollected(false);
        }else{
            resumeVo.setIfCollected(true);
        }
        return resumeVo;
    }

    @Override
    public JobResumeVo getResumeById(String id) {
        JobResume resume=this.getById(id);
        if (resume==null){
            return null;
        }
        JobResumeVo resumeVo=new JobResumeVo();
        BeanUtils.copyProperties(resume, resumeVo);
        //求职意向
        JobResumeIntention intention=intentionService.getResumeIntention(resume.getUserId());
        resumeVo.setIntention(intention);
        //项目经验
        List<JobResumeExpe> expList=expService.getResumeExp(resume.getUserId());
        resumeVo.setExpList(expList);
        //技能证书
        List<JobResumeCert> certList=certService.getResumeCert(resume.getUserId());
        resumeVo.setCertList(certList);
        //是否实名认证
        UmsRealnameAuth auth=realnameAuthService.getRealNameAuth(resume.getUserId());
        if (auth!=null && auth.getAuthStatus() == BizConstants.AUTH_STATUS_SUCCESS){
            resumeVo.setIfRealName(true);
        }else{
            resumeVo.setIfRealName(false);
        }
        return resumeVo;
    }

    @Override
    public IPage<Map<String,Object>> getResumeList(Page<JobResume> page, JobResume params) {
        IPage<Map<String,Object>> pageInfo=baseMapper.getResumeList(page,params);
        return pageInfo;
    }

    @Override
    public IPage<Map<String, Object>> getResumeListForAdmin(Page<JobResume> page, JobResume params) {
        IPage<Map<String,Object>> pageInfo=baseMapper.getResumeListForAdmin(page,params);
        return pageInfo;
    }

}
