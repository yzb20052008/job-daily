package org.jeecg.modules.job.pay.utils;


import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Expose;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 发起转账
 */
public class TransferToUser {
    private static String HOST = "https://api.mch.weixin.qq.com";
    private static String METHOD = "POST";
    private static String PATH = "/v3/fund-app/mch-transfer/transfer-bills";

    public static void main(String[] args) {
        // 示例入口：请从环境变量或 private 配置读取，禁止提交真实商户号/证书/用户信息
        throw new UnsupportedOperationException("请使用 PayService.transferNew 与私有配置联调，勿在源码中硬编码凭证");
    }

    public TransferToUserResponse run(TransferToUserRequest request) {
        String uri = PATH;
        String reqBody = WXPayUtility.toJson(request);
        System.out.println("reqBody==="+reqBody);
        System.out.println("wechatPayPublicKeyId==="+wechatPayPublicKeyId);
        Request.Builder reqBuilder = new Request.Builder().url(HOST + uri);
        reqBuilder.addHeader("Accept", "application/json");
        reqBuilder.addHeader("Wechatpay-Serial", wechatPayPublicKeyId);
        reqBuilder.addHeader("Authorization", WXPayUtility.buildAuthorization(mchid, certificateSerialNo,privateKey, METHOD, uri, reqBody));
        reqBuilder.addHeader("Content-Type", "application/json");
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), reqBody);
        reqBuilder.method(METHOD, requestBody);
        Request httpRequest = reqBuilder.build();

        // 发送HTTP请求
        OkHttpClient client = new OkHttpClient.Builder().build();
        try (Response httpResponse = client.newCall(httpRequest).execute()) {
            String respBody = WXPayUtility.extractBody(httpResponse);
            System.out.println("respBody==="+respBody);
            if (httpResponse.code() >= 200 && httpResponse.code() < 300) {
                // 2XX 成功，验证应答签名
                WXPayUtility.validateResponse(this.wechatPayPublicKeyId, this.wechatPayPublicKey,
                        httpResponse.headers(), respBody);

                // 从HTTP应答报文构建返回数据
                return WXPayUtility.fromJson(respBody, TransferToUserResponse.class);
            } else {
                throw new WXPayUtility.ApiException(httpResponse.code(), respBody, httpResponse.headers());
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Sending request to " + uri + " failed.", e);
        }
    }

    private final String mchid;
    private final String certificateSerialNo;
    private final PrivateKey privateKey;
    private final String wechatPayPublicKeyId;
    private final PublicKey wechatPayPublicKey;

    public TransferToUser(String mchid, String certificateSerialNo, String privateKeyFilePath, String wechatPayPublicKeyId, String wechatPayPublicKeyFilePath) {
        this.mchid = mchid;
        this.certificateSerialNo = certificateSerialNo;
        this.privateKey = WXPayUtility.loadPrivateKeyFromPath(privateKeyFilePath);
        this.wechatPayPublicKeyId = wechatPayPublicKeyId;
        this.wechatPayPublicKey = WXPayUtility.loadPublicKeyFromPath(wechatPayPublicKeyFilePath);
        System.out.println("wechatPayPublicKey=="+this.wechatPayPublicKey);
    }

    public String encrypt(String plainText) {
        return WXPayUtility.encrypt(this.wechatPayPublicKey, plainText);
    }

    public static class TransferToUserRequest {
        @SerializedName("appid")
        public String appid;

        @SerializedName("out_bill_no")
        public String outBillNo;

        @SerializedName("transfer_scene_id")
        public String transferSceneId;

        @SerializedName("openid")
        public String openid;

        @SerializedName("user_name")
        public String userName;

        @SerializedName("transfer_amount")
        public Long transferAmount;

        @SerializedName("transfer_remark")
        public String transferRemark;

        @SerializedName("notify_url")
        public String notifyUrl;

        @SerializedName("user_recv_perception")
        public String userRecvPerception;

        @SerializedName("transfer_scene_report_infos")
        public List<TransferSceneReportInfo> transferSceneReportInfos = new ArrayList<TransferSceneReportInfo>();
    }

    public static class TransferToUserResponse {
        @SerializedName("out_bill_no")
        public String outBillNo;

        @SerializedName("transfer_bill_no")
        public String transferBillNo;

        @SerializedName("create_time")
        public String createTime;

        @SerializedName("state")
        public TransferBillStatus state;

        @SerializedName("package_info")
        public String packageInfo;
    }

    public static class TransferSceneReportInfo {
        @SerializedName("info_type")
        public String infoType;

        @SerializedName("info_content")
        public String infoContent;
    }

    public enum TransferBillStatus {
        @SerializedName("ACCEPTED")
        ACCEPTED,
        @SerializedName("PROCESSING")
        PROCESSING,
        @SerializedName("WAIT_USER_CONFIRM")
        WAIT_USER_CONFIRM,
        @SerializedName("TRANSFERING")
        TRANSFERING,
        @SerializedName("SUCCESS")
        SUCCESS,
        @SerializedName("FAIL")
        FAIL,
        @SerializedName("CANCELING")
        CANCELING,
        @SerializedName("CANCELLED")
        CANCELLED
    }

}
