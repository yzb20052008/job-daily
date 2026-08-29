<template>
	<view class="content">
		<public-module></public-module>
		<mescroll-uni :fixed="false" height="100%" width="100%" ref="mescrollRef" @init="mescrollInit" :up="upOption"
			@down="downCallback" @up="upCallback" bottom="50upx">
			<view class="item" v-for="(item,index) in list" :key="index" @click="toDetail(item)">
				<text class="time">{{$u.timeFrom(new Date((item.createTime||'').replace(/-/g,'/')).getTime())}}</text>
				<view class="info">
					<view class="title"><text class="info">{{item.title}}</text></view>
					<view class="address">
						<view class="address-item">
							<text class="dot-get"></text>
							<text class="address-name">{{item.postTitle}}</text>
						</view>
					</view>
					<text class="desc">{{item.content}}</text>
				</view>
			</view>
		</mescroll-uni>
	</view>
</template>

<script>
	import {
		mapState,
	} from 'vuex';
	import MescrollMixin from '@/uni_modules/mescroll-uni/components/mescroll-uni/mescroll-mixins.js';
	export default {
		mixins: [MescrollMixin], // 使用mixin
		computed: {
			...mapState(['userInfo', 'memberRole'])
		},
		data() {
			return {
				clickable: true,
				list: [],
				upOption: {
					onScroll: true,
					auto: false, // 不自动加载
					noMoreSize: 5, //如果列表已无数据,可设置列表的总数量要大于半页才显示无更多数据;避免列表数据过少(比如只有一条数据),显示无更多数据会不好看; 默认5
					empty: {
						tip: '空空如也', // 提示
						icon: 'https://oldbiao.oss-cn-beijing.aliyuncs.com/odb/public/data.jpg'
					}
				},

				query: {
					pageNo: 1,
					pageSize: 10,
					userId: null,
					type: 1,
					roleCode: null,
				}
			};
		},
		onLoad() {},

		onShow() {},

		methods: {
			async getNoticeList(pageNo = 1, pageSize = 10) {
				this.query.pageNo = pageNo;
				this.query.pageSize = pageSize;
				if (this.userInfo.token) {
					this.query.userId = this.userInfo.id;
					this.query.roleCode = this.memberRole || this.userInfo.memberRole;
				}
				let res = await this.$apis.getNoticeList({
					params: this.query,
					custom: {
						isFactory: true
					}
				});
				if (pageNo == 1) {
					this.list = [];
				}
				if (res) {
					this.list = this.list.concat(res.records); //追加新数据
				}
				console.log("list=========", this.list);
				this.mescroll.endSuccess(res.records.length);
				console.log('===getNoticeList===', res);
			},

			toDetail(item) {
				// uni.$u.route('/pages/notice/detail?id=' + item.id);
				uni.$u.route('/pages/order/detail?id=' + item.orderId);
			},

			/*下拉刷新的回调 */
			downCallback() {
				// 这里加载你想下拉刷新的数据, 比如刷新轮播数据
				// loadSwiper();
				// 下拉刷新的回调,默认重置上拉加载列表为第一页 (自动执行 page.num=1, 再触发upCallback方法 )
				this.mescroll.resetUpScroll();
			},
			/*上拉加载的回调: 其中page.num:当前页 从1开始, page.size:每页数据条数,默认10 */
			upCallback(page) {
				this.getNoticeList(page.num, page.size);
			}
		}
	};
</script>

<style lang="scss">
	page {
		background-color: #f5f6fa;
	}

	.content {
		flex: 1;
		padding: 30rpx;
		box-sizing: border-box;
		width: 100vw;
	}

	.item {
		display: flex;
		flex-direction: column;
		margin-bottom: 30rpx;
		width: 100%;

		.time {
			margin: 0 auto;
			padding: 15rpx 0;
			color: #999;
			font-size: 26rpx;
		}

		.info {
			background-color: #fff;
			display: flex;
			flex-direction: column;
			align-items: center;
			border-radius: 20rpx;
			width: 100%;

			image {
				width: 690rpx;
				height: 330rpx;
				border-radius: 20rpx;
			}

			.title {
				padding: 20rpx;
				box-sizing: border-box;
				display: flex;
				flex-direction: row;
				align-items: flex-start;
				width: 100%;

				.info {
					font-size: 16px;
					text-align: start;
					overflow: hidden;
					word-break: break-all;
					/* break-all(允许在单词内换行。) */
					text-overflow: ellipsis;
					/* 超出部分省略号 */
					display: -webkit-box;
					/** 对象作为伸缩盒子模型显示 **/
					-webkit-box-orient: vertical;
					/** 设置或检索伸缩盒对象的子元素的排列方式 **/
					-webkit-line-clamp: 2;
					/** 显示的行数 **/
				}
			}

			.address {
				margin-top: 10upx;
				padding: 0 20upx 20upx 20upx;
				width: 100%;

				.address-item {
					display: flex;
					flex-direction: row;
					align-items: center;
					margin-bottom: 10upx;

					.dot-get {
						background-color: green;
						width: 20upx;
						height: 20upx;
						border-radius: 50%;
					}

					.dot-mid {
						background-color: #3F536E;
						width: 20upx;
						height: 20upx;
						border-radius: 50%;
					}

					.dot-mid {
						background-color: #f0ad4e;
						width: 20upx;
						height: 20upx;
						border-radius: 50%;
					}

					.dot-send {
						background-color: #FC6314;
						width: 20upx;
						height: 20upx;
						border-radius: 50%;
					}

					.address-name {
						color: #333;
						margin-left: 20upx;
						font-size: 14px;
					}
				}

				image {
					margin-right: 5upx;
					width: 50upx;
					height: 50upx;
				}
			}

			.desc {
				padding:20upx;
				font-size: 14px;
				color: #999;
				line-height: 1.6;
				margin: 20upx;
				flex: 1;
				width: 96%;
				border-radius: 20upx;
				background-color: #f8f8f8;
			}
		}
	}
</style>