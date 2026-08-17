<template>
	<view class="content">
		<view class="info" v-if="userInfo.memberRole == 'company'">
			<image class="img" src="../../static/boss.png"></image>
			<text class="role">您当前身份是：老板</text>
		</view>
		<view class="info" v-else>
			<image class="img" src="../../static/worker.png"></image>
			<text class="role">您当前身份是：工人</text>
		</view>
		<view class="btn">
			<view>
				<u-button type="primary" :plain="false" size="large" @click="switchRole" :text="userInfo.memberRole=='company'?'切换为工人身份':'切换为老板身份'"></u-button>
			</view>
			<view style="margin-top: 40upx;">
				<u-button :plain="false" size="large" text="暂不切换" @click="toBack"></u-button>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		mapState,
		mapMutations
	} from 'vuex';
	export default {
		computed: {
			...mapState(['userInfo'])
		},
		data() {
			return {

			}
		},
		methods: {
			...mapMutations(['emptyUserInfo', 'setUserInfo','setMemberRole']),
			
			toBack() {
				uni.navigateBack();
			},

			switchRole() {
				let param={
					role:'company'
				}
				if(this.userInfo.memberRole=='company'){
					param.role="member"
				}
				this.$apis
					.switchRole(param)
					.then(res => {
						console.log('xxxxxxxxxxxxxx', res);
						//查询用户信息
						this.getUserInfo();
					})
					.catch(err => {
						uni.$u.toast('操作失败');
						console.log(err, 'catch');
					});
			},
			
			getUserInfo() {
				this.$apis
					.getUserInfo()
					.then(res => {
						console.log('getUserInfo', res);
						if (res) {
							this.setUserInfo(res);
							this.setMemberRole(res.memberRole);
							uni.switchTab({
								url:'/pages/index/index'
							})
						}
					})
					.catch(err => {
						console.log(err, 'catch');
					});
			},
		}
	}
</script>

<style lang="scss">
	.content {
		width: 100vw;
		height: 100vh;
		display: flex;
		flex-direction: column;
		align-items: center;
	}

	.info {
		margin-top: 100upx;
		display: flex;
		align-items: center;
		flex-direction: column;

		.img {
			width: 400upx;
			height: 400upx;
		}

		.role {
			font-size: 36upx;
			font-weight: bold;
		}
	}

	.btn {
		width: 80%;
		margin-top: 160upx;
	}
</style>