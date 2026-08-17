<template>
	<view>
		<m-start-ad :list="list" url="/pages/index/index" :time="3" />
	</view>
</template>

<script>
	import {
		mapState,
		mapMutations
	} from 'vuex';
	export default {
		computed: {
			...mapState(['userInfo', 'memberRole'])
		},
		data() {
			return {
				list: [],
			}
		},
		
		onLoad(ops) {
			console.log("=splash=onload==",ops)
			this.getAdList();
			this.getConfig();
		},

		methods: {
			...mapMutations(['setRunMode']),
			
			getAdList() {
				let param = {
					roleCode: 'member',
					adPosition: 'start_ad'
				}
				if (this.memberRole == 'company') {
					param.roleCode='company';
				}
				this.$apis.getAdList({
					params: param
				}).then(res => {
					console.log('getAdList', res);
					this.list = res;
				});
			},
			
			async getConfig() {
				//运行模式
				let res = await this.$apis.getBaseConfig({
					params: {
						code: "runMode"
					}
				});
				if (res) {
					let runMode = Number(res.configValue);
					console.log("===runMode===",runMode);
					this.setRunMode(runMode);
					if(runMode==0){
						uni.hideTabBar();
					}
				}
				//地图秘钥
				let res2 = await this.$apis.getBaseConfig({
					params: {
						code: "map_key"
					}
				});
				if (res2) {
					console.log("mapData.key===",this.$base.mapData.key)
					let key = res2.configValue;
					this.$base.mapData.key=key;
					console.log("mapData.key===",this.$base.mapData.key)
				}
			},
		}
	}
</script>

<style>

</style>