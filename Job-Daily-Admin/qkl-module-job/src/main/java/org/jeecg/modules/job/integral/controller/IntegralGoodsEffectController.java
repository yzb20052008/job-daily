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
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.job.integral.entity.IntegralGoodsEffect;
import org.jeecg.modules.job.integral.service.IIntegralGoodsEffectService;

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
 * @Description: 道具时效
 * @Author: qingkonglan
 * @Date:   2024-07-30
 * @Version: V1.0
 */
@Api(tags="道具时效")
@RestController
@RequestMapping("/integral/integralGoodsEffect")
@Slf4j
public class IntegralGoodsEffectController extends JeecgController<IntegralGoodsEffect, IIntegralGoodsEffectService> {
	@Autowired
	private IIntegralGoodsEffectService integralGoodsEffectService;
	
	/**
	 * 分页列表查询
	 *
	 * @param integralGoodsEffect
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "道具时效-分页列表查询")
	@ApiOperation(value="道具时效-分页列表查询", notes="道具时效-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<IntegralGoodsEffect>> queryPageList(IntegralGoodsEffect integralGoodsEffect,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<IntegralGoodsEffect> queryWrapper = QueryGenerator.initQueryWrapper(integralGoodsEffect, req.getParameterMap());
		Page<IntegralGoodsEffect> page = new Page<IntegralGoodsEffect>(pageNo, pageSize);
		IPage<IntegralGoodsEffect> pageList = integralGoodsEffectService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param integralGoodsEffect
	 * @return
	 */
	@AutoLog(value = "道具时效-添加")
	@ApiOperation(value="道具时效-添加", notes="道具时效-添加")
	@RequiresPermissions("integral:integral_goods_effect:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody IntegralGoodsEffect integralGoodsEffect) {
		integralGoodsEffectService.save(integralGoodsEffect);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param integralGoodsEffect
	 * @return
	 */
	@AutoLog(value = "道具时效-编辑")
	@ApiOperation(value="道具时效-编辑", notes="道具时效-编辑")
	@RequiresPermissions("integral:integral_goods_effect:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody IntegralGoodsEffect integralGoodsEffect) {
		integralGoodsEffectService.updateById(integralGoodsEffect);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "道具时效-通过id删除")
	@ApiOperation(value="道具时效-通过id删除", notes="道具时效-通过id删除")
	@RequiresPermissions("integral:integral_goods_effect:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		integralGoodsEffectService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "道具时效-批量删除")
	@ApiOperation(value="道具时效-批量删除", notes="道具时效-批量删除")
	@RequiresPermissions("integral:integral_goods_effect:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.integralGoodsEffectService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "道具时效-通过id查询")
	@ApiOperation(value="道具时效-通过id查询", notes="道具时效-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<IntegralGoodsEffect> queryById(@RequestParam(name="id",required=true) String id) {
		IntegralGoodsEffect integralGoodsEffect = integralGoodsEffectService.getById(id);
		if(integralGoodsEffect==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(integralGoodsEffect);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param integralGoodsEffect
    */
    @RequiresPermissions("integral:integral_goods_effect:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, IntegralGoodsEffect integralGoodsEffect) {
        return super.exportXls(request, integralGoodsEffect, IntegralGoodsEffect.class, "道具时效");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("integral:integral_goods_effect:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, IntegralGoodsEffect.class);
    }

}
