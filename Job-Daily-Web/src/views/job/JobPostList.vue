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
            <a-form-item label="招工状态">
              <j-dict-select-tag placeholder="请选择招工状态" v-model="queryParam.postStatus" dictCode="post_status"/>
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
      <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>
      <!-- <a-button type="primary" icon="download" @click="handleExportXls('招工信息')">导出</a-button>
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
        <template slot="workTimeSlot" slot-scope="text,record">
          <div>{{formatDate(record.startTime)}}-{{formatDate(record.endTime)}}</div>
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
        <template slot="textSlot" slot-scope="text">
          <ellipsis :length="40" tooltip>
            {{text}}
          </ellipsis>
        </template>
        <template slot="citySlot" slot-scope="text, record">
          <div>{{record.pCity}}{{record.city}}</div>
        </template>
        <template slot="salarySlot" slot-scope="text, record">
          <div>{{record.salary}}{{record.salaryUnit}}</div>
        </template>
        
        <!-- 招工状态：1-待审核，2-招工中，3-发布失败，4-已停招，5-已取消，6-已招满 -->
        <template slot="statusSlot" slot-scope="text">
          <div v-if="text==1">待审核</div>
          <div v-else-if="text==2" style="color: darkgreen;">招工中</div>
          <div v-else-if="text==3" style="color: darkred;">发布失败</div>
          <div v-else-if="text==4">已停招</div>
          <div v-else-if="text==5">已取消</div>
          <div v-else-if="text==6">已招满</div>
        </template>
        <template slot="sourceSlot" slot-scope="text">
          <div v-if="text==1" style="color: darkred;">平台发布</div>
          <div v-else style="color: darkgreen;">老板发布</div>
        </template>
        <template slot="nameSlot" slot-scope="text,record">
          <div v-if="record.postSource==1">{{record.name}}</div>
           <div v-else>{{record.userName}}</div>
        </template>
        <span slot="action" slot-scope="text, record">
          <a @click="handleDetail(record)">详情</a>
          <a-divider type="vertical" />
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down" /></a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a @click="handleEdit(record)">编辑</a>
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

    <job-post-modal ref="modalForm" @ok="modalFormOk"></job-post-modal>
  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import JobPostModal from './modules/JobPostModal'
  import Ellipsis from '@/components/Ellipsis'
  import moment from 'moment'
  moment.locale('zh-cn')

  export default {
    name: 'JobPostList',
    mixins:[JeecgListMixin, mixinDevice],
    components: {
      JobPostModal,
      Ellipsis
    },
    data () {
      return {
        description: '招工信息管理页面',
        // 表头
        columns: [
          {
            title:'招工来源',
            align:"center",
            dataIndex: 'postSource',
            scopedSlots: { customRender: 'sourceSlot' }
          },
          {
            title:'老板头像',
            align:"center",
            dataIndex: 'userAvatar',
            scopedSlots: { customRender: 'imgSlot' }
          },
          {
            title:'老板姓名',
            align:"center",
            dataIndex: 'userName',
            scopedSlots: { customRender: 'nameSlot' }
          },
          {
            title:'标题',
            align:"center",
            dataIndex: 'title'
          },
          // {
          //   title:'结算方式，兼职，日结、周结、月结、完工结',
          //   align:"center",
          //   dataIndex: 'settlementType'
          // },
          {
            title:'工种',
            align:"center",
            dataIndex: 'typeNames',
            scopedSlots: { customRender: 'textSlot' }    
          },
          {
            title:'所在城市',
            align:"center",
            dataIndex: 'city',
            scopedSlots: { customRender: 'citySlot' }    
          },
          {
            title:'招聘人数',
            align:"center",
            dataIndex: 'recruitsNumber'
          },
          {
            title:'招工状态',
            align:"center",
            dataIndex: 'postStatus',
            scopedSlots: { customRender: 'statusSlot' }
          },
          {
            title:'工作时间',
            align:"center",
            dataIndex: 'startTime',
            scopedSlots: { customRender: 'workTimeSlot' }
          },
          // {
          //   title:'结束时间',
          //   align:"center",
          //   dataIndex: 'endTime'
          // },
          {
            title:'性别要求',
            align:"center",
            dataIndex: 'sexRequire'
          },
          // {
          //   title:'年龄要求',
          //   align:"center",
          //   dataIndex: 'ageRequire'
          // },
          // {
          //   title:'工作地点',
          //   align:"center",
          //   dataIndex: 'addressName',
          //   scopedSlots: { customRender: 'textSlot' }    
          // },
          // {
          //   title:'门牌号',
          //   align:"center",
          //   dataIndex: 'addressHouse'
          // },
          // {
          //   title:'工作地点',
          //   align:"center",
          //   dataIndex: 'address'
          // },
          // {
          //   title:'地点经度',
          //   align:"center",
          //   dataIndex: 'longitude'
          // },
          // {
          //   title:'地点纬度',
          //   align:"center",
          //   dataIndex: 'latitude'
          // },
          {
            title:'联系电话',
            align:"center",
            dataIndex: 'phone'
          },
          // {
          //   title:'职位描述',
          //   align:"center",
          //   dataIndex: 'descr',
          //   scopedSlots: { customRender: 'textSlot' }    
          // },
          // {
          //   title:'计价方式：计时、计件',
          //   align:"center",
          //   dataIndex: 'pricingMode'
          // },
          {
            title:'薪资',
            align:"center",
            dataIndex: 'salary',
            scopedSlots: { customRender: 'salarySlot' }    
          },
          // {
          //   title:'薪资单位',
          //   align:"center",
          //   dataIndex: 'salaryUnit'
          // },
          // {
          //   title:'招工要求',
          //   align:"center",
          //   dataIndex: 'jobRequires'
          // },
          // {
          //   title:'工作视频',
          //   align:"center",
          //   dataIndex: 'videoUrl'
          // },
          // {
          //   title:'工作图片',
          //   align:"center",
          //   dataIndex: 'imgUrl'
          // },
          // {
          //   title:'是否需要电话沟通',
          //   align:"center",
          //   dataIndex: 'ifCall'
          // },
          {
            title:'浏览数量',
            align:"center",
            dataIndex: 'browseNumber'
          },
          // {
          //   title:'是否置顶',
          //   align:"center",
          //   dataIndex: 'ifTopping'
          // },
          // {
          //   title:'是否加粗',
          //   align:"center",
          //   dataIndex: 'ifBold'
          // },
          // {
          //   title:'内容描述',
          //   align:"center",
          //   dataIndex: 'remark'
          // },
          // {
          //   title:'排序',
          //   align:"center",
          //   dataIndex: 'sort'
          // },
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
          list: "/job/jobPost/list",
          delete: "/job/jobPost/delete",
          deleteBatch: "/job/jobPost/deleteBatch",
          exportXlsUrl: "/job/jobPost/exportXls",
          importExcelUrl: "job/jobPost/importExcel",
          
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
      
      handleDetail(record){
        this.$refs.modalForm.detail(record);
        this.$refs.modalForm.title="详情";
        this.$refs.modalForm.disableSubmit = true;
      },
      
      formatDate(val){
        let time = moment(val).format('MM-DD HH:mm');
        return time;
      },
      
      initDictConfig(){
      },
      
      getSuperFieldList(){
        let fieldList=[];
        fieldList.push({type:'string',value:'userId',text:'用户ID',dictCode:''})
        fieldList.push({type:'string',value:'title',text:'标题',dictCode:''})
        fieldList.push({type:'int',value:'settlementType',text:'结算方式，兼职，日结、周结、月结、完工结',dictCode:''})
        fieldList.push({type:'string',value:'jobTypes',text:'职位名称',dictCode:''})
        fieldList.push({type:'string',value:'startTime',text:'开始时间',dictCode:''})
        fieldList.push({type:'string',value:'endTime',text:'结束时间',dictCode:''})
        fieldList.push({type:'string',value:'sexRequire',text:'性别要求',dictCode:''})
        fieldList.push({type:'string',value:'ageRequire',text:'年龄要求',dictCode:''})
        fieldList.push({type:'int',value:'recruitsNumber',text:'招聘人数',dictCode:''})
        fieldList.push({type:'string',value:'addressName',text:'地址名称',dictCode:''})
        fieldList.push({type:'string',value:'addressHouse',text:'门牌号',dictCode:''})
        fieldList.push({type:'string',value:'address',text:'工作地点',dictCode:''})
        fieldList.push({type:'string',value:'longitude',text:'地点经度',dictCode:''})
        fieldList.push({type:'string',value:'latitude',text:'地点纬度',dictCode:''})
        fieldList.push({type:'string',value:'phone',text:'联系电话',dictCode:''})
        fieldList.push({type:'Text',value:'descr',text:'职位描述',dictCode:''})
        fieldList.push({type:'string',value:'pricingMode',text:'计价方式：计时、计件',dictCode:''})
        fieldList.push({type:'string',value:'salary',text:'薪资范围',dictCode:''})
        fieldList.push({type:'string',value:'salaryUnit',text:'薪资单位，兼职，元/时，元/天，元/周，元/月',dictCode:''})
        fieldList.push({type:'string',value:'jobRequires',text:'招工要求',dictCode:''})
        fieldList.push({type:'string',value:'videoUrl',text:'工作视频',dictCode:''})
        fieldList.push({type:'string',value:'imgUrl',text:'工作图片',dictCode:''})
        fieldList.push({type:'int',value:'ifCall',text:'是否需要电话沟通',dictCode:''})
        fieldList.push({type:'int',value:'browseNumber',text:'浏览数量',dictCode:''})
        fieldList.push({type:'int',value:'ifTopping',text:'是否置顶',dictCode:''})
        fieldList.push({type:'int',value:'ifBold',text:'是否加粗',dictCode:''})
        fieldList.push({type:'string',value:'remark',text:'内容描述',dictCode:''})
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