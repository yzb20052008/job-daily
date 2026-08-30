<template>
	<view class="content">
		<view class="order">
			<view class="order-info">
				<text class="order-title">{{post.title}}</text>
				<view class="order-address">
					<view class="row">
						<text class="address-name">工作地点：</text>
						<text class="address-info">{{post.address}}</text>
					</view>
				</view>
				<view class="order-time">
					<text class="time-title">工作时间：</text>
					<text class="time-info">
						{{$u.timeFormat(post.startTime,'mm-dd hh:MM')}} 至 {{$u.timeFormat(post.endTime,'mm-dd hh:MM')}}
					</text>
				</view>
				<view class="order-price">
					<view class="tags-item" v-if="post.postStatus==2">
						<u-tag :text="post.postStatus|formatStatus" size="medium" color="#007aff" borderColor="#E5F4FF" bgColor="#E5F4FF"></u-tag>
					</view>
					<view class="tags-item" v-else>
						<u-tag :text="post.postStatus|formatStatus" size="medium" color="#333" borderColor="#eee" bgColor="#eee"></u-tag>
					</view>
					<view class="tags-item">
						<u-tag :text="post.salaryUnit=='面议'?'面议':post.salary+post.salaryUnit" size="medium" color="red" borderColor="#FCEDEC"
							bgColor="#FCEDEC"></u-tag>
					</view>
				</view>
			</view>
		</view>
		<u-sticky offset-top="0">
			<view class="tab">
				<u-tabs :list="tabs" @click="tabClick" :scrollable="true" lineWidth="30" lineColor="#007aff"
					:activeStyle="{
						color: '#000',
						fontWeight: 'bold',
						transform: 'scale(1.1)'
					}" :inactiveStyle="{
						color: '#333',
						fontWeight: 'bold',
						transform: 'scale(1)'
					}"></u-tabs>
			</view>
		</u-sticky>
		<mescroll-body ref="mescrollRef" @init="mescrollInit" @down="downCallback" @up="upCallback" :up="upOption"
			@scroll="scroll" @topclick="topClick">
			<y-worker-list :list="list" @ensure="ensure" @cancel="cancel" @pay="toPay"></y-worker-list>
		</mescroll-body>

		<u-popup :show="show" @close="show=false" :round="10" mode="center" closeable :safeAreaInsetBottom="false">
			<view class="pop-content">
				<text class="pop-title">{{popTitle}}</text>
				<u-form labelPosition="top" labelWidth="auto" :model="form" ref="form" :rules="rules">
					<u-form-item label="工人姓名" labelPosition="left" borderBottom>
						<text>{{currentItem.userName}}</text>
					</u-form-item>
					<u-form-item label="开始时间" labelPosition="left" borderBottom>
						<text>{{currentItem.startTime}}</text>
					</u-form-item>
					<u-form-item label="结束时间" labelPosition="left" borderBottom>
						<text>{{currentItem.endTime}}</text>
					</u-form-item>
					<u-form-item label="工作时长" labelPosition="left" borderBottom>
						<text
							style="color: red;font-weight: bold;">{{djsTime(currentItem.startTime,currentItem.endTime)}}</text>
					</u-form-item>
					<u-form-item label="结算工资" labelPosition="top" prop="amount">
						<u-input placeholder="可按与工人协商金额修改" type="digit" v-model="form.amount" prefixIcon="rmb"
							prefixIconStyle="font-size: 22px;color: #909399"></u-input>
					</u-form-item>
				</u-form>
				<text class="tips">已按单价×工时预填建议金额，可按协商结果修改后再支付</text>
				<view class="pay">
					<view class="pay-item" v-for="(item,index) in pays" :key="index" @click="selectPayType(index,item)">
						<view class="left">
							<image :src="item.icon"></image>
							<text>{{item.title}}</text>
						</view>
						<text class="right yzb"
							:class="item.selected?'yzb-yuanxingxuanzhongfill':'yzb-yuanxingweixuanzhong'"></text>
					</view>
				</view>
				<view class="pop-btn">
					<view class="btn">
						<u-button text="取消" @click="show=false"></u-button>
					</view>
					<view class="btn" style="margin-left: 20upx;">
						<u-button type="primary" text="立即支付" @click="btnAClick()"></u-button>
					</view>
				</view>
			</view>
		</u-popup>
	</view>
</template>

<script>
	import MescrollMixin from "@/uni_modules/mescroll-uni/components/mescroll-uni/mescroll-mixins.js";
	import {
		getTime
	} from '@/plugins/utils';

	export default {
		mixins: [MescrollMixin], // 使用mixin
		data() {
			return {
				popTitle: '工资结算',
				currentItem: {
					index: 0,
					id: "",
				},
				show: false,
				form: {
					amount: '',
				},
				id: '',
				post: {},
				list: [],
				actionSubmitting: false,
				//订单状态：0-待确认，1-待开工，2-工作中，3-待结算，4-待评价，5-已完成，6-已取消
				tabs: [{
					name: '全部'
				}, {
					name: '待确认',
					orderStatus: 0,
					badge: {
						value: 0,
					}
				}, {
					name: '待开工',
					orderStatus: 1,
					badge: {
						value: 0,
					}
				}, {
					name: '工作中',
					orderStatus: 2,
					badge: {
						value: 0,
					}
				}, {
					name: '待结算',
					orderStatus: 3,
					badge: {
						value: 0,
					}
				}, {
					name: '待评价',
					orderStatus: 4,
					badge: {
						value: 0,
					}
				}, ],

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
				query: {
					pageNo: 1,
					pageSize: 10,
					postId: '',
					orderStatus: null,
				},
				
				selectedPay: null,
				pays: [{
						id: "1",
						payType: "wxPay",
						title: '微信支付',
						icon: '/static/images/my/weix.png',
						selected: true,
						active: true,
					},
					{
						id: "2",
						payType: "aliPay",
						title: '支付宝支付',
						icon: '/static/images/my/zfb.png',
						selected: false,
						active: true,
					},
				],

				rules: {
					amount: [{
						required: true,
						message: '请输入结算工资',
						trigger: ['blur', 'change']
					}],
				}
			}
		},
		
		filters:{
			formatStatus(status) {
				let str = '';
				switch (status) {
					case "1":
						str = "待审核"
						break;
					case "2":
						str = "招工中"
						break;
					case "3":
						str = "发布失败"
						break;
					case "4":
						str = "已停招"
						break;
					case "5":
						str = "已取消"
						break;
					case "6":
						str = "已招满"
						break;
				}
				return str;
			},
		},

		onLoad(options) {
			// 微信小程序不展示支付宝；其它端保留
			// #ifdef MP-WEIXIN
			this.pays = this.pays.filter(p => p.payType !== 'aliPay');
			// #endif
			this.pays.forEach((p, i) => {
				p.selected = i === 0;
			});
			this.selectedPay = this.pays[0] || null;
			this.id = options.id;
			this.getPostDetail();
			// this.getOrderStatistics();
		},
		
		onShow() {
			this.mescroll.resetUpScroll();
		},

		methods: {

			//放弃接单
			cancel(id, index) {
				let that = this;
				uni.showModal({
					title: "温馨提示",
					content: "确定取消接单？",
					confirmText: "确定",
					cancelText: "取消",
					success: (data) => {
						if (data.confirm) {
							// 订单状态：0-待确认，1-待开工，2-工作中，3-待结算，4-待评价，5-已完成，6-已取消
							that.updateOrderStatus(id, index, 6)
						}
					}
				});
			},

			//确认接单
			ensure(id, index) {
				let that = this;
				uni.showModal({
					title: "温馨提示",
					content: "确认已沟通并接单？",
					confirmText: "确定",
					cancelText: "取消",
					success: (data) => {
						if (data.confirm) {
							//订单状态：0-待确认，1-待开工，2-工作中，3-待结算，4-待评价，5-已完成，6-已取消
							that.updateOrderStatus(id, index, 1)
						}
					}
				});
			},

			toPay(item, index) {
				this.currentItem = item;
				this.currentItem.index = index;
				// 预填建议金额（单价×工时），老板可改，后端不再强制一致
				let suggest = '';
				const price = Number(item.unitPrice || item.unit_price || 0);
				const duration = Number(item.duration || 0);
				if (price > 0 && duration > 0) {
					suggest = (price * duration).toFixed(2);
				} else if (item.payableAmount) {
					suggest = String(item.payableAmount);
				}
				this.form.amount = suggest;
				this.show = true;
			},

			btnAClick() {
				console.log('btnClick');
				let that = this;
				this.$refs.form
					.validate()
					.then(res => {
						that.pay();
					})
					.catch(errors => {
						uni.$u.toast('请输入结算工资');
					});
			},
			
			pay(){
				if (this.actionSubmitting) {
					return;
				}
				console.log("money==",this.money)
				this.show = false;
				if (!this.selectedPay) {
					this.selectedPay = this.pays[0];
				}
				if(this.selectedPay.payType=='wxPay'){
					this.minipay();
				} else if(this.selectedPay.payType=='aliPay'){
					this.alipay();
				} else {
					uni.$u.toast('暂不支持该支付方式');
				}
			},
			
			async minipay() {
				if (this.actionSubmitting) {
					return;
				}
				let that = this;
				this.actionSubmitting = true;
				this.form.orderId=this.currentItem.id;
				console.log('-------params-------', this.form);
				try {
				let res = await this.$apis.miniPaySalary({
					params: this.form
				});
				if (res) {
					console.log('--------res-------', res);
					var data = res;
					console.log(data);
					data = JSON.parse(data);
					console.log(typeof data);
					uni.requestPayment({
						provider: 'wxpay',
						timeStamp: data.timeStamp,
						nonceStr: data.nonceStr,
						package: data.package,
						signType: data.signType,
						paySign: data.paySign,
						success: function(res) {
							console.log('-------res-------');
							console.log(res);
							uni.showToast({
								icon: 'none',
								title: "结算成功"
							})
							setTimeout(() => {
								that.getOrderStatistics();
								that.mescroll.resetUpScroll();
							}, 10);
						},
						fail: function(err) {
							console.log('-------err-------', err);
							uni.$u.toast('支付取消或失败');
						},
						complete: function() {
							that.actionSubmitting = false;
						}
					});
				} else {
					this.actionSubmitting = false;
				}
				} catch (e) {
					this.actionSubmitting = false;
					throw e;
				}
			},

			async alipay() {
				if (this.actionSubmitting) {
					return;
				}
				let that = this;
				this.actionSubmitting = true;
				this.form.orderId = this.currentItem.id;
				try {
				let res = await this.$apis.appPaySalary({
					params: this.form
				});
				if (res) {
					uni.requestPayment({
						provider: 'alipay',
						orderInfo: res,
						success: function() {
							uni.showToast({
								icon: 'none',
								title: '结算成功'
							});
							setTimeout(() => {
								that.getOrderStatistics();
								that.mescroll.resetUpScroll();
							}, 10);
						},
						fail: function(fail) {
							console.log('alipay fail', fail);
							uni.showToast({
								icon: 'none',
								title: '结算失败，请重试'
							});
						},
						complete: function() {
							that.actionSubmitting = false;
						}
					});
				} else {
					this.actionSubmitting = false;
				}
				} catch (e) {
					this.actionSubmitting = false;
					throw e;
				}
			},
			
			selectPayType(index, item) {
				if (item.active == false) {
					uni.showToast({
						icon: 'none',
						title: "暂不支持"
					})
					return;
				}
				for (var i = 0; i < this.pays.length; i++) {
					this.pays[i].selected = false;
				}
				this.pays[index].selected = true;
				this.selectedPay = this.pays[index];
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
					// 优先走动作接口，由服务端校验状态机与权限
					let res;
					if (status == 1) {
						res = await this.$apis.confirmOrder(params);
					} else if (status == 6) {
						res = await this.$apis.cancelOrder(params);
					} else {
						params.orderStatus = status;
						res = await this.$apis.updateOrderStatus(params);
					}
					console.log("updateOrderStatus", res);
					if (res) {
						uni.$u.toast('操作成功');
						this.mescroll.resetUpScroll();
					}
				} finally {
					this.actionSubmitting = false;
				}
			},

			getOrderStatistics() {
				let params = {
					postId: this.id
				}
				this.$apis
					.getOrderStatistics({
						params: params
					})
					.then(res => {
						console.log('getOrderStatistics', res);
						if (res) {
							this.tabs[1].badge.value = res.waitEnsure;
							this.tabs[2].badge.value = res.waitStart;
							this.tabs[3].badge.value = res.working;
							this.tabs[4].badge.value = res.waitPay;
							this.tabs[5].badge.value = res.waitComment;
						}
					})
					.catch(err => {
						console.log(err, 'catch');
					});
			},

			getPostDetail() {
				let params = {
					id: this.id
				}
				this.$apis
					.getPostDetail({
						params: params
					})
					.then(res => {
						console.log('getPostDetail', res);
						if (res) {
							this.post = res;
						}
					})
					.catch(err => {
						console.log(err, 'catch');
					});
			},

			getPostUserList(pageNum, pageSize) {
				this.query.pageNo = pageNum;
				this.query.pageSize = pageSize;
				this.query.postId = this.id;
				this.$apis.getPostUserList({
					params: this.query
				}).then(res => {
					console.log('getPostUserList', res);
					if (pageNum == 1) {
						this.list = [];
					}
					if (res) {
						let data = res.records;
						this.list = this.list.concat(data);
						this.mescroll.endSuccess(res.records.length);
					}
				});
			},

			tabClick(e) {
				console.log("tabClick===", e);
				this.query.orderStatus = e.orderStatus;
				this.mescroll.resetUpScroll();
			},

			djsTime(start, end) {
				let time = getTime(new Date(start).getTime(), new Date(end).getTime());
				if (time.mm == 0) {
					return time.hh + "小时"
				}
				return time.hh + "小时" + time.mm + "分钟"
				console.log("djsTime==", time);
			},

			/*下拉刷新的回调 */
			downCallback() {
				this.mescroll.resetUpScroll();
			},
			/*上拉加载的回调: 其中page.num:当前页 从1开始, page.size:每页数据条数,默认10 */
			upCallback(page) {
				this.getPostUserList(page.num, page.size);
				this.getOrderStatistics();
			},
		}
	}
</script>

<style lang="scss">
	page {
		background-color: #f5f6fa;
	}

	.order {
		background-color: #fff;
		padding: 20upx 20upx 0 20upx;
	}

	.order-info {
		padding: 20upx;
		background-color: #f5f6fa;
		display: flex;
		flex-direction: column;
		border-radius: 20upx;

		.order-title {
			font-weight: bold;
			font-size: 32rpx;
			padding: 10upx 0;
		}

		.order-address {
			display: flex;
			flex-direction: row;
			align-items: center;
			justify-content: space-between;

			.address-name {
				font-size: 14px;
				color: #666;
			}

			.address-info {
				font-size: 28rpx;
				color: #666;
			}

			.price {
				color: red;
				font-size: 28rpx;
			}
		}

		.order-time {
			.time-title {
				font-size: 28rpx;
				color: #666;
			}

			.time-info {
				font-size: 28rpx;
				color: #666;
			}
		}

		.order-price {
			display: flex;
			flex-direction: row;
			flex-wrap: wrap;
			margin-top: 10upx;

			.tags-item {
				margin-right: 15upx;
			}
		}
	}

	.tab {
		background-color: #fff;
	}

	/deep/ .u-tabs__wrapper__nav__item__text {
		font-size: 18px;
	}

	.pop-content {
		display: flex;
		flex-direction: column;
		padding: 30upx;
		min-width: 90vw;

		.pop-title {
			font-size: 18px;
			font-weight: bold;
			text-align: center;
			padding: 0 30upx 30upx 30upx;
			border-bottom: 1upx solid #eee;
		}

		.tips {
			font-size: 28upx;
			color: #f0ad4e;
			margin-bottom: 20upx;
			margin-top: 20upx;
		}

		.pay {
			background-color: #fff;
			margin-top: 30upx;
			padding: 20upx;
			border-radius: 10upx;

			.pay-item {
				padding: 20upx 20upx;
				display: flex;
				flex-direction: row;
				align-items: center;
				justify-content: space-between;
				border-bottom: 1upx solid #eee;

				.left {
					display: flex;
					flex-direction: row;
					align-items: center;

					image {
						width: 45upx;
						height: 45upx;
					}

					text {
						margin-left: 15upx;
						color: #333;
					}
				}

				.right {
					font-size: 44upx;
					color: #666;
				}

				.yzb-yuanxingxuanzhongfill {
					color: #007aff;
				}

				.yzb-yuanxingweixuanzhong {
					color: #999;
				}

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

	/deep/ .u-form-item__body__left__content__label {
		font-size: 18px;
		color: #333;
		font-weight: bold;
		min-width: 90px;
		padding: 0upx 0;
	}
</style>