<template>
	<view class="content">
		<public-module></public-module>
		<!-- #ifdef MP-WEIXIN -->
		<f-navbar bgColor="#fff" :navbarType="0" fontColor="#333" transparentTitleColor="#000" :isShowLeft="true"
			:scrollTop="scrollTop" title="零工招聘" :isShowTransparentTitle="false">
			<view class="navbar-slot" slot="left">
				<view class="location" @click="selectCity">
					<text class="location-name">{{cityInfo.area}}</text>
					<text class="yzb yzb-xiangxia1 location-icon"></text>
				</view>
				<view class="search" @click="toSearch">
					<u-search :placeholder="placeholder" borderColor="#eee" bgColor="#f5f6fa" disabled
						:showAction="false"></u-search>
				</view>
				<view class="nav-right" :style="{width:rightWidth+'px',height:'10px'}"></view>
			</view>
		</f-navbar>
		<!-- #endif -->
		<!-- #ifdef APP-PLUS -->
		<f-navbar bgColor="#fff" :navbarType="0" fontColor="#333" transparentTitleColor="#000" :isShowLeft="false"
			:scrollTop="scrollTop" title="零工招聘" :isShowTransparentTitle="false" titleWidth="750">
			<view class="navbar">
				<view class="location" @click="selectCity">
					<text class="location-name">{{cityInfo.area}}</text>
					<text class="yzb yzb-xiangxia1 location-icon"></text>
				</view>
				<view class="search" @click="toSearch">
					<u-search :placeholder="placeholder" borderColor="#eee" bgColor="#fff" disabled
						:showAction="false"></u-search>
				</view>
			</view>
		</f-navbar>
		<!-- #endif -->
		<float-popup-dialog :isShow="showMenuPop" :top="80" :left="30">
			<view class="pop-content" slot="content">
				<text class="pop-info">开启定位，查看附近岗位</text>
				<text class="pop-btn" @click="openLocationSet">开启</text>
				<text class="pop-close yzb yzb-cuo" @click="showMenuPop=false"></text>
			</view>
		</float-popup-dialog>
		<!-- 该内容在小程序编译代码上添加。此处添加无效 -->
		<mescroll-uni ref="mescrollRef" @init="mescrollInit" :up="upOption" @down="downCallback" @up="upCallback"
			:top="navHeight+'px'" @scroll="onScroll">
			<template v-if="memberRole=='company'">
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
				<view class="y-swiper"><u-swiper :list="banners" keyName="pic" :showTitle="false" imgMode="aspectFill"
						radius="10" :autoplay="true" :indicator="banners.length>1?true:false" indicatorMode="dot"
						circular height="160" @click="swiperClick"></u-swiper></view>
				<view class="y-type"><yzb-grid :columnNum="4" :data="grid" show-border="false"
						@click="handleClickGrid"></yzb-grid></view>
				<view class="y-notice">
					<yzb-notice theme="primary" :list="noticeList" theKey="title" :showIcon="true" :showMore="true"
						@goItem="goItem" @goMore="goMore"></yzb-notice>
				</view>
				<u-sticky offset-top="0">
					<view class="y-expected">
						<u-tabs :list="topTabs" :current="currentTab" lineWidth="30" lineColor="#007aff" :activeStyle="{
						color: '#000',
						fontWeight: 'bold',
						transform: 'scale(1.30)'
					}" :inactiveStyle="{
						color: '#606266',
						fontWeight: 'bold',
						transform: 'scale(1)'
					}" itemStyle="padding-left: 15px; padding-right: 15px; height: 50px;" @click="tabChange">
							<view slot="right" class="post-add" @tap.stop="toTypes">
								<u-icon name="plus-circle-fill" color="#007aff" size="18" bold></u-icon>
								<text class="add-name">添加工种</text>
							</view>
						</u-tabs>
					</view>
					<view class="y-sub">
						<view class="sub">
							<u-subsection :list="subsections" fontSize="14" activeColor="#007aff" bgColor="#f5f5f5"
								inactiveColor="#303133" :current="subIndex" @change="subChange"></u-subsection>
						</view>
					</view>
				</u-sticky>
				<view class="y-base">
					<y-resume-list :list="list" @success="success"></y-resume-list>
				</view>
			</template>
			<template v-else>
				<!-- <view class="y-swiper">
					<u-swiper :list="banners" keyName="pic" previousMargin="30" nextMargin="30" indicator indicatorMode="dot" circular
						:autoplay="true" height="180" radius="5" bgColor="#ffffff" imgMode=""></u-swiper>
				</view> -->
				<!-- <view><u-swiper :list="banners" keyName="pic" :showTitle="false" imgMode="" radius="0" :autoplay="true"
						indicator indicatorMode="dot" circular height="190" @click="swiperClick"></u-swiper></view> -->
				<view class="y-swiper"><u-swiper :list="banners" keyName="pic" :showTitle="false" imgMode="aspectFill"
						radius="10" :autoplay="true" :indicator="banners.length>1?true:false" indicatorMode="dot"
						circular height="160" @click="swiperClick"></u-swiper></view>
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
				<view class="y-type"><yzb-grid :columnNum="4" :data="grid" show-border="false"
						@click="handleClickGrid"></yzb-grid></view>
				<view class="y-notice">
					<yzb-notice theme="primary" :list="noticeList" theKey="title" :showIcon="true" :showMore="true"
						@goItem="goItem" @goMore="goMore"></yzb-notice>
				</view>
				<u-sticky offset-top="0">
					<view class="y-expected">
						<u-tabs :list="topTabs" :current="currentTab" lineWidth="30" lineColor="#007aff" :activeStyle="{
						color: '#000',
						fontWeight: 'bold',
						transform: 'scale(1.30)'
					}" :inactiveStyle="{
						color: '#606266',
						fontWeight: 'bold',
						transform: 'scale(1)'
					}" itemStyle="padding-left: 15px; padding-right: 15px; height: 50px;" @click="tabChange">
							<view slot="right" class="post-add" @tap="toTypes">
								<u-icon name="plus-circle-fill" color="#007aff" size="18" bold></u-icon>
								<text class="add-name">添加工种</text>
							</view>
						</u-tabs>
					</view>
					<view class="y-sub">
						<view class="sub">
							<u-subsection :list="subsections" fontSize="14" activeColor="#007aff" bgColor="#f5f5f5"
								inactiveColor="#303133" :current="subIndex" @change="subChange"></u-subsection>
						</view>
					</view>
				</u-sticky>
				<view class="y-base">
					<y-post-list :list="list" @success="success"></y-post-list>
				</view>
			</template>
		</mescroll-uni>

		<aui-dialog ref="dialog" :title="auiDialog.title" :msg="auiDialog.msg" :btns="auiDialog.btns"
			:mask="auiDialog.mask" :maskTapClose="auiDialog.maskTapClose" :isButton="true" @opensetting="opensetting"
			:items="auiDialog.items" :theme="auiDialog.theme" @callback="dialogCallback"></aui-dialog>

		<!-- <page-tabpars></page-tabpars> -->
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
		loGetGaodeLocation,
		saveReferrer
	} from '@/config/common';
	import {
		judgeLogin
	} from '@/config/login';
	import permision from "@/plugins/permission.js"
	export default {
		mixins: [MescrollMixin], // 使用mixin
		computed: {
			...mapState(['userInfo', "runMode", 'locateInformation', 'memberRole', 'lastLocation'])
		},
		data() {
			return {
				isIos: false,
				showMenuPop: false,
				lastMemberRole: 'member',
				cityInfo: {
					area: '城市'
				},
				lastCity: '城市',
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
				placeholder: '搜索你想找的工作',

				auiDialog: {
					title: '',
					msg: '',
					btns: [{
						name: '确定'
					}],
					mask: true,
					maskTapClose: true,
					items: [],
					theme: 1,
				},
				confirmInfo:null,
			};
		},

		onReady() {
			// 此处为屏幕宽度
			const windowWidth = uni.$u.sys().windowWidth
			// #ifdef MP-WEIXIN
			let menuButtonInfo = uni.getMenuButtonBoundingClientRect();
			this.rightWidth = windowWidth - menuButtonInfo.left - uni.upx2px(24);
			this.navHeight = uni.$u.sys().statusBarHeight + 44; //菜单栏总高度--单位px
			console.log("windowWidth===", windowWidth);
			console.log("menuButtonInfo===", menuButtonInfo);
			// #endif
			// #ifdef APP-PLUS
			this.navHeight = uni.$u.sys().statusBarHeight + 44; //菜单栏总高度--单位px
			console.log("windowWidth===", windowWidth);
			// #endif
		},

		onLoad(ops) {
			console.log("ops==", ops);
			// #ifdef APP-PLUS
			this.isIos = (plus.os.name == "iOS");
			// #endif
			// this.judgeLocation();
			if (this.lastLocation.area) {
				this.cityInfo = this.lastLocation;
			}
			if (!this.locateInformation.location) {
				this.getLocation();
			}
			// this.initData();
			// #ifdef MP-WEIXIN
			//分享配置
			if (this.userInfo.token) {
				uni.$u.mpShare.path = '/pages/index/index?referrer=' + this.userInfo.id;
			} else {
				uni.$u.mpShare.path = '/pages/index/index';
			}
			uni.$u.mpShare.imageUrl = 'https://img.qinkonglan.cn/imgs/share-lg.png';
			saveReferrer(ops);
			// #endif

		},

		onShow() {
			if (!this.locateInformation.location) {
				// this.getLocation();
			}
			if (this.memberRole == 'company') {
				this.placeholder = "搜索你想找的工人";
			} else {
				this.placeholder = "搜索你想找的工作";
			}
			console.log("==onshow==", this.types);
			//处理顶部tab列表
			this.topTabs = [];
			this.topTabs.push(this.defaultTab);
			if (this.types.length > 0) {
				this.topTabs = this.topTabs.concat(this.types);
				this.tabIds = this.types.map(item => item.id).join(', ');
				console.log("==tabIds==", this.tabIds);
				uni.setStorageSync('topTabs', this.topTabs);
				console.log("==topTabs==", this.topTabs);
				this.query.typeIds = '';
				this.currentTab = 0;
			} else {
				this.initShowData();
			}
			//处理城市选择
			if (this.cityInfo.area != this.lastCity) {
				console.log("===this.cityInfo===", this.cityInfo);
				if (this.locateInformation.location) {
					this.locateInformation.cityInfo = this.cityInfo;
					this.setLocateInformation(this.locateInformation);
					this.setLastLocation(this.cityInfo);
				}
				this.getDataList(1, 10);
			}
			console.log("==this.locateInformation.location==", this.locateInformation.location);
			console.log("==cityInfo==", this.cityInfo);
			this.getUnReadCount();
		},


		methods: {
			...mapMutations(['setRunMode', 'setUserInfo', 'setLocateInformation', 'setLastLocation']),

			getTransferConfirmList() {
				let params = {
					pageNo: 1,
					pageSize:10
				}
				this.$apis.getTransferConfirmList({
					params: params
				}).then(res => {
					console.log('getTransferConfirmList', res);
					if(res.records.length>0){
						this.confirmInfo=res.records[0];
						this.showConfirm();
					}
				});
			},
			
			getTransferByOutBillNo() {
				let params = {
					outBillNo: this.confirmInfo.outBillNo
				}
				this.$apis.getTransferByOutBillNo({
					params: params
				}).then(res => {
					console.log('getTransferByOutBillNo', res);
					this.confirmInfo=null;
				});
			},
			
			showConfirm(){
				if (wx.canIUse('requestMerchantTransfer')) {
					wx.requestMerchantTransfer({
						mchId: this.confirmInfo.mchId,
						appId: this.confirmInfo.appId,
						package: this.confirmInfo.packageInfo,
						success: (res) => {
							// res.err_msg将在页面展示成功后返回应用时返回ok，并不代表付款成功
							console.log('success:', res);
							//查询账单结果
							this.getTransferByOutBillNo();
						},
						fail: (res) => {
							console.log('fail:', res);
						},
					});
				} else {
					wx.showModal({
						content: '你的微信版本过低，请更新至最新版本。',
						showCancel: false,
					});
				}
			},

			judgeLocation() {
				let that = this;
				// #ifdef MP-WEIXIN
				uni.getSetting({
					success: function(res) {
						console.log("res==", res);
						console.log("==res.authSetting['scope.userLocation']==", res.authSetting[
							'scope.userLocation'])
						if (res.authSetting['scope.userLocation'] == false) {
							//未授权
							that.showMenuPop = true;
						} else {
							that.getLocation();
						}
					},
					fail: function(res) {
						that.$u.toast('调用授权窗口失败~');
					}
				})
				// #endif
				//#ifdef APP-PLUS
				if (this.isIos) {
					that.judgeIosPermission();
				} else {
					that.requestAndroidPermission();
				}
				//#endif
			},

			async requestAndroidPermission() {
				let locationPermission = null;
				if (typeof uni.getStorageSync('switchLocationPermissionValue') == "string") {
					locationPermission = true;
				} else {
					locationPermission = uni.getStorageSync('switchLocationPermissionValue');
				}
				console.log("===========locationPermission================", locationPermission)
				if (locationPermission == false) {
					var result = await permision.requestAndroidPermission("android.permission.ACCESS_FINE_LOCATION")
					//1：已获得授权；0：未获得授权；被永久拒绝权限
					if (result == 1) {
						uni.setStorage({
							key: 'switchLocationPermissionValue',
							data: true
						})
					} else {
						that.showMenuPop = true;
						uni.setStorage({
							key: 'switchLocationPermissionValue',
							data: false
						})
					}
				}
			},

			judgeIosPermission: function() {
				var result = permision.judgeIosPermission("location")
				if (result) {
					console.log("已获取位置服务权限");
				} else {
					this.locationAlert();
				}
			},

			locationAlert() {
				var _this = this;
				this.$nextTick(function() {
					console.log(_this.$refs);
					_this.auiDialog.title = '<div style="font-size:18px;font-weight:bold;">定位服务未授权</div>';
					_this.auiDialog.msg = '<div style="font-size:14px;">开启定位服务授权以获取周边岗位信息</div>';
					_this.auiDialog.items = [];
					_this.auiDialog.btns = [{
							name: '取消'
						},
						{
							name: '去开启'
						}
					];
					_this.auiDialog.theme = 1;
					_this.$refs.dialog.show();
				});
				return;
			},

			initData() {
				console.log("runMode===", this.runMode);
				// this.getConfig();
				this.getAdList();
				this.getNoticeList();
				this.getTypeList();
				// this.getDataList(1, 10);
			},

			initShowData() {
				if (this.userInfo.token) {
					if (this.memberRole == 'member') {
						this.getIntention();
					}
					//解决角色切换后数据更新问题
					if (this.lastMemberRole != this.userInfo.memberRole) {
						this.getTypeList();
						this.getDataList(1, 10);
					}
					this.lastMemberRole = this.userInfo.memberRole;
					if(this.confirmInfo==null){
						this.getTransferConfirmList();
					}
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


			getDataList(pageNo, pageSize) {
				if (this.memberRole == 'company') {
					this.getResumeList(pageNo, pageSize);
				} else {
					this.getPostList(pageNo, pageSize);
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
				//获取定位信息
				loGetLocation(
					res => {
						console.log(res, 'loGetLocation');
						console.log('locateInformation', this.locateInformation);
						this.cityInfo.city = this.locateInformation.ad_info.city;
						this.cityInfo.area = this.locateInformation.ad_info.district;
						this.cityInfo.areaCode = this.locateInformation.ad_info.adcode;
						// this.cityInfo.cityCode = this.locateInformation.ad_info.city_code;
						this.cityInfo.cityCode = this.cityInfo.areaCode.substr(0, 4);
						this.cityInfo.latitude = this.locateInformation.location.lat;
						this.cityInfo.longitude = this.locateInformation.location.lng;
						this.lastCity = this.cityInfo.area;
						this.locateInformation.cityInfo = this.cityInfo;
						this.setLocateInformation(this.locateInformation);
						this.setLastLocation(this.cityInfo);
						this.getDataList(1, 10);
						this.updateLocation();
					},
					err => {
						console.log('err==', err);
					},
					true,
					true
				);
			},

			updateLocation() {
				console.log("this.userInfo.token", this.userInfo.token)
				if (!this.userInfo.token) {
					console.log("未登录  无需更新位置")
					return;
				}
				console.log('===updateUserLocation===', this.locateInformation);
				let param = {
					latitude: this.locateInformation.location.lat,
					longitude: this.locateInformation.location.lng,
					city: this.locateInformation.cityInfo.area,
					cityCode: this.locateInformation.cityInfo.areaCode,
					pcity: this.locateInformation.cityInfo.city,
					pcityCode: this.locateInformation.cityInfo.cityCode,
					address: this.locateInformation.address,
				};
				this.$apis
					.updateUserLocation(param)
					.then(res => {
						this.userInfo.lat = latlng.lat;
						this.userInfo.lng = latlng.lng;
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
				this.$apis.getAdList().then(res => {
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
				if (!this.locateInformation.location) {
					// console.log("暂无定位信息")
					if (this.cityInfo.latitude) {
						this.query.latitude = this.cityInfo.latitude;
						this.query.longitude = this.cityInfo.longitude;
					} else {
						console.log("暂无定位信息")
						return;
					}
				} else {
					this.query.latitude = this.locateInformation.location.lat;
					this.query.longitude = this.locateInformation.location.lng;
					// if (this.cityInfo.areaCode.length == 4) { //整个地区
					// 	this.query.pCityCode = this.cityInfo.cityCode;
					// 	this.query.cityCode = "";
					// } else {
					// 	this.query.cityCode = this.cityInfo.areaCode;
					// }
					//默认显示当前所在地区，体验版关闭，显示全部数据20250607
					// this.query.pCityCode = this.cityInfo.cityCode;
					// this.query.cityCode = "";
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
					//默认显示当前所在地区，体验版关闭，显示全部数据20250607
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
				uni.$u.route('/pages/notice/detail?id=' + item.id);
			},

			goMore() {
				this.onJump('/pages/notice/notice');
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
				this.getDataList(page.num, page.size);
			},

			//点击空布局按钮的回调
			emptyClick() {
				uni.showToast({
					title: '点击了按钮,具体逻辑自行实现'
				});
			},

			//未读消息显示
			async getUnReadCount() {
				let params = {
					userId: null,
				}
				if (this.userInfo.token) {
					params.userId = this.userInfo.id;
				} else {
					return;
				}
				let res = await this.$apis.getUnReadCount({
					params: params
				});
				if (res) {
					let count = res.privateCount + res.violationCount + res.publicCount + res.orderCount + res
						.financeCount;
					if (count > 0) {
						uni.setTabBarBadge({
							index: 1,
							text: count + ''
						})
					} else {
						uni.removeTabBarBadge({
							index: 1
						})
					}
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

	.y-notice {}

	.y-expected {
		background-color: #fff;
		padding: 20upx;

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

	.y-swiper {
		// padding: 20upx 0;
		// margin-top: 10upx;
		padding: 10upx 20upx 0 20upx;
		// border-radius: 20upx;
		background-color: #fff;
	}

	.y-type {
		padding: 20upx 0;
		background-color: #fff;

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

	.y-sub {
		background-color: #fff;
		padding: 10upx 20upx 20upx 20upx;

		.sub {
			width: 300upx;
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