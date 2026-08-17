<template>
	<view class="y-news-item">
		<uni-list>
			<!-- to属性：将新闻ID和标题传给详情页 -->
			<!-- TODO：需增加日期格式化的示例 -->
			<!-- to 属性携带参数跳转详情页面，当前只为参考 -->
			<uni-list-item
				direction="row"
				v-for="item in newsList"
				ellipsis="2"
				:key="item.id"
				:title="item.title"
				:note="item.publishTime"
				@click="toDetail(item)"
				:clickable="clickable"
			>
				<!-- 通过header插槽定义列表左侧的图片显示，其他内容通过List组件内置属性实现-->
				<template v-slot:header>
					<view class="uni-thumb">
						<!-- 当前判断长度只为简单判断类型，实际业务中，根据逻辑直接渲染即可 -->
						<image :src="item.avatar.length > 3 ? item.avatar : item.avatar[0]" mode="aspectFill"></image>
					</view>
				</template>
			</uni-list-item>
		</uni-list>
	</view>
</template>

<script>
	export default {
		name:"y-news-item",
		options: { styleIsolation: 'shared' },
		props: {
			newsList: {
				type: Array
			},
			url: {
				type: String
			}
		},
		data() {
			return {
				clickable:true,
			};
		},
		methods:{
			
			toDetail(item){
				let newurl = item.openUrl.replace('?', '-');
				newurl = newurl.replace('=', '@');
				uni.navigateTo({
					url: '/pages/common/webview?url=' + newurl
				});
			}
		}
		
	}
</script>

<style lang="scss" scoped>
@import '@/common/uni-ui.scss';
.y-news-item{
	.uni-thumb{
		width: 220upx;
		height: 150upx;
	}
}

</style>