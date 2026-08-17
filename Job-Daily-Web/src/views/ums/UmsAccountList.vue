<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="会员ID">
              <a-input placeholder="请输入会员ID" v-model="queryParam.memberId"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
              <a @click="handleToggleSearch" style="margin-left: 8px">
                {{ toggleSearchStatus ? '收起' : '展开' }}
                <a-icon :type="toggleSearchStatus ? 'up' : 'down'"/>
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
      <!-- <a-button type="primary" icon="download" @click="handleExportXls('会员账户')">导出</a-button>
      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">
        <a-button type="primary" icon="import">导入</a-button>
      </a-upload> -->
      <!-- 高级查询区域 -->
      <!-- <j-super-query :fieldList="superFieldList" ref="superQueryModal" @handleSuperQuery="handleSuperQuery"></j-super-query> -->
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel"><a-icon type="delete"/>删除</a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px"> 批量操作 <a-icon type="down" /></a-button>
      </a-dropdown>
    </div>

    <!-- table区域-begin -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
      </div>

      <a-table
        ref="table"
        size="middle"
        :scroll="{x:true}"
        bordered
        rowKey="id"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        class="j-table-force-nowrap"
        @change="handleTableChange">

        <template slot="htmlSlot" slot-scope="text">
          <div v-html="text"></div>
        </template>
        <template slot="imgSlot" slot-scope="text,record">
          <span v-if="!text" style="font-size: 12px;font-style: italic;">无图片</span>
          <img v-else :src="getImgView(text)" :preview="record.id" height="25px" alt="" style="max-width:80px;font-size: 12px;font-style: italic;"/>
        </template>
        <template slot="fileSlot" slot-scope="text">
          <span v-if="!text" style="font-size: 12px;font-style: italic;">无文件</span>
          <a-button
            v-else
            :ghost="true"
            type="primary"
            icon="download"
            size="small"
            @click="downloadFile(text)">
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

    <ums-account-modal ref="modalForm" @ok="modalFormOk"></ums-account-modal>
  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import UmsAccountModal from './modules/UmsAccountModal'

  export default {
    name: 'UmsAccountList',
    mixins:[JeecgListMixin, mixinDevice],
    components: {
      UmsAccountModal
    },
    data () {
      return {
        description: '会员账户管理页面',
        // 表头
        columns: [
          {
            title:'账户ID',
            align:"center",
            dataIndex: 'accountId'
          },
          {
            title:'会员ID',
            align:"center",
            dataIndex: 'memberId'
          },
          {
            title:'冻结余额',
            align:"center",
            dataIndex: 'balanceFrozen'
          },
          {
            title:'可提余额',
            align:"center",
            dataIndex: 'balanceWithdraw'
          },
          {
            title:'可用余额',
            align:"center",
            dataIndex: 'balance'
          },
          {
            title:'充值总额',
            align:"center",
            dataIndex: 'totalRecharge'
          },
          {
            title:'提现总额',
            align:"center",
            dataIndex: 'totalWithdraw'
          },
          {
            title:'消费总额',
            align:"center",
            dataIndex: 'totalConsume'
          },
          {
            title:'会员积分',
            align:"center",
            dataIndex: 'integral'
          },
          {
            title:'累计积分',
            align:"center",
            dataIndex: 'totalIntegral'
          },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            fixed:"right",
            width:147,
            scopedSlots: { customRender: 'action' }
          }
        ],
        url: {
          list: "/ums/umsAccount/list",
          delete: "/ums/umsAccount/delete",
          deleteBatch: "/ums/umsAccount/deleteBatch",
          exportXlsUrl: "/ums/umsAccount/exportXls",
          importExcelUrl: "ums/umsAccount/importExcel",
          
        },
        dictOptions:{},
        superFieldList:[],
      }
    },
    created() {
    this.getSuperFieldList();
    },
    computed: {
      importExcelUrl: function(){
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      },
    },
    methods: {
      initDictConfig(){
      },
      getSuperFieldList(){
        let fieldList=[];
        fieldList.push({type:'string',value:'accountId',text:'账户ID',dictCode:''})
        fieldList.push({type:'string',value:'memberId',text:'会员ID',dictCode:''})
        fieldList.push({type:'BigDecimal',value:'balanceFrozen',text:'冻结余额',dictCode:''})
        fieldList.push({type:'BigDecimal',value:'balanceWithdraw',text:'可提余额',dictCode:''})
        fieldList.push({type:'BigDecimal',value:'balance',text:'可用余额',dictCode:''})
        fieldList.push({type:'BigDecimal',value:'totalRecharge',text:'充值总额',dictCode:''})
        fieldList.push({type:'BigDecimal',value:'totalWithdraw',text:'提现总额',dictCode:''})
        fieldList.push({type:'BigDecimal',value:'totalConsume',text:'消费总额',dictCode:''})
        fieldList.push({type:'int',value:'integral',text:'会员积分',dictCode:''})
        fieldList.push({type:'int',value:'totalIntegral',text:'累计积分',dictCode:''})
        fieldList.push({type:'string',value:'remark',text:'备注',dictCode:''})
        fieldList.push({type:'int',value:'tenantId',text:'tenantId',dictCode:''})
        fieldList.push({type:'int',value:'delFlag',text:'删除状态(0-正常,1-已删除)',dictCode:''})
        this.superFieldList = fieldList
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>