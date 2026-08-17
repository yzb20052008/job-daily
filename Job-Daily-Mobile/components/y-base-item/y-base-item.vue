<template>
	<view class="y-base-item">
		<uni-list>
			<!-- to属性：将新闻ID和标题传给详情页 -->
			<!-- TODO：需增加日期格式化的示例 -->
			<!-- to 属性携带参数跳转详情页面，当前只为参考 -->
			<uni-list-item
				:border="true"
				direction="row"
				v-for="item in list"
				ellipsis="2"
				:key="item.id"
				:to="url + '?id=' + item.id + '&title=' + item.title"
			>
				<!-- 通过header插槽定义列表左侧的图片显示，其他内容通过List组件内置属性实现-->
				<template v-slot:header>
					<view class="uni-thumb">
						<!-- 当前判断长度只为简单判断类型，实际业务中，根据逻辑直接渲染即可 -->
						<image :src="item.cover" mode="aspectFill"></image>
					</view>
				</template>
				<template v-slot:body>
					<view class="slot-body">
						<text class="slot-title">{{item.name}}</text>
						<text class="slot-time" style="display: block;">开放时间：{{item.businessTime}}</text>
						<text class="slot-address">{{item.address}}</text>
					</view>
				</template>
			</uni-list-item>
		</uni-list>
	</view>
</template>

<script>
export default {
	name: 'y-base-item',
	options: { styleIsolation: 'shared' },
	props: {
		list: {
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
.y-base-item {
	.uni-thumb {
		width: 220upx;
		height: 150upx;
	}
}
.slot-body{
	display: flex;
	flex-direction: column;
	// align-items: center;
	justify-content: center;
}
.slot-title{
	font-size: 32upx;
	color: #000;
	font-weight: bold;
	padding: 5upx 0;
}
.slot-time{
	font-size: 26upx;
	color: #666;
}
.slot-address{
	font-size: 26upx;
	color: #666;
}
</style>
