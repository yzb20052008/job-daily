package org.jeecg.modules.job.integral.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.job.integral.entity.IntegralLog;
import org.jeecg.modules.job.integral.service.IIntegralLogService;

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
 * @Description: 积分日志
 * @Author: qingkonglan
 * @Date:   2024-07-30
 * @Version: V1.0
 */
@Api(tags="积分日志")
@RestController
@RequestMapping("/integral/integralLog")
@Slf4j
public class IntegralLogController extends JeecgController<IntegralLog, IIntegralLogService> {
	@Autowired
	private IIntegralLogService integralLogService;
	
	/**
	 * 分页列表查询
	 *
	 * @param integralLog
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "积分日志-分页列表查询")
	@ApiOperation(value="积分日志-分页列表查询", notes="积分日志-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<IntegralLog>> queryPageList(IntegralLog integralLog,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<IntegralLog> queryWrapper = QueryGenerator.initQueryWrapper(integralLog, req.getParameterMap());
		Page<IntegralLog> page = new Page<IntegralLog>(pageNo, pageSize);
		IPage<IntegralLog> pageList = integralLogService.getIntegralLogList(page, integralLog);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param integralLog
	 * @return
	 */
	@AutoLog(value = "积分日志-添加")
	@ApiOperation(value="积分日志-添加", notes="积分日志-添加")
	@RequiresPermissions("integral:integral_log:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody IntegralLog integralLog) {
		integralLogService.save(integralLog);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param integralLog
	 * @return
	 */
	@AutoLog(value = "积分日志-编辑")
	@ApiOperation(value="积分日志-编辑", notes="积分日志-编辑")
	@RequiresPermissions("integral:integral_log:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody IntegralLog integralLog) {
		integralLogService.updateById(integralLog);
		return Result.OK("编辑成功!");
	}

	 /**
	  * 修改用户积分信息
	  * @param jsonObject
	  * @return
	  */
	 @RequestMapping(value = "/editIntegral", method = {RequestMethod.PUT, RequestMethod.POST})
	 public Result<LoginUser> editIntegral(@RequestBody JSONObject jsonObject) {
		 Result<LoginUser> result = new Result<LoginUser>();
		 try {
			 int integral = jsonObject.getIntValue("integral");
			 String userId = jsonObject.getString("id");
			 if (oConvertUtils.isEmpty(userId)){
				 result.error500("参数错误");
			 	return result;
			 }
		 	 integralLogService.updateIntegral(userId,integral);
			 result.success("修改成功!");
		 } catch (Exception e) {
			 log.error(e.getMessage(), e);
			 result.error500("操作失败");
		 }
		 return result;
	 }

	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "积分日志-通过id删除")
	@ApiOperation(value="积分日志-通过id删除", notes="积分日志-通过id删除")
	@RequiresPermissions("integral:integral_log:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		integralLogService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "积分日志-批量删除")
	@ApiOperation(value="积分日志-批量删除", notes="积分日志-批量删除")
	@RequiresPermissions("integral:integral_log:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.integralLogService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "积分日志-通过id查询")
	@ApiOperation(value="积分日志-通过id查询", notes="积分日志-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<IntegralLog> queryById(@RequestParam(name="id",required=true) String id) {
		IntegralLog integralLog = integralLogService.getById(id);
		if(integralLog==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(integralLog);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param integralLog
    */
    @RequiresPermissions("integral:integral_log:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, IntegralLog integralLog) {
        return super.exportXls(request, integralLog, IntegralLog.class, "积分日志");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("integral:integral_log:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, IntegralLog.class);
    }

}
