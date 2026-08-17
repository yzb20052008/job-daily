import xf_show_modal from './index.js'
const f_show_modal = {
	install: function(Vue) {
        const show_modal_fun=function(op={}){
            return	new Promise((resolve, reject)=>{
			   let ssm=new xf_show_modal({
					...op,
					$event:function(e){
						if(e.res){
							resolve(e);
						}else{
							reject(e);
						}
					 }
					})
					ssm.show();
					Vue.prototype.$hide=function(){
						ssm.hide();
					}
			})
		}
        // $showModal挂载到uni对象上
        uni.$showModal = show_modal_fun
        Vue.prototype.$showModal = show_modal_fun
	}
};

export default f_show_modal
