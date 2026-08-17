<template>
	<view :class="{ appintAlertMask: true, aMShow: isShow }" @click="_maskClose">
		<view :class="{ appintAlertContent: true, aCShow: isShow }" @click.stop="_stopFunc">
			<view class="closeBtn" v-if="closeBtn" @click="_closeBtnClose">×</view>
			<view class="alertTtitle">
				<view>{{ title }}</view>
				<view>{{ subhead }}</view>
			</view>
			<view class="alertChangetab"></view>
			<view class="alertTimebox">
				<view class="left_box">
					<view v-if="item.timeArr.length > 0" @click="_changeDay(index)" :class="{ active: item.checked }" v-for="(item, index) in timeList" :key="item.dateStr">
						{{ item.name }}
					</view>
				</view>
				<view class="right_box">
					<view :class="{ active: item.checked }" v-for="(item, index) in activeTimeArr" :key="item.time" @click="_changeTime(index,item)" >
						{{ item.time }}{{ rangeType ? '-' + item.endtime : '' }}
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
export default {
	props: {
		title: {
			type: String,
			default: '请选择预约时间'
		},
		subhead: {
			type: String,
			default: ''
		},
		rangeType: {
			type: Boolean,
			default: false
		},
		isShow: {
			type: Boolean,
			default: false
		},
		maskHide: {
			type: Boolean,
			default: true
		},
		closeBtn: {
			type: Boolean,
			default: true
		},
		rangeDay: {
			type: [Number, String],
			default: 2
		},
		rangeStartTime: {
			type: String,
			default: '10:00:00'
		},
		rangeEndTime: {
			type: String,
			default: '22:00:00'
		},
		defaultTime: {
			type: String,
			default: ''
		},
		isRoundingTime: {
			type: Boolean,
			default: true
		},
		intervalTime: {
			//间隔时间
			type: [Number, String],
			default: 30
		},
		isNow:{
			type: Boolean,
			default: false
		},
		dayStartIntTime: {
			//每天开始间隔时间
			type: [Number, String],
			default: 0
		},
		disabled: {
			type: [String, Array],
			default: () => []
		}
	},
	data() {
		return {
			timeList: [],
			selectDateStr: '',
			select_dateStr: '',
			selectTime: '',
			selectEndime: '',
			activeTimeArr: []
		};
	},
	beforeMount() {
		this._initDay();
	},
	watch: {
		isShow: function(val, oldVal) {
			val && this.timeList.length <= 0 && this._initDay();
		}
	},
	methods: {
		_stopFunc() {},

		_maskClose() {
			let { maskHide } = this;
			maskHide && this._closeAlert();
		},
		_closeBtnClose() {
			let { closeBtn } = this;
			closeBtn && this._closeAlert();
		},
		_closeAlert(data = '') {
			this.$emit('closeAlert', data);
		},
		_changeDay(e) {
			console.log("===_changeDay===",e)
			let _ind = e - 0;
			let { timeList } = this;
			timeList.forEach(ele => {
				ele.checked = false;
			});
			timeList[_ind].checked = true;
			this.timeList = timeList;
			this.selectDateStr = timeList[_ind].dateStr;
			this.select_dateStr = timeList[_ind]._dateStr;
			this.activeTimeArr = timeList[_ind].timeArr;
			this.$forceUpdate();
		},
		_changeTime(e,item) {
			console.log("_changeTime",e)
			let _ind = e - 0;
			let { activeTimeArr } = this;
			let timeArr = JSON.parse(JSON.stringify(activeTimeArr));
			timeArr.forEach(ele => {
				ele.checked = false;
			});
			timeArr[_ind].checked = true;
			this.selectTime = timeArr[_ind].time;
			this.selectEndime = timeArr[_ind].endtime;
			this.activeTimeArr = timeArr;
			let _data = this._handleData();
			this._closeAlert(_data);
		},
		_handleData() {
			let _data = {};
			let { selectDateStr, select_dateStr, selectTime, selectEndime } = this;
			_data.date = selectDateStr + ' ' + selectTime;
			_data._date = select_dateStr + ' ' + selectTime;
			_data.dateRange = selectDateStr + ' ' + selectTime + '-' + selectEndime;
			_data._dateRange = select_dateStr + ' ' + selectTime + '-' + selectEndime;
			_data.timeStamp = new Date(selectDateStr + ' ' + selectTime).getTime();
			return _data;
		},
		_initDay() {
			let _timeList = [];
			let { rangeDay, defaultTime } = this;
			for (let index = 0; index < rangeDay; index++) {
				let _item = {
					...this._getDate(index)
				};
				_item.timeArr = this._initTime(index);
				_timeList.push(_item);
			}

			if (defaultTime) {
				//存在默认时间
				let _day = defaultTime.split(' ')[0].replace(/-/g, '/');
				let _time = defaultTime.split(' ')[1];
				let _flag = true;
				for (let index = 0; index < _timeList.length; index++) {
					const element = _timeList[index];
					element.checked = false;
					if (element.timeArr.length > 0 && element.dateStr === _day) {
						element.checked = true;
						_flag = false;
						element.timeArr.forEach(item => {
							if (this._timeRange(item.time + ':00', item.endtime + ':00', _time)) {
								item.checked = true;
								this.time = item.time;
								this.endtime = item.endtime;
							}
						});

						this.selectDateStr = element.dateStr;
						this.select_dateStr = element._dateStr;
						this.activeTimeArr = element.timeArr;
					}
				}
				if (_flag) {
					this._setDefaultTime(_timeList);
				}
			} else {
				this._setDefaultTime(_timeList);
			}

			this.timeList = _timeList;
		},
		_setDefaultTime(list) {
			for (let index = 0; index < list.length; index++) {
				const element = list[index];
				if (element.timeArr.length > 0) {
					element.checked = true;
					//是否默认选择时间
					// element.timeArr[0].checked = true;
					this.selectDateStr = element.dateStr;
					this.select_dateStr = element._dateStr;
					this.activeTimeArr = element.timeArr;
					break;
				}
			}
		},
		_initTime(num) {
			let _item = [];
			let { disabled, rangeStartTime, rangeEndTime, intervalTime } = this;
			if(intervalTime<=0){
				intervalTime = 1
			}
			if (typeof disabled === 'string') {
				disabled = disabled ? disabled.split(',') : [];
			} else if (Array.isArray(disabled)) {
				disabled = disabled.map(ele => {
					return ele + '';
				});
			}
			if (num === 0 && !disabled.includes('0')) {
				//今天
				let _nowTime = this._roundingTime();
				if (this._timeRange(rangeStartTime, rangeEndTime, _nowTime)) {
					//当前时间在营业时间内
					return this._forTime(_nowTime, rangeEndTime, intervalTime, _nowTime);
				} else if (this._toTimeStr(_nowTime) < this._toTimeStr(rangeStartTime)) {
					//早于今天开始时间
					return this._forTime(rangeStartTime, rangeEndTime, intervalTime, _nowTime);
				} else {
					return [];
				}
			} else {
				//其他日期

				if (disabled.includes(num + '')) {
					//禁止当前日期了
					return [];
				}

				return this._forTime(rangeStartTime, rangeEndTime, intervalTime);
			}
		},
		_getDate(num) {
			let date = new Date();
			let date1 = new Date(date);
			let weekday = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
			date1.setDate(date.getDate() + num);
			let month=(date1.getMonth() - 0 + 1);
			let day=date1.getDate();
			let month2=month < 10 ? '0' + month : month.toString();
			let day2=day < 10 ? '0' + day : day.toString();
			let name = '',
				dateStr = '';
			name = date1.getMonth() - 0 + 1 + '月' + date1.getDate() + '日(' + weekday[date1.getDay()] + ')';
			dateStr = date1.getFullYear() + '/' + month2 + '/' + day2;
			if (num == 0) {
				name = '今天(' + weekday[date1.getDay()] + ')';
			} else if (num == 1) {
				name = '明天(' + weekday[date1.getDay()] + ')';
			}
			return {
				name: name,
				dateStr: dateStr,
				_dateStr: dateStr.replace(/\//g, '-')
			};
		},
		_addZero(num) {
			num = num + '';
			if (num.length === 1) {
				return '0' + num;
			}
			return num;
		},
		_roundingTime() {
			let _now = new Date();
			let _h = _now.getHours() - 0;
			let _m = _now.getMinutes() - 0;
			if (_m > 30) {
				_m = '00';
				_h += 1;
			}else if (_m - 0 > 0&&_m - 0<30) {
				_m = 30;
			}
			if (this.isRoundingTime&&!this.isNow) {
				return _h + ':' + _m + ':00';
			}
			return new Date().getHours() + ':' + new Date().getMinutes() + ':00';
		},
		_forTime(st, et, it, dt = 0) {
			let { dayStartIntTime } = this;
			let _dayStartIntTime = dayStartIntTime * 1000 * 60;
			let _st = this._toTimeStr(st);
			let _et = this._toTimeStr(et);
			let _it = it * 1000 * 60;
			let _list = [];
			if (_st < _et) {
				for (let i = _st + _dayStartIntTime; i < _et; i += _it) {
					_list.push({
						time: this._toLocalTime(i),
						endtime: this._toLocalTime(i + _it > _et ? _et : i + _it),
						checked: false
					});
				}
			} else {
				//跨天了
				for (let i = dt; i < _et; i += _it) {
					_list.push({
						time: this._toLocalTime(i),
						endtime: this._toLocalTime(i + _it > _et ? _et : i + _it),
						checked: false
					});
				}
				for (let i = _st  + _dayStartIntTime; i < this._toTimeStr('23:59:59'); i += _it) {
					_list.push({
						time: this._toLocalTime(i),
						endtime: this._toLocalTime(i + _it),
						checked: false
					});
				}
			}
			return _list;
		},
		_toTimeStr(time = '') {
			let newTime = time;
			let itemStr = 0;
			let timeArr = newTime.split(':');
			let h = timeArr[0] * 1000 * 60 * 60;
			let m = timeArr[1] * 1000 * 60;
			let s = timeArr[2] * 1000;
			itemStr = h + m + s;
			return itemStr;
		},
		_toLocalTime(v, type = 0) {
			let h = parseInt((v / 1000 / 60 / 60) % 24);
			let m = parseInt((v / 1000 / 60) % 60);
			let s = parseInt((v / 1000) % 60);
			v = type === 0 ? `${this._addZero(h)}:${this._addZero(m)}` : `${this._addZero(h)}:${this._addZero(m)}:${this._addZero(s)}`;
			return v;
		},
		_timeRange(beginTime, endTime, nowTime) {
			let strb = beginTime.split(':');
			if (strb.length != 3) {
				return false;
			}

			let stre = endTime.split(':');
			if (stre.length != 3) {
				return false;
			}

			let strn = nowTime.split(':');
			if (stre.length != 3) {
				return false;
			}
			let b = new Date();
			let e = new Date();
			let n = new Date();

			b.setHours(strb[0]);
			b.setMinutes(strb[1]);
			e.setHours(stre[0]);
			e.setMinutes(stre[1]);
			n.setHours(strn[0]);
			n.setMinutes(strn[1]);

			let _b = b.getTime();
			let _e = e.getTime();
			let _n = n.getTime();

			if (_b > _e) {
				if (_n - _e >= 0 && _n - _b < 0) {
					return false;
				} else {
					return true;
				}
			} else {
				if (_n - _b >= 0 && _n - _e < 0) {
					return true;
				} else {
					return false;
				}
			}
		}
	}
};
</script>

<style>
.appintAlertMask {
	position: fixed;
	left: 0;
	right: 0;
	bottom: 0;
	top: 0;
	background-color: rgba(0, 0, 0, 0.5);
	z-index: -9999;
	opacity: 0;
	transition: opacity 0.3s;
}
.aMShow {
	z-index: 999;
	opacity: 1;
}
.appintAlertMask view {
	box-sizing: border-box;
}
.appintAlertContent {
	height: 0rpx;
	background-color: #fff;
	position: absolute;
	bottom: 0;
	left: 0;
	width: 100%;
	padding: 40rpx 0 10rpx;
	transition: height 0.5s;
}
.aCShow {
	height: auto;
}
.alertTtitle {
	text-align: center;
	font-size: 32rpx;
	color: #222222;
	font-weight: 600;
	position: relative;
}

.closeBtn {
	position: absolute;
	width: 80rpx;
	line-height: 80rpx;
	text-align: center;
	right: 0rpx;
	padding-right: 15rpx;
	top: 0;
	color: #999999;
	z-index: 99;
	font-size: 44rpx;
}
.alertTtitle > view:nth-child(2) {
	font-size: 22rpx;
	color: #919191;
	margin-top: 10rpx;
}
.alertChangetab {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 25rpx 60rpx;
	position: relative;
}
.alertChangetab::after {
	content: ' ';
	width: 690rpx;
	position: absolute;
	left: 20rpx;
	bottom: 0;
	height: 1rpx;
	background-color: #f4f4f4;
}
.alertChangetab > view {
	width: 282rpx;
	height: 110rpx;
	border: 1rpx solid rgba(145, 145, 145, 1);
	border-radius: 20rpx;
	font-size: 36rpx;
	display: flex;
	justify-content: center;
	align-items: center;
	transition: background-color 0.3s;
}
.alertChangetab > view image {
	width: 82rpx;
	height: 82rpx;
	margin-right: 25rpx;
	background-color: #dd0000;
}
.alertChangetab > view > view {
	text-align: center;
}
.alertChangetab > view > view:nth-child(2) {
	font-size: 20px;
}
.alertChangetab > .active {
	background-color: #dcb477;
	color: #fff;
	border: 1px solid #dcb477;
}

.alertTimebox {
	background-color: #fbf8fb;
	height: 660rpx;
	display: flex;
	justify-content: space-between;
	align-items: center;
}
.alertTimebox > view {
	height: 100%;
	overflow-y: auto;
	-webkit-overflow-scrolling: touch;
	font-size: 28rpx;
}
.alertTimebox .left_box {
	color: #919191;
	width: 45%;
}
.alertTimebox .left_box view,
.alertTimebox .right_box view {
	text-align: center;
	line-height: 90rpx;
	position: relative;
}

.alertTimebox .right_box view::before {
	content: ' ';
	width: 80%;
	position: absolute;
	left: 10%;
	bottom: 0;
	border: 1px dashed #f4f4f4;
}
.alertTimebox .right_box view:last-child::before {
	border: none;
}

.alertTimebox .right_box {
	color: #222222;
	width: 55%;
	flex: 1;
	background-color: #fff;
	text-align: center;
}
.alertTimebox .right_box .active {
	position: relative;
	color: #dcb477;
}
.alertTimebox .left_box .active {
	background-color: #fff;
	color: #222222;
}
.alertTimebox .right_box .active::after {
	content: ' ';
	position: absolute;
	top: 50%;
	margin-top: -16rpx;
	right: 50rpx;
	width: 10rpx;
	height: 20rpx;
	border-color: #dcb477;
	border-style: solid;
	border-width: 0 3rpx 5rpx 0;
	transform: rotate(45deg);
}
</style>
