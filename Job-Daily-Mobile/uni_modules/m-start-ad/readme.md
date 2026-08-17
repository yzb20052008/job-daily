# m-start-ad

快速制作一个自定义开屏广告，支持轮播

### 属性说明

| 属性名 | 类型 | 默认值 | 必填 | 说明 |
| ------- | ------- | ------- | ------- | ------- |
|  list       |  Array       |    []    |    true     |  开屏图片列表  | 
|  hasTabbar  |  Boolean       |    false    |    false     |  页面存在原生tabbar，组件会自动判断隐藏tabbar  |
|  hasNavbar       |  Boolean       |    false    |    false     |  页面是否包含navbar  | 
|  interval       |  Number       |    3000     |      false     |  轮播自动播放时间（多图时可用）  |
| time     |  Number   |   3  |   false  |   倒计时跳过 |
| url      |  String  |   ''  |   false  |   时间倒计时结束后跳转地址,如果是tabbar页面，可为空 |		
| bgColor  | String  |   '#BB1219' |   false |   页面的背景颜色, 开屏图片为自动垂直居中，在大屏幕手机下，有些时候高度不够的情况下，会造成上下留白，使用此属性可以填充背景色 |			
| afterColor  | String  |     ''  |   false  |   页面底部背景色填充，有些时候设置会设计渐变色图片，如果只使用bgcolor无法解决需求，此时定义此属性，可在最下面填充颜色，以达到颜色过度效果 |	
| currentColor  | String   |     '#BB1219'  |   false  |  定义dot当前状态颜色, list长度大于1可见 |


### 如何做自定义开屏界面

#### 方式一
1、新建一个新页面 pages/start
2、修改pages.js配置文件，把start地址放在最前
```
{
	"pages": [ 
		{
		    "path" : "pages/start", //page第一页就是开屏第一个页面
		    "style" :                                                                                    
		    {
		        "navigationBarTitleText": "",
				"navigationStyle": "custom"  //取消默认导航做到满屏效果
		    }
		    
		}
	]
}
```
3、页面引入组件，传入list, url

### 使用方式
```
<template>
	<m-start-ad :list="list" url="pages/index/index" />
</template>

```

#### 方式二
1、页面关闭原生导航
```
{
	"pages": [ 
		{
		    "path" : "pages/index/index", 
		    "style" :                                                                                    
		    {
		        "navigationBarTitleText": "",
				"navigationStyle": "custom"  //取消默认导航做到满屏效果
		    }
		    
		}
	]
}
```
1、页面直接使用
```
<m-start-ad :list="list" />
```
2、倒计时结束后，自动隐藏组件（无需操作）


### 方式对比
 |对比  | 优点  | 缺点  |
 | -------  | -------  |-------  |
 | 方式一  |  所有页面都可以公用  | 如果内页需要tabbar,需要自定义 |
 | 方式二  |  按需配置   | 无法控制navbar，需要自定义   |