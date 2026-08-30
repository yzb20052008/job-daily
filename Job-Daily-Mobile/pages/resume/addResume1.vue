<template>
	<view class="page">
		<!-- 公共组件-每个页面必须引入 -->
		<public-module></public-module>
		<f-navbar :navbarType="5" :isShowLeft="true">
		</f-navbar>
		<view class="title">
			<text class="name">创建简历</text>
			<text class="tip">创建发布简历后，老板会主动联系你</text>
		</view>
		<view class="reg">
			<u-form labelPosition="left" labelWidth="auto" :model="form" :rules="rules" ref="form">
				<u-form-item label="想干的工种" labelPosition="top" prop="typeNames" borderBottom
					@click.native="selectPost">
					<u-input placeholder="请选择想干的工种" readonly border="none" v-model="form.typeNames"
						suffixIcon="arrow-right" suffixIconStyle="font-size: 14px;color: #999" />
				</u-form-item>
				<u-form-item label="期望工作地" labelPosition="top" prop="workCity" borderBottom @click.native="selectCity">
					<u-input placeholder="请选择期望工作地" readonly border="none" v-model="form.workCity" 
					suffixIcon="arrow-right" suffixIconStyle="font-size: 14px;color: #999"/>
				</u-form-item>
			</u-form>
		</view>
		<view class="bottom"><button class="throttle" @tap="$u.throttle(btnAClick, 500)">下一步</button></view>
	</view>
</template>

<script>
	import BaseUrl from '@/config/baseUrl.js';
	export default {
		data() {
			return {
				fileList1: [],
				
				types: [],
				form: {
					typeNames: '',
					typeIds:'',
					typeCodes:'',
					address: '',
				},
				
				cityInfo: {
					area: ''
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
				}
			};
		},
		onLoad() {},
		
		
		onShow() {
			if (this.types.length > 0) {
				let typelist = this.types.map(item => item.name);
				this.form.typeNames = typelist.join("、")
				let typeIds = this.types.map(item => item.id);
				this.form.typeIds=typeIds.join(",");
				let typeCodes = this.types.map(item => item.typeCode);
				this.form.typeCodes=typeCodes.join(",")
			}
			if(this.cityInfo.area){
				this.form.workCity=this.cityInfo.area;
			}
			this.$forceUpdate();
		},
		
		methods: {
			
			selectPost() {
				uni.$u.route("/pages/job/types");
			},
			
			selectCity() {
				uni.$u.route('/pages/common/selectCity');
			},
			
			btnAClick() {
				console.log('btnClick');
				let that = this;
				this.$refs.form
					.validate()
					.then(res => {
						uni.showModal({
							title: '温馨提示',
							content: '确认信息是否准确？',
							success: res => {
								if (res.confirm) {
									that.addIntention();
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

			async addIntention() {
				let res = await this.$apis.updateIntention(this.form);
				console.log(res);
				if (res) {
					uni.redirectTo({
						url:'/pages/resume/addResume2'
					})
				}
			},
		}
	};
</script>

<style lang="scss">
	.page {
		background-image: linear-gradient(#007aff, #fff);
	}
	
	.title{
		margin-top: 200upx;
		display: flex;
		flex-direction: column;
		padding: 30upx;
		.name{
			font-size: 22px;
			font-weight: bold;
			color: #fff;
		}
		.tip{
			font-size: 14px;
			color: #f5f6fa;
			margin-top: 10upx;
		}
	}

	.reg {
		background-color: #fff;
		padding: 20upx;
		border-top-left-radius: 30upx;
		border-top-right-radius: 30upx;
		
		.u-form-item__body__left__content__label{
			font-weight: bold;
			font-size: 18px;
		}
		
		/deep/ .u-form-item__body__left__content__label{
			height: 30px;
			line-height: 30px;
		}
	}

	.bottom {
		width: 100%;
		background: #fff;
		position: fixed;
		bottom: 0;
		/* 全面屏底部安全区 */
		padding-bottom: constant(safe-area-inset-bottom);
		padding-bottom: env(safe-area-inset-bottom);

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