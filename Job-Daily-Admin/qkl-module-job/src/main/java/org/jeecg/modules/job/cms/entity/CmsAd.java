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
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 广告信息
 * @Author: qingkonglan
 * @Date:   2022-10-12
 * @Version: V1.0
 */
@Data
@TableName("cms_ad")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="cms_ad对象", description="广告信息")
public class CmsAd implements Serializable {
    private static final long serialVersionUID = 1L;

    /**广告id*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "广告id")
    private java.lang.String id;
    /**广告位置*/
    @Excel(name = "广告位置", width = 15)
    @ApiModelProperty(value = "广告位置")
    @Dict(dicCode = "ad_position")
    private java.lang.String adPosition;
    /**面向角色*/
    @Excel(name = "面向角色", width = 15)
    @ApiModelProperty(value = "面向角色")
    @Dict(dicCode = "role_code")
    private java.lang.String roleCode;
    /**标题*/
    @Excel(name = "标题", width = 15)
    @ApiModelProperty(value = "标题")
    private java.lang.String title;
    /**图片地址*/
    @Excel(name = "图片地址", width = 15)
    @ApiModelProperty(value = "图片地址")
    private java.lang.String pic;
    /**URL链接类型*/
    @Excel(name = "URL链接类型", width = 15, dicCode = "url_type")
    @Dict(dicCode = "url_type")
    @ApiModelProperty(value = "URL链接类型")
    private java.lang.Integer urlType;
    /**跳转链接*/
    @Excel(name = "跳转链接", width = 15)
    @ApiModelProperty(value = "跳转链接")
    private java.lang.String openUrl;
    /**排序*/
    @Excel(name = "排序", width = 15)
    @ApiModelProperty(value = "排序")
    private java.lang.Integer sort;
    /**启用状态*/
    @Excel(name = "启用状态", width = 15)
    @ApiModelProperty(value = "启用状态")
    @Dict(dicCode = "status")
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
}
