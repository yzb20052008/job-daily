<template>
	<view class="content">
		<!-- 公共组件-每个页面必须引入 -->
		<public-module></public-module>
		<template v-if="form.authStatus==1">
			<view class="realname-auth">
				<view class="auth-info">
					<view class="row-align">
						<text class="realname">{{form.realName}}</text>
						<text class="realname-tag">已认证</text>
					</view>
					<text class="id-num">法人姓名：{{form.legalPerson}}</text>
					<text class="id-num">信用代码：{{form.identity}}</text>
					<!-- <text class="id-type">营业执照</text> -->
					<view style="margin-top: 20upx;">
						<u-upload :fileList="fileList1" :deletable="false" name="1"
							:multiple="false" :maxCount="1" uploadText="上传照片" width="120" height="120">
						</u-upload>
					</view>
					<view class="anquan">
						<text class="yzb yzb-anquan"></text>
						<text>企业信息安全保障中</text>
					</view>
				</view>
				<text class="tips">您的企业信息仅用于企业认证核实,如需修改请联系客服</text>
				<text class="protocol" @click="toRule">《零工招聘企业认证服务协议》</text>
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
						<text>请填写您的企业信息</text>
						<text>信息仅用于平台认证</text>
					</view>
					<u-form-item label="公司名称" labelPosition="top" prop="realName" borderBottom required>
						<u-input v-model="form.realName" maxlength="30" border="none" placeholder="请输入本人公司名称"></u-input>
					</u-form-item>
					<u-form-item label="法人姓名" labelPosition="top" prop="legalPerson" borderBottom required>
						<u-input placeholder="请输入法人姓名" maxlength="30" border="none" v-model="form.legalPerson" />
					</u-form-item>
					<u-form-item label="信用代码" labelPosition="top" prop="identity" borderBottom required>
						<u-input placeholder="请输入信用代码" maxlength="30" border="none" v-model="form.identity" />
					</u-form-item>
					<u-form-item label="营业执照" labelPosition="top" prop="img" required>
						<u-upload :fileList="fileList1" @afterRead="afterRead" @delete="deletePic" name="1"
							:multiple="false" :maxCount="1" uploadText="上传照片" width="100" height="100">
						</u-upload>
					</u-form-item>
				</u-form>
			</view>
			<view class="bottom"><button class="throttle" @tap="$u.throttle(btnAClick, 500)">提交</button></view>
			<view class="protocol-info">
				<u-checkbox-group @change="checkboxChange">
					<u-checkbox shape="circle" labelSize="12" iconSize="14" label="您已阅读并同意"></u-checkbox>
					<text class="protocol" @click="toRule">《零工招聘企业认证服务协议》</text>
				</u-checkbox-group>
			</view>
			<u-modal @confirm="modalConfirm" :show="modalShow" :title="modalTitle" :content="modalContent"></u-modal>
		</template>
	</view>
</template>

<script>
	import uploadImage from '@/plugins/ossutil/uploadFile';
	import BaseUrl from '@/config/baseUrl.js';
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
				form: {
					realName: '',
					legalPerson: '',
					businessLicense: '',
					identity: '',
					authStatus: -1
				},
				checked: false,
				modalShow: false,
				modalTitle: '温馨提示',
				modalContent: '提交成功，请随时关注审核结果！',

				rules: {
					realName: [{
						required: true,
						message: '请输入公司名称',
						trigger: ['blur', 'change']
					}],
					legalPerson: [{
						required: true,
						message: '请输入法人姓名',
						trigger: ['blur', 'change']
					}],
					identity: [{
						required: true,
						message: '请输入信用代码',
						trigger: ['blur', 'change']
					}],
				}
			};
		},
		onLoad() {
			if (this.userInfo.companyAuth != -1) {
				this.getCompanyAuth();
			}
		},
		methods: {
			async getCompanyAuth() {
				let res = await this.$apis.getCompanyAuth();
				console.log("res", res);
				this.form = res;
				if (res.businessLicense) {
					let imgList = res.businessLicense.split(',');
					//货物照片
					imgList.forEach(item => {
						this.fileList1.push({
							url: item
						})
					});
				}
			},

			btnAClick() {
				console.log('btnClick');
				let that = this;
				this.$refs.form
					.validate()
					.then(res => {
						if (that.fileList1.length == 0) {
							uni.$u.toast('请上传营业执照');
							return;
						}
						//判定是否勾选
						if (that.checked == false) {
							uni.$u.toast('请勾选服务协议');
							return;
						}
						uni.showModal({
							title: '温馨提示',
							content: '确定提交企业认证？',
							success: res => {
								if (res.confirm) {
									that.addCompanyAuth();
								} else if (res.cancel) {
									console.log('用户点击取消');
								}
							}
						});
					})
					.catch(errors => {
						// uni.$u.toast('校验失败');
					});
			},

			toRule() {
				uni.$u.route("/pages/job/authRule");
			},

			checkboxChange(e) {
				console.log("checkboxChange==", e);
				if (e.length == 1) {
					this.checked = true;
				}else{
					this.checked = false;
				}
			},

			async addCompanyAuth() {
				if (this.fileList1) {
					let arr = [];
					this.fileList1.forEach(item => {
						arr.push(item.url);
					});
					if (arr.length > 0) {
						this.form.businessLicense = arr.join(',');
					}
				}
				let res = await this.$apis.addCompanyAuth(this.form);
				console.log(res);
				if (res) {
					this.modalShow = true;
				}
			},

			modalConfirm() {
				this.show = false;
				uni.navigateBack();
			},

			// 删除图片
			deletePic(event) {
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
						formData: {
							biz: "job/company"
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
	
	.realname-auth{
		display: flex;
		flex-direction: column;
		padding: 30upx;
		
		.auth-info{
			display: flex;
			flex-direction: column;
			background-color: #E5F4FF;
			padding: 30upx;
			border-radius: 20upx;
			border: 10upx solid #40BAFF;
			
			.realname{
				font-weight: bold;
				font-size: 20px;
				margin-right: 20upx;
			}
			.realname-tag{
				background-color: $main-color;
				color: #fff;
				font-size: 12px;
				padding: 4upx 10upx;
				border-radius: 8upx;
				
			}
			
			.id-num{
				font-size: 15px;
				margin-top: 15upx;
			}
			.id-type{
				font-size: 16px;
				color: #666;
				margin-top: 15upx;
			}
			
			.anquan{
				margin-top: 100upx;
				.yzb-anquan{
					margin-right: 10upx;
				}
				text{
					color: #007aff;
					font-size: 15px;
				}
			}
		}
		
		.tips{
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