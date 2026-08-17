<template>
	<view class="y-case-item">
		<uni-list class="uni-list--waterfall">
			<!-- to 属性携带参数跳转详情页面，当前只为参考 -->
			<uni-list-item
				:border="false"
				class="uni-list-item--waterfall"
				title="自定义商品列表"
				v-for="item in caseList"
				:key="item.id"
				:to="url+'?id=' + item.id + '&title=' + item.name"
			>
				<template v-slot:header>
					<view class="uni-thumb shop-picture shop-picture-column"><image :src="item.avatar" mode="aspectFill"></image></view>
				</template>
				<template v-slot:body>
					<view class="shop">
						<view class="uni-title" style="margin-bottom: 0;">
							<text class="uni-ellipsis-2">{{ item.title }}</text>
						</view>
						<view class="uni-note uni-ellipsis-1" style="margin-top: 0;">{{ item.excerpt }}</view>
					</view>
				</template>
			</uni-list-item>
		</uni-list>
	</view>
</template>

<script>
export default {
	name: 'y-case-item',
	options: { styleIsolation: 'shared' },
	props: {
		caseList: {
			type: Array
		},
		url: {
			type: String
		}
	},
	data() {
		return {};
	}
};
</script>

<style lang="scss" scoped>
@import '@/common/uni-ui.scss';

.y-case-item {
	display: flex;
	flex-direction: column;
	box-sizing: border-box;

	.ellipsis {
		display: flex;
		overflow: hidden;
	}
	
	.uni-ellipsis-1 {
		overflow: hidden;
		text-overflow: ellipsis;
		display: -webkit-box;
		-webkit-line-clamp: 1;
		-webkit-box-orient: vertical;
	}

	.uni-ellipsis-2 {
		overflow: hidden;
		text-overflow: ellipsis;
		display: -webkit-box;
		-webkit-line-clamp: 2;
		-webkit-box-orient: vertical;
	}

	.shop {
		flex: 1;
		display: flex;
		flex-direction: column;
		justify-content: space-between;
	}

	.shop-picture {
		width: 100px;
		height: 100px;
	}

	.shop-picture-column {
		width: 100%;
		height: 100px;
		margin-bottom: 10px;
	}

	.shop-price {
		margin-top: 5px;
		font-size: 12px;
		color: #ff5a5f;
	}

	.shop-price-text {
		font-size: 16px;
	}

	.uni-list--waterfall {
		/* #ifndef H5 || APP-VUE */
		// 小程序 编译后会多一层标签，而其他平台没有，所以需要特殊处理一下
		::v-deep .uni-list {
			/* #endif */
			display: flex;
			flex-direction: row;
			flex-wrap: wrap;
			padding: 5px;
			box-sizing: border-box;

			/* #ifdef H5 || APP-VUE */
			// h5 和 app-vue 使用深度选择器，因为默认使用了 scoped ，所以样式会无法穿透
			::v-deep
				/* #endif */
				  .uni-list-item--waterfall {
				width: 50%;
				box-sizing: border-box;
				margin-top: 20upx;
				.uni-list-item__container {
					display: flex;
					padding: 5px;
					flex-direction: column;
				}
			}

			/* #ifndef H5 || APP-VUE */
		}
		/* #endif */
	}
	
	.uni-title{
		font-weight: normal;
	}
}
</style>
