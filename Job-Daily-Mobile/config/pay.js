import store from '@/store';
import { isWechat } from '@/config/h5Utils';

// 支付(app、小程序)
function setPay(payInfo, callback){
	let userInfo = store.state.userInfo
	let httpData = {
	    userId: userInfo.userId || '',
	    sessionId: userInfo.sessionId || '',
	    openid: userInfo.openId,
        taskId: payInfo.tradeNo, // 创建支付订单时返回的 订单号
	}
    var url = '/wxPay/taskMiniAppPay' // 您的支付接口
    uni.$u.http.get(url, {params:httpData}).then((data) => {
		console.log("===data====",data)
		let payData = {
			success: function(res) {
				console.log("===res====",res)
                var flag = true
                // #ifdef MP-ALIPAY
                if(res.resultCode == 9000){
                    flag = true
                }else{
                    flag = false
                }
                // #endif
				console.log('success:' + JSON.stringify(res));
                callback && callback({
                	success: flag,
                	data: res
                });
			},
			fail: function(err) {
				console.log('fail:' + JSON.stringify(err));
				callback && callback({
					success: false,
					data: err
				});
			}
		};
		data = JSON.parse(data);
		if (payInfo.type == 'mpwxpay') {
			// 微信小程序
			payData.provider = 'wxpay';
			payData.timeStamp = data.timeStamp;
			payData.nonceStr = data.nonceStr;
			payData.package = data.package;
			payData.signType = data.signType;
			payData.paySign = data.paySign;
		} else if (payInfo.type == 'wxpay') {
			// app微信
			payData.provider = 'wxpay';
			payData.orderInfo = data.tradeNo;
		} else if (payInfo.type == 'alipay') {
			payData.provider = 'alipay';
			payData.orderInfo = data.sdk_transaction_id;
		} else if (payInfo.type == 'baidu') {
			payData.provider = 'baidu';
			payData.orderInfo = data.tradeNo;
		} else if (payInfo.type == 'toutiao') {
			payData.provider = 'toutiao';
            payData.service = 5; // 5：拉起小程序收银台   其他：不拉起字节跳动小程序收银台
            payData._debug = 1;
            // payData.payChannel = {
            //     default_pay_channel: 'wx' //  wx|| alipay
            // };
			payData.orderInfo = {
                order_id: data.order_id,
                order_token: data.order_token
            };
		}
		console.log("支付参数", payData);
		uni.requestPayment(payData);
	}, err => {
		callback && callback({
			success: false,
			data: err
		});
	});
}
//公众号微信支付
function wxPublicPay(payInfo, callback){
    if(!isWechat()) {
        uni.showToast({
        	title: "需微信公众号才能使用~",
        	icon: "none"
        });
        return
    }
    var url = '/api/PayApi' // 您的支付接口
    uni.$u.http.post(url, {
		tradeNo: payInfo.tradeNo //订单号
	}).then(data => {
		let wxConfigObj = {
			appId: data.appId,
			timeStamp: data.timeStamp,
			nonceStr: data.nonceStr,
			package: data.package,
			signType: data.signType,
			paySign: data.sign
		};
		function onBridgeReady() {
			window.WeixinJSBridge.invoke("getBrandWCPayRequest", wxConfigObj, function(
				res
			) {
				if (res.err_msg == "get_brand_wcpay_request:ok") {
					callback && callback({
                        success: true,
                        data: res
                    });
				} else // 使用以上方式判断前端返回,微信团队郑重提示：res.err_msg将在用户支付成功后返回    ok，但并不保证它绝对可靠。
					if (res.err_msg == "get_brand_wcpay_request:cancel") {
						// 您取消了支付
                        callback && callback({
                            success: false,
                            data: res
                        });
					} else
				if (res.err_msg == "get_brand_wcpay_request:fail") {
                    // 支付失败
					callback && callback({
					    success: false,
					    data: res
					});
				}
			});
		}
		if (typeof window.WeixinJSBridge == "undefined") {
			if (document.addEventListener) {
				document.addEventListener("WeixinJSBridgeReady", onBridgeReady, false);
			} else if (document.attachEvent) {
				document.attachEvent("WeixinJSBridgeReady", onBridgeReady);
				document.attachEvent("onWeixinJSBridgeReady", onBridgeReady);
			}
		} else {
			onBridgeReady();
		}
	});
};

export {
    setPay,           //支付(app、小程序)
    wxPublicPay,      //公众号微信支付
}