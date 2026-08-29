<template>
	<view class="content">
		<view class="area">
			<u--textarea v-model="personalSkill" :customStyle="{borderRadius:'10px',whiteSpace:'pre-wrap'}" placeholderStyle="color:red" :placeholder="placeholder" count border="none" height="300" maxlength="300"></u--textarea>
		</view>
		<view class="bottom"><button class="throttle" @tap="$u.throttle(btnAClick, 500)">保存</button></view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				personalSkill: "",
				placeholder:"请输入个人情况、优势特长、工作要求等\n示例\n本人已从事此行业10年，熟练掌握各项专业技能，工作积极，勤劳肯干。",
				
			}
		},
		onLoad(options) {
			if(options.personalSkill){
				this.personalSkill=options.personalSkill
			}
		},
		methods: {
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
					uni.navigateBack();
				}
			},	
		}
	}
</script>

<style lang="scss">
	page{
		background-color: #f5f6fa;
	}
	.area{
		padding: 20upx;
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
		box-sizing: border-box;

		.throttle {
			background-color: #007aff;
			color: #ffffff;
			margin: 30upx;
		}
	}
</style>