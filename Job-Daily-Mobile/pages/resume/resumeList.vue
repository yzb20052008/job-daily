<template>
	<view class="content">
		<view class="top">
			<view class="top-item" @click="selectCity">
				<text class="top-item-title">{{cityInfo.area}}</text>
				<text class="top-item-icon yzb yzb-xiangxia1"></text>
			</view>
			<view class="top-item" @click="toTypes">
				<text class="top-item-title">{{typeNames}}</text>
				<text class="top-item-icon yzb yzb-xiangxia1"></text>
			</view>
		</view>
		<mescroll-uni :fixed="false" height="100%" ref="mescrollRef" @init="mescrollInit" :up="upOption" @down="downCallback" @up="upCallback">
			<view class="list">
				<y-resume-list :list="list" @success="success"></y-resume-list>
			</view> 
		</mescroll-uni>
	</view>
</template>

<script>
import { mapState,mapMutations} from 'vuex';
import MescrollMixin from '@/uni_modules/mescroll-uni/components/mescroll-uni/mescroll-mixins.js';
export default {
	mixins: [MescrollMixin],
	computed: {
		...mapState(['userInfo', 'locateInformation']),
	},
	data() {
		return {
			clickable: true,
			list: [],
			upOption: {
				onScroll: true,
				auto: false, // 不自动加载
				noMoreSize: 10, //如果列表已无数据,可设置列表的总数量要大于半页才显示无更多数据;避免列表数据过少(比如只有一条数据),显示无更多数据会不好看; 默认5
				empty: {
					tip: '空空如也', // 提示
					icon: 'https://img.qinkonglan.cn/imgs/data.jpg'
				}
			},
			
			query: {
				pageNo: 1,
				pageSize: 10,
				cityCode:'',
				pCityCode:'',
				latitude: 0,
				longitude: 0,
				keyword: null,
				orderBy: 'new',
				typeCodes: ''
			},
			
			cityInfo: {
				area: '选择城市'
			},
			lastCity:'城市',
			types: [],
			typeNames:'选择工种',
			typeIds:'',
		};
	},
	onLoad(options) {
		console.log("options==",options);
		if(options.typeCode){
			this.query.typeCodes = options.typeCode;
			let ids=options.typeCode.split('|');
			this.typeNames="已选工种数："+ids.length;
			console.log("options=this.query.typeCodes=",this.query.typeCodes);
		}
		this.cityInfo=this.locateInformation.cityInfo;
	},
	
	onShow() {
		if (this.types.length > 0) {
			this.query.typeIds = '';
			if(this.types.length==1){
				this.typeNames=this.types[0].name;
				this.query.typeCodes=this.types[0].typeCode;
			}else{
				this.typeNames="已选工种数："+this.types.length;
				let typelist = this.types.map(item => item.typeCode);
				this.query.typeCodes = typelist.join("|");
				this.tabIds = this.types.map(item => item.id).join(', ');
			}
			console.log("xxxxxxx",this.query.typeIds);
			this.getResumeList(1, 10);
		} else {
			
		}
		//处理城市选择
		if(this.cityInfo || (this.cityInfo.area != this.lastCity)){
			if(this.locateInformation.location){
				this.locateInformation.cityInfo=this.cityInfo;
				this.setLocateInformation(this.locateInformation);
			}
			this.getResumeList(1, 10);
		}
	},
	
	methods: {
		...mapMutations(['setLocateInformation']),
		
		getResumeList(pageNo, pageSize) {
			this.query.pageNo = pageNo;
			this.query.pageSize = pageSize;
			if (!this.locateInformation.location) {
				console.log("暂无定位信息")
				// return;
			} else {
				this.query.latitude = this.locateInformation.location.lat;
				this.query.longitude = this.locateInformation.location.lng;
				// this.query.cityCode=this.cityInfo.areaCode;
				this.query.pCityCode=this.cityInfo.areaCode.substr(0,4);
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
				// this.mescroll.endSuccess(0);
			});
		},
		
		
		toTypes() {
			// uni.$u.route('/pages/job/types');
			uni.$u.route('/pages/job/types?ids=' + this.tabIds);
		},
		
		toSearch() {
			uni.$u.route('/pages/common/search');
		},
		
		selectCity() {
			uni.$u.route('/pages/common/selectCity');
		},
		

		/*下拉刷新的回调 */
		downCallback() {
			this.mescroll.resetUpScroll();
		},
		/*上拉加载的回调: 其中page.num:当前页 从1开始, page.size:每页数据条数,默认10 */
		upCallback(page) {
			this.getResumeList(page.num, page.size);
		},
	}
};
</script>

<style lang="scss">
page {
	background-color: #f5f5f5;
}
.content {
	flex: 1;
	flex-direction: column;
	align-items: center;
	height: 100vh;
	box-sizing: border-box;
}

.top{
	display: flex;
	flex-direction: row;
	align-items: center;
	background-color: #fff;
	.top-item{
		flex: 1;
		display: flex;
		align-items: center;
		justify-content: center;
		padding-bottom: 20upx;
	}
	
	.top-item-title{
		font-size: 15px;
	}
	
	.top-item-icon{
		margin-left: 8upx;
		font-size: 13px;
		color: #666;
	}
}

.item {
	display: flex;
	flex-direction: column;
	margin-bottom: 20rpx;
	background-color: #fff;
	border-radius: 20rpx;
	flex: 1;
	padding: 20upx;
	.item-title{
		// font-weight: bold;
		font-size: 30upx;
		color: #000;
		display: flex;
		flex-direction: row;
		justify-content: space-between;
		.integral{
			color: #007aff;
		}
		.integral2{
			color: red;
		}
	}
	.item-li{
		align-items: center;
		color:#666;
		font-size:28upx;
		margin-top:15upx;
	}
	.yzb{
		margin-right: 8upx;
	}
}
</style>
