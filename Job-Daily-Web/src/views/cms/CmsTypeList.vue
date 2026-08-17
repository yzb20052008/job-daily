<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="分类名称">
              <j-input placeholder="请输入分类名称" v-model="queryParam.name"></j-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="面向角色">
               <j-dict-select-tag type="list" v-model="queryParam.roleCode" dictCode="role_code" placeholder="请选择面向角色" />
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
      <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>
      <!-- <a-button type="primary" icon="download" @click="handleExportXls('首页分类')">导出</a-button> -->
      <!-- <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">
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

    <cms-type-modal ref="modalForm" @ok="modalFormOk"></cms-type-modal>
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
  import CmsTypeModal from './modules/CmsTypeModal'
  import {
    filterMultiDictText
  } from '@/components/dict/JDictSelectUtil'

  export default {
    name: 'CmsTypeList',
    mixins: [JeecgListMixin, mixinDevice],
    components: {
      CmsTypeModal
    },
    data() {
      return {
        description: '首页分类管理页面',
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
            title: '类别图标',
            align: "center",
            dataIndex: 'icon',
            scopedSlots: {
              customRender: 'imgSlot'
            }
          },
          {
            title: '分类名称',
            align: "center",
            dataIndex: 'name'
          },
          {
            title: '面向角色',
            align: "center",
            dataIndex: 'roleCode_dictText'
          },

          // {
          //   title: '跳转地址',
          //   align: "center",
          //   dataIndex: 'openUrl'
          // },
          {
            title: '排序',
            align: "center",
            dataIndex: 'sort'
          },
          {
            title: '启用状态',
            align: "center",
            dataIndex: 'status_dictText'
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
          list: "/cms/cmsType/list",
          delete: "/cms/cmsType/delete",
          deleteBatch: "/cms/cmsType/deleteBatch",
          exportXlsUrl: "/cms/cmsType/exportXls",
          importExcelUrl: "cms/cmsType/importExcel",

        },
        dictOptions: {},
        superFieldList: [],

        disableMixinCreated: true,
      }
    },
    created() {
      this.isorter.column = "";
      this.isorter.order = "";
      this.loadData();
      //初始化字典配置 在自己页面定义
      this.initDictConfig();
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
          value: 'name',
          text: '分类名称',
          dictCode: ''
        })
        fieldList.push({
          type: 'string',
          value: 'icon',
          text: '类别图标',
          dictCode: ''
        })
        fieldList.push({
          type: 'string',
          value: 'openUrl',
          text: '类别跳转地址',
          dictCode: ''
        })
        fieldList.push({
          type: 'int',
          value: 'sort',
          text: '排序',
          dictCode: ''
        })
        fieldList.push({
          type: 'int',
          value: 'status',
          text: '启用状态',
          dictCode: 'status'
        })
        this.superFieldList = fieldList
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>