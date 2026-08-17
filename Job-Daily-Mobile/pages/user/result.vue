<template>
	<view class="page">
		<!-- 公共组件-每个页面必须引入 -->
		<public-module></public-module>
		<view class="u-flex result">
			<!-- <image class="icon" src="../../static/images/celebrities/icon-19.png"></image> -->
			<view class="icon u-flex">
				<block v-if="icon=='ok'">
					<u-icon name="checkmark" :color="PrimaryColor" size="80rpx"></u-icon>
				</block>
				<block v-if="icon=='no'">
					<u-icon name="close" :color="PrimaryColor" size="60rpx"></u-icon>
				</block>
			</view>
			<block v-if="type==1">
				<view class="title u-p-t-60">提交成功</view>
				<view class="detail u-p-t-24">我们会尽快处理您的请求，请注意查收</view>
			</block>
			<block v-if="type==2">
				<view class="title u-p-t-60">支付成功</view>
				<view class="detail u-p-t-24">欢迎继续光临~</view>
			</block>
			<block v-if="type==3 && icon=='no'">
				<view class="title u-p-t-60">支付失败</view>
				<view class="detail u-p-t-24">可前往订单继续支付~</view>
			</block>
		</view>
	</view>
</template>

<script>
import { mapState, mapMutations } from 'vuex';
export default {
	data() {
		return {
			icon:'ok',
			type:2,
		}
	},
	computed:{
	    ...mapState(['PrimaryColor'])
	},
	onLoad(e) {
		this.icon = e.icon || 'ok'
		this.type = e.type || '1'
	},
	methods:{
		
	}
}
</script>

<style lang="scss" scoped>
.page{
	min-height: 100vh;
	background-color: #fff;
}
.result{
	padding-top: 144rpx;
	flex-direction: column;
	justify-content: center;
	.icon{
		justify-content: center;
		width: 140rpx;
		height: 140rpx;
		border-radius: 50%;
		background-color: #F2F2F2;
	}
	.title{
		font-weight: bold;
		font-size: 36rpx;
		color: #000;
	}
	.detail{
		font-size: 28rpx;
		color: #666;
	}
}
</style>