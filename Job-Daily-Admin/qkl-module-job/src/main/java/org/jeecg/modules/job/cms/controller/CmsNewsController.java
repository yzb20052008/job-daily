package org.jeecg.modules.job.cms.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.job.cms.entity.CmsNews;
import org.jeecg.modules.job.cms.service.ICmsNewsService;

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
 * @Description: 社科动态
 * @Author: qingkonglan
 * @Date:   2022-10-19
 * @Version: V1.0
 */
@Api(tags="社科动态")
@RestController
@RequestMapping("/cms/cmsNews")
@Slf4j
public class CmsNewsController extends JeecgController<CmsNews, ICmsNewsService> {
	@Autowired
	private ICmsNewsService cmsNewsService;
	
	/**
	 * 分页列表查询
	 *
	 * @param cmsNews
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "社科动态-分页列表查询")
	@ApiOperation(value="社科动态-分页列表查询", notes="社科动态-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<CmsNews>> queryPageList(CmsNews cmsNews,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<CmsNews> queryWrapper = QueryGenerator.initQueryWrapper(cmsNews, req.getParameterMap());
		Page<CmsNews> page = new Page<CmsNews>(pageNo, pageSize);
		IPage<CmsNews> pageList = cmsNewsService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param cmsNews
	 * @return
	 */
	@AutoLog(value = "社科动态-添加")
	@ApiOperation(value="社科动态-添加", notes="社科动态-添加")
	//@RequiresPermissions("org.jeecg.modules.demo:cms_news:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody CmsNews cmsNews) {
		cmsNewsService.save(cmsNews);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param cmsNews
	 * @return
	 */
	@AutoLog(value = "社科动态-编辑")
	@ApiOperation(value="社科动态-编辑", notes="社科动态-编辑")
	//@RequiresPermissions("org.jeecg.modules.demo:cms_news:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody CmsNews cmsNews) {
		cmsNewsService.updateById(cmsNews);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "社科动态-通过id删除")
	@ApiOperation(value="社科动态-通过id删除", notes="社科动态-通过id删除")
	//@RequiresPermissions("org.jeecg.modules.demo:cms_news:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		cmsNewsService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "社科动态-批量删除")
	@ApiOperation(value="社科动态-批量删除", notes="社科动态-批量删除")
	//@RequiresPermissions("org.jeecg.modules.demo:cms_news:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.cmsNewsService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "社科动态-通过id查询")
	@ApiOperation(value="社科动态-通过id查询", notes="社科动态-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<CmsNews> queryById(@RequestParam(name="id",required=true) String id) {
		CmsNews cmsNews = cmsNewsService.getById(id);
		if(cmsNews==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(cmsNews);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param cmsNews
    */
    //@RequiresPermissions("org.jeecg.modules.demo:cms_news:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, CmsNews cmsNews) {
        return super.exportXls(request, cmsNews, CmsNews.class, "社科动态");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("cms_news:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, CmsNews.class);
    }

}
