<template>
	<view class="page">
		<!-- 公共组件-每个页面必须引入 -->
		<public-module></public-module>
		<view class="login">
			<image src="../../static/logo.png"></image>
			<view class="login-form">
				<view class="login-form-item"><u--input v-model="phone" placeholder="手机号" shape="circle" :customStyle="inputStyle"></u--input></view>
				<view class="login-form-item">
					<u-input v-model="vCode" placeholder="验证码" shape="circle" :customStyle="inputStyle">
						<template slot="suffix">
							<u-code ref="uCode" @change="codeChange" seconds="60" changeText="已发送(XS)"></u-code>
							<u-button @tap="getVcode" :text="tips" type="success" size="mini" :customStyle="codeStyle"></u-button>
						</template>
					</u-input>
				</view>
				<view class="login-form-agreement">
					<u-checkbox-group v-model="isAgree" @change="change"><u-checkbox activeColor="green"></u-checkbox></u-checkbox-group>
					<text class="login-form-agreement-text">
						我已阅读并同意
						<text class="agreement" @click="onJump('/pages/user/protocol?code=agreement&title=用户协议')">《用户协议》</text>
						和
						<text class="agreement" @click="onJump('/pages/user/protocol?code=privacy&title=隐私政策')">《隐私政策》</text>
					</text>
				</view>
				<view class="login-form-bottom"><button class="login-form-bottom-throttle" @tap="$u.throttle(btnAClick, 500)">登录</button></view>
			</view>
		</view>
		<view class="otherLogin">
			<view style="padding: 0 150rpx;"><u-divider text="其他登录方式"></u-divider></view>
			<view class="info u-flex">
				<view @click="otherLogin('weixin')"><u-icon name="weixin-circle-fill" size="40" color="#1fba1a"></u-icon></view>
				<!-- <view @click="otherLogin('qq')">
                    <u-icon name="qq-circle-fill" size="40" color="#333"></u-icon>
                </view> -->
				<!-- <view><u-icon name="zhifubao-circle-fill" size="40" color="#007AFF"></u-icon></view> -->
				<!-- <view @click="otherLogin('apple')"><u-icon name="apple-fill" size="40" color="#333"></u-icon></view> -->
			</view>
		</view>
	</view>
</template>

<script>
var clear;
import { mapState, mapMutations } from 'vuex';
import { loginByCode, getSmsCode } from '@/config/api';
export default {
	data() {
		return {
			phone: '', //号码
			vCode: '', //验证码
			isAgree: [], //是否同意协议

			tips: '',
			inputStyle: {
				padding: '8px 15px'
			},
			codeStyle: {
				height: '26px'
			}
		};
	},
	//第一次加载
	onLoad(e) {},
	//页面显示
	onShow() {},
	//方法
	methods: {
		...mapMutations(['setUserInfo']),
		onJump(url) {
			uni.navigateTo({
				url: url
			});
		},
		
		change(val) {
			console.log(val);
		},

		codeChange(text) {
			this.tips = text;
		},
		//获取验证码
		getVcode() {
			console.log('getVcode');
			if (!this.$refs.uCode.canGetCode) {
				return;
			}
			if (this.phone == '') {
				uni.showToast({
					title: '请输入手机号',
					icon: 'none'
				});
				return;
			}
			const phoneRegular = /^1\d{10}$/;
			if (!phoneRegular.test(this.phone)) {
				uni.showToast({
					title: '手机号格式不正确~',
					icon: 'none'
				});
				return;
			}
			let httpData = {
				mobile: this.phone,
				smsmode: 0 // 0 .登录模板、1.注册模板、2.忘记密码模板
			};
			// 获取验证码接口
			getSmsCode(httpData)
				.then(res => {
					console.log(res, 'getSmsCode');
					uni.$u.toast('验证码已发送');
					// 通知验证码组件内部开始倒计时
					this.$refs.uCode.start();
				})
				.catch(err => {
					console.log(err, 'catch');
				});
		},

		// 手机号登录
		btnAClick() {
			if (this.phone == '') {
				uni.showToast({
					title: '请输入手机号',
					icon: 'none'
				});
				return;
			}
			const phoneRegular = /^1\d{10}$/;
			if (!phoneRegular.test(this.phone)) {
				uni.showToast({
					title: '手机号格式不正确',
					icon: 'none'
				});
				return;
			}
			if (this.vCode == '') {
				uni.showToast({
					title: '请输入验证码',
					icon: 'none'
				});
				return;
			}
			if (!this.isAgree) {
				uni.showToast({
					title: '请同意用户协议',
					icon: 'none'
				});
				return;
			}
			let httpData = {
				phone: this.phone,
				code: this.vCode
			};
			loginByCode(httpData)
				.then(res => {
					console.log(res, 'loginByCode');
					let userInfo = {
						...res.userInfo,
						token: res.token //token用于判断是否登录
					};
					this.setUserInfo(userInfo);
					uni.showToast({
						title: '登录成功~',
						icon: 'none'
					});
					setTimeout(() => {
						// uni.navigateBack(); 
						uni.switchTab({
							url:'/pages/index/index'
						})
					}, 500);
				})
				.catch(err => {
					console.log(err, 'catch');
				});
		},

		//第三方登录
		otherLogin(e) {
			loginApp(e, httpData => {
				console.log(httpData, 'httpData');
				// uni.$u.http.post('您的接口',httpData).then(res => {
				// 储存登录信息
				// let userInfo = {
				//     ...res,
				//     token:true,//token用于判断是否登录
				// }
				// this.setUserInfo(userInfo)
				uni.showToast({
					title: '登录成功~',
					icon: 'none'
				});
				setTimeout(() => {
					uni.navigateBack();
				}, 500);
				// });
			});
		}
	}
};
</script>
<style lang="scss" scoped>
.page {
	min-height: 100vh;
	background-color: #fff;
}

.login {
	image {
		width: 230upx;
		height: 230upx;
		margin: 70upx auto;
	}
	&-form {
		margin: 0 10%;
		&-item {
			margin-top: 30upx;
		}
		&-bottom {
			margin-top: 30upx;
			width: 100%;
			&-throttle {
				width: 100%;
				height: 88upx;
				line-height: 88upx;
				border-radius: 44upx;
				margin: 10upx auto;
				background-color: #1fba1a;
				color: #ffffff;
			}
		}
		&-agreement {
			margin-top: 80upx;
			display: flex;
			flex-direction: row;
			align-items: center;
			&-text {
				color: #999;
				font-size: 24upx;
			}
		}

		.agreement {
			padding: 0 10upx;
			color: #007aff;
		}
	}
}

.otherLogin {
	position: fixed;
	left: 0;
	right: 0;
	bottom: 70rpx;
	.info {
		justify-content: space-around;
		padding: 32rpx;
	}
}
</style>
