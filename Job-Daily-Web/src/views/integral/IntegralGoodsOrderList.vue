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
      <!-- <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>
      <a-button type="primary" icon="download" @click="handleExportXls('积分订单')">导出</a-button>
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

    <integral-goods-order-modal ref="modalForm" @ok="modalFormOk"></integral-goods-order-modal>
  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import IntegralGoodsOrderModal from './modules/IntegralGoodsOrderModal'

  export default {
    name: 'IntegralGoodsOrderList',
    mixins:[JeecgListMixin, mixinDevice],
    components: {
      IntegralGoodsOrderModal
    },
    data () {
      return {
        description: '积分订单管理页面',
        // 表头
        columns: [
          // {
          //   title:'订单编码',
          //   align:"center",
          //   dataIndex: 'orderSn'
          // },
          {
            title:'用户头像',
            align:"center",
            dataIndex: 'userAvatar',
            scopedSlots: { customRender: 'imgSlot' }
          },
          {
            title:'用户名称',
            align:"center",
            dataIndex: 'userName'
          },
          {
            title:'用户手机',
            align:"center",
            dataIndex: 'userPhone'
          },
          {
            title:'道具名称',
            align:"center",
            dataIndex: 'goodsName'
          },
          // {
          //   title:'原单价',
          //   align:"center",
          //   dataIndex: 'originalPrice'
          // },
          {
            title:'单价',
            align:"center",
            dataIndex: 'price'
          },
          {
            title:'购买数量',
            align:"center",
            dataIndex: 'number'
          },
          // {
          //   title:'订单原价',
          //   align:"center",
          //   dataIndex: 'originalAmount'
          // },
          // {
          //   title:'订单优惠价',
          //   align:"center",
          //   dataIndex: 'discountAmount'
          // },
          {
            title:'订单总额',
            align:"center",
            dataIndex: 'amount'
          },
          {
            title:'订单状态',
            align:"center",
            dataIndex: 'orderStatus'
          },
          {
            title:'支付方式',
            align:"center",
            dataIndex: 'payType'
          },
          {
            title:'创建时间',
            align:"center",
            dataIndex: 'createTime'
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
          list: "/integral/integralGoodsOrder/list",
          delete: "/integral/integralGoodsOrder/delete",
          deleteBatch: "/integral/integralGoodsOrder/deleteBatch",
          exportXlsUrl: "/integral/integralGoodsOrder/exportXls",
          importExcelUrl: "integral/integralGoodsOrder/importExcel",
          
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
        fieldList.push({type:'string',value:'orderSn',text:'订单编码',dictCode:''})
        fieldList.push({type:'string',value:'userId',text:'会员ID',dictCode:''})
        fieldList.push({type:'string',value:'goodsId',text:'商品ID',dictCode:''})
        fieldList.push({type:'BigDecimal',value:'originalPrice',text:'原单价',dictCode:''})
        fieldList.push({type:'BigDecimal',value:'price',text:'现单价',dictCode:''})
        fieldList.push({type:'int',value:'number',text:'购买数量',dictCode:''})
        fieldList.push({type:'BigDecimal',value:'originalAmount',text:'订单原价',dictCode:''})
        fieldList.push({type:'BigDecimal',value:'discountAmount',text:'订单优惠价',dictCode:''})
        fieldList.push({type:'BigDecimal',value:'amount',text:'订单总额',dictCode:''})
        fieldList.push({type:'string',value:'orderStatus',text:'订单状态',dictCode:''})
        fieldList.push({type:'string',value:'payType',text:'支付方式',dictCode:''})
        fieldList.push({type:'int',value:'sort',text:'排序',dictCode:''})
        this.superFieldList = fieldList
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>