export class show_model{
	constructor(option={}) {
	
		this.bodyModel=null;
		this.cancelModel=null;
		this.confirmModel=null;
		this.pageHeight=uni.getSystemInfoSync().screenHeight;
		let opacity = option.opacity || 0.4;
		let model_tit=option.title||'温馨提示';
		let model_content=option.content||"请输入内容~"
		let clickEvent=option.IsclickEvent||false;
		let cancelVal=option.cancelVal||'取消';
		let confirmVal=option.confirmVal||'确认';
		let cancelColor=option.cancelColor||'#0F7EF5';
		let confirmColor=option.confirmColor||'#0F7EF5';
		let delCancel=option.delCancel||false;
		let align=option.align||"center";
		let fn = ()=>{};
		this.$event = option.$event || fn;
		let backOff=option.backOff||false;
		
		//#ifdef APP-PLUS
		this.creatView({height:`${this.pageHeight}px`,top:0},opacity,clickEvent,{'tit':model_tit,'content':model_content,cancelVal,confirmVal,confirmColor,cancelColor,delCancel,align})
		if(!backOff){
			this.backbtn();
		}
		//#endif
	}
    backbtn(){
		let that=this;
		plus.key.addEventListener('backbutton', function (e) {
			 console.log(e,'back');
			 	that.hide();
		},false)
	}
	//生成提示框view
	creatView(style,opa,clickEvent,modelInfo){
		style = {
			left:'0px',
			width:'100%',
			...style
		}
        
		let view = new plus.nativeObj.View('showModalView',style);
		view.draw([
			{tag:'rect',id:'modal',color:`rgba(0,0,0,${opa})`,position:{top:'0px',left:'0px',width:'100%',height:'100%'}},
		    {tag:'rect',id:'content',color:`rgb(255,255,255)`,rectStyles:{borderWidth:'2px',radius:'8px'},position:{top:'40%',left:'10%',width:'80%',height:'20%'}},
		    {tag:'font',id:'title',text:modelInfo.tit,textStyles:{size:'16px',color:'#000'},position:{top:this.pageHeight*0.418+'px',left:'10%',width:'80%',height:'2%'}},
		    {tag:'font',id:'text',text:modelInfo.content,textStyles:{size:'14px',color:'#666',whiteSpace:'normal',align:modelInfo.align},position:{top:this.pageHeight*0.425+'px',left:'13%',width:'74%',height:'12%'}},
		    {tag:'rect',id:'line',color:'#EAEAEC',position:{top:'55%',left:'10%',width:'80%',height:'1px'}},
			{tag:'rect',id:'line2',color:'#EAEAEC',position:{top:'55%',left:'49%',width:modelInfo.delCancel?'0px':'1px',height:modelInfo.delCancel?'0px':'5%'}}
			
		]);

		if(!modelInfo.delCancel){
			// 取消	
			let viewCancel=new plus.nativeObj.View('cancel',{width:'40%',height:'5%',top:this.pageHeight*0.55+'px',left:'10%',backgroundColor:'rgba(255,255,255,0)'});
			    viewCancel.draw([
				  {tag:'font',id:'cancel',text:modelInfo.cancelVal,textStyles:{color:modelInfo.cancelColor,size:'14px'}},
				]);
				
				viewCancel.addEventListener("click",(e)=>{
					this.$event({res:false,types:'cancel'});
					this.hide();
				},false);
				this.cancelModel=viewCancel;
		}
		
		// 确认
		let viewconfirm=new plus.nativeObj.View('confirm',{width:modelInfo.delCancel?'80%':'40%',height:'5%',top:this.pageHeight*0.55+'px',left:modelInfo.delCancel?'10%':'50%',backgroundColor:'rgba(255,255,255,0)'});
	        viewconfirm.draw([
			  {tag:'font',id:'confirm',text:modelInfo.confirmVal,textStyles:{color:modelInfo.confirmColor,size:'14px'}},
			]);
		
			viewconfirm.addEventListener("click",(e)=>{
				this.$event({res:true,types:'confirm'});
				this.hide();
				
			},false);	
		  //点击蒙布
		  if(clickEvent){
			
			 view.addEventListener("click", (e) => {
				this.$event({res:false,types:'cover'});
				this.hide();
			}, false);
		}
	   this.bodyModel=view;
	   this.confirmModel=viewconfirm;
	}
    showModalAnimationClose(){
		var options = {type:'pop-out',duration:300};
			plus.nativeObj.View.startAnimation(options,{view:this.bodyModel},{view:this.cancelModel},{view:this.viewconfirm},function(){
				console.log('plus.nativeObj.View.startAnimation动画结束');
				// 关闭原生动画
				plus.nativeObj.View.clearAnimation();
			});
	}
	showModalAnimationOpen(){
		var options = {type:'pop-in',duration:1000};
		
			plus.nativeObj.View.startAnimation(options,{view:this.bodyModel},{view:this.cancelModel},{view:this.viewconfirm},function(){
				console.log('plus.nativeObj.View.startAnimation动画结束');
				// 关闭原生动画
				plus.nativeObj.View.clearAnimation();
			});
	}
	show(){
        
		this.showModalAnimationOpen();
		this.bodyModel.show();
		if(this.cancelModel){
			this.cancelModel.show();
		}
		this.confirmModel.show();
	
	}
	hide(){
		this.showModalAnimationClose();
		this.bodyModel.hide();
		if(this.cancelModel){
	      this.cancelModel.hide();	
		}
		this.confirmModel.hide();
		
		
		
	}
}

export default show_model