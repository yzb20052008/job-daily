<template>
	<view class="y-post-list">
		<view class="post-item" v-for="(item,index) in list" :key="index" @click="itemClick(item)">
			<view class="worker">
				<view class="worker-left">
					<u-avatar :src="item.avatar" size="50" shape="circle"></u-avatar>
					<view class="worker-info">
						<view class="worker-name">
							<text class="name">{{item.name}}</text>
							<view class="tag" v-if="item.realName">
								<u-tag text="已实名" size="mini" color="#007aff" borderColor="#E5F4FF" bgColor="#E5F4FF"></u-tag>
							</view>
						</view>
						<view class="age-sex">
							<view class="row-align" @click.stop="toComments(item)" v-if="item.score">
								<text class="score" v-if="item.score">{{item.score | formatScore}}分</text>
								<text class="yzb yzb-next"></text>
							</view>
							<view class="dot-grey" v-if="item.sex && item.score"></view>
							<text v-if="item.sex">{{item.sex | formatSex}}</text>
							<view class="dot-grey" v-if="item.sex && item.birthday"></view>
							<text class="age" v-if="item.birthday">{{calCurrentYear(item.birthday)}}岁</text>
							<template v-if="item.jobStatus==1">
								<view class="dot-grey"></view>
								<text>正在找工作</text>
							</template>
							<template v-else>
								<view class="dot-grey"></view>
								<text>暂不找工作</text>
							</template>
						</view>
					</view>
				</view>
				<!-- <view class="worker-right" v-if="item.jobStatus==1">
					<view class="dot"></view>
					<text class="status">正在找工作</text>
				</view>
				<view class="worker-right" v-else>
					<view class="dot"></view>
					<text class="status">暂不找工作</text>
				</view> -->
				<view class="worker-right" v-if="calTime(item.loginTime)">
					<view class="dot"></view>
					<text class="status">今日活跃</text>
				</view>
			</view>
			<view class="tags2">
				<view class="tags-item" v-for="(item3,index3) in toList(item.typeNames,'、')" :key="index3">
					<u-tag :text="item3" color="#666" borderColor="#f5f6fa" bgColor="#f5f6fa" @click="itemClick(item)"></u-tag>
				</view>
				<view class="tags-item" v-for="(item2,index2) in toList(item.skills,',')" :key="index2"  @click="itemClick(item)">
					<u-tag :text="item2" color="#007aff" borderColor="#E5F4FF" bgColor="#E5F4FF"></u-tag>
				</view>
				<view class="tags-item" v-if="item.skilled">
					<u-tag :text="item.skilled" color="#007aff" borderColor="#E5F4FF" bgColor="#E5F4FF" @click="itemClick(item)"> </u-tag>
				</view>
				<view class="tags-item" v-if="item.salaryUnit" >
					<u-tag :text="item.salaryUnit=='面议'?'面议':item.expectSalary+(item.salaryUnit||'')" color="#007aff" borderColor="#E5F4FF" bgColor="#E5F4FF" @click="itemClick(item)"></u-tag>
				</view>
				<view class="tags-item" v-if="item.settlementType"  >
					<u-tag :text="item.settlementType" color="#007aff" borderColor="#E5F4FF" bgColor="#E5F4FF" @click="itemClick(item)"></u-tag>
				</view>
				<view class="tags-item" v-if="item.workType"  >
					<u-tag :text="item.workType" color="#007aff" borderColor="#E5F4FF" bgColor="#E5F4FF" @click="itemClick(item)"></u-tag>
				</view>
				<view class="tags-item" v-if="item.employMethod"  >
					<u-tag :text="item.employMethod" color="#007aff" borderColor="#E5F4FF" bgColor="#E5F4FF" @click="itemClick(item)"></u-tag>
				</view>
			</view>
			<text class="introduction" v-if="item.persionSkill">{{item.persionSkill}}</text>
			<view class="bottom">
				<view class="location">
					<text class="location-icon yzb yzb-dingwei"></text>
					<text class="location-name">{{item.addressName}}</text>
					<text class="load-distance">距你 {{item.distance | formatDistance}}</text>
				</view>
				<!-- <text class="time">08月05日 19:30</text> -->
			</view>
		</view>
	</view>
</template>

<!-- #E5F4FF
#f5f6fa -->

<script>
	import {
		mapState
	} from 'vuex';
	import {
		judgeLogin
	} from '@/config/login';
	import {
		calCurrentYear
	} from '@/config/common';
	export default {
		name: 'y-post-list',
		computed: {
			...mapState(['userInfo'])
		},
		options: {
			styleIsolation: 'shared'
		},
		props: {
			list: {
				type: Array
			},
			url: {
				type: String
			}
		},

		data() {
			return {};
		},
		
		filters: {
			
			formatScore(val){
				if(val){
					return val.toFixed(1);
				}
				return "0";
			},
			
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
				if(!distance && distance!=0){
					return '';
				}
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

		methods: {
			
			toList(val,filter){
				if(val){
					return val.split(filter);
				}
				return [];
			},
			
			//判断是否今日登录过
			calTime(time){
				let time1 = uni.$u.timeFormat(time, 'yyyy-mm-dd');
				let time2 = uni.$u.timeFormat(new Date(), 'yyyy-mm-dd');
				if(time1==time2){
					return true;
				}else{
					return false;
				}
			},
			
			calCurrentYear(val) {
				return calCurrentYear(val)
			},

			itemClick(item) {
				this.judgeLogin(() => {
					this.$emit('itemClick', item)
					uni.$u.route("/pages/resume/resumeDetail?userId=" + item.userId);
				})
			},
			
			toComments(item){
				this.judgeLogin(() => {
					uni.$u.route("/pages/job/comments?roleCode=company&&userId="+item.userId);
				})
				
			}
		}
	};
</script>

<style lang="scss" scoped>
	page {
		background-color: #f5f6fa;
	}

	.y-post-list {}

	.post-item {
		display: flex;
		flex-direction: column;
		margin: 20upx 20upx 0upx 20upx;
		// margin-bottom: 20upx;
		padding: 30upx 20upx;
		box-sizing: border-box;
		background-color: #FFFFFF;
		border-radius: 20upx;
		
		.worker{
			display: flex;
			flex-direction: row;
			justify-content: space-between;
			
			.worker-left{
				display: flex;
				flex-direction: row;
				align-items: center;
			}
			
			.worker-right{
				display: flex;
				flex-direction: row;
				align-items: center;
				margin-top: -15upx;
				.dot{
					background-color: limegreen;
					width: 10upx;
					height: 10upx;
					border-radius: 5upx;
					margin-right: 10upx;
				}
				.status{
					font-size: 14px;
					color: #999;
				}
			}
			
			.worker-info{
				margin-left: 20upx;
			}
			
			.worker-name{
				display: flex;
				flex-direction: row;
				align-items: center;
				.name{
					font-size: 18px;
					font-weight: bold;
				}
				.tag{
					margin-left: 10upx;
				}
			}
			
			.age-sex{
				display: flex;
				flex-direction: row;
				align-items: center;
				margin-top: 8upx;
				text{
					font-size: 14px;
					color: #999;
				}
				
				.score{
					color: #dd524d;
					font-weight: bold;
				}
				
				.yzb-next{
					color: #dd524d;
					font-weight: bold;
					margin-left: 5upx;
				}
				
				.dot-grey{
					background-color: #999;
					width: 6upx;
					height: 6upx;
					border-radius: 3upx;
					margin: 0 10upx;
				}
				
				.age{
				}
			}
			
		}
		
		.title{
			font-size: 16px;
			font-weight: bold;
			text-overflow: -o-ellipsis-lastline;
			overflow: hidden;
			text-overflow: ellipsis;
			display: -webkit-box;
			-webkit-line-clamp: 2;
			-webkit-box-orient: vertical;
		}

		.tags2 {
			display: flex;
			flex-direction: row;
			flex-wrap: wrap;
			align-items: center;
			padding: 20upx 0;
			
			.tags-item{
				margin-right: 15upx;
				margin-top: 10upx;
			}
		}
		
		.introduction{
			font-size: 14px;
			color: #666;
			text-overflow: -o-ellipsis-lastline;
			overflow: hidden;
			text-overflow: ellipsis;
			display: -webkit-box;
			-webkit-line-clamp: 1;
			-webkit-box-orient: vertical;
		}
		
		.bottom{
			display: flex;
			flex-direction: row;
			align-items: center;
			justify-content: space-between;
			margin-top: 20upx;
			
			.location{
				display: flex;
				flex-direction: row;
				align-items: center;
				
				.location-icon{
					color: #888;
					font-size: 12px;
					margin-right: 10upx;
				}
				
				.location-name{
					font-size: 12px;
					color: #888;
				}
				
				.load-distance{
					color: #888;
					font-size: 12px;
					margin-left: 10upx;
				}
			}
			
			
			.time{
				font-size: 12px;
				color: #999;
				min-width: 210upx;
				text-align: right;
			}
		}
	}
	
	.row-align{
		display: flex;
		flex-direction: row;
		align-items: center;
	}
</style>