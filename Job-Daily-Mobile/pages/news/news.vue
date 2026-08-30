<template>
	<view class="content">
		<public-module></public-module>
		<mescroll-uni :fixed="false" height="100%" ref="mescrollRef" @init="mescrollInit" :up="upOption" @down="downCallback" @up="upCallback" >
			<y-news-item :newsList="list" :url="detailUrl"></y-news-item>
		</mescroll-uni>
	</view>
</template>

<script>
import MescrollMixin from '@/uni_modules/mescroll-uni/components/mescroll-uni/mescroll-mixins.js';
export default {
	mixins: [MescrollMixin], // 使用mixin
	data() {
		return {
			detailUrl: '/pages/base/detail',
			list: [],
			upOption: {
				onScroll: true,
				auto: false, // 不自动加载
				noMoreSize: 5, //如果列表已无数据,可设置列表的总数量要大于半页才显示无更多数据;避免列表数据过少(比如只有一条数据),显示无更多数据会不好看; 默认5
				empty: {
					tip: '空空如也', // 提示
					icon: 'https://cdn.example.com/imgs/data.jpg'
				}
			},
			query:{
				pageNo: 1,
				pageSize: 10,
				area:null,
				sortType:null
			}
		};
	},
	onLoad() {
	},

	onShow() {},

	methods: {
		
		getNewsList(pageNo=1, pageSize=10) {
			this.query.pageNo=pageNo;
			this.query.pageSize=pageSize;
			this.$apis.getNewsList({ params: this.query, custom: { isFactory: true } }).then(res => {
				console.log('getNewsList', res);
				if (pageNo == 1) {
					this.list = [];
				}
				if (res) {
					this.list = this.list.concat(res.records); //追加新数据
				}
				console.log("list=========",this.list);
				this.mescroll.endSuccess(res.records.length);
			});
		},

		toDetail(item) {
			uni.$u.route('/pages/notice/detail?id=' + item.id);
		},

		/*下拉刷新的回调 */
		downCallback() {
			this.mescroll.resetUpScroll();
		},
		/*上拉加载的回调: 其中page.num:当前页 从1开始, page.size:每页数据条数,默认10 */
		upCallback(page) {
			this.getNewsList(page.num, page.size);
		}
	}
};
</script>

<style lang="scss">
page {
	// background-color: #f5f6fa;
}

.content {
	display: flex;
	flex-direction: column;
	align-items: center;
	height: 100vh;
}

.top {
	width: 100%;
	height: 80upx;
	position: fixed;
	top: 0;
	z-index: 99;
}

.search {
	padding: 20upx;
	width: 100%;
	box-sizing: border-box;
}
</style>
