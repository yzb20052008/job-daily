<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="关键字">
              <a-input placeholder="请输入关键字" v-model="queryParam.userId"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="求职状态">
              <j-dict-select-tag placeholder="请选择求职状态" v-model="queryParam.jobStatus" dictCode="job_status"/>
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
      <a-button type="primary" icon="download" @click="handleExportXls('简历信息')">导出</a-button>
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
        <template slot="textSlot" slot-scope="text">
          <ellipsis :length="40" tooltip>
            {{text}}
          </ellipsis>
        </template>
        <!-- 求职状态:1-正在找工作，2-暂不找工作 -->
        <template slot="statusSlot" slot-scope="text">
          <div v-if="text==1">正在找工作</div>
          <div v-if="text==2">暂不找工作</div>
        </template>
        <template slot="percentage" slot-scope="text">
          <div>{{text}}%</div>
        </template>
        <template slot="sexSlot" slot-scope="text">
          <div v-if="text==1">男</div>
          <div v-else-if="text==2">女</div>
          <div v-else="text==2">未知</div>
        </template>
        <span slot="action" slot-scope="text, record">
          <a @click="handleDetail(record)">详情</a>
          <a-divider type="vertical" />
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down" /></a>
            <a-menu slot="overlay">
              <!-- <a-menu-item>
                <a @click="handleDetail(record)">详情</a>
              </a-menu-item> -->
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

    <job-resume-modal ref="modalForm" @ok="modalFormOk"></job-resume-modal>
  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import JobResumeModal from './modules/JobResumeModal'
  import Ellipsis from '@/components/Ellipsis'

  export default {
    name: 'JobResumeList',
    mixins:[JeecgListMixin, mixinDevice],
    components: {
      JobResumeModal,
      Ellipsis
    },
    data () {
      return {
        description: '简历信息管理页面',
        // 表头
        columns: [
          {
            title:'头像',
            align:"center",
            dataIndex: 'avatar',
            scopedSlots: { customRender: 'imgSlot' }
          },
          {
            title:'姓名',
            align:"center",
            dataIndex: 'name'
          },
          {
            title:'联系方式',
            align:"center",
            dataIndex: 'phone'
          },
          {
            title:'性别',
            align:"center",
            dataIndex: 'sex',
            scopedSlots: { customRender: 'sexSlot' }
          },
          {
            title:'简历完善度',
            align:"center",
            dataIndex: 'percentage',
            scopedSlots: { customRender: 'percentage' }
          },
          // {
          //   title:'最高学历',
          //   align:"center",
          //   dataIndex: 'education'
          // },
          {
            title:'出生日期',
            align:"center",
            dataIndex: 'birthday'
          },
          // {
          //   title:'身高,cm',
          //   align:"center",
          //   dataIndex: 'height'
          // },
          {
            title:'个人特长/自我介绍',
            align:"center",
            dataIndex: 'personalSkill',
            scopedSlots: { customRender: 'textSlot' }
          },
          {
            title:'技能标签',
            align:"center",
            dataIndex: 'skills'
          },
          {
            title:'工龄',
            align:"center",
            dataIndex: 'workYear'
          },
          // {
          //   title:'邮箱地址',
          //   align:"center",
          //   dataIndex: 'email'
          // },
          {
            title:'求职状态',
            align:"center",
            dataIndex: 'jobStatus',
            scopedSlots: { customRender: 'statusSlot' }
          },
          {
            title:'熟练工种',
            align:"center",
            dataIndex: 'typeNames'
          },
          {
            title:'期望工作地',
            align:"center",
            dataIndex: 'workCity'
          },
          {
            title:'常住地',
            align:"center",
            dataIndex: 'addressName',
            scopedSlots: { customRender: 'textSlot' }
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
          list: "/job/jobResume/list",
          delete: "/job/jobResume/delete",
          deleteBatch: "/job/jobResume/deleteBatch",
          exportXlsUrl: "/job/jobResume/exportXls",
          importExcelUrl: "job/jobResume/importExcel",
          
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
      
      initDictConfig(){
      },
      getSuperFieldList(){
        let fieldList=[];
        fieldList.push({type:'string',value:'userId',text:'用户ID',dictCode:''})
        fieldList.push({type:'int',value:'percentage',text:'简历完善度',dictCode:''})
        fieldList.push({type:'string',value:'name',text:'姓名',dictCode:''})
        fieldList.push({type:'string',value:'phone',text:'联系方式',dictCode:''})
        fieldList.push({type:'string',value:'avatar',text:'头像',dictCode:''})
        fieldList.push({type:'int',value:'sex',text:'性别：0-未知，1-男，2-女',dictCode:''})
        fieldList.push({type:'string',value:'education',text:'最高学历',dictCode:''})
        fieldList.push({type:'string',value:'birthday',text:'出生日期',dictCode:''})
        fieldList.push({type:'int',value:'height',text:'身高,cm',dictCode:''})
        fieldList.push({type:'string',value:'personalSkill',text:'个人特长/自我介绍',dictCode:''})
        fieldList.push({type:'string',value:'skills',text:'技能标签',dictCode:''})
        fieldList.push({type:'int',value:'workYear',text:'工龄',dictCode:''})
        fieldList.push({type:'string',value:'email',text:'邮箱地址',dictCode:''})
        fieldList.push({type:'string',value:'jobStatus',text:'求职状态',dictCode:''})
        fieldList.push({type:'string',value:'jobTypes',text:'熟练工种',dictCode:''})
        fieldList.push({type:'string',value:'expectCity',text:'期望工作地',dictCode:''})
        fieldList.push({type:'string',value:'address',text:'常住地',dictCode:''})
        fieldList.push({type:'string',value:'addressLat',text:'常住地纬度',dictCode:''})
        fieldList.push({type:'string',value:'addressLng',text:'常住地经度',dictCode:''})
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