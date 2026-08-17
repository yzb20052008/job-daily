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
 * @Description: 账号流水
 * @Author: qingkonglan
 * @Date:   2022-12-23
 * @Version: V1.0
 */
@Data
@TableName("ums_account_records")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="ums_account_records对象", description="账号流水")
public class UmsAccountRecords implements Serializable {
    private static final long serialVersionUID = 1L;

	/**账户id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "账户id")
    private java.lang.String id;
	/**会员id*/
	@Excel(name = "会员id", width = 15)
    @ApiModelProperty(value = "会员id")
    private java.lang.String userId;
	/**账户类型*/
	@Excel(name = "账户类型", width = 15, dicCode = "account_type")
	@Dict(dicCode = "account_type")
    @ApiModelProperty(value = "账户类型")
    private java.lang.Integer accountType;
    /**交易类型*/
    @Excel(name = "交易类型", width = 15, dicCode = "trade_type")
    @Dict(dicCode = "trade_type")
    @ApiModelProperty(value = "交易类型")
    private java.lang.Integer tradeType;
	/**正负号*/
	@Excel(name = "正负号", width = 15, dicCode = "add_sign")
	@Dict(dicCode = "add_sign")
    @ApiModelProperty(value = "正负号")
    private java.lang.String addSign;
	/**金额*/
	@Excel(name = "金额", width = 15)
    @ApiModelProperty(value = "金额")
    private java.math.BigDecimal money;
	/**说明信息*/
	@Excel(name = "说明信息", width = 15)
    @ApiModelProperty(value = "说明信息")
    private java.lang.String content;
	/**关联数据，如关联提现记录等*/
	@Excel(name = "关联数据，如关联提现记录等", width = 15)
    @ApiModelProperty(value = "关联数据，如关联提现记录等")
    private java.lang.String dataId;
	/**天加时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "天加时间")
    private java.util.Date createTime;
	/**创建者*/
    @ApiModelProperty(value = "创建者")
    private java.lang.String createBy;
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
    private String searchTime;
}
