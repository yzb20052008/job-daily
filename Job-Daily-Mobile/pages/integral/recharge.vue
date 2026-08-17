<template>
	<view class="content">
		<view class="recharge">
			<text class="title">充值金额：</text>
			<view class="money">
				<input type="digit" v-model="money" placeholder="请输入金额" @input="inputChange" />
				<text>元</text>
			</view>
			<view class="ratio">
				<text>可得积分</text>
				<text class="integral">{{integral}}</text>
				<text>（兑换比例 1:{{ratio}}）</text>
			</view>
		</view>

		<view class="pay">
			<view class="pay-item" v-for="(item,index) in pays" :key="index" @click="selectPayType(index,item)">
				<view class="left">
					<image :src="item.icon"></image>
					<text>{{item.title}}</text>
					<text v-if="item.showBalance" class="balance">（可用：¥{{account.balanceWithdraw}}）</text>
				</view>
				<text class="right yzb"
					:class="item.selected?'yzb-yuanxingxuanzhongfill':'yzb-yuanxingweixuanzhong'"></text>
			</view>
		</view>
		<view class="btn">
			<button @click="pay">开始充值</button>
		</view>
		<view class="warn">
			<text class="yzb yzb-tixing"></text>
			<text class="tips">
				充值过程中遇到任务问题可到客服中心联系我们
			</text>
		</view>
	</view>
</template>

<script>
	import {
		mapState,
	} from 'vuex';
	export default {
		computed: {
			...mapState(['userInfo'])
		},
		data() {
			return {
				money: '',
				integral: 0,
				ratio: 1,
				pays: [{
						id: "1",
						payType: "wxPay",
						title: '微信支付',
						icon: '/static/images/my/weix.png',
						selected: true,
						active: true,
						showBalance:false,
					},
					{
						id: "2",
						payType: "yePay",
						title: '余额支付',
						icon: '/static/images/my/qianbao.png',
						balance:0,
						selected: false,
						active: true,
						showBalance:true
					}
				],
				selectedPay: null,
				account:{
					balanceWithdraw:0,
				},
			}
		},

		onLoad() {
			this.selectedPay = this.pays[0];
			this.getBaseConfig();
			this.getAccount();
		},

		methods: {
			
			async getAccount() {
				let res = await this.$apis.getAccountDetail();
				if (res) {
					this.account = res;
				}
			},

			inputChange(e) {
				console.log("==inputChange==", e)
				console.log("==money==", this.money)
				this.integral = this.money * this.ratio;
			},

			getBaseConfig() {
				let params = {
					code: 'jf_recharge_ratio',
				}
				this.$apis.getBaseConfig({
					params: params
				}).then(res => {
					console.log('getBaseConfig', res);
					if (res) {
						this.ratio = Number(res.configValue);
					}
				});
			},

			selectPayType(index, item) {
				if (item.active == false) {
					uni.showToast({
						icon: 'none',
						title: "暂不支持"
					})
					return;
				}
				for (var i = 0; i < this.pays.length; i++) {
					this.pays[i].selected = false;
				}
				this.pays[index].selected = true;
				this.selectedPay = this.pays[index];
			},

			getPayTypeList() {
				this.$apis.getPayTypeList().then(res => {
					console.log('getPayTypeList', res);
					if (res) {
						res.forEach((item, index) => {
							if (index == 0) {
								item.selected = true
							} else {
								item.selected = false;
							}
						})
						this.pays = res;
					}
				});
			},


			pay() {
				let that=this;
				console.log("money==", this.money)
				if (!this.money) {
					uni.showToast({
						icon: 'none',
						title: "金额不能为空"
					})
					return;
				}
				if (this.selectedPay.payType == 'wxPay') {//微信支付
					this.minipay();
				} else if (this.selectedPay.payType == 'yePay') {//余额支付
					//判断余额是否足够
					if(this.account.balanceWithdraw < this.money){
						uni.showToast({
							icon: 'none',
							title: "余额不足"
						})
						return;
					}
					uni.showModal({
						title:"温馨提示",
						content: '确定使用余额充值积分',
						success(res) {
							if (res.confirm) {
								that.yePay();
							}
						}
					})
				}
			},
			
			async yePay(){
				let that = this;
				let params = {
					money: this.money,
				}
				console.log('-------params-------', params);
				let res = await this.$apis.yePayIntegral(params);
				if(res){
					uni.showToast({
						icon: 'none',
						title: "充值成功"
					})
					setTimeout(() => {
						uni.navigateBack(-1);
					}, 1000);
				}
			},

			async minipay() {
				let that = this;
				let params = {
					money: this.money,
				}
				console.log('-------params-------', params);
				let res = await this.$apis.miniPayIntegral({
					params: params
				});
				if (res) {
					console.log('--------res-------', res);
					var data = res;
					console.log(data);
					data = JSON.parse(data);
					console.log(typeof data);
					uni.requestPayment({
						provider: 'wxpay',
						timeStamp: data.timeStamp,
						nonceStr: data.nonceStr,
						package: data.package,
						signType: data.signType,
						paySign: data.paySign,
						success: function(res) {
							console.log('-------res-------');
							console.log(res);
							uni.showToast({
								icon: 'none',
								title: "充值成功"
							})
							setTimeout(() => {
								uni.navigateBack(-1);
							}, 1000);
						},
						fail: function(fail) {
							console.log('--------fail-------');
							console.log(fail);
							uni.showToast({
								icon: 'none',
								title: '支付失败，请重试'
							});
						}
					});
				}
			},
		}
	}
</script>

<style lang="scss">
	page {
		background-color: #f5f6fa;
	}

	.content {
		padding: 20upx;
	}

	.recharge {
		background-color: #ffffff;
		padding: 80upx 20upx;
		border-radius: 20upx;

		.title {
			font-weight: bold;
			font-size: 32upx;
		}

		.money {
			display: flex;
			flex-direction: row;
			align-items: center;

			input {
				font-size: 34upx;
				color: #D91E0D;
				font-weight: bold;
				width: 90%;
				text-align: center;
				height: 120upx;
				border-bottom: 1upx solid #eee;
			}

			text {
				font-weight: bold;
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
				color: #D91E0D;
			}
		}
	}

	.btn {
		margin-top: 150upx;

		button {
			background-color: $main-color;
			// background-color: #999;
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

	.pay {
		background-color: #fff;
		margin-top: 30upx;
		padding: 20upx;
		border-radius: 10upx;

		.pay-item {
			padding: 20upx 20upx;
			display: flex;
			flex-direction: row;
			align-items: center;
			justify-content: space-between;

			.left {
				display: flex;
				flex-direction: row;
				align-items: center;
				
				.balance{
					font-size: 14px;
					color: #D91E0D;
				}

				image {
					width: 45upx;
					height: 45upx;
				}

				text {
					margin-left: 15upx;
					color: #333;
				}
			}

			.right {
				font-size: 44upx;
				color: #666;
			}

			.yzb-yuanxingxuanzhongfill {
				color: #007aff;
			}

			.yzb-yuanxingweixuanzhong {
				color: #999;
			}

		}
	}
</style>