package org.jeecg.modules.job.job.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 简历信息
 * @Author: qingkonglan
 * @Date:   2024-07-30
 * @Version: V1.0
 */
@Data
@TableName("job_resume")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="job_resume对象", description="简历信息")
public class JobResume implements Serializable {
    private static final long serialVersionUID = 1L;

	/**VIP id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "VIP id")
    private java.lang.String id;
	/**用户ID*/
	@Excel(name = "用户ID", width = 15)
    @ApiModelProperty(value = "用户ID")
    private java.lang.String userId;
	/**简历完善度*/
	@Excel(name = "简历完善度", width = 15)
    @ApiModelProperty(value = "简历完善度")
    private java.lang.Integer percentage;
	/**姓名*/
	@Excel(name = "姓名", width = 15)
    @ApiModelProperty(value = "姓名")
    private java.lang.String name;
	/**联系方式*/
	@Excel(name = "联系方式", width = 15)
    @ApiModelProperty(value = "联系方式")
    private java.lang.String phone;
	/**头像*/
	@Excel(name = "头像", width = 15)
    @ApiModelProperty(value = "头像")
    private java.lang.String avatar;
	/**性别：0-未知，1-男，2-女*/
	@Excel(name = "性别：0-未知，1-男，2-女", width = 15)
    @ApiModelProperty(value = "性别：0-未知，1-男，2-女")
    private java.lang.Integer sex;
	/**最高学历*/
	@Excel(name = "最高学历", width = 15)
    @ApiModelProperty(value = "最高学历")
    private java.lang.String education;
	/**出生日期*/
	@Excel(name = "出生日期", width = 15)
    @ApiModelProperty(value = "出生日期")
    private java.lang.String birthday;
	/**身高,cm*/
	@Excel(name = "身高,cm", width = 15)
    @ApiModelProperty(value = "身高,cm")
    private java.lang.Integer height;
	/**个人特长/自我介绍*/
	@Excel(name = "个人特长/自我介绍", width = 15)
    @ApiModelProperty(value = "个人特长/自我介绍")
    private java.lang.String personalSkill;
    /**熟练度：小工，中工、大工*/
    @Excel(name = "熟练度", width = 15)
    @ApiModelProperty(value = "熟练度")
    private java.lang.String skilled;
	/**技能标签*/
	@Excel(name = "技能标签", width = 15)
    @ApiModelProperty(value = "技能标签")
    private java.lang.String skills;
	/**工龄*/
	@Excel(name = "工龄", width = 15)
    @ApiModelProperty(value = "工龄")
    private java.lang.Integer workYear;
	/**邮箱地址*/
	@Excel(name = "邮箱地址", width = 15)
    @ApiModelProperty(value = "邮箱地址")
    private java.lang.String email;
	/**求职状态:1-正在找工作，2-暂不找工作*/
	@Excel(name = "求职状态", width = 15)
    @ApiModelProperty(value = "求职状态")
    private java.lang.String jobStatus;
	/**熟练工种*/
	@Excel(name = "熟练工种", width = 15)
    @ApiModelProperty(value = "熟练工种")
    private java.lang.String jobTypes;
	/**期望工作地*/
	@Excel(name = "期望工作地", width = 15)
    @ApiModelProperty(value = "期望工作地")
    private java.lang.String expectCity;
	/**常住地*/
	@Excel(name = "常住地", width = 15)
    @ApiModelProperty(value = "常住地")
    private java.lang.String addressName;
    /**常住地*/
    @Excel(name = "常住地", width = 15)
    @ApiModelProperty(value = "常住地")
    private java.lang.String address;
	/**常住地纬度*/
	@Excel(name = "常住地纬度", width = 15)
    @ApiModelProperty(value = "常住地纬度")
    private java.lang.String addressLat;
	/**常住地经度*/
	@Excel(name = "常住地经度", width = 15)
    @ApiModelProperty(value = "常住地经度")
    private java.lang.String addressLng;
	/**内容描述*/
	@Excel(name = "内容描述", width = 15)
    @ApiModelProperty(value = "内容描述")
    private java.lang.String remark;
	/**排序*/
	@Excel(name = "排序", width = 15)
    @ApiModelProperty(value = "排序")
    private java.lang.Integer sort;
	/**启用状态*/
	@Excel(name = "启用状态", width = 15)
    @ApiModelProperty(value = "启用状态")
    private java.lang.Integer status;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
	/**修改人*/
    @ApiModelProperty(value = "修改人")
    private java.lang.String updateBy;
	/**修改时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
    private java.util.Date updateTime;
	/**所属部门编码*/
    @ApiModelProperty(value = "所属部门编码")
    private java.lang.String sysOrgCode;
	/**tenantId*/
	@Excel(name = "tenantId", width = 15)
    @ApiModelProperty(value = "tenantId")
    private java.lang.Integer tenantId;
	/**删除状态(0-正常,1-已删除)*/
	@Excel(name = "删除状态(0-正常,1-已删除)", width = 15)
    @ApiModelProperty(value = "删除状态(0-正常,1-已删除)")
    private java.lang.Integer delFlag;

    /**县级城市名称*/
    @ApiModelProperty(value = "县级城市名称")
    private java.lang.String city;
    /**县级城市编码*/
    @ApiModelProperty(value = "县级城市编码")
    private java.lang.String cityCode;
    /**市级城市名称*/
    @ApiModelProperty(value = "市级城市名称")
    private java.lang.String pCity;
    /**市级城市编码*/
    @ApiModelProperty(value = "市级城市编码")
    private java.lang.String pCityCode;

	@TableField(exist = false)
    private String keyword;
    @TableField(exist = false)
    private String orderBy;//all、new、near
    @TableField(exist = false)
    private String latitude;
    @TableField(exist = false)
    private String longitude;
    @TableField(exist = false)
    private String typeIds;
    @TableField(exist = false)
    private String typeCodes;

}
