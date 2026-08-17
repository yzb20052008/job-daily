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
 * @Description: 会员账户
 * @Author: qingkonglan
 * @Date:   2022-12-23
 * @Version: V1.0
 */
@Data
@TableName("ums_account")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="ums_account对象", description="会员账户")
public class UmsAccount implements Serializable {
    private static final long serialVersionUID = 1L;

	/**账户ID*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "ID")
    private java.lang.String id;
	/**会员ID*/
	@Excel(name = "会员ID", width = 15)
    @ApiModelProperty(value = "会员ID")
    private java.lang.String userId;
	/**冻结余额*/
	@Excel(name = "冻结余额", width = 15)
    @ApiModelProperty(value = "冻结余额")
    private java.math.BigDecimal balanceFrozen;
	/**可提余额*/
	@Excel(name = "可提余额", width = 15)
    @ApiModelProperty(value = "可提余额")
    private java.math.BigDecimal balanceWithdraw;
	/**可用余额*/
	@Excel(name = "可用余额", width = 15)
    @ApiModelProperty(value = "可用余额")
    private java.math.BigDecimal balance;
	/**充值总额*/
	@Excel(name = "充值总额", width = 15)
    @ApiModelProperty(value = "充值总额")
    private java.math.BigDecimal totalRecharge;
	/**提现总额*/
	@Excel(name = "提现总额", width = 15)
    @ApiModelProperty(value = "提现总额")
    private java.math.BigDecimal totalWithdraw;
	/**消费总额*/
	@Excel(name = "消费总额", width = 15)
    @ApiModelProperty(value = "消费总额")
    private java.math.BigDecimal totalConsume;
	/**会员积分*/
	@Excel(name = "会员积分", width = 15)
    @ApiModelProperty(value = "会员积分")
    private java.lang.Integer integral;
	/**累计积分*/
	@Excel(name = "累计积分", width = 15)
    @ApiModelProperty(value = "累计积分")
    private java.lang.Integer totalIntegral;
	/**添加时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "添加时间")
    private java.util.Date createTime;
	/**修改时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "修改时间")
    private java.util.Date updateTime;
	/**创建者*/
    @ApiModelProperty(value = "创建者")
    private java.lang.String createBy;
	/**更新者*/
    @ApiModelProperty(value = "更新者")
    private java.lang.String updateBy;
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


    //汇总收入
    @TableField(exist = false)
    private BigDecimal totalMoney;
}
