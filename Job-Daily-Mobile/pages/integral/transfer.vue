<template>
	<view class="content">
		<view class="recharge">
			<text class="title">到账账户：</text>
			<view class="money">
				<input class="account" v-model="account" type="digit" placeholder="请输入到账手机号" />
				<!-- <text class="yzb yzb-zhucetianjiahaoyou" @click="selectFriend"></text> -->
			</view>
			<text class="title">转赠数量：</text>
			<view class="money">
				<input class="integral" v-model="integral" type="digit" placeholder="请输入转赠数量" />
			</view>
			<view class="ratio">
				<text>当前可赠积分:</text>
				<text class="integral">{{userInfo.integral}}</text>
				<text class="all" @click="allTransfer">全部赠送</text>
			</view>

			<view class="btn">
				<button @click="transfer">转赠</button>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		mapState,
		mapMutations
	} from 'vuex';
	export default {
		computed: {
			...mapState(['userInfo'])
		},
		data() {
			return {
				account: '',
				integral:null,
			}
		},

		onLoad() {
			let that=this;
			uni.$on('selectedFriends', function(data) {
				console.log('监听到事件来自返回的参数：', data);
				let item=data[0];
				that.account=item.userPhone;
			})
		},
		
		onUnload() {
			uni.$off("selectedFriends")
		},

		methods: {
			...mapMutations(['setUserInfo']),
			
			selectFriend() {
				uni.navigateTo({
					url: '/pages/friends/selectFriend'
				})
			},
			
			allTransfer(){
				this.integral=this.userInfo.integral;
			},
			
			async transfer(){
				if(!this.account){
					uni.showToast({
						icon:'error',
						title:"到账账户不能为空"
					})
					return;
				}
				if(!this.integral){
					uni.showToast({
						icon:'error',
						title:"转赠积分不能为空"
					})
					return;
				}
				if(this.account == this.userInfo.phone){
					uni.showToast({
						icon:'error',
						title:"不能转赠给自己"
					})
					return;
				}
				let params={
					integral:this.integral,
					userPhone:this.account
				}
				let res = await this.$apis.integralTransfer(params);
				console.log(res);
				if (res) {
					this.getUserInfo();
					uni.showToast({
						icon:'success',
						title:"转赠成功"
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
	
	.content{
		padding: 20upx;
	}

	.recharge {
		display: flex;
		flex-direction: column;
		margin-top: 20upx;
		background-color: #ffffff;
		padding: 20upx;
		border-radius: 20upx;

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
				border-bottom: 1upx solid #e7e7e7;
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

			.integral {
				margin: 0 10upx;
			}

			.all {
				margin-left: 20upx;
				color: $main-color;
			}
		}

		.btn {
			margin-top: 150upx;
			padding-bottom: 50upx;

			button {
				background-color: $main-color;
				color: #ffffff;
			}
		}

		.warn {
			margin-top: 30upx;
			font-size: 28upx;

			.yzb {
				color: #f0ad4e;
				margin: 0 10upx;
				margin-top: 2upx;
			}

			.tips {
				color: #DE868F;
			}
		}

	}
</style>