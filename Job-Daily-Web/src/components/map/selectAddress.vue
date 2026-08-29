<template>
  <div>
    <div class="selector-box">
      <div>
        <i type="iconweizhi" />
        <a-button :disabled="disabled" v-if="value.length != 0 && showLatLng" type="link" @click="showMapSelector">经度：{{ value[0] }} 纬度：{{ value[1] }}</a-button>
        <a-button :disabled="disabled" v-else type="link" @click="showMapSelector">选择地址</a-button>
      </div>
      <i v-if="value.length != 0 && !disabled" class="close-icon" type="iconai54" @click="clearLocation" />
    </div>

    <a-modal title="选择地址" :visible="isShowMap" centered width="860px" @ok="save" @cancel="closeModal" destroyOnClose>
      <a-row class="m-b-10" :gutter="8">
        <a-col :span="16">
          <a-input-search
            v-model="searchKey"
            placeholder="搜索地点、小区、写字楼"
            enter-button="搜索"
            :loading="searchLoading"
            @search="doSearch"
          />
          <div v-if="poiList.length" class="tip-box beauty-scroll">
            <div
              v-for="item in poiList"
              :key="item.id || item.title"
              class="poi-item"
              @click="onPoiClick(item)"
            >
              <div class="poi-title">{{ item.title }}</div>
              <div class="poi-addr">{{ item.address }}</div>
            </div>
          </div>
        </a-col>
        <a-col :span="8"><a-button type="link" @click="resetMap">重置</a-button></a-col>
      </a-row>
      <div class="container m-b-10">
        <div ref="mapEl" class="tencent-map"></div>
        <div v-if="!mapReady" class="map-mask">地图加载中…</div>
      </div>
      <p>当前地址为：{{ address || '请在地图上选点或搜索' }}</p>
      <p>经纬度：({{ lng }},{{ lat }})</p>
    </a-modal>
  </div>
</template>

<script>
/**
 * 腾讯地图选点（Key/逆地理/搜索均走后台，不直连厂商密钥）
 * 兼容原 change 事件：{ location: { address, addressName, latitude, longitude, province, city, district, cityCode, districtCode } }
 */
import { getAction } from '@/api/manage'

const DEFAULT_LAT = 25.831
const DEFAULT_LNG = 114.935

export default {
  props: {
    value: {
      type: Array,
      default: function() {
        return []
      }
    },
    disabled: {
      type: Boolean,
      default: false
    },
    showLatLng: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      isShowMap: false,
      address: null,
      searchKey: '',
      searchLoading: false,
      poiList: [],
      lng: DEFAULT_LNG,
      lat: DEFAULT_LAT,
      mapReady: false,
      mapKey: '',
      location: {
        address: null,
        addressName: null,
        province: null,
        city: null,
        district: null,
        latitude: null,
        longitude: null,
        cityCode: null,
        districtCode: null
      },
      mapInstance: null,
      markerLayer: null
    }
  },
  beforeDestroy() {
    this.destroyMap()
  },
  methods: {
    showMapSelector() {
      this.isShowMap = true
      this.$nextTick(() => {
        this.initMap()
      })
    },
    async ensureMapKey() {
      if (this.mapKey) return this.mapKey
      const res = await getAction('/job/jobPost/mapConfig')
      this.mapKey = (res && res.result && res.result.mapKey) || ''
      if (!this.mapKey) {
        this.$message.warning('未配置 map_key，请在基础配置中填写腾讯地图 Key')
      }
      return this.mapKey
    },
    loadMapScript(key) {
      return new Promise((resolve, reject) => {
        if (window.TMap) {
          resolve()
          return
        }
        const script = document.createElement('script')
        script.src = 'https://map.qq.com/api/gljs?v=1.exp&key=' + encodeURIComponent(key)
        script.onload = () => resolve()
        script.onerror = () => reject(new Error('地图脚本加载失败'))
        document.head.appendChild(script)
      })
    },
    destroyMap() {
      if (this.mapInstance && this.mapInstance.destroy) {
        this.mapInstance.destroy()
      }
      this.mapInstance = null
      this.markerLayer = null
      this.mapReady = false
    },
    async initMap() {
      const key = await this.ensureMapKey()
      if (!key) return
      try {
        await this.loadMapScript(key)
      } catch (e) {
        this.$message.error('地图加载失败')
        return
      }
      this.destroyMap()
      const initLng = this.value && this.value.length === 2 ? Number(this.value[0]) : DEFAULT_LNG
      const initLat = this.value && this.value.length === 2 ? Number(this.value[1]) : DEFAULT_LAT
      this.lng = initLng
      this.lat = initLat
      const TMap = window.TMap
      this.mapInstance = new TMap.Map(this.$refs.mapEl, {
        center: new TMap.LatLng(initLat, initLng),
        zoom: 15
      })
      this.markerLayer = new TMap.MultiMarker({
        map: this.mapInstance,
        geometries: []
      })
      this.mapInstance.on('click', (evt) => {
        const lat = evt.latLng.getLat()
        const lng = evt.latLng.getLng()
        this.reversePick(lat, lng, '地图选点')
      })
      this.mapReady = true
      this.setMarker(initLat, initLng)
      if (this.value && this.value.length === 2) {
        this.reversePick(initLat, initLng, '当前位置')
      }
    },
    setMarker(lat, lng) {
      if (!this.markerLayer || !window.TMap) return
      this.markerLayer.setGeometries([
        { id: 'pick', position: new window.TMap.LatLng(lat, lng) }
      ])
    },
    async doSearch() {
      const kw = (this.searchKey || '').trim()
      if (!kw) {
        this.poiList = []
        return
      }
      this.searchLoading = true
      try {
        const res = await getAction('/job/jobPost/map/suggestion', {
          keyword: kw,
          latitude: this.lat,
          longitude: this.lng,
          pageSize: 20
        })
        this.poiList = (res && res.success && Array.isArray(res.result)) ? res.result : []
      } catch (e) {
        this.poiList = []
        this.$message.error('地点搜索失败')
      } finally {
        this.searchLoading = false
      }
    },
    onPoiClick(item) {
      const loc = item.location || {}
      const lat = Number(loc.lat)
      const lng = Number(loc.lng)
      if (!lat || !lng) return
      this.poiList = []
      this.searchKey = item.title || ''
      if (this.mapInstance) {
        this.mapInstance.setCenter(new window.TMap.LatLng(lat, lng))
      }
      this.applyPick(lat, lng, item.title || '选中地点', item.address || '', item)
    },
    async reversePick(lat, lng, title) {
      this.setMarker(lat, lng)
      this.lat = lat
      this.lng = lng
      try {
        const res = await getAction('/job/jobPost/map/reverseGeocoder', {
          latitude: lat,
          longitude: lng
        })
        const data = (res && res.success) ? res.result : null
        const ad = (data && data.ad_info) || {}
        const ac = (data && data.address_component) || {}
        const address = (data && (data.address || (data.formatted_addresses && data.formatted_addresses.recommend))) || ''
        this.applyPick(lat, lng, title || address || '地图选点', address, { ad_info: ad, address_component: ac })
      } catch (e) {
        this.applyPick(lat, lng, title || '地图选点', '', {})
      }
    },
    applyPick(lat, lng, title, address, raw) {
      this.lat = lat
      this.lng = lng
      this.address = address || title
      this.setMarker(lat, lng)
      const ad = (raw && raw.ad_info) || {}
      const ac = (raw && raw.address_component) || {}
      const adcode = String(ad.adcode || '')
      this.location = {
        address: address || title,
        addressName: title || address,
        latitude: lat,
        longitude: lng,
        province: ac.province || ad.province || '',
        city: ac.city || ad.city || '',
        district: ac.district || ad.district || '',
        districtCode: adcode,
        cityCode: adcode ? adcode.substr(0, 4) : ''
      }
    },
    resetMap() {
      this.searchKey = ''
      this.poiList = []
      this.destroyMap()
      this.$nextTick(() => this.initMap())
    },
    clearLocation() {
      this.$emit('change', {
        location: {
          address: null,
          addressName: null,
          latitude: null,
          longitude: null,
          province: null,
          city: null,
          district: null,
          cityCode: null,
          districtCode: null
        }
      })
      this.$emit('input', [])
    },
    save() {
      if (!this.location.latitude || !this.location.longitude) {
        this.$message.warning('请先选择地址')
        return
      }
      this.$emit('change', { location: this.location })
      this.$emit('input', [this.location.longitude, this.location.latitude])
      this.closeModal()
    },
    closeModal() {
      this.isShowMap = false
      this.destroyMap()
      this.poiList = []
    }
  }
}
</script>

<style scoped>
.selector-box {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.container {
  position: relative;
  width: 100%;
  height: 420px;
}
.tencent-map {
  width: 100%;
  height: 100%;
}
.map-mask {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.7);
  z-index: 2;
}
.tip-box {
  max-height: 180px;
  overflow: auto;
  border: 1px solid #eee;
  margin-top: 6px;
  background: #fff;
}
.poi-item {
  padding: 8px 10px;
  cursor: pointer;
  border-bottom: 1px solid #f0f0f0;
}
.poi-item:hover {
  background: #f5f7fa;
}
.poi-title {
  font-weight: 500;
}
.poi-addr {
  color: #999;
  font-size: 12px;
}
.m-b-10 {
  margin-bottom: 10px;
}
.close-icon {
  cursor: pointer;
}
</style>
