package org.jeecg.modules.job.pay.controller.haoda;

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

import org.jeecg.modules.job.pay.controller.alipay.AbstractAliPayApiController;
import org.jeecg.modules.job.pay.entity.H5SceneInfo;
import org.jeecg.modules.job.pay.entity.WxPayBean;
import org.jeecg.modules.job.pay.haoda.Client;
import org.jeecg.modules.job.pay.haoda.Config;
import org.jeecg.modules.job.pay.haoda.RequestException;
import org.jeecg.modules.job.pay.vo.AjaxResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * <p>好哒聚合支付 https://open.iboxpay.com/</p>
 *
 * @author qingkonglan
 */
@Controller
@RequestMapping("/haoda")
public class HaoDaPayController {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private String notifyUrl;
	private static Client client;

	static{
		final Config config = new Config();
		config.setAppId("");
		config.setAppSecret("");
		config.setSignAlgorithm("MD5");
		client = new Client(config);
	}

	/**
	 * 微信小程序  积分充值
	 */
	@RequestMapping(value = "/rechargeIntegral", method = {RequestMethod.POST, RequestMethod.GET})
	public void wapPay(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 准备请求体内容
		Map<String, Object> reqBodyObj = new HashMap<>();
		reqBodyObj.put("mchtNo", "180095");
		reqBodyObj.put("storeNo", "20180087");
		reqBodyObj.put("subAppIdType", "SUBSCRIPTION");
		reqBodyObj.put("onlineType", "1");
		reqBodyObj.put("ledgerModel", "1");
		try {
			final String resBody = this.client.execute("/hzg/v2/unitedtrade/wechat_js_pay", reqBodyObj, String.class);
			System.err.println("resBody==="+resBody);
		} catch (RequestException e) {
			throw new RuntimeException(String.format("请求错误！HTTP 状态码: %d，服务端响应的内容: %s", e.getStatusCode(), e.getBody()), e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
