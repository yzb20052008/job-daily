<template>
	<view class="content">
		<view class="boss-info">
			<view class="info row-align">
				<u-avatar size="50" shape="square" :src="order.postUser.avatar"></u-avatar>
				<view class="boss column-align">
					<view class="boss-top">
						<text class="boss-name">{{order.postUser.nickname}}</text>
						<view class="tags">
							<view class="tags-item" v-if="order.ifCompanyAuth">
								<u-tag text="企业认证" size="mini" color="#007aff" borderColor="#E5F4FF"
									bgColor="#E5F4FF"></u-tag>
							</view>
							<view class="tags-item" v-if="!order.ifCompanyAuth && order.ifRealName">
								<u-tag text="已实名" size="mini" color="#007aff" borderColor="#E5F4FF"
									bgColor="#E5F4FF"></u-tag>
							</view>
						</view>
					</view>
					<view class="row-align">
						<text class="boss-phone">{{phoneHiden(order.postUser.phone)}}</text>
					</view>
				</view>
			</view>
			<view class="call column-align" @click="call" v-if="isUser">
				<text class="call-icon yzb yzb-bohao2"></text>
				<text class="call-name">打老板电话</text>
			</view>
		</view>
		<view class="post-info">
			<view class="y-top">
				<view class="row">
					<text class="top-title">{{order.post.title}}</text>
				</view>
			</view>
			<view class="y-time">
				工作时间：{{$u.timeFormat (new Date(order.post.startTime).getTime(),'mm-dd hh:MM')}} 至
				{{$u.timeFormat (new Date(order.post.endTime).getTime(),'mm-dd hh:MM')}}
			</view>
			<view class="y-status">
				<view class="tags-item" v-if="order.post.postStatus==2">
					<u-tag :text="order.post.postStatus|formatPostStatus" size="medium" color="#007aff"
						borderColor="#E5F4FF" bgColor="#E5F4FF"></u-tag>
				</view>
				<view class="tags-item" v-else>
					<u-tag :text="order.post.postStatus|formatPostStatus" size="medium" color="#333" borderColor="#eee"
						bgColor="#eee"></u-tag>
				</view>
				<view class="tags-item">
					<u-tag :text="order.post.salaryUnit=='面议'?'面议':order.post.salary+order.post.salaryUnit" size="medium" color="red"
						borderColor="#FCEDEC" bgColor="#FCEDEC"></u-tag>
				</view>
			</view>
			<view class="location">
				<text class="location-icon yzb yzb-dingwei"></text>
				<text class="location-name">{{order.post.addressName}}</text>
			</view>
		</view>
		<view class="order-info">
			<view class="info-item border-bottom">
				<text class="title">订单状态</text>
				<text class="info">{{order | formatStatus}}</text>
			</view>
			<view class="info-item border-bottom">
				<text class="title">创建时间</text>
				<text class="info">{{order.createTime}}</text>
			</view>
		</view>
		<view class="clock-info" v-if="order.startClock">
			<view class="info-item-column border-bottom">
				<text class="title">现场照片(上班)</text>
				<view class="img">
					<u-album rowCount="4" :urls="imgList" singleSize="70" v-if="imgList.length>0"></u-album>
					<text v-else class="img-none">无</text>
				</view>
			</view>
			<view class="info-item border-bottom">
				<text class="title">打卡位置</text>
				<text class="info">{{order.startClock.address}}</text>
			</view>
			<view class="info-item border-bottom">
				<text class="title">打卡距离</text>
				<text class="info">{{order.startClock.distance}}米</text>
			</view>
			<view class="info-item border-bottom">
				<text class="title">上班打卡时间</text>
				<text class="info">{{order.startClock.createTime}}</text>
			</view>
		</view>

		<view class="clock-info" v-if="order.endClock">
			<view class="info-item-column border-bottom">
				<text class="title">现场照片(下班)</text>
				<view class="img">
					<u-album rowCount="4" :urls="imgList2" singleSize="70" v-if="imgList2.length>0"></u-album>
					<text v-else class="img-none">无</text>
				</view>
			</view>
			<view class="info-item border-bottom">
				<text class="title">打卡位置</text>
				<text class="info">{{order.endClock.address}}</text>
			</view>
			<view class="info-item border-bottom">
				<text class="title">打卡距离</text>
				<text class="info">{{order.endClock.distance}}米</text>
			</view>
			<view class="info-item border-bottom">
				<text class="title">下班打卡时间</text>
				<text class="info">{{order.endClock.createTime}}</text>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		phoneHiden,
	} from '@/config/common';
	import {
		mapState,
		mapMutations
	} from 'vuex';
	import {
		commonDistance
	} from '@/plugins/utils';
	export default {
		computed: {
			...mapState(['userInfo'])
		},

		data() {
			return {
				order: {
					postUser: {},
					post: {},
					startClock: null,
					endClock: null
				},
				isUser: true,
				imgList: [],
				imgList2: []
			}
		},

		filters: {
			formatPostStatus(status) {
				let str = '';
				switch (status) {
					case '1':
						str = "待审核"
						break;
					case '2':
						str = "招工中"
						break;
					case '3':
						str = "发布失败"
						break;
					case '4':
						str = "已停招"
						break;
					case '5':
						str = "已取消"
						break;
					case '6':
						str = "已招满"
						break;
				}
				return str;
			},
			
			//<!-- 0-待确认，1-待开工，2-工作中，3-待结算，4-待评价，5-已完成 ,6-已取消-->
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
					if (item.userEvaluate == 1) {
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

		onLoad(options) {
			this.id = options.id;
			if (this.userInfo.token && this.userInfo.memberRole == 'company') {
				this.isUser = false;
			} else {
				this.isUser = true;
			}
			this.getOrderDetail();
		},

		methods: {
			async getOrderDetail() {
				let res = await this.$apis.getOrderDetail({
					params: {
						id: this.id
					}
				});
				console.log('res=====', res);
				if (res) {
					this.order = res;
					if (res.startClock) {
						this.imgList = res.startClock.images.split(',');
					}
					if (res.endClock) {
						this.imgList2 = res.endClock.images.split(',');
					}

				}
			},

			phoneHiden(val) {
				return phoneHiden(val);
			},
			
			call(){
				uni.makePhoneCall({
					phoneNumber: this.order.postUser.phone,
					success: () => {
						console.log('成功拨打电话');
					},
					fail: (err) => {
						console.log(err);
					},
				});
			}

		}
	}
</script>

<style lang="scss">
	page {
		background-color: #f5f6fa;
	}
	
	.content{
		padding-bottom: 50upx;
	}

	.boss-info {
		background-color: #fff;
		padding: 30upx;
		margin: 20upx;
		border-radius: 20upx;
		display: flex;
		flex-direction: row;
		justify-content: space-between;
		align-items: center;
		// border-radius: 20upx;

		.boss {
			margin-left: 20upx;

			.boss-top {
				display: flex;
				flex-direction: row;
				align-items: center;
			}

			.boss-name {
				font-weight: bold;
				font-size: 18px;
				margin-right: 15upx;
			}

			.boss-score {
				font-size: 14px;
				color: #dd524d;
				margin-top: 10upx;
				margin-right: 15upx;
				font-weight: bold;
			}

			.boss-phone {
				font-size: 14px;
				color: #666;
				margin-top: 10upx;
			}
		}

		.call {
			align-items: center;

			.call-icon {
				font-size: 24px;
				color: $main-color;
			}

			.call-name {
				font-size: 12px;
				color: #666;
				margin-top: 10upx;
			}

		}
	}

	.post-info {
		background-color: #fff;
		margin: 20upx;
		box-sizing: border-box;
		border-radius: 20upx;
		padding: 20upx;

		.y-top {
			padding: 20upx;

			.top-title {
				font-size: 18px;
				font-weight: bold;
				flex-wrap: wrap;
			}
		}

		.y-time {
			padding: 0 20upx;
			font-size: 15px;
			color: #666;
			width: 100%;
		}

		.y-read {
			padding: 0 20upx;
			font-size: 14px;
			color: #999;
		}

		.y-status {
			display: flex;
			flex-direction: row;
			flex-wrap: wrap;
			padding: 20upx;

			.tags-item {
				margin-right: 15upx;
			}

			.add-price {
				background: $main-color;
				color: #fff;
				font-size: 12px;
				padding: 14upx 15upx;
				border-radius: 10upx;
			}
		}

		.location {
			display: flex;
			flex-direction: row;
			align-items: center;
			padding: 10upx 30upx 20upx 30upx;

			.location-icon {
				color: #666;
				font-size: 13px;
				margin-right: 10upx;
			}

			.location-name {
				max-width: 80%;
				font-size: 13px;
				color: #666;
				text-overflow: -o-ellipsis-lastline;
				overflow: hidden;
				text-overflow: ellipsis;
				display: -webkit-box;
				-webkit-line-clamp: 1;
				-webkit-box-orient: vertical;
			}
		}
	}

	.order-info {
		background-color: #fff;
		margin: 20upx;
		padding: 20upx;

		.info-item {
			display: flex;
			flex-direction: row;
			align-items: center;
			justify-content: space-between;
			padding: 20upx 0;

			.title {
				color: #333;
				font-size: 15px;
			}

			.info {
				color: #333;
				font-size: 15px;
			}
		}

		.border-bottom {
			border-bottom: 1upx solid #eee;
		}
	}

	.clock-info {
		background-color: #fff;
		margin: 20upx;
		padding: 20upx;

		.info-item-column {
			display: flex;
			flex-direction: column;
			padding: 20upx 0;

			.title {
				color: #333;
				font-size: 15px;
			}

			.img {
				padding: 20upx;
			}

			.img-none {
				padding: 20upx;
				color: #999;
			}
		}

		.info-item {
			display: flex;
			flex-direction: row;
			align-items: center;
			justify-content: space-between;
			padding: 20upx 0;

			.title {
				color: #333;
				font-size: 15px;
			}

			.info {
				color: #333;
				font-size: 15px;
			}
		}

		.border-bottom {
			border-bottom: 1upx solid #eee;
		}
	}

	.row-align {
		display: flex;
		flex-direction: row;
		align-items: center;
	}

	.column-align {
		display: flex;
		flex-direction: column;
	}
</style>