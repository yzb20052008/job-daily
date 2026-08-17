<script>
    import base from "@/config/baseUrl";
    import store from "@/store";
    // #ifdef APP-PLUS
    import APPUpdate from '@/uni_modules/zhouWei-APPUpdate/js_sdk/appUpdate';
    // #endif
    import { mpUpData,scene } from '@/config/common';
	import { mapState, mapMutations } from 'vuex';
	export default {
		computed: {
			...mapState(['userInfo'])
		},
		globalData: {
			imService: null,
		},
		
		onLaunch(e) {
			//取出缓存数据
			store.commit('setCacheData');
            // #ifdef APP-PLUS
            APPUpdate() //检测app更新
            // #endif
		},
		onShow(e) {
			// #ifdef MP
			scene(e) //获取二维码信息
			mpUpData() //检测小程序更新
			// #endif
		},
		onHide() {
			console.log('App Hide')
		}
	}
</script>

<style lang="scss">
/* #ifndef APP-NVUE */
@import "@/uni_modules/uview-ui/index.scss";
@import './style/iconfont.scss';
@import "./style/common.scss";
@import "@/uni_modules/uview-ui/libs/css/flex.scss";
@import './static/yzb/iconfont.css'; 
// page {
//     background-color: #f5f6fa;
// }
// 隐藏滚动条
::-webkit-scrollbar {
    width: 0;
    height: 0;
    color: transparent;
}
/* #endif */
</style>
