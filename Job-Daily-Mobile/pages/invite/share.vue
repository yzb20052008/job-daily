<template>
	<view class="content">
		<view class="card">
			<view class="header">
				<text>好工作，找小蓝</text>
			</view>
			<view class="qrcode">
				<tki-qrcode cid="qrcode" ref="qrcode" :val="qrval" :size="size" :unit="unit" :background="background"
					:icon="iconImage" :iconSize="iconsize" :onval="onval" :loadMake="loadMake" :usingComponents="true"
					@result="qrR" />
			</view>
			<!-- <view class="tips">
				<text>邀请码：</text>
				<text class="code">{{userInfo.inviteCode}}</text>
				<text class="copy" @click="copy">复制</text>
			</view> -->
			<view class="note">
				<text class="note-title">分享说明：</text>
				<!-- #ifdef H5 -->
				<text class="note-content">
					1、复制分享链接，分享好友注册。
				</text>
				<!-- #endif -->
				<!-- #ifdef MP-WEIXIN -->
				<text class="note-content">
					1、长按图片区域保存二维码图片，好友扫码即可。
					2、点击分享按钮，分享给微信好友或者微信朋友圈。
				</text>
				<!-- #endif -->
			</view>
			<view class="btn">
				<!-- #ifdef H5 -->
				<button class="btn-link" @click="shareLink">复制链接</button>
				<!-- #endif -->
				<!-- #ifdef MP-WEIXIN -->
				<button class="btn-img" @click="saveImg">保存图片</button>
				<button open-type="share" plain="true" class="btn-share">
					分享好友
				</button>
				<!-- #endif -->
			</view>
		</view>
	</view>
</template>

<script>
	import {
		mapState,
		mapMutations
	} from 'vuex';
	import BaseUrl from '@/config/baseUrl.js';
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
				qrval: '', //二维码内容

				show2: false,
				actions2: [{
					name: '保存到手机'
				}]
			};
		},

		onNavigationBarButtonTap(val) {
			console.log('onNavigationBarButtonTap', val);
			this.show2 = true;
		},

		onReady() {
			// #ifdef MP-WEIXIN
			this.qrval = BaseUrl.shareUrl + "?referrer=" + this.userInfo.id;
			// #endif
		},
		
		onLoad() {
			uni.$u.mpShare.title = '零工招聘';
			uni.$u.mpShare.path = '/pages/index/index?referrer='+ this.userInfo.id;
			uni.$u.mpShare.imageUrl = '/static/logo.png';
		},

		methods: {

			shareLink() {
				uni.setClipboardData({
					data: this.qrval, //要被复制的内容
					success: () => {
						uni.showToast({
							title: "链接复制成功"
						})
					}
				});
			},
			
			copy(){
				uni.setClipboardData({
					data: this.userInfo.inviteCode, //要被复制的内容
					success: () => {
						uni.showToast({
							title: "邀请码复制成功"
						})
					}
				});
			},

			saveImg() {
				// #ifndef H5
				uni.saveImageToPhotosAlbum({
					filePath: this.src,
					success: function(res) {
						console.log('save success', res);
						uni.showToast({
							icon:'none',
							title:"图片已保存到相册"
						})
					}
				});
				// #endif
				// #ifdef H5
				var oA = document.createElement("a");
				oA.download = '图片名称.png'; // 设置下载的文件名，默认是'下载'
				oA.href = res.tempFilePath;    //图片url
				document.body.appendChild(oA);
				oA.click();
				oA.remove(); // 下载之后把创建的元素删除
				// #endif
			},

			qrR(res) {
				this.src = res;
				// console.log(this.src);
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
		// align-items: center;
		box-sizing: border-box;
		// justify-content: center;
		padding: 0 4%;
		margin-top: 4%;
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
		text{
			font-weight: bold;
			font-size: 18px;
		}
	}

	.qrcode {
		margin-top: 50upx;
	}

	.tips {
		display: flex;
		flex-direction: row;
		align-items: center;
		margin-top: 40upx;
		color: #333;
		font-size: 28upx;
		.code{
			font-weight: bold;
			color: red;
			font-size: 34upx;
		}
		.copy{
			color: $main-color;
			margin-left: 30upx;
		}
	}

	.btn {
		display: flex;
		flex-direction: row;
		margin-top: 40upx;
	}

	.btn .btn-link {
		background-color: #f0ad4e;
		padding: 0 40upx;
		color: #fff;
	}

	.btn .btn-img {
		// margin-left: 80upx;
		background-color: #12ae85;
		padding: 0 40upx;
		color: #fff;
		border: none;
	}
	
	.btn .btn-share{
		margin-left: 30upx;
		background-color: #f0ad4e;
		padding: 0 40upx;
		color: #fff;
		border: none;
	}

	.note {
		display: flex;
		flex-direction: column;
		margin-top: 40upx;
		color: #999;
		font-size: 28upx;

	}
</style>