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
 * @Description: 订单信息
 * @Author: qingkonglan
 * @Date:   2024-07-30
 * @Version: V1.0
 */
@Data
@TableName("job_order")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="job_order对象", description="订单信息")
public class JobOrder implements Serializable {
    private static final long serialVersionUID = 1L;

	/**VIP id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "VIP id")
    private java.lang.String id;
    /**订单编码*/
    @Excel(name = "订单编码", width = 15)
    @ApiModelProperty(value = "订单编码")
    private java.lang.String orderSn;
	/**用户ID*/
	@Excel(name = "用户ID", width = 15)
    @ApiModelProperty(value = "用户ID")
    private java.lang.String userId;
	/**老板ID*/
	@Excel(name = "老板ID", width = 15)
    @ApiModelProperty(value = "老板ID")
    private java.lang.String postUserId;
	/**招工ID*/
	@Excel(name = "招工ID", width = 15)
    @ApiModelProperty(value = "招工ID")
    private java.lang.String postId;
	/**是否电话联系*/
	@Excel(name = "是否电话联系", width = 15)
    @ApiModelProperty(value = "是否电话联系")
    private java.lang.Integer ifCalled;
	/**开始工作时间*/
	@Excel(name = "开始工作时间", width = 15)
    @ApiModelProperty(value = "开始工作时间")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    private java.util.Date startTime;
	/**工作结束时间*/
	@Excel(name = "工作结束时间", width = 15)
    @ApiModelProperty(value = "工作结束时间")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    private java.util.Date endTime;
	/**单价（元/时）*/
	@Excel(name = "单价（元/时）", width = 15)
    @ApiModelProperty(value = "单价（元/时）")
    private java.math.BigDecimal unitPrice;
	/**时长（小时）*/
	@Excel(name = "时长（小时）", width = 15)
    @ApiModelProperty(value = "时长（小时）")
    private java.lang.Integer duration;
	/**应付金额*/
	@Excel(name = "应付金额", width = 15)
    @ApiModelProperty(value = "应付金额")
    private java.math.BigDecimal payableAmount;
	/**实付金额*/
	@Excel(name = "实付金额", width = 15)
    @ApiModelProperty(value = "实付金额")
    private java.math.BigDecimal amount;
	/**订单状态：0-待确认，1-待开工，2-工作中，3-待结算，4-待评价，5-已完成，6-已取消*/
	@Excel(name = "订单状态：0-待确认，1-待开工，2-工作中，3-待结算，4-待评价，5-已完成，6-已取消", width = 15)
    @ApiModelProperty(value = "订单状态：0-待确认，1-待开工，2-工作中，3-待结算，4-待评价，5-已完成，6-已取消")
    private java.lang.String orderStatus;

      /**工人评价状态：1-已评价，0-未评价*/
    @Excel(name = "工人评价状态", width = 15)
    @ApiModelProperty(value = "工人评价状态")
    private java.lang.Integer userEvaluate;

    /**老板评价状态：1-已评价，0-未评价*/
    @Excel(name = "老板评价状态", width = 15)
    @ApiModelProperty(value = "老板评价状态")
    private java.lang.Integer companyEvaluate;

    /**支付方式*/
    @Excel(name = "支付方式", width = 15)
    @ApiModelProperty(value = "支付方式")
    private java.lang.String payType;
    /**结算状态*/
    @Excel(name = "结算状态", width = 15)
    @ApiModelProperty(value = "结算状态")
    private java.lang.String payStatus;
	/**描述*/
	@Excel(name = "描述", width = 15)
    @ApiModelProperty(value = "描述")
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
    /**确认时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date ensureTime;
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
    @TableLogic
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

}
