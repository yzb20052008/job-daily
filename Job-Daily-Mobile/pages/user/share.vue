<template>
	<view class="content">
		<public-module></public-module>
		<view class="card">
			<view class="header">
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
			<!-- <text class="tips">扫一扫上面的二维码加我好友</text> -->
			<view class="note">
				<text class="note-title">应用分享说明：</text>
				<text class="note-content">
					1、复制分享链接，好友访问下载应用。
					2、保存应用下载二维码，好友扫码下载安装。
				</text>
			</view>
			<view class="btn">
				<button class="btn-link" @click="shareLink">分享链接</button>
				<button class="btn-img" @click="saveImg">保存图片</button>
			</view>
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
			qrval: 'https://www.pgyer.com/2uUz', //核销码

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
	},

	methods: {
		
		shareLink(){
			uni.setClipboardData({
				data: this.qrval, //要被复制的内容
				success: () => {
					uni.showToast({
						title: "链接复制成功"
					})
				}
			});
		},
		
		saveImg(){
			this.show2 = true;
		},
		
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
	box-sizing: border-box;
	justify-content: center;
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
}

.qrcode {
	margin-top: 50upx;
}
.tips {
	margin-top: 40upx;
	color: #999;
	font-size: 28upx;
}

.btn{
	display: flex;
	flex-direction: row;
	margin-top: 40upx;
}

.btn .btn-link{
	background-color: #f0ad4e;
	padding: 0 40upx;
	color: #fff;
}

.btn .btn-img{
	margin-left: 80upx;
	background-color: #12ae85;
	padding: 0 40upx;
	color: #fff;
}

.note{
	display: flex;
	flex-direction: column;
	margin-top: 40upx;
	color: #999;
	font-size: 28upx;
	
}

</style>
