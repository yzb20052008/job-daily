package org.jeecg.modules.job.cms.entity;

import java.io.Serializable;

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

/**
 * @Description: 内容类别
 * @Author: qingkonglan
 * @Date:   2022-08-21
 * @Version: V1.0
 */
@Data
@TableName("cms_category")
@ApiModel(value="cms_category对象", description="内容类别")
public class CmsCategory implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键ID")
    private java.lang.String id;
	/**分类编码*/
	@Excel(name = "分类编码", width = 15)
    @ApiModelProperty(value = "分类编码")
    private java.lang.String code;
	/**分类名称*/
	@Excel(name = "分类名称", width = 15)
    @ApiModelProperty(value = "分类名称")
    private java.lang.String name;
	/**类别副名称*/
	@Excel(name = "类别副名称", width = 15)
    @ApiModelProperty(value = "类别副名称")
    private java.lang.String subName;
	/**类别描述*/
	@Excel(name = "类别描述", width = 15)
    @ApiModelProperty(value = "类别描述")
    private java.lang.String description;
	/**类别图标*/
	@Excel(name = "类别图标", width = 15)
    @ApiModelProperty(value = "类别图标")
    private java.lang.String icon;
	/**类别大图*/
	@Excel(name = "类别大图", width = 15)
    @ApiModelProperty(value = "类别大图")
    private java.lang.String img;
	/**类别跳转地址*/
	@Excel(name = "类别跳转地址", width = 15)
    @ApiModelProperty(value = "类别跳转地址")
    private java.lang.String openUrl;
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
	/**父级节点*/
	@Excel(name = "父级节点", width = 15)
    @ApiModelProperty(value = "父级节点")
    private java.lang.String pid;
	/**是否有子节点*/
	@Excel(name = "是否有子节点", width = 15, dicCode = "yn")
	@Dict(dicCode = "yn")
    @ApiModelProperty(value = "是否有子节点")
    private java.lang.String hasChild;
}
