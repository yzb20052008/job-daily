package org.jeecg.modules.job.api.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 微信小程序 配置
 */
@Configuration
public class WxConfiguration {

    @Value("${jeecg.wx.appId}")
    private String appId;
    @Value("${jeecg.wx.appSecret}")
    private String appSecret;

    @Bean
    public void initWxConfiguration() {
        WxUtil.setAppId(appId);
        WxUtil.setAppSecret(appSecret);
    }
}