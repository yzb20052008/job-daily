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
import org.jeecg.modules.job.cms.entity.CmsNoticeRead;
import org.jeecg.modules.job.cms.service.ICmsNoticeReadService;

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
import org.apache.shiro.authz.annotation.RequiresPermissions;

 /**
 * @Description: 阅读记录
 * @Author: qingkonglan
 * @Date:   2023-12-03
 * @Version: V1.0
 */
@Api(tags="阅读记录")
@RestController
@RequestMapping("/cms/cmsNoticeRead")
@Slf4j
public class CmsNoticeReadController extends JeecgController<CmsNoticeRead, ICmsNoticeReadService> {
	@Autowired
	private ICmsNoticeReadService cmsNoticeReadService;
	
	/**
	 * 分页列表查询
	 *
	 * @param cmsNoticeRead
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "阅读记录-分页列表查询")
	@ApiOperation(value="阅读记录-分页列表查询", notes="阅读记录-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<CmsNoticeRead>> queryPageList(CmsNoticeRead cmsNoticeRead,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<CmsNoticeRead> queryWrapper = QueryGenerator.initQueryWrapper(cmsNoticeRead, req.getParameterMap());
		Page<CmsNoticeRead> page = new Page<CmsNoticeRead>(pageNo, pageSize);
		IPage<CmsNoticeRead> pageList = cmsNoticeReadService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param cmsNoticeRead
	 * @return
	 */
	@AutoLog(value = "阅读记录-添加")
	@ApiOperation(value="阅读记录-添加", notes="阅读记录-添加")
	@RequiresPermissions("cms:cms_notice_read:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody CmsNoticeRead cmsNoticeRead) {
		cmsNoticeReadService.save(cmsNoticeRead);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param cmsNoticeRead
	 * @return
	 */
	@AutoLog(value = "阅读记录-编辑")
	@ApiOperation(value="阅读记录-编辑", notes="阅读记录-编辑")
	@RequiresPermissions("cms:cms_notice_read:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody CmsNoticeRead cmsNoticeRead) {
		cmsNoticeReadService.updateById(cmsNoticeRead);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "阅读记录-通过id删除")
	@ApiOperation(value="阅读记录-通过id删除", notes="阅读记录-通过id删除")
	@RequiresPermissions("cms:cms_notice_read:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		cmsNoticeReadService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "阅读记录-批量删除")
	@ApiOperation(value="阅读记录-批量删除", notes="阅读记录-批量删除")
	@RequiresPermissions("cms:cms_notice_read:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.cmsNoticeReadService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "阅读记录-通过id查询")
	@ApiOperation(value="阅读记录-通过id查询", notes="阅读记录-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<CmsNoticeRead> queryById(@RequestParam(name="id",required=true) String id) {
		CmsNoticeRead cmsNoticeRead = cmsNoticeReadService.getById(id);
		if(cmsNoticeRead==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(cmsNoticeRead);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param cmsNoticeRead
    */
    @RequiresPermissions("cms:cms_notice_read:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, CmsNoticeRead cmsNoticeRead) {
        return super.exportXls(request, cmsNoticeRead, CmsNoticeRead.class, "阅读记录");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("cms:cms_notice_read:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, CmsNoticeRead.class);
    }

}
