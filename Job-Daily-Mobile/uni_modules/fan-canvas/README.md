## canvas万能绘图方法


下载下来后，将plugins目录中的canvasUtils.js复制到你项目中  
需要保存图片和生成二维码需将plugins目录中的qrcode.js和utils.js复制到你项目中  
js中包含绘制矩形、加载图片、绘制圆形头像、绘制图片cover不变形、文本自定义换行超出省略、绘制圆角按钮、绘制二维码等方法  



### 使用方法
插件市场中直接导入插件到uni_modules
引入万能绘图方法js
```
import _canvas from '@/uni_modules/fan-canvas/canvasUtils';
```
需要保存图片和生成二维码需引用以下js
```
import { saveImg,qrcodeCanvas } from '@/uni_modules/fan-canvas/plugins/utils';
```


绘制矩形图
```
//参数：cxt、x坐标、y坐标、宽度、高度、圆角、颜色
_canvas.fillRoundRect(ctx,0,0,cvsW,cvsH,20,"#ddd222");
```

绘制渐变边框矩形图
```
//绘制矩形
//参数：cxt、x坐标、y坐标、宽度、高度、圆角、颜色
_canvas.fillRoundRect(ctx, 0, 0, cvsW, cvsH, 20,'yellow')
ctx.clip() //超出剪切
//绘制边框
//参数：cxt、x坐标、y坐标、宽度、高度、圆角、边框宽度
_canvas.roundRectBorder(ctx, 0, 0, cvsW, cvsH, 20, 10)
// 渐变填充边框
const grd = ctx.createLinearGradient(0, 0, 200, 200)
grd.addColorStop(0, 'red')
grd.addColorStop(1, 'blue')
ctx.strokeStyle = grd //填充颜色--非渐变设置为颜色即可
ctx.stroke() //画出当前路径的边框
```

绘制头像
```
// 同步加载图片
var logo = await _canvas.getImageInfo('https://img02.163.gg/img/96/66/75/966675-ieuskndmvo.jpg!YM0000');
// 参数：cxt、图片路径、x坐标、y坐标、宽度、高度
_canvas.drawCircular(ctx, logo.path, 0, 0, cvsW, cvsH);

```

绘制不变形图片-最短边显示完整
```
// 同步加载图片--------注意1.0.4版本后xy坐标移动
var logo = await _canvas.getImageInfo('https://img02.163.gg/img/96/66/75/966675-ieuskndmvo.jpg!YM0000');
// 参数：cxt、图片属性、x坐标、y坐标、宽度、高度
_canvas.drawImgCover(ctx, logo, 0, 0, cvsW, cvsH);
```

绘制文本自动换行超出省略
```
// 文本参数
var options = {
	font:"32px",            //设置字体
	ctx:ctx,
	word:that.content,      //文本
	maxWidth:920,           //最大宽度
	maxLine:3,              //最大行数--超出省略
	x:40,                   //x坐标
	y:40,                   //y坐标
	l_h:60,                 //行高
	textCenter:false,       //是否居中显示
	cvsW:cvsW,              //画布总宽--用于计算文字居中
}
// 文字颜色
ctx.setFillStyle('#000000')
// 绘文本自定义换行
_canvas.dealWords(options)
```

绘制圆角按钮
```
// 参数：cxt、背景颜色、x坐标、y坐标、宽度、高度、圆角、文字、文字颜色、文字位置
_canvas.drawButton(ctx, 'red', 0,0, cvsW, cvsH, cvsH/2, '会员价￥2.88', '#fff', 'center')
```

### 小程序预览
![小程序预览](https://img02.163.gg/img/1/48/59/7/1485907-nmnnmpjcdm.jpg!YM0000)

### 欢迎加入wx群聊，一起交流技术

| `微信交流群（加我好友备注"进群"）`                  |
|--------------------------- |
|![微信交流群](https://img02.163.gg/img/1/19/33/61/1193361-dtzzkprpse.jpg!YM0000)|
|微信号：wbt10302015|
