<template>
	<view class="pop" v-if="showDialog" @click="onClickPopBk">
		<view class="pop-dialog" :style="'top:'+topPosition+'px;left:'+leftPosition+'px'">
			<!-- 三角形  如果要定制的话，从这里找到css triangle进行修改 -->
			<view class="triangle" :style="'margin-left:'+triangleMarginLeft+'rpx;border-bottom: 20rpx solid '+bgColor+';'"></view>
			<view class="pop-body" :style="'background-color:'+bgColor+';'">
				<slot name="content"></slot>
			</view>
		</view>  
	</view>
</template>

<script>
	export default{
		props:{
			textColor:{
				type:String,
				default: '#ffffff'
			},
			bgColor:{
				type:String,
				default: '#000'
			},
			lineColor:{
				type:String,
				default: '#6C6C6C'
			},
			isShow:{
				type: Boolean,
				default: false
			},
			list:{
				type:Array,
				default: ()=>{
					return []
				}
			},
			top:{
				type:Number,
				default:0
			},
			left:{
				type:Number,
				default:0
			},
			right:{
				type:Number,
				default:0
			},
			triangleMarginLeft:{
				type:Number,
				default: 16
			}
		},
		data(){
			return {
				showDialog:false,
				menuList:[],
				topPosition:0,
				rightPosition:0,
				leftPosition:0
			}
		},
		watch:{
			isShow:{
				immediate:true,
				handler:async function (newVal,oldVal){
					this.showDialog = newVal;
				}
			},
			list:{
				immediate:true,
				handler:async function (newVal,oldVal){
					this.menuList = newVal;
				}
			},
			top:{
				immediate:true,
				handler:async function (newVal,oldVal){
					this.topPosition = newVal;
				}
			},
			left:{
				immediate:true,
				handler:async function (newVal,oldVal){
					this.leftPosition = newVal;
				}
			},
			right:{
				immediate:true,
				handler:async function (newVal,oldVal){
					this.rightPosition = newVal;
				}
			},
		},
		methods:{
			onClickPopMenu(item){
				this.$emit('onClickPopupItem',item);
			},
			onClickPopBk(){
				this.$emit('onClickPopBk');
			}
		}
	}
</script>

<style>

.pop{
	background-color:rgba(0,0,0,0);
	z-index: 99999;
	position: fixed;
	top: 0;
	width: 100%;
	/* height: 100%; */
}

.pop-dialog{
	display: flex;
	flex-direction: column;
	align-items: flex-start;
	justify-content: center;
	z-index: 999;
	position: absolute;
	border-radius: 20upx;
}

.triangle {
    width: 0;
    height: 0;
    border-right: 16rpx solid transparent;
    border-left: 16rpx solid transparent;
}

.pop-body{
	border-radius: 10px;
	/* width: 288rpx; */
}

.pop-item{
	display: flex;
	flex-direction: column;
	align-items: flex-end;
	justify-content: center;
}

.pop-line-full{
	width: 288rpx;
	height: 2rpx;
}
.pop-line{
	width: 166rpx;
	height: 2rpx;
}

.pop-img{
	width: 45rpx;
	height: 45rpx;
	margin-left: 40rpx;
}
</style>
