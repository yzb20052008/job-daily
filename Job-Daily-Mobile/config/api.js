// 获取手机验证码
export const getSmsCode = (params) => uni.$u.http.get('/api/login/getSmsCode', params)
// 手机号验证码登录
export const loginByCode = (params) => uni.$u.http.post('/api/login/mPhoneLogin', params)
// 微信小程序授权登录
export const loginByWx = (params) => uni.$u.http.post('/api/login/mWxLogin', params)
// 抖音小程序授权登录
export const loginByDy = (params) => uni.$u.http.post('/api/login/mDyLogin', params)
// 小程序静默授权登录
export const loginByUnion = (params) => uni.$u.http.post('/api/login/mLoginByCode', params)
// 小程序授权手机号登录
export const mPhoneAuthLogin = (params) => uni.$u.http.post('/api/login/mPhoneAuthLogin', params)
// 小程序授权手机号登录
export const loginByCodeForGzh = (params) => uni.$u.http.post('/api/login/loginByCodeForGzh', params)
// 短信验证码校验
export const judgeSmsCode = (params) => uni.$u.http.post('/api/login/judgeSmsCode', params)

// 编辑用户信息
export const updateUser = (params) => uni.$u.http.post('/api/user/updateUser', params)
// 查询用户信息-token
export const getUserInfoByToken = (params) => uni.$u.http.get('/api/user/getUserInfoByToken', params)
// 查询用户信息
export const getUserInfo = (params) => uni.$u.http.get('/api/user/getUserInfo', params)
// 用户角色切换
export const switchRole = (params) => uni.$u.http.post('/api/user/switchRole', params)
// 更新手机号
export const updatePhone = (params) => uni.$u.http.post('/api/user/updatePhone', params)
// 更新用户定位
export const updateUserLocation = (params) => uni.$u.http.post('/api/user/updateUserLocation', params)


// 查询收支明细
export const getFinanceList = (data) => uni.$u.http.get('/api/finance/getFinanceList', data)
// 查询提现记录
export const getWithdrawList = (data) => uni.$u.http.get('/api/finance/getWithdrawList', data)
// 查询账户余额
export const getAccountDetail = (data) => uni.$u.http.get('/api/finance/getAccountDetail', data)
// 提现申请
export const withdrawApply = (data) => uni.$u.http.post('/api/finance/withdrawApply', data)
// 查询提现账户
export const getWithdrawAccount = (data) => uni.$u.http.get('/api/finance/getWithdrawAccount', data)
//查询待确认收款信息
export const getTransferConfirmList = (data) => uni.$u.http.get('/api/finance/getTransferConfirmList', data)
//查询转账状态
export const getTransferByOutBillNo = (data) => uni.$u.http.get('/api/finance/getTransferByOutBillNo', data)


// 兑换积分
export const integralExchange = (data) => uni.$u.http.post('/api/integral/integralExchange', data)
// 积分转赠
export const integralTransfer = (data) => uni.$u.http.post('/api/integral/integralTransfer', data)
// 积分日志
export const getIntegralLogList = (data) => uni.$u.http.get('/api/integral/getIntegralLogList', data)
// 查询积分道具列表
export const getJfGoodsList = (data) => uni.$u.http.get('/api/integral/getJfGoodsList', data)
// 购买积分道具
export const createJfOrder = (data) => uni.$u.http.post('/api/integral/createJfOrder', data)
// 积分充值余额支付
export const yePayIntegral = (data) => uni.$u.http.post('/api/integral/yePayIntegral', data)


// 查询内容信息
export const getArticle = (data) => uni.$u.http.get('/api/cms/getArticle', data)
// 查询公告列表
export const getNoticeList = (data) => uni.$u.http.get('/api/cms/getNoticeList', data)
// 查询广告列表
export const getAdList = (data) => uni.$u.http.get('/api/cms/getAdList', data)
// 查询公告详情
export const getNoticeDetail = (data) => uni.$u.http.get('/api/cms/getNoticeDetail', data)
// 查询内容列表
export const getArticleList = (data) => uni.$u.http.get('/api/cms/getArticleList', data)
// 意见反馈
export const addFeedback = (params) => uni.$u.http.post('/api/cms/addFeedback', params)
// 查询动态
export const getNewsList = (data) => uni.$u.http.get('/api/cms/getNewsList', data)
// 查询动态
export const getNewsDetail = (data) => uni.$u.http.get('/api/cms/getNewsDetail', data)
// 查询基础配置信息
export const getConfig = (data) => uni.$u.http.get('/api/cms/getConfig', data)
// 查询首页分类信息
export const getTypeList = (data) => uni.$u.http.get('/api/cms/getTypeList', data)
// 查询未读数量
export const getUnReadCount = (data) => uni.$u.http.get('/api/cms/getUnReadCount', data)
// 全部已读
export const setAllRead = (data) => uni.$u.http.post('/api/cms/setAllRead', data)
// 查询未读数量
export const getUnReadTotalCount = (data) => uni.$u.http.get('/api/cms/getUnReadTotalCount', data)



// 字典查询
export const getDictItems = (params) => uni.$u.http.get('/api/dict/getDictItems', params)

// 提交留言
export const addComment = (params) => uni.$u.http.post('/api/comment/addComment', params)
// 查询评论列表
export const getCommentList = (data) => uni.$u.http.get('/api/comment/getCommentList', data)
// 查询积分记录
export const getIntegralList = (data) => uni.$u.http.get('/api/integral/getIntegralList', data)

// 微信小程序积分充值支付
export const miniPayIntegral = (data) => uni.$u.http.get('/wxPay/miniPayIntegral', data)
// 微信小程序工资支付
export const miniPaySalary = (data) => uni.$u.http.get('/wxPay/miniPaySalary', data)
// 支付宝结算工资
export const appPaySalary = (data) => uni.$u.http.get('/aliPay/appPaySalary', data)


// 抖音小程序支付
export const getDyMiniPay = (data) => uni.$u.http.get('/dyPay/miniAppPay', data)

// 查询工种信息
export const getTypes = (data) => uni.$u.http.get('/api/types/getTypes', data)
// 添加工种信息
export const addTypes = (data) => uni.$u.http.post('/api/types/addTypes', data)
// 查询工种信息
export const getTypesList = (data) => uni.$u.http.get('/api/types/getTypeList', data)


// 实名认证
export const addRealNameAuth = (data) => uni.$u.http.post('/api/auth/addRealNameAuth', data)
// 企业认证
export const addCompanyAuth = (data) => uni.$u.http.post('/api/auth/addCompanyAuth', data)
// 查询实名认证
export const getRealNameAuth = (data) => uni.$u.http.get('/api/auth/getRealNameAuth', data)
// 查询企业认证
export const getCompanyAuth = (data) => uni.$u.http.get('/api/auth/getCompanyAuth', data)


// 查询招工列表
export const getPostList = (data) => uni.$u.http.get('/api/post/getPostList', data)
// 查询我的招工列表
export const getMyPostList = (data) => uni.$u.http.get('/api/post/getMyPostList', data)
// 添加招工信息
export const addPost = (data) => uni.$u.http.post('/api/post/addPost', data)
// 更新招工信息
export const updatePost = (data) => uni.$u.http.post('/api/post/updatePost', data)
// 更新招工状态
export const updatePostStatus = (data) => uni.$u.http.post('/api/post/updatePostStatus', data)
// 查询招工详情
export const getPostDetail = (data) => uni.$u.http.get('/api/post/getPostDetail', data)
// 查询招工基本信息，id查询
export const getPostInfo = (data) => uni.$u.http.get('/api/post/getPostInfo', data)


// 查询配置参数
export const getBaseConfig = (data) => uni.$u.http.get('/api/base/getBaseConfig', data)

// 添加拨号记录
export const addContact = (data) => uni.$u.http.post('/api/contact/addContact', data)
// 查询拨号记录
export const getContactList = (data) => uni.$u.http.get('/api/contact/getContactList', data)
// 删除拨号记录
export const delContact = (data) => uni.$u.http.post('/api/contact/delContact', data)
// 更新合作意向
export const updateAgreeState = (data) => uni.$u.http.post('/api/contact/updateAgreeState', data)

// 添加或移除收藏
export const updateCollect = (data) => uni.$u.http.post('/api/collect/updateCollect', data)
// 移除收藏
export const delCollect = (data) => uni.$u.http.post('/api/collect/delCollect', data)
// 查询收藏列表
export const getCollectList = (data) => uni.$u.http.get('/api/collect/getCollectList', data)
// 查询我的统计
export const getMyStatistics = (data) => uni.$u.http.get('/api/collect/getMyStatistics', data)


// 查询订单列表
export const getOrderList = (data) => uni.$u.http.get('/api/order/getOrderList', data)
// 查询员工列表
export const getPostUserList = (data) => uni.$u.http.get('/api/order/getPostUserList', data)
// 查询招工下状态统计
export const getOrderStatistics = (data) => uni.$u.http.get('/api/order/getOrderStatistics', data)
// 修改订单状态
export const updateOrderStatus = (data) => uni.$u.http.post('/api/order/updateOrderStatus', data)
// 老板确认接单
export const confirmOrder = (data) => uni.$u.http.post('/api/order/confirm', data)
// 取消订单
export const cancelOrder = (data) => uni.$u.http.post('/api/order/cancel', data)
// 上下班打卡
export const workClock = (data) => uni.$u.http.post('/api/order/workClock', data)
// 查询订单详情
export const getOrderDetail = (data) => uni.$u.http.get('/api/order/getOrderDetail', data)
// 提交申请
export const addApply = (data) => uni.$u.http.post('/api/order/addApply', data)

// 查询简历列表
export const getResumeList = (data) => uni.$u.http.get('/api/resume/getResumeList', data)
// 添加/更新简历
export const updateResume = (data) => uni.$u.http.post('/api/resume/updateResume', data)
// 查询简历信息
export const getResumeInfo = (data) => uni.$u.http.get('/api/resume/getResumeInfo', data)
// 查询简历信息(基础信息)
export const getResumeBase = (data) => uni.$u.http.get('/api/resume/getResumeBase', data)
// 添加/更新求职意向
export const updateIntention = (data) => uni.$u.http.post('/api/resume/updateIntention', data)
// 查询求职意向
export const getIntention = (data) => uni.$u.http.get('/api/resume/getIntention', data)
// 添加工作经验
export const addResumeExp = (data) => uni.$u.http.post('/api/resume/addResumeExp', data)
// 查询工作经验
export const getResumeExp = (data) => uni.$u.http.get('/api/resume/getResumeExp', data)
// 更新工作经验
export const updateResumeExp = (data) => uni.$u.http.post('/api/resume/updateResumeExp', data)
// 删除工作经验
export const delResumeExp = (data) => uni.$u.http.post('/api/resume/delResumeExp', data)
// 添加技能证书
export const addResumeCert = (data) => uni.$u.http.post('/api/resume/addResumeCert', data)
// 查询技能证书
export const getResumeCert = (data) => uni.$u.http.get('/api/resume/getResumeCert', data)
// 更新技能证书
export const updateResumeCert = (data) => uni.$u.http.post('/api/resume/updateResumeCert', data)
// 删除技能证书
export const delResumeCert = (data) => uni.$u.http.post('/api/resume/delResumeCert', data)

// 查询推荐记录
export const getReferrerList = (data) => uni.$u.http.get('/api/referrer/getReferrerList', data)
// 查询推荐统计
export const getReferrerCount = (data) => uni.$u.http.get('/api/referrer/getReferrerCount', data)

// 添加评论
export const addEvaluate = (data) => uni.$u.http.post('/api/evaluate/addEvaluate', data)
// 查询我的评价
export const getMyEvaluateList = (data) => uni.$u.http.get('/api/evaluate/getMyEvaluateList', data)
// 查询用户评价列表
export const getEvaluateList = (data) => uni.$u.http.get('/api/evaluate/getEvaluateList', data)

