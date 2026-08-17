<!-- 自定义图标请参考https://v1.uviewui.com/guide/customIcon.html -->
<template>
    <view class="u-icon u-flex" @tap="clickHandler">
        <view :class="[customPrefix,customPrefix+'-'+name]" :style="{color:color,fontSize:size+'rpx',fontWeight:bold?'bold':'normal'}"></view>
    </view>
</template>

<script>
export default {
    name: 'f-icon',
    props: {
        // 图标类名
        name: {
        	type: String,
        	default: ''
        },
        // 图标颜色，可接受主题色
        color: {
        	type: String,
        	default: ''
        },
        // 字体大小，单位rpx
        size: {
        	type: [Number, String],
        	default: 'inherit'
        },
        // 自定义扩展前缀，方便用户扩展自己的图标库
        customPrefix: {
        	type: String,
        	default: 'custom-icon'
        },
		// 是否加粗
		bold: {
			type: Boolean,
			default: false
		},
		stop: {
        	type: Boolean,
        	default: true
        },
    },
    data() {
        return {

        };
    },
    methods: {
    	clickHandler(e) {
    		this.$emit('click')
            // 是否阻止事件冒泡
            this.stop && this.preventEvent(e)
    	},
        // 阻止事件冒泡
        preventEvent(e) {
            e && typeof (e.stopPropagation) === 'function' && e.stopPropagation()
        },
    }
};
</script>
<style lang="scss" scoped>
.u-flex{
    /* #ifndef APP-NVUE */
    display: flex;
    /* #endif */
    flex-direction: row;
    align-items: center;
}
.u-icon {
	display: inline-flex;
    justify-content: center;
}

</style>
