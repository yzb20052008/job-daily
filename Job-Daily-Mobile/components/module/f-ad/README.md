## 微信流量主广告组件

下载下来后，将组件中f-ad.vue复制到您的组件中


### 使用方法

引用组件--注意您组件的路径
```
import fSignature from '@/components/module/f-ad/f-ad';
```

案例

```
//typeArray： 1.banner广告 2.视频广告 3.原生模板广告 4.插屏视频广告 5.激励视频广告 6.视频贴片广告
<f-ad ref="FAD" :typeArray="[1,2,3,4,5,6]" @excitationAdCallback="excitationAdCallback"></f-ad>
```
激励广告回调

```
excitationAdCallback(e){
	if(e==1){
		console.log('激励回调--播放完成')
	}else if(e==2){
		console.log('激励回调--中途退出')
	}
}
```

### 欢迎加入wx群聊，一起交流技术

| `微信交流群（加我好友备注"进群"）`                  |
|--------------------------- |
|![微信交流群](https://img02.163.gg/img/1/19/33/61/1193361-dtzzkprpse.jpg!YM0000)|
|微信号：wbt10302015|

