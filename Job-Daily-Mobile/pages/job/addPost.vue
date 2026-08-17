<template>
	<view class="page">
		<!-- 公共组件-每个页面必须引入 -->
		<public-module></public-module>
		<view class="reg">
			<u-form labelPosition="left" labelWidth="auto" :model="form" :rules="form.salaryUnit=='面议'?rules2:rules" ref="form">
				<view class="work-item">
					<view id="typeNames">
						<u-form-item label="想招的工种" labelPosition="top" prop="typeNames" borderBottom required
							@click.native="selectPost">
							<u-input placeholder="请选择想招的工种" readonly border="none" v-model="form.typeNames"
								suffixIcon="arrow-right" suffixIconStyle="font-size: 14px;color: #999" />
						</u-form-item>
					</view>
					<view id="title">
						<u-form-item label="招工标题(20字内)" labelPosition="top" prop="title" borderBottom required>
							<u--textarea v-model="form.title" :customStyle="{borderRadius:'10px',whiteSpace:'pre-wrap'}"
								placeholder="请填写招工标题,如:招收3名贴标打包熟手" count border="none" autoHeight
								maxlength="20"></u--textarea>
							<!-- <u-input placeholder="请填写招工标题,如:招收3名贴标打包熟手" maxlength="20" border="none" v-model="form.title" /> -->
						</u-form-item>
					</view>
					<view id="descr">
						<u-form-item label="招工详情" labelPosition="top" prop="descr" required>
							<u--textarea v-model="form.descr" :customStyle="{borderRadius:'10px',whiteSpace:'pre-wrap'}"
								placeholder="请输入招工详情,如填写招工条件以及福利等" count border="surround" height="200"
								maxlength="500"></u--textarea>
						</u-form-item>
					</view>

					<u-form-item label="工作时间" labelPosition="top" borderBottom required>
						<view class="time">
							<view id="startTime">
								<u-form-item labelPosition="top" prop="startTime">
									<view class="time-item" @click.native="selectTime(1)">
										<u-input placeholder="请选择工作开始时间" border="none" readonly inputAlign="center"
											v-model="form.startTime" />
									</view>
								</u-form-item>
							</view>
							<view class="time-mid">
								至
							</view>
							<view id="endTime">
								<u-form-item labelPosition="top" prop="endTime">
									<view class="time-item" @click.native="selectTime(2)">
										<u-input placeholder="请选择工作结束时间" border="none" readonly inputAlign="center"
											v-model="form.endTime" />
									</view>
								</u-form-item>
							</view>
						</view>
					</u-form-item>
					<view id="addressName">
						<u-form-item label="工作地点" labelPosition="top" prop="addressName" borderBottom required
							@click.native="selectLocation()">
							<u-input placeholder="请选择工作地点" border="none" readonly v-model="form.addressName"
								suffixIcon="arrow-right" suffixIconStyle="font-size: 14px;color: #999" />
						</u-form-item>
					</view>
					<view id="closeTime">
						<u-form-item label="招工截止时间" labelPosition="top" prop="closeTime" borderBottom required
							suffixIcon="arrow-right" suffixIconStyle="font-size: 14px;color: #999"
							@click.native="selectTime(3)">
							<u-input placeholder="请选择招工截止时间" border="none" readonly inputAlign="left"
								v-model="form.closeTime" />
						</u-form-item>
					</view>
					<view id="sexRequire">
						<u-form-item label="性别要求" labelPosition="left" prop="sexRequire"  borderBottom
							required>
							<view class="u-tag-item">
								<view class="tag" v-for="(item, index) in sexRadios" :key="index">
									<u-tag :text="item.name" color="#333" :plain="!item.checked"
										:bgColor="item.checked?'#E0F3FF':'#f5f6fa'"
										:borderColor="item.checked?'#007aff':'#f5f6fa'" size="large" type="primary"
										:name="index" @click="sexRadioClick">
									</u-tag>
								</view>
							</view>
						</u-form-item>
					</view>
					<!-- <u-form-item label="年龄限制" labelPosition="left" prop="age" borderBottom>
						<u-input placeholder="请输入年龄限制,如50岁以下" border="none" inputAlign="left" v-model="form.age"/>
					</u-form-item> -->
					<view id="recruitsNumber">
						<u-form-item label="招工人数" labelPosition="left" prop="recruitsNumber" borderBottom required>
							<view style="display: flex; justify-content: flex-end;">
								<u-number-box integer buttonSize="28" :min="1" :max="100"
									v-model="form.recruitsNumber"></u-number-box>
							</view>
						</u-form-item>
					</view>
					<!-- <u-form-item label="工作类型" labelPosition="top" prop="sex" borderBottom>
						<view class="u-tag-item">
							<view class="tag" v-for="(item, index) in radios" :key="index">
								<u-tag :text="item.name" color="#333" :plain="!item.checked"
									:bgColor="item.checked?'#E0F3FF':'#f5f6fa'"
									:borderColor="item.checked?'#007aff':'#f5f6fa'" size="large" type="primary"
									:name="index" @click="radioClick">
								</u-tag>
							</view>
						</view>
					</u-form-item> -->
					<!-- <u-form-item label="结算方式" labelPosition="left" prop="sex" borderBottom>
						<view class="u-tag-item">
							<view class="tag" v-for="(item, index) in radios3" :key="index">
								<u-tag :text="item.name" color="#333" :plain="!item.checked"
									:bgColor="item.checked?'#E0F3FF':'#f5f6fa'"
									:borderColor="item.checked?'#007aff':'#f5f6fa'" size="large" type="primary"
									:name="index" @click="radioClick3">
								</u-tag>
							</view>
						</view>
					</u-form-item> -->
					<view id="salary" v-if="form.salaryUnit=='面议'">
						<u-form-item label="工价设置" labelPosition="left" borderBottom  >
							<u-input border="none" readonly type="digit" inputAlign="right" v-model="form.salary">
								<template slot="suffix" @click="showUnit=true">
									<view class="unit">
										<text class="unit-name">{{form.salaryUnit}}</text>
										<text class="unit-icon yzb yzb-xiangxia1"></text>
									</view>
								</template>
							</u-input>
						</u-form-item>
					</view>
					<view id="salary" v-else>
						<u-form-item label="工价设置" labelPosition="left" prop="salary" borderBottom required>
							<u-input placeholder="请输入工价金额" border="none" type="digit" inputAlign="right" v-model="form.salary">
								<template slot="suffix" @click="showUnit=true">
									<view class="unit">
										<text class="unit-name">{{form.salaryUnit}}</text>
										<text class="unit-icon yzb yzb-xiangxia1"></text>
									</view>
								</template>
							</u-input>
						</u-form-item>
					</view>
					<view id="phone">
						<u-form-item label="联系电话" labelPosition="left" prop="phone" @click="updatePhone">
							<u-input placeholder="请输入联系电话" readonly border="none" inputAlign="right"
								v-model="form.phone" suffixIcon="arrow-right"
								suffixIconStyle="font-size: 14px;color: #999">
								<!-- <template slot="suffix">
								<text class="update-phone">修改</text>
							</template> -->
							</u-input>
						</u-form-item>
					</view>
				</view>
			</u-form>
		</view>
		<view class="bottom"><button class="throttle" @tap="$u.throttle(btnAClick, 500)">提交</button></view>
		<h-time-alert :title="timeTitle" :subhead="subTitle" rangeStartTime="00:00:00" rangeEndTime="24:00:00"
			defaultTime="2024/8/09 18:00:00" intervalTime="10" dayStartIntTime="0" rangeDay="30" :isShow="showTime"
			:maskHide="true" :rangeType="false" :closeBtn="true" @closeAlert="handelClose">
		</h-time-alert>
		<u-action-sheet :show="showUnit" :actions="unitArray" title="请选择工价单位" @close="showUnit = false"
			@select="unitSelect"></u-action-sheet>
	</view>
</template>

<script>
	import {
		mapState,
		mapMutations
	} from 'vuex';
	import uploadImage from '@/plugins/ossutil/uploadFile';
	import BaseUrl from '@/config/baseUrl.js';
	import {
		getRegeo,
		requestSubscribe
	} from '@/config/common';
	import {
		render
	} from "vue";

	export default {
		computed: {
			...mapState(['userInfo'])
		},
		data() {
			return {
				showUnit: false,
				unitArray: [{
						name: '元/小时'
					},
					{
						name: '元/天'
					},
					{
						name: '元/件'
					},
					{
						name: '面议'
					},
				],
				types: [],
				timeType: 1,
				timeTitle: '开始时间',
				subTitle: '请选择开始工作时间',
				sexRadios: [{
						checked: false,
						name: '男'
					},
					{
						checked: false,
						name: '女'
					},
					{
						checked: true,
						name: '不限'
					}
				],
				radios: [{
						checked: true,
						name: '全职'
					},
					{
						checked: false,
						name: '兼职'
					},
					{
						checked: false,
						name: '全兼职皆可'
					}
				],
				radios2: [{
						checked: true,
						name: '点工'
					},
					{
						checked: false,
						name: '包工'
					},
					{
						checked: false,
						name: '点包皆可'
					},
				],
				radios3: [{
						checked: true,
						name: '日结'
					},
					{
						checked: false,
						name: '月结'
					},
					{
						checked: false,
						name: '完工结'
					},
				],
				fileList1: [],
				form: {
					typeNames: '',
					title: '',
					descr: '',
					sexRequire: '不限',
					ageRequire: '',
					imgs: '',
					contact: '',
					detail: '',
					address: '',
					addressName: '',
					latitude: '',
					longitude: '',
					salaryUnit: '元/小时',
					phone: '',
					recruitsNumber: 1,
					salary: '',
					city: '',
					cityCode: '',
					pcity: '',
					pcityCode: ''
				},

				showTime: false,
				columnRefTops: [],

				rules: {
					typeNames: [{
						required: true,
						message: '请选择想招的工种',
						trigger: ['blur', 'change']
					}],
					title: [{
						required: true,
						message: '请填写招工标题',
						trigger: ['blur', 'change']
					}],
					descr: [{
						required: true,
						message: '请填写招工详情',
						trigger: ['blur', 'change']
					}],
					startTime: [{
						required: true,
						message: '请请选择工作开始时间',
						trigger: ['blur', 'change']
					}],
					endTime: [{
						required: true,
						message: '请请选择工作结束时间',
						trigger: ['blur', 'change']
					}],
					addressName: [{
						required: true,
						message: '请选择工作地点',
						trigger: ['blur', 'change']
					}],
					closeTime: [{
						required: true,
						message: '请选择招工截止时间',
						trigger: ['blur', 'change']
					}],

					// sexRequire: [{
					// 	required: true,
					// 	message: '请选择性别要求',
					// 	trigger: ['blur', 'change']
					// }],
					// recruitsNumber: [{
					// 	required: true,
					// 	message: '请填写招工人数',
					// 	trigger: ['blur', 'change']
					// }],
					salary: [{
						required: true,
						message: '请填写工价金额',
						trigger: ['blur', 'change']
					}],
				},
				
				rules2: {
					typeNames: [{
						required: true,
						message: '请选择想招的工种',
						trigger: ['blur', 'change']
					}],
					title: [{
						required: true,
						message: '请填写招工标题',
						trigger: ['blur', 'change']
					}],
					descr: [{
						required: true,
						message: '请填写招工详情',
						trigger: ['blur', 'change']
					}],
					startTime: [{
						required: true,
						message: '请请选择工作开始时间',
						trigger: ['blur', 'change']
					}],
					endTime: [{
						required: true,
						message: '请请选择工作结束时间',
						trigger: ['blur', 'change']
					}],
					addressName: [{
						required: true,
						message: '请选择工作地点',
						trigger: ['blur', 'change']
					}],
					closeTime: [{
						required: true,
						message: '请选择招工截止时间',
						trigger: ['blur', 'change']
					}],
				}
			};
		},
		onLoad(options) {
			this.id = options.id;
			if (this.id) {
				this.getPostInfo();
			} else {
				this.form.phone = this.userInfo.phone;
			}
		},

		onReady() {},

		onShow() {
			console.log("types===", this.types)
			if (this.types.length > 0) {
				let typelist = this.types.map(item => item.name);
				this.form.typeNames = typelist.join("、")
				let typeIds = this.types.map(item => item.id);
				this.form.typeIds = typeIds.join(",")
				let typeCodes = this.types.map(item => item.typeCode);
				this.form.typeCodes = typeCodes.join(",")
				this.$refs.form.validateField('typeNames')
				console.log("types===", this.form.typeNames)
			}
			console.log("form===", this.form)
		},
		methods: {

			getPostInfo() {
				let params = {
					id: this.id
				}
				this.$apis
					.getPostInfo({
						params: params
					})
					.then(res => {
						console.log('getPostInfo', res);
						if (res) {
							this.form = res;
							this.sexRadios.forEach(item => {
								item.checked = false;
								if (item.name == this.form.sexRequire) {
									item.checked = true;
								}
							})
							console.log('sexRadios', this.sexRadios);
						}
					})
					.catch(err => {
						console.log(err, 'catch');
					});
			},

			//修改联系电话
			updatePhone() {
				uni.$u.route("/pages/job/updatePhone");
			},

			handelClose(data) {
				this.showTime = false;
				console.log(data);
				if (this.timeType == 1) {
					this.form.startTime = data._date;
					this.judgeTime(1);
				} else if (this.timeType == 2) {
					this.form.endTime = data._date;
					this.judgeTime(2);
				} else if (this.timeType == 3) {
					this.form.closeTime = data._date;
				}
				//data={
				// 	date: "2020/3/30 09:00"
				// 	_date: "2020-3-30 09:00"
				// 	dateRange: "2020/3/30 09:00-09:30"
				// 	_dateRange: "2020-3-30 09:00-09:30"
				// 	timeStamp: 1585530000000
				// }
			},

			/**
			 * 校验工作时间
			 */
			judgeTime(type) {
				if (this.form.startTime && this.form.endTime) {
					console.log("this.form.startTime==",this.form.startTime);
					console.log("this.form.endTime==",this.form.endTime);
					if (this.form.startTime > this.form.endTime) {
						uni.$u.toast('开始时间不能大于结束时间');
						if (type == 1) {
							this.form.startTime = ''
						} else if (type == 2) {
							this.form.endTime = ''
						}
						return false;
					}
				}
				return true;
			},

			unitSelect(val) {
				this.form.salaryUnit = val.name;
				if(val.name=='面议'){
					this.form.salary='';
				}
				// this.$refs.form.validateField('salaryUnit');
			},

			selectAge() {

			},
			selectTime(type) {
				this.timeType = type;
				if (type == 1) {
					this.timeTitle = "开始时间";
					this.subTitle = "请选择工作开始时间"
				} else if (type == 2) {
					this.timeTitle = "结束时间";
					this.subTitle = "请选择工作结束时间"
				} else if (type == 3) {
					this.timeTitle = "截止时间";
					this.subTitle = "请选择招工截止时间"
				}
				this.showTime = true;
			},
			sexRadioClick(idx) {
				this.sexRadios.map((item, index) => {
					item.checked = index === idx ? true : false
				})
				this.form.sexRequire = this.sexRadios[idx].name;
				console.log("sexRadioClick", idx);
			},

			radioClick(name) {
				this.radios.map((item, index) => {
					item.checked = index === name ? true : false
				})
			},

			radioClick2(name) {
				this.radios2.map((item, index) => {
					item.checked = index === name ? true : false
				})
			},

			radioClick3(name) {
				this.radios3.map((item, index) => {
					item.checked = index === name ? true : false
				})
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
						this.form.latitude = res.latitude;
						this.form.longitude = res.longitude;
						getRegeo(res.latitude, res.longitude,
							res => {
								console.log('getRegeo', res);
								this.form.city = res.ad_info.district;
								this.form.cityCode = res.ad_info.adcode;
								this.form.pcity = res.ad_info.city;
								// this.form.pcityCode = res.ad_info.city_code;
								//根据城市编码获取
								this.form.pcityCode =this.form.cityCode.substr(0,4);
							},
							err => {
								console.log('err==', err);
							}
						);

					}
				});
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

			btnAClick() {
				console.log('btnClick');
				let that = this;
				this.$refs.form
					.validate()
					.then(res => {
						uni.showModal({
							title: '温馨提示',
							content: '确定发布职位？',
							success: res => {
								if (res.confirm) {
									that.addPost();
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

			async addPost() {
				if (this.fileList1) {
					let arr = [];
					this.fileList1.forEach(item => {
						arr.push(item.url);
					});
					if (arr.length > 0) {
						this.form.imgs = arr.join(',');
					}
				}
				let res;
				if (this.id) {
					this.form.postStatus = 2;
					res = await this.$apis.updatePost(this.form);
				} else {
					res = await this.$apis.addPost(this.form);
				}
				console.log(res);
				if (res) {
					uni.showToast({
						icon: 'none',
						title: '添加成功'
					})
					// uni.navigateBack();
					this.doScribe();
				}
			},
			
			doScribe(){
				requestSubscribe(3,
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

			toRule() {
				uni.$u.route("/pages/job/authRule");
			},

			selectCity() {
				uni.$u.route("/pages/common/selectCity");
			},

			selectPost() {
				uni.$u.route("/pages/job/types?showAdd=true&&ids="+this.form.typeIds);
			},
		}
	};
</script>

<style lang="scss">
	page {
		background-color: #f5f6fa;
	}

	.reg {
		padding: 20upx 20upx 200upx 20upx;

		/deep/ .u-form-item__body__left__content__label {
			height: 40px;
			line-height: 40px;
			font-size: 18px;
			color: #333;
			font-weight: bold;
			min-width: 90px;
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

		.time {
			display: flex;
			flex-direction: row;
			align-items: center;

			.time-item {}

			.time-mid {
				font-size: 15px;
				color: #333;
				margin: 0 10upx;
			}
		}

		.unit {
			margin-left: 20upx;

			.unit-name {
				font-size: 14px;
				color: $main-color;
			}

			.unit-icon {
				font-size: 14px;
				color: #666;
				margin-left: 5upx;
			}
		}

		.work-item {
			background-color: #fff;
			padding: 20upx;
			border-radius: 30upx;
			margin-top: 20upx;

			.u-tag-item {
				display: flex;
				flex-direction: row;
				flex-wrap: wrap;
				align-items: center;

				.tag {
					margin-right: 15px;
				}
			}
		}

		.update-phone {
			color: $main-color;
			font-size: 14px;
		}
	}

	.bottom {
		width: 100%;
		background: #fff;
		position: fixed;
		bottom: 0;
		z-index: 99;

		.throttle {
			background-color: #007aff;
			color: #ffffff;
			margin: 30upx;
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