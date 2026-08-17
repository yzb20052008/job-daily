package org.jeecg.modules.job.cms.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.job.cms.entity.CmsNotice;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.job.ums.entity.UmsReferrerLog;

/**
 * @Description: 系统通知
 * @Author: qingkonglan
 * @Date:   2022-09-26
 * @Version: V1.0
 */
public interface CmsNoticeMapper extends BaseMapper<CmsNotice> {

    /**
     *   统计未读量
     * @return
     */
    int getUnReadCount(@Param("roleCode") String roleCode,@Param("userId") String userId, @Param("type") Integer type);

    /**
     * 查询未读消息
     * @param page
     * @param userId 参数信息
     * @return
     */
    IPage<CmsNotice> getUnReadList(IPage page, @Param("userId") String userId);

}
