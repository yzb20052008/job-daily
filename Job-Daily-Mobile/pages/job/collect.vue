<template>
	<view class="content">
		<mescroll-uni :fixed="false" height="100%" ref="mescrollRef" @init="mescrollInit" :up="upOption"
			@down="downCallback" @up="upCallback">
			<template v-if="userInfo.memberRole=='company'">
				<y-collect-resume :list="list" @cancel="cancel"></y-collect-resume>
			</template>
			<template v-else>
				<y-collect-post :list="list" @cancel="cancel"></y-collect-post>
			</template>
		</mescroll-uni>
	</view>
</template>

<script>
	import {
		mapState
	} from 'vuex';
	import MescrollMixin from '@/uni_modules/mescroll-uni/components/mescroll-uni/mescroll-mixins.js';
	export default {
		mixins: [MescrollMixin],
		computed: {
			...mapState(['userInfo', 'locateInformation']),
		},
		data() {
			return {
				clickable: true,
				list: [],
				upOption: {
					onScroll: true,
					auto: false, // 不自动加载
					noMoreSize: 10, //如果列表已无数据,可设置列表的总数量要大于半页才显示无更多数据;避免列表数据过少(比如只有一条数据),显示无更多数据会不好看; 默认5
					empty: {
						tip: '空空如也', // 提示
						icon: 'https://img.qinkonglan.cn/imgs/data.jpg'
					}
				},
				query: {
					pageNo: 1,
					pageSize: 10,
					latitude: 0,
					longitude: 0,
					roleCode:'',
				},
			};
		},
		onLoad() {},

		onShow() {},

		methods: {

			cancel(item) {
				let that = this;
				uni.showModal({
					title: '温馨提示',
					content: '确定取消收藏？',
					success: res => {
						if (res.confirm) {
							that.delCollect(item);
						} else if (res.cancel) {
							console.log('用户点击取消');
						}
					}
				});
			},

			/**
			 * 职位收藏
			 */
			async delCollect(item) {
				let param = {
					id: item.id,
				};
				let res = await this.$apis.delCollect(param);
				if (res) {
					this.mescroll.resetUpScroll();
					uni.showToast({
						icon: 'none',
						title: '操作成功'
					});
				}
			},

			getCollectList(pageNum, pageSize) {
				this.query.pageNo = pageNum;
				this.query.pageSize = pageSize;
				if(this.userInfo.memberRole=='company'){
					this.query.roleCode='company';
				}else{
					this.query.roleCode='member';
				}
				if (!this.locateInformation.location) {
					console.log("暂无定位信息")
				} else {
					this.query.latitude = this.locateInformation.location.lat;
					this.query.longitude = this.locateInformation.location.lng;
				}
				this.$apis.getCollectList({
					params: this.query
				}).then(res => {
					console.log('getCollectList', res);
					if (pageNum == 1) {
						this.list = [];
					}
					if (res) {
						let data = res.records;
						this.list = this.list.concat(data);
						this.mescroll.endSuccess(res.records.length);
					}
				});
			},

			/*下拉刷新的回调 */
			downCallback() {
				this.mescroll.resetUpScroll();
			},
			/*上拉加载的回调: 其中page.num:当前页 从1开始, page.size:每页数据条数,默认10 */
			upCallback(page) {
				this.getCollectList(page.num, page.size);
			},
		}
	};
</script>

<style lang="scss">
	page {
		background-color: #f5f5f5;
	}

	.content {
		flex: 1;
		flex-direction: column;
		align-items: center;
		box-sizing: border-box;
	}

	.item {
		display: flex;
		flex-direction: column;
		margin-bottom: 20rpx;
		background-color: #fff;
		border-radius: 20rpx;
		flex: 1;
		padding: 20upx;

		.item-title {
			// font-weight: bold;
			font-size: 30upx;
			color: #000;
			display: flex;
			flex-direction: row;
			justify-content: space-between;

			.integral {
				color: #007aff;
			}

			.integral2 {
				color: red;
			}
		}

		.item-li {
			align-items: center;
			color: #666;
			font-size: 28upx;
			margin-top: 15upx;
		}

		.yzb {
			margin-right: 8upx;
		}
	}
</style>