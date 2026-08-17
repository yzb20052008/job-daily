<template>
	<view class="page">
		<!-- 公共组件-每个页面必须引入 -->
		<public-module></public-module>
        <view class="f__login">
            <view class="logo">
                <image class="img" src="/static/logo.png"></image>
            </view>
            <view class="title">欢迎登录~</view>
            <view class="desc">会员用户登录后消费可享受折扣，享受更好的服务体验</view>
            <!-- 验证码登录 -->
            <view class="loginPhone">
                <view class="form-row">
                    <input class="input" type="number" v-model="phone" placeholder="请输入手机号码" placeholder-style="font-weight:normal;color:#bbbbbb;"></input>
                </view>
                <view class="form-row">
                    <input class="input" type="number" v-model="vCode" placeholder="请输入验证码" placeholder-style="font-weight:normal;color:#bbbbbb;"></input>
                    <view class="getvcode" :class="{forhidden:readonly}" @click="getVcode">{{ codeText }}</view>
                </view>
                <button class="submit" size="default" @click="onSubmit" :style="{background:PrimaryColor}">确定</button>
                <view class="agreement u-flex">
                    <view class="icon u-flex" :style="{borderColor:isAgree?PrimaryColor:'#eee'}" @click="isAgree = !isAgree">
                        <view v-if="isAgree">
                            <u-icon name="checkbox-mark" :size="14" :color="PrimaryColor"></u-icon>
                        </view>
                    </view>
                    <view class="text">我已同意</view>
                    <view class="protocol" @click="onJump('/pages/user/protocol')">《用户协议》</view>
                </view>
            </view>
        </view>
        <!-- #ifdef APP-PLUS -->
        <view class="otherLogin">
            <view style="padding: 0 150rpx;">
                <u-divider text="其他登录方式"></u-divider>
            </view>
            <view class="info u-flex">
                <view @click="otherLogin('weixin')">
                    <u-icon name="weixin-circle-fill" size="40" color="#1fba1a"></u-icon>
                </view>
                <view @click="otherLogin('qq')">
                    <u-icon name="qq-circle-fill" size="40" color="#333"></u-icon>
                </view>
                <!-- <view>
                    <u-icon name="zhifubao-circle-fill" size="40" color="#007AFF"></u-icon>
                </view> -->
                <view @click="otherLogin('apple')">
                    <u-icon name="apple-fill" size="40" color="#333"></u-icon>
                </view>
            </view>
        </view>
        <!-- #endif -->
	</view>
</template>

<script>
var clear;
import { mapState, mapMutations } from 'vuex';
import { loginApp } from '@/config/login';
export default {
	data() {
		return {
            readonly: false,
            codeText: '获取验证码',
            phone: '', //号码
            vCode: '', //验证码
            isAgree: false, //是否同意协议
		};
	},
	computed:{
	    ...mapState(['PrimaryColor'])
	},
	//第一次加载
	onLoad(e) {
        
	},
	//页面显示
	onShow() {},
	//方法
	methods: {
        ...mapMutations(['setUserInfo']),
        onJump(url){
            uni.navigateTo({
                url:url
            })
        },
        //验证码按钮文字状态
        getCodeState() {
        	const _this = this;
        	this.readonly = true;
        	this.codeText = '60S后重新获取';
        	var s = 60;
        	clear = setInterval(() => {
        		s--;
        		_this.codeText = s + 'S后重新获取';
        		if (s <= 0) {
        			clearInterval(clear);
        			_this.codeText = '获取验证码';
        			_this.readonly = false;
        		}
        	}, 1000);
        },
        //获取验证码
        getVcode(){
            console.log('getVcode')
        	if (this.readonly) {
                uni.showToast({
                	title: '验证码已发送~',
                	icon: 'none'
                });
        		return;
        	}
        	if (this.phone == '') {
                uni.showToast({
                	title: '请输入手机号~',
                	icon: 'none'
                });
        		return;
        	}
            const phoneRegular = /^1\d{10}$/;
        	if (!phoneRegular.test(this.phone)) {
                uni.showToast({
                	title: '手机号格式不正确~',
                	icon: 'none'
                });
        		return;
        	}
            let httpData = {}
            // 获取验证码接口
            // uni.$u.http.post('您的接口', httpData).then(res => {
                this.getCodeState(); //开始倒计时
            // })
        },
        // 手机号登录
        onSubmit() {
        	if (this.phone == '') {
                uni.showToast({
                	title: '请输入手机号~',
                	icon: 'none'
                });
        		return;
        	}
            const phoneRegular = /^1\d{10}$/;
        	if (!phoneRegular.test(this.phone)) {
                uni.showToast({
                	title: '手机号格式不正确~',
                	icon: 'none'
                });
        		return;
        	}
        	if (this.vCode == '') {
                uni.showToast({
                	title: '请输入验证码~',
                	icon: 'none'
                });
        		return;
        	}
            if (!this.isAgree) {
                uni.showToast({
                	title: '请同意用户协议~',
                	icon: 'none'
                });
            	return;
            }
        	let httpData = {};
        	// uni.$u.http.post('您的接口',httpData).then(res => {
                // 储存登录信息
                // let userInfo = {
                //     ...res,
                //     token:true,//token用于判断是否登录
                // }
                // this.setUserInfo(userInfo)
                uni.showToast({
                	title: '登录成功~',
                	icon: 'none'
                });
                setTimeout(()=>{
                    uni.navigateBack()
                },500)
            // });
        },
        //第三方登录
        otherLogin(e){
            loginApp(e,httpData=>{
                console.log(httpData,'httpData')
                // uni.$u.http.post('您的接口',httpData).then(res => {
                    // 储存登录信息
                    // let userInfo = {
                    //     ...res,
                    //     token:true,//token用于判断是否登录
                    // }
                    // this.setUserInfo(userInfo)
                    uni.showToast({
                    	title: '登录成功~',
                    	icon: 'none'
                    });
                    setTimeout(()=>{
                        uni.navigateBack()
                    },500)
                // });
            })
        }
    
	}
};
</script>
<style lang="scss" scoped>
.page{
    min-height: 100vh;
    background-color: #fff;
}
.f__login {
    padding: 48rpx 32rpx;
    border-radius: 18rpx 18rpx 0 0;
    z-index: 99;
    position: relative;
    .logo {
        width: 90rpx;
        height: 90rpx;
        border-radius: 18rpx;
        overflow: hidden;
        .img {
            width: 90rpx;
            height: 90rpx;
        }
    }
    .title {
        font-size: 40rpx;
        font-weight: bold;
        margin-top: 24rpx;
    }
    .desc {
        font-size: 24rpx;
        color: #666;
        margin-top: 16rpx;
    }
}
.loginPhone{
    .form-row {
        position: relative;
        border-bottom: 1rpx solid #e8e8e8;
        line-height: 1;
        margin-top: 24rpx;
        .input{
            font-size: 34rpx;
            line-height: 102rpx;
            height: 94rpx;
            width: 100%;
            box-sizing: border-box;
            font-size: 30rpx;
            padding: 0;
            font-weight: bold;
        }
        .getvcode {
            font-size: 26rpx;
            height: 80rpx;
            color: #333;
            line-height: 80rpx;
            background: #eee;
            min-width: 188rpx;
            text-align: center;
            border-radius: 8rpx;
            position: absolute;
            top: 50%;
            transform: translateY(-50%);
            right: 0;
            z-index: 11;
            &.forhidden {
                background: #eee;
                color: #cccccc;
            }
        }
    }
    .submit{
        margin-top: 60rpx;
        color: #fff;
        width: 100%;
        border: none;
    }
    .agreement{
        padding-top: 24rpx;
        .icon{
            width: 36rpx;
            height: 36rpx;
            border-radius: 36rpx;
            border: 1rpx solid #eee;
            justify-content: center;
        }
        .text{
            padding-left: 10rpx;
            font-size: 26rpx;
            color: #999;
        }
        .protocol{
            font-size: 26rpx;
            color: #333;
        }
    }
}
.otherLogin{
    position: fixed;
    left: 0;
    right: 0;
    bottom: 70rpx;
    .info{
        justify-content: space-around;
        padding: 32rpx;
    }
}
</style>
