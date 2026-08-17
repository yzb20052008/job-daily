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
import org.jeecg.modules.job.ums.entity.UmsParamLimit;
import org.jeecg.modules.job.ums.service.IUmsParamLimitService;

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
 * @Description: 用户阈值
 * @Author: qingkonglan
 * @Date:   2023-11-20
 * @Version: V1.0
 */
@Api(tags="用户阈值")
@RestController
@RequestMapping("/ums/umsParamLimit")
@Slf4j
public class UmsParamLimitController extends JeecgController<UmsParamLimit, IUmsParamLimitService> {
	@Autowired
	private IUmsParamLimitService umsParamLimitService;
	
	/**
	 * 分页列表查询
	 *
	 * @param umsParamLimit
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "用户阈值-分页列表查询")
	@ApiOperation(value="用户阈值-分页列表查询", notes="用户阈值-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<UmsParamLimit>> queryPageList(UmsParamLimit umsParamLimit,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<UmsParamLimit> queryWrapper = QueryGenerator.initQueryWrapper(umsParamLimit, req.getParameterMap());
		Page<UmsParamLimit> page = new Page<UmsParamLimit>(pageNo, pageSize);
		IPage<UmsParamLimit> pageList = umsParamLimitService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param umsParamLimit
	 * @return
	 */
	@AutoLog(value = "用户阈值-添加")
	@ApiOperation(value="用户阈值-添加", notes="用户阈值-添加")
	@RequiresPermissions("ums:ums_param_limit:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody UmsParamLimit umsParamLimit) {
		umsParamLimitService.save(umsParamLimit);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param umsParamLimit
	 * @return
	 */
	@AutoLog(value = "用户阈值-编辑")
	@ApiOperation(value="用户阈值-编辑", notes="用户阈值-编辑")
	@RequiresPermissions("ums:ums_param_limit:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody UmsParamLimit umsParamLimit) {
		umsParamLimitService.updateById(umsParamLimit);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "用户阈值-通过id删除")
	@ApiOperation(value="用户阈值-通过id删除", notes="用户阈值-通过id删除")
	@RequiresPermissions("ums:ums_param_limit:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		umsParamLimitService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "用户阈值-批量删除")
	@ApiOperation(value="用户阈值-批量删除", notes="用户阈值-批量删除")
	@RequiresPermissions("ums:ums_param_limit:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.umsParamLimitService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "用户阈值-通过id查询")
	@ApiOperation(value="用户阈值-通过id查询", notes="用户阈值-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<UmsParamLimit> queryById(@RequestParam(name="id",required=true) String id) {
		UmsParamLimit umsParamLimit = umsParamLimitService.getById(id);
		if(umsParamLimit==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(umsParamLimit);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param umsParamLimit
    */
    @RequiresPermissions("ums:ums_param_limit:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, UmsParamLimit umsParamLimit) {
        return super.exportXls(request, umsParamLimit, UmsParamLimit.class, "用户阈值");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("ums:ums_param_limit:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, UmsParamLimit.class);
    }

}
