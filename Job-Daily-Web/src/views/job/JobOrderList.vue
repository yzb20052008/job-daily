<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="关键字">
              <a-input placeholder="请输入关键字" v-model="queryParam.keyword"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="订单状态">
              <j-dict-select-tag placeholder="请选择订单状态" v-model="queryParam.orderStatus" dictCode="order_status"/>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
              <!-- <a @click="handleToggleSearch" style="margin-left: 8px">
                {{ toggleSearchStatus ? '收起' : '展开' }}
                <a-icon :type="toggleSearchStatus ? 'up' : 'down'" />
              </a> -->
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <!-- 查询区域-END -->

    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <!-- <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>
      <a-button type="primary" icon="download" @click="handleExportXls('订单信息')">导出</a-button>
      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl"
        @change="handleImportExcel">
        <a-button type="primary" icon="import">导入</a-button>
      </a-upload> -->
      <!-- 高级查询区域 -->
      <!-- <j-super-query :fieldList="superFieldList" ref="superQueryModal"
        @handleSuperQuery="handleSuperQuery"></j-super-query> -->
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel"><a-icon type="delete" />删除</a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px"> 批量操作 <a-icon type="down" /></a-button>
      </a-dropdown>
    </div>

    <!-- table区域-begin -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a
          style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
      </div>

      <a-table ref="table" size="middle" :scroll="{x:true}" bordered rowKey="id" :columns="columns"
        :dataSource="dataSource" :pagination="ipagination" :loading="loading"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}" class="j-table-force-nowrap"
        @change="handleTableChange">

        <template slot="htmlSlot" slot-scope="text">
          <div v-html="text"></div>
        </template>
        <template slot="imgSlot" slot-scope="text,record">
          <span v-if="!text" style="font-size: 12px;font-style: italic;">无图片</span>
          <img v-else :src="getImgView(text)" :preview="record.id" height="25px" alt=""
            style="max-width:80px;font-size: 12px;font-style: italic;" />
        </template>
        <template slot="fileSlot" slot-scope="text">
          <span v-if="!text" style="font-size: 12px;font-style: italic;">无文件</span>
          <a-button v-else :ghost="true" type="primary" icon="download" size="small" @click="downloadFile(text)">
            下载
          </a-button>
        </template>

        <template slot="workTimeSlot" slot-scope="text,record">
          <div>{{formatDate(record.startTime)}}-{{formatDate(record.endTime)}}</div>
        </template>
        <template slot="salarySlot" slot-scope="text,record">
          <div>{{record.salary}}{{record.salaryUnit}}</div>
        </template>

        <template slot="userSlot" slot-scope="text,record">
          <div style="display: flex;flex-direction: row; align-items: center;justify-content: center;">
            <img :src="getImgView(record.userAvatar)" :preview="record.id" height="25px" alt=""
              style="max-width:80px;font-size: 12px;font-style: italic;" />
            <div style="margin-left: 10px;">{{record.userName}}</div>
          </div>
        </template>
        
        <template slot="postUserSlot" slot-scope="text,record">
          <div style="display: flex;flex-direction: row; align-items: center;justify-content: center;">
            <img :src="getImgView(record.postUserAvatar)" :preview="record.id" height="25px" alt=""
              style="max-width:80px;font-size: 12px;font-style: italic;" />
            <div style="margin-left: 10px;">{{record.postUserName}}</div>
          </div>
        </template>

        <!-- 状态：0-待确认，1-待开工，2-工作中，3-待结算，4-待评价，5-已完成，6-已取消 -->
        <template slot="statusSlot" slot-scope="text">
          <div v-if="text==0" style="color: red;">待确认</div>
          <div v-else-if="text==1" style="color: darkgreen;">待开工</div>
          <div v-else-if="text==2" style="color: darkred;">工作中</div>
          <div v-else-if="text==3">待结算</div>
          <div v-else-if="text==4">待评价</div>
          <div v-else-if="text==5">已完成</div>
          <div v-else-if="text==6">已取消</div>
        </template>

        <span slot="action" slot-scope="text, record">
          <a @click="showPostDetail(record)">招工信息</a>
          <a-divider type="vertical" />
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down" /></a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a @click="handleDetail(record)">订单详情</a>
              </a-menu-item>
              <a-menu-item>
                <a @click="showOrderLog(record)">操作记录</a>
              </a-menu-item>
              <!-- <a-menu-item>
                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                  <a>删除订单</a>
                </a-popconfirm>
              </a-menu-item> -->
            </a-menu>
          </a-dropdown>
        </span>

      </a-table>
    </div>

    <job-order-modal ref="modalForm" @ok="modalFormOk"></job-order-modal>
    <job-order-log-list-modal ref="logForm" @ok="modalFormOk"></job-order-log-list-modal>
    <job-post-modal ref="postModal" @ok="modalFormOk"></job-post-modal>
  </a-card>
</template>

<script>
  import '@/assets/less/TableExpand.less'
  import {
    mixinDevice
  } from '@/utils/mixin'
  import {
    JeecgListMixin
  } from '@/mixins/JeecgListMixin'
  import JobOrderModal from './modules/JobOrderModal'
  import JobOrderLogListModal from './modules/JobOrderLogListModal'
   import JobPostModal from './modules/JobPostModal'
  import {
    filterMultiDictText
  } from '@/components/dict/JDictSelectUtil'
  import moment from 'moment'
  moment.locale('zh-cn')
  export default {
    name: 'JobOrderList',
    mixins: [JeecgListMixin, mixinDevice],
    components: {
      JobOrderModal,
      JobOrderLogListModal,
      JobPostModal
    },
    data() {
      return {
        description: '订单信息管理页面',
        // 表头
        columns: [
          {
            title: '工人信息',
            align: "center",
            dataIndex: 'userId',
            scopedSlots: {
              customRender: 'userSlot'
            }
          },
          {
            title: '招工标题',
            align: "center",
            dataIndex: 'title'
          },
          {
            title: '工作时间',
            align: "center",
            dataIndex: 'startTime',
            scopedSlots: {
              customRender: 'workTimeSlot'
            }
          },
          {
            title: '单价',
            align: "center",
            dataIndex: 'salary',
            scopedSlots: {
              customRender: 'salarySlot'
            }
          },
          // {
          //   title: '时长（小时）',
          //   align: "center",
          //   dataIndex: 'duration'
          // },
          {
            title: '工资金额',
            align: "center",
            dataIndex: 'amount'
          },
          {
            title: '订单状态',
            align: "center",
            dataIndex: 'orderStatus',
            scopedSlots: {
              customRender: 'statusSlot'
            }
          },
          {
            title: '老板信息',
            align: "center",
            dataIndex: 'postUserId',
            scopedSlots: {
              customRender: 'postUserSlot'
            }
          },
          {
            title: '创建时间',
            align: "center",
            dataIndex: 'createTime',
          },
          {
            title: '操作',
            dataIndex: 'action',
            align: "center",
            fixed: "right",
            width: 147,
            scopedSlots: {
              customRender: 'action'
            }
          }
        ],
        url: {
          list: "/job/jobOrder/list",
          delete: "/job/jobOrder/delete",
          deleteBatch: "/job/jobOrder/deleteBatch",
          exportXlsUrl: "/job/jobOrder/exportXls",
          importExcelUrl: "job/jobOrder/importExcel",

        },
        dictOptions: {},
        superFieldList: [],
      }
    },
    created() {
      this.getSuperFieldList();
    },
    computed: {
      importExcelUrl: function() {
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      },
    },
    methods: {
      
      showPostDetail(record){
        record.id=record.postId;
        this.$refs.postModal.detail(record);
        this.$refs.postModal.title="详情";
        this.$refs.postModal.disableSubmit = true;
      },
      
      showOrderLog(record){
        this.$refs.logForm.show(record);
      },

      formatDate(val) {
        let time = moment(val).format('MM-DD HH:mm');
        return time;
      },

      initDictConfig() {},
      getSuperFieldList() {
        let fieldList = [];
        fieldList.push({
          type: 'string',
          value: 'userId',
          text: '用户ID',
          dictCode: ''
        })
        fieldList.push({
          type: 'string',
          value: 'postUserId',
          text: '老板ID',
          dictCode: ''
        })
        fieldList.push({
          type: 'string',
          value: 'postId',
          text: '招工ID',
          dictCode: ''
        })
        fieldList.push({
          type: 'int',
          value: 'ifCalled',
          text: '是否电话联系',
          dictCode: ''
        })
        fieldList.push({
          type: 'string',
          value: 'startTime',
          text: '所需积分',
          dictCode: ''
        })
        fieldList.push({
          type: 'string',
          value: 'endTime',
          text: '日期长度',
          dictCode: ''
        })
        fieldList.push({
          type: 'BigDecimal',
          value: 'unitPrice',
          text: '单价（元/时）',
          dictCode: ''
        })
        fieldList.push({
          type: 'int',
          value: 'duration',
          text: '时长（小时）',
          dictCode: ''
        })
        fieldList.push({
          type: 'BigDecimal',
          value: 'payableAmount',
          text: '应付金额',
          dictCode: ''
        })
        fieldList.push({
          type: 'BigDecimal',
          value: 'amount',
          text: '实付金额',
          dictCode: ''
        })
        fieldList.push({
          type: 'string',
          value: 'orderStatus',
          text: '订单状态：0-待确认，1-待开工，2-工作中，3-待结算，4-待评价，5-已完成',
          dictCode: 'order_status'
        })
        fieldList.push({
          type: 'string',
          value: 'remark',
          text: '描述',
          dictCode: ''
        })
        fieldList.push({
          type: 'int',
          value: 'sort',
          text: '排序',
          dictCode: ''
        })
        fieldList.push({
          type: 'int',
          value: 'status',
          text: '启用状态',
          dictCode: ''
        })
        this.superFieldList = fieldList
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>