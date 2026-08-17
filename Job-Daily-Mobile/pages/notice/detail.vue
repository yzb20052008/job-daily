<template>
	<view class="page">
		<!-- 公共组件-每个页面必须引入 -->
		<public-module></public-module>
		<view class="content">
			<text class="title">{{ detail.title }}</text>
			<text class="publish-time">{{ detail.createTime }}</text>
			<u-parse :content="detail.content"></u-parse>
			<!-- <u-parse :content="detail.content" :tagStyle="style"></u-parse> -->
			<!-- <rich-text style="word-wrap: break-word;word-break: break-all;white-space: pre-wrap;" :nodes="detail.content" selectable="true"></rich-text> -->
		</view>
	</view>
</template>

<script>
import { mapState, mapMutations } from 'vuex';
import { getNoticeDetail } from '@/config/api.js';
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
		if (options.title) {
			uni.setNavigationBarTitle({
				title: options.title
			});
		}
		this.init();
	},
	//页面显示
	onShow() {},
	//方法
	methods: {
		init() {
			let httpData = {
				id: this.id
			};
			getNoticeDetail({ params: httpData, custom: { isFactory: true } })
				.then(res => {
					console.log(res, 'getArticle');
					if (res) {
						this.detail = res;
					}
				})
				.catch(err => {
					console.log(err, 'catch');
				});
		}
	}
};
</script>
<style lang="scss" scoped>
.page {
	min-height: 100vh;
	background-color: #fff;
	padding: 24rpx;
	font-size: 32rpx;
	color: #666;
	line-height: 1.6;
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
