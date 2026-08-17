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

import com.alibaba.fastjson.JSONObject;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.job.ums.entity.UmsMemberRole;
import org.jeecg.modules.job.ums.service.IUmsMemberRoleService;

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
 * @Description: 会员公司关系
 * @Author: qingkonglan
 * @Date:   2022-12-18
 * @Version: V1.0
 */
@Api(tags="会员公司关系")
@RestController
@RequestMapping("/ums/umsMemberRole")
@Slf4j
public class UmsMemberRoleController extends JeecgController<UmsMemberRole, IUmsMemberRoleService> {
	@Autowired
	private IUmsMemberRoleService umsMemberRoleService;
	
	/**
	 * 分页列表查询
	 *
	 * @param umsMemberRole
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "会员公司关系-分页列表查询")
	@ApiOperation(value="会员公司关系-分页列表查询", notes="会员公司关系-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<UmsMemberRole>> queryPageList(UmsMemberRole umsMemberRole,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<UmsMemberRole> queryWrapper = QueryGenerator.initQueryWrapper(umsMemberRole, req.getParameterMap());
		Page<UmsMemberRole> page = new Page<UmsMemberRole>(pageNo, pageSize);
		IPage<UmsMemberRole> pageList = umsMemberRoleService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param umsMemberRole
	 * @return
	 */
	@AutoLog(value = "会员公司关系-添加")
	@ApiOperation(value="会员公司关系-添加", notes="会员公司关系-添加")
	//@RequiresPermissions("org.jeecg.modules.job:ums_member_role:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody UmsMemberRole umsMemberRole) {
		umsMemberRoleService.save(umsMemberRole);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param umsMemberRole
	 * @return
	 */
	@AutoLog(value = "会员公司关系-编辑")
	@ApiOperation(value="会员公司关系-编辑", notes="会员公司关系-编辑")
	//@RequiresPermissions("org.jeecg.modules.job:ums_member_role:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody UmsMemberRole umsMemberRole) {
		umsMemberRoleService.updateById(umsMemberRole);
		return Result.OK("编辑成功!");
	}


	 /**
	  * 解绑关联企业
	  * @param jsonObject
	  * @return
	  */
	 @RequestMapping(value = "/unbindCompany", method = RequestMethod.PUT)
	 public Result<Object> unbindCompany(@RequestBody JSONObject jsonObject) {
		 try {
			 String id = jsonObject.getString("id");
			 String companyId = jsonObject.getString("companyId");
			 if (oConvertUtils.isEmpty(id) || oConvertUtils.isEmpty(companyId)){
				 //参数异常
				 return Result.error("参数异常");
			 }
			 umsMemberRoleService.remove(new QueryWrapper<>(new UmsMemberRole().setMemberId(id).setCompanyId(companyId)));
			 return Result.ok("操作成功");
		 } catch (Exception e) {
			 log.error(e.getMessage(), e);
			 return Result.error("操作失败："+e.getMessage());
		 }
	 }
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "会员公司关系-通过id删除")
	@ApiOperation(value="会员公司关系-通过id删除", notes="会员公司关系-通过id删除")
	//@RequiresPermissions("org.jeecg.modules.job:ums_member_role:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		umsMemberRoleService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "会员公司关系-批量删除")
	@ApiOperation(value="会员公司关系-批量删除", notes="会员公司关系-批量删除")
	//@RequiresPermissions("org.jeecg.modules.job:ums_member_role:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.umsMemberRoleService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "会员公司关系-通过id查询")
	@ApiOperation(value="会员公司关系-通过id查询", notes="会员公司关系-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<UmsMemberRole> queryById(@RequestParam(name="id",required=true) String id) {
		UmsMemberRole umsMemberRole = umsMemberRoleService.getById(id);
		if(umsMemberRole==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(umsMemberRole);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param umsMemberRole
    */
    //@RequiresPermissions("org.jeecg.modules.job:ums_member_role:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, UmsMemberRole umsMemberRole) {
        return super.exportXls(request, umsMemberRole, UmsMemberRole.class, "会员公司关系");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("ums_member_role:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, UmsMemberRole.class);
    }

}
