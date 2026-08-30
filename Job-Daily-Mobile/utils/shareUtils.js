/**
 * 微信小程序分享工具类
 * 支持分享给好友和分享到朋友圈
 */
import baseUrl from '@/config/baseUrl.js'

class ShareUtils {
    
    /**
     * 设置页面分享配置
     * @param {Object} options 分享配置
     * @param {string} options.title 分享标题
     * @param {string} options.path 分享路径
     * @param {string} options.imageUrl 分享图片
     * @param {string} options.query 朋友圈分享参数
     */
    static setPageShare(options = {}) {
        const defaultOptions = {
            title: baseUrl.share.title || '小蓝零工',
            path: '/pages/index/index',
            imageUrl: 'https://cdn.example.com/imgs/share-zp.png',
            query: ''
        }
        
        const shareConfig = Object.assign({}, defaultOptions, options)
        // 设置分享给好友的配置
        uni.$u.mpShare = {
            title: shareConfig.title,
            path: shareConfig.path,
            imageUrl: shareConfig.imageUrl
        }
        
        // 设置分享到朋友圈的配置
        uni.$u.mpShareTimeline = {
            title: shareConfig.title,
            query: shareConfig.query,
            imageUrl: shareConfig.imageUrl
        }
        
        return shareConfig
    }
    
    /**
     * 设置职位分享配置
     * @param {Object} jobInfo 职位信息
     */
    static setJobShare(jobInfo) {
        const title = `【${jobInfo.title}】${jobInfo.salary || '面议'} - 小蓝零工`
        const path = `/pages/job/postDetail?id=${jobInfo.id}`
        const query = `id=${jobInfo.id}`
        
        return this.setPageShare({
            title,
            path,
            query
        })
    }
    
    /**
     * 设置公司分享配置
     * @param {Object} companyInfo 公司信息
     */
    static setCompanyShare(companyInfo) {
        const title = `【${companyInfo.name}】正在招聘 - 小蓝零工`
        const path = `/pages/company/detail?id=${companyInfo.id}`
        const query = `id=${companyInfo.id}`
        
        return this.setPageShare({
            title,
            path,
            query
        })
    }
    
    /**
     * 设置邀请分享配置
     * @param {Object} userInfo 用户信息
     */
    static setInviteShare(userInfo) {
        const title = '真实招聘，高效上岗 - 小蓝零工'
        const path = `/pages/index/index?referrer=${userInfo.id}`
        const query = `referrer=${userInfo.id}`
        
        return this.setPageShare({
            title,
            path,
            query
        })
    }
    
    /**
     * 设置简历分享配置
     * @param {Object} resumeInfo 简历信息
     */
    static setResumeShare(resumeInfo) {
        const title = `【${resumeInfo.name}】的简历 - 小蓝零工`
        const path = `/pages/resume/detail?id=${resumeInfo.id}`
        const query = `id=${resumeInfo.id}`
        
        return this.setPageShare({
            title,
            path,
            query
        })
    }
    
    /**
     * 获取当前页面的分享配置
     * @param {Object} currentPage 当前页面信息
     */
    static getCurrentPageShare(currentPage) {
        const pages = getCurrentPages()
        const page = pages[pages.length - 1]
        const route = page.route
        const options = page.options
        
        let query = ''
        if (options && Object.keys(options).length > 0) {
            query = Object.keys(options).map(key => `${key}=${options[key]}`).join('&')
        }
        
        return this.setPageShare({
            title: currentPage.title || baseUrl.share.title,
            path: `/${route}${query ? '?' + query : ''}`,
            query: query
        })
    }
}

export default ShareUtils
