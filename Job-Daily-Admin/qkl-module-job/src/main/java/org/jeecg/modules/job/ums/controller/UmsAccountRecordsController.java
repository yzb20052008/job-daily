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
import org.jeecg.modules.job.ums.entity.UmsAccountRecords;
import org.jeecg.modules.job.ums.service.IUmsAccountRecordsService;

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
 * @Description: 账号流水
 * @Author: qingkonglan
 * @Date:   2022-12-23
 * @Version: V1.0
 */
@Api(tags="账号流水")
@RestController
@RequestMapping("/ums/umsAccountRecords")
@Slf4j
public class UmsAccountRecordsController extends JeecgController<UmsAccountRecords, IUmsAccountRecordsService> {
	@Autowired
	private IUmsAccountRecordsService umsAccountRecordsService;
	
	/**
	 * 分页列表查询
	 *
	 * @param umsAccountRecords
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "账号流水-分页列表查询")
	@ApiOperation(value="账号流水-分页列表查询", notes="账号流水-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<UmsAccountRecords>> queryPageList(UmsAccountRecords umsAccountRecords,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<UmsAccountRecords> queryWrapper = QueryGenerator.initQueryWrapper(umsAccountRecords, req.getParameterMap());
		Page<UmsAccountRecords> page = new Page<UmsAccountRecords>(pageNo, pageSize);
		IPage<UmsAccountRecords> pageList = umsAccountRecordsService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param umsAccountRecords
	 * @return
	 */
	@AutoLog(value = "账号流水-添加")
	@ApiOperation(value="账号流水-添加", notes="账号流水-添加")
	//@RequiresPermissions("org.jeecg.modules.job:ums_account_records:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody UmsAccountRecords umsAccountRecords) {
		umsAccountRecordsService.save(umsAccountRecords);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param umsAccountRecords
	 * @return
	 */
	@AutoLog(value = "账号流水-编辑")
	@ApiOperation(value="账号流水-编辑", notes="账号流水-编辑")
	//@RequiresPermissions("org.jeecg.modules.job:ums_account_records:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody UmsAccountRecords umsAccountRecords) {
		umsAccountRecordsService.updateById(umsAccountRecords);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "账号流水-通过id删除")
	@ApiOperation(value="账号流水-通过id删除", notes="账号流水-通过id删除")
	//@RequiresPermissions("org.jeecg.modules.job:ums_account_records:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		umsAccountRecordsService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "账号流水-批量删除")
	@ApiOperation(value="账号流水-批量删除", notes="账号流水-批量删除")
	//@RequiresPermissions("org.jeecg.modules.job:ums_account_records:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.umsAccountRecordsService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "账号流水-通过id查询")
	@ApiOperation(value="账号流水-通过id查询", notes="账号流水-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<UmsAccountRecords> queryById(@RequestParam(name="id",required=true) String id) {
		UmsAccountRecords umsAccountRecords = umsAccountRecordsService.getById(id);
		if(umsAccountRecords==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(umsAccountRecords);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param umsAccountRecords
    */
    //@RequiresPermissions("org.jeecg.modules.job:ums_account_records:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, UmsAccountRecords umsAccountRecords) {
        return super.exportXls(request, umsAccountRecords, UmsAccountRecords.class, "账号流水");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("ums_account_records:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, UmsAccountRecords.class);
    }

}
