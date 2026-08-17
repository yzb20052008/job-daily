<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <a-form-model ref="form" :model="model" :rules="validatorRules" slot="detail">
        <a-row>
          <a-col :span="24">
            <a-form-model-item label="通知标题" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="title">
              <a-input v-model="model.title" placeholder="请输入通知标题"></a-input>
            </a-form-model-item>
          </a-col>
          <!-- <a-col :span="24">
            <a-form-model-item label="通知类型：0-系统通知，1-应用通知" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="type">
              <j-dict-select-tag type="list" v-model="model.type" dictCode="" placeholder="请选择通知类型：0-系统通知，1-应用通知" />
            </a-form-model-item>
          </a-col> -->
          <!-- <a-col :span="24">
            <a-form-model-item label="轮播大图" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="banner">
              <j-image-upload isMultiple  v-model="model.banner" ></j-image-upload>
            </a-form-model-item>
          </a-col> -->
          <a-col :span="24">
            <a-form-model-item label="封面图标" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="avatar">
              <j-image-upload isMultiple v-model="model.avatar"></j-image-upload>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" prop="msgType" label="接收用户">
              <a-radio-group v-model="model.msgType" @change="chooseMsgType">
                <a-radio value="USER">指定用户</a-radio>
                <a-radio value="ALL">全体用户</a-radio>
              </a-radio-group>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="指定用户" v-if="userType">
              <j-select-multi-user2 :returnKeys="returnKeys" placeholder="请选择指定用户" v-model="userIds"
                :trigger-change="true" @selectedChange="dataChange"></j-select-multi-user2>
            </a-form-model-item>
          </a-col>
          <a-col :span="24" v-if="userType">
            <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" prop="type" label="消息类型">
              <a-radio-group v-model="model.type">
                <a-radio value="2">平台私信</a-radio>
                <a-radio value="4">违规记录</a-radio>
              </a-radio-group>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="是否置顶" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="setTop">
              <j-dict-select-tag type="radio" v-model="model.setTop" dictCode="set_top" placeholder="请选择是否置顶" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="启用状态" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="status">
              <j-dict-select-tag type="radio" v-model="model.status" dictCode="status" placeholder="请选择启用状态" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="内容" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="content">
              <j-editor v-model="model.content" />
            </a-form-model-item>
          </a-col>
          <!-- <a-col :span="24">
            <a-form-model-item label="排序" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="sort">
              <a-input-number v-model="model.sort" placeholder="请输入排序" style="width: 100%" />
            </a-form-model-item>
          </a-col> -->
        </a-row>
      </a-form-model>
    </j-form-container>
  </a-spin>
</template>

<script>
  import {
    httpAction,
    getAction
  } from '@/api/manage'
  import {
    validateDuplicateValue
  } from '@/utils/util'

  export default {
    name: 'CmsNoticeForm',
    components: {},
    props: {
      //表单禁用
      disabled: {
        type: Boolean,
        default: false,
        required: false
      }
    },
    data() {
      return {
        model: {},

        userType: false,
        userIds: "",
        selectedUser: [],
        returnKeys: ['id', 'id'], //用户选择返回字段

        labelCol: {
          xs: {
            span: 24
          },
          sm: {
            span: 5
          },
        },
        wrapperCol: {
          xs: {
            span: 24
          },
          sm: {
            span: 16
          },
        },
        confirmLoading: false,
        validatorRules: {},
        url: {
          add: "/cms/cmsNotice/add",
          edit: "/cms/cmsNotice/edit",
          queryById: "/cms/cmsNotice/queryById"
        }
      }
    },
    computed: {
      formDisabled() {
        return this.disabled
      },
    },
    created() {
      //备份model原始值
      this.modelDefault = JSON.parse(JSON.stringify(this.model));
    },
    methods: {

      chooseMsgType(e) {
        if ("USER" == e.target.value) {
          this.userType = true;
        } else {
          this.userType = false;
          //update-begin---author:wangshuai ---date:20220318  for：[issues/I4X63V]vue有些页面报错，但是在线演示的却没有-----
          this.userIds = "";
          //update-end---author:wangshuai ---date:20220318  for：[issues/I4X63V]vue有些页面报错，但是在线演示的却没有-----
        }
      },
      
      dataChange(e){
        console.log("dataChange===",e)
        this.model.userId=e;
      },

      add() {
        this.edit(this.modelDefault);
      },
      edit(record) {
        this.model = Object.assign({}, record);
        this.visible = true;
      },
      submitForm() {
        console.log("model===",this.model)
        const that = this;
        // 触发表单验证
        this.$refs.form.validate(valid => {
          if (valid) {
            that.confirmLoading = true;
            let httpurl = '';
            let method = '';
            if (!this.model.id) {
              httpurl += this.url.add;
              method = 'post';
            } else {
              httpurl += this.url.edit;
              method = 'put';
            }
            httpAction(httpurl, this.model, method).then((res) => {
              if (res.success) {
                that.$message.success(res.message);
                that.$emit('ok');
              } else {
                that.$message.warning(res.message);
              }
            }).finally(() => {
              that.confirmLoading = false;
            })
          }
        })
      },
    }
  }
</script>