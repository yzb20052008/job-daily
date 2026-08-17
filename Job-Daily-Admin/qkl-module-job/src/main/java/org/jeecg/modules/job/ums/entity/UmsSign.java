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
 * @Description: 签到记录
 * @Author: qingkonglan
 * @Date:   2023-11-15
 * @Version: V1.0
 */
@Data
@TableName("ums_sign")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="ums_sign对象", description="签到记录")
public class UmsSign implements Serializable {
    private static final long serialVersionUID = 1L;

	/**VIP id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "VIP id")
    private java.lang.String id;
	/**用户ID*/
	@Excel(name = "用户ID", width = 15)
    @ApiModelProperty(value = "用户ID")
    private java.lang.String userId;
	/**签到类型：0-正常签到，1-补签*/
	@Excel(name = "签到类型：0-正常签到，1-补签", width = 15, dicCode = "sign_type")
	@Dict(dicCode = "sign_type")
    @ApiModelProperty(value = "签到类型：0-正常签到，1-补签")
    private java.lang.Integer signType;
	/**签到日期,如2023-10-20*/
	@Excel(name = "签到日期,如2023-10-20", width = 15)
    @ApiModelProperty(value = "签到日期,如2023-10-20")
    private java.lang.String signDate;
	/**连续签到天数*/
	@Excel(name = "连续签到天数", width = 15)
    @ApiModelProperty(value = "连续签到天数")
    private java.lang.Integer seriesDays;
	/**获得积分*/
	@Excel(name = "获得积分", width = 15)
    @ApiModelProperty(value = "获得积分")
    private java.lang.Integer integral;
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
	private String userName;
    @TableField(exist = false)
	private String userAvatar;
    @TableField(exist = false)
	private String userPhone;
    @TableField(exist = false)
	private String keyword;
}
