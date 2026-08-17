<template>
	<view class="auth">
		<image class="qrcode" src="../../static/id-back.png"></image>
		<text>扫码关注公众号</text>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				content: "",
				userId: ''
			}
		},

		onLoad(options) {
			console.log("options===", options);
			this.content = options;
			this.userId = options.userId;
			this.code = options.code;
			const redirect_uri = encodeURIComponent(location.href);
			if (this.code) {
				//授权登录账号
				this.loginByCodeForGzh();
			} else {
				//未登录才需要
				let appId = "wx2e44a0988f1e973a";
				const redirect_uri = encodeURIComponent(location.href);
				let url =
					`https://open.weixin.qq.com/connect/oauth2/authorize?appid=${appId}&redirect_uri=${redirect_uri}&response_type=code&scope=snsapi_base&state=1#wechat_redirect`;
				location.href = url;
			}
		},

		methods: {

			//公众号授权登录
			async loginByCodeForGzh() {
				console.log("code===", this.code);
				let params = {
					code: this.code,
					userId: this.userId
				}
				let res = await this.$apis.loginByCodeForGzh(params);
				if (res) {

				}
			},
		}
	}
</script>

<style lang="scss">
	.auth {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		margin-top: 200upx;
	}

	.qrcode {
		width: 400upx;
		height: 400upx;
	}
</style>