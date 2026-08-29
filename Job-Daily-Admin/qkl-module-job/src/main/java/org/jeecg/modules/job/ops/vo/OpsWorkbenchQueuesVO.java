package org.jeecg.modules.job.ops.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.modules.job.job.entity.JobCompany;
import org.jeecg.modules.job.job.entity.JobOrder;
import org.jeecg.modules.job.job.entity.JobPost;
import org.jeecg.modules.job.ums.entity.UmsRealnameAuth;
import org.jeecg.modules.job.ums.entity.UmsWithdraw;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 运营工作台待办队列
 */
@Data
@ApiModel("运营工作台待办队列")
public class OpsWorkbenchQueuesVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("待审岗位")
    private List<JobPost> pendingPosts = new ArrayList<>();
    @ApiModelProperty("待审实名")
    private List<UmsRealnameAuth> pendingRealnames = new ArrayList<>();
    @ApiModelProperty("待审企业")
    private List<JobCompany> pendingCompanies = new ArrayList<>();
    @ApiModelProperty("异常订单")
    private List<JobOrder> abnormalOrders = new ArrayList<>();
    @ApiModelProperty("提现队列")
    private List<UmsWithdraw> withdrawQueue = new ArrayList<>();
}
