/**
 * 已废弃：客户端不再直传阿里云 OSS。
 * 上传统一走后台 /api/file/upload（见 uploadFile.js）。
 * OSS 密钥仅配置在服务端 application-*.yml → jeecg.oss.*
 */
module.exports = {
	deprecated: true,
	hint: 'use /api/file/upload via uploadFile.js'
};
