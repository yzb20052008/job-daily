<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <a-form-model ref="form" :model="model" :rules="validatorRules" slot="detail">
        <a-row>
          <a-col :span="24">
            <a-form-model-item label="文章标题" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="title">
              <a-input v-model="model.title" placeholder="请输入文章标题"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="所属分类" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="categoryId">
  	          <j-tree-select
                ref="treeSelect"
                placeholder="请选择所属分类"
                v-model="model.categoryId"
                dict="cms_category,name,id"
                pidValue="0"
                >
              </j-tree-select>
            </a-form-model-item>
          </a-col>
         <!-- <a-col :span="24">
            <a-form-model-item label="文章摘要" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="excerpt">
              <a-textarea v-model="model.excerpt" rows="4" placeholder="请输入文章摘要" />
            </a-form-model-item>
          </a-col> -->
          <a-col :span="24">
            <a-form-model-item label="文章内容" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="content">
              <j-editor v-model="model.content" />
            </a-form-model-item>
          </a-col>
          <!-- <a-col :span="24">
            <a-form-model-item label="轮播大图" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="banner">
              <a-input v-model="model.banner" placeholder="请输入轮播大图"  ></a-input>
            </a-form-model-item>
          </a-col> -->
         <!-- <a-col :span="24">
            <a-form-model-item label="封面图标" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="avatar">
              <a-input v-model="model.avatar" placeholder="请输入封面图标"  ></a-input>
            </a-form-model-item>
          </a-col> -->
          <!-- <a-col :span="24">
            <a-form-model-item label="版本信息" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="version">
              <a-input v-model="model.version" placeholder="请输入版本信息"  ></a-input>
            </a-form-model-item>
          </a-col> -->
          <!-- <a-col :span="24">
            <a-form-model-item label="阅读数量" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="viewCount">
              <a-input-number v-model="model.viewCount" placeholder="请输入阅读数量" style="width: 100%" />
            </a-form-model-item>
          </a-col> -->
          <a-col :span="24">
            <a-form-model-item label="排序" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="sort">
              <a-input-number v-model="model.sort" placeholder="请输入排序" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="启用状态" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="status">
              <j-dict-select-tag type="radio" v-model="model.status" dictCode="status" placeholder="请选择启用状态" />
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
    name: 'CmsArticlesForm',
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
          style:{width:'120px'}
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
          style:{width:'800px'}
        },
        confirmLoading: false,
        validatorRules: {
        },
        url: {
          add: "/cms/cmsArticles/add",
          edit: "/cms/cmsArticles/edit",
          queryById: "/cms/cmsArticles/queryById"
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