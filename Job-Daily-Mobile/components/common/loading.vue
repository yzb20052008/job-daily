<template>
	<view class="mask mask-show u_loading" v-if="loadingShow" @touchmove.stop.prevent="preventTouchMove">
		<!-- 加载动画开始 -->
		<view class="preloader">
		  <view class="loader">
              <view></view>
              <view></view>
              <view></view>
              <view></view>
              <view></view>
          </view>
		</view>
		<!-- 加载动画结束 -->
		<view class="title">加载中...</view>
	</view>
</template>

<script scoped="true">
import { mapState, mapMutations } from 'vuex';
export default {
	computed: {
		...mapState(['loadingShow'])
	},
	methods:{
		preventTouchMove(){
			console.log('stop user scroll it!');
			return;
		}
	}
};
</script>

<style lang="scss" scoped>
.u_loading{
    &.mask {
        /* pointer-events: none; */
        position: fixed;
        z-index: 99999;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        height: 100vh;
        width: 100vw;
        /* #ifndef APP-NVUE */
        display: flex;
        /* #endif */
        flex-direction: column;
        justify-content: center;
        align-items: center;
        flex-wrap: wrap;
    }
    &.mask-show {
        background: rgba(255,255,255, 0.3);
    }
    .title {
    	color: #666;
    	font-size: 28rpx;
    	margin-top: 40rpx;
    }
    .loader {
      width: 45px;
      position: relative;
    }
    .loader view {
      display: block;
      position: absolute;
      bottom: 0px;
      width: 7px;
      height: 5px;
      background: coral;
      -webkit-animation: loader 1.5s infinite ease-in-out;
      animation: loader 1.5s infinite ease-in-out;
    }
    .loader view:nth-child(2) {
      left: 11px;
      -webkit-animation-delay: 0.2s;
      animation-delay: 0.2s;
    }
    .loader view:nth-child(3) {
      left: 22px;
      -webkit-animation-delay: 0.4s;
      animation-delay: 0.4s;
    }
    .loader view:nth-child(4) {
      left: 33px;
      -webkit-animation-delay: 0.6s;
      animation-delay: 0.6s;
    }
    .loader view:nth-child(5) {
      left: 44px;
      -webkit-animation-delay: 0.8s;
      animation-delay: 0.8s;
    }
    
    @-webkit-keyframes loader {
      0% {
        height: 5px;
        -webkit-transform: translateY(0px);
        transform: translateY(0px);
        background: coral;
      }
      25% {
        height: 30px;
        -webkit-transform: translateY(15px);
        transform: translateY(15px);
        background: cornflowerblue;
      }
      50% {
        height: 5px;
        -webkit-transform: translateY(0px);
        transform: translateY(0px);
        background: cornflowerblue;
      }
      100% {
        height: 5px;
        -webkit-transform: translateY(0px);
        transform: translateY(0px);
        background: coral;
      }
    }
    
    @keyframes loader {
      0% {
        height: 5px;
        -webkit-transform: translateY(0px);
        transform: translateY(0px);
        background: coral;
      }
      25% {
        height: 30px;
        -webkit-transform: translateY(15px);
        transform: translateY(15px);
        background: cornflowerblue;
      }
      50% {
        height: 5px;
        -webkit-transform: translateY(0px);
        transform: translateY(0px);
        background: cornflowerblue;
      }
      100% {
        height: 5px;
        -webkit-transform: translateY(0px);
        transform: translateY(0px);
        background: coral;
      }
    }
    
}

</style>
