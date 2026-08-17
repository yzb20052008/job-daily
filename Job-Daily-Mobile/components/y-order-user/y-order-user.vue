<template>
	<view class="y-order-list">
		<view class="order-item" v-for="(item,index) in list" :key="index" @click="toDetail(item)">
			<view class="item-top space-between-algin" :class="(item.orderStatus<5 && item.userEvaluate != 1)?'top-blue':'top-grey'">
				<text class="load-time">{{$u.timeFormat(item.startTime, 'mm月dd日 hh:MM')}} 开始工作</text>
				<text class="status" style="color: white;">{{item|formatStatus}}</text>
			</view>
			<view class="y-top">
				<view class="row">
					<text class="top-title">{{item.title}}</text>
				</view>
			</view>
			<view class="y-time">
				<text>工作时间：{{$u.timeFormat (new Date(item.startTime).getTime(),'mm-dd hh:MM')}} 至 {{$u.timeFormat (new Date(item.endTime).getTime(),'mm-dd hh:MM')}}</text>
			</view>
			<view class="y-status">
				<view class="tags-item" v-if="item.postStatus==2">
					<u-tag :text="item.postStatus|formatPostStatus" size="medium" color="#007aff" borderColor="#E5F4FF" bgColor="#E5F4FF"></u-tag>
				</view>
				<view class="tags-item" v-else>
					<u-tag :text="item.postStatus|formatPostStatus" size="medium" color="#333" borderColor="#eee" bgColor="#eee"></u-tag>
				</view>
				<view class="tags-item">
					<u-tag :text="item.salaryUnit=='面议'?'面议':item.salary+(item.salaryUnit||'')" size="medium" color="red" borderColor="#FCEDEC"
						bgColor="#FCEDEC"></u-tag>
				</view>
			</view>
			<view class="location">
				<text class="location-icon yzb yzb-dingwei"></text>
				<text class="location-name">{{item.addressName}}</text>
			</view>
			<view class="info-btn">
				<view @click.stop="cancelWork(item.id, index)" v-if="item.orderStatus == 0">
					<u-button :customStyle="{width:'90px',marginLeft:'10px',height:'30px',color:'#333'}" plain
						type="info" shape="circle">放弃工作</u-button>
				</view>
				<view @click.stop="startWork(item, index)" v-if="item.orderStatus == 1">
					<u-button :customStyle="{width:'90px',marginLeft:'10px',height:'30px',color:'#fff'}"
						type="warning" shape="circle">上班打卡</u-button>
				</view>
				<view @click.stop="endWork(item, index)" v-if="item.orderStatus == 2">
					<u-button :customStyle="{width:'90px',marginLeft:'10px',height:'30px',color:'#fff'}"
						type="warning" shape="circle">下班打卡</u-button>
				</view>
				<!-- <view @click.stop="worker(item)" v-if="item.orderStatus == 3">
					<u-button :customStyle="{width:'90px',marginLeft:'10px',height:'30px'}" type="warning"
						shape="circle">结算确认</u-button>
				</view> -->
				<view @click.stop="comment(item)" v-if="item.orderStatus == 4  &&  item.userEvaluate==0">
					<u-button :customStyle="{width:'80px',marginLeft:'20px',height:'30px'}" type="warning"
						shape="circle">评价</u-button>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		name: 'y-order-list',
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
				urls: []
			};
		},

		filters: {
			formatDistance(distance) {
				var strDistance = '';
				if (distance < 1) { //一公里以内的
					distance = distance.toFixed(3);
					strDistance = distance * 1000 + " m";
				} else {
					distance = distance.toFixed(2);
					strDistance = distance + " km";
				}
				return strDistance;
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
			//
			formatPostStatus(status) {
				let str = '';
				switch (status) {
					case 1:
						str = "待审核"
						break;
					case 2:
						str = "招工中"
						break;
					case 3:
						str = "发布失败"
						break;
					case 4:
						str = "已停招"
						break;
					case 5:
						str = "已取消"
						break;
					case 6:
						str = "已招满"
						break;
				}
				return str;
			},
		},

		methods: {

			getTime(end) {
				if (!end) {
					return 0;
				}
				let statTime = new Date().getTime();
				var closeTime = new Date(end.replace(/-/g, '/')).getTime(); //转时间戳
				let leftTime = closeTime - statTime;
				return leftTime;
			},

			toDetail(item) {
				uni.navigateTo({
					url: '/pages/order/detail?id=' + item.id
				})
			},

			startWork(item,index) {
				this.$emit('startWork', item,index)
			},
			
			endWork(item,index) {
				this.$emit('endWork', item,index)
			},

			cancelWork(id, index) {
				this.$emit('cancelWork', id, index)
			},
			
			comment(item) {
				uni.$u.route("/pages/order/commentUser?id=" + item.id + "&userId=" + item.userId);
			},

		}
	};
</script>

<style lang="scss" scoped>
	page {
		background-color: #f5f6fa;
	}

	.y-order-list {}

	.row {
		display: flex;
		flex-direction: row;
		align-items: center;
	}

	.order-item {
		display: flex;
		flex-direction: column;
		margin: 20upx;
		// border-bottom: 20upx solid #eee;
		margin-bottom: 20upx;
		box-sizing: border-box;
		background-color: #FFFFFF;
		border-radius: 20upx;
	}

	.space-between-algin {
		display: flex;
		flex-direction: row;
		align-items: center;
		justify-content: space-between;
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

	.info-btn {
		display: flex;
		flex-direction: row;
		justify-content: flex-end;
		align-items: center;
		border-top: 1upx solid #eee;
		padding: 20upx 20upx;
	}

	/deep/ .u-count-down__text {
		color: red;
		font-size: 30upx;
		font-weight: bold;
	}

	.top-blue {
		background-color: #0159DE;
	}

	.top-grey {
		background-color: grey;
	}

	.item-top {
		display: flex;
		flex-direction: row;
		align-items: center;
		padding: 20upx;
		border-bottom: 1upx solid #eee;
		// background-color: #0159DE;
		margin: 20upx 20upx 0 20upx;
		border-radius: 10upx;

		.load-time {
			color: #FFFFFF;
		}

		.load-distance {
			color: #FFFFFF;
		}

		.item-avatar {
			position: relative;
			width: 100upx;
			height: 100upx;
			border: 1upx solid #eee;
			border-radius: 50upx;

			image {
				width: 100%;
				height: 100%;
				border-radius: 50%;
			}
		}

		.item-info {
			display: flex;
			flex-direction: row;
			align-items: center;

			.item-name {
				display: flex;
				flex-direction: column;
				margin-left: 20upx;

				.time {
					color: #999;
					font-size: 28upx;
					margin-top: 5upx;
				}

				.info-name {
					font-weight: bold;
					font-size: 34upx;
					color: #333;
					font-weight: bold;

				}

				.icon-vip {
					color: orange;
					font-size: 36upx;
					font-weight: normal;
					margin-left: 5upx;
				}
			}
		}
	}

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


	.y-number {
		background-color: #f5f6fa;
		margin: 20upx;
		display: flex;
		align-items: center;
		border-radius: 20upx;
		box-sizing: border-box;

		.grid-info {
			display: flex;
			flex-direction: column;
			align-items: center;
			width: 100%;
			padding: 20upx 0;
		}

		.grid-icon {
			color: #333;
			font-size: 18px;
			font-weight: bold;
		}

		.grid-text {
			color: #999;
			font-size: 12px;
			margin-top: 10upx;
		}

		.grid-right-line {
			border-right: 1upx solid #f5f5f5;
		}
	}
</style>