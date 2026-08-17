## 支付宝微信手机号授权、个人信息授权登录，手机验证码登录

支持支付宝微信手机号授权、个人信息授权登录、手机验证码登录。手机验证码登录支持所有平台  
注意：  
1.此登录组件用了uview弹窗组件u-popup和加载组件u-loadmore，没安装uview可自行更改  
2.此登录组件本人设计的是全局可弹窗，所以用到了vuex，需vuex设置loginPopupShow参数，this.setLoginPopupShow(true)全局打开弹窗。如果不需要全局弹窗，可将loginPopupShow放props自行更改。
     
不多说了，开干  
引用组件和vuex--注意组件的路径
```
import { mapState, mapMutations } from 'vuex';
import fLogin from '@/components/module/f-login/f-login.vue';
```

vuex需在main.js中全局引入
```
import store from '@/store'
Vue.prototype.$store = store;
```

vuex的store中定义显示弹窗参数
```
export const state = {
    loginPopupShow:false,//控制是否打开登录弹窗
};
```

vuex的store中定义打开关闭登录弹窗方法
```
setLoginPopupShow(state, data){
	state.loginPopupShow = data
}
```

有不懂的请下载示例，查看pagesDome/login/login

### 小程序预览
![小程序预览](https://img02.163.gg/img/1/48/59/7/1485907-nmnnmpjcdm.jpg!YM0000)

### 欢迎加入wx群聊，一起交流技术

| `微信交流群（加我好友备注"进群"）`                  |
|--------------------------- |
|![微信交流群](https://img02.163.gg/img/1/19/33/61/1193361-dtzzkprpse.jpg!YM0000)|
|微信号：wbt10302015|

