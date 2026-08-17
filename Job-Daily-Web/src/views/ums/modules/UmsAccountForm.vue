<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <a-form-model ref="form" :model="model" :rules="validatorRules" slot="detail">
        <a-row>
          <a-col :span="24">
            <a-form-model-item label="账户ID" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="accountId">
              <a-input v-model="model.accountId" placeholder="请输入账户ID"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="会员ID" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="memberId">
              <a-input v-model="model.memberId" placeholder="请输入会员ID"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="冻结余额" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="balanceFrozen">
              <a-input-number v-model="model.balanceFrozen" placeholder="请输入冻结余额" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="可提余额" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="balanceWithdraw">
              <a-input-number v-model="model.balanceWithdraw" placeholder="请输入可提余额" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="可用余额" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="balance">
              <a-input-number v-model="model.balance" placeholder="请输入可用余额" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="充值总额" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="totalRecharge">
              <a-input-number v-model="model.totalRecharge" placeholder="请输入充值总额" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="提现总额" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="totalWithdraw">
              <a-input-number v-model="model.totalWithdraw" placeholder="请输入提现总额" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="消费总额" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="totalConsume">
              <a-input-number v-model="model.totalConsume" placeholder="请输入消费总额" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="会员积分" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="integral">
              <a-input-number v-model="model.integral" placeholder="请输入会员积分" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="累计积分" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="totalIntegral">
              <a-input-number v-model="model.totalIntegral" placeholder="请输入累计积分" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="remark">
              <a-input v-model="model.remark" placeholder="请输入备注"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="tenantId" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="tenantId">
              <a-input-number v-model="model.tenantId" placeholder="请输入tenantId" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="删除状态(0-正常,1-已删除)" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="delFlag">
              <a-input-number v-model="model.delFlag" placeholder="请输入删除状态(0-正常,1-已删除)" style="width: 100%" />
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
    name: 'UmsAccountForm',
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
           accountId: [
              { required: true, message: '请输入账户ID!'},
           ],
        },
        url: {
          add: "/ums/umsAccount/add",
          edit: "/ums/umsAccount/edit",
          queryById: "/ums/umsAccount/queryById"
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