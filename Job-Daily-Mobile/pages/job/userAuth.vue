<template>
	<view class="content">
		<!-- 公共组件-每个页面必须引入 -->
		<public-module></public-module>
		<template v-if="form.authStatus==1">
			<view class="realname-auth">
				<view class="auth-info">
					<view class="row-align">
						<text class="realname">{{form.realname}}</text>
						<text class="realname-tag">已实名</text>
					</view>
					<text class="id-num">{{form.idNo}}</text>
					<text class="id-type">身份证</text>
					<view class="anquan">
						<text class="yzb yzb-anquan"></text>
						<text>个人信息安全保障中</text>
					</view>
				</view>
				<text class="tips">您的身份信息仅用于身份核实,如需修改请联系客服</text>
				<text class="protocol" @click="toRule">《零工招聘实名认证服务协议》</text>
			</view>
		</template>
		<template v-else>
			<view class="status" v-if="form.authStatus>-1">
				<view class="row-align">
					<text class="status-icon yzb yzb-jubao1"></text>
					<text class="status-title" v-if="form.authStatus==0">待审核</text>
					<text class="status-title" v-if="form.authStatus==1">认证通过</text>
					<text class="status-title" v-if="form.authStatus==2">认证不通过</text>
				</view>
				<view class="status-reason" v-if="form.authRemark">
					{{form.authRemark}}
				</view>
				<view class="status-reason" v-if="form.authStatus==0">
					待审核状态下，可重新提交认证信息。
				</view>
			</view>
			<view class="reg">
				<u-form labelPosition="left" labelWidth="auto" :model="form" :rules="rules" ref="form">
					<view class="title">
						<text>请填写您的真实身份信息</text>
						<text>信息仅用于身份核实</text>
					</view>
					<u-form-item label="姓名" labelPosition="top" prop="realname" borderBottom required>
						<u-input v-model="form.realname" maxlength="10" border="none" placeholder="请输入本人姓名"></u-input>
					</u-form-item>
					<u-form-item label="身份证号码" labelPosition="top" prop="idNo" borderBottom required>
						<u-input placeholder="请输入本人身份证号码" maxlength="30" border="none" v-model="form.idNo" />
					</u-form-item>
					<u-form-item labelPosition="top" label="身份证照片" prop="" required>
						<view class="img-slot">
							<view class="img-item">
								<u-upload :disabled="disabled" :deletable="!disabled" :fileList="fileList1" uploadText="正面" @afterRead="afterRead" @delete="deletePic"
									name="1" multiple :multiple="false" :maxCount="1" width="150" height="100">
									<image src="../../static/id-front.png" mode="widthFix"></image>
								</u-upload>
								<text class="tips">身份证人像面</text>
							</view>
							<view class="img-item img-ml">
								<u-upload :disabled="disabled" :deletable="!disabled" :fileList="fileList2" uploadText="方面" @afterRead="afterRead" @delete="deletePic"
									name="2" multiple :multiple="false" :maxCount="1" width="150" height="100">
									<image src="../../static/id-back.png" mode="widthFix"></image>
								</u-upload>
								<text class="tips">身份证国徽面</text>
							</view>
						</view>
					</u-form-item>
				</u-form>
			</view>
			<view class="bottom"><button class="throttle" @tap="$u.throttle(btnAClick, 500)">提交</button></view>
			<view class="protocol-info">
				<u-checkbox-group @change="checkboxChange">
					<u-checkbox name="1" shape="circle" labelSize="12" iconSize="14" label="您已阅读并同意"></u-checkbox>
					<text class="protocol" @click="toRule">《零工招聘实名认证服务协议》</text>
				</u-checkbox-group>
			</view>
			<u-modal @confirm="modalConfirm" :show="modalShow" :title="modalTitle" :content="modalContent"></u-modal>
		</template>
	</view>
</template>

<script>
	import BaseUrl from '@/config/baseUrl.js';
	import {
		requestSubscribe
	} from '@/config/common';
	import {
		mapState
	} from 'vuex';
	export default {
		computed: {
			...mapState(['userInfo'])
		},
		data() {
			return {
				fileList1: [],
				fileList2: [],
				form: {
					realname: '',
					idNo: '',
					authStatus: -1
				},
				checked: false,
				modalShow: false,
				modalTitle: '温馨提示',
				modalContent: '提交成功！',

				rules: {
					realname: [{
							required: true,
							message: '请输入本人姓名',
							trigger: ['blur', 'change']
						},
						{
							// 自定义验证函数，见上说明
							validator: (rule, value, callback) => {
								return uni.$u.test.rangeLength(value, [2, 10]);
							},
							message: '长度2-10个字符',
							// 触发器可以同时用blur和change
							trigger: ['change', 'blur'],
						}
					],

					idNo: [{
							required: true,
							message: '请输入本人身份证号码',
							trigger: ['blur', 'change']
						},
						{
							// 自定义验证函数，见上说明
							validator: (rule, value, callback) => {
								return uni.$u.test.idCard(value);
							},
							message: '输入有效的身份证号',
							// 触发器可以同时用blur和change
							trigger: ['change', 'blur'],
						}

					],
				}
			};
		},
		onLoad() {
			if (this.userInfo.realNameAuth != -1) {
				this.getRealNameAuth();
			}
		},

		onReady() {
			this.$refs.form.setRules(this.rules)
		},

		methods: {

			async getRealNameAuth() {
				let res = await this.$apis.getRealNameAuth();
				console.log("res", res);
				this.form = res;
				this.fileList1.push({
					url: res.idCardFront
				});
				this.fileList2.push({
					url: res.idCardBack
				});
			},

			btnAClick() {
				console.log('btnClick');
				let that = this;
				this.$refs.form
					.validate()
					.then(res => {
						if (this.fileList1.length > 0) {
							let arr = [];
							this.fileList1.forEach(item => {
								arr.push(item.url);
							});
							if (arr.length > 0) {
								this.form.idCardFront = arr.join(',');
							}
						} else {
							uni.$u.toast('身份证人像面不能为空');
							return;
						}
						if (this.fileList2.length > 0) {
							let arr = [];
							this.fileList2.forEach(item => {
								arr.push(item.url);
							});
							if (arr.length > 0) {
								this.form.idCardBack = arr.join(',');
							}
						} else {
							uni.$u.toast('身份证国徽面不能为空');
							return;
						}
						//判定是否勾选
						if (that.checked == false) {
							uni.$u.toast('请勾选服务协议');
							return;
						}
						uni.showModal({
							title: '温馨提示',
							content: '确定提交实名认证？',
							success: res => {
								if (res.confirm) {
									that.addRealNameAuth();
								} else if (res.cancel) {
									console.log('用户点击取消');
								}
							}
						});
					})
					.catch(err => {
						// uni.$u.toast('校验失败');
						console.log('err', err);
						let key = '#' + err[0].key
						uni.pageScrollTo({
							selector: key,
							duration: 500,
							offsetTop: 10,
							success: function() {
							}
						})
					});
			},
			
			doScribe(){
				requestSubscribe(0,
					res => {
						console.log("订阅成功：", res)
						console.log("res：", res)
						if (res == true) {
							uni.$u.toast('订阅成功');
						} else {
							uni.$u.toast('订阅失败');
						}
					}, err => {
						console.log("订阅失败：", err)
						uni.$u.toast('订阅失败');
					});
			},
			
			toRule() {
				uni.$u.route("/pages/job/authRule");
			},

			checkboxChange(e) {
				console.log("checkboxChange==", e);
				if (e.length == 1) {
					this.checked = true;
				} else {
					this.checked = false;
				}
			},

			async addRealNameAuth() {
				let res = await this.$apis.addRealNameAuth(this.form);
				console.log(res);
				if (res) {
					this.modalShow = true;
					this.doScribe();
				}
			},

			modalConfirm() {
				this.show = false;
				uni.navigateBack();
			},
			
			// 删除图片
			deletePic(event) {
				if(this.disabled){
					return;
				}
				this[`fileList${event.name}`].splice(event.index, 1);
			},
			// 新增图片
			async afterRead(event) {
				// 当设置 mutiple 为 true 时, file 为数组格式，否则为对象格式
				let lists = [].concat(event.file);
				let fileListLen = this[`fileList${event.name}`].length;
				lists.map(item => {
					this[`fileList${event.name}`].push({
						...item,
						status: 'uploading',
						message: '上传中'
					});
				});
				for (let i = 0; i < lists.length; i++) {
					const result = await this.uploadFilePromise(lists[i].url);
					let item = this[`fileList${event.name}`][fileListLen];
					this[`fileList${event.name}`].splice(
						fileListLen,
						1,
						Object.assign(item, {
							status: 'success',
							message: '',
							url: result
						})
					);
					fileListLen++;
				}
				console.log('上传成功', this.fileList1);
			},
			uploadFilePromise(url) {
				return new Promise((resolve, reject) => {
					uni.uploadFile({
						url: BaseUrl.baseUrl + "/api/file/upload",
						filePath: url,
						name: 'file',
						header: {
							'X-Access-Token': (uni.getStorageSync('userInfo') && uni.getStorageSync('userInfo').token) || ''
						},
						formData: {
							biz: "job/idcard"
						},
						success: (res) => {
							let data = res.data;
							let imgUrl = JSON.parse(data).message;
							console.log('上传成功', imgUrl);
							resolve(imgUrl);
						},
						fail: (res) => {
							console.log('上传失败', res);
						},
					});
				});
			},
		}
	};
</script>

<style lang="scss">
	.realname-auth {
		display: flex;
		flex-direction: column;
		padding: 30upx;

		.auth-info {
			display: flex;
			flex-direction: column;
			background-color: #E5F4FF;
			padding: 30upx;
			border-radius: 20upx;
			border: 10upx solid #40BAFF;

			.realname {
				font-weight: bold;
				font-size: 20px;
				margin-right: 20upx;
			}

			.realname-tag {
				background-color: $main-color;
				color: #fff;
				font-size: 12px;
				padding: 4upx 10upx;
				border-radius: 8upx;

			}

			.id-num {
				padding: 15upx 0;
				font-size: 15px;
			}

			.id-type {
				font-size: 16px;
				color: #666;
			}

			.anquan {
				margin-top: 100upx;

				.yzb-anquan {
					margin-right: 10upx;
				}

				text {
					color: #007aff;
					font-size: 15px;
				}
			}
		}

		.tips {
			font-size: 15px;
			color: #999;
			margin-top: 30upx;
		}

		.protocol {
			margin-top: 20upx;
			font-size: 15px;
			color: $main-color;
		}
	}

	.row-align {
		display: flex;
		flex-direction: row;
		align-items: center;
	}

	.status {
		background-color: #FFF5EC;
		padding: 30upx;
		display: flex;
		flex-direction: column;
		align-items: center;

		.status-icon {
			font-size: 20px;
			color: $uni-color-error;
			font-weight: bold;
		}

		.status-title {
			font-size: 18px;
			font-weight: bold;
			color: $uni-color-error;
			margin-left: 10upx;
		}

		.status-reason {
			margin-top: 10upx;
			color: #666;
		}
	}

	.reg {
		padding: 30upx;
		// margin-bottom: 200upx;
		padding-bottom: 200upx;

		.title {
			width: 100%;
			display: flex;
			flex-direction: column;
			padding: 30upx 0;

			text:nth-child(1) {
				font-weight: bold;
				font-size: 18px;
			}

			text:nth-child(2) {
				font-size: 14px;
				margin-top: 10upx;
				color: #999;
			}

		}

		.tips {
			font-size: 26upx;
			color: #999;
			margin-bottom: 20upx;
		}
		
		.img-slot {
			display: flex;
			flex-direction: row;
			align-items: center;
			justify-content: center;
			margin-top: 20upx;
		
			.img-item {
				display: flex;
				flex-direction: column;
				align-items: center;
		
				image {
					width: 150px;
					height: 100px;
				}
			}
		
			.img-ml {
				margin-left: 40upx;
			}
		}
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

	.protocol-info {
		margin: 20upx;

		.protocol {
			font-size: 12px;
			color: $main-color;
		}
	}
</style>