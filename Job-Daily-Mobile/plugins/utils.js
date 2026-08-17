import Vue from 'vue';
import base from "@/config/baseUrl";
// #ifdef APP-PLUS
import {
	judgePermission
} from './permission'
// #endif
//金额过滤
Vue.filter('money', function(val) {
	if (val) {
		let value = Math.round(parseFloat(val) * 100) / 100;
		let valMoney = value.toString().split(".");
		if (valMoney.length == 1) {
			value = value.toString() + ".00";
			return value;
		}
		if (valMoney.length > 1) {
			if (valMoney[1].length < 2) {
				value = value.toString() + "0";
			}
			return value;
		}
		return value;
	} else {
		return "0.00";
	}
});
//手机号中间4位为*
Vue.filter('phone', function(val) {
	var tel = val;
	tel = "" + tel;
	var telShort = tel.replace(tel.substring(3, 7), "****")
	return telShort
})
//获取系统信息、判断ipX安全距离
export const getTabbarHeight = function() {
	var systemInfo = uni.getSystemInfoSync()
	var data = {
		...systemInfo,
		tabbarH: 50, //tabbar高度--单位px
		tabbarPaddingB: 0, //tabbar底部安全距离高度--单位px
		device: systemInfo.system.indexOf('iOS') != -1 ? 'iOS' : 'Android', //苹果或者安卓设备
	}
	let modelArr = ['10,3', '10,6', 'X', 'XR', 'XS', '11', '12', '13', '14', '15', '16'];
	let model = systemInfo.model;
	model && modelArr.forEach(item => {
		//适配iphoneX以上的底部，给tabbar一定高度的padding-bottom
		if (model.indexOf(item) != -1 && (model.indexOf('iPhone') != -1 || model.indexOf('iphone') != -1)) {
			data.tabbarH = 70
			data.tabbarPaddingB = 20
		}
	})
	return data;
}

//计算两点距离
export const commonDistance = function(lat1, lng1, lat2, lng2) {
	console.log("commonDistance===", lat1, lng1, lat2, lng2)
	var f = ((lat1 + lat2) / 2) * Math.PI / 180.0;
	var g = ((lat1 - lat2) / 2) * Math.PI / 180.0;
	var l = ((lng1 - lng2) / 2) * Math.PI / 180.0;
	var sg = Math.sin(g);
	var sl = Math.sin(l);
	var sf = Math.sin(f);
	var s, c, w, r, d, h1, h2;
	var a = 6378137.0; //地球的直径
	var fl = 1 / 298.257;
	sg = sg * sg;
	sl = sl * sl;
	sf = sf * sf;
	s = sg * (1 - sl) + (1 - sf) * sl;
	c = (1 - sg) * (1 - sl) + sf * sl;
	w = Math.atan(Math.sqrt(s / c));
	r = Math.sqrt(s * c) / w;
	d = 2 * w * a;
	h1 = (3 * r - 1) / 2 / c;
	h2 = (3 * r + 1) / 2 / s;
	var num = d * (1 + fl * (h1 * sf * (1 - sg) - h2 * (1 - sf) * sg))
	// 换算单位
	if (num == undefined) {
		num = "0.0 m"
	};
	if (num < 1000) {
		num = (Math.round(num)).toFixed(1) + "m"
	} else if (num > 1000) {
		num = (Math.round(num / 100) / 10).toFixed(1) + "km"
	}
	return num
}
// px转upx
export const px2upx = function(n) {
	return n / (uni.upx2px(n) / n);
}

// 判断两时间段之间活动状态、判断活动还有多长时间开始、多长时间结束----添加定时器运行此方法可倒计时
// var startTime = new Date(item.startTime.replace(/-/g, '/')).getTime(); //转时间戳
// var closeTime = new Date(item.closeTime.replace(/-/g, '/')).getTime(); //转时间戳
// var djs = this.djsTime(startTime, closeTime);
export const djsTime = function(startTime, endTime) {
	var bbb = new Date().getTime(),
		leftTime = startTime - bbb,
		rightTime = endTime - bbb,
		djsTime = '',
		speed = 0,
		activityStatus = 0, //活动状态 1：未开始 2：进行中 3：已结束
		dd, hh, mm, ss;
	if (leftTime > 0) { //还未开始
		activityStatus = 1
		dd = Math.floor(leftTime / 1000 / 60 / 60 / 24);
		hh = Math.floor((leftTime / 1000 / 60 / 60) % 24) < 10 ? '0' + Math.floor((leftTime / 1000 / 60 / 60) %
			24) : Math.floor((leftTime / 1000 / 60 / 60) % 24);
		mm = Math.floor((leftTime / 1000 / 60) % 60) < 10 ? '0' + Math.floor((leftTime / 1000 / 60) % 60) : Math
			.floor((leftTime / 1000 / 60) % 60);
		ss = Math.floor((leftTime / 1000) % 60) < 10 ? '0' + Math.floor((leftTime / 1000) % 60) : Math.floor((
			leftTime / 1000) % 60);
		if (dd != 0) {
			djsTime = dd + "天 " + hh + ":" + mm + ":" + ss;
		} else {
			djsTime = hh + ":" + mm + ":" + ss;
		}
	} else if (leftTime <= 0) {
		if (rightTime > 0) { //进行中
			activityStatus = 2;
			speed = (1 - rightTime / (endTime - startTime)) * 100;
			dd = Math.floor(rightTime / 1000 / 60 / 60 / 24);
			hh = Math.floor((rightTime / 1000 / 60 / 60) % 24) < 10 ? '0' + Math.floor((rightTime / 1000 / 60 /
				60) % 24) : Math.floor((rightTime / 1000 / 60 / 60) % 24);
			mm = Math.floor((rightTime / 1000 / 60) % 60) < 10 ? '0' + Math.floor((rightTime / 1000 / 60) % 60) :
				Math.floor((rightTime / 1000 / 60) % 60);
			ss = Math.floor((rightTime / 1000) % 60) < 10 ? '0' + Math.floor((rightTime / 1000) % 60) : Math.floor((
				rightTime / 1000) % 60);
			if (dd != 0) {
				djsTime = dd + "天 " + hh + ":" + mm + ":" + ss;
			} else {
				djsTime = hh + ":" + mm + ":" + ss;
			}
		} else { //已结束
			speed = 100;
			djsTime = '已结束';
			activityStatus = 3;
		}
	}
	var item = {
		djsTime: djsTime, //距离当前时间差
		activityStatus: activityStatus, //活动状态 1：未开始 2：进行中 3：已结束
		speed: speed, //进度（单位%）
		dd: dd, //天
		hh: hh, //小时
		mm: mm, //分
		ss: ss, //秒
	}
	return item;
}


// 计算两个时间间隔
// var startTime = new Date(item.startTime.replace(/-/g, '/')).getTime(); //转时间戳
// var closeTime = new Date(item.closeTime.replace(/-/g, '/')).getTime(); //转时间戳
// var djs = this.djsTime(startTime, closeTime);
export const getTime = function(startTime, endTime) {
	var leftTime = endTime - startTime,
		djsTime = '',
		speed = 0,
		activityStatus = 0, //活动状态 1：未开始 2：进行中 3：已结束
		dd, hh, mm, ss;
	activityStatus = 1
	dd = Math.floor(leftTime / 1000 / 60 / 60 / 24);
	hh = Math.floor((leftTime / 1000 / 60 / 60) % 24) < 10 ? '0' + Math.floor((leftTime / 1000 / 60 / 60) %
		24) : Math.floor((leftTime / 1000 / 60 / 60) % 24);
	mm = Math.floor((leftTime / 1000 / 60) % 60) < 10 ? '0' + Math.floor((leftTime / 1000 / 60) % 60) : Math
		.floor((leftTime / 1000 / 60) % 60);
	ss = Math.floor((leftTime / 1000) % 60) < 10 ? '0' + Math.floor((leftTime / 1000) % 60) : Math.floor((
		leftTime / 1000) % 60);
	if (dd != 0) {
		djsTime = dd + "天 " + hh + ":" + mm + ":" + ss;
	} else {
		djsTime = hh + ":" + mm + ":" + ss;
	}
	var item = {
		djsTime: djsTime, //距离当前时间差
		activityStatus: activityStatus, //活动状态 1：未开始 2：进行中 3：已结束
		speed: speed, //进度（单位%）
		dd: dd, //天
		hh: hh, //小时
		mm: mm, //分
		ss: ss, //秒
	}
	return item;
}

// 小程序获取定位权限判断
// isOpenSetting  默认false:不检验授权，true:检验授权后获取地址
function getMpLocation(successCallback, errCallback, isOpenSetting) {
	uni.getSetting({
		success: res => {
			console.log('===getSetting===res==', res);
			if (res.authSetting['scope.userLocation'] || !isOpenSetting) {
				uni.getLocation({
					// #ifndef MP-ALIPAY
					type: 'gcj02',
					// #endif
					success(res) {
						console.log('--getLocation---successCallback')
						successCallback(res);
					},
					fail(err) {
						console.log("位置信息错误", err);
						errCallback("位置信息获取失败");
					}
				});
			} else {
				errCallback("“位置信息”未授权");
				isOpenSetting && uni.showModal({
					title: '提示',
					content: '请先在设置页面打开“位置信息”使用权限',
					confirmText: '去设置',
					cancelText: '再逛会',
					success: res => {
						if (res.confirm) {
							uni.openSetting();
						}
					}
				});
			}
		}
	});
}
// 获取地址信息
let locationAuthorize = true;
export const getAppLatLon = function(successCallback, errCallback, isOpenSetting) {
	const _this = this;
	// #ifdef MP-WEIXIN 
	if (locationAuthorize && isOpenSetting) {
		uni.authorize({
			scope: 'scope.userLocation',
			success: () => {
				getMpLocation(successCallback, errCallback, isOpenSetting);
				locationAuthorize = false;
			},
			fail: (res) => {
				locationAuthorize = false;
			}
		});
	} else {
		getMpLocation(successCallback, errCallback, isOpenSetting);
	}
	// #endif
	// #ifdef MP-TOUTIAO
	getMpLocation(successCallback, errCallback, false);
	// #endif
	// #ifdef MP-ALIPAY
	getMpLocation(successCallback, errCallback, false);
	// #endif
	// #ifdef H5
	uni.getLocation({
		type: 'gcj02',
		success(res) {
			console.log('successCallback')
			successCallback(res);
		},
		fail(err) {
			console.log("位置信息错误", err);
			errCallback("位置信息获取失败");
		}
	});
	// #endif
	// #ifdef APP-PLUS
	judgePermission("location", function(result) {
		if (result == 1) {
			uni.getLocation({
				type: 'gcj02',
				success: res => {
					successCallback(res);
				},
				fail: (err) => {
					console.log("位置信息错误", err);
					errCallback("位置信息获取失败");
				}
			});
		}
	});
	// #endif
}

// 打开外链
export const openLink = function(href) {
	var that = this
	// #ifdef APP-PLUS
	plus.runtime.openURL(href)
	// #endif
	// #ifdef H5
	window.open(href)
	// #endif
	// #ifdef MP
	uni.setClipboardData({
		data: href,
		success: () => {
			uni.hideToast();
			that.$nextTick(() => {
				that.$u.toast('链接已复制，请在浏览器打开');
			})
		}
	});
	// #endif
}