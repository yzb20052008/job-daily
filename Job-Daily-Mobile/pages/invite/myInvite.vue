<template>
	<view class="content">
		<view class="account">
			<view class="item">
				<text class="money">{{data.totalNumber}}</text>
				<text class="title">累计邀请人数</text>
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
	export default {
		data() {
			return {
				data: {
					totalNumber: 0,
					currentNumber: 0,
				},
				severList: [
					[{
						name: '邀请好友',
						text: '去邀请',
						show: true,
						icon: '/static/images/my/us.png',
						color: '#FF9552',
						url: '/pages/invite/share'
					}, ],
					[{
							name: '邀请记录',
							show: true,
							icon: '/static/images/my/list.png',
							color: '#FCB138',
							url: '/pages/invite/inviteLog'
						},
						{
							name: '邀请规则',
							show: true,
							icon: '/static/images/my/help.png',
							color: '#FF8948',
							url: '/pages/invite/inviteRule'
						},
					]
				],
			}
		},

		onShow() {
			this.getReferrerCount();
		},

		methods: {
			async getReferrerCount() {
				let res = await this.$apis.getReferrerCount();
				console.log('res=====', res);
				if (res) {
					this.data = res;
				}
			},

			//用户点击列表项
			toPage(list_i, li_i) {
				uni.$u.route(this.severList[list_i][li_i].url);
			},

		}
	}
</script>

<style lang="scss">
	page {
		background-color: #f5f6fa;
	}

	// .content {
	// 	height: 100vh;
	// 	background-image: linear-gradient(#007aff, #DEEFFA 40%, #f5f5f5);
	// 	padding-top: 180upx;
	// 	padding-bottom: 50upx;
	// }

	.account {
		display: flex;
		flex-direction: column;
		background-color: #fff;
		margin: 20upx;
		border-radius: 20upx;

		.item {
			padding: 30upx;
			display: flex;
			flex-direction: column;
			align-items: center;
			justify-content: center;

			.money {
				color: #333;
				font-size: 30px;
				font-weight: bold;
			}

			.title {
				color: #666;
				font-size: 16px;
				margin-top: 10upx;
			}
		}

		.bottom {
			display: flex;
			flex-direction: row;
			justify-content: space-between;

			.btn {
				padding: 15upx 0;
				display: flex;
				align-items: center;
				justify-content: center;
				color: #333;
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
		display: flex;
		flex-direction: column;
		background-color: #ffffff;
		margin: 0 20upx;
		margin-bottom: 20upx;
		border-radius: 20upx;

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
			width: 300upx;
			display: flex;
			flex-direction: row;
			align-items: center;
			justify-content: flex-end;

			.right-text {
				color: #999;
				margin-right: 10upx;
				font-size: 15px;
			}
		}
	}
</style>