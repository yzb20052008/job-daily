package org.jeecg.modules.job.job.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import java.util.List;

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
 * @Description: 招工信息
 * @Author: qingkonglan
 * @Date:   2024-07-30
 * @Version: V1.0
 */
@Data
@TableName("job_post")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="job_post对象", description="招工信息")
public class JobPost implements Serializable {
    private static final long serialVersionUID = 1L;

	/**ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "ID")
    private java.lang.String id;
	/**用户ID*/
	@Excel(name = "用户ID", width = 15)
    @ApiModelProperty(value = "用户ID")
    private java.lang.String userId;
	/**标题*/
	@Excel(name = "标题", width = 15)
    @ApiModelProperty(value = "标题")
    private java.lang.String title;
	/**结算方式，兼职，日结、周结、月结、完工结*/
	@Excel(name = "结算方式，兼职，日结、周结、月结、完工结", width = 15)
    @ApiModelProperty(value = "结算方式，兼职，日结、周结、月结、完工结")
    private java.lang.Integer settlementType;
    /**职位模式*/
    @Excel(name = "职位模式，0-自行发布，1-平台发布", width = 15)
    @ApiModelProperty(value = "职位模式，0-自行发布，1-平台发布")
    private java.lang.Integer postSource;
	/**职位ID*/
	@Excel(name = "职位ID", width = 15)
    @ApiModelProperty(value = "职位ID")
    private java.lang.String typeIds;
    /**职位编码*/
    @Excel(name = "职位编码", width = 15)
    @ApiModelProperty(value = "职位编码")
    private java.lang.String typeCodes;
    /**职位名称*/
    @Excel(name = "职位名称", width = 15)
    @ApiModelProperty(value = "职位名称")
    private java.lang.String typeNames;
	/**开始时间*/
	@Excel(name = "开始时间", width = 15)
    @ApiModelProperty(value = "开始时间")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    private java.util.Date startTime;
	/**结束时间*/
	@Excel(name = "结束时间", width = 15)
    @ApiModelProperty(value = "结束时间")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    private java.util.Date endTime;
    /**截止时间*/
    @Excel(name = "截止时间", width = 15)
    @ApiModelProperty(value = "截止时间")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    private java.util.Date closeTime;
	/**性别要求*/
	@Excel(name = "性别要求", width = 15)
    @ApiModelProperty(value = "性别要求")
    private java.lang.String sexRequire;
	/**年龄要求*/
	@Excel(name = "年龄要求", width = 15)
    @ApiModelProperty(value = "年龄要求")
    private java.lang.String ageRequire;
	/**招聘人数*/
	@Excel(name = "招聘人数", width = 15)
    @ApiModelProperty(value = "招聘人数")
    private java.lang.Integer recruitsNumber;
	/**地址名称*/
	@Excel(name = "地址名称", width = 15)
    @ApiModelProperty(value = "地址名称")
    private java.lang.String addressName;
	/**门牌号*/
	@Excel(name = "门牌号", width = 15)
    @ApiModelProperty(value = "门牌号")
    private java.lang.String addressHouse;
	/**工作地点*/
	@Excel(name = "工作地点", width = 15)
    @ApiModelProperty(value = "工作地点")
    private java.lang.String address;
	/**地点经度*/
	@Excel(name = "地点经度", width = 15)
    @ApiModelProperty(value = "地点经度")
    private java.lang.String longitude;
	/**地点纬度*/
	@Excel(name = "地点纬度", width = 15)
    @ApiModelProperty(value = "地点纬度")
    private java.lang.String latitude;
	/**联系电话*/
	@Excel(name = "联系电话", width = 15)
    @ApiModelProperty(value = "联系电话")
    private java.lang.String phone;
    /**联系人姓名*/
    @Excel(name = "联系人姓名", width = 15)
    @ApiModelProperty(value = "联系人姓名")
    private String name;
	/**职位描述*/
	@Excel(name = "职位描述", width = 15)
    @ApiModelProperty(value = "职位描述")
    private java.lang.String descr;
	/**计价方式：计时、计件*/
	@Excel(name = "计价方式：计时、计件", width = 15)
    @ApiModelProperty(value = "计价方式：计时、计件")
    private java.lang.String pricingMode;
	/**薪资范围*/
	@Excel(name = "薪资范围", width = 15)
    @ApiModelProperty(value = "薪资范围")
    private java.lang.String salary;
	/**薪资单位，兼职，元/时，元/天，元/周，元/月*/
	@Excel(name = "薪资单位，兼职，元/时，元/天，元/周，元/月", width = 15)
    @ApiModelProperty(value = "薪资单位，兼职，元/时，元/天，元/周，元/月")
    private java.lang.String salaryUnit;
	/**招工要求*/
	@Excel(name = "招工要求", width = 15)
    @ApiModelProperty(value = "招工要求")
    private java.lang.String jobRequires;
	/**工作视频*/
	@Excel(name = "工作视频", width = 15)
    @ApiModelProperty(value = "工作视频")
    private java.lang.String videoUrl;
	/**工作图片*/
	@Excel(name = "工作图片", width = 15)
    @ApiModelProperty(value = "工作图片")
    private java.lang.String imgUrl;
	/**是否需要电话沟通*/
	@Excel(name = "是否需要电话沟通", width = 15)
    @ApiModelProperty(value = "是否需要电话沟通")
    private java.lang.Integer ifCall;
	/**浏览数量*/
	@Excel(name = "浏览数量", width = 15)
    @ApiModelProperty(value = "浏览数量")
    private java.lang.Integer browseNumber;
	/**是否置顶*/
	@Excel(name = "是否置顶", width = 15)
    @ApiModelProperty(value = "是否置顶")
    private java.lang.Integer ifTopping;
	/**是否加粗*/
	@Excel(name = "是否加粗", width = 15)
    @ApiModelProperty(value = "是否加粗")
    private java.lang.Integer ifBold;
	/**内容描述*/
	@Excel(name = "内容描述", width = 15)
    @ApiModelProperty(value = "内容描述")
    private java.lang.String remark;
	/**排序*/
	@Excel(name = "排序", width = 15)
    @ApiModelProperty(value = "排序")
    private java.lang.Integer sort;
	/**招工状态：1-待审核，2-招工中，3-发布失败，4-已停招，5-已取消，6-已招满   */
	@Excel(name = "招工状态", width = 15)
    @ApiModelProperty(value = "招工状态")
    @Dict(dicCode = "post_status")
    private java.lang.String postStatus;
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


	@TableField(exist = false)
    private String keyword;
    @TableField(exist = false)
    private String orderBy;//all、new、near

    @TableField(exist = false)
    private long orderCount;//接单人数
    @TableField(exist = false)
    private long settlementCount;//结算人数
    @TableField(exist = false)
    private Integer status;
}
