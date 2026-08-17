package org.jeecg.modules.job.ums.mapper;

import java.util.Map;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.job.ums.entity.UmsMemberRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 会员公司关系
 * @Author: qingkonglan
 * @Date:   2022-12-18
 * @Version: V1.0
 */
public interface UmsMemberRoleMapper extends BaseMapper<UmsMemberRole> {


    /**
     * 分页查询公司招聘人员列表
     * @param page
     * @param paramCondition 参数信息
     * @return
     */
    IPage<Map<String, Object>> getMemeberRoleList(IPage page, @Param("paramCondition") UmsMemberRole paramCondition);

}
