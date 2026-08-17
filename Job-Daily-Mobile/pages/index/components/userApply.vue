<!-- 菜单悬浮的原理: 监听滚动条的位置大于某个值时,控制顶部菜单的显示和隐藏, 用法比sticky复杂, 但APP端可兼容低端机 -->
<template>
	<view class="content">
		<public-module></public-module>
		<!-- 菜单 (悬浮,预先隐藏)-->
		<!-- <me-tabs v-if="isShowSticky" v-model="tabIndex" :fixed="true" :tabs="tabs" @change="tabChange"></me-tabs> -->
		<!-- <u-sticky offset-top="0"> -->
		<view class="top" id="top">
			<view class="navbar" :style="{height:systemInfo.navBarH+'px'}">
				<view class="search" :style="{right:searchRight+'px'}">
					<u-search placeholder="输入关键字搜索" v-model="keyword" :clearabled="true" :showAction="false" 
					 @clear="clearInput"  @change="inputChange"  @search="search"></u-search>
				</view>
			</view>
			<view id="tabInList" class="tabInList">
				<view class="tab-filter-container">
					<view class="right-content">
						<me-tabs v-model="tabIndex" :tabs="tabs" @change="tabChange"></me-tabs>
					</view>
				</view>
			</view>
		</view>
		<!-- </u-sticky> -->
		<mescroll-body ref="mescrollRef" @init="mescrollInit" @down="downCallback" @up="upCallback" :up="upOption"
			@scroll="scroll" @topclick="topClick" :top="mTop+'px'">
			<!-- 菜单 (在mescroll-uni中不能使用fixed,否则iOS滚动时会抖动, 所以需在mescroll-uni之外存在一个一样的菜单) -->
			<view class="y-list">
				<yzb-process :list="tabs[tabIndex].list" @onDelete="toDelete" @ensure="toEnsure"></yzb-process>
			</view>
		</mescroll-body>
	</view>
</template>

<script>
	import MescrollMixin from "@/uni_modules/mescroll-uni/components/mescroll-uni/mescroll-mixins.js";
	import BaseUrl from '@/config/baseUrl.js';
	import {
		mapState,
		mapMutations
	} from 'vuex';
	import {
		loGetLocation,
		loGetGaodeLocation,
		getDistance
	} from '@/config/common';
	import yDualFilter from '@/components/y-dual-filter/y-dual-filter.vue';
	const GoEasy = uni.$GoEasy;
	export default {
		mixins: [MescrollMixin], // 使用mixin
		components: {
			yDualFilter
		},
		computed: {
			...mapState(['userInfo', 'memberRole']),
			// 列表数据
			list() {
				return this.tabs[this.tabIndex].list || [];
			}
		},

		data() {
			return {
				keyword:'',
				title: ' ',
				systemInfo: this.$base.systemInfo,
				mTop:0,
				searchRight:1,
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
				//求职状态:1-已投递，2-被查看，3-初筛，4-笔试，5-面试，6-录用，9-不合适
				tabs: [{
						name: '待查看',
						list: null,
						num: 1,
						y: 0,
						curPageLen: 0,
						hasNext: true,
						status: 1
					},
					{
						name: '被查看',
						list: null,
						num: 1,
						y: 0,
						curPageLen: 0,
						hasNext: true,
						status: 2
					},
					{
						name: '初筛',
						list: null,
						num: 1,
						y: 0,
						curPageLen: 0,
						hasNext: true,
						status: 3
					},
					// {
					// 	name: '笔试',
					// 	list: null,
					// 	num: 1,
					// 	y: 0,
					// 	curPageLen: 0,
					// 	hasNext: true,
					// 	status: 4
					// },
					{
						name: '面试',
						list: null,
						num: 1,
						y: 0,
						curPageLen: 0,
						hasNext: true,
						status: 5
					},
					{
						name: '录用',
						list: null,
						num: 1,
						y: 0,
						curPageLen: 0,
						hasNext: true,
						status: 6
					},
					{
						name: '不合适',
						list: null,
						num: 1,
						y: 0,
						curPageLen: 0,
						hasNext: true,
						status: 9
					},
				],
				tabIndex: 0, // 当前菜单下标
				preIndex: 0, // 前一个菜单下标
				navTop: null, // nav距离到顶部的距离 (如计算不准确,可直接写死某个值)
				isShowSticky: false, // 是否悬浮
				query: {
					keyword:null,
					pageNo: 1,
					pageSize: 10,
					applyStatus: 1,
				},
			}
		},


		methods: {

			initData() {
				let menuButtonInfo = uni.getMenuButtonBoundingClientRect();
				this.searchRight = this.systemInfo. windowWidth-menuButtonInfo.left+10;
				this.$nextTick(() => {
					let view = uni.createSelectorQuery().in(this).select('#top');
					view.boundingClientRect(data => {
						 
						 this.mTop=data.height;
					}).exec();
				});
				this.mescroll.resetUpScroll();
			},
			
			
			clearInput(){
				this.keyword="";
			},
			
			inputChange(e){
				console.log("===inputChange==",e)
				this.query.keyword=this.keyword;
				this.getList(1,10);
			},
			
			search(){
				console.log("===inputChange==",e)
			},

			getList(pageIndex, pageSize) {
				if (!this.userInfo.token) {
					console.log("===getList===未登录")
					uni.hideLoading();
					this.mescroll.endSuccess(0);
					return;
				}
				this.getApplyResponseList(pageIndex, pageSize);
			},

			/**
			 * 任务列表
			 */
			async getApplyResponseList(pageIndex, pageSize) {
				this.query.pageNo = pageIndex;
				this.query.pageSize = pageSize;
				let res = await this.$apis.getApplyResponseList({
					params: this.query
				});
				// 当前tab数据
				let curTab = this.tabs[this.tabIndex]
				//设置列表数据
				if (pageIndex == 1) {
					curTab.list = []; //如果是第一页需手动制空列表
				}
				let data = res.records;
				curTab.list = curTab.list.concat(data); //追加新数据
				setTimeout(() => {
					// 需先隐藏加载状态
					this.mescroll.endSuccess(data.length);
					// 再记录当前页的数据
					curTab.num = pageIndex; // 页码
					curTab.curPageLen = data.length; // 当前页长
					curTab.hasNext = this.mescroll.optUp.hasNext; // 是否还有下一页

					// 设置nav到顶部的距离 (需根据自身的情况获取navTop的值, 这里放到列表数据渲染完毕之后)
					// 也可以放到onReady里面,或者菜单顶部的数据(轮播等)加载完毕之后..
					if (!this.navTop) this.setNavTop()
					// 保持tab悬浮,列表数据显示第一条
					if (this.isChangeTab) {
						this.isChangeTab = false;
						uni.hideLoading();
						if (this.isShowSticky) this.mescroll.scrollTo(this.navTop, 0)
					}
				}, 20)
			},

			/*下拉刷新的回调 */
			downCallback() {
				console.log("downCallback");
				// 这里加载你想下拉刷新的数据, 比如刷新轮播数据
				// loadSwiper();
				// 下拉刷新的回调,默认重置上拉加载列表为第一页 (自动执行 page.num=1, 再触发upCallback方法 )
				this.mescroll.resetUpScroll()
			},
			/*上拉加载的回调: 其中page.num:当前页 从1开始, page.size:每页数据条数,默认10 */
			upCallback(page) {
				console.log("upCallback", page);
				//联网加载数据
				if (this.isChangeTab) {
					this.mescroll.hideUpScroll(); // 切换菜单,不显示mescroll进度, 显示系统进度条
					uni.showLoading();
				}
				if (this.tabIndex == 0) {
					this.getList(page.num, page.size, null);
				} else {
					this.getList(page.num, page.size, this.tabs[this.tabIndex].taskStatus);
				}
			},
			// 设置nav到顶部的距离 (滚动条为0, 菜单顶部的数据加载完毕获取到的navTop数值是最精确的)
			setNavTop() {
				let view = uni.createSelectorQuery().in(this).select('#tabInList');
				view.boundingClientRect(data => {
					this.navTop = data.top // 到屏幕顶部的距离
				}).exec();
			},
			// mescroll-uni的滚动事件 (需在up配置onScroll:true才生效)
			// 而mescroll-body最简单只需在onPageScroll处理即可
			scroll() {
				console.log("===scroll==");
				// 菜单悬浮的原理: 监听滚动条的位置大于某个值时,控制顶部菜单的显示和隐藏
				if (this.mescroll.getScrollTop() >= this.navTop) {
					this.isShowSticky = true // 显示悬浮菜单
				} else {
					this.isShowSticky = false // 隐藏悬浮菜单
				}
			},
			// 点击回到顶部按钮时,先隐藏悬浮菜单,避免闪动
			topClick() {
				this.isShowSticky = false
			},
			// 切换菜单
			tabChange(index) {
				// 记录前一个菜单的数据
				let tabs = this.tabs;
				let preTab = tabs[this.preIndex]
				preTab.y = this.mescroll.getScrollTop(); // 滚动条位置
				this.preIndex = index;
				// 当前菜单的数据
				let curTab = tabs[index]
				this.query.applyStatus = curTab.status;
				this.mescroll.resetUpScroll()
				// if (!curTab.list) {
				// 	// 没有初始化,则初始化
				// 	this.isChangeTab = true;
				// 	this.mescroll.resetUpScroll()
				// } else {
				// 	// 初始化过,则恢复之前的列表数据
				// 	this.mescroll.setPageNum(curTab.num + 1); // 恢复当前页码
				// 	this.mescroll.endSuccess(curTab.curPageLen, curTab.hasNext); // 恢复是否有下一页或显示空布局
				// 	this.$nextTick(() => {
				// 		this.mescroll.scrollTo(curTab.y, 0) // 恢复滚动条的位置
				// 	})
				// }
			},

			// 使用mescroll-body最简单只需在onPageScroll处理即可
			onPageScroll(e) {
				console.log("onPageScroll===", e)
				if (e.scrollTop >= this.navTop) {
					this.isShowSticky = true // 显示悬浮菜单
				} else {
					this.isShowSticky = false // 隐藏悬浮菜单
				}
			}
		},
	}
</script>

<style lang="scss" scoped>
	page {
		min-height: 100vh;
		background-color: #f5f6fa;
	}

	.content {
		// background-image: linear-gradient(180deg, #007aff 10%,  #f5f6fa 20%);
		// background-image: linear-gradient(180deg, #007aff 0%, #f5f6fa 30%);
	}

	.top {
		position: fixed;
		top: 0;
		z-index: 999;
		width: 100%;
		background-image: linear-gradient(180deg, #007aff 10%, #CDE2FB 90%);
	}
	
	.navbar{
		position:relative;
		.search{
			position: absolute;
			bottom: 10rpx;
			left: 20rpx;
		}
	}

	/deep/ .u-form-item__body__left__content__label {
		height: 40px;
		line-height: 40px;
		font-size: 18px;
		color: #333;
		font-weight: bold;
		min-width: 90px;
	}

	.y-list {
		padding: 0 20upx 20upx 20upx;
		background-image: linear-gradient(180deg, #CDE2FB 0%, #f5f6fa 5%);
	}

	/* 左右布局样式 */
	.tab-filter-container {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 0 20rpx;
		// background-color: #fff;
	}

	.left-content {
		flex-shrink: 0;
		min-width: 120rpx;
		margin-right: 20rpx;
	}

	.right-content {
		flex: 1;
	}
</style>