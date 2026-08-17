package org.jeecg.modules.job.cms.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.job.cms.entity.CmsAd;
import org.jeecg.modules.job.cms.service.ICmsAdService;

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
 * @Description: 广告信息
 * @Author: qingkonglan
 * @Date:   2022-10-12
 * @Version: V1.0
 */
@Api(tags="广告信息")
@RestController
@RequestMapping("/cms/cmsAd")
@Slf4j
public class CmsAdController extends JeecgController<CmsAd, ICmsAdService> {
	@Autowired
	private ICmsAdService cmsAdService;
	
	/**
	 * 分页列表查询
	 *
	 * @param cmsAd
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "广告信息-分页列表查询")
	@ApiOperation(value="广告信息-分页列表查询", notes="广告信息-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<CmsAd>> queryPageList(CmsAd cmsAd,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<CmsAd> queryWrapper = QueryGenerator.initQueryWrapper(cmsAd, req.getParameterMap());
		queryWrapper.orderByAsc("sort");
		Page<CmsAd> page = new Page<CmsAd>(pageNo, pageSize);
		IPage<CmsAd> pageList = cmsAdService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param cmsAd
	 * @return
	 */
	@AutoLog(value = "广告信息-添加")
	@ApiOperation(value="广告信息-添加", notes="广告信息-添加")
	//@RequiresPermissions("org.jeecg.modules.demo:cms_ad:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody CmsAd cmsAd) {
		cmsAdService.save(cmsAd);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param cmsAd
	 * @return
	 */
	@AutoLog(value = "广告信息-编辑")
	@ApiOperation(value="广告信息-编辑", notes="广告信息-编辑")
	//@RequiresPermissions("org.jeecg.modules.demo:cms_ad:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody CmsAd cmsAd) {
		cmsAdService.updateById(cmsAd);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "广告信息-通过id删除")
	@ApiOperation(value="广告信息-通过id删除", notes="广告信息-通过id删除")
	//@RequiresPermissions("org.jeecg.modules.demo:cms_ad:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		cmsAdService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "广告信息-批量删除")
	@ApiOperation(value="广告信息-批量删除", notes="广告信息-批量删除")
	//@RequiresPermissions("org.jeecg.modules.demo:cms_ad:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.cmsAdService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "广告信息-通过id查询")
	@ApiOperation(value="广告信息-通过id查询", notes="广告信息-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<CmsAd> queryById(@RequestParam(name="id",required=true) String id) {
		CmsAd cmsAd = cmsAdService.getById(id);
		if(cmsAd==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(cmsAd);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param cmsAd
    */
    //@RequiresPermissions("org.jeecg.modules.demo:cms_ad:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, CmsAd cmsAd) {
        return super.exportXls(request, cmsAd, CmsAd.class, "广告信息");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("cms_ad:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, CmsAd.class);
    }

}
