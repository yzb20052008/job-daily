<template>
	<view class="content">
		<!-- 公共组件-每个页面必须引入 -->
		<public-module></public-module>
		<view class="header">
			<text class="header-title">问题和意见</text>
			<text class="header-desc">提交使用中遇到的问题或者对本应用的建议</text>
		</view>
		<view class="reg">
			<u-form labelPosition="top" labelWidth="auto" :model="form" :rules="rules" ref="form">
				<!-- <view class="title"><text>反馈信息</text></view> -->
				<u-form-item label="问题或意见" labelPosition="top" prop="content" required>
					<u--textarea v-model="form.content" placeholder="请填写问题描述或者意见建议内容"></u--textarea>
				</u-form-item>
				<u-form-item label="图片列表" labelPosition="top" prop="imgs">
					<u-upload :fileList="fileList1" @afterRead="afterRead" @delete="deletePic" name="1" multiple :multiple="true" :maxCount="9"></u-upload>
				</u-form-item>
				<text class="tips">温馨提示：选填，可以提供问题截图</text>
				<u-form-item label="联系方式" labelPosition="top" prop="mobile" required>
					<u-input placeholder="方便我们联系您" type="number" v-model="form.mobile" prefixIcon="phone-fill" prefixIconStyle="font-size: 22px;color: #909399" />
				</u-form-item>
			</u-form>
		</view>
		<view class="bottom"><button class="throttle" @tap="$u.throttle(btnAClick, 500)">提交</button></view>
		<u-modal  @confirm="modalConfirm" :show="modalShow" :title="modalTitle" :content="modalContent"></u-modal>
	</view>
</template>

<script>
	import {
		mapState
	} from 'vuex';
import uploadImage from '@/plugins/ossutil/uploadFile';
import BaseUrl from '@/config/baseUrl.js';

export default {
	computed: {
		...mapState(['userInfo'])
	},
	data() {
		return {
			fileList1: [],
			form: {
				title: '',
				content: '',
				imgs: '',
				contact: '',
				mobile: ''
			},
			
			modalShow: false,
			modalTitle: '温馨提示',
			modalContent: '提交成功，我们会尽快处理您的问题或者建议！',
			
			rules: {
				content: [
					{
						required: true,
						message: '请填写问题或建议',
						trigger: ['blur', 'change']
					}
				],
				mobile: [
					{
						required: true,
						message: '请填写联系方式',
						trigger: ['blur', 'change']
					},
					{
						// 自定义验证函数，见上说明
						validator: (rule, value, callback) => {
							return uni.$u.test.mobile(value);
						},
						message: '输入正确的手机号',
						// 触发器可以同时用blur和change
						trigger: ['change', 'blur'],
					}
					
				],
			}
		};
	},
	onLoad() {},
	onReady() {
		this.$refs.form.setRules(this.rules)
	},
	methods: {
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
					url: BaseUrl.baseUrl+"/api/file/upload",
					filePath: url,
					name: 'file',
					formData:{biz:"job/feedback"},
					success: (res) => {
						let data=res.data;
						let imgUrl=JSON.parse(data).message;
						console.log('上传成功', imgUrl);
						resolve(imgUrl);
					},
					fail:(res)=>{
						console.log('上传失败', res);
					},
				});
			});
		},

		btnAClick() {
			console.log('btnClick');
			let that = this;
			this.$refs.form
				.validate()
				.then(res => {
					uni.showModal({
						title: '温馨提示',
						content: '确定提交意见反馈？',
						success: res => {
							if (res.confirm) {
								that.addRequire();
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

		async addRequire() {
			if (this.fileList1) {
				let arr = [];
				this.fileList1.forEach(item => {
					arr.push(item.url);
				});
				if (arr.length > 0) {
					this.form.imgs = arr.join(',');
				}
			}
			this.form.userId=this.userInfo.id;
			let res = await this.$apis.addFeedback(this.form);
			console.log(res);
			if (res) {
				this.modalShow = true;
			}
		},
		
		modalConfirm(){
			this.show = false;
			uni.navigateBack();
		}
	}
};
</script>

<style lang="scss" scoped>
.header {
	background-image: url('@/static/bg.jpg');
	height: 300upx;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	background-size: 750upx 300upx;

	.header-title {
		font-size: 50upx;
		color: #ffffff;
		font-weight: bold;
	}

	.header-desc {
		margin-top: 20upx;
		font-size: 26upx;
		color: #c0c0c0;
	}
}

.reg {
	padding: 30upx;
	// margin-bottom: 200upx;
	padding-bottom: 200upx;
	
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
	.tips {
		font-size: 26upx;
		color: #999;
		margin-bottom: 20upx;
	}
}

.bottom {
	position: fixed;
	bottom: 0upx;
	/* 全面屏底部安全区 */
	padding-bottom: constant(safe-area-inset-bottom);
	padding-bottom: env(safe-area-inset-bottom);

	width: 100%;
	background: #f5f6fa;
	z-index: 99;
	border-top: 1upx solid #dadbde;

	.throttle {
		width: 94%;
		margin: 10upx auto;
		background-color: #007aff;
		color: #ffffff;
	}
}
</style>
