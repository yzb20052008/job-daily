package org.jeecg.modules.job.ums.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
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
 * @Description: 支付方式
 * @Author: qingkonglan
 * @Date:   2023-08-17
 * @Version: V1.0
 */
@Data
@TableName("ums_pay_type")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="ums_pay_type对象", description="支付方式")
public class UmsPayType implements Serializable {
    private static final long serialVersionUID = 1L;

	/**VIP id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "VIP id")
    private java.lang.String id;
	/**编码*/
	@Excel(name = "编码", width = 15)
    @ApiModelProperty(value = "编码")
    private java.lang.String code;
	/**应用模块*/
	@Excel(name = "应用模块", width = 15, dicCode = "app_module")
	@Dict(dicCode = "app_module")
    @ApiModelProperty(value = "应用模块")
    private java.lang.String appModule;
	/**名称*/
	@Excel(name = "名称", width = 15)
    @ApiModelProperty(value = "名称")
    private java.lang.String name;
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
