<template>
	<view class="content">
		<!-- 公共组件-每个页面必须引入 -->
		<public-module></public-module>
		<view class="reg">
			<u-form labelPosition="left" labelWidth="auto" :model="form" :rules="rules" ref="form">
				<u-form-item label="手机号" labelPosition="left" prop="phone" borderBottom>
					<u-input :disabled="disabled" placeholder="请输入手机号" border="none" v-model="form.phone" />
				</u-form-item>
				<u-form-item label="验证码" labelPosition="left" prop="code" borderBottom>
					<u-input :disabled="disabled" v-model="form.code" border="none" placeholder="请填写验证码">
						<template slot="suffix">
							<u-code ref="uCode" @change="codeChange" seconds="60" changeText="已发送(XS)"></u-code>
							<u-button @tap="getCode" :text="tips" type="primary" size="mini"
								:customStyle="codeStyle"></u-button>
						</template>
					</u-input>
				</u-form-item>
			</u-form>
		</view>
		<view class="bottom">
			<button class="throttle" @tap="$u.throttle(btnAClick, 500)">确认新号码</button>
			<!-- <u-button @tap="$u.throttle(btnAClick, 500)" type="primary" size="large">确认新号码</u-button> -->
		</view>
	</view>
</template>

<script>
	import uploadImage from '@/plugins/ossutil/uploadFile';
	import BaseUrl from '@/config/baseUrl.js';
	import {
		getSmsCode
	} from '@/config/api';
	export default {
		data() {
			return {
				disabled: false,
				form: {
					id: "",
					phone: '',
					code: ""
				},
				tips: "获取验证码",
				codeStyle: {
					height: '26px'
				},
				rules: {
					phone: [{
						required: true,
						message: '请填写手机号码',
						trigger: ['blur', 'change']
					}],
					code: {
						type: 'string',
						required: true,
						len: 6,
						message: '请填写6位验证码',
						trigger: ['blur']
					},
				}
			};
		},

		onReady() {
			this.$refs.form.setRules(this.rules)
		},

		onLoad() {},

		methods: {
			codeChange(text) {
				this.tips = text;
			},

			getCode() {
				if (this.disabled) {
					return;
				}
				console.log('getVcode');
				if (!this.$refs.uCode.canGetCode) {
					return;
				}
				if (this.form.phone == '') {
					uni.showToast({
						title: '请输入手机号',
						icon: 'none'
					});
					return;
				}
				let httpData = {
					phone: this.form.phone,
					smsmode: 0 // 0 .登录模板、1.注册模板、2.忘记密码模板
				};
				// 获取验证码接口
				getSmsCode({
						params: httpData,
						custom: {
							isFactory: false
						}
					})
					.then(res => {
						console.log(res, 'getSmsCode');
						uni.$u.toast(res.message);
						// 通知验证码组件内部开始倒计时
						this.$refs.uCode.start();
					})
					.catch(err => {
						console.log(err, 'catch');
					});
			},

			btnAClick() {
				console.log('btnClick');
				let that = this;
				this.$refs.form
					.validate()
					.then(res => {
						that.judgeSmsCode();
					})
					.catch(errors => {
						console.log("errors===",errors)
						uni.$u.toast('校验失败');
					});
			},

			async judgeSmsCode() {
				let res = await this.$apis.judgeSmsCode(this.form);
				console.log(res);
				if (res) {
					let pages = getCurrentPages(); //获取page
					let prevPage = pages[pages.length - 2]; //上一个页面（父页面）
					console.log('上一页参数', prevPage);
					prevPage.$vm.form.phone = this.form.phone; //修改上一页data里面的地址
					uni.navigateBack({
						delta: 1
					});
				}
			},

		}
	};
</script>

<style lang="scss">
	.content {
		display: flex;
		flex-direction: column;
	}

	.reg {
		padding: 30upx;
		padding-bottom: 50upx;

		/deep/ .u-form-item__body__left__content__label {
			height: 40px;
			line-height: 40px;
			font-size: 18px;
			color: #333;
			font-weight: bold;
			min-width: 90px;
		}
	}

	.bottom {
		width: 100%;
		background: #f5f6fa;
		margin-top: 30px;

		.throttle {
			width: 94%;
			height: 80upx;
			line-height: 80upx;
			margin: 0upx auto;
			background-color: #007aff;
			color: #ffffff;
			font-size: 16px;
		}
	}
</style>