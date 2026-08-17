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
 * @Description: 提现账号
 * @Author: qingkonglan
 * @Date:   2024-07-30
 * @Version: V1.0
 */
@Data
@TableName("ums_withdraw_account")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="ums_withdraw_account对象", description="提现账号")
public class UmsWithdrawAccount implements Serializable {
    private static final long serialVersionUID = 1L;

	/**ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "ID")
    private java.lang.String id;
	/**会员ID*/
	@Excel(name = "会员ID", width = 15)
    @ApiModelProperty(value = "会员ID")
    private java.lang.String userId;
	/**账户类型：0-支付宝，1-微信，2-银联*/
    @Excel(name = "提现状态:0-待审核，1-审核通过，2-审核失败", width = 15, dicCode = "account_type")
    @Dict(dicCode = "account_type")
    @ApiModelProperty(value = "账户类型：0-支付宝，1-微信，2-银联")
    private java.lang.Integer accountType;
	/**提现账号（账号信息、银行卡号等）*/
	@Excel(name = "提现账号（账号信息、银行卡号等）", width = 15)
    @ApiModelProperty(value = "提现账号（账号信息、银行卡号等）")
    private java.lang.String account;
	/**关联名称（支付宝-实名，微信-昵称，银联-持卡人）*/
	@Excel(name = "关联名称（支付宝-实名，微信-昵称，银联-持卡人）", width = 15)
    @ApiModelProperty(value = "关联名称（支付宝-实名，微信-昵称，银联-持卡人）")
    private java.lang.String realname;
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
	private String keyword;
    @TableField(exist = false)
	private String userAvatar;
    @TableField(exist = false)
	private String userName;
    @TableField(exist = false)
	private String userPhone;



}
