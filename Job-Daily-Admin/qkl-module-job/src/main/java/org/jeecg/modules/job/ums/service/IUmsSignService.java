package org.jeecg.modules.job.ums.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.job.ums.entity.UmsSign;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * @Description: 签到记录
 * @Author: qingkonglan
 * @Date:   2023-11-15
 * @Version: V1.0
 */
public interface IUmsSignService extends IService<UmsSign> {


    /**
     * 签到
     * @param userId
     * @return
     */
    boolean addSign(String userId);

    /**
     * 补签
     * @param userId
     * @return
     */
    boolean addReSign(String userId,String signDate);

    /**
     * 查询某天签到结果
     * @param date
     * @return
     */
    UmsSign getSign(String userId, String date);


    /**
     * 查询当月可用补签次数
     * @param userId
     * @return
     */
    Map<String,Object> getLeftReSignCount(String userId);

    /**
     *  分页查询签到情况
     * @param page
     * @param params
     * @return
     */
    IPage<UmsSign> getSignList(Page<UmsSign> page, UmsSign params);
}
