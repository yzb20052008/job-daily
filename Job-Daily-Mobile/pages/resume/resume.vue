<template>
	<view class="page">
		<public-module></public-module>
		<f-navbar bgColor="#007aff" :navbarType="5" fontColor="#fff" transparentTitleColor="#000" :isShowLeft="true"
			:scrollTop="scrollTop" title="个人简历" :isShowTransparentTitle="false">
		</f-navbar>
		<view class="none" v-if="hasResume==false">
			<u-empty mode="data" icon="https://cdn.example.com/imgs/data.jpg" text=" ">
				<view class="none-info">
					<text class="none-title">您未创建简历</text>
					<text class="none-tip">创建发布简历，老板将会主动联系你</text>
					<view class="none-btn">
						<u-button :plain="false" type="primary" text="创建简历" @click="addResume"></u-button>
					</view>
				</view>
			</u-empty>
		</view>
		<view class="resume" v-else>
			<view class="resume-percent column">
				<view class="space-between">
					<text class="percent-title">简历完善度：{{resumeInfo.percentage}}%</text>
					<!-- <view class="row">
						<text class="percent-more">去完善</text>
						<text class="yzb yzb-next"></text>
					</view> -->
				</view>
				<u-line-progress :percentage="resumeInfo.percentage" activeColor="#007aff"></u-line-progress>
				<text class="percent-tip">简历越完善，老板更容易联系你</text>
			</view>
			<view class="worker" @click="toBaseInfo">
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
					<text class="yzb yzb-next"></text>
				</view>
				<view class="tags">
					<view class="tags-item" v-for="(item,index) in toList(resumeInfo.skills,',')" :key="index">
						<u-tag :text="item" color="#007aff" borderColor="#E5F4FF" bgColor="#E5F4FF"></u-tag>
					</view>
					<view class="tags-item" v-if="resumeInfo.skilled">
						<u-tag :text="resumeInfo.skilled" color="#007aff" borderColor="#E5F4FF" bgColor="#E5F4FF"></u-tag>
					</view>
				</view>
				<view class="user-other-info">
					<view class="info-item">
						<text class="other-title">联系电话：</text>
						<text class="other-info">{{resumeInfo.phone}}</text>
					</view>
					<view class="info-item" v-if="resumeInfo.addressName">
						<text class="other-title">常住地址：</text>
						<text class="other-info">{{resumeInfo.addressName}}</text>
					</view>
				</view>
			</view>
			<view class="job-status">
				<text class="status-title">当前状态</text>
				<view @click="stateShow=true">
					<text class="status-info">{{resumeInfo.jobStatus|formatStatus}}</text>
					<text class="yzb yzb-next"></text>
				</view>
			</view>
			<view class="expect" @click="toExpect">
				<view class="item-top space-between">
					<text class="item-title">找工意向</text>
					<text class="item-next yzb yzb-next"></text>
				</view>
				<view class="tags bottom-line" v-if="resumeInfo.intention">
					<view class="tags-item" v-for="(item,index) in toList(resumeInfo.intention.typeNames,'、')" :key="index">
						<u-tag :text="item" color="#666" borderColor="#f5f6fa" bgColor="#f5f6fa"></u-tag>
					</view>
				</view>
				<view class="tags">
					<view class="tags-item" v-if="resumeInfo.intention.salaryUnit">
						<u-tag :text="resumeInfo.intention.expectSalary+resumeInfo.intention.salaryUnit" color="#007aff" borderColor="#E5F4FF" bgColor="#E5F4FF"></u-tag>
					</view>
					<view class="tags-item" v-if="resumeInfo.intention.settlementType">
						<u-tag :text="resumeInfo.intention.settlementType" color="#007aff" borderColor="#E5F4FF" bgColor="#E5F4FF"></u-tag>
					</view>
					<view class="tags-item" v-if="resumeInfo.intention.workType">
						<u-tag :text="resumeInfo.intention.workType" color="#007aff" borderColor="#E5F4FF" bgColor="#E5F4FF"></u-tag>
					</view>
					<view class="tags-item" v-if="resumeInfo.intention.employMethod">
						<u-tag :text="resumeInfo.intention.employMethod" color="#007aff" borderColor="#E5F4FF" bgColor="#E5F4FF"></u-tag>
					</view>
				</view>
				<view class="city" v-if="resumeInfo.intention">
					<text class="city-title">期望工作地：</text>
					<text class="city-info">{{resumeInfo.intention.workCity}}</text>
				</view>
			</view>
			<view class="introduce column" @click="toIntroduce">
				<view class="item-top space-between">
					<text class="item-title">自我介绍</text>
					<text class="item-next yzb yzb-next"></text>
				</view>
				<text class="desc">{{resumeInfo.personalSkill || ""}}</text>
			</view>
			<view class="project column">
				<view class="item-top space-between">
					<text class="item-title">项目经验</text>
					<text class="item-next yzb yzb-jiahao" @click="toProject()" v-if="resumeInfo.expList.length<3"></text>
				</view>
				<view class="project-item" :class="index>0?'top-line':''" v-for="(item,index) in resumeInfo.expList" :key="index"
				 @click="toProject(item)">
					<text class="project-detail">{{item.descr}}</text>
					<text class="yzb yzb-next"></text>
				</view>
			</view>
			<view class="skill column">
				<view class="item-top space-between">
					<text class="item-title">技能证书</text>
					<text class="item-next yzb yzb-jiahao" @click="toSkill" v-if="resumeInfo.certList.length<3"></text>
				</view>
				<view class="skill-item" :class="index>0?'top-line':''" v-for="(item,index) in resumeInfo.certList" :key="index"
				 @click="toSkill(item)">
					<view class="column">
						<text class="skill-name">{{item.certName}}</text>
						<u-avatar :src="item.certImg" size="50" shape="square"></u-avatar>
					</view>
					<text class="yzb yzb-next"></text>
				</view>
			</view>
		</view>
		<u-action-sheet :show="stateShow" @close="stateShow = false" :actions="stateActions" @select="selectState"
			cancelText="取消"></u-action-sheet>
	</view>
</template>

<script>
	import {
		calCurrentYear
	} from '@/config/common';
	export default {
		data() {
			return {
				stateShow: false,
				stateActions: [{
						value: 1,
						name: '正在找工作'
					},
					{
						value: 2,
						name: '暂不找工作'
					},
				],

				scrollTop: 0,
				data: [{}, {}],
				hasResume: false,
				resumeInfo: {},
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
		},

		onPageScroll(e) {
			this.scrollTop = e.scrollTop;
		},

		onShow() {
			this.getResumeInfo();
		},

		methods: {

			async getResumeInfo() {
				let res = await this.$apis.getResumeInfo();
				console.log('res=====', res);
				if (res) {
					this.hasResume = true;
					this.resumeInfo = res;
				}
			},

			calCurrentYear(val) {
				return calCurrentYear(val)
			},
			
			toList(val,filter){
				if(val){
					return val.split(filter);
				}
				return [];
			},
			

			selectState(val) {
				console.log('===selectState===', val);
				let param = {
					jobStatus: val.value
				};
				this.$apis
					.updateResume(param)
					.then(res => {
						console.log('xxxxxxxxxxxxxx', res);
						uni.$u.toast('操作成功');
						this.resumeInfo.jobStatus = val.value;
					})
					.catch(err => {
						console.log(err, 'catch');
					});
			},

			addResume() {
				uni.$u.route("/pages/resume/addResume1");
			},

			toBaseInfo() {
				uni.$u.route("/pages/resume/resumeBase");
			},

			toIntroduce() {
				uni.$u.route("/pages/resume/resumeIntroduce?personalSkill="+this.resumeInfo.personalSkill);
			},

			toExpect() {
				uni.$u.route("/pages/resume/resumeExpect");
			},

			toProject(item) {
				if(item){
					uni.$u.route("/pages/resume/resumeProject?id="+item.id);
				}else{
					uni.$u.route("/pages/resume/resumeProject");
				}
			},

			toSkill(item) {
				if(item){
					uni.$u.route("/pages/resume/resumeSkill?id="+item.id);
				}else{
					uni.$u.route("/pages/resume/resumeSkill");
				}
				
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
		padding-bottom: 50upx;
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
			padding-bottom: 30upx;
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
			padding: 5upx 0;

			.name {
				font-size: 20px;
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
				color: #999;
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
			padding-top: 30upx;
			font-size: 16px;
			border-top: 1upx solid #eee;

			.info-item {
				display: flex;
				flex-direction: row;
				align-items: center;
				padding-bottom: 15upx;

				.other-title {
					color: #666;
				}

				.other-info {
					color: #333;
				}
			}
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
	}

	.introduce {
		background-color: #fff;
		margin-top: 30upx;
		border-radius: 20upx;
		padding: 30upx;

		.desc {
			color: #333;
			margin-top: 10upx;
			// text-overflow: -o-ellipsis-lastline;
			// overflow: hidden;
			// text-overflow: ellipsis;
			// display: -webkit-box;
			// -webkit-line-clamp: 1;
			// -webkit-box-orient: vertical;
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
</style>