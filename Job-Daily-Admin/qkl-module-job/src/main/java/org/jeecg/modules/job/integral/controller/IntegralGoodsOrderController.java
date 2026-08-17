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
import org.jeecg.modules.job.integral.entity.IntegralGoodsOrder;
import org.jeecg.modules.job.integral.service.IIntegralGoodsOrderService;

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
 * @Description: 积分订单
 * @Author: qingkonglan
 * @Date:   2024-07-30
 * @Version: V1.0
 */
@Api(tags="积分订单")
@RestController
@RequestMapping("/integral/integralGoodsOrder")
@Slf4j
public class IntegralGoodsOrderController extends JeecgController<IntegralGoodsOrder, IIntegralGoodsOrderService> {
	@Autowired
	private IIntegralGoodsOrderService integralGoodsOrderService;
	
	/**
	 * 分页列表查询
	 *
	 * @param integralGoodsOrder
	 * @param pageNo
	 * @param pageSize
	 * @param
	 * @return
	 */
	//@AutoLog(value = "积分订单-分页列表查询")
	@ApiOperation(value="积分订单-分页列表查询", notes="积分订单-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<Map<String,Object>>> queryPageList(IntegralGoodsOrder integralGoodsOrder,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		Page<IntegralGoodsOrder> page = new Page<>(pageNo, pageSize);
		IPage<Map<String,Object>> pageList = integralGoodsOrderService.getGoodsOrderList(page, integralGoodsOrder);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param integralGoodsOrder
	 * @return
	 */
	@AutoLog(value = "积分订单-添加")
	@ApiOperation(value="积分订单-添加", notes="积分订单-添加")
	@RequiresPermissions("integral:integral_goods_order:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody IntegralGoodsOrder integralGoodsOrder) {
		integralGoodsOrderService.save(integralGoodsOrder);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param integralGoodsOrder
	 * @return
	 */
	@AutoLog(value = "积分订单-编辑")
	@ApiOperation(value="积分订单-编辑", notes="积分订单-编辑")
	@RequiresPermissions("integral:integral_goods_order:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody IntegralGoodsOrder integralGoodsOrder) {
		integralGoodsOrderService.updateById(integralGoodsOrder);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "积分订单-通过id删除")
	@ApiOperation(value="积分订单-通过id删除", notes="积分订单-通过id删除")
	@RequiresPermissions("integral:integral_goods_order:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		integralGoodsOrderService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "积分订单-批量删除")
	@ApiOperation(value="积分订单-批量删除", notes="积分订单-批量删除")
	@RequiresPermissions("integral:integral_goods_order:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.integralGoodsOrderService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "积分订单-通过id查询")
	@ApiOperation(value="积分订单-通过id查询", notes="积分订单-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<IntegralGoodsOrder> queryById(@RequestParam(name="id",required=true) String id) {
		IntegralGoodsOrder integralGoodsOrder = integralGoodsOrderService.getById(id);
		if(integralGoodsOrder==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(integralGoodsOrder);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param integralGoodsOrder
    */
    @RequiresPermissions("integral:integral_goods_order:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, IntegralGoodsOrder integralGoodsOrder) {
        return super.exportXls(request, integralGoodsOrder, IntegralGoodsOrder.class, "积分订单");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("integral:integral_goods_order:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, IntegralGoodsOrder.class);
    }

}
