<template>
  <div>
    <div class="selector-box">
      <div>
        <!-- 阿里图标需替换 -->
        <i type="iconweizhi" />
        <a-button :disabled="disabled" v-if="value.length != 0 && showLatLng" type="link" @click="showMapSelector">经度：{{ value[0] }} 纬度：{{ value[1] }}</a-button>
        <a-button :disabled="disabled" v-else type="link" @click="showMapSelector">选择地址</a-button>
      </div>
      <!-- 阿里图标需替换 -->
      <i v-if="value.length != 0 && !disabled" class="close-icon" type="iconai54" @click="clearLocation" />
    </div>

    <a-modal title="选择地址" :visible="isShowMap" centered width="800px" @ok="save" @cancel="closeModal">
      <a-row class="m-b-10">
        <a-col :span="15">
          <a-input id="search" v-model="searchKey" placeholder="请输入地址"></a-input>
          <div class="tip-box beauty-scroll" id="searchTip"></div>
        </a-col>
        <a-col :span="9"><a-button type="link" @click="resetMap">重置</a-button></a-col>
      </a-row>
      <div class="container m-b-10">
        <el-amap :key="key" class="amap-box" :amap-manager="amapManager" :vid="'amap-vue' + key" :zoom="zoom" :plugin="plugin" :center="center" :events="events">
          <!-- 标记 -->
          <el-amap-marker v-for="(marker, index) in markers" :position="marker" :key="index"></el-amap-marker>
        </el-amap>
      </div>
      <p>当前地址为：{{ address }}</p>
      <p>经纬度：({{ lng }},{{ lat }})</p>
    </a-modal>
  </div>
</template>

<script>
/**
 * 高德地图选点组件
 *
 * @property {Array} value 地图坐标点
 * @property {Boolean} disabled 组件是否可用
 *
 * @event {Function()} change 点击确认或清除坐标点
 * @return {Object} location 经纬度 / address 位置
 *
 * @demo <by-amap-point-selector v-model="location" @change="changeLocation" />
 * */

import { AMapManager, lazyAMapApiLoaderInstance } from 'vue-amap';

import VueAMap from 'vue-amap';
import Vue from 'vue';

let amapManager = new AMapManager();
let defaultPoint = [119.635948, 29.110679];

export default {
  props: {
    // 初始化位置
    value: {
      type: Array,
      default: function() {
        return [];
      }
    },
    // 是否禁用
    disabled: {
      type: Boolean,
      default: false
    },
    // 是否显示经纬度
    showLatLng: {
      type: Boolean,
      default: false
    }
  },
  created() {
    // 初始化高德地图
    Vue.use(VueAMap);
    VueAMap.initAMapApiLoader({
      key: 'fcf4239b0c08c279265cdd247d256aba',
      plugin: ['AMap.Scale', 'AMap.OverView', 'AMap.ToolBar', 'AMap.MapType', 'AMap.PlaceSearch', 'AMap.Geolocation', 'AMap.Geocoder'],
      v: '1.4.4',
      uiVersion: '1.0.11'
    });
  },
  data() {
    let _this = this;

    // 高德地图事件处理
    const events = {
      // 地图初始化完毕
      init() {
        lazyAMapApiLoaderInstance.load().then(() => {
          _this.initSearch();
        });
      },
      // 地图点击
      click(e) {
        _this.markers = [];
        let { lng, lat } = e.lnglat;
        _this.lng = lng;
        _this.lat = lat;
        _this.center = [lng, lat];
        _this.markers.push([lng, lat]);
        _this.getAddress(lng, lat).then(res => {
          console.log("==getAddress==",res);
          _this.address = res.formattedAddress;
          _this.setLocation(res);
        });
      }
    };

    // 高德地图插件
    const plugin = [
      {
        // 定位
        pName: 'Geolocation',
        events: {
          init(o) {
            console.log('Geolocation:', o);

            // o是高德地图定位插件实例
            o.getCurrentPosition((status, result) => {
              console.log('getCurrentPosition:', result);
              if (result && result.position) {
                // 设置经度
                _this.lng = result.position.lng;
                // 设置维度
                _this.lat = result.position.lat;
                // 设置坐标
                _this.center = [_this.lng, _this.lat];
                _this.defaultCenter = _this.center;
                _this.markers.push([_this.lng, _this.lat]);
                // 获取地址
                _this.getAddress(_this.lng, _this.lat).then(res => {
                  _this.address = _this.searchKey = res.formattedAddress;
                  _this.setLocation(res);
                });
              }
            });
          },
          click(e) {
            console.log(e);
          }
        }
      },
      {
        // 工具栏
        pName: 'ToolBar',
        events: {
          // init(instance) {
          // 	console.log(instance);
          // }
        }
      },
      {
        // 搜索
        pName: 'PlaceSearch',
        events: {
          init(instance) {
            console.log(instance);
          }
        }
      }
    ];

    return {
      key: +new Date(),
      isShowMap: false, // 展示选点弹窗
      address: null, // 地址
      searchKey: '', // 搜索
      markers: [defaultPoint], // 地图标点
      center: defaultPoint, // 当前坐标位置
      zoom: 12, // 最大缩放比例
      lng: 0, // 经度
      lat: 0, // 纬度
      amapManager, // 高德地图管理器
      events, // 高德地图事件处理
      plugin, // 高德地图插件
      location:{
        address:null,
        addressName:null,
        province:null,
        district:null,
        latitude:null,
        longitude:null,
        city:null,
        cityCode:null,
        district:null,
        districtCode:null,
      }
    };
  },
  methods: {
    // 展示地图选点组件
    showMapSelector() {
      this.isShowMap = true;
      this.$nextTick(() => {
        this.resetMap();
      });
    },
    
    setLocation(item){
      console.log("setLocation===",item);
      this.location.address=item.addressComponent.city+item.addressComponent.district+item.addressComponent.township+item.addressComponent.street+item.addressComponent.streetNumber;
      this.location.addressName=item.formattedAddress;
      this.location.latitude= this.lat;
      this.location.longitude= this.lng;
      this.location.province=item.addressComponent.province;
      this.location.city=item.addressComponent.city;
      this.location.district=item.addressComponent.district;
      this.location.districtCode=item.addressComponent.adcode;
      this.location.cityCode=item.addressComponent.adcode.substr(0,4);
    },

    // 初始化搜索
    initSearch() {
      let vm = this;
      let map = this.amapManager.getMap();
      AMapUI.loadUI(['misc/PoiPicker'], function(PoiPicker) {
        var poiPicker = new PoiPicker({
          input: 'search',
          placeSearchOptions: {
            map: map,
            pageSize: 10
          },
          suggestContainer: 'searchTip',
          searchResultsContainer: 'searchTip'
        });
        vm.poiPicker = poiPicker;
        // 监听poi选中信息
        poiPicker.on('poiPicked', function(poiResult) {
          let source = poiResult.source;
          let poi = poiResult.item;
          if (source !== 'search') {
            poiPicker.searchByKeyword(poi.name);
          } else {
            poiPicker.clearSearchResults();
            console.log("poiPicker==poi=",poi);
            vm.markers = [];
            let lng = poi.location.lng;
            let lat = poi.location.lat;
            let address = poi.cityname + poi.adname + poi.name;
            vm.center = [lng, lat];
            vm.markers.push([lng, lat]);
            vm.lng = lng;
            vm.lat = lat;
            vm.address = address;
            vm.searchKey = address;
            vm.location.address=poi.cityname+poi.adname+poi.address;
            vm.location.addressName=poi.name;
            vm.location.latitude=lat;
            vm.location.longitude=lng;
            vm.location.province=poi.pname;
            vm.location.city=poi.cityname;
            vm.location.district=poi.adname;
            vm.location.districtCode=poi.adcode;
            vm.location.cityCode=poi.adcode.substr(0,4);
          }
        });
      });
    },
    // 返回经纬度地址
    getAddress(lng, lat) {
      let geocoder = new AMap.Geocoder({
        radius: 1000,
        extensions: 'all'
      });
      return new Promise(resolve => {
        geocoder.getAddress([lng, lat], function(status, result) {
          if (status === 'complete' && result.info === 'OK') {
            if (result && result.regeocode) {
              resolve(result.regeocode);
            }
          }
        });
      });
    },
    // 重置地图组件
    resetMap() {
      let tempPos = this.value.length > 0 ? this.value : defaultPoint;
      this.center = tempPos;
      this.markers = [tempPos];
      this.lng = tempPos[0];
      this.lat = tempPos[1];
      this.searchKey = '';

      // 延迟调用地址避免报错
      setTimeout(() => {
        this.getAddress(this.lng, this.lat).then(res => {
          this.address = res.formattedAddress;
           this.setLocation(res);
        });
      }, 1000);
    },
    // 保存
    save() {
      this.change({
        address: this.address,
        // location: [this.lng, this.lat]
        location:this.location
      });
      this.closeModal();
    },
    // 清除当前位置
    clearLocation() {
      this.change({
        address: '',
        location: []
      });
    },
    // 修改数据
    change(res) {
      this.$emit('input', res.location);
      this.$emit('change', res);
    },
    // 退出
    closeModal() {
      this.isShowMap = false;
    }
  }
};
</script>

<style scoped lang="less">
.container {
  width: 100%;
  height: 400px;
}

p {
  margin: 0;
}

.m-b-10 {
  margin-bottom: 10px;
}

.tip-box {
  width: 100%;
  max-height: 200px;
  position: absolute;
  top: 33px;
  overflow-y: auto;
  background-color: #fff;
  z-index: 999;
}

.selector-box {
  width: 260px;
  padding: 0 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border: 1px solid #d9d9d9;
  background-color: #fff;

  > div {
    display: flex;
    justify-content: flex-start;
    align-items: center;
  }
}

.close-icon {
  color: rgba(0, 0, 0, 0.25);
}
</style>
