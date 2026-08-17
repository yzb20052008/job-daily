<template>
	<view class="y-post-list">
		<view class="post-item" v-for="(item,index) in list" :key="index" @click="itemClick(item)">
			<view class="worker">
				<u-avatar :src="item.userAvatar" size="54" shape="square" @click.stop="toResume(item)"></u-avatar>
				<view class="worker-info">
					<view class="worker-name">
						<view class="row">
							<text class="name">{{item.userName}}</text>
							<view class="tags">
								<view class="tag" v-if="item.sex">
									<u-tag :text="item.sex|formatSex" size="mini" color="#007aff" borderColor="#E5F4FF"
										bgColor="#E5F4FF"></u-tag>
								</view>
								<view class="tag" v-if="item.birthday">
									<u-tag :text="calCurrentYear(item.birthday)+'岁'" size="mini" color="#007aff"
										borderColor="#E5F4FF" bgColor="#E5F4FF"></u-tag>
								</view>
							</view>
						</view>
						<text class="status">{{item | formatStatus}}</text>
					</view>
					<view class="tips">
						<!-- 订单状态：0-待确认，1-待开工，2-工作中，3-待结算，4-待评价，5-已完成，6-已取消 -->
						<text v-if="item.orderStatus==0">您需在2小时内确认接单，超时将自动取消</text>
						<text v-else-if="item.orderStatus==1">已确认接单，待工人到场开工</text>
						<text
							v-else-if="item.orderStatus==2">{{$u.timeFormat (new Date(item.startTime).getTime(),'mm-dd hh:MM')}}
							已开工</text>
						<text v-else-if="item.orderStatus==3">员工已工作完成，等待工资结算</text>
						<text v-else-if="item.orderStatus==4">员工此次工作表现如何呢，期待您的评价</text>
						<text v-else-if="item.orderStatus==5">订单已完成</text>
						<text v-else-if="item.orderStatus==6">订单已取消</text>
						<view class="left-time" v-if="item.orderStatus==0">
							<text>剩余时间：</text>
							<u-count-down :time="getTime(item.ensureTime)" format="HH:mm:ss">
							</u-count-down>
						</view>

					</view>
				</view>
			</view>
			<view class="info-btn">
				<view @click.stop="cancel(item.id, index)" v-if="item.orderStatus==0">
					<u-button :customStyle="{width:'70px',marginLeft:'10px',height:'30px',color:'#fff'}" type="warning"
						shape="circle">放弃</u-button>
				</view>
				<view @click.stop="ensure(item.id,index)" v-if="item.orderStatus==0">
					<u-button :customStyle="{width:'70px',marginLeft:'10px',height:'30px',color:'#fff'}" type="primary"
						shape="circle">同意</u-button>
				</view>
				<view @click.stop="phoneCall(item.userPhone)">
					<u-button :customStyle="{width:'70px',marginLeft:'10px',height:'30px',color:'#333'}" plain
						type="info" shape="circle">打电话</u-button>
				</view>
				<view @click.stop="pay(item,index)" v-if="item.orderStatus==3">
					<u-button :customStyle="{width:'70px',marginLeft:'10px',height:'30px',color:'#fff'}" type="primary"
						shape="circle">去结算</u-button>
				</view>
				<view @click.stop="comment(item,index)" v-if="item.orderStatus==4 && item.companyEvaluate==0">
					<u-button :customStyle="{width:'70px',marginLeft:'10px',height:'30px',color:'#fff'}" type="warning"
						shape="circle">去评价</u-button>
				</view>
				<!-- <view @click.stop="update(id, index)">
					<u-button :customStyle="{width:'70px',marginLeft:'10px',height:'30px',color:'#333'}" plain type="info"
						shape="circle">详情</u-button>
				</view> -->
			</view>
		</view>
	</view>
</template>

<!-- #E5F4FF
#f5f6fa -->

<script>
	import {
		mapState
	} from 'vuex';
	import {
		judgeLogin
	} from '@/config/login';
	import {
		calCurrentYear
	} from '@/config/common';
	export default {
		name: 'y-post-list',
		computed: {
			...mapState(['userInfo'])
		},
		options: {
			styleIsolation: 'shared'
		},
		props: {
			list: {
				type: Array
			},
			url: {
				type: String
			}
		},

		data() {
			return {
				timeData: {},
			};
		},

		filters: {
			formatDistance(distance) {
				var strDistance = '';
				if (distance < 1) { //一公里以内的
					distance = distance.toFixed(3);
					strDistance = distance * 1000 + " m";
				} else {
					if (distance > 10000) {
						distance = 0;
					}
					distance = distance.toFixed(2);
					strDistance = distance + " km";
				}
				return strDistance;
			},
			formatSex(val) {
				if (val == 1) {
					return "男"
				} else if (val == 2) {
					return "女"
				}
			},
			// 订单状态：0-待确认，1-待开工，2-工作中，3-待结算，4-待评价，5-已完成，6-已取消
			formatStatus(item) {
				let str = '';
				if (item.orderStatus == '0') {
					return "待确认"
				} else if (item.orderStatus == '1') {
					return "待开工"
				} else if (item.orderStatus == '2') {
					return "工作中"
				} else if (item.orderStatus == '3') {
					return "待结算"
				} else if (item.orderStatus == '4') {
					if (item.companyEvaluate == 1) {
						return "已完成"
					} else {
						return "待评价"
					}
				} else if (item.orderStatus == '5') {
					return "已完成"
				} else if (item.orderStatus == '6') {
					return "已取消"
				}
				return str;
			},
		},

		methods: {

			onChange(e) {
				this.timeData = e
			},

			getTime(end) {
				if (!end) {
					return 0;
				}
				let statTime = new Date().getTime();
				var closeTime = new Date(end.replace(/-/g, '/')).getTime(); //转时间戳
				let leftTime = closeTime - statTime;
				return leftTime;
			},

			calCurrentYear(val) {
				return calCurrentYear(val)
			},

			// 电话客服
			phoneCall(phone) {
				console.log("phone==", phone)
				uni.makePhoneCall({
					phoneNumber: phone,
					success: () => {
						console.log('成功拨打电话');
					},
					fail: (err) => {
						console.log(err);
					},
				});
			},

			cancel(id, index) {
				this.$emit('cancel', id, index)
			},

			ensure(id, index) {
				this.$emit('ensure', id, index)
			},

			pay(item, index) {
				this.$emit('pay', item, index)
			},

			comment(item, index) {
				uni.$u.route("/pages/order/commentUser?id=" + item.id + "&userId=" + item.userId);
			},

			itemClick(item) {
				//订单详情
				uni.$u.route("/pages/order/detail?id=" + item.id);
			},
			
			toResume(item){
				this.$emit('itemClick', item)
				uni.$u.route("/pages/resume/resumeDetail?userId=" + item.userId);
			},
		}
	};
</script>

<style lang="scss" scoped>
	page {
		background-color: #f5f6fa;
	}

	.y-post-list {}

	.row {
		display: flex;
		flex-direction: row;
		align-items: center;
	}

	.post-item {
		display: flex;
		flex-direction: column;
		margin: 20upx 20upx 0upx 20upx;
		// margin-bottom: 20upx;
		padding: 30upx 20upx;
		box-sizing: border-box;
		background-color: #FFFFFF;
		border-radius: 20upx;

		.worker {
			display: flex;
			flex-direction: row;
			justify-content: space-between;

			.worker-info {
				margin-left: 20upx;
				width: 100%;
			}

			.worker-name {
				display: flex;
				flex-direction: row;
				align-items: center;
				justify-content: space-between;

				.name {
					font-size: 20px;
					font-weight: bold;
				}

				.tags {
					display: flex;
					flex-direction: row;
					flex-wrap: wrap;
					align-items: center;
				}

				.tag {
					margin-left: 10upx;
				}

				.status {
					color: $main-color;
					font-size: 16px;
				}
			}

			.tips {
				font-size: 14px;
				color: #999;
				margin-top: 10upx;
			}

			.left-time {
				color: red;
				display: flex;
				flex-direction: row;
				align-items: center;
				color: #999;
				font-size: 13px;
			}
			
			/deep/ .u-count-down__text {
				color: red;
				font-size: 13px;
			}

		}

		.info-btn {
			display: flex;
			flex-direction: row;
			justify-content: flex-end;
			align-items: center;
			border-top: 1upx solid #eee;
			padding: 20upx 20upx;
			margin-top: 20upx;
		}

		.title {
			font-size: 16px;
			font-weight: bold;
			text-overflow: -o-ellipsis-lastline;
			overflow: hidden;
			text-overflow: ellipsis;
			display: -webkit-box;
			-webkit-line-clamp: 2;
			-webkit-box-orient: vertical;
		}
	}
</style>