package org.jeecg.modules.job.ums.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
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
 * @Description: VIP信息
 * @Author: qingkonglan
 * @Date:   2022-12-18
 * @Version: V1.0
 */
@Data
@TableName("ums_vip")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="ums_vip对象", description="VIP信息")
public class UmsVip implements Serializable {
    private static final long serialVersionUID = 1L;

	/**VIP id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "VIP id")
    private java.lang.String id;
    /**角色编码*/
    @Excel(name = "角色编码", width = 15)
    @ApiModelProperty(value = "角色编码")
    @Dict(dictTable ="sys_role",dicText = "role_name",dicCode = "role_code")
    private java.lang.String roleCode;
	/**VIP类型*/
	@Excel(name = "VIP类型", width = 15)
    @ApiModelProperty(value = "VIP类型")
    @Dict(dicCode = "vip_type")
    private java.lang.String vipType;
	/**名称*/
	@Excel(name = "名称", width = 15)
    @ApiModelProperty(value = "名称")
    private java.lang.String name;
	/**VIP价格信息*/
	@Excel(name = "VIP价格信息", width = 15)
    @ApiModelProperty(value = "VIP价格信息")
    private java.math.BigDecimal price;

    /**月单价*/
    @Excel(name = "月单价", width = 15)
    @ApiModelProperty(value = "月单价")
    private java.math.BigDecimal unitPrice;

    /**VIP价格信息*/
    @ApiModelProperty(value = "VIP价格信息")
    private Integer integral;

    /**有效天数*/
    @ApiModelProperty(value = "有效天数")
    private Integer days;
    /**会员权益*/
    @Excel(name = "会员权益", width = 15)
    @ApiModelProperty(value = "会员权益")
    private java.lang.String equity;
	/**内容描述*/
	@Excel(name = "内容描述", width = 15)
    @ApiModelProperty(value = "内容描述")
    private java.lang.String remark;
	/**排序*/
	@Excel(name = "排序", width = 15)
    @ApiModelProperty(value = "排序")
    private java.lang.Integer sort;
	/**启用状态*/
	@Excel(name = "启用状态", width = 15, dicCode = "status")
	@Dict(dicCode = "status")
    @ApiModelProperty(value = "启用状态")
    private java.lang.Integer status;
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
}
