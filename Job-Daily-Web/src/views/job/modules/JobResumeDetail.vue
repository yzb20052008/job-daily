<template>
  <a-spin :spinning="confirmLoading">
    <a-collapse v-model="activeKey">
      <a-collapse-panel key="1">
        <template slot="header">
          <div style="font-weight: bold;">基本信息</div>
        </template>
        <a-descriptions title="" bordered :column="2" size="middle">
          <a-descriptions-item label="姓名">{{ model.name }}</a-descriptions-item>
          <a-descriptions-item label="性别">{{ model.sex | formatSex }}</a-descriptions-item>
          <a-descriptions-item label="联系电话">{{ model.phone}}</a-descriptions-item>
          <a-descriptions-item label="实名认证">{{ model.ifRealName?'是':'否' }}</a-descriptions-item>
          <a-descriptions-item label="出生日期">{{ model.birthday || '无' }}</a-descriptions-item>
          <a-descriptions-item label="工龄">{{ model.workYear  || '无' }}</a-descriptions-item>
          <a-descriptions-item label="技能标签" :span="2">{{ model.skills  || '无' }}</a-descriptions-item>
          <a-descriptions-item label="招工状态" :span="2">{{ model.jobStatus | formatStatus }}</a-descriptions-item>
        </a-descriptions>
        <a-descriptions class="label" style="margin-top: 10px;" title="" bordered :column="1" layout="vertical"
          size="middle">
          <a-descriptions-item>
            <div slot="label" style="font-weight: bold;">个人介绍</div>
            <span class="span-wrap">{{ model.personalSkill ||'未填写'}}</span>
          </a-descriptions-item>
        </a-descriptions>
      </a-collapse-panel>
      <a-collapse-panel key="2">
        <template slot="header">
          <div style="font-weight: bold;">求职意向</div>
        </template>
        <a-descriptions title="" bordered :column="2" size="middle" v-if="model.intention">
          <a-descriptions-item label="想干的工种">{{ model.intention.typeNames  || '无'}}</a-descriptions-item>
          <a-descriptions-item label="期望工作地">{{ model.intention.workCity  || '无'}}</a-descriptions-item>
          <a-descriptions-item label="工作类型">{{ model.intention.workType || '无'}}</a-descriptions-item>
          <a-descriptions-item label="用工方式">{{ model.intention.employMethod || '无' }}</a-descriptions-item>
          <a-descriptions-item label="结算方式">{{ model.intention.settlementType || '无' }}</a-descriptions-item>
          <a-descriptions-item label="期望工资">{{ model.intention.expectSalary+model.intention.salaryUnit}}</a-descriptions-item>
        </a-descriptions>
        <div v-else>未填写</div>
      </a-collapse-panel>
      <a-collapse-panel key="3">
        <template slot="header">
          <div style="font-weight: bold;">项目经验</div>
        </template>
        <a-descriptions class="label" style="margin-bottom: 10px;" title="" bordered :column="1" layout="vertical"
          size="middle">
          <template v-for="(item,index) in model.expList" >
            <a-descriptions-item :span="1" >
              <div slot="label" style="font-weight: bold;">经验{{index+1}}：</div>
              <div style="display: flex;flex-direction: column;">
                <span class="span-wrap">{{ item.descr || '未填写'}}</span>
                <div style="margin-top: 10px;">
                  <j-image-upload text="未提交" disabled :value="item.url"></j-image-upload>
                </div>
              </div>
            </a-descriptions-item>
          </template>
        </a-descriptions>
      </a-collapse-panel>
      <a-collapse-panel key="2">
        <template slot="header">
          <div style="font-weight: bold;">技能证书</div>
        </template>
        <a-descriptions class="label" style="margin-bottom: 10px;" title="" bordered :column="1" layout="vertical"
          size="middle">
          <template v-for="(item,index) in model.certList" >
            <a-descriptions-item :span="1" >
              <div slot="label" style="font-weight: bold;">证书{{index+1}}：</div>
              <div style="display: flex;flex-direction: column;">
                <span class="span-wrap">{{ item.certName || '未填写'}}</span>
                <div style="margin-top: 10px;">
                  <j-image-upload text="未提交" disabled :value="item.certImg"></j-image-upload>
                </div>
              </div>
            </a-descriptions-item>
          </template>
        </a-descriptions>
      </a-collapse-panel>
    </a-collapse>
  </a-spin>
</template>

<script>
  import { init } from 'tinymce'
import {
    httpAction,
    getAction
  } from '@/api/manage'
  import {
    validateDuplicateValue
  } from '@/utils/util'

  export default {
    name: 'JobResumeForm',
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
        id:'',
        activeKey: ['1', '2', '3'],
        model: {},
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
          add: "/job/jobResume/add",
          edit: "/job/jobResume/edit",
          queryById: "/job/jobResume/queryById"
        }
      }
    },
    computed: {
      formDisabled() {
        return this.disabled
      },
    },

    filters: {
      formatSex(val) {
        if (val == 1) {
          return '男'
        } else if (val == 2) {
          return '女'
        } else {
          return '未知'
        }
      },
      
      formatStatus(val){
        if (val == 1) {
          return '正在找工作'
        } else if (val == 2) {
          return '暂不找工作'
        }
      }
    },

    created() {
      //备份model原始值
      this.modelDefault = JSON.parse(JSON.stringify(this.model));
    },
    methods: {
      edit(record) {
        this.visible = true;
        this.id=record.id;
        this.init();
      },
      
      init() {
        getAction(this.url.queryById, { id: this.id }).then(res => {
          console.log(res);
          if (res.code == 200) {
            this.model = res.result;
          }
        });
      },
    }
  }
</script>

<style>
  .span-wrap {
    white-space: pre-wrap;
  }
   .ant-descriptions-item-label{
    width: 130px;
  }
</style>