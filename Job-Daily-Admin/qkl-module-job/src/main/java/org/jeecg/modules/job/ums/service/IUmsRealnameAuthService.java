package org.jeecg.modules.job.ums.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.job.ums.entity.UmsRealnameAuth;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.job.ums.entity.UmsSign;

/**
 * @Description: 实名认证
 * @Author: qingkonglan
 * @Date:   2024-07-30
 * @Version: V1.0
 */
public interface IUmsRealnameAuthService extends IService<UmsRealnameAuth> {

    /**
     * 添加/更新实名认证
     * @param realnameAuth
     * @return
     */
    boolean addOrUpdateRealNameAuth(UmsRealnameAuth realnameAuth);

    /**
     * 修改状态
     * @param id  ID
     * @param status  状态 ：0-待审核，1-通过，2-失败
     * @return
     */
    boolean updateStatus(String id,int status,String reason);

    /**
     * 查询用户实名认证情况
     * @param userId
     * @return
     */
    UmsRealnameAuth getRealNameAuth(String userId);

    /**
     *  分页查询
     * @param page
     * @param params
     * @return
     */
    IPage<UmsRealnameAuth> getRealNameAuthList(Page<UmsRealnameAuth> page, UmsRealnameAuth params);


}
