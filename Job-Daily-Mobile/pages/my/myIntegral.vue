<template>
	<view class="content">
		<public-module></public-module>
		<mescroll-uni :fixed="false" height="100%" ref="mescrollRef" @init="mescrollInit" :up="upOption" @down="downCallback" @up="upCallback">
			<view class="item" v-for="(item,index) in list" :key="index">
				<view class="item-title">
					<text>{{item.remark}}</text>
					<text class="integral" v-if="item.ifAdd">+{{item.integral}}</text>
					<text class="integral2" v-else>-{{item.integral}}</text>
				</view>
				<view class="item-li">
					<text class="yzb yzb-shijian"></text>
					<text class="time">{{$u.timeFormat(item.createTime,'yyyy年mm月dd日 hh:MM')}}</text>
				</view>
			</view>
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
		getIntegralList(pageNum,pageSize) {
			let param={
				pageNo:pageNum,
				pageSize:pageSize,
				userId:this.userInfo.id
			}
			this.$apis.getIntegralList({ params: param, custom: { isFactory: true } }).then(res => {
				console.log('getIntegralList', res);
				if (pageNum == 1) {
					this.list = [];
				}
				if (res) {
					let data=res.records;
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
			this.mescroll.resetUpScroll();
		},
		/*上拉加载的回调: 其中page.num:当前页 从1开始, page.size:每页数据条数,默认10 */
		upCallback(page) {
			this.getIntegralList(page.num, page.size);
		}
	}
};
</script>

<style lang="scss">
page {
	background-color: #f5f6fa;
}
view{
	flex: 1;
}
.content {
	flex: 1;
	padding: 20upx;
	flex-direction: column;
	align-items: center;
	height: 100vh;
}
.item {
	display: flex;
	flex-direction: column;
	margin-bottom: 20rpx;
	background-color: #fff;
	border-radius: 20rpx;
	flex: 1;
	padding: 20upx;
	.item-title{
		font-weight: bold;
		font-size: 32upx;
		color: #000;
		display: flex;
		flex-direction: row;
		justify-content: space-between;
		.integral{
			color: #007aff;
		}
		.integral2{
			color: red;
		}
	}
	.item-li{
		align-items: center;
		color:#666;
		font-size:28upx;
		margin-top:10upx;
	}
	.yzb{
		margin-right: 8upx;
	}
}
</style>
