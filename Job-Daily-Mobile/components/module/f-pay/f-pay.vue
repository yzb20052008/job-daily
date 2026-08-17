<template>
    <view class="payTypeBox">
        <!-- <view class="payHead">
            <view class="title">选择支付类型</view>
        </view> -->
        <view class="payCenter">
            <view class="topInfo">
                <view class="shopName">支付金额</view>
                <view class="payMoney">
                    <view class="text">￥</view>
                    <view class="number">{{payMoney | money}}</view>
                </view>
                <view class="tips"></view>
            </view>
            <view class="selPayType">
                <view class="typeList" :class="{opacity:!item.state}" v-for="(item,index) in payType" :key="index" @click="onPayTypeList(index)">
                    <view class="left">
                        <view class="icon">
                            <u-icon :name="item.icon" size="22" :color="item.color"></u-icon>
                        </view>
                        <view class="word">
                            <view class="title">{{item.title}}</view>
                        </view>
                    </view>
                    <view class="right">
                        <view class="yue" v-if="item.yue">可用:￥{{item.yue}}</view>
                        <view class="state">
                            <view class="quan" :style="{background:index == payTypeIndex?PrimaryColor:'',border:'1rpx solid '+(index == payTypeIndex?PrimaryColor:'#ddd')}">
                                <view v-if="index == payTypeIndex">
                                    <u-icon name="checkmark" :size="14" color="#fff"></u-icon>
                                </view>
                            </view>
                        </view>
                    </view>
                </view>
            </view>
            <view class="payButton">
                <view class="button">
                    <u-button @click="onPayButton" :custom-style="{backgroundColor:PrimaryColor,borderColor:PrimaryColor,color:'#fff'}" :disabled="isPayDisabled" :loading="isPayDisabled">立即支付</u-button>
                </view>
            </view>
        </view>
        <!-- 点击支付时候不能在操作任何 以下透明背景 -->
        <view class="Mask" v-if="isPayDisabled"></view>
    </view>

</template>

<script>
    import { mapState, mapMutations } from 'vuex';
    import { setPay,wxPublicPay } from '@/config/pay';
    export default {
        props:{
            // 金额
            payMoney:{
            	type: [String,Number]
            },
            //支付订单号
            tradeNo:{
            	type: [String,Number]
            },
            // 用于监听弹窗显示
            show:{
            	type: Boolean,
            	default() {
            		return false;
            	}
            },
        },
        watch:{
            show(val){
                if(val){
                    this.upPay()
                }
            }
        },
        data() {
            return {
                PrimaryColor: '#fe461d', // 主题色
                payType: [{
                    icon: "rmb-circle-fill",
                    title: "余额支付",
                    yue: 0, //余额
                    state: true, //是否有效
                    color:'#b09665'
                }, {
                    icon: "weixin-fill",
                    title: "微信支付",
                    state: true,
                    color:'#2dc800'
                }, {
                    icon: "zhifubao-circle-fill",
                    title: "支付宝支付",
                    state: true,
                    color:'#108ee9'
                }],
                payTypeIndex: 0,//当前支付选项
                isPayDisabled: false,
                wallet:0,//会员余额
                isMember:false,//是否是会员
            }
        },
        created() {
            // #ifdef MP-WEIXIN
            this.payType = [{
                    icon: "rmb-circle-fill",
                    title: "余额支付",
                    yue: '0',
                    state: true,
                    color:'#b09665'
                }, {
                    icon: "weixin-fill",
                    title: "微信支付",
                    state: true,
                    color:'#2dc800'
                }]
            // #endif
            // #ifdef MP-ALIPAY
            this.payType = [{
                    icon: "rmb-circle-fill",
                    title: "余额支付",
                    yue: '0',
                    state: true,
                    color:'#b09665'
                },{
                    icon: "zhifubao-circle-fill",
                    title: "支付宝支付",
                    state: true,
                    color:'#108ee9'
                }]
            // #endif
            this.isPayType()
        },
        methods:{
            isPayType(){
                this.isMember = false //判断是否会员--此处需您的接口获取
                this.wallet = 0 //当前会员余额--此处需您的接口获取
                this.upPay()
            },
            // 判断当前默认选择的支付选项
            upPay(){
                console.log('upPay')
                if(this.wallet && this.wallet>0){//如果是会员--有余额
                    // 判断支付金额是否大于余额
                    this.payType[0].yue = this.wallet;
                    var payMoney = parseFloat(this.payMoney);
                    if (payMoney > this.wallet) {
                        this.payType[0].state = false
                        this.payTypeIndex = 1
                    }else{
                        this.payType[0].state = true
                        this.payTypeIndex = 0
                    }
                }else{
                    this.payType[0].state = false
                }
                if(!this.payType[0].state){
                    console.log(!this.payType[0].state,'!this.payType[0].state')
                    // #ifdef MP-ALIPAY
                    this.payTypeIndex = 1
                    // #endif
                    // #ifdef MP-WEIXIN || APP-PLUS
                    this.payTypeIndex = 1
                    // #endif
                }
            },
            onPayTypeList(e){
                if(this.payType[e].title=="余额支付"){
                    if(this.isMember) { //判断是否是会员
                        // 判断支付金额是否大于余额
                        var payMoney = parseFloat(this.payMoney);
                        if (payMoney > this.wallet) {
                            this.payType[0].state = false;
                            // #ifndef APP-PLUS
                            uni.showModal({
                                title: '提示',
                                content: '余额不足,是否前去充值？',
                                success(res) {
                                    if (res.confirm) {
                                        console.log('用户点击确定')
                                        // uni.navigateTo({
                                        //     url: '/pages/member/MemberInfo',
                                        // })
                                    } else if (res.cancel) {
                                        console.log('用户点击取消')
                                    }
                                }
                            })
                            // #endif
                            // #ifdef APP-PLUS
                            this.$showModal({
                                    concent:'余额不足,是否前去充值？',
                                }).then(res=>{
                                    // uni.navigateTo({
                                    //     url: '/pages/member/MemberInfo',
                                    // })
                                }).catch(res=>{
                                    console.log('用户点击取消')
                                })
                            // #endif
                        }else{
                            this.payTypeIndex = e
                        }
                    } else {
                        this.$u.toast('暂不支持该支付方式')
                    }
                }else{
                    this.payTypeIndex = e
                }
            },
            // 去支付
            onPayButton: function() {
                var tradeNo = this.tradeNo;
                this.isPayDisabled = true
                if(this.payType[this.payTypeIndex].title == "微信支付") {
                    // #ifdef MP-WEIXIN
                    this.onPay('mpwxpay',tradeNo)
                    // #endif
                    // #ifdef H5
                    this.onH5WxPay(tradeNo)
                    // #endif
                    // #ifdef APP
                    this.onPay('wxpay',tradeNo)
                    // #endif
                }else if(this.payType[this.payTypeIndex].title == "支付宝支付") {
                    // #ifdef APP || MP
                    this.onPay('alipay',tradeNo)
                    // #endif
                    // #ifdef H5
                    this.$u.toast('h5支付宝支付正在开发中~')
                    this.isPayDisabled = false
                    // #endif
                }else{
                    // 会员钱包支付
                    this.onPayByShopMemberCard()
                }
                
            },
            //支付(app、小程序)
            onPay(type,tradeNo){
                var that = this;
            	setPay({
            		type: type,
            		tradeNo: tradeNo // 订单编号
            	},res => {
                    that.isPayDisabled = false
            		// 小程序支付的回调
            		if(res.success){
                        that.$u.toast('支付成功')
                        that.payResult(1)
            		}else{
                        that.$u.toast('支付失败')
                        that.payResult(0)
            		}
            	})
            },
            // 微信公众号支付
            onH5WxPay(tradeNo){
                var that = this
                wxPublicPay({
                    tradeNo:tradeNo,
                },res => {
                    that.isPayDisabled = false
                    // 支付的回调
                    if(res.success){
                        uni.$u.toast('支付成功')
                        that.payResult(1)
                    }else{
                        uni.$u.toast('支付失败')
                        that.payResult(0)
                    }
                })
            },
            // 会员钱包支付
            onPayByShopMemberCard(){
                // 这里会员余额支付详情----这里是您的钱包支付接口
                uni.showToast({
                    title: '会员支付',
                    icon: "none"
                })
            },
            // 支付统一回调
            payResult(payStatus){
                var goPayPageParameter = {
                    tradeNo: this.tradeNo, //订单号
                    payMoney: parseFloat(this.payMoney), //付款金额
                    payStatus: payStatus, //1成功、0失败 2正在查询支付结果3：支付结果未知
                }
                this.$emit('payResult',goPayPageParameter)
                console.log(goPayPageParameter, 'goPayPageParameter--')
                var goPayPageParameterData = JSON.stringify(goPayPageParameter)
                // uni.reLaunch({
                //     url: '/pages/pay/payResult?goPayPageParameterData=' + goPayPageParameterData,
                // })
            },
        }
    }
</script>

<style lang="scss" scoped>
.payTypeBox {
    .payHead {
        text-align: center;
        padding: 32rpx;
        border-bottom: 1rpx solid #eeeeee;
        .title {
            font-weight: bold;
            font-size: 30rpx;
        }
    }
}
.payCenter {
    .topInfo {
        margin-bottom: 82rpx;
        padding: 32rpx;
        /* #ifndef APP-NVUE */
        display: flex;
        /* #endif */
        flex-direction: column;
        align-items: center;
        .shopName {
            color: #999999;
            font-size: 28rpx;
        }
        .payMoney {
            /* #ifndef APP-NVUE */
            display: flex;
            /* #endif */
            flex-direction: row;
            align-items: flex-end;
            margin-top: 24rpx;
            .text {
                font-size: 40rpx;
                font-weight: bold;
            }
            .number {
                font-size: 70rpx;
                line-height: 70rpx;
                font-weight: bold;
            }
        }
    }
    .selPayType {
        padding: 0 40rpx 32rpx;
        .typeList {
            /* #ifndef APP-NVUE */
            display: flex;
            /* #endif */
            flex-direction: row;
            justify-content: space-between;
            align-items: center;
            padding: 24rpx 0;
            &.opacity {
                opacity: .4;
            }
            .left {
                /* #ifndef APP-NVUE */
                display: flex;
                /* #endif */
                flex-direction: row;
                align-items: center;
                .icon {
                    width: 44rpx;
                    height: 44rpx;
                    overflow: hidden;
                    /* #ifndef APP-NVUE */
                    display: flex;
                    /* #endif */
                    flex-direction: row;
                    justify-content: center;
                    align-items: center;
                    border-radius: 10rpx;
                }
                .word {
                    margin-left: 10rpx;
                    .title {
                        font-size: 28rpx;
                        font-weight: bold;
                        color: #333;
                    }
                }
            }
            .right {
                /* #ifndef APP-NVUE */
                display: flex;
                /* #endif */
                flex-direction: row;
                align-items: center;
                .yue {
                    color: #999;
                }
                .state {
                    margin-left: 16rpx;
                }
                .quan {
                    width: 36rpx;
                    height: 36rpx;
                    border-radius: 36rpx;
                    border: 1rpx solid #eee;
                    /* #ifndef APP-NVUE */
                    display: flex;
                    /* #endif */
                    flex-direction: row;
                    align-items: center;
                    justify-content: center;
                }
            }
        }
    }
    .payButton {
        padding: 32rpx;
        .button{
            .button-index-inlineflex {
                width: 100%;
                .btn_box {
                    width: 100%;
                }
            }
        }
        .payTips {
            margin-top: 32rpx;
            /* #ifndef APP-NVUE */
            display: flex;
            /* #endif */
            flex-direction: row;
            justify-content: center;
            align-items: center;
            .text {
                font-size: 24rpx;
                color: #999;
                margin-left: 8rpx;
            }
        }
    }
}
.Mask {
    position: fixed;
    left: 0;
    right: 0;
    top: 0;
    width: 100%;
    height: 100vh;
    z-index: 101;
}
</style>
