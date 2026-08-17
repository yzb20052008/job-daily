## 菜单导航navbar

自定义导航，基于uview2.0制作，上拉渐变显示背景色，左侧按钮自定义事件，左中右插槽，背景渐变色，支持多平台  
如果是第一个页面左侧图标显示去首页按钮（分享的页面左侧图标显示去首页按钮）
     
首先安装uview2.0

进入配置您的路径
```
//不显示首页按钮路径
const mainPagePath = ['pages/index/index','pages/goods/goods','pages/my/myInfo'];
//返回首页的地址
const homePath = '/pages/index/index';
```

引入组件
```
import fNavbar from '@/components/module/f-navbar/f-navbar';
```

默认导航固定顶部
```
<f-navbar title="导航栏"></f-navbar>
```

不固定导航栏
```
<f-navbar title="不固定导航栏" fontColor="#fff" bgColor="#fe461d" navbarType='1'></f-navbar>
```

上拉渐变显示navbar背景
```
<f-navbar title="上拉渐变显示navbar背景" fontColor="#fff" bgColor="#fe461d" :scrollTop="scrollTop" navbarType='5'></f-navbar>
```

上拉渐变白色背景、文字和左侧图标渐变
```
<f-navbar title="上拉渐变显示navbar背景" fontColor="#303133" transparentTitleColor="#fff" bgColor="#fff" :scrollTop="scrollTop" navbarType='5'></f-navbar>
```

自定义左侧按钮事件
```
<f-navbar title="导航栏-自定义左侧按钮事件" navbarType='2' @leftClick='leftClick'></f-navbar>
```

自定义左侧插槽slot
```
<f-navbar title="导航栏-自定义左侧插槽">
	<view class="u-flex" slot="left">
		<u-icon name="map" size="19" color="#fff"></u-icon>
		<view class="actName u-line-1">北京市</view>
	</view>
</f-navbar>
```

导航背景渐变
```
<f-navbar title="导航栏-背景渐变" fontColor="#fff" gradient="linear-gradient(-90deg, yellow, red)"></f-navbar>
```

同时显示返回和去首页按钮
```
<f-navbar title="导航栏" navbarType='3'></f-navbar>
```

#### 有不懂的请下载示例，查看pagesDome/navbar/navbar

### 小程序预览
![小程序预览](https://img02.163.gg/img/1/48/59/7/1485907-nmnnmpjcdm.jpg!YM0000)

### 参数说明

| 参数名		    | 说明																								        | 是否必填				| 数据类型	| 默认			|
| -------		| -------																								    | -------				| -------	| -------		|
| title			| 导航名称																								    |  false				|  String	|				|
|isShowTransparentTitle | navbarType等于5透明背景时title是否显示                                                             |  false                |  Boolean  |    true       |
| leftText		| 左侧文字																								    |  false				|  String	|	 		    |
| bgColor		| 背景颜色																								    |  false				|  String	|    #fff		|
| image         | 背图图片                                                                                                   |  false                |  String   |               |
| imageMode     | 背图图片mode                                                                                               |  false                |  String   |   aspectFill  |
| navbarType    | 0、默认固定在顶部 1、不固定在顶部 2、自定义左侧点击事件 3、同时显示箭头和去首页按钮 4、不显示左侧内容 5、上拉渐变显示背景色	|  false	|  Number	|    0			|
| isShowLeft    | 是否显示左侧内容（navbarType=4效果一样）                                                                    |  false                |  Boolean  |    true       |
| isCustomLeftClick  | 是否自定义左侧点击事件（navbarType=2效果一样）                                                         |  false                |  Boolean  |    true       |
| leftIcon		| 左侧图标（uview中icon）																					|  false				|  String	|    arrow-left	|
| leftIconColor	| 左边icon颜色																							    |  false				|  String	|    #303133	|
| scrollTop		| 滚动条参数																								    |  navbarType等于5必填	|  Number	|    0			|
| fontColor		| 导航title颜色																							    |  false				|  String	|    #303133	|
| transparentTitleColor		| navbarType=5时透明背景时导航title颜色															|  false				|  String	|     			|
| titleWidth	| 导航中间title宽度（rpx）																				    |  false				|  Number	|    400		|
| fontSize		| title文字大小（rpx）																					    |  false				|  Number	|    30			|
| gradient		| 渐变背景																								    |  false				|  String	|				|
| isFillHeight	| 是否设置防止塌陷高度																					    |  false				|  Boolean	|    true		|




### 欢迎加入wx群聊，一起交流技术

| `微信交流群（加我好友备注"进群"）`                  |
|--------------------------- |
|![微信交流群](https://img02.163.gg/img/1/19/33/61/1193361-dtzzkprpse.jpg!YM0000)|
|微信号：wbt10302015|

