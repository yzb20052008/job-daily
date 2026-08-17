package org.jeecg.modules.job.pay.entity;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.modules.job.pay.utils.TransferToUser;

@Data
@Accessors(chain = true)
public class TransferToUserResponse {
    //商户单号
    public String out_bill_no;
    //商家转账订单号
    public String transfer_bill_no;
    //单据创建时间
    public String create_time;
    //单据状态
    public String state;
    //跳转领取页面的package信息
    public String package_info;
    //转账金额
    public Integer transfer_amount;
    //失败原因
    public String fail_reason;

}
