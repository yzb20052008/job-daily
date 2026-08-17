package org.jeecg.modules.job.job.service;

import org.jeecg.common.system.vo.SelectTreeModel;
import org.jeecg.modules.job.job.entity.JobTypes;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.exception.JeecgBootException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.util.List;

/**
 * @Description: 工种信息
 * @Author: qingkonglan
 * @Date:   2024-07-30
 * @Version: V1.0
 */
public interface IJobTypesService extends IService<JobTypes> {

	/**根节点父ID的值*/
	public static final String ROOT_PID_VALUE = "0";
	
	/**树节点有子节点状态值*/
	public static final String HASCHILD = "1";
	
	/**树节点无子节点状态值*/
	public static final String NOCHILD = "0";

	/**
	 * 新增节点
	 *
	 * @param jobTypes
	 */
	JobTypes addJobTypes(JobTypes jobTypes);

	/**
	 * 根据父类ID查询编码和层级
	 * @param pid
	 * @return
	 */
	JobTypes getTypeCodeAndLevel(String pid);

	
	/**
   * 修改节点
   *
   * @param jobTypes
   * @throws JeecgBootException
   */
	void updateJobTypes(JobTypes jobTypes) throws JeecgBootException;
	
	/**
	 * 删除节点
	 *
	 * @param id
   * @throws JeecgBootException
	 */
	void deleteJobTypes(String id) throws JeecgBootException;

	  /**
	   * 查询所有数据，无分页
	   *
	   * @param queryWrapper
	   * @return List<JobTypes>
	   */
    List<JobTypes> queryTreeListNoPage(QueryWrapper<JobTypes> queryWrapper);

	/**
	 * 【vue3专用】根据父级编码加载分类字典的数据
	 *
	 * @param parentCode
	 * @return
	 */
	List<SelectTreeModel> queryListByCode(String parentCode);

	/**
	 * 【vue3专用】根据pid查询子节点集合
	 *
	 * @param pid
	 * @return
	 */
	List<SelectTreeModel> queryListByPid(String pid);


	/**
	 * 根据类型id获取类型名称
	 * @param ids 逗号分隔
	 * @return 逗号分隔
	 */
	public JobTypes getTypeNameCodes(String ids);

}
