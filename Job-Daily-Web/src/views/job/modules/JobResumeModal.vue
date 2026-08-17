<template>
  <j-modal :title="title" :width="width" :visible="visible" switchFullscreen @ok="handleOk"
    :okButtonProps="{ class:{'jee-hidden': disableSubmit} }" @cancel="handleCancel" cancelText="关闭">
    <job-resume-detail v-if="ifDetail" ref="realForm" @ok="submitCallback"
      :disabled="disableSubmit"></job-resume-detail>
    <job-resume-form v-else ref="realForm" @ok="submitCallback" :disabled="disableSubmit"></job-resume-form>
  </j-modal>
</template>

<script>
  import JobResumeForm from './JobResumeForm'
  import JobResumeDetail from './JobResumeDetail'
  export default {
    name: 'JobResumeModal',
    components: {
      JobResumeForm,
      JobResumeDetail
    },
    data() {
      return {
        title: '',
        width: 1000,
        visible: false,
        disableSubmit: false,
        ifDetail: true
      }
    },
    methods: {
      add() {
        this.visible = true
        this.$nextTick(() => {
          this.$refs.realForm.add();
        })
      },
      edit(record) {
        this.ifDetail = false;
        this.visible = true
        this.$nextTick(() => {
          this.$refs.realForm.edit(record);
        })
      },

      detail(record) {
        this.ifDetail = true;
        this.visible = true
        this.$nextTick(() => {
          this.$refs.realForm.edit(record);
        })
      },

      close() {
        this.$emit('close');
        this.visible = false;
      },
      handleOk() {
        this.$refs.realForm.submitForm();
      },
      submitCallback() {
        this.$emit('ok');
        this.visible = false;
      },
      handleCancel() {
        this.close()
      }
    }
  }
</script>