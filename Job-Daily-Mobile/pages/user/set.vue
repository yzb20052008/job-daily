<template>
	<view>
		<public-module></public-module>
		<!-- <view class="group">
			<view class="cell u-flex">
				<view class="title u-flex-m">版本号</view>
				<view class="text">1.0.0</view>
			</view>
			<view class="cell u-flex">
				<view class="title u-flex-m">发布日期</view>
				<view class="text">2024-08-30</view>
			</view>
		</view> -->
		<view class="group">
			<view class="cell u-flex" @click="onJump('/pages/user/aboutus')">
				<view class="title u-flex-m">关于我们</view>
				<u-icon name="arrow-right" :size="14" color="#333"></u-icon>
			</view>
			<view class="cell u-flex" @click="onJump('/pages/user/help')">
				<view class="title u-flex-m">帮助中心</view>
				<u-icon name="arrow-right" :size="14" color="#333"></u-icon>
			</view>
			<view class="cell u-flex" @click="onJump('/pages/user/protocol?code=agreement&title=用户协议')">
				<view class="title u-flex-m">用户协议</view>
				<u-icon name="arrow-right" :size="14" color="#333"></u-icon>
			</view>
			<view class="cell u-flex" @click="onJump('/pages/user/protocol?code=privacy&title=隐私政策')">
				<view class="title u-flex-m">隐私政策</view>
				<u-icon name="arrow-right" :size="14" color="#333"></u-icon>
			</view>
			<!-- #ifdef MP || APP-PLUS -->
			<!-- <view class="cell u-flex" @click="checkUpData">
				<view class="title u-flex-m">检查更新</view> -->
			<!-- #ifdef APP-PLUS -->
			<!-- <view class="text" v-if="versionInfo.versionName">{{ versionInfo.versionName }}</view> -->
			<!-- #endif -->
			<!-- <u-icon name="arrow-right" :size="14" color="#333"></u-icon> -->
			<!-- </view> -->
			<!-- #endif -->
			<!-- #ifdef APP-PLUS -->
			<view class="cell u-flex">
				<view class="title u-flex-m">缓存大小</view>
				<view class="text">{{ fileSizeString }}</view>
				<!-- app删除缓存 -->
				<view class="u-p-l-14" @tap.stop="appClearCache"><u-icon name="trash" :size="20" color="#888"></u-icon>
				</view>
			</view>
			<!-- #endif -->
		</view>
		<view class="u-p-t-50 u-p-l-30 u-p-r-30" v-if="userInfo.token"><u-button shape="square" type="error" @click="loginOut">退出登录</u-button>
		</view>
	</view>
</template>

<script>
	import {
		mapState,
		mapMutations
	} from 'vuex';
	// #ifdef MP
	import {
		getPhoneInfo
	} from '@/config/login';
	import {
		mpUpData
	} from '@/config/common';
	// #endif
	// #ifdef APP-PLUS
	import {
		formatSize,
		clearCache
	} from '@/config/common';
	import APPUpdate from '@/uni_modules/zhouWei-APPUpdate/js_sdk/appUpdate';
	// #endif
	export default {
		computed: {
			...mapState(['userInfo'])
		},
		data() {
			return {
				fileSizeString: '0B', //App缓存大小
				versionInfo: {}, //版本信息
				phoneNum: this.$store.state.userInfo.phoneNum || '',
				avatar: require('@//static/logo.png')
			};
		},
		onLoad() {
			// #ifdef APP-PLUS
			this.appFormatSize(); //计算app缓存大小
			this.getCurrentNo(); //获取版本号
			// #endif
		},
		onShow() {
			this.bindPhoneSuccess();
		},
		methods: {
			...mapMutations(['emptyUserInfo', 'setUserInfo']),
			onJump(url) {
				console.log('url===', url);
				uni.navigateTo({
					url: url
				});
			},
			loginOut() {
				var that = this;
				uni.showModal({
					title: '退出登录',
					content: '你确定退出登录吗？',
					success(res) {
						if (res.confirm) {
							that.emptyUserInfo();
							if (that.goEasy.getConnectionStatus() === 'connected') {
								console.log('do  disconnect');
								//断开连接
								that.goEasy.disconnect({
									onSuccess: function() {
										console.log('GoEasy disconnect successfully.');
									},
									onFailed: function(error) {
										console.log('Failed to disconnect GoEasy, code:' + error.code +
											',error:' + error.content);
									}
								});
							}
							setTimeout(() => {
								uni.navigateTo({
									url: '/pages/user/login'
								});
							}, 500);
						} else if (res.cancel) {}
					}
				});
			},
			// 修改头像
			upAvatar() {
				var that = this;
				uni.chooseImage({
					count: 1,
					success: res => {
						const tempFilePaths = res.tempFilePaths;
						// uni.$u.http.upload('api/upload/img', {
						//     filePath:tempFilePaths[0],
						//     name:'avatar',
						// }).then(res => {
						//     that.avatar = res
						// })
					}
				});
			},
			// 检查更新
			checkUpData() {
				// #ifdef MP
				this.inspectMpUpData();
				// #endif
				// #ifdef APP-PLUS
				this.appUpData();
				// #endif
			},
			getPhone() {
				// #ifdef H5
				this.openBindPhone(1);
				// #endif
			},
			//檢查小程序更新
			inspectMpUpData() {
				// #ifdef MP
				var that = this;
				mpUpData(res => {
					if (res.type === 1) {
						//请求完新版本信息的回调
						if (!res.data.hasUpdate) {
							that.$u.toast('当前没有新版发布');
						}
					} else if (res.type === 2) {
						// 如果希望用户在最新版本的客户端上体验您的小程序，可以这样子提示
						uni.showModal({
							title: '提示',
							content: '当前微信版本过低，无法使用该功能，请升级到最新微信版本后重试。'
						});
					}
				});
				// #endif
			},
			appUpData() {
				APPUpdate(); //检测app更新
			},
			// App计算缓存
			appFormatSize() {
				let that = this;
				formatSize(res => {
					that.fileSizeString = res;
				});
			},
			// App清理缓存
			appClearCache() {
				console.log('清除缓存--');
				let that = this;
				clearCache(this.fileSizeString).then(() => {
					that.appFormatSize();
				});
			},
			//授权手机号
			decryptPhoneNumber(e) {
				console.log(e, '授权手机号');
				var that = this;
				// #ifdef MP-WEIXIN
				if (e.detail.errMsg == 'getPhoneNumber:ok') {
					if (e.detail.iv) {
						var authorizeInfo = e.detail;
						//请去getPhoneInfo方法中使用您的接口绑定信息
						getPhoneInfo(
							authorizeInfo,
							info => {
								//绑定成功后--保存您的信息
								// var userInfo = {
								//     phoneNum: res.phoneNum,
								// };
								// that.setUserInfo(userInfo);
								// that.bindPhoneSuccess()
								setTimeout(() => {
									uni.$u.toast('绑定成功');
								}, 100);
							},
							err => {
								that.openBindPhone(2, '获取手机号失败,请使用手机号登录');
							}
						);
					} else {
						that.openBindPhone(2, '获取手机号失败,请使用手机号登录');
					}
				} else {
					// 拒绝
					this.onAuthError();
				}
				// #endif

				// #ifdef MP-ALIPAY
				uni.getPhoneNumber({
					success: res => {
						console.log(res.response, 'res.response');
						//请去getPhoneInfo方法中使用您的接口绑定信息
						getPhoneInfo(
							res.response,
							res => {
								//绑定成功后--保存您的信息
								// var userInfo = {
								//     phoneNum: res.phoneNum,
								// };
								// that.setUserInfo(userInfo);
								// that.bindPhoneSuccess()
								setTimeout(() => {
									uni.$u.toast('绑定成功');
								}, 100);
							},
							err => {
								that.openBindPhone(2, '获取手机号失败,请使用手机号登录');
							}
						);
					},
					fail: res => {
						that.openBindPhone(2, '获取手机号失败,请使用手机号登录');
					}
				});
				// #endif
			},
			// 拒绝手机授权
			onAuthError(e) {
				this.openBindPhone(2, '您已拒绝或授权失败，可使用手机号绑定~');
			},
			// 手机号绑定成功回调方法
			bindPhoneSuccess() {
				this.phoneNum = this.$store.state.userInfo.phoneNum || '';
			},
			openBindPhone(type = 1, content) {
				var that = this;
				if (type == 1) {
					that.onJump('/pages/user/login'); //绑定号码可以自定义页面或者弹窗
				} else if (type == 2) {
					uni.showModal({
						title: '温馨提示',
						content: content,
						success(res) {
							if (res.confirm) {
								that.onJump('/pages/user/login'); //绑定号码可以自定义页面或者弹窗
							}
						}
					});
				}
			},
			// 获取当前应用的版本号
			getCurrentNo(callback) {
				var that = this;
				// 获取本地应用资源版本号
				plus.runtime.getProperty(plus.runtime.appid, function(inf) {
					that.versionInfo = {
						versionCode: inf.versionCode,
						versionName: inf.version
					};
				});
			}
		}
	};
</script>

<style lang="scss">
	page {
		background-color: #f5f6fa;
	}

	.group {
		padding: 0 30rpx;
		margin-top: 20rpx;
		background: #fff;
	}

	.cell {
		position: relative;
		// line-height: normal;
		padding: 24rpx 0;
		border-bottom: 1rpx solid #eee;

		.title {
			font-size: 30rpx;
			color: #333;
		}

		.text {
			font-size: 28rpx;
			color: #888;
		}

		.itemButton {
			border-radius: 0;
			text-align: left;
			opacity: 0;
			width: 100%;
			height: 100%;
			position: absolute;
			left: 0;
			top: 0;
		}

		&:last-child {
			border-bottom: none;
		}

		.avatar {
			width: 80rpx;
			height: 80rpx;
			border-radius: 40rpx;
			margin-right: 10rpx;
		}
	}
</style>