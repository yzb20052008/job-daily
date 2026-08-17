<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="关键词">
              <a-input placeholder="请输入关键词" v-model="queryParam.keyword"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="认证状态">
              <j-dict-select-tag placeholder="请选择认证状态" v-model="queryParam.authStatus" dictCode="auth_status" />
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
              <a @click="handleToggleSearch" style="margin-left: 8px">
                {{ toggleSearchStatus ? '收起' : '展开' }}
                <a-icon :type="toggleSearchStatus ? 'up' : 'down'" />
              </a>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <!-- 查询区域-END -->

    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <!-- <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>
      <a-button type="primary" icon="download" @click="handleExportXls('实名认证')">导出</a-button>
      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">
        <a-button type="primary" icon="import">导入</a-button>
      </a-upload> -->
      <!-- 高级查询区域 -->
      <!-- <j-super-query :fieldList="superFieldList" ref="superQueryModal" @handleSuperQuery="handleSuperQuery"></j-super-query> -->
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
        <template slot="statusSlot" slot-scope="text,record">
          <div v-if="record.authStatus == 0" style="color: crimson;font-style: italic">{{text}}</div>
          <div v-else-if="record.authStatus == 1" style="color: darkgreen;font-style: italic">{{text}}</div>
          <div v-else style="color: crimson;font-style: italic">{{text}}</div>
        </template>
        <span slot="action" slot-scope="text, record">
          <a @click="handleVerify(record)" v-if="record.authStatus==0">审核</a>
          <a-divider type="vertical" v-if="record.authStatus==0"/>
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down" /></a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a @click="showDetail(record)">详情</a>
              </a-menu-item>
              <a-menu-item>
                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                  <a>删除</a>
                </a-popconfirm>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </span>

      </a-table>
    </div>
    <ums-realname-verify-modal :type="detailType" :verify="true" ref="verifyForm" @success="handleSuccess"></ums-realname-verify-modal>
    <ums-realname-auth-modal ref="modalForm" @ok="modalFormOk"></ums-realname-auth-modal>
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
  import UmsRealnameAuthModal from './modules/UmsRealnameAuthModal'
  import UmsRealnameVerifyModal from './modules/UmsRealnameVerifyModal.vue'
  export default {
    name: 'UmsRealnameAuthList',
    mixins: [JeecgListMixin, mixinDevice],
    components: {
      UmsRealnameAuthModal,
      UmsRealnameVerifyModal
    },
    data() {
      return {
        description: '实名认证管理页面',
        detailType:0,
        // 表头
        columns: [
          {
            title:'头像',
            align:"center",
            dataIndex: 'userAvatar',
            scopedSlots: {customRender: 'imgSlot'}
          },
          {
            title: '昵称',
            align: "center",
            dataIndex: 'userName'
          },
          {
            title: '手机号',
            align: "center",
            dataIndex: 'userPhone'
          },
          {
            title: '真实姓名',
            align: "center",
            dataIndex: 'realname'
          },
          {
            title: '身份证号',
            align: "center",
            dataIndex: 'idNo'
          },
          {
            title: '身份证正面',
            align: "center",
            dataIndex: 'idCardFront',
            scopedSlots: {customRender: 'imgSlot'}
          },
          {
            title: '身份证反面',
            align: "center",
            dataIndex: 'idCardBack',
            scopedSlots: {customRender: 'imgSlot'}
          },
          {
            title: '审核状态',
            align: "center",
            dataIndex: 'authStatus_dictText',
            scopedSlots: {customRender: 'statusSlot'}
          },
          {
            title: '提交时间',
            align: "center",
            dataIndex: 'createTime'
          },
          // {
          //   title: '审核时间',
          //   align: "center",
          //   dataIndex: 'authTime'
          // },
          // {
          //   title: '审核备注',
          //   align: "center",
          //   dataIndex: 'authRemark'
          // },
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
          list: "/ums/umsRealnameAuth/list",
          delete: "/ums/umsRealnameAuth/delete",
          deleteBatch: "/ums/umsRealnameAuth/deleteBatch",
          exportXlsUrl: "/ums/umsRealnameAuth/exportXls",
          importExcelUrl: "ums/umsRealnameAuth/importExcel",

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
      
      handleVerify(record) {
         this.detailType=0;
        console.log("===handleVerify===", record);
        this.$refs.verifyForm.edit(record);
        this.$refs.verifyForm.title = "详情";
        this.$refs.verifyForm.disableSubmit = true;
      },
      
      showDetail(record) {
        this.detailType=1;
        console.log("===showDetail===", record);
        this.$refs.verifyForm.edit(record);
        this.$refs.verifyForm.title = "详情";
        this.$refs.verifyForm.disableSubmit = true;
      },
      
      handleSuccess() {
        console.log('=====handleSuccess=====');
        this.$refs.verifyForm.close();
        this.loadData();
        this.onClearSelected();
      },
      
      initDictConfig() {},
      getSuperFieldList() {
        let fieldList = [];
        fieldList.push({
          type: 'string',
          value: 'userId',
          text: '会员ID',
          dictCode: ''
        })
        fieldList.push({
          type: 'string',
          value: 'realname',
          text: '姓名',
          dictCode: ''
        })
        fieldList.push({
          type: 'string',
          value: 'idNo',
          text: '身份证号',
          dictCode: ''
        })
        fieldList.push({
          type: 'string',
          value: 'idImg',
          text: '身份证照片',
          dictCode: ''
        })
        fieldList.push({
          type: 'string',
          value: 'authStatus',
          text: '认证状态:0-待认证，1-认证通过，2-认证失败',
          dictCode: "auth_status,,"
        })
        fieldList.push({
          type: 'datetime',
          value: 'authTime',
          text: '审核时间'
        })
        fieldList.push({
          type: 'string',
          value: 'authRemark',
          text: '审核备注',
          dictCode: ''
        })
        fieldList.push({
          type: 'string',
          value: 'remark',
          text: '备注',
          dictCode: ''
        })
        fieldList.push({
          type: 'int',
          value: 'tenantId',
          text: 'tenantId',
          dictCode: ''
        })
        fieldList.push({
          type: 'int',
          value: 'delFlag',
          text: '删除状态(0-正常,1-已删除)',
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