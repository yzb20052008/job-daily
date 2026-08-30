<template>
	<view>
		<public-module></public-module>
		<view class="group">
			<view class="cell u-flex" @click.stop="upAvatar">
				<view class="title u-flex-m">头像</view>
				<!-- <image :src="avatar" class="avatar"></image> -->
				<view class="avatar">
					<u-avatar size="70" shape="square" :src="userInfo.avatar"></u-avatar>
					<!-- <yq-avatar :bgImage="imgBg" selWidth="480upx" selHeight="480upx" ref='avatar' fileType='png'
						:avatarSrc="userInfo.avatar" @upload="myUpload" quality="1" inner=true
						avatarStyle="width: 140upx; height: 140upx;border-radius:20upx">
					</yq-avatar> -->
				</view>
				<u-icon name="arrow-right" :size="14" color="#999"></u-icon>
			</view>
			<view class="cell u-flex" @click="inputDialogToggle('nickname', '修改昵称', 'input')">
				<view class="title u-flex-m">昵称</view>
				<view class="text">{{ userInfo.nickname || '未设置' }}</view>
				<u-icon name="arrow-right" :size="14" color="#999"></u-icon>
			</view>
			<view class="cell u-flex" @click="sexShow=true">
				<view class="title u-flex-m">性别</view>
				<view class="text">{{ userInfo.sex | formatSex }}</view>
				<u-icon name="arrow-right" :size="14" color="#666"></u-icon>
			</view>
			<view class="cell u-flex" @click="updateContact">
				<view class="title u-flex-m">绑定手机</view>
				<view class="text">{{ userInfo.phone || '' }}</view>
				<u-icon name="arrow-right" :size="14" color="#666"></u-icon>
			</view>
		</view>
		<view class="u-p-t-50 u-p-l-30 u-p-r-30">
			<u-button shape="square" type="error" @click="loginOut">退出登录</u-button>
		</view>
		<uni-popup ref="inputDialog" type="dialog">
			<uni-popup-dialog ref="inputClose" type="info" :mode="inputDialogMode" :title="inputDialogTitle"
				:value="inputDialogValue" placeholder="请输入内容" @confirm="dialogInputConfirm"></uni-popup-dialog>
		</uni-popup>
		<u-action-sheet :show="sexShow" @close="sexShow = false" :actions="sexActions" @select="selectSex"
			cancelText="取消"></u-action-sheet>
	</view>
</template>

<script>
	import {
		mapState,
		mapMutations
	} from 'vuex';
	import {
		getDictItems
	} from '@/config/api.js';
	import BaseUrl from '@/config/baseUrl.js';
	export default {
		computed: {
			...mapState(['userInfo'])
		},
		data() {
			return {
				imgBg: "",
				inputDialogTitle: '',
				inputDialogValue: '',
				inputDialogMode: 'input',
				inputDialogName: '',

				sexShow: false,
				sexActions: [{
						value: 1,
						name: '男'
					},
					{
						value: 2,
						name: '女'
					},
					{
						value: 0,
						name: '未知'
					}
				],

				avatar: require('@//static/logo.png'),
				tmpImageUrl: '',
				driverInfo: {},
			};
		},

		filters: {
			formatSex(val) {
				if (val == 1) {
					return '男';
				} else if (val == 2) {
					return '女';
				} else if (val == 0) {
					return '未知';
				}
			}
		},

		onLoad() {
			// this.init();
		},

		onUnload() {
			// 移除监听事件 优化性能
			uni.$off('city');
		},

		onShow() {
			if (this.userInfo.ifDriver) {
				this.getDriverInfo();
			}
		},
		methods: {
			...mapMutations(['emptyUserInfo', 'setUserInfo']),
			onJump(url) {
				console.log('===onJump===', url);
				uni.navigateTo({
					url: url
				});
			},

			getDriverInfo() {
				this.$apis
					.getDriverInfo()
					.then(res => {
						console.log('getDriverInfo', res);
						if (res) {
							this.driverInfo = res;
						}
					})
					.catch(err => {
						console.log(err, 'catch');
					});
			},

			init() {
				let httpData = {
					dictCode: 'sex'
				};
				getDictItems({
						params: httpData,
						custom: {
							isFactory: true
						}
					})
					.then(res => {
						console.log('res====', res);
						if (res) {
							this.sexActions = res;
						}
					})
					.catch(err => {
						console.log(err, 'catch');
					});
			},

			selectSex(val) {
				console.log('===selectSex===', val);
				let param = {
					sex: val.value
				};
				this.$apis
					.updateUser(param)
					.then(res => {
						console.log('xxxxxxxxxxxxxx', res);
						this.userInfo.sex = val.value;
						this.setUserInfo(this.userInfo);
						uni.$u.toast('操作成功');
					})
					.catch(err => {
						console.log(err, 'catch');
					});
			},

			inputDialogToggle(name, title, mode) {
				this.inputDialogTitle = title;
				this.inputDialogMode = mode;
				this.inputDialogName = name;
				if (name == "sign") {
					this.inputDialogValue = this.userInfo.sign;
				} else if (name == "contact") {
					this.inputDialogValue = this.userInfo.phone;
				} else {
					this.inputDialogValue = this.userInfo.nickname;
				}
				this.$refs.inputDialog.open();
			},

			dialogInputConfirm(val) {
				if(!val){
					uni.$u.toast('内容不能为空');
					return;
				}
				if(val.length>10){
					uni.$u.toast('昵称不能超过10个字符');
					return;
				}
				uni.showLoading();
				this.value = val;
				// this.$refs.inputDialog.close();
				if (this.inputDialogName == 'contact') {
					// this.updateDriverInfo(val);
				} else {
					let param;
					if (this.inputDialogName == 'nickname') {
						param = {
							nickname: val
						};
					} else if (this.inputDialogName == 'sign') {
						param = {
							sign: val
						};
					}
					this.$apis
						.updateUser(param)
						.then(res => {
							uni.hideLoading();
							if (this.inputDialogName == 'nickname') {
								this.userInfo.nickname = val;
							} else if (this.inputDialogName == 'sign') {
								this.userInfo.sign = val;
							}
							this.setUserInfo(this.userInfo);
							uni.$u.toast('操作成功');
						})
						.catch(err => {
							uni.hideLoading();
							console.log(err, 'catch');
						});
				}
			},

			updateDriverInfo(val) {
				let param = {
					id: this.driverInfo.id,
					contact: val
				};
				this.$apis
					.updateDriver(param)
					.then(res => {
						uni.hideLoading();
						console.log('xxxxxxxxxxxxxx', res);
						uni.$u.toast('操作成功');
						this.getDriverInfo();
					})
					.catch(err => {
						uni.hideLoading();
						console.log(err, 'catch');
					});
			},

			updateContact() {
				uni.$u.route('/pages/my/updatePhone');
			},

			loginOut() {
				var that = this;
				uni.showModal({
					title: '退出登录',
					content: '你确定退出登录吗？',
					success(res) {
						if (res.confirm) {
							that.emptyUserInfo();
							setTimeout(() => {
								uni.navigateBack({
									delta: 1
								});
							}, 500);
						} else if (res.cancel) {}
					}
				});
			},
			// 修改头像
			upAvatar() {
				console.log("===================upAvatar===================")
				// this.clk();
				let that = this;
				uni.chooseImage({
					success(res) {
						let tempFilePaths = res.tempFilePaths;
						that.tmpImageUrl = tempFilePaths[0];
						that.uploadFile();
					}
				});
			},

			// 上传图片（后台中转）
			uploadFile() {
				uni.showLoading({
					title: '图片上传中'
				});
				let that = this;
				uni.uploadFile({
					url: BaseUrl.baseUrl + "/api/file/upload",
					filePath: this.tmpImageUrl,
					name: 'file',
					header: {
						'X-Access-Token': (this.userInfo && this.userInfo.token) || ''
					},
					formData: {
						biz: "job/user"
					},
					success: (res) => {
						let data = res.data;
						let parsed = typeof data === 'string' ? JSON.parse(data) : data;
						let imgUrl = parsed.message;
						console.log('上传成功', imgUrl);
						that.userInfo.avatar = imgUrl;
						let param = {
							avatar: imgUrl
						};
						this.$apis
							.updateUser(param)
							.then(res => {
								this.setUserInfo(this.userInfo);
								uni.$u.toast('操作成功');
							})
							.catch(err => {
								console.log(err, 'catch');
							});
						uni.hideLoading();
					},
					fail: (res) => {
						console.log('上传失败', res);
						uni.hideLoading();
						uni.$u.toast('上传失败');
					},
				});
			},




			listenCitySelect() {
				let that = this;
				uni.$on('city', res => {
					console.log('city', res);
					let param = {
						province: res.province,
						city: res.pcity,
						area: res.city
					};
					this.$apis
						.updateUser(param)
						.then(res => {
							console.log('xxxxxxxxxxxxxx', res);
							that.userInfo.province = param.province;
							that.userInfo.city = param.city;
							that.userInfo.area = param.area;
							that.setUserInfo(that.userInfo);
							uni.$u.toast('操作成功');
						})
						.catch(err => {
							console.log(err, 'catch');
						});
				});
			},

			clk() {
				let avatar = this.$refs.avatar;
				avatar.fChooseImg(1, {
					selWidth: "480upx",
					selHeight: "480upx",
					expWidth: "480px",
					expHeight: "480upx",
					inner: false
				}, {
					data: 'xxx'
				});
			},
			myUpload(rsp) {
				console.log(rsp);
				this.tmpImageUrl = rsp.path;
				this.uploadFile();
			}
		}
	};
</script>

<style lang="scss">
	page {
		background: #f5f6fa;
	}

	.group {
		padding: 0 30rpx;
		margin-top: 20rpx;
		background: #fff;
	}

	.cell {
		position: relative;
		// line-height: normal;
		padding: 24rpx 0;
		border-bottom: 1rpx solid #eee;

		.title {
			font-size: 30rpx;
			color: #333;
		}

		.text {
			font-size: 28rpx;
			color: #666;
			margin-right: 10rpx;
			width: 60%;
			text-align: end;

			text-overflow: -o-ellipsis-lastline;
			overflow: hidden;
			text-overflow: ellipsis;
			display: -webkit-box;
			-webkit-line-clamp: 1;
			-webkit-box-orient: vertical;
		}

		.itemButton {
			border-radius: 0;
			text-align: left;
			opacity: 0;
			width: 100%;
			height: 100%;
			position: absolute;
			left: 0;
			top: 0;
		}

		&:last-child {
			border-bottom: none;
		}

		.avatar {
			margin-right: 10rpx;
		}

		.yzb-erweima {
			font-size: 40upx;
			color: #888;
			margin-right: 10rpx;
		}
	}
</style>