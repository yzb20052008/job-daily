package org.jeecg.modules.job.ums.controller;

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
import org.jeecg.modules.job.ums.entity.UmsReferrerLog;
import org.jeecg.modules.job.ums.service.IUmsReferrerLogService;

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
 * @Description: 邀请记录
 * @Author: qingkonglan
 * @Date:   2023-09-03
 * @Version: V1.0
 */
@Api(tags="邀请记录")
@RestController
@RequestMapping("/ums/umsReferrerLog")
@Slf4j
public class UmsReferrerLogController extends JeecgController<UmsReferrerLog, IUmsReferrerLogService> {
	@Autowired
	private IUmsReferrerLogService umsReferrerLogService;
	
	/**
	 * 分页列表查询
	 *
	 * @param umsReferrerLog
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "邀请记录-分页列表查询")
	@ApiOperation(value="邀请记录-分页列表查询", notes="邀请记录-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<UmsReferrerLog>> queryPageList(UmsReferrerLog umsReferrerLog,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		Page<UmsReferrerLog> page = new Page<UmsReferrerLog>(pageNo, pageSize);
		IPage<UmsReferrerLog> pageList = umsReferrerLogService.getReferrerPageList(page, umsReferrerLog);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param umsReferrerLog
	 * @return
	 */
	@AutoLog(value = "邀请记录-添加")
	@ApiOperation(value="邀请记录-添加", notes="邀请记录-添加")
	@RequiresPermissions("ums:ums_referrer_log:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody UmsReferrerLog umsReferrerLog) {
		umsReferrerLogService.save(umsReferrerLog);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param umsReferrerLog
	 * @return
	 */
	@AutoLog(value = "邀请记录-编辑")
	@ApiOperation(value="邀请记录-编辑", notes="邀请记录-编辑")
	@RequiresPermissions("ums:ums_referrer_log:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody UmsReferrerLog umsReferrerLog) {
		umsReferrerLogService.updateById(umsReferrerLog);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "邀请记录-通过id删除")
	@ApiOperation(value="邀请记录-通过id删除", notes="邀请记录-通过id删除")
	@RequiresPermissions("ums:ums_referrer_log:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		umsReferrerLogService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "邀请记录-批量删除")
	@ApiOperation(value="邀请记录-批量删除", notes="邀请记录-批量删除")
	@RequiresPermissions("ums:ums_referrer_log:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.umsReferrerLogService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "邀请记录-通过id查询")
	@ApiOperation(value="邀请记录-通过id查询", notes="邀请记录-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<UmsReferrerLog> queryById(@RequestParam(name="id",required=true) String id) {
		UmsReferrerLog umsReferrerLog = umsReferrerLogService.getById(id);
		if(umsReferrerLog==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(umsReferrerLog);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param umsReferrerLog
    */
    @RequiresPermissions("ums:ums_referrer_log:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, UmsReferrerLog umsReferrerLog) {
        return super.exportXls(request, umsReferrerLog, UmsReferrerLog.class, "邀请记录");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("ums:ums_referrer_log:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, UmsReferrerLog.class);
    }

}
