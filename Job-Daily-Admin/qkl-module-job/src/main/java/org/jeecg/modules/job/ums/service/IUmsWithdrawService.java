package org.jeecg.modules.job.ums.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.jeecg.modules.job.pay.entity.TransferToUserResponse;
import org.jeecg.modules.job.ums.entity.UmsWithdraw;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 用户提现
 * @Author: qingkonglan
 * @Date:   2023-03-30
 * @Version: V1.0
 */
public interface IUmsWithdrawService extends IService<UmsWithdraw> {

    /**
     * 提交提现申请（冻结余额）
     */
    boolean add(UmsWithdraw param);

    /**
     * 审核提现：仅改审核态 + 拒绝时解冻；通过后需再调 {@link #initiateTransfer(String)}
     */
    boolean updateStatus(String id, int status, String reason);

    /**
     * 审核通过后发起微信转账（与审核拆事务；发起前先查单防重发）
     */
    void initiateTransfer(String id);

    /**
     * 回写转账状态（CAS，终态幂等）
     */
    boolean updateTransferStatus(String outBillNo, String transferStatus, String packageInfo);

    /**
     * 回写转账状态（含渠道失败原因）
     */
    boolean updateTransferStatus(String outBillNo, String transferStatus, String packageInfo, String failReason);

    /**
     * 微信查单并回写（管理端/对账）
     */
    TransferToUserResponse getTransferByOutBillNo(String outBillNo);

    /**
     * 微信查单并回写；requireUserId 非空时校验单据归属
     */
    TransferToUserResponse getTransferByOutBillNo(String outBillNo, String requireUserId);

    /**
     * 关闭异常提现并解冻：适用于「审核通过但无商户单号 / 无法发起转账」的历史脏数据
     */
    boolean closeAbnormalWithdraw(String id, String reason);

    /**
     * 分页查询提现记录
     */
    IPage<UmsWithdraw> getWithdrawPageList(IPage page, UmsWithdraw paramCondition);
}
