<template>
	<view class="page">
		<public-module></public-module>
		<u-parse :content="content"></u-parse>
	</view>
</template>

<script>
	import {
		getArticle
	} from '@/config/api.js';
	export default {
		data() {
			return {
				content: "",
				code: 'realname'
			};
		},

		onLoad(option) {
			this.init();
		},

		methods: {
			init() {
				let httpData = {
					categoryCode: this.code,
				}
				getArticle({
					params: httpData,
					custom: {
						isFactory: true
					}
				}).then((res) => {
					if (res) {
						this.content = res.content;
					}
				}).catch((err) => {
					console.log(err, 'catch')
				})
			},
			async getAbouts() {
				let data = await this.$apis.getHelpDetail({
					categoryCode: 'integral'
				});
				console.log('data===', data);
				if (data) {
					this.aboutData = data;
					let desc = data.content.replace(/\<p/gi,
						'<p style="padding-bottom:5px;line-height:24px;color:#333333;font-size: 14px;"');
					this.aboutData.content = desc.replace(/\<img/gi,
						'<img style="width:100%;height:auto;display:block;" ');
				}
			}
		}
	};
</script>

<style>
	.page {
		min-height: 100vh;
		background-color: #fff;
		padding: 24rpx;
		font-size: 32rpx;
		line-height: 1.6;
	}
</style>