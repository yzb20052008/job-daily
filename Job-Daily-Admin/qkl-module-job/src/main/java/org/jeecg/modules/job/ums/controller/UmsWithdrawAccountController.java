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
import org.jeecg.modules.job.ums.entity.UmsWithdrawAccount;
import org.jeecg.modules.job.ums.service.IUmsWithdrawAccountService;

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
 * @Description: 提现账号
 * @Author: qingkonglan
 * @Date:   2024-07-30
 * @Version: V1.0
 */
@Api(tags="提现账号")
@RestController
@RequestMapping("/ums/umsWithdrawAccount")
@Slf4j
public class UmsWithdrawAccountController extends JeecgController<UmsWithdrawAccount, IUmsWithdrawAccountService> {
	@Autowired
	private IUmsWithdrawAccountService umsWithdrawAccountService;
	
	/**
	 * 分页列表查询
	 *
	 * @param umsWithdrawAccount
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	//@AutoLog(value = "提现账号-分页列表查询")
	@ApiOperation(value="提现账号-分页列表查询", notes="提现账号-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<UmsWithdrawAccount>> queryPageList(UmsWithdrawAccount umsWithdrawAccount,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		Page<UmsWithdrawAccount> page = new Page<>(pageNo, pageSize);
		IPage<UmsWithdrawAccount> pageList = umsWithdrawAccountService.getWithdrawAccountList(page, umsWithdrawAccount);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param umsWithdrawAccount
	 * @return
	 */
	@AutoLog(value = "提现账号-添加")
	@ApiOperation(value="提现账号-添加", notes="提现账号-添加")
	@RequiresPermissions("ums:ums_withdraw_account:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody UmsWithdrawAccount umsWithdrawAccount) {
		umsWithdrawAccountService.save(umsWithdrawAccount);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param umsWithdrawAccount
	 * @return
	 */
	@AutoLog(value = "提现账号-编辑")
	@ApiOperation(value="提现账号-编辑", notes="提现账号-编辑")
	@RequiresPermissions("ums:ums_withdraw_account:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody UmsWithdrawAccount umsWithdrawAccount) {
		umsWithdrawAccountService.updateById(umsWithdrawAccount);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "提现账号-通过id删除")
	@ApiOperation(value="提现账号-通过id删除", notes="提现账号-通过id删除")
	@RequiresPermissions("ums:ums_withdraw_account:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		umsWithdrawAccountService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "提现账号-批量删除")
	@ApiOperation(value="提现账号-批量删除", notes="提现账号-批量删除")
	@RequiresPermissions("ums:ums_withdraw_account:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.umsWithdrawAccountService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "提现账号-通过id查询")
	@ApiOperation(value="提现账号-通过id查询", notes="提现账号-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<UmsWithdrawAccount> queryById(@RequestParam(name="id",required=true) String id) {
		UmsWithdrawAccount umsWithdrawAccount = umsWithdrawAccountService.getById(id);
		if(umsWithdrawAccount==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(umsWithdrawAccount);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param umsWithdrawAccount
    */
    @RequiresPermissions("ums:ums_withdraw_account:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, UmsWithdrawAccount umsWithdrawAccount) {
        return super.exportXls(request, umsWithdrawAccount, UmsWithdrawAccount.class, "提现账号");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("ums:ums_withdraw_account:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, UmsWithdrawAccount.class);
    }

}
