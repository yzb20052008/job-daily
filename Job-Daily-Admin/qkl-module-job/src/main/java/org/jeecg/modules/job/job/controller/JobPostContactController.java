package org.jeecg.modules.job.job.controller;

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
import org.jeecg.modules.job.job.entity.JobPostContact;
import org.jeecg.modules.job.job.service.IJobPostContactService;

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
 * @Description: 拨号记录
 * @Author: qingkonglan
 * @Date:   2024-07-30
 * @Version: V1.0
 */
@Api(tags="拨号记录")
@RestController
@RequestMapping("/job/jobPostContact")
@Slf4j
public class JobPostContactController extends JeecgController<JobPostContact, IJobPostContactService> {
	@Autowired
	private IJobPostContactService jobPostContactService;
	
	/**
	 * 分页列表查询
	 *
	 * @param jobPostContact
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	//@AutoLog(value = "拨号记录-分页列表查询")
	@ApiOperation(value="拨号记录-分页列表查询", notes="拨号记录-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<Map<String,Object>>> queryPageList(JobPostContact jobPostContact,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		Page<JobPostContact> page = new Page<JobPostContact>(pageNo, pageSize);
		IPage<Map<String,Object>> pageList = jobPostContactService.getContactListForAdmin(page, jobPostContact);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param jobPostContact
	 * @return
	 */
	@AutoLog(value = "拨号记录-添加")
	@ApiOperation(value="拨号记录-添加", notes="拨号记录-添加")
	@RequiresPermissions("job:job_post_contact:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody JobPostContact jobPostContact) {
		jobPostContactService.save(jobPostContact);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param jobPostContact
	 * @return
	 */
	@AutoLog(value = "拨号记录-编辑")
	@ApiOperation(value="拨号记录-编辑", notes="拨号记录-编辑")
	@RequiresPermissions("job:job_post_contact:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody JobPostContact jobPostContact) {
		jobPostContactService.updateById(jobPostContact);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "拨号记录-通过id删除")
	@ApiOperation(value="拨号记录-通过id删除", notes="拨号记录-通过id删除")
	@RequiresPermissions("job:job_post_contact:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		jobPostContactService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "拨号记录-批量删除")
	@ApiOperation(value="拨号记录-批量删除", notes="拨号记录-批量删除")
	@RequiresPermissions("job:job_post_contact:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.jobPostContactService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "拨号记录-通过id查询")
	@ApiOperation(value="拨号记录-通过id查询", notes="拨号记录-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<JobPostContact> queryById(@RequestParam(name="id",required=true) String id) {
		JobPostContact jobPostContact = jobPostContactService.getById(id);
		if(jobPostContact==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(jobPostContact);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param jobPostContact
    */
    @RequiresPermissions("job:job_post_contact:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, JobPostContact jobPostContact) {
        return super.exportXls(request, jobPostContact, JobPostContact.class, "拨号记录");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("job:job_post_contact:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, JobPostContact.class);
    }

}
