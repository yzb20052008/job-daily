<!-- 菜单悬浮的原理: 监听滚动条的位置大于某个值时,控制顶部菜单的显示和隐藏, 用法比sticky复杂, 但APP端可兼容低端机 -->
<template>
	<view>
		<public-module></public-module>
		<!-- 菜单 (悬浮,预先隐藏)-->
		<me-tabs v-if="isShowSticky" v-model="tabIndex" :fixed="true" :tabs="userInfo.memberRole=='company'?tabs2:tabs"
			@change="tabChange"></me-tabs>
		<mescroll-body ref="mescrollRef" @init="mescrollInit" @down="downCallback" @up="upCallback" :up="upOption"
			@scroll="scroll" @topclick="topClick">
			<!-- 菜单 (在mescroll-uni中不能使用fixed,否则iOS滚动时会抖动, 所以需在mescroll-uni之外存在一个一样的菜单) -->
			<view id="tabInList">
				<me-tabs v-model="tabIndex" :tabs="userInfo.memberRole=='company'?tabs2:tabs"
					@change="tabChange"></me-tabs>
			</view>
			<!-- 数据列表 -->
			<template v-if="userInfo.memberRole=='company'">
				<y-order-item :list="tabs2[tabIndex].list" @addPrice="addPrice" @close="toClose" @start="toStart"
					@cancel="cancelPost"></y-order-item>
			</template>
			<template v-else>
				<y-order-user :list="tabs[tabIndex].list" @startWork="startWork" @endWork="endWork"
					@cancelWork="cancelWork"></y-order-user>
			</template>
		</mescroll-body>
		<u-popup :show="show" @close="show=false" :round="10" closeable :safeAreaInsetBottom="false">
			<view class="pop-content">
				<text class="pop-title">{{popTitle}}</text>
				<u-form labelPosition="top" labelWidth="auto" :model="form" :rules="rules" ref="form">
					<u-form-item label="现场照片" labelPosition="top" prop="images" required>
						<u-upload :fileList="fileList1" @afterRead="afterRead" @delete="deletePic" name="1" multiple
							:multiple="true" :maxCount="3"></u-upload>
					</u-form-item>
					<text class="tips">拍摄工地照片，可以为后续可能的纠纷提供法律依据</text>
					<u-form-item label="工作位置" labelPosition="top" prop="address" required>
						<u-input placeholder="工作地点" border="none" readonly v-model="form.address" />
					</u-form-item>
					<u-form-item label="当前距离" labelPosition="top">
						<view class="distance-row">
							<text class="distance-value">{{ distanceText }}</text>
							<text class="distance-hint">（允许 {{clockRange}} 米内打卡）</text>
						</view>
					</u-form-item>
				</u-form>
				<view class="pop-btn">
					<view class="btn">
						<u-button text="关闭" @click="show=false"></u-button>
					</view>
					<view class="btn" style="margin-left: 20upx;">
						<u-button type="primary" text="确认" @click="doStartEndWork"></u-button>
					</view>
				</view>
			</view>
		</u-popup>
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
	export default {
		mixins: [MescrollMixin], // 使用mixin
		computed: {
			...mapState(['userInfo', 'locateInformation', 'lastLocation']),
			// 列表数据
			list() {
				return this.tabs[this.tabIndex].list || [];
			},
			distanceText() {
				if (this.form.latitude === '' || this.form.latitude == null) {
					return '定位中…';
				}
				const d = Number(this.form.distance);
				if (isNaN(d)) {
					return '—';
				}
				return '距工作点 ' + Math.round(d) + ' 米';
			}
		},
		data() {
			return {
				startEnd: 1, //1-上班，2-下班
				popTitle: '上班打卡',
				currentItem: {},
				show: false,
				fileList1: [],
				form: {
					address: '',
					latitude: '',
					longitude: '',
					images: '',
					distance: 0,
				},
				clockRange: 2000,
				actionSubmitting: false,
				upOption: {
					onScroll: true,
					auto: false, // 不自动加载
					noMoreSize: 10, //如果列表已无数据,可设置列表的总数量要大于半页才显示无更多数据;避免列表数据过少(比如只有一条数据),显示无更多数据会不好看; 默认5
					textNoMore: '-- 没有更多 --',
					empty: {
						tip: '空空如也', // 提示
						icon: 'https://cdn.example.com/imgs/data.jpg'
					}
				},
				//订单状态：0-待确认，1-待开工，2-工作中，3-待结算，4-待评价，5-已完成,6-已取消
				//招工状态：1-待审核，2-招工中，3-发布失败，4-已停招，5-已取消，6-已招满
				tabs: [{
						name: '全部',
						list: null,
						num: 1,
						y: 0,
						curPageLen: 0,
						hasNext: true,
						status: null
					},
					{
						name: '进行中',
						list: null,
						num: 1,
						y: 0,
						curPageLen: 0,
						hasNext: true,
						status: 1
					},
					{
						name: '待评价',
						list: null,
						num: 1,
						y: 0,
						curPageLen: 0,
						hasNext: true,
						status: 2
					},
					{
						name: '已完成',
						list: null,
						num: 1,
						y: 0,
						curPageLen: 0,
						hasNext: true,
						status: 3
					},
					{
						name: '已取消',
						list: null,
						num: 1,
						y: 0,
						curPageLen: 0,
						hasNext: true,
						status: 4
					},
				],
				tabs2: [{
						name: '全部',
						list: null,
						num: 1,
						y: 0,
						curPageLen: 0,
						hasNext: true,
						status: 0
					},
					{
						name: '进行中',
						list: null,
						num: 1,
						y: 0,
						curPageLen: 0,
						hasNext: true,
						status: 1
					},
					{
						name: '已完成',
						list: null,
						num: 1,
						y: 0,
						curPageLen: 0,
						hasNext: true,
						status: 2
					},
					{
						name: '已取消',
						list: null,
						num: 1,
						y: 0,
						curPageLen: 0,
						hasNext: true,
						status: 3
					},
				],
				tabIndex: 0, // 当前菜单下标
				preIndex: 0, // 前一个菜单下标
				navTop: null, // nav距离到顶部的距离 (如计算不准确,可直接写死某个值)
				isShowSticky: false, // 是否悬浮
				newsDetailUrl: '',
				query: {
					pageNo: 1,
					pageSize: 10,
					status: null,
				},
				rules: {
					images: [{
						required: true,
						message: '现场照片不能为空',
						trigger: ['blur', 'change']
					}],
					address: [{
						required: true,
						message: '工作位置不能为空',
						trigger: ['blur', 'change']
					}],
				}
			}
		},
		onLoad(parms) {
			this.currentTab = this.tabs[0];
			this.getBaseConfig();
		},

		onShow() {
			this.mescroll.resetUpScroll();
			this.getUnReadCount();
		},

		methods: {
			...mapMutations(['setLocateInformation']),

			getList(pageIndex, pageSize) {
				if (!this.userInfo.token) {
					console.log("===getList===未登录")
					uni.hideLoading();
					this.mescroll.endSuccess(0);
					return;
				}
				if (this.userInfo.memberRole == 'company') {
					this.getMyPostList(pageIndex, pageSize);
				} else {
					this.getOrderList(pageIndex, pageSize);
				}
			},

			/**
			 * 任务列表
			 */
			async getOrderList(pageIndex, pageSize) {
				this.query.pageNo = pageIndex;
				this.query.pageSize = pageSize;
				let res = await this.$apis.getOrderList({
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
				// this.mescroll.endSuccess(res.length);
			},


			/**
			 * 任务列表
			 */
			async getMyPostList(pageIndex, pageSize) {
				this.query.pageNo = pageIndex;
				this.query.pageSize = pageSize;
				let res = await this.$apis.getMyPostList({
					params: this.query,
				});
				// 当前tab数据
				let curTab = this.tabs2[this.tabIndex]
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
				// this.mescroll.endSuccess(res.length);
			},

			//停招
			toClose(id, index) {
				let that = this;
				uni.showModal({
					title: "温馨提示",
					content: "确定停止招工？",
					confirmText: "确定",
					cancelText: "取消",
					success: (data) => {
						if (data.confirm) {
							that.updatePostStatus(id, index, 4)
						}
					}
				});
			},

			//停招
			toStart(id, index) {
				let that = this;
				uni.showModal({
					title: "温馨提示",
					content: "确定开始招工？",
					confirmText: "确定",
					cancelText: "取消",
					success: (data) => {
						if (data.confirm) {
							that.updatePostStatus(id, index, 2)
						}
					}
				});
			},

			//取消招工
			cancelPost(id, index) {
				let that = this;
				uni.showModal({
					title: "温馨提示",
					content: "确定取消招工？",
					confirmText: "确定",
					cancelText: "取消",
					success: (data) => {
						if (data.confirm) {
							//1-待审核，2-招工中，3-发布失败，4-已停招，5-已取消，6-已招满
							that.updatePostStatus(id, index, 5)
						}
					}
				});
			},

			addPrice() {
				uni.$u.toast('加价');
			},

			async updatePostStatus(id, index, status) {
				let params = {
					id: id,
					postStatus: status,
				}
				let res = await this.$apis.updatePostStatus(params);
				if (res) {
					uni.$u.toast('操作成功');
					this.tabs2[this.tabIndex].list[index].postStatus = status + "";
					this.$forceUpdate();
				}
			},
			//--------------------------------------工人端相关--------------------------------------------

			startWork(item, index) {
				this.startEnd = 1;
				this.popTitle = "上班打卡";
				this.currentItem = item;
				this.currentItem.index = index;
				this.form.address = item.addressName || item.address || '工作地点';
				this.form.distance = 0;
				this.form.latitude = '';
				this.form.longitude = '';
				this.show = true;
				this.getLocation();
			},

			endWork(item, index) {
				this.startEnd = 2;
				this.popTitle = "下班打卡";
				this.currentItem = item;
				this.currentItem.index = index;
				this.form.address = item.addressName || item.address || '工作地点';
				this.form.distance = 0;
				this.form.latitude = '';
				this.form.longitude = '';
				this.show = true;
				this.getLocation();
			},

			//放弃
			cancelWork(id, index) {
				let that = this;
				uni.showModal({
					title: "温馨提示",
					content: "确定放弃此工作？",
					confirmText: "确定",
					cancelText: "取消",
					success: (data) => {
						if (data.confirm) {
							that.updateOrderStatus(id, index, 6);
						}
					}
				});
			},

			async updateOrderStatus(id, index, status) {
				if (this.actionSubmitting) {
					return;
				}
				this.actionSubmitting = true;
				try {
					let params = {
						id: id,
					}
					let res;
					if (status == 6) {
						res = await this.$apis.cancelOrder(params);
					} else if (status == 1) {
						res = await this.$apis.confirmOrder(params);
					} else {
						params.orderStatus = status;
						res = await this.$apis.updateOrderStatus(params);
					}
					if (res) {
						uni.$u.toast('操作成功');
						this.tabs[this.tabIndex].list[index].orderStatus = status + "";
					}
				} finally {
					this.actionSubmitting = false;
				}
			},

			resetForm() {
				this.fileList1 = [];
				this.form.images = '';
				this.form.orderId = '';
				this.form.address = '';
				this.form.latitude = '';
				this.form.longitude = ""
			},

			async doStartEndWork() {
				if (this.actionSubmitting) {
					return;
				}
				if (this.form.distance > this.clockRange) {
					uni.$u.toast('请在工作地点' + this.clockRange + "米范围内打卡");
					return;
				}
				if (this.fileList1.length == 0) {
					uni.$u.toast('请上传现场照片');
					return;
				}
				if (!this.form.address) {
					this.form.address = (this.currentItem && (this.currentItem.addressName || this.currentItem.address))
						|| '工作地点';
				}
				if (this.form.latitude === '' || this.form.latitude == null) {
					uni.$u.toast('未获取到定位坐标，请开启位置权限');
					return;
				}
				// 订单状态：0-待确认，1-待开工，2-工作中，3-待结算，4-待评价，5-已完成,6-已取消
				let orderStatus = 2;
				let clockType = 1;
				if (this.startEnd == 2) {
					orderStatus = 3;
					clockType = 2;
				}
				if (this.fileList1) {
					let arr = [];
					this.fileList1.forEach(item => {
						arr.push(item.url);
					});
					if (arr.length > 0) {
						this.form.images = arr.join(',');
					}
				}
				this.form.orderId = this.currentItem.id;
				this.form.clockType = clockType;
				this.actionSubmitting = true;
				try {
					let res = await this.$apis.workClock(this.form);
					if (res) {
						this.show = false;
						uni.$u.toast('打卡成功');
						this.resetForm();
						this.tabs[this.tabIndex].list[this.currentItem.index].orderStatus = orderStatus + "";
						this.$forceUpdate();
					}
				} finally {
					this.actionSubmitting = false;
				}
			},

			// 删除图片
			deletePic(event) {
				this[`fileList${event.name}`].splice(event.index, 1);
			},
			// 新增图片
			async afterRead(event) {
				// 当设置 mutiple 为 true 时, file 为数组格式，否则为对象格式
				let lists = [].concat(event.file);
				let fileListLen = this[`fileList${event.name}`].length;
				lists.map(item => {
					this[`fileList${event.name}`].push({
						...item,
						status: 'uploading',
						message: '上传中'
					});
				});
				for (let i = 0; i < lists.length; i++) {
					const result = await this.uploadFilePromise(lists[i].url);
					let item = this[`fileList${event.name}`][fileListLen];
					this[`fileList${event.name}`].splice(
						fileListLen,
						1,
						Object.assign(item, {
							status: 'success',
							message: '',
							url: result
						})
					);
					fileListLen++;
				}
				console.log('上传成功', this.fileList1);
			},
			uploadFilePromise(url) {
				return new Promise((resolve, reject) => {
					uni.uploadFile({
						url: BaseUrl.baseUrl + "/api/file/upload",
						filePath: url,
						name: 'file',
						header: {
							'X-Access-Token': (uni.getStorageSync('userInfo') && uni.getStorageSync('userInfo').token) || ''
						},
						formData: {
							biz: "job/work"
						},
						success: (res) => {
							let data = res.data;
							let imgUrl = JSON.parse(data).message;
							console.log('上传成功', imgUrl);
							resolve(imgUrl);
						},
						fail: (res) => {
							console.log('上传失败', res);
						},
					});
				});
			},

			getLocation() {
				console.log('============getLocation==============');
				// 打卡只需坐标算距离，不依赖逆地理；展示工作位置来自岗位
				loGetLocation(
					res => {
						const location = (res && res.location) || {};
						const lat = location.lat != null ? location.lat : res.latitude;
						const lng = location.lng != null ? location.lng : res.longitude;
						this.form.latitude = lat;
						this.form.longitude = lng;
						// 留痕地址固定为工作位置，不改写为当前位置解析结果
						if (!this.form.address) {
							this.form.address = (this.currentItem && (this.currentItem.addressName || this.currentItem.address))
								|| '工作地点';
						}
						if (this.currentItem && this.currentItem.latitude != null && this.currentItem.longitude != null
							&& lat != null && lng != null) {
							this.form.distance = getDistance(
								this.currentItem.latitude,
								this.currentItem.longitude,
								lat,
								lng
							);
						} else {
							this.form.distance = 0;
							uni.$u.toast('岗位未设置坐标，无法校验距离');
						}
					},
					err => {
						console.log('err==', err);
						uni.$u.toast('定位失败，请开启位置权限后重试');
					},
					{ purpose: 'coord', isOpenSetting: true }
				);
			},

			getBaseConfig() {
				this.$apis.getBaseConfig({
					params: {
						code: 'clock_range'
					}
				}).then(res => {
					if (res) {
						this.clockRange = Number(res.configValue);
					}
				});
			},


			//未读消息显示
			async getUnReadCount() {
				let params = {
					userId: null,
				}
				if (this.userInfo.token) {
					params.userId = this.userInfo.id;
					params.roleCode = this.userInfo.memberRole;
				} else {
					return;
				}
				let res = await this.$apis.getUnReadCount({
					params: params
				});
				if (res) {
					let count = (res.privateCount || 0) + (res.violationCount || 0) + (res.publicCount || 0)
						+ (res.orderCount || 0) + (res.financeCount || 0);
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
				let view = uni.createSelectorQuery().select('#tabInList');
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
				let preTab = this.tabs[this.preIndex]
				preTab.y = this.mescroll.getScrollTop(); // 滚动条位置
				this.preIndex = index;
				// 当前菜单的数据
				let curTab = this.tabs[index]
				this.query.status = curTab.status;
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
			}
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

<style lang="scss">
	page {
		min-height: 100vh;
		background-color: #f5f6fa;
	}

	/deep/ .u-form-item__body__left__content__label {
		height: 40px;
		line-height: 40px;
		font-size: 18px;
		color: #333;
		font-weight: bold;
		min-width: 90px;
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

		.distance-row {
			display: flex;
			flex-direction: row;
			align-items: baseline;
			flex-wrap: wrap;
			padding: 8upx 0 16upx;
		}

		.distance-value {
			font-size: 18px;
			font-weight: bold;
			color: #e54d42;
		}

		.distance-hint {
			font-size: 13px;
			color: #999;
			margin-left: 12upx;
		}
	}
</style>