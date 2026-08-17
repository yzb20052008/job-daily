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
import org.jeecg.modules.job.ums.entity.UmsUserVip;
import org.jeecg.modules.job.ums.service.IUmsUserVipService;

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
 * @Description: 用户会员
 * @Author: qingkonglan
 * @Date:   2023-09-23
 * @Version: V1.0
 */
@Api(tags="用户会员")
@RestController
@RequestMapping("/ums/umsUserVip")
@Slf4j
public class UmsUserVipController extends JeecgController<UmsUserVip, IUmsUserVipService> {
	@Autowired
	private IUmsUserVipService umsUserVipService;
	
	/**
	 * 分页列表查询
	 *
	 * @param umsUserVip
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "用户会员-分页列表查询")
	@ApiOperation(value="用户会员-分页列表查询", notes="用户会员-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<UmsUserVip>> queryPageList(UmsUserVip umsUserVip,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<UmsUserVip> queryWrapper = QueryGenerator.initQueryWrapper(umsUserVip, req.getParameterMap());
		Page<UmsUserVip> page = new Page<UmsUserVip>(pageNo, pageSize);
		IPage<UmsUserVip> pageList = umsUserVipService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param umsUserVip
	 * @return
	 */
	@AutoLog(value = "用户会员-添加")
	@ApiOperation(value="用户会员-添加", notes="用户会员-添加")
	@RequiresPermissions("ums:ums_user_vip:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody UmsUserVip umsUserVip) {
		umsUserVipService.save(umsUserVip);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param umsUserVip
	 * @return
	 */
	@AutoLog(value = "用户会员-编辑")
	@ApiOperation(value="用户会员-编辑", notes="用户会员-编辑")
	@RequiresPermissions("ums:ums_user_vip:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody UmsUserVip umsUserVip) {
		umsUserVipService.updateById(umsUserVip);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "用户会员-通过id删除")
	@ApiOperation(value="用户会员-通过id删除", notes="用户会员-通过id删除")
	@RequiresPermissions("ums:ums_user_vip:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		umsUserVipService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "用户会员-批量删除")
	@ApiOperation(value="用户会员-批量删除", notes="用户会员-批量删除")
	@RequiresPermissions("ums:ums_user_vip:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.umsUserVipService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "用户会员-通过id查询")
	@ApiOperation(value="用户会员-通过id查询", notes="用户会员-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<UmsUserVip> queryById(@RequestParam(name="id",required=true) String id) {
		UmsUserVip umsUserVip = umsUserVipService.getById(id);
		if(umsUserVip==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(umsUserVip);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param umsUserVip
    */
    @RequiresPermissions("ums:ums_user_vip:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, UmsUserVip umsUserVip) {
        return super.exportXls(request, umsUserVip, UmsUserVip.class, "用户会员");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("ums:ums_user_vip:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, UmsUserVip.class);
    }

}
