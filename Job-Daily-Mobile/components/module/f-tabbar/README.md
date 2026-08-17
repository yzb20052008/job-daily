## 自定义底部导航tabbar
 
自定义底部导航，基于uview2.0二次制作

首先安装uview2.0,已安装忽略

引入组件
```
import fTabbar from '@/components/module/f-tabbar/f-tabbar';
```

pages.json中设置原生tabbar-----basics.png为站位图
```
"tabBar": {
    "color": "#ffffff",
    "selectedColor": "#ffffff",
    "borderStyle": "black",
    "backgroundColor": "#ffffff",
    "list": [{
            "pagePath": "pages/index/index",
            "iconPath": "static/basics.png",
            "selectedIconPath": "static/basics.png",
            "text": "首页"
        },
        {
            "pagePath": "pages/goods/goods",
            "iconPath": "static/basics.png",
            "selectedIconPath": "static/basics.png",
            "text": "商品"
        },
        {
            "pagePath": "pages/my/myInfo",
            "iconPath": "static/basics.png",
            "selectedIconPath": "static/basics.png",
            "text": "我的"
        }
    ]
}
```
设置您的tabbar参数--icon图标为iconfont库图标
```
list:[{ 
    name: '首页',
    url: 'pages/index/index',
    icon: 'custom-icon-home',
    iconFill: 'custom-icon-home-filling'
},
{
    name: '商品',
    url: 'pages/goods/goods',
    icon: 'custom-icon-shangpin',
    iconFill: 'custom-icon-shangpin-filling'
},
{
    name: '我的',
    url: 'pages/my/myInfo',
    icon: 'custom-icon-my',
    iconFill: 'custom-icon-my-filling'
}]
```

引入组件的页面在onLoad中隐藏原生tabbar
```
onLoad() {
	// 隐藏原生的tabbar
	uni.hideTabBar();
}
```


#### 有不懂的请下载示例

### 小程序预览
![小程序预览](https://img02.163.gg/img/1/48/59/7/1485907-nmnnmpjcdm.jpg!YM0000)


### 欢迎加入wx群聊，一起交流技术

| `微信交流群（加我好友备注"进群"）`                  |
|--------------------------- |
|![微信交流群](https://img02.163.gg/img/1/19/33/61/1193361-dtzzkprpse.jpg!YM0000)|
|微信号：wbt10302015|

