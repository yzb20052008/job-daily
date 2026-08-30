<template>
	<view class="content">
		<view class="search">
			<u-search :placeholder="placeholder" v-model="searchValue" bgColor="#fff" :clearabled="true"
				:showAction="true" :animation="true" @search="search" @custom="search" @clear="clear"
				:actionStyle="{color:'#fff'}"></u-search>
		</view>
		<view class="searchRecent" v-if="searchRecent == true">
			<!-- 占位 -->
			<view class="placeholder-90"></view>
			<view class="searchRecent-title space-between-algin" v-if="searchRecentList.length > 0">
				<text style="color: #333;font-weight: bold;">最近搜索</text>
				<text class="yzb yzb-shanchu" style="color: #999;" @click="clearRecent">清空</text>
			</view>
			<view class="history">
				<view class="searchRecent-content" v-for="(item, index1) in searchRecentList" :key="index1"
					@click="recentClick(item)">
					<text class="text-size-mim">{{ item }}</text>
				</view>
				<view class="history-none" style="margin-top: 20upx;" v-if="searchRecentList.length == 0">
					<u-empty mode="data" icon="https://cdn.example.com/imgs/data.jpg"></u-empty>
				</view>
			</view>
		</view>
		<view v-if="searchRecent == false">
			<mescroll-uni ref="mescrollRef" @init="mescrollInit" :up="upOption" @down="downCallback" @up="upCallback"
				@scroll="onScroll" :top="upx2px(90)+'px'">
				<view class="items" v-if="userInfo.memberRole=='company'">
					<y-resume-list :list="list" @success="success"></y-resume-list>
				</view>
				<view class="items" v-else>
					<y-post-list :list="list" @success="success"></y-post-list>
				</view>
			</mescroll-uni>
		</view>
	</view>
</template>

<script>
	import {
		mapState,
		mapGetters
	} from 'vuex';
	import MescrollMixin from '@/uni_modules/mescroll-uni/components/mescroll-uni/mescroll-mixins.js';
	export default {
		mixins: [MescrollMixin], // 使用mixin
		computed: {
			...mapState(['userInfo', 'locateInformation']),
			...mapGetters(['hasLogin'])
		},
		data() {
			return {
				searchValue: '',
				placeholder: '请输入关键词搜索相关内容',
				searchRecent: true, // 最近搜索
				searchRecentList: [], // 最近搜索
				list: [], //搜索结果列表
				query: {
					pageNo: 1,
					pageSize: 10,
					latitude: 0,
					longitude: 0,
					keyword: null,
				},
				upOption: {
					onScroll: true,
					auto: false, // 不自动加载
					noMoreSize: 10, //如果列表已无数据,可设置列表的总数量要大于半页才显示无更多数据;避免列表数据过少(比如只有一条数据),显示无更多数据会不好看; 默认5
					textNoMore: '-- 没有更多 --',
					empty: {
						tip: '没有搜索结果', // 提示
						icon: 'http://cdn.uviewui.com/uview/empty/search.png'
					}
				},
			};
		},
		onLoad() {
			this.searchRecentList = uni.getStorageSync('historySearch');
			if (this.searchValue == '' && this.searchRecentList != null && this.searchRecentList != '') {
				this.searchRecent = true;
			}
		},
		onReachBottom() {
			this.page++;
			this.getList();
		},
		methods: {
			
			upx2px(val){
				return uni.upx2px(val);
			},
			
			getList(pageNo, pageSize) {
				if (this.userInfo.memberRole == 'company') {
					this.getResumeList(pageNo, pageSize);
				} else {
					this.getPostList(pageNo, pageSize);
				}
			},

			getPostList(pageNo, pageSize) {
				this.query.pageNo = pageNo;
				this.query.pageSize = pageSize;
				console.log("searchValue==",this.searchValue)
				if(this.searchValue){
					this.query.keyword=this.searchValue;
				}
				if (!this.locateInformation.location) {
					console.log("暂无定位信息")
					// return;
				} else {
					this.query.latitude = this.locateInformation.location.lat;
					this.query.longitude = this.locateInformation.location.lng;
				}
				if (this.userInfo.token) {
					this.query.userId = this.userInfo.id;
				}
				this.$apis.getPostList({
					params: this.query,
					custom: {
						isFactory: true
					}
				}).then(res => {
					console.log('getPostList', res);
					if (pageNo == 1) {
						this.list = [];
					}
					if (res) {
						this.list = this.list.concat(res.records); //追加新数据
						this.$forceUpdate();
					}
					this.mescroll.endSuccess(res.records.length);
				});
			},

			getResumeList(pageNo, pageSize) {
				this.query.pageNo = pageNo;
				this.query.pageSize = pageSize;
				if(this.searchValue){
					this.query.keyword=this.searchValue;
				}
				if (!this.locateInformation.location) {
					console.log("暂无定位信息")
					// return;
				} else {
					this.query.latitude = this.locateInformation.location.lat;
					this.query.longitude = this.locateInformation.location.lng;
				}
				if (this.userInfo.token) {
					this.query.userId = this.userInfo.id;
				}
				this.$apis.getResumeList({
					params: this.query,
					custom: {
						isFactory: true
					}
				}).then(res => {
					console.log('getResumeList', res);
					if (pageNo == 1) {
						this.list = [];
					}
					if (res) {
						this.list = this.list.concat(res.records); //追加新数据
						this.$forceUpdate();
					}
					this.mescroll.endSuccess(res.records.length);
				});
			},

			// 最近搜索点击
			recentClick(item) {
				this.searchValue = item;
				this.search();
			},

			// 搜索
			search() {
				console.log("==search==", this.searchValue);
				if (this.searchValue) {
					this.searchRecent = false;
					this.historySearch();
					this.getList(1,10);
				} else {
					// uni.showToast({
					// 	icon: 'none',
					// 	title: '请输入要搜索的关键字'
					// });
					this.cancelSearch();
				}
			},
			
			clear(){
				this.searchRecent = true;
				this.getList(1,10);
			},

			// 清空最近搜索
			clearRecent() {
				var that = this;
				uni.showModal({
					title: '提示',
					content: '确定清空搜索记录吗',
					success(res) {
						if (res.confirm) {
							uni.removeStorageSync('historySearch');
							// 隐藏最近搜索
							that.searchRecent = false;
							that.list = [];
						}
					}
				});
			},

			// 保存最近搜索
			historySearch() {
				var history = uni.getStorageSync('historySearch');
				console.log(history);
				if (history == null || history == '') {
					history = [];
				}
				//判定是否已经看过,先删除
				for (var i = 0; i < history.length; i++) {
					if (history[i] == this.searchValue) {
						console.log('删除该元素' + history[i]);
						history.splice(i, 1);
					}
				}
				//控制最多保存10个
				if (history.length == 10) {
					history.splice(9, 1);
				}
				history.unshift(this.searchValue);
				uni.setStorageSync('historySearch', history);
				this.searchRecentList=history;
			},

			popular(item) {
				console.log(item);
				this.searchValue = item.text;
			},

			// 取消搜索
			cancelSearch() {
				this.searchValue = '';
				this.searchRecent = true;
			},

			searchInput(e) {
				this.searchRecentList = uni.getStorageSync('historySearch');
				if (this.searchValue == '' && this.searchRecentList != null && this.searchRecentList != '') {
					this.searchRecent = true;
				}
			},
			
			onScroll(){
			},
			
			/*下拉刷新的回调 */
			downCallback() {
				// 这里加载你想下拉刷新的数据, 比如刷新轮播数据
				if(this.searchRecent==false){
					this.mescroll.resetUpScroll();
				}
			},
			
			/*上拉加载的回调: 其中page.num:当前页 从1开始, page.size:每页数据条数,默认10 */
			upCallback(page) {
				console.log('===upCallback===', page);
				if(this.searchRecent==false){
					this.getList(page.num, page.size);
				}
			},
		}
	};
</script>

<style lang="scss">
	page {
		background: #f5f6fa;
	}

	.search {
		height: 90upx;
		position: fixed;
		top: 0;
		z-index: 5;
		width: 100%;
		padding: 0 20upx;
		box-sizing: border-box;
		display: flex;
		flex-direction: row;
		background-color: $main-color;
		color: #ffffff;
		align-items: center;
		justify-content: center;
	}

	.searchRecent {
		padding: 20upx;
		background-color: #fff;
		height: 100vh;
	}

	.searchRecent-title {
		height: 60upx;
		display: flex;
		flex-direction: row;
		justify-content: space-between;

		text {
			font-size: 30upx;
			color: #666;
		}
	}

	.history {
		flex-wrap: wrap;
		display: flex;
		flex-direction: row;
		width: 100%;
	}
	
	.history-none{
		display: flex;
		flex-direction: row;
		align-items: center;
		justify-content: center;
		width: 100%;
	}

	.searchRecent-content {
		margin: 15upx 20upx 15upx 0;
		padding: 8upx 30upx;
		background-color: #eee;
		display: flex;
		border-radius: 7upx;

		text {
			font-size: 26upx;
			color: #666;
		}
	}

	.load-more-box {
		padding: 20upx 0;
	}

	.type {
		width: 100%;
		height: 80upx;
		position: fixed;
		top: 90upx;
		z-index: 5;
	}

	.type-view {
		width: 25%;
		height: 80upx;
	}

	.type-view-line {
		width: 45upx;
		height: 4upx;
		border-radius: 15upx;
	}

	.wonderful {
		width: 95%;
		flex-wrap: wrap;
	}

	.wonderful-content {
		width: 47%;
		height: 340upx;
		position: relative;
	}

	.wonderful-playImg {
		width: 80upx;
		height: 80upx;
		position: absolute;
		top: 60upx;
		left: 125upx;
	}

	.wonderful-content-img {
		width: 100%;
		height: 190upx;
		border-radius: 12upx;
	}

	.placeholder-90 {
		width: 100%;
		height: 90upx;
	}

	.placeholder-170 {
		width: 100%;
		height: 170upx;
	}

	.forum {
		/* margin-top: 100upx; */
	}

	.forum-line {
		border-top: 5upx #efefef solid;
	}

	.forum-top {
		width: 94.5%;
	}

	.forum-top-left image {
		width: 100upx;
		height: 100upx;
		border-radius: 50%;
	}

	.share {
		width: 50upx;
		height: 50upx;
	}

	.forum-top-content {
		width: 94.5%;
		overflow: hidden;
		text-overflow: ellipsis;
		display: -webkit-box;
		-webkit-line-clamp: 3;
		-webkit-box-orient: vertical;
	}

	.forum-img {
		width: 94.5%;
		flex-wrap: wrap;
		padding-bottom: 0;
		/* justify-content: space-around; */
	}

	.forum-img-image {
		width: 226upx;
		height: 226upx;
		margin-right: 15upx;
		margin-bottom: 15upx;
	}

	.forum-img-image:nth-of-type(3n) {
		margin-right: 0upx;
	}

	.forum-btn {
		height: 100upx;
	}

	.btn {
		width: 250upx;
	}

	.btn image {
		width: 40upx;
		height: 40upx;
		margin-right: 10upx;
	}
</style>