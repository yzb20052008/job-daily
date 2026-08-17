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
        // TODO: 请准备商户开发必要参数，参考：https://pay.weixin.qq.com/doc/v3/merchant/4013070756
        TransferToUser client = new TransferToUser(
                "1632349884",                    // 商户号，是由微信支付系统生成并分配给每个商户的唯一标识符，商户号获取方式参考 https://pay.weixin.qq.com/doc/v3/merchant/4013070756
                "628F1A8218AF21336C65CA15D1F2B1E376978F5D",         // 商户API证书序列号，如何获取请参考 https://pay.weixin.qq.com/doc/v3/merchant/4013053053
                "D:/cert/qkl/apiclient_key.pem",     // 商户API证书私钥文件路径，本地文件路径
                "PUB_KEY_ID_0116323498842025110300112294000802",      // 微信支付公钥ID，如何获取请参考 https://pay.weixin.qq.com/doc/v3/merchant/4013038816
                "D:/cert/qkl/pub_key.pem"           // 微信支付公钥文件路径，本地文件路径
        );

        TransferToUserRequest request = new TransferToUserRequest();
        request.appid = "wx8eb6df2b696e3e39";
        request.outBillNo = "plfk2020042013";
        request.transferSceneId = "1005";
        request.openid = "oAG2m7UjmKiNU3li2pg4TZENhCZs";
        request.userName = client.encrypt("易祖柏");
        request.transferAmount = 400000L;
        request.transferRemark = "新会员开通有礼";
        request.notifyUrl = "https://www.weixin.qq.com/wxpay/pay.php";
        request.userRecvPerception = "现金奖励";
        request.transferSceneReportInfos = new ArrayList<>();
        {
            TransferSceneReportInfo transferSceneReportInfosItem0 = new TransferSceneReportInfo();
            transferSceneReportInfosItem0.infoType = "活动名称";
            transferSceneReportInfosItem0.infoContent = "新会员有礼";
            request.transferSceneReportInfos.add(transferSceneReportInfosItem0);
            TransferSceneReportInfo transferSceneReportInfosItem1 = new TransferSceneReportInfo();
            transferSceneReportInfosItem1.infoType = "奖励说明";
            transferSceneReportInfosItem1.infoContent = "注册会员抽奖一等奖";
            request.transferSceneReportInfos.add(transferSceneReportInfosItem1);
        };
        try {
            TransferToUserResponse response = client.run(request);
            // TODO: 请求成功，继续业务逻辑
            System.out.println(response);
        } catch (WXPayUtility.ApiException e) {
            // TODO: 请求失败，根据状态码执行不同的逻辑
            e.printStackTrace();
        }
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
