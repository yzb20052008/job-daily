<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="通知标题">
              <j-input placeholder="请输入通知标题" v-model="queryParam.title"></j-input>
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
      <!-- <a-button type="primary" icon="download" @click="handleExportXls('系统通知')">导出</a-button> -->
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
        
        <template slot="typeSlot" slot-scope="text">
          <div v-if="text==0">系统通知</div>
          <div v-else-if="text==1">订单动态</div>
          <div v-else-if="text==2">平台私信</div>
          <div v-else-if="text==3">动账通知</div>
          <div v-else-if="text==4">违规记录</div>
        </template>
        <template slot="userSlot" slot-scope="text,record">
          <div>
            <img :src="getImgView(record.userAvatar)" :preview="record.id" height="40px" alt=""
              style="width:40px;font-size: 12px;font-style: italic; border-radius: 50%;" />
            {{record.userName}}
          </div>
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

    <cms-notice-modal ref="modalForm" @ok="modalFormOk"></cms-notice-modal>
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
  import CmsNoticeModal from './modules/CmsNoticeModal'
  import {
    filterMultiDictText
  } from '@/components/dict/JDictSelectUtil'

  export default {
    name: 'CmsNoticeList',
    mixins: [JeecgListMixin, mixinDevice],
    components: {
      CmsNoticeModal
    },
    data() {
      return {
        description: '系统通知管理页面',
        // 表头
        columns: [{
            title: '通知标题',
            align: "center",
            dataIndex: 'title'
          },
          {
            title: '通知类型',
            align: "center",
            dataIndex: 'type',
            scopedSlots: {
              customRender: 'typeSlot'
            }
          },
          // {
          //   title:'轮播大图',
          //   align:"center",
          //   dataIndex: 'banner',
          //   scopedSlots: {customRender: 'imgSlot'}
          // },
          {
            title: '封面图标',
            align: "center",
            dataIndex: 'avatar',
            scopedSlots: {
              customRender: 'imgSlot'
            }
          },
          {
            title:'用户信息',
            align:"center",
            dataIndex: 'userName',
            scopedSlots: {customRender: 'userSlot'}
          },
          // {
          //   title:'内容',
          //   align:"center",
          //   dataIndex: 'content',
          //   scopedSlots: {customRender: 'htmlSlot'}
          // },
          // {
          //   title:'排序',
          //   align:"center",
          //   dataIndex: 'sort'
          // },
          {
            title: '启用状态',
            align: "center",
            dataIndex: 'status_dictText'
          },
          {
            title: '创建时间',
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
          list: "/cms/cmsNotice/list",
          delete: "/cms/cmsNotice/delete",
          deleteBatch: "/cms/cmsNotice/deleteBatch",
          exportXlsUrl: "/cms/cmsNotice/exportXls",
          importExcelUrl: "cms/cmsNotice/importExcel",

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
          value: 'title',
          text: '通知标题',
          dictCode: ''
        })
        fieldList.push({
          type: 'int',
          value: 'type',
          text: '通知类型：0-系统通知，1-应用通知',
          dictCode: ''
        })
        fieldList.push({
          type: 'string',
          value: 'banner',
          text: '轮播大图',
          dictCode: ''
        })
        fieldList.push({
          type: 'string',
          value: 'avatar',
          text: '封面图标',
          dictCode: ''
        })
        fieldList.push({
          type: 'string',
          value: 'version',
          text: '版本信息',
          dictCode: ''
        })
        fieldList.push({
          type: 'int',
          value: 'viewCount',
          text: '阅读数量',
          dictCode: ''
        })
        fieldList.push({
          type: 'string',
          value: 'excerpt',
          text: '摘要',
          dictCode: ''
        })
        fieldList.push({
          type: 'Text',
          value: 'content',
          text: '内容',
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