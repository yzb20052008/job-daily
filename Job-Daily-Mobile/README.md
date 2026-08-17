## uniapp快速开发项目框架

此项目框架基于uview2.0开发，支持登录、canvas绘图、request接口、支付、store等功能，适合大多数开发者使用，简单易用，新手、伸手党请拿走

### 开始使用

1.请设置您的登录接口--路径components/module/f-login/f-login  
2.请设置您的支付接口--路径config/common.js  
3.app请设置更新接口--路径config/componentConfig.js  
4.配置好了，简单吧，不要忘记是uview2.0开发的

### 小程序预览
![小程序预览](https://img02.163.gg/img/1/48/59/7/1485907-nmnnmpjcdm.jpg!YM0000)

### 项目结构

``` 
├── components                             	// 组件
├── config                                  
│   ├── baseUrl.js                        	// 基础配置(域名、系统信息等)
│   ├── common.js                         	// 通用js(小程序更新、app缓存、扫码、定位等)
│   ├── componentConfig.js                  // app升级配置接口
│   ├── h5Utils.js                          // h5通用js(包含h5公众号登录等)
│   ├── login.js                            // 登录js
│   ├── pay.js                              // 支付js
│   └── request.js                        	// 接口配置(基于uview配置)
├── pages  									// 项目页面
│   ├── goods                        	 	// 商品列表
│   ├── index                         		// 首页
│   ├── my                        	    	// 个人中心
│   └── user                                // 登录页等
├── pagesDome                              	// 项目分包
│   ├── canvas                            	// 绘图demo
│   ├── cartAnimation                       // 加入购物车demo
│   ├── getLocation                         // 获取定位
│   ├── http                              	// 接口使用demo
│   ├── login                             	// 登录使用demo
│   ├── mescroll                            // mescroll分页demo
│   ├── navbar                            	// 导航使用demo
│   ├── pay                               	// 支付使用demo
│   ├── showModal                         	// showModal使用demo
│   └── sticky                       	 	// 吸顶demo
├── pagesPackageA                           // 项目分包
│   └── address                            	// 地址
├── plugins                                 // 功能js
├── static                             		// 图片文件
├── store                          			// vuex
├── style
│   ├── common.scss                       	// 公共样式文件
│   └── iconfont.scss                     	// 图标样式
├── uni_modules                             
│   ├── fan-canvas                        	// 绘图通用方法
│   ├── mescroll-uni                        // mescroll分页
│   ├── uview-ui                          	// uview2.0
│   └── zhouWei-APPUpdate                   // app更新升级(wgt)
├── unpackage                              	// 项目编译后的文件
├── App.vue                                	// 项目基础文件
├── main.js                                	// 项目基础文件
├── manifest.json                          	// 项目基础文件
├── uni.scss                                // 项目基础文件
├── pages.json                             	// 项目基础文件json
├── README.md                              	// 项目介绍md
└── template.h5.html                       	// h5自定义模板
```

### 欢迎加入wx群聊，一起交流技术

| `微信交流群（加我好友备注"进群"）`                  |
|--------------------------- |
|![微信交流群](https://img02.163.gg/img/1/19/33/61/1193361-dtzzkprpse.jpg!YM0000)|
|微信号：wbt10302015|
