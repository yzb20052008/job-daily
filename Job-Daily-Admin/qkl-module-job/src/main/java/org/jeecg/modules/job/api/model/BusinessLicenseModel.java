package org.jeecg.modules.job.api.model;

import lombok.Data;

/**
 * 营业执照OCR返回文本信息
 */
@Data
public class BusinessLicenseModel {

    //注册地址
    private String Address;
    //有效期
    private String ValidPeriod;
    //注册资本
    private String Capital;
    //法人代表
    private String LegalPerson;
    //成立日期
    private String EstablishDate;
    //企业名称
    private String Name;
    //信用代码
    private String RegisterNumber;
    //类型
    private String Type;
    //经营范围
    private String Business;
}
