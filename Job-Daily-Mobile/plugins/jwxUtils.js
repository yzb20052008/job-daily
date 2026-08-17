// 获取微信公众号SDK权限
import base from '@/config/baseUrl';
import { isWechat } from '@/config/h5Utils';
import store from "@/store";

//获取地理位置
export const getLocation = () => {
	return new Promise((resolve, reject) => {
		jWeixin.ready(function () {
			jWeixin.getLocation({
				type: 'gcj02',
				success: function (res) {
					resolve(res);
				},
				fail: (err) => {
					reject(err);
				}
			});
		});
	});
}
export const shareData = (info) => {
    let item = {
        title: info.title || base.share.title, // 分享标题
        desc: info.desc || base.share.desc, // 分享描述
        imgUrl: info.imgUrl || base.share.imgUrl, // 分享链接
        link: info.link || base.share.link, // 分享图标
    }
    return item
}
//设置分享信息
export const setShare = (data, callback) => {
	//配置校验成功后执行
	jWeixin.ready(function () {
		if (!data.link) {
			let url = window.location.href;
			let index = url.indexOf("?");
			if (index != -1) {
				if (url.indexOf("#") != -1 && url.indexOf("#") > index) {
					url = url.substring(0, index) + url.substring(url.indexOf("#"));
				} else {
					url = url.substr(0, index);
				}
			}
			data.link = url;
		}
		jWeixin.updateAppMessageShareData(shareData(data));
		jWeixin.updateTimelineShareData(shareData(data));
	});
}
//微信扫一扫
export const scanQRCode = ( callback,needResult = 0) => {
	//配置校验成功后执行
	jWeixin.ready(function () {
		jWeixin.scanQRCode({
		  needResult: needResult, // 默认为0，扫描结果由微信处理，1则直接返回扫描结果，
		  scanType: ["qrCode","barCode"], // 可以指定扫二维码还是一维码，默认二者都有
		  success: function (res) {
			callback && callback(res);
		  }
		});
	});
}
//刷脸
export const requestWxFacePictureVerify = (data, callback) => {
	//配置校验成功后执行
	jWeixin.ready(function () {
		jWeixin.invoke('checkIsSupportFaceDetect', {}, function (res) {
			if (res.err_code == 0) {
				jWeixin.invoke("requestWxFacePictureVerify",{
					"appid": base.publicAppId, //appId
					"request_verify_pre_info": JSON.stringify({name:data.name,id_card_number:data.id_card_number}),
					// request_verify_pre_info:JSON.stringify({user_id_key:data.user_id_key}),
					"check_alive_type":data.check_alive_type, //0：读数字、1：屏幕闪烁、2：先检查是否可以屏幕闪烁，不可以则自动为读数字
				},function(res){
					callback && callback(res);
				})
			}else{
				uni.$u.toast("该设备不支持人脸识别！")
			}
		})
	});
}
//获取微信JSSDK授权
window.onload = function () {
    // 需配置公众号appId
    if (!(base.publicAppId && isWechat())) {
        return;
    }
    //获取当前页面地址
    let url = window.location.href;
    url = url.substring(0, url.indexOf("#"));
    //获取微信公众号SDK权限的签名、随机数、时间戳
    uni.$u.http.post("api/jWeixinConfig", {
        url: url
    }).then(res => {
        // 微信SDK配置
        jWeixin.config({
            debug: false,
            appId: base.publicAppId, // 必填，公众号的唯一标识
            timestamp: res.timestamp, // 必填，生成签名的时间戳
            nonceStr: res.noncestr, // 必填，生成签名的随机串
            signature: res.signature,// 必填，签名
            jsApiList: [
                'scanQRCode',
                "getLocation",
                "updateAppMessageShareData",
                "updateTimelineShareData",
                'onMenuShareAppMessage',  //旧的接口，即将废弃
                'onMenuShareTimeline', //旧的接口，即将废弃
				'checkIsSupportFaceDetect',//刷脸校验
				'requestWxFacePictureVerify'//刷脸
            ]
        });
        //设置分享内容
        setShare({});
    });
    //配置校验失败后执行
    jWeixin.error(function (res) {
        // config信息验证失败会执行error函数，如签名过期导致验证失败，具体错误信息可以打开config的debug模式查看，也可以在返回的res参数中查看，对于SPA可以在这里更新签名。
        console.log(res);
    });
};