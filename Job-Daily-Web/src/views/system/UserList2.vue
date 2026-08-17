<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">

          <a-col :md="6" :sm="12">
            <a-form-item label="关键字">
              <a-input placeholder="输入关键字查询" v-model="queryParam.keyword"></a-input>
            </a-form-item>
          </a-col>
          <!-- <a-col :md="6" :sm="8">
           <a-form-item label="角色">
             <a-select v-model="queryParam.roleCode" placeholder="请选择">
               <a-select-option value="">请选择</a-select-option>
               <a-select-option value="member">工人</a-select-option>
               <a-select-option value="company">老板</a-select-option>
             </a-select>
           </a-form-item>
         </a-col> -->
          <template v-if="toggleSearchStatus">
            <a-col :md="6" :sm="8">
              <a-form-item label="用户状态">
                <a-select v-model="queryParam.status" placeholder="请选择">
                  <a-select-option value="">请选择</a-select-option>
                  <a-select-option value="1">正常</a-select-option>
                  <a-select-option value="2">冻结</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="8">
              <a-form-item label="性别">
                <a-select v-model="queryParam.sex" placeholder="请选择性别">
                  <a-select-option value="">请选择</a-select-option>
                  <a-select-option value="0">未知</a-select-option>
                  <a-select-option value="1">男</a-select-option>
                  <a-select-option value="2">女</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
          </template>

          <a-col :md="6" :sm="8">
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

    <!-- 操作按钮区域 -->
    <div class="table-operator" style="border-top: 5px">
      <!-- <a-button @click="handleAdd" type="primary" icon="plus" >添加用户</a-button> -->
      <!-- <a-button type="primary" icon="download" @click="handleExportXls('用户信息')">导出</a-button> -->
      <!-- <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">
        <a-button type="primary" icon="import">导入</a-button>
      </a-upload> -->
      <!-- <j-third-app-button biz-type="user" :selected-row-keys="selectedRowKeys" syncToApp syncToLocal @sync-finally="onSyncFinally"/> -->
      <a-button type="primary" icon="hdd" @click="recycleBinVisible=true">回收站</a-button>
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay" @click="handleMenuClick">
          <a-menu-item key="1">
            <a-icon type="delete" @click="batchDel" />
            删除
          </a-menu-item>
          <a-menu-item key="2">
            <a-icon type="lock" @click="batchFrozen('2')" />
            冻结
          </a-menu-item>
          <a-menu-item key="3">
            <a-icon type="unlock" @click="batchFrozen('1')" />
            解冻
          </a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px">
          批量操作
          <a-icon type="down" />
        </a-button>
      </a-dropdown>
      <!-- <j-super-query :fieldList="superQueryFieldList" @handleSuperQuery="handleSuperQuery"/> -->
    </div>

    <!-- table区域-begin -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i>已选择&nbsp;<a
          style="font-weight: 600">{{ selectedRowKeys.length }}</a>项&nbsp;&nbsp;
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
      </div>

      <a-table ref="table" bordered size="middle" rowKey="id" :columns="columns" :dataSource="dataSource"
        :pagination="ipagination" :loading="loading"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}" @change="handleTableChange">

        <template slot="avatarslot" slot-scope="text, record, index">
          <div class="anty-img-wrap">
            <a-avatar shape="square" :src="getAvatarView(record.avatar)" icon="user" />
          </div>
        </template>

        <template slot="integralSlot" slot-scope="text, record, index">
          <a @click="handleIntegral(record)">{{text}}</a>
        </template>

        <template slot="vipLevel" slot-scope="text">
          <div v-if="text=='1'">黄金会员</div>
          <div v-else-if="text=='2'">铂金会员</div>
          <div v-else-if="text=='3'">钻石会员</div>
          <div v-else>普通用户</div>
        </template>
        <template slot="sex" slot-scope="text">
          <div v-if="text=='0'">未知</div>
          <div v-else-if="text=='1'">男</div>
          <div v-else-if="text=='2'">女</div>
        </template>
        <template slot="status" slot-scope="text">
          <div v-if="text=='1'">正常</div>
          <div v-else-if="text=='2'">冻结</div>
        </template>
        <span slot="action" slot-scope="text, record">

          <!-- <a @click="handleEditIntegral(record)" >积分兑换</a>
          <a-divider type="vertical" /> -->

         <!-- <a @click="handleEdit(record)">编辑</a>
          <a-divider type="vertical" /> -->

          <a-dropdown>
            <a class="ant-dropdown-link">
              更多 <a-icon type="down" />
            </a>
            <a-menu slot="overlay">
              <!-- <a-menu-item>
                <a href="javascript:;" @click="handleDetail(record)">详情</a>
              </a-menu-item> -->
              <!-- <a-menu-item>
                <a href="javascript:;" @click="handleChangePassword(record.username)">密码</a>
              </a-menu-item> -->
              <a-menu-item>
                <a href="javascript:;" @click="showReferrerLog(record)">邀新记录</a>
              </a-menu-item>
              <a-menu-item>
                <a href="javascript:;" @click="showIntegralLog(record)">积分记录</a>
              </a-menu-item>
              <a-menu-item>
                <a href="javascript:;" @click="showBalanceLog(record)">余额记录</a>
              </a-menu-item>
              <a-menu-item>
                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                  <a>删除</a>
                </a-popconfirm>
              </a-menu-item>
              <a-menu-item v-if="record.status==1">
                <a-popconfirm title="确定冻结吗?" @confirm="() => handleFrozen(record.id,2,record.username)">
                  <a>冻结</a>
                </a-popconfirm>
              </a-menu-item>
              <a-menu-item v-if="record.status==2">
                <a-popconfirm title="确定解冻吗?" @confirm="() => handleFrozen(record.id,1,record.username)">
                  <a>解冻</a>
                </a-popconfirm>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </span>


      </a-table>
    </div>
    <!-- table区域-end -->

    <user-modal ref="modalForm" @ok="modalFormOk"></user-modal>
    <user-integral-modal ref="modalIntegralForm" @ok="modalFormOk"></user-integral-modal>
    <integral-modal ref="integralmodal" @ok="integralModalFormOk"></integral-modal>
    <integral-log-list-modal ref="integralLogmodal" @ok="modalFormOk"></integral-log-list-modal>
    <referrer-log-list-modal  ref="referrerLogmodal" @ok="modalFormOk"></referrer-log-list-modal>
    <account-log-list-modal ref="accountLogmodal" @ok="modalFormOk"></account-log-list-modal>

    <password-modal ref="passwordmodal" @ok="passwordModalOk"></password-modal>

    <sys-user-agent-modal ref="sysUserAgentModal"></sys-user-agent-modal>

    <!-- 用户回收站 -->
    <user-recycle-bin-modal :visible.sync="recycleBinVisible" @ok="modalFormOk" />

  </a-card>
</template>

<script>
  import UserModal from './modules/UserModal2'
  import UserIntegralModal from './modules/UserIntegralModal'
  import IntegralModal from './modules/IntegralModal'
  import IntegralLogListModal from '../integral/modules/IntegralLogListModal.vue'
  import PasswordModal from './modules/PasswordModal'
  import ReferrerLogListModal from '../ums/modules/ReferrerLogListModal.vue'
  import AccountLogListModal from '../ums/modules/AccountLogListModal.vue'
  
  import {
    putAction,
    getFileAccessHttpUrl
  } from '@/api/manage';
  import {
    frozenBatch,
    unbindCompany
  } from '@/api/api'
  import {
    JeecgListMixin
  } from '@/mixins/JeecgListMixin'
  import SysUserAgentModal from "./modules/SysUserAgentModal";
  import JInput from '@/components/jeecg/JInput'
  import UserRecycleBinModal from './modules/UserRecycleBinModal'
  import JSuperQuery from '@/components/jeecg/JSuperQuery'
  import JThirdAppButton from '@/components/jeecgbiz/thirdApp/JThirdAppButton'

  export default {
    name: "UserList",
    mixins: [JeecgListMixin],
    components: {
      JThirdAppButton,
      SysUserAgentModal,
      UserModal,
      PasswordModal,
      JInput,
      UserRecycleBinModal,
      JSuperQuery,
      UserIntegralModal,
      IntegralModal,
      IntegralLogListModal,
      ReferrerLogListModal,
      AccountLogListModal
    },
    data() {
      return {
        description: '这是用户管理页面',
        queryParam: {},
        recycleBinVisible: false,
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
          // {
          //   title: '编号',
          //   align: "center",
          //   dataIndex: 'userCode',
          // },
          {
            title: '头像',
            align: "center",
            dataIndex: 'avatar',
            scopedSlots: {
              customRender: "avatarslot"
            }
          },
          // {
          //   title: '用户昵称',
          //   align: "center",
          //   dataIndex: 'nickname',
          // },
          {
            title: '用户姓名',
            align: "center",
            dataIndex: 'nickname',
          },
          // {
          //   title: '用户账号',
          //   align: "center",
          //   dataIndex: 'username',
          //   width: 120,
          //   sorter: true
          // },
          {
            title: '手机号码',
            align: "center",
            dataIndex: 'phone'
          },
          {
            title: '性别',
            align: "center",
            dataIndex: 'sex',
            scopedSlots: {
              customRender: 'sex'
            },
          },
          // {
          //   title: '会员等级',
          //   align: "center",
          //   dataIndex: 'vipLevel',
          //   scopedSlots: {customRender: "vipLevel"}
          // },
          {
            title: '可用积分',
            align: "center",
            dataIndex: 'integral',
            scopedSlots: {
              customRender: "integralSlot"
            }
          },
          {
            title: '账户余额',
            align: "center",
            dataIndex: 'balance',
          },
          {
            title: '可提余额',
            align: "center",
            dataIndex: 'balanceWithdraw',
          },
          {
            title: '冻结余额',
            align: "center",
            dataIndex: 'balanceFrozen',
          },
          {
            title: '状态',
            align: "center",
            dataIndex: 'status',
            scopedSlots: {
              customRender: 'status'
            },
          },
          {
            title: '添加时间',
            align: "center",
            dataIndex: 'createTime'
          },
          {
            title: '操作',
            dataIndex: 'action',
            scopedSlots: {
              customRender: 'action'
            },
            align: "center",
            width: 200
          }

        ],
        superQueryFieldList: [{
            type: 'input',
            value: 'username',
            text: '用户账号',
          },
          {
            type: 'input',
            value: 'realname',
            text: '用户姓名',
          },
          {
            type: 'select',
            value: 'sex',
            dbType: 'int',
            text: '性别',
            dictCode: 'sex'
          },
        ],
        url: {
          syncUser: "/act/process/extActProcess/doSyncUser",
          list: "/sys/user/list2?userIdentity=3",
          delete: "/sys/user/delete",
          deleteBatch: "/sys/user/deleteBatch",
          exportXlsUrl: "/sys/user/exportXls",
          importExcelUrl: "sys/user/importExcel",
        },
      }
    },
    computed: {
      importExcelUrl: function() {
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      }
    },
    methods: {
      
      getAvatarView: function(avatar) {
        return getFileAccessHttpUrl(avatar)
      },

      batchFrozen: function(status) {
        if (this.selectedRowKeys.length <= 0) {
          this.$message.warning('请选择一条记录！');
          return false;
        } else {
          let ids = "";
          let that = this;
          let isAdmin = false;
          that.selectionRows.forEach(function(row) {
            if (row.username == 'admin') {
              isAdmin = true;
            }
          });
          if (isAdmin) {
            that.$message.warning('管理员账号不允许此操作,请重新选择！');
            return;
          }
          that.selectedRowKeys.forEach(function(val) {
            ids += val + ",";
          });
          that.$confirm({
            title: "确认操作",
            content: "是否" + (status == 1 ? "解冻" : "冻结") + "选中账号?",
            onOk: function() {
              frozenBatch({
                ids: ids,
                status: status
              }).then((res) => {
                if (res.success) {
                  that.$message.success(res.message);
                  that.loadData();
                  that.onClearSelected();
                } else {
                  that.$message.warning(res.message);
                }
              });
            }
          });
        }
      },
      handleMenuClick(e) {
        if (e.key == 1) {
          this.batchDel();
        } else if (e.key == 2) {
          this.batchFrozen(2);
        } else if (e.key == 3) {
          this.batchFrozen(1);
        }
      },
      handleFrozen: function(id, status, username) {
        let that = this;
        //TODO 后台校验管理员角色
        if ('admin' == username) {
          that.$message.warning('管理员账号不允许此操作！');
          return;
        }
        frozenBatch({
          ids: id,
          status: status
        }).then((res) => {
          if (res.success) {
            that.$message.success(res.message);
            that.loadData();
          } else {
            that.$message.warning(res.message);
          }
        });
      },
      handleUnbind: function(record) {
        let that = this;
        unbindCompany({
          id: record.id,
          companyId: record.companyId
        }).then((res) => {
          if (res.success) {
            that.$message.success(res.message);
            that.loadData();
          } else {
            that.$message.warning(res.message);
          }
        });
      },

      handleChangePassword(username) {
        this.$refs.passwordmodal.show(username);
      },
      passwordModalOk() {
        //TODO 密码修改完成 不需要刷新页面，可以把datasource中的数据更新一下
      },
      onSyncFinally({
        isToLocal
      }) {
        // 同步到本地时刷新下数据
        if (isToLocal) {
          this.loadData()
        }
      },

      handleEditIntegral(item) {
        this.$refs.modalIntegralForm.edit(item);
      },

      handleIntegral(record) {
        this.$refs.integralmodal.edit(record);
      },

      integralModalFormOk() {
        this.$refs.integralmodal.close();
        this.loadData();
      },
      
      showIntegralLog(record){
        this.$refs.integralLogmodal.show(record);
      },
      
      showReferrerLog(record){
         this.$refs.referrerLogmodal.show(record);
      },
      
      showBalanceLog(record){
         this.$refs.accountLogmodal.show(record);
      },
    }

  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>