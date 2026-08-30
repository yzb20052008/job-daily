<template>
	<view class="page">
		<!-- 公共组件-每个页面必须引入 -->
		<public-module></public-module>
		<view class="reg">
			<u-form labelPosition="left" labelWidth="auto" :model="form" :rules="rules" ref="form">
				<u-form-item label="证书名称" labelPosition="left" prop="certName" borderBottom>
					<u-input v-model="form.certName" maxlength="10" border="none" placeholder="请输入证书名称"></u-input>
				</u-form-item>
				<u-form-item label="上传证书图片(必填)" labelPosition="top" prop="certImg">
					<u-upload :fileList="fileList1" @afterRead="afterRead" @delete="deletePic" name="1"
						:multiple="false" :maxCount="1" uploadText="上传证书" width="100" height="100">
					</u-upload>
				</u-form-item>
			</u-form>
		</view>
		<view class="bottom">
			<button class="throttle1" v-if="id" @tap="$u.throttle(toDelete, 500)">删除</button>
			<button class="throttle" @tap="$u.throttle(btnAClick, 500)">保存</button>
		</view>
	</view>
</template>

<script>
	import BaseUrl from '@/config/baseUrl.js';
	export default {
		data() {
			return {
				id: '',
				fileList1: [],
				form: {
					certName: '',
					certImg: '',
				},
				rules: {
					certName: [{
						required: true,
						message: '请输入证书名称',
						trigger: ['blur', 'change']
					}],
					certImg: [{
						required: true,
						message: '请上传证书图片',
						trigger: ['blur', 'change']
					}],
				}
			};
		},

		onLoad(options) {
			if (options.id) {
				this.id = options.id;
				this.getResumeCert();
			}
		},

		methods: {

			async getResumeCert() {
				let res = await this.$apis.getResumeCert({
					params: {
						id: this.id
					}
				});
				console.log('res=====', res);
				if (res) {
					this.form = res;
					if (res.certImg) {
						let imgList = res.certImg.split(',');
						//照片
						imgList.forEach(item => {
							this.fileList1.push({
								url: item
							})
						});
					}
				}
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
						header: {
							'X-Access-Token': (uni.getStorageSync('userInfo') && uni.getStorageSync('userInfo').token) || ''
						},
						formData: {
							biz: "worker/skill"
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

			btnAClick() {
				console.log('btnClick');
				let that = this;
				console.log('this.fileList1==',this.fileList1);
				if (this.fileList1) {
					let arr = [];
					this.fileList1.forEach(item => {
						arr.push(item.url);
					});
					if (arr.length > 0) {
						this.form.certImg = arr.join(',');
					}else{
						this.form.certImg =''
					}
				}else{
					this.form.certImg =''
				}
				console.log('form',this.form);
				this.$refs.form
					.validate()
					.then(res => {
						uni.showModal({
							title: '温馨提示',
							content: '确定保存？',
							success: res => {
								if (res.confirm) {
									that.addResumeCert();
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
			async addResumeCert() {
				let res;
				if (this.form.id) {
					res = await this.$apis.updateResumeCert(this.form);
				} else {
					res = await this.$apis.addResumeCert(this.form);
				}
				console.log(res);
				if (res) {
					uni.navigateBack();
				}
			},
			
			toDelete(){
				let that=this;
				uni.showModal({
					title: '温馨提示',
					content: '确定删除？',
					success: res => {
						if (res.confirm) {
							that.delResumeCert();
						} else if (res.cancel) {
							console.log('用户点击取消');
						}
					}
				});
			},

			async delResumeCert() {
				let res = await this.$apis.delResumeCert({id:this.id});
				console.log(res);
				if (res) {
					uni.navigateBack();
				}
			},


			toRule() {
				uni.$u.route("/pages/job/authRule");
			},

			selectCity() {
				uni.$u.route("/pages/common/selectCity");
			},

			selectLocation() {
				uni.chooseLocation({
					success: res => {
						console.log('选择详细地址结果');
						console.log(res);
						console.log(res.name);
						console.log(res.address);
						if (res.address == '') {
							return;
						}
						this.form.address = res.name;
						this.form.latitude = res.latitude;
						this.form.longitude = res.longitude;
					}
				});
			},
		}
	};
</script>

<style lang="scss">
	page {
		background-color: #f5f6fa;
	}

	.reg {
		margin: 20upx;
		background-color: #fff;
		padding: 20upx;
		border-radius: 20upx;

		/deep/ .u-form-item__body__left__content__label {
			height: 40px;
			line-height: 40px;
			font-size: 18px;
			min-width: 80px;
			color: #333;
		}

		.base-item {
			background-color: #fff;
			padding: 20upx;
			border-radius: 30upx;
		}
	}

	.bottom {
		width: 100%;
		background: #fff;
		padding: 30upx;
		position: fixed;
		bottom: 0;
		/* 全面屏底部安全区 */
		padding-bottom: constant(safe-area-inset-bottom);
		padding-bottom: env(safe-area-inset-bottom);

		z-index: 99;
		display: flex;
		flex-direction: row;
		justify-content: center;

		.throttle1 {
			background-color: #eee;
			color: #333;
			flex: 1;
			margin-right: 30upx;
		}

		.throttle {
			background-color: #007aff;
			color: #ffffff;
			flex: 2;
		}
	}
</style>