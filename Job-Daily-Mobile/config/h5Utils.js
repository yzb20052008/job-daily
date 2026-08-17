import store from '@/store';
import base from "@/config/baseUrl";

// 获取链接参数
function getUrlParam(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) return unescape(r[2]);
    return null;
}
// h5登录
export const h5Login = (callback) => {
    if (isWechat()) {
        // 公众号登录
        var local = encodeURIComponent(window.location.href);
        var url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + base.publicAppId + "&redirect_uri=" + local + "&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect"
        var code = getUrlParam("code");
        if (!code) {
            window.location = url;
        } else {
            var httpData = {
                code: code
            }
            uni.$u.http.post('/user/login', httpData).then((res) => {
                let userInfo = {
                    ...res,
                    token:true,//token用于判断是否登录
                }
                store.commit('setUserInfo',userInfo)
                callback && callback(res)
            })
        }
    } else {
        // 通用登录页--验证码登录
        uni.showModal({
        	title:"温馨提示",
        	content:"您还未登录，请先登录~",
        	confirmText: "去登录",
        	cancelText: "再逛会",
        	success: (res) => {
        		if(res.confirm){
        			uni.navigateTo({
        				url: "/pages/user/login"
        			});
        		}
        	}
        });
    }
}
// 浏览器判断是否微信
export const isWechat = () => {
	let ua = navigator.userAgent.toLowerCase();
	if (ua.match(/MicroMessenger/i) == "micromessenger") {
		//console.log('是微信客户端')  
		return true;  
	}else{
        //console.log('不是微信客户端')  
        return false;  
    }
};