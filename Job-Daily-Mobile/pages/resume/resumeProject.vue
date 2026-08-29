<template>
	<view class="content">
		<view class="area">
			<u--textarea v-model="form.descr" :customStyle="{borderRadius:'10px',whiteSpace:'pre-wrap'}"
				placeholderStyle="color:red" :placeholder="placeholder" count border="none" height="200"
				maxlength="300"></u--textarea>
		</view>
		<view class="other-info">
			<text class="title">上传项目资料（选填）</text>
			<text class="tips">可上传工地现场、干活实操、项目成果照片等</text>
			<u-upload :fileList="fileList1" @afterRead="afterRead" @delete="deletePic" name="1" :multiple="true"
				:maxCount="3">
			</u-upload>
		</view>
		<view class="bottom">
			<button class="throttle1" v-if="id" @tap="$u.throttle(toDelete, 500)">删除</button>
			<button class="throttle" @tap="$u.throttle(btnAClick, 500)">保存</button>
		</view>
	</view>
</template>

<script>
	import uploadImage from '@/plugins/ossutil/uploadFile';
	import BaseUrl from '@/config/baseUrl.js';
	export default {
		data() {
			return {
				id: '',
				data: "",
				placeholder: "例如：2020-2022年在碧桂园项目中，做挑架、爬架、铝膜、资料现场等。",
				fileList1: [],
				form: {
					descr: "",
					url: ''
				}
			}
		},

		onLoad(options) {
			if (options.id) {
				this.id = options.id;
				this.getResumeExp();
			}
		},

		methods: {

			async getResumeExp() {
				let res = await this.$apis.getResumeExp({
					params: {
						id: this.id
					}
				});
				console.log('res=====', res);
				if (res) {
					this.form = res;
					if (res.url) {
						let imgList = res.url.split(',');
						//照片
						imgList.forEach(item => {
							this.fileList1.push({
								url: item
							})
						});
					}
				}
			},

			btnAClick() {
				console.log('btnClick');
				let that = this;
				if (!this.form.descr) {
					uni.$u.toast('内容不能为空');
					return;
				}
				uni.showModal({
					title: '温馨提示',
					content: '确定保存？',
					success: res => {
						if (res.confirm) {
							that.updateResumeExp();
						} else if (res.cancel) {
							console.log('用户点击取消');
						}
					}
				});
			},

			async updateResumeExp() {
				if (this.fileList1) {
					let arr = [];
					this.fileList1.forEach(item => {
						arr.push(item.url);
					});
					if (arr.length > 0) {
						this.form.url = arr.join(',');
					} else {
						this.form.url = ''
					}
				} else {
					this.form.url = ''
				}
				let res;
				if (this.form.id) {
					res = await this.$apis.updateResumeExp(this.form);
				} else {
					res = await this.$apis.addResumeExp(this.form);
				}
				console.log(res);
				if (res) {
					uni.navigateBack();
				}
			},


			toDelete() {
				let that = this;
				uni.showModal({
					title: '温馨提示',
					content: '确定删除？',
					success: res => {
						if (res.confirm) {
							that.delResumeExp();
						} else if (res.cancel) {
							console.log('用户点击取消');
						}
					}
				});
			},

			async delResumeExp() {
				let res = await this.$apis.delResumeExp({
					id: this.id
				});
				console.log(res);
				if (res) {
					uni.navigateBack();
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
						formData: {
							biz: "worker/project"
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
	}
</script>

<style lang="scss">
	page {
		background-color: #f5f6fa;
	}

	.area {
		padding: 20upx;
	}

	.other-info {
		margin: 20upx;
		padding: 20upx;
		display: flex;
		flex-direction: column;
		background-color: #fff;
		border-radius: 20upx;

		.title {
			font-size: 18px;
			color: #333;
		}

		.tips {
			font-size: 14px;
			color: #999;
			padding: 20upx 0;
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