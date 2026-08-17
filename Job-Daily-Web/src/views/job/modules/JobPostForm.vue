<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <a-form-model ref="form" :model="model" :rules="validatorRules" slot="detail">
        <a-row>
          <a-col :span="24">
            <a-form-model-item label="招工标题" :labelCol="labelCol2" :wrapperCol="wrapperCol2" prop="title">
              <a-input v-model="model.title" placeholder="请输入标题,限20字以内"></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="想招的工种" :labelCol="labelCol2" :wrapperCol="wrapperCol2" prop="typeIds">
              <j-tree-select ref="treeSelect" placeholder="请选择父级节点" v-model="model.typeIds" dict="job_types,name,id"
                pidField="pid" pidValue="0" hasChildField="has_child">
              </j-tree-select>
            </a-form-model-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col :span="12">
            <a-form-model-item label="开始时间" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="startTime">
              <j-date v-model="model.startTime" placeholder="请选择工作开始时间" :showTime="true"
                dateFormat="YYYY-MM-DD HH:mm:ss" style="width: 100%;"></j-date>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="结束时间" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="endTime">
              <j-date v-model="model.endTime" placeholder="请选择工作结束时间" :showTime="true" dateFormat="YYYY-MM-DD HH:mm:ss"
                style="width: 100%;"></j-date>
            </a-form-model-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col :span="12">
            <a-form-model-item label="截止时间" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="closeTime">
              <j-date v-model="model.closeTime" placeholder="请选择招工截止时间" :showTime="true"
                dateFormat="YYYY-MM-DD HH:mm:ss" style="width: 100%;"></j-date>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="招聘人数" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="recruitsNumber">
              <a-input-number v-model="model.recruitsNumber" placeholder="请输入招聘人数" :min="1" style="width: 100%" />
            </a-form-model-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col :span="12">
            <a-form-model-item label="联系人" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="name">
              <a-input v-model="model.name" placeholder="请输入联系人"></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="联系电话" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="phone">
              <a-input v-model="model.phone" placeholder="请输入联系电话"></a-input>
            </a-form-model-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col :span="12">
            <a-form-item label="工价单位" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="salaryUnit">
              <j-dict-select-tag placeholder="请选择薪资单位" v-model="model.salaryUnit" dictCode="salary_unit" />
            </a-form-item>
          </a-col>
          <a-col :span="12" v-if="model.salaryUnit !='面议'">
            <a-form-model-item label="工价设置" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="salary">
              <a-input v-model="model.salary" placeholder="请输入金额"></a-input>
            </a-form-model-item>
          </a-col>
        </a-row>
        <a-row v-if="model.id">
          <a-col :span="12">
            <a-form-model-item label="招工状态" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="postStatus">
              <j-dict-select-tag placeholder="请选择招工状态" v-model="model.postStatus" dictCode="post_status" />
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="浏览数量" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="browseNumber">
              <a-input v-model="model.browseNumber" placeholder="请输入浏览数量"></a-input>
            </a-form-model-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col :span="24">
            <a-form-model-item label="职位描述" :labelCol="labelCol2" :wrapperCol="wrapperCol2" prop="descr">
              <a-textarea v-model="model.descr" rows="4" placeholder="请输入职位描述" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="工作地点" :labelCol="labelCol2" :wrapperCol="wrapperCol2" prop="latitude">
              <a-input v-model="model.address" placeholder="请选择工作地点, 选择后可编辑地址" readonly></a-input>
              <a-input v-model="model.latitude" style="width: 0;height: 0;display: none;" placeholder="请输入工作地点" readonly></a-input>
              <select-address @change="addressChange" :value="lngLat" :showLatLng="showLatLng"></select-address>
            </a-form-model-item>
          </a-col>
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
  import selectAddress from '../../../components/map/selectAddress.vue';

  export default {
    name: 'JobPostForm',
    components: {
      selectAddress
    },
    props: {
      //表单禁用
      disabled: {
        type: Boolean,
        default: false,
        required: false,
      }
    },
    data() {
      return {
        lngLat: [],
        showLatLng: false,
        model: {
          salaryUnit: null,
          salary: '',
          pcity:'',
          pcityCode:'',
          latitude:'',
          longitude:'',
        },
        labelCol: {
          xs: {
            span: 24
          },
          sm: {
            span: 4
          },
        },
        wrapperCol: {
          xs: {
            span: 24
          },
          sm: {
            span: 17
          },
        },
        labelCol2: {
          xs: {
            span: 24
          },
          sm: {
            span: 2
          }
        },
        wrapperCol2: {
          xs: {
            span: 24
          },
          sm: {
            span: 21
          }
        },
        confirmLoading: false,
        validatorRules: {
          title: [{
              required: true,
              message: '请输入招工标题!',
            },
            {
              max: 20,
              message: '长度不能超过20字'
            }
          ],
          typeIds: [{
            required: true,
            message: '请选择想招的工种!'
          }],
          closeTime: [{
            required: true,
            message: '请选择招工截止时间!'
          }],
          recruitsNumber: [{
            required: true,
            message: '请输入招聘人数!'
          }],
          name: [{
            required: true,
            message: '请输入联系人!'
          }],
          phone: [{
            required: true,
            message: '请输入联系电话!'
          }],
          salary: [{
            required: true,
            message: '请选择工价单位!'
          }],
          descr: [{
            required: true,
            message: '请输入职位描述!'
          }],
          latitude: [{
            required: true,
            message: '请选择地址!'
          }],
        },
        url: {
          add: "/job/jobPost/add",
          edit: "/job/jobPost/edit",
          queryById: "/job/jobPost/queryById"
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
      
      addressChange(res) {
        console.log('addressChange', res);
        this.model.address = res.location.address;
        this.model.addressName = res.location.addressName;
        this.model.latitude = res.location.latitude+"";
        this.model.longitude = res.location.longitude+"";
        this.model.city = res.location.district;
        this.model.cityCode = res.location.districtCode;
        this.model.pcity = res.location.city;
        this.model.pcityCode = res.location.cityCode;
        this.$refs.form.validateField(['latitude']);
        this.$forceUpdate();
      },
      
      add() {
        this.edit(this.modelDefault);
      },
      edit(record) {
        this.model = Object.assign({}, record);
        this.visible = true;
      },
      submitForm() {
        const that = this;
        // 触发表单""验证
        console.log("model===",this.model);
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

<style>
  .ant-form-item {
    margin-bottom: 10px;
  }

  .title {
    font-weight: bold;
    margin-bottom: 10px;
  }
</style>