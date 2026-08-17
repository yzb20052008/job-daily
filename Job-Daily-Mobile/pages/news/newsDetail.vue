<template>
	<view class="page">
		<!-- 公共组件-每个页面必须引入 -->
		<public-module></public-module>
		<view class="content">
			<text class="title">{{ detail.title }}</text>
			<text class="publish-time">{{ detail.createTime }}</text>
			<!-- <u-parse :content="detail.content"></u-parse> -->
			<!-- <u-parse :content="detail.content" :tagStyle="style"></u-parse> -->
			<rich-text style="word-wrap: break-word;word-break: break-all;white-space: pre-wrap;" :nodes="detail.content" selectable="true"></rich-text>
		</view>
	</view>
</template>

<script>
import { mapState, mapMutations } from 'vuex';
export default {
	data() {
		return {
			detail: '',
			id: ''
		};
	},
	//第一次加载
	onLoad(options) {
		if (options.id) {
			this.id = options.id;
		}
		this.getNewsDetail();
	},
	//页面显示
	onShow() {},
	//方法
	methods: {
		
		getNewsDetail() {
			let params = {
				id: this.id
			}
			this.$apis
				.getNewsDetail({
					params: params
				})
				.then(res => {
					console.log('getNewsDetail', res);
					if (res) {
						this.detail = res;
						if(this.detail.content){
							let content = this.detail.content.replace(/\<p/gi,'<p style="padding-bottom:5px;line-height:24px;color:#333333;font-size: 14px;"');
							this.detail.content = content.replace(/\<img/gi, '<img style="width:100%;height:auto;display:block;" ');	
						}	
					}
				})
				.catch(err => {
					console.log(err, 'catch');
				});
		},
		
	}
};
</script>
<style lang="scss" scoped>
.page {
	min-height: 100vh;
	background-color: #fff;
	// padding: 24rpx;
	font-size: 32rpx;
	color: #666;
	line-height: 1.8;
}

.content {
	padding: 0 20upx 20upx 20upx;
	display: flex;
	flex-direction: column;
	background-color: #fff;
	.title {
		margin: 0 auto;
		font-weight: bold;
		font-size: 32upx;
		padding: 20upx 0;
		color: #333;
	}
	.publish-time {
		padding-bottom: 30upx;
		margin: 0 auto;
		font-size: 26upx;
		color: $uni-text-color-grey;
	}
}
</style>
