<template>
  <j-modal
    :title="title"
    :width="width"
    :visible="visible"
    switchFullscreen
    @ok="handleOk"
    :okButtonProps="{ class:{'jee-hidden': disableSubmit} }"
    @cancel="handleCancel"
    cancelText="关闭">
    <job-post-detail v-if="isDetail" ref="realForm" @ok="submitCallback" :disabled="disableSubmit"></job-post-detail>
    <job-post-form v-else ref="realForm" @ok="submitCallback" :disabled="disableSubmit"></job-post-form>
    
  </j-modal>
</template>

<script>

  import JobPostForm from './JobPostForm'
  import JobPostDetail from './JobPostDetail'
  export default {
    name: 'JobPostModal',
    components: {
      JobPostForm,
      JobPostDetail
    },
    data () {
      return {
        title:'',
        width:1200,
        visible: false,
        disableSubmit: false,
        isDetail:false,
      }
    },
    methods: {
      add () {
        this.visible=true;
         this.isDetail=false;
        this.$nextTick(()=>{
          this.$refs.realForm.add();
        })
      },
      edit (record) {
        this.isDetail=false;
        this.visible=true
        this.$nextTick(()=>{
          this.$refs.realForm.edit(record);
        })
      },
      
      detail (record) {
        this.isDetail=true;
        this.visible=true
        this.$nextTick(()=>{
          this.$refs.realForm.edit(record);
        })
      },
      
      close () {
        this.$emit('close');
        this.visible = false;
      },
      handleOk () {
        this.$refs.realForm.submitForm();
      },
      submitCallback(){
        this.$emit('ok');
        this.visible = false;
      },
      handleCancel () {
        this.close()
      }
    }
  }
</script>