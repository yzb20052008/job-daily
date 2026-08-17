package org.jeecg.modules.job.pay.haoda;

import lombok.Getter;
import lombok.Setter;

/**
 * 盒子科技网关接入配置参数.
 * 相关配置信息可通过<a href="https://open.iboxpay.com">盒子开放平台</a>获取.
 *
 * @author Laeni
 */
@Setter
@Getter
public class Config {

    /**
     * 开放平台应用 Id.
     */
    private String appId;

    /**
     * 盒子开放平台应用密钥.
     */
    private String appSecret;

    /**
     * 签名算法.
     * <p> 支持 MD5 和 SHA-256.
     */
    private String signAlgorithm;

    /**
     * 盒子开放平台网关地址.
     */
    private String gatewayUrl = "https://openapi.iboxpay.com/api";

    @Override
    public String toString() {
        return "Config{" +
                "appId='" + appId + '\'' +
                ", appSecret='" + (appSecret == null ? null : "***") + '\'' +
                ", signAlgorithm='" + signAlgorithm + '\'' +
                ", gatewayUrl='" + gatewayUrl + '\'' +
                '}';
    }
}