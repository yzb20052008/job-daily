package org.jeecg.modules.job.cms.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.job.cms.entity.CmsType;
import org.jeecg.modules.job.cms.service.ICmsTypeService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

 /**
 * @Description: 首页分类
 * @Author: qingkonglan
 * @Date:   2022-10-21
 * @Version: V1.0
 */
@Api(tags="首页分类")
@RestController
@RequestMapping("/cms/cmsType")
@Slf4j
public class CmsTypeController extends JeecgController<CmsType, ICmsTypeService> {
	@Autowired
	private ICmsTypeService cmsTypeService;
	
	/**
	 * 分页列表查询
	 *
	 * @param cmsType
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "首页分类-分页列表查询")
	@ApiOperation(value="首页分类-分页列表查询", notes="首页分类-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<CmsType>> queryPageList(CmsType cmsType,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<CmsType> queryWrapper = QueryGenerator.initQueryWrapper(cmsType, req.getParameterMap());
		queryWrapper.orderByAsc("status");
		queryWrapper.orderByAsc("sort");
		Page<CmsType> page = new Page<CmsType>(pageNo, pageSize);
		IPage<CmsType> pageList = cmsTypeService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param cmsType
	 * @return
	 */
	@AutoLog(value = "首页分类-添加")
	@ApiOperation(value="首页分类-添加", notes="首页分类-添加")
	//@RequiresPermissions("org.jeecg.modules.demo:cms_type:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody CmsType cmsType) {
		cmsTypeService.save(cmsType);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param cmsType
	 * @return
	 */
	@AutoLog(value = "首页分类-编辑")
	@ApiOperation(value="首页分类-编辑", notes="首页分类-编辑")
	//@RequiresPermissions("org.jeecg.modules.demo:cms_type:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody CmsType cmsType) {
		cmsTypeService.updateById(cmsType);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "首页分类-通过id删除")
	@ApiOperation(value="首页分类-通过id删除", notes="首页分类-通过id删除")
	//@RequiresPermissions("org.jeecg.modules.demo:cms_type:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		cmsTypeService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "首页分类-批量删除")
	@ApiOperation(value="首页分类-批量删除", notes="首页分类-批量删除")
	//@RequiresPermissions("org.jeecg.modules.demo:cms_type:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.cmsTypeService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "首页分类-通过id查询")
	@ApiOperation(value="首页分类-通过id查询", notes="首页分类-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<CmsType> queryById(@RequestParam(name="id",required=true) String id) {
		CmsType cmsType = cmsTypeService.getById(id);
		if(cmsType==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(cmsType);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param cmsType
    */
    //@RequiresPermissions("org.jeecg.modules.demo:cms_type:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, CmsType cmsType) {
        return super.exportXls(request, cmsType, CmsType.class, "首页分类");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("cms_type:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, CmsType.class);
    }

}
