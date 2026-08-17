<template>
	<view class="content">
		<public-module></public-module>
		<mescroll-uni :fixed="true" ref="mescrollRef" @init="mescrollInit" :up="upOption" @down="downCallback"
			@up="upCallback" top="0upx">
			<view class="list">
				<y-task-comment2 :list="list"></y-task-comment2>
			</view>
		</mescroll-uni>
	</view>
</template>

<script>
	import MescrollMixin from '@/uni_modules/mescroll-uni/components/mescroll-uni/mescroll-mixins.js';
	import {
		mapState,
		mapMutations
	} from 'vuex';
	export default {
		mixins: [MescrollMixin], // 使用mixin
		computed: {
			...mapState(['userInfo'])
		},
		data() {
			return {
				detailUrl: '/pages/base/detail',
				list: [],
				upOption: {
					onScroll: true,
					auto: false, // 不自动加载
					noMoreSize: 10, //如果列表已无数据,可设置列表的总数量要大于半页才显示无更多数据;避免列表数据过少(比如只有一条数据),显示无更多数据会不好看; 默认5
					textNoMore: '-- 没有更多 --',
					empty: {
						tip: '空空如也', // 提示
						icon: 'https://img.qinkonglan.cn/imgs/data.jpg'
					}
				},
				query: {
					pageNo: 1,
					pageSize: 10,
					roleCode:''
				}
			};
		},
		onLoad(options) {
			console.log("options===",options);
			console.log("options.roleCode===",options.roleCode);
			if(options.roleCode=='company'){
				//老板评价员工，查询员工的评价列表
				this.query.roleCode="company";
				this.query.userId=options.userId;
			}else{
				//员工评价老板，查询老板的评价列表
				this.query.roleCode="member";
				this.query.postUserId=options.userId;
			}
		},

		onShow() {},

		methods: {
			getTaskCommentList(pageNo = 1, pageSize = 10) {
				this.query.pageNo = pageNo;
				this.query.pageSize = pageSize;
				this.$apis.getEvaluateList({
					params: this.query,
					custom: {
						isFactory: true
					}
				}).then(res => {
					console.log('getEvaluateList', res);
					if (pageNo == 1) {
						this.list = [];
					}
					let records=res.records;
					for(let i=0;i<records.length;i++){
						if(records[i].images){
							let imgList = records[i].images.split(',');
							let fileList=[];
							imgList.forEach(item => {
								fileList.push({
									url: item
								})
							});
							records[i].fileList=fileList;
						}
					}
					
					if (res) {
						this.list = this.list.concat(records); //追加新数据
					}
					this.mescroll.endSuccess(res.records.length);
				});
			},

			success() {
				this.mescroll.resetUpScroll();
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
				this.getTaskCommentList(page.num, page.size);
			}
		}
	};
</script>

<style lang="scss">
	page {
		background-color: #f5f6fa;
	}

	.content {
		display: flex;
		flex-direction: column;
		height: 100vh;
		width: 100%;
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
		background-color: #fff;
	}

	.list {
		padding-bottom: 50upx;
	}
</style>