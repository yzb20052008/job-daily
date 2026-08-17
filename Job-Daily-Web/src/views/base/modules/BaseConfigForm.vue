<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <a-form-model ref="form" :model="model" :rules="validatorRules" slot="detail">
        <a-row>
          <a-col :span="24">
            <a-form-model-item label="所属分类" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="groupCode">
              <j-dict-select-tag type="list" v-model="model.groupCode" dictCode="group_code" placeholder="请选择所属分类的编码，来自于“常量的分类”字典" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="参数名称" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="configName">
              <a-input v-model="model.configName" placeholder="请输入名称"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="参数编码" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="configCode">
              <a-input v-model="model.configCode" placeholder="请输入属性编码"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="参数值" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="configValue">
              <a-textarea v-model="model.configValue" rows="4" placeholder="请输入属性值" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="参数说明" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="remark">
              <a-textarea v-model="model.remark" rows="4" placeholder="请输入参数说明" />
            </a-form-model-item>
          </a-col>
          <!-- <a-col :span="24">
            <a-form-model-item label="是否系统参数" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="configFlag">
              <j-dict-select-tag type="radio" v-model="model.configFlag" dictCode="config_flag" placeholder="请选择启用状态" />
            </a-form-model-item>
          </a-col> -->
          <!-- <a-col :span="24">
            <a-form-model-item label="是否启用" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="status">
              <j-dict-select-tag type="radio" v-model="model.status" dictCode="status" placeholder="请选择启用状态" />
            </a-form-model-item>
          </a-col> -->
        </a-row>
      </a-form-model>
    </j-form-container>
  </a-spin>
</template>

<script>

  import { httpAction, getAction } from '@/api/manage'
  import { validateDuplicateValue } from '@/utils/util'

  export default {
    name: 'BaseConfigForm',
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
          add: "/base/baseConfig/add",
          edit: "/base/baseConfig/edit",
          queryById: "/base/baseConfig/queryById"
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