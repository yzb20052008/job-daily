<template>
	<view class="content">
		<mescroll-uni :fixed="false" height="100%" ref="mescrollRef" @init="mescrollInit" :up="upOption"
			@down="downCallback" @up="upCallback">
			<view class="account">
				<text>当前可用积分：<text class="integral">{{userInfo.integral}}</text></text>
			</view>
			<view class="goods-list">
				<view v-for="(item, index) in list" :key="index" class="goods-item">
					<view class="image-wrapper">
						<image :src="item.image" mode="aspectFill"></image>
					</view>
					<text class="title">{{ item.name }}</text>
					<text class="desc">{{item.effect}}</text>
					<view class="price-box">
						<view class="price-info">
							<text class="price">{{ item.integral }}积分</text>
							<text class="unit">/ {{item.period | formatUnit}}</text>
						</view>
						<!-- <text class="">购买</text> -->
					</view>
					<text class="buy" @click="toBuy(item)">立即购买</text>
				</view>
			</view>
		</mescroll-uni>

		<uni-popup ref="toppingPopup" background-color="#fff">
			<view class="topping-pop">
				<view class="top">
					<text class="title">岗位列表</text>
					<text class="yzb yzb-shanchu" @click="closeToppingPop"></text>
				</view>
				<scroll-view scroll-y style="height: 700upx;" v-if="positionList.length>0" class="m-position">
					<radio-group @change="toppingRadioChange">
						<view class="pop-item bottom-line" v-for="(item2, index2) in positionList" :key="index2">
							<view class="item-left">
								<view class="title space-between">
									<text :class="true ? 'left' : 'left-close'">{{ item2.postName }}</text>
								</view>
								<view class="company">
									{{ item2.expRequire }}
									<view class="height-line"></view>
									{{ item2.minEducation }}
									<view class="height-line"></view>
									{{ item2.salary }}<text v-if="item2.unit != '元/月'">{{ item2.unit }}</text>
								</view>
								<view class="desc">
									<!-- <text class="position-type" v-if="item2.positionType == 1">普通职位</text> -->
									<text class="position-type" v-if="item2.positionType == 2">灵活用工</text>
									<text class="position-type2" v-if="item2.positionType == 3">共享用工</text>
									<text class="position-type3"
										v-if="item2.shareMoney >0 ">佣金¥{{item2.shareMoney}}</text>
									<text v-for="(item3, index3) in item2.skill" :key="index3">{{ item3 }}</text>
								</view>
								<view class="company" v-if="item2.positionType==3">
									空闲{{ item2.shareNumber }}人
									<view class="height-line"></view>
									{{ formatCreateTime(item2.startDate) }}-{{ formatCreateTime(item2.endDate) }}
									<view class="height-line"></view>
									最多{{ item2.workDays }}天
								</view>
							</view>
							<view class="resume-dot">
								<radio :value="index2+''" :checked="item2.checked"></radio>
							</view>
						</view>
					</radio-group>
				</scroll-view>
				<view class="pop-none" v-else>
					暂无记录，请先创建岗位
				</view>
				<view class="pop-btn">
					<text class="cancel" @click="closeToppingPop">取消</text>
					<text class="ensure" @click="ensureTopping">确定</text>
				</view>
			</view>
		</uni-popup>
	</view>
</template>

<script>
	import {
		mapState
	} from 'vuex';
	import MescrollMixin from '@/uni_modules/mescroll-uni/components/mescroll-uni/mescroll-mixins.js';
	export default {
		mixins: [MescrollMixin],
		computed: {
			...mapState(['userInfo']),
		},
		data() {
			return {
				list: [],
				upOption: {
					onScroll: true,
					auto: false, // 不自动加载
					noMoreSize: 10, //如果列表已无数据,可设置列表的总数量要大于半页才显示无更多数据;避免列表数据过少(比如只有一条数据),显示无更多数据会不好看; 默认5
					empty: {
						tip: '空空如也', // 提示
						icon: 'https://cdn.example.com/imgs/data.jpg'
					}
				},
				query: {
					pageNum: 1,
					pageSize: 10,
				},

				applyList: [],
				positionList: [],

				selApply: null,
				selPosition: null,
				selectItem: {},

			}
		},

		onLoad() {},

		filters: {
			formatUnit(val) {
				if (val == -1) {
					return '次'
				} else if (val == 0) {
					return '永久'
				} else if (val > 0) {
					return val + "小时"
				}
			},
		},

		methods: {
			//---------------------------刷新卡-----------------------------------
			//确定购买刷新卡
			ensure() {
				let params = {
					amount: this.selectItem.integral,
					goodsId: this.selectItem.id,
					dataId: this.selApply.id //投递ID
				}
				this.createJfOrder(params);
				this.closePop();
			},

			radioChange(e) {
				console.log("radioChange==", e.detail.value);
				this.selApply = this.applyList[e.detail.value];
				console.log("radioChange==", this.selApply);
			},

			closePop() {
				this.$refs.popup.close();
			},

			getApplyResponseList() {
				let params = {
					page: 1,
					limit: 100,
					type: 0,
					applyStatus: 1,
				}
				this.$apis.getApplyResponseList(params).then(res => {
					console.log('getApplyResponseList', res);
					if (res) {
						let data = res.records;
						this.applyList = data;
						this.$refs.popup.open("bottom");
					}
				});
			},

			//---------------------置顶卡---------------------------------
			//查询已发布的岗位
			async getPositionList() {
				let params = {
					limit: 100,
					page: 1,
					status: 2,
					companyId: this.userInfo.companyId
				}
				let data = await this.$apis.getCompanyPositionList(params);
				let list = data.records;
				for (let i = 0; i < list.length; i++) {
					if (list[i].skill) {
						list[i].skill = list[i].skill.split(',');
					}
				}
				this.positionList = list;
				this.showToppingDialog();
				console.log(this.list);
			},

			showToppingDialog() {
				this.$refs.toppingPopup.open("bottom");
			},

			toppingRadioChange(e) {
				console.log("==toppingRadioChange==", e.detail.value);
				this.selPosition = this.positionList[e.detail.value];
			},

			closeToppingPop() {
				this.$refs.toppingPopup.close();
			},

			//确定购买置顶卡
			ensureTopping() {
				let params = {
					amount: this.selectItem.integral,
					goodsId: this.selectItem.id,
					dataId: this.selPosition.id //职位ID
				}
				this.createJfOrder(params);
				this.closeToppingPop();
			},


			toBuy(item) {
				uni.showToast({
					icon: 'none',
					title: "开发中"
				})
				return;
				let that = this;
				let params = {
					amount: item.integral,
					goodsId: item.id,
				}
				if (item.code == "ai") {
					uni.showToast({
						icon: 'none',
						title: "该道具已下线"
					})
				} else if (item.code == "topping" || item.code == "eye") { //置顶卡、醒目卡
					this.selectItem = item;
					this.getPositionList();
				} else if (item.code == "refresh") { //刷新卡
					this.selectItem = item;
					this.getApplyResponseList();
				} else if (item.code == "perspective") { //透视卡
					uni.showToast({
						icon: 'none',
						title: "暂未开放"
					})
				}
			},

			async createJfOrder(params) {
				let res = await this.$apis.createJfOrder(params);
				console.log(res);
				if (res) {
					uni.showToast({
						icon: 'success',
						title: "购买成功"
					})
					this.getUserInfo();
				}
			},

			getUserInfo() {
				this.$apis
					.getUserInfo()
					.then(res => {
						console.log("getUserInfo", res);
						if (res) {
							this.$store.commit('SET_USERINFO', res);
						}
					})
					.catch(err => {
						console.log(err, 'catch');
					});
			},

			//查询积分道具
			getGoodsList(pageNum, pageSize) {
				this.query.pageNum = pageNum;
				this.query.pageSize = pageSize;
				this.query.roleCode = this.userInfo.memberRole;
				this.$apis.getJfGoodsList({ params: this.query}).then(res => {
					console.log('getJfGoodsList', res);
					if (pageNum == 1) {
						this.list = [];
					}
					if (res) {
						// 过滤已下线的 AI 卡
						let data = (res.records || []).filter(item => item.code !== 'ai');
						this.list = this.list.concat(data);
						this.mescroll.endSuccess(res.records.length);
					}
				});
			},

			/*下拉刷新的回调 */
			downCallback() {
				this.mescroll.resetUpScroll();
			},
			/*上拉加载的回调: 其中page.num:当前页 从1开始, page.size:每页数据条数,默认10 */
			upCallback(page) {
				this.getGoodsList(page.num, page.size);
			},
		}
	}
</script>

<style lang="scss">
	page {
		background-color: #f5f6fa;
	}

	view {
		box-sizing: border-box;
	}

	.account {
		margin-top: 20upx;
		padding: 10upx 20upx;
		text-align: center;

		text {
			color: #666;
		}

		.integral {
			font-weight: bold;

		}
	}

	.goods-list {
		display: flex;
		flex-wrap: wrap;
		padding: 2%;

		// background: #fff;
		.goods-item {
			display: flex;
			flex-direction: column;
			width: 49%;
			border: 1upx solid #eee;
			margin-bottom: 2%;
			border-radius: 15upx;
			background-color: #fff;

			&:nth-child(2n + 1) {
				margin-right: 2%;
			}
		}

		.image-wrapper {
			width: 100%;
			height: 300upx;
			border-top-right-radius: 15upx;
			border-top-left-radius: 15upx;
			overflow: hidden;
			display: flex;
			align-items: center;
			justify-content: center;

			image {
				width: 200upx;
				height: 200upx;
				opacity: 1;
			}
		}

		.title {
			font-weight: bold;
			font-size: 32upx;
			color: #333;
			line-height: 1.3;
			padding: 0 20upx;
		}

		.desc {
			color: #666;
			font-size: 28upx;
			padding: 0upx 20upx;
		}

		.price-box {
			display: flex;
			align-items: center;
			justify-content: space-between;
			font-size: 24upx;
			color: #f5f6fa;
			padding: 10upx 20upx;
		}

		.price {
			font-size: 32upx;
			color: red;
			line-height: 1;
			// &:before {
			// 	content: '￥';
			// 	font-size: 26upx;
			// }
		}

		.unit {
			color: #333;
			margin-left: 10upx;
			font-size: 28upx;
		}

		.buy {
			border: 1upx solid #eee;
			text-align: center;
			background-color: #12ae85;
			color: #fff;
			padding: 8upx 0;
			border-bottom-left-radius: 20upx;
			border-bottom-right-radius: 20upx;
		}
	}

	.popup-content {
		.top {
			position: relative;
			text-align: center;
			align-items: center;
			padding: 20upx;
			border-bottom: 2upx solid #eee;
			box-sizing: border-box;
			background-color: #f5f6fa;

			.title {
				font-weight: bold;
			}

			.yzb-shanchu {
				position: absolute;
				right: 0;
				padding-right: 20upx;
			}
		}

		.m-position {
			.item {
				background-color: #fff;
				display: flex;
				flex-direction: row;
				align-items: center;
				justify-content: flex-start;
				margin-bottom: 20upx;
				border-radius: 10upx;
				padding: 15upx;
				border-bottom: 1upx solid #eee;
			}

			.logo {
				margin-right: 20upx;
				border: #eee solid 2upx;
				border-radius: 10upx;

				image {
					width: 120upx;
					height: 120upx;
					border-radius: 10upx;
				}
			}

			.job {
				flex: 1;

				.title {
					.left {
						font-weight: bold;
						font-size: 32upx;
					}

					.title-name {
						font-weight: bold;
						font-size: 32upx;
					}

					.title-share {
						color: $main-color;
						border: 1upx solid $main-color;
						border-radius: 20upx 0upx;
						font-size: 28upx;
						margin-left: 20upx;
						padding: 3upx 10upx;
					}

					.right {
						color: #666;
					}
				}

				.company {
					color: #666;
					margin-top: 5upx;
					font-size: 30upx;
					display: flex;
					flex-direction: row;
					align-items: center;
					flex-wrap: wrap;

					.height-line {
						height: 20upx;
						width: 4upx;
						background-color: #ccc;
						margin: 0 15upx;
					}
				}

				.desc {
					text {
						color: #666;
					}
				}
			}

		}

		.space-between {
			display: flex;
			justify-content: space-between;
			flex-direction: row;
		}

		.pop-none {
			height: 600upx;
			text-align: center;
			box-sizing: border-box;
			padding-top: 200upx;
			color: #999;
		}

		.pop-btn {
			display: flex;
			flex-direction: row;
			align-items: center;
			justify-content: center;
			text-align: center;
			padding: 20upx 0;
			background-color: #f5f6fa;

			text {
				height: 60upx;
				line-height: 60upx;
				border-radius: 10upx;
				width: 40%;
			}

			.cancel {
				background-color: #eee;
				color: #333;
			}

			.ensure {
				margin-left: 5%;
				background-color: #12ae85;
				color: #fff;
			}
		}
	}

	.topping-pop {
		.top {
			position: relative;
			text-align: center;
			align-items: center;
			padding: 20upx;
			border-bottom: 2upx solid #eee;
			box-sizing: border-box;
			background-color: #f5f6fa;

			.title {
				font-weight: bold;
			}

			.yzb-shanchu {
				position: absolute;
				right: 0;
				padding-right: 20upx;
			}
		}

		.space-between {
			display: flex;
			justify-content: space-between;
			flex-direction: row;
		}

		.pop-item {
			padding: 15upx;
			display: flex;
			flex-direction: row;
			justify-content: space-between;
			align-items: center;

			.title {
				.left {
					font-weight: bold;
					font-size: 32upx;
				}

				.left-close {
					font-weight: bold;
					color: #999;
					font-size: 32upx;
				}

				.right {
					color: #999;
					font-weight: bold;
				}

				.right-yellow {
					color: $main-color;
					font-weight: bold;
				}
			}

			.company {
				color: #666;
				margin-top: 5upx;
				font-size: 30upx;
				display: flex;
				flex-direction: row;
				align-items: center;

				.height-line {
					height: 20upx;
					width: 4upx;
					background-color: #ccc;
					margin: 0 15upx;
				}
			}

			.desc {
				margin: 10upx 0;
				display: flex;
				flex-wrap: wrap;
				align-items: flex-start;
				align-content: flex-start;
				flex-direction: row;
				justify-content: flex-start;

				text {
					font-size: 30upx;
					padding: 0upx 10upx;
					margin-right: 15upx;
					margin-bottom: 8upx;
					background-color: #eee;
					border-radius: 5upx;
					color: #666;
				}

				.position-type {
					border: $main-color 1upx solid;
					background-color: #fff;
					color: $main-color;
				}

				.position-type2 {
					border: green 1upx solid;
					background-color: #fff;
					color: green;
				}

				.position-type3 {
					border: #666 1upx solid;
					background-color: #fff;
					color: #666;
				}
			}
		}

		.pop-btn {
			display: flex;
			flex-direction: row;
			align-items: center;
			justify-content: center;
			text-align: center;
			padding: 20upx 0;
			background-color: #f5f6fa;

			text {
				height: 60upx;
				line-height: 60upx;
				border-radius: 10upx;
				width: 40%;
			}

			.cancel {
				background-color: #eee;
				color: #333;
			}

			.ensure {
				margin-left: 5%;
				background-color: #12ae85;
				color: #fff;
			}
		}
	}
</style>