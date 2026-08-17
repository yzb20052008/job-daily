import base from "@/config/baseUrl";
/* 
* 选择图片
* count:上传图片数量
* sourceType: 1.从相册选图，2.使用相机 3.二者都有
* callback:回调
*/
function chooseImage(callback,count=9,sourceType=3) {
	let that = this
	var sourceType = ['album', 'camera']
	if(sourceType==1){
		sourceType = ['album']
	}else if(sourceType==2){
		sourceType = ['camera']
	}
	uni.chooseImage({
		count: count,  // 最多可以选择的图片张数，默认9
		sizeType: ['compressed'], // original 原图，compressed 压缩图，默认二者都有
		sourceType: sourceType, // album 从相册选图，camera 使用相机，默认二者都有
		success: function (res) {
			console.log(res,'chooseImage')
			callback && callback(res)
			// 上传图片用到res.tempFilePaths,这个是列表多个图需循环上传
		}
	})
}

/* 
* 选择视频
* sourceType: 1.从相册选视频，2.使用相机拍摄 3.二者都有
* callback:回调
*/
function chooseVideo(callback,sourceType=3) {
	let that = this
	var sourceType = ['album', 'camera']
	if(sourceType==1){
		sourceType = ['album']
	}else if(sourceType==2){
		sourceType = ['camera']
	}
	uni.chooseVideo({
		sourceType: sourceType, // album 从相册选视频，camera 使用相机拍摄，默认为：['album', 'camera']
		success: function (res) {
			console.log(res,'chooseVideo')
			callback && callback(res)
			// 上传视频用到res.tempFilePath
		}
	})
}

/* 
* 选择文件
* count:上传数量
* callback:回调
*/
function chooseFile(callback,count=1) {
	// #ifdef MP-WEIXIN
	wx.chooseMessageFile({
	    count: count,
	    success: function (res) {
	    	console.log(res,'chooseFile');
	    	callback && callback(res)
	    }
	})
	// #endif
	// #ifdef H5
	// 需要hx2.9.9以上才支持uni.chooseFile
	uni.chooseFile({
	    count: count,
	    success: function (res) {
	    	console.log(res,'chooseFile');
	    	callback && callback(res)
	    }
	})
	// #endif
}

/* 
* 文件上传
* filePath:临时上传路径
* type：1.图片上传、2.视频上传 3.文件上传
* formData：其他参数
*/
function uploadFilePromise(filePath,type=1,formData={type:'image'}) {
	var url = base.baseImg+'/YmUpload_image'
	if(type==2){
		url = base.baseImg+'/YmUpload_videoFile'
	}
	return new Promise((resolve, reject) => {
		let a = uni.uploadFile({
			url: url, // 上传接口
			filePath: filePath,
			name: 'file',
			formData: formData,
			success: (res) => {
				console.log(res,'uploadFilePromise')
				var data = JSON.parse(res.data)
				resolve(data.data)
			}
		});
	})
}

export {
	chooseImage,       //选择图片
	chooseVideo,       //选择视频
	chooseFile,        //选择文件
	uploadFilePromise, //上传
}