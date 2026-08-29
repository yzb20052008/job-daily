<template>
  <a-modal
    :title="title"
    :width="720"
    :visible="visible"
    :confirmLoading="confirmLoading"
    :okButtonProps="{ props: { disabled: disableSubmit } }"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
  >
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-form-item label="模板编码" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input
            v-decorator="['templateCode', { rules: [{ required: true, message: '请输入模板编码' }] }]"
            placeholder="如 wx_auth、site_order_paid_member"
            :disabled="!!model.id && !disableSubmit"
          />
        </a-form-item>
        <a-form-item label="通道" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-select
            v-decorator="['channel', { initialValue: 'site', rules: [{ required: true, message: '请选择通道' }] }]"
            :disabled="disableSubmit"
            @change="onChannelChange"
          >
            <a-select-option value="site">站内信</a-select-option>
            <a-select-option value="wx">微信</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="微信模板ID" :labelCol="labelCol" :wrapperCol="wrapperCol" v-show="channel === 'wx' || disableSubmit">
          <a-input
            v-decorator="['wxTemplateId', { rules: channel === 'wx' && !disableSubmit ? [{ required: true, message: '微信通道必填模板ID' }] : [] }]"
            placeholder="小程序订阅消息 TemplateId"
            :disabled="disableSubmit"
          />
        </a-form-item>
        <a-form-item label="标题" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input
            v-decorator="['title']"
            placeholder="支持 {amount} 等占位符"
            :disabled="disableSubmit"
          />
        </a-form-item>
        <a-form-item label="内容" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-textarea
            v-decorator="['content']"
            :rows="4"
            placeholder="支持 {amount} 等占位符"
            :disabled="disableSubmit"
          />
        </a-form-item>
        <a-form-item label="状态" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-select v-decorator="['status', { initialValue: '1' }]" :disabled="disableSubmit">
            <a-select-option value="1">启用</a-select-option>
            <a-select-option value="0">停用</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['remark']" :disabled="disableSubmit" />
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
import { httpAction } from '@/api/manage'
import pick from 'lodash.pick'

export default {
  name: 'BizMsgTemplateModal',
  data() {
    return {
      title: '操作',
      visible: false,
      model: {},
      channel: 'site',
      disableSubmit: false,
      confirmLoading: false,
      form: this.$form.createForm(this),
      labelCol: { xs: { span: 24 }, sm: { span: 5 } },
      wrapperCol: { xs: { span: 24 }, sm: { span: 16 } },
      url: {
        add: '/msg/bizMsgTemplate/add',
        edit: '/msg/bizMsgTemplate/edit'
      }
    }
  },
  methods: {
    add() {
      this.disableSubmit = false
      this.edit({})
    },
    edit(record) {
      this.form.resetFields()
      this.model = Object.assign({}, record || {})
      this.channel = this.model.channel || 'site'
      // 外部 handleDetail 会再设 disableSubmit=true；编辑入口由 mixin 置 false
      this.visible = true
      this.$nextTick(() => {
        this.form.setFieldsValue(
          pick(this.model, 'templateCode', 'channel', 'wxTemplateId', 'title', 'content', 'status', 'remark')
        )
      })
    },
    /** 兼容直接调用 */
    detail(record) {
      this.disableSubmit = true
      this.title = '详情'
      this.edit(record)
    },
    onChannelChange(val) {
      this.channel = val
    },
    close() {
      this.$emit('close')
      this.visible = false
      this.disableSubmit = false
    },
    handleOk() {
      if (this.disableSubmit) {
        this.close()
        return
      }
      this.form.validateFields((err, values) => {
        if (err) return
        this.confirmLoading = true
        const formData = Object.assign({}, this.model, values)
        const isEdit = !!formData.id
        const url = isEdit ? this.url.edit : this.url.add
        const method = isEdit ? 'put' : 'post'
        httpAction(url, formData, method)
          .then((res) => {
            if (res.success) {
              this.$message.success(res.message)
              this.$emit('ok')
              this.close()
            } else {
              this.$message.warning(res.message)
            }
          })
          .finally(() => {
            this.confirmLoading = false
          })
      })
    },
    handleCancel() {
      this.close()
    }
  }
}
</script>
