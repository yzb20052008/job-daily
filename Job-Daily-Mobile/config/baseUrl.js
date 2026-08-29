import {
	getTabbarHeight
} from '@/plugins/utils';
let baseUrl = "";
if (process.env.NODE_ENV === 'development') {
	// 开发环境
	// baseUrl = 'https://lgapi.qinkonglan.cn/'
	baseUrl = 'http://192.168.31.60:8081'
	
} else if (process.env.NODE_ENV === 'production') {
	// 生产环境
	baseUrl = 'https://lgapi.qinkonglan.cn/'
} 
let shareUrl = "https://lgs.qinkonglan.cn"; //分享二维码打开小程序网址
let systemInfo = {
	...getTabbarHeight(),
	// #ifdef MP-ALIPAY
	navBarH: uni.getSystemInfoSync().statusBarHeight + uni.getSystemInfoSync().titleBarHeight, //菜单栏总高度--单位px
	titleBarHeight: uni.getSystemInfoSync().titleBarHeight, //标题栏高度--单位px
	// #endif
	// #ifndef MP-ALIPAY
	navBarH: uni.getSystemInfoSync().statusBarHeight + 44, //菜单栏总高度--单位px
	titleBarHeight: 44, //标题栏高度--单位px
	// #endif
};
// 平台
// #ifdef MP-WEIXIN
systemInfo.platform = 'weixin'
// #endif
// #ifdef MP-ALIPAY
systemInfo.platform = 'alipay'
// #endif
// #ifdef MP-TOUTIAO
systemInfo.platform = 'toutiao'
// #endif
// #ifdef APP-PLUS
systemInfo.platform = 'plus'
// #endif
console.log(systemInfo, 'systemInfo')
const courtConfig = {
	publicAppId: '', //公众号appId
	baseUrl: baseUrl, //域名
	shareUrl: shareUrl,
	systemInfo: systemInfo, //系统信息
	mapData: {
		// 仅作兜底；正式 key 由 splash 拉取 base_config.map_key，逆地理/搜索走后台 /api/map/*
		key: '',
		sk: '',
	},
	share: {
		title: '小蓝零工', //分享标题
		desc: "小蓝零工招聘系统", //分享详情
		link: "", // 分享链接
		imgUrl: "", // 分享图
	}
};
export default Object.assign({}, courtConfig);