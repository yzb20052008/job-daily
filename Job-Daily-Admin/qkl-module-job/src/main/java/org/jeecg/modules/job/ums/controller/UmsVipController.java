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
import org.jeecg.modules.job.ums.entity.UmsVip;
import org.jeecg.modules.job.ums.service.IUmsVipService;

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
 * @Description: VIP信息
 * @Author: qingkonglan
 * @Date:   2022-12-18
 * @Version: V1.0
 */
@Api(tags="VIP信息")
@RestController
@RequestMapping("/ums/umsVip")
@Slf4j
public class UmsVipController extends JeecgController<UmsVip, IUmsVipService> {
	@Autowired
	private IUmsVipService umsVipService;
	
	/**
	 * 分页列表查询
	 *
	 * @param umsVip
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "VIP信息-分页列表查询")
	@ApiOperation(value="VIP信息-分页列表查询", notes="VIP信息-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<UmsVip>> queryPageList(UmsVip umsVip,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<UmsVip> queryWrapper = QueryGenerator.initQueryWrapper(umsVip, req.getParameterMap());
		Page<UmsVip> page = new Page<UmsVip>(pageNo, pageSize);
		IPage<UmsVip> pageList = umsVipService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param umsVip
	 * @return
	 */
	@AutoLog(value = "VIP信息-添加")
	@ApiOperation(value="VIP信息-添加", notes="VIP信息-添加")
	//@RequiresPermissions("org.jeecg.modules.job:ums_vip:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody UmsVip umsVip) {
		umsVipService.save(umsVip);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param umsVip
	 * @return
	 */
	@AutoLog(value = "VIP信息-编辑")
	@ApiOperation(value="VIP信息-编辑", notes="VIP信息-编辑")
	//@RequiresPermissions("org.jeecg.modules.job:ums_vip:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody UmsVip umsVip) {
		umsVipService.updateById(umsVip);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "VIP信息-通过id删除")
	@ApiOperation(value="VIP信息-通过id删除", notes="VIP信息-通过id删除")
	//@RequiresPermissions("org.jeecg.modules.job:ums_vip:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		umsVipService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "VIP信息-批量删除")
	@ApiOperation(value="VIP信息-批量删除", notes="VIP信息-批量删除")
	//@RequiresPermissions("org.jeecg.modules.job:ums_vip:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.umsVipService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "VIP信息-通过id查询")
	@ApiOperation(value="VIP信息-通过id查询", notes="VIP信息-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<UmsVip> queryById(@RequestParam(name="id",required=true) String id) {
		UmsVip umsVip = umsVipService.getById(id);
		if(umsVip==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(umsVip);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param umsVip
    */
    //@RequiresPermissions("org.jeecg.modules.job:ums_vip:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, UmsVip umsVip) {
        return super.exportXls(request, umsVip, UmsVip.class, "VIP信息");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("ums_vip:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, UmsVip.class);
    }

}
