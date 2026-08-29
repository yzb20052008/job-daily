<template>
	<view class="content">
		<public-module></public-module>
		<liu-add-tips tips='点击「添加小程序」，下次访问更便捷' :duration='10' @change='checked' color='#000'></liu-add-tips>
		<news-index ref="news" v-if="runMode==0"></news-index>
		<boss-index ref="boss" v-else-if="memberRole=='company'"></boss-index>
		<user-index ref="user" v-else></user-index>
		<float-popup-dialog :isShow="showMenuPop" :top="80" :left="30">
			<view class="pop-content" slot="content">
				<text class="pop-info">开启定位，查看附近岗位</text>
				<text class="pop-btn" @click="openLocationSet">开启</text>
				<text class="pop-close yzb yzb-cuo" @click="showMenuPop=false"></text>
			</view>
		</float-popup-dialog>
		<!-- 待确认收款：底部悬浮，避免顶栏被刘海遮挡、打断首页布局 -->
		<view v-if="pendingConfirmTip && confirmInfo" class="transfer-confirm-float" @click="manualConfirmTransfer">
			<text class="transfer-confirm-text">您有一笔提现待确认收款</text>
			<text class="transfer-confirm-action">去确认</text>
		</view>
	</view>
</template>

<script>
	import {
		mapState,
		mapMutations
	} from 'vuex';
	import {
		loGetLocation,
		loGetGaodeLocation,
		saveReferrer
	} from '@/config/common';
	import {
		judgeLogin
	} from '@/config/login';
	import ShareUtils from '@/utils/shareUtils.js'
	const GoEasy = uni.$GoEasy;
	import bossIndex from "./components/bossIndex.vue"
	import userIndex from "./components/userIndex.vue"
	import newsIndex from "./components/newsIndex.vue"
	// 导入分享 mixin
	import mpShare from '@/uni_modules/uview-ui/libs/mixin/mpShare.js';

	/** 确认收款免打扰缓存（按商户单号） */
	const TRANSFER_SNOOZE_KEY = 'wx_transfer_confirm_snooze';
	/** 取消或未到账后，30 分钟内不自动再弹 */
	const TRANSFER_SNOOZE_MS = 30 * 60 * 1000;

	export default {
		mixins: [mpShare],
		computed: {
			...mapState(['userInfo', "runMode", 'locateInformation', 'memberRole', 'lastLocation'])
		},
		components: {
			bossIndex,
			userIndex,
			newsIndex
		},
		data() {
			return {
				showMenuPop: false,
				tabIds: '',
				unreadTotal: 0,
				noticeCount: 0,
				msgTotal: 0,
				types: [],
				cityInfo: {
					area: '选城市'
				},
				confirmInfo: null,
				/** 正在拉起微信确认收款，防 onShow 重入连环弹 */
				confirmLock: false,
				/** 有待确认但已免打扰：展示顶栏手动入口 */
				pendingConfirmTip: false,
			};
		},

		onLoad(ops) {
			console.log("==onLoad==ops==", ops);
			console.log("runMode==", this.runMode);
			if(this.runMode==0){
				uni.hideTabBar();
				return;
			}
			if (this.memberRole == 'company') {
				// this.$refs.boss.initData();
			} else if (this.memberRole == 'member') {
				// this.$refs.user.initData();
			}
			// #ifdef MP-WEIXIN
			//分享配置
			if (this.userInfo.token) {
				ShareUtils.setInviteShare(this.userInfo);
			} else {
				ShareUtils.setPageShare({
					title: '小蓝零工 - 真实招聘，高效上岗',
					path: '/pages/index/index'
				});
			}
			saveReferrer(ops);
			// #endif
		},

		onShow() {
			if(this.runMode==1){
				this.initShowData();
				this.getUnReadCount();
			}
		},

		methods: {
			...mapMutations(['setRunMode', 'setUserInfo', 'setMemberRole']),

			initData() {
				// this.getConfig();
			},

			initShowData() {
				if (this.userInfo.token) {
					this.getUserInfo();
					// #ifdef MP-WEIXIN
					this.tryAutoConfirmTransfer();
					// #endif
				}
				let data = {
					types: this.types,
					cityInfo: this.cityInfo
				}
				if (this.memberRole == 'company') {
					this.$refs.boss && this.$refs.boss.initShowData(data);
				} else if (this.memberRole == 'member') {
					this.$refs.user && this.$refs.user.initShowData(data);
				}
			},

			/**
			 * 拉取待确认收款：自动弹窗仅对未免打扰单据；
			 * 微信 success 不代表到账，禁止因此清空后再 onShow 连环弹。
			 */
			tryAutoConfirmTransfer() {
				if (this.confirmLock) {
					return;
				}
				this.$apis.getTransferConfirmList({
					params: {
						pageNo: 1,
						pageSize: 10
					}
				}).then(res => {
					const records = (res && res.records) ? res.records : [];
					if (!records.length) {
						this.confirmInfo = null;
						this.pendingConfirmTip = false;
						return;
					}
					this.confirmInfo = records[0];
					const autoTarget = records.find(r =>
						r && r.packageInfo && r.outBillNo && !this.isTransferSnoozed(r.outBillNo)
					);
					if (autoTarget && !this.confirmLock) {
						this.confirmInfo = autoTarget;
						this.pendingConfirmTip = false;
						this.showConfirmTransfer();
					} else {
						// 仍有待确认但已免打扰 / 无 package：只展示顶栏
						this.pendingConfirmTip = true;
					}
				}).catch(err => {
					console.log('getTransferConfirmList', err);
				});
			},

			/** 顶栏手动再次拉起确认（忽略免打扰） */
			manualConfirmTransfer() {
				if (!this.confirmInfo || !this.confirmInfo.packageInfo) {
					this.tryAutoConfirmTransfer();
					return;
				}
				if (this.confirmInfo.outBillNo) {
					this.clearTransferSnooze(this.confirmInfo.outBillNo);
				}
				this.pendingConfirmTip = false;
				this.showConfirmTransfer();
			},

			showConfirmTransfer() {
				// #ifndef MP-WEIXIN
				return;
				// #endif
				// #ifdef MP-WEIXIN
				if (this.confirmLock) {
					return;
				}
				if (!this.confirmInfo || !this.confirmInfo.packageInfo) {
					uni.showToast({ title: '暂无待确认收款信息', icon: 'none' });
					return;
				}
				if (!wx.canIUse('requestMerchantTransfer')) {
					wx.showModal({
						content: '你的微信版本过低，请更新至最新版本。',
						showCancel: false,
					});
					return;
				}
				this.confirmLock = true;
				const outBillNo = this.confirmInfo.outBillNo;
				wx.requestMerchantTransfer({
					mchId: this.confirmInfo.mchId,
					appId: this.confirmInfo.appId,
					package: this.confirmInfo.packageInfo,
					success: (res) => {
						// success 仅表示回到小程序，不代表付款成功
						console.log('requestMerchantTransfer success:', res);
						this.refreshTransferAfterConfirm(outBillNo);
					},
					fail: (res) => {
						console.log('requestMerchantTransfer fail:', res);
						// 用户取消或失败：免打扰，避免 onShow 再次自动弹
						this.snoozeTransferConfirm(outBillNo);
						this.pendingConfirmTip = true;
						this.confirmLock = false;
					},
				});
				// #endif
			},

			/**
			 * 确认收款页返回后查单：仅终态才清队列；仍为 WAIT 则免打扰防连环弹
			 */
			refreshTransferAfterConfirm(outBillNo) {
				if (!outBillNo) {
					this.confirmLock = false;
					return;
				}
				this.$apis.getTransferByOutBillNo({
					params: { outBillNo }
				}).then(res => {
					console.log('getTransferByOutBillNo', res);
					const state = res && res.state;
					if (state === 'SUCCESS') {
						uni.showToast({ title: '收款确认成功', icon: 'success' });
						this.clearTransferSnooze(outBillNo);
						this.confirmInfo = null;
						this.pendingConfirmTip = false;
					} else if (state === 'FAIL' || state === 'CANCELLED') {
						uni.showToast({ title: '转账已关闭', icon: 'none' });
						this.clearTransferSnooze(outBillNo);
						this.confirmInfo = null;
						this.pendingConfirmTip = false;
					} else {
						// 仍待确认等中间态：免打扰，改顶栏入口，杜绝连环弹窗
						this.snoozeTransferConfirm(outBillNo);
						this.pendingConfirmTip = !!this.confirmInfo;
					}
				}).catch(err => {
					console.log('getTransferByOutBillNo err', err);
					this.snoozeTransferConfirm(outBillNo);
					this.pendingConfirmTip = true;
				}).finally(() => {
					this.confirmLock = false;
				});
			},

			isTransferSnoozed(outBillNo) {
				if (!outBillNo) {
					return false;
				}
				try {
					const map = uni.getStorageSync(TRANSFER_SNOOZE_KEY) || {};
					const ts = map[outBillNo];
					return !!(ts && (Date.now() - ts < TRANSFER_SNOOZE_MS));
				} catch (e) {
					return false;
				}
			},

			snoozeTransferConfirm(outBillNo) {
				if (!outBillNo) {
					return;
				}
				try {
					const map = uni.getStorageSync(TRANSFER_SNOOZE_KEY) || {};
					map[outBillNo] = Date.now();
					uni.setStorageSync(TRANSFER_SNOOZE_KEY, map);
				} catch (e) {
					// ignore
				}
			},

			clearTransferSnooze(outBillNo) {
				if (!outBillNo) {
					return;
				}
				try {
					const map = uni.getStorageSync(TRANSFER_SNOOZE_KEY) || {};
					if (map[outBillNo]) {
						delete map[outBillNo];
						uni.setStorageSync(TRANSFER_SNOOZE_KEY, map);
					}
				} catch (e) {
					// ignore
				}
			},

			getUserInfo() {
				this.$apis
					.getUserInfo()
					.then(res => {
						console.log('getUserInfo', res);
						if (res) {
							this.setUserInfo(res);
							this.setMemberRole(res.memberRole);
						}
					})
					.catch(err => {
						console.log(err, 'catch');
					});
			},

			checked() {
				console.log('点击了')
			},

			getConfig() {
				this.$apis.getConfig().then(res => {
					console.log('getConfig', res);
					this.setRunMode(res.runMode);
					console.log("runMode222===", this.runMode);
				});
			},

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

	.transfer-confirm-float {
		position: fixed;
		left: 24rpx;
		right: 24rpx;
		/* 紧贴 tabBar 上方，仅留少量间距 */
		bottom: calc(16rpx + 50px + env(safe-area-inset-bottom));
		z-index: 999;
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 22rpx 28rpx;
		background: rgba(255, 247, 230, 0.96);
		border: 1rpx solid #ffd591;
		border-radius: 48rpx;
		box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.08);
	}

	.transfer-confirm-text {
		flex: 1;
		font-size: 26rpx;
		color: #ad6800;
		padding-right: 16rpx;
	}

	.transfer-confirm-action {
		flex-shrink: 0;
		padding: 8rpx 24rpx;
		font-size: 26rpx;
		color: #fff;
		font-weight: 600;
		background: #fa8c16;
		border-radius: 28rpx;
	}
</style>
