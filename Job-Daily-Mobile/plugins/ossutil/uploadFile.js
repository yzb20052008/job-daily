import base from '@/config/baseUrl';
import store from '@/store';

/**
 * 上传文件到后台中转（/api/file/upload），由服务端按 jeecg.uploadType 写入本地/OSS/MinIO。
 * 不再在客户端持有或使用 OSS AccessKey。
 *
 * @param {number} type 0-图片，1-视频（仅影响扩展名兜底，实际以后台为准）
 * @param {string} filePath 本地临时路径
 * @param {string} dir 业务目录，对应 formData.biz（如 job/user）
 * @param {Function} successc 成功回调，参数为可访问 URL/路径（Result.message）
 * @param {Function} failc 失败回调
 */
const uploadFile = function(type, filePath, dir, successc, failc) {
	if (!filePath || String(filePath).length < 4) {
		uni.showModal({
			title: '文件错误',
			content: '请重试',
			showCancel: false
		});
		failc && failc(new Error('invalid filePath'));
		return;
	}

	const token =
		(store.state.userInfo && store.state.userInfo.token) ||
		(uni.getStorageSync('userInfo') && uni.getStorageSync('userInfo').token) ||
		'';
	if (!token) {
		uni.showToast({ title: '请先登录后再上传', icon: 'none' });
		failc && failc(new Error('unauthorized'));
		return;
	}

	// 后台 biz 不允许含 ..；去掉首尾斜杠
	let biz = (dir || 'upload').replace(/^\/+|\/+$/g, '');
	if (!biz) {
		biz = 'upload';
	}

	uni.uploadFile({
		url: base.baseUrl + '/api/file/upload',
		filePath: filePath,
		name: 'file',
		header: {
			'X-Access-Token': token
		},
		formData: {
			biz: biz
		},
		success: function(res) {
			try {
				const data = typeof res.data === 'string' ? JSON.parse(res.data) : res.data;
				if (res.statusCode === 200 && data && (data.success === true || data.code === 200 || data.code === 0)) {
					const url = data.message || (data.result && (data.result.url || data.result)) || '';
					if (url) {
						successc && successc(url);
						return;
					}
				}
				const errMsg = (data && (data.message || data.msg)) || '上传失败';
				uni.showToast({ title: errMsg, icon: 'none' });
				failc && failc(new Error(errMsg));
			} catch (e) {
				failc && failc(e);
			}
		},
		fail: function(err) {
			uni.showToast({ title: '上传失败', icon: 'none' });
			failc && failc(err);
		}
	});
};

module.exports = uploadFile;
