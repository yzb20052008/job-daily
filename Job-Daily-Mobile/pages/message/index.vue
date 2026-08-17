<template>
	<view class="content">
		<public-module></public-module>
		<view class="top">
			<text class="top-title">
				全部消息
			</text>
			<view class="top-clear" @click="setAllRead">
				<text class="yzb yzb-bianji"></text>
				<text class="clear-txt">全部已读</text>
			</view>
		</view>
		<uni-list :border="true">
			<uni-list-chat v-for="(item,index) in getNotices()" :key="index" :title="item.title" :avatar="item.icon"
				:note="item.desc" :time="item.createTime" badge-positon="right" :badge-text="item.count"
				:showBadge="item.count>0" @click="listClick(item)" :clickable="clickable"
				:avatarCircle="true"></uni-list-chat>
		</uni-list>
		<!-- <page-tabpars></page-tabpars> -->
	</view>
</template>

<script>
	import {
		mapState
	} from 'vuex';
	import {
		requestSubscribe,
		getSpecificTmplStatus,
	} from '@/config/common';
	export default {
		computed: {
			...mapState(['userInfo', 'memberRole'])
		},
		data() {
			return {
				notices: [{
						icon: '/static/images/msg-sys.png',
						title: '系统通知',
						desc: "暂无系统通知",
						createTime: '',
						count: 0,
						url: '/pages/notice/notice',
						login: true,
					},
					{
						icon: '/static/images/msg-private.png',
						title: '平台私信',
						desc: "暂无私信消息",
						createTime: '',
						count: 0,
						url: '/pages/message/privateList?type=2&&title=平台私信',
						login: true,
					},
					{
						icon: '/static/images/msg-voilation.png',
						title: '违规记录',
						desc: "暂无违规记录",
						createTime: '',
						count: 0,
						url: '/pages/message/privateList?type=4&&title=违规记录',
						login: true,
					},
					{
						icon: '/static/images/msg-order.png',
						title: '订单动态',
						desc: "暂无订单消息",
						createTime: '',
						count: 0,
						url: '/pages/message/orderList',
						login: true,
					},
					{
						icon: '/static/images/msg-money.png',
						title: '动账通知',
						desc: "暂无私信消息",
						createTime: '',
						count: 0,
						url: '/pages/message/financeList',
						login: true,
					},
				],
				notices2: [{
						icon: '/static/images/msg-sys.png',
						title: '系统通知',
						desc: "暂无系统通知",
						createTime: '',
						count: 0,
						url: '/pages/notice/notice',
						login: true,
					},
					{
						icon: '/static/images/msg-private.png',
						title: '平台私信',
						desc: "暂无私信消息",
						createTime: '',
						count: 0,
						url: '/pages/message/privateList?type=2&&title=平台私信',
						login: true,
					},
					{
						icon: '/static/images/msg-voilation.png',
						title: '违规记录',
						desc: "暂无违规记录",
						createTime: '',
						count: 0,
						url: '/pages/message/privateList?type=4&&title=违规记录',
						login: true,
					},
					{
						icon: '/static/images/msg-order.png',
						title: '订单动态',
						desc: "暂无订单消息",
						createTime: '',
						count: 0,
						url: '/pages/message/orderList',
						login: true,
					},
					{
						icon: '/static/images/msg-money.png',
						title: '动账通知',
						desc: "暂无私信消息",
						createTime: '',
						count: 0,
						url: '/pages/message/financeList',
						login: true,
					},
				],
				countInfo: {},
				clickable: true,
			}
		},

		onShow() {
			this.getUnReadCount();
		},

		onTabItemTap(ops) {
			console.log("onTabItemTap==", ops)
			if (this.memberRole == 'driver') {
				requestSubscribe(3,
					res => {
						console.log("订阅成功：", res)
					}, err => {
						console.log("订阅失败：", err)
					});
			}
		},

		methods: {

			allReaded() {
				this.judgeLogin(() => {
					this.setAllRead();
				})
			},

			async setAllRead() {
				let params = {
					userId: this.userInfo.id
				}
				this.$apis
					.setAllRead(params)
					.then(res => {
						uni.$u.toast('操作成功');
						this.getUnReadCount();
					})
					.catch(err => {
						console.log(err, 'catch');
					});
			},

			getNotices() {
				if (this.memberRole == 'driver') {
					return this.notices2;
				} else {
					return this.notices;
				}
			},

			setUnReadCount() {
				let count = 0;
				if (this.userInfo.memberRole == 'driver') {
					this.notices2[0].count = this.countInfo.publicCount;
					this.notices2[1].count = this.countInfo.privateCount;
					this.notices2[2].count = this.countInfo.violationCount;
					this.notices2[3].count = this.countInfo.orderCount;
					this.notices2[4].count = this.countInfo.financeCount;
					count = this.countInfo.privateCount + this.countInfo.violationCount + this.countInfo.publicCount + this.countInfo.orderCount + this
						.countInfo.financeCount

					this.notices2[0].desc = this.countInfo.publicDesc;
					this.notices2[1].desc = this.countInfo.privateDesc;
					this.notices2[2].desc = this.countInfo.violationDesc;
					this.notices2[3].desc = this.countInfo.orderDesc;
					this.notices2[4].desc = this.countInfo.financeDesc;

					this.notices2[0].createTime = this.countInfo.publicTime;
					this.notices2[1].createTime = this.countInfo.privateTime;
					this.notices2[2].createTime = this.countInfo.violationTime;
					this.notices2[3].createTime = this.countInfo.orderTime;
					this.notices2[4].createTime = this.countInfo.financeTime;
				} else {
					this.notices[0].count = this.countInfo.publicCount;
					this.notices[1].count = this.countInfo.privateCount;
					this.notices[2].count = this.countInfo.violationCount;
					this.notices[3].count = this.countInfo.orderCount;
					this.notices[4].count = this.countInfo.financeCount;
					count = this.countInfo.privateCount + this.countInfo.violationCount + this.countInfo.publicCount  + this
						.countInfo.orderCount + this.countInfo.financeCount

					this.notices[0].desc = this.countInfo.publicDesc;
					this.notices[1].desc = this.countInfo.privateDesc;
					this.notices[2].desc = this.countInfo.violationDesc;
					this.notices[3].desc = this.countInfo.orderDesc;
					this.notices[4].desc = this.countInfo.financeDesc;

					this.notices[0].createTime = this.countInfo.publicTime;
					this.notices[1].createTime = this.countInfo.privateTime;
					this.notices[2].createTime = this.countInfo.violationTime;
					this.notices[3].createTime = this.countInfo.orderTime;
					this.notices[4].createTime = this.countInfo.financeTime;
				}
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

			},

			async getUnReadCount() {
				let params = {
					userId: null,
				}
				if (this.userInfo.token) {
					params.userId = this.userInfo.id;
				} else {
					return;
				}
				let res = await this.$apis.getUnReadCount({
					params: params
				});
				if (res) {
					this.countInfo = res;
					this.setUnReadCount();
				}
			},

			toUrl(item) {
				if (item.login) {
					this.judgeLogin(() => {
						return item.url;
					})
				} else {
					return item.url;
				}
			},

			listClick(item) {
				console.log("listClick===", item);
				if (item.login) {
					this.judgeLogin(() => {
						uni.$u.route(item.url);
					})
				} else {
					uni.$u.route(item.url);
				}

			},
		}
	}
</script>

<style lang="scss">
	page {
		background-color: #f5f6fa;
	}

	.top {
		background-color: #fff;
		display: flex;
		flex-direction: row;
		align-items: center;
		justify-content: space-between;
		padding: 20upx 20upx 30upx 20upx;

		.top-title {
			font-weight: bold;
			font-size: 16px;
		}

		.top-clear {
			text {
				color: #888;
				font-size: 14px;
			}

			.clear-txt {
				margin-left: 5upx;
			}
		}
	}
</style>