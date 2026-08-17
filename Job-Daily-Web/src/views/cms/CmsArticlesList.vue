<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="文章标题">
              <j-input placeholder="请输入文章标题" v-model="queryParam.title"></j-input>
            </a-form-item>
          </a-col>
        <!--  <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="所属分类">
              <j-input placeholder="请输入所属分类" v-model="queryParam.categoryId"></j-input>
            </a-form-item>
          </a-col> -->
          <template v-if="toggleSearchStatus">
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-form-item label="启用状态">
                <j-dict-select-tag placeholder="请选择启用状态" v-model="queryParam.status" dictCode="status"/>
              </a-form-item>
            </a-col>
          </template>
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
      <!-- <a-button type="primary" icon="download" @click="handleExportXls('文章内容')">导出</a-button> -->
     <!-- <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">
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
        <!-- 主要代码在这里 -- 下面这些 -->
        <span slot="bzbz" slot-scope="text, record">
          <a-tooltip placement="top">
            <template slot="title">
              <span>{{ text }}</span>
            </template>
            <span class="ellipsis">{{ text }}</span>
          </a-tooltip>
        </span>
      </a-table>
    </div>

    <cms-articles-modal ref="modalForm" @ok="modalFormOk"></cms-articles-modal>
  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import CmsArticlesModal from './modules/CmsArticlesModal'
  import {filterMultiDictText} from '@/components/dict/JDictSelectUtil'

  export default {
    name: 'CmsArticlesList',
    mixins:[JeecgListMixin, mixinDevice],
    components: {
      CmsArticlesModal
    },
    data () {
      return {
        description: '文章内容管理页面',
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
            title:'文章标题',
            align:"center",
            dataIndex: 'title',
          },
          {
            title:'所属分类',
            align:"center",
            dataIndex: 'categoryId_dictText',
          },
          {
            title:'文章内容',
            align:"center",
            dataIndex: 'content',
            width:500,
            scopedSlots: { customRender: 'bzbz' },
          },
          {
            title:'排序',
            align:"center",
            dataIndex: 'sort'
          },
          {
            title:'启用状态',
            align:"center",
            dataIndex: 'status_dictText'
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
          list: "/cms/cmsArticles/list",
          delete: "/cms/cmsArticles/delete",
          deleteBatch: "/cms/cmsArticles/deleteBatch",
          exportXlsUrl: "/cms/cmsArticles/exportXls",
          importExcelUrl: "cms/cmsArticles/importExcel",
          
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
        fieldList.push({type:'string',value:'title',text:'文章标题',dictCode:''})
        fieldList.push({type:'string',value:'categoryId',text:'所属分类'})
        fieldList.push({type:'string',value:'banner',text:'轮播大图',dictCode:''})
        fieldList.push({type:'string',value:'avatar',text:'封面图标',dictCode:''})
        fieldList.push({type:'string',value:'version',text:'版本信息',dictCode:''})
        fieldList.push({type:'int',value:'viewCount',text:'阅读数量',dictCode:''})
        fieldList.push({type:'string',value:'excerpt',text:'文章摘要',dictCode:''})
        fieldList.push({type:'Text',value:'content',text:'文章内容',dictCode:''})
        fieldList.push({type:'int',value:'sort',text:'排序',dictCode:''})
        fieldList.push({type:'int',value:'status',text:'启用状态',dictCode:'status'})
        this.superFieldList = fieldList
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
  .ellipsis{
      display:block;
      width:500px;
      text-overflow: ellipsis;
      white-space: nowrap;
      overflow: hidden;
    }
</style>