package org.jeecg.modules.job.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.job.api.vo.JobOrderVo;
import org.jeecg.modules.job.constant.BizConstants;
import org.jeecg.modules.job.constant.BizErrorCodes;
import org.jeecg.modules.job.exception.BizException;
import org.jeecg.modules.job.job.entity.JobOrder;
import org.jeecg.modules.job.job.entity.JobOrderClock;
import org.jeecg.modules.job.job.service.IJobOrderClockService;
import org.jeecg.modules.job.job.service.IJobOrderService;
import org.jeecg.modules.job.support.IdempotentHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 移动端订单
 */
@RestController
@RequestMapping("/api/order")
@Api(tags = "移动端订单管理模块")
@Slf4j
public class OrderController {

    @Autowired
    private IJobOrderService orderService;
    @Resource
    private IJobOrderClockService orderClockService;
    @Resource
    private IdempotentHelper idempotentHelper;

    @RequestMapping(value = "/getOrderList", method = RequestMethod.GET)
    @ApiOperation(value = "查询订单列表")
    public Result<Object> getOrderList(JobOrder param,
                                       @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                       @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        param.setUserId(user.getId());
        IPage<Map<String, Object>> pageInfo = orderService.getOrderList(new Page<>(pageNo, pageSize), param);
        return Result.OK(pageInfo);
    }

    @ResponseBody
    @RequestMapping(value = "/getOrderDetail", method = RequestMethod.GET)
    @ApiOperation(value = "查询订单详情")
    public Result<Object> getOrderDetail(@RequestParam("id") String id) {
        try {
            return Result.OK(orderService.getOrderDetail(id));
        } catch (BizException e) {
            return Result.error(e.getErrCode(), e.getMessage());
        } catch (Exception e) {
            log.error("查询订单详情失败", e);
            return Result.error(e.getMessage());
        }
    }

    @RequestMapping(value = "/getPostUserList", method = RequestMethod.GET)
    @ApiOperation(value = "查询员工列表")
    public Result<Object> getPostUserList(JobOrder param,
                                          @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                          @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        // 强制按当前老板过滤，禁止扫全库
        param.setPostUserId(user.getId());
        param.setUserId(null);
        IPage<Map<String, Object>> pageInfo = orderService.getPostUserList(new Page<>(pageNo, pageSize), param);
        return Result.OK(pageInfo);
    }

    @ApiOperation("提交报名申请")
    @PostMapping(value = "/addApply")
    public Result<Object> addApply(@RequestBody JSONObject jsonObject) {
        try {
            String id = jsonObject.getString("id");
            // integral 兼容旧客户端，服务端忽略并以配置扣减
            Integer integral = jsonObject.getInteger("integral");
            if (oConvertUtils.isEmpty(id)) {
                return Result.error(BizErrorCodes.PARAM_INVALID.getCode(), BizErrorCodes.PARAM_INVALID.getMessage());
            }
            LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            if (user == null) {
                return Result.error(BizErrorCodes.NOT_LOGIN.getCode(), BizErrorCodes.NOT_LOGIN.getMessage());
            }
            idempotentHelper.assertApplyOnce(user.getId(), id);
            boolean result = orderService.doApply(user.getId(), id, integral);
            return Result.ok(result);
        } catch (BizException e) {
            return Result.error(e.getErrCode(), e.getMessage());
        } catch (Exception e) {
            log.error("提交报名申请失败", e);
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("修改订单状态（服务端校验状态机与权限）")
    @PostMapping(value = "/updateOrderStatus")
    public Result<Object> updateOrderStatus(@RequestBody JSONObject jsonObject) {
        try {
            String id = jsonObject.getString("id");
            String orderStatus = jsonObject.getString("orderStatus");
            String imgs = jsonObject.getString("imgs");
            if (oConvertUtils.isEmpty(id) || oConvertUtils.isEmpty(orderStatus)) {
                return Result.error(BizErrorCodes.PARAM_INVALID.getCode(), BizErrorCodes.PARAM_INVALID.getMessage());
            }
            return Result.ok(orderService.updateOrderStatus(id, orderStatus, imgs));
        } catch (BizException e) {
            return Result.error(e.getErrCode(), e.getMessage());
        } catch (Exception e) {
            log.error("修改订单状态失败", e);
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("老板确认接单")
    @PostMapping(value = "/confirm")
    public Result<Object> confirm(@RequestBody JSONObject jsonObject) {
        try {
            String id = jsonObject.getString("id");
            if (oConvertUtils.isEmpty(id)) {
                return Result.error(BizErrorCodes.PARAM_INVALID.getCode(), BizErrorCodes.PARAM_INVALID.getMessage());
            }
            return Result.ok(orderService.updateOrderStatus(id, BizConstants.ORDER_STATUS_WAIT_START, null));
        } catch (BizException e) {
            return Result.error(e.getErrCode(), e.getMessage());
        } catch (Exception e) {
            log.error("确认接单失败", e);
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("取消订单")
    @PostMapping(value = "/cancel")
    public Result<Object> cancel(@RequestBody JSONObject jsonObject) {
        try {
            String id = jsonObject.getString("id");
            if (oConvertUtils.isEmpty(id)) {
                return Result.error(BizErrorCodes.PARAM_INVALID.getCode(), BizErrorCodes.PARAM_INVALID.getMessage());
            }
            return Result.ok(orderService.updateOrderStatus(id, BizConstants.ORDER_STATUS_CANCEL, null));
        } catch (BizException e) {
            return Result.error(e.getErrCode(), e.getMessage());
        } catch (Exception e) {
            log.error("取消订单失败", e);
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("上下班打卡")
    @PostMapping(value = "/workClock")
    public Result<Object> workClock(@RequestBody JobOrderClock clock) {
        try {
            if (oConvertUtils.isEmpty(clock.getOrderId()) || oConvertUtils.isEmpty(clock.getAddress())
                    || oConvertUtils.isEmpty(clock.getClockType())) {
                return Result.error(BizErrorCodes.PARAM_INVALID.getCode(), BizErrorCodes.PARAM_INVALID.getMessage());
            }
            return Result.ok(orderClockService.addOrderClock(clock));
        } catch (BizException e) {
            return Result.error(e.getErrCode(), e.getMessage());
        } catch (Exception e) {
            log.error("上下班打卡失败", e);
            return Result.error(e.getMessage());
        }
    }

    @RequestMapping(value = "/getOrderStatistics", method = RequestMethod.GET)
    @ApiOperation(value = "订单统计")
    public Result<Object> getOrderStatistics(@RequestParam(name = "postId") String postId) {
        return Result.OK(orderService.getOrderStatistics(postId));
    }
}
