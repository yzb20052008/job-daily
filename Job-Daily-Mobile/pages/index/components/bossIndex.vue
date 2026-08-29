<template>
	<view class="content">
		<public-module></public-module>
		<view class="top-bar" :style="{top:titleTop+'px'}" :class="{ 'scrolled': isScrolled }">
			<text class="title" :class="{ 'scrolled-title': isScrolled }" v-if="!isScrolled">小蓝直聘</text>
			<view class="search-box" :class="{ 'scrolled-search': isScrolled }" @click="toSearch">
				<text class="search-icon yzb yzb-search"></text>
				<text class="search-placeholder" :class="{ 'scrolled-placeholder': isScrolled }">{{placeholder}}</text>
			</view>
		</view>
		<!-- 该内容在小程序编译代码上添加。此处添加无效 -->
		<mescroll-uni ref="mescrollRef" @init="mescrollInit" :up="upOption" @down="downCallback" @up="upCallback"
			:top="navHeight+'px'" @scroll="onScroll">
			<!-- 	<view><u-swiper :list="banners" keyName="pic" :showTitle="false" imgMode="" radius="0" :autoplay="true"
						indicator indicatorMode="dot" circular height="190" @click="swiperClick"></u-swiper></view> -->
			<!-- <view class="y-type">
					<u-scroll-list @right="right" @left="left">
						<view class="types">
							<view class="type-item" v-for="(item, index) in grid" :key="index"
								@click="handleClickGrid(item)">
								<image class="type-icon" :src="item.icon"></image>
								<text class="type-name">{{item.name}}</text>
							</view>
						</view>
					</u-scroll-list>
				</view> -->
			<view class="y-swiper" v-if="banners.length>0"><u-swiper :list="banners" keyName="pic" :showTitle="false"
					imgMode="aspectFill" radius="10" :autoplay="true" :indicator="banners.length>1?true:false"
					indicatorMode="dot" circular height="120" @click="swiperClick"></u-swiper></view>
			<view class="y-type" v-if="grid.length>0"><yzb-grid :columnNum="4" :data="grid" show-border="false"
					@click="handleClickGrid"></yzb-grid></view>
			<view class="y-notice">
				<yzb-notice theme="primary" :list="noticeList" theKey="title" :showIcon="true" :showMore="true"
					@goItem="goItem" @goMore="goMore"></yzb-notice>
			</view>
			<u-sticky offset-top="0">
				<view class="y-tabs">
					<view class="y-expected">
						<u-tabs :list="topTabs" :current="currentTab" lineWidth="30" lineColor="#007aff" :activeStyle="{
						color: '#000',
						fontWeight: 'bold',
						transform: 'scale(1.30)'
					}" :inactiveStyle="{
						color: '#606266',
						fontWeight: 'bold',
						transform: 'scale(1)'
					}" itemStyle="padding-left: 15px; padding-right: 15px; height: 50px;" @click="tabChange" ref="tabsRef">
							<view slot="right" class="post-add" @tap.stop="toTypes">
								<u-icon name="plus-circle-fill" color="#007aff" size="18" bold></u-icon>
								<text class="add-name">添加岗位</text>
							</view>
						</u-tabs>
					</view>
					<view class="y-sub">
						<view class="sub">
							<u-subsection :list="subsections" fontSize="14" activeColor="#007aff" bgColor="#f5f5f5"
								inactiveColor="#303133" :current="subIndex" @change="subChange"></u-subsection>
						</view>
					</view>
				</view>
			</u-sticky>
			<view class="y-base">
				<y-resume-list :list="list" @success="success"></y-resume-list>
			</view>
		</mescroll-uni>
	</view>
</template>

<script>
	import {
		mapState,
		mapMutations
	} from 'vuex';
	import MescrollMixin from '@/uni_modules/mescroll-uni/components/mescroll-uni/mescroll-mixins.js';
	import {
		loGetLocation,
		loGetGaodeLocation
	} from '@/config/common';
	import {
		judgeLogin
	} from '@/config/login';

	export default {
		mixins: [MescrollMixin], // 使用mixin
		computed: {
			...mapState(['userInfo', "runMode", 'locateInformation', 'memberRole', 'lastLocation'])
		},
		data() {
			return {
				systemInfo: this.$base.systemInfo,
				isScrolled: false, // 是否滚动（用于切换样式）
				lastMemberRole: 'member',
				cityInfo: {
					area: '选城市'
				},
				lastCity: '选城市',
				topTabs: [],
				tabIds: '',
				currentTab: 0,
				defaultTab: {
					name: '推荐'
				},
				types: [],
				subsections: [
					"全部", "附近"
					// "全部", "最新", "附近"
				],
				subIndex: 0,
				scrollTop: 0,
				rightWidth: 100,
				titleTop: 0,
				navHeight: 80,
				banners: [],
				groupList: [],
				grid: [],
				noticeList: [],
				list: [],
				query: {
					pageNo: 1,
					pageSize: 10,
					cityCode: '',
					latitude: 0,
					longitude: 0,
					keyword: null,
					orderBy: 'new',
					typeIds: ''
				},
				upOption: {
					onScroll: true,
					auto: false, // 不自动加载
					noMoreSize: 10, //如果列表已无数据,可设置列表的总数量要大于半页才显示无更多数据;避免列表数据过少(比如只有一条数据),显示无更多数据会不好看; 默认5
					textNoMore: '-- 没有更多 --',
					empty: {
						tip: '暂无数据', // 提示
						icon: 'https://img.qinkonglan.cn/imgs/data.jpg'
					}
				},
				placeholder: '搜索你想找的候选人',
				ifToTypes: false,
			};
		},

		onReady() {
			// 此处为屏幕宽度
			const windowWidth = uni.$u.sys().windowWidth
			// #ifdef MP-WEIXIN
			let menuButtonInfo = uni.getMenuButtonBoundingClientRect();
			this.titleTop = menuButtonInfo.top;
			this.rightWidth = windowWidth - menuButtonInfo.left - uni.upx2px(24);
			this.navHeight = this.systemInfo.navBarH + 40; //菜单栏总高度--单位px
			console.log("windowWidth===", windowWidth);
			console.log("menuButtonInfo===", menuButtonInfo);
			// #endif
			// #ifdef APP-PLUS
			this.navHeight = uni.$u.sys().statusBarHeight + 44; //菜单栏总高度--单位px
			console.log("windowWidth===", windowWidth);
			// #endif
		},

		methods: {
			...mapMutations(['setRunMode', 'setUserInfo', 'setLocateInformation', 'setLastLocation']),

			initData() {
				console.log("this.lastLocation==", this.lastLocation);
				if (this.lastLocation && this.lastLocation.area) {
					this.cityInfo = Object.assign({}, this.lastLocation);
					this.lastCity = this.cityInfo.area;
					this.getResumeList(1, 10);
				}
				this.getLocation();
				this.topTabs = [];
				this.topTabs.push(this.defaultTab);
				this.getAdList();
				this.getNoticeList();
				this.getTypeList();
			},

			initShowData(options) {
				if (options && options.cityInfo && options.cityInfo.areaCode) {
					this.cityInfo = options.cityInfo;
				}
				this.types = options.types;
				if (this.ifToTypes) {
					//处理顶部tab列表
					this.topTabs = [];
					this.topTabs.push(this.defaultTab);
					if (this.types.length > 0) {
						this.topTabs = this.topTabs.concat(this.types);
						this.tabIds = this.types.map(item => item.id).join(', ');
					} else {
						this.tabIds = '';
					}
					uni.setStorageSync('topTabs', this.topTabs);
					this.query.typeIds = '';
					this.resetTab();
					this.ifToTypes = false;
				}
				//处理城市选择
				if (this.cityInfo.areaCode && this.cityInfo.area != this.lastCity) {
					console.log("===this.cityInfo===", this.cityInfo);
					if (this.locateInformation.location) {
						this.locateInformation.cityInfo = this.cityInfo;
						this.setLocateInformation(this.locateInformation);
						this.setLastLocation(this.cityInfo);
						this.cityOn = true;
					}
					this.getResumeList(1, 10);
				}
				if (this.userInfo.token) {
					//解决角色切换后数据更新问题
					if (this.lastMemberRole != this.userInfo.memberRole) {
						this.getTypeList();
						this.getResumeList(1, 10);
					}
					this.lastMemberRole = this.userInfo.memberRole;
				}
			},

			//查询求职意向
			async getIntention() {
				//判断
				let tabs = uni.getStorageSync("topTabs");
				if (tabs) {
					this.topTabs = tabs;
					this.tabIds = this.topTabs.map(item => item.id).join(', ');
					return;
				}
				let res = await this.$apis.getIntention();
				if (res) {
					let names = res.typeNames.split("、");
					let ids = res.typeIds.split(",");
					for (let i = 0; i < names.length; i++) {
						let tab = {
							id: ids[i],
							name: names[i]
						}
						this.topTabs.push(tab);
						this.tabIds = this.topTabs.map(item => item.id).join(', ');
						uni.setStorageSync('topTabs', this.topTabs);
					}
				}
			},


			resetTab() {
				const tabs = this.$refs.tabsRef;
				if (tabs) {
					// 调用组件内部切换方法（需查看组件源码确认方法名）
					tabs.clickHandler(this.topTabs[0], 0); // 假设内部方法为 handleClick
					// 同步更新 current
					this.currentTab = 0;
				}
			},

			tabChange(e) {
				console.log("===tabChange==", e);
				if (e.id) {
					this.query.typeIds = e.id;
				} else {
					this.query.typeIds = '';
				}
				this.mescroll.resetUpScroll();
			},

			left() {
				console.log('left');
			},

			right() {
				console.log('right');
			},

			subChange(index) {
				console.log('subChange');
				this.subIndex = index;
				if (index == 0) {
					//全部
					// this.query.orderBy = "all";
					this.query.orderBy = "new";
				}
				// else if (index == 1) {
				// 	//最新
				// 	this.query.orderBy = "new";
				// } 
				else if (index == 1) {
					//附近
					this.query.orderBy = "near";
				}
				this.mescroll.resetUpScroll();
			},

			openLocationSet() {
				let that = this;
				uni.openSetting({
					success: function(data) {
						if (data.authSetting[
								"scope.userLocation"
							] === true) {
							that.$u.toast('授权成功~');
							//授权成功之后，再调用chooseLocation选择地方
						} else {
							that.$u.toast('授权失败~');
						}
					}
				})
			},

			toTypes() {
				this.ifToTypes = true;
				uni.$u.route('/pages/job/types?ids=' + this.tabIds);
			},

			toSearch() {
				uni.$u.route('/pages/common/search');
			},

			selectCity() {
				uni.$u.route('/pages/common/selectCity');
			},

			onScroll(e) {
				this.scrollTop = e.scrollTop;
				this.isScrolled = e.scrollTop > 50;
				if (this.isScrolled) {
					this.navHeight = this.systemInfo.navBarH;
				} else {
					this.navHeight = this.systemInfo.navBarH + 40;
				}
			},

			getQueryString(url, name) {
				var reg = new RegExp('(^|&|/?)' + name + '=([^&|/?]*)(&|/?|$)', 'i')
				var r = url.substr(1).match(reg)
				if (r != null) {
					return r[2]
				}
				return null;
			},

			getLocation() {
				console.log('============getLocation==============');
				loGetLocation(
					res => {
						console.log(res, 'loGetLocation');
						const adInfo = res && res.ad_info;
						const location = (res && res.location) || {};
						const lat = location.lat != null ? location.lat : res.latitude;
						const lng = location.lng != null ? location.lng : res.longitude;
						if (adInfo && adInfo.adcode && adInfo.adcode !== '000000') {
							this.cityInfo.city = adInfo.city || '';
							this.cityInfo.area = adInfo.district || adInfo.city || '附近';
							this.cityInfo.areaCode = adInfo.adcode;
							this.cityInfo.cityCode = String(adInfo.adcode).substr(0, 4);
						} else if (this.lastLocation && this.lastLocation.areaCode) {
							this.cityInfo = Object.assign({}, this.lastLocation);
						} else {
							this.cityInfo.city = (adInfo && adInfo.city) || '附近';
							this.cityInfo.area = (adInfo && adInfo.district) || '附近';
							this.cityInfo.areaCode = '';
							this.cityInfo.cityCode = '';
						}
						this.cityInfo.latitude = lat;
						this.cityInfo.longitude = lng;
						this.cityInfo.address = (res && res.address) || this.cityInfo.address || '';
						this.lastCity = this.cityInfo.area;
						const locate = Object.assign({}, res || {}, {
							location: { lat: lat, lng: lng },
							cityInfo: this.cityInfo
						});
						this.setLocateInformation(locate);
						this.setLastLocation(this.cityInfo);
						this.getResumeList(1, 10);
						this.updateLocation();
					},
					err => {
						console.log('err==', err);
						if (this.lastLocation && this.lastLocation.area) {
							this.cityInfo = Object.assign({}, this.lastLocation);
						} else {
							this.cityInfo = { city: '附近', area: '附近', areaCode: '', cityCode: '' };
						}
						this.lastCity = this.cityInfo.area;
						this.getResumeList(1, 10);
					},
					{ purpose: 'city', isOpenSetting: true }
				);
			},

			updateLocation() {
				console.log("this.userInfo.token", this.userInfo.token)
				if (!this.userInfo.token) {
					console.log("未登录  无需更新位置")
					return;
				}
				const locate = this.locateInformation || {};
				if (!locate.location || !locate.cityInfo || !locate.cityInfo.areaCode) {
					console.log("定位城市信息不完整，跳过同步");
					return;
				}
				console.log('===updateUserLocation===', locate);
				let param = {
					latitude: locate.location.lat,
					longitude: locate.location.lng,
					city: locate.cityInfo.area,
					cityCode: locate.cityInfo.areaCode,
					pcity: locate.cityInfo.city,
					pcityCode: locate.cityInfo.cityCode,
					address: locate.address,
				};
				this.$apis
					.updateUserLocation(param)
					.then(res => {
						this.userInfo.lat = param.latitude;
						this.userInfo.lng = param.longitude;
						this.setUserInfo(this.userInfo);
					})
					.catch(err => {
						console.log(err, 'catch');
					});
			},

			getConfig() {
				this.$apis.getConfig().then(res => {
					console.log('getConfig', res);
					this.setRunMode(res.runMode);
					console.log("runMode222===", this.runMode);
				});
			},

			getTypeList() {
				let params = {
					roleCode: this.memberRole
				}
				this.$apis.getTypeList({
					params: params
				}).then(res => {
					console.log('getTypeList', res);
					this.grid = res;
				});
			},

			getAdList() {
				let param = {
					roleCode: 'company',
					adPosition: 'index_banner'
				}
				this.$apis.getAdList({
					params: param
				}).then(res => {
					console.log('getAdList', res);
					this.banners = res;
				});
			},

			getNoticeList() {
				let httpData = {
					pageNo: 1,
					pageSize: 5,
					plat: 1,
				};
				// #ifdef MP-TOUTIAO
				httpData.plat = 2;
				// #endif
				this.$apis.getNoticeList({
					params: httpData,
					custom: {
						isFactory: true
					}
				}).then(res => {
					console.log('getNoticeList', res);
					this.noticeList = res.records;
				});
			},

			getPostList(pageNo, pageSize) {
				this.query.pageNo = pageNo;
				this.query.pageSize = pageSize;
				// if (!this.locateInformation.location) {
				// 	console.log("暂无定位信息")
				// 	return;
				// } else {
				// 	this.query.latitude = this.locateInformation.location.lat;
				// 	this.query.longitude = this.locateInformation.location.lng;
				// }
				// if (this.cityInfo.areaCode.length == 4) { //整个地区
				// 	this.query.pCityCode = this.cityInfo.cityCode;
				// 	this.query.cityCode = "";
				// } else {
				// 	this.query.cityCode = this.cityInfo.areaCode;
				// }
				//默认显示当前所在地区，体验版关闭，显示全部数据20250607
				// this.query.pCityCode = this.cityInfo.cityCode;
				// this.query.cityCode = "";

				if (this.locateInformation.location) {
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
					// this.mescroll.endSuccess(0);
				});
			},

			getResumeList(pageNo, pageSize) {
				this.query.pageNo = pageNo;
				this.query.pageSize = pageSize;
				if (!this.locateInformation.location) {
					console.log("暂无定位信息")
					return;
				} else {
					this.query.latitude = this.locateInformation.location.lat;
					this.query.longitude = this.locateInformation.location.lng;
					// if (this.cityInfo.areaCode.length == 4) { //整个地区
					// 	this.query.pCityCode = this.cityInfo.cityCode;
					// 	this.query.cityCode = "";
					// } else {
					// 	this.query.cityCode = this.cityInfo.areaCode;
					// }
					//默认显示当前所在地区
					// this.query.pCityCode = this.cityInfo.cityCode;
					// this.query.cityCode = "";
				}
				this.query.userId = null;
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

			onJump(url) {
				uni.navigateTo({
					url: url
				});
			},

			swiperClick(index) {
				console.log("swiperClick", index);
				let item = this.banners[index];
				if (item.openUrl) {
					let url = item.openUrl;
					if (url.substr(0, 7).toLowerCase() == 'http://' || url.substr(0, 8).toLowerCase() == 'https://') {
						let newurl = url.replace('?', '-');
						newurl = newurl.replace('=', '@');
						uni.navigateTo({
							url: '/pages/common/webview?url=' + newurl
						});
					} else {
						//内部链接
						uni.navigateTo({
							url: url
						});
					}
					return
				}
			},

			handleClickGrid(item) {
				console.log('handleClickGrid', item);
				// uni.navigateTo({
				// 	url: item.openUrl
				// });
				if (item.openUrl) {
					let url = item.openUrl;
					if (url.substr(0, 7).toLowerCase() == 'http://' || url.substr(0, 8).toLowerCase() == 'https://') {
						let newurl = url.replace('?', '-');
						newurl = newurl.replace('=', '@');
						uni.navigateTo({
							url: '/pages/common/webview?url=' + newurl
						});
					} else {
						//内部链接
						uni.navigateTo({
							url: url
						});
					}
					return
				}
			},

			goItem(item) {
				console.log('goItem', item);
				uni.$u.route('/pages/message/detail?id=' + item.id);
			},

			goMore() {
				this.onJump('/pages/message/notice');
			},

			success() {
				this.mescroll.resetUpScroll();
			},

			/*下拉刷新的回调 */
			downCallback() {
				// 这里加载你想下拉刷新的数据, 比如刷新轮播数据
				this.initData();
				this.mescroll.resetUpScroll();
			},

			/*上拉加载的回调: 其中page.num:当前页 从1开始, page.size:每页数据条数,默认10 */
			upCallback(page) {
				console.log('===upCallback===', page);
				this.getResumeList(page.num, page.size);
			},

			//点击空布局按钮的回调
			emptyClick() {
				uni.showToast({
					title: '点击了按钮,具体逻辑自行实现'
				});
			},

			//初始化goeasy
			initChat() {
				if (this.userInfo.token) {
					console.log("goeasy status == ", GoEasy.getConnectionStatus())
					if (GoEasy.getConnectionStatus() === 'disconnected') {
						GoEasy.connect({
							id: this.userInfo.id,
							data: {
								name: this.userInfo.nickname,
								avatar: this.userInfo.avatar
							},
							onSuccess: () => {
								console.log('GoEasy connect successfully.')
								setTimeout(() => {
									this.loadConversations();
								}, 1000)
							},
							onFailed: (error) => {
								console.log('Failed to connect GoEasy, code:' + error.code + ',error:' + error
									.content);
							},
							onProgress: (attempts) => {
								console.log('GoEasy is connecting', attempts);
							}
						});
					} else {
						this.loadConversations();
					}
				}
			},

			// 加载最新的会话列表
			loadConversations() {
				GoEasy.im.latestConversations({
					onSuccess: (result) => {
						let content = result.content;
						this.setUnreadAmount(content);
					},
					onFailed: (error) => {
						uni.hideLoading();
						console.log('获取最新会话列表失败, error:', error);
					}
				});
			},

			setUnreadAmount(content) {
				console.log("unreadAmount===", content);
				this.msgTotal = content.unreadTotal;
				this.updateBradge();
			},

			async getUnReadCount() {
				let params = {
					userId: null,
				}
				if (this.userInfo.token) {
					params.userId = this.userInfo.id;
				} else {
					return;
				}
				let res = await this.$apis.getUnReadTotalCount({
					params: params
				});
				if (res) {
					this.noticeCount = res.totalCount;
					this.updateBradge();
				}
			},

			updateBradge() {
				console.log("this.msgTotal===", this.msgTotal);
				console.log("this.noticeCount===", this.noticeCount);
				this.unreadTotal = this.msgTotal + this.noticeCount;
				if (this.unreadTotal > 0) {
					uni.setTabBarBadge({
						index: 1,
						text: this.unreadTotal.toString()
					});
				} else {
					uni.removeTabBarBadge({
						index: 1
					});
				}
			},
		}
	};
</script>

<style lang="scss">
	page {
		background-color: #f5f6fa;
	}

	.content {
		height: 100vh;
		display: flex;
		flex-direction: column;
		// background-image: linear-gradient(#007aff, #fff);
		background-image: linear-gradient(180deg, #007aff 0%, #f5f6fa 40%);
	}

	/* 顶部栏容器样式 */
	.top-bar {
		padding: 0rpx 20rpx 20rpx 20rpx;
		// background: linear-gradient(to bottom, #007aff, #007aff); /* 初始渐变背景 */
		transition: all 0.5s ease;
		/* 所有样式变化添加过渡动画 */
		position: absolute;
		width: 100%;
	}

	/* 标题样式 */
	.title {
		font-size: 40rpx;
		font-weight: bold;
		margin-left: 10rpx;
		color: #fff;
		display: block;
		transition: all 0.5s ease;
		margin-bottom: 20rpx;
	}

	/* 搜索框初始样式（宽且扁） */
	.search-box {
		width: 100%;
		/* 初始宽度：占满大部分横向空间 */
		height: 66rpx;
		background: rgba(255, 255, 255, 0.8);
		/* 半透明白色背景 */
		border-radius: 30rpx;
		/* 圆角较圆润 */
		padding: 0 30rpx;
		display: flex;
		align-items: center;
		transition: all 0.5s ease;
		/* 宽度、背景等变化添加过渡 */
	}

	.search-icon {
		color: #888;
		font-size: 28rpx;
		transition: all 0.5s ease;
		margin-top: 6rpx;
	}

	/* 搜索框占位文字样式 */
	.search-placeholder {
		color: #888;
		font-size: 28rpx;
		transition: all 0.5s ease;
		margin-left: 10rpx;
	}

	/* 滚动后的顶部栏样式 */
	.scrolled {
		// background: #fff; /* 背景变为白色 */
		// box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1); /* 增加阴影 */
	}

	/* 滚动后的标题样式 */
	.scrolled-title {
		font-size: 32rpx;
		color: #333;
		/* 文字变深 */
		margin-bottom: 10rpx;
	}

	/* 滚动后的搜索框样式（缩短+变样式） */
	.scrolled-search {
		width: 70%;
		/* 宽度缩短到 70% */
		// background: #f5f5f5; /* 背景变为浅灰色 */
		border-radius: 20rpx;
		/* 圆角略微收紧，增强紧凑感 */
		background: rgba(255, 255, 255, 0.8);
		/* 半透明白色背景 */
		border-radius: 30rpx;
		/* 圆角较圆润 */
	}

	/* 滚动后的占位文字样式 */
	.scrolled-placeholder {
		color: #999;
		/* 文字变灰 */
	}

	.navbar-slot {
		display: flex;
		flex-direction: row;
		align-items: center;
		justify-content: space-around;
		width: 100%;

		.location {
			flex: 1;
			display: flex;
			flex-direction: row;
			align-items: center;

			.location-name {
				color: #333;
				overflow: hidden;
				white-space: nowrap;
				text-overflow: ellipsis;
				font-weight: bold;
			}

			.location-icon {
				color: #333;
				font-size: 26upx;
				margin-left: 8upx;
			}
		}

		.search {
			flex: 3;
			margin: 0 20upx;
		}

		.nav-right {}
	}

	.navbar {
		display: flex;
		flex-direction: row;
		align-items: center;
		justify-content: space-around;
		width: 100%;
		padding: 20upx;

		.location {
			display: flex;
			flex-direction: row;
			align-items: center;
			flex: 1;
			max-width: 30%;

			.location-name {
				color: #333;
				overflow: hidden;
				white-space: nowrap;
				text-overflow: ellipsis;
			}

			.location-icon {
				color: #333;
				font-size: 26upx;
				margin-left: 8upx;
			}
		}

		.search {
			margin: 0 20upx;
			flex: 4;
		}
	}

	.y-notice {
		margin: 20rpx 20rpx 0 20rpx;
		border-radius: 20rpx;
		box-shadow: 0 0rpx 5rpx #eee;
		background-color: #ECF5FF;
		// background: rgba(255, 255, 255, 0.7);
	}

	.y-swiper {
		// padding: 20upx 0;
		// margin-top: 10upx;
		padding: 0upx 20upx 0 20upx;
		// border-radius: 20upx;
		// background-color: #fff;
	}

	.y-type {
		padding: 10rpx 0 20rpx 0;
		// background-color: #fff;
		background: rgba(255, 255, 255, 0.5);
		margin: 20rpx 20rpx 0 20rpx;
		border-radius: 20rpx;
		box-shadow: 0 0rpx 5rpx #eee;

		.types {
			display: flex;
			flex-direction: row;
		}

		.type-item {
			display: flex;
			flex-direction: column;
			align-items: center;
			width: 175upx;

			.type-icon {
				width: 90upx;
				height: 90upx;
			}

			.type-name {
				font-size: $uni-font-size-base;
				margin-top: 5upx;
				color: #333;
			}
		}
	}

	.y-tabs {
		background-color: #fff;
		margin: 20rpx 0rpx 0 0rpx;
		border-radius: 20rpx 20rpx 0 0;
		box-shadow: 0 0rpx 5rpx #eee;
	}

	.y-expected {
		padding: 0 20rpx 20rpx 20rpx;

		.post-add {
			display: flex;
			flex-direction: row;
			align-items: center;
			border: 1upx solid #007aff;
			padding: 7upx 15upx;
			border-radius: 16px;
			margin-left: 20upx;
		}

		.add-name {
			font-size: $uni-font-size-sm;
			color: $main-color;
			margin-left: 6upx;
		}
	}

	.y-sub {
		padding: 10upx 20upx 20upx 20upx;
		display: flex;
		flex-direction: row;
		align-items: center;
		justify-content: space-between;

		.sub {
			width: 300upx;
		}

		.city-select {
			align-items: baseline;
			// background-color: #f5f5f5;
			padding: 0rpx 20rpx;
			border-radius: 10rpx;
			height: 32px;
			line-height: 32px;

			.city-name {
				font-size: 28rpx;
			}

			.city-name-off {
				color: #303133;
			}

			.city-name-on {
				color: #007aff;
			}

			.city-icon {
				margin-left: 5rpx;
				margin-bottom: -5rpx;
				font-size: 20rpx;
			}

			.city-icon-off {
				color: #777;
			}

			.city-icon-on {
				color: #007aff;
			}
		}
	}

	.y-base {
		// border-top: 20upx #f5f6fa solid;
		// padding: 0 20upx;
		margin-top: 20upx;
		padding-bottom: 50upx;
	}

	.pop-content {
		background-color: #000;
		display: flex;
		flex-direction: row;
		align-items: center;
		color: #fff;
		padding: 20upx;
		border-radius: 10upx;

		.pop-btn {
			background-color: #007aff;
			padding: 5upx 15upx;
			border-radius: 5upx;
			font-size: 13px;
			margin-left: 30upx;
		}

		.pop-close {
			margin-left: 50upx;
			font-size: 20px;
		}
	}
</style>