package org.jeecg.modules.job.msg.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 业务消息模板（站内信 / 微信订阅）
 */
@Data
@TableName("biz_msg_template")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "biz_msg_template", description = "业务消息模板")
public class BizMsgTemplate implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty("主键")
    private String id;

    @Excel(name = "模板编码", width = 20)
    @ApiModelProperty("模板编码")
    private String templateCode;

    @Excel(name = "通道", width = 10)
    @ApiModelProperty("通道：site/wx")
    private String channel;

    @Excel(name = "微信模板ID", width = 40)
    @ApiModelProperty("微信订阅消息模板ID")
    private String wxTemplateId;

    @Excel(name = "标题", width = 30)
    @ApiModelProperty("标题模板，支持 {var}")
    private String title;

    @Excel(name = "内容", width = 50)
    @ApiModelProperty("内容模板，支持 {var}")
    private String content;

    @ApiModelProperty("字段映射JSON（预留）")
    private String fieldMapping;

    @Excel(name = "状态", width = 10)
    @ApiModelProperty("1启用 0停用")
    private String status;

    @Excel(name = "备注", width = 30)
    private String remark;

    private String createBy;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    private String updateBy;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @TableLogic
    private Integer delFlag;
}
