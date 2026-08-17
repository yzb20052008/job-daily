<template>
	<view class="body">
		<view class="balance">
			<text>
				可用余额：
				<text class="money">{{account.balanceWithdraw}}</text>
				元
			</text>
			<text class="btn-w" @click="withdrawAll">全部提现</text>
		</view>
		<view class="forms">
			<u-form labelPosition="top" labelWidth="auto" :model="form" :rules="rules" ref="form">
				<u-form-item label="提现金额" prop="money" borderBottom required>
					<u-input placeholder="请输入提现金额" border="none" type="number" v-model="form.money" />
				</u-form-item>
				<u-form-item label="提现方式" labelPosition="left" prop="accountType" required borderBottom>
					<view class="u-tag-item">
						<view class="tag" v-for="(item, index) in radios" :key="index">
							<u-tag :text="item.name" color="#333" :plain="!item.checked"
								:bgColor="item.checked?'#E0F3FF':'#f5f6fa'"
								:borderColor="item.checked?'#007aff':'#f5f6fa'" size="large" type="primary"
								:name="index" @click="sexRadioClick">
							</u-tag>
						</view>
					</view>
				</u-form-item>
				<u-form-item :label="userName" prop="withdrawName" borderBottom required>
					<u-input placeholder="请输入实名信息" border="none" readonly v-model="form.withdrawName" />
				</u-form-item>
				<u-form-item :label="accountName" prop="withdrawAccount" borderBottom required>
					<u-input placeholder="请输入账号信息" border="none" v-model="form.withdrawAccount" />
				</u-form-item>
			</u-form>
		</view>
		<view class="tips">
			<text>
				1、至少{{withdrawMin}}元才能发起提现申请；
				2、单次提现金额不能超过{{withdrawMax}}元；
				2、每天提现金额不能超过{{withdrawDayMax}}元；
			</text>
		</view>
		<button class="btn" @click="$u.throttle(submit, 500)">确认提现</button>
	</view>
</template>

<script>
	import {
		mapState,
	} from 'vuex';
	import {
		requestSubscribe
	} from '@/config/common';
	export default {
		computed: {
			...mapState(['userInfo'])
		},
		data() {
			return {
				accountName: '账号',
				userName: '姓名',
				form: {
					money: '',
					withdrawAccount: '',
					withdrawName: '',
					accountType: 0
				},
				
				withdrawDayMax: 5000,
				withdrawMax: 2000,
				withdrawMin: 100,

				account: {
					balanceWithdraw: 0,
				},
				radios: [{
						checked: true,
						name: '支付宝',
						value: 0
					},
					{
						checked: false,
						name: '微信',
						value: 1
					},
					// {
					// 	checked: false,
					// 	name: '银联',
					// 	value: 2
					// }
				],

				rules: {
					money: [{
						required: true,
						message: '请输入提现金额',
						trigger: ['blur', 'change']
					}],

					withdrawName: [{
						required: true,
						message: '请输入姓名',
						trigger: ['blur', 'change']
					}],

					withdrawAccount: [{
						required: true,
						message: '请输入账号或卡号',
						trigger: ['blur', 'change']
					}],
				}
			};
		},

		onLoad() {
			this.form.withdrawName = this.userInfo.realname;
		},

		onShow() {
			this.getAccount();
			this.getWithdrawAccount();
			this.getBaseConfig();
		},

		onReady() {
			this.$refs.form.setRules(this.rules)
		},

		methods: {

			getBaseConfig() {
				this.$apis.getBaseConfig({
					params: {
						code: 'withdraw_day_max'
					}
				}).then(res => {
					if (res) {
						this.withdrawDayMax = Number(res.configValue);
					}
				});
				this.$apis.getBaseConfig({
					params: {
						code: 'withdraw_max'
					}
				}).then(res => {
					if (res) {
						this.withdrawMax = Number(res.configValue);
					}
				});
				this.$apis.getBaseConfig({
					params: {
						code: 'withdraw_min'
					}
				}).then(res => {
					if (res) {
						this.withdrawMin = Number(res.configValue);
					}
				});
			},

			sexRadioClick(idx) {
				this.radios.map((item, index) => {
					item.checked = index === idx ? true : false
				})
				this.form.accountType = this.radios[idx].value;
				console.log("sexRadioClick", idx);
				this.formatInfo(this.form.accountType);
				this.getWithdrawAccount();
			},


			async getWithdrawAccount() {
				let res = await this.$apis.getWithdrawAccount({
					params: {
						accountType: this.form.accountType
					}
				});
				if (res) {
					this.form.withdrawAccount = res.account;
					this.form.withdrawName = res.realname;
				} else {
					this.form.withdrawAccount = '';
				}
			},

			formatInfo(accountType) {
				if (accountType == 0) {
					this.accountName = "账号";
					this.userName = "姓名"
				} else if (accountType == 1) {
					this.accountName = "账号";
					this.userName = "姓名"
				} else if (accountType == 2) {
					this.accountName = "卡号";
					this.userName = "姓名"
				}
			},

			async getAccount() {
				let res = await this.$apis.getAccountDetail();
				if (res) {
					this.account = res;
				}
			},

			withdrawAll() {
				this.form.money = this.account.balanceWithdraw+'';
				this.$refs.form.validateField('money');
			},

			// 触发提交表单
			submit() {
				let that=this;
				this.$refs.form
					.validate()
					.then(res => {
						if (this.form.money < this.withdrawMin) {
							uni.showToast({
								icon: "none",
								title: '提现金额不能低于' + this.withdrawMin + '元',
							})
						} else {
							uni.showModal({
								title:"温馨提示",
								content: '请确定账号信息正确，账号错误将提现失败',
								success(res) {
									if (res.confirm) {
										that.doWithdraw();
									}
								}
							})
						}
					})
					.catch(err => {
						console.log('表单错误信息：', err);
					});
			},

			async doWithdraw() {
				let res = await this.$apis.withdrawApply(this.form);
				console.log('res=====', res);
				if (res) {
					this.getAccount();
					let that=this;
					uni.showModal({
						showCancel: false,
						content: '提交成功，等待后台审核',
						success: function() {
							that.doScribe();
						}
					})
				}
			},
			
			doScribe(){
				requestSubscribe(1,
					res => {
						console.log("订阅成功：", res)
						console.log("res：", res)
						if (res == true) {
							uni.$u.toast('订阅成功');
						} else {
							uni.$u.toast('订阅失败');
						}
						uni.navigateBack({
							delta: 1
						})
					}, err => {
						console.log("订阅失败：", err)
						uni.$u.toast('订阅失败');
						uni.navigateBack({
							delta: 1
						})
					});
			},
		}
	};
</script>

<style lang="scss">
	page {
		background-color: #f5f6fa;
	}

	/deep/ .u-form-item__body__left__content__label {
		height: 40px;
		line-height: 40px;
		font-size: 18px;
		color: #333;
		font-weight: bold;
		min-width: 90px;
	}

	/deep/ .u-form-item__body__left__content__required {
		height: 40px;
		line-height: 40px;
	}

	.body {
		padding: 20upx;
	}

	.tips {
		padding: 30upx 0;

		text {
			line-height: 1.6;
			font-size: 14px;
			color: #666;
		}
	}

	.btn {
		background-color: $main-color;
		color: #ffffff;
		font-size: 30upx;
		margin-top: 30upx;
	}

	.forms {
		background-color: #ffffff;
		padding: 20upx;
		border-radius: 20upx;
		margin-top: 20upx;
	}

	.balance {
		display: flex;
		flex-direction: row;
		align-items: center;
		padding: 20upx 0;
		margin-left: 10upx;

		.money {
			color: $main-color;
			font-size: 34upx;
			font-weight: bold;
			padding: 0 10upx;
		}

		.btn-w {
			margin-left: 40upx;
			color: $main-color;
		}
	}

	.u-tag-item {
		display: flex;
		flex-direction: row;
		flex-wrap: wrap;
		align-items: center;

		.tag {
			margin-right: 15px;
		}
	}
</style>