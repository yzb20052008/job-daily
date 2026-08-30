package org.jeecg.modules.job.ums.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.job.api.util.WxUtil;
import org.jeecg.modules.job.base.entity.BaseConfig;
import org.jeecg.modules.job.base.service.IBaseConfigService;
import org.jeecg.modules.job.cms.service.ICmsNoticeService;
import org.jeecg.modules.job.constant.BizConstants;
import org.jeecg.modules.job.constant.BizErrorCodes;
import org.jeecg.modules.job.exception.BizException;
import org.jeecg.modules.job.pay.entity.TransferToUserResponse;
import org.jeecg.modules.job.pay.service.IPayService;
import org.jeecg.modules.job.ums.entity.UmsAccount;
import org.jeecg.modules.job.ums.entity.UmsWithdraw;
import org.jeecg.modules.job.ums.mapper.UmsWithdrawMapper;
import org.jeecg.modules.job.ums.service.IUmsAccountRecordsService;
import org.jeecg.modules.job.ums.service.IUmsAccountService;
import org.jeecg.modules.job.ums.service.IUmsWithdrawAccountService;
import org.jeecg.modules.job.ums.service.IUmsWithdrawService;
import org.jeecg.modules.job.utils.WX_TemplateMsgUtil;
import org.jeecg.modules.job.utils.WxMsgSendUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * @Description: 用户提现
 * @Author: qingkonglan
 * @Date:   2023-03-30
 * @Version: V1.0
 */
@Slf4j
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
    @Resource
    private IBaseConfigService configService;
    /** 代理自身，保证 initiateTransfer → updateTransferStatus 走事务切面 */
    @Lazy
    @Resource
    private IUmsWithdrawService self;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean add(UmsWithdraw param) {
        if (param.getMoney() == null || param.getMoney().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BizException(BizErrorCodes.WITHDRAW_AMOUNT_INVALID);
        }
        if (oConvertUtils.isEmpty(param.getUserId())) {
            throw new JeecgBootException("用户不能为空");
        }
        if (param.getAccountType() == null) {
            throw new JeecgBootException("请选择提现方式");
        }
        if (!BizConstants.ACCOUNT_TYPE_WX.equals(param.getAccountType())) {
            throw new BizException(BizErrorCodes.WITHDRAW_CHANNEL_UNSUPPORTED);
        }
        validateWithdrawLimits(param.getUserId(), param.getMoney());

        // 行锁账户，防止并发超提
        UmsAccount account = accountService.lockByUserId(param.getUserId());
        if (account.getBalanceWithdraw() == null
                || account.getBalanceWithdraw().compareTo(param.getMoney()) < 0) {
            throw new BizException(BizErrorCodes.WITHDRAW_BALANCE_SHORT);
        }
        param.setLastBalance(account.getBalanceWithdraw());
        param.setBalance(account.getBalanceWithdraw().subtract(param.getMoney()));
        param.setWithdrawStatus(BizConstants.WITHDRAW_STATUS_DEFAULT);
        String no = DateUtils.formatDate(new Date(), "yyyyMMddHHmmss") + RandomUtil.randomNumbers(5);
        param.setOutBillNo(no);
        this.save(param);

        UmsAccount accountParam = new UmsAccount();
        accountParam.setId(account.getId());
        BigDecimal frozen = account.getBalanceFrozen() == null ? BigDecimal.ZERO : account.getBalanceFrozen();
        accountParam.setBalanceFrozen(frozen.add(param.getMoney()));
        accountParam.setBalanceWithdraw(account.getBalanceWithdraw().subtract(param.getMoney()));
        accountService.updateById(accountParam);

        withdrawAccountService.updateAccount(param.getUserId(), param.getAccountType(),
                param.getWithdrawName(), param.getWithdrawAccount());
        accountRecordsService.addAccountRecords(param.getUserId(), param.getMoney(),
                BizConstants.TRADE_TYPE_WITHDRAW, "", "提现申请,临时冻结提现金额");
        noticeService.addBalanceNotice(param.getUserId(), "提交提现申请",
                "提现申请提交成功，提现金额：" + param.getMoney() + "元", param.getId());
        return true;
    }

    /**
     * 校验单次/当日提现限额（与 base_config、前端规则一致）
     */
    private void validateWithdrawLimits(String userId, BigDecimal money) {
        BigDecimal min = readConfigDecimal(BizConstants.WITHDRAW_MIN, new BigDecimal("100"));
        BigDecimal max = readConfigDecimal(BizConstants.WITHDRAW_MAX, new BigDecimal("2000"));
        BigDecimal dayMax = readConfigDecimal(BizConstants.WITHDRAW_DAY_MAX, new BigDecimal("5000"));
        if (money.compareTo(min) < 0) {
            throw new BizException(BizErrorCodes.WITHDRAW_BELOW_MIN.getCode(),
                    "提现金额不能低于" + min.stripTrailingZeros().toPlainString() + "元");
        }
        if (money.compareTo(max) > 0) {
            throw new BizException(BizErrorCodes.WITHDRAW_ABOVE_MAX.getCode(),
                    "单次提现不能超过" + max.stripTrailingZeros().toPlainString() + "元");
        }
        Date dayStart = startOfToday();
        QueryWrapper<UmsWithdraw> dayQw = new QueryWrapper<>();
        dayQw.select("IFNULL(SUM(money),0) AS total")
                .eq("user_id", userId)
                .ge("create_time", dayStart)
                .ne("withdraw_status", BizConstants.WITHDRAW_STATUS_FAILURE);
        Map<String, Object> sumMap = this.getMap(dayQw);
        BigDecimal dayUsed = BigDecimal.ZERO;
        if (sumMap != null && sumMap.get("total") != null) {
            dayUsed = new BigDecimal(sumMap.get("total").toString());
        }
        if (dayUsed.add(money).compareTo(dayMax) > 0) {
            throw new BizException(BizErrorCodes.WITHDRAW_DAY_LIMIT.getCode(),
                    "今日提现累计不能超过" + dayMax.stripTrailingZeros().toPlainString() + "元");
        }
    }

    private BigDecimal readConfigDecimal(String code, BigDecimal defaultVal) {
        try {
            BaseConfig cfg = configService.getConfigByCode(code);
            if (cfg != null && oConvertUtils.isNotEmpty(cfg.getConfigValue())) {
                return new BigDecimal(cfg.getConfigValue().trim());
            }
        } catch (Exception e) {
            log.warn("读取提现限额配置失败 code={}", code, e);
        }
        return defaultVal;
    }

    private Date startOfToday() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    @Transactional(rollbackFor = Exception.class)
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

        if (status == BizConstants.WITHDRAW_STATUS_SUCCESS) {
            if (umsWithdraw.getMoney() == null || umsWithdraw.getMoney().compareTo(BigDecimal.ZERO) <= 0) {
                throw new JeecgBootException("提现金额异常");
            }
            if (oConvertUtils.isEmpty(umsWithdraw.getWithdrawAccount())) {
                throw new JeecgBootException("提现账号为空");
            }
            if (umsWithdraw.getAccountType() != null
                    && !BizConstants.ACCOUNT_TYPE_WX.equals(umsWithdraw.getAccountType())) {
                throw new BizException(BizErrorCodes.WITHDRAW_CHANNEL_UNSUPPORTED);
            }
            // 先查微信商户余额（避免持行锁期间调外部接口）
            payService.assertMerchantBalanceEnough(umsWithdraw.getMoney());
            // 审核通过必须以「已冻结金额」为准：申请时已从可提余额划入冻结，不可再用可提余额判断
            UmsAccount account = accountService.lockByUserId(umsWithdraw.getUserId());
            BigDecimal frozen = account.getBalanceFrozen() == null ? BigDecimal.ZERO : account.getBalanceFrozen();
            if (frozen.compareTo(umsWithdraw.getMoney()) < 0) {
                throw new BizException(BizErrorCodes.WITHDRAW_FROZEN_SHORT.getCode(),
                        "冻结余额不足（当前冻结" + frozen.stripTrailingZeros().toPlainString()
                                + "元，本单" + umsWithdraw.getMoney().stripTrailingZeros().toPlainString()
                                + "元），请先核对账户后再审，禁止在资金未冻结时放行");
            }
            // CAS：仅待审 → 审核通过；微信转账在事务外 initiateTransfer
            boolean cas = this.update(new UpdateWrapper<UmsWithdraw>()
                    .eq("id", id)
                    .eq("withdraw_status", BizConstants.WITHDRAW_STATUS_DEFAULT)
                    .set("withdraw_status", BizConstants.WITHDRAW_STATUS_SUCCESS));
            if (!cas) {
                throw new JeecgBootException("提现单已处理，请勿重复操作");
            }
            return true;
        }

        // 审核拒绝：CAS + 解冻
        boolean casFail = this.update(new UpdateWrapper<UmsWithdraw>()
                .eq("id", id)
                .eq("withdraw_status", BizConstants.WITHDRAW_STATUS_DEFAULT)
                .set("withdraw_status", BizConstants.WITHDRAW_STATUS_FAILURE)
                .set("reason", reason));
        if (!casFail) {
            throw new JeecgBootException("提现单已处理，请勿重复操作");
        }
        accountService.balanceWithdrawResult(umsWithdraw.getUserId(), umsWithdraw.getMoney(),
                BizConstants.WITHDRAW_STATUS_FAILURE);
        noticeService.addBalanceNotice(umsWithdraw.getUserId(), "提现未通过",
                "提现申请未通过审核，失败原因：" + reason, umsWithdraw.getId());
        try {
            LoginUser userInfo = sysBaseAPI.getUserById(umsWithdraw.getUserId());
            if (userInfo != null) {
                String token = WX_TemplateMsgUtil.getAccessToken(WxUtil.getAppId(), WxUtil.getAppSecret());
                String page = "pages/index/index";
                WxMsgSendUtils.sendMsgForWithdraw(DateUtils.formatTime(umsWithdraw.getCreateTime()),
                        umsWithdraw.getMoney().toString(), "提现失败", reason, userInfo.getThirdId(), token, page);
            }
        } catch (Exception e) {
            // 通知失败不影响审核主流程
        }
        return true;
    }

    /**
     * 审核通过后发起转账：先查单，无单再发起，避免审核与渠道同事务回滚导致重发。
     */
    @Override
    public void initiateTransfer(String id) {
        UmsWithdraw withdraw = this.getById(id);
        if (withdraw == null) {
            throw new JeecgBootException("提现单不存在");
        }
        if (!BizConstants.WITHDRAW_STATUS_SUCCESS.equals(withdraw.getWithdrawStatus())) {
            throw new JeecgBootException("仅审核通过的提现单可发起转账");
        }
        if (isTransferTerminal(withdraw.getTransferStatus())) {
            log.info("转账已终态，跳过发起 outBillNo={}, status={}",
                    withdraw.getOutBillNo(), withdraw.getTransferStatus());
            return;
        }
        if (oConvertUtils.isEmpty(withdraw.getOutBillNo())) {
            throw new JeecgBootException("商户单号缺失，无法发起转账");
        }
        if (oConvertUtils.isEmpty(withdraw.getWithdrawAccount())) {
            throw new JeecgBootException("提现账号为空");
        }

        // 发起前查单：渠道已有单据则只回写，禁止同单号重发
        try {
            TransferToUserResponse existing = payService.getTransferByOutBillNo(withdraw.getOutBillNo());
            if (existing != null && oConvertUtils.isNotEmpty(existing.getState())) {
                log.info("渠道已存在转账单，仅回写 outBillNo={}, state={}",
                        withdraw.getOutBillNo(), existing.getState());
                self.updateTransferStatus(existing.getOut_bill_no(), existing.getState(),
                        existing.getPackage_info(), existing.getFail_reason());
                return;
            }
        } catch (Exception e) {
            log.info("查单无记录或失败，继续发起新转账 outBillNo={}, err={}",
                    withdraw.getOutBillNo(), e.getMessage());
        }

        TransferToUserResponse result = payService.transferNew(
                withdraw.getWithdrawAccount(),
                withdraw.getOutBillNo(),
                withdraw.getMoney(),
                "余额提现",
                "余额提现申请通过");
        if (result == null || oConvertUtils.isEmpty(result.getOut_bill_no())) {
            throw new JeecgBootException("发起转账失败，请稍后重试");
        }
        self.updateTransferStatus(result.getOut_bill_no(), result.getState(),
                result.getPackage_info(), result.getFail_reason());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateTransferStatus(String outBillNo, String transferStatus, String packageInfo) {
        return doUpdateTransferStatus(outBillNo, transferStatus, packageInfo, null);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateTransferStatus(String outBillNo, String transferStatus, String packageInfo, String failReason) {
        return doUpdateTransferStatus(outBillNo, transferStatus, packageInfo, failReason);
    }

    /**
     * CAS 回写转账态：仅非终态可更新；终态成功后才扣冻/解冻，防并发双解冻。
     */
    private boolean doUpdateTransferStatus(String outBillNo, String transferStatus,
                                           String packageInfo, String failReason) {
        if (oConvertUtils.isEmpty(outBillNo) || oConvertUtils.isEmpty(transferStatus)) {
            return false;
        }
        UmsWithdraw withdraw = this.getOne(new QueryWrapper<>(new UmsWithdraw().setOutBillNo(outBillNo)));
        if (withdraw == null) {
            // 无对应提现单：无法靠重试修复，确认收到避免微信死循环
            log.error("转账回写未找到提现单 outBillNo={}", outBillNo);
            return true;
        }
        String currentTransfer = withdraw.getTransferStatus();
        if (isTransferTerminal(currentTransfer)) {
            // 已终态：幂等成功，不重复动账
            return true;
        }

        boolean terminalTarget = isTransferTerminal(transferStatus);
        UpdateWrapper<UmsWithdraw> uw = new UpdateWrapper<>();
        uw.eq("out_bill_no", outBillNo)
                .and(w -> w.isNull("transfer_status")
                        .or().eq("transfer_status", "")
                        .or().notIn("transfer_status",
                                BizConstants.TRANSFER_STATUS_SUCCESS,
                                BizConstants.TRANSFER_STATUS_FAIL,
                                BizConstants.TRANSFER_STATUS_CANCELLED));
        // 中间态仅在状态变化时更新，避免回调重试重复通知
        if (!terminalTarget) {
            uw.and(w -> w.isNull("transfer_status")
                    .or().eq("transfer_status", "")
                    .or().ne("transfer_status", transferStatus));
        }
        uw.set("transfer_status", transferStatus);
        if (oConvertUtils.isNotEmpty(packageInfo)) {
            uw.set("package_info", packageInfo);
        }
        if (BizConstants.TRANSFER_STATUS_SUCCESS.equals(transferStatus)) {
            uw.set("withdraw_status", BizConstants.WITHDRAW_STATUS_SUCCESS);
        } else if (BizConstants.TRANSFER_STATUS_FAIL.equals(transferStatus)
                || BizConstants.TRANSFER_STATUS_CANCELLED.equals(transferStatus)) {
            String reason = oConvertUtils.isNotEmpty(failReason) ? failReason : "转账失败";
            uw.set("withdraw_status", BizConstants.WITHDRAW_STATUS_FAILURE);
            uw.set("reason", reason);
        }

        boolean casOk = this.update(uw);
        if (!casOk) {
            // 并发下另一线程已写成终态，或中间态未变化：均视为幂等成功
            return true;
        }

        // CAS 成功后再动账 / 通知
        LoginUser userInfo = null;
        try {
            userInfo = sysBaseAPI.getUserById(withdraw.getUserId());
        } catch (Exception e) {
            log.warn("查询用户失败 userId={}", withdraw.getUserId());
        }

        if (BizConstants.TRANSFER_STATUS_ACCEPTED.equals(transferStatus)
                || BizConstants.TRANSFER_STATUS_PROCESSING.equals(transferStatus)
                || BizConstants.TRANSFER_STATUS_WAIT_USER_CONFIRM.equals(transferStatus)
                || BizConstants.TRANSFER_STATUS_TRANSFERING.equals(transferStatus)) {
            noticeService.addBalanceNotice(withdraw.getUserId(), "提现审核通过",
                    "提现申请已通过审核，请尽快进入小程序确认", withdraw.getId());
            sendWithdrawWxMsg(userInfo, withdraw, "提现通过", "及时进入小程序确认收款");
        } else if (BizConstants.TRANSFER_STATUS_SUCCESS.equals(transferStatus)) {
            accountService.balanceWithdrawResult(withdraw.getUserId(), withdraw.getMoney(),
                    BizConstants.WITHDRAW_STATUS_SUCCESS);
            noticeService.addBalanceNotice(withdraw.getUserId(), "提现成功",
                    "提现已到账，金额：" + withdraw.getMoney() + "元", withdraw.getId());
            sendWithdrawWxMsg(userInfo, withdraw, "提现成功", "及时查看是否到账");
        } else if (BizConstants.TRANSFER_STATUS_FAIL.equals(transferStatus)
                || BizConstants.TRANSFER_STATUS_CANCELLED.equals(transferStatus)) {
            String reason = oConvertUtils.isNotEmpty(failReason) ? failReason : "转账失败";
            accountService.balanceWithdrawResult(withdraw.getUserId(), withdraw.getMoney(),
                    BizConstants.WITHDRAW_STATUS_FAILURE);
            noticeService.addBalanceNotice(withdraw.getUserId(), "提现失败",
                    "提现转账失败，金额已解冻退回，原因：" + reason, withdraw.getId());
            sendWithdrawWxMsg(userInfo, withdraw, "提现失败", reason);
        }
        return true;
    }

    private void sendWithdrawWxMsg(LoginUser userInfo, UmsWithdraw withdraw, String title, String tip) {
        if (userInfo == null || oConvertUtils.isEmpty(userInfo.getThirdId())) {
            return;
        }
        try {
            String token = WX_TemplateMsgUtil.getAccessToken(WxUtil.getAppId(), WxUtil.getAppSecret());
            String page = "pages/index/index";
            WxMsgSendUtils.sendMsgForWithdraw(DateUtils.formatTime(withdraw.getCreateTime()),
                    withdraw.getMoney().toString(), title, tip, userInfo.getThirdId(), token, page);
        } catch (Exception e) {
            // ignore
        }
    }

    private boolean isTransferTerminal(String status) {
        return BizConstants.TRANSFER_STATUS_SUCCESS.equals(status)
                || BizConstants.TRANSFER_STATUS_FAIL.equals(status)
                || BizConstants.TRANSFER_STATUS_CANCELLED.equals(status);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TransferToUserResponse getTransferByOutBillNo(String outBillNo) {
        return getTransferByOutBillNo(outBillNo, null);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TransferToUserResponse getTransferByOutBillNo(String outBillNo, String requireUserId) {
        if (oConvertUtils.isEmpty(outBillNo)) {
            throw new JeecgBootException("商户单号不能为空");
        }
        if (oConvertUtils.isNotEmpty(requireUserId)) {
            UmsWithdraw local = this.getOne(new QueryWrapper<>(new UmsWithdraw().setOutBillNo(outBillNo)));
            if (local == null) {
                throw new JeecgBootException("提现单不存在");
            }
            if (!requireUserId.equals(local.getUserId())) {
                throw new JeecgBootException("无权查询该提现单");
            }
        }
        TransferToUserResponse response = payService.getTransferByOutBillNo(outBillNo);
        if (response == null) {
            return null;
        }
        doUpdateTransferStatus(outBillNo, response.getState(), response.getPackage_info(), response.getFail_reason());
        return response;
    }

    /**
     * 关闭「审核通过但无转账单号」的异常提现并解冻，避免资金长期冻结。
     * 注意：已有 out_bill_no 的单据禁止走本方法，应先微信查单。
     */
    @Transactional(rollbackFor = Exception.class)
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
        if (isTransferTerminal(withdraw.getTransferStatus())) {
            throw new JeecgBootException("转账已终态，无需重复处理");
        }
        String closeReason = oConvertUtils.isEmpty(reason)
                ? "系统关闭：审核通过但未生成转账单号，已解冻金额"
                : reason;
        boolean cas = this.update(new UpdateWrapper<UmsWithdraw>()
                .eq("id", id)
                .eq("withdraw_status", BizConstants.WITHDRAW_STATUS_SUCCESS)
                .and(w -> w.isNull("transfer_status")
                        .or().eq("transfer_status", "")
                        .or().notIn("transfer_status",
                                BizConstants.TRANSFER_STATUS_SUCCESS,
                                BizConstants.TRANSFER_STATUS_FAIL,
                                BizConstants.TRANSFER_STATUS_CANCELLED))
                .set("withdraw_status", BizConstants.WITHDRAW_STATUS_FAILURE)
                .set("transfer_status", BizConstants.TRANSFER_STATUS_FAIL)
                .set("reason", closeReason));
        if (!cas) {
            throw new JeecgBootException("提现单状态已变更，请刷新后重试");
        }
        accountService.balanceWithdrawResult(withdraw.getUserId(), withdraw.getMoney(),
                BizConstants.WITHDRAW_STATUS_FAILURE);
        noticeService.addBalanceNotice(withdraw.getUserId(), "提现已关闭", closeReason, withdraw.getId());
        return true;
    }

    @Override
    public IPage<UmsWithdraw> getWithdrawPageList(IPage page, UmsWithdraw paramCondition) {
        return baseMapper.getWithdrawPageList(page, paramCondition);
    }
}
