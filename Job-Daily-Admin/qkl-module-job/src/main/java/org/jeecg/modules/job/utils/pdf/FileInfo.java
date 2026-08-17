package org.jeecg.modules.job.utils.pdf;

import lombok.Data;

@Data
public class FileInfo {
    //文件名称
    private String name;
    //文件路径
    private String url;
    //文件大小,带单位
    private String size;
    //文件类型：如pdf，word，excel等，目前仅支持pdf
    private String type;
    //文件后缀
    private String ext;

}
