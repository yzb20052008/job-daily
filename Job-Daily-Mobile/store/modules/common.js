export const state = {
	PrimaryColor: '#fe461d', //主题色
	loginPopupShow: false, //控制是否打开登录弹窗
	loadingShow: false, //加载动画
	chatScenesInfo: {}, //扫码参数
	locateInformation: {}, //定位信息
	lastLocation: {}, //上一次定位
	runMode: 1, //运行模式，1-正式，0-调试
	memberRole: 'member', //当前-上次登录使用的角色。
	active: 'index',
	animate: 'zoomIn',
	tabbars: []
};
//缓存浏览器的数据名称
const cacheNameList = ["userInfo", "memberRole", "lastLocation"];
let clearTime;
export const mutations = {
	//取出缓存数据（打开APP就取出）
	setCacheData(state) {
		for (let name of cacheNameList) {
			let data;
			// #ifndef H5
			data = uni.getStorageSync(name);
			// #endif
			// #ifdef H5
			data = sessionStorage.getItem(name) || localStorage.getItem(name);
			// #endif
			if (data) {
				// #ifdef H5
				try {
					data = JSON.parse(data);
				} catch (e) {}
				// #endif
				state[name] = data;
			}
		}
	},
	setPrimaryColor(state, data) {
		state.PrimaryColor = data;
		// #ifdef H5
		window.sessionStorage.setItem('PrimaryColor', JSON.stringify(state.PrimaryColor));
		// #endif
		// #ifndef H5
		uni.setStorageSync('PrimaryColor', state.PrimaryColor);
		// #endif
	},
	setLoginPopupShow(state, data) {
		state.loginPopupShow = data
	},
	setLoadingShow(state, data) {
		state.loadingShow = data
	},
	setChatScenesInfo(state, data) {
		state.chatScenesInfo = data
	},
	setLocateInformation(state, data) {
		state.locateInformation = data
	},
	setLastLocation(state, data) {
		state.lastLocation = data
		uni.setStorageSync("lastLocation", data);
	},
	setRunMode(state, data) {
		state.runMode = data
	},
	SET_TABBARS(state, data) {
		state.tabbars = data
	},
	SET_ACTIVE(state, data) {
		state.active = data
	},
	setMemberRole(state, data) {
		state.memberRole = data;
		uni.setStorageSync("memberRole", data);
	},
};
export const actions = {

};