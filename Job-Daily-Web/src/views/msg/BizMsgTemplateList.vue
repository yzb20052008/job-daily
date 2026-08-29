<template>
  <a-card :bordered="false">
    <!-- 查询区域：默认展示 2 项，展开显示全部 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="模板编码">
              <a-input placeholder="如 wx_auth / site_" v-model="queryParam.templateCode" allowClear />
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="通道">
              <a-select v-model="queryParam.channel" placeholder="全部" allowClear style="width:100%">
                <a-select-option value="site">站内信</a-select-option>
                <a-select-option value="wx">微信</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <template v-if="toggleSearchStatus">
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-form-item label="标题">
                <a-input placeholder="标题关键字" v-model="queryParam.title" allowClear />
              </a-form-item>
            </a-col>
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-form-item label="状态">
                <a-select v-model="queryParam.status" placeholder="全部" allowClear style="width:100%">
                  <a-select-option value="1">启用</a-select-option>
                  <a-select-option value="0">停用</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
          </template>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <span class="table-page-search-submitButtons" style="float:left;overflow:hidden">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left:8px">重置</a-button>
              <a @click="handleToggleSearch" style="margin-left:8px">
                {{ toggleSearchStatus ? '收起' : '展开' }}
                <a-icon :type="toggleSearchStatus ? 'up' : 'down'" />
              </a>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>

    <!-- 操作按钮 -->
    <div class="table-operator">
      <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel">
            <a-icon type="delete" />批量删除
          </a-menu-item>
          <a-menu-item key="2" @click="batchUpdateStatus('1')">
            <a-icon type="check-circle" />批量启用
          </a-menu-item>
          <a-menu-item key="3" @click="batchUpdateStatus('0')">
            <a-icon type="stop" />批量停用
          </a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px">
          批量操作 <a-icon type="down" />
        </a-button>
      </a-dropdown>
    </div>

    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i>
        已选择 <a style="font-weight: 600">{{ selectedRowKeys.length }}</a> 项
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
      </div>

      <a-table
        ref="table"
        size="middle"
        :scroll="{ x: true }"
        bordered
        rowKey="id"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :rowSelection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
        class="j-table-force-nowrap"
        @change="handleTableChange"
      >
        <span slot="channelSlot" slot-scope="text">
          <a-tag :color="text === 'wx' ? 'green' : 'blue'">{{ text === 'wx' ? '微信' : '站内信' }}</a-tag>
        </span>
        <span slot="statusSlot" slot-scope="text, record">
          <a-switch
            :checked="text === '1'"
            checked-children="启"
            un-checked-children="停"
            @change="(checked) => handleStatusChange(record, checked)"
          />
        </span>
        <span slot="action" slot-scope="text, record">
          <a @click="handleEdit(record)">编辑</a>
          <a-divider type="vertical" />
          <a @click="handleDetail(record)">详情</a>
          <a-divider type="vertical" />
          <a-popconfirm title="确定删除该模板吗？" @confirm="() => handleDelete(record.id)">
            <a style="color:#cf1322">删除</a>
          </a-popconfirm>
        </span>
      </a-table>
    </div>

    <biz-msg-template-modal ref="modalForm" @ok="modalFormOk" />
  </a-card>
</template>

<script>
import '@/assets/less/TableExpand.less'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import { postAction } from '@/api/manage'
import BizMsgTemplateModal from './modules/BizMsgTemplateModal'

export default {
  name: 'BizMsgTemplateList',
  mixins: [JeecgListMixin],
  components: { BizMsgTemplateModal },
  data() {
    return {
      description: '业务消息模板',
      // * 模糊查询字段
      columns: [
        { title: '模板编码', dataIndex: 'templateCode', width: 200, ellipsis: true },
        { title: '通道', dataIndex: 'channel', width: 90, align: 'center', scopedSlots: { customRender: 'channelSlot' } },
        { title: '微信模板ID', dataIndex: 'wxTemplateId', width: 220, ellipsis: true },
        { title: '标题', dataIndex: 'title', width: 160, ellipsis: true },
        { title: '内容', dataIndex: 'content', width: 260, ellipsis: true },
        { title: '状态', dataIndex: 'status', width: 90, align: 'center', scopedSlots: { customRender: 'statusSlot' } },
        { title: '备注', dataIndex: 'remark', width: 140, ellipsis: true },
        { title: '更新时间', dataIndex: 'updateTime', width: 170 },
        {
          title: '操作',
          dataIndex: 'action',
          align: 'center',
          fixed: 'right',
          width: 180,
          scopedSlots: { customRender: 'action' }
        }
      ],
      url: {
        list: '/msg/bizMsgTemplate/list',
        delete: '/msg/bizMsgTemplate/delete',
        deleteBatch: '/msg/bizMsgTemplate/deleteBatch',
        updateStatusBatch: '/msg/bizMsgTemplate/updateStatusBatch'
      }
    }
  },
  methods: {
    /** 编码/标题走模糊查询 */
    searchQuery() {
      const p = Object.assign({}, this.queryParam)
      if (p.templateCode) {
        p.templateCode = '*' + p.templateCode + '*'
      }
      if (p.title) {
        p.title = '*' + p.title + '*'
      }
      this.queryParam = p
      this.loadData(1)
      // 还原输入框展示值（去掉星号）
      if (this.queryParam.templateCode) {
        this.queryParam.templateCode = this.queryParam.templateCode.replace(/^\*|\*$/g, '')
      }
      if (this.queryParam.title) {
        this.queryParam.title = this.queryParam.title.replace(/^\*|\*$/g, '')
      }
    },
    handleStatusChange(record, checked) {
      const status = checked ? '1' : '0'
      const that = this
      postAction(this.url.updateStatusBatch, { ids: record.id, status })
        .then((res) => {
          if (res.success) {
            that.$message.success(res.message || '状态已更新')
            that.loadData()
          } else {
            that.$message.warning(res.message || '更新失败')
          }
        })
        .catch(() => that.$message.error('状态更新请求失败'))
    },
    batchUpdateStatus(status) {
      if (!this.selectedRowKeys.length) {
        this.$message.warning('请先勾选记录')
        return
      }
      const that = this
      const tip = status === '1' ? '启用' : '停用'
      this.$confirm({
        title: `确认批量${tip}？`,
        content: `将对选中的 ${this.selectedRowKeys.length} 条模板执行${tip}`,
        onOk() {
          return postAction(that.url.updateStatusBatch, {
            ids: that.selectedRowKeys.join(','),
            status
          }).then((res) => {
            if (res.success) {
              that.$message.success(res.message || `批量${tip}成功`)
              that.loadData()
              that.onClearSelected()
            } else {
              that.$message.warning(res.message || '操作失败')
            }
          })
        }
      })
    }
  }
}
</script>
<style scoped>
@import '~@assets/less/common.less';
</style>
