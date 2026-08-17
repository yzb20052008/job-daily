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
 * @Description: 用户阈值
 * @Author: qingkonglan
 * @Date:   2023-11-20
 * @Version: V1.0
 */
@Data
@TableName("ums_param_limit")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="ums_param_limit对象", description="用户阈值")
public class UmsParamLimit implements Serializable {
    private static final long serialVersionUID = 1L;

	/**消息id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "消息id")
    private java.lang.String id;
	/**回复内容*/
	@Excel(name = "回复内容", width = 15)
    @ApiModelProperty(value = "回复内容")
    private java.lang.String userId;
	/**AI可用次数*/
	@Excel(name = "AI可用次数", width = 15)
    @ApiModelProperty(value = "AI可用次数")
    private java.lang.Integer aiNum;
	/**AI已用次数*/
	@Excel(name = "AI已用次数", width = 15)
    @ApiModelProperty(value = "AI已用次数")
    private java.lang.Integer usedAiNum;
	/**简历投递次数*/
	@Excel(name = "简历投递次数", width = 15)
    @ApiModelProperty(value = "简历投递次数")
    private java.lang.Integer applyNum;
	/**已投递次数*/
	@Excel(name = "已投递次数", width = 15)
    @ApiModelProperty(value = "已投递次数")
    private java.lang.Integer usedApplyNum;
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
}
