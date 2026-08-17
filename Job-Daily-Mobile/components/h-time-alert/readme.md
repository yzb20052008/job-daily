### 预约时间弹窗

预约时间弹窗组件。

**使用方式：**

在 ``script`` 中引用组件 

```javascript
import hTimeAlert from "@/components/h-time-alert/h-time-alert.vue";
export default {
    components: {hTimeAlert}
}
```

基本用法

```html
<hTimeAlert title="预约时间" subhead='我是副标题' rangeDay="5" disabled="0,2" :isShow="isShow" @closeAlert="handelClose"></hTimeAlert>
```

例子

```html
<hTimeAlert
			title="预约时间"
			subhead="我是副标题"
			rangeStartTime="8:00:00"
			rangeEndTime="22:00:00"
			defaultTime="2020/3/29 18:00:00"
			intervalTime="30"
			dayStartIntTime="0"
			rangeDay="5"
			disabled="0,2"
			:isShow="isShow"
			:maskHide="maskHide"
			:rangeType="rangeType"
			:closeBtn="closeBtn"
			@closeAlert="handelClose"
		></hTimeAlert>
```
```script

import hTimeAlert from '@/components/h-time-alert/h-time-alert.vue';

export default {
	components: {
		hTimeAlert
	},
	data() {
		return {
			isShow: false,
			maskHide: false,
			closeBtn: false,
			rangeType: false
		};
	},
	onLoad() {},
	methods: {
		showMask() {
			this.isShow = true;
			console.log(this.isShow);
		},

		handelClose(data) {
			this.isShow = false;
			console.log(data); //data={
			// 	date: "2020/3/30 09:00"
			// 	_date: "2020-3-30 09:00"
			// 	dateRange: "2020/3/30 09:00-09:30"
			// 	_dateRange: "2020-3-30 09:00-09:30"
			// 	timeStamp: 1585530000000
			// }
		}
	}
};

```


**属性说明：**

|属性名|类型|默认值	|说明|
|---|----|---|---|
|title|String|请选择预约时间|弹窗头部文字|
|subhead|String|''|弹窗头部文字下的小标题|
|rangeType|Boolean|false|时间是否是范围选择false,显示时间点，true显示时间段|
|isShow|Boolean|false|控制弹窗显示隐藏|
|maskHide|Boolean|true|控制弹窗是否可以通过mask点击隐藏|
|closeBtn|Boolean|true|控制弹窗右上角关闭按钮显示隐藏|
|isRoundingTime|Boolean|true|今天的时间是否取整|
|isRoundingTime|Boolean|false|今天的时间是否从当前开始,true的时候取整会失效|
|rangeDay|Number, String|3|可选择的日期范围，默认为2,今天和明天|
|rangeStartTime|String|'10:00:00'|每天的开始时间|
|rangeEndTime|String|22:00:00|每天的结束时间|
|defaultTime|String|''|默认选中的时间，2020/01/20 20:00:00|
|intervalTime|Number, String|30|时间间隔，分钟|
|dayStartIntTime|Number, String|0|每天除了时间间隔之外，还要延迟多少分钟|
|disabled|String, Array|[]|禁止的日期，0是今天，1是明天，依次类推，如果用字符串就要用逗号隔开|

**事件说明：**

|事件称名|说明|返回参数|
|---|----|---|
|closeAlert|关闭事件，如果选了时间会返回，返回参数是用户选择的时间|
			data:{
			date: "2020/3/30 09:00"
			_date: "2020-3-30 09:00"
			dateRange: "2020/3/30 09:00-09:30"
			_dateRange: "2020-3-30 09:00-09:30"
			timeStamp: 1585530000000
			}