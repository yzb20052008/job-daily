import store from '@/store';
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
 * 定位 / 逆地理：客户端缓存 + 位移/TTL 节流 + 失败降级默认值
 * purpose: city=首页城市 | address=打卡地址 | coord=只要坐标
 */
const GEO_CACHE_KEY = 'geoCache';
/** 城市用途：30 分钟内、位移未超 800m 复用逆地理 */
const GEO_CITY_TTL_MS = 30 * 60 * 1000;
const GEO_CITY_MOVE_M = 800;
/** 打卡地址：10 分钟内、位移未超 150m 复用 */
const GEO_ADDR_TTL_MS = 10 * 60 * 1000;
const GEO_ADDR_MOVE_M = 150;
/** 逆地理失败时的默认城市（保证列表仍可浏览） */
const DEFAULT_CITY_INFO = {
	city: '附近',
	area: '附近',
	areaCode: '',
	cityCode: '',
};
/** 逆地理失败时的默认地址文案（保证打卡可提交展示） */
const DEFAULT_ADDRESS = '当前位置（地址解析暂不可用）';

function readGeoCache() {
	try {
		const mem = store.state && store.state.locateInformation;
		if (mem && mem.location && (mem.ad_info || mem.address) && mem._geoTs) {
			return mem;
		}
		return uni.getStorageSync(GEO_CACHE_KEY) || null;
	} catch (e) {
		return null;
	}
}

function writeGeoCache(data) {
	if (!data) {
		return;
	}
	const payload = Object.assign({}, data, {
		_geoTs: data._geoTs || Date.now()
	});
	try {
		uni.setStorageSync(GEO_CACHE_KEY, {
			location: payload.location,
			latitude: payload.latitude || (payload.location && payload.location.lat),
			longitude: payload.longitude || (payload.location && payload.location.lng),
			address: payload.address,
			ad_info: payload.ad_info,
			_geoTs: payload._geoTs,
			_geoFallback: !!payload._geoFallback
		});
	} catch (e) {
		// ignore
	}
	store.commit('setLocateInformation', payload);
}

function canReuseGeoCache(cache, lat, lng, maxMeters, maxAgeMs) {
	if (!cache || !cache._geoTs || !cache.location) {
		return false;
	}
	// 逆地理失败降级包禁止复用，避免「地址解析暂不可用」被 TTL 卡住
	if (cache._geoFallback) {
		return false;
	}
	if (!cache.ad_info && !cache.address) {
		return false;
	}
	if (Date.now() - Number(cache._geoTs) > maxAgeMs) {
		return false;
	}
	const cLat = cache.location.lat != null ? cache.location.lat : cache.latitude;
	const cLng = cache.location.lng != null ? cache.location.lng : cache.longitude;
	if (cLat == null || cLng == null) {
		return false;
	}
	// 简易球面距离（米），避免依赖后方 getDistance 声明顺序
	const toRad = d => (Number(d) * Math.PI) / 180;
	const r = 6378137;
	const dLat = toRad(lat - cLat);
	const dLng = toRad(lng - cLng);
	const a =
		Math.sin(dLat / 2) * Math.sin(dLat / 2) +
		Math.cos(toRad(cLat)) * Math.cos(toRad(lat)) * Math.sin(dLng / 2) * Math.sin(dLng / 2);
	const dist = 2 * r * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	return dist <= maxMeters;
}

/**
 * 从腾讯逆地理结果拼详细地址（address 可能为空，优先 formatted_addresses）
 */
function pickGeoAddress(raw, adInfo) {
	if (!raw && !adInfo) {
		return '';
	}
	const r = raw || {};
	const ad = adInfo || r.ad_info || {};
	const formatted = r.formatted_addresses || {};
	const direct = r.address || formatted.recommend || formatted.rough || '';
	if (direct) {
		return direct;
	}
	const parts = [ad.province, ad.city, ad.district].filter(Boolean);
	// 省市去重（直辖市 city==province）
	const uniq = [];
	parts.forEach(p => {
		if (uniq.indexOf(p) < 0) {
			uniq.push(p);
		}
	});
	return uniq.join('');
}

/**
 * 逆地理失败或无结果时的降级包：优先沿用缓存城市/地址，否则用默认值
 */
function buildFallbackGeo(lat, lng, cached) {
	const last = (store.state && store.state.lastLocation) || {};
	const ad = (cached && cached.ad_info) || {};
	const city = ad.city || last.city || DEFAULT_CITY_INFO.city;
	const district = ad.district || last.area || DEFAULT_CITY_INFO.area;
	const adcode = ad.adcode || last.areaCode || '';
	const province = ad.province || last.province || '';
	// 有城市信息时拼可读地址，避免首页有城市、打卡却显示「解析暂不可用」
	let address = (cached && cached.address) || last.address || '';
	if (!address || address === DEFAULT_ADDRESS) {
		address = pickGeoAddress(null, {
			province: province,
			city: city,
			district: district
		});
	}
	if (!address || address === '附近') {
		address = DEFAULT_ADDRESS;
	}
	return {
		location: { lat: lat, lng: lng },
		latitude: lat,
		longitude: lng,
		address: address,
		ad_info: {
			nation: ad.nation || '中国',
			province: province,
			city: city,
			district: district,
			adcode: adcode || '000000'
		},
		_geoFallback: true,
		_geoTs: Date.now()
	};
}

function normalizeGeoResult(raw, lat, lng) {
	if (!raw || !raw.ad_info) {
		return null;
	}
	const location = raw.location || { lat: lat, lng: lng };
	const address = pickGeoAddress(raw, raw.ad_info) || DEFAULT_ADDRESS;
	return Object.assign({}, raw, {
		location: location,
		latitude: location.lat != null ? location.lat : lat,
		longitude: location.lng != null ? location.lng : lng,
		address: address,
		_geoTs: Date.now(),
		_geoFallback: false
	});
}

/**
 * 获取定位信息（坐标走设备，逆地理走后台 /api/map，带缓存节流）
 * @param successCallback
 * @param errCallback
 * @param isGeoCode true|false，或 options: { isGeoCode, isOpenSetting, purpose, force }
 *        purpose: city|address|coord，默认 city（兼容旧调用）
 * @param isOpenSetting
 */
function loGetLocation(successCallback, errCallback, isGeoCode = true, isOpenSetting = false) {
	let purpose = 'city';
	let force = false;
	let needGeo = true;
	let openSetting = isOpenSetting;
	if (typeof isGeoCode === 'object' && isGeoCode !== null) {
		const opt = isGeoCode;
		needGeo = opt.isGeoCode !== false && opt.purpose !== 'coord';
		openSetting = !!opt.isOpenSetting;
		purpose = opt.purpose || (needGeo ? 'city' : 'coord');
		force = !!opt.force;
	} else {
		needGeo = !!isGeoCode;
		purpose = needGeo ? 'city' : 'coord';
	}

	getAppLatLon(item => {
		const latitude = item.latitude;
		const longitude = item.longitude;
		const cached = readGeoCache();

		// 只要坐标
		if (!needGeo || purpose === 'coord') {
			const result = {
				location: { lat: latitude, lng: longitude },
				latitude: latitude,
				longitude: longitude,
				address: (cached && cached.address) || DEFAULT_ADDRESS,
				ad_info: (cached && cached.ad_info) || null,
				_geoTs: (cached && cached._geoTs) || Date.now(),
				_geoFromCache: true
			};
			store.commit('setLocateInformation', Object.assign({}, cached || {}, result));
			successCallback && successCallback(result);
			return;
		}

		const maxMeters = purpose === 'address' ? GEO_ADDR_MOVE_M : GEO_CITY_MOVE_M;
		const maxAge = purpose === 'address' ? GEO_ADDR_TTL_MS : GEO_CITY_TTL_MS;
		if (!force && canReuseGeoCache(cached, latitude, longitude, maxMeters, maxAge)) {
			const reused = Object.assign({}, cached, {
				location: { lat: latitude, lng: longitude },
				latitude: latitude,
				longitude: longitude,
				_geoFromCache: true
			});
			// 打卡要有 address
			if (!reused.address) {
				reused.address = DEFAULT_ADDRESS;
			}
			store.commit('setLocateInformation', reused);
			successCallback && successCallback(reused);
			return;
		}

		uni.$u.http.get('/api/map/reverseGeocoder', {
			params: { latitude: latitude, longitude: longitude },
			custom: { load: false, auth: false }
		}).then(res => {
			const normalized = normalizeGeoResult(res, latitude, longitude);
			if (!normalized) {
				console.warn('逆地理结果缺 ad_info，使用降级默认值', res);
				const fallback = buildFallbackGeo(latitude, longitude, cached);
				writeGeoCache(fallback);
				successCallback && successCallback(fallback);
				return;
			}
			writeGeoCache(normalized);
			successCallback && successCallback(normalized);
		}).catch(err => {
			console.warn('逆地理编码失败，使用降级默认值', err);
			// 打卡场景给出可操作提示（常见：map_key 未配 / Key 无效 / 开了签名校验）
			if (purpose === 'address') {
				const msg = (err && (err.message || err.msg)) || '';
				uni.showToast({
					title: msg && msg.length < 40 ? msg : '地址解析失败，请检查地图Key配置',
					icon: 'none',
					duration: 2500
				});
			}
			const fallback = buildFallbackGeo(latitude, longitude, cached);
			writeGeoCache(fallback);
			successCallback && successCallback(fallback);
		});
	}, err => {
		// 定位权限失败：尽量用缓存保证可浏览
		const cached = readGeoCache();
		const last = (store.state && store.state.lastLocation) || {};
		if (cached && cached.location) {
			const soft = buildFallbackGeo(cached.location.lat, cached.location.lng, cached);
			store.commit('setLocateInformation', soft);
			successCallback && successCallback(soft);
			return;
		}
		if (last.latitude != null && last.longitude != null) {
			const soft = buildFallbackGeo(last.latitude, last.longitude, null);
			soft.ad_info.city = last.city || soft.ad_info.city;
			soft.ad_info.district = last.area || soft.ad_info.district;
			soft.ad_info.adcode = last.areaCode || soft.ad_info.adcode;
			store.commit('setLocateInformation', soft);
			successCallback && successCallback(soft);
			return;
		}
		store.commit('setLocateInformation', {});
		errCallback && errCallback(err);
	}, openSetting);
}

/*
 * 逆地理编码（后台代理腾讯地图），失败返回带默认值的结果
 */
function getRegeo(latitude, longitude, successCallback, errCallback) {
	uni.$u.http.get('/api/map/reverseGeocoder', {
		params: { latitude: latitude, longitude: longitude },
		custom: { load: false, auth: false }
	}).then(res => {
		const normalized = normalizeGeoResult(res, latitude, longitude);
		if (!normalized) {
			const fallback = buildFallbackGeo(latitude, longitude, readGeoCache());
			successCallback && successCallback(fallback);
			return;
		}
		writeGeoCache(normalized);
		successCallback && successCallback(normalized);
	}).catch(err => {
		console.warn('逆地理编码失败', err);
		const fallback = buildFallbackGeo(latitude, longitude, readGeoCache());
		successCallback && successCallback(fallback);
		errCallback && errCallback(err);
	});
}

function geocodeAddress(address, successCallback, errCallback) {
	if (!address) { errCallback && errCallback('地址为空'); return; }
	uni.$u.http.get('/api/map/geocoder', {
		params: { address: address },
		custom: { load: false, auth: false }
	}).then(res => { successCallback && successCallback(res) }).catch(err => { errCallback && errCallback(err) });
}

function searchNearbyPois(latitude, longitude, successCallback, errCallback) {
	uni.$u.http.get('/api/map/nearby', {
		params: { latitude: latitude, longitude: longitude, pageSize: 20 },
		custom: { load: false, auth: true }
	}).then(res => { successCallback && successCallback(res || []) }).catch(err => { errCallback && errCallback(err) });
}

function searchPlaceSuggestion(keyword, latitude, longitude, successCallback, errCallback) {
	uni.$u.http.get('/api/map/suggestion', {
		params: { keyword: keyword, latitude: latitude, longitude: longitude, pageSize: 20 },
		custom: { load: false, auth: false }
	}).then(res => { successCallback && successCallback(res || []) }).catch(err => { errCallback && errCallback(err) });
}

/** 兼容旧调用：转发到后台逆地理 */
function loGetGaodeLocation(successCallback, errCallback, isOpenSetting = false) {
	loGetLocation(successCallback, errCallback, true, isOpenSetting)
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
	loGetGaodeLocation, //兼容旧调用
	choiseRegion, //选择地址
	phoneHiden, //隐藏手机号中间四位
	requestSubscribe, //小程序消息订阅
	getDistance,
	getRad,
	calCurrentYear,
	getRegeo,
	geocodeAddress,
	searchNearbyPois,
	searchPlaceSuggestion,
	saveReferrer,
	getQueryString
}