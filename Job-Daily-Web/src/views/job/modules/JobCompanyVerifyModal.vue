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
    <JobCompanyVerify :id="id" :type="type" :verify="verify" @success="handleSuccess" ref="realForm"></JobCompanyVerify>
  </j-modal>
</template>

<script>

  import JobCompanyVerify from './JobCompanyVerify.vue'
  export default {
    name: 'JobCompanyVerifyModal',
    components: {
      JobCompanyVerify
    },
    props: {
      type: {
        type: Number,
        default:0,
        required: false
      },
      verify: {
        type: Boolean,
        default: false,
        required: false
      }
    },
    data () {
      return {
        id:null,
        title:'详情',
        width:1000,
        visible: false,
        disableSubmit: true
      }
    },
    
    watch: {
      type:{
        handler(newType,oldType){
          if (this.type === 1) {
            this.title="详情"
          }
        },
        immediate:true,
      },
    },
    methods: {
      edit (record) {
        this.visible=true;
        this.id=record.id;
      },
      close () {
        this.$emit('close');
        this.visible = false;
      },
      handleOk () {
        this.$refs.realForm.submitForm();
      },
      
      handleSuccess(){
         this.$emit('success');
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