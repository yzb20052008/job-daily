<template>
	<view class="content">
		<view class="account">
			<view class="item">
				<text class="money">{{account.balance}}</text>
				<text>账户余额</text>
			</view>
			<!-- <view class="bottom">
				<view class="btn" @click="toRule">账户明细</view>
				<view class="btn" @click="toWithdraw">申请提现</view>
			</view> -->
			<view class="bottom">
				<view class="btn" @click="">可用 ¥{{account.balanceWithdraw}}</view>
				<view class="btn" @click="">冻结 ¥{{account.balanceFrozen}}</view>
			</view>
		</view>
		<view class="list">
			<view class="li"  hover-class="hover" @click="toWithdraw">
				<view class="left">
					<view class="icons">
						<image src="../../static/images/my/zhuanzhang.png"></image>
					</view>
					<view class="text">我要提现</view>
				</view>
				<view class="right">
					<text class="right-text">¥{{account.balanceWithdraw}}</text>
					<text class="yzb yzb-next"></text>
				</view>
			</view>
		</view>
		
		<view class="list" v-for="(list, list_i) in severList" :key="list_i">
			<view class="li" v-for="(li, li_i) in list" @tap="toPage(list_i, li_i)"
				v-bind:class="{ noborder: li_i == list.length - 1 }" hover-class="hover" :key="li.name">
				<view class="left">
					<view class="icons">
						<!-- <text class="yzb" :class="li.icon" :style="{ color: li.color }"></text> -->
						<image :src="li.icon"></image>
					</view>
					<view class="text">{{ li.name }}</view>
				</view>
				<view class="right">
					<text class="right-text">{{li.text}}</text>
					<text class="yzb yzb-next" v-if="li.show"></text>
				</view>
			</view>
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
				account: {
					balanceWithdraw: 0,
					totalMoney: 0,
				},
				severList: [
					// [{
					// 		name: '本月收入',
					// 		text: '￥0.00',
					// 		show: false,
					// 		icon: '/static/images/my/qianbao.png',
					// 		color: '#FF9552',
					// 		url: null
					// 	},
					// 	// { name: '提现申请', show: true, icon: '/static/images/my/zhuanzhang.png', color: '#FCB138', url: '/pages/finance/withdrawLog' },
					// ],
					[
						{
							name: '工资支出',
							show: true,
							icon: '/static/images/my/gjj.png',
							color: '#FCB138',
							url: '/pages/finance/records'
						},
						{
							name: '工资收入',
							show: true,
							icon: '/static/images/my/money.png',
							color: '#FCB138',
							url: '/pages/finance/records'
						},
						{
							name: '提现记录',
							show: true,
							icon: '/static/images/my/zhuanzhang.png',
							color: '#FCB138',
							url: '/pages/finance/withdrawLog'
						},
						{
							name: '交易明细',
							show: true,
							icon: '/static/images/my/list.png',
							color: '#FF8948',
							url: '/pages/finance/records'
						},
						{
							name: '提现规则',
							show: true,
							icon: '/static/images/my/help.png',
							color: '#FF8948',
							url: '/pages/finance/rule'
						},
					]
				],
			}
		},

		onShow() {
			this.getAccount();
		},

		methods: {
			async getAccount() {
				let res = await this.$apis.getAccountDetail();
				console.log('res=====', res);
				if (res) {
					this.account = res;
				}
			},

			//用户点击列表项
			toPage(list_i, li_i) {
				let that = this;
				if (this.severList[list_i][li_i].name == '工资收入') {
					uni.$u.route(this.severList[list_i][li_i].url, {
						tradeType: 1
					});
					return;
				}else if (this.severList[list_i][li_i].name == '工资支出') {
					uni.$u.route(this.severList[list_i][li_i].url, {
						tradeType: 2
					});
					return;
				}else if (this.severList[list_i][li_i].name == '提现记录') {
					uni.$u.route(this.severList[list_i][li_i].url, {
						tradeType: 4
					});
					return;
				}
				uni.$u.route(this.severList[list_i][li_i].url);
			},

			toWithdraw() {
				//判断是否实名认证
				if(this.userInfo.realNameAuth!=1){
					uni.$u.toast('请先完成实名认证');
					return;
				}
				uni.$u.route("/pages/finance/withdraw");
			},

			toRule() {
				uni.$u.route("/pages/finance/rule");
			}


		}
	}
</script>

<style lang="scss">
	page {
		background-color: #f5f5f5;
	}

	.account {
		display: flex;
		flex-direction: column;
		background-color: $main-color;

		.item {
			padding: 30upx;
			display: flex;
			flex-direction: column;
			align-items: center;
			justify-content: center;

			text {
				color: #ffffff;
			}

			.money {
				font-size: 60upx;
				::before{
					content: '¥';
					font-size: 40upx;
				}
			}
		}

		.bottom {
			display: flex;
			flex-direction: row;
			justify-content: space-between;

			.btn {
				padding: 20upx 0;
				display: flex;
				align-items: center;
				justify-content: center;
				color: #ffffff;
				width: 50%;
				background: rgba(255, 255, 255, 0.15);
				font-size: $uni-font-size-lg;
			}

			:first-child {
				border-right: solid 1upx #FF822B;
			}
		}
	}

	.list {
		width: 100%;
		border-bottom: solid 26upx #f5f5f5;
		display: flex;
		flex-direction: column;
		background-color: #ffffff;

		.li {
			width: 100%;
			height: 100upx;
			padding: 0 4%;
			border-bottom: solid 1upx #ebeef5;
			display: flex;
			flex-direction: row;
			align-items: center;
			justify-content: space-between;

			&.noborder {
				border-bottom: 0;
			}

			.left {
				display: flex;
				flex-direction: row;
				align-items: center;
			}

			.icons {
				display: flex;
				align-items: center;
				margin-right: 20upx;
				align-items: center;

				text {
					font-size: 40upx;
				}

				image {
					width: 40upx;
					height: 40upx;
				}
			}

			.text {
				// padding-left: 20upx;
				width: 100%;
				color: #666;
			}

			.to {
				flex-shrink: 0;
				width: 40upx;
				height: 40upx;
			}

			.icon-next {
				font-size: 34upx;
				color: #999;
			}
		}

		.right {
			width: 140upx;
			display: flex;
			flex-direction: row;
			align-items: center;
			justify-content: flex-end;

			.right-text {
				color: #666;
				margin-right: 10upx;
			}
		}
	}
</style>