package org.jeecg.modules.job.ums.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.job.ums.entity.UmsMemberRole;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * @Description: 会员公司关系
 * @Author: qingkonglan
 * @Date:   2022-12-18
 * @Version: V1.0
 */
public interface IUmsMemberRoleService extends IService<UmsMemberRole> {

    /**
     * 添加或更新会员公司关系
     * @param role
     * @return
     */
    boolean saveOrUpdateRole(UmsMemberRole role);

    /**
     * 添加公司招聘成员
     * @param phone 联系方式
     * @param name 真实姓名
     * @param postName 担任职位
     * @return
     */
    boolean addCompanyUser(String phone,String name,String postName,String companyId);


    /**
     * 根据用户ID查询会员公司
     * @param memberId
     * @return
     */
    UmsMemberRole getMemberRole(String memberId);

    /**
     * 根据用户ID查询关联公司ID
     * @param memberId
     * @return
     */
    String getCompanyId(String memberId);


    /**
     * 分页查询公司招聘人员列表
     * @param page
     * @param paramCondition 参数信息
     * @return
     */
    IPage<Map<String, Object>> getMemeberRoleList(Page<UmsMemberRole> page, UmsMemberRole paramCondition);

    /**
     * 根据公司ID查询公司管理员
     * @param companyId
     * @return
     */
    UmsMemberRole getCompanyManager(String companyId);

}
