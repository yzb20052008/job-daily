package org.jeecg.modules.job.cms.service;

import org.jeecg.common.system.vo.SelectTreeModel;
import org.jeecg.modules.job.cms.entity.CmsCategory;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.exception.JeecgBootException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.util.List;

/**
 * @Description: 内容类别
 * @Author: qingkonglan
 * @Date:   2022-08-21
 * @Version: V1.0
 */
public interface ICmsCategoryService extends IService<CmsCategory> {

	/**根节点父ID的值*/
	public static final String ROOT_PID_VALUE = "0";
	
	/**树节点有子节点状态值*/
	public static final String HASCHILD = "1";
	
	/**树节点无子节点状态值*/
	public static final String NOCHILD = "0";

	/**
	 * 新增节点
	 *
	 * @param cmsCategory
	 */
	void addCmsCategory(CmsCategory cmsCategory);
	
	/**
   * 修改节点
   *
   * @param cmsCategory
   * @throws JeecgBootException
   */
	void updateCmsCategory(CmsCategory cmsCategory) throws JeecgBootException;
	
	/**
	 * 删除节点
	 *
	 * @param id
   * @throws JeecgBootException
	 */
	void deleteCmsCategory(String id) throws JeecgBootException;

	  /**
	   * 查询所有数据，无分页
	   *
	   * @param queryWrapper
	   * @return List<CmsCategory>
	   */
    List<CmsCategory> queryTreeListNoPage(QueryWrapper<CmsCategory> queryWrapper);

	/**
	 * 根据code查询子ID
	 *
	 * @param code
	 * @return String
	 */
	String queryChildIds(String code);


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
	 * 查询子分类
	 * @param code
	 * @return
	 */
	List<CmsCategory> getCategoryChilds(String code);

}
