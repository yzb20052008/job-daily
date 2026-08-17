<template>
  <a-spin :spinning="confirmLoading">
    <a-collapse v-model="activeKey">
      <a-collapse-panel key="1">
        <template slot="header">
          <div style="font-weight: bold;">订单信息</div>
        </template>
        <a-descriptions title="" bordered :column="1" size="middle">
          <a-descriptions-item label="订单状态" :span="1">{{ model | formatOrderStatus }}</a-descriptions-item>
          <a-descriptions-item label="创建时间">{{ formatDate(model.createTime)}}</a-descriptions-item>
          <a-descriptions-item label="上班打卡" :span="1" v-if="model.startClock">
            <div>
              <div>
                <div>打卡时间：{{model.startClock.createTime}}</div>
                <div>打卡地点：{{model.startClock.address}}</div>
                <div>打卡距离：{{model.startClock.distance}}米</div>
                <div>
                  <img :src="getImgView(model.startClock.images)" :preview="model.startClock.id" alt=""
                      style="width:120px;height: 120px;;font-size: 12px;font-style: italic;" />
                </div>
              </div>
            </div>
          </a-descriptions-item>
          <a-descriptions-item label="下班打卡" :span="1" v-if="model.endClock">
            <div>
              <div>打卡时间：{{model.endClock.createTime}}</div>
              <div>打卡地点：{{model.endClock.address}}</div>
              <div>打卡距离：{{model.endClock.distance}}米</div>
              <div>
                <div style="">
                  <img :src="getImgView(model.endClock.images)" :preview="model.endClock.id" height="25px" alt=""
                    style="width:120px;height: 120px;;font-size: 12px;font-style: italic;" />
                </div>
              </div>
            </div>
          </a-descriptions-item>
        </a-descriptions>
      </a-collapse-panel>
      <a-collapse-panel key="2">
        <template slot="header">
          <div style="font-weight: bold;">招工信息</div>
        </template>
        <a-descriptions title="" bordered :column="2" size="middle">
          <a-descriptions-item label="标题" :span="2">{{ model.post.title }}</a-descriptions-item>
          <a-descriptions-item label="想招工种">{{ model.post.typeNames}}</a-descriptions-item>
          <a-descriptions-item label="工资">{{ model.post.salary}}{{ model.post.salaryUnit}}</a-descriptions-item>
          <a-descriptions-item label="性别要求">{{ model.post.sexRequire}}</a-descriptions-item>
          <a-descriptions-item label="联系电话">{{ model.post.phone}}</a-descriptions-item>
          <a-descriptions-item label="发布时间">{{ formatDate(model.post.createTime) }}</a-descriptions-item>
          <a-descriptions-item label="浏览次数">{{ model.post.browseNumber}}</a-descriptions-item>
          <a-descriptions-item label="截止时间">{{ formatDate(model.post.closeTime)}}</a-descriptions-item>
          <a-descriptions-item label="招工状态" :span="2">{{ model.post.postStatus | formatStatus }}</a-descriptions-item>
          <a-descriptions-item label="工作地址" :span="2">
            <div>
              {{ model.post.addressName}}（{{ model.post.address}}）
            </div>
          </a-descriptions-item>
        </a-descriptions>
      </a-collapse-panel>
      <a-collapse-panel key="3">
        <template slot="header">
          <div style="font-weight: bold;">用户信息</div>
        </template>
        <a-descriptions title="" bordered :column="2" size="middle">
          <a-descriptions-item label="老板头像">
            <img :src="getImgView(model.postUser.avatar)" :preview="model.postUser.id" height="25px" alt=""
              style="max-width:80px;font-size: 12px;font-style: italic;" />
          </a-descriptions-item>
          <a-descriptions-item label="工人头像">
            <img :src="getImgView(model.user.avatar)" :preview="model.user.id" height="25px" alt=""
              style="max-width:80px;font-size: 12px;font-style: italic;" />
          </a-descriptions-item>
          <a-descriptions-item label="老板姓名">{{ model.postUser.nickname }}</a-descriptions-item>
          <a-descriptions-item label="工人姓名">{{ model.user.nickname }}</a-descriptions-item>
          <a-descriptions-item label="老板电话">{{ model.postUser.phone}}</a-descriptions-item>
          <a-descriptions-item label="工人电话">{{ model.user.phone}}</a-descriptions-item>
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
    name: 'JobOrderForm',
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
        id: '',
        activeKey: ['1', '2'],
        model: {
          post: {},
          user: {},
          postUser: {},
          startClock: null,
          endClock: null,
        },
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
          add: "/job/jobOrder/add",
          edit: "/job/jobOrder/edit",
          queryById: "/job/jobOrder/queryById"
        }
      }
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

      // 订单状态：1-待审核，2-招工中，3-发布失败，4-已停招，5-已取消，6-已招满
      formatOrderStatus(item) {
        let str = '';
        if (item.orderStatus == '0') {
          return "待确认"
        } else if (item.orderStatus == '1') {
          return "待开工"
        } else if (item.orderStatus == '2') {
          return "工作中"
        } else if (item.orderStatus == '3') {
          return "待结算"
        } else if (item.orderStatus == '4') {
          if (item.userEvaluate == 1) {
            return "已完成"
          } else {
            return "待评价"
          }
        } else if (item.orderStatus == '5') {
          return "已完成"
        } else if (item.orderStatus == '6') {
          return "已取消"
        }
        return str;
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
    }
  }
</script>