<template>
	<view class="page">
		<mescroll-uni :fixed="false" height="100%" ref="mescrollRef" @init="mescrollInit" :up="upOption" @down="downCallback" @up="upCallback">
			<view class="body">
				<view class="item" v-for="(item, index) in list" :key="index">
					<view class="space-between item-top">
						<text class="title">用户提现</text>
						<text>
							{{ item.money }}元
						</text>
					</view>
					<view class="space-between item-bottom">
						<text class="time">{{item.createTime}}</text>
						<text v-if="item.withdrawStatus==0" style="color: chocolate;">待审核</text>
						<text v-if="item.withdrawStatus==1" style="color: darkgreen;">审核通过</text>
						<text v-if="item.withdrawStatus==2" style="color: darkred;">审核失败：{{ item.reason }}</text>
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
				type: 0
			},
			upOption: {
				onScroll: true,
				auto: false, // 不自动加载
				noMoreSize: 5, //如果列表已无数据,可设置列表的总数量要大于半页才显示无更多数据;避免列表数据过少(比如只有一条数据),显示无更多数据会不好看; 默认5
				empty: {
					tip: '暂无提现记录', // 提示
					icon: 'https://img.qinkonglan.cn/imgs/data.jpg'
				}
			},
		};
	},
	onLoad(options) {
	},
	methods: {
		async getList(pageNo=1, pageSize=10) {
			// 提现状态:0-待审核，1-审核通过，2-审核失败
			this.query.pageNo=pageNo;
			this.query.pageSize=pageSize;
			let res = await this.$apis.getWithdrawList({ params: this.query, custom: { isFactory: true } });
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
