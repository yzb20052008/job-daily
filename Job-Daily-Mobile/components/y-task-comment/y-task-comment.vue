<template>
	<view class="y-driver-list">
		<view class="drive-item" v-for="(item,index) in list" :key="index" @click="toDetail(item)">
			<view class="item-top space-between-algin">
				<view class="item-info" v-if="item.roleCode=='company'">
					<view class="item-avatar">
						<u-avatar size="50" shape="square" :src="item.userAvatar"></u-avatar>
					</view>
					<view class="item-name">
						<view class="row-align">
							<text class="info-name">{{item.userName}}</text>
							<text class="role">员工</text>
						</view>
						<view class="row-align" style="margin-top: 10upx;">
							<text class="phone">{{phoneHiden(item.userPhone)}}</text>
							<text class="score">{{item.userScore}}分</text>
						</view>
					</view>
				</view>
				<view class="item-info" v-else>
					<view class="item-avatar">
						<u-avatar size="50" shape="square" :src="item.bossAvatar"></u-avatar>
					</view>
					<view class="item-name">
						<view class="row-align">
							<text class="info-name">{{item.bossName}}</text>
							<text class="role">老板</text>
						</view>
						<view class="row-align" style="margin-top: 10upx;">
							<text class="phone">{{phoneHiden(item.bossPhone)}}</text>
							<text class="score">{{item.bossScore}}分</text>
						</view>
					</view>
				</view>
			</view>
			<view class="comment">
				<view class="star">
					<u-rate size="22" v-model="item.score" count="5" readonly activeColor="#FD7716"></u-rate>
				</view>
				<view class="remark" v-if="item.content">
					<text>{{item.content}}</text>
				</view>
				<view class="images" v-if="item.fileList">
					<u-upload :fileList="item.fileList" name="1" multiple disabled :deletable="false"
						:multiple="true" :maxCount="item.fileList.length"></u-upload>
				</view>
				<view class="bottom">
					<text></text>
					<text class="time">{{$u.timeFormat(item.createTime, 'yyyy年mm月dd日 hh:MM')}}</text>
					<!-- <view class="right">
						<text class="">查看任务</text>
						<text class="yzb yzb-next"></text>
					</view> -->
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		phoneHiden,
	} from '@/config/common';
	export default {
		name: 'y-driver-list',
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
			return {};
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
		},

		methods: {
			
			phoneHiden(val) {
				return phoneHiden(val);
			},

			toDetail(item) {
				uni.navigateTo({
					url: '/pages/task/detail?type=1&id=' + item.taskId
				})
			},
		}
	};
</script>

<style lang="scss" scoped>
	page {
		background-color: #f5f6fa;
	}

	.y-driver-list {}

	.drive-item {
		display: flex;
		flex-direction: column;
		margin: 20upx;
		// border-bottom: 20upx solid #eee;
		margin-bottom: 20upx;
		box-sizing: border-box;
		background-color: #FFFFFF;
		border-radius: 20upx;
	}

	.item-top {
		display: flex;
		flex-direction: row;
		align-items: center;
		padding: 20upx;
		border-bottom: 1upx solid #eee;

		.item-avatar {
			position: relative;
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
				
				.score{
					font-size: 14px;
					color: #dd524d;
					font-weight: bold;
				}
				
				.phone{
					font-size: 14px;
					color: #666;
					margin-right: 20upx;
				}
				
				.role{
					margin-left: 20upx;
					color: #36343c;
					font-weight: bold;
					background-color: #f7d680;
					font-size: 26upx;
					border-radius: 20upx;
					padding: 4upx 10upx;
				}

				.icon-vip {
					color: orange;
					font-size: 36upx;
					font-weight: normal;
					margin-left: 5upx;
				}
			}
		}

		.status {
			font-weight: bold;
			color: red;
		}
	}
	
	.row-align{
		display: flex;
		flex-direction: row;
		align-items: center;
	}

	.space-between-algin {
		display: flex;
		flex-direction: row;
		align-items: center;
		justify-content: space-between;
	}
	
	.comment{
		padding: 20upx;
		.star{
			
		}
		
		.tags{
			padding: 20upx 0;
			.tag{
				flex-wrap: wrap;
				background-color: #eee;
				border-radius: 10upx;
				margin-right: 30upx;
				padding: 5upx 15upx;
				color: #333;
				font-size: 28upx;
			}
		}
		
		.remark{
			background-color: #f5f6fa;
			padding: 20upx;
			border-radius: 10upx;
			margin-top: 20upx;
			text{
				font-size: 30upx;
				color: #333;
			}
		}
		
		.images{
			margin-top: 20upx;
		}
		
		.bottom{
			margin-top: 20upx;
			display: flex;
			flex-direction: row;
			align-items: center;
			justify-content: space-between;
			.time{
				color: #999;
				font-size: 28upx;
			}
			
			.right{
				text{
					color: #666;
					font-size: 28upx;
				}
			}
		}
	}

</style>