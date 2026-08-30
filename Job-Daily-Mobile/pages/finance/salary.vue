<template>
	<view class="page">
		<mescroll-uni :fixed="false" height="100%" ref="mescrollRef" @init="mescrollInit" :up="upOption" @down="downCallback" @up="upCallback">
			<view class="body">
				<view class="item" v-for="(item, index) in list" :key="index">
					<view class="space-between item-top">
						<text class="title">{{item.content}}</text>
						<text v-if="item.addSign == 1" style="color: green;">
							+{{ item.money }}元
						</text>
						<text v-else style="color: red;">
							-{{ item.money }}元
						</text>
					</view>
					<view class="space-between item-bottom">
						<text class="time">{{item.createTime}}</text>
					</view>
				</view>
			</view>
		</mescroll-uni>
	</view>
</template>

<script>
import MescrollMixin from '@/uni_modules/mescroll-uni/components/mescroll-uni/mescroll-mixins.js';
export default {
	mixins: [MescrollMixin], // 使用mixin
	data() {
		return {
			list: [],
			query: {
				pageNo: 1,
				pageSize: 10,
				addSign: ''
			},
			upOption: {
				onScroll: true,
				auto: false, // 不自动加载
				noMoreSize: 5, //如果列表已无数据,可设置列表的总数量要大于半页才显示无更多数据;避免列表数据过少(比如只有一条数据),显示无更多数据会不好看; 默认5
				empty: {
					tip: '空空如也', // 提示
					icon: 'https://cdn.example.com/imgs/data.jpg'
				}
			},
		};
	},
	onLoad(options) {
		if(options.addSign){
			this.query.addSign=options.addSign;
			if(options.addSign==1){
				uni.setNavigationBarTitle({
					title:'收入记录'
				})
			}
		}
	},
	methods: {
		async getList(pageNo=1, pageSize=10) {
			// 提现状态:0-待审核，1-审核通过，2-审核失败
			this.query.pageNo=pageNo;
			this.query.pageSize=pageSize;
			let res = await this.$apis.getFinanceList({ params: this.query, custom: { isFactory: true } });
			console.log('res=====', res);
			if (pageNo == 1) {
				this.list = [];
			}
			if (res) {
				this.list = this.list.concat(res.records); //追加新数据
			}
			console.log("list=========",this.list);
			this.mescroll.endSuccess(res.records.length);
		},

		/*下拉刷新的回调 */
		downCallback() {
			this.mescroll.resetUpScroll();
		},
		/*上拉加载的回调: 其中page.num:当前页 从1开始, page.size:每页数据条数,默认10 */
		upCallback(page) {
			this.getList(page.num, page.size);
		}
	}
};
</script>

<style lang="scss">

page{
	background-color: #f5f6fa;
	padding: 20upx;
}

.body {
	width: 100%;
	box-sizing: border-box;
	.item {
		border-bottom: 1upx solid #eee;
		padding: 30upx;
		background-color: #fff;
		margin-bottom: 20upx;
		border-radius: 20upx;
	}
	.space-between {
		display: flex;
		flex-direction: row;
		justify-content: space-between;
		align-items: center;
	}
	.item-top text {
		color: #000;
		font-size: 30upx;
	}
	.item-bottom {
		color: #666;
		margin-top: 10upx;
	}
	
	.time{
		font-size: 14px;
		color: #888;
	}
}
</style>
