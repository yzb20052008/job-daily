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
				confirmInfo:null,
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
					if(this.confirmInfo==null){
						this.getTransferConfirmList();
					}
				}
				let data = {
					types: this.types,
					cityInfo: this.cityInfo
				}
				if (this.memberRole == 'company') {
					this.$refs.boss.initShowData(data);
				} else if (this.memberRole == 'member') {
					this.$refs.user.initShowData(data);
				}
			},
			
			
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

			getUserInfo() {
				this.$apis
					.getUserInfo()
					.then(res => {
						console.log('getUserInfo', res);
						if (res) {
							this.setUserInfo(res);
							this.setMemberRole(res.memberRole);
							// console.log("chatRole===", this.chatRole);
							// if (this.chatRole != res.memberRole) {
							// 	console.log("====角色不一致=====");
							// 	this.disconnectChat();
							// 	this.handleShow();
							// }
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
</style>