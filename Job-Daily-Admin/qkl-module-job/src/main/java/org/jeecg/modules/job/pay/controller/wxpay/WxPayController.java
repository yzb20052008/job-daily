package org.jeecg.modules.job.pay.controller.wxpay;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.ijpay.core.enums.SignType;
import com.ijpay.core.enums.TradeType;
import com.ijpay.core.kit.*;
import com.ijpay.wxpay.WxPayApi;
import com.ijpay.wxpay.WxPayApiConfig;
import com.ijpay.wxpay.WxPayApiConfigKit;
import com.ijpay.wxpay.model.*;
import com.ijpay.core.kit.HttpKit;
import com.ijpay.core.kit.IpKit;
import com.ijpay.core.kit.WxPayKit;
import com.jfinal.kit.StrKit;

import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.job.constant.BizConstants;
import org.jeecg.modules.job.exception.BizException;
import org.jeecg.modules.job.integral.service.IIntegralRechargeService;
import org.jeecg.modules.job.job.entity.JobOrder;
import org.jeecg.modules.job.job.service.IJobOrderService;
import org.jeecg.modules.job.pay.entity.H5SceneInfo;
import org.jeecg.modules.job.pay.entity.WxPayBean;
import org.jeecg.modules.job.pay.vo.AjaxResult;
import org.jeecg.modules.job.support.IdempotentHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * <p>IJPay 让支付触手可及，封装了微信支付、支付宝支付、银联支付常用的支付方式以及各种常用的接口。</p>
 *
 * <p>不依赖任何第三方 mvc 框架，仅仅作为工具使用简单快速完成支付模块的开发，可轻松嵌入到任何系统里。 </p>
 *
 * <p>IJPay 交流群: 723992875、864988890</p>
 *
 * <p>Node.js 版: <a href="https://gitee.com/javen205/TNWX">https://gitee.com/javen205/TNWX</a></p>
 *
 * <p>微信支付 Demo</p>
 *
 * @author Javen
 */
@Controller
@RequestMapping("/wxPay")
public class WxPayController extends AbstractWxPayApiController {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	WxPayBean wxPayBean;
	@Resource
	private IIntegralRechargeService integralRechargeService;
	@Resource
	private IJobOrderService jobOrderService;
	@Resource
	private org.jeecg.modules.job.ums.service.IUmsBalanceRechargeService balanceRechargeService;
	@Resource
	private IdempotentHelper idempotentHelper;

	private String notifyUrl;
	private String refundNotifyUrl;
	private static final String USER_PAYING = "USERPAYING";

	//积分充值
	public static final String ATTACH_INTEGRAL="attach_integral";
	//余额充值
	public static final String ATTACH_BALANCE="attach_balance";
	//工资结算
	public static final String ATTACH_SALARY="attach_salary";
	//转账回调通知
	public static final String ATTACH_WITHDRAW="attach_withdraw";


	@Override
	public WxPayApiConfig getApiConfig() {
		WxPayApiConfig apiConfig;

		try {
			apiConfig = WxPayApiConfigKit.getApiConfig(wxPayBean.getAppId());
		} catch (Exception e) {
			apiConfig = WxPayApiConfig.builder()
					.appId(wxPayBean.getAppId())
					.mchId(wxPayBean.getMchId())
					.partnerKey(wxPayBean.getPartnerKey())
					.certPath(wxPayBean.getCertPath())
					.domain(wxPayBean.getDomain())
					.build();
		}
		notifyUrl = apiConfig.getDomain().concat("/wxPay/payNotify");
		refundNotifyUrl = apiConfig.getDomain().concat("/wxPay/refundNotify");
		return apiConfig;
	}

	/**
	 * 微信小程序支付-积分充值
	 * @param money 充值金额
	 */
	@RequestMapping(value = "/miniPayIntegral", method = {RequestMethod.GET})
	@ResponseBody
	public Result<?> miniAppPay(String money, HttpServletRequest request) {
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		String ip = IpKit.getRealIp(request);
		if (StrKit.isBlank(ip)) {
			ip = "127.0.0.1";
		}
		WxPayApiConfigKit.putApiConfig(this.getApiConfig());
		WxPayApiConfig wxPayApiConfig = WxPayApiConfigKit.getWxPayApiConfig();
//		String orderSn= StringUtils.getOutTradeNo();
		String orderSn= DateUtils.formatDate(new Date(),"yyyyMMddHHmmss")+ RandomUtil.randomNumbers(5);
		Map<String, String> params = UnifiedOrderModel
				.builder()
				.appid(wxPayApiConfig.getAppId())
				.mch_id(wxPayApiConfig.getMchId())
				.nonce_str(WxPayKit.generateStr())
				.body("积分充值")
				.attach(ATTACH_INTEGRAL)
				.out_trade_no(orderSn)
				.total_fee(new BigDecimal(money).multiply(new BigDecimal(100)).intValue() + "")
//				.total_fee(new BigDecimal(0.01).multiply(new BigDecimal(100)).intValue() + "")
				.spbill_create_ip(ip)
				.notify_url(notifyUrl)
				.trade_type(TradeType.JSAPI.getTradeType())
				.openid(user.getThirdId())
				.build()
				.createSign(wxPayApiConfig.getPartnerKey(), SignType.HMACSHA256);

		String xmlResult = WxPayApi.pushOrder(false, params);
		System.err.println(notifyUrl);
		log.info(xmlResult);
		Map<String, String> result = WxPayKit.xmlToMap(xmlResult);

		String returnCode = result.get("return_code");
		String returnMsg = result.get("return_msg");
		if (!WxPayKit.codeIsOk(returnCode)) {
			return Result.error(returnMsg);
		}
		String resultCode = result.get("result_code");
		if (!WxPayKit.codeIsOk(resultCode)) {
			return Result.error(returnMsg);
		}
		// 以下字段在 return_code 和 result_code 都为 SUCCESS 的时候有返回
		String prepayId = result.get("prepay_id");
		Map<String, String> packageParams = WxPayKit.miniAppPrepayIdCreateSign(wxPayApiConfig.getAppId(), prepayId,
				wxPayApiConfig.getPartnerKey(), SignType.HMACSHA256);
		String jsonStr = JSON.toJSONString(packageParams);
		log.info("小程序支付的参数:" + jsonStr);
		//添加订单记录
		integralRechargeService.createRechargeOrder(orderSn,user.getId(),money,BizConstants.PAY_TYPE_WX);
		return Result.ok(jsonStr);
	}

	/**
	 * 微信小程序支付-余额充值
	 * @param money 充值金额
	 */
	@RequestMapping(value = "/miniPayBalance", method = {RequestMethod.GET})
	@ResponseBody
	public Result<?> miniPayBalance(String money, HttpServletRequest request) {
		if (oConvertUtils.isEmpty(money)) {
			return Result.error("参数错误");
		}
		BigDecimal amount;
		try {
			amount = new BigDecimal(money).setScale(2, java.math.RoundingMode.HALF_UP);
		} catch (Exception e) {
			return Result.error("金额格式错误");
		}
		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			return Result.error("充值金额必须大于0");
		}
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		if (user == null) {
			return Result.error("请先登录");
		}
		String ip = IpKit.getRealIp(request);
		if (StrKit.isBlank(ip)) {
			ip = "127.0.0.1";
		}
		WxPayApiConfigKit.putApiConfig(this.getApiConfig());
		WxPayApiConfig wxPayApiConfig = WxPayApiConfigKit.getWxPayApiConfig();
		String orderSn= DateUtils.formatDate(new Date(),"yyyyMMddHHmmss")+ RandomUtil.randomNumbers(5);
		Map<String, String> params = UnifiedOrderModel
				.builder()
				.appid(wxPayApiConfig.getAppId())
				.mch_id(wxPayApiConfig.getMchId())
				.nonce_str(WxPayKit.generateStr())
				.body("余额充值")
				.attach(ATTACH_BALANCE)
				.out_trade_no(orderSn)
				.total_fee(amount.multiply(new BigDecimal(100)).intValue() + "")
				.spbill_create_ip(ip)
				.notify_url(notifyUrl)
				.trade_type(TradeType.JSAPI.getTradeType())
				.openid(user.getThirdId())
				.build()
				.createSign(wxPayApiConfig.getPartnerKey(), SignType.HMACSHA256);

		String xmlResult = WxPayApi.pushOrder(false, params);
		log.info(xmlResult);
		Map<String, String> result = WxPayKit.xmlToMap(xmlResult);

		String returnCode = result.get("return_code");
		String returnMsg = result.get("return_msg");
		if (!WxPayKit.codeIsOk(returnCode)) {
			return Result.error(returnMsg);
		}
		String resultCode = result.get("result_code");
		if (!WxPayKit.codeIsOk(resultCode)) {
			return Result.error(returnMsg);
		}
		String prepayId = result.get("prepay_id");
		Map<String, String> packageParams = WxPayKit.miniAppPrepayIdCreateSign(wxPayApiConfig.getAppId(), prepayId,
				wxPayApiConfig.getPartnerKey(), SignType.HMACSHA256);
		String jsonStr = JSON.toJSONString(packageParams);
		log.info("小程序支付的参数:" + jsonStr);
		// 落余额充值单（禁止误写积分充值表）
		balanceRechargeService.createRechargeOrder(orderSn, user.getId(), amount, BizConstants.PAY_TYPE_WX);
		return Result.ok(jsonStr);
	}

	/**
	 * 微信小程序支付-工资支付
	 * @param amount 支付金额
	 */
	@RequestMapping(value = "/miniPaySalary", method = {RequestMethod.GET})
	@ResponseBody
	public Result<?> miniPaySalary(String orderId,String amount, HttpServletRequest request) {
		try {
		if (oConvertUtils.isEmpty(orderId) || oConvertUtils.isEmpty(amount)){
			return Result.error("参数错误");
		}
		BigDecimal clientMoney;
		try {
			clientMoney = new BigDecimal(amount).setScale(2, java.math.RoundingMode.HALF_UP);
		} catch (Exception e) {
			return Result.error("金额格式错误");
		}
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		if (user == null) {
			return Result.error("请先登录");
		}
		JobOrder order = jobOrderService.getById(orderId);
		if (order == null) {
			return Result.error("订单不存在");
		}
		if (!user.getId().equals(order.getPostUserId())) {
			return Result.error("仅老板可结算工资");
		}
		// 防连点重复预下单
		idempotentHelper.assertPaySalaryOnce(orderId);
		BigDecimal money;
		try {
			money = jobOrderService.resolvePaySalaryAmount(orderId, clientMoney);
		} catch (Exception e) {
			return Result.error(e.getMessage());
		}
		String ip = IpKit.getRealIp(request);
		if (StrKit.isBlank(ip)) {
			ip = "127.0.0.1";
		}
		WxPayApiConfigKit.putApiConfig(this.getApiConfig());
		WxPayApiConfig wxPayApiConfig = WxPayApiConfigKit.getWxPayApiConfig();
		String orderSn= DateUtils.formatDate(new Date(),"yyyyMMddHHmmss")+ RandomUtil.randomNumbers(5);
		Map<String, String> params = UnifiedOrderModel
				.builder()
				.appid(wxPayApiConfig.getAppId())
				.mch_id(wxPayApiConfig.getMchId())
				.nonce_str(WxPayKit.generateStr())
				.body("支付员工工资")
				.attach(ATTACH_SALARY)
				.out_trade_no(orderSn)
				.total_fee(money.multiply(new BigDecimal(100)).intValue() + "")
				.spbill_create_ip(ip)
				.notify_url(notifyUrl)
				.trade_type(TradeType.JSAPI.getTradeType())
				.openid(user.getThirdId())
				.build()
				.createSign(wxPayApiConfig.getPartnerKey(), SignType.HMACSHA256);
		String xmlResult = WxPayApi.pushOrder(false, params);
		log.info(xmlResult);
		Map<String, String> result = WxPayKit.xmlToMap(xmlResult);

		String returnCode = result.get("return_code");
		String returnMsg = result.get("return_msg");
		if (!WxPayKit.codeIsOk(returnCode)) {
			return Result.error(returnMsg);
		}
		String resultCode = result.get("result_code");
		if (!WxPayKit.codeIsOk(resultCode)) {
			return Result.error(returnMsg);
		}
		String prepayId = result.get("prepay_id");
		Map<String, String> packageParams = WxPayKit.miniAppPrepayIdCreateSign(wxPayApiConfig.getAppId(), prepayId,
				wxPayApiConfig.getPartnerKey(), SignType.HMACSHA256);
		String jsonStr = JSON.toJSONString(packageParams);
		log.info("小程序支付的参数:" + jsonStr);
		jobOrderService.updatePayMoney(orderId,orderSn,money.toPlainString(), BizConstants.PAY_TYPE_WX);
		return Result.ok(jsonStr);
		} catch (BizException e) {
			return Result.error(e.getErrCode(), e.getMessage());
		} catch (Exception e) {
			log.error("微信结算下单失败 orderId={}", orderId, e);
			return Result.error(e.getMessage());
		}
	}

	/**
	 * 异步通知
	 */
	@RequestMapping(value = "/payNotify", method = {RequestMethod.POST, RequestMethod.GET})
	@ResponseBody
	public String payNotify(HttpServletRequest request) {
		String xmlMsg = HttpKit.readData(request);
		log.info("支付通知=" + xmlMsg);
		Map<String, String> params = WxPayKit.xmlToMap(xmlMsg);
		String returnCode = params.get("return_code");
		String attach = params.get("attach");
		// 注意重复通知的情况，同一订单号可能收到多次通知，请注意一定先判断订单状态
		// 注意此处签名方式需与统一下单的签名类型一致
		WxPayApiConfigKit.putApiConfig(this.getApiConfig());
		if (WxPayKit.verifyNotify(params, WxPayApiConfigKit.getWxPayApiConfig().getPartnerKey(), SignType.HMACSHA256)) {
			if (WxPayKit.codeIsOk(returnCode)) {
				String out_trade_no = params.get("out_trade_no");
				if (ATTACH_INTEGRAL.equals(attach)){
					//积分充值
					integralRechargeService.updateRechargeOrder(out_trade_no);
				}else if (ATTACH_BALANCE.equals(attach)){
					//余额充值：校验渠道金额
					String totalFee = params.get("total_fee");
					java.math.BigDecimal paidAmount = null;
					if (oConvertUtils.isNotEmpty(totalFee)) {
						paidAmount = new java.math.BigDecimal(totalFee).movePointLeft(2);
					}
					boolean ok = balanceRechargeService.paySuccess(out_trade_no, paidAmount);
					if (!ok) {
						log.error("余额充值回调处理失败 out_trade_no={}, total_fee={}", out_trade_no, totalFee);
						return null;
					}
				}else if (ATTACH_SALARY.equals(attach)){
					//工资支付：校验渠道金额（分→元）
					String totalFee = params.get("total_fee");
					java.math.BigDecimal paidAmount = null;
					if (oConvertUtils.isNotEmpty(totalFee)) {
						paidAmount = new java.math.BigDecimal(totalFee).movePointLeft(2);
					}
					boolean handled = jobOrderService.paySalarySuccess(out_trade_no, paidAmount);
					if (!handled) {
						log.error("工资支付回调处理失败 out_trade_no={}, total_fee={}", out_trade_no, totalFee);
						return null;
					}
				}
				// 发送通知等
				Map<String, String> xml = new HashMap<String, String>(2);
				xml.put("return_code", "SUCCESS");
				xml.put("return_msg", "OK");
				return WxPayKit.toXml(xml);
			}
		}
		return null;
	}
}
