<template>
	<view class="detail">
		<public-module></public-module>
		<view class="card-module">
			<text class="title">{{post.title}}</text>
			<view class="price-time">
				<view class="price">
					<text class="price-num" v-if="post.salaryUnit!='面议'">{{post.salary}}</text>
					<text class="price-unit">{{post.salaryUnit || ''}}</text>
				</view>
				<!-- <view class="time-ip">
					<text class="time">8月6日 09:23</text>
					<text class="ip">发布于赣州</text>
				</view> -->
			</view>
			<view class="city-info">
				<text class="city-icon yzb yzb-dingwei"></text>
				<text class="city-name">{{post.pcity}}-{{post.city}}</text>
			</view>
			<view class="time-ip">
				<text class="time">更新：{{post.updateTime || post.createTime}}</text>
				<text class="ip">浏览：{{post.browseNumber}}次</text>
			</view>
		</view>
		<view class="boss-info">
			<view class="info row-align">
				<u-avatar size="50" shape="square" :src="post.userAvatar"></u-avatar>
				<view class="boss column-align">
					<view class="boss-top">
						<text class="boss-name">{{post.userName}}</text>
						<view class="tags">
							<view class="tags-item" v-if="post.ifCompanyAuth">
								<u-tag text="企业认证" size="mini" color="#007aff" borderColor="#E5F4FF"
									bgColor="#E5F4FF"></u-tag>
							</view>
							<view class="tags-item" v-if="!post.ifCompanyAuth && post.ifRealName">
								<u-tag text="已实名" size="mini" color="#007aff" borderColor="#E5F4FF"
									bgColor="#E5F4FF"></u-tag>
							</view>
						</view>
					</view>
					<view class="row-align">
						<text class="boss-phone">{{phoneHiden(post.phone)}}</text>
						<view class="row-align" @click="toComments" v-if="post.score>0">
							<text class="boss-score" v-if="post.score>0">{{post.score}}分</text>
							<text class="score-next yzb yzb-next"></text>
						</view>
					</view>
				</view>
			</view>
			<view class="call column-align" @click="call" v-if="isUser">
				<text class="call-icon yzb yzb-bohao2"></text>
				<text class="call-name">打老板电话</text>
			</view>
		</view>
		<view class="y-ensure" v-if="post.ifShowAgree && isUser">
			<text class="title">是否和老板达成合作意向？</text>
			<text class="desc">如果选择已达成，将生成订单，方便后续对接、结算等。</text>
			<view class="ensure">
				<view class="ensure-item" @click="agreeState=1">
					<view class="ensure-icon-bg"
						:class="agreeState==1?'ensure-icon-bg-selected':'ensure-icon-bg-unSelected'">
						<text class="yzb yzb-dachenghezuo"
							:class="agreeState==1?'ensure-icon-selected':'ensure-icon'"></text>
					</view>
					<view :class="agreeState==1?'ensure-name-selected':'ensure-name'">已达成</view>
				</view>
				<view class="ensure-item" @click="agreeState=2">
					<view class="ensure-icon-bg"
						:class="agreeState==2?'ensure-icon-bg-selected':'ensure-icon-bg-unSelected'">
						<text class="yzb yzb-xinsui" style="margin-top: 4upx;"
							:class="agreeState==2?'ensure-icon-selected':'ensure-icon'"></text>
					</view>
					<view :class="agreeState==2?'ensure-name-selected':'ensure-name'">未达成</view>
				</view>
			</view>
			<text class="tips" v-if="agreeState==1">请根据实际沟通结果进行选择，如发现随意选择，将进行封号处理。</text>
			<view class="submit" v-if="agreeState>0"><u-button @click="submitAgree" type="primary" text="提交"></u-button>
			</view>
		</view>
		<view class="card-module">
			<text class="card-title">职位详情</text>
			<view class="tags">
				<view class="tags-item" v-if="post.postStatus==2">
					<u-tag :text="post.postStatus|formatStatus" size="medium" color="#007aff" borderColor="#E5F4FF"
						bgColor="#E5F4FF"></u-tag>
				</view>
				<view class="tags-item" v-else>
					<u-tag :text="post.postStatus|formatStatus" size="medium" color="#333" borderColor="#eee"
						bgColor="#eee"></u-tag>
				</view>
				<view class="tags-item" v-for="(item,index) in post.typeNames" :key="index">
					<u-tag :text="item" color="#007aff" borderColor="#E5F4FF" bgColor="#E5F4FF"></u-tag>
				</view>
				<!-- <view class="tags-item">
					<u-tag text="日结" color="#666" borderColor="#f5f6fa" bgColor="#f5f6fa"></u-tag>
				</view> -->
				<!-- <view class="tags-item">
					<u-tag text="50岁以下" color="#666" borderColor="#f5f6fa" bgColor="#f5f6fa"></u-tag>
				</view> -->
				<view class="tags-item">
					<u-tag :text="post.sexRequire=='不限'?'男女不限':post.sexRequire" color="#666" borderColor="#f5f6fa"
						bgColor="#f5f6fa"></u-tag>
				</view>
			</view>
			<view class="times row-align">
				<text>用工时间：</text>
				<text>{{$u.timeFormat(post.startTime, 'mm-dd hh:MM')}}</text>
				<text style="margin: 0 15rpx;"> 至 </text>
				<text>{{$u.timeFormat(post.endTime, 'mm-dd hh:MM')}}</text>
			</view>
			<view class="desc">
				<u-read-more ref="uReadMore" showHeight="200" textIndent="0" :toggle="true" closeText="查看全部" v-if="post.descr">
					<text class="desc-content">{{post.descr}} </text>
				</u-read-more>
			</view>
		</view>
		<view class="post-info">
			<view class="location" @click="toMap">
				<map style="width: 100%; height: 100px;" :enable-scroll="false" :latitude="post.latitude"
					:longitude="post.longitude" :markers="covers"></map>
				<view class="cover">
				</view>
				<view class="address">
					<view class="address-item">
						<view class="left">
							<text class="yzb yzb-dingwei"></text>
							<text class="address-name">{{post.address}}</text>
						</view>
						<text class="address-distance">{{commonDistance(post.latitude,post.longitude)}}</text>
					</view>
				</view>
			</view>
		</view>
		<view class="safe-tips">
			<view class="tips-top">
				<view class="row">
					<text class="tip-icon yzb yzb-anquan"></text>
					<text class="tip-title">安全提示</text>
				</view>
				<view class="row" @click="toSafe">
					<text class="tip-detail">查看详情</text>
					<text class="yzb yzb-next"></text>
				</view>
			</view>
			<view class="tips">
				<text class="tip-info">该信息由用户发布，如遇到虚假招聘、诈骗、传销等违规行为，请立即联系平台</text>
				<!-- <text class="tip-report yzb yzb-jubao" @click="toReport">投诉举报</text> -->
			</view>
		</view>
		<view class="more" @click="toPostList">
			<text>查看更多职位</text>
			<text class="yzb yzb-next"></text>
		</view>
		<view class="bottom" v-if="isUser">
			<uni-goods-nav :fill="true" :options="options"
				:button-group="createType==1?customButtonGroup:customButtonGroup2" @click="onClick"
				@buttonClick="buttonClick" />
		</view>
		<u-modal :show="mShow" :title="mTitle" :showCancelButton="showcancel" :confirmText="mConfirmText"
			@cancel="mShow=false" @confirm="mConfirm">
			<view class="slot-content">
				<!-- <text class="m-content" v-if="mContent">{{mContent}}</text> -->
				<rich-text v-if="mContent" class="m-content" :nodes="mContent"></rich-text>
				<view v-else class="column-align">
					<view class="column-align">
						<view class="row-align">
							<u-icon name="info-circle" color="red" size="18"></u-icon>
							<text class="m-title">工作前</text>
						</view>
						<text class="m-desc">请确认好对方身份，找活过程中不要缴纳任何费用、押金。</text>
					</view>
					<view class="column-align">
						<view class="row-align">
							<u-icon name="info-circle" color="red" size="18"></u-icon>
							<text class="m-title">工作中</text>
						</view>
						<text class="m-desc">可拍照、视频留有证据。若发生纠纷，请立即报警或前往劳动局投诉。</text>
					</view>
				</view>
			</view>
			<view slot="confirmButton" class="row-align">
				<u-button type="info" shape="circle" text="取消" :customStyle="{marginRight:'15px'}" v-if="showcancel"
					@click="mShow=false"></u-button>
				<u-button type="primary" shape="circle" :text="mConfirmText" @click="mConfirm"></u-button>
			</view>
		</u-modal>

		<!-- 分享弹窗-->
		<view class="share-pro">
			<view class="share-pro-mask" v-if="deliveryFlag" @click="deliveryFlag=false"></view>
			<view class="share-pro-dialog" :class="deliveryFlag ? 'open' : 'close'">
				<view class="close-btn" @tap="deliveryFlag=false"><u-icon name="close" color="red" size="20" /></view>
				<view class="share-pro-title">分享</view>
				<view class="share-pro-body">
					<view class="share-item">
						<button open-type="share" plain="true" class="btn-share">
							<!-- <text class="yzb yzb-weixinhaoyou"></text> -->
							<u-icon name="weixin-circle-fill" color="#07C160" size="34"></u-icon>
							<view class="share-title">分享给好友</view>
						</button>
					</view>
					<!-- <view class="share-item" @tap="handleShowPoster">
						<text class="yzb yzb-Photo-share"></text>
						<view>生成分享图片</view>
					</view> -->
				</view>
			</view>
		</view>

		<u-popup :show="showAgree" :round="10" mode="bottom" @close="showAgree=false">
			<view class="y-ensure">
				<text class="title">是否和老板达成合作意向？</text>
				<text class="desc">如果选择已达成，将生成订单，方便后续对接、结算等。</text>
				<view class="ensure">
					<view class="ensure-item" @click="agreeState=1">
						<view class="ensure-icon-bg"
							:class="agreeState==1?'ensure-icon-bg-selected':'ensure-icon-bg-unSelected'">
							<text class="yzb yzb-dachenghezuo"
								:class="agreeState==1?'ensure-icon-selected':'ensure-icon'"></text>
						</view>
						<view :class="agreeState==1?'ensure-name-selected':'ensure-name'">已达成</view>
					</view>
					<view class="ensure-item" @click="agreeState=2">
						<view class="ensure-icon-bg"
							:class="agreeState==2?'ensure-icon-bg-selected':'ensure-icon-bg-unSelected'">
							<text class="yzb yzb-xinsui" style="margin-top: 4upx;"
								:class="agreeState==2?'ensure-icon-selected':'ensure-icon'"></text>
						</view>
						<view :class="agreeState==2?'ensure-name-selected':'ensure-name'">未达成</view>
					</view>
				</view>
				<text class="tips" v-if="agreeState==1">请根据实际沟通结果进行选择，如发现随意选择，将进行封号处理。</text>
				<view class="submit" v-if="agreeState>0"><u-button @click="submitAgree" type="primary"
						text="提交"></u-button></view>
			</view>
		</u-popup>
	</view>
</template>

<script>
	import {
		phoneHiden,
		saveReferrer,
		requestSubscribe
	} from '@/config/common';
	import {
		judgeLogin
	} from '@/config/login';
	import {
		mapState,
		mapMutations
	} from 'vuex';
	import {
		commonDistance
	} from '@/plugins/utils';
	export default {
		computed: {
			...mapState(['userInfo', 'locateInformation'])
		},
		data() {
			return {
				mShow: false,
				mTitle: '温馨提示',
				mContent: '',
				mConfirmText: "确认",
				mType: 1,
				showcancel: true,
				callIntegral: 1,
				agreeState: 0,
				showAgree: false,
				createType: 1, //订单提交方式：1-拨号提交，2-立即提交

				deliveryFlag: false, //
				isUser: true,

				id: '',
				src: 'https://cdn.uviewui.com/uview/album/1.jpg',
				content: "",
				post: {
					latitude: 0,
					longitude: 0,
				},
				covers: [{
					id: 1,
					iconPath: "../../static/position.png",
					latitude: 0,
					longitude: 0,
					width: '36px',
					height: '36px',
				}, ],

				options: [
					// {
					// 	icon: 'home',
					// 	text: '首页',
					// 	color: '#646566'
					// },
					{
						icon: 'redo',
						text: '分享',
						color: '#646566'
					},
					{
						icon: 'star', //star-filled
						text: '收藏',
						color: '#646566'
					}
				],

				customButtonGroup: [{
					text: '打老板电话',
					backgroundColor: 'linear-gradient(90deg, #007aff, #007aff)',
					color: '#fff',
					iconClass: 'yzb yzb-hujiaobohao',
					fontSize: '16px'
				}],

				customButtonGroup2: [{
					text: '我要报名',
					backgroundColor: 'linear-gradient(90deg, #007aff, #007aff)',
					color: '#fff',
					iconClass: 'yzb yzb-bianji1',
					fontSize: '16px'
				}]
			}
		},

		onLoad(options) {
			this.id = options.id;
			if (this.userInfo.token && this.userInfo.memberRole == 'company') {
				this.isUser = false;
			} else {
				this.isUser = true;
			}
			if (this.userInfo.token) {
				uni.$u.mpShare.path = '/pages/job/postDetail?id=' + this.id + "&referrer=" + this.userInfo.id;
			} else {
				uni.$u.mpShare.path = '/pages/job/postDetail?id=' + this.id;
			}
			this.init();
			saveReferrer(options);
		},

		filters: {
			// 招工状态：1-待审核，2-招工中，3-发布失败，4-已停招，5-已取消，6-已招满
			formatStatus(val) {
				let str = ''
				if (val == '1') {
					str = '待审核'
				} else if (val == '2') {
					str = '招工中'
				} else if (val == '3') {
					str = '发布失败'
				} else if (val == '4') {
					str = '已停招'
				} else if (val == '5') {
					str = '已取消'
				} else if (val == '6') {
					str = '已招满'
				}
				return str;
			}
		},

		onShow() {
			this.getPostDetail();
		},

		methods: {
			...mapMutations(['setUserInfo']),

			async init() {
				let res = await this.$apis.getBaseConfig({
					params: {
						code: 'jf_call'
					},
				});
				if (res) {
					this.callIntegral = res.configValue;
				}
				let res2 = await this.$apis.getBaseConfig({
					params: {
						code: 'create_type'
					},
				});
				if (res2) {
					this.createType = Number(res2.configValue);
					console.log("createType===", this.createType);
				}
			},

			phoneHiden(val) {
				return phoneHiden(val);
			},

			commonDistance(lat, lng) {
				if (!this.locateInformation.location) {
					console.log("暂无定位信息")
					return 0
				} else {
					return commonDistance(parseFloat(lat), parseFloat(lng), this.locateInformation.location.lat, this
						.locateInformation.location.lng)
				}
			},

			getPostDetail() {
				let params = {
					id: this.id
				}
				if (this.userInfo.token) {
					params.userId = this.userInfo.id;
				}
				this.$apis
					.getPostDetail({
						params: params
					})
					.then(res => {
						console.log('getPostDetail', res);
						if (res) {
							this.post = res;
							this.covers[0].latitude = res.latitude;
							this.covers[0].longitude = res.longitude;
							this.post.typeNames = this.post.typeNames.split('、');
							this.updateCollectViewState(res.ifCollected);
							this.updateBtnStatus(res.ifApply);
							if (this.post.ifShowAgree && this.isUser) {
								this.showAgree = true;
							}
						}
					})
					.catch(err => {
						console.log(err, 'catch');
					});
			},


			call() {
				// 判断是否登录
				this.judgeLogin(() => {
					//判断是否自己的
					if (this.post.userId == this.userInfo.id) {
						uni.$u.toast('无法拨打自己电话');
						return;
					}
					//判断是否已沟通过
					if (this.post.ifCalled) {
						this.mShow = true;
						this.showcancel = true;
						this.mType = 4;
						this.mTitle = "温馨提示";
						this.mContent = "已联系过，可直接拨打号码~";
						this.mConfirmText = "立即拨打"
						//直接拨号
						// this.addCallRecord(true);
						return;
					}
					if (this.callIntegral > 0) {
						//判断积分是否足够（须 >= 所需积分）
						if (Number(this.userInfo.integral) >= Number(this.callIntegral)) {
							this.mShow = true;
							this.showcancel = false;
							this.mType = 2;
							this.mTitle = "温馨提示";
							this.mContent = "";
							this.mConfirmText = "知道了"
						} else {
							this.mType = 1;
							this.mShow = true;
							this.showcancel = true;
							this.mTitle = "您当前积分不足";
							this.mContent = "需要<strong style='color:red;padding:0 4px'>" + this.callIntegral +
								"</strong>积分联系对方，是否前往获取积分";
							this.mConfirmText = "获取积分"
						}
					}
				})
			},

			apply() {
				// 判断是否登录
				this.judgeLogin(() => {
					//判断是否自己的
					if (this.post.userId == this.userInfo.id) {
						uni.$u.toast('无法报名自己发布的职位');
						return;
					}
					//判断是否申请
					if (this.post.ifApply) {
						this.mShow = true;
						this.showcancel = false;
						this.mType = 4;
						this.mTitle = "温馨提示";
						this.mContent = "已经提交过，请勿重复报名~";
						this.mConfirmText = "知道了"
						//直接拨号
						// this.addCallRecord(true);
						return;
					}
					if (this.callIntegral > 0) {
						//判断积分是否足够（须 >= 所需积分）
						if (Number(this.userInfo.integral) >= Number(this.callIntegral)) {
							this.mShow = true;
							this.showcancel = false;
							this.mType = 2;
							this.mTitle = "温馨提示";
							this.mContent = "";
							this.mConfirmText = "知道了"
						} else {
							this.mType = 1;
							this.mShow = true;
							this.showcancel = true;
							this.mTitle = "您当前积分不足";
							this.mContent = "需要<strong style='color:red;padding:0 4px'>" + this.callIntegral +
								"</strong>积分报名，是否前往获取积分";
							this.mConfirmText = "获取积分"
						}
					}
				})
			},

			mConfirm(e) {
				console.log("confirm===", e);
				this.mShow = false;
				if (this.createType == 1) {
					this.callConfirm();
				} else {
					this.applyConfirm();
				}
			},


			callConfirm() {
				if (this.mType == 1) { //积分
					uni.$u.route('/pages/integral/integral');
				} else if (this.mType == 2) { //知道了
					this.mShow = true;
					this.showcancel = true;
					this.mType = 3;
					this.mTitle = "与老板沟通";
					this.mContent =
						"拨打消耗<strong style='color:red;padding:0 4px'>" + this.callIntegral +
						"</strong>积分，当前<strong style='color:red;padding:0 4px'>" +
						this.userInfo.integral + "</strong>积分可用";
					this.mConfirmText = "立即拨打"
				} else if (this.mType == 3) {
					//扣除积分，新增拨号记录
					this.addCallRecord(false);
				} else if (this.mType == 4) {
					//不扣除积分，新增拨号记录
					this.addCallRecord(true);
				}
			},

			applyConfirm() {
				if (this.mType == 1) { //积分
					uni.$u.route('/pages/integral/integral');
				} else if (this.mType == 2) { //知道了
					this.mShow = true;
					this.showcancel = true;
					this.mType = 3;
					this.mTitle = "我要报名";
					this.mContent =
						"报名消耗<strong style='color:red;padding:0 4px'>" + this.callIntegral +
						"</strong>积分，当前<strong style='color:red;padding:0 4px'>" +
						this.userInfo.integral + "</strong>积分可用";
					this.mConfirmText = "立即报名"
				} else if (this.mType == 3) {
					//扣除积分，新增拨号记录
					this.addApply();
				} else if (this.mType == 4) {
					//不扣除积分，新增拨号记录
					// this.addApply(true);
				}
			},

			//添加报名
			async addApply() {
				let param = {
					id: this.post.id,
					integral:this.callIntegral,
				};
				let res = await this.$apis.addApply(param);
				console.log("res=addApply====================", res);
				if (res) {
					this.doScribe();
					uni.showModal({
						title: '操作成功',
						content: '请前往【我的订单】查看',
						confirmText: "我的订单",
						cancelText: "取消",
						success: (data) => {
							if (data.confirm) {
								uni.switchTab({
									url: '/pages/order/list'
								})
							}
						}
					})
				}
			},

			//添加拨号记录
			async addCallRecord(ifFree) {
				let params = {
					postId: this.post.id,
					postUserId: this.post.userId,
					roleCode: 'member',
					ifFree: ifFree
				}
				let res = await this.$apis.addContact(params);
				console.log("addCallRecord", res);
				if (res) {
					uni.makePhoneCall({
						phoneNumber: this.post.phone,
						success: () => {
							console.log('成功拨打电话');
						},
						fail: (err) => {
							console.log(err);
						},
					});
					//更新用户信息
					this.getUserInfo();
				}
			},

			getUserInfo() {
				this.$apis
					.getUserInfo()
					.then(res => {
						console.log('getUserInfo', res);
						if (res) {
							this.setUserInfo(res);
						}
					})
					.catch(err => {
						console.log(err, 'catch');
					});
			},

			/**
			 * 更新收藏状态
			 */
			updateCollectViewState(collected) {
				if (collected) {
					this.options[1].icon = 'star-filled';
					this.options[1].color = '#007aff';
				} else {
					this.options[1].icon = 'star';
					this.options[1].color = '#646566';
				}
			},
			
			/**
			 * 更新收藏状态
			 */
			updateBtnStatus(ifApply) {
				if (!ifApply) {
					this.customButtonGroup2[0].color = '#fff';
					this.customButtonGroup2[0].backgroundColor = '#007aff';
				} else {
					this.customButtonGroup2[0].color = '#fff';
					this.customButtonGroup2[0].backgroundColor = '#999';
				}
			},


			/**
			 * 职位收藏
			 */
			async doCollect() {
				let param = {
					roleCode: 'member',
					dataId: this.post.id,
				};
				let res = await this.$apis.updateCollect(param);
				console.log("res=updateCollect====================", res);
				if (res) {
					this.post.ifCollected = !this.post.ifCollected;
					this.updateCollectViewState(this.post.ifCollected)
					uni.showToast({
						icon: 'none',
						title: '操作成功'
					});
				}
			},

			//提交合作意向确认
			async submitAgree() {
				let param = {
					agreeState: this.agreeState,
					id: this.post.contactId,
				};
				let res = await this.$apis.updateAgreeState(param);
				console.log("res=updateAgreeState====================", res);
				if (res) {
					this.post.ifShowAgree = false;
					this.showAgree = false;
					this.doScribe();
					if (this.agreeState == 1) {
						uni.showModal({
							title: '操作成功',
							content: '请前往【我的订单】查看',
							confirmText: "我的订单",
							cancelText: "取消",
							success: (data) => {
								if (data.confirm) {
									uni.switchTab({
										url: '/pages/order/list'
									})
								}
							}
						})
					} else {
						uni.showToast({
							icon: 'none',
							title: '操作成功'
						});
					}
				}
			},

			doScribe() {
				requestSubscribe(2,
					res => {
						console.log("订阅成功：", res)
						console.log("res：", res)
						if (res == true) {
							uni.$u.toast('订阅成功');
						} else {
							uni.$u.toast('订阅失败');
						}
					}, err => {
						console.log("订阅失败：", err)
						uni.$u.toast('订阅失败');
					});
			},


			onClick(e) {
				console.log(e);
				if (e.index == 0) {
					//分享
					this.deliveryFlag = true;
				} else if (e.index == 1) {
					//收藏
					this.judgeLogin(() => {
						this.doCollect();
					})
				}
			},

			buttonClick(e) {
				console.log(e);
				let that = this;
				if (e.index == 0) {
					if (this.createType == 1) {
						this.call();
					} else {
						this.apply();
					}
				} else {

				}
			},

			toMap() {
				var that = this;
				uni.openLocation({
					latitude: parseFloat(that.post.latitude),
					longitude: parseFloat(that.post.longitude),
					success: function() {
						console.log('success');
					}
				});
			},

			toSafe() {
				uni.$u.route("/pages/job/safe");
			},


			toPostList() {
				uni.$u.route("/pages/job/postList");
			},

			toReport() {
				uni.$u.route("/pages/job/report?id=1");
			},

			toComments() {
				uni.$u.route("/pages/job/comments?roleCode='member'&&userId=" + this.post.userId);
			},
		}
	}
</script>

<style lang="scss">
	page {
		background-color: #f5f5f5;
	}

	.detail {
		// padding: 20upx;
		padding-bottom: calc(150upx + constant(safe-area-inset-bottom));
		padding-bottom: calc(150upx + env(safe-area-inset-bottom));
	}

	.boss-info {
		background-color: #fff;
		padding: 20upx;
		margin: 20rpx 20rpx 0 20rpx;
		box-shadow: 0 10rpx #f5f5f5;
		border-radius: 15rpx;
		display: flex;
		flex-direction: row;
		justify-content: space-between;
		align-items: center;
		// border-radius: 20upx;

		.boss {
			margin-left: 20upx;

			.boss-top {
				display: flex;
				flex-direction: row;
				align-items: center;
				margin-bottom: 10upx;
			}

			.boss-name {
				font-weight: bold;
				font-size: 32rpx;
				margin-right: 15upx;
			}

			.boss-score {
				font-size: 28rpx;
				color: #dd524d;
				font-weight: bold;
			}

			.score-next {
				color: #dd524d;
				font-size: 28rpx;
			}

			.boss-phone {
				font-size: 28rpx;
				color: #666;
				margin-right: 20upx;
			}
		}

		.call {
			align-items: center;

			.call-icon {
				font-size: 48rpx;
				color: $main-color;
			}

			.call-name {
				font-size: 24rpx;
				color: #666;
				margin-top: 10upx;
			}

		}
	}

	.card-module {
		margin: 20rpx 20rpx 0rpx 20rpx;
		padding: 20rpx;
		background-color: #fff;
		box-shadow: 0 0 10rpx #f5f5f5;
		border-radius: 15rpx;
		display: flex;
		flex-direction: column;
	}

	.card-title {
		font-weight: bold;
		font-size: 32rpx;
		padding: 10rpx 0 30rpx 0;
	}

	.title {
		font-weight: bold;
		font-size: 36rpx;
	}

	.price-time {
		padding: 20upx 0;
		display: flex;
		flex-direction: row;
		align-items: center;
		justify-content: space-between;
	}

	.price {
		.price-num {
			font-size: 48rpx;
			color: red;
			font-weight: bold;
		}

		.price-unit {
			font-size: 28rpx;
			color: red;
			margin-left: 6upx;
		}
	}

	.desc {
		margin-top: 30upx;

		text {
			line-height: 1.8;
		}

		.desc-content {
			color: #333;
			font-size: 30rpx;
			white-space: pre-wrap;
		}
	}

	.times {
		margin-top: 30upx;
		font-size: 28rpx;
		color: #666;
	}

	.time-ip {
		margin-top: 10upx;

		text {
			font-size: 24rpx;
			color: #999;
		}

		.ip {
			margin-left: 30upx;
		}
	}

	.city-info {
		display: flex;
		flex-direction: row;
		align-items: center;

		.city-icon {
			color: #666;
			font-size: 24rpx;
			margin-right: 15upx;
		}

		.city-name {
			font-size: 24rpx;
			color: #666;
		}
	}

	.location {
		position: relative;
		margin-top: 20upx;

		.cover {
			top: 0;
			width: 100%;
			height: 200rpx;
			position: absolute;
		}

		.address {
			margin: 0 auto;
			bottom: -30upx;
			width: 100%;
			box-sizing: border-box;
			position: absolute;

			.address-item {
				.left {
					display: flex;
					flex-direction: row;
					align-items: center;
				}

				padding: 20upx;
				border-radius: 20upx;
				display: flex;
				flex-direction: row;
				align-items: center;
				justify-content: space-between;
				background-color: #fff;
				box-shadow: 0 0 20upx rgba(0, 0, 0, 0.15);
				margin: 0 10upx;

				text {
					color: #333;
					font-size: 28rpx;
				}

				.address-name {
					margin-left: 10upx;
					text-overflow: -o-ellipsis-lastline;
					overflow: hidden;
					text-overflow: ellipsis;
					display: -webkit-box;
					-webkit-line-clamp: 1;
					-webkit-box-orient: vertical;
				}

				.address-distance {}
			}
		}
	}

	.post-info {
		background-color: #fff;
		padding: 20upx 20upx 60upx 20upx;
		margin: 20rpx 20rpx 0rpx 20rpx;
		display: flex;
		flex-direction: column;
		border-radius: 15rpx;
		box-shadow: 0 10rpx #f5f5f5;

	}

	.tags {
		display: flex;
		flex-direction: row;
		flex-wrap: wrap;
		align-items: center;

		.tags-item {
			margin-right: 10upx;
		}
	}

	.safe-tips {
		background-color: #fff;
		padding: 20upx;
		margin: 20rpx 20rpx 0rpx 20rpx;
		border-radius: 15rpx;
		box-shadow: 0 10rpx #f5f5f5;

		.tips-top {
			display: flex;
			flex-direction: row;
			align-items: center;
			justify-content: space-between;
			padding: 0 0 20upx 0;

			.tip-icon {
				color: $main-color;
				font-size: 32upx;
			}

			.tip-title {
				margin-left: 15upx;
				font-size: 32upx;
				font-weight: bold;
				color: #000;
			}

			.tip-detail {
				font-size: 26upx;
				color: #999;
			}

			.yzb-next {
				color: #999;
			}
		}

		.tips {
			.tip-info {
				line-height: 1.8;
				color: #666;
				font-size: 28upx;
			}

			.tip-report {
				color: $main-color;
				margin-left: 10upx;
			}
		}
	}

	.more {
		padding: 20upx;
		background-color: #fff;
		text-align: center;
		color: $main-color;
		margin: 20rpx 20rpx 0rpx 20rpx;
		border-radius: 15rpx;
		box-shadow: 0 10rpx #f5f5f5;
	}

	.bottom {
		position: fixed;
		bottom: 0;

		width: 100%;
		padding: 10upx 0;
		/* 全面屏底部安全区 */
		padding-bottom: calc(10upx + constant(safe-area-inset-bottom));
		padding-bottom: calc(10upx + env(safe-area-inset-bottom));
		background-color: #fff;
		border-top: 1upx solid #eee;
	}

	.row-align {
		display: flex;
		flex-direction: row;
		align-items: center;
	}

	.column-align {
		display: flex;
		flex-direction: column;
	}

	.slot-content {
		padding: 20upx 0;

		.m-content {
			font-size: 30rpx;
			color: #666;
		}

		.m-title {
			font-size: 32rpx;
			color: #000;
			margin-left: 4upx;
		}

		.m-desc {
			font-size: 28rpx;
			color: #666;
			padding: 20upx 0;
			line-height: 1.6;
		}
	}

	.share-pro {
		z-index: 1000;
		display: flex;
		line-height: 1;
		box-sizing: border-box;
		align-items: center;
		justify-content: flex-end;
		flex-direction: column;

		.share-pro-mask {
			position: fixed;
			top: 0;
			right: 0;
			bottom: 0;
			left: 0;
			width: 100%;
			height: 100%;
			background: rgba(0, 0, 0, 0.5);
		}

		.share-pro-dialog {
			position: relative;
			position: fixed;
			bottom: 0;
			/* 全面屏底部安全区 */
			padding-bottom: constant(safe-area-inset-bottom);
			padding-bottom: env(safe-area-inset-bottom);

			width: 750rpx;
			min-height: 310rpx;
			height: auto;
			overflow: hidden;
			background-color: #fff;
			border-radius: 24rpx 24rpx 0 0;
			box-sizing: border-box;
			display: flex;
			flex-direction: column;

			.yzb-shanchu {
				font-size: 40upx;
			}

			.close-btn {
				position: absolute;
				top: 0rpx;
				right: 29rpx;
				padding: 20rpx 15rpx;
			}

			.share-pro-title {
				padding: 28rpx 41rpx;
				font-size: 28rpx;
				color: #1c1c1c;
				background-color: #f7f7f7;
			}

			.share-pro-body {
				display: flex;
				font-size: 28rpx;
				height: 100%;
				color: #1c1c1c;
				flex-direction: row;
				justify-content: space-around;

				.share-item {
					display: flex;
					flex-direction: column;
					justify-content: center;
					justify-content: center;
					align-items: center;

					.share-title {
						font-size: 30rpx;
						color: #333;
					}

					.btn-share {
						background: #ffff;
						border: #ffffff;
						display: flex;
						flex-direction: column;
						align-items: center;
					}

					.share-icon {
						// margin-top: 30rpx;
						// margin-bottom: 16rpx;
						font-size: 70rpx;
						color: #42ae3c;
						text-align: center;
					}

					&:nth-child(2) {
						.share-icon {
							color: #ff5f33;
						}
					}

					.yzb {
						font-size: 60upx;
						line-height: 90upx;
					}

					.yzb-weixinhaoyou {
						color: $uni-color-success;
					}

					.yzb-Photo-share {
						color: $uni-color-error;
					}
				}
			}
		}

		/* 显示或关闭内容时动画 */

		.open {
			transform: translateY(0);
			transition: all 0.3s ease-out;
		}

		.close {
			transform: translateY(310rpx);
			transition: all 0.3s ease-out;
		}
	}

	.y-ensure {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		background-color: #fff;
		margin: 20upx;
		border-radius: 20upx;
		padding: 30upx 20upx;

		.title {
			font-weight: bold;
			font-size: 36rpx;
			color: #333;
		}

		.desc {
			font-size: 28rpx;
			color: #666;
			padding: 20upx 0;
		}

		.ensure {
			display: flex;
			flex-direction: row;
			align-items: center;
			width: 100%;
			margin-top: 20upx;

			.ensure-item {
				display: flex;
				flex-direction: row;
				align-items: center;
				justify-content: center;
				flex: 1;
			}

			.ensure-icon-bg {
				z-index: 2;
				width: 90upx;
				height: 90upx;
				border-radius: 45upx;
				display: flex;
				flex-direction: row;
				align-items: center;
				justify-content: center;
			}

			.ensure-icon-bg-selected {
				background-color: #007aff;
			}

			.ensure-icon-bg-unSelected {
				background-color: #f5f6fa;
			}

			.ensure-icon-selected {
				font-size: 32rpx;
				color: #fff;
			}

			.ensure-icon {
				font-size: 32rpx;
				color: #666;
			}

			.ensure-name {
				font-size: 36rpx;
				color: #333;
				background-color: #f5f6fa;
				height: 64upx;
				line-height: 64upx;
				padding: 0 40upx;
				text-align: center;
				border-top-right-radius: 32upx;
				border-bottom-right-radius: 32upx;
				margin-left: -20upx;
			}

			.ensure-name-selected {
				font-size: 36rpx;
				color: #007aff;
				background-color: #E5F4FF;
				height: 64upx;
				line-height: 64upx;
				padding: 0 40upx;
				border-top-right-radius: 32upx;
				border-bottom-right-radius: 32upx;
				margin-left: -20upx;
				font-weight: bold;
			}
		}

		.tips {
			font-size: 28rpx;
			color: #dd524d;
			padding: 30upx;
			line-height: 1.8;
		}

		.submit {
			width: 94%;
			margin-top: 40upx;
		}
	}
</style>