<template>
	<view>
		<!-- 加载动画组件 -->
		<!-- <f-loading></f-loading> -->
		<!-- 登录弹窗组件 -->
		<f-login></f-login>
	</view>
</template>

<script>
    import { mapState, mapMutations } from 'vuex';
	import fLoading  from "@/components/common/loading"
	import fLogin  from "@/components/module/f-login/f-login.vue"
	export default {
		components:{
			fLoading,
			fLogin
		},
        data() {
            return {}
        },
        methods:{

        }
	};
</script>
<style>

</style>
