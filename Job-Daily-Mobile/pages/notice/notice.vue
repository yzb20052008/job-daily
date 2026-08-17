<template>
	<view class="content">
		<public-module></public-module>
		<mescroll-uni :fixed="false" height="100%" width="100%" ref="mescrollRef" @init="mescrollInit" :up="upOption"
			@down="downCallback" @up="upCallback" bottom="50upx">
			<view class="item" v-for="(item,index) in list" :key="index" @click="toDetail(item)">
				<text class="time">{{$u.timeFrom(new Date(item.createTime).getTime())}}</text>
				<view class="info">
					<image :src="item.avatar" mode="aspectFill"></image>
					<view class="title"><text v-if="item.setTop" class="set-top">置顶</text><text
							class="info">{{item.title}}</text></view>
				</view>
			</view>
		</mescroll-uni>
	</view>
</template>

<script>
	import {
		mapState
	} from 'vuex';
	import MescrollMixin from '@/uni_modules/mescroll-uni/components/mescroll-uni/mescroll-mixins.js';
	export default {
		mixins: [MescrollMixin], // 使用mixin
		computed: {
			...mapState(['userInfo'])
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
					type: 0,
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
			},

			toDetail(item) {
				uni.$u.route('/pages/notice/detail?id=' + item.id);
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

				.set-top {
					background-color: #007aff;
					color: #fff;
					width: 80upx;
					padding: 5upx 0;
					font-size: 24upx;
					text-align: center;
					border-radius: 5upx;
					margin-right: 10upx;
					margin-top: 5upx;
				}

				.info {
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
		}
	}
</style>