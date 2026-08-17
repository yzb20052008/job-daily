package org.jeecg.modules.job.api.model;

import lombok.Data;

@Data
public class UserLocation {
    //纬度
    private String latitude;
    //经度
    private String longitude;
    //地址
    private String address;
    //城市
    private String city;
    //城市编码
    private String cityCode;
    //上级城市
    private String pCity;
    //上级城市编码
    private String pCityCode;
}
