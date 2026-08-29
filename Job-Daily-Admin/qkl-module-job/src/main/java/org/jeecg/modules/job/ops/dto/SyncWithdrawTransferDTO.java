package org.jeecg.modules.job.ops.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 提现查单回写请求
 */
@Data
@ApiModel("提现查单回写请求")
public class SyncWithdrawTransferDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "商户单号不能为空")
    @ApiModelProperty(value = "微信商户转账单号", required = true)
    private String outBillNo;
}
