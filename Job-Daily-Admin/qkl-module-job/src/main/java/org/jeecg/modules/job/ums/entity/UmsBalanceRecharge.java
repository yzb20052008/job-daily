package org.jeecg.modules.job.ums.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 余额充值单
 */
@Data
@TableName("ums_balance_recharge")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "ums_balance_recharge", description = "余额充值单")
public class UmsBalanceRecharge implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;

    @Excel(name = "用户ID", width = 15)
    @ApiModelProperty(value = "用户ID")
    private String userId;

    @Excel(name = "商户订单号", width = 20)
    @ApiModelProperty(value = "商户订单号")
    private String orderSn;

    @Excel(name = "充值金额", width = 15)
    @ApiModelProperty(value = "充值金额")
    private BigDecimal money;

    @Excel(name = "支付方式", width = 15)
    @ApiModelProperty(value = "支付方式")
    private String payType;

    /** 0未支付 1成功 2失败 */
    @Excel(name = "充值状态", width = 15)
    @ApiModelProperty(value = "充值状态")
    private String rechargeStatus;

    private String createBy;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private String updateBy;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    private String remark;
    private String sysOrgCode;
    private Integer tenantId;
    private Integer delFlag;
}
