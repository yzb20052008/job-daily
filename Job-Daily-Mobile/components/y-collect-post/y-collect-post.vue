<template>
	<view class="y-post-list">
		<view class="post-item" v-for="(item,index) in list" :key="index" @click="itemClick(item)">
			<text class="title">{{item.title}}</text>
			<view class="tags">
				<view class="tags-item"  v-if="item.companyName"  @click="itemClick(item)">
					<u-tag text="企业认证" size="mini" color="#007aff" borderColor="#E5F4FF" bgColor="#E5F4FF"></u-tag>
				</view>
				<view class="tags-item" v-if="item.realName"  @click="itemClick(item)">
					<u-tag text="已实名" size="mini" color="#007aff" borderColor="#E5F4FF" bgColor="#E5F4FF"></u-tag>
				</view>
				<view class="tags-item" v-for="(itx,idx) in toList(item.typeNames)" :key="idx">
					<u-tag :text="itx" size="mini" color="#666" borderColor="#f5f6fa" bgColor="#f5f6fa"  @click="itemClick(item)"></u-tag>
				</view>
			</view>
			<view class="bottom">
				<view class="location">
					<text class="location-icon yzb yzb-dingwei"></text>
					<text class="location-name">{{item.addressName}}</text>
					<text class="load-distance">{{item.distance | formatDistance}}</text>
				</view>
				<text class="time">{{$u.timeFormat(item.createTime,'mm月dd日 hh:MM')}}</text>
			</view>
			<view class="cancel">
				<text class="collect-time">{{item.createTime}}</text>
				<text class="btn-cancel" @click.stop="cancel(item)">取消收藏</text>
			</view>
		</view>
	</view>
</template>


<script>
	import {
		mapState
	} from 'vuex';
	import {
		judgeLogin
	} from '@/config/login';
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
			return {};
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
			}
		},

		methods: {
			
			toList(val){
				if(val){
					return val.split("、");
				}
				return [];
			},

			itemClick(item) {
				uni.$u.route("/pages/job/postDetail?id=" + item.dataId);
			},

			// 取消收藏
			cancel(item) {
				this.$emit('cancel', item)
			},
		}
	};
</script>

<style lang="scss" scoped>
	page {
		background-color: #f5f6fa;
	}

	.y-post-list {}

	.post-item {
		display: flex;
		flex-direction: column;
		margin: 20upx 20upx 0upx 20upx;
		// margin-bottom: 20upx;
		padding: 30upx 20upx;
		box-sizing: border-box;
		background-color: #FFFFFF;
		border-radius: 20upx;
		
		.title{
			font-size: 18px;
			font-weight: bold;
			color: #333;
			text-overflow: -o-ellipsis-lastline;
			overflow: hidden;
			text-overflow: ellipsis;
			display: -webkit-box;
			-webkit-line-clamp: 2;
			-webkit-box-orient: vertical;
		}

		.tags {
			display: flex;
			flex-direction: row;
			flex-wrap: wrap;
			align-items: center;
			padding: 20upx 0;
			
			.tags-item{
				margin-right: 15upx;
			}
		}
		
		.bottom{
			display: flex;
			flex-direction: row;
			align-items: center;
			justify-content: space-between;
			padding-bottom: 20upx;
			
			.location{
				display: flex;
				flex-direction: row;
				align-items: center;
				
				.location-icon{
					color: #666;
					font-size: 12px;
					margin-right: 10upx;
				}
				
				.location-name{
					max-width: 60%;
					font-size: 12px;
					color: #666;
					text-overflow: -o-ellipsis-lastline;
					overflow: hidden;
					text-overflow: ellipsis;
					display: -webkit-box;
					-webkit-line-clamp: 1;
					-webkit-box-orient: vertical;
				}
				
				.load-distance{
					color: #666;
					font-size: 12px;
					margin-left: 10upx;
				}
			}
			
			
			.time{
				font-size: 12px;
				color: #999;
				min-width: 210upx;
				text-align: right;
			}
		}
	}
	
	.cancel{
		display: flex;
		flex-direction: row;
		align-items: center;
		justify-content: space-between;
		padding-top: 20upx;
		border-top: 1upx solid #eee;
		.collect-time{
			font-size: 14px;
			color: #999;
		}
		.btn-cancel{
			font-size: 15px;
			color: #dd524d;
		}
	}
</style>