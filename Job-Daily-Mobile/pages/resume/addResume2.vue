<template>
	<view class="page">
		<!-- 公共组件-每个页面必须引入 -->
		<public-module></public-module>
		<f-navbar :navbarType="5" :isShowLeft="true">
		</f-navbar>
		<view class="title">
			<text class="name">完善基础信息</text>
			<text class="tip">完善后，找工作更快</text>
		</view>
		<view class="reg">
			<u-form labelPosition="left" labelWidth="auto" :model="form" :rules="rules" ref="form">
				<view class="base-item">
					<!-- <view class="avatar-item">
						<view class="avatar-left">
							<text class="avatar-title">我的头像</text>
							<text class="avatar-tip">上传自己的照片更容易获得工作</text>
						</view>
						<view>
							<u-upload :fileList="fileList1" @afterRead="afterRead" @delete="deletePic" name="1"
								:multiple="false" :maxCount="1" uploadText="上传头像">
								<view class="upload-default">
									<u-icon name="camera" size="26" label="上传头像" labelColor="#999" color="#D3D4D6"
										labelSize="14px" labelPos="bottom"></u-icon>
								</view>
							</u-upload>
						</view>
					</view> -->
					<view id="name">
						<u-form-item label="姓名" labelPosition="left" prop="name" borderBottom required>
							<u-input v-model="form.name" maxlength="10" border="none" placeholder="请输入你的姓名"></u-input>
						</u-form-item>
					</view>
					<view id="sex">
						<u-form-item label="性别" labelPosition="left" prop="sex" borderBottom required>
							<u-radio-group v-model="form.sex">
								<u-radio label="男" name="1"></u-radio>
								<view style="margin-left: 30upx;">
									<u-radio label="女" name="2"></u-radio>
								</view>
							</u-radio-group>
						</u-form-item>
					</view>
					<view id="birthday">
						<u-form-item label="出生年月" labelPosition="left" prop="birthday" borderBottom required
							@click.native="selectTime()">
							<u-input placeholder="请选择出生年月" border="none" readonly v-model="form.birthday"
								suffixIcon="arrow-right" suffixIconStyle="font-size: 14px;color: #999" />
						</u-form-item>
					</view>
					<!-- <u-form-item label="民族" labelPosition="left" prop="nation" borderBottom>
						<u-input placeholder="请选择名族" border="none" v-model="form.nation" suffixIcon="arrow-right"
							suffixIconStyle="font-size: 14px;color: #999" />
					</u-form-item>
					<u-form-item label="身高" labelPosition="left" prop="idNumber">
						<u-input placeholder="请输入你的身高,厘米,如180" border="none" v-model="form.idNumber">
							<template slot="suffix">
								<text>厘米</text>
							</template>
						</u-input>
					</u-form-item> -->
				</view>
				<view class="work-item">
					<view id="workYear">
						<u-form-item label="工龄" labelPosition="left" prop="workYear" borderBottom required>
							<u-input placeholder="请输入你工龄" border="none" type="number" v-model="form.workYear">
								<template slot="suffix">
									<text>年</text>
								</template>
							</u-input>
						</u-form-item>
					</view>
					<u-form-item label="熟练度" labelPosition="left" prop="sex" borderBottom>
						<view class="u-tag-item">
							<view class="tag" v-for="(item, index) in skilledRadios" :key="index">
								<u-tag :text="item.name" :plain="!item.checked" size="medium" type="primary"
									:name="index" @click="skilledRadioClick">
								</u-tag>
							</view>
						</view>
					</u-form-item>
					<u-form-item label="特长标签" labelPosition="left" prop="sex">
						<view class="u-tag-item">
							<view class="tag" v-for="(item, index) in skills" :key="index">
								<u-tag :text="item" size="medium" color="#007aff" closable
									@close="removeSkill(index,item)" borderColor="#E5F4FF" bgColor="#E5F4FF"
									@click="radioClick">
								</u-tag>
							</view>
							<view class="tag" v-if="skills.length<3">
								<u-tag text="+新增 " size="medium" type="warning"
									@click="inputDialogToggle('tag', '添加标签', 'input')">
								</u-tag>
							</view>
						</view>
					</u-form-item>
					<text class="tag-tip">添加您个人特长标签，如退伍军人等,最多3个标签</text>
					<!-- <u-form-item label="人员构成" labelPosition="left" prop="sex">
						<view class="u-tag-item">
							<view class="tag" v-for="(item, index) in radios2" :key="index">
								<u-tag :text="item.name" :plain="!item.checked" size="medium" type="primary"
									:name="index" @click="radioClick2">
								</u-tag>
							</view>
						</view>
					</u-form-item> -->
				</view>
				<view class="address-item">
					<u-form-item label="手机号码" labelPosition="left" prop="phone" borderBottom>
						<u-input placeholder="请输入手机号" border="none" readonly v-model="form.phone">
							<template slot="suffix">
								<text class="update-phone" @click="updatePhone">修改</text>
							</template>
						</u-input>
					</u-form-item>
					<!-- <u-form-item label="期望工作地" labelPosition="left" prop="idNumber" borderBottom
						@click.native="selectCity()">
						<u-input placeholder="请选择期望工作地" border="none" readonly v-model="form.idNumber"
							suffixIcon="arrow-right" suffixIconStyle="font-size: 14px;color: #999" />
					</u-form-item> -->
					<view id="addressName">
						<u-form-item label="常住地" labelPosition="left" prop="addressName" required
							@click.native="selectLocation()">
							<u-input placeholder="请选择常住地址" border="none" readonly v-model="form.addressName"
								suffixIcon="arrow-right" suffixIconStyle="font-size: 14px;color: #999" />
						</u-form-item>
					</view>
				</view>
			</u-form>
		</view>
		<view class="bottom">
			<button class="throttle1" @tap="toNext">跳过</button>
			<button class="throttle" @tap="$u.throttle(btnAClick, 500)">保存</button>
		</view>
		<uni-popup ref="inputDialog" type="dialog">
			<uni-popup-dialog ref="inputClose" type="info" :mode="inputDialogMode" :title="inputDialogTitle"
				v-model="inputDialogValue" placeholder="请输入内容" @confirm="dialogInputConfirm"></uni-popup-dialog>
		</uni-popup>
		<u-datetime-picker :show="showDate" :minDate="minDate" :maxDate="maxDate" @cancel="showDate=false"
			@confirm="dateConfirm" v-model="dateValue" mode="date"></u-datetime-picker>
	</view>
</template>

<script>
	import uploadImage from '@/plugins/ossutil/uploadFile';
	import BaseUrl from '@/config/baseUrl.js';
	import {
		getRegeo
	} from '@/config/common';
	import {
		mapState,
		mapMutations,
	} from 'vuex';
	export default {
		computed: {
			...mapState(['userInfo'])
		},
		data() {
			return {
				inputDialogTitle: '',
				inputDialogValue: '',
				inputDialogMode: 'input',
				inputDialogName: '',

				showDate: false,
				dateValue: '',
				minDate: 0,
				maxDate: 0,
				skills: [],
				skilledRadios: [{
						checked: true,
						name: '小工'
					},
					{
						checked: false,
						name: '中工'
					},
					{
						checked: false,
						name: '大工'
					}
				],
				radios2: [{
						checked: true,
						name: '个人'
					},
					{
						checked: false,
						name: '班组'
					},
					{
						checked: false,
						name: '施工队'
					},
					{
						checked: false,
						name: '突击队'
					}
				],
				fileList1: [],
				form: {
					name: '',
					sex: '',
					birthday: '',

					workYear: '',
					skilled: '小工',
					phone: '',
					address: '',
					addressName: '',
					addressLat: '',
					addressLng: '',
					skills: '',
					city: '',
					cityCode: '',
					package: '',
					pcityCode: ''
				},

				rules: {
					name: [{
						required: true,
						message: '请填写姓名',
						trigger: ['blur', 'change']
					}],
					sex: [{
						required: true,
						message: '请选择性别',
						trigger: ['blur', 'change']
					}],
					birthday: [{
						required: true,
						message: '请选择出生年月',
						trigger: ['blur', 'change']
					}],
					workYear: [{
						required: true,
						message: '请输入工龄',
						trigger: ['blur', 'change']
					}],
					addressName: [{
						required: true,
						message: '请选择常住地址',
						trigger: ['blur', 'change']
					}],
				}
			};
		},
		onLoad() {
			this.form.phone = this.userInfo.phone;
			this.maxDate = new Date().getTime() - 16 * 365 * 24 * 60 * 60 * 1000;
			this.dateValue = this.maxDate;
		},
		methods: {

			removeSkill(index, item) {
				this.skills.splice(index, 1);
				console.log("remove===", item);
			},

			inputDialogToggle(name, title, mode) {
				this.inputDialogTitle = title;
				this.inputDialogMode = mode;
				this.inputDialogName = name;
				this.inputDialogValue = "";
				this.$refs.inputDialog.open();
			},

			dialogInputConfirm(val) {
				console.log("==dialogInputConfirm==", val)
				if (!val) {
					uni.$u.toast('标签不能为空');
					return;
				}
				if (val.length > 5) {
					uni.$u.toast('标签不能超过5个字符');
					return;
				}
				this.skills.push(val);
			},
			selectTime(type) {
				this.showDate = true;
			},

			dateConfirm(e) {
				this.showDate = false;
				console.log("date==", e);
				let time = uni.$u.timeFormat(e.value, 'yyyy-mm-dd');
				this.form.birthday = time;
				console.log("time==", time);
			},

			skilledRadioClick(idx) {
				this.skilledRadios.map((item, index) => {
					item.checked = index === idx ? true : false
				})
				this.form.skilled = this.skilledRadios[idx].name;
			},

			radioClick2(name) {
				this.radios2.map((item, index) => {
					item.checked = index === name ? true : false
				})
			},
			
			//修改联系电话
			updatePhone() {
				uni.$u.route("/pages/job/updatePhone");
			},
			

			btnAClick() {
				console.log('btnClick', this.form);
				let that = this;
				this.$refs.form
					.validate()
					.then(res => {
						uni.showModal({
							title: '温馨提示',
							content: '确定保存信息？',
							success: res => {
								if (res.confirm) {
									that.updateResume();
								} else if (res.cancel) {
									console.log('用户点击取消');
								}
							}
						});
					})
					.catch(err => {
						// uni.$u.toast('校验失败');
						console.log('err', err);
						let key = '#' + err[0].field
						this.scrollTo(key)
					});
			},


			scrollTo(key) {
				let view = uni.createSelectorQuery().select(key);
				view.boundingClientRect(data => {
					console.log('tabInList基本信息 = ', data);
					uni.pageScrollTo({
						duration: 100,
						scrollTop: data.top,
						success: function() {

						}
					})
				}).exec();
			},

			async updateResume() {
				//特长标签处理
				this.form.skills = this.skills.join(',');
				let res = await this.$apis.updateResume(this.form);
				console.log(res);
				if (res) {
					uni.redirectTo({
						url: '/pages/resume/addResume3'
					})
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
							biz: "worker/feedback"
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


			toNext() {
				uni.redirectTo({
					url: '/pages/resume/addResume3'
				})
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
						this.form.addressName = res.name;
						this.form.address = res.address;
						this.form.addressLat = res.latitude;
						this.form.addressLng = res.longitude;
						getRegeo(res.latitude, res.longitude,
							res => {
								console.log('getRegeo', res);
								this.form.city = res.ad_info.district;
								this.form.cityCode = res.ad_info.adcode;
								this.form.pcity = res.ad_info.city;
								// this.form.pcityCode = res.ad_info.city_code;
								this.form.pcityCode =this.form.cityCode.substr(0,4);
							},
							err => {
								console.log('err==', err);
							}
						);
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

	.page {
		background-image: linear-gradient(#007aff, #f5f6fa, #f5f6fa);
	}

	.title {
		margin-top: 200upx;
		display: flex;
		flex-direction: column;
		padding: 30upx;

		.name {
			font-size: 22px;
			font-weight: bold;
			color: #fff;
		}

		.tip {
			font-size: 14px;
			color: #f5f6fa;
			margin-top: 10upx;
		}
	}

	.reg {
		padding: 20upx 20upx 200upx 20upx;
		border-top-left-radius: 30upx;
		border-top-right-radius: 30upx;

		/deep/ .u-form-item__body__left__content__label {
			height: 40px;
			line-height: 40px;
			font-size: 18px;
			min-width: 80px;
			color: #333;
		}

		.avatar-item {
			display: flex;
			flex-direction: row;
			align-items: center;
			justify-content: space-between;
			width: 100%;

			.avatar-left {
				display: flex;
				flex-direction: column;

				.avatar-title {
					font-size: 15px;
				}

				.avatar-tip {
					color: #666;
					font-size: 12px;
					margin-top: 15upx;
				}
			}

			.upload-default {
				background-color: #f4f5f7;
				width: 80px;
				height: 80px;
				display: flex;
				flex-direction: column;
				align-items: center;
				justify-content: center;
				border-radius: 20upx;
			}
		}

		.base-item {
			background-color: #fff;
			padding: 20upx;
			border-radius: 30upx;
		}

		.work-item {
			background-color: #fff;
			padding: 20upx;
			border-radius: 30upx;
			margin-top: 20upx;
			display: flex;
			flex-direction: column;

			.u-tag-item {
				display: flex;
				flex-direction: row;
				flex-wrap: wrap;
				align-items: center;

				.tag {
					// margin-right: 10px;
					padding: 10upx;
				}
			}

			.tag-tip {
				font-size: 14px;
				color: #888;
				padding: 10upx 0 20upx 0;
			}
		}

		.address-item {
			background-color: #fff;
			padding: 20upx;
			border-radius: 30upx;
			margin-top: 20upx;

			.update-phone {
				color: $main-color;
				font-size: 14px;
			}
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
			width: 30%;
		}

		.throttle {
			background-color: #007aff;
			color: #ffffff;
			width: 60%;
			margin-left: 40upx;
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