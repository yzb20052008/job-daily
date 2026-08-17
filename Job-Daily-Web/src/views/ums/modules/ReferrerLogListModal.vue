<template>
  <a-drawer :title="title" :maskClosable="true" :width="drawerWidth" placement="right" :closable="true" @close="handleCancel" :visible="visible" style="height: 100%;">
    <template slot="title">
      <div style="width: 100%;">
        <span>{{ title }}</span>
        <span style="display:inline-block;width:calc(100% - 51px);padding-right:10px;text-align: right">
          <a-button @click="toggleScreen" icon="appstore" style="height:20px;width:20px;border:0px"></a-button>
        </span>
      </div>
    </template>

    <a-spin :spinning="confirmLoading">
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
        <template slot="htmlSlot" slot-scope="text">
          <div v-html="text"></div>
        </template>
        <template slot="imgSlot" slot-scope="text, record">
          <span v-if="!text" style="font-size: 12px;font-style: italic;">无图片</span>
          <img v-else :src="getImgView(text)" :preview="record.id" height="25px" alt="" style="max-width:80px;font-size: 12px;font-style: italic;" />
        </template>
        <template slot="fileSlot" slot-scope="text">
          <span v-if="!text" style="font-size: 12px;font-style: italic;">无文件</span>
          <a-button v-else :ghost="true" type="primary" icon="download" size="small" @click="downloadFile(text)">下载</a-button>
        </template>
        <template slot="integralSlot" slot-scope="text,record">
          <div v-if="record.ifAdd==1" style="color: darkgreen;">+{{record.integral}}</div>
          <div v-else style="color: darkred;">-{{record.integral}}</div>
        </template>
        <span slot="action" slot-scope="text, record"><a @click="showDetail(record)">课题详情</a></span>
      </a-table>
    </a-spin>
  </a-drawer>
</template>

<script>
import moment from 'moment';
import Vue from 'vue';
import '@/assets/less/TableExpand.less';
import { mixinDevice } from '@/utils/mixin';
import { JeecgListMixin } from '@/mixins/JeecgListMixin';
import { ACCESS_TOKEN } from '@/store/mutation-types';
import { getAction } from '@/api/manage';
import { addUser, editUser, queryUserRole, queryall } from '@/api/api';
import { disabledAuthFilter } from '@/utils/authFilter';
import { duplicateCheck } from '@/api/api';
import { filterMultiDictText } from '@/components/dict/JDictSelectUtil';

export default {
  name: 'VerifyListModal',
  mixins: [JeecgListMixin, mixinDevice],
  components: {},
  data() {
    return {
      departDisabled: false, //是否是我的部门调用该页面
      roleDisabled: false, //是否是角色维护调用该页面
      modalWidth: 900,
      drawerWidth: 800,
      modaltoggleFlag: true,
      userId: '', //保存用户id
      title: '操作记录',
      visible: false,
      confirmLoading: false,
      headers: {},
      columns: [
          // {
          //   title:'邀请人头像',
          //   align:"center",
          //   dataIndex: 'referrerAvatar',
          //   scopedSlots: { customRender: 'imgSlot' }
          // },
          // {
          //   title:'邀请人名称',
          //   align:"center",
          //   dataIndex: 'referrerName'
          // },
          // {
          //   title:'邀请人手机',
          //   align:"center",
          //   dataIndex: 'referrerPhone'
          // },
          {
            title:'用户头像',
            align:"center",
            dataIndex: 'userAvatar',
            scopedSlots: { customRender: 'imgSlot' }
          },
          {
            title:'用户名称',
            align:"center",
            dataIndex: 'userName'
          },
          {
            title:'用户手机',
            align:"center",
            dataIndex: 'userPhone'
          },
          {
            title:'奖励积分',
            align:"center",
            dataIndex: 'integral'
          },
          {
            title:'注册时间',
            align:"center",
            dataIndex: 'createTime'
          },
        ],
      url: {
        list: "/ums/umsReferrerLog/list",
        delete: "/ums/umsReferrerLog/delete",
        deleteBatch: "/ums/umsReferrerLog/deleteBatch",
        exportXlsUrl: "/ums/umsReferrerLog/exportXls",
        importExcelUrl: "ums/umsReferrerLog/importExcel",
        
      },
      dictOptions: {},
      superFieldList: []
    };
  },
  created() {},
  methods: {
    show(row) {
      console.log('show==', row);
      let that = this;
      that.visible = true;
      //根据屏幕宽度自适应抽屉宽度
      this.resetScreenSize();
      this.queryParam.referrerId = row.id;
      this.loadData();
    },
    //窗口最大化切换
    toggleScreen() {
      if (this.modaltoggleFlag) {
        this.modalWidth = window.innerWidth;
      } else {
        this.modalWidth = 900;
      }
      this.modaltoggleFlag = !this.modaltoggleFlag;
    },
    // 根据屏幕变化,设置抽屉尺寸
    resetScreenSize() {
      let screenWidth = document.body.clientWidth;
      if (screenWidth < 500) {
        this.drawerWidth = screenWidth;
      } else {
        this.drawerWidth = 800;
      }
    },
    close() {
      this.$emit('close');
      this.visible = false;
      // this.$refs.form.resetFields();
    },
    moment,
    handleCancel() {
      this.close();
    }
  }
};
</script>

<style scoped>
.avatar-uploader > .ant-upload {
  width: 104px;
  height: 104px;
}
.ant-upload-select-picture-card i {
  font-size: 49px;
  color: #999;
}

.ant-upload-select-picture-card .ant-upload-text {
  margin-top: 8px;
  color: #666;
}

.ant-table-tbody .ant-table-row td {
  padding-top: 10px;
  padding-bottom: 10px;
}

.drawer-bootom-button {
  position: absolute;
  bottom: 0;
  width: 100%;
  border-top: 1px solid #e8e8e8;
  padding: 10px 16px;
  text-align: right;
  left: 0;
  background: #fff;
  border-radius: 0 0 2px 2px;
}

/*【JTC-502】 添加用户两个滚动条*/
/deep/ .ant-drawer-body {
  padding-bottom: 53px;
}
</style>
