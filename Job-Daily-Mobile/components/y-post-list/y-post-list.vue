<template>
	<view class="y-post-list">
		<view class="post-item" v-for="(item,index) in list" :key="index" @click="itemClick(item)">
			<text class="title">{{item.title}}</text>
			<view class="tags">
				<view class="tags-item"  v-if="item.companyName"  @click="itemClick(item)">
					<u-tag text="企业认证" size="mini" color="#007aff" borderColor="#E5F4FF" bgColor="#E5F4FF"></u-tag>
				</view>
				<view class="tags-item" v-if="!item.companyName && item.realName"  @click="itemClick(item)">
					<u-tag text="已实名" size="mini" color="#007aff" borderColor="#E5F4FF" bgColor="#E5F4FF"></u-tag>
				</view>
				<view class="tags-item" v-for="(itx,idx) in toList(item.typeNames)" :key="idx">
					<u-tag :text="itx" size="mini" color="#666" borderColor="#f5f6fa" bgColor="#f5f6fa"  @click="itemClick(item)"></u-tag>
				</view>
				<view class="tags-item" v-if="item.postStatus==2">
					<u-tag :text="item.postStatus|formatPostStatus" size="mini" color="#007aff" borderColor="#E5F4FF" bgColor="#E5F4FF"></u-tag>
				</view>
				<view class="tags-item" v-else>
					<u-tag :text="item.postStatus|formatPostStatus" size="mini" color="#666" borderColor="#f5f6fa" bgColor="#f5f6fa"></u-tag>
				</view>
				<view class="tags-item">
					<u-tag :text="item.salaryUnit=='面议'?'面议':item.salary+(item.salaryUnit||'')" size="mini" color="red" borderColor="#FCEDEC"
						bgColor="#FCEDEC"></u-tag>
				</view>
			</view>
			<view class="bottom">
				<view class="location">
					<text class="location-icon yzb yzb-dingwei"></text>
					<!-- <text class="location-name">{{item.addressName}}</text> -->
					<text class="location-name">{{item.pCity}}-{{item.city}}</text>
					<text class="load-distance">{{item.distance | formatDistance}}</text>
				</view>
				<text class="time">{{$u.timeFormat(item.createTime,'mm月dd日 hh:MM')}}</text>
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
			},
			
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
			
			toList(val){
				if(val){
					return val.split("、");
				}
				return [];
			},

			// 电话客服
			phoneCall(item) {
				console.log("phone==", item)
				let that = this;
				if (item.vipLevel < 1) {
					return;
				}
				console.log("judgeLogin==")
				this.judgeLogin(() => {
					console.log("addCallRecord==")
					that.addCallRecord(item);
				})
			},

			//添加拨号记录
			async addCallRecord(item) {
				let params = {
					driverId: item.driverId
				}
				let res = await this.$apis.addContact(params);
				console.log("addCallRecord", res);
				if (res) {
					uni.makePhoneCall({
						phoneNumber: item.contact,
						success: () => {
							console.log('成功拨打电话');
						},
						fail: (err) => {
							console.log(err);
						},
					});
					this.$emit('success', item)
				}
			},

			itemClick(item) {
				this.$emit('itemClick', item)
				uni.$u.route("/pages/job/postDetail?id=" + item.id);
			},

			toDriver(item) {
				this.judgeLogin(() => {
					uni.$u.route("/pages/driver/driverInfo?id=" + item.userId);
				})
			},

			//举报
			toReport(item) {
				this.judgeLogin(() => {
					uni.$u.route("/pages/driver/report?userId=" + item.userId);
				})
			},

			//收藏
			addCollect(item) {
				let that = this;
				this.judgeLogin(() => {
					uni.showModal({
						title: "温馨提示",
						content: "确定收藏该司机？",
						confirmText: "确定",
						cancelText: "取消",
						success: (data) => {
							if (data.confirm) {
								that.doCollect(item);
							}
						}
					});
				})
			},

			async doCollect(item) {
				let params = {
					driverId: item.driverId
				}
				let res = await this.$apis.addCollect(params);
				console.log("doCollect", res);
				if (res) {
					this.$emit('success', item)
					uni.$u.toast('收藏成功');
				}
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
			font-size: 38rpx;
			font-weight: bold;
			color: #000;
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
			padding: 20upx 0 10upx 0;
			
			.tags-item{
				margin-right: 15upx;
				margin-bottom: 15upx;
			}
		}
		
		.bottom{
			display: flex;
			flex-direction: row;
			align-items: center;
			justify-content: space-between;
			
			.location{
				display: flex;
				flex-direction: row;
				align-items: center;
				
				.location-icon{
					color: #666;
					font-size: 24rpx;
					margin-right: 10upx;
				}
				
				.location-name{
					max-width: 60%;
					font-size: 24rpx;
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
					font-size: 24rpx;
					margin-left: 10upx;
				}
			}
			
			
			.time{
				font-size: 24rpx;
				color: #999;
				min-width: 210upx;
				text-align: right;
			}
		}
	}
</style>