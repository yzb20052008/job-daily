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
import org.jeecg.modules.job.ums.entity.UmsWithdraw;
import org.jeecg.modules.job.ums.service.IUmsWithdrawService;

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
 * @Description: 用户提现
 * @Author: qingkonglan
 * @Date:   2023-03-30
 * @Version: V1.0
 */
@Api(tags="用户提现")
@RestController
@RequestMapping("/ums/umsWithdraw")
@Slf4j
public class UmsWithdrawController extends JeecgController<UmsWithdraw, IUmsWithdrawService> {
	@Autowired
	private IUmsWithdrawService umsWithdrawService;
	
	/**
	 * 分页列表查询
	 *
	 * @param umsWithdraw
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@ApiOperation(value="用户提现-分页列表查询", notes="用户提现-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<UmsWithdraw>> queryPageList(UmsWithdraw umsWithdraw,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		Page<UmsWithdraw> page = new Page<UmsWithdraw>(pageNo, pageSize);
		IPage<UmsWithdraw> pageList = umsWithdrawService.getWithdrawPageList(page, umsWithdraw);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param umsWithdraw
	 * @return
	 */
	@AutoLog(value = "用户提现-添加")
	@ApiOperation(value="用户提现-添加", notes="用户提现-添加")
	//@RequiresPermissions("org.jeecg.modules.job:ums_withdraw:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody UmsWithdraw umsWithdraw) {
		umsWithdrawService.save(umsWithdraw);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param umsWithdraw
	 * @return
	 */
	@AutoLog(value = "用户提现-编辑")
	@ApiOperation(value="用户提现-编辑", notes="用户提现-编辑")
	//@RequiresPermissions("org.jeecg.modules.job:ums_withdraw:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody UmsWithdraw umsWithdraw) {
		umsWithdrawService.updateById(umsWithdraw);
		return Result.OK("编辑成功!");
	}


	 /**
	  *  更新状态
	  *
	  * @param umsWithdraw
	  * @return
	  */
	 @AutoLog(value = "用户提现-更新状态")
	 @ApiOperation(value="用户提现-更新状态", notes="用户提现-更新状态")
	 @RequestMapping(value = "/updateStatus", method = {RequestMethod.PUT,RequestMethod.POST})
	 public Result<String> updateStatus(@RequestBody UmsWithdraw umsWithdraw) {
	 	try{
			if (oConvertUtils.isEmpty(umsWithdraw.getId()) || oConvertUtils.isEmpty(umsWithdraw.getWithdrawStatus())){
				return Result.error("参数错误");
			}
			umsWithdrawService.updateStatus(umsWithdraw.getId(),umsWithdraw.getWithdrawStatus(),umsWithdraw.getReason());
			return Result.OK("编辑成功!");
		}catch (Exception e){
			return Result.error("操作失败:"+e.getMessage());
		}
	 }

	/**
	 * 关闭异常提现（无商户单号）并解冻
	 */
	@AutoLog(value = "用户提现-关闭异常单并解冻")
	@ApiOperation(value = "关闭异常提现并解冻", notes = "仅允许：审核通过且无 out_bill_no 的脏数据")
	@RequestMapping(value = "/closeAbnormal", method = {RequestMethod.PUT, RequestMethod.POST})
	public Result<String> closeAbnormal(@RequestBody UmsWithdraw umsWithdraw) {
		try {
			if (oConvertUtils.isEmpty(umsWithdraw.getId())) {
				return Result.error("参数错误");
			}
			umsWithdrawService.closeAbnormalWithdraw(umsWithdraw.getId(), umsWithdraw.getReason());
			return Result.OK("已关闭并解冻");
		} catch (Exception e) {
			return Result.error("操作失败:" + e.getMessage());
		}
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "用户提现-通过id删除")
	@ApiOperation(value="用户提现-通过id删除", notes="用户提现-通过id删除")
	//@RequiresPermissions("org.jeecg.modules.job:ums_withdraw:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		umsWithdrawService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "用户提现-批量删除")
	@ApiOperation(value="用户提现-批量删除", notes="用户提现-批量删除")
	//@RequiresPermissions("org.jeecg.modules.job:ums_withdraw:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.umsWithdrawService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "用户提现-通过id查询")
	@ApiOperation(value="用户提现-通过id查询", notes="用户提现-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<UmsWithdraw> queryById(@RequestParam(name="id",required=true) String id) {
		UmsWithdraw umsWithdraw = umsWithdrawService.getById(id);
		if(umsWithdraw==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(umsWithdraw);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param umsWithdraw
    */
    //@RequiresPermissions("org.jeecg.modules.job:ums_withdraw:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, UmsWithdraw umsWithdraw) {
        return super.exportXls(request, umsWithdraw, UmsWithdraw.class, "用户提现");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("ums_withdraw:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, UmsWithdraw.class);
    }

}
