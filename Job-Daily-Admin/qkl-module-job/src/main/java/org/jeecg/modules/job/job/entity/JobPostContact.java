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
 * @Description: 拨号记录
 * @Author: qingkonglan
 * @Date:   2024-07-30
 * @Version: V1.0
 */
@Data
@TableName("job_post_contact")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="job_post_contact对象", description="拨号记录")
public class JobPostContact implements Serializable {
    private static final long serialVersionUID = 1L;

	/**VIP id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "VIP id")
    private java.lang.String id;
	/**应聘者ID*/
	@Excel(name = "应聘者ID", width = 15)
    @ApiModelProperty(value = "应聘者ID")
    private java.lang.String userId;
	/**招聘者ID*/
	@Excel(name = "招聘者ID", width = 15)
    @ApiModelProperty(value = "招聘者ID")
    private java.lang.String postUserId;
	/**招工ID*/
	@Excel(name = "招工ID", width = 15)
    @ApiModelProperty(value = "招工ID")
    private java.lang.String postId;
	/**拨号方角色：零工，老板*/
	@Excel(name = "拨号方角色：零工，老板", width = 15)
    @ApiModelProperty(value = "拨号方角色：零工，老板")
    private java.lang.String roleCode;
    /**合作意向：0-默认，1-达成，2-未达成，3-超时*/
    @Excel(name = "合作意向", width = 15)
    @ApiModelProperty(value = "合作意向")
    private java.lang.Integer agreeState;
	/**拨打方号码*/
	@Excel(name = "拨打方号码", width = 15)
    @ApiModelProperty(value = "拨打方号码")
    private java.lang.String phoneA;
	/**被拨打方号码*/
	@Excel(name = "被拨打方号码", width = 15)
    @ApiModelProperty(value = "被拨打方号码")
    private java.lang.String phoneB;
	/**中间号*/
	@Excel(name = "中间号", width = 15)
    @ApiModelProperty(value = "中间号")
    private java.lang.String phoneX;
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
    private java.lang.Integer delFlag;

    @TableField(exist = false)
    private String keyword;
    // 是否免费，不扣积分
    @TableField(exist = false)
    private boolean ifFree;
}
