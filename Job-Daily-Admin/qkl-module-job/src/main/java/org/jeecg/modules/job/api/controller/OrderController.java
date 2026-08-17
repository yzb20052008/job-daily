package org.jeecg.modules.job.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import org.jeecg.modules.job.job.entity.JobOrder;
import org.jeecg.modules.job.job.entity.JobOrderClock;
import org.jeecg.modules.job.job.service.IJobOrderClockService;
import org.jeecg.modules.job.job.service.IJobOrderService;
import org.jeecg.modules.job.job.vo.JobPostVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 */
@RestController
@RequestMapping("/api/order")
@Api(tags="移动端订单管理模块")
@Slf4j
public class OrderController {
	@Autowired
	private IJobOrderService orderService;
	@Resource
	private IJobOrderClockService orderClockService;

	/**
	 * 查询订单列表
	 */
	@RequestMapping(value = "/getOrderList",method = RequestMethod.GET)
	@ApiOperation(value = "查询订单列表", notes = "查询订单列表")
	public Result<Object> getOrderList(JobOrder param,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize
	) {
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		param.setUserId(user.getId());
		IPage<Map<String,Object>> pageInfo= orderService.getOrderList(new Page<>(pageNo,pageSize),param);
		return Result.OK(pageInfo);
	}

	/**
	 * 查询订单详情
	 */
	@ResponseBody
	@RequestMapping(value = "/getOrderDetail", method = RequestMethod.GET)
	@ApiOperation(value = "查询订单详情", notes = "查询订单详情")
	public Result<Object> getOrderDetail(@RequestParam("id") String id) {
		try {
			JobOrderVo order=orderService.getOrderDetail(id);
			return Result.OK(order);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error(e.getMessage());
		}
	}

	/**
	 * 查询员工列表
	 */
	@RequestMapping(value = "/getPostUserList",method = RequestMethod.GET)
	@ApiOperation(value = "查询员工列表", notes = "查询员工列表")
	public Result<Object> getPostUserList(JobOrder param,
									   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize
	) {
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		param.setUserId(user.getId());
		IPage<Map<String,Object>> pageInfo= orderService.getPostUserList(new Page<>(pageNo,pageSize),param);
		return Result.OK(pageInfo);
	}


	@ApiOperation("提交报名申请")
	@PostMapping(value = "/addApply")
	public Result<Object> addApply(@RequestBody JSONObject jsonObject) {
		try {
			String id = jsonObject.getString("id");
			Integer integral = jsonObject.getInteger("integral");
			if (oConvertUtils.isEmpty(id) || oConvertUtils.isEmpty(integral)){
				return Result.error("参数错误");
			}
			LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
			boolean result=orderService.doApply(user.getId(),id,integral);
			return Result.ok(result);
		} catch (Exception e) {
			log.error("提交报名申请：%s", e.getMessage(), e);
			return Result.error("操作失败："+e.getMessage());
		}
	}


	@ApiOperation("修改订单状态（服务端校验状态机与权限）")
	@PostMapping(value = "/updateOrderStatus")
	public Result<Object> updateOrderStatus(@RequestBody JSONObject jsonObject) {
		try {
			String id = jsonObject.getString("id");
			String orderStatus = jsonObject.getString("orderStatus");
			String imgs = jsonObject.getString("imgs");
			if (oConvertUtils.isEmpty(id) || oConvertUtils.isEmpty(orderStatus)){
				return Result.error("参数错误");
			}
			boolean result=orderService.updateOrderStatus(id,orderStatus,imgs);
			return Result.ok(result);
		} catch (Exception e) {
			log.error("修改订单状态失败：{}", e.getMessage(), e);
			return Result.error(e.getMessage());
		}
	}

	@ApiOperation("老板确认接单")
	@PostMapping(value = "/confirm")
	public Result<Object> confirm(@RequestBody JSONObject jsonObject) {
		try {
			String id = jsonObject.getString("id");
			if (oConvertUtils.isEmpty(id)) {
				return Result.error("参数错误");
			}
			boolean result = orderService.updateOrderStatus(id, BizConstants.ORDER_STATUS_WAIT_START, null);
			return Result.ok(result);
		} catch (Exception e) {
			log.error("确认接单失败：{}", e.getMessage(), e);
			return Result.error(e.getMessage());
		}
	}

	@ApiOperation("取消订单")
	@PostMapping(value = "/cancel")
	public Result<Object> cancel(@RequestBody JSONObject jsonObject) {
		try {
			String id = jsonObject.getString("id");
			if (oConvertUtils.isEmpty(id)) {
				return Result.error("参数错误");
			}
			boolean result = orderService.updateOrderStatus(id, BizConstants.ORDER_STATUS_CANCEL, null);
			return Result.ok(result);
		} catch (Exception e) {
			log.error("取消订单失败：{}", e.getMessage(), e);
			return Result.error(e.getMessage());
		}
	}


	@ApiOperation("上下班打卡")
	@PostMapping(value = "/workClock")
	public Result<Object> workClock(@RequestBody JobOrderClock clock) {
		try {
			if (oConvertUtils.isEmpty(clock.getOrderId()) || oConvertUtils.isEmpty(clock.getAddress())|| oConvertUtils.isEmpty(clock.getClockType())){
				return Result.error("参数错误");
			}
			boolean result=orderClockService.addOrderClock(clock);
			return Result.ok(result);
		} catch (Exception e) {
			log.error("修改招工状态：%s", e.getMessage(), e);
			return Result.error("操作失败："+e.getMessage());
		}
	}



	/**
	 * 查询指定招工下的订单统计数量
	 */
	@RequestMapping(value = "/getOrderStatistics",method = RequestMethod.GET)
	@ApiOperation(value = "查询员工列表", notes = "查询员工列表")
	public Result<Object> getOrderStatistics(@RequestParam(name="postId") String postId){
		Map<String,Object> map=orderService.getOrderStatistics(postId);
		return Result.OK(map);
	}
}
