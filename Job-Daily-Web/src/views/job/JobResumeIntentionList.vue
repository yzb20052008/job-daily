<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
        </a-row>
      </a-form>
    </div>
    <!-- 查询区域-END -->

    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>
      <a-button type="primary" icon="download" @click="handleExportXls('求职意向')">导出</a-button>
      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">
        <a-button type="primary" icon="import">导入</a-button>
      </a-upload>
      <!-- 高级查询区域 -->
      <j-super-query :fieldList="superFieldList" ref="superQueryModal" @handleSuperQuery="handleSuperQuery"></j-super-query>
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

    <job-resume-intention-modal ref="modalForm" @ok="modalFormOk"></job-resume-intention-modal>
  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import JobResumeIntentionModal from './modules/JobResumeIntentionModal'

  export default {
    name: 'JobResumeIntentionList',
    mixins:[JeecgListMixin, mixinDevice],
    components: {
      JobResumeIntentionModal
    },
    data () {
      return {
        description: '求职意向管理页面',
        // 表头
        columns: [
          {
            title: '#',
            dataIndex: '',
            key:'rowIndex',
            width:60,
            align:"center",
            customRender:function (t,r,index) {
              return parseInt(index)+1;
            }
          },
          {
            title:'用户ID',
            align:"center",
            dataIndex: 'userId'
          },
          {
            title:'名称',
            align:"center",
            dataIndex: 'resumeId'
          },
          {
            title:'工种',
            align:"center",
            dataIndex: 'jobTypes'
          },
          {
            title:'用工方式：如点工、包工等',
            align:"center",
            dataIndex: 'employMethod'
          },
          {
            title:'结算方式：日结、月结、完工结d等',
            align:"center",
            dataIndex: 'settlementType'
          },
          {
            title:'工作城市',
            align:"center",
            dataIndex: 'workCity'
          },
          {
            title:'工作类型：全职、兼职等',
            align:"center",
            dataIndex: 'workType'
          },
          {
            title:'期望薪资',
            align:"center",
            dataIndex: 'expectSalary'
          },
          {
            title:'薪资单位：元/日、元/月、元/件等',
            align:"center",
            dataIndex: 'salaryUnit'
          },
          {
            title:'排序',
            align:"center",
            dataIndex: 'sort'
          },
          {
            title:'启用状态',
            align:"center",
            dataIndex: 'status'
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
          list: "/job/jobResumeIntention/list",
          delete: "/job/jobResumeIntention/delete",
          deleteBatch: "/job/jobResumeIntention/deleteBatch",
          exportXlsUrl: "/job/jobResumeIntention/exportXls",
          importExcelUrl: "job/jobResumeIntention/importExcel",
          
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
        fieldList.push({type:'string',value:'userId',text:'用户ID',dictCode:''})
        fieldList.push({type:'string',value:'resumeId',text:'名称',dictCode:''})
        fieldList.push({type:'string',value:'jobTypes',text:'工种',dictCode:''})
        fieldList.push({type:'string',value:'employMethod',text:'用工方式：如点工、包工等',dictCode:''})
        fieldList.push({type:'string',value:'settlementType',text:'结算方式：日结、月结、完工结d等',dictCode:''})
        fieldList.push({type:'string',value:'workCity',text:'工作城市',dictCode:''})
        fieldList.push({type:'string',value:'workType',text:'工作类型：全职、兼职等',dictCode:''})
        fieldList.push({type:'string',value:'expectSalary',text:'期望薪资',dictCode:''})
        fieldList.push({type:'string',value:'salaryUnit',text:'薪资单位：元/日、元/月、元/件等',dictCode:''})
        fieldList.push({type:'int',value:'sort',text:'排序',dictCode:''})
        fieldList.push({type:'int',value:'status',text:'启用状态',dictCode:''})
        this.superFieldList = fieldList
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>