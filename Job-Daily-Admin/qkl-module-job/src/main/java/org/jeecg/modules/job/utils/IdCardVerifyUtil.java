package org.jeecg.modules.job.utils;

import com.tencentcloudapi.common.AbstractModel;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.faceid.v20180301.FaceidClient;
import com.tencentcloudapi.faceid.v20180301.models.IdCardVerificationRequest;
import com.tencentcloudapi.faceid.v20180301.models.IdCardVerificationResponse;

/**
 * 腾讯云身份证实名核验工具。
 * 密钥与证件号请走配置中心/环境变量，禁止在源码中硬编码。
 */
public class IdCardVerifyUtil {

    /**
     * 核验身份证姓名是否一致
     *
     * @param secretId  腾讯云 SecretId
     * @param secretKey 腾讯云 SecretKey
     * @param idCard    身份证号
     * @param name      姓名
     */
    public static IdCardVerificationResponse verify(String secretId, String secretKey, String idCard, String name)
            throws TencentCloudSDKException {
        Credential cred = new Credential(secretId, secretKey);
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint("faceid.tencentcloudapi.com");
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);
        FaceidClient client = new FaceidClient(cred, "", clientProfile);
        IdCardVerificationRequest req = new IdCardVerificationRequest();
        req.setIdCard(idCard);
        req.setName(name);
        return client.IdCardVerification(req);
    }

    public static void main(String[] args) {
        throw new UnsupportedOperationException("请传入环境变量中的密钥与测试证件信息，勿在源码中硬编码");
    }
}
