package org.jeecg.modules.job.ums.mapper;


import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.job.ums.entity.UmsReferrerLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description: 邀请记录
 * @Author: qingkonglan
 * @Date:   2023-09-03
 * @Version: V1.0
 */
public interface UmsReferrerLogMapper extends BaseMapper<UmsReferrerLog> {


    /**
     * 分页查询浏览记录
     * @param page
     * @param paramCondition 参数信息
     * @return
     */
    IPage<UmsReferrerLog> getReferrerPageList(IPage page, @Param("paramCondition") UmsReferrerLog paramCondition);


    /**
     *   统计邀请数量
     * @param dayStart
     * @param dayEnd
     * @return
     */
    UmsReferrerLog getReferrerCount(@Param("memberId") String memberId,@Param("dayStart") String dayStart, @Param("dayEnd") String dayEnd);

}
