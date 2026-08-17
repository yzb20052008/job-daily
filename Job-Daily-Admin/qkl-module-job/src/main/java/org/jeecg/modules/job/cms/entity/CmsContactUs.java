package org.jeecg.modules.job.cms.entity;

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
 * @Description: 联系我们
 * @Author: qingkonglan
 * @Date:   2022-12-21
 * @Version: V1.0
 */
@Data
@TableName("cms_contact_us")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="cms_contact_us对象", description="联系我们")
public class CmsContactUs implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id,主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id,主键")
    private java.lang.String id;
	/**公司名称*/
	@Excel(name = "公司名称", width = 15)
    @ApiModelProperty(value = "公司名称")
    private java.lang.String name;
	/**公司地址*/
	@Excel(name = "公司地址", width = 15)
    @ApiModelProperty(value = "公司地址")
    private java.lang.String address;
	/**LOGO*/
	@Excel(name = "LOGO", width = 15)
    @ApiModelProperty(value = "LOGO")
    private java.lang.String logo;
	/**官方网址*/
	@Excel(name = "官方网址", width = 15)
    @ApiModelProperty(value = "官方网址")
    private java.lang.String website;
	/**客服电话*/
	@Excel(name = "客服电话", width = 15)
    @ApiModelProperty(value = "客服电话")
    private java.lang.String servicePhone;
	/**微信号码*/
	@Excel(name = "微信号码", width = 15)
    @ApiModelProperty(value = "微信号码")
    private java.lang.String wechatNumber;
	/**QQ号码*/
	@Excel(name = "QQ号码", width = 15)
    @ApiModelProperty(value = "QQ号码")
    private java.lang.String qqNumber;
	/**邮箱地址*/
	@Excel(name = "邮箱地址", width = 15)
    @ApiModelProperty(value = "邮箱地址")
    private java.lang.String email;
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
}
