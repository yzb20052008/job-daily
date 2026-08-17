package org.jeecg.modules.job.job.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.job.job.entity.JobCompany;
import org.jeecg.modules.job.job.service.IJobCompanyService;

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
 * @Description: 企业认证
 * @Author: qingkonglan
 * @Date:   2024-07-30
 * @Version: V1.0
 */
@Api(tags="企业认证")
@RestController
@RequestMapping("/job/jobCompany")
@Slf4j
public class JobCompanyController extends JeecgController<JobCompany, IJobCompanyService> {
	@Resource
	private IJobCompanyService jobCompanyService;
	@Resource
	private ISysBaseAPI sysBaseAPI;
	
	/**
	 * 分页列表查询
	 *
	 * @param jobCompany
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "企业认证-分页列表查询")
	@ApiOperation(value="企业认证-分页列表查询", notes="企业认证-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<JobCompany>> queryPageList(JobCompany jobCompany,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		Page<JobCompany> page = new Page<JobCompany>(pageNo, pageSize);
		IPage<JobCompany> pageList = jobCompanyService.getCompanyAuthList(page, jobCompany);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param jobCompany
	 * @return
	 */
	@AutoLog(value = "企业认证-添加")
	@ApiOperation(value="企业认证-添加", notes="企业认证-添加")
	@RequiresPermissions("job:job_company:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody JobCompany jobCompany) {
		jobCompanyService.save(jobCompany);
		return Result.OK("添加成功！");
	}

	 /**
	  *  更新状态
	  *
	  * @param jsonObject
	  * @return
	  */
	 @AutoLog(value = "更新状态")
	 @ApiOperation(value="更新状态", notes="更新状态")
	 @RequestMapping(value = "/updateStatus", method = {RequestMethod.POST})
	 public Result<String> updateStatus(@RequestBody JSONObject jsonObject) {
		 String id=jsonObject.getString("id");
		 int authStatus=jsonObject.getIntValue("authStatus");
		 String reason=jsonObject.getString("reason");
		 if (oConvertUtils.isEmpty(id)){
			 return Result.error("ID不能为空!");
		 }
		 if (oConvertUtils.isEmpty(authStatus)){
			 return Result.error("审核状态不能为空!");
		 }
		 try {
			 jobCompanyService.updateStatus(id,authStatus,reason);
		 }catch (Exception e){
			 e.printStackTrace();
			 return Result.error(e.getMessage());
		 }
		 return Result.OK("操作成功!");
	 }

	
	/**
	 *  编辑
	 *
	 * @param jobCompany
	 * @return
	 */
	@AutoLog(value = "企业认证-编辑")
	@ApiOperation(value="企业认证-编辑", notes="企业认证-编辑")
	@RequiresPermissions("job:job_company:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody JobCompany jobCompany) {
		jobCompanyService.updateById(jobCompany);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "企业认证-通过id删除")
	@ApiOperation(value="企业认证-通过id删除", notes="企业认证-通过id删除")
	@RequiresPermissions("job:job_company:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		jobCompanyService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "企业认证-批量删除")
	@ApiOperation(value="企业认证-批量删除", notes="企业认证-批量删除")
	@RequiresPermissions("job:job_company:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.jobCompanyService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "企业认证-通过id查询")
	@ApiOperation(value="企业认证-通过id查询", notes="企业认证-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<JobCompany> queryById(@RequestParam(name="id",required=true) String id) {
		JobCompany jobCompany = jobCompanyService.getById(id);
		if(jobCompany==null) {
			return Result.error("未找到对应数据");
		}
		LoginUser user=sysBaseAPI.getUserById(jobCompany.getUserId());
		jobCompany.setUserName(user.getNickname());
		jobCompany.setUserAvatar(user.getAvatar());
		jobCompany.setUserPhone(user.getPhone());
		return Result.OK(jobCompany);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param jobCompany
    */
    @RequiresPermissions("job:job_company:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, JobCompany jobCompany) {
        return super.exportXls(request, jobCompany, JobCompany.class, "企业认证");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("job:job_company:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, JobCompany.class);
    }

}
