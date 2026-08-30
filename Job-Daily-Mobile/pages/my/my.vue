<template>
	<view class="content">
		<public-module></public-module>
		<f-navbar bgColor="#007aff" :navbarType="5" fontColor="#fff" transparentTitleColor="#000" :isShowLeft="false"
			:scrollTop="scrollTop" title="我的" :isShowTransparentTitle="false">
		</f-navbar>
		<view class="header">
			<view class="header-top">
				<view class="userinfo" @click="toLogin">
					<view class="face">
						<u-avatar size="70" shape="circle" :src="userInfo.avatar"></u-avatar>
					</view>
					<view class="info" v-if="userInfo.token">
						<view class="name">
							<text class="username">{{ userInfo.nickname || '暂无昵称' }}</text>
							<text class="driver">{{userInfo.memberRole=='company'?'老板':'员工'}}</text>
							<text class="info-nav yzb yzb-next"></text>
						</view>
						<view class="other-info">
							<text class="phone" v-if="userInfo.phone">{{phoneHiden(userInfo.phone)}}</text>
							<view class="auth" v-if="userInfo.companyAuth==1">
								<text class="yzb yzb-qiyerenzheng"></text>
								<text class="auth-name">企业认证</text>
							</view>
							<view class="auth" v-if="userInfo.companyAuth==0 && userInfo.realNameAuth==1">
								<text class="yzb yzb-xuanzhong"></text>
								<text class="auth-name">已实名</text>
							</view>
						</view>
					</view>
					<view class="info" v-else>
						<view class="username">未登录/注册</view>
						<view class="integral">点击可登录/注册</view>
					</view>
				</view>
				<view class="switch" @click="toSwitch">
					<text class="switch-name">{{userInfo.memberRole=='company'?'我要找工作':'我要招人'}}</text>
					<text class="switch-icon yzb yzb-jiaoseqiehuan1"></text>
				</view>
			</view>
			<!-- <view class="vip-card-box" v-if="userInfo.vipLevel=='1'" @click="toVip('/pages/vip/index')">
				<view class="tit" v-if="userInfo">
					<text class="yticon icon-iLinkapp-"></text>
					<text>超级会员</text>
				</view>
				<view class="e-b">{{$u.timeFormat(userInfo.vipEndTime, 'yyyy-mm-dd')}} 到期</view>
			</view>
			<view class="vip-card-box" v-else @click="toVip('/pages/vip/index')">
				<view class="tit" v-if="userInfo">
					<text class="yticon icon-iLinkapp-"></text>
					<text>企业开通会员，招人高效又省钱</text>
				</view>
				<view class="b-btn">立即开通</view>
			</view> -->
		</view>
		<view class="body">
			<!-- <view class="w-list">
				<uni-grid class="grid" :column="4" :showBorder="false" :square="true" @change="gridClick">
					<uni-grid-item class="item" v-for="(item, index) in getGridList()" :key="index" :index="index">
						<view class="grid-info" :class="(index+1)%4!=0?'grid-right-line':''">
							<text class="grid-icon">{{ item.number }}</text>
							<text class="text">{{ item.text }}</text>
						</view>
					</uni-grid-item>
				</uni-grid>
			</view> -->
			<view class="w-list">
				<view class="grid">
					<view class="grid-item" v-for="(item, index) in getGridList()" :key="index"
						@click="gridClick(item)">
						<text class="grid-icon">{{ item.number }}</text>
						<text class="grid-text">{{ item.text }}</text>
					</view>
				</view>
			</view>
			<view class="scribe" v-if="userInfo.memberRole=='company' && userInfo.companyAuth!=1">
				<text class="scribe-tips">完成企业认证，增加招工曝光度！</text>
				<text class="btn-scribe" @click="doScribe">去认证</text>
			</view>
			<view class="list" v-for="(list, list_i) in getSeverList()" :key="list_i">
				<text class="title" v-if="list.title">{{ list.title }}</text>
				<view class="li" v-for="(li, li_i) in list.menus" v-bind:class="{ noborder: li_i == list.length - 1 }"
					hover-class="hover" :key="li.name">
					<button open-type="contact" class="item" v-if="li.type=='button'">
						<view class="left">
							<view class="icon">
								<image :src="'/static/images/my/' + li.icon"></image>
							</view>
							<view class="text">{{ li.name }}</view>
						</view>
						<view class="right">
							<text class="yzb yzb-next icon-next"></text>
						</view>
					</button>
					<view class="item" v-else @tap="toPage(li)">
						<view class="left">
							<view class="icon">
								<image :src="'/static/images/my/' + li.icon"></image>
							</view>
							<view class="text">{{ li.name }}</view>
						</view>
						<view class="right">
							<!-- <text class="auth-result" :class="userInfo.driverStatus == 2 ? 'auth-success' : ''"
								v-if="li.name == '实名认证'">{{ userInfo.realNameAuth | formatStatus }}</text> -->
							<text class="yzb yzb-next icon-next"></text>
						</view>
					</view>
				</view>
			</view>
		</view>
		<u-popup :show="show" mode="center" round="20" bgColor="transparent">
			<view class="pop-content">
				<image :show-menu-by-longpress="true"
					src="https://bendao.oss-cn-shenzhen.aliyuncs.com/odb/common/gzh.jpg"></image>
			</view>
			<view class="close" @click="show=false">✕</view>
		</u-popup>
		<!-- <page-tabpars></page-tabpars> -->
	</view>
</template>
<script>
	import {
		mapState,
		mapMutations,
		mapGetters
	} from 'vuex';
	import {
		judgeLogin
	} from '@/config/login';
	import {
		phoneHiden,
		saveReferrer
	} from '@/config/common';
	export default {
		computed: {
			...mapState(['userInfo', "runMode",'memberRole'])
		},
		data() {
			return {
				show: false,
				scrollTop: 0,
				authState: '待认证',
				gridList: [{
						text: '我的余额',
						url: '/pages/finance/wallet',
						number: 0
					},
					{
						text: '我的积分',
						number: 0,
						url: '/pages/integral/integral',
					},
					{
						text: '我的关注',
						number: 0,
						url: '/pages/job/collect',
					},
					{
						text: '拨打记录',
						number: 0,
						url: '/pages/job/call',
					}
				],
				gridList2: [{
						text: '我的余额',
						url: '/pages/finance/wallet',
						number: 0
					},
					{
						text: '我的积分',
						number: 0,
						url: '/pages/integral/integral',
					},
					{
						text: '我的关注',
						number: 0,
						url: '/pages/job/collect',
					},
					{
						text: '联系记录',
						number: 0,
						url: '/pages/job/call',
					}
				],
				severList: [{
						title: '我的服务',
						menus: [{
								name: '我的简历',
								icon: 'card.png',
								url: '/pages/resume/resume',
								auth: true,
							},
							// {
							// 	name: '获取积分',
							// 	icon: 'ye.png',
							// 	url: '/pages/integral/integralGet',
							// 	auth: true,
							// },
							// {
							// 	name: '积分道具',
							// 	icon: 'jfdj.png',
							// 	url: '/pages/integral/goods',
							// 	auth: true,
							// },
							{
								name: '我的评价',
								icon: 'comment.png',
								url: '/pages/job/myComments',
								auth: true,
							},
							{
								name: '我的推荐',
								icon: 'team.png',
								url: '/pages/invite/myInvite',
								auth: true,
							},
							{
								name: '实名认证',
								icon: 'auth.png',
								url: '/pages/job/userAuth',
								auth: true,
							},
						]
					},
					{
						title: '其他功能',
						menus: [
							// { name: '系统通知', show: true, icon: 'notice.png', url: '/pages/notice/notice' },
							// {
							// 	name: '关于我们',s
							// 	show: true,
							// 	icon: 'us.png',
							// 	url: '/pages/user/aboutus'
							// },
							{
								name: '联系客服',
								show: true,
								icon: 'service.png',
								url: '/pages/user/contactus',
								auth: true,
								type: 'button'
							},
							{
								name: '意见反馈',
								show: true,
								icon: 'feedback.png',
								url: '/pages/user/feedback',
								auth: true,
							},
							// {
							// 	name: '更换账号',
							// 	show: true,
							// 	icon: 'service.png',
							// 	url: '/pages/user/switchAccount',
							// 	auth: false,
							// },
							// {
							// 	name: '帮助中心',
							// 	show: true,
							// 	icon: 'help.png',
							// 	url: '/pages/user/help',
							// 	auth: false,
							// },
							{
								name: '系统设置',
								show: true,
								icon: 'settings.png',
								url: '/pages/user/set',
								auth: false,
							}
						]
					}
				],
				severList2: [{
						title: '我的服务',
						menus: [{
								name: '发布职位',
								icon: 'tianjia.png',
								url: '/pages/job/addPost',
								auth: true,
							},
							// {
							// 	name: '获取积分',
							// 	icon: 'ye.png',
							// 	url: '/pages/integral/integralGet',
							// 	auth: true,
							// },
							// {
							// 	name: '积分道具',
							// 	icon: 'jfdj.png',
							// 	url: '/pages/integral/goods',
							// 	auth: true,
							// },
							{
								name: '我的评价',
								icon: 'comment.png',
								url: '/pages/job/myComments',
								auth: true,
							},
							{
								name: '我的推荐',
								icon: 'team.png',
								url: '/pages/invite/myInvite',
								auth: true,
							},
							{
								name: '实名认证',
								icon: 'auth.png',
								url: '/pages/job/userAuth',
								auth: true,
							},
							{
								name: '企业认证',
								icon: 'company.png',
								url: '/pages/job/companyAuth',
								auth: true,
							},
						]
					},
					{
						title: '其他功能',
						menus: [
							// { name: '系统通知', show: true, icon: 'notice.png', url: '/pages/notice/notice' },
							// {
							// 	name: '关于我们',s
							// 	show: true,
							// 	icon: 'us.png',
							// 	url: '/pages/user/aboutus'
							// },
							{
								name: '联系客服',
								show: true,
								icon: 'service.png',
								url: '/pages/user/contactus',
								auth: true,
								type: 'button'
							},
							{
								name: '意见反馈',
								show: true,
								icon: 'feedback.png',
								url: '/pages/user/feedback',
								auth: true,
							},
							// {
							// 	name: '更换账号',
							// 	show: true,
							// 	icon: 'service.png',
							// 	url: '/pages/user/switchAccount',
							// 	auth: false,
							// },
							// {
							// 	name: '帮助中心',
							// 	show: true,
							// 	icon: 'help.png',
							// 	url: '/pages/user/help',
							// 	auth: false,
							// },
							{
								name: '系统设置',
								show: true,
								icon: 'settings.png',
								url: '/pages/user/set',
								auth: false,
							}
						]
					}
				],
			};
		},

		filters: {
			formatStatus(val) {
				if (val == 0) {
					return "待审核"
				} else if (val == 1) {
					return "已认证"
				} else if (val == 2) {
					return "认证失败"
				} else {
					return "未认证"
				}
			},
		},

		watch: {
			userInfo: {
				immediate: true,
				handler(newValue, oldValue) {
					console.log("newValue===", newValue)
					console.log("oldValue===", oldValue)
					if (!newValue.id) {
						//未登录
						this.gridList[0].number = 0;
						this.gridList2[0].number = 0;
						this.gridList[1].number = 0;
						this.gridList2[1].number = 0;
						this.gridList[2].number = 0;
						this.gridList2[2].number = 0;
						this.gridList[3].number = 0;
						this.gridList2[3].number = 0;
					}
					if ((newValue.id && newValue.integral == null)) {
						this.getUserInfo();
					}
				}
			},
		},

		onLoad(options) {
		},

		onShow() {
			console.log('userInfo===', this.userInfo);
			if (this.userInfo.token) {
				this.getUserInfo();
				this.getStatistics();
				this.getAccount();
				this.getUnReadCount();
			}
		},
		methods: {
			...mapMutations(['emptyUserInfo', 'setUserInfo', 'setMemberRole']),

			getSeverList() {
				if (this.userInfo.memberRole == 'company') {
					return this.severList2;
				} else {
					return this.severList;
				}
			},

			updateIntegral() {
				console.log("updateIntegral===", this.userInfo)
				if (this.userInfo.token) {
					this.gridList[1].number = this.userInfo.integral;
					this.gridList2[1].number = this.userInfo.integral;
				} else {
					this.gridList[1].number = 0;
					this.gridList2[1].number = 0;
				}
			},

			async getAccount() {
				let res = await this.$apis.getAccountDetail();
				if (res) {
					this.gridList[0].number = res.balance;
					this.gridList2[0].number = res.balance;
				}
			},

			getGridList() {
				if (this.userInfo.memberRole == 'company') {
					return this.gridList2;
				} else {
					return this.gridList;
				}
			},

			phoneHiden(val) {
				return phoneHiden(val);
			},

			getUserInfo() {
				this.$apis
					.getUserInfo()
					.then(res => {
						console.log('getUserInfo', res);
						if (res) {
							this.setUserInfo(res);
							this.setMemberRole(res.memberRole)
							this.updateIntegral();
						}
					})
					.catch(err => {
						console.log(err, 'catch');
					});
			},

			getDriverInfo() {
				this.$apis
					.getDriverInfo()
					.then(res => {
						console.log('getDriverInfo', res);
						if (res) {}
					})
					.catch(err => {
						console.log(err, 'catch');
					});
			},

			getStatistics() {
				let params = {
					roleCode: 'member'
				}
				if (this.userInfo.memberRole == 'company') {
					params.roleCode = 'company'
				}
				this.$apis.getMyStatistics({
					params: params
				}).then(res => {
					console.log('getMyStatistics', res);
					if (res) {
						this.gridList[2].number = res.collect;
						this.gridList[3].number = res.contact;
						this.gridList2[2].number = res.collect;
						this.gridList2[3].number = res.contact;
					}
				});
			},
			toLogin() {
				this.judgeLogin(() => {
					uni.$u.route('/pages/my/myInfo');
				})
			},

			toSwitch() {
				this.judgeLogin(() => {
					uni.$u.route('/pages/job/switch');
				})
			},

			toPage(item) {
				if (item.auth) {
					this.judgeLogin(() => {
						this.toDetail(item);
					})
				} else {
					this.toDetail(item);
				}
			},


			toDetail(item) {
				if (item.url == '/pages/job/companyAuth') {
					//判断是否实名认证
					if (this.userInfo.realNameAuth != 1) {
						uni.$u.toast('请先完成实名认证');
						return;
					}
					uni.$u.route(item.url);
				} else {
					uni.$u.route(item.url);
				}
			},

			gridClick(item) {
				console.log("gridClick", item)
				// let index = e.detail.index;
				// let item = this.gridList[index];
				console.log("item==", item);
				this.judgeLogin(() => {
					uni.$u.route(item.url);
				})
			},

			toVip(url) {
				this.judgeLogin(() => {
					if (!this.userInfo.ifDriver) {
						//非司机
						uni.$u.toast('司机才能开通会员');
						return;
					}
					if (this.userInfo.driverStatus != 2) {
						//待认证
						uni.$u.toast('入驻审核通过才能开通会员');
						return;
					}
					uni.$u.route(url);
				})
			},

			doScribe() {
				//判断是否实名认证
				if (this.userInfo.realNameAuth != 1) {
					uni.$u.toast('请先完成实名认证');
					return;
				}
				uni.$u.route("/pages/job/companyAuth");
			},

			onPageScroll(e) {
				this.scrollTop = e.scrollTop;
			},

			//未读消息显示
			async getUnReadCount() {
				let params = {
					userId: null,
				}
				if (this.userInfo.token) {
					params.userId = this.userInfo.id;
					params.roleCode = this.memberRole || this.userInfo.memberRole;
				} else {
					return;
				}
				let res = await this.$apis.getUnReadCount({
					params: params
				});
				if (res) {
					let count = (res.privateCount || 0) + (res.violationCount || 0) + (res.publicCount || 0)
						+ (res.orderCount || 0) + (res.financeCount || 0);
					if (count > 0) {
						uni.setTabBarBadge({
							index: 1,
							text: count + ''
						})
					} else {
						uni.removeTabBarBadge({
							index: 1
						})
					}
				}
			},
		}
	};
</script>

<style lang="scss">
	page {
		background-color: #f5f6fa;
	}

	.content {
		flex: 1;
		width: 100%;
		height: 100%;
		flex-direction: column;
		align-items: center;
	}

	.body {
		padding: 0 3%;
		// margin-top: 40upx;
	}

	.header {
		// background-image: linear-gradient(#fff, #f5f5f5);
		padding: 170upx 0 20upx 0;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: space-between;
		background-image: url('https://img.qinkonglan.cn/imgs/bg/bg_01.jpg');
		background-size: 750upx 400upx;
		height: 400upx;
		width: 750upx;

		.header-top {
			width: 100%;
			display: flex;
			align-items: center;
			justify-content: space-between;
		}

		.userinfo {
			display: flex;
			flex-direction: row;
			align-items: center;
			flex: 1;
			margin-left: 3%;

			.icon-vip {
				position: absolute;
				top: 2upx;
				color: #ffd465;
				font-size: 40upx;
				background-color: #999;
			}

			.info {
				display: flex;
				flex-direction: column;
				padding-left: 20upx;
				// margin-top: -15upx;

				.name {
					margin-bottom: 15upx;
					flex-direction: row;
					align-items: center;
				}

				.username {
					width: 100%;
					color: #fff;
					font-weight: bold;
					font-size: 36upx;
					margin-left: 10upx;
				}

				.info-nav {
					margin-left: 5upx;
					color: #333;
				}

				.driver {
					margin-left: 20upx;
					color: #36343c;
					font-weight: bold;
					background-color: #f7d680;
					font-size: 26upx;
					border-radius: 20upx;
					padding: 4upx 10upx;
				}

				.other-info {
					display: flex;
					flex-direction: row;
					align-items: center;
					margin-left: 10upx;

					.phone {
						// color: #666;
						// font-size: 28upx;
						display: flex;
						align-items: center;
						justify-content: center;
						padding: 10upx 20upx;
						color: #333;
						background-color: rgba(255, 255, 255, 0.2);
						border-radius: 20upx;
						font-size: 26upx;
					}

					.auth {
						margin-left: 20upx;
						background-color: #E5F4FF;
						padding: 4px 10upx;
						border-radius: 10upx;
						display: flex;
						align-items: center;
						flex-direction: row;

						text {
							color: #007aff;
							font-size: 24rpx;
						}

						.auth-name {
							margin-left: 6upx;
							font-weight: bold;
						}
					}

				}

				.integral {
					display: flex;
					align-items: center;
					justify-content: center;
					padding: 10upx 0upx;
					color: #ddd;
					// background-color: rgba(0, 0, 0, 0.1);
					border-radius: 20upx;
					font-size: 26upx;
				}
			}
		}

		.switch {
			display: flex;
			flex-direction: row;
			align-items: center;
			// background-color: #FF9829;//#FFBA51
			background-color: rgba(0, 0, 0, 0.3);
			padding: 0upx 10upx 0 20upx;
			height: 60upx;
			line-height: 60upx;
			border-top-left-radius: 25upx;
			border-bottom-left-radius: 25upx;

			.switch-name {
				color: #fff;
				font-size: 26rpx;
			}

			.switch-icon {
				margin-left: 8upx;
				color: #FFFFFF;
			}
		}

		.vip-card-box {
			display: flex;
			flex-direction: row;
			align-items: center;
			width: 94%;
			margin: 0 auto;
			margin-top: 20upx;
			color: #f7d680;
			background-color: #2C386A;
			// background: linear-gradient(left, rgba(0, 0, 0, 0.7), rgba(0, 0, 0, 0.8));
			border-radius: 16upx 16upx 0 0;
			overflow: hidden;
			position: relative;
			padding: 20upx 24upx;
			// align-items: center;

			.card-bg {
				position: absolute;
				top: 20upx;
				right: 0;
				width: 380upx;
				height: 260upx;
			}

			.b-btn {
				position: absolute;
				right: 20upx;
				// top: 16upx;
				width: 146upx;
				height: 52upx;
				text-align: center;
				line-height: 50upx;
				font-size: 28upx;
				color: #36343c;
				border-radius: 25px;
				background-color: #ffd465;
				// background: linear-gradient(left, #f9e6af, #ffd465);
				z-index: 1;
			}

			.tit {
				color: #f7d680;

				text {
					font-size: 30upx;
				}

				// margin-bottom: 28upx;
				.yticon {
					color: #f6e5a3;
					margin-right: 16upx;
				}
			}

			.e-b {
				color: #909399;
				position: absolute;
				right: 20upx;
				text-align: center;
				line-height: 40upx;
				font-size: 22upx;
				border-radius: 20px;
				z-index: 2;
			}
		}
	}

	.hover {
		background-color: #f8f8f8;
	}

	.w-list {
		background-color: #fff;
		width: 100%;
		margin-top: -60upx;
		// padding: 0 2%;
		// margin: 20upx;
		// margin-bottom: 20upx;
		// margin-top: 20upx;
		display: flex;
		align-items: center;
		border-radius: 25upx;
		box-sizing: border-box;
		// border-top: solid 30rpx #f8f8f8;

		.grid {
			background-color: #ffffff;
			margin-bottom: 6upx;
			width: 100%;
			border-radius: 20upx;
			display: flex;
			flex-direction: row;
			box-shadow: 0 0 10upx rgba(0, 0, 0, 0.15);

			.grid-item {
				flex: 1;
				display: flex;
				flex-direction: column;
				justify-content: center;
				align-items: center;
				padding: 30upx 0;
			}

			.grid-dot {
				position: absolute;
				top: 5upx;
				right: 15upx;
			}

			.grid-icon {
				color: $main-color;
				font-size: 34upx;
			}

			.grid-text {
				margin-top: 10upx;
				font-size: 28upx;
				color: #817f82;
			}
		}
	}

	.scribe {
		z-index: 9999;
		display: flex;
		flex-direction: row;
		background-color: #FFF5EC;
		padding: 20upx 30upx;
		align-items: center;
		justify-content: space-between;
		// height: 80upx;
		margin-top: 20upx;

		.scribe-tips {
			font-size: 14px;
			color: #F87C08;
		}

		.btn-scribe {
			font-size: 28upx;
			border: #F87C08 1upx solid;
			padding: 6upx 15upx;
			border-radius: 10upx;
			color: #F87C08;
		}
	}

	.list {
		width: 100%;
		background-color: #fff;
		display: flex;
		flex-direction: column;
		border-radius: 20upx;
		margin-top: 20upx;
		// box-shadow: 0 0 10upx rgba(0, 0, 0, 0.1);

		.title {
			padding: 4%;
			font-weight: bold;
			font-size: $uni-font-size-lg;
		}

		.li {
			width: 100%;

			.item {
				display: flex;
				flex-direction: row;
				justify-content: space-between;
				align-items: center;

				width: 100%;
				height: 90upx;
				padding: 0 4%;
				border-bottom: solid 1upx #eee;
			}

			&.noborder {
				border-bottom: 0;
			}

			.left {
				display: flex;
				flex-direction: row;
				align-items: center;
			}

			.icon {
				flex-shrink: 0;
				width: 55upx;
				height: 55upx;

				image {
					width: 55upx;
					height: 55upx;
				}
			}

			.text {
				padding-left: 30upx;
				width: 100%;
				color: #666;
				font-size: 30upx;
			}

			.to {
				flex-shrink: 0;
				width: 40upx;
				height: 40upx;
			}

			.icon-next {
				font-size: 34upx;
				color: #999;
			}


			.right {
				display: flex;
				flex-direction: row;
				align-items: center;
			}

			.auth-result {
				min-width: 200upx;
				text-align: right;
				margin-right: 10upx;
				font-size: 28upx;
				color: #dd524d;
				font-weight: bold;
			}

			.auth-success {
				color: $uni-color-success;
			}
		}
	}

	.pop-content {
		width: 600upx;
		height: 600upx;
		border-radius: 20upx;

		image {
			width: 600upx;
			height: 600upx;
			border-radius: 20upx;
		}
	}

	.close {
		width: 60rpx;
		height: 60rpx;
		color: #FFFFFF;
		line-height: 60rpx;
		text-align: center;
		border-radius: 50%;
		border: 1px solid #FFFFFF;
		position: relative;
		bottom: -10%;
		left: 50%;
		transform: translate(-50%, -50%);
	}
</style>