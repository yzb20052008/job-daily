package org.jeecg.modules.job.job.entity;

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
 * @Description: 求职意向
 * @Author: qingkonglan
 * @Date:   2024-07-30
 * @Version: V1.0
 */
@Data
@TableName("job_resume_intention")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="job_resume_intention对象", description="求职意向")
public class JobResumeIntention implements Serializable {
    private static final long serialVersionUID = 1L;

	/**VIP id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "VIP id")
    private java.lang.String id;
	/**用户ID*/
	@Excel(name = "用户ID", width = 15)
    @ApiModelProperty(value = "用户ID")
    private java.lang.String userId;
	/**名称*/
	@Excel(name = "名称", width = 15)
    @ApiModelProperty(value = "名称")
    private java.lang.String resumeId;
	/**工种*/
	@Excel(name = "工种", width = 15)
    @ApiModelProperty(value = "工种")
    private java.lang.String typeNames;
    /**工种*/
    @Excel(name = "工种", width = 15)
    @ApiModelProperty(value = "工种")
    private java.lang.String typeIds;
    /**工种*/
    @Excel(name = "工种", width = 15)
    @ApiModelProperty(value = "工种")
    private java.lang.String typeCodes;
	/**用工方式：如点工、包工等*/
	@Excel(name = "用工方式：如点工、包工等", width = 15)
    @ApiModelProperty(value = "用工方式：如点工、包工等")
    private java.lang.String employMethod;
	/**结算方式：日结、月结、完工结d等*/
	@Excel(name = "结算方式：日结、月结、完工结d等", width = 15)
    @ApiModelProperty(value = "结算方式：日结、月结、完工结d等")
    private java.lang.String settlementType;
	/**工作城市*/
	@Excel(name = "工作城市", width = 15)
    @ApiModelProperty(value = "工作城市")
    private java.lang.String workCity;
	/**工作类型：全职、兼职等*/
	@Excel(name = "工作类型：全职、兼职等", width = 15)
    @ApiModelProperty(value = "工作类型：全职、兼职等")
    private java.lang.String workType;
	/**期望薪资*/
	@Excel(name = "期望薪资", width = 15)
    @ApiModelProperty(value = "期望薪资")
    private java.lang.String expectSalary;
	/**薪资单位：元/日、元/月、元/件等*/
	@Excel(name = "薪资单位：元/日、元/月、元/件等", width = 15)
    @ApiModelProperty(value = "薪资单位：元/日、元/月、元/件等")
    private java.lang.String salaryUnit;
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

    /**县级城市名称*/
    @ApiModelProperty(value = "县级城市名称")
    private java.lang.String city;
    /**县级城市编码*/
    @ApiModelProperty(value = "县级城市编码")
    private java.lang.String cityCode;
    /**市级城市名称*/
    @ApiModelProperty(value = "市级城市名称")
    private java.lang.String pCity;
    /**市级城市编码*/
    @ApiModelProperty(value = "市级城市编码")
    private java.lang.String pCityCode;
}
