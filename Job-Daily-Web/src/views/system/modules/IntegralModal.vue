<template>
  <a-modal
    :title="title"
    :width="800"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    wrapClassName="ant-modal-cust-warp"
    style="top:5%;height: 85%;overflow-y: hidden">

    <a-spin :spinning="confirmLoading">
      <a-form-model ref="form"  v-bind="layout"  :model="model" :rules="validatorRules">
        <a-form-model-item label="积分数量" required prop="integral">
          <a-input-number v-model="model.integral"  placeholder="请输入积分数量" style="width: 100%;"/>
        </a-form-model-item>
        <p style="margin-left: 15px;color: #999;">会员积分操作，正数为增加积分，负数为扣除积分</p>
      </a-form-model>
    </a-spin>
  </a-modal>
</template>

<script>
  import { httpAction, getAction } from '@/api/manage'
  export default {
    name: "RoleModal",
    data () {
      return {
        title:"会员积分操作",
        visible: false,
        roleDisabled: false,
        model: {
          integral:null,
        },
        layout: {
          labelCol: { span: 3 },
          wrapperCol: { span: 14 },
        },
        confirmLoading: false,
        validatorRules:{
          integral: [
            { required: true, message: '请输入积分数量!' },
          ],
        },
        url: {
          edit: "/integral/integralLog/editIntegral",
        }
      }
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
        this.model.id = record.id;
        this.model.integral=null;
        this.visible = true;
      },
      close () {
        this.$refs.form.clearValidate();
        this.$emit('close');
        this.visible = false;
      },
      handleOk () {
        const that = this;
        // 触发表单验证
        this.$refs.form.validate(valid => {
          if (valid) {
            that.confirmLoading = true;
            httpAction(this.url.edit,this.model,"post").then((res)=>{
               console.log("httpAction");
              if(res.success){
                that.$message.success(res.message);
                that.$emit('ok');
              }else{
                that.$message.warning(res.message);
              }
            }).finally(() => {
              that.confirmLoading = false;
            })
            
            
          }else{
            return false;
          }
        })
      },
      handleCancel () {
        this.close()
      },
    }
  }
</script>

<style scoped>

</style>