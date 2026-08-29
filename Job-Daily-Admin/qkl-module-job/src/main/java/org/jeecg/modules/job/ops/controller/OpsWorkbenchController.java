package org.jeecg.modules.job.ops.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.modules.job.job.service.IJobOrderService;
import org.jeecg.modules.job.ops.dto.SyncWithdrawTransferDTO;
import org.jeecg.modules.job.ops.service.IOpsWorkbenchService;
import org.jeecg.modules.job.ops.vo.OpsWorkbenchQueuesVO;
import org.jeecg.modules.job.ops.vo.OpsWorkbenchSummaryVO;
import org.jeecg.modules.job.pay.entity.TransferToUserResponse;
import org.jeecg.modules.job.ums.service.IUmsWithdrawService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 运营工作台
 */
@Api(tags = "运营工作台")
@RestController
@RequestMapping("/ops/workbench")
@Slf4j
public class OpsWorkbenchController {

    @Resource
    private IOpsWorkbenchService opsWorkbenchService;
    @Resource
    private IUmsWithdrawService withdrawService;
    @Resource
    private IJobOrderService orderService;

    @AutoLog(value = "运营工作台-汇总")
    @ApiOperation("汇总待办与近7日指标")
    @GetMapping("/summary")
    public Result<OpsWorkbenchSummaryVO> summary() {
        return Result.OK(opsWorkbenchService.getSummary());
    }

    @AutoLog(value = "运营工作台-待办明细")
    @ApiOperation("待办明细（岗/认证/订单/提现）")
    @GetMapping("/queues")
    public Result<OpsWorkbenchQueuesVO> queues(@RequestParam(name = "limit", defaultValue = "20") Integer limit) {
        int safe = limit == null ? 20 : limit;
        OpsWorkbenchQueuesVO vo = new OpsWorkbenchQueuesVO();
        vo.setPendingPosts(opsWorkbenchService.listPendingPosts(safe));
        vo.setPendingRealnames(opsWorkbenchService.listPendingRealnames(safe));
        vo.setPendingCompanies(opsWorkbenchService.listPendingCompanies(safe));
        vo.setAbnormalOrders(opsWorkbenchService.listAbnormalOrders(safe));
        vo.setWithdrawQueue(opsWorkbenchService.listWithdrawQueue(safe));
        return Result.OK(vo);
    }

    @AutoLog(value = "运营工作台-提现查单回写")
    @ApiOperation("按商户单号查微信转账状态并回写")
    @PostMapping("/syncWithdrawTransfer")
    public Result<TransferToUserResponse> syncWithdrawTransfer(@Validated @RequestBody SyncWithdrawTransferDTO dto) {
        try {
            TransferToUserResponse resp = withdrawService.getTransferByOutBillNo(dto.getOutBillNo());
            if (resp == null) {
                return Result.error("查单无结果，请稍后重试");
            }
            return Result.OK(resp);
        } catch (Exception e) {
            log.error("提现查单失败 outBillNo={}", dto.getOutBillNo(), e);
            return Result.error("查单失败：" + e.getMessage());
        }
    }

    /**
     * 立即执行订单自动清理（等同 OrderJob）：待确认超时、待开工过期、待评价超时
     */
    @AutoLog(value = "运营工作台-执行订单自动清理")
    @ApiOperation("立即执行订单自动清理（待确认超时/待开工过期/待评价超时）")
    @PostMapping("/runOrderAutoFinish")
    public Result<Map<String, Object>> runOrderAutoFinish() {
        try {
            orderService.autoFinishOrder();
            Map<String, Object> ret = new HashMap<>(4);
            ret.put("message", "已执行，详见服务日志 OrderJob 完成统计");
            return Result.OK(ret);
        } catch (Exception e) {
            log.error("手动执行订单自动清理失败", e);
            return Result.error("执行失败：" + e.getMessage());
        }
    }
}