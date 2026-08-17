package org.jeecg.modules.job.integral.entity;

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
 * @Description: 积分日志
 * @Author: qingkonglan
 * @Date:   2024-07-30
 * @Version: V1.0
 */
@Data
@TableName("integral_log")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="integral_log对象", description="积分日志")
public class IntegralLog implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键ID")
    private java.lang.String id;
	/**用户ID*/
	@Excel(name = "用户ID", width = 15)
    @ApiModelProperty(value = "用户ID")
    private java.lang.String userId;
	/**积分数量*/
	@Excel(name = "积分数量", width = 15)
    @ApiModelProperty(value = "积分数量")
    private java.math.BigDecimal integral;
	/**原有目标积分数量*/
	@Excel(name = "原有目标积分数量", width = 15)
    @ApiModelProperty(value = "原有目标积分数量")
    private java.math.BigDecimal integralLast;
	/**积分来源*/
	@Excel(name = "积分来源", width = 15, dicCode = "integral_resource")
	@Dict(dicCode = "integral_resource")
    @ApiModelProperty(value = "积分来源")
    private java.lang.Integer integralResource;
	/**关联数据ID*/
	@Excel(name = "关联数据ID", width = 15)
    @ApiModelProperty(value = "关联数据ID")
    private java.lang.String dataId;
	/**是否增加,1-增加，其他-减少*/
	@Excel(name = "是否增加,1-增加，其他-减少", width = 15)
    @ApiModelProperty(value = "是否增加,1-增加，其他-减少")
    private java.lang.Integer ifAdd;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private java.lang.String remark;
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
    private String userName;
    @TableField(exist = false)
    private String userAvatar;
    @TableField(exist = false)
    private String userPhone;
    @TableField(exist = false)
    private String keyword;

}
