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
    <UmsRealnameVerify :id="id" :type="type" :verify="verify" @success="handleSuccess" ref="realForm"></UmsRealnameVerify>
  </j-modal>
</template>

<script>

  import UmsRealnameVerify from './UmsRealnameVerify.vue'
  export default {
    name: 'UmsRealnameVerifyModal',
    components: {
      UmsRealnameVerify
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
        title:'司机详情',
        width:1000,
        visible: false,
        disableSubmit: true
      }
    },
    
    watch: {
      type:{
        handler(newType,oldType){
          if (this.type === 1) {
            this.title="司机详情"
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