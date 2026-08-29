import Request from '@/uni_modules/uview-ui/libs/luch-request/index.js'
import base from "@/config/baseUrl";
import store from '@/store';

import { getToken,judgeLogin } from '@/config/login';
var throttleLogin = true

// 初始化请求配置
uni.$u.http.setConfig((config) => {
	// 需要token的打开
    var token = store.state.userInfo.token || (uni.getStorageSync('userInfo').token || undefined)
    /* config 为默认全局配置*/
    config.baseURL = base.baseUrl; /* 根域名 */
    config.header = {
		 'Content-Type': 'application/json',//application/json  、application/x-www-form-urlencoded
         'X-Access-Token':token
    }
    config.custom = {
        load:true,//是否显示加载动画
        isFactory: true,//true:返回的数据成功只返回data  false:返回response
        catch: true,//默认数据返回不成功进入catch返回
        auth: true,//token
    }
    return config
})

// 请求拦截
uni.$u.http.interceptors.request.use((config) => { // 可使用async await 做异步操作
    // 初始化请求拦截器时，会执行此方法，此时data为undefined，赋予默认{}
    config.data = config.data || {}
    // 根据custom参数中配置的是否需要token，添加对应的请求头
    if(config?.custom?.auth) {
        config.header['X-Access-Token'] = store.state.userInfo.token || (uni.getStorageSync('userInfo').token || undefined)
    }
    console.log("请求开始", config);
    if (config?.custom?.load) {
        //打开加载动画
        store.commit("setLoadingShow", true);
    }
    
    return config 
}, config => { // 可使用async await 做异步操作
    return Promise.reject(config)
})

/**
 * 避免进入死循环，重新new一个用于重新发起请求的实例
 */
const againHttp = new Request({
	baseURL: base.baseUrl
})
againHttp.interceptors.request.use(config => {
	config.header.Authorization = store.state.userInfo.token || (uni.getStorageSync('userInfo').token || '')
	return config
}, error => {
	return Promise.reject(error)
})

// 响应拦截
uni.$u.http.interceptors.response.use(async (response) => { /* 对响应成功做点什么 可使用async await 做异步操作*/
    // 关闭加载动画
    store.commit("setLoadingShow", false);
    const data = response.data
	console.log("data====----=========",data);
    // 自定义参数
    const custom = response.config?.custom
	// code参数根据后台定义自行更改
    // code:  200、请求成功 -1、请求成功，没有更多参数 2、被迫下线重新登录、
    if(data.code == 200 || data.code ==0){
        if(!custom.isFactory){
            return data
        }else{
            return data.result === undefined ? {} : data.result
        }
    }else if(data.code == 401){//被迫下线/token失效
		// token无感刷新
		// try {
		// 	// 重新请求token
		// 	const { code } = await getToken()
		// 	if (code == 1) {
		// 		try {
		// 			const localResponse = await againHttp.middleware(response.config)
		// 			if(!custom.isFactory){
		// 			    return localResponse.data
		// 			}else{
		// 			    return localResponse.data.data === undefined ? {} : localResponse.data.data
		// 			}
		// 		} catch (e) {
		// 			// 清空登录信息
		// 			store.commit("emptyUserInfo");
		// 			// 重新登录
		// 			judgeLogin()
		// 			return Promise.reject(e)
		// 		}
		// 	} else {
		// 		// 清空登录信息
		// 		store.commit("emptyUserInfo");
		// 		// 重新登录
		// 		judgeLogin()
		// 		return Promise.reject(data)
		// 	}
		// } catch (e) {
		// 	// 可能由于网络原因;可以根据自身逻辑处理，我这里直接放弃本次请求，会进入catch
		// 	return Promise.reject(data)
		// }
		
		// 失效直接登录--（token无感刷新使用后，以下方法需删除）
        store.commit("emptyUserInfo");// 清空登录信息
		// 15秒节流登录
		if(throttleLogin){
		    throttleLogin = false
			setTimeout(()=>{
			    throttleLogin = true
			},15000)
			judgeLogin();
			// uni.reLaunch({
			// 	url: '/pages/user/login'
			// });
		}
        return new Promise(() => { })
    }else{
        // 如果没有显式定义custom的toast参数为false的话，默认对报错进行toast弹出提示
        if (custom.toast !== false) {
			// 业务错误码（如 90001）仍展示 message，兼容 W6 统一错误码
			if(data.message!="该用户不存在，请注册"){
				uni.$u.toast(data.message || data.msg)
			}
        }
        // 如果需要catch返回，则进行reject
        if (custom?.catch) {
            return Promise.reject(data)
        } else {
            // 否则返回一个pending中的promise，请求不会进入catch中
            return new Promise(() => { })
        }
    }
}, (response) => {
	console.log("---------response--------",response.data)
    // 关闭加载动画
    store.commit("setLoadingShow", false);
	if(response.data.code == 401){//被迫下线/token失效
		// 失效直接登录--（token无感刷新使用后，以下方法需删除）
	    store.commit("emptyUserInfo");// 清空登录信息
		// 15秒节流登录
		if(throttleLogin){
		    throttleLogin = false
			setTimeout(()=>{
			    throttleLogin = true
			},15000)
			// uni.reLaunch({
			// 	url: '/pages/user/login'
			// });
			judgeLogin();
		}
	    return new Promise(() => { })
	}
	
    // 对响应错误做点什么 （statusCode !== 200）
    let errorData = '请检查网络或服务器'
    let message = response.message || response.msg
    if(message == "request:fail url not in domain list") {
        errorData = '检查请求域名是否添加了域名白名单'
    }else if (message == 'request:fail timeout') {
        errorData = '请求超时:请检查网络'
    }else{
        errorData = message || '请检查网络或服务器'
    }
	if(errorData!="该用户不存在，请注册"){
		uni.$u.toast(errorData)
	}
    return Promise.reject(response)
})