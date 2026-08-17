<template>
    <view :style="{height: isFixed ? height + 'px' : 'auto'}">
        <view :class="{'statusFixed':isFixed}" :style="{top:offsetTop+'rpx',zIndex: zIndex,width: width == 'auto' ? 'auto' : width + 'px',}" id="f__sticky">
            <slot></slot>
        </view>
    </view>
</template>

<script>
    export default {
        name: 'f-sticky',
        props:{
            // 距离顶部多高吸顶，单位rpx
            offsetTop: {
            	type: [Number, String],
            	default: 0
            },
            // 图层
            zIndex:{
                type: [Number,String],
                default: 10
            },
            // 滚动条参数
            scrollTop: {
            	type: Number,
            	default: 0
            },
            // 是否开启吸顶功能
            enable: {
            	type: Boolean,
            	default: true
            },
			// 因fixed定位忽略了h5导航栏高度，这里加上导航栏高度，不需要定义为0即可(非h5请忽略)
			navBarH: {
				type: [Number,String],
				default: 44
			},
        },
        watch:{
            scrollTop(val){
				console.log(val,this.viewH)
                if(val>this.viewH && this.enable){
                    this.isFixed = true
                }else{
                    this.isFixed = false
                }
            },
            enable(val){
            	if(val == false){
            		this.isFixed = false;
            	}
            }
        },
        data(){
            return {
                viewH:0,// 获取元素距离顶部的高度
                isFixed:false,//是否吸顶
                height: 'auto',
                width: 'auto',
            }
        },
        mounted(){
			this.$nextTick(()=>{
				this.getStickyH('#f__sticky')
			})
        },
        methods:{
            // 获取元素距离顶部的高度
            getStickyH(id) {
                var that = this;
                var query = uni.createSelectorQuery().in(that);
                query.selectViewport().scrollOffset()
                query.select(id).boundingClientRect();
                query.exec(function(res) {
					// #ifndef H5
					that.viewH = res[0].scrollTop + res[1].top - uni.upx2px(that.offsetTop);
					// #endif
					// #ifdef H5
					that.viewH = res[0].scrollTop + res[1].top - uni.upx2px(that.offsetTop) + that.navBarH;
					// #endif
                    that.height = res[1].height
                    that.width = res[1].width
                });
            },

        }
    }
</script>

<style lang="scss" scoped>
.statusFixed{
    position: fixed;
    top:0;
    left: 0;
    right: 0;
    z-index: 10;
}
</style>
