<template>
    <view class="f-navbar">
        <view :class="[navbarType==1?'f-relative':'f-fixed']" :style="[navbarType==1?{height:systemInfo.navBarH+'px'}:{paddingTop:systemInfo.statusBarHeight+'px'}]">
            <!-- 背景色 -->
            <view class="bgColor" :style="{height:systemInfo.navBarH+'px',background: bgColor,backgroundImage: gradient, opacity: navbarType == 5 ? transparentValue:''}">
                <image :src="image" :mode="imageMode" style="width: 750rpx;" :style="{height:systemInfo.navBarH+'px'}" v-if="image"></image>
            </view>
            <view class="u-flex contents" :style="{height:systemInfo.titleBarHeight+'px',top:systemInfo.statusBarHeight+'px'}">
				<!-- #ifndef MP-BAIDU -->
                <block v-if="navbarType!=4 && isShowLeft">
                    <view class="u-flex left" hover-class="left--hover" hover-start-time="150" v-if="navbarType==3">
                        <view class="u-flex u-home-arrow-left" :style="{borderColor:isWhite?'rgba(255,255,255,.5)':'#dadbde'}">
                            <view @click="onBack">
                                <u-icon name="arrow-left" size="19" :color="leftColor"></u-icon>
                            </view>
                            <u-line direction="column" :hairline="false" length="16" margin="0 8px" :color="isWhite?'rgba(255,255,255,.3)':'#dadbde'"></u-line>
                            <view @click="onHome">
                                <u-icon name="home" size="20" :color="leftColor"></u-icon>
                            </view>
                        </view>
                    </view>
                    <view class="u-flex left" hover-class="left--hover" hover-start-time="150" @click="leftClick" v-else>
                        <slot name="left">
                            <!-- 左边箭头背景渐变 -->
                            <view class="iconBg u-flex" :style="{background: 'rgba(0,0,0,'+(navbarType == 5 ?(transparentValue2>0.4?0.4:transparentValue2):0)+')'}">
                                <u-icon :name="iconLeft" :size="iconLeft=='home'?20:16" :color="leftColor"></u-icon>
                            </view>
                            <text v-if="leftText" class="left__text" :style="{color: leftColor}">{{ leftText }}</text>
                        </slot>
                    </view>
                </block>
				<!-- #endif -->
                <view class="center" :style="{width:titleWidth+'rpx'}">
                    <slot name="default">
						<block v-if="navbarType == 5 && transparentTitleColor && isShowTransparentTitle">
							<!-- 透明背景时实现上拉title颜色改变 -->
							<view class="title u-line-1" :style="[{opacity: transparentValue2},{color:transparentTitleColor,width:titleWidth+'rpx',fontSize:fontSize+'rpx'}]" v-if="transparentValue<proportion">{{title}}</view>
							<view class="title u-line-1" :style="[{opacity: transparentValue},{color:fontColor,width:titleWidth+'rpx',fontSize:fontSize+'rpx'}]" v-else>{{title}}</view>
						</block>
                        <view v-else class="title u-line-1" :style="[isShowTransparentTitle?{}:{opacity: navbarType == 5 ? transparentValue :''},{color:fontColor,width:titleWidth+'rpx',fontSize:fontSize+'rpx'}]">{{title}}</view>
                    </slot>
                </view>
                <view class="u-flex right" @click="rightClick">
                    <slot name="right"></slot>
                </view>
            </view>
        </view>
        <!-- 防止塌陷高度 -->
        <view v-if="isFillHeight && navbarType!=5 && navbarType!=1" :style="{height:systemInfo.navBarH+'px'}"></view>
    </view>
</template>

<script>
// 主页页面的页面路径
// 关联功能：打开的页面只有一个的时候右上角自动显示返回首页按钮，下面这个数组是排除显示返回首页的页面。
// 主页使用场景：小程序分享出去的页面，用户点击开是分享页面，很多情况下是没有返回首页按钮的
const mainPagePath = ['pages/index/index','pages/goods/goods','pages/my/myInfo'];
//返回首页的地址
const homePath = '/pages/index/index';
//白色表达值
const whiteList = ['#FFF', '#fff', '#FFFFFF', '#ffffff', 'white', 'rgb(255,255,255)', 'rgba(255,255,255,1)'];
const colorTheme = '#303133'; //字体、左边箭头颜色等默认颜色
// import base from '@/config/baseUrl.js';
export default {
    name: 'f-navbar',
    props:{
        title:{
            type:String,
        },
        // navbarType等于5透明背景时title是否显示
        isShowTransparentTitle:{
            type:Boolean,
            default: function() {
        		return true;
        	}
        },
        // 左边文字
        leftText:{
            type:String
        },
        // 背景颜色
        bgColor:{
            type:String,
            default:'#fff'
        },
        // 背景图片
        image:{
            type:String
        },
        // 背景图片mode
        imageMode:{
            type:String,
            default:'aspectFill'
        },
        // 导航状态 0、默认固定在顶部 1、不固定在顶部 2、自定义点击事件 3、同时显示箭头和去首页按钮 4、不显示左侧内容 5、上拉渐变显示背景色
        navbarType:{
            type:[String,Number],
            default: 0
        },
        // 是否显示左侧内容
        isShowLeft:{
            type:Boolean,
            default: function() {
        		return true;
        	}
        },
        // 左边按钮icon
        leftIcon:{
            type:String,
            default: 'arrow-left'
        },
        // 左边icon颜色
        leftIconColor: {
        	type: String,
        	default: colorTheme
        },
        // 屏幕滑动距离顶部距离(透明固定导航比传)
        scrollTop: {
        	type: [String,Number],
        	default: 0
        },
        //导航字体颜色，字体颜色为白色的时候会把手机状态栏设置为白色，否则为黑色
        fontColor: {
        	type: String,
        	default: colorTheme
        },
		// navbarType等于5透明背景时title颜色
		transparentTitleColor: {
        	type: String
        },
        titleWidth: {
        	type: [String,Number],
        	default: 400
        },
        fontSize: {
        	type: [String,Number],
        	default: 30
        },
		// 背景渐变色
        gradient: {
        	type: String
        },
        // 是否设置防止塌陷高度
        isFillHeight:{
            type:Boolean,
            default: function() {
        		return true;
        	}
        },
    },
    data(){
        return {
            // systemInfo:base.systemInfo,
            systemInfo:{
                statusBarHeight:uni.getSystemInfoSync().statusBarHeight,
                // #ifdef MP-ALIPAY
                navBarH: uni.getSystemInfoSync().statusBarHeight + uni.getSystemInfoSync().titleBarHeight, //菜单栏总高度--单位px
                titleBarHeight: uni.getSystemInfoSync().titleBarHeight, //标题栏高度--单位px
                // #endif
                // #ifndef MP-ALIPAY
                navBarH: uni.getSystemInfoSync().statusBarHeight + 44, //菜单栏总高度--单位px
                titleBarHeight: 44, //标题栏高度--单位px
                // #endif
            },
            //当前页面是否是第一个页面
            firstPage: false,
            //透明度值
            transparentValue: 0,
            iconLeft: 'arrow-left',//左边icon
            // 上次显示的导航栏颜色
            lastFrontColor: '',
            //字体色
            // navFontColor: colorTheme,
            isWhite:false,//是否白色
			proportion:0.5,//navbarType==5发生渐变的比值
			oldLeftIconColor:colorTheme,//首次获取的左边icon颜色
        }
    },
    watch: {
		leftIconColor: {
            immediate: true,
            handler (val) {
				if(val!=colorTheme && this.oldLeftIconColor==colorTheme){
					this.oldLeftIconColor = val
				}
            }
        },
        leftIcon: {
            immediate: true,
            handler (val) {
                this.iconLeft = val
            }
        },
        // fontColor: {
        //     immediate: true,
        //     handler (val) {
        //         this.navFontColor = val;
        //         this.settingColor();
        //     }
        // },
        navbarType: {
            immediate: true,
            handler (val) {
                if(val == 5){
                    if (this.scrollTop > 180) {
                    	this.transparentValue = 1;
                    } else {
                    	this.transparentValue = this.scrollTop / 180;
                    }
                }
            }
        },
        scrollTop(val) {
        	this.pageScroll({
        		scrollTop: val
        	});
        }
    },
    computed:{
        leftColor(){
			var color2 = colorTheme
            if(this.leftIconColor==color2){//如果等于默认值
                // 背景不是白色或者背景是渐变色
                if(!whiteList.includes(this.bgColor) || this.gradient || this.navbarType == 5){
                    color2 = '#fff'
                }
            }else{
                color2 = this.leftIconColor
            }
            return color2
        },
		// 字体色
		navFontColor(){
			var n = colorTheme
			if(this.transparentValue>this.proportion){
				n = this.fontColor
			}else{
				n = this.transparentTitleColor || this.fontColor
			}
			return n
        },
		// 字体透明度
		transparentValue2(){
			var val = 1-this.transparentValue
			return val
		},
    },
    created() {
        this.pageScroll({
        	scrollTop: this.scrollTop
        });
        //获取所有页面
        let currentPages = getCurrentPages();
        let pageLen = currentPages.length;
        //判断是否是第一个页面，如果是有设置back为true的页面，将不显示返回箭头，而显示返回首页按钮
        if (pageLen == 1 && !mainPagePath.includes(currentPages[0].route)) {
        	this.firstPage = true;
            this.iconLeft = 'home'
            console.log(this.firstPage,'this.firstPage')
        }
    },
    methods:{
        onBack(){
            if(this.firstPage){
                uni.reLaunch({
                	url: homePath
                });
            }else{
                uni.navigateBack();
            }
        },
        onHome(){
            uni.switchTab({
            	url: homePath
            });
        },
        leftClick(){
            if(this.navbarType == 2){
                this.$emit('leftClick');
            }else{
                this.onBack()
            }
        },
        rightClick(){
            this.$emit('rightClick');
        },
        pageScroll(e) {
        	if (this.navbarType == 5) {
        		if (e.scrollTop && e.scrollTop > 0) {
        			if (e.scrollTop > 180) {
        				this.transparentValue = 1;
        			} else {
        				this.transparentValue = e.scrollTop / 180;
        			}
        		} else {
        			this.transparentValue = 0;
        		}
				
				if (whiteList.includes(this.bgColor)) {
					if(this.transparentValue>this.proportion){
						// this.leftIconColor = this.$u.colorToRgba(this.$u.rgbToHex(this.oldLeftIconColor),this.transparentValue)
						this.leftIconColor = this.colorToRGB(this.oldLeftIconColor, this.transparentValue)
					}else{
						this.leftIconColor = `rgba(255,255,255,${1-this.transparentValue})`
					}
				}
        	}
			this.settingColor();
        },
        //设置手机状态栏颜色
        settingColor() {
        	let navColor = this.navFontColor;
        	let frontColor = '#000000';
        	if (whiteList.includes(navColor)) {
        		frontColor = '#ffffff';
                this.isWhite = true
        	}
        	if (this.lastFrontColor == frontColor) {
        		return;
        	}
        	setTimeout(() => {
        		this.lastFrontColor = frontColor;
        		// 改变手机状态栏颜色
                // #ifndef MP-ALIPAY
        		uni.setNavigationBarColor({
        			frontColor: frontColor,
        			backgroundColor: '#FFFFFF'
        		});
                // #endif
                // #ifdef MP-ALIPAY
                uni.setNavigationBarColor({
                	backgroundColor: frontColor == '#000000'?'#ffffff':'#000000'
                });
                // #endif
        	}, 150);
            
        },
		// 主题色颜色转换RGB（一般用于主题色有透明度时）
		colorToRGB: function(val, opa) {
		    const pattern = /^(#?)[a-fA-F0-9]{6}$/; //16进制颜色值校验规则
		    const flag = typeof opa == 'number'; //判断是否有设置不透明度
		    if (!pattern.test(val)) { //如果值不符合规则返回空字符
		        return '';
		    }
		    let v = val.replace(/#/, ''); //如果有#号先去除#号
		    let rgbArr = [];
		    let rgbStr = '';
		    for (let i = 0; i < 3; i++) {
		        let item = v.substring(i * 2, i * 2 + 2);
		        let num = parseInt(item, 16);
		        rgbArr.push(num);
		    }
		    rgbStr = rgbArr.join();
		    rgbStr = 'rgb' + (flag ? 'a' : '') + '(' + rgbStr + (flag ? ',' + opa : '') + ')';
		    return rgbStr;
		},
    }
}
</script>

<style scoped lang="scss">
.u-flex{
    /* #ifndef APP-NVUE */
    display: flex;
    /* #endif */
    flex-direction: row;
    align-items: center;
}
.f-navbar{
    width: 750rpx;
    .f-fixed{
        position: fixed;
        top: 0;
        left: 0;
        right: 0;
        z-index: 1000;
    }
    .f-relative{
        position: relative;
    }
    .bgColor{
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
    }
    .contents{
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
    }
    .title{
        text-align: center;
        color: #303133;
    }
    .center{
        margin: 0 auto;
    }
    .left,.right{
        padding: 0 24rpx;
        position: absolute;
        top: 0;
        bottom: 0;
    }
    .left{
        left: 0;
        &--hover {
        	opacity: 0.7;
        }
        &__text{
            font-size: 30rpx;
            margin-left: 3px;
        }
        .iconBg{
            width: 50rpx;
            height: 50rpx;
            background: rgba(0,0,0,.4);
            border-radius: 25rpx;
            justify-content: center;
        }
    }
    .right{
        right: 0;
    }
    .u-home-arrow-left{
        justify-content: space-between;
        border-radius: 100px;
        padding: 3px 7px;
        opacity: .8;
        border: .5px solid #dadbde;
    }
}
</style>
