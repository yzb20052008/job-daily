package org.jeecg.modules.job.ums.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.job.pay.entity.TransferToUserResponse;
import org.jeecg.modules.job.ums.entity.UmsAccount;
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
     * 提交提现申请
     * @param param
     * @return
     */
    boolean add(UmsWithdraw param);

    /**
     * 更新提现状态
     * @param id
     * @param status
     * @param reason
     * @return
     */
    boolean updateStatus(String id,int status,String reason);

    /**
     * 更新转账状态
     * @param outBillNo
     * @param transferStatus
     * @param packageInfo
     * @return
     */
    boolean updateTransferStatus(String outBillNo,String transferStatus,String packageInfo);

    /**
     * 查询转账状态
     * @param outBillNo
     * @return
     */
    TransferToUserResponse getTransferByOutBillNo(String outBillNo);

    /**
     * 关闭异常提现并解冻：适用于「审核通过但无商户单号 / 无法发起转账」的历史脏数据
     * @param id 提现单ID
     * @param reason 关闭原因
     * @return 是否处理成功
     */
    boolean closeAbnormalWithdraw(String id, String reason);

    /**
     * 分页查询提现记录
     * @param page
     * @param paramCondition 参数信息
     * @return
     */
    IPage<UmsWithdraw> getWithdrawPageList(IPage page, UmsWithdraw paramCondition);


}
