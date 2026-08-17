<template>
	<view>
		<gjsSelectCity :scrollHeight="scrollHeight" :cityCode="cityInfo.cityCode" :countyCode="cityInfo.areaCode" @select="select"></gjsSelectCity>
	</view>
</template>

<script>
	import { mapState,mapMutations} from 'vuex';
	import gjsSelectCity from '@/components/gjs-selectCity/gjs-selectCity.vue'
	export default {
		components: {
			gjsSelectCity
		},
		computed: {
			...mapState(['userInfo', 'locateInformation'])
		},
		data() {
			return {
				scrollHeight: 800,
				cityInfo:{},
			}
		},
		
		mounted() {
			this.cityInfo=this.locateInformation.cityInfo;
		},
		
		onLoad(options) {
			this.scrollHeight = uni.getSystemInfoSync().windowHeight;
			console.log("this.cityInfo=",this.locateInformation.cityInfo)
		},
		
		methods: {
			select(e) {
				console.log('选中的地区', e);
				let pages = getCurrentPages(); //获取page
				let prevPage = pages[pages.length - 2]; //上一个页面（父页面）
				console.log('上一页参数', prevPage);
				prevPage.$vm.cityInfo = e; //修改上一页data里面的地址
				uni.navigateBack({
					delta: 1
				});
			}
		}
	}
</script>

<style>

</style>
