<template>
	<view class="content">
		<view class="recharge">
			<text class="title">兑换码：</text>
			<view class="money">
				<input class="integral" v-model="code" placeholder="请输入兑换码" />
			</view>
			<view class="ratio">
				<text>温馨提示：
					1、一个秘钥只能兑换一次，一旦兑换成功，将无法进行撤回。
					2、卡券请在对应有效期内使用，不在有效期将无法兑换。
					3、卡券没有绑定用户，可以任意用户进行兑换，请保存好自己的卡券兑换码。
				</text>
			</view>
			<view class="btn">
				<button @click="transfer">兑换</button>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		mapState
	} from 'vuex';
	export default {
		data() {
			return {
				code: '',
			}
		},
		methods: {
			
			async transfer(){
				if(!this.code){
					uni.showToast({
						icon:'error',
						title:"兑换码不能为空"
					})
					return;
				}
				let params={
					cardNo:this.code
				}
				let res = await this.$apis.integralExchange(params);
				console.log(res);
				if (res) {
					this.getUserInfo();
					uni.showToast({
						icon:'success',
						title:"兑换成功"
					})
					setTimeout(() => {
						uni.navigateBack(-1);
					}, 1000);
				}
				
			},
			
			getUserInfo() {
				this.$apis
					.getUserInfo()
					.then(res => {
						console.log("getUserInfo", res);
						if (res) {
							this.$store.commit('SET_USERINFO', res);
						}
					})
					.catch(err => {
						console.log(err, 'catch');
					});
			},
		}
	}
</script>

<style lang="scss">
	page {
		background-color: #f5f6fa;
	}

	.recharge {
		display: flex;
		flex-direction: column;
		margin-top: 20upx;
		background-color: #ffffff;
		padding: 20upx;

		.title {
			font-weight: bold;
			font-size: 32upx;
			margin-top: 20upx;
		}

		.money {
			display: flex;
			flex-direction: row;
			align-items: center;

			input {
				font-size: 30upx;
				width: 90%;
				text-align: center;
				height: 120upx;
				border-bottom: 1upx solid #eee;
			}

			.integral {
				font-size: 30upx;
			}

			text {
				font-size: 40upx;
				color: $main-color;
			}
		}

		.ratio {
			margin-top: 30upx;
			color: #999;
			display: flex;
			flex-direction: row;
			align-items: center;
			font-size: 28upx;
			line-height: 1.8;
		}

		.btn {
			margin-top: 100upx;
			padding-bottom: 50upx;

			button {
				background-color: $main-color;
				color: #ffffff;
			}
		}
	}
</style>