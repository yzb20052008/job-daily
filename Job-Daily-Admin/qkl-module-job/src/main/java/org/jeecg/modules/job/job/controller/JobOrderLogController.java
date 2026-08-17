package org.jeecg.modules.job.job.controller;

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
import org.jeecg.modules.job.job.entity.JobOrderLog;
import org.jeecg.modules.job.job.service.IJobOrderLogService;

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
 * @Description: 订单操作记录
 * * @Author: qingkonglan
 * @Date:   2024-08-26
 * @Version: V1.0
 */
@Api(tags="订单操作记录")
@RestController
@RequestMapping("/job/jobOrderLog")
@Slf4j
public class JobOrderLogController extends JeecgController<JobOrderLog, IJobOrderLogService> {
	@Autowired
	private IJobOrderLogService jobOrderLogService;
	
	/**
	 * 分页列表查询
	 *
	 * @param jobOrderLog
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "订单操作记录-分页列表查询")
	@ApiOperation(value="订单操作记录-分页列表查询", notes="订单操作记录-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<JobOrderLog>> queryPageList(JobOrderLog jobOrderLog,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<JobOrderLog> queryWrapper = QueryGenerator.initQueryWrapper(jobOrderLog, req.getParameterMap());
		Page<JobOrderLog> page = new Page<JobOrderLog>(pageNo, pageSize);
		IPage<JobOrderLog> pageList = jobOrderLogService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param jobOrderLog
	 * @return
	 */
	@AutoLog(value = "订单操作记录-添加")
	@ApiOperation(value="订单操作记录-添加", notes="订单操作记录-添加")
	@RequiresPermissions("job:job_order_log:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody JobOrderLog jobOrderLog) {
		jobOrderLogService.save(jobOrderLog);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param jobOrderLog
	 * @return
	 */
	@AutoLog(value = "订单操作记录-编辑")
	@ApiOperation(value="订单操作记录-编辑", notes="订单操作记录-编辑")
	@RequiresPermissions("job:job_order_log:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody JobOrderLog jobOrderLog) {
		jobOrderLogService.updateById(jobOrderLog);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "订单操作记录-通过id删除")
	@ApiOperation(value="订单操作记录-通过id删除", notes="订单操作记录-通过id删除")
	@RequiresPermissions("job:job_order_log:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		jobOrderLogService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "订单操作记录-批量删除")
	@ApiOperation(value="订单操作记录-批量删除", notes="订单操作记录-批量删除")
	@RequiresPermissions("job:job_order_log:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.jobOrderLogService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "订单操作记录-通过id查询")
	@ApiOperation(value="订单操作记录-通过id查询", notes="订单操作记录-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<JobOrderLog> queryById(@RequestParam(name="id",required=true) String id) {
		JobOrderLog jobOrderLog = jobOrderLogService.getById(id);
		if(jobOrderLog==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(jobOrderLog);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param jobOrderLog
    */
    @RequiresPermissions("job:job_order_log:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, JobOrderLog jobOrderLog) {
        return super.exportXls(request, jobOrderLog, JobOrderLog.class, "订单操作记录");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("job:job_order_log:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, JobOrderLog.class);
    }

}
