package org.jeecg.modules.job.ums.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.job.ums.entity.UmsSign;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 签到记录
 * @Author: qingkonglan
 * @Date:   2023-11-15
 * @Version: V1.0
 */
public interface UmsSignMapper extends BaseMapper<UmsSign> {

    /**
     *  分页查询签到情况
     * @param page
     * @param params
     * @return
     */
    IPage<UmsSign> getSignList(IPage page, @Param("paramCondition")UmsSign params);

}
