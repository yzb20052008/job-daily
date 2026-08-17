<template>
	<view class="content">
		<view class="account">
			<view class="item">
				<text class="money">{{userInfo.integral}}</text>
				<text>可用积分</text>
			</view>
			<view class="bottom">
				<view class="btn border-right">
					<!-- <text class="integral-number">{{userInfo.totalIntegral-userInfo.integral}}</text> -->
					<text class="integral-title">已用： {{userInfo.totalIntegral-userInfo.integral}}</text>
				</view>
				<view class="btn border-right">
					<!-- <text class="integral-number">{{userInfo.totalIntegral}}</text> -->
					<text class="integral-title">累计：{{userInfo.totalIntegral}}</text>
				</view>
				<!-- <view class="btn">
					<text class="integral-number">0</text>
					<text class="integral-title">充值积分</text>
				</view> -->
				<!-- <view class="btn">
					<text class="integral-number">{{userInfo.totalIntegral}}</text>
					<text class="integral-title">累计积分</text>
				</view> -->
			</view>
		</view>
		<view class="list"  v-for="(list, list_i) in severList" :key="list_i">
			<view class="li" v-for="(li, li_i) in list" @tap="toPage(list_i, li_i)" v-bind:class="{ noborder: li_i == list.length - 1 }" hover-class="hover" :key="li.name">
				<view class="left">
					<view class="icons" >
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
		mapMutations
	} from 'vuex';
	export default {
		computed: {
			...mapState(['userInfo'])
		},
		data() {
			return {
				account:{
					balanceWithdraw:0,
					totalMoney:0,
				},
				severList: [
					[
						{ name: '积分充值', show: true, icon: '/static/images/my/money.png', color: '#FF9552', url:'/pages/integral/recharge' },
						// { name: '积分转赠', show: true, icon: '/static/images/my/zhuanzhang.png', color: '#FCB138', url: '/pages/integral/transfer' },
						// { name: '卡券兑换', show: true, icon: '/static/images/my/qianbao.png', color: '#FCB138', url: '/pages/integral/exchange' },
					],
					[
						{ name: '积分明细', show: true, icon: '/static/images/my/list.png', color: '#FF8948', url: '/pages/integral/records' },
						{ name: '积分规则', show: true, icon: '/static/images/my/help.png', color: '#FF8948', url: '/pages/integral/rules' },
					]
				],
			}
		},
		
		onShow() {
			this.getUserInfo();
		},
		
		methods: {
			...mapMutations(['setUserInfo']),
			
			getUserInfo() {
				this.$apis
					.getUserInfo()
					.then(res => {
						console.log('getUserInfo', res);
						if (res) {
							this.setUserInfo(res);
						}
					})
					.catch(err => {
						console.log(err, 'catch');
					});
			},
			
			//用户点击列表项
			toPage(list_i, li_i) {
				uni.navigateTo({
					url:this.severList[list_i][li_i].url
				})
			},
		}
	}
</script>

<style lang="scss">
	
	page{
		background-color: #F5F5F5;
		height: 100vh;
	}

.account{
	display: flex;
	flex-direction: column;
	background-color: $main-color;
	.item{
		padding: 30upx;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		text{
			color: #ffffff;
		}
		.money{
			font-size: 60upx;
		}
	}
	.bottom{
		display: flex;
		flex-direction: row;
		justify-content: space-between;
		.btn{
			padding: 15upx 0;
			display: flex;
			flex:1;
			flex-direction: column;
			align-items: center;
			justify-content: center;
			color: #ffffff;
			background: rgba(255, 255, 255, 0.15);
			font-size: 32upx;
		}
		.integral-title{
			font-size: 30upx;
			color: #F5F5F5;
		}
		
		.integral-number{
			font-size: 34upx;
		}
		
		.border-right{
			border-right: 1upx solid #eee;
		}
	}
}

.list {
	background-color: #ffffff;
	width: 100%;
	// border-bottom: solid 26upx #F5F5F5;
	margin-bottom: 20upx;
	display: flex;
	flex-direction: column;
	.li {
		width: 100%;
		margin: 0 auto;
		height: 100upx;
		padding: 0 4%;
		border-bottom: solid 1upx #EEE;
		display: flex;
		flex-direction: row;
		align-items: center;
		justify-content: space-between;
		&.noborder {
			border-bottom: 0;
		}
		.left{
			display: flex;
			flex-direction: row;
			align-items: center;
		}
		.icons {
			display: flex;
			align-items: center;
			margin-right: 20upx;
			align-items: center;
			text{
				font-size: 40upx;
			}
			image{
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
	.right{
		width: 300upx;
		display: flex;
		flex-direction: row;
		align-items: center;
		justify-content: flex-end;
		.right-text{
			color: #999;
			margin-right: 10upx;
		}
	}
}
</style>
