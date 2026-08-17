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
            <a-form-item label="充值状态">
              <a-select placeholder="请选择" v-model="queryParam.rechargeStatus">
                <a-select-option value="0">待支付</a-select-option>
                <a-select-option value="1">充值成功</a-select-option>
                <a-select-option value="2">充值失败</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <template v-if="toggleSearchStatus">
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-form-item label="充值方式">
                <a-select placeholder="请选择充值方式" v-model="queryParam.payType">
                  <a-select-option value="yePay">余额支付</a-select-option>
                  <a-select-option value="wxPay">微信支付</a-select-option>
                </a-select>
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
      <!-- <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>
      <a-button type="primary" icon="download" @click="handleExportXls('积分充值记录')">导出</a-button>
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
        <template slot="payType" slot-scope="text">
          <div v-if="text=='yePay'">余额支付</div>
          <div v-if="text=='aliPay'">支付宝支付</div>
          <div v-if="text=='wxPay'">微信支付</div>
        </template>
        <template slot="rechargeStatus" slot-scope="text">
          <div v-if="text==0">待支付</div>
          <div v-if="text==1" style="color: darkgreen;">充值成功</div>
          <div v-if="text==2">支付失败</div>
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
          <a @click="handleEdit(record)">编辑</a>

          <a-divider type="vertical" />
          <a-dropdown>
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
          </a-dropdown>
        </span>

      </a-table>
    </div>

    <integral-recharge-modal ref="modalForm" @ok="modalFormOk"></integral-recharge-modal>
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
  import IntegralRechargeModal from './modules/IntegralRechargeModal'

  export default {
    name: 'IntegralRechargeList',
    mixins: [JeecgListMixin, mixinDevice],
    components: {
      IntegralRechargeModal
    },
    data() {
      return {
        description: '积分充值记录管理页面',
        // 表头
        columns: [
          // {
          //   title:'订单编码',
          //   align:"center",
          //   dataIndex: 'orderSn'
          // },
          {
            title: '用户头像',
            align: "center",
            dataIndex: 'userAvatar',
            scopedSlots: {
              customRender: 'imgSlot'
            }
          },
          {
            title: '用户名称',
            align: "center",
            dataIndex: 'userName'
          },
          {
            title: '用户手机',
            align: "center",
            dataIndex: 'userPhone'
          },
          {
            title: '支付金额',
            align: "center",
            dataIndex: 'money'
          },
          {
            title: '充值积分',
            align: "center",
            dataIndex: 'integral'
          },
          {
            title: '充值方式',
            align: "center",
            dataIndex: 'payType',
            scopedSlots: {
              customRender: 'payType'
            }
          },
          {
            title: '充值状态',
            align: "center",
            dataIndex: 'rechargeStatus',
            scopedSlots: {
              customRender: 'rechargeStatus'
            }
          },
          {
            title: '创建时间',
            align: "center",
            dataIndex: 'createTime',
          },
          // {
          //   title: '操作',
          //   dataIndex: 'action',
          //   align: "center",
          //   fixed: "right",
          //   width: 147,
          //   scopedSlots: {
          //     customRender: 'action'
          //   }
          // }
        ],
        url: {
          list: "/integral/integralRecharge/list",
          delete: "/integral/integralRecharge/delete",
          deleteBatch: "/integral/integralRecharge/deleteBatch",
          exportXlsUrl: "/integral/integralRecharge/exportXls",
          importExcelUrl: "integral/integralRecharge/importExcel",

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
      initDictConfig() {},
      getSuperFieldList() {
        let fieldList = [];
        fieldList.push({
          type: 'string',
          value: 'orderSn',
          text: '订单编码',
          dictCode: ''
        })
        fieldList.push({
          type: 'string',
          value: 'userId',
          text: '用户ID',
          dictCode: ''
        })
        fieldList.push({
          type: 'BigDecimal',
          value: 'money',
          text: '充值金额',
          dictCode: ''
        })
        fieldList.push({
          type: 'BigDecimal',
          value: 'integral',
          text: '充值积分',
          dictCode: ''
        })
        fieldList.push({
          type: 'string',
          value: 'payType',
          text: '充值方式',
          dictCode: ''
        })
        fieldList.push({
          type: 'string',
          value: 'rechargeStatus',
          text: '充值状态',
          dictCode: ''
        })
        fieldList.push({
          type: 'int',
          value: 'tenantId',
          text: 'tenantId',
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