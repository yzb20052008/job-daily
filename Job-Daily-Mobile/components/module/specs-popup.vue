<template>
    <view>
        <view class="popup">
            <!-- 头部商品开始 -->
            <view class="topGoods">
                <view class="Pic">
                    <image mode="aspectFill" class="goodsImage" :src="specGoodsData.goodsImg"></image>
                </view>
                <view class="goodsInfo">
                    <view class="goodsName moreText">{{specGoodsData.name}}</view>
                    <view class="goodsDescription">{{specGoodsData.goodsDescription}}</view>
                    <view class="goodsData">
                        <view class="monthlySales" v-if="selArray || (specGoodsData.attr_array && specGoodsData.attr_array[0])">
                            <text>已选:</text>
                            <text class="clickWord">{{selArray || specGoodsData.attr_array[0]}}</text>
                        </view>
                        <view class="totalStockCount" v-if="specGoodsData.totalStockCount>0">
                            <text>库存:</text>
                            <text>{{specGoodsData.totalStockCount}}</text>
                            <text>{{specGoodsData.unit}}</text>
                        </view>
                    </view>
                    <view class="bottomOperation">
                        <view class="retailPrice">
                            <view class="fuhao">￥</view>
                            <view class="Price">{{specGoodsData.price}}</view>
                        </view>
                        <!-- 会员价 -->
                        <view class="retailPrice" v-if="specGoodsData.memberPrice>0">
                            <view class="fuhao">会员价:￥</view>
                            <view class="Price">{{specGoodsData.memberPrice}}</view>
                        </view>
                    </view>
                </view>
            </view>
            <!-- 头部商品结束 -->
            <!-- 商品规格开始 -->
            <view class="popupCenter">
                <scroll-view scroll-with-animation="true" enable-back-to-top="true" scroll-y="true" style="max-height:520rpx;padding-bottom: 24rpx;">
                    <!-- 属性 -->
                    <view class="goodsSpec" v-if="specGoodsData.attr_array && specGoodsData.attr_array.length>0">
                        <view class="name">请选择规格</view>
                        <view class="specList">
                            <button @click="onfreeSpecs(item,index)" v-for="(item,index) in specGoodsData.attr_array" :key="index" size="mini" type="default" class="button" :class="{click:current == index}" :style="[current == index ? { background: freeSpecsButtonBackground,color:PrimaryColor } : {},]">{{item}}</button>
                        </view>
                    </view>
                    <!-- 数量 -->
                    <view class="u-flex" v-if="isShowNum">
                        <view class="u-flex-m" style="font-size: 28rpx;">数量</view>
						<view class="numberBox u-flex">
						    <view class="minus u-flex" style="line-height: 54rpx;" @click="minus">
								-
						    </view>
						    <view class="inputBox u-flex">
						        <input v-model="num" @input="inputChange" class="u-flex" type="number"/>
						    </view>
						    <view class="plus u-flex" style="line-height: 54rpx;" @click="plus">
								+
						    </view>
						</view>
                        <!-- <view style="width: 220rpx;">
                            <u-number-box v-model="num" :min="1" :max="specGoodsData.totalStockCount" @plus="plus" @minus="minus"></u-number-box>
                        </view> -->
                    </view>
                </scroll-view>
            </view>
            <!-- 商品规格结束 -->
            <!-- 确定按钮 -->
            <view class="determine" v-if="popupType==2">
                <button @click="onPurchase(specGoodsData)" size="default" type="primary" class="button" :style="{background:PrimaryColor}">{{buyName}}</button>
            </view>
            <view class="determine u-flex" v-else-if="popupType==3">
                <button @click="onDetermine(specGoodsData)" size="default" type="primary" class="button u-flex-m" style="background:#ffa63a;margin:0 10rpx;">加入购物车</button>
                <button @click="onPurchase(specGoodsData)" size="default" type="primary" class="button u-flex-m" style="margin:0 10rpx;" :style="{background:PrimaryColor}">{{buyName}}</button>
            </view>
            <view class="determine" v-else>
                <button @click="onDetermine(specGoodsData)" size="default" type="primary" class="button" :style="{background:PrimaryColor}">加入购物车</button>
            </view>
        </view>
    </view>
</template>

<script>
	import { mapState, mapMutations } from 'vuex';
	export default {
		props:{
            isPopup: {
                type: Boolean
            },
			specGoodsData: {
				type: Object,
				default(){
					return {};
				}
			},
            //规格状态 1：默认 加入购物车 2：不加人购物车 按钮名称改成去购买 配合isAuto=true使用 3:显示加入购物车按钮、去购买按钮
            popupType: {
                type: [Number,String],
                default(){
                	return 1;
                }
            },
            //是否显示数量控件 false：隐藏 true：显示
            isShowNum: {
                type: Boolean,
                default(){
                	return true;
                }
            },
		},
		computed:{
		    ...mapState(['PrimaryColor']),
		},
        data() {
            return {
                freeSpecsButtonBackground:'rgba(254,70,29,.1)',
                showPopup: false,
                addNumber:1,
                disabled:true,//是否禁止规格数量控件 true：禁止
                buyName:'去购买',
                current:0,//选择的规格
                selArray:'',//已选规格属性
				num:1,
            };
        },
        watch:{
            isPopup(val){
                console.log(val)
                if(val){
                    setTimeout(()=>{
                        if(this.specGoodsData.attr_array){
                            this.selArray = this.specGoodsData.attr_array[0]
                        }
                    },100)
                }
            },
        },
        created() {

        },
        methods:{
            //选择属性/规格
            onfreeSpecs(e,index){
                this.current = index
                this.selArray = e
                var item = {
                    type:1,//1:选择规格 2：加入购物车 3：去购买 4:输入的数量
					selArray:e
                }
                this.$emit('change',item)
            },
            // 加入购物车
            onDetermine(e) {
                var item={
                    type:2,//1:选择规格 2：加入购物车 3：去购买 4:输入的数量
					goods:e,
                    num:this.num,
                    selArray:this.selArray || (this.specGoodsData.attr_array && this.specGoodsData.attr_array[0]),
                }
                this.$emit('change',item)
            },
            //去购买
            onPurchase(e){
                var item = {
                    type:3,//1:选择规格 2：加入购物车 3：去购买 4:输入的数量
					goods:e,
                    num:this.num,
                    selArray:this.selArray || (this.specGoodsData.attr_array && this.specGoodsData.attr_array[0]),
                }
                this.$emit('change',item)
            },
            minus(){
				if(this.num<=1){
					this.num=1
				}else{
					this.num--
				}
				var item = {
				    type:4,//1:选择规格 2：加入购物车 3：去购买 4:输入的数量
					num:this.num
				}
				this.$emit('change',item)
			},
			plus(){
                var maxNum = this.specGoodsData.totalStockCount || null
                if(maxNum && this.num>=maxNum){
                	this.num = maxNum
                }else{
                    this.num++
                }
				var item = {
				    type:4,//1:选择规格 2：加入购物车 3：去购买 4:输入的数量
					num:this.num
				}
				this.$emit('change',item)
			},
			inputChange(event){
				var val = event.detail.value
                var maxNum = this.specGoodsData.totalStockCount || null
				setTimeout(() => {
					if(val > 1){}else{
						this.num = 1
					}
                    if(maxNum && val>=maxNum){
                    	this.num = maxNum
                    }
				}, 1)
			},
            
        }
	}
</script>

<style lang="scss">
.u-flex{
    /* #ifndef APP-NVUE */
    display: flex;
    /* #endif */
}
.u-flex-m {
    -webkit-box-flex: 1;
    -webkit-flex: 1;
    flex: 1;
    overflow: hidden
}
.moreText {
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 2;
    overflow: hidden;
}
.popup {
    background: #fff;
    border-radius: 16rpx 16rpx 0 0;
}
/* 头部商品开始 */
.topGoods {
    overflow: hidden;
    position: relative;
    /* #ifndef APP-NVUE */
    display: flex;
    /* #endif */
    /* border-bottom: 1px solid #f5f5f5; */
    padding: 40rpx 40rpx 24rpx 40rpx;
    box-sizing: border-box;
    .Pic {
        width: 136rpx;
        height: 136rpx;
        .goodsImage {
            width: 136rpx;
            height: 136rpx;
            border-radius: 10rpx;
        }
    }
    .goodsInfo {
        margin-left: 20rpx;
        width: 100%;
        .goodsName {
            font-size: 32rpx;
            font-weight: bold;
            padding-right: 35rpx;
        }
        .goodsDescription {
            color: #999;
            font-size: 22rpx;
            line-height: 24rpx;
            margin-top: 10rpx;
            display: inline-block;
            white-space: nowrap;
            width: 490rpx;
            overflow: hidden;
            text-overflow: ellipsis;
        }
        .goodsData {
            /* #ifndef APP-NVUE */
            display: flex;
            /* #endif */
            flex-direction: row;
            // margin-top: 4rpx;
            text {
                color: #666;
                font-size: 24rpx;
            }
            .monthlySales {
                margin-right: 20rpx;
                .clickWord {
                    max-width: 300rpx;
                    white-space: nowrap;
                    overflow: hidden;
                    text-overflow: ellipsis;
                }
            }
        }
        .bottomOperation {
            /* #ifndef APP-NVUE */
            display: flex;
            /* #endif */
            flex-direction: row;
            margin-top: 10rpx;
            align-items: center;
            .retailPrice {
                /* #ifndef APP-NVUE */
                display: flex;
                /* #endif */
                flex-direction: row;
                align-items: flex-end;
                color: #fe461d;
                margin-right: 24rpx;
                .fuhao {
                    font-size: 22rpx;
                }
                .Price {
                    font-size: 38rpx;
                    font-weight: bold;
                    line-height: 38rpx;
                }
            }
        }
    }
}

.popupCenter {
    color: #333;
    position: relative;
    width: 100%;
    padding: 0 40rpx;
    .goodsSpec{
        color: #333;
        .freeSpecs {
            color: #333;
        }
        .name {
            color: #333;
            font-size: 26rpx;
            font-weight: bold;
        }
        .specList {
            margin-top: 20rpx;
            .button {
                padding: 0rpx 44rpx;
                color: #262626;
                background: #f2f2f2;
                margin-right: 30rpx;
                margin-bottom: 30rpx;
                height: 70rpx;
                line-height: 68rpx;
                border-radius: 35px;
                box-sizing: border-box;
                font-size: 28rpx;
                overflow: initial;
                display: inline-block;
                &.click {
                    color: #65b05b;
                    background: #f0f9eb;
                }
                &::after {
                    border: none;
                }
            }
        }
    }
}
.determine {
    padding: 20rpx 40rpx;
    background: #fff;
    .button {
        padding: 0rpx 44rpx;
        height: 90rpx;
        line-height: 90rpx;
        border-radius: 45px;
        background: #65b05b;
        border: none;
    }
}
.numberBox{
    .minus,.plus{
        width: 60rpx;
        height: 54rpx;
        border: 1rpx solid #eee;
        justify-content: center;
    }
    .minus{
        border-radius: 6rpx 0 0 6rpx;
    }
    .plus{
        border-radius:0 6rpx 6rpx 0;
    }
    .inputBox{
        width: 60rpx;
        height: 54rpx;
        border-top: 1rpx solid #eee;
        border-bottom: 1rpx solid #eee;
        input{
            width: 100%;
            height: 100%;
            border: 0;
            text-align: center;
            font-size: 26rpx;
        }
    }
}
</style>
