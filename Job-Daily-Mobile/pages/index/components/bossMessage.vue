<template>
	<view class="content">
		<public-module></public-module>
		<view class="top">
			<view class="navbar" :style="{height:systemInfo.navBarH+'px'}">
				<text class="nav-title">消息</text>
			</view>
			<view class="y-clear">
				<text class="top-title">
					全部消息
				</text>
				<view class="top-clear" @click="setAllRead">
					<text class="yzb yzb-bianji"></text>
					<text class="clear-txt">全部已读</text>
				</view>
			</view>
		</view>
		<view class="y-list" :style="{marginTop:mTop+'px'}">
			<uni-list :border="true">
				<uni-list-chat v-for="(item,index) in notices" :key="index" :title="item.title" :avatar="item.icon"
					:note="item.desc" :time="formatCreateTime(item.createTime)" badge-positon="left" :badge-text="item.count"
					:showBadge="item.count>0" @click="listClick(item)" :clickable="clickable"
					:avatarCircle="true"></uni-list-chat>
				<uni-list-chat v-for="(item, index) in conversations" :key="index" :title="formatTitle(item)"
					:avatar="item.data.avatar" :note="item.lastMessage.payload.text"
					:time="formatTimeFrom(item.lastMessage.timestamp)" badge-positon="left" :badge-text="item.unread"
					:showBadge="true" @click="toChat(item)" @longpress="longTimeClick(item)"
					:clickable="clickable"></uni-list-chat>
			</uni-list>
		</view>
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
	import {
		judgeLogin
	} from '@/config/login';
	const GoEasy = uni.$GoEasy;
	export default {
		computed: {
			...mapState(['userInfo', 'memberRole'])
		},
		data() {
			return {
				systemInfo: this.$base.systemInfo,
				mTop:100,
				clickable: true,
				where: {},
				loading: false,
				notice: null,
				unreadTotal: 0,
				noticeCount: 0,
				msgTotal: 0,
				conversations: [],
				countInfo: {},
				notices: [{
						icon: '/static/images/msg-sys.png',
						title: '系统通知',
						desc: "暂无系统通知",
						createTime: '',
						count: 0,
						url: '/pages/message/notice',
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
					// {
					// 	icon: '/static/images/msg-voilation.png',
					// 	title: '违规记录',
					// 	desc: "暂无违规记录",
					// 	createTime: '',
					// 	count: 0,
					// 	url: '/pages/message/privateList?type=4&&title=违规记录',
					// 	login: true,
					// },
					{
						icon: '/static/images/msg-order.png',
						title: '招聘动态',
						desc: "暂无订单消息",
						createTime: '',
						count: 0,
						url: '/pages/message/orderList',
						login: true,
					},
					// {
					// 	icon: '/static/images/msg-money.png',
					// 	title: '账户通知',
					// 	desc: "暂无私信消息",
					// 	createTime: '',
					// 	count: 0,
					// 	url: '/pages/message/financeList',
					// 	login: true,
					// },
				],
			}
		},

		methods: {
			
			initData() {
				this.mTop=this.systemInfo.navBarH+uni.upx2px(80);
				this.getUnReadCount();
				this.initChat();
				this.getUnReadTotalCount();
			},
			
			async getUnReadCount() {
				let params = {
					userId: null,
				}
				if (this.userInfo.token) {
					params.userId = this.userInfo.id;
					params.roleCode = this.memberRole;
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
			
			setUnReadCount() {
				let count = 0;
				this.notices[0].count = this.countInfo.publicCount;
				this.notices[1].count = this.countInfo.privateCount;
				this.notices[2].count = this.countInfo.orderCount;
				count = this.countInfo.privateCount + this.countInfo.violationCount + this.countInfo.publicCount + this.countInfo.orderCount + this
					.countInfo.financeCount
				
				this.notices[0].desc = this.countInfo.publicDesc;
				this.notices[1].desc = this.countInfo.privateDesc;
				this.notices[2].desc = this.countInfo.orderDesc;
				
				this.notices[0].createTime = this.countInfo.publicTime;
				this.notices[1].createTime = this.countInfo.privateTime;
				this.notices[2].createTime = this.countInfo.orderTime;
			},

			//初始化goeasy
			initChat() {
				if (this.userInfo.token) {
					console.log("goeasy status == ", GoEasy.getConnectionStatus())
					if (GoEasy.getConnectionStatus() === 'disconnected') {
						GoEasy.connect({
							id: this.userInfo.id,
							data: {
								name: this.userInfo.nickname,
								avatar: this.userInfo.avatar
							},
							onSuccess: () => {
								console.log('GoEasy connect successfully.')
								this.loadConversations();
							},
							onFailed: (error) => {
								console.log('Failed to connect GoEasy, code:' + error.code + ',error:' + error
									.content);
							},
							onProgress: (attempts) => {
								console.log('GoEasy is connecting', attempts);
							}
						});
					} else {
						this.loadConversations();
					}
				}
			},

			initGoEasyListeners() {
				GoEasy.im.on(GoEasy.IM_EVENT.CONVERSATIONS_UPDATED, this.renderConversations); //监听会话列表变化
				GoEasy.im.off(GoEasy.IM_EVENT.CONVERSATIONS_UPDATED, this.setUnreadAmount); // 移除之前的设置角标回调，防止重复回调
				GoEasy.im.on(GoEasy.IM_EVENT.CONVERSATIONS_UPDATED, this.setUnreadAmount); // 设置角标
			},

			// 加载最新的会话列表
			loadConversations() {
				GoEasy.im.latestConversations({
					onSuccess: (result) => {
						let content = result.content;
						this.renderConversations(content);
						this.setUnreadAmount(content);
					},
					onFailed: (error) => {
						uni.hideLoading();
						console.log('获取最新会话列表失败, error:', error);
					}
				});
			},
			renderConversations(content) {
				this.conversations = content.conversations;
				console.log("===conversations===", this.conversations);
				//格式化内容
				this.conversations.forEach(function(item, index) {
					if (item.lastMessage.type == 'text') {} else if (item.lastMessage.type == 'video') {
						item.lastMessage.payload.text = '[视频消息]';
					} else if (item.lastMessage.type == 'audio') {
						item.lastMessage.payload.text = '[语音消息]';
					} else if (item.lastMessage.type == 'image') {
						item.lastMessage.payload.text = '[图片消息]';
					} else if (item.lastMessage.type == 'file') {
						item.lastMessage.payload.text = '[文件消息]';
					} else if (item.lastMessage.type == 'resume') {
						item.lastMessage.payload.text = '[简历附件]';
					} else {
						item.lastMessage.payload.text = '[其他消息]';
					}
				});
			},

			setUnreadAmount(content) {
				this.msgTotal = content.unreadTotal;
				this.updateBradge();
			},

			updateBradge() {
				this.unreadTotal = this.msgTotal + this.noticeCount;
				if (this.unreadTotal > 0) {
					uni.setTabBarBadge({
						index: 1,
						text: this.unreadTotal.toString()
					});
				} else {
					uni.removeTabBarBadge({
						index: 1
					});
				}
				// #ifdef APP-PLUS
				GoEasy.setBadge({
					badge: this.unreadTotal,
					onSuccess: function() {
						console.log("setBadge successfully.")
					},
					onFailed: function(error) {
						console.log("Failed to setBadge,error:" + error);
					}
				});
				// #endif
			},

			toChat(item) {
				this.judgeLogin(() => {
					uni.$u.route("/pages/common/privateChat?to=" + item.userId + "&&postId=" + item.data.postId);
				})
			},

			formatCreateTime(time) {
				if (time == null || time === '') {
					return null;
				}
				let str = time.replace(/-/g, '/');
				let date = new Date(str);
				return uni.$u.timeFormat(date, 'mm-dd hh:MM');
			},

			formatTimeFrom(time) {
				return uni.$u.timeFrom(time);
			},

			formatTitle(item) {
				//求职对话
				if (this.userInfo.memberRole == "company") {
					if (item.lastMessage.payload.postName) {
						return item.data.name + " -【" + item.lastMessage.payload.postName + "】";
					} else {
						return item.data.name;
					}
				} else if (this.userInfo.memberRole == "member") {
					if (item.lastMessage.payload.companyName) {
						return item.data.name + " -【" + item.lastMessage.payload.companyName + " - " + item.lastMessage
							.payload.memberPostName + "】";
					} else {
						return item.data.name;
					}
				} else {
					//求职对话
					return item.data.name;
				}
			},

			tapGrid(item) {
				this.judgeLogin(() => {
					uni.$u.route(item.to);
				})
			},

			toNotice() {
				this.judgeLogin(() => {
					uni.$u.route("/pages/message/index");
				})
			},


			allReaded() {
				this.judgeLogin(() => {
					this.setAllRead();
				})
			},

			async setAllRead() {
				let params = {
					userId: this.userInfo.id,
					roleCode: this.memberRole
				}
				this.$apis
					.setAllRead(params)
					.then(res => {
						uni.$u.toast('操作成功');
						this.getUnReadCount();
						this.getUnReadTotalCount();
					})
					.catch(err => {
						console.log(err, 'catch');
					});
			},

			async getUnReadTotalCount() {
				let params = {
					userId: null,
				}
				if (this.userInfo.token) {
					params.userId = this.userInfo.id;
					params.roleCode = this.memberRole;
				} else {
					return;
				}
				let res = await this.$apis.getUnReadTotalCount({
					params: params
				});
				if (res) {
					this.countInfo = res;
					this.noticeCount = res.totalCount;
					this.updateBradge();
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

			longTimeClick(item) {
				let that = this;
				uni.showModal({
					title: '删除记录',
					content: '确定删除此会话？',
					success: res => {
						if (res.confirm) {
							GoEasy.im.removeConversation({
								conversation: item,
								onSuccess: function() {
									console.log('删除会话成功');
									uni.showToast({
										icon: "none",
										title: '删除会话成功'
									});
									that.loadConversations();
								},
								onFailed: function(error) {
									console.log(error);
								},
							});
						} else {

						}
					},
					fail: () => {},
					complete: () => {}
				});
			},
		}
	}
</script>

<style lang="scss" scoped>
	
	.content{
		height: 100vh;
		// background-image: linear-gradient(180deg, #007aff 10%,  #f5f6fa 20%);
	}
	
	.top{
		position: fixed;
		top: 0;
		z-index: 99;
		width: 100%;
		background-image: linear-gradient(180deg, #007aff 20%, #65ADFD 60%, #E0ECFB 100%);
	}
	
	.navbar{
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		position: relative;
		.nav-title{
			position: absolute;
			bottom: 30rpx;
			font-size: 30rpx;
			color: #fff;
		}
	}
	
	.y-clear {
		display: flex;
		flex-direction: row;
		align-items: center;
		justify-content: space-between;
		padding: 0 20rpx;
		height: 80rpx;
		// background: rgba(255, 255, 255, 0.2);
		

		.top-title {
			font-weight: bold;
			font-size: 32rpx;
		}

		.top-clear {
			text {
				color: #666;
				font-size: 28rpx;
			}

			.clear-txt {
				margin-left: 5upx;
			}
		}
	}
	
	.y-list{
		padding-bottom: 30rpx;
	}

	.nodata {
		display: flex;
		justify-content: center;
		align-items: center;
		color: #666;
		font-size: 32upx;
		margin-top: 250upx;
	}
</style>