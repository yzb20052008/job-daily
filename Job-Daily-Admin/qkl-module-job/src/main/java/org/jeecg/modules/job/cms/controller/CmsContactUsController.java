package org.jeecg.modules.job.cms.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.job.cms.entity.CmsContactUs;
import org.jeecg.modules.job.cms.service.ICmsContactUsService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

 /**
 * @Description: 联系我们
 * @Author: qingkonglan
 * @Date:   2022-12-21
 * @Version: V1.0
 */
@Api(tags="联系我们")
@RestController
@RequestMapping("/cms/cmsContactUs")
@Slf4j
public class CmsContactUsController extends JeecgController<CmsContactUs, ICmsContactUsService> {
	@Autowired
	private ICmsContactUsService cmsContactUsService;
	
	/**
	 * 分页列表查询
	 *
	 * @param cmsContactUs
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "联系我们-分页列表查询")
	@ApiOperation(value="联系我们-分页列表查询", notes="联系我们-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<CmsContactUs>> queryPageList(CmsContactUs cmsContactUs,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<CmsContactUs> queryWrapper = QueryGenerator.initQueryWrapper(cmsContactUs, req.getParameterMap());
		Page<CmsContactUs> page = new Page<CmsContactUs>(pageNo, pageSize);
		IPage<CmsContactUs> pageList = cmsContactUsService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param cmsContactUs
	 * @return
	 */
	@AutoLog(value = "联系我们-添加")
	@ApiOperation(value="联系我们-添加", notes="联系我们-添加")
	//@RequiresPermissions("org.jeecg.modules.job:cms_contact_us:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody CmsContactUs cmsContactUs) {
		cmsContactUsService.save(cmsContactUs);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param cmsContactUs
	 * @return
	 */
	@AutoLog(value = "联系我们-编辑")
	@ApiOperation(value="联系我们-编辑", notes="联系我们-编辑")
	//@RequiresPermissions("org.jeecg.modules.job:cms_contact_us:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody CmsContactUs cmsContactUs) {
		cmsContactUsService.updateById(cmsContactUs);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "联系我们-通过id删除")
	@ApiOperation(value="联系我们-通过id删除", notes="联系我们-通过id删除")
	//@RequiresPermissions("org.jeecg.modules.job:cms_contact_us:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		cmsContactUsService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "联系我们-批量删除")
	@ApiOperation(value="联系我们-批量删除", notes="联系我们-批量删除")
	//@RequiresPermissions("org.jeecg.modules.job:cms_contact_us:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.cmsContactUsService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "联系我们-通过id查询")
	@ApiOperation(value="联系我们-通过id查询", notes="联系我们-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<CmsContactUs> queryById(@RequestParam(name="id",required=true) String id) {
		CmsContactUs cmsContactUs = cmsContactUsService.getById(id);
		if(cmsContactUs==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(cmsContactUs);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param cmsContactUs
    */
    //@RequiresPermissions("org.jeecg.modules.job:cms_contact_us:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, CmsContactUs cmsContactUs) {
        return super.exportXls(request, cmsContactUs, CmsContactUs.class, "联系我们");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("cms_contact_us:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, CmsContactUs.class);
    }

}
