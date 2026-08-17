<template>
  <view ref="item"
    :class="[
      'lb-tabbar-item',
      'animate__animated',
      isActive ? 'lb-tabbar-item--active' : ''
    ]"
    :style="{
      paddingTop: paddingBT  + 'px',
      paddingBottom: paddingBT + 'px',
	  width:width
    }"
    @tap="handleTap">
    <view :class="[
        'lb-tabbar-item__icon',
        `lb-tabbar-item__icon--${raisede ? 'raisede' : 'default'}`,
        isAnimate ? `animate__animated animate__${tabbarInfo.animate}` : '',
      ]"
      :style="{
        height: tabbarInfo.iconSize,
        lineHeight: tabbarInfo.iconSize,
        transform: raisede ? `translateY(-${ty}px) scale(${tabbarInfo.raisedeScale})` : ''
      }">
      <!-- 图片图标 -->
	  <view v-if="raisede && isImage" class="lb-tabbar-item__imageview">
		  <image v-if="isImage"
		    class="lb-tabbar-item__image"
		    :src="icon"
		    :style="{
		      width: tabbarInfo.iconSize,
		      height: tabbarInfo.iconSize,
		  		  padding: raisede ?'4px':''
		    }">
		  </image>
	  </view>
	  <image v-else-if="isImage"
	    class="lb-tabbar-item__image"
	    :src="icon"
	    :style="{
	      width: tabbarInfo.iconSize,
	      height: tabbarInfo.iconSize,
	  		  padding: raisede ?'4px':''
	    }">
	  </image>

      <!-- icon图标 -->
      <text v-else
        :class="[
          'lb-tabbar-icon',
          iconPrefix,
          `${iconPrefix}-${icon}`,
          isActive ? 'lb-tabbar-item__icon--active' : ''
        ]"
        :style="{
          width: tabbarInfo.iconSize,
          height: tabbarInfo.iconSize,
          lineHeight: tabbarInfo.iconSize,
          fontSize: tabbarInfo.iconSize,
          color: isActive ? tabbarInfo.activeColor : tabbarInfo.inactiveColor
        }">{{ iconCode }}</text>

      <!-- 非nvue dot info显示 -->
      <!-- #ifndef APP-NVUE -->
      <text v-if="dot || hasInfo"
        :class="[
          dot && !hasInfo ? 'lb-tabbar-item__dot' : '',
          hasInfo ? 'lb-tabbar-item__dot--info' : '',
          'lb-tabbar-item__dot--style'
        ]"
        :style="{
          backgroundColor: tabbarInfo.dotColor
        }">{{ hasInfo ? info : '' }}</text>
      <!-- #endif -->
    </view>

    <!-- nvue dot info 显示 -->
    <!-- #ifdef APP-NVUE -->
    <text v-if="dot || hasInfo"
      :class="[
        'lb-tabbar-item__dot--nvue',
        'lb-tabbar-item__dot--style',
        dot && !hasInfo ? 'lb-tabbar-item__dot' : '',
        hasInfo ? 'lb-tabbar-item__dot--info' : '',
        hasInfo ? 'lb-tabbar-item__dot--info' : '',
        nvueDotShow ? 'lb-tabbar-item__dot--show' : ''
      ]"
      :style="{
        backgroundColor: tabbarInfo.dotColor,
        top: paddingBT + raisedeHeight + 'px',
        right: dotLeft + 'px'
      }">{{ hasInfo ? info : '' }}</text>
    <!-- #endif -->

    <!-- 非nvue图标文字 -->
    <!-- #ifndef APP-NVUE -->
    <view :class="[
        'lb-tabbar-item__text',
        isActive ? 'lb-tabbar-item__text--active' : ''
      ]"
      :style="{
        fontSize: tabbarInfo.textSize,
        lineHeight: tabbarInfo.textSize,
        maxHeight: tabbarInfo.textSize,
        marginTop: tabbarInfo.textTop,
        color: isActive
          ? tabbarInfo.activeTextColor || tabbarInfo.activeColor
          : tabbarInfo.inactiveTextColor || tabbarInfo.inactiveColor
      }">
      <slot></slot>
      <view v-if="raisede"
        class="lb-tabbar-item__text--block"
        :style="{
          height: tabbarInfo.textSize
        }">
      </view>
    </view>
    <!-- #endif -->

    <!-- nvue图标文字 -->
    <!-- #ifdef APP-NVUE -->
    <text v-if="text || raisede"
      :class="[
        'lb-tabbar-item__text',
        isActive ? 'lb-tabbar-item__text--active' : ''
      ]"
      :style="{
        fontSize: tabbarInfo.textSize,
        height: tabbarInfo.textSize,
        lineHeight: tabbarInfo.textSize,
        marginTop: tabbarInfo.textTop,
        color: isActive
          ? tabbarInfo.activeTextColor || tabbarInfo.activeColor
          : tabbarInfo.inactiveTextColor || tabbarInfo.inactiveColor
      }">{{ text || '' }}</text>
    <!-- #endif -->
  </view>
</template>

<script>
// #ifdef APP-NVUE
const dom = uni.requireNativePlugin('dom')
// #endif
import { getPx } from './utils'
export default {
  props: {
    name: [String, Number],
    text: [String, Number],
    icon: String,
    iconPrefix: String,
    dot: Boolean,
    info: [String, Number],
    raisede: Boolean,
	width: String,
  },
  inject: ['tabbar'],
  data () {
    return {
      tabbarInfo: {},
      itemWidth: 0,
      dotLeft: 0,
      nvueDotShow: false
    }
  },
  computed: {
    isImage () {
      return this.icon && this.icon.indexOf('/') > -1
    },
    isActive () {
      return this.tabbarInfo.value === this.name
    },
    isAnimate () {
      return (
        this.isActive &&
        this.tabbarInfo.animate &&
        !(this.raisede && this.tabbarInfo.closeAnimateOnRaisede)
      )
    },
    height () {
      return getPx(this.tabbarInfo.height)
    },
    iconHeight () {
      return getPx(this.tabbarInfo.iconSize)
    },
    textSize () {
      return getPx(this.tabbarInfo.textSize)
    },
    textTop () {
      return getPx(this.tabbarInfo.textTop)
    },
    ty () {
      return this.height / 2 - (this.textSize + this.textTop) / 2
    },
    iconCode () {
      let code = ''
      // #ifdef APP-NVUE
      code = this.icon
      // #endif
      return code
    },
    hasInfo () {
      return this.info
	  // return this.info || this.info === 0
    },
    paddingBT () {
      return (this.height - this.iconHeight - this.textSize - this.textTop) / 2
    },
    hasRaisede () {
		console.log("hasRaisede",this.tabbar)
		if(!this.tabbar){
			return false;
		}
        return this.tabbar.hasRaisede
    },
    raisedeHeight () {
	  console.log("====this.name====",this.name)
	  console.log("====this.raisede====",this.raisede)
      return this.raisede ? this.iconHeight * this.tabbarInfo.raisedeScale / 2 : 0 // 凸起高度
    },
		infoLength () {
			return this.hasInfo ? (this.info + '').length : 0
		}
  },
  created () {
    this.tabbarInfo = this.tabbar._props
    this.tabbar.tabbarItems.push(this._props)
  },
  mounted() {
		// #ifdef APP-NVUE
    this.setNvueDot()
		// #endif
  },
  methods: {
    handleTap () {
      this.tabbar.active = this.name
      this.$emit('click', this._props)
    },
		// #ifdef APP-NVUE
    setNvueDot () {
			if (this.dot || this.hasInfo) {
				this.$nextTick(() => {
					const $el = this.$refs.item
					dom.getComponentRect($el, res => {
						this.itemWidth = res.size.width
            const halfWidth = this.itemWidth / 2
            if (this.dot) {
              this.dotLeft = halfWidth - 8
            }
            if (this.hasInfo) {
              const length = this.infoLength > 1 ? this.infoLength -1 : this.infoLength
              this.dotLeft = halfWidth - 8 * length
            }
						this.nvueDotShow = true
					})
				})
			}
    }
		// #endif
  },
	// #ifdef APP-NVUE
  watch: {
    dot () {
      this.setNvueDot()
    },
    info () {
      this.setNvueDot()
    }
  }
	// #endif
}
</script>

<style lang="scss">
@import "./style.scss";
/* #ifndef APP-NVUE */
@import "./animate.scss";
/* #endif */
</style>
