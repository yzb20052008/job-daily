package org.jeecg.modules.job.api.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 抖音小程序 配置
 */
@Configuration
public class DyConfiguration {

    @Value("${jeecg.dy.appId}")
    private String appId;
    @Value("${jeecg.dy.appSecret}")
    private String appSecret;

    @Bean
    public void initDyConfiguration() {
        DyUtil.setAppId(appId);
        DyUtil.setAppSecret(appSecret);
    }
}