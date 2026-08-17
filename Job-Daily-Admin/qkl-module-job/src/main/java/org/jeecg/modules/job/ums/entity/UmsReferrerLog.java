package org.jeecg.modules.job.ums.entity;

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
 * @Description: 邀请记录
 * @Author: qingkonglan
 * @Date:   2023-09-03
 * @Version: V1.0
 */
@Data
@TableName("ums_referrer_log")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="ums_referrer_log对象", description="邀请记录")
public class UmsReferrerLog implements Serializable {
    private static final long serialVersionUID = 1L;

	/**ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "ID")
    private java.lang.String id;
	/**新注册用户ID*/
	@Excel(name = "新注册用户ID", width = 15)
    @ApiModelProperty(value = "新注册用户ID")
    private java.lang.String memberId;
	/**推荐人用户ID*/
	@Excel(name = "推荐人用户ID", width = 15)
    @ApiModelProperty(value = "推荐人用户ID")
    private java.lang.String referrerId;
	/**奖励积分*/
	@Excel(name = "奖励积分", width = 15)
    @ApiModelProperty(value = "奖励积分")
    private java.lang.Integer integral;
	/**是否启用*/
	@Excel(name = "是否启用", width = 15)
    @ApiModelProperty(value = "是否启用")
    private java.lang.String status;
	/**排序*/
	@Excel(name = "排序", width = 15)
    @ApiModelProperty(value = "排序")
    private java.lang.Integer sort;
	/**创建者*/
    @ApiModelProperty(value = "创建者")
    private java.lang.String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
	/**更新者*/
    @ApiModelProperty(value = "更新者")
    private java.lang.String updateBy;
	/**更新时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
    private java.util.Date updateTime;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private java.lang.String remark;
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

    @TableField(exist = false)
	private String userName;
    @TableField(exist = false)
	private String userAvatar;
    @TableField(exist = false)
	private String userPhone;
    @TableField(exist = false)
	private String referrerName;
    @TableField(exist = false)
	private String referrerAvatar;
    @TableField(exist = false)
	private String referrerPhone;
    @TableField(exist = false)
	private String keyword;
    @TableField(exist = false)
    private long integralCount;
    @TableField(exist = false)
    private long totalNumber;

}
