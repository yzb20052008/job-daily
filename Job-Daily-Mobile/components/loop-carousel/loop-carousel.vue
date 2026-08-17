<template>
	<swiper class="image-container" previous-margin="180rpx" next-margin="180rpx" circular @change="swiperChange"
		@animationfinish="animationfinish" :current="current" :duration="duration">
		<swiper-item :class="currentIndex == index ? 'swiper-item' : 'swiper-item-side'"
			v-for="(item, index) in imgList" :key="item[urlKey]">
			<image @click="clickImg(item)" :class="currentIndex == index ? 'item-img' : 'item-img-side'"
				:src="item[urlKey]" lazy-load :style="dontFirstAnimation ? 'animation: none;' : ''" mode="heightFix">
			</image>
		</swiper-item>
	</swiper>
</template>
<script>
	export default {
		props: {
			imgList: {
				type: Array,
				default () {
					return []
				}
			},
			urlKey: {
				type: String,
				default () {
					return ''
				}
			},
			current: { // 当前所在滑块的 index
				type: Number,
				default: 0
			},
			duration:{
				type: Number,
				default: 0
			}
		},
		data() {
			return {
				currentIndex: 0,
				dontFirstAnimation: true
			}
		},
		methods: {
			swiperChange(item) {
				this.dontFirstAnimation = false
				this.currentIndex = item.detail.current
				this.$emit('swiperChange', item, this.currentIndex)
			},
			clickImg(item) {
				this.$emit('selected', item, this.currentIndex)
			},
			animationfinish: function(e) {
				this.dontFirstAnimation = false;
				this.currentIndex = e.detail.current;
				this.$emit('animationfinish', e)
			}
		}
	}
</script>
<style scoped>
	.image-container {
		width: 750rpx;
		height: 666rpx;
	}

	.item-img {
		width: 375rpx;
		height: 666rpx;
		border-radius: 14rpx;
		animation: to-big .2s;
	}

	.swiper-item {
		width: 375rpx;
		height: 666rpx;
		display: flex;
		justify-content: center;
		align-items: center;
	}

	.item-img-side {
		width: 281rpx;
		height: 500rpx;
		border-radius: 14rpx;
		animation: to-mini .2s;
	}

	.swiper-item-side {
		width: 281rpx;
		height: 666rpx;
		display: flex;
		justify-content: center;
		align-items: center;
	}

	@keyframes to-mini {
		from {
			width: 375rpx;
			height: 666rpx;
		}

		to {
			width: 281rpx;
			height: 500rpx;
		}
	}

	@keyframes to-big {
		from {
			width: 281rpx;
			height: 500rpx;
		}

		to {
			width: 375rpx;
			height: 666rpx;
		}
	}
</style>
