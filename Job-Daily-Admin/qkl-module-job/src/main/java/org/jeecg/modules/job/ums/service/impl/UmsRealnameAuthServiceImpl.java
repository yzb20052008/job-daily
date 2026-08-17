package org.jeecg.modules.job.ums.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.common.util.sms.TsSmsEnum;
import org.jeecg.common.util.sms.TsSmsHelper;
import org.jeecg.modules.job.api.util.WxUtil;
import org.jeecg.modules.job.base.entity.BaseConfig;
import org.jeecg.modules.job.base.service.IBaseConfigService;
import org.jeecg.modules.job.constant.BizConstants;
import org.jeecg.modules.job.integral.service.IIntegralLogService;
import org.jeecg.modules.job.ums.entity.UmsRealnameAuth;
import org.jeecg.modules.job.ums.mapper.UmsRealnameAuthMapper;
import org.jeecg.modules.job.ums.service.IUmsRealnameAuthService;
import org.jeecg.modules.job.utils.WX_TemplateMsgUtil;
import org.jeecg.modules.job.utils.WxMsgSendUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @Description: 实名认证
 * @Author: qingkonglan
 * @Date: 2024-07-30
 * @Version: V1.0
 */
@Service
public class UmsRealnameAuthServiceImpl extends ServiceImpl<UmsRealnameAuthMapper, UmsRealnameAuth> implements IUmsRealnameAuthService {

    @Resource
    private IBaseConfigService configService;
    @Resource
    private ISysBaseAPI sysBaseAPI;
    @Resource
    private IIntegralLogService integralLogService;

    @Override
    public boolean addOrUpdateRealNameAuth(UmsRealnameAuth realnameAuth) {
        realnameAuth.setAuthStatus(BizConstants.AUTH_STATUS_DEFAULT);
        if (oConvertUtils.isEmpty(realnameAuth.getId())) {
            //添加
            UmsRealnameAuth auth=getRealNameAuth(realnameAuth.getUserId());
            if (auth!=null){
                throw new RuntimeException("已提交过实名认证");
            }
            this.save(realnameAuth);
        } else {
            //更新
            this.updateById(realnameAuth);
        }
        BaseConfig max=configService.getConfigByCode(BizConstants.REALNAME_AUTH);
        int ifOpen=Integer.parseInt(max.getConfigValue());//1-人工，2-自动
        if (ifOpen==2){
            //开始自动审核
        }
        return true;
    }

    @Transactional
    @Override
    public boolean updateStatus(String id, int status, String reason) {
        UmsRealnameAuth info=this.getById(id);
        info.setAuthStatus(status);
        info.setAuthRemark(reason);
        if (status > 0){
            info.setAuthTime(new Date());
        }
        this.updateById(info);
        LoginUser userInfo=sysBaseAPI.getUserById(info.getUserId());
        if (status==1){
            //认证通过,更新实名信息
            LoginUser user=new LoginUser();
            user.setId(info.getUserId());
            user.setRealname(info.getRealname());
            sysBaseAPI.updateUserInfo(user);
            //积分
            integralLogService.addIntegralLog(info.getUserId(),BizConstants.JF_USER_VERIFY,info.getId(),"实名认证成功获得积分：");
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
            //推送订阅通知
            try{
                String token= WX_TemplateMsgUtil.getAccessToken(WxUtil.getAppId(),WxUtil.getAppSecret());
                String page = "pages/index/index";
                WxMsgSendUtils.sendMsgForAuth("用户实名认证","认证通过","可进入小程序体验更多功能",userInfo.getThirdId(),token,page);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            try{
                String token= WX_TemplateMsgUtil.getAccessToken(WxUtil.getAppId(),WxUtil.getAppSecret());
                String page = "pages/index/index";
                WxMsgSendUtils.sendMsgForAuth("用户实名认证","认证通过","可进入小程序体验更多功能",userInfo.getThirdId(),token,page);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public UmsRealnameAuth getRealNameAuth(String userId) {
        return this.getOne(new LambdaQueryWrapper<UmsRealnameAuth>().eq(UmsRealnameAuth::getUserId,userId));
    }

    @Override
    public IPage<UmsRealnameAuth> getRealNameAuthList(Page<UmsRealnameAuth> page, UmsRealnameAuth params) {
        return baseMapper.getRealNameAuthList(page,params);
    }
}
