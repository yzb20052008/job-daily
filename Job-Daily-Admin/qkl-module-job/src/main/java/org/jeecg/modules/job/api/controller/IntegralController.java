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
import org.jeecg.modules.job.integral.entity.IntegralGoods;
import org.jeecg.modules.job.integral.entity.IntegralLog;
import org.jeecg.modules.job.integral.service.IIntegralGoodsOrderService;
import org.jeecg.modules.job.integral.service.IIntegralGoodsService;
import org.jeecg.modules.job.integral.service.IIntegralLogService;
import org.jeecg.modules.job.integral.service.IIntegralRechargeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 */
@RestController
@RequestMapping("/api/integral")
@Api(tags="移动端积分管理模块")
@Slf4j
public class IntegralController {
	@Autowired
	private IIntegralLogService integralLogService;
//	@Autowired
//	private IUmsIntegralTransferService transferService;
//	@Autowired
//	private IUmsIntegralExchangeService exchangeService;
	@Resource
	private IIntegralGoodsService jfGoodsService;
	@Resource
	private IIntegralGoodsOrderService jfOrderService;
	@Resource
	private IIntegralRechargeService rechargeService;

//	/**
//	 * 卡券兑换积分
//	 *
//	 * @param exchange
//	 * @return
//	 */
//	@ApiOperation("卡券兑换积分")
//	@PostMapping(value = "/integralExchange")
//	public Result<Object> integralExchange(@RequestBody UmsIntegralExchange exchange) {
//		try {
//			boolean result=exchangeService.integralExchange(exchange);
//			return Result.ok(result);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return Result.error(e.getMessage());
//		}
//	}
//
//	/**
//	 * 积分转赠
//	 *
//	 * @param transfer
//	 * @return
//	 */
//	@ApiOperation("积分转赠")
//	@PostMapping(value = "/integralTransfer")
//	public Result<Object> integralTransfer(@RequestBody UmsIntegralTransfer transfer) {
//		try {
//			boolean result=transferService.integralTransfer(transfer);
//			return Result.ok(result);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return Result.error(e.getMessage());
//		}
//	}


	/**
	 * 分页查询积分明细
	 *
	 * @param log
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@ApiOperation(value="分页查询积分明细", notes="分页查询积分明细")
	@GetMapping(value = "/getIntegralLogList")
	public Result<IPage<IntegralLog>> getIntegralLogList(IntegralLog log,
														 @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
														 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		log.setUserId(user.getId());
		Page<IntegralLog> page = new Page<>(pageNo, pageSize);
		IPage<IntegralLog> pageList = integralLogService.page(page, new QueryWrapper<>(log).orderByDesc("create_time"));
		return Result.OK(pageList);
	}


	/**
	 * 查询积分道具列表
	 *
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@ApiOperation(value="查询积分道具列表", notes="查询积分道具列表")
	@GetMapping(value = "/getJfGoodsList")
	public Result<IPage<IntegralGoods>> getJfGoodsList(@RequestParam(name="roleCode") String roleCode,
													   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
													   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		Page<IntegralGoods> page = new Page<>(pageNo, pageSize);
		IPage<IntegralGoods> pageList = jfGoodsService.page(page, new QueryWrapper<IntegralGoods>().eq("status",1).apply("(role_code = '"+roleCode+"' or role_code is null)").orderByAsc("sort"));
		return Result.OK(pageList);
	}


	/**
	 * 兑换道具
	 *
	 * @param jsonObject
	 * @return
	 */
	@ApiOperation("兑换道具")
	@PostMapping(value = "/createJfOrder")
	public Result<Object> createJfOrder(@RequestBody JSONObject jsonObject) {
		try {
			String amount = jsonObject.getString("amount");
			String goodsId = jsonObject.getString("goodsId");
			String dataId = jsonObject.getString("dataId");
			if (oConvertUtils.isEmpty(amount) || oConvertUtils.isEmpty(goodsId)){
				return Result.error("参数错误");
			}
			LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
			boolean result=jfOrderService.createJfOrder(user.getId(),"1",goodsId,amount,dataId);
			return Result.ok(result);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error(e.getMessage());
		}
	}

	/**
	 * 余额充值积分
	 *
	 * @param jsonObject
	 * @return
	 */
	@ApiOperation("余额充值积分")
	@PostMapping(value = "/yePayIntegral")
	public Result<Object> yePayIntegral(@RequestBody JSONObject jsonObject) {
		try {
			String money = jsonObject.getString("money");//充值金额
			if (oConvertUtils.isEmpty(money)){
				return Result.error("参数错误");
			}
			LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
			boolean result=rechargeService.yePayIntegral(user.getId(),new BigDecimal(money));
			return Result.ok(result);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error(e.getMessage());
		}
	}


}