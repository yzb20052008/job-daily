<template>
	<view class="page">
		<public-module></public-module>
		<f-navbar bgColor="#007aff" :navbarType="5" fontColor="#fff" transparentTitleColor="#000" :isShowLeft="true"
			:scrollTop="scrollTop" :title="resumeInfo.name" :isShowTransparentTitle="false">
		</f-navbar>
		<view class="none"  v-if="hasResume==false">
			<u-empty mode="data" icon="https://img.qinkonglan.cn/imgs/data.jpg" text="无简历">
			</u-empty>
		</view>
		<view class="resume" v-else>
			<view class="worker">
				<view class="base-info space-between">
					<view class="base-info space-between">
						<view class="row">
							<u-avatar :src="resumeInfo.avatar" size="50" shape="square"></u-avatar>
							<view class="worker-info">
								<view class="worker-name">
									<text class="name">{{resumeInfo.name}}</text>
									<view class="tag" v-if="resumeInfo.ifRealName">
										<u-tag text="已实名" size="mini" color="#007aff" borderColor="#E5F4FF"
											bgColor="#E5F4FF"></u-tag>
									</view>
								</view>
								<view class="age-sex">
									<text v-if="resumeInfo.sex">{{resumeInfo.sex|formatSex}}</text>
									<view class="dot-grey" v-if="resumeInfo.birthday"></view>
									<text class="age"
										v-if="resumeInfo.birthday">{{calCurrentYear(resumeInfo.birthday)}}岁</text>
									<view class="dot-grey"></view>
									<text class="age">{{resumeInfo.workYear}}年工龄</text>
									<!-- <view class="dot-grey"></view>
									<text class="age">汉族</text> -->
								</view>
							</view>
						</view>
						<!-- <text class="yzb yzb-next"></text> -->
					</view>
				</view>
				<view class="tags">
					<view class="tags-item" v-for="(item,index) in toList(resumeInfo.skills,',')" :key="index">
						<u-tag :text="item" color="#007aff" borderColor="#E5F4FF" bgColor="#E5F4FF"></u-tag>
					</view>
					<view class="tags-item" v-if="resumeInfo.skilled">
						<u-tag :text="resumeInfo.skilled" color="#007aff" borderColor="#E5F4FF"
							bgColor="#E5F4FF"></u-tag>
					</view>
				</view>
				<view class="user-other-info">
					<text class="other-title">{{resumeInfo.addressName}}</text>
					<text class="other-info">距你 {{commonDistance(resumeInfo.addressLat,resumeInfo.addressLng)}}</text>
				</view>
			</view>
			<view class="job-status">
				<text class="status-title">当前状态</text>
				<view>
					<text class="status-info">{{resumeInfo.jobStatus|formatStatus}}</text>
				</view>
			</view>
			<view class="expect">
				<view class="item-top space-between">
					<text class="item-title">找工意向</text>
				</view>
				<view class="tags bottom-line" v-if="resumeInfo.intention">
					<view class="tags-item" v-for="(item,index) in toList(resumeInfo.intention.typeNames,'、')"
						:key="index">
						<u-tag :text="item" color="#666" borderColor="#f5f6fa" bgColor="#f5f6fa"></u-tag>
					</view>
				</view>
				<view class="tags">
					<view class="tags-item" v-if="resumeInfo.intention.salaryUnit">
						<u-tag :text="resumeInfo.intention.expectSalary+resumeInfo.intention.salaryUnit" color="#007aff"
							borderColor="#E5F4FF" bgColor="#E5F4FF"></u-tag>
					</view>
					<view class="tags-item" v-if="resumeInfo.intention.settlementType">
						<u-tag :text="resumeInfo.intention.settlementType" color="#007aff" borderColor="#E5F4FF"
							bgColor="#E5F4FF"></u-tag>
					</view>
					<view class="tags-item" v-if="resumeInfo.intention.workType">
						<u-tag :text="resumeInfo.intention.workType" color="#007aff" borderColor="#E5F4FF"
							bgColor="#E5F4FF"></u-tag>
					</view>
					<view class="tags-item" v-if="resumeInfo.intention.employMethod">
						<u-tag :text="resumeInfo.intention.employMethod" color="#007aff" borderColor="#E5F4FF"
							bgColor="#E5F4FF"></u-tag>
					</view>
				</view>
				<view class="city">
					<text class="city-title">期望工作地：</text>
					<text class="city-info">{{resumeInfo.intention.workCity}}</text>
				</view>
			</view>
			<view class="introduce column">
				<view class="item-top space-between">
					<text class="item-title">自我介绍</text>
				</view>
				<text class="desc">{{resumeInfo.personalSkill||"无"}}</text>
			</view>

			<view class="project column">
				<view class="item-top space-between">
					<text class="item-title">项目经验</text>
				</view>
				<view class="project-item" :class="index>0?'top-line':''" v-for="(item,index) in resumeInfo.expList"
					:key="index">
					<text class="project-detail">{{item.descr}}</text>
				</view>
			</view>
			<view class="skill column">
				<view class="item-top space-between">
					<text class="item-title">技能证书</text>
				</view>
				<view class="skill-item" :class="index>0?'top-line':''" v-for="(item,index) in resumeInfo.certList"
					:key="index">
					<view class="column">
						<text class="skill-name">{{item.certName}}</text>
						<u-avatar :src="item.certImg" size="50" shape="square"></u-avatar>
					</view>
				</view>
			</view>
		</view>
		<view class="bottom" v-if="hasResume">
			<uni-goods-nav :fill="true" :options="options" :button-group="customButtonGroup" @click="onClick"
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
	</view>
</template>

<script>
	import {
		mapState,
		mapMutations
	} from 'vuex';
	import {
		calCurrentYear,
		saveReferrer
	} from '@/config/common';
	import {
		commonDistance
	} from '@/plugins/utils';
	export default {
		computed: {
			...mapState(['userInfo', 'locateInformation', ])
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
				deliveryFlag: false, //
				hasResume:false,

				userId: '',
				distance: '',
				resumeInfo: {},
				scrollTop: 0,
				data: [{}, {}],

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
					text: '联系工人',
					backgroundColor: 'linear-gradient(90deg, #007aff, #007aff)',
					color: '#fff',
					iconClass: 'yzb yzb-hujiaobohao',
					fontSize: '16px'
				}, ],
			}
		},

		filters: {
			formatStatus(val) {
				if (val == 1) {
					return "正在找工作"
				} else if (val == 2) {
					return "暂不找工作"
				}
			},

			formatSex(val) {
				if (val == 1) {
					return "男"
				} else if (val == 2) {
					return "女"
				}
			},

			formatDistance(distance) {
				var strDistance = '';
				if (distance < 1) { //一公里以内的
					distance = distance.toFixed(3);
					strDistance = distance * 1000 + " m";
				} else {
					if (distance > 10000) {
						distance = 0;
					}
					distance = distance.toFixed(2);
					strDistance = distance + " km";
				}
				return strDistance;
			}
		},

		onPageScroll(e) {
			this.scrollTop = e.scrollTop;
		},

		onLoad(options) {
			this.userId = options.userId;
			this.getResumeInfo();
			this.init();
			saveReferrer(options);
		},

		methods: {

			init() {
				let httpData = {
					code: 'jf_call'
				};
				this.$apis
					.getBaseConfig({
						params: httpData,
						custom: {
							isFactory: true
						}
					})
					.then(res => {
						console.log('res====', res);
						if (res) {
							this.callIntegral = res.configValue;
						}
					})
					.catch(err => {
						console.log(err, 'catch');
					});
			},

			call() {
				// 判断是否登录
				this.judgeLogin(() => {
					//判断积分是否足够
					if (this.userInfo.integral > 0) {
						// this.mShow = true;
						// this.showcancel = false;
						// this.mType = 2;
						// this.mTitle = "温馨提示";
						// this.mContent = "";
						// this.mConfirmText = "知道了"
						this.mShow = true;
						this.showcancel = false;
						this.mType = 3;
						this.mTitle = "与工人沟通";
						this.mContent =
							"拨打消耗<strong style='color:red;padding:0 4px'>" + this.callIntegral +
							"</strong>积分，当前<strong style='color:red;padding:0 4px'>" +
							this.userInfo.integral + "</strong>积分可用";
						this.mConfirmText = "立即拨打"
					} else {
						this.mType = 1;
						this.mShow = true;
						this.showcancel = true;
						this.mTitle = "您当前积分不足";
						this.mContent = "需要<strong style='color:red;padding:0 4px'>" + this.callIntegral +
							"</strong>积分联系对方，是否前往获取积分";
						this.mConfirmText = "获取积分"
					}
				})
			},

			mConfirm(e) {
				console.log("confirm===", e);
				this.mShow = false;
				if (this.mType == 1) { //积分
					uni.$u.route('/pages/integral/integral');
				} else if (this.mType == 2) { //知道了
					this.mShow = true;
					this.showcancel = false;
					this.mType = 3;
					this.mTitle = "与工人沟通";
					this.mContent =
						"拨打消耗<strong style='color:red;padding:0 4px'>" + this.callIntegral +
						"</strong>积分，当前<strong style='color:red;padding:0 4px'>" +
						this.userInfo.integral + "</strong>积分可用";
					this.mConfirmText = "立即拨打"
				} else if (this.mType == 3) {
					//扣除积分，新增拨号记录
					this.addCallRecord();
				}
			},

			//添加拨号记录
			async addCallRecord() {
				let params = {
					userId: this.resumeInfo.userId, //工人id
					roleCode: 'company'
				}
				let res = await this.$apis.addContact(params);
				console.log("addCallRecord", res);
				if (res) {
					uni.makePhoneCall({
						phoneNumber: this.resumeInfo.phone,
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
			 * 职位收藏
			 */
			async doCollect() {
				let param = {
					roleCode: 'company',
					dataId: this.resumeInfo.id,
				};
				let res = await this.$apis.updateCollect(param);
				console.log("res=updateCollect====================", res);
				if (res) {
					this.resumeInfo.ifCollected = !this.resumeInfo.ifCollected;
					this.updateCollectViewState(this.resumeInfo.ifCollected)
					uni.showToast({
						icon: 'none',
						title: '操作成功'
					});
				}
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

			calCurrentYear(val) {
				return calCurrentYear(val)
			},

			toList(val, filter) {
				if (val) {
					return val.split(filter);
				}
				return [];
			},

			async getResumeInfo() {
				let res = await this.$apis.getResumeInfo({
					params: {
						userId: this.userId
					}
				});
				console.log('res=====', res);
				if (res) {
					this.resumeInfo = res;
					this.hasResume = true;
					this.updateCollectViewState(res.ifCollected);
				}
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
					this.call();
				} else {}
			},

			addResume() {
				uni.$u.route("/pages/resume/addResume1");
			},

			toBaseInfo() {
				uni.$u.route("/pages/resume/resumeBase");
			},

			toIntroduce() {
				uni.$u.route("/pages/resume/resumeIntroduce");
			},

			toExpect() {
				uni.$u.route("/pages/resume/resumeExpect");
			},

			toProject() {
				uni.$u.route("/pages/resume/resumeProject");
			},

			toSkill() {
				uni.$u.route("/pages/resume/resumeSkill");
			},
		}
	}
</script>

<style lang="scss">
	page {
		background-color: #f5f5f5;
	}
	
	.page {
		background-image: linear-gradient(#007aff, #DEEFFA 40%, #f5f5f5);
		padding-top: 180upx;
		padding-bottom: calc(150upx + constant(safe-area-inset-bottom));
		padding-bottom: calc(150upx + env(safe-area-inset-bottom));
	}

	.none {
		padding-top: 100upx;

		.none-info {
			display: flex;
			flex-direction: column;
			align-items: center;
			justify-content: center;

			.none-title {
				font-size: 18px;
				font-weight: bold;
			}

			.none-tip {
				margin-top: 10upx;
				font-size: 14px;
				color: #888;
			}

			.none-btn {
				width: 50%;
				margin-top: 50upx;
			}
		}
	}

	.resume {
		padding: 20upx;

		.space-between {
			display: flex;
			flex-direction: row;
			align-items: center;
			justify-content: space-between;
		}

		.row {
			display: flex;
			flex-direction: row;
			align-items: center;
		}

		.column {
			display: flex;
			flex-direction: column;
		}
	}

	.resume-percent {
		background-color: #fff;
		padding: 30upx;
		border-radius: 20upx;

		.percent-title {
			font-size: 18px;
			font-weight: bold;
			margin-bottom: 15upx;
		}

		.percent-more {
			color: $main-color;
			font-size: 14px;
		}

		.yzb-next {
			color: $main-color;
		}

		.percent-tip {
			font-size: 12px;
			color: #666;
			margin-top: 15upx;
		}
	}

	.worker {
		display: flex;
		flex-direction: column;
		background-color: #fff;
		border-radius: 20upx;
		padding: 30upx;
		margin-top: 30upx;

		.base-info {
			width: 100%;
			// border-bottom: 1upx solid #eee;
			padding-bottom: 10upx;
		}

		.worker-left {
			display: flex;
			flex-direction: row;
			align-items: center;
		}

		.worker-right {
			display: flex;
			flex-direction: row;
			align-items: center;
			margin-top: -15upx;

			.dot {
				background-color: limegreen;
				width: 10upx;
				height: 10upx;
				border-radius: 5upx;
				margin-right: 10upx;
			}

			.status {
				font-size: 14px;
				color: #999;
			}
		}

		.worker-info {
			margin-left: 20upx;
		}

		.worker-name {
			display: flex;
			flex-direction: row;
			align-items: center;

			.name {
				font-size: 22px;
				font-weight: bold;
			}

			.tag {
				margin-top: 10upx;
				margin-left: 10upx;
			}
		}

		.age-sex {
			display: flex;
			flex-direction: row;
			align-items: center;
			margin-top: 5upx;

			text {
				font-size: 14px;
				color: #888;
			}

			.dot-grey {
				background-color: #999;
				width: 6upx;
				height: 6upx;
				border-radius: 3upx;
				margin: 0 10upx;
			}

			.age {}
		}

		.user-other-info {
			margin-top: 20upx;
			font-size: 16px;
			color: #888;
			display: flex;
			flex-direction: row;
			align-items: center;
			justify-content: space-between;
		}
	}

	.job-status {
		display: flex;
		flex-direction: row;
		align-items: center;
		justify-content: space-between;
		background-color: #fff;
		padding: 30upx;
		margin-top: 30upx;
		border-radius: 20upx;

		.status-title {
			font-weight: bold;
			font-size: 18px;
		}

		.status-info {
			color: $main-color;
			margin-right: 10upx;
		}
	}

	.item-title {
		font-weight: bold;
		font-size: 18px;
	}

	.item-next {
		color: $main-color;
		font-size: 18px;
		font-weight: bold;
	}

	.item-top {
		padding-bottom: 10upx;
	}

	.bottom-line {
		border-bottom: 1upx solid #eee;
	}

	.top-line {
		border-top: 1upx solid #eee;
	}

	.tags {
		display: flex;
		flex-direction: row;
		flex-wrap: wrap;
		align-items: center;
		padding: 20upx 0;

		.tags-item {
			margin-right: 15upx;
		}
	}

	.expect {
		background-color: #fff;
		margin-top: 30upx;
		border-radius: 20upx;
		padding: 30upx;

		.city {
			margin-top: 10upx;

			.city-title {
				color: #666;
			}

			.city-info {
				color: #333;
			}
		}

		.info-item {
			display: flex;
			flex-direction: row;
			align-items: center;
			padding: 10upx 0;

			.other-title {
				color: #666;
			}

			.other-info {
				color: #333;
			}
		}
	}

	.introduce {
		background-color: #fff;
		margin-top: 30upx;
		border-radius: 20upx;
		padding: 30upx;

		.desc {
			color: #333;
			margin-top: 10upx;
			line-height: 1.6;
			font-size: 15px;
		}
	}

	.project {
		background-color: #fff;
		margin-top: 30upx;
		border-radius: 20upx;
		padding: 30upx;

		.project-item {
			padding: 15upx 0;
			display: flex;
			flex-direction: row;
			align-items: center;
			justify-content: space-between;

			.project-detail {
				color: #333;
				line-height: 1.6;
				font-size: 15px;
			}

			.yzb-next {
				color: #666;
				font-size: 16px;
				margin-left: 20upx;
			}
		}
	}

	.skill {
		background-color: #fff;
		margin-top: 30upx;
		border-radius: 20upx;
		padding: 30upx;

		.skill-item {
			padding: 15upx 0;
			display: flex;
			flex-direction: row;
			align-items: center;
			justify-content: space-between;

			.skill-name {
				color: #333;
				font-size: 14px;
				margin-bottom: 10upx;
			}

			.yzb-next {
				color: #666;
				font-size: 16px;
				margin-left: 20upx;
			}
		}
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
			font-size: 15px;
			color: #666;
		}

		.m-title {
			font-size: 16px;
			color: #000;
			margin-left: 4upx;
		}

		.m-desc {
			font-size: 14px;
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
						font-size: 15px;
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
</style>