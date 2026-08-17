<template>
	<view class="y-order-list">
		<view class="order-item" v-for="(item,index) in list" :key="index" @click="toDetail(item)">
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
					<u-tag :text="item.postStatus|formatStatus" size="medium" color="#007aff" borderColor="#E5F4FF" bgColor="#E5F4FF"></u-tag>
				</view>
				<view class="tags-item" v-else>
					<u-tag :text="item.postStatus|formatStatus" size="medium" color="#333" borderColor="#eee" bgColor="#eee"></u-tag>
				</view>
				<view class="tags-item">
					<u-tag :text="item.salaryUnit=='面议'?'面议':item.salary+(item.salaryUnit||'')" size="medium" color="red" borderColor="#FCEDEC" bgColor="#FCEDEC"></u-tag>
				</view>
				<!-- <view class="tags-item" @click.stop="addPrice(item,index)">
					<text class="add-price yzb yzb-bianji">加工价</text>
				</view> -->
			</view>
			<view class="y-read">
				<text>{{item.browseNumber}}人已查看</text>
			</view>
			<view class="y-number">
				<view class="grid-info">
					<text class="grid-icon">{{item.recruitsNumber}}</text>
					<text class="grid-text">招工人数</text>
				</view>
				<view class="grid-info">
					<text class="grid-icon">{{item.orderCount}}</text>
					<text class="grid-text">接单人数</text>
				</view>
				<view class="grid-info">
					<text class="grid-icon">{{item.settlementCount}}</text>
					<text class="grid-text">已结算人数</text>
				</view>
			</view>
			<view class="y-settlement"></view>
			<view class="info-btn">
				<!-- <view @click.stop="cancel(item.id, index)">
					<u-button :customStyle="{width:'70px',marginLeft:'10px',height:'30px',color:'#333'}" plain type="info"
						shape="circle">取消</u-button>
				</view> -->
				<view @click.stop="close(item.id, index)" v-if="item.postStatus==2">
					<u-button :customStyle="{width:'70px',marginLeft:'10px',height:'30px',color:'#333'}" plain type="info"
						shape="circle">停招</u-button>
				</view>
				<view @click.stop="start(item.id, index)" v-if="item.postStatus==4">
					<u-button :customStyle="{width:'70px',marginLeft:'10px',height:'30px',color:'#333'}" plain type="info"
						shape="circle">开招</u-button>
				</view>
				<view @click.stop="updatePost(item.id, index)">
					<u-button :customStyle="{width:'70px',marginLeft:'10px',height:'30px',color:'#333'}" plain type="info"
						shape="circle">修改</u-button>
				</view>
				<view @click.stop="worker(item)">
					<u-button :customStyle="{width:'90px',marginLeft:'10px',height:'30px'}" type="warning"
						shape="circle">员工管理</u-button>
				</view>
				<view @click.stop="report(item)" v-if="item.taskStatus == 2 || item.taskStatus == 3 ">
					<u-button :customStyle="{width:'90px',marginLeft:'10px',height:'30px'}" type="error"
						shape="circle">投诉司机</u-button>
				</view>
				<view @click.stop="updateStatus(item.id,3,index)" v-if="item.taskStatus == 2">
					<u-button :customStyle="{width:'90px',marginLeft:'10px',height:'30px'}" type="primary"
						shape="circle">确认送达</u-button>
				</view>
				<view @click.stop="toComment(item)" v-if="item.taskStatus == 3">
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
			// 订单状态：0-待确认，1-待开工，2-工作中，3-待结算，4-待评价，5-已完成
			// 招工状态：1-待审核，2-招工中，3-发布失败，4-已停招，5-已取消，6-已招满
			formatStatus(status) {
				let str = '';
				switch (status) {
					case "1":
						str = "待审核"
						break;
					case "2":
						str = "招工中"
						break;
					case "3":
						str = "发布失败"
						break;
					case "4":
						str = "已停招"
						break;
					case "5":
						str = "已取消"
						break;
					case "6":
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
					url: '/pages/job/postDetail?id=' + item.id
				})
			},

			toEnsure(item) {
				uni.navigateTo({
					url: '/pages/task/taskConfirm?id=' + item.id
				})
			},

			toPay(item) {
				this.$emit('pay', item)
			},

			cancel(id, index) {
				this.$emit('cancel', id, index)
			},

			close(id, index) {
				this.$emit('close', id, index)
			},
			
			start(id, index) {
				this.$emit('start', id, index)
			},

			//修改任务
			updatePost(id, index) {
				uni.$u.route("/pages/job/addPost?id=" + id);
			},

			addPrice(item,index) {
				this.$emit('addPrice',item,index)
			},
			
			worker(item) {
				uni.$u.route("/pages/order/workerManage?id=" + item.id);
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