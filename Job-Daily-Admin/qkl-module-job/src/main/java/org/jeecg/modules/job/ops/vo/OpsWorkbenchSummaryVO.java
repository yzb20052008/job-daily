package org.jeecg.modules.job.ops.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 运营工作台汇总指标
 */
@Data
@ApiModel("运营工作台汇总")
public class OpsWorkbenchSummaryVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("待审岗位数")
    private long pendingPostCount;
    @ApiModelProperty("待审实名认证数")
    private long pendingRealnameCount;
    @ApiModelProperty("待审企业认证数")
    private long pendingCompanyCount;
    @ApiModelProperty("待审提现数")
    private long pendingWithdrawCount;
    @ApiModelProperty("转账异常/未终态提现数")
    private long abnormalWithdrawCount;
    @ApiModelProperty("超时异常订单数（待确认超时 + 待开工已过结束时间）")
    private long timeoutOrderCount;
    @ApiModelProperty("待结算订单数")
    private long waitPayOrderCount;

    @ApiModelProperty("近7日完单率(%)")
    private BigDecimal finishRate7d;
    @ApiModelProperty("近7日结算成功率(%)")
    private BigDecimal paySuccessRate7d;
    @ApiModelProperty("近7日提现成功率(%)")
    private BigDecimal withdrawSuccessRate7d;

    @ApiModelProperty("近7日订单总数")
    private long orderTotal7d;
    @ApiModelProperty("近7日完成订单数")
    private long orderFinish7d;
    @ApiModelProperty("近7日已结算订单数")
    private long orderPaid7d;
    @ApiModelProperty("近7日提现申请数")
    private long withdrawTotal7d;
    @ApiModelProperty("近7日提现成功数")
    private long withdrawSuccess7d;
}
