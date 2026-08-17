<template>
    <view class="goodsBox">
        <view class="goodsList" v-for="item in list" :key="item.id" @click="toDetails(item)">
            <view class="Pic">
                <image mode="aspectFill" class="goodsImage" :src="item.goodsImg" />
                <!-- 特价限购商品标识 -->
                <view class="PromoteIcon" v-if="item.isPromote==1">特价限购</view>
                <view class="totalStockCount" v-if="item.totalStockCount==0">已售罄</view>
            </view>
            <view class="goodsInfo u-flex-m">
                <view class="goodsTop"><view class="goodsName moreText">{{item.name}}</view></view>
                <view class="goodsDescription" v-if="item.goodsDescription">{{item.goodsDescription}}</view>
                <!-- 特价限购商品标识 -->
                <view class="PromoteText" v-if="item.isPromote==1"><view class="word">特价商品限购1件</view></view>
                <view class="goodsData">
                    <view class="monthlySales" v-if="item.monthlySales">
                        <text>月售:</text>
                        <text>{{item.monthlySales || '0'}}</text>
                        <text>{{item.unit}}</text>
                    </view>
                    <view class="totalStockCount" v-if="item.totalStockCount">
                        <text>库存:</text>
                        <text>{{item.totalStockCount}}</text>
                        <text>{{item.unit}}</text>
                    </view>
                </view>
                <view class="memberPrice" v-if="item.memberPrice>0">
                    <view class="left">会员价</view>
                    <view class="right">
                        <view class="Price">￥{{item.memberPrice}}</view>
                    </view>
                </view>
                <view class="bottomOperation">
                    <view class="retailPrice">
                        <text class="icon">￥</text>
                        <text class="Price">{{item.price}}</text>
                        <text class="qi" v-if="item.hasProduct==1">起</text>
                    </view>
                    <view class="rightButton">
                        <button class="button products" @tap.stop="onPopupButton(item)" :style="[{background:PrimaryColor}]">
                            选规格
                        </button>
                    </view>
                </view>
            </view>
        </view>
    </view>
</template>

<script>
	import { mapState, mapMutations } from 'vuex';
	export default {
		props:{
			list: {
				type: Array,
				default(){
					return []
				}
			}
		},
		computed:{
		    ...mapState(['PrimaryColor']),
		},
        data() {
        	return {
                isPopup:false,
                specGoodsData:{},
        	};
        },
        methods:{
            toDetails(item){
                // uni.navigateTo({
                //     url: '/pages/index/goodsDetails?goodsId='+item.goodsId
                // });
            },
            //打开规格--多规格/多属性
            onPopupButton(item){
                this.specGoodsData = item
                console.log(item,'specGoodsData')
				this.$emit('onOpenPopup',item)
            },
            //购物车减
            minus(e){
                this.judgeLogin(() => {
                    var addNumber = -1;
                    // onUpdateToCart(e, addNumber,'',(res)=>{
                    //     this.$emit('minus',res)
                    // })
                })
            },
             //购物车加--单规格、属性
            plus(e){

            },
            
        }
	}
</script>

<style lang="scss" scoped>
.moreText {
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 2;
    overflow: hidden;
}
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
.goodsBox {
    .goodsList {
        /* #ifndef APP-NVUE */
        display: flex;
        /* #endif */
        padding: 30rpx 0;
        border-bottom: 1px solid #f5f5f5;
        box-sizing: border-box;
        transition: all 1s;
        -webkit-transition: all 1s;
        .Pic {
            width: 160rpx;
            height: 160rpx;
            position: relative;
            .PromoteIcon {
                position: absolute;
                left: 0;
                top: 0;
                background: #fe461d;
                color: #fff;
                font-size: 20rpx;
                padding: 2rpx 10rpx;
                border-radius: 10rpx 0 10rpx 0;
            }
            .totalStockCount {
                position: absolute;
                left: 0;
                top: 0;
                background: rgba(0, 0, 0, .5);
                color: #fff;
                font-size: 24rpx;
                width: 160rpx;
                height: 160rpx;
                border-radius: 10rpx;
                z-index: 9;
                text-align: center;
                line-height: 160rpx;
            }
            .goodsImage {
                width: 160rpx;
                height: 160rpx;
                border-radius: 10rpx;
            }
        }
        .goodsInfo {
            margin-left: 20rpx;
            /* #ifndef APP-NVUE */
            display: flex;
            /* #endif */
            flex-direction: column;
            justify-content: space-between;
            .goodsTop {
                /* #ifndef APP-NVUE */
                display: flex;
                /* #endif */
                flex-direction: row;
                justify-content: flex-start;
            }
            .goodsName {
                font-size: 28rpx;
                font-weight: bold;
            }
            .goodsDescription {
                color: #999;
                font-size: 22rpx;
                line-height: 24rpx;
                margin-top: 10rpx;
                display: inline-block;
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
            }
            .goodsData {
                /* #ifndef APP-NVUE */
                display: flex;
                /* #endif */
                flex-direction: row;
                margin-top: 8rpx;
                text {
                    color: #666;
                    font-size: 24rpx;
                }
                .monthlySales {
                    margin-right: 20rpx;
                }
            }
            .bottomOperation {
                /* #ifndef APP-NVUE */
                display: flex;
                /* #endif */
                flex-direction: row;
                justify-content: space-between;
                margin-top: 10rpx;
                align-items: center;
                .rightButton {
                    .button {
                        background: #fe461d;
                        color: #fff;
                        font-weight: normal;
                        padding: 0 12rpx;
                        height: 44rpx;
                        border-radius: 44rpx;
                        text-align: center;
                        line-height: 44rpx;
                        font-size: 24rpx;
                    }
                }
            }
            .retailPrice {
                color: #fe461d;
                .icon {
                    font-size: 22rpx;
                    line-height: 22rpx;
                }
                .Price {
                    font-size: 40rpx;
                    font-weight: bold;
                    line-height: 38rpx;
                }
                .qi {
                    color: #999;
                    font-size: 22rpx;
                    margin-left: 5rpx;
                    line-height: 10rpx;
                }
            }
            .memberPrice {
                /* #ifndef APP-NVUE */
                display: flex;
                /* #endif */
                flex-direction: row;
                align-items: center;
                color: #fe461d;
                margin-top: 16rpx;
                justify-content: flex-start;
                .left {
                    background: #fdf3f3;
                    color: #fe461d;
                    font-size: 22rpx;
                    font-weight: bold;
                    height: 40rpx;
                    padding: 0 10rpx;
                    line-height: 40rpx;
                    border-radius: 10rpx 0 0 10rpx;
                }
                .right {
                    /* #ifndef APP-NVUE */
                    display: flex;
                    /* #endif */
                    flex-direction: row;
                    justify-content: flex-start;
                    align-items: center;
                    background-image: linear-gradient(to bottom, #ff6700, #ff1739);
                    height: 40rpx;
                    padding: 0 16rpx;
                    line-height: 40rpx;
                    border-radius: 0 10rpx 10rpx 0;
                }
                .Price {
                    color: #fff;
                    font-size: 22rpx;
                    line-height: 22rpx;
                    font-weight: bold;
                }
            }
        }
    }
}
.PromoteText .word {
    font-size: 20rpx;
    color: #fe461d;
    border: 1rpx solid #fe461d;
    padding: 2rpx 8rpx;
    border-radius: 6rpx;
    display: inline-block;
}

</style>
