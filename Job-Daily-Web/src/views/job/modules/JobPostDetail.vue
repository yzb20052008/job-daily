<template>
  <a-spin :spinning="confirmLoading">
    <a-collapse v-model="activeKey">
      <a-collapse-panel key="1">
        <template slot="header">
          <div style="font-weight: bold;">招工信息</div>
        </template>
        <a-descriptions title="" bordered :column="2" size="middle">
          <a-descriptions-item label="标题" :span="2">{{ model.title }}</a-descriptions-item>
          <a-descriptions-item label="想招工种">{{ model.typeNames}}</a-descriptions-item>
          <a-descriptions-item label="工资">{{ model.salary}}{{ model.salaryUnit}}</a-descriptions-item>
          <a-descriptions-item label="性别要求">{{ model.sexRequire}}</a-descriptions-item>
          <a-descriptions-item label="联系电话">{{ model.phone}}</a-descriptions-item>
          <a-descriptions-item label="发布时间">{{ formatDate(model.createTime) }}</a-descriptions-item>
          <a-descriptions-item label="浏览次数">{{ model.browseNumber}}</a-descriptions-item>
          <a-descriptions-item label="截止时间">{{ formatDate(model.closeTime)}}</a-descriptions-item>
          <a-descriptions-item label="招工状态" :span="2">{{ model.postStatus | formatStatus }}</a-descriptions-item>
          <a-descriptions-item label="工作地址" :span="2">
            <div>
              {{ model.addressName}}（{{ model.address}}）
            </div>
          </a-descriptions-item>
          <!-- <a-descriptions-item>
            <div slot="label" style="font-weight: normal;">工作地址</div>
            <div style="width: 100%;height: 280px">
              <el-amap :key="key" class="amap-box" :amap-manager="amapManager" :vid="'amap-vue' + key" :zoom="zoom"
                :plugin="plugin" :center="center" :events="events">
                <el-amap-marker v-for="(marker, index) in markers" :position="marker" :key="index"></el-amap-marker>
              </el-amap>
            </div>
            <span class="span-wrap" style="padding-top: 10px;">{{ model.addressName}}（{{ model.address}}）</span>
          </a-descriptions-item> -->
        </a-descriptions>
      </a-collapse-panel>
      <a-collapse-panel key="2">
        <template slot="header">
          <div style="font-weight: bold;">发布者信息</div>
        </template>
        <a-descriptions title="" bordered :column="2" size="middle">
          <a-descriptions-item label="头像">
            <img :src="getImgView(model.userAvatar)" :preview="model.id" height="25px" alt=""
              style="max-width:80px;font-size: 12px;font-style: italic;" />
          </a-descriptions-item>
          <a-descriptions-item label="姓名">{{ model.userName }}</a-descriptions-item>
          <a-descriptions-item label="电话">{{ model.userPhone}}</a-descriptions-item>
          <a-descriptions-item label="实名认证">{{ model.ifRealName?'已认证':'未认证'}}</a-descriptions-item>
          <a-descriptions-item label="企业认证">{{ model.ifCompanyAuth?'已认证':'未认证'}}</a-descriptions-item>
        </a-descriptions>
      </a-collapse-panel>
    </a-collapse>
  </a-spin>
</template>

<script>
  import {
    httpAction,
    getAction,
    getFileAccessHttpUrl
  } from '@/api/manage'
  import {
    validateDuplicateValue
  } from '@/utils/util'
  import {
    AMapManager,
    lazyAMapApiLoaderInstance
  } from 'vue-amap';
  import VueAMap from 'vue-amap';
  import Vue from 'vue';
  import moment from 'moment'

  let amapManager = new AMapManager();
  let defaultPoint = [114.93, 25.83];
  moment.locale('zh-cn')
  export default {
    name: 'JobPostForm',
    components: {},
    props: {
      //表单禁用
      disabled: {
        type: Boolean,
        default: false,
        required: false
      }
    },
    data() {
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
        location: {
          address: null,
          addressName: null,
          province: null,
          city: null,
          district: null,
          latitude: null,
          longitude: null
        },


        id: '',
        activeKey: ['1', '2'],
        model: {},
        labelCol: {
          xs: {
            span: 24
          },
          sm: {
            span: 5
          },
        },
        wrapperCol: {
          xs: {
            span: 24
          },
          sm: {
            span: 16
          },
        },
        confirmLoading: false,
        validatorRules: {},
        url: {
          add: "/job/jobPost/add",
          edit: "/job/jobPost/edit",
          queryById: "/job/jobPost/queryById"
        }
      };

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
          let {
            lng,
            lat
          } = e.lnglat;
          _this.lng = lng;
          _this.lat = lat;
          _this.center = [lng, lat];
          _this.markers.push([lng, lat]);
          _this.getAddress(lng, lat).then(res => {
            _this.address = res.formattedAddress;
            _this.setLocation(res);
          });
        }
      };

      // 高德地图插件
      const plugin = [{
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
    },
    filters: {
      formatSex(val) {
        if (val == 1) {
          return '男'
        } else if (val == 2) {
          return '女'
        } else {
          return '未知'
        }
      },
      // 招工状态：1-待审核，2-招工中，3-发布失败，4-已停招，5-已取消，6-已招满 
      formatStatus(val) {
        if (val == 1) {
          return '待审核'
        } else if (val == 2) {
          return '招工中'
        } else if (val == 3) {
          return '发布失败'
        } else if (val == 4) {
          return '已停招'
        } else if (val == 5) {
          return '已取消'
        } else if (val == 6) {
          return '已招满'
        }
      }
    },
    computed: {
      formDisabled() {
        return this.disabled
      },
    },
    created() {
      //备份model原始值
      this.modelDefault = JSON.parse(JSON.stringify(this.model));
    },
    methods: {

      formatDate(val) {
        let time = moment(val).format('yyyy-MM-DD HH:mm');
        return time;
      },

      /* 图片预览 */
      getImgView(text) {
        if (text && text.indexOf(",") > 0) {
          text = text.substring(0, text.indexOf(","))
        }
        return getFileAccessHttpUrl(text)
      },

      add() {
        this.edit(this.modelDefault);
      },
      edit(record) {
        this.visible = true;
        this.id = record.id;
        this.init();
      },
      init() {
        getAction(this.url.queryById, {
          id: this.id
        }).then(res => {
          console.log(res);
          if (res.code == 200) {
            this.model = res.result;
          }
        });
      },
    }
  }
</script>

<style>
  .span-wrap {
    white-space: pre-wrap;
  }

  .ant-descriptions-item-label {
    width: 130px;
  }
</style>