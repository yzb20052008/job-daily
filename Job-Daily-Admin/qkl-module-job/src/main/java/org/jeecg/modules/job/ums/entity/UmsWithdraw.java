package org.jeecg.modules.job.ums.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
 * @Description: 用户提现
 * @Author: qingkonglan
 * @Date:   2023-03-30
 * @Version: V1.0
 */
@Data
@TableName("ums_withdraw")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="ums_withdraw对象", description="用户提现")
public class UmsWithdraw implements Serializable {
    private static final long serialVersionUID = 1L;

	/**ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "ID")
    private java.lang.String id;
	/**会员ID*/
	@Excel(name = "会员ID", width = 15)
    @ApiModelProperty(value = "会员ID")
    private java.lang.String userId;
    /**商户单号*/
    @Excel(name = "商户单号", width = 15)
    @ApiModelProperty(value = "商户单号")
    private java.lang.String outBillNo;
	/**提现金额*/
	@Excel(name = "提现金额", width = 15)
    @ApiModelProperty(value = "提现金额")
    private java.math.BigDecimal money;
	/**提现前余额*/
	@Excel(name = "提现前余额", width = 15)
    @ApiModelProperty(value = "提现前余额")
    private java.math.BigDecimal lastBalance;
	/**提现后余额*/
	@Excel(name = "提现后余额", width = 15)
    @ApiModelProperty(value = "提现后余额")
    private java.math.BigDecimal balance;
	/**提现状态:0-待审核，1-审核通过，2-审核失败*/
	@Excel(name = "提现状态:0-待审核，1-审核通过，2-审核失败", width = 15, dicCode = "withdraw_status")
	@Dict(dicCode = "withdraw_status")
    @ApiModelProperty(value = "提现状态:0-待审核，1-审核通过，2-审核失败")
    private java.lang.Integer withdrawStatus;
	/**提现失败原因*/
	@Excel(name = "提现失败原因", width = 15)
    @ApiModelProperty(value = "提现失败原因")
    private java.lang.String reason;
	/**账户类型：0-支付宝，1-微信，2-银联*/
	@Excel(name = "账户类型：0-支付宝，1-微信，2-银联", width = 15, dicCode = "account_type")
	@Dict(dicCode = "account_type")
    @ApiModelProperty(value = "账户类型：0-支付宝，1-微信，2-银联")
    private java.lang.Integer accountType;
	/**提现账号（账号信息、银行卡号等）*/
	@Excel(name = "提现账号（账号信息、银行卡号等）", width = 15)
    @ApiModelProperty(value = "提现账号（账号信息、银行卡号等）")
    private java.lang.String withdrawAccount;
	/**关联名称（支付宝-实名，微信-昵称，银联-持卡人）*/
	@Excel(name = "关联名称（支付宝-实名，微信-昵称，银联-持卡人）", width = 15)
    @ApiModelProperty(value = "关联名称（支付宝-实名，微信-昵称，银联-持卡人）")
    private java.lang.String withdrawName;
	/**银行名称*/
	@Excel(name = "银行名称", width = 15)
    @ApiModelProperty(value = "银行名称")
    private java.lang.String bankName;
	/**支行信息*/
	@Excel(name = "支行信息", width = 15)
    @ApiModelProperty(value = "支行信息")
    private java.lang.String bankBranchName;

    /**转账状态：ACCEPTED-已受理，PROCESSING-转账锁定资金中，WAIT_USER_CONFIRM-待收款用户确认，TRANSFERING-转账中，SUCCESS-转账成功，FAIL- 转账失败，CANCELING-转账撤销中，CANCELLED-转账撤销完成*/
    @Excel(name = "转账状态", width = 15)
    @ApiModelProperty(value = "转账状态")
    private java.lang.String transferStatus;

    /**跳转领取页面的package信息*/
    @Excel(name = "跳转领取页面的package信息", width = 15)
    @ApiModelProperty(value = "跳转领取页面的package信息")
    private java.lang.String packageInfo;

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
    private String startDate;
    @TableField(exist = false)
    private String endDate;
    @TableField(exist = false)
    private String userName;
    @TableField(exist = false)
    private String userPhone;
    @TableField(exist = false)
    private String userAvatar;

    @TableField(exist = false)
    private String appId;
    @TableField(exist = false)
    private String mchId;
}
