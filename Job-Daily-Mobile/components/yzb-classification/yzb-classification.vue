<template>
	<view class="classification">
		<view class="left">
			<scroll-view class="leftScroll" :style="{height:scrollHeight}" :scroll-top="scrollTop" scroll-y="true">
				<view :class="{type1:true, current:(index1 === i)}" :style="{fontSize: type1Size, color: selected(i)}"
					v-for="(item,i) in list" :key="i" @click="changeType1(item,i)">
					<view class="type1-left">
						<view :class="index1==i?'height-line':'height-line-none'"></view>
						<text class="type1-name">{{item.name}}</text>
					</view>
					<view :class="item.selected?'dot':'dot-none'"></view>
				</view>
			</scroll-view>
		</view>
		<view class="right">
			<scroll-view class="rightScroll" :style="{height:scrollHeight}" :scroll-top="scrollTop" scroll-y="true">
				<view class="type2" v-for="(item,i) in data2" :key="i">
					<p :style="{fontSize: type2Size}">{{ item.name }}</p>
					<view class="type3Box">
						<view class="type3" v-for="(item1,i1) in item.child" :key="i1" @click="selectType1(i,i1)">
							<!-- <image class="icon" mode="aspectFill" lazy-load="true" :src="item1.icon"></image> -->
							<text :class="item1.selected?'text-selected':'text-normal'"
								:style="{fontSize: type3Size}">{{item1.name}}</text>
						</view>
						<view class="type3" @click="showDefine(i,i1)" v-if="showAdd">
							<text class="text-add" :style="{fontSize: type3Size}">+自定义</text>
						</view>
					</view>
				</view>
			</scroll-view>
		</view>

		<u-popup :show="show" :round="10" mode="center" @close="show=false" closeable>
			<view class="pop-content">
				<view class="pop-title">
					<text>自定义工种名称</text>
				</view>
				<view class="pop-form">
					<u--form labelWidth="auto">
						<!-- <u-form-item label="工种名称" prop="name" required> -->
						<u-input placeholder="请输入工种名称(限10字内)" maxlength="10" v-model="type.name" />
						<!-- </u-form-item> -->
					</u--form>
				</view>
				<view class="pop-btn">
					<u-button type="primary" @click="$u.debounce(toAdd, 500)">确定</u-button>
				</view>
			</view>
		</u-popup>
	</view>
</template>

<script>
	export default {
		props: {
			index: {
				type: Number,
				default: 0
			},
			selectedColor: {
				type: String,
				default: "#007aff"
			},
			type1Size: {
				type: String,
				default: "14px"
			},
			type2Size: {
				type: String,
				default: "14px"
			},
			type3Size: {
				type: String,
				default: "10px"
			},
			type1Height: {
				type: String,
				default: "90rpx"
			},
			type1LineHeight: {
				type: String,
				default: "90rpx"
			},
			scrollHeight: {
				type: String,
				default: "100vh"
			},
			showAdd: {
				type: Boolean,
				default: false
			},
		},
		data() {
			return {
				scrollTop: 0,
				data1: [], // 用来存储传来的数据
				index1: 0, // 标识当前选中的type1
				data2: [], // 用来存储当前选中的type2数据
				color: "", // 用来存储选中type1的颜色
				load: 0, // 用来判断是否是第一次加载数据
				list: [],
				selectedList: [],

				show: false,
				type: {
					pid:'',
					name: '',
					ifPublic:0,//是否公共类型：1-是，0-否
				},
				index2:0,
				index3:0,
			}
		},
		mounted() {},
		beforeUpdate() {
			if (this.load == 0) {
				this.index1 = this.index
				this.data1 = this.list
				this.color = this.selectedColor
				this.changeType1(this.data1[this.index1], this.index1)
				this.load++
			}
		},
		methods: {

			showDefine(index2,index3) {
				this.show = true;
				this.index2=index2;
				this.index3=index3
			},
			
			toAdd(){
				this.show=false;
				//设置父类id  pid
				this.type.pid=this.data2[this.index2].id;
				this.$emit("addType", this.type,this.index1,this.index2)
				
			},

			init(list) {
				console.log("init")
				this.index1 = this.index
				this.list = list;
				this.data1 = this.list
				this.color = this.selectedColor
				this.changeType1(this.data1[this.index1], this.index1)
			},

			initData2(data) {
				this.data2 = data;
				this.data1 = this.list
			},
			
			init2(item,data,list) {
				console.log("init")
				this.data2 = data;
				this.data1 = this.list
				this.list = list;
				this.setSelected(item)
			},

			removeSelected(item) {
				this.data1[item.index1].child[item.index2].child[item.index3].selected = false;
				this.$forceUpdate();
				this.getSelectedList();
				this.$emit("selectType", this.selectedList)
			},
			
			setSelected(item){
				this.data1[item.index1].child[item.index2].child[item.index3].selected = true;
				this.$forceUpdate();
				this.getSelectedList();
				this.$emit("selectType", this.selectedList)
			},

			changeType1(item, index) {
				this.index1 = index
				if (item === undefined || item === null || item.child === undefined || item.child === null) {
					console.log("该内容为空！")
					this.data2 = []
					this.$emit("getTypeChild", item.id, index)
				} else {
					this.data2 = item.child
				}
			},


			selectType1(i, i1) {
				if (this.selectedList.length == 5) {
					if (this.data2[i].child[i1].selected == false) {
						uni.showToast({
							icon: 'none',
							title: '最多选择5个'
						})
						return;
					}
				}
				this.data2[i].child[i1].selected = !this.data2[i].child[i1].selected;
				this.$forceUpdate();
				this.getSelectedList();
				this.$emit("selectType", this.selectedList)
			},

			getSelectedList() {
				this.selectedList = [];
				for (let i = 0; i < this.data1.length > 0; i++) {
					this.list[i].selected=false;
					if (this.data1[i].child) {
						for (let j = 0; j < this.data1[i].child.length > 0; j++) {
							if (this.data1[i].child[j].child) {
								for (let k = 0; k < this.data1[i].child[j].child.length > 0; k++) {
									if (this.data1[i].child[j].child[k].selected) {
										let item = this.data1[i].child[j].child[k];
										item.index1 = i;
										item.index2 = j;
										item.index3 = k;
										this.selectedList.push(item);
										this.list[i].selected=true;
										this.$forceUpdate();
									}
								}
							}
						}
					}
				}
			},
			// 更换颜色
			selected: function(val) {
				return (val === this.index1 ? this.selectedColor + " !important" : "")
			}
		}
	}
</script>

<style lang="less" scoped>
	.classification {
		display: flex;
		justify-content: space-between;
		width: 100%;
		height: 100%;
		background-color: #FFFFFF;

		/* 隐藏滚动条 */
		::-webkit-scrollbar {
			display: none;
			width: 0 !important;
			height: 0 !important;
			-webkit-appearance: none;
			background: transparent;
		}

		.left {
			width: 30%;
			background-color: #f5f6fa;
			border-right: 1upx solid #EBEEF5;

			.height-line {
				background-color: #007aff;
				width: 8upx;
				height: 30upx;
				margin-right: 10upx;
			}

			.height-line-none {
				background-color: #fff;
				width: 8upx;
				height: 30upx;
				margin-right: 10upx;
			}

			.dot {
				background-color: #007aff;
				width: 10upx;
				height: 10upx;
				min-width: 10upx;
				border-radius: 5upx;
				margin-right: 10upx;
			}

			.dot-none {
				background-color: #fff;
				width: 10upx;
				height: 10upx;
				min-width: 10upx;
				border-radius: 5upx;
				margin-right: 10upx;
			}

			.type1-name {
				padding: 10upx 10upx 10upx 10upx;
			}

			.type1-left {
				display: flex;
				flex-direction: row;
				align-items: center;
			}

			.type1 {
				display: flex;
				flex-direction: row;
				align-items: center;
				justify-content: space-between;
				width: 100%;
				// height: 90rpx;
				// line-height: 90rpx;
				padding: 20upx 0upx;
			}

			.current {
				color: #007aff;
				font-weight: bold;
				background-color: #fff;
			}
		}

		.right {
			float: right;
			width: 70%;
			box-sizing: border-box;
			padding: 2%;

			.type2 {
				margin-bottom: 10upx;

				p {
					// margin: 46rpx 0 26rpx 0;
					font-size: 14px;
					padding: 20upx 0 20upx 10upx;
					font-weight: bold;
				}

				.type3Box {
					width: 100%;
					height: auto;
					border-radius: 28rpx;
					background-color: #fff;
					display: flex;
					flex-wrap: wrap;

					// padding-top: 36rpx;
					// margin-bottom: 36rpx;
					.type3 {
						// .icon {
						// 	width: 100rpx;
						// 	height: 100rpx;
						// 	display: block;
						// }
						align-items: center;
						justify-content: center;
						width: 50%;
						box-sizing: border-box;
						display: flex;
						margin-bottom: 20upx;

						text {
							// padding: 18rpx 0;
							// display: block;
							// width: 100rpx;
							// font-size: 10px;
							// overflow: hidden;
							// text-align: center;
							border-radius: 6upx;
							padding: 0 20upx;
							height: 70upx;
							line-height: 70upx;
							width: 80%;
							text-align: center;

						}

						.text-normal {
							background-color: #f5f6fa;
							border: 1upx solid #f5f6fa;
						}

						.text-add {
							background-color: #f5f6fa;
							border: 1upx solid #f5f6fa;
						}

						.text-selected {
							font-weight: bold;
							background-color: #E0F3FF;
							border: 1upx solid #007aff;
							color: #007aff;
						}
					}
				}
			}
		}
	}

	.pop-content {
		min-width: 600upx;

		.pop-title {
			display: flex;
			flex-direction: row;
			justify-content: center;
			align-items: center;
			border-bottom: 1upx solid #eee;
			padding: 30upx;

			text {
				font-weight: bold;
				font-size: 34upx;
			}
		}

		.pop-form {
			padding: 50upx;
			display: flex;
			flex-direction: column;

			.tips {
				margin-top: 20upx;
				font-size: 28upx;
				color: #666;
			}
		}

		.pop-btn {
			display: flex;
			flex-direction: row;
			align-items: center;
			padding: 0 50upx 50upx 50upx;
		}
	}
</style>