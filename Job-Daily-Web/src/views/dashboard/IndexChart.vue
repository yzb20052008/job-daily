<template>
  <div class="page-header-index-wide">
    <a-row :gutter="24">
      <a-col :sm="24" :md="12" :xl="6" :style="{ marginBottom: '24px' }">
        <chart-card :loading="loading" title="总用户数" :total="userInfo.userCount.totalCount| NumberFormat">
          <a-tooltip title="注册用户总数" slot="action">
            <a-icon type="info-circle-o" />
          </a-tooltip>
          <div>
            <trend flag="up" style="margin-right: 16px;">
              <span slot="term">今日新增</span>
               {{userInfo.todayCount.totalCount | NumberFormat }}
            </trend>
            <trend flag="up">
              <span slot="term">昨日新增</span>
              {{userInfo.yesterdayCount.totalCount | NumberFormat}}
            </trend>
          </div>
        </chart-card>
      </a-col>
      <a-col :sm="24" :md="12" :xl="6" :style="{ marginBottom: '24px' }">
        <chart-card :loading="loading" title="认证用户数" :total="userInfo.userCount.verifyedCount | NumberFormat">
          <a-tooltip title="认证用户数" slot="action">
            <a-icon type="info-circle-o" />
          </a-tooltip>
          <!-- <div>
            <mini-area />
          </div> -->
          <div>
            <trend flag="up" style="margin-right: 16px;">
              <span slot="term">今日新增</span>
               {{userInfo.todayCount.verifyedCount | NumberFormat }}
            </trend>
            <trend flag="up">
              <span slot="term">昨日新增</span>
              {{userInfo.yesterdayCount.verifyedCount | NumberFormat}}
            </trend>
          </div>
        </chart-card>
      </a-col>
      <a-col :sm="24" :md="12" :xl="6" :style="{ marginBottom: '24px' }">
        <chart-card :loading="loading" title="待审核实名认证" :total="userInfo.userCount.verifyCount | NumberFormat ">
          <a-tooltip title="待审核实名认证" slot="action">
            <a-icon type="info-circle-o" />
          </a-tooltip>
          <div>
            <trend flag="up" style="margin-right: 16px;">
              <span slot="term">今日新增</span>
               {{userInfo.todayCount.verifyCount | NumberFormat }}
            </trend>
            <trend flag="up">
              <span slot="term">昨日新增</span>
              {{userInfo.yesterdayCount.verifyCount | NumberFormat}}
            </trend>
          </div>
        </chart-card>
      </a-col>
      <a-col :sm="24" :md="12" :xl="6" :style="{ marginBottom: '24px' }">
        <chart-card :loading="loading" title="订单数量" :total="companyInfo.orderCount.totalCount | NumberFormat ">
          <a-tooltip title="订单数量" slot="action">
            <a-icon type="info-circle-o" />
          </a-tooltip>
          <div>
            <trend flag="up" style="margin-right: 16px;">
              <span slot="term">今日新增</span>
               {{companyInfo.todayOrderCount.totalCount | NumberFormat }}
            </trend>
            <trend flag="up">
              <span slot="term">昨日新增</span>
              {{companyInfo.yesterdayOrderCount.totalCount | NumberFormat}}%
            </trend>
          </div>
        </chart-card>
      </a-col>
    </a-row>
    
    <a-row :gutter="24">
      <a-col :sm="24" :md="12" :xl="6" :style="{ marginBottom: '24px' }">
        <chart-card :loading="loading" title="企业认证数" :total="companyInfo.companyCount.verifyedCount| NumberFormat">
          <a-tooltip title="企业认证数" slot="action">
            <a-icon type="info-circle-o" />
          </a-tooltip>
          <div>
            <trend flag="up" style="margin-right: 16px;">
              <span slot="term">今日新增</span>
               {{companyInfo.todayCompanyCount.totalCount | NumberFormat }}
            </trend>
            <trend flag="up">
              <span slot="term">昨日新增</span>
              {{companyInfo.yesterdayCompanyCount.totalCount | NumberFormat}}%
            </trend>
          </div>
        </chart-card>
      </a-col>
      <a-col :sm="24" :md="12" :xl="6" :style="{ marginBottom: '24px' }">
        <chart-card :loading="loading" title="待审核企业认证" :total="companyInfo.companyCount.waitVerifyCount | NumberFormat">
          <a-tooltip title="待审核企业认证" slot="action">
            <a-icon type="info-circle-o" />
          </a-tooltip>
          <!-- <div>
            <mini-area />
          </div> -->
          <div>
            <trend flag="up" style="margin-right: 16px;">
              <span slot="term">今日新增</span>
               {{companyInfo.todayCompanyCount.waitVerifyCount | NumberFormat }}
            </trend>
            <trend flag="up">
              <span slot="term">昨日新增</span>
              {{companyInfo.yesterdayCompanyCount.waitVerifyCount | NumberFormat}}%
            </trend>
          </div>
        </chart-card>
      </a-col>
      <a-col :sm="24" :md="12" :xl="6" :style="{ marginBottom: '24px' }">
        <chart-card :loading="loading" title="发布职位数" :total="companyInfo.positionCount.totalCount | NumberFormat ">
          <a-tooltip title="发布职位数" slot="action">
            <a-icon type="info-circle-o" />
          </a-tooltip>
          <div>
            <trend flag="up" style="margin-right: 16px;">
              <span slot="term">今日新增</span>
               {{companyInfo.todayPositionCount.totalCount | NumberFormat }}
            </trend>
            <trend flag="up">
              <span slot="term">昨日新增</span>
              {{companyInfo.yesterdayPositionCount.totalCount | NumberFormat}}%
            </trend>
          </div>
        </chart-card>
      </a-col>
      <a-col :sm="24" :md="12" :xl="6" :style="{ marginBottom: '0px' }">
        <chart-card :loading="loading" title="在线职位数" :total="companyInfo.positionCount.openCount | NumberFormat ">
          <a-tooltip title="在线职位数" slot="action">
            <a-icon type="info-circle-o" />
          </a-tooltip>
          <div>
            <trend flag="up" style="margin-right: 16px;">
              <span slot="term"> 今日新增</span>
               {{companyInfo.todayPositionCount.openCount | NumberFormat }}
            </trend>
            <trend flag="up">
              <span slot="term">昨日新增</span>
              {{companyInfo.yesterdayPositionCount.openCount | NumberFormat}}
            </trend>
          </div>
        </chart-card>
      </a-col>
    </a-row>

    <a-row>
      <a-col :span="24">
        <a-card :loading="loading" :bordered="false" title="最近一周访问量统计" :style="{ marginTop: '24px' }">
          <a-row>
            <a-col :span="6">
              <head-info title="今日IP" :content="loginfo.todayIp"></head-info>
            </a-col>
            <a-col :span="2">
              <a-spin class='circle-cust'>
                <a-icon slot="indicator" type="environment" style="font-size: 24px"  />
              </a-spin>
            </a-col>
            <a-col :span="6">
              <head-info title="今日访问" :content="loginfo.todayVisitCount"></head-info>
            </a-col>
            <a-col :span="2">
              <a-spin class='circle-cust'>
                <a-icon slot="indicator" type="team" style="font-size: 24px"  />
              </a-spin>
            </a-col>
            <a-col :span="6">
              <head-info title="总访问量" :content="loginfo.totalVisitCount"></head-info>
            </a-col>
            <a-col :span="2">
              <a-spin class='circle-cust'>
                <a-icon slot="indicator" type="rise" style="font-size: 24px"  />
              </a-spin>
            </a-col>
          </a-row>
          <line-chart-multid :fields="visitFields" :dataSource="visitInfo"></line-chart-multid>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script>
  import ChartCard from '@/components/ChartCard'
  import ACol from "ant-design-vue/es/grid/Col"
  import ATooltip from "ant-design-vue/es/tooltip/Tooltip"
  import MiniArea from '@/components/chart/MiniArea'
  import MiniBar from '@/components/chart/MiniBar'
  import MiniProgress from '@/components/chart/MiniProgress'
  import RankList from '@/components/chart/RankList'
  import Bar from '@/components/chart/Bar'
  import LineChartMultid from '@/components/chart/LineChartMultid'
  import HeadInfo from '@/components/tools/HeadInfo.vue'

  import Trend from '@/components/Trend'
  import { getLoginfo,getVisitInfo,getUserCount,getCompanyCount} from '@/api/api'

  const rankList = []
  for (let i = 0; i < 7; i++) {
    rankList.push({
      name: '白鹭岛 ' + (i+1) + ' 号店',
      total: 1234.56 - i * 100
    })
  }
  const barData = []
  for (let i = 0; i < 12; i += 1) {
    barData.push({
      x: `${i + 1}月`,
      y: Math.floor(Math.random() * 1000) + 200
    })
  }
  export default {
    name: "IndexChart",
    components: {
      ATooltip,
      ACol,
      ChartCard,
      MiniArea,
      MiniBar,
      MiniProgress,
      RankList,
      Bar,
      Trend,
      LineChartMultid,
      HeadInfo
    },
    data() {
      return {
        loading: true,
        center: null,
        rankList,
        barData,
        loginfo:{},
        visitFields:['ip','visit'],
        visitInfo:[],
        userInfo: {
          todayCount: {},
          yesterdayCount: {}
        },
        companyInfo: {
          todayCompanyCount: {},
          yesterdayCompanyCount: {}
        },
        indicator: <a-icon type="loading" style="font-size: 24px" spin />
      }
    },
    created() {
      setTimeout(() => {
        this.loading = !this.loading
      }, 1000)
      this.initLogInfo();
    },
    methods: {
      
      initLogInfo () {
        getUserCount({}).then(res=>{
           console.log("getUserCount",res);
          if(res.success){
              this.userInfo=res.result;
           }
         })
         getCompanyCount({}).then(res=>{
            console.log("getCompanyCount",res);
           if(res.success){
               this.companyInfo=res.result;
            }
          })
         
        getLoginfo(null).then((res)=>{
          if(res.success){
            Object.keys(res.result).forEach(key=>{
              res.result[key] =res.result[key]+""
            })
            this.loginfo = res.result;
          }
        })
        getVisitInfo().then(res=>{
          if(res.success){
             this.visitInfo = res.result;
           }
         })
      },
    }
  }
</script>

<style lang="less" scoped>
  .circle-cust{
    position: relative;
    top: 28px;
    left: -100%;
  }
  .extra-wrapper {
    line-height: 55px;
    padding-right: 24px;

    .extra-item {
      display: inline-block;
      margin-right: 24px;

      a {
        margin-left: 24px;
      }
    }
  }

  /* 首页访问量统计 */
  .head-info {
    position: relative;
    text-align: left;
    padding: 0 32px 0 0;
    min-width: 125px;

    &.center {
      text-align: center;
      padding: 0 32px;
    }

    span {
      color: rgba(0, 0, 0, .45);
      display: inline-block;
      font-size: .95rem;
      line-height: 42px;
      margin-bottom: 4px;
    }
    p {
      line-height: 42px;
      margin: 0;
      a {
        font-weight: 600;
        font-size: 1rem;
      }
    }
  }
</style>