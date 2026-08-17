<template>
	<view class="content">
		<public-module></public-module>
		<view class="card">
			<view class="header">
				<view class="userinfo">
					<view class="face"><u-avatar size="80" shape="square" :src="userInfo.avatar"></u-avatar></view>
					<view class="info">
						<view class="name">
							<text class="username">{{ userInfo.nickname || '暂无昵称' }}</text>
							<view>
								<u-icon
									size="22px"
									:name="userInfo.sex == 1 ? 'man' : userInfo.sex == 2 ? 'woman' : ''"
									:color="userInfo.sex == 1 ? '#12ae85' : '#f56c6c'"
								></u-icon>
							</view>
						</view>
						<view class="integral">手机：{{ userInfo.phone | phone }}</view>
						<view class="integral">城市：{{ userInfo.area || '无' }}</view>
					</view>
				</view>
			</view>
			<view class="qrcode">
				<tki-qrcode
					cid="qrcode"
					ref="qrcode"
					:val="qrval"
					:size="size"
					:unit="unit"
					:background="background"
					:icon="iconImage"
					:iconSize="iconsize"
					:onval="onval"
					:loadMake="loadMake"
					:usingComponents="true"
					@result="qrR"
				/>
			</view>
			<text class="tips">扫一扫上面的二维码加我好友</text>
		</view>
		<u-action-sheet :show="show2" @close="show2 = false" @select="sheetSelect" :actions="actions2" cancelText="取消"></u-action-sheet>
	</view>
</template>

<script>
import { mapState, mapMutations } from 'vuex';
export default {
	computed: {
		...mapState(['userInfo'])
	},
	data() {
		return {
			val: '二维码', // 要生成的二维码值
			size: 500, // 二维码大小
			unit: 'upx', // 单位
			background: '#fff', // 背景色
			foreground: '#fff', // 前景色
			pdground: '#32dbc6', // 角标色
			iconImage: '/static/logo.png', // 二维码图标
			iconsize: 60, // 二维码图标大小
			lv: 3, // 二维码容错级别 ， 一般不用设置，默认就行
			onval: true, // val值变化时自动重新生成二维码
			loadMake: true, // 组件加载完成后自动生成二维码
			src: '', // 二维码生成后的图片地址或base64
			qrval: '', //核销码

			show2: false,
			actions2: [
				{
					name: '保存到手机'
				}
			]
		};
	},

	onNavigationBarButtonTap(val) {
		console.log('onNavigationBarButtonTap', val);
		this.show2 = true;
	},

	onReady() {
		console.log('avatar', this.userInfo.avatar);
		let that = this;
		uni.getImageInfo({
			src: this.userInfo.avatar,
			success: function(image) {
				that.iconImage = image.path;
				that.qrval = "chat_user@"+that.userInfo.uuid;
			},
			fail: function(error) {
				console.log('====error====', error);
			}
		});
	},

	methods: {
		sheetSelect(val) {
			uni.saveImageToPhotosAlbum({
				filePath: this.src ,
				success: function(res) {
					console.log('save success',res);
					uni.$u.toast('图片已保存到相册');
				}
			});
		},

		qrR(res) {
			this.src = res;
			console.log(this.src);
		}
	}
};
</script>

<style lang="scss">
page {
	background-color: #f5f6fa;
}

.content {
	display: flex;
	align-items: center;
	justify-content: center;
	box-sizing: border-box;
	padding: 0 4%;
	height: 90vh;
}

.card {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	background-color: #fff;
	width: 100%;
	padding: 40upx;
	box-sizing: border-box;
	border-radius: 20upx;
}

.header {
	display: flex;
	flex-direction: row;
	align-items: center;
	width: 100%;
	.userinfo {
		display: flex;
		flex-direction: row;
		align-items: center;
		.face {
			flex-shrink: 0;
		}
		.info {
			display: flex;
			flex-direction: column;
			padding-left: 40upx;
			box-sizing: border-box;
			align-items: flex-start;
			.name {
				display: flex;
				flex-direction: row;
				align-items: center;
				margin-bottom: 10upx;
			}
			.username {
				width: 100%;
				color: #000;
				font-weight: bold;
				font-size: 38upx;
				margin-right: 10rpx;
			}
			.midele {
				display: flex;
				flex-direction: row;
				justify-content: space-between;
				width: 100%;
				.setting {
					display: flex;
					flex-direction: row;
					text {
						color: #999;
						font-size: 40rpx;
					}
					text:nth-child(2) {
						margin-left: 30upx;
					}
				}
			}
			.integral {
				display: flex;
				height: 40upx;
				color: #333;
				font-size: 24upx;
			}
		}
	}
}

.qrcode {
	margin-top: 50upx;
}
.tips {
	margin-top: 40upx;
	color: #999;
	font-size: 28upx;
}
</style>
