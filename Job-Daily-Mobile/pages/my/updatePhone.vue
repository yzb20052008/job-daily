<template>
	<view class="content">
		<!-- 公共组件-每个页面必须引入 -->
		<public-module></public-module>
		<view class="reg">
			<u-form labelPosition="top" labelWidth="auto" :model="form" :rules="rules" ref="form">
				<u-form-item label="原手机号" borderBottom>
					<u-input placeholder="请输入联系方式" readonly v-model="oldContact" border="none" />
				</u-form-item>
				<u-form-item label="新手机号" prop="phone" borderBottom>
					<u-input :disabled="disabled" placeholder="请输入联系方式" v-model="form.phone" border="none" />
				</u-form-item>
				<u-form-item label="验证码" prop="code" borderBottom>
					<u-input :disabled="disabled" v-model="form.code" placeholder="请填写验证码" border="none">
						<template slot="suffix">
							<u-code ref="uCode" @change="codeChange" seconds="60" changeText="已发送(XS)"></u-code>
							<u-button @tap="getCode" :text="tips" type="success" size="mini"
								:customStyle="codeStyle"></u-button>
						</template>
					</u-input>
				</u-form-item>
			</u-form>
		</view>
		<view class="bottom"><button class="throttle" @tap="$u.throttle(btnAClick, 500)">提交</button></view>
		<u-modal @confirm="modalConfirm" :show="modalShow" :title="modalTitle" :content="modalContent"></u-modal>
	</view>
</template>

<script>
	import {
		mapState,
		mapMutations
	} from 'vuex';
	import BaseUrl from '@/config/baseUrl.js';
	import {
		getSmsCode
	} from '@/config/api';
	export default {
		computed: {
			...mapState(['userInfo'])
		},
		data() {
			return {
				disabled: false,
				lastUpdateTime: '',
				oldContact: "",
				form: {
					id: "",
					phone: '',
					code: ""
				},
				tips: "获取验证码",
				codeStyle: {
					height: '26px'
				},
				modalShow: false,
				modalTitle: '温馨提示',
				modalContent: '电话修改成功！',
				rules: {
					phone: [{
						required: true,
						message: '请填写新手机号',
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

		onLoad() {
			this.oldContact = this.userInfo.phone;
		},

		methods: {
			...mapMutations([ 'setUserInfo']),

			codeChange(text) {
				this.tips = text;
			},

			async getCode() {
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
				if (this.form.phone == this.oldContact) {
					uni.showToast({
						title: '手机号不能一致',
						icon: 'none'
					});
					return;
				}

				let httpData = {
					phone: this.form.phone,
					smsmode: 0 // 0 .登录模板、1.注册模板、2.忘记密码模板
				};

				// let res = await this.$apis.getSmsCode({ params: httpData, custom: { isFactory: true } });
				// console.log(res, 'getSmsCode');
				// uni.$u.toast(res);
				// // 通知验证码组件内部开始倒计时
				// this.$refs.uCode.start();
				// // 获取验证码接口
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
						that.updatePhone();
					})
					.catch(errors => {
						uni.$u.toast('校验失败');
					});
			},

			async updatePhone() {
				this.$apis
					.updatePhone(this.form)
					.then(res => {
						uni.hideLoading();
						this.userInfo.phone = this.form.phone;
						this.setUserInfo(this.userInfo);
						uni.$u.toast('操作成功');
					})
					.catch(err => {
						uni.hideLoading();
						console.log(err, 'catch');
					});
			},

			modalConfirm() {
				this.show = false;
				uni.navigateBack();
			}
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

		.title {
			text-align: center;
			width: 100%;
			border-bottom: 1upx solid #dadbde;
			padding: 20upx;

			text {
				font-weight: bold;
			}
		}

	}

	.note {
		padding: 0 20upx;
		font-size: 28upx;
		color: #999;
		text-align: center;
		margin-top: 10upx;
	}

	.bottom {
		width: 100%;
		background: #f5f6fa;

		.throttle {
			width: 94%;
			margin: 10upx auto;
			background-color: #007aff;
			color: #ffffff;
		}
	}
</style>