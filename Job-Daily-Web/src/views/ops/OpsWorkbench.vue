<template>
  <div class="ops-workbench">
    <a-spin :spinning="loading">
      <!-- 汇总指标 -->
      <a-row :gutter="16">
        <a-col :span="6" v-for="item in todoCards" :key="item.key">
          <a-card :bordered="false" class="todo-card" @click="scrollTo(item.anchor)">
            <a-statistic :title="item.title" :value="item.value" :value-style="{ color: item.color }">
              <template #suffix>
                <span class="todo-unit">待处理</span>
              </template>
            </a-statistic>
          </a-card>
        </a-col>
      </a-row>

      <a-row :gutter="16" style="margin-top: 16px">
        <a-col :span="8">
          <a-card title="近7日完单率" :bordered="false">
            <a-statistic :value="summary.finishRate7d || 0" suffix="%" :precision="2" />
            <div class="sub-tip">完成 {{ summary.orderFinish7d || 0 }} / 订单 {{ summary.orderTotal7d || 0 }}</div>
          </a-card>
        </a-col>
        <a-col :span="8">
          <a-card title="近7日结算成功率" :bordered="false">
            <a-statistic :value="summary.paySuccessRate7d || 0" suffix="%" :precision="2" />
            <div class="sub-tip">已结算 {{ summary.orderPaid7d || 0 }} / 订单 {{ summary.orderTotal7d || 0 }}</div>
          </a-card>
        </a-col>
        <a-col :span="8">
          <a-card title="近7日提现成功率" :bordered="false">
            <a-statistic :value="summary.withdrawSuccessRate7d || 0" suffix="%" :precision="2" />
            <div class="sub-tip">成功 {{ summary.withdrawSuccess7d || 0 }} / 申请 {{ summary.withdrawTotal7d || 0 }}</div>
          </a-card>
        </a-col>
      </a-row>

      <div class="toolbar">
        <a-button type="primary" icon="reload" :loading="loading" @click="reload">刷新</a-button>
        <a-button style="margin-left: 8px" :loading="orderCleanLoading" @click="runOrderAutoFinish">清理逾期待开工</a-button>
        <a-button style="margin-left: 8px" @click="go('/job/jobPostList')">招工审核</a-button>
        <a-button style="margin-left: 8px" @click="go('/ums/umsRealnameAuthList')">实名认证</a-button>
        <a-button style="margin-left: 8px" @click="go('/ums/umsWithdrawList')">提现管理</a-button>
        <a-button style="margin-left: 8px" @click="go('/job/jobOrderList')">订单管理</a-button>
      </div>

      <!-- 待审岗位 -->
      <a-card id="sec-post" title="待审岗位" style="margin-top: 16px" :bordered="false">
        <a-table
          size="middle"
          rowKey="id"
          :pagination="false"
          :dataSource="queues.pendingPosts || []"
          :columns="postColumns"
          :locale="{ emptyText: '暂无待审岗位' }"
        >
          <span slot="action" slot-scope="text, record">
            <a @click="go('/job/jobPostList', { postStatus: '1', keyword: record.title })">去审核</a>
          </span>
        </a-table>
      </a-card>

      <!-- 待审认证 -->
      <a-card id="sec-auth" title="待审认证" style="margin-top: 16px" :bordered="false">
        <a-tabs>
          <a-tab-pane key="realname" :tab="'实名(' + ((queues.pendingRealnames || []).length) + ')'">
            <a-table
              size="middle"
              rowKey="id"
              :pagination="false"
              :dataSource="queues.pendingRealnames || []"
              :columns="realnameColumns"
              :locale="{ emptyText: '暂无待审实名' }"
            >
              <span slot="action" slot-scope="text, record">
                <a @click="go('/ums/umsRealnameAuthList')">去审核</a>
              </span>
            </a-table>
          </a-tab-pane>
          <a-tab-pane key="company" :tab="'企业(' + ((queues.pendingCompanies || []).length) + ')'">
            <a-table
              size="middle"
              rowKey="id"
              :pagination="false"
              :dataSource="queues.pendingCompanies || []"
              :columns="companyColumns"
              :locale="{ emptyText: '暂无待审企业' }"
            >
              <span slot="action" slot-scope="text, record">
                <a @click="go('/job/jobCompanyList')">去审核</a>
              </span>
            </a-table>
          </a-tab-pane>
        </a-tabs>
      </a-card>

      <!-- 异常订单 -->
      <a-card id="sec-order" title="异常订单（超时待确认 / 逾期待开工 / 待结算）" style="margin-top: 16px" :bordered="false">
        <a-table
          size="middle"
          rowKey="id"
          :pagination="false"
          :dataSource="queues.abnormalOrders || []"
          :columns="orderColumns"
          :locale="{ emptyText: '暂无异常订单' }"
        >
          <span slot="status" slot-scope="text">
            {{ orderStatusText(text) }}
          </span>
          <span slot="action" slot-scope="text, record">
            <a @click="go('/job/jobOrderList', { keyword: record.orderSn || record.id })">查看</a>
          </span>
        </a-table>
      </a-card>

      <!-- 提现队列 -->
      <a-card id="sec-withdraw" title="提现队列（待审 / 转账未终态）" style="margin-top: 16px" :bordered="false">
        <a-table
          size="middle"
          rowKey="id"
          :pagination="false"
          :loading="withdrawLoading"
          :dataSource="queues.withdrawQueue || []"
          :columns="withdrawColumns"
          :locale="{ emptyText: '暂无提现待办' }"
        >
          <span slot="action" slot-scope="text, record">
            <a v-if="record.withdrawStatus == 0" @click="go('/ums/umsWithdrawList')">去审核</a>
            <a
              v-if="record.withdrawStatus == 1 && record.outBillNo"
              @click="syncTransfer(record)"
              style="margin-left: 8px"
            >查单回写</a>
            <a
              v-if="record.withdrawStatus == 1 && !record.outBillNo"
              style="color: #cf1322; margin-left: 8px"
              @click="closeAbnormal(record)"
            >关闭解冻</a>
          </span>
        </a-table>
      </a-card>
    </a-spin>
  </div>
</template>

<script>
import { getAction, postAction } from '@/api/manage'

export default {
  name: 'OpsWorkbench',
  data() {
    return {
      loading: false,
      withdrawLoading: false,
      orderCleanLoading: false,
      summary: {},
      queues: {},
      postColumns: [
        { title: '标题', dataIndex: 'title', ellipsis: true },
        { title: '城市', dataIndex: 'city' },
        { title: '薪资', dataIndex: 'salary' },
        { title: '创建时间', dataIndex: 'createTime', width: 170 },
        { title: '操作', key: 'action', width: 100, scopedSlots: { customRender: 'action' } }
      ],
      realnameColumns: [
        { title: '姓名', dataIndex: 'realname' },
        { title: '身份证', dataIndex: 'idNo' },
        { title: '提交时间', dataIndex: 'createTime', width: 170 },
        { title: '操作', key: 'action', width: 100, scopedSlots: { customRender: 'action' } }
      ],
      companyColumns: [
        { title: '企业名称', dataIndex: 'realName', ellipsis: true },
        { title: '执照号', dataIndex: 'identity' },
        { title: '提交时间', dataIndex: 'createTime', width: 170 },
        { title: '操作', key: 'action', width: 100, scopedSlots: { customRender: 'action' } }
      ],
      orderColumns: [
        { title: '订单号', dataIndex: 'orderSn' },
        { title: '状态', dataIndex: 'orderStatus', scopedSlots: { customRender: 'status' } },
        { title: '金额', dataIndex: 'amount' },
        { title: '确认截止', dataIndex: 'ensureTime', width: 170 },
        { title: '操作', key: 'action', width: 100, scopedSlots: { customRender: 'action' } }
      ],
      withdrawColumns: [
        { title: '金额', dataIndex: 'money', width: 90 },
        { title: '审核状态', dataIndex: 'withdrawStatus', width: 90, customRender: (t) => (t == 0 ? '待审' : t == 1 ? '已通过' : '已拒绝') },
        { title: '转账状态', dataIndex: 'transferStatus' },
        { title: '商户单号', dataIndex: 'outBillNo', ellipsis: true },
        { title: '提交时间', dataIndex: 'createTime', width: 170 },
        { title: '操作', key: 'action', width: 180, scopedSlots: { customRender: 'action' } }
      ]
    }
  },
  computed: {
    todoCards() {
      const s = this.summary || {}
      return [
        { key: 'post', title: '待审岗位', value: s.pendingPostCount || 0, color: '#1890ff', anchor: 'sec-post' },
        { key: 'auth', title: '待审认证', value: (s.pendingRealnameCount || 0) + (s.pendingCompanyCount || 0), color: '#722ed1', anchor: 'sec-auth' },
        { key: 'order', title: '异常订单', value: (s.timeoutOrderCount || 0) + (s.waitPayOrderCount || 0), color: '#fa8c16', anchor: 'sec-order' },
        { key: 'withdraw', title: '提现待办', value: (s.pendingWithdrawCount || 0) + (s.abnormalWithdrawCount || 0), color: '#cf1322', anchor: 'sec-withdraw' }
      ]
    }
  },
  created() {
    this.reload()
  },
  methods: {
    reload() {
      this.loading = true
      Promise.all([
        getAction('/ops/workbench/summary'),
        getAction('/ops/workbench/queues', { limit: 20 })
      ])
        .then(([summaryRes, queueRes]) => {
          if (summaryRes && summaryRes.success) {
            this.summary = summaryRes.result || {}
          } else {
            this.$message.error((summaryRes && summaryRes.message) || '汇总加载失败')
          }
          if (queueRes && queueRes.success) {
            this.queues = queueRes.result || {}
          } else {
            this.$message.error((queueRes && queueRes.message) || '队列加载失败')
          }
        })
        .catch(() => {
          this.$message.error('工作台加载失败，请稍后重试')
        })
        .finally(() => {
          this.loading = false
        })
    },
    orderStatusText(status) {
      const map = {
        '0': '待确认',
        '1': '待开工',
        '2': '工作中',
        '3': '待结算',
        '4': '待评价',
        '5': '已完成',
        '6': '已取消'
      }
      return map[status] || status
    },
    go(path, query) {
      this.$router.push({ path, query: query || {} }).catch(() => {})
    },
    scrollTo(id) {
      const el = document.getElementById(id)
      if (el) {
        el.scrollIntoView({ behavior: 'smooth', block: 'start' })
      }
    },
    runOrderAutoFinish() {
      const that = this
      this.$confirm({
        title: '立即清理异常订单？',
        content: '将取消：待确认超时、待开工已过结束时间；并完结评价超时订单（每类最多 500 条）。',
        onOk() {
          that.orderCleanLoading = true
          return postAction('/ops/workbench/runOrderAutoFinish', {})
            .then((res) => {
              if (res && res.success) {
                that.$message.success((res.result && res.result.message) || '已执行清理')
                that.reload()
              } else {
                that.$message.error((res && res.message) || '执行失败')
              }
            })
            .catch(() => that.$message.error('请求失败'))
            .finally(() => {
              that.orderCleanLoading = false
            })
        }
      })
    },
    syncTransfer(record) {
      if (!record.outBillNo) {
        this.$message.warning('缺少商户单号')
        return
      }
      this.withdrawLoading = true
      postAction('/ops/workbench/syncWithdrawTransfer', { outBillNo: record.outBillNo })
        .then((res) => {
          if (res && res.success) {
            this.$message.success('查单成功，状态：' + ((res.result && res.result.state) || '已回写'))
            this.reload()
          } else {
            this.$message.error((res && res.message) || '查单失败')
          }
        })
        .catch(() => this.$message.error('查单请求失败'))
        .finally(() => {
          this.withdrawLoading = false
        })
    },
    closeAbnormal(record) {
      const that = this
      this.$confirm({
        title: '确认关闭异常提现？',
        content: '将解冻金额并标记失败，仅适用于无商户单号的脏数据。',
        onOk() {
          return postAction('/ums/umsWithdraw/closeAbnormal', {
            id: record.id,
            reason: '运营工作台关闭：无转账单号异常单'
          }).then((res) => {
            if (res && res.success) {
              that.$message.success('已关闭并解冻')
              that.reload()
            } else {
              that.$message.error((res && res.message) || '操作失败')
            }
          })
        }
      })
    }
  }
}
</script>

<style scoped>
.ops-workbench {
  padding: 12px;
  background: #f0f2f5;
  min-height: 100%;
}
.todo-card {
  cursor: pointer;
  margin-bottom: 8px;
}
.todo-unit {
  font-size: 12px;
  color: #999;
  margin-left: 4px;
}
.sub-tip {
  margin-top: 8px;
  color: #8c8c8c;
  font-size: 12px;
}
.toolbar {
  margin-top: 16px;
}
</style>
