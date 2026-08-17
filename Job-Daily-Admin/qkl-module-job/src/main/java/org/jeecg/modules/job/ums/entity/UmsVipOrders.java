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
 * @Description: VIP订单
 * @Author: qingkonglan
 * @Date:   2022-12-18
 * @Version: V1.0
 */
@Data
@TableName("ums_vip_orders")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="ums_vip_orders对象", description="VIP订单")
public class UmsVipOrders implements Serializable {
    private static final long serialVersionUID = 1L;

	/**订单ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "订单ID")
    private java.lang.String id;
	/**订单编号*/
	@Excel(name = "订单编号", width = 15)
    @ApiModelProperty(value = "订单编号")
    private java.lang.String ordersSn;
	/**购买会员id*/
	@Excel(name = "购买会员id", width = 15)
    @ApiModelProperty(value = "购买会员id")
    private java.lang.String memberId;
	/**VIP id*/
	@Excel(name = "VIP id", width = 15)
    @ApiModelProperty(value = "VIP id")
    private java.lang.String vipId;
	/**VIP名称*/
	@Excel(name = "VIP名称", width = 15)
    @ApiModelProperty(value = "VIP名称")
    private java.lang.String vipName;
	/**订单原价*/
	@Excel(name = "订单原价", width = 15)
    @ApiModelProperty(value = "订单原价")
    private java.math.BigDecimal totalAmount;
	/**订单实付*/
	@Excel(name = "订单实付", width = 15)
    @ApiModelProperty(value = "订单实付")
    private java.math.BigDecimal payAmount;
	/**支付方式*/
	@Excel(name = "支付方式", width = 15)
    @ApiModelProperty(value = "支付方式")
    private java.lang.String payType;
	/**订单状态*/
	@Excel(name = "订单状态", width = 15)
    @ApiModelProperty(value = "订单状态")
    private java.lang.Integer ordersStatus;
	/**订单备注*/
	@Excel(name = "订单备注", width = 15)
    @ApiModelProperty(value = "订单备注")
    private java.lang.String note;
	/**订单来源*/
	@Excel(name = "订单来源", width = 15)
    @ApiModelProperty(value = "订单来源")
    private java.lang.Integer sourceType;
	/**软件版本*/
	@Excel(name = "软件版本", width = 15)
    @ApiModelProperty(value = "软件版本")
    private java.lang.String sourceVersion;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
	/**修改人*/
    @ApiModelProperty(value = "修改人")
    private java.lang.String updateBy;
	/**修改时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
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
	@TableField(exist = false)
	private String keyword;
}
