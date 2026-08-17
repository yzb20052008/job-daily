package org.jeecg.modules.job.api.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class Payload implements Serializable {

    /**
     * 文本内容
     */
    private String text;

    /**
     * 名称，针对非文本
     */
    private String name;

    /**
     * url地址，图片或者视频地址
     */
    private String url;

    /**
     * 时长，视频、音频
     */
    private Double duration;

    /**
     * 宽度
     */
    private Integer width;

    /**
     * 高度
     */
    private Integer height;

    /**
     * 大小
     */
    private Double size;

    //视频
    private Payload video;
    private Payload thumbnail;
}
