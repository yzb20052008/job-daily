import store from '@/store';
import base from "@/config/baseUrl";
import QQMapWX from '@/plugins/qqmap-wx-jssdk.js';
import amap from '@/plugins/amap-wx.js';
import {
	getAppLatLon
} from '@/plugins/utils';

//檢查小程序更新
function mpUpData(callback) {
	if (uni.getUpdateManager) {
		const updateManager = uni.getUpdateManager();
		updateManager.onCheckForUpdate(function(res) {
			// 请求完新版本信息的回调
			// console.log(res.hasUpdate);
			// if(!res.hasUpdate){
			//     uni.showToast({
			//         title: '当前没有新版发布~',
			//         duration: 2000
			//     });
			// }
			let info = {
				type: 1,
				data: res
			}
			callback && callback(info)
		});
		updateManager.onUpdateReady(function(res) {
			uni.showModal({
				title: "更新提示",
				content: "检测到新版本，是否下载新版本并重启小程序？",
				success(res) {
					if (res.confirm) {
						// 新的版本已经下载好，调用 applyUpdate 应用新版本并重启
						updateManager.applyUpdate();
					}
				}
			});
		});
		updateManager.onUpdateFailed(function(res) {
			// 新的版本下载失败
			uni.showModal({
				title: "已经有新版本了哟~",
				content: "新版本已经上线啦~，请您删除当前小程序，重新搜索打开哟~",
				showCancel: false
			});
		});
	} else {
		// 如果希望用户在最新版本的客户端上体验您的小程序，可以这样子提示
		// uni.showModal({
		//     title: '提示',
		//     content: '当前微信版本过低，无法使用该功能，请升级到最新微信版本后重试。'
		// })
		let info = {
			type: 2
		}
		callback && callback(info)
	}
}

// App计算缓存
function formatSize(callback) {
	let fileSizeString = "0B";
	plus.cache.calculate(function(size) {
		let sizeCache = parseInt(size);
		if (sizeCache == 0) {
			fileSizeString = "0B";
		} else if (sizeCache < 1024) {
			fileSizeString = sizeCache + "B";
		} else if (sizeCache < 1048576) {
			fileSizeString = (sizeCache / 1024).toFixed(2) + "KB";
		} else if (sizeCache < 1073741824) {
			fileSizeString = (sizeCache / 1048576).toFixed(2) + "MB";
		} else {
			fileSizeString = (sizeCache / 1073741824).toFixed(2) + "GB";
		}
		callback && callback(fileSizeString)
	});
}
/*
 *  App清理缓存
 *  fileSizeString:当前缓存
 */
function clearCache(fileSizeString) {
	if (fileSizeString == '0B') {
		uni.showToast({
			title: '暂无缓存~',
			duration: 2000,
			icon: 'none'
		});
		return
	}
	return new Promise((resolve, reject) => {
		let os = plus.os.name;
		if (os == 'Android') {
			let main = plus.android.runtimeMainActivity();
			let sdRoot = main.getCacheDir();
			let files = plus.android.invoke(sdRoot, "listFiles");
			let len = files.length;
			for (let i = 0; i < len; i++) {
				let filePath = '' + files[i]; // 没有找到合适的方法获取路径，这样写可以转成文件路径 
				plus.io.resolveLocalFileSystemURL(filePath, function(entry) {
					if (entry.isDirectory) {
						entry.removeRecursively(function(entry) { //递归删除其下的所有文件及子目录 
							uni.showToast({
								title: '缓存清理完成',
								duration: 2000
							});
							// 回调
							resolve()
						}, function(e) {
							console.log(e.message)
						});
					} else {
						entry.remove();
					}
				}, function(e) {
					console.log('文件路径读取失败')
				});
			}
		} else { // ios 
			plus.cache.clear(function() {
				uni.showToast({
					title: '缓存清理完成',
					duration: 2000
				});
				// 回调
				resolve()
			});
		}
	});
}

//e 扫码参数
//flag uni.scanCode方法点击扫码的参数没有q，为true转换参数
function scene(e, callback, flag = false) {
	store.commit("setChatScenesInfo", {}); //先请空
	console.log(e, 'scene')
	var qrCodeValue = ''
	// #ifndef MP-ALIPAY
	if (flag) {
		e.query = {
			q: e.result
		}
		console.log(e, 'scene-转换后')
	}
	if (e.query.q) {
		let scene = decodeURIComponent(e.query.q).split("?")[1];
		scene = scene.split("&");
		let data = {
			//场景值
			scene: e.scene,
		};
		scene.forEach(item => {
			let arr = item.split("=");
			if (arr.length == 2) {
				data[arr[0]] = arr[1];
			}
		});
		store.commit("setChatScenesInfo", Object.assign(e.query, data));
		console.log(store.state.chatScenesInfo, 'scene--解码参数')
	} else {
		store.commit("setChatScenesInfo", Object.assign(e.query, {
			path: e.path
		}));
	}
	// #endif
	// #ifdef MP-ALIPAY
	var AliqrCode = {}
	if (flag) {
		e.query = {
			qrCode: e.result
		}
		console.log(e, 'scene-转换后')
	}
	if (e.query && e.query.qrCode) {
		AliqrCode.q = e.query.qrCode;
	} else if (e.query && !e.query.qrCode) {
		AliqrCode = e.query;
	}
	if (AliqrCode.q) {
		var queryParam = AliqrCode.q.split("?")[1] //二维码清除域名
		var scene = queryParam.split("&");
		let data = {};
		scene.forEach(item => {
			let arr = item.split("=");
			if (arr.length == 2) {
				data[arr[0]] = arr[1];
			}
		});
		store.commit("setChatScenesInfo", Object.assign(AliqrCode, data));
		console.log(store.state.chatScenesInfo, 'scene--支付宝小程序解码参数')
	} else {
		store.commit("setChatScenesInfo", Object.assign(AliqrCode, {
			path: e.path
		}));
	}
	// #endif
	callback && callback()
}

/*
 * 获取定位信息
 * successCallback:成功回调
 * errCallback:失败回调
 * isOpenSetting:是否检验授权 默认不检验
 */
function loGetLocation(successCallback, errCallback, isGeoCode = true, isOpenSetting = false) {
	var that = this
	getAppLatLon(item => {
		if (base.mapData?.key && isGeoCode) {
			var latitude = item.latitude; // 纬度，浮点数，范围为90 ~ -90
			var longitude = item.longitude; // 经度，浮点数，范围为180 ~ -180
			var qqmapsdk = new QQMapWX({
				key: base.mapData
					?.key, // 您的key---秘钥key值可通过https://lbs.qq.com/qqmap_wx_jssdk/index.html申请
				// #ifdef H5
				vm: that
				// #endif
			});
			// 地址逆解析可获取省市区等信息
			qqmapsdk.reverseGeocoder({
				location: {
					latitude: latitude,
					longitude: longitude
				},
				sig: base.mapData?.sk,
				success: function(res) {
					store.commit('setLocateInformation', res.result)
					successCallback && successCallback(res.result)
				},
				fail: function(err) {
					item.location = {
						lat: latitude,
						lng: longitude,
					}
					store.commit('setLocateInformation', item)
					successCallback && successCallback(item)
				}
			});
		} else {
			item.location = {
				lat: item.latitude,
				lng: item.longitude,
			}
			store.commit('setLocateInformation', item)
			successCallback && successCallback(item)
		}
	}, err => {
		store.commit('setLocateInformation', {})
		errCallback && errCallback(err)
	}, isOpenSetting)
}


/*
 * 逆地理编码
 * successCallback:成功回调
 * errCallback:失败回调
 * isOpenSetting:是否检验授权 默认不检验
 */
function getRegeo(latitude,longitude,successCallback, errCallback) {
	var that = this
	var qqmapsdk = new QQMapWX({
		key: base.mapData
			?.key, // 您的key---秘钥key值可通过https://lbs.qq.com/qqmap_wx_jssdk/index.html申请
		// #ifdef H5
		vm: that
		// #endif
	});
	// 地址逆解析可获取省市区等信息
	qqmapsdk.reverseGeocoder({
		location: {
			latitude: latitude,
			longitude: longitude
		},
		sig: base.mapData?.sk,
		success: function(res) {
			successCallback && successCallback(res.result)
		},
		fail: function(err) {
			item.location = {
				lat: latitude,
				lng: longitude,
			}
			successCallback && successCallback(item)
		}
	});
}

/*
 * 获取定位信息
 * successCallback:成功回调
 * errCallback:失败回调
 * isOpenSetting:是否检验授权 默认不检验
 */
function loGetGaodeLocation(successCallback, errCallback, isOpenSetting = false) {
	var that = this
	getAppLatLon(item => {
		if (base.mapData?.key) {
			var latitude = item.latitude; // 纬度，浮点数，范围为90 ~ -90
			var longitude = item.longitude; // 经度，浮点数，范围为180 ~ -180
			//高德地图插件
			var gdmapsdk = new amap.AMapWX({
				key: base.mapData?.key
			})
			// 地址逆解析可获取省市区等信息
			gdmapsdk.getRegeo({
				success: function(data) {
					//成功回调
					console.log('定位信息：', data);
					let mlocation = {
						latitude: data[0].latitude,
						longitude: data[0].longitude,
						pcity: data[0].regeocodeData.addressComponent.city,
						city: data[0].regeocodeData.addressComponent.district,
						citycode: data[0].regeocodeData.addressComponent.adcode + '000000',
						pcitycode: data[0].regeocodeData.addressComponent.adcode.substr(0, 4) +
							'00000000'
					};
					store.commit('setLocateInformation', mlocation)
					successCallback && successCallback(mlocation)
				},
				fail: function(info) {
					//失败回调
					console.log("==fail==",info);
					item.location = {
						lat: latitude,
						lng: longitude,
					}
					store.commit('setLocateInformation', item)
					successCallback && successCallback(item)
				}
			});
		} else {
			item.location = {
				lat: item.latitude,
				lng: item.longitude,
			}
			store.commit('setLocateInformation', item)
			successCallback && successCallback(item)
		}
	}, err => {
		store.commit('setLocateInformation', {})
		errCallback && errCallback(err)

	}, isOpenSetting)
}


// 选择地址
function choiseRegion(callback) {
	var that = this;
	uni.chooseLocation({
		success: function(res) {
			callback && callback(res)
		},
		fail: function() {
			uni.getSetting({
				success: function(res) {
					if (res.authSetting['scope.userLocation']) {
						uni.showModal({
							title: '是否授权当前位置',
							content: '需要获取您的地理位置，请确认授权，否则地图功能将无法使用',
							success: function(tip) {
								if (tip.confirm) {
									uni.openSetting({
										success: function(data) {
											if (data.authSetting[
													"scope.userLocation"
												] === true) {
												that.$u.toast('授权成功~');
												//授权成功之后，再调用chooseLocation选择地方
												that.choiseRegion()
											} else {
												that.$u.toast('授权失败~');
											}
										}
									})
								}
							}
						})
					}
				},
				fail: function(res) {
					that.$u.toast('调用授权窗口失败~');
				}
			})
		}
	})
}

function phoneHiden(cellValue) {
	if (Number(cellValue) && String(cellValue).length === 11) {
		var mobile = String(cellValue)
		var reg = /^(\d{3})\d{4}(\d{4})$/
		return mobile.replace(reg, '$1****$2')
	} else {
		return cellValue
	}
}

//发起消息订阅type:0-认证审核，1-提现申请，2-用工结果,3-新订单通知
function requestSubscribe(type, successCallback, errorCallback) {
	// #ifdef MP-WEIXIN
	let tmplIds;
	if (type == 0) {
		tmplIds = ['rORH6Ct2fOi5JMGsZABamh5LrB2w0ZmhWIA03T0IS8I'];
	} else if (type == 1) {
		tmplIds = ['ij_HvuqyZZ_lZnAlMpWv2zcUCm_Nwd4jTUlGPk1FEaM']
	}else if (type == 2) {
		tmplIds = ['dKD4VQ-E9kChwTRI5nsteBGRIM9WYOWuT9acAPwGGGc']
	}else if (type == 3) {
		tmplIds = ['6wvo2jmjl9e5JrkcyTUBnTQZ_YYfVi75WXXr96mpL4E']
	}
	wx.requestSubscribeMessage({
		tmplIds: tmplIds,
		success(res) {
			console.log("订阅消息 成功 ", res);
			if (type == 0) {
				if (res['rORH6Ct2fOi5JMGsZABamh5LrB2w0ZmhWIA03T0IS8I'] == 'reject') {
					successCallback && successCallback(false)
				} else {
					successCallback && successCallback(true)
				}
			} else if (type == 1) {
				if (res['ij_HvuqyZZ_lZnAlMpWv2zcUCm_Nwd4jTUlGPk1FEaM'] == 'reject') {
					successCallback && successCallback(false)
				} else {
					successCallback && successCallback(true)
				}
			}else if (type == 2) {
				if (res['dKD4VQ-E9kChwTRI5nsteBGRIM9WYOWuT9acAPwGGGc'] == 'reject') {
					successCallback && successCallback(false)
				} else {
					successCallback && successCallback(true)
				}
			}else if (type == 3) {
				if (res['6wvo2jmjl9e5JrkcyTUBnTQZ_YYfVi75WXXr96mpL4E'] == 'reject') {
					successCallback && successCallback(false)
				} else {
					successCallback && successCallback(true)
				}
			}
			// successCallback && successCallback(res)
		},
		fail(error) {
			console.log("订阅消息 失败 ", error);
			errorCallback && errorCallback(error)
		},
		complete(errMsg) {
			console.log("订阅消息 完成 ", errMsg);
		}
	});
	// #endif
}


/**
 * 获取两个经纬度之间的距离
 * @param lat1 第一点的纬度
 * @param lng1 第一点的经度
 * @param lat2 第二点的纬度
 * @param lng2 第二点的经度
 * @returns {Number}
 */
function getDistance(lat11, lng11, lat22, lng22) {
	//格式转换，避免输入的string字符
	var lat1 = parseFloat(lat11);
	var lng1 = parseFloat(lng11);
	var lat2 = parseFloat(lat22);
	var lng2 = parseFloat(lng22);
	//相等的直接返回
	if (lat1 == lat2 && lng1 == lng2) {
		return 0;
	}
	var f = getRad((lat1 + lat2) / 2);
	var g = getRad((lat1 - lat2) / 2);
	var l = getRad((lng1 - lng2) / 2);
	var sg = Math.sin(g);
	var sl = Math.sin(l);
	var sf = Math.sin(f);
	var s, c, w, r, d, h1, h2;
	var a = 6378137.0; //The Radius of eath in meter.   
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
	s = d * (1 + fl * (h1 * sf * (1 - sg) - h2 * (1 - sf) * sg));
	// s = s / 1000;
	// s = s.toFixed(2); //指定小数点后的位数。 
	s = s.toFixed(0)
	return s;
}

function getRad(d) {
	var PI = Math.PI;
	return d * PI / 180.0;
}

/**
 * 计算两个日期相隔年限，用于计算年龄等
 * @param {Object} date1
 * @param {Object} date2
 */
function  calCurrentYear(date) {
	if(date==null || date==undefined){
		return 0;
	}
	let a = new Date(date).getFullYear();
	let b = new Date().getFullYear();
	return b-a;
}

/**
 * 保存推荐码
 * @param {Object} query
 */
function  saveReferrer(query) {
	// #ifdef H5
	// H5环境推广码注册
	if (query.referrer && query.referrer != " ") {
		//保存推广用户id
		uni.setStorageSync('referrer', query.referrer);
		return;
	}
	//  #endif
	// #ifdef MP-WEIXIN
	if (query.referrer && query.referrer != " ") {
		uni.setStorageSync('referrer', query.referrer);
		return;
	}
	if (query.q) {
		let url = decodeURIComponent(query.q); //此处得到的是一个地址
		let referrer = getQueryString(url, "referrer");
		console.log("==referrer==",referrer);
		if (referrer) {
			uni.setStorageSync('referrer', referrer);
		}
		return;
	}
	//  #endif
}

function getQueryString(url, name) {
		var reg = new RegExp('(^|&|/?)' + name + '=([^&|/?]*)(&|/?|$)', 'i')
		var r = url.substr(1).match(reg)
		if (r != null) {
			return r[2]
		}
		return null;
	}



export {
	mpUpData, //小程序更新
	formatSize, //App计算缓存
	clearCache, //App清理缓存
	scene, //扫码信息
	loGetLocation, //获取定位信息
	loGetGaodeLocation, //获取高德定位信息
	choiseRegion, //选择地址
	phoneHiden, //隐藏手机号中间四位
	requestSubscribe, //小程序消息订阅
	getDistance,
	getRad,
	calCurrentYear,
	getRegeo,
	saveReferrer,
	getQueryString
}