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
import org.jeecg.modules.job.ums.entity.UmsAccount;
import org.jeecg.modules.job.ums.service.IUmsAccountService;

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
 * @Description: 会员账户
 * @Author: qingkonglan
 * @Date:   2022-12-23
 * @Version: V1.0
 */
@Api(tags="会员账户")
@RestController
@RequestMapping("/ums/umsAccount")
@Slf4j
public class UmsAccountController extends JeecgController<UmsAccount, IUmsAccountService> {
	@Autowired
	private IUmsAccountService umsAccountService;
	
	/**
	 * 分页列表查询
	 *
	 * @param umsAccount
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "会员账户-分页列表查询")
	@ApiOperation(value="会员账户-分页列表查询", notes="会员账户-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<UmsAccount>> queryPageList(UmsAccount umsAccount,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<UmsAccount> queryWrapper = QueryGenerator.initQueryWrapper(umsAccount, req.getParameterMap());
		Page<UmsAccount> page = new Page<UmsAccount>(pageNo, pageSize);
		IPage<UmsAccount> pageList = umsAccountService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param umsAccount
	 * @return
	 */
	@AutoLog(value = "会员账户-添加")
	@ApiOperation(value="会员账户-添加", notes="会员账户-添加")
	//@RequiresPermissions("org.jeecg.modules.job:ums_account:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody UmsAccount umsAccount) {
		umsAccountService.save(umsAccount);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param umsAccount
	 * @return
	 */
	@AutoLog(value = "会员账户-编辑")
	@ApiOperation(value="会员账户-编辑", notes="会员账户-编辑")
	//@RequiresPermissions("org.jeecg.modules.job:ums_account:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody UmsAccount umsAccount) {
		umsAccountService.updateById(umsAccount);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "会员账户-通过id删除")
	@ApiOperation(value="会员账户-通过id删除", notes="会员账户-通过id删除")
	//@RequiresPermissions("org.jeecg.modules.job:ums_account:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		umsAccountService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "会员账户-批量删除")
	@ApiOperation(value="会员账户-批量删除", notes="会员账户-批量删除")
	//@RequiresPermissions("org.jeecg.modules.job:ums_account:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.umsAccountService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "会员账户-通过id查询")
	@ApiOperation(value="会员账户-通过id查询", notes="会员账户-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<UmsAccount> queryById(@RequestParam(name="id",required=true) String id) {
		UmsAccount umsAccount = umsAccountService.getById(id);
		if(umsAccount==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(umsAccount);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param umsAccount
    */
    //@RequiresPermissions("org.jeecg.modules.job:ums_account:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, UmsAccount umsAccount) {
        return super.exportXls(request, umsAccount, UmsAccount.class, "会员账户");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("ums_account:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, UmsAccount.class);
    }

}
