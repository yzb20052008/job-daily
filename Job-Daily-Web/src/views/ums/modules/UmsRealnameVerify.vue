<template>
  <a-spin :spinning="confirmLoading" style="background-color: #fff;margin-left: 10px;">
    <template>
      <a-collapse v-model="activeKey">
        <a-collapse-panel key="1">
          <template slot="header">
            <div style="font-weight: bold;">基本信息</div>
          </template>
          <a-descriptions title="" bordered :column="1" size="middle">
            <a-descriptions-item label="用户头像">
              <img style="width: 40px;height: 40px;" :src="userInfo.userAvatar" />
            </a-descriptions-item>
            <a-descriptions-item label="用户昵称">{{ userInfo.userName }}</a-descriptions-item>
            <a-descriptions-item label="联系方式">{{ userInfo.userPhone }}</a-descriptions-item>
            <a-descriptions-item label="真实姓名">{{ userInfo.realname }}</a-descriptions-item>
            <a-descriptions-item label="身份证号">{{ userInfo.idNo }}</a-descriptions-item>
            <a-descriptions-item label="身份证正面">
              <j-image-upload text="未提交" disabled v-model="userInfo.idCardFront"></j-image-upload>
            </a-descriptions-item>
            <a-descriptions-item label="身份证反面">
              <j-image-upload text="未提交" disabled v-model="userInfo.idCardBack"></j-image-upload>
            </a-descriptions-item>
            <a-descriptions-item label="审核状态">{{ userInfo.authStatus | formatStatus }}</a-descriptions-item>
            <a-descriptions-item label="提交时间">{{ userInfo.createTime }}</a-descriptions-item>
            <a-descriptions-item label="审核时间" v-if="type!=0">{{ userInfo.authTime}}</a-descriptions-item>
            <a-descriptions-item label="审核意见" v-if="type!=0 && userInfo.authStatus==2">{{ userInfo.authRemark}}</a-descriptions-item>
          </a-descriptions>
        </a-collapse-panel>
      </a-collapse>
      <template v-if="type==0">
        <div style="width: 100%;text-align: center;padding:30px 0">
          <a-button type="primary" size="large" @click.prevent="handleUpdate(1)">审核通过</a-button>
          <a-button type="warnning" size="large" style="margin-left: 20px;"
            @click.prevent="handleUpdate(2)">审核不通过</a-button>
        </div>
      </template>
    </template>
    <a-back-top />
    <a-modal :title="modalTitle" :visible="showModal" @ok="ok" @cancel="showModal = false" okText="确认" cancelText="关闭">
      <p>{{ modalContent }}</p>
      <a-textarea v-if="status != 1" v-model="reason" rows="4" placeholder="请输入审核意见" />
    </a-modal>
  </a-spin>
</template>

<script>
  import {
    httpAction,
    getAction
  } from '@/api/manage';
  import {
    validateDuplicateValue
  } from '@/utils/util';
  import {
    JVXETypes
  } from '@/components/jeecg/JVxeTable';
  import {
    mapActions,
    mapGetters,
    mapState
  } from 'vuex';
  import {
    judgeRole
  } from "@/utils/util"

  export default {
    name: 'UmsRealnameVerify',
    components: {},
    props: {
      //表单禁用
      disabled: {
        type: Boolean,
        default: false,
        required: false
      },
      id: {
        type: String,
        required: true
      },
      type: {
        type: Number,
        default: 0,
        required: false
      },
      verify: {
        type: Boolean,
        default: false,
        required: false
      }
    },
    data() {
      return {

        template: 1, //模板

        modalTitle: '温馨提示',
        showModal: false,
        modalContent: '确认？',
        status: null,
        reason: null,
        score: null,
        popVisible: false,

        userInfo: {},
        activeKey: ['1', '2', '3'],
        customStyle: 'background: #eee;font-weight:bold',
        confirmLoading: false,
        url: {
          queryById: '/ums/umsRealnameAuth/queryById',
          updateStatus: '/ums/umsRealnameAuth/updateStatus',
        },
      };
    },
    filters: {
      formatSex(val) {
        if (val == 1) {
          return '男';
        } else if (val == 2) {
          return '女';
        }
      },
      formatStatus(val) {
        if (val == 0) {
          return '待审核';
        } else if (val == 1) {
          return '已认证';
        } else if (val == 2) {
          return '认证失败';
        }
      }
    },
    created() {
      this.init();
    },
    methods: {

      init() {
        getAction(this.url.queryById, {
          id: this.id
        }).then(res => {
          console.log(res);
          if (res.code == 200) {
            this.userInfo = res.result;
          }
        });
      },
      handleUpdate(status) {
        this.showModal = true;
        switch (status) {
          case 1:
            this.status = 1;
            this.modalContent = '确定审核通过？';
            break;
          case 2:
            this.status = 2;
            this.modalContent = '确定审核不通过？';
            break;
        }
      },

      ok() {
        if (this.status != 1 && !this.reason) {
          this.$message.warning('请输入审核意见');
          return;
        }
        this.showModal = false;
        this.updateStatus();
      },

      updateStatus() {
        let that = this;
        that.confirmLoading = true;
        let method = 'post';
        let params = {
          id: this.userInfo.id,
          authStatus: this.status,
          reason: this.reason,
        };
        httpAction(this.url.updateStatus, params, method)
          .then(res => {
            if (res.success) {
              that.$message.success(res.message);
              that.$emit('success');
            } else {
              that.$message.warning(res.message);
            }
          })
          .finally(() => {
            that.confirmLoading = false;
          });
      }
    }
  };
</script>
<style scoped lang="less">
  .span-wrap {
    white-space: pre-wrap;
  }

  /deep/ .ant-descriptions-item-label {
    width: 180px
  }
</style>