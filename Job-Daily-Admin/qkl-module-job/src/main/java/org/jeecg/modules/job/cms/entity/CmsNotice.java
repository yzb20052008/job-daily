package org.jeecg.modules.job.cms.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.jeecg.modules.job.job.service.IJobPostService;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 系统通知
 * @Author: qingkonglan
 * @Date:   2022-09-26
 * @Version: V1.0
 */
@Data
@TableName("cms_notice")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="cms_notice对象", description="系统通知")
public class CmsNotice implements Serializable {
    private static final long serialVersionUID = 1L;

    /**主键ID*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键ID")
    private java.lang.String id;
    /**角色编码*/
    @Excel(name = "角色编码", width = 15)
    @ApiModelProperty(value = "角色编码")
    private java.lang.String roleCode;
    /**通知标题*/
    @Excel(name = "通知标题", width = 15)
    @ApiModelProperty(value = "通知标题")
    private java.lang.String title;
    /**通知类型：0-系统通知，1-订单动态，2-平台私信*/
    @Excel(name = "通知类型：0-系统通知，1-订单动态，2-平台私信，3-动账消息,4-违规记录", width = 15)
    @ApiModelProperty(value = "通知类型：0-系统通知，1-订单动态，2-平台私信，3-动账消息,4-违规记录")
    private java.lang.Integer type;
    /**平台：1-微信，2-抖音*/
    @Excel(name = "平台：1-微信，2-抖音", width = 15)
    @ApiModelProperty(value = "平台：1-微信，2-抖音")
    private java.lang.String plat;
    /**轮播大图*/
    @Excel(name = "轮播大图", width = 15)
    @ApiModelProperty(value = "轮播大图")
    private java.lang.String banner;
    /**封面图标*/
    @Excel(name = "封面图标", width = 15)
    @ApiModelProperty(value = "封面图标")
    private java.lang.String avatar;
    /**版本信息*/
    @Excel(name = "版本信息", width = 15)
    @ApiModelProperty(value = "版本信息")
    private java.lang.String version;
    /**阅读数量*/
    @Excel(name = "阅读数量", width = 15)
    @ApiModelProperty(value = "阅读数量")
    private java.lang.Integer viewCount;
    /**摘要*/
    @Excel(name = "摘要", width = 15)
    @ApiModelProperty(value = "摘要")
    private java.lang.String excerpt;
    /**内容*/
    @Excel(name = "内容", width = 15)
    @ApiModelProperty(value = "内容")
    private java.lang.String content;
    /**是否置顶*/
    @Excel(name = "是否置顶", width = 15)
    @ApiModelProperty(value = "是否置顶")
    @Dict(dicCode = "set_top")
    private java.lang.Integer setTop;
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

    /**用户ID*/
    @ApiModelProperty(value = "userId")
    private String userId;
    /**是否公共消息*/
    @ApiModelProperty(value = "ifPublic")
    private Integer ifPublic;
    /**数据ID*/
    @ApiModelProperty(value = "dataId")
    private String dataId;
    /**订单ID*/
    @ApiModelProperty(value = "orderId")
    private String orderId;

    //是否已读
    @TableField(exist = false)
    private boolean ifRead;

    @TableField(exist = false)
    private String userName;
    @TableField(exist = false)
    private String userAvatar;
    @TableField(exist = false)
    private String userPhone;
    @TableField(exist = false)
    private String postTitle;//岗位信息
}
