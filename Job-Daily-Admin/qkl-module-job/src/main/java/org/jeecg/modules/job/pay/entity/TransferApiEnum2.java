package org.jeecg.modules.job.pay.entity;

import com.ijpay.wxpay.enums.WxApiEnum;

public enum TransferApiEnum2  implements WxApiEnum {

    /**
     * 发起商家转账
     */
    TRANSFER_BILLS("/v3/fund-app/mch-transfer/transfer-bills", "发起商家转账"),

    /**
     * 商户单号查询转账单
     */
    TRANSFER_QUERY_BY_OUT_BILL_NO("/v3/fund-app/mch-transfer/transfer-bills/out-bill-no/%s", "商户单号查询转账单");


    /**
     * 接口URL
     */
    private final String url;

    /**
     * 接口描述
     */
    private final String desc;

    TransferApiEnum2(String url, String desc) {
        this.url = url;
        this.desc = desc;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}
