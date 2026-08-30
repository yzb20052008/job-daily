package org.jeecg.modules.job.pay.service;

import com.ijpay.wxpay.model.v3.TransferDetailInput;
import org.jeecg.modules.job.pay.entity.TransferToUserResponse;

import java.math.BigDecimal;
import java.util.List;

public interface IPayService {


    /**
     * 转账到零钱2025升级版
     * @param openId 收款用户openid
     * @param outBillNo 自定义编码
     * @param money 转账金额
     * @param userName 收款方真实姓名大余2000元时必填
     * @param remark 提现备注
     */
    TransferToUserResponse transferNew(String openId, String outBillNo, BigDecimal money, String userName, String remark);


    /**
     * 根据商户号查询转账单
     * @param outBillNo
     * @return
     */
    TransferToUserResponse getTransferByOutBillNo(String outBillNo);

    /**
     * 校验微信商户可用余额是否足够发起转账（单位：元）
     * @param moneyYuan 提现金额（元）
     * @throws org.jeecg.common.exception.JeecgBootException 余额不足或无法查询时抛出
     */
    void assertMerchantBalanceEnough(BigDecimal moneyYuan);

    /**
     * 转账到零钱
     * @param openId 收款用户openid
     * @param money 转账金额
     * @param batchName 批量提现名称
     * @param remark 提现备注
     */
    boolean transfer(String openId,BigDecimal money,String batchName,String remark);


    /**
     * 批量转账到零钱
     * @param transfer_detail_list
     */
    boolean batchTransfer(List<TransferDetailInput> transfer_detail_list,String batchName,String remark);


    /**
     * 订单退款
     * @param outTradeNo 原交易订单号
     * @param payAmount 实付金额
     * @param refundAmount 退款金额
     * @param reason 退款原因
     * @return
     */
    boolean orderRefund(String outTradeNo,BigDecimal payAmount, BigDecimal refundAmount, String reason);

}
