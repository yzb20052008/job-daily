<template>
	<view>
		<image class="bg" src="../../static/login-bg.png"></image>
		<!-- #ifndef MP-TOUTIAO -->
		<view class="navbar">
			<uni-nav-bar :border="false" :statusBar="true" color="#000" backgroundColor="rgba(0,0,0,0)" left-icon="left"
				@clickLeft="back" />
		</view>
		<!-- #endif -->
		<view class="wx-auth">
			<view class="top">
				<view class="header">
					<image class="logo" src="../../static/logo.png"></image>
					<text class="name">实习快聘</text>
					<text class="tips">首次验证通过即注册实习快聘账号</text>
				</view>

				<view class="main">
					<view class="picker">
						<text class="role">角色选择：</text>
						<picker @change="bindPickerChange" range-key="name" :value="verCode" :range="roleList">
							<input class="padding-10" :focus="false" :disabled="true" placeholder="首次登录请选择"
								:value="role.name" />
						</picker>
						<text class="yzb yzb-next"></text>
					</view>
					<!-- <view class="picker">
						<text class="role">邀请码：</text>
						<input maxlength="12" placeholder="选填,首次登录可输入邀请码" v-model="inviteCode" />
					</view> -->
				</view>
				<!-- <view class="btn">
					<button class="btn-get-phone" open-type="getPhoneNumber" @getphonenumber="getPhoneNumber"
						v-if="isGetPhone == true">一键绑定手机号登录</button>
					<view class="btn-wx" @click="authLogin" v-if="isGetPhone == false">
						<text class="wx-name">一键登录</text>
					</view>
				</view> -->
				<!-- #ifdef MP-TOUTIAO -->
				<!-- <view class="btn">
					<view class="btn-wx" @click="authLogin">
						<text class="wx-name">抖音用户信息授权登录</text>
					</view>
				</view> -->
				<view class="btn">
					<button v-if="radioValue == 1" class="btn-wx" open-type="getPhoneNumber"
						@getphonenumber="getPhoneNumber">
						<text class="wx-name">授权手机号快捷登录</text>
					</button>
					<button class="btn-wx-no" v-else @click="agree">
						<text class="wx-name">授权手机号快捷登录</text>
					</button>
				</view>
				<!-- #endif -->
				<!-- #ifndef MP-TOUTIAO -->
				<view class="btn">
					<button v-if="radioValue == 1" class="btn-wx" open-type="getPhoneNumber"
						@getphonenumber="getPhoneNumber">
						<text class="wx-name">一键登录</text>
					</button>
					<button class="btn-wx-no" v-else @click="agree">
						<text class="wx-name">一键登录</text>
					</button>
				</view>
				<!-- #endif -->
				<!-- <view class="other-login">
					<text class="title">其他登录方式</text>
					<text class="yzb yzb-shoujisel" @click="phoneLogin()"></text>
				</view> -->
				<view class="other-login">
					<text class="title" @click="phoneLogin()">其他登录方式</text>
					<text class="yzb yzb-shoujisel" @click="phoneLogin()"></text>
				</view>
				<view class="agreement">
					<radio-group @change="radioGroup">
						<radio color="#006CFF" :value="1" :checked="radioValue" style="transform:scale(0.72);"></radio>
					</radio-group>
					<view class="agreement2" @click="radioValue =1">
						已阅读并同意
						<text class="agreement-text" @click.stop="toTerms">用户协议</text>
						和
						<text class="agreement-text" @click.stop="toPrivacy">隐私政策</text>
					</view>
				</view>
			</view>
		</view>
		<!-- //授权弹框 -->
		<view class="auth_wrap" :class="isAuth?'show':''">
			<view class="mask"></view>
			<view class="auth_content">
				<view class="auth_top">
					<view class="ptitle">获取您的昵称、头像、手机号</view>
					<view class="txt">获取用户头像、昵称、手机号信息，主要用于完善个人资料，向用户提供更好使用体验</view>
					<view class="close" @tap="closePopup">
						<!-- <image src="../../static/logo.png" mode=""></image> -->
						<text class="yzb yzb-shanchu"></text>
					</view>
				</view>
				<view class="auth_ul">
					<view class="auth_li">
						<view class="tit">头像</view>
						<view class="rit">
							<button class="avatar-wrapper" open-type="chooseAvatar" @chooseavatar="onChooseAvatar">
								<image class="avatar" :src="avatarUrl || '../../static/img/default.png'"></image>
							</button>
						</view>
					</view>
					<view class="auth_li">
						<view class="tit">昵称</view>
						<view class="rit">
							<input type="nickname" class="weui-input" placeholder="请输入昵称" @blur="onNickname" />
						</view>
					</view>
				</view>
				<view class="confirm_btn" @tap.stop="WxgetUserProfile">保存</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		pathToBase64
	} from '@/plugins/image-tools.js' //引入图片转base64js
	import uploadImage from '@/plugins/ossutil/uploadFile';
	export default {
		data() {
			return {
				//判断小程序的API，回调，参数，组件等是否在当前版本可用。
				canIUse: true,
				bindUserInfo: null,
				verCode:"",

				radioValue: 0,
				isGetPhone: false,
				isAuth: false, //控制授权弹框显示与隐藏
				isFrist: 1, //判断是否是第一次授权

				referrer: '', //推荐人
				inviteCode: '', //推荐码

				avatarUrl: '', //头像
				nickName: '', //昵称

				role: {
					code: '',
					name: ''
				},
				roleList: [],
			};
		},

		onShow: function() {},

		onLoad() {
			// #ifdef MP-WEIXIN
			this.canIUse = uni.canIUse('button.open-type.getUserInfo');
			// #endif
			//用于判断是否是首次授权，有值说明不是首次授权，没有值说明是首次授权
			let AlertLogin = uni.getStorageSync('AlertLogin');
			if (AlertLogin) {
				console.log('登录过一次')
				this.isFrist = 2
			} else {
				console.log('首次登录');
				this.isFrist = 1
			}
			this.referrer = uni.getStorageSync('referrer');
			this.getAppRoles();
		},

		methods: {


			//移动端角色列表
			async getAppRoles() {
				let roles = await this.$apis.getAppRoles();
				this.roleList = roles;
			},

			bindPickerChange: function(e) {
				console.log("bindPickerChange==", e);
				this.role = this.roleList[e.target.value];
				console.log("this.role==", this.role);
			},

			back() {
				uni.navigateBack({
					delta: 1
				})
			},

			authLogin() {
				if (this.radioValue != 1) {
					uni.showToast({
						title: '请勾选用户协议',
						icon: 'none',
						position: 'bottom'
					});
					return false;
				}
				//#ifdef MP-TOUTIAO
				this.wxGetUserInfo();
				//#endif
				//#ifdef MP-WEIXIN
				//首次授权，出现授权弹框，以后退出后再登录则直接登录上去，不再出现授权弹框
				if (this.isFrist == 1) {
					this.isAuth = true;
				} else {
					this.isAuth = false;
					this.wxGetUserInfo();
				}
				//#endif
			},

			//微信登录接口
			WxgetUserProfile() {
				let _this = this;
				if (this.isFrist == 1) {
					// 授权授权时提示输入昵称
					if (!this.nickName) {
						uni.showToast({
							icon: 'none',
							title: '请输入昵称'
						})
						return
					}
				}
				
				uni.checkSession({
					success(res) {
						console.log("success==",res)
					},
					fail(res) {
						console.log("fail==",res)
					}
				})
				this.wxGetUserInfo();
			},

			// 小程序微信登录
			wxGetUserInfo: function(res) {
				const that = this;
				uni.getUserProfile({
					desc: '完善用户信息',
					success: (info) => {
						console.log("==getUserProfile==", info);
						// #ifdef MP-TOUTIAO
						that.nickName = info.userInfo.nickName;
						that.avatarUrl = info.userInfo.avatarUrl;
						uni.login({
							provider: 'toutiao',
							success: function(res) {
								console.log("login",res)
								console.log("code",res.code)
								console.log("anonymous_code",res.anonymousCode)
								let httpData={
									code: res.code,
									iv: info.iv, //小程序加密算法的初始向量
									encryptedData: info
										.encryptedData, //包括敏感数据在内的完整用户信息的加密数据
									nickName: info.userInfo.nickName || '', //昵称
									avatarUrl: info.userInfo.avatarUrl || '', //头像
									gender: info.userInfo.gender || '', //性别 0:未知 1:男 2:女
									referrer: that.referrer,
									role:that.role.code,
								}
								that.wxLogin(httpData);
								// that.getOpenId(res.code);
							}
						});
						// #endif
						// #ifdef MP-WEIXIN
						uni.login({
							provider: 'weixin',
							success: function(res) {
								// that.wxLogin(httpData);
								that.getOpenId(res.code);
							}
						});
						// #endif
					},
					fail: (res) => {
						console.log(res);
						uni.showToast({
							title: '您取消了授权,登录失败',
							icon: 'none'
						});
					}
				});
			},

			async getOpenId(code) {
				let params = {
					code: code
				}
				let res;
				// #ifdef MP-TOUTIAO
				res = await this.$apis.getDyOpenId(params);
				// #endif
				// #ifdef MP-WEIXIN
				res = await this.$apis.getOpenId(params);
				// #endif
				console.log("==getOpenId==", res);
				this.openid = res.openid;
				this.session_key = res.session_key;
				this.$store.commit('SET_OPENID', res.openId);
				uni.setStorage({
					key: 'session_key',
					data: res.session_key
				});
				if (res != null) {
					this.xcxLogin(res.openId);
				}
			},

			async xcxLogin(openid) {
				var that = this;
				let param = {
					openid: openid,
					avatar: this.avatarUrl,
					nickName: this.nickName
				};
				let res = await this.$apis.xcxLogin(param);
				this.isAuth = false;
				console.log('xcxUserLogin===', res);
				if (res != '账号不存在' && res != '未绑定手机号') {
					this.$store.commit('SET_USERINFO', res.userInfo || {});
					// 前端自动登录
					this.$store.commit('SET_TOKEN', res.token);
					let permission = await this.$apis.getUserPermissionByToken();
					this.$store.commit('SET_TABBARS', permission.menu);
					this.$store.commit('SET_PERMISSION', permission.menu);
					this.$db.set('AlertLogin', 1);
					setTimeout(() => {
						uni.hideLoading();
						uni.showToast({
							title: '登录成功',
							icon: 'none'
						});
						uni.navigateBack({
							delta: 1
						});
					}, 1000)
				} else {
					console.log('绑定手机号');
					this.isGetPhone = true;
				}
			},

			async wxLogin(param) {
				uni.showLoading({
					title: "登陆中···"
				})
				var that = this;
				let res;
				// #ifdef MP-TOUTIAO
				res = await this.$apis.dyUserLogin(param);
				// #endif
				// #ifdef MP-WEIXIN
				res = await this.$apis.xcxUserLogin(param);
				// #endif
				console.log('mWxLogin===', res);
				if (res) {
					this.$store.commit('SET_USERINFO', res.userInfo || {});
					// 前端自动登录
					this.$store.commit('SET_TOKEN', res.token);
					let permission = await this.$apis.getUserPermissionByToken();
					this.$store.commit('SET_TABBARS', permission.menu);
					this.$store.commit('SET_PERMISSION', permission.menu);
					this.$db.set('AlertLogin', 1);
					setTimeout(() => {
						uni.hideLoading();
						uni.showToast({
							title: '登录成功',
							icon: 'none'
						});
						uni.navigateBack({
							delta: 1
						});
					}, 1000)
				} else {
					uni.hideLoading();
					console.log('登录失败');
				}
			},

			//绑定手机号并登录
			async bindPhoneForWx(ivStr, encDataStr) {
				let param = {
					openid: uni.getStorageSync('openId'),
					keyStr: uni.getStorageSync('session_key'),
					ivStr: ivStr,
					encDataStr: encDataStr,
					avatar: this.avatarUrl,
					nickName: this.nickName,
					referrer: this.referrer,
					inviteCode: this.inviteCode,
					role: this.role.code
				};
				let res = await this.$apis.bindPhoneForXcx(param);
				if (res) {
					this.$store.commit('SET_USERINFO', res.userInfo || {});
					// 前端自动登录
					this.$store.commit('SET_TOKEN', res.token);
					let permission = await this.$apis.getUserPermissionByToken();
					this.$store.commit('SET_TABBARS', permission.menu);
					this.$store.commit('SET_PERMISSION', permission.menu);
					this.$db.set('AlertLogin', 1);
					setTimeout(() => {
						uni.hideLoading();
						uni.showToast({
							title: '登录成功',
							icon: 'none'
						});
						uni.navigateBack({
							delta: 1
						});
					}, 1000)
				}
			},

			//绑定手机号并登录
			async phoneLoginForWx(ivStr, encDataStr, code) {
				let param = {
					code: code,
					ivStr: ivStr,
					encDataStr: encDataStr,
					referrer: this.referrer,
					inviteCode: this.inviteCode,
					role: this.role.code
				};
				let res = await this.$apis.phoneLoginForWx(param);
				if (res) {
					this.$store.commit('SET_USERINFO', res.userInfo || {});
					// 前端自动登录
					this.$store.commit('SET_TOKEN', res.token);
					let permission = await this.$apis.getUserPermissionByToken();
					this.$store.commit('SET_TABBARS', permission.menu);
					this.$store.commit('SET_PERMISSION', permission.menu);
					this.$db.set('AlertLogin', 1);
					setTimeout(() => {
						uni.hideLoading();
						uni.showToast({
							title: '登录成功',
							icon: 'none'
						});
						uni.navigateBack({
							delta: 1
						});
					}, 1000)
				}
			},
			
			
			//绑定手机号并登录
			async phoneLoginForDy(ivStr, encDataStr, code) {
				let param = {
					code: code,
					ivStr: ivStr,
					encDataStr: encDataStr,
					referrer: this.referrer,
					inviteCode: this.inviteCode,
					role: this.role.code
				};
				let res = await this.$apis.phoneLoginForDy(param);
				if (res) {
					this.$store.commit('SET_USERINFO', res.userInfo || {});
					// 前端自动登录
					this.$store.commit('SET_TOKEN', res.token);
					let permission = await this.$apis.getUserPermissionByToken();
					this.$store.commit('SET_TABBARS', permission.menu);
					this.$store.commit('SET_PERMISSION', permission.menu);
					this.$db.set('AlertLogin', 1);
					setTimeout(() => {
						uni.hideLoading();
						uni.showToast({
							title: '登录成功',
							icon: 'none'
						});
						uni.navigateBack({
							delta: 1
						});
					}, 1000)
				}
			},

			agree() {
				uni.showToast({
					icon: 'none',
					title: "请先阅读并同意《用户协议》和《隐私政策》"
				})
			},

			// 获取微信手机号码
			getPhoneNumber(params) {
				console.log('getPhoneNumber', params);
				var that = this;
				if (!params.detail.iv) {
					return;
				}
				uni.login({
					//#ifdef MP-WEIXIN
					provider: 'weixin',
					success: function(res) {
						that.code = res.code;
						that.phoneLoginForWx(params.detail.iv, params.detail.encryptedData, res.code);
					},
					//#endif
					//#ifdef MP-TOUTIAO
					// provider: 'weixin',
					success: function(res) {
						that.code = res.code;
						that.phoneLoginForDy(params.detail.iv, params.detail.encryptedData, res.code);
					}
					//#endif
				});
				// this.bindPhoneForWx(params.detail.iv, params.detail.encryptedData);
			},

			phoneLogin() {
				this.$mRouter.push({
					route: this.$mRoutesConfig.loginPwd
				});
			},

			cancle() {
				uni.navigateBack({
					delta: 1
				});
			},

			updateMember() {
				if (this.bindUserInfo != null) {
					var member = {
						avatar: this.avatarUrl,
						name: this.nickName,
						sex: this.bindUserInfo.gender,
						nickName: this.nickName
					};
					api.getUpdateMemberInfo(member, res => {
						if (res.data.code == 200) {
							uni.showToast({
								title: res.data.message
							});
						}
					});
				}
			},

			radioGroup(e) {
				this.radioValue = e.detail.value;
			},

			toTerms() {
				this.$mRouter.push({
					route: this.$mRoutesConfig.terms
				});
			},

			toPrivacy() {
				this.$mRouter.push({
					route: this.$mRoutesConfig.privacy
				});
			},

			//关闭授权弹框
			closePopup() {
				this.isAuth = false;
			},
			//监听昵称变化
			onNickname(e) {
				this.nickName = e.detail.value;
			},
			//获取头像变化的值
			onChooseAvatar(e) {
				//将微信返的临时图片转为base64格式图
				console.log("onChooseAvatar====", e.detail);
				uni.showLoading({
					title: '图片上传中'
				});
				let that = this;
				uploadImage(0, e.detail.avatarUrl, 'job/user/', result => {
					console.log('图片上传结果：', result);
					that.avatarUrl = result;
					uni.hideLoading();
				});
				// pathToBase64(e.detail.avatarUrl).then(base64 => {
				// 	console.log(base64)
				// 	this.avatarUrl = base64;
				// }).catch(error => {
				// 	console.error(error)
				// })
			},

		}
	};
</script>

<style lang="scss">
	/* 微信授权 */

	.bg {
		width: 750upx;
		height: 618upx;
		position: absolute;
		z-index: 0;
	}

	.navbar {}

	.wx-auth {
		// height: calc(100vh);
		width: 100%;

		.header {
			display: flex;
			flex-direction: column;
			align-items: center;
			justify-content: center;
			text-align: center;
			width: 100%;
			height: 480upx;
			// background-image: url('https://gzqkl.oss-cn-hangzhou.aliyuncs.com/sxkp/common/login-bg.png');;
		}

		.logo {
			width: 200upx;
			height: 200upx;
			z-index: 1;
			margin-top: 5upx;
		}

		.name {
			margin-top: 30upx;
			color: #000;
			font-size: 34upx;
			font-weight: bold;
			z-index: 1;
		}

		.tips {
			margin-top: 10upx;
			color: #999;
			font-size: 26upx;
			z-index: 1;
		}

		.btn {
			display: flex;
			flex-direction: column;
			justify-content: center;
			align-items: center;
		}

		.btn-auth {
			border-radius: 20rpx;
			margin-top: 50upx;
			font-size: 35rpx;
			width: 90%;
			background-image: linear-gradient(90deg, #5AE287, #01E8FD);
			color: #ffffff;
		}

		.btn-wx-no {
			border-radius: 30rpx;
			font-size: 35rpx;
			width: 90%;
			background-color: #999;
			color: #ffffff;
			align-items: center;
			display: flex;
			justify-content: center;
			height: 80upx;
		}

		.btn-wx {
			border-radius: 30rpx;
			font-size: 35rpx;
			width: 90%;
			// background-color: $main-color;
			background-image: linear-gradient(90deg, #5AE287, #01E8FD);
			color: #ffffff;
			align-items: center;
			display: flex;
			justify-content: center;
			height: 80upx;
		}

		.wx-icon {
			color: #fff;
			margin-right: 15upx;
		}

		.wx-name {
			font-size: 32upx;
		}

		.btn-get-phone {
			border-radius: 20rpx;
			margin-top: 50upx;
			font-size: 35rpx;
			width: 90%;
			background-color: #07C160;
			color: #ffffff;
			// background-image: linear-gradient(#5AE287, #01E8FD);
			// #5AE287   #01E8FD
		}

		.cancel-login {
			border-radius: 15rpx;
			margin: 70rpx 20rpx 50rpx 50rpx;
			font-size: 35rpx;
			background-color: #eeeeee;
			width: 30%;
		}
	}

	.main {
		display: flex;
		flex-direction: column;
		padding: 0 3%;
		box-sizing: border-box;
		margin-bottom: 40upx;
		z-index: 1;

		input {
			padding: 20upx;
			width: 100%;
		}

		.picker {
			display: flex;
			flex-direction: row;
			align-items: center;
			justify-content: space-between;
			padding: 20upx;
			border-bottom: 1upx solid #eee;
			z-index: 1;

			.role {
				width: 230upx;
				font-size: 30upx;
				font-weight: bold;
			}

			picker {
				font-size: $uni-font-size-lg;
				width: 100%;
			}

			.yzb-next {
				float: right;
				color: #999;
			}
		}
	}


	.other-login {
		display: flex;
		width: 100%;
		flex-direction: column;
		justify-content: center;
		align-items: center;
		margin-top: 40upx;

		.title {
			color: #333;
			font-size: 32upx;
		}

		.yzb-shoujisel {
			font-size: 100upx;
			color: #7FB6FF;
		}
	}

	.agreement {
		margin-top: 50upx;
		display: flex;
		flex-direction: row;
		align-items: center;
		justify-content: center;

		.agreement2 {
			font-size: 28upx;
			color: #9B9B9B;
		}

		.agreement-text {
			text-decoration: underline;
			color: #006CFF;
			padding: 0 10upx;
			font-size: 28upx;
		}
	}


	.auth_wrap {
		position: fixed;
		width: 100%;
		bottom: -120%;
		transition: all 0.35s linear;
		z-index: 999;

		&.show {
			bottom: 0;
			transition: all 0.35s linear;

			.mask {
				display: block;
			}
		}

		.mask {
			width: 100%;
			height: 100vh;
			position: fixed;
			background: rgba(0, 0, 0, 0.5);
			z-index: 98;
			top: 0;
			display: none;
		}

		.auth_content {
			padding: 32rpx 32rpx 72rpx 32rpx;
			position: relative;
			z-index: 99;
			background: #fff;
			border-radius: 16rpx 16rpx 0 0;
			display: flex;
			flex-direction: column;

			.auth_top {
				position: relative;
				display: flex;
				flex-direction: column;

				.ptitle {
					font-size: 30rpx;
					font-weight: bold;
					margin-bottom: 24rpx;
				}

				.txt {
					font-size: 26rpx;
					color: #999;
				}

				.close {
					width: 26rpx;
					height: 26rpx;
					position: absolute;
					right: 0;
					top: 0;

					image {
						width: 100%;
						height: 100%;
					}

					text {
						font-size: 34upx;
						color: #666;
					}
				}
			}

			.auth_ul {
				margin-top: 16rpx;
				display: flex;
				flex-direction: column;

				.auth_li {
					display: flex;
					align-items: center;
					border-top: solid 1px #f5f5f5;
					padding: 24rpx 0;

					&:last-child {
						border-bottom: solid 1px #f5f5f5;
					}

					.tit {
						width: 140rpx;
						font-size: 30rpx;
					}

					.rit {
						width: calc(100% - 140rpx);

						input {
							font-size: 28rpx;
							height: 72rpx;
							width: 100%;
						}

						image {
							width: 60rpx;
							height: 60rpx;
							border-radius: 50%;
						}

						button {
							width: 100%;
							height: 72rpx;
							background: #fff;
							text-align: left;
							padding: 0;

							&:after {
								border: solid 1px #fff;
							}

							// opacity: 0;
						}
					}
				}
			}

			.confirm_btn {
				width: 420rpx;
				margin: 46rpx auto 0 auto;
				background: #39b54a;
				color: #fff;
				border-radius: 8rpx;
				height: 94rpx;
				display: flex;
				align-items: center;
				justify-content: center;
				font-size: 30rpx;
			}
		}
	}
</style>