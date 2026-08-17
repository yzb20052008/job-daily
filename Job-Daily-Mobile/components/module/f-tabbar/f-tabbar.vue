<template>
    <view>
        <view :class="[isFixed?'f-fixed':'']">
            <!-- 二次封装tabbar -->
            <u-tabbar
            	:value="tabIndex"
            	@change="onTabbar"
            	:fixed="false"
            	:placeholder="false"
            	:safeAreaInsetBottom="safeAreaInsetBottom"
                :activeColor="activeColor || PrimaryColor"
                :inactiveColor="inactiveColor"
                :border="border"
            >   
                <block v-for="(item,index) in list" :key="index">
                    <!-- 自定义icon -->
                    <u-tabbar-item :text="item.name" :badge="item.badge" :dot="item.dot" :badgeStyle="item.badgeStyle">
                        <view slot="active-icon">
                            <view class="custom-icon" :class="['custom-icon-'+item.iconFill]" style="font-size: 20px;" :style="{color:activeColor || PrimaryColor}"></view>
                            <!-- 自定义图标 -->
							<!-- <f-icon :name="item.iconFill" size="40" :color="activeColor || PrimaryColor"></f-icon> -->
							<!-- 图片路径 -->
							<!-- <image class="icon" :src="item.iconFill"></image> -->
						</view>
                        <view slot="inactive-icon">
                            <view class="custom-icon" :class="['custom-icon-'+item.icon]" style="font-size: 20px;" :style="{color:inactiveColor}"></view>
                            <!-- 自定义图标 -->
							<!-- <f-icon :name="item.icon" size="40" :color="inactiveColor"></f-icon> -->
							<!-- 图片路径 -->
							<!-- <image class="icon" :src="item.icon"></image> -->
                        </view>
                    </u-tabbar-item>
                </block>
            </u-tabbar>
            <!-- 苹果安全距离-默认20px -->
            <view :style="{paddingBottom:systemInfo.tabbarPaddingB+'px',background:'#fff'}"></view>
        </view>
        <!-- 防止塌陷高度 -->
        <view v-if="isFixed && isFillHeight" :style="{height:systemInfo.tabbarH+'px'}"></view>
		<!-- #ifdef H5 -->
		<u-safe-bottom></u-safe-bottom>
		<!-- #endif -->
    </view>
</template>

<script>
import { mapState, mapMutations } from 'vuex';
import base from '@/config/baseUrl.js';
export default {
    name: 'f-tabbar',
    props:{
        // 是否固定在底部
        isFixed:{
            type:Boolean,
            default:true,
        },
        // 是否设置防止塌陷高度
        isFillHeight:{
            type:Boolean,
            default:true,
        },
        // 选中的颜色--为空显示主题色
        activeColor:{
            type:String,
            default:'',
        },
        // 未选中颜色
        inactiveColor:{
            type:String,
            default:'#606266',
        },
        // 是否显示边框色
        border:{
            type:Boolean,
            default: function() {
        		return true;
        	}
        },
        // 右上角的角标提示信息
        badge: {
            type: [String, Number, null],
            default: uni.$u.props.tabbarItem.badge
        },
        // 是否显示圆点，将会覆盖badge参数
        dot: {
            type: Boolean,
            default: uni.$u.props.tabbarItem.dot
        },
        // 控制徽标的位置，对象或者字符串形式，可以设置top和right属性
        badgeStyle: {
            type: [Object, String],
            default: uni.$u.props.tabbarItem.badgeStyle
        }
    },
	computed:{
	    ...mapState(['PrimaryColor']),
	},
    data(){
        return {
			safeAreaInsetBottom:false,
            systemInfo:base.systemInfo,
            tabIndex:0,
            path:'', //当前路径
            list:[{ 
                name: '首页',
                url: 'pages/index/index',
                icon: 'home',
                iconFill: 'home-filling'
            },
            {
                name: '商品',
                url: 'pages/goods/goods',
                icon: 'shangpin',
                iconFill: 'shangpin-filling',
                badge: 16
            },
            {
                name: '我的',
                url: 'pages/my/myInfo',
                icon: 'my',
                iconFill: 'my-filling',
                dot: true
            }]
        }
    },
    created() {
    	//获取页面路径
    	let currentPages = getCurrentPages();
    	let page = currentPages[currentPages.length - 1];
    	this.path = page.route;
        //获取页面路径
        this.list.forEach((item,index)=>{
            if(this.path == item.url){
                this.tabIndex = index
            }
        })
		// #ifdef H5
		this.safeAreaInsetBottom = true
		// #endif
    },
    methods:{
        onTabbar(index){
            if (this.path !== this.list[index].url) {
            	uni.switchTab({
            		url: '/' + this.list[index].url
            	});
            }
        }
    }
}
</script>

<style lang="scss" scoped>
.f-fixed{
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    z-index: 1000;
	.icon{
	    width: 20px;
	    height: 20px;
	}
}
</style>
