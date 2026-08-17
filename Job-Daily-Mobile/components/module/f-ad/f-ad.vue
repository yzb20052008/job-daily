<template>
    <view>
        <!-- #ifdef MP-WEIXIN -->
        <view class="adContent">
            <block v-for="(item,index) in typeArr" :key="index">
                <block v-if="item===1">
                    <!-- banner广告 -->
					<view class="name">banner广告</view>
                    <ad unit-id="adunit-6c54818af3fc0507" ad-intervals="30"></ad>
                </block>
                <block v-if="item===2">
                    <!-- 视频广告 -->
					<view class="name">视频广告</view>
                    <ad unit-id="adunit-9986a8b7220e43c5" ad-type="video" ad-theme="white"></ad>
                </block>
                <block v-if="item===3">
                    <!-- 原生模板广告 -->
					<view class="name">原生模板广告</view>
                    <ad-custom unit-id="adunit-f3ffef7673d567eb"></ad-custom>
                </block>
				<block v-if="item===6">
				    <!-- 视频贴片广告 -->
					<view class="name">视频贴片广告</view>
				    <ad unit-id="adunit-d87f4c3c0fa3924b" ad-type="video" ad-theme="white"></ad>
				</block>
            </block>
        </view>
        <!-- #endif -->
    </view>
</template>

<script>
import { mapState, mapMutations } from 'vuex';
let rewardedVideoAd = null  // 定义激励视频广告
let insertScreenVideoAd = null // 定义插屏视频广告
export default {
    props:{
        //1.banner广告 2.视频广告 3.原生模板广告 4.插屏视频广告 5.激励视频广告 6.视频贴片广告
        typeArray:{
            type: Array,
            default: function() {
                return [];
            },
        }
    },
    data() {
        return {
            _isExcitationLoaded:false, //激励广告是否加载好
            _isInsertScreenLoaded:false,//插屏广告是否加载好
            typeArr:[1]
        };
    },
    created() {
        // #ifdef MP-WEIXIN
        this.typeArr = this.typeArray
        this.typeArr.forEach(item=>{
            if(item===5){
                this.excitationAd() //激励
            }else if(item===4){
                this.insertScreenAd() //插屏
            }
        })
        // #endif
    },
    mounted(e) {
        
    },
    //方法
    methods: {
        // 激励广告
        excitationAd(){
			const adUnitId = 'adunit-974e7ed6c92dbc32'
            this._isExcitationLoaded = false
            // 在页面onLoad回调事件中创建激励视频广告实例
            if (uni.createRewardedVideoAd) {
                rewardedVideoAd = uni.createRewardedVideoAd({
                    adUnitId: adUnitId
                })
                rewardedVideoAd.onLoad(() => {
                    this._isExcitationLoaded = true
					// this.showExcitationAd()
                })
                rewardedVideoAd.onError((err) => {})
                rewardedVideoAd.onClose((res) => {
                    // 用户点击了【关闭广告】按钮
                    if (res && res.isEnded) {
                        // 正常播放结束，可以下发游戏奖励
                        this.$emit('excitationAdCallback',1)
                    } else {
                        // 播放中途退出，不下发游戏奖励
                        this.$emit('excitationAdCallback',2)
                    }
                })
            }
        },
        // 显示激励广告
        showExcitationAd(){
            if (this._isExcitationLoaded) {
                rewardedVideoAd.show().catch(() => {
                // 失败重试
                rewardedVideoAd.load()
                    .then(() => rewardedVideoAd.show())
                    .catch(err => {
                        console.log(err,'激励视频 广告显示失败')
						this.$u.toast(err.errMsg)
                    })
                })
            }
        },
        // 插屏广告
        insertScreenAd(){
			const adUnitId = 'adunit-f0a7d292b52340d9'
            this._isInsertScreenLoaded = false
            // 在页面onLoad回调事件中创建激励视频广告实例
            if (uni.createInterstitialAd) {
                insertScreenVideoAd = uni.createInterstitialAd({
                    adUnitId: adUnitId
                })
                insertScreenVideoAd.onLoad(() => {
                    this._isInsertScreenLoaded = true
                    console.log('插屏广告加载完')
                    // this.showInsertScreenAd()
                })
                insertScreenVideoAd.onError((err) => {
                    console.log(err,'插屏广告拉取失败')
                })
                insertScreenVideoAd.onClose((res) => {
                    console.log('插屏广告关闭')
                })
            }
        },
        // 显示插屏广告
        showInsertScreenAd(){
            console.log('显示插屏广告')
            if (this._isInsertScreenLoaded) {
                insertScreenVideoAd.show().catch((err) => {
                    console.error(err,'插屏广告显示失败')
					this.$u.toast(err.errMsg)
                })
            }
        }
        
        
        
    }
};
</script>
<style lang="scss" scoped>
.adContent{
    width: 100%;
}
.name{
	padding: 10rpx 20rpx;
	color: #666;
	font-size: 24rpx;
}
</style>
