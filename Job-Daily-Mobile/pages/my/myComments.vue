<template>
	<view class="content">
		<public-module></public-module>
		<mescroll-uni :fixed="false" height="100%" ref="mescrollRef" @init="mescrollInit" :up="upOption" @down="downCallback" @up="upCallback">
			<view class="item" v-for="(item, index) in list" :key="index"><y-comment :item="item"></y-comment></view>
		</mescroll-uni>
	</view>
</template>

<script>
import { mapState} from 'vuex';
import MescrollMixin from '@/uni_modules/mescroll-uni/components/mescroll-uni/mescroll-mixins.js';
export default {
	mixins: [MescrollMixin], // 使用mixin
	computed: {
		...mapState(['userInfo']),
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
			}
		};
	},
	onLoad() {
	},

	onShow() {},

	methods: {

		getCommentList(pageNum,pageSize) {
			let param={
				pageNo:pageNum,
				pageSize:pageSize,
				userId:this.userInfo.id
			}
			this.$apis.getCommentList({ params: param, custom: { isFactory: true } }).then(res => {
				console.log('getCommentList', res);
				if (pageNum == 1) {
					this.list = [];
				}
				if (res) {
					let data=res.records;
					data.forEach((item,index)=>{
						if(item.imgs){
							item.urlList = item.imgs.split(",");
						}
					})
					this.list = this.list.concat(data);
					this.mescroll.endSuccess(res.records.length);
				}
			});
		},

		toDetail(item) {
			uni.$u.route('/pages/notice/detail?id='+item.id);
		},

		/*下拉刷新的回调 */
		downCallback() {
			// 这里加载你想下拉刷新的数据, 比如刷新轮播数据
			// 下拉刷新的回调,默认重置上拉加载列表为第一页 (自动执行 page.num=1, 再触发upCallback方法 )
			this.mescroll.resetUpScroll();
		},
		/*上拉加载的回调: 其中page.num:当前页 从1开始, page.size:每页数据条数,默认10 */
		upCallback(page) {
			console.log("upCallback",page);
			this.getCommentList(page.num, page.size);
		}
	}
};
</script>

<style lang="scss">
page {
	// background-color: #f5f6fa;
}

.content {
	flex: 1;
	flex-direction: column;
	align-items: center;
	height: 100vh;
}
.item {
	display: flex;
	flex-direction: column;
	margin-bottom: 30rpx;
	flex: 1;

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
		image {
			width: 690rpx;
			height: 330rpx;
			border-radius: 20rpx;
		}
		.title {
			padding: 20rpx;
			box-sizing: border-box;
			text {
				text-align: start;
				overflow: hidden;
				word-break: break-all; /* break-all(允许在单词内换行。) */
				text-overflow: ellipsis; /* 超出部分省略号 */
				display: -webkit-box; /** 对象作为伸缩盒子模型显示 **/
				-webkit-box-orient: vertical; /** 设置或检索伸缩盒对象的子元素的排列方式 **/
				-webkit-line-clamp: 2; /** 显示的行数 **/
			}
		}
	}
}
</style>
