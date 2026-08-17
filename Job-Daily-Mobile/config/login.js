import store from '@/store';
import { h5Login } from '@/config/h5Utils';
// APP--授权登录
// type授权登录平台 'qq'、'weixin'、'apple'
export const loginApp = (type,successCallback,errorCallback) => {
    uni.login({
        provider: type,
        success: function(loginRes) {
            // 获取用户信息
            uni.getUserInfo({
                provider: type,
                success: function(infoRes) {
                    let data = {}
                    if(type=='qq'){
                        data = {
                            'openid': loginRes.authResult.openid,
                            'nickname': infoRes.userInfo.nickname,
                            'gender': infoRes.userInfo.gender,
                            'province': infoRes.userInfo.province,
                            'city': infoRes.userInfo.city,
                            'figureurl': infoRes.userInfo.figureurl_qq
                        }
                    }else if(type=='weixin'){
                        data = {
                            'openid': loginRes.authResult.openid,
                            'nickname': infoRes.userInfo.nickName,
                            'sex': infoRes.userInfo.gender,
                            'province': infoRes.userInfo.province,
                            'city': infoRes.userInfo.city,
                            'country': infoRes.userInfo.country,
                            'headimgurl': infoRes.userInfo.avatarUrl,
                            'unionid': loginRes.authResult.unionid
                        }
                    }else if(type=='apple'){
                        data = {
                            verifyType: 'password',
                            personalPhone: '13888888888',
                            personalPhoneCountryCode: '86',
                            password:'123456'
                        }
                    }
                    successCallback && successCallback(data)
                }
            });
        },fail: function (err) {  
            console.log(res, "失败")
            errorCallback && errorCallback(res)
        }  
    });
}
// 微信/支付宝小程序---手机号授权登录时使用
// info: uni.login获取的参数
export const getPhoneInfo = (info, successCallback, errCallback) => {
    let httpData = {}
    // #ifdef MP-WEIXIN
    httpData = {
    	code: info.code, //小程序code
    	iv: info.iv, //小程序加密算法的初始向量
    	encryptedData: info.encryptedData, //包括敏感数据在内的完整用户信息的加密数据
    };
    // #endif
    // #ifdef MP-ALIPAY
	httpData = {
		code: '', //小程序code
		iv: '', //小程序加密算法的初始向量
		encryptedData: info, //包括敏感数据在内的完整用户信息的加密数据
	};
    // #endif
    
    // 此时需要您的接口返回个人信息
	// uni.$u.http.post('您的接口', httpData).then(res => {
        var loginInfo = {
            // userId: res.id,
            // sessionId: res.sessionId,
            // phoneNum: res.phoneNum,
            // userName: res.userName,
            // openId: res.openId,
        };
        successCallback && successCallback(loginInfo)
    // }, err => {
    //     errCallback && errCallback(err)
    // });
}
// 微信/支付宝小程序---通用授权个人信息登录
export const getUserInfo = (successCallback,errorCallback) => {
    uni.showLoading({
        title: '正在申请授权',
    });
    // #ifdef MP-WEIXIN
    uni.getUserProfile({
        desc: '用于完善会员资料',
        success: function(res) {
            uni.hideLoading()
            var offUserInfo = res.userInfo
            successCallback && successCallback(offUserInfo)
        },fail: (res) => {
            uni.hideLoading()
            errorCallback && errorCallback(res)
        }
    })
    // #endif
    // #ifdef MP-ALIPAY
    uni.getOpenUserInfo({
        success: (res) => {
            uni.hideLoading()
            var offUserInfo = JSON.parse(res.response).response // 以下方的报文格式解析两层 response
            offUserInfo.avatarUrl = offUserInfo.avatar
            successCallback && successCallback(offUserInfo)
        },fail: (res) => {
            uni.hideLoading()
            console.log(res, "失败")
            errorCallback && errorCallback(res)
        }
    })                    
    // #endif
}

let lock = false
let promiseResult = []
// 获取token
export const getToken = () => {
	return new Promise((resolve, reject) => {
		promiseResult.push({
			resolve,
			reject
		})
		if (!lock) {
			lock = true
			// uni.login({
			//     success(res){
					var httpData = {
						username: 'ceshi',
						password: '111111',
					}
					uni.$u.http.post('/api/Tokensys/login',httpData,{custom: {isFactory:false}}).then(res => {
						if(res.code==1){
							let userInfo = {
								token:res.token,//token用于判断是否登录
							}
							console.log(res.token,'res.token')
							store.commit('setUserInfo',userInfo)
							console.log(store.state.userInfo.token,'getToken-token')
							// uni.$emit("loginCallback") //全局登录监听回调方法
						}
						while (promiseResult.length) {
							// p1.resolve(res.data)
							promiseResult.shift().resolve(res)
						}
						lock = false
					}).catch(err => {
						while (promiseResult.length) {
							// p1.reject(err)
							promiseResult.shift().reject(err)
						}
						lock = false
					})
			//     }
			// })
		}
	})
}

//判断是否登录（所有端）
export const judgeLogin = (callback) => {
    let storeUserInfo = store.state.userInfo;
    if (!storeUserInfo.userId){ // nvue页面读取不到vuex里面数据，将取缓存
        storeUserInfo = uni.getStorageSync('userInfo')
    }
	console.log("=judgeLogin==")
    if (!storeUserInfo.token) {
        // #ifdef MP
		console.log("setLoginPopupShow==")
        store.commit('setLoginPopupShow', true);
        // #endif
        // #ifdef APP-PLUS
        uni.$showModal({
            title: "登录提示",
            confirmVal:'去登录',
            cancelVal:'再逛会',
            content:'此时此刻需要您登录喔~',
        }).then(res=>{
            uni.navigateTo({
                url: "/pages/user/login"
            });
        }).catch(res=>{})
        // #endif
        // #ifdef H5
        h5Login();
        // #endif
    }else{
        callback(true)
    }
}