import Vue from 'vue'
import App from './App'

import * as $apis from './config/api.js';
Vue.prototype.$apis = $apis;

// 工具
import '@/plugins/utils.js';
//权限配置中心
import base from '@/config/baseUrl'
Vue.prototype.$base = base;
// vuex数据管理中心
import store from '@/store'
Vue.prototype.$store = store;
//判断是否登录
import { judgeLogin } from '@/config/login';
Vue.prototype.judgeLogin = judgeLogin;

// #ifdef APP
// 自定义showModal组件
import f_show_modal from '@/components/module/show_modal/f_show_modal.js'
Vue.use(f_show_modal)
// #endif

// uview
import uView from '@/uni_modules/uview-ui'
Vue.use(uView)
// #ifdef MP
// 引入uView对小程序分享的mixin封装
// const mpShare = require('@/uni_modules/uview-ui/libs/mixin/mpShare.js')
// Vue.mixin(mpShare)
// #endif
// #ifdef H5
//微信公众号(分享、扫码、获取位置等)
import '@/plugins/jwxUtils.js';
// #endif
// 基于iconfont图标库组件
import fIcon from "@/components/module/f-icon/f-icon.vue";
Vue.component("f-icon", fIcon);
// 公共组件
import publicModule from "@/components/common/public-module.vue";
Vue.component("public-module", publicModule);

Vue.config.productionTip = false
App.mpType = 'app'

const app = new Vue({
    store,
    ...App
})
// request
import '@/config/request.js';
app.$mount()

