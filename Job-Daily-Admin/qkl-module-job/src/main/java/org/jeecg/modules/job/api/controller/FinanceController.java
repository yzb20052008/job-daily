package org.jeecg.modules.job.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.job.constant.BizConstants;
import org.jeecg.modules.job.exception.BizException;
import org.jeecg.modules.job.pay.entity.TransferToUserResponse;
import org.jeecg.modules.job.pay.entity.WxPayV3Bean;
import org.jeecg.modules.job.pay.service.IPayService;
import org.jeecg.modules.job.support.IdempotentHelper;
import org.jeecg.modules.job.ums.entity.UmsAccount;
import org.jeecg.modules.job.ums.entity.UmsAccountRecords;
import org.jeecg.modules.job.ums.entity.UmsWithdraw;
import org.jeecg.modules.job.ums.entity.UmsWithdrawAccount;
import org.jeecg.modules.job.ums.service.IUmsAccountRecordsService;
import org.jeecg.modules.job.ums.service.IUmsAccountService;
import org.jeecg.modules.job.ums.service.IUmsWithdrawAccountService;
import org.jeecg.modules.job.ums.service.IUmsWithdrawService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * 财务管理模块
 */
@RestController
@RequestMapping("/api/finance")
@Api(tags="移动端财务管理模块")
@Slf4j
public class FinanceController {
    @Autowired
    private IUmsAccountRecordsService accountRecordsService;
    @Autowired
    private IUmsAccountService accountService;
    @Autowired
    private IUmsWithdrawService withdrawService;
    @Resource
    private IUmsWithdrawAccountService withdrawAccountService;
    @Autowired
    private IPayService payService;
    @Resource
    private IdempotentHelper idempotentHelper;


    /**
     * 查询收支明细
     */
    @ResponseBody
    @RequestMapping(value = "/getFinanceList", method = RequestMethod.GET)
    @ApiOperation(value = "查询收支明细", notes = "查询收支明细")
    public Result<Object> getApplyList(UmsAccountRecords param,@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                       @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        param.setUserId(user.getId());
        IPage<UmsAccountRecords> pageInfo = accountRecordsService.page(new Page<>(pageNo,pageSize),new QueryWrapper<>(param).orderByDesc("create_time"));
        return Result.OK(pageInfo);
    }


    /**
     * 查询提现记录
     */
    @ResponseBody
    @RequestMapping(value = "/getWithdrawList", method = RequestMethod.GET)
    @ApiOperation(value = "查询提现记录", notes = "查询提现记录")
    public Result<Object> getWithdrawList(UmsWithdraw param,
                                          @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                          @RequestParam(name="limit", defaultValue="10") Integer pageSize) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        param.setUserId(user.getId());
        IPage<UmsWithdraw> pageInfo = withdrawService.page(new Page<>(pageNo,pageSize),new QueryWrapper<>(param).orderByDesc("create_time"));
        return Result.OK(pageInfo);
    }

    /**
     * 查询账户余额
     */
    @ResponseBody
    @RequestMapping(value = "/getAccountDetail", method = RequestMethod.GET)
    @ApiOperation(value = "查询账户余额", notes = "查询账户余额")
    public Result<Object> getApplyDetail() {
        try {
            LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            UmsAccount result = accountService.getOne(new LambdaQueryWrapper<UmsAccount>().eq(UmsAccount::getUserId,user.getId()));
            if (result==null){
                result=new UmsAccount();
                //创建会员账户信息
                result.setUserId(user.getId());
                result.setBalance(BigDecimal.ZERO);
                result.setBalanceWithdraw(BigDecimal.ZERO);
                result.setBalanceFrozen(BigDecimal.ZERO);
                accountService.save(result);
            }
            //查询本月收入
            String month= DateUtils.getCurrentTime("yyyy-MM");
            month=month+"-01";
            UmsAccountRecords accountRecordsResult=accountRecordsService.getStatistics(new UmsAccountRecords().setUserId(user.getId()).setStartDate(month));
            if (accountRecordsResult!=null){
                result.setTotalMoney(accountRecordsResult.getMoney());
            }else{
                result.setTotalMoney(BigDecimal.ZERO);
            }
            return Result.OK(result);
        } catch (Exception e) {
            log.error("查询账户余额失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 提现申请
     */
    @ResponseBody
    @RequestMapping(value = "/withdrawApply", method = RequestMethod.POST)
    @ApiOperation(value = "提现申请", notes = "提现申请")
    public Result<Object> withdrawApply(@RequestBody UmsWithdraw param) {
        try {
            LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            if (user == null) {
                return Result.error("请先登录");
            }
            // 按用户短时幂等（替代全局 lockKey=withdrawApply）
            idempotentHelper.assertWithdrawOnce(user.getId());
            param.setUserId(user.getId());
            if (param.getAccountType() == null) {
                return Result.error("请选择提现方式");
            }
            if (BizConstants.ACCOUNT_TYPE_WX.equals(param.getAccountType())) {
                param.setWithdrawAccount(user.getThirdId());
            }
            withdrawService.add(param);
            return Result.ok(true);
        } catch (BizException e) {
            return Result.error(e.getErrCode(), e.getMessage());
        } catch (Exception e) {
            log.error("提现申请失败", e);
            return Result.error(e.getMessage());
        }
    }


    /**
     * 查询提现账户
     */
    @ResponseBody
    @RequestMapping(value = "/getWithdrawAccount", method = RequestMethod.GET)
    @ApiOperation(value = "查询提现账户", notes = "查询提现账户")
    public Result<Object> getWithdrawAccount(@RequestParam(name="accountType") Integer accountType) {
        try {
            LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            UmsWithdrawAccount result = withdrawAccountService.getWithdrawAccount(user.getId(),accountType);
            return Result.OK(result);
        } catch (Exception e) {
			log.error("操作异常", e);
			return Result.error(e.getMessage());
        }
    }

    @Resource
    WxPayV3Bean wxPayV3Bean;
    /**
     * 查询待确认提现收款信息
     */
    @ResponseBody
    @RequestMapping(value = "/getTransferConfirmList", method = RequestMethod.GET)
    @ApiOperation(value = "查询待确认提现收款信息", notes = "查询待确认提现收款信息")
    public Result<Object> getTransferConfirmList(@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                          @RequestParam(name="limit", defaultValue="10") Integer pageSize) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        IPage<UmsWithdraw> pageInfo = withdrawService.page(new Page<>(pageNo,pageSize),new QueryWrapper<>(new UmsWithdraw().setUserId(user.getId()).setTransferStatus(BizConstants.TRANSFER_STATUS_WAIT_USER_CONFIRM)).select("id,package_info,user_id,transfer_status,out_bill_no,create_time,update_time").orderByDesc("create_time"));
        //配置小程序ID-appid，商户ID-mchId
        pageInfo.getRecords().forEach(item->{
            item.setAppId(wxPayV3Bean.getAppId());
            item.setMchId(wxPayV3Bean.getMchId());
        });
        return Result.OK(pageInfo);
    }

    /**
     * 查询待确认提现收款信息
     */
    @ResponseBody
    @RequestMapping(value = "/getTransferByOutBillNo", method = RequestMethod.GET)
    @ApiOperation(value = "查询待确认提现收款信息", notes = "查询待确认提现收款信息")
    public Result<Object> getTransferByOutBillNo(@RequestParam(name="outBillNo") String outBillNo) {
        try {
            LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            if (user == null) {
                return Result.error("请先登录");
            }
            TransferToUserResponse response = withdrawService.getTransferByOutBillNo(outBillNo, user.getId());
            return Result.OK(response);
        } catch (Exception e) {
            log.error("查单失败 outBillNo={}", outBillNo, e);
            return Result.error(e.getMessage());
        }
    }
}

