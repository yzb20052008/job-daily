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
 * @Description: 评价记录
 * * @Author: qingkonglan
 * @Date:   2024-08-22
 * @Version: V1.0
 */
@Data
@TableName("job_evaluate_log")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="job_evaluate_log对象", description="评价记录")
public class JobEvaluateLog implements Serializable {
    private static final long serialVersionUID = 1L;

	/**ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "ID")
    private java.lang.String id;
	/**用户ID，评价方*/
	@Excel(name = "用户ID，评价方", width = 15)
    @ApiModelProperty(value = "用户ID，评价方")
    private java.lang.String userId;
	/**老板ID*/
	@Excel(name = "老板ID", width = 15)
    @ApiModelProperty(value = "老板ID")
    private java.lang.String postUserId;
	/**招工ID*/
	@Excel(name = "招工ID", width = 15)
    @ApiModelProperty(value = "招工ID")
    private java.lang.String postId;
    /**订单ID*/
    @Excel(name = "订单ID", width = 15)
    @ApiModelProperty(value = "订单ID")
    private java.lang.String orderId;
	/**角色：member/company*/
	@Excel(name = "角色：member/company", width = 15)
    @ApiModelProperty(value = "角色：member/company")
    private java.lang.String roleCode;
	/**评分*/
	@Excel(name = "评分", width = 15)
    @ApiModelProperty(value = "评分")
    private java.math.BigDecimal score;
	/**评价内容*/
	@Excel(name = "评价内容", width = 15)
    @ApiModelProperty(value = "评价内容")
    private java.lang.String content;
    /**评价图片*/
    @Excel(name = "评价图片", width = 15)
    @ApiModelProperty(value = "评价图片")
    private java.lang.String images;
	/**是否匿名*/
	@Excel(name = "是否匿名", width = 15)
    @ApiModelProperty(value = "是否匿名")
    private java.lang.Integer ifAnonymous;
	/**创建者*/
    @ApiModelProperty(value = "创建者")
    private java.lang.String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
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
}
