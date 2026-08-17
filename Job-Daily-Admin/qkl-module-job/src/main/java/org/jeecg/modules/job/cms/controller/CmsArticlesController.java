package org.jeecg.modules.job.cms.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.job.cms.entity.CmsArticles;
import org.jeecg.modules.job.cms.service.ICmsArticlesService;

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
 * @Description: 文章内容
 * @Author: qingkonglan
 * @Date:   2022-08-21
 * @Version: V1.0
 */
@Api(tags="文章内容")
@RestController
@RequestMapping("/cms/cmsArticles")
@Slf4j
public class CmsArticlesController extends JeecgController<CmsArticles, ICmsArticlesService> {
	@Autowired
	private ICmsArticlesService cmsArticlesService;
	
	/**
	 * 分页列表查询
	 *
	 * @param cmsArticles
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "文章内容-分页列表查询")
	@ApiOperation(value="文章内容-分页列表查询", notes="文章内容-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<CmsArticles>> queryPageList(CmsArticles cmsArticles,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<CmsArticles> queryWrapper = QueryGenerator.initQueryWrapper(cmsArticles, req.getParameterMap());
		Page<CmsArticles> page = new Page<CmsArticles>(pageNo, pageSize);
		IPage<CmsArticles> pageList = cmsArticlesService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param cmsArticles
	 * @return
	 */
	@AutoLog(value = "文章内容-添加")
	@ApiOperation(value="文章内容-添加", notes="文章内容-添加")
	//@RequiresPermissions("org.jeecg.modules:cms_articles:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody CmsArticles cmsArticles) {
		cmsArticlesService.save(cmsArticles);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param cmsArticles
	 * @return
	 */
	@AutoLog(value = "文章内容-编辑")
	@ApiOperation(value="文章内容-编辑", notes="文章内容-编辑")
	//@RequiresPermissions("org.jeecg.modules:cms_articles:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody CmsArticles cmsArticles) {
		cmsArticlesService.updateById(cmsArticles);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "文章内容-通过id删除")
	@ApiOperation(value="文章内容-通过id删除", notes="文章内容-通过id删除")
	//@RequiresPermissions("org.jeecg.modules:cms_articles:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		cmsArticlesService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "文章内容-批量删除")
	@ApiOperation(value="文章内容-批量删除", notes="文章内容-批量删除")
	//@RequiresPermissions("org.jeecg.modules:cms_articles:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.cmsArticlesService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "文章内容-通过id查询")
	@ApiOperation(value="文章内容-通过id查询", notes="文章内容-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<CmsArticles> queryById(@RequestParam(name="id",required=true) String id) {
		CmsArticles cmsArticles = cmsArticlesService.getById(id);
		if(cmsArticles==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(cmsArticles);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param cmsArticles
    */
    //@RequiresPermissions("org.jeecg.modules:cms_articles:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, CmsArticles cmsArticles) {
        return super.exportXls(request, cmsArticles, CmsArticles.class, "文章内容");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("cms_articles:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, CmsArticles.class);
    }

}
