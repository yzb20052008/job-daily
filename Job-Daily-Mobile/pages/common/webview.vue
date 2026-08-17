<template>
	<view>
		<web-view :webview-styles="webviewStyles" :src="url" @message="message"></web-view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				title: null,
				url: '',
				webviewStyles: {
					progress: {
						color: '#FF3333'
					}
				},
			}
		},
		onLoad: function(option) {
			console.log("----------option------------");
			console.log(option);
			console.log("----------option.url------------" + option.url);
			var tmp = option.url;
			tmp = tmp.replace('-', '?');
			tmp = tmp.replace('@', '=');
			console.log("----------option------URL=====" + tmp);
			this.url = tmp;
			this.title = option.title
			console.log("title=" + this.title);
			if (this.title != null && this.title != undefined) {
				uni.setNavigationBarTitle({
					title: this.title,
				});
			}
		},

		methods: {
			message(msg) {
				console.log("msg=========",msg)
			}
		}
	}
</script>

<style>
</style>