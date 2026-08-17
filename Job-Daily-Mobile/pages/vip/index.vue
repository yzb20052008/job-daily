<template>
	<view class="content">
		<view class="header">
			<view class="face">
				<image :src="userInfo.avatar"></image>
			</view>
			<view class="info" v-if="userInfo.token">
				<view class="name">
					<text class="username">{{ userInfo.nickname}}</text>
					<text class="driver" v-if="userInfo.ifDriver">司机</text>
					<!-- <view>
						<text class="yzb sex" :class="userInfo.sex==1?'yzb-nan':'yzb-nv'"></text>
					</view> -->
				</view>
				<view class="time" v-if="userInfo.vipLevel!='1'">未开通会员</view>
				<view class="time" v-else>{{$u.timeFormat(userInfo.vipEndTime, 'yyyy-mm-dd')}} 到期，续费有效期顺延</view>
			</view>
		</view>
		<view class="tj-sction">
			<view class="tj-item " :class="item.selected?'item-active':'item-normal'" v-for="(item,index) in items"
				:key="index" @click="selectGoods(index)">
				<text class="num">{{item.name}}</text>
				<view class="bottom">
					<text class="unit">¥</text>
					<text class="money">{{item.price}}</text>
				</view>
				<text class="days">{{item.remark}}</text>
			</view>
		</view>

		<!-- <view class="pay">
			<view class="pay-item" v-for="(item,index) in pays" :key="index" @click="selectPayType(index,item)">
				<view class="left">
					<image :src="item.icon"></image>
					<text>{{item.title}}</text>
				</view>
				<text class="right yzb"
					:class="item.selected?'yzb-yuanxingxuanzhongfill':'yzb-yuanxingweixuanzhong'"></text>
			</view>
		</view> -->

		<view class="equity">
			<text class="title">会员权益</text>
			<!-- <view class="equity-item">
				<text class="icon yzb yzb-gaoliangdu"></text>
				<view class="equity-info">
					<text class="info-title">显示靠前</text>
					<text class="info-desc">正常显示，靠前展示</text>
				</view>
			</view> -->
			<view class="equity-item">
				<text class="icon yzb yzb-lianxifangshi"></text>
				<view class="equity-info">
					<text class="info-title">开放联系方式</text>
					<text class="info-desc">成为会员后，货主可以直接给你打电话下单</text>
				</view>
			</view>
		</view>

		<view class="btn">
			<button @click="pay" v-if="userInfo.vipLevel<1">
				<text class="btn-name">
					开通会员
				</text>
				<text class="btn-money">
					（¥{{selectedItem.price}}）
				</text>
			</button>
			<button @click="pay" v-else>
				<text class="btn-name">
					会员续费
				</text>
				<text class="btn-money">
					（¥{{selectedItem.price}}）
				</text>
			</button>
			<view class="protocol">
				<text>购买即视为同意</text>
				<text class="member-protocol" @click="toNav('/pages/vip/protocol')">《会员服务协议》</text>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		mapState,
		mapGetters
	} from 'vuex';
	export default {
		computed: {
			...mapState(['userInfo']),
		},
		data() {
			return {
				items: [],

				pays: [{
						id: "1",
						payType: "wxPay",
						title: '微信支付',
						icon: '../../../static/kp/weix.png',
						selected: false,
						active: true,
					},
					// {
					// 	id: "2",
					// 	payType:"aliPay",
					// 	title: '支付宝支付',
					// 	icon: '../../../static/kp/zfb.png',
					// 	selected: false,
					// 	active: false,
					// },
					{
						id: "3",
						payType: "jfPay",
						title: '积分支付',
						icon: '../../../static/kp/jf.png',
						selected: true,
						active: true,
					}
				],

				selectedItem: null,
				selectedPay: null,

				show: false,
				title: '温馨提示',
				content: '购买成功无法取消，确定支付？',
			}
		},

		onLoad() {
			this.selectedPay = this.pays[1];
			this.getVipList();
			// this.getPayTypeList();
		},

		onShow() {
			uni.getProvider({
				service: 'payment',
				success: function(res) {
					console.log("res===", res);
					uni.setStorageSync('providerpayment', res.provider[0]);
				}
			});
		},

		methods: {

			getVipList() {
				this.$apis.getVipList().then(res => {
					console.log('getVipList', res);
					if (res) {
						res.forEach((item, index) => {
							if (index == 0) {
								item.selected = true
								this.selectedItem = item;
							} else {
								item.selected = false;
							}
						})
						this.items = res;
					}
				});
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

			selectGoods(index) {
				for (var i = 0; i < this.items.length; i++) {
					this.items[i].selected = false;
				}
				this.items[index].selected = true;
				this.selectedItem = this.items[index];
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

			toNav(url) {
				uni.navigateTo({
					url: url
				});
			},

			confirm() {
				this.show = false;
				this.submit();
			},

			pay() {

				let that = this;
				uni.showModal({
					title: '提示',
					content: '确定支付？',
					success: res => {
						if (res.confirm) {
							// #ifdef MP-WEIXIN
							that.submit();
							// #endif
							// #ifdef MP-TOUTIAO
							that.submitDy();
							// #endif
						}
					}
				});

			},

			async submit() {
				let that = this;
				let params = {
					vipId: this.selectedItem.id,
				}
				console.log('-------params-------', params);
				let res = await this.$apis.getWxMiniPay({
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
								title: "购买成功"
							})
							//更新用户信息
							that.getUserInfo();
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

			async submitDy() {
				let that = this;
				let params = {
					vipId: this.selectedItem.id,
				}
				console.log('-------params-------', params);
				let res = await this.$apis.getDyMiniPay({
					params: params
				});
				if (res) {
					console.log('--------res-------', res);
					var data = res;
					console.log(data);
					data = JSON.parse(data);
					console.log(typeof data);
					uni.requestPayment({
						provider: 'toutiao',
						orderInfo: data,
						service: 5,
						// _debug: 1,//调试字段
						success: function(res) {
							console.log('-------res-------');
							console.log(res);
							uni.showToast({
								icon: 'none',
								title: "购买成功"
							})
							//更新用户信息
							that.getUserInfo();
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

			// 获取用户信息
			async getUserInfo() {
				let userInfo = await this.$apis.getUserInfo();
				this.$store.commit('SET_USERINFO', userInfo);
			},
		}
	}
</script>

<style lang="scss">
	page {
		background-color: #f5f6fa;
	}

	.header {
		display: flex;
		flex-direction: row;
		align-items: center;
		width: 100%;
		padding: 30upx 20upx 80upx 20upx;
		box-sizing: border-box;
		background-color: $main-color;

		.face {
			image {
				width: 150upx;
				height: 150upx;
				border-radius: 75upx;
			}
		}

		.info {
			display: flex;
			flex-direction: column;
			padding-left: 30upx;
			box-sizing: border-box;
			align-items: flex-start;
			width: 100%;

			.name {
				display: flex;
				flex-direction: row;
				align-items: center;
				margin-bottom: 10upx;
				width: 100%;
			}

			.username {
				color: #fff;
				font-weight: bold;
				font-size: 36upx;
				margin-right: 10rpx;
			}

			.driver {
				margin-left: 20upx;
				color: #36343c;
				font-weight: bold;
				background-color: #f7d680;
				font-size: 26upx;
				border-radius: 20upx;
				padding: 4upx 10upx;
			}

			.yzb-nan {
				color: #f56c6c;
				font-weight: bold;
			}

			.yzb-nv {
				font-weight: bold;
				color: #f56c6c;
			}

			.time {
				// margin-top: 15upx;
				display: flex;
				height: 40upx;
				color: #ccf783;
				font-size: 26upx;
			}
		}
	}

	%flex-center {
		display: flex;
		flex-direction: column;
		justify-content: center;
		align-items: center;
	}

	%section {
		display: flex;
		flex-direction: row;
		justify-content: space-around;
		align-content: center;
		background: #fff;
		border-radius: 10upx;
	}

	.tj-sction {
		@extend %section;
		border-radius: 20upx;
		width: 96%;
		padding: 5% 2%;
		box-sizing: border-box;
		margin: 0 auto;
		margin-top: -50upx;

		.tj-item {
			@extend %flex-center;
			flex-direction: column;
			width: 27%;
			height: 250upx;
			font-size: 26upx;
			color: #75787d;
			border-radius: 10upx;
		}

		.item-normal {
			background-color: #f5f6fa;
		}

		.item-active {
			background-color: #F4CE98;
		}

		.num {
			font-size: 32upx;
			color: #666;
			margin-bottom: 10upx;
		}

		.days {
			font-size: 26upx;
			color: #999;
			margin-bottom: 25upx;
			margin-top: 10upx;
		}

		.unit {
			font-size: 26upx;
			color: #dd524d;
		}

		.money {
			margin-top: 30upx;
			font-size: 42upx;
			font-weight: bold;
			color: #dd524d;
		}
	}

	.pay {
		background-color: #fff;
		margin: 30upx 20upx;
		padding: 20upx;
		border-radius: 10upx;

		.pay-item {
			padding: 10upx 20upx;
			display: flex;
			flex-direction: row;
			align-items: center;
			justify-content: space-between;

			.left {
				display: flex;
				flex-direction: row;
				align-items: center;

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
				color: #12ae85;
			}

			.yzb-yuanxingweixuanzhong {
				color: #999;
			}

		}
	}

	.equity {
		background-color: #fff;
		margin: 30upx 20upx;
		padding: 20upx;
		border-radius: 10upx;
		display: flex;
		flex-direction: column;

		.title {
			font-weight: bold;
			padding: 10upx 0 20upx 0;
		}

		.equity-item {
			display: flex;
			flex-direction: row;
			align-items: center;
			margin-top: 20upx;
		}

		.icon {
			color: $main-color;
			font-size: 60upx;
		}

		.equity-info {
			margin-left: 20upx;
			display: flex;
			flex-direction: column;
			line-height: 1.6;

			.info-title {
				color: #333;
				font-weight: bold;
			}

			.info-desc {
				font-size: 26upx;
				color: #999;
			}
		}
	}

	.btn {
		display: flex;
		flex-direction: column;
		align-items: center;
		margin-top: 100upx;
		padding-bottom: 50upx;

		button {
			margin: 0 auto;
			width: 94%;
			height: 85upx;
			border-radius: 20upx;
			background-color: $main-color;
			// background-image: linear-gradient(10deg, #007AFF, #005ADF, #007AFF);
			color: #ffffff;
			align-items: center;
			display: flex;
			justify-content: center;
		}

		.btn-money {
			font-size: 34upx;
		}

		.btn-name {
			font-size: 34upx;
		}

		.protocol {
			font-size: 26upx;
			margin-top: 10upx;
			color: #999;

			.member-protocol {
				color: $main-color;
			}
		}

	}
</style>