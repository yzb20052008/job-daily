package org.jeecg.modules.job.utils.pdf;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class SubResumeVo {

    private static final long serialVersionUID = 1L;

    //工作经历
    private String workExpList;
    //教育经历
    private String eduExpList;
    //求职期望
    private String jobExpectList;
    //项目经历
    private String proExpList;

    private String address;

    /**
     * 会员名称
     */
    private String name;

    /**
     * 联系方式
     */
    private String phone;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 性别
     */
    private String sex;


    /**
     * 最高学历
     */
    private String education;

    /**
     * 开始工作时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String timeToWork;

    /**
     * 工作年限
     */
    private String workYear;

    /**
     * 出生日期
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String birthday;


    /**
     * 个人特长
     */
    private String personalSkill;


    /**
     * 求职状态
     */
    private String jobStatus;

    /**
     * 邮箱地址
     */
    private String email;

}
