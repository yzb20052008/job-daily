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
            <a-form-item label="提现状态">
              <j-dict-select-tag placeholder="请选择提现状态" v-model="queryParam.withdrawStatus" dictCode="withdraw_status" />
            </a-form-item>
          </a-col>
          <template v-if="toggleSearchStatus">
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-form-item label="账户类型">
                <j-dict-select-tag placeholder="请选择账户类型" v-model="queryParam.accountType" dictCode="account_type" />
              </a-form-item>
            </a-col>
          </template>
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
      <!-- <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button> -->
      <!-- <a-button type="primary" icon="download" @click="handleExportXls('用户提现')">导出</a-button>
      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">
        <a-button type="primary" icon="import">导入</a-button>
      </a-upload> -->
      <!-- 高级查询区域 -->
      <!-- <j-super-query :fieldList="superFieldList" ref="superQueryModal" @handleSuperQuery="handleSuperQuery"></j-super-query> -->
      <!-- <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel"><a-icon type="delete"/>删除</a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px"> 批量操作 <a-icon type="down" /></a-button>
      </a-dropdown> -->
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

        <span slot="action" slot-scope="text, record">
          <a style="color: green;" v-if="record.withdrawStatus==0" @click="handleVerify(record,1)">通过</a>
          <a-divider type="vertical" v-if="record.withdrawStatus==0"/>
          <a style="color: red;" v-if="record.withdrawStatus==0" @click="handleVerify(record,2)">拒绝</a>
          <!--  <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down" /></a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a @click="handleDetail(record)">详情</a>
              </a-menu-item>
              <a-menu-item>
                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                  <a>删除</a>
                </a-popconfirm>
              </a-menu-item>
            </a-menu>
          </a-dropdown> -->
        </span>

      </a-table>
    </div>

    <a-modal :title="modalTitle" :visible="showModal" @ok="ok" @cancel="showModal = false" okText="确认" cancelText="关闭">
      <p>{{ modalContent }}</p>
      <a-textarea v-if="withdrawStatus==2" v-model="reason" rows="4" placeholder="请输入审核意见" />
    </a-modal>
    <ums-withdraw-modal ref="modalForm" @ok="modalFormOk"></ums-withdraw-modal>
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
  import UmsWithdrawModal from './modules/UmsWithdrawModal'
  import {
    filterMultiDictText
  } from '@/components/dict/JDictSelectUtil'
  import {
    getAction,
    httpAction,
    postAction
  } from '@/api/manage';
  export default {
    name: 'UmsWithdrawList',
    mixins: [JeecgListMixin, mixinDevice],
    components: {
      UmsWithdrawModal
    },
    data() {
      return {
        description: '用户提现管理页面',

        modalTitle: '温馨提示',
        showModal: false,
        modalContent: '确认已打款？',
        selectItem: {},
        withdrawStatus: 0,

        // 表头
        columns: [
          // {
          //   title: '#',
          //   dataIndex: '',
          //   key:'rowIndex',
          //   width:60,
          //   align:"center",
          //   customRender:function (t,r,index) {
          //     return parseInt(index)+1;
          //   }
          // },
          {
            title: '头像',
            align: "center",
            dataIndex: 'userAvatar',
            scopedSlots: {
              customRender: 'imgSlot'
            }
          },
          {
            title: '姓名',
            align: "center",
            dataIndex: 'userName',
          },
          {
            title: '联系方式',
            align: "center",
            dataIndex: 'userPhone',
          },
          // {
          //   title: '账户余额',
          //   align: "center",
          //   dataIndex: 'lastBalance'
          // },
          {
            title: '提现金额',
            align: "center",
            dataIndex: 'money'
          },
          {
            title: '账户类型',
            align: "center",
            dataIndex: 'accountType_dictText'
          },
          // {
          //   title:'提现后余额',
          //   align:"center",
          //   dataIndex: 'balance'
          // },
          {
            title: '提现账号',
            align: "center",
            dataIndex: 'withdrawAccount'
          },
          {
            title: '账户姓名',
            align: "center",
            dataIndex: 'withdrawName'
          },
          {
            title: '提现状态',
            align: "center",
            dataIndex: 'withdrawStatus_dictText'
          },
          {
            title: '转账状态',
            align: "center",
            dataIndex: 'transferStatus'
          },
          {
            title: '失败原因',
            align: "center",
            dataIndex: 'reason'
          },
          {
            title: '提交时间',
            align: "center",
            dataIndex: 'createTime'
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
          list: "/ums/umsWithdraw/list",
          delete: "/ums/umsWithdraw/delete",
          deleteBatch: "/ums/umsWithdraw/deleteBatch",
          exportXlsUrl: "/ums/umsWithdraw/exportXls",
          importExcelUrl: "ums/umsWithdraw/importExcel",
          updateStatus: "ums/umsWithdraw/updateStatus",
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

      handleVerify(record, status) {
        //1-通过，2-拒绝
        this.selectItem = record;
        this.withdrawStatus = status;
        if(status==1){//同意
          this.modalContent="审核通过，已打款？"
        }else if(status==2){//拒绝
           this.modalContent="审核不通过"
        }
         this.showModal = true;
      },

      ok() {
        if (this.withdrawStatus == 2 && !this.reason) {
          this.$message.warning('请输入审核意见');
          return;
        }
        this.showModal = false;
        this.updateStatus();
      },

      updateStatus() {
        let that = this;
        that.confirmLoading = true;
        let method = 'post';
        let params = {
          id: this.selectItem.id,
          withdrawStatus: this.withdrawStatus,
          reason: this.reason,
        };
        httpAction(this.url.updateStatus, params, method)
          .then(res => {
            if (res.success) {
              that.$message.success(res.message);
              that.loadData();
            } else {
              that.$message.warning(res.message);
            }
          })
          .finally(() => {
            that.confirmLoading = false;
          });
      },


      initDictConfig() {},
      getSuperFieldList() {
        let fieldList = [];
        fieldList.push({
          type: 'string',
          value: 'memberId',
          text: '会员ID',
          dictCode: ''
        })
        fieldList.push({
          type: 'BigDecimal',
          value: 'money',
          text: '提现金额',
          dictCode: ''
        })
        fieldList.push({
          type: 'BigDecimal',
          value: 'lastBalance',
          text: '提现前余额',
          dictCode: ''
        })
        fieldList.push({
          type: 'BigDecimal',
          value: 'balance',
          text: '提现后余额',
          dictCode: ''
        })
        fieldList.push({
          type: 'int',
          value: 'withdrawStatus',
          text: '提现状态:0-待审核，1-审核通过，2-审核失败',
          dictCode: 'withdraw_status'
        })
        fieldList.push({
          type: 'string',
          value: 'reason',
          text: '提现失败原因',
          dictCode: ''
        })
        fieldList.push({
          type: 'int',
          value: 'accountType',
          text: '账户类型：0-支付宝，1-微信，2-银联',
          dictCode: 'account_type'
        })
        fieldList.push({
          type: 'string',
          value: 'withdrawAccount',
          text: '提现账号（账号信息、银行卡号等）',
          dictCode: ''
        })
        fieldList.push({
          type: 'string',
          value: 'withdrawName',
          text: '关联名称（支付宝-实名，微信-昵称，银联-持卡人）',
          dictCode: ''
        })
        fieldList.push({
          type: 'string',
          value: 'bankName',
          text: '银行名称',
          dictCode: ''
        })
        fieldList.push({
          type: 'string',
          value: 'bankBranchName',
          text: '支行信息',
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