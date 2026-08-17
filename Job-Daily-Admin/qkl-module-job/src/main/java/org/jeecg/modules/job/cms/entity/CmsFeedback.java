package org.jeecg.modules.job.cms.entity;

import java.io.Serializable;

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
 * @Description: 意见反馈
 * @Author: qingkonglan
 * @Date:   2022-08-21
 * @Version: V1.0
 */
@Data
@TableName("cms_feedback")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="cms_feedback对象", description="意见反馈")
public class CmsFeedback implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键ID")
    private java.lang.String id;
	/**反馈类型*/
	@Excel(name = "反馈类型", width = 15, dicCode = "feedback_type")
	@Dict(dicCode = "feedback_type")
    @ApiModelProperty(value = "反馈类型")
    private java.lang.String type;
	/**反馈内容*/
	@Excel(name = "反馈内容", width = 15)
    @ApiModelProperty(value = "反馈内容")
    private java.lang.String content;
	/**描述图片*/
	@Excel(name = "描述图片", width = 15)
    @ApiModelProperty(value = "描述图片")
    private java.lang.String imgs;
	/**联系方式*/
	@Excel(name = "联系方式", width = 15)
    @ApiModelProperty(value = "联系方式")
    private java.lang.String phone;
	/**反馈标题*/
	@Excel(name = "反馈标题", width = 15)
    @ApiModelProperty(value = "反馈标题")
    private java.lang.String title;
	/**用户id*/
	@Excel(name = "用户id", width = 15)
    @ApiModelProperty(value = "用户id")
    private java.lang.String userId;
    /**处理结果*/
    @Excel(name = "处理结果", width = 15)
    @Dict(dicCode = "feedback_result")
    @ApiModelProperty(value = "处理结果")
    private java.lang.Integer feedbackResult;

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

    @TableField(exist = false)
    private java.lang.String keyword;
    @TableField(exist = false)
    private java.lang.String nickname;
    @TableField(exist = false)
    private java.lang.String userAvatar;
    @TableField(exist = false)
    private java.lang.String userPhone;

}
