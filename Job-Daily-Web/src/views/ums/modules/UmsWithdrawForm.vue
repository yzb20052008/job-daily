<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <a-form-model ref="form" :model="model" :rules="validatorRules" slot="detail">
        <a-row>
          <a-col :span="24">
            <a-form-model-item label="会员ID" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="memberId">
              <a-input v-model="model.memberId" placeholder="请输入会员ID"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="提现金额" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="money">
              <a-input-number v-model="model.money" placeholder="请输入提现金额" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="提现前余额" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="lastBalance">
              <a-input-number v-model="model.lastBalance" placeholder="请输入提现前余额" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="提现后余额" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="balance">
              <a-input-number v-model="model.balance" placeholder="请输入提现后余额" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="提现状态:0-待审核，1-审核通过，2-审核失败" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="withdrawStatus">
              <j-dict-select-tag type="list" v-model="model.withdrawStatus" dictCode="withdraw_status" placeholder="请选择提现状态:0-待审核，1-审核通过，2-审核失败" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="提现失败原因" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="reason">
              <a-input v-model="model.reason" placeholder="请输入提现失败原因"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="账户类型：0-支付宝，1-微信，2-银联" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="accountType">
              <j-dict-select-tag type="list" v-model="model.accountType" dictCode="account_type" placeholder="请选择账户类型：0-支付宝，1-微信，2-银联" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="提现账号（账号信息、银行卡号等）" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="withdrawAccount">
              <a-input v-model="model.withdrawAccount" placeholder="请输入提现账号（账号信息、银行卡号等）"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="关联名称（支付宝-实名，微信-昵称，银联-持卡人）" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="withdrawName">
              <a-input v-model="model.withdrawName" placeholder="请输入关联名称（支付宝-实名，微信-昵称，银联-持卡人）"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="银行名称" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="bankName">
              <a-input v-model="model.bankName" placeholder="请输入银行名称"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="支行信息" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="bankBranchName">
              <a-input v-model="model.bankBranchName" placeholder="请输入支行信息"  ></a-input>
            </a-form-model-item>
          </a-col>
        </a-row>
      </a-form-model>
    </j-form-container>
  </a-spin>
</template>

<script>

  import { httpAction, getAction } from '@/api/manage'
  import { validateDuplicateValue } from '@/utils/util'

  export default {
    name: 'UmsWithdrawForm',
    components: {
    },
    props: {
      //表单禁用
      disabled: {
        type: Boolean,
        default: false,
        required: false
      }
    },
    data () {
      return {
        model:{
         },
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        confirmLoading: false,
        validatorRules: {
        },
        url: {
          add: "/ums/umsWithdraw/add",
          edit: "/ums/umsWithdraw/edit",
          queryById: "/ums/umsWithdraw/queryById"
        }
      }
    },
    computed: {
      formDisabled(){
        return this.disabled
      },
    },
    created () {
       //备份model原始值
      this.modelDefault = JSON.parse(JSON.stringify(this.model));
    },
    methods: {
      add () {
        this.edit(this.modelDefault);
      },
      edit (record) {
        this.model = Object.assign({}, record);
        this.visible = true;
      },
      submitForm () {
        const that = this;
        // 触发表单验证
        this.$refs.form.validate(valid => {
          if (valid) {
            that.confirmLoading = true;
            let httpurl = '';
            let method = '';
            if(!this.model.id){
              httpurl+=this.url.add;
              method = 'post';
            }else{
              httpurl+=this.url.edit;
               method = 'put';
            }
            httpAction(httpurl,this.model,method).then((res)=>{
              if(res.success){
                that.$message.success(res.message);
                that.$emit('ok');
              }else{
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