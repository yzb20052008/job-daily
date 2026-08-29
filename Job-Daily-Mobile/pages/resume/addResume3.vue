<template>
	<view class="page">
		<!-- 公共组件-每个页面必须引入 -->
		<public-module></public-module>
		<f-navbar :navbarType="5" :isShowLeft="true">
		</f-navbar>
		<view class="title">
			<text class="name">完善自我介绍</text>
			<text class="tip">完善后，找工作更快</text>
		</view>
		<view class="area">
			<u--textarea v-model="personalSkill" :customStyle="{borderRadius:'10px',whiteSpace:'pre-wrap'}"
				placeholderStyle="color:red" :placeholder="placeholder" count border="none" height="300"
				maxlength="300"></u--textarea>
		</view>
		<view class="bottom">
			<button class="throttle1" @tap="toNext">跳过</button>
			<button class="throttle" @tap="$u.throttle(btnAClick, 500)">保存</button>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				personalSkill: "",
				placeholder: "请输入个人情况、优势特长、工作要求等\n示例\n本人已从事此行业10年，熟练掌握各项专业技能，工作积极，勤劳肯干。",

			}
		},
		methods: {
			toNext() {
				uni.redirectTo({
					url:'/pages/resume/resume'
				})
			},
			
			btnAClick() {
				console.log('btnClick');
				this.updateResume();
			},
			
			async updateResume() {
				if(!this.personalSkill){
					uni.$u.toast('内容不能为空');
					return;
				}
				let param={
					personalSkill:this.personalSkill
				}
				let res = await this.$apis.updateResume(param);
				console.log(res);
				if (res) {
					uni.$u.toast('操作成功');
					this.toNext();
				}
			},
		}
	}
</script>

<style lang="scss">
	page {
		background-color: #f5f6fa;
	}

	.page {
		height: 100vh;
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

	.area {
		padding: 20upx;
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
</style>