<template>
	<view class="flex">
		<scroll-view scroll-y="true" class="box1" :style="{height: scrollHeight+'px'}" :scroll-into-view="provinceId">
			<view class="itemClass text-overflow" v-for="(item,index) in provinceList" :key="index" :id="'province-'+index"
				:class="{ 'selectedProvice': provinceIndex==index }"
				:style="{color:provinceIndex==index?selectedColor:''}" @click="selectProvince(index)">
				{{item.name}}
			</view>
		</scroll-view>
		<scroll-view scroll-y="true" class="box2" :style="{height: scrollHeight+'px'}" :scroll-into-view="cityId">
			<view class="itemClass text-overflow" :class="{ 'selectedProvice': cityIndex==index }" :id="'city-'+index"
				v-for="(item,index) in cityList" :key="index" :style="{color:cityIndex==index?selectedColor:''}"
				@click="selectCity(index)">
				{{ item.name }}
			</view>
		</scroll-view>
		<scroll-view v-if="countyList.length>0" scroll-y="true" class="box3" :style="{height: scrollHeight+'px'}"
			:class="{'bl':countyList.length>0}" :scroll-into-view="countyId">
			<view class="itemClass text-overflow" :class="{ 'selectedProvice': countyIndex==index }" :id="'county-'+index"
				v-for="(item,index) in countyList" :key="index" :style="{color:countyIndex==index?selectedColor:''}"
				@click="selectCounty(index)">
				{{ item.name }}
			</view>
		</scroll-view>
	</view>
</template>

<script>
	import cityData from './city.json'
	export default {
		name: "gjs-selectCity",
		props: {
			scrollHeight: {
				type: Number,
				default: 800
			},
			selectedColor: {
				type: String,
				default: "#007aff"
			},
			//市区，默认
			cityCode: {
				type: String,
				default: ''
			},
			countyCode: {
				type: String,
				default: ''
			},
		},
		data() {
			return {
				provinceIndex: 0,
				cityIndex: -1,
				countyIndex: -1,

				provinceList: [],
				cityList: [],
				countyList: [],
				
				provinceId: '',
				cityId: '',
				countyId: '',
			};
		},
		mounted() {
			this.provinceList = cityData
			this.cityList = this.provinceList[this.provinceIndex].children
			// if (this.cityList[this.cityIndex].area) {
			// 	this.countyList = this.cityList[this.cityIndex].area
			// } else {
			// 	this.countyList = [];
			// }
			// this.initData();
		},

		watch: {
			cityCode: {
				handler() {
					console.log("==watch===city")
					this.initData();
				},
				immediate: true
			},
		},

		methods: {

			initData() {
				console.log("initData======", this.cityCode, this.countyCode)
				if (!this.cityCode) {
					return;
				}
				let provinceCode = this.cityCode.substr(0, 2);
				console.log("provinceCode======", provinceCode)
				for (let i = 0; i < this.provinceList.length; i++) {
					if (provinceCode == this.provinceList[i].code) {
						this.provinceIndex = i;
					}
				}
				console.log("this.provinceIndex===", this.provinceIndex)
				//城市
				this.cityList = this.provinceList[this.provinceIndex].children
				for (let i = 0; i < this.cityList.length; i++) {
					if (this.cityCode == this.cityList[i].code) {
						this.cityIndex = i;
					}
				}
				//直辖市
				if(!this.cityIndex){
					for (let i = 0; i < this.cityList.length; i++) {
						if (this.countyCode == this.cityList[i].code) {
							this.cityIndex = i;
						}
					}
				}else{
					console.log("this.cityList===", this.cityList)
					this.countyList = this.cityList[this.cityIndex].children || []
					for (let i = 0; i < this.countyList.length; i++) {
						if (this.countyCode == this.countyList[i].code) {
							this.countyIndex = i;
						}
					}
				}
				console.log("this.provinceIndex===", this.provinceIndex)
				this.scrollToIndex();
			},

			scrollToIndex() {
				// 确保索引在有效范围内
				if (this.provinceIndex < 0) {
					this.provinceIndex = 0;
				}
				if (this.provinceIndex >= this.provinceList.length) {
					this.provinceIndex = this.provinceList.length - 1;
				}
				// 设置目标ID，触发滚动
				this.provinceId = `province-${this.provinceIndex}`;
				this.cityId = `city-${this.cityIndex}`;
				this.countyId = `county-${this.countyIndex}`;
			},

			selectProvince(e) {
				this.provinceIndex = e
				this.cityIndex = -1
				this.countyIndex = -1
				this.cityList = this.provinceList[this.provinceIndex].children
				// if (this.cityList[this.cityIndex].area) {
				// 	this.countyList = this.cityList[this.cityIndex].area
				// } else {
				this.countyList = [];
				// }
				console.log("cityList==", this.cityList);
				console.log("countyList==", this.countyList);
			},
			selectCity(e) {
				this.cityIndex = e
				this.countyIndex = -1
				this.countyList = this.cityList[this.cityIndex].children || []
				if (this.countyList.length == 0) {
					let province = this.provinceList[this.provinceIndex]
					let city = this.cityList[this.cityIndex]
					//自行配置 需要返回的 地址 ↓↓↓↓↓↓
					// let address = province + '-' + city
					let address = {
						province: province.name,
						city: '市辖区',
						cityCode: province.code + "01",
						area: city.name,
						areaCode: city.code,
					}
					this.$emit('select', address)
				}
			},
			selectCounty(e) {
				this.countyIndex = e
				let province = this.provinceList[this.provinceIndex].name
				let city = this.cityList[this.cityIndex]
				let county = this.countyList[this.countyIndex]
				//自行配置 需要返回的 地址 ↓↓↓↓↓↓
				// let address = province + '-' + city + '-' + county
				let address = {
					province: province,
					city: city.name,
					cityCode: city.code,
					area: county.name,
					areaCode: county.code,
				}
				this.$emit('select', address)
			}
		}
	}
</script>

<style>
	.box1 {
		width: 250upx;
		background-color: #F5F6FA;
	}

	.box2 {
		width: 250upx;
	}

	.bl {
		border-left: 1rpx solid #EEEEEE;
	}

	.box3 {
		width: calc(100% - 500rpx)
	}

	.itemClass {
		height: 90rpx;
		line-height: 90rpx;
		padding-left: 20rpx;
		padding-right: 15rpx;
		color: #333;
	}

	.selectedProvice {
		background-color: #FFFFFF;
		font-weight: bold;
	}

	/deep/::-webkit-scrollbar {
		display: none;
		width: 0;
		height: 0;
	}

	/* 超出部分显示... */
	.text-overflow {
		overflow: hidden;
		/*超出部分隐藏*/
		white-space: nowrap;
		/*不换行*/
		text-overflow: ellipsis;
		/*超出部分文字以...显示*/
	}

	.flex {
		display: flex;
		flex-direction: row;
	}
</style>