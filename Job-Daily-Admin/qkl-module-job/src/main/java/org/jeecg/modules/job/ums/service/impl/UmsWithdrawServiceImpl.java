package org.jeecg.modules.job.ums.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.job.api.util.WxUtil;
import org.jeecg.modules.job.cms.service.ICmsNoticeService;
import org.jeecg.modules.job.constant.BizConstants;
import org.jeecg.modules.job.pay.entity.TransferToUserResponse;
import org.jeecg.modules.job.pay.service.IPayService;
import org.jeecg.modules.job.ums.entity.UmsAccount;
import org.jeecg.modules.job.ums.entity.UmsAccountRecords;
import org.jeecg.modules.job.ums.entity.UmsWithdraw;
import org.jeecg.modules.job.ums.mapper.UmsAccountMapper;
import org.jeecg.modules.job.ums.mapper.UmsWithdrawMapper;
import org.jeecg.modules.job.ums.service.IUmsAccountRecordsService;
import org.jeecg.modules.job.ums.service.IUmsAccountService;
import org.jeecg.modules.job.ums.service.IUmsWithdrawAccountService;
import org.jeecg.modules.job.ums.service.IUmsWithdrawService;
import org.jeecg.modules.job.utils.WX_TemplateMsgUtil;
import org.jeecg.modules.job.utils.WxMsgSendUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @Description: 用户提现
 * @Author: qingkonglan
 * @Date:   2023-03-30
 * @Version: V1.0
 */
@Service
public class UmsWithdrawServiceImpl extends ServiceImpl<UmsWithdrawMapper, UmsWithdraw> implements IUmsWithdrawService {

    @Resource
    private IUmsAccountService accountService;
    @Resource
    private IUmsWithdrawAccountService withdrawAccountService;
    @Resource
    private IUmsAccountRecordsService accountRecordsService;
    @Resource
    private ISysBaseAPI sysBaseAPI;
    @Resource
    private ICmsNoticeService noticeService;
    @Resource
    private IPayService payService;

    @Override
    public boolean add(UmsWithdraw param) {
        //判断提现金额是否合理
        UmsAccount account=accountService.getOne(new LambdaQueryWrapper<UmsAccount>().eq(UmsAccount::getUserId,param.getUserId()));
        if (account!=null && account.getBalanceWithdraw().compareTo(param.getMoney())>=0){
            //可以提现
            param.setLastBalance(account.getBalanceWithdraw());
            param.setBalance(account.getBalanceWithdraw().subtract(param.getMoney()));
            param.setWithdrawStatus(0);//待审核
            String no= DateUtils.formatDate(new Date(),"yyyyMMddHHmmss")+ RandomUtil.randomNumbers(5);
            param.setOutBillNo(no);
            this.save(param);
            //更改账户余额
            UmsAccount accountParam=new UmsAccount();
            accountParam.setId(account.getId());
            accountParam.setBalanceFrozen(account.getBalanceFrozen().add(param.getMoney()));//冻结余额
            accountParam.setBalanceWithdraw(account.getBalanceWithdraw().subtract(param.getMoney()));
            accountService.updateById(accountParam);
            //更新提现账号
            withdrawAccountService.updateAccount(param.getUserId(),param.getAccountType(),param.getWithdrawName(),param.getWithdrawAccount());
            //添加余额操作记录
            accountRecordsService.addAccountRecords(param.getUserId(),param.getMoney(),BizConstants.TRADE_TYPE_WITHDRAW,"","提现申请,临时冻结提现金额");
            //提现动账通知
            noticeService.addBalanceNotice(param.getUserId(),"提交提现申请","提现申请提交成功，提现金额："+param.getMoney()+"元",param.getId());
        }else{
            throw new RuntimeException("当前可提现余额不足");
        }
        return true;
    }

    @Transactional
    @Override
    public boolean updateStatus(String id, int status, String reason) {
        UmsWithdraw umsWithdraw = this.getById(id);
        if (umsWithdraw == null) {
            throw new JeecgBootException("提现单不存在");
        }
        if (!BizConstants.WITHDRAW_STATUS_DEFAULT.equals(umsWithdraw.getWithdrawStatus())) {
            throw new JeecgBootException("提现单已处理，请勿重复操作");
        }
        if (status != BizConstants.WITHDRAW_STATUS_SUCCESS && status != BizConstants.WITHDRAW_STATUS_FAILURE) {
            throw new JeecgBootException("提现审核状态无效");
        }
        LoginUser userInfo = sysBaseAPI.getUserById(umsWithdraw.getUserId());
        umsWithdraw.setWithdrawStatus(status);
        if (status == BizConstants.WITHDRAW_STATUS_SUCCESS) {
            if (umsWithdraw.getMoney() == null || umsWithdraw.getMoney().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                throw new JeecgBootException("提现金额异常");
            }
            if (oConvertUtils.isEmpty(umsWithdraw.getWithdrawAccount())) {
                throw new JeecgBootException("提现账号为空");
            }
            // 发起微信商家转账（金额以提现单为准）
            TransferToUserResponse result = payService.transferNew(
                    umsWithdraw.getWithdrawAccount(),
                    umsWithdraw.getOutBillNo(),
                    umsWithdraw.getMoney(),
                    "余额提现",
                    "余额提现申请通过");
            if (result == null || oConvertUtils.isEmpty(result.getOut_bill_no())) {
                throw new JeecgBootException("发起转账失败，请稍后重试");
            }
            this.updateTransferStatus(result.getOut_bill_no(), result.getState(), result.getPackage_info());
        } else {
            umsWithdraw.setReason(reason);
            accountService.balanceWithdrawResult(umsWithdraw.getUserId(), umsWithdraw.getMoney(), BizConstants.WITHDRAW_STATUS_FAILURE);
            noticeService.addBalanceNotice(umsWithdraw.getUserId(), "提现未通过", "提现申请未通过审核，失败原因：" + reason, umsWithdraw.getId());
            try {
                String token = WX_TemplateMsgUtil.getAccessToken(WxUtil.getAppId(), WxUtil.getAppSecret());
                String page = "pages/index/index";
                WxMsgSendUtils.sendMsgForWithdraw(DateUtils.formatTime(umsWithdraw.getCreateTime()), umsWithdraw.getMoney().toString(), "提现失败", reason, userInfo.getThirdId(), token, page);
            } catch (Exception e) {
                // 通知失败不影响审核主流程
            }
        }
        return this.updateById(umsWithdraw);
    }

    @Transactional
    @Override
    public boolean updateTransferStatus(String outBillNo, String transferStatus, String packageInfo) {
        UmsWithdraw withdraw = this.getOne(new QueryWrapper<>(new UmsWithdraw().setOutBillNo(outBillNo)));
        if (withdraw == null) {
            return false;
        }
        // 终态幂等：已成功或已失败不再重复动账
        String currentTransfer = withdraw.getTransferStatus();
        if (BizConstants.TRANSFER_STATUS_SUCCESS.equals(currentTransfer)
                || BizConstants.TRANSFER_STATUS_FAIL.equals(currentTransfer)
                || BizConstants.TRANSFER_STATUS_CANCELLED.equals(currentTransfer)) {
            return true;
        }
        withdraw.setTransferStatus(transferStatus);
        if (oConvertUtils.isNotEmpty(packageInfo)) {
            withdraw.setPackageInfo(packageInfo);
        }
        this.updateById(withdraw);
        LoginUser userInfo = sysBaseAPI.getUserById(withdraw.getUserId());
        if (BizConstants.TRANSFER_STATUS_ACCEPTED.equals(transferStatus)
                || BizConstants.TRANSFER_STATUS_WAIT_USER_CONFIRM.equals(transferStatus)) {
            noticeService.addBalanceNotice(withdraw.getUserId(), "提现审核通过", "提现申请已通过审核，请尽快进入小程序确认", withdraw.getId());
            try {
                String token = WX_TemplateMsgUtil.getAccessToken(WxUtil.getAppId(), WxUtil.getAppSecret());
                String page = "pages/index/index";
                WxMsgSendUtils.sendMsgForWithdraw(DateUtils.formatTime(withdraw.getCreateTime()), withdraw.getMoney().toString(), "提现通过", "及时进入小程序确认收款", userInfo.getThirdId(), token, page);
            } catch (Exception e) {
                // ignore
            }
        } else if (BizConstants.TRANSFER_STATUS_SUCCESS.equals(transferStatus)) {
            accountService.balanceWithdrawResult(withdraw.getUserId(), withdraw.getMoney(), BizConstants.WITHDRAW_STATUS_SUCCESS);
            noticeService.addBalanceNotice(withdraw.getUserId(), "提现通过", "提现申请已通过审核，请关注到账情况，提现金额：" + withdraw.getMoney() + "元", withdraw.getId());
            try {
                String token = WX_TemplateMsgUtil.getAccessToken(WxUtil.getAppId(), WxUtil.getAppSecret());
                String page = "pages/index/index";
                WxMsgSendUtils.sendMsgForWithdraw(DateUtils.formatTime(withdraw.getCreateTime()), withdraw.getMoney().toString(), "提现成功", "及时查看是否到账", userInfo.getThirdId(), token, page);
            } catch (Exception e) {
                // ignore
            }
        } else if (BizConstants.TRANSFER_STATUS_FAIL.equals(transferStatus) || BizConstants.TRANSFER_STATUS_CANCELLED.equals(transferStatus)) {
            String reason = "转账失败";
            withdraw.setReason(reason);
            this.updateById(withdraw);
            accountService.balanceWithdrawResult(withdraw.getUserId(), withdraw.getMoney(), BizConstants.WITHDRAW_STATUS_FAILURE);
            noticeService.addBalanceNotice(withdraw.getUserId(), "提现未通过", "提现申请未通过审核，失败原因：" + reason, withdraw.getId());
            try {
                String token = WX_TemplateMsgUtil.getAccessToken(WxUtil.getAppId(), WxUtil.getAppSecret());
                String page = "pages/index/index";
                WxMsgSendUtils.sendMsgForWithdraw(DateUtils.formatTime(withdraw.getCreateTime()), withdraw.getMoney().toString(), "提现失败", reason, userInfo.getThirdId(), token, page);
            } catch (Exception e) {
                // ignore
            }
        }
        return true;
    }

    @Transactional
    @Override
    public TransferToUserResponse getTransferByOutBillNo(String outBillNo) {
        TransferToUserResponse response=payService.getTransferByOutBillNo(outBillNo);
        if (response==null){
            return null;
        }
        this.updateTransferStatus(outBillNo,response.getState(),null);
        return response;
    }

    /**
     * 关闭「审核通过但无转账单号」的异常提现并解冻，避免资金长期冻结。
     * 注意：已有 out_bill_no 的单据禁止走本方法，应先微信查单。
     */
    @Transactional
    @Override
    public boolean closeAbnormalWithdraw(String id, String reason) {
        UmsWithdraw withdraw = this.getById(id);
        if (withdraw == null) {
            throw new JeecgBootException("提现单不存在");
        }
        if (!BizConstants.WITHDRAW_STATUS_SUCCESS.equals(withdraw.getWithdrawStatus())) {
            throw new JeecgBootException("仅允许处理审核通过状态的异常提现");
        }
        if (oConvertUtils.isNotEmpty(withdraw.getOutBillNo())) {
            throw new JeecgBootException("该单已有商户单号，请先执行微信查单，禁止直接关闭");
        }
        String currentTransfer = withdraw.getTransferStatus();
        if (BizConstants.TRANSFER_STATUS_SUCCESS.equals(currentTransfer)
                || BizConstants.TRANSFER_STATUS_FAIL.equals(currentTransfer)
                || BizConstants.TRANSFER_STATUS_CANCELLED.equals(currentTransfer)) {
            throw new JeecgBootException("转账已终态，无需重复处理");
        }
        String closeReason = oConvertUtils.isEmpty(reason)
                ? "系统关闭：审核通过但未生成转账单号，已解冻金额"
                : reason;
        withdraw.setWithdrawStatus(BizConstants.WITHDRAW_STATUS_FAILURE);
        withdraw.setTransferStatus(BizConstants.TRANSFER_STATUS_FAIL);
        withdraw.setReason(closeReason);
        this.updateById(withdraw);
        accountService.balanceWithdrawResult(withdraw.getUserId(), withdraw.getMoney(), BizConstants.WITHDRAW_STATUS_FAILURE);
        noticeService.addBalanceNotice(withdraw.getUserId(), "提现已关闭", closeReason, withdraw.getId());
        return true;
    }

    @Override
    public IPage<UmsWithdraw> getWithdrawPageList(IPage page, UmsWithdraw paramCondition) {
        return baseMapper.getWithdrawPageList(page,paramCondition);
    }
}
