<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="工种名称">
              <j-input placeholder="请输入工种名称" v-model="queryParam.name"></j-input>
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
      <a-button @click="handleAdd" type="primary" icon="plus" v-has="'job:job_types:add'">新增</a-button>
      <!-- <a-button type="primary" icon="download" @click="handleExportXls('工种信息')" v-has="'job:job_types:exportXls'">导出</a-button>
      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">
        <a-button type="primary" icon="import" v-has="'job:job_types:importExcel'">导入</a-button>
      </a-upload> -->
      <!-- 高级查询区域 -->
      <!-- <j-super-query :fieldList="superFieldList" ref="superQueryModal" @handleSuperQuery="handleSuperQuery"></j-super-query> -->
      <a-dropdown v-if="selectedRowKeys.length > 0" v-has="'job:job_types:deleteBatch'">
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

      <a-table ref="table" size="middle" rowKey="id" class="j-table-force-nowrap" :scroll="{x:true}" :columns="columns"
        :dataSource="dataSource" :pagination="ipagination" :loading="loading" :expandedRowKeys="expandedRowKeys"
        @change="handleTableChange" @expand="handleExpand" v-bind="tableProps">

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
          <a @click="handleEdit(record)" v-has="'job:job_types:edit'">编辑</a>
          <a-divider type="vertical" v-has="'job:job_types:edit'" />
          <a @click="handleAddChild(record)" v-has="'job:job_types:add'">添加下级</a>
          <a-divider type="vertical" v-has="'job:job_types:delete'" />
          <a-popconfirm title="确定删除吗?" @confirm="() => handleDeleteNode(record.id)" placement="topLeft" v-has="'job:job_types:delete'">
            <a>删除</a>
          </a-popconfirm>
        </span>

      </a-table>
    </div>

    <jobTypes-modal ref="modalForm" @ok="modalFormOk"></jobTypes-modal>
  </a-card>
</template>

<script>
  import {
    getAction,
    deleteAction
  } from '@/api/manage'
  import {
    JeecgListMixin
  } from '@/mixins/JeecgListMixin'
  import JobTypesModal from './modules/JobTypesModal'
  import {
    filterMultiDictText
  } from '@/components/dict/JDictSelectUtil'
  import {
    filterObj
  } from '@/utils/util';

  export default {
    name: "JobTypesList",
    mixins: [JeecgListMixin],
    components: {
      JobTypesModal
    },
    data() {
      return {
        description: '工种信息管理页面',
        // 表头
        columns: [{
            title: '工种名称',
            align: "left",
            dataIndex: 'name'
          },
          {
            title: '工种编码',
            align: "left",
            dataIndex: 'typeCode'
          },
          {
            title: '层级',
            align: "left",
            dataIndex: 'level'
          },
          // {
          //   title:'是否热门',
          //   align:"left",
          //   dataIndex: 'ifHot'
          // },
          // {
          //   title:'描述',
          //   align:"left",
          //   dataIndex: 'remark'
          // },
          {
            title: '排序',
            align: "left",
            dataIndex: 'sort'
          },
          // {
          //   title:'启用状态',
          //   align:"left",
          //   dataIndex: 'status_dictText'
          // },
          {
            title: '是否有子节点',
            align: "left",
            dataIndex: 'hasChild'
          },
          {
            title: '操作',
            dataIndex: 'action',
            align: "center",
            fixed: "right",
            width: 147,
            scopedSlots: {
              customRender: 'action'
            },
          }
        ],
        url: {
          list: "/job/jobTypes/rootList",
          childList: "/job/jobTypes/childList",
          getChildListBatch: "/job/jobTypes/getChildListBatch",
          delete: "/job/jobTypes/delete",
          deleteBatch: "/job/jobTypes/deleteBatch",
          exportXlsUrl: "/job/jobTypes/exportXls",
          importExcelUrl: "job/jobTypes/importExcel",
        },
        expandedRowKeys: [],
        hasChildrenField: "hasChild",
        pidField: "pid",
        dictOptions: {},
        loadParent: false,
        superFieldList: [],
      }
    },
    created() {
      this.getSuperFieldList();
    },
    computed: {
      importExcelUrl() {
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      },
      tableProps() {
        let _this = this
        return {
          // 列表项是否可选择
          rowSelection: {
            selectedRowKeys: _this.selectedRowKeys,
            onChange: (selectedRowKeys) => _this.selectedRowKeys = selectedRowKeys
          }
        }
      }
    },
    methods: {
      loadData(arg) {
        if (arg == 1) {
          this.ipagination.current = 1
        }
        this.loading = true
        let params = this.getQueryParams()
        params.hasQuery = 'false'
        if (params.name != null) {
          params.hasQuery = "true";
        }
        params.ifPublic = 1; //个人的不显示
        getAction(this.url.list, params).then(res => {
          if (res.success) {
            let result = res.result
            if (Number(result.total) > 0) {
              this.ipagination.total = Number(result.total)
              this.dataSource = this.getDataByResult(res.result.records)
              return this.loadDataByExpandedRows(this.dataSource)
            } else {
              this.ipagination.total = 0
              this.dataSource = []
            }
          } else {
            this.$message.warning(res.message)
          }
        }).finally(() => {
          this.loading = false
        })
      },
      // 根据已展开的行查询数据（用于保存后刷新时异步加载子级的数据）
      loadDataByExpandedRows(dataList) {
        if (this.expandedRowKeys.length > 0) {
          return getAction(this.url.getChildListBatch, {
            parentIds: this.expandedRowKeys.join(',')
          }).then(res => {
            if (res.success && res.result.records.length > 0) {
              //已展开的数据批量子节点
              let records = res.result.records
              const listMap = new Map();
              for (let item of records) {
                let pid = item[this.pidField];
                if (this.expandedRowKeys.join(',').includes(pid)) {
                  let mapList = listMap.get(pid);
                  if (mapList == null) {
                    mapList = [];
                  }
                  mapList.push(item);
                  listMap.set(pid, mapList);
                }
              }
              let childrenMap = listMap;
              let fn = (list) => {
                if (list) {
                  list.forEach(data => {
                    if (this.expandedRowKeys.includes(data.id)) {
                      data.children = this.getDataByResult(childrenMap.get(data.id))
                      fn(data.children)
                    }
                  })
                }
              }
              fn(dataList)
            }
          })
        } else {
          return Promise.resolve()
        }
      },
      getQueryParams(arg) {
        //获取查询条件
        let sqp = {}
        let param = {}
        if (this.superQueryParams) {
          sqp['superQueryParams'] = encodeURI(this.superQueryParams)
          sqp['superQueryMatchType'] = this.superQueryMatchType
        }
        if (arg) {
          param = Object.assign(sqp, this.filters);
        } else {
          param = Object.assign(sqp, this.queryParam, this.filters);
        }
        if (JSON.stringify(this.queryParam) === "{}" || arg) {
          param.hasQuery = 'false'
        } else {
          param.hasQuery = 'true'
        }
        param.field = this.getQueryField();
        param.pageNo = this.ipagination.current;
        param.pageSize = this.ipagination.pageSize;
        return filterObj(param);
      },
      searchReset() {
        //重置
        this.expandedRowKeys = []
        this.queryParam = {}
        this.loadData(1);
      },
      getDataByResult(result) {
        if (result) {
          return result.map(item => {
            //判断是否标记了带有子节点
            if (item[this.hasChildrenField] == '1') {
              let loadChild = {
                id: item.id + '_loadChild',
                name: 'loading...',
                isLoading: true
              }
              item.children = [loadChild]
            }
            return item
          })
        }
      },
      handleExpand(expanded, record) {
        // 判断是否是展开状态
        if (expanded) {
          this.expandedRowKeys.push(record.id)
          if (record.children.length > 0 && record.children[0].isLoading === true) {
            let params = this.getQueryParams(1); //查询条件
            params[this.pidField] = record.id
            params.hasQuery = 'false'
            params.superQueryParams = ""
            params.ifPublic = 1; //个人的不显示
            getAction(this.url.childList, params).then((res) => {
              if (res.success) {
                if (res.result.records) {
                  record.children = this.getDataByResult(res.result.records)
                  this.dataSource = [...this.dataSource]
                } else {
                  record.children = ''
                  record.hasChildrenField = '0'
                }
              } else {
                this.$message.warning(res.message)
              }
            })
          }
        } else {
          let keyIndex = this.expandedRowKeys.indexOf(record.id)
          if (keyIndex >= 0) {
            this.expandedRowKeys.splice(keyIndex, 1);
          }
        }
      },
      handleAddChild(record) {
        this.loadParent = true
        let obj = {}
        obj[this.pidField] = record['id']
        this.$refs.modalForm.add(obj);
      },
      handleDeleteNode(id) {
        if (!this.url.delete) {
          this.$message.error("请设置url.delete属性!")
          return
        }
        var that = this;
        deleteAction(that.url.delete, {
          id: id
        }).then((res) => {
          if (res.success) {
            that.loadData(1)
          } else {
            that.$message.warning(res.message);
          }
        });
      },
      batchDel() {
        if (this.selectedRowKeys.length <= 0) {
          this.$message.warning('请选择一条记录！');
          return false;
        } else {
          let ids = "";
          let that = this;
          that.selectedRowKeys.forEach(function(val) {
            ids += val + ",";
          });
          that.$confirm({
            title: "确认删除",
            content: "是否删除选中数据?",
            onOk: function() {
              that.handleDeleteNode(ids)
              that.onClearSelected();
            }
          });
        }
      },
      getSuperFieldList() {
        let fieldList = [];
        fieldList.push({
          type: 'string',
          value: 'name',
          text: '工种名称',
          dictCode: ''
        })
        fieldList.push({
          type: 'string',
          value: 'ifHot',
          text: '是否热门',
          dictCode: ''
        })
        fieldList.push({
          type: 'string',
          value: 'remark',
          text: '描述',
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
          dictCode: ''
        })
        fieldList.push({
          type: 'string',
          value: 'pid',
          text: '父级节点',
          dictCode: ''
        })
        fieldList.push({
          type: 'string',
          value: 'hasChild',
          text: '是否有子节点',
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