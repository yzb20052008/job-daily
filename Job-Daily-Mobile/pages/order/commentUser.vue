<template>
	<view class="comment">
		<view class="driver">
			<view class="avatar">
				<!-- <image :src="user.avatar"></image> -->
				<u-avatar size="80" shape="circle" :src="user.avatar"></u-avatar>
			</view>
			<text style="margin-top: 20upx;">{{user.nickname}}</text>
		</view>
		<view class="title">
			<u-divider :text="divider"></u-divider>
		</view>
		<view class="star">
			<u-rate size="32" v-model="star" count="5" activeColor="#FD7716"></u-rate>
			<text class="star-tip">{{starTip}}</text>
		</view>

		<view class="content">
			<u-form labelPosition="top" labelWidth="auto" :model="form" :rules="rules" ref="form">
				<u-form-item label="评价内容" labelPosition="top" prop="content">
					<u--textarea v-model="form.content" :placeholder="placeholder"></u--textarea>
				</u-form-item>
				<u-form-item label="图片列表" labelPosition="top" prop="imgs">
					<u-upload :fileList="fileList1" @afterRead="afterRead" @delete="deletePic" name="1" multiple
						:multiple="true" :maxCount="9"></u-upload>
				</u-form-item>
				<text class="tips">温馨提示：可以提供评价图片，比如工作图片</text>
			</u-form>
		</view>
		<u-button type="warning" color="#FD7716" shape="circle" @click.stop="comment()">提交评价</u-button>
	</view>
</template>

<script>
	import {
		mapState,
		mapMutations
	} from 'vuex';
	import BaseUrl from '@/config/baseUrl.js';
	export default {
		computed: {
			...mapState(['userInfo'])
		},
		data() {
			return {
				id: '',
				userId: '',
				user: {},
				content: '',
				star: 5,
				starTip: '非常满意',
				placeholder:'对工人此次工作的评价',
				divider:'匿名评价工人',
				fileList1: [],
				form: {
					score:5,
					content: '',
					orderId:'',
					id:'',
					images:'',
					roleCode:'member',
				},
				rules: {
					contents: [{
						required: true,
						message: '请填写问题或建议',
						trigger: ['blur', 'change']
					}],
				}
			}
		},

		watch: {
			star: {
				immediate: true,
				handler(val) {
					if (val == 1) {
						this.starTip = "非常差"
					} else if (val == 2) {
						this.starTip = "较差"
					} else if (val == 3) {
						this.starTip = "一般"
					} else if (val == 4) {
						this.starTip = "满意"
					} else if (val == 5) {
						this.starTip = "非常满意"
					}
				}
			},
		},

		onLoad(options) {
			console.log("options===",options);
			this.id = options.id;
			this.userId = options.userId;
			this.getUserInfo();
			if(this.userInfo.memberRole=='member'){
				this.placeholder="针对本次工作对老板的评价";
				this.divider="匿名评价老板";
			}else{
			}
		},

		methods: {

			async getUserInfo() {
				let params = {
					id: this.userId
				}
				let res = await this.$apis.getUserInfo({
					params: params
				});
				console.log("res===", res)
				this.user = res;
			},

			comment() {
				let that = this;
				uni.showModal({
					title: "温馨提示",
					content: "确定提交评价？",
					confirmText: "确定",
					cancelText: "取消",
					success: (data) => {
						if (data.confirm) {
							that.doComment();
						}
					}
				});
			},

			async doComment() {
				if (this.fileList1) {
					let arr = [];
					this.fileList1.forEach(item => {
						arr.push(item.url);
					});
					if (arr.length > 0) {
						this.form.images = arr.join(',');
					}
				}
				this.form.score=this.star;
				this.form.orderId=this.id;
				if(this.userInfo.memberRole=='company'){
					this.form.roleCode='company';
				}
				console.log("params====", this.form)
				let res = await this.$apis.addEvaluate(this.form);
				console.log("addEvaluate", res);
				if (res) {
					uni.$u.toast('评价成功');
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
						header: {
							'X-Access-Token': (uni.getStorageSync('userInfo') && uni.getStorageSync('userInfo').token) || ''
						},
						formData: {
							biz: "job/comment"
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
	.comment {
		padding: 30upx;
	}

	.driver {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		margin: 30upx 0;

		// .avatar {
		// 	position: relative;
		// 	width: 200upx;
		// 	height: 200upx;
		// 	border: 1upx solid #eee;
		// 	border-radius: 50%;
		// 	margin-bottom: 20upx;

		// 	image {
		// 		width: 100%;
		// 		height: 100%;
		// 		border-radius: 50%;
		// 	}
		// }
	}

	.title {
		margin-top: 20upx;
	}

	.star {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		padding: 20upx 0;

		.star-tip {
			color: #FD7716;
			font-size: 32upx;
			margin-top: 20upx;
			font-weight: bold;
		}

	}

	.tags {
		display: flex;
		flex-direction: column;

		.tag-title {
			font-size: 30upx;
			color: #333;
			margin-top: 30upx;
			margin-bottom: 30upx;
		}

		.tag-item {
			display: flex;
			flex-direction: row;
			flex-wrap: wrap;
		}

		.tag-name {
			display: flex;
			border: 1upx solid #eee;
			padding: 10upx 25upx;
			margin-right: 30upx;
			font-size: 30upx;
			color: #666;
			margin-bottom: 20upx;
			border-radius: 10upx;
		}

		.tag-selected {
			background-color: #FEF2E8;
			border: 1upx solid #FD7716;
		}

		.tag-unselected {
			background-color: #fff;
			border: 1upx solid #eee;
		}
	}

	.content {
		margin: 30upx 0;
	}
	
	.tips {
		font-size: 26upx;
		color: #999;
		margin-bottom: 20upx;
	}
</style>