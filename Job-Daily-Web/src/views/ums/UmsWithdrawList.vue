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
          <a-divider type="vertical" v-if="needSyncTransfer(record)"/>
          <a v-if="needSyncTransfer(record)" @click="handleSyncTransfer(record)">查单回写</a>
          <a-divider type="vertical" v-if="needRetryTransfer(record)"/>
          <a style="color: #fa8c16;" v-if="needRetryTransfer(record)" @click="handleRetryTransfer(record)">重发转账</a>
          <a-divider type="vertical" v-if="needCloseAbnormal(record)"/>
          <a style="color: #cf1322;" v-if="needCloseAbnormal(record)" @click="handleCloseAbnormal(record)">关闭解冻</a>
        </span>

      </a-table>
    </div>

    <a-modal :title="modalTitle" :visible="showModal" :confirmLoading="confirmLoading"
             @ok="ok" @cancel="onModalCancel" okText="确认" cancelText="关闭">
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
        modalContent: '确认审核通过？',
        selectItem: {},
        withdrawStatus: 0,
        reason: '',
        confirmLoading: false,

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
            title: '商户单号',
            align: "center",
            dataIndex: 'outBillNo'
          },
          {
            title: '操作',
            dataIndex: 'action',
            align: "center",
            fixed: "right",
            width: 220,
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
          syncTransfer: "/ops/workbench/syncWithdrawTransfer",
          closeAbnormal: "/ums/umsWithdraw/closeAbnormal",
          retryTransfer: "/ums/umsWithdraw/retryTransfer",
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
      /** 审核通过且转账未终态，有商户单号时可查单 */
      needSyncTransfer(record) {
        if (!record || record.withdrawStatus != 1 || !record.outBillNo) {
          return false
        }
        const s = record.transferStatus
        return !s || s === '' || ['ACCEPTED', 'PROCESSING', 'WAIT_USER_CONFIRM', 'TRANSFERING', 'CANCELING'].indexOf(s) >= 0
      },
      /** 审核通过、有单号、尚无转账态：可重发（先查单再发起） */
      needRetryTransfer(record) {
        return record && record.withdrawStatus == 1 && record.outBillNo
          && (!record.transferStatus || record.transferStatus === '')
      },
      /** 审核通过但无商户单号的脏数据，可关闭解冻 */
      needCloseAbnormal(record) {
        return record && record.withdrawStatus == 1 && !record.outBillNo
      },
      onModalCancel() {
        if (this.confirmLoading) {
          return;
        }
        this.showModal = false;
        this.reason = '';
      },

      handleVerify(record, status) {
        //1-通过，2-拒绝
        this.selectItem = record;
        this.withdrawStatus = status;
        this.reason = '';
        if (status == 1) {
          this.modalContent = '确认审核通过？通过后将发起微信转账，请确认账号无误。'
        } else if (status == 2) {
          this.modalContent = '确认审核不通过？拒绝后将解冻用户余额。'
        }
        this.showModal = true;
      },

      ok() {
        if (this.confirmLoading) {
          return;
        }
        if (this.withdrawStatus == 2 && !this.reason) {
          this.$message.warning('请输入审核意见');
          return;
        }
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
              that.showModal = false;
              that.reason = '';
              that.loadData();
            } else {
              that.$message.warning(res.message);
            }
          })
          .finally(() => {
            that.confirmLoading = false;
          });
      },

      handleSyncTransfer(record) {
        const that = this
        this.loading = true
        postAction(this.url.syncTransfer, { outBillNo: record.outBillNo })
          .then(res => {
            if (res && res.success) {
              that.$message.success('查单成功，状态：' + ((res.result && res.result.state) || '已回写'))
              that.loadData()
            } else {
              that.$message.error((res && res.message) || '查单失败')
            }
          })
          .catch(() => that.$message.error('查单请求失败'))
          .finally(() => {
            that.loading = false
          })
      },

      handleRetryTransfer(record) {
        const that = this
        this.$confirm({
          title: '重新发起转账？',
          content: '将先查微信是否已有该单，无单号记录时再发起；请勿在渠道已付款时重复操作。',
          onOk() {
            that.loading = true
            return postAction(that.url.retryTransfer, { id: record.id })
              .then(res => {
                if (res && res.success) {
                  that.$message.success(res.message || '已发起')
                  that.loadData()
                } else {
                  that.$message.error((res && res.message) || '发起失败')
                }
              })
              .finally(() => {
                that.loading = false
              })
          }
        })
      },

      handleCloseAbnormal(record) {
        const that = this
        this.$confirm({
          title: '确认关闭异常提现？',
          content: '将解冻金额并标记失败，仅适用于无商户单号的脏数据。',
          onOk() {
            return postAction(that.url.closeAbnormal, {
              id: record.id,
              reason: '提现列表关闭：无转账单号异常单'
            }).then(res => {
              if (res && res.success) {
                that.$message.success('已关闭并解冻')
                that.loadData()
              } else {
                that.$message.error((res && res.message) || '操作失败')
              }
            })
          }
        })
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