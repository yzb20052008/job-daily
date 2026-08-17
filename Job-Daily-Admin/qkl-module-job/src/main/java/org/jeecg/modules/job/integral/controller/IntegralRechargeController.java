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
import org.jeecg.modules.job.integral.entity.IntegralRecharge;
import org.jeecg.modules.job.integral.service.IIntegralRechargeService;

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
 * @Description: 积分充值记录
 * * @Author: qingkonglan
 * @Date:   2024-08-29
 * @Version: V1.0
 */
@Api(tags="积分充值记录")
@RestController
@RequestMapping("/integral/integralRecharge")
@Slf4j
public class IntegralRechargeController extends JeecgController<IntegralRecharge, IIntegralRechargeService> {
	@Autowired
	private IIntegralRechargeService integralRechargeService;
	
	/**
	 * 分页列表查询
	 *
	 * @param integralRecharge
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	//@AutoLog(value = "积分充值记录-分页列表查询")
	@ApiOperation(value="积分充值记录-分页列表查询", notes="积分充值记录-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<Map<String,Object>>> queryPageList(IntegralRecharge integralRecharge,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		Page<IntegralRecharge> page = new Page<IntegralRecharge>(pageNo, pageSize);
		IPage<Map<String,Object>> pageList = integralRechargeService.getRechargeOrderList(page, integralRecharge);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param integralRecharge
	 * @return
	 */
	@AutoLog(value = "积分充值记录-添加")
	@ApiOperation(value="积分充值记录-添加", notes="积分充值记录-添加")
	@RequiresPermissions("integral:integral_recharge:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody IntegralRecharge integralRecharge) {
		integralRechargeService.save(integralRecharge);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param integralRecharge
	 * @return
	 */
	@AutoLog(value = "积分充值记录-编辑")
	@ApiOperation(value="积分充值记录-编辑", notes="积分充值记录-编辑")
	@RequiresPermissions("integral:integral_recharge:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody IntegralRecharge integralRecharge) {
		integralRechargeService.updateById(integralRecharge);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "积分充值记录-通过id删除")
	@ApiOperation(value="积分充值记录-通过id删除", notes="积分充值记录-通过id删除")
	@RequiresPermissions("integral:integral_recharge:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		integralRechargeService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "积分充值记录-批量删除")
	@ApiOperation(value="积分充值记录-批量删除", notes="积分充值记录-批量删除")
	@RequiresPermissions("integral:integral_recharge:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.integralRechargeService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "积分充值记录-通过id查询")
	@ApiOperation(value="积分充值记录-通过id查询", notes="积分充值记录-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<IntegralRecharge> queryById(@RequestParam(name="id",required=true) String id) {
		IntegralRecharge integralRecharge = integralRechargeService.getById(id);
		if(integralRecharge==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(integralRecharge);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param integralRecharge
    */
    @RequiresPermissions("integral:integral_recharge:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, IntegralRecharge integralRecharge) {
        return super.exportXls(request, integralRecharge, IntegralRecharge.class, "积分充值记录");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("integral:integral_recharge:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, IntegralRecharge.class);
    }

}
