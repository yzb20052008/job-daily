package org.jeecg.modules.job.pay.service;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ijpay.core.IJPayHttpResponse;
import com.ijpay.core.enums.AuthTypeEnum;
import com.ijpay.core.enums.RequestMethodEnum;
import com.ijpay.core.kit.PayKit;
import com.ijpay.core.kit.WxPayKit;
import com.ijpay.wxpay.WxPayApi;
import com.ijpay.wxpay.WxPayApiConfig;
import com.ijpay.wxpay.WxPayApiConfigKit;
import com.ijpay.wxpay.enums.WxDomainEnum;
import com.ijpay.wxpay.enums.v3.BasePayApiEnum;
import com.ijpay.wxpay.enums.v3.TransferApiEnum;
import com.ijpay.wxpay.model.TransferModel;
import com.ijpay.wxpay.model.v3.*;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.job.pay.entity.*;
import org.jeecg.modules.job.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PayService implements IPayService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final static int OK = 200;
    String serialNo;
    String platSerialNo;

    @Resource
    WxPayV3Bean wxPayV3Bean;

    @Override
    public TransferToUserResponse transferNew(String openId,String outBillNo, BigDecimal money, String userName, String remark) {
        // 金额校验：元转分，禁止硬编码测试金额
        long transferAmountFen = toFen(money);
        if (StrUtil.isBlank(openId) || StrUtil.isBlank(outBillNo)) {
            throw new JeecgBootException("转账参数不完整");
        }
        try {
            TransferSceneReportInfo transferSceneReportInfo = new TransferSceneReportInfo();
            transferSceneReportInfo.info_type = "岗位类型";
            transferSceneReportInfo.info_content = "工人";
            TransferSceneReportInfo transferSceneReportInfo2 = new TransferSceneReportInfo();
            transferSceneReportInfo2.info_type = "报酬说明";
            transferSceneReportInfo2.info_content = "支付佣金";
            List<TransferSceneReportInfo> transferSceneReportInfos=new ArrayList<>();
            transferSceneReportInfos.add(transferSceneReportInfo);
            transferSceneReportInfos.add(transferSceneReportInfo2);
            TransferToUserRequest transferToUserRequest = new TransferToUserRequest()
                    .setAppid(wxPayV3Bean.getAppId())
                    .setOut_bill_no(outBillNo)
                    .setTransfer_scene_id("1005")
                    .setOpenid(openId)
                    .setTransfer_amount(transferAmountFen)
                    .setTransfer_remark(remark)
                    .setNotify_url(wxPayV3Bean.getDomain()+"/v3/transferNotify")
                    .setTransfer_scene_report_infos(transferSceneReportInfos);
            log.info("发起商家转账 outBillNo={}, amountFen={}, openId={}", outBillNo, transferAmountFen, openId);
            IJPayHttpResponse response = WxPayApi.v3(
                    RequestMethodEnum.POST,
                    WxDomainEnum.CHINA.toString(),
                    TransferApiEnum2.TRANSFER_BILLS.getUrl(),
                    wxPayV3Bean.getMchId(),
                    getSerialNumber(),
                    null,
                    wxPayV3Bean.getKeyPath(),
                    JSONUtil.toJsonStr(transferToUserRequest)
            );
            log.info("发起商家转账响应 status={}, body={}", response.getStatus(), response.getBody());
            // 根据证书序列号查询对应的证书来验证签名结果
            boolean verifySignature = WxPayKit.verifySignature(response, wxPayV3Bean.getPlatformCertPath());
            log.info("verifySignature: {}", verifySignature);
            if (response.getStatus() == OK) {
                return JsonUtils.jsonToPojo(response.getBody(),TransferToUserResponse.class);
            }
            throw new JeecgBootException("发起转账失败：" + response.getBody());
        } catch (JeecgBootException e) {
            throw e;
        } catch (Exception e) {
            log.error("商家转账异常 outBillNo={}", outBillNo, e);
            throw new JeecgBootException("操作失败：" + e.getMessage());
        }
    }

    /**
     * 人民币元转分，保留两位小数四舍五入
     */
    private long toFen(BigDecimal money) {
        if (money == null || money.compareTo(BigDecimal.ZERO) <= 0) {
            throw new JeecgBootException("转账金额必须大于0");
        }
        try {
            return money.movePointRight(2).setScale(0, RoundingMode.HALF_UP).longValueExact();
        } catch (ArithmeticException ex) {
            throw new JeecgBootException("转账金额格式不正确");
        }
    }

    public static void main(String[] args) {
        System.err.println(String.format(TransferApiEnum2.TRANSFER_QUERY_BY_OUT_BILL_NO.getUrl(), "outBillNo"));
    }

    @Override
    public TransferToUserResponse getTransferByOutBillNo(String outBillNo) {
        try {
            Map<String, String> params = new HashMap<>(16);
            params.put("out_bill_no", outBillNo);
            log.info("查询参数 {}", JSONUtil.toJsonStr(params));
            IJPayHttpResponse response = WxPayApi.v3(
                    RequestMethodEnum.GET,
                    WxDomainEnum.CHINA.toString(),
                    String.format(TransferApiEnum2.TRANSFER_QUERY_BY_OUT_BILL_NO.getUrl(), outBillNo),
                    wxPayV3Bean.getMchId(),
                    getSerialNumber(),
                    null,
                    wxPayV3Bean.getKeyPath(),
                    "",
                    AuthTypeEnum.RSA.getCode()
            );
            log.info("查询账单响应 {}", response);
            // 根据证书序列号查询对应的证书来验证签名结果
            boolean verifySignature = WxPayKit.verifySignature(response, wxPayV3Bean.getPlatformCertPath());
            log.info("verifySignature: {}", verifySignature);
            if (response.getStatus() == OK) {
                //解析
                TransferToUserResponse res = JsonUtils.jsonToPojo(response.getBody(),TransferToUserResponse.class);
                return res;
            }
        } catch (Exception e) {
            log.error("系统异常", e);
            throw new RuntimeException("操作失败："+e.getMessage());
        }
        return null;
    }


    @Override
    public boolean transfer(String openId, BigDecimal money, String batchName, String remark) {
        List<TransferDetailInput> transfer_detail_list=new ArrayList<>();
        int time = money.intValue() / 200 ;
        if (time==0){
            TransferDetailInput input=new TransferDetailInput();
            input.setOpenid(openId);
            input.setOut_detail_no(PayKit.generateStr());
            input.setTransfer_amount((money.multiply(new BigDecimal(100))).intValueExact());
            input.setTransfer_remark(remark);
            transfer_detail_list.add(input);
        }else{
            //超过200元，需要分多次
            for (int i=0;i<=time;i++){
                TransferDetailInput input=new TransferDetailInput();
                input.setOpenid(openId);
                input.setOut_detail_no(PayKit.generateStr());
                input.setTransfer_remark(remark);
                if (i<time){
                    input.setTransfer_amount(200*100);
                    transfer_detail_list.add(input);
                }else{
                    int mm = money.multiply(new BigDecimal(100)).intValue();
                    int last=mm % 20000;
                    input.setTransfer_amount(last);
                    if (last>0){
                        transfer_detail_list.add(input);
                    }
                }

            }
        }
        return batchTransfer(transfer_detail_list,batchName,remark);
    }

    @Override
    public boolean batchTransfer(List<TransferDetailInput> transfer_detail_list,String batchName,String remark) {
        try {
            //计算总价
            int total=0;
            for (TransferDetailInput detailInput:transfer_detail_list){
                total+=detailInput.getTransfer_amount();
            }
            BatchTransferModel batchTransferModel = new BatchTransferModel()
                    .setAppid(wxPayV3Bean.getAppId())
                    .setOut_batch_no(PayKit.generateStr())
                    .setBatch_name(batchName)
                    .setBatch_remark(remark)
                    .setTotal_amount(total)
                    .setTotal_num(transfer_detail_list.size())
                    .setTransfer_detail_list(transfer_detail_list);

            log.info("发起商家转账请求参数 {}", JSONUtil.toJsonStr(batchTransferModel));
            IJPayHttpResponse response = WxPayApi.v3(
                    RequestMethodEnum.POST,
                    WxDomainEnum.CHINA.toString(),
                    TransferApiEnum.TRANSFER_BATCHES.toString(),
                    wxPayV3Bean.getMchId(),
                    getSerialNumber(),
                    null,
                    wxPayV3Bean.getKeyPath(),
                    JSONUtil.toJsonStr(batchTransferModel)
            );
            log.info("发起商家转账响应 {}", response);
            // 根据证书序列号查询对应的证书来验证签名结果
            boolean verifySignature = WxPayKit.verifySignature(response, wxPayV3Bean.getPlatformCertPath());
            log.info("verifySignature: {}", verifySignature);
            if (response.getStatus() == OK) {
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("系统异常", e);
           throw new RuntimeException("操作失败："+e.getMessage());
        }
    }

    @Override
    public boolean orderRefund(String outTradeNo,BigDecimal payAmount,BigDecimal refundAmount,String reason) {
        try {
            String outRefundNo = PayKit.generateStr();
            log.info("商户退款单号: {}", outRefundNo);
            List<RefundGoodsDetail> list = new ArrayList<>();
            RefundGoodsDetail refundGoodsDetail = new RefundGoodsDetail()
                    .setMerchant_goods_id("123")
                    .setGoods_name("运送服务费")
                    .setUnit_price(payAmount.multiply(new BigDecimal(100)).intValue())
                    .setRefund_amount(refundAmount.multiply(new BigDecimal(100)).intValue())
                    .setRefund_quantity(1);
            list.add(refundGoodsDetail);
            RefundModel refundModel = new RefundModel()
                    .setOut_refund_no(outRefundNo)
                    .setReason("任务取消退款")
                    .setAmount(new RefundAmount().setRefund(refundAmount.multiply(new BigDecimal(100)).intValue()).setTotal(payAmount.multiply(new BigDecimal(100)).intValue()).setCurrency("CNY"))
                    .setGoods_detail(list);
            refundModel.setOut_trade_no(outTradeNo);
            log.info("退款参数 {}", JSONUtil.toJsonStr(refundModel));
            IJPayHttpResponse response = WxPayApi.v3(
                    RequestMethodEnum.POST,
                    WxDomainEnum.CHINA.toString(),
                    BasePayApiEnum.REFUND.toString(),
                    wxPayV3Bean.getMchId(),
                    getSerialNumber(),
                    null,
                    wxPayV3Bean.getKeyPath(),
                    JSONUtil.toJsonStr(refundModel)
            );
            // 根据证书序列号查询对应的证书来验证签名结果
            boolean verifySignature = WxPayKit.verifySignature(response, wxPayV3Bean.getPlatformCertPath());
            log.info("verifySignature: {}", verifySignature);
            log.info("退款响应 {}", response);
            if (verifySignature) {
            }
            if (response.getStatus() == OK) {
                return true;
            }
            JSONObject object=JSONUtil.parseObj(response.getBody());
            String message= (String) object.get("message");
            System.out.println(object.get("message"));
            if ("订单已全额退款".equals(message)){
                return true;
            }
            throw new RuntimeException("退款失败");
        } catch (Exception e) {
            log.error("系统异常", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    private String getSerialNumber() {
        if (StrUtil.isEmpty(serialNo)) {
            // 获取证书序列号
            X509Certificate certificate = PayKit.getCertificate(wxPayV3Bean.getCertPath());
            if (null != certificate) {
                serialNo = certificate.getSerialNumber().toString(16).toUpperCase();
                // 提前两天检查证书是否有效
                boolean isValid = PayKit.checkCertificateIsValid(certificate, wxPayV3Bean.getMchId(), -2);
                log.info("证书是否可用 {} 证书有效期为 {}", isValid, DateUtil.format(certificate.getNotAfter(), DatePattern.NORM_DATETIME_PATTERN));
            }
//            System.out.println("输出证书信息:\n" + certificate.toString());
//            // 输出关键信息，截取部分并进行标记
//            System.out.println("证书序列号:" + certificate.getSerialNumber().toString(16));
//            System.out.println("版本号:" + certificate.getVersion());
//            System.out.println("签发者：" + certificate.getIssuerDN());
//            System.out.println("有效起始日期：" + certificate.getNotBefore());
//            System.out.println("有效终止日期：" + certificate.getNotAfter());
//            System.out.println("主体名：" + certificate.getSubjectDN());
//            System.out.println("签名算法：" + certificate.getSigAlgName());
//            System.out.println("签名：" + certificate.getSignature().toString());
        }
        System.out.println("serialNo:" + serialNo);
        return serialNo;
    }
}
