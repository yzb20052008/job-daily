<!-- 菜单悬浮的原理: 监听滚动条的位置大于某个值时,控制顶部菜单的显示和隐藏, 用法比sticky复杂, 但APP端可兼容低端机 -->
<template>
	<view>
		<public-module></public-module>
		<!-- 菜单 (悬浮,预先隐藏)-->
		<!-- <me-tabs v-if="isShowSticky" v-model="tabIndex" :fixed="true" :tabs="tabs" @change="tabChange"></me-tabs> -->
		<view class="top" id="top">
			<view class="navbar" :style="{height:systemInfo.navBarH+'px'}">
				<view class="search" :style="{right:searchRight+'px'}">
					<u-search placeholder="输入关键字搜索" v-model="keyword" :clearabled="true" :showAction="false" 
					 @clear="clearInput"  @change="inputChange"  @search="search"></u-search>
				</view>
			</view>
			<view id="tabInList" class="tabInList">
				<view class="tab-filter-container">
					<view class="left-content">
						<y-dual-filter :jobTypes="jobTypes" @confirm="handleFilterChange">
						</y-dual-filter>
					</view>
					<view class="right-content">
						<me-tabs v-model="tabIndex" :tabs="tabs" @change="tabChange"></me-tabs>
					</view>
				</view>
			</view>
		</view>
		<mescroll-body ref="mescrollRef" @init="mescrollInit" @down="downCallback" @up="upCallback" :up="upOption"
			@scroll="scroll" @topclick="topClick" :top="mTop+'px'">
			<!-- 数据列表 -->
			<view class="y-list">
				<yzb-process-resume :list="tabs[tabIndex].list" @addPrice="addPrice" @close="toClose" @start="toStart"
					@cancel="cancelPost"></yzb-process-resume>
			</view>
		</mescroll-body>
	</view>
</template>

<script>
	import MescrollMixin from "@/uni_modules/mescroll-uni/components/mescroll-uni/mescroll-mixins.js";
	import {
		mapState,
		mapMutations
	} from 'vuex';
	import yDualFilter from '@/components/y-dual-filter/y-dual-filter.vue';
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

		watch: {
			// 监听用户信息变化
			'userInfo.memberRole': {
				handler(newRole, oldRole) {
					console.log('用户角色变化:', oldRole, '->', newRole);
					if (newRole === 'company' && this.userInfo.token) {
						console.log('用户角色变为企业，开始获取筛选数据...');
						this.getJobTypes();
						this.getWorkTypes();
					}
				},
				immediate: false
			},
			// 监听用户token变化
			'userInfo.token': {
				handler(newToken, oldToken) {
					console.log('用户token变化:', !!oldToken, '->', !!newToken);
					if (newToken && this.userInfo.memberRole === 'company') {
						console.log('用户登录且为企业，开始获取筛选数据...');
						this.getJobTypes();
						this.getWorkTypes();
					}
				},
				immediate: false
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
				tabs: [{
						name: '新简历',
						list: null,
						num: 1,
						y: 0,
						curPageLen: 0,
						hasNext: true,
						status: 1
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
				newsDetailUrl: '',
				query: {
					keyword:null,
					pageNo: 1,
					pageSize: 10,
					applyStatus: 1,
				},

				// 筛选相关数据
				jobTypes: [], // 岗位类型数据
				workTypes: [], // 工作类型数据
				currentFilters: {
					jobTypes: [],
					workTypes: []
				}
			}
		},

		methods: {
			...mapMutations(['setLocateInformation']),

			initData() {
				let menuButtonInfo = uni.getMenuButtonBoundingClientRect();
				this.searchRight = this.systemInfo. windowWidth-menuButtonInfo.left+10;
				this.$nextTick(() => {
					let view = uni.createSelectorQuery().in(this).select('#top');
					view.boundingClientRect(data => {
						 
						 this.mTop=data.height;
					}).exec();
				});
				this.getJobTypes();
				this.getWorkTypes();
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

			// 处理筛选变化
			handleFilterChange(filters) {
				console.log('筛选条件变化:', filters);
				// 更新筛选条件
				this.currentFilters = {
					jobTypes: filters.jobTypes || [],
					workTypes: filters.workTypes || []
				};
				console.log('当前筛选条件:', this.currentFilters);
				// 重新加载数据
				this.mescroll.resetUpScroll();
			},

			// 获取岗位类型数据（本公司发布的职位）
			async getJobTypes() {
				// 检查用户是否已登录且为企业用户
				if (!this.userInfo.token) {
					console.log('用户未登录，跳过获取职位数据');
					return;
				}

				if (this.userInfo.memberRole !== 'company') {
					console.log('非企业用户，跳过获取职位数据');
					return;
				}

				try {
					console.log('开始获取公司职位数据...');
					// 使用公司职位接口，获取本公司发布的职位信息
					let params = {
						pageNo: 1,
						pageSize: 100 // 获取足够多的职位用于筛选
					};
					const res = await this.$apis.getMyPostList({
						params: params
					});
					console.log('公司职位数据:', res);
					if (res && res.records && res.records.length > 0) {
						// 将职位数据转换为筛选组件需要的格式
						this.jobTypes = this.convertPostsToJobTypes(res.records);
						console.log('设置职位筛选数据:', this.jobTypes);
					} else {
						console.log('未获取到公司职位数据');
						this.jobTypes = [];
					}
				} catch (error) {
					console.error('获取公司职位失败:', error);
					this.jobTypes = [];
				}
			},

			// 将公司职位数据转换为筛选组件需要的格式
			convertPostsToJobTypes(posts) {
				const jobTypes = [];
				const uniqueJobs = new Map(); // 用于去重

				posts.forEach(post => {
					// 使用职位标题作为筛选项
					if (post.title && !uniqueJobs.has(post.title)) {
						uniqueJobs.set(post.title, true);
						jobTypes.push({
							id: post.id,
							name: post.title,
							text: post.title,
							value: post.id,
							postId: post.id,
							typeIds: post.typeIds,
							typeCodes: post.typeCodes,
							typeNames: post.typeNames,
							settlementType: post.settlementType
						});
					}
				});

				return jobTypes;
			},

			// 将层级岗位数据扁平化处理
			flattenJobTypes(jobTypes) {
				const flattened = [];

				jobTypes.forEach(parentType => {
					// 添加父级岗位
					flattened.push({
						id: parentType.id,
						name: parentType.name,
						text: parentType.name,
						value: parentType.id,
						level: 1
					});

					// 添加子级岗位
					if (parentType.child && parentType.child.length > 0) {
						parentType.child.forEach(childType => {
							flattened.push({
								id: childType.id,
								name: childType.name,
								text: childType.name,
								value: childType.id,
								level: 2,
								parentId: parentType.id,
								parentName: parentType.name
							});

							// 添加三级岗位
							if (childType.child && childType.child.length > 0) {
								childType.child.forEach(grandChildType => {
									flattened.push({
										id: grandChildType.id,
										name: grandChildType.name,
										text: grandChildType.name,
										value: grandChildType.id,
										level: 3,
										parentId: childType.id,
										parentName: childType.name,
										grandParentId: parentType.id,
										grandParentName: parentType.name
									});
								});
							}
						});
					}
				});

				return flattened;
			},

			// 获取工作类型数据
			getWorkTypes() {
				// 直接使用默认的结算方式数据，对应JobPost.settlementType字段
				console.log('设置默认工作类型数据');
				this.workTypes = [{
						text: '日结',
						value: '1',
						id: '1',
						name: '日结'
					},
					{
						text: '周结',
						value: '2',
						id: '2',
						name: '周结'
					},
					{
						text: '月结',
						value: '3',
						id: '3',
						name: '月结'
					},
					{
						text: '完工结',
						value: '4',
						id: '4',
						name: '完工结'
					}
				];
				console.log('工作类型数据:', this.workTypes);
			},

			getList(pageIndex, pageSize) {
				if (!this.userInfo.token) {
					console.log("===getList===未登录")
					uni.hideLoading();
					this.mescroll.endSuccess(0);
					return;
				}
				this.getApplyComList(pageIndex, pageSize);
			},

			/**
			 * 任务列表
			 */
			async getApplyComList(pageIndex, pageSize) {
				this.query.pageNo = pageIndex;
				this.query.pageSize = pageSize;

				// 添加筛选条件
				console.log('应用筛选条件:', this.currentFilters);
				if (this.currentFilters.jobTypes && this.currentFilters.jobTypes.length > 0) {
					// 现在筛选的是具体的职位ID，而不是岗位类型
					this.query.postIds = this.currentFilters.jobTypes.join(',');
					console.log('设置职位筛选:', this.query.postIds);
				} else {
					delete this.query.postIds;
				}

				if (this.currentFilters.workTypes && this.currentFilters.workTypes.length > 0) {
					this.query.workTypes = this.currentFilters.workTypes.join(',');
					console.log('设置工作类型筛选:', this.query.workTypes);
				} else {
					delete this.query.workTypes;
				}

				console.log('最终查询参数:', this.query);
				let res = await this.$apis.getApplyComList({
					params: this.query,
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
		},
		// 使用mescroll-body最简单只需在onPageScroll处理即可
		onPageScroll(e) {
			if (e.scrollTop >= this.navTop) {
				this.isShowSticky = true // 显示悬浮菜单
			} else {
				this.isShowSticky = false // 隐藏悬浮菜单
			}
		}
	}
</script>

<style lang="scss" scoped>
	page {
		min-height: 100vh;
		background-color: #f5f6fa;
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

	.pop-content {
		display: flex;
		flex-direction: column;
		padding: 30upx;

		.pop-title {
			font-size: 18px;
			font-weight: bold;
			text-align: center;
			padding: 0 30upx 30upx 30upx;
			border-bottom: 1upx solid #eee;
		}

		.tips {
			font-size: 26upx;
			color: #999;
			margin-bottom: 20upx;

			.distance {
				color: red;
				padding: 0 10upx;
			}
		}

		.pop-btn {
			margin-top: 30px;
			display: flex;
			flex-direction: row;

			.btn {
				flex: 1;
			}
		}
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