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
import org.jeecg.modules.job.ums.entity.UmsPayType;
import org.jeecg.modules.job.ums.service.IUmsPayTypeService;

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
 * @Description: 支付方式
 * @Author: qingkonglan
 * @Date:   2023-08-17
 * @Version: V1.0
 */
@Api(tags="支付方式")
@RestController
@RequestMapping("/ums/umsPayType")
@Slf4j
public class UmsPayTypeController extends JeecgController<UmsPayType, IUmsPayTypeService> {
	@Autowired
	private IUmsPayTypeService umsPayTypeService;
	
	/**
	 * 分页列表查询
	 *
	 * @param umsPayType
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "支付方式-分页列表查询")
	@ApiOperation(value="支付方式-分页列表查询", notes="支付方式-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<UmsPayType>> queryPageList(UmsPayType umsPayType,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<UmsPayType> queryWrapper = QueryGenerator.initQueryWrapper(umsPayType, req.getParameterMap());
		Page<UmsPayType> page = new Page<UmsPayType>(pageNo, pageSize);
		IPage<UmsPayType> pageList = umsPayTypeService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param umsPayType
	 * @return
	 */
	@AutoLog(value = "支付方式-添加")
	@ApiOperation(value="支付方式-添加", notes="支付方式-添加")
	@RequiresPermissions("ums:ums_pay_type:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody UmsPayType umsPayType) {
		umsPayTypeService.save(umsPayType);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param umsPayType
	 * @return
	 */
	@AutoLog(value = "支付方式-编辑")
	@ApiOperation(value="支付方式-编辑", notes="支付方式-编辑")
	@RequiresPermissions("ums:ums_pay_type:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody UmsPayType umsPayType) {
		umsPayTypeService.updateById(umsPayType);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "支付方式-通过id删除")
	@ApiOperation(value="支付方式-通过id删除", notes="支付方式-通过id删除")
	@RequiresPermissions("ums:ums_pay_type:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		umsPayTypeService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "支付方式-批量删除")
	@ApiOperation(value="支付方式-批量删除", notes="支付方式-批量删除")
	@RequiresPermissions("ums:ums_pay_type:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.umsPayTypeService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "支付方式-通过id查询")
	@ApiOperation(value="支付方式-通过id查询", notes="支付方式-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<UmsPayType> queryById(@RequestParam(name="id",required=true) String id) {
		UmsPayType umsPayType = umsPayTypeService.getById(id);
		if(umsPayType==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(umsPayType);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param umsPayType
    */
    @RequiresPermissions("ums:ums_pay_type:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, UmsPayType umsPayType) {
        return super.exportXls(request, umsPayType, UmsPayType.class, "支付方式");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("ums:ums_pay_type:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, UmsPayType.class);
    }

}
