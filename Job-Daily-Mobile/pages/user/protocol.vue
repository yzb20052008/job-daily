<template>
	<view class="page">
		<!-- 公共组件-每个页面必须引入 -->
		<public-module></public-module>
        <u-parse :content="content"></u-parse>
	</view>
</template>

<script>
import { mapState, mapMutations } from 'vuex';
import {  getArticle } from '@/config/api.js';
export default {
	data() {
		return {
            content: "",
			code:'agreement'
		};
	},
	//第一次加载
	onLoad(options) {
		if(options.code){
			this.code=options.code;
		}
		if(options.title){
			uni.setNavigationBarTitle({
				title:options.title
			})
		}
		this.init();
	},
	//页面显示
	onShow() {},
	//方法
	methods: {
		
		init(){
			let httpData={
				categoryCode: this.code,
			}
			getArticle({params: httpData,  custom: {isFactory:true}}).then((res) => {
				if(res){
					this.content=res.content;
				}
			}).catch((err) =>{
				console.log(err,'catch')
			})
		}
	}
};
</script>
<style lang="scss" scoped>
.page{
    min-height: 100vh;
    background-color: #fff;
    padding: 24rpx;
    font-size: 32rpx;
    line-height: 1.6;
}
</style>
