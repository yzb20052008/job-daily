<template>
	<view class="page">
		<!-- 公共组件-每个页面必须引入 -->
		<public-module></public-module>
		<view class="reg">
			<u-form labelPosition="left" labelWidth="auto" :model="form" :rules="rules" ref="form">
				<view class="work-item">
					<view id="typeNames">
						<u-form-item label="想干的工种" labelPosition="top" prop="typeNames" borderBottom
							@click.native="selectPost">
							<u-input placeholder="请选择想干的工种" readonly border="none" v-model="form.typeNames"
								suffixIcon="arrow-right" suffixIconStyle="font-size: 14px;color: #999" />
						</u-form-item>
					</view>
					<view id="workCity">
						<u-form-item label="期望工作地" labelPosition="top" prop="workCity" borderBottom
							@click.native="selectCity">
							<u-input placeholder="请选择期望工作地" readonly border="none" v-model="form.workCity"
								suffixIcon="arrow-right" suffixIconStyle="font-size: 14px;color: #999" />
						</u-form-item>
					</view>
					<view id="workType">
						<u-form-item label="工作类型" labelPosition="top" prop="workType" borderBottom>
							<view class="u-tag-item">
								<view class="tag" v-for="(item, index) in radios" :key="index">
									<u-tag :text="item.name" color="#333" :plain="!item.checked"
										:bgColor="item.checked?'#E0F3FF':'#f5f6fa'"
										:borderColor="item.checked?'#007aff':'#f5f6fa'" size="large" type="primary"
										:name="index" @click="radioClick">
									</u-tag>
								</view>
							</view>
						</u-form-item>
					</view>
					<view id="employMethod">
						<u-form-item label="用工方式" labelPosition="top" prop="employMethod">
							<view class="u-tag-item">
								<view class="tag" v-for="(item, index) in radios2" :key="index">
									<u-tag :text="item.name" color="#333" :plain="!item.checked"
										:bgColor="item.checked?'#E0F3FF':'#f5f6fa'"
										:borderColor="item.checked?'#007aff':'#f5f6fa'" size="large" type="primary"
										:name="index" @click="radioClick2">
									</u-tag>
								</view>
							</view>
						</u-form-item>
					</view>
					<view id="settlementType">
						<u-form-item label="结算方式" labelPosition="top" prop="settlementType">
							<view class="u-tag-item">
								<view class="tag" v-for="(item, index) in radios3" :key="index">
									<u-tag :text="item.name" color="#333" :plain="!item.checked"
										:bgColor="item.checked?'#E0F3FF':'#f5f6fa'"
										:borderColor="item.checked?'#007aff':'#f5f6fa'" size="large" type="primary"
										:name="index" @click="radioClick3">
									</u-tag>
								</view>
							</view>
						</u-form-item>
					</view>
					<view id="expectSalary" v-if="form.salaryUnit=='面议'">
						<u-form-item label="期望工资" labelPosition="top" borderBottom>
							<u-input placeholder="面议无需输入" border="none" readonly v-model="form.expectSalary">
								<template slot="suffix" @click="showUnit=true">
									<view class="unit">
										<text class="unit-name">{{form.salaryUnit}}</text>
										<text class="unit-icon yzb yzb-xiangxia1"></text>
									</view>
								</template>
							</u-input>
						</u-form-item>
					</view>
					<view id="expectSalary" v-else>
						<u-form-item label="期望工资" labelPosition="top" prop="expectSalary" borderBottom>
							<u-input placeholder="请输入你期望的工资" border="none" v-model="form.expectSalary">
								<template slot="suffix" @click="showUnit=true">
									<view class="unit">
										<text class="unit-name">{{form.salaryUnit}}</text>
										<text class="unit-icon yzb yzb-xiangxia1"></text>
									</view>
								</template>
							</u-input>
						</u-form-item>
					</view>
				</view>
			</u-form>
		</view>
		<view class="bottom"><button class="throttle" @tap="$u.throttle(btnAClick, 500)">保存</button></view>
		<u-action-sheet :show="showUnit" :actions="unitArray" title="请选择工价单位" @close="showUnit = false"
			@select="unitSelect"></u-action-sheet>
	</view>
</template>

<script>
	import uploadImage from '@/plugins/ossutil/uploadFile';
	import BaseUrl from '@/config/baseUrl.js';
	export default {
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
				radios: [{
						checked: false,
						name: '全职'
					},
					{
						checked: false,
						name: '兼职'
					},
					{
						checked: true,
						name: '全兼职皆可'
					}
				],
				radios2: [{
						checked: false,
						name: '点工'
					},
					{
						checked: false,
						name: '包工'
					},
					{
						checked: true,
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
						name: '计量'
					},
				],
				fileList1: [],

				types: [],
				cityInfo: {
					area: ''
				},
				form: {
					typeNames: '',
					typeIds: '',
					typeCodes:'',
					workCity: '',
					workType: '全兼职皆可',
					employMethod: '点包皆可',
					settlementType: '日结',
					expectSalary: '',
					salaryUnit: '面议'
				},

				rules: {
					typeNames: [{
						required: true,
						message: '请选择想干的工种',
						trigger: ['blur', 'change']
					}],
					workCity: [{
						required: true,
						message: '请选择期望工作地',
						trigger: ['blur', 'change']
					}],
					workType: [{
						required: true,
						message: '请选择工作类型',
						trigger: ['blur', 'change']
					}],
					employMethod: [{
						required: true,
						message: '请选择用工方式',
						trigger: ['blur', 'change']
					}],
					settlementType: [{
						required: true,
						message: '请选择结算方式',
						trigger: ['blur', 'change']
					}],
					expectSalary: [{
						required: true,
						message: '请输入期望薪资',
						trigger: ['blur', 'change']
					}],
				}
			};
		},
		onLoad() {
			this.getIntention();
		},

		onShow() {
			if (this.types.length > 0) {
				let typelist = this.types.map(item => item.name);
				this.form.typeNames = typelist.join("、")
				let typeIds = this.types.map(item => item.id);
				this.form.typeIds = typeIds.join(",")
				let typeCodes = this.types.map(item => item.typeCode);
				this.form.typeCodes=typeCodes.join(",")
				console.log("types===",this.types)
			}
			if (this.cityInfo.area) {
				this.form.workCity = this.cityInfo.area;
				this.form.city = this.cityInfo.area;
				this.form.cityCode = this.cityInfo.areaCode;
				this.form.pcity = this.cityInfo.city;
				this.form.pcityCode =this.cityInfo.cityCode;
			}
			this.$forceUpdate();
		},

		methods: {

			async getIntention() {
				let res = await this.$apis.getIntention();
				console.log('res=====', res);
				if (res) {
					this.form = res;
					if (!this.form.salaryUnit) {
						this.form.salaryUnit = "元/小时";
					}
					this.radios.map((item, index) => {
						item.checked = item.name === res.workType ? true : false
					})
					this.radios2.map((item, index) => {
						item.checked = item.name === res.employMethod ? true : false
					})
					this.radios3.map((item, index) => {
						item.checked = item.name === res.settlementType ? true : false
					})
				}
			},

			unitSelect(val) {
				this.form.salaryUnit = val.name;
				// this.$refs.form.validateField('salaryUnit');
			},

			radioClick(idx) {
				this.radios.map((item, index) => {
					item.checked = index === idx ? true : false
				})
				this.form.workType = this.radios[idx].name;
			},

			radioClick2(idx) {
				this.radios2.map((item, index) => {
					item.checked = index === idx ? true : false
				})
				this.form.employMethod = this.radios2[idx].name;
			},

			radioClick3(idx) {
				this.radios3.map((item, index) => {
					item.checked = index === idx ? true : false
				})
				this.form.settlementType = this.radios3[idx].name;
			},

			btnAClick() {
				console.log('btnClick');
				let that = this;
				this.$refs.form
					.validate()
					.then(res => {
						uni.showModal({
							title: '温馨提示',
							content: '确定保存？',
							success: res => {
								if (res.confirm) {
									that.updateIntention();
								} else if (res.cancel) {
									console.log('用户点击取消');
								}
							}
						});
					})
					.catch(err => {
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

			async updateIntention() {
				let res = await this.$apis.updateIntention(this.form);
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

			selectPost() {
				uni.$u.route("/pages/job/types");
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
		border-top-left-radius: 30upx;
		border-top-right-radius: 30upx;

		/deep/ .u-form-item__body__left__content__label {
			height: 40px;
			line-height: 40px;
			font-size: 18px;
			color: #333;
			font-weight: bold;
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

			.u-tag-item {
				display: flex;
				flex-direction: row;
				flex-wrap: wrap;
				align-items: center;

				.tag {
					margin-right: 20px;
				}
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
		position: fixed;
		bottom: 0;
		z-index: 99;

		.throttle {
			background-color: #007aff;
			color: #ffffff;
			margin: 30upx;
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
</style>