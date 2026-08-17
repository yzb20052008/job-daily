package org.jeecg.modules.job.cms.service;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.job.cms.entity.CmsNotice;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 系统通知
 * @Author: qingkonglan
 * @Date:   2022-09-26
 * @Version: V1.0
 */
public interface ICmsNoticeService extends IService<CmsNotice> {

    /**
     * 添加通知
     * @param notice
     * @return
     */
    boolean addNotice(CmsNotice notice);


    /**
     *   统计未读量
     * @return
     */
    int getUnReadCount(String roleCode,String userId,Integer type);


    /**
     * 设置全部已读
     * @param userId
     * @return
     */
    boolean setAllRead(String userId);


    /**
     *  任务通知
     * @param userId 用户ID
     * @param title 标题
     * @param content 内容
     * @return
     */
    boolean addOrderNotice(String roleCode,String userId,String title,String content,String cover,String orderId,String dataId);

    /**
     *  动账通知
     * @param userId 用户ID
     * @param title 标题
     * @param content 内容
     * @return
     */
    boolean addBalanceNotice(String userId,String title,String content,String dataId);


    /**
     *  会员到期提醒
     * @param userId 用户ID
     * @param content 内容
     * @return
     */
    boolean addVipNotice(String userId,String content);

    /**
     *  验证码通知
     * @param userId 用户ID
     * @return
     */
    boolean addCodeNotice(String userId,String phone,String logId);

    /**
     *  发送平台私信
     * @param userId 用户ID
     * @param title 标题
     * @param content 内容
     * @return
     */
    boolean addPrivateNotice(String userId,String title,String content,String dataId);

}
