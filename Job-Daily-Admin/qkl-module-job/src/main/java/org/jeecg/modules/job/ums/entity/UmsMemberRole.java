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
 * @Description: 会员公司关系
 * @Author: qingkonglan
 * @Date:   2022-12-18
 * @Version: V1.0
 */
@Data
@TableName("ums_member_role")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="ums_member_role对象", description="会员公司关系")
public class UmsMemberRole implements Serializable {
    private static final long serialVersionUID = 1L;

	/**会员ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "会员ID")
    private java.lang.String id;
	/**用户id*/
	@Excel(name = "用户id", width = 15)
    @ApiModelProperty(value = "用户id")
    private java.lang.String memberId;
	/**公司ID*/
	@Excel(name = "公司ID", width = 15)
    @ApiModelProperty(value = "公司ID")
    private java.lang.String companyId;
	/**公司职务*/
	@Excel(name = "公司职务", width = 15)
    @ApiModelProperty(value = "公司职务")
    private java.lang.String postName;
	/**身份证号码*/
	@Excel(name = "身份证号码", width = 15)
    @ApiModelProperty(value = "身份证号码")
    private java.lang.String idNumber;
	/**会员角色：0-求职者，1-招聘者，*/
	@Excel(name = "会员角色：0-求职者，1-招聘者，", width = 15)
    @ApiModelProperty(value = "会员角色：0-求职者，1-招聘者，")
    private java.lang.Integer memberRole;
	/**备注信息*/
	@Excel(name = "备注信息", width = 15)
    @ApiModelProperty(value = "备注信息")
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
