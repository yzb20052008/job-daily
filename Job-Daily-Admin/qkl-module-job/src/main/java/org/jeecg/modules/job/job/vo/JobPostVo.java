package org.jeecg.modules.job.job.vo;

import lombok.Data;
import org.jeecg.modules.job.job.entity.JobPost;

import java.math.BigDecimal;

@Data
public class JobPostVo extends JobPost {
    //老板姓名
    private String userName;
    //老板手机
    private String userPhone;
    //老板头像
    private String userAvatar;
    //是否收藏
    private boolean ifCollected;
    //是否实名认证
    private boolean ifRealName;
    //是否企业认证
    private boolean ifCompanyAuth;
    //是否确认合作
    private boolean ifShowAgree;
    //记录id
    private String contactId;
    //评分
    private String score;
    //是否已拨号
    private boolean ifCalled;
    //是否报名
    private boolean ifApply;
}
