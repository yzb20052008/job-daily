package org.jeecg.modules.job.job.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.job.base.entity.BaseConfig;
import org.jeecg.modules.job.base.service.IBaseConfigService;
import org.jeecg.modules.job.constant.BizConstants;
import org.jeecg.modules.job.job.entity.JobCompany;
import org.jeecg.modules.job.job.mapper.JobCompanyMapper;
import org.jeecg.modules.job.job.service.IJobCompanyService;
import org.jeecg.modules.job.ums.entity.UmsRealnameAuth;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @Description: 企业认证
 * @Author: qingkonglan
 * @Date:   2024-07-30
 * @Version: V1.0
 */
@Service
public class JobCompanyServiceImpl extends ServiceImpl<JobCompanyMapper, JobCompany> implements IJobCompanyService {

    @Resource
    private IBaseConfigService configService;

    @Override
    public boolean addOrUpdateCompanyAuth(JobCompany company) {
        company.setAuthStatus(BizConstants.AUTH_STATUS_DEFAULT);
        if (oConvertUtils.isEmpty(company.getId())) {
            //添加
            JobCompany auth=this.getCompanyAuth(company.getUserId());
            if (auth!=null){
                throw new RuntimeException("已提交过企业认证");
            }
            this.save(company);
        } else {
            //更新
            this.updateById(company);
        }
        BaseConfig max=configService.getConfigByCode(BizConstants.COMPANY_AUTH);
        int ifOpen=Integer.parseInt(max.getConfigValue());//1-人工，2-自动
        if (ifOpen==2){
            //开始自动审核

        }
        return true;
    }

    @Override
    public JobCompany getCompanyAuth(String userId) {
        return this.getOne(new LambdaQueryWrapper<JobCompany>().eq(JobCompany::getUserId,userId));
    }

    @Transactional
    @Override
    public boolean updateStatus(String id, int status, String reason) {
        JobCompany info=new JobCompany();
        info.setId(id);
        info.setAuthStatus(status);
        info.setAuthRemark(reason);
        if (status > 0){
            info.setAuthTime(new Date());
        }
        this.updateById(info);
        //短信通知
//        try{
//            LoginUser user=sysBaseAPI.getUserById(id);
//            if (status== BizConstants.AUTH_STATUS_SUCCESS){
//                //审核通过
//                TsSmsHelper.sendSms(user.getPhone(),new String[]{user.getNickname()}, TsSmsEnum.VERIFY_SUCCESS_CODE);
//            }else if(status== BizConstants.AUTH_STATUS_FAILURE){
//                //审核失败
//                TsSmsHelper.sendSms(user.getPhone(),new String[]{user.getNickname()}, TsSmsEnum.VERIF_FAILURE_CODE);
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        return true;
    }

    @Override
    public IPage<JobCompany> getCompanyAuthList(Page<JobCompany> page, JobCompany params) {
        return baseMapper.getCompanyAuthList(page,params);
    }
}
