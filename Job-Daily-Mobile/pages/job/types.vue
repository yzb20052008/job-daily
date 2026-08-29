<template>
	<view class="content">
		<!-- <view class="search">
			<u-search placeholder="输入关键字搜索" v-model="query.keyword" @change="inputChange" @clear="inputClear"
				:show-action="false"></u-search>
		</view> -->
		<view class="body">
			<yzb-classification ref="yzb" :index="index" :selectedColor="color" :type1LineHeight="lineheight1"
				:type1Size="size1" :type2Size="size2" :scrollHeight="scrollHeight" :type3Size="size3" :type1Height="heigth1" @selectType="select"
				@getTypeChild="getTypeChild" @addType="addType" :showAdd="showAdd">
			</yzb-classification>
		</view>
		<view class="select-info" v-if="selectedList.length>0">
			<view class="select-title">
				<text>我选择的工种</text>
				<text>已选{{selectedList.length}}/5</text>
			</view>
			<view class="select-item">
				<view class="tag" v-for="(item,index) in selectedList" :key="index">
					<u-tag :text="item.name" closable :show="close1" @close="removeItem(item)" color="#007aff"
						bgColor="#E5F4FF" borderColor="#E5F4FF"></u-tag>
				</view>
			</view>
			<view class="select-btn">
				<view style="flex: 1;">
					<u-button text="清除" @click="clear"></u-button>
				</view>
				<view style="margin-left: 30upx; flex: 1;">
					<u-button type="primary" text="确定选择" @click="ensure"></u-button>
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
	export default {
		computed: {
			...mapState(['userInfo'])
		},
		components: {},
		data() {
			return {
				showAdd:false,
				types: [],
				newTypes: [],
				selectedList: [],
				list: [],
				index: 0,
				color: '#007aff',
				size1: '14px',
				size2: '16px',
				size3: '12px',
				heigth1: '90rpx',
				lineheight1: '90rpx',
				// type11: []
				height: "0",

				close1: true,

				searchValue: '',
				placeholder: '请输入关键词',
				
				scrollHeight:"",
				contentHeight:0,
				ids:'',
				defaultSelected:[],
			};
		},
		onLoad(options) {
			console.log("===options===",options);
			if(options.showAdd){
				this.showAdd=true;
			}
			if(options.ids){
				this.ids=options.ids.split(",");
			}
			this.getTypes();
		},

		onReady() {
			this.getContentHeight();
		},

		methods: {
			
			//自定义工种
			async addType(type,index1,index2){
				console.log("addType",type,index1,index2);
				let res = await this.$apis.addTypes(type);
				console.log(res);
				if (res) {
					// this.getTypeChild(this.list[index1].id,index1);
					res.index1=index1;
					res.index2=index2;
					res.index3=this.list[index1].child[index2].child.length;
					this.list[index1].child[index2].child.push(res);
					this.$refs.yzb.init2(res,this.list[index1].child,this.list);
				}
			},
			
			async getTypes() {
				let params={
					userId:'',
				}
				if(this.userInfo.token){
					params.userId=this.userInfo.id;
				}
				this.types = await this.$apis.getTypesList({params:params});
				this.list = JSON.parse(JSON.stringify(this.types));
				this.$refs.yzb.init(this.list);
				
				if(this.ids.length>0){
					setTimeout(()=>{
					    for(let i=0;i<this.ids.length;i++){
					    	for(let m=0;m<this.types.length;m++){
					    		for(let n=0;n<this.types[m].child.length;n++){
					    			for(let k=0;k<this.types[m].child[n].child.length;k++){
					    				if(this.ids[i].trim()==this.types[m].child[n].child[k].id){
					    					let item=this.types[m].child[n].child[k];
											item.index1=m;
											item.index2=n;
											item.index3=k;
					    					console.log("xxxx===item===xxxx",item)
					    					this.$refs.yzb.setSelected(item);
					    				}
					    			}
					    		}
					    	}
					    }
					},100)
				}
			},

			async getTypeChild(id, index) {
				let params={
					pid: id,
					userId:'',
				}
				if(this.userInfo.token){
					params.userId=this.userInfo.id;
				}
				let ctypes = await this.$apis.getTypes({
					params: params
				});
				let child = JSON.parse(JSON.stringify(ctypes));
				this.list[index].child = child;
				this.$refs.yzb.initData2(child);
			},

			clear() {
				this.selectedList.forEach(item=>{
					this.$refs.yzb.removeSelected(item);
				})
			},
			
			getContentHeight(){
				let that=this;
				this.$nextTick(()=>{
					let query = uni.createSelectorQuery().in(this);
					query.select('.content').boundingClientRect(data => {
						that.contentHeight=data.height;
						that.scrollHeight=data.height+'px';
					}).exec();
				})
			},
			
			getScrollHeight(){
				let that=this;
				this.$nextTick(()=>{
					let query = uni.createSelectorQuery().in(this);
					query.select('.select-info').boundingClientRect(data => {
						console.log('select-info==基本信息 = ' + JSON.stringify(data));
						if(data){
							that.scrollHeight=that.contentHeight-data.height +'px';
						}else{
							that.scrollHeight=that.contentHeight +'px';
						}
					}).exec();
				})
			},

			ensure() {
				let pages = getCurrentPages(); //获取page
				let prevPage = pages[pages.length - 2]; //上一个页面（父页面）
				console.log('上一页参数', prevPage);
				prevPage.$vm.types = this.selectedList; //修改上一页data里面的地址
				uni.navigateBack({
					delta: 1
				});
			},

			removeItem(item) {
				console.log('removeItem', item);
				this.$refs.yzb.removeSelected(item);
			},

			select(list) {
				console.log('点击传来的值为', list);
				this.selectedList = list;
				this.getScrollHeight();
			},

			inputChange(e) {
				console.log('searchInput', e);
				this.searchValue = e;
				if (this.searchValue) {
					this.typeSearch(this.searchValue);
				} else {
					this.cancelSearch();
				}
			},
			// 取消搜索
			cancelSearch() {
				this.searchValue = '';
				this.list = JSON.parse(JSON.stringify(this.types));
				this.$refs.yzb.init(this.list);
			},

			typeSearch(keyword) {
				let newTypes = [];
				for (let i = 0; i < this.types.length; i++) {
					if (this.types[i].name.toLowerCase().indexOf(keyword.toLowerCase()) !== -1) {
						newTypes.push(this.types[i]);
					} else {
						//判断下一级
						if (this.types[i].child.length == 0) {
							break;
						}
						for (let j = 0; j < this.types[i].child.length; j++) {
							if (this.types[i].child[j].name.toLowerCase().indexOf(keyword.toLowerCase()) !== -1) {
								let type = JSON.parse(JSON.stringify(this.types[i]));
								let child = JSON.parse(JSON.stringify(this.types[i].child[j]));
								type.child = [];
								type.child.push(child);
								newTypes.push(type);
							} else {
								//判断第三级
								if (this.types[i].child[j].child.length == 0) {
									break;
								}
								for (let k = 0; k < this.types[i].child[j].child.length; k++) {
									if (this.types[i].child[j].child[k].name.toLowerCase().indexOf(keyword
											.toLowerCase()) !== -1) {
										let type = JSON.parse(JSON.stringify(this.types[i]));
										let child1 = JSON.parse(JSON.stringify(this.types[i].child[j]));
										let child2 = JSON.parse(JSON.stringify(this.types[i].child[j].child[k]));
										child1.child = [];
										child1.child.push(child2);
										//判断一级是否已存在
										let t = -1;
										for (let m = 0; m < newTypes.length; m++) {
											if (newTypes[m].name == type.name) {
												t = m;
											}
										}
										if (t != -1) {
											newTypes[t].child.push(child1);
										} else {
											type.child = [];
											type.child.push(child1);
											newTypes.push(type);
										}
									} else {}
								}
							}
						}
					}
				}
				this.list = newTypes;
				this.$refs.yzb.init(this.list);
			},

		}
	};
</script>

<style lang="scss">
	
	.content{
		height: 100vh;
	}
	.search {
		width: 100%;
		display: flex;
		height: 90upx;
		z-index: 999;
		padding: 0 20upx;
	}

	.body {
		width: 100%;
	}

	.select-info {
		position: fixed;
		bottom: 0;
		z-index: 999;
		width: 100%;
		padding: 30upx;
		/* 全面屏底部安全区（须在 padding 简写之后） */
		padding-bottom: calc(30upx + constant(safe-area-inset-bottom));
		padding-bottom: calc(30upx + env(safe-area-inset-bottom));
		box-sizing: border-box;
		background-color: #fff;
		box-shadow: 0 0 20upx rgba(0, 0, 0, 0.15);

		.select-title {
			display: flex;
			flex-direction: row;
			align-items: center;
			justify-content: space-between;

			text:nth-child(1) {
				font-weight: bold;
			}
		}

		.select-item {
			display: flex;
			flex-direction: row;
			flex-wrap: wrap;
			align-items: center;
			margin-top: 10upx;

			.tag {
				margin-right: 10upx;
			}
		}

		.select-btn {
			margin-top: 30upx;
			display: flex;
			flex-direction: row;
		}
	}
</style>