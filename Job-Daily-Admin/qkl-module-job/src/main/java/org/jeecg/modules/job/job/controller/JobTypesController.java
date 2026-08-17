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
import org.jeecg.common.system.vo.SelectTreeModel;
import org.jeecg.modules.job.job.entity.JobTypes;
import org.jeecg.modules.job.job.service.IJobTypesService;

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
 * @Description: 工种信息
 * @Author: qingkonglan
 * @Date:   2024-07-30
 * @Version: V1.0
 */
@Api(tags="工种信息")
@RestController
@RequestMapping("/job/jobTypes")
@Slf4j
public class JobTypesController extends JeecgController<JobTypes, IJobTypesService>{
	@Autowired
	private IJobTypesService jobTypesService;

	/**
	 * 分页列表查询
	 *
	 * @param jobTypes
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "工种信息-分页列表查询")
	@ApiOperation(value="工种信息-分页列表查询", notes="工种信息-分页列表查询")
	@GetMapping(value = "/rootList")
	public Result<IPage<JobTypes>> queryPageList(JobTypes jobTypes,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		String hasQuery = req.getParameter("hasQuery");
        if(hasQuery != null && "true".equals(hasQuery)){
            QueryWrapper<JobTypes> queryWrapper =  QueryGenerator.initQueryWrapper(jobTypes, req.getParameterMap());
			queryWrapper.orderByAsc("sort");
            List<JobTypes> list = jobTypesService.queryTreeListNoPage(queryWrapper);
            IPage<JobTypes> pageList = new Page<>(1, 10, list.size());
            pageList.setRecords(list);
            return Result.OK(pageList);
        }else{
            String parentId = jobTypes.getPid();
            if (oConvertUtils.isEmpty(parentId)) {
                parentId = "0";
            }
            jobTypes.setPid(null);
            QueryWrapper<JobTypes> queryWrapper = QueryGenerator.initQueryWrapper(jobTypes, req.getParameterMap());
			queryWrapper.orderByAsc("sort");
            // 使用 eq 防止模糊查询
            queryWrapper.eq("pid", parentId);
            Page<JobTypes> page = new Page<JobTypes>(pageNo, pageSize);
            IPage<JobTypes> pageList = jobTypesService.page(page, queryWrapper);
            return Result.OK(pageList);
        }
	}

	 /**
	  * 【vue3专用】加载节点的子数据
	  *
	  * @param pid
	  * @return
	  */
	 @RequestMapping(value = "/loadTreeChildren", method = RequestMethod.GET)
	 public Result<List<SelectTreeModel>> loadTreeChildren(@RequestParam(name = "pid") String pid) {
		 Result<List<SelectTreeModel>> result = new Result<>();
		 try {
			 List<SelectTreeModel> ls = jobTypesService.queryListByPid(pid);
			 result.setResult(ls);
			 result.setSuccess(true);
		 } catch (Exception e) {
			 e.printStackTrace();
			 result.setMessage(e.getMessage());
			 result.setSuccess(false);
		 }
		 return result;
	 }

	 /**
	  * 【vue3专用】加载一级节点/如果是同步 则所有数据
	  *
	  * @param async
	  * @param pcode
	  * @return
	  */
	 @RequestMapping(value = "/loadTreeRoot", method = RequestMethod.GET)
	 public Result<List<SelectTreeModel>> loadTreeRoot(@RequestParam(name = "async") Boolean async, @RequestParam(name = "pcode") String pcode) {
		 Result<List<SelectTreeModel>> result = new Result<>();
		 try {
			 List<SelectTreeModel> ls = jobTypesService.queryListByCode(pcode);
			 if (!async) {
				 loadAllChildren(ls);
			 }
			 result.setResult(ls);
			 result.setSuccess(true);
		 } catch (Exception e) {
			 e.printStackTrace();
			 result.setMessage(e.getMessage());
			 result.setSuccess(false);
		 }
		 return result;
	 }

	 /**
	  * 【vue3专用】递归求子节点 同步加载用到
	  *
	  * @param ls
	  */
	 private void loadAllChildren(List<SelectTreeModel> ls) {
		 for (SelectTreeModel tsm : ls) {
			 List<SelectTreeModel> temp = jobTypesService.queryListByPid(tsm.getKey());
			 if (temp != null && temp.size() > 0) {
				 tsm.setChildren(temp);
				 loadAllChildren(temp);
			 }
		 }
	 }

	 /**
      * 获取子数据
      * @param jobTypes
      * @param req
      * @return
      */
	//@AutoLog(value = "工种信息-获取子数据")
	@ApiOperation(value="工种信息-获取子数据", notes="工种信息-获取子数据")
	@GetMapping(value = "/childList")
	public Result<IPage<JobTypes>> queryPageList(JobTypes jobTypes,HttpServletRequest req) {
		QueryWrapper<JobTypes> queryWrapper = QueryGenerator.initQueryWrapper(jobTypes, req.getParameterMap());
		queryWrapper.orderByAsc("sort");
		List<JobTypes> list = jobTypesService.list(queryWrapper);
		IPage<JobTypes> pageList = new Page<>(1, 10, list.size());
        pageList.setRecords(list);
		return Result.OK(pageList);
	}

    /**
      * 批量查询子节点
      * @param parentIds 父ID（多个采用半角逗号分割）
      * @return 返回 IPage
      * @param parentIds
      * @return
      */
	//@AutoLog(value = "工种信息-批量获取子数据")
    @ApiOperation(value="工种信息-批量获取子数据", notes="工种信息-批量获取子数据")
    @GetMapping("/getChildListBatch")
    public Result getChildListBatch(@RequestParam("parentIds") String parentIds) {
        try {
            QueryWrapper<JobTypes> queryWrapper = new QueryWrapper<>();
			queryWrapper.eq("if_public",1);
            List<String> parentIdList = Arrays.asList(parentIds.split(","));
            queryWrapper.in("pid", parentIdList);
            List<JobTypes> list = jobTypesService.list(queryWrapper);
            IPage<JobTypes> pageList = new Page<>(1, 10, list.size());
            pageList.setRecords(list);
            return Result.OK(pageList);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error("批量查询子节点失败：" + e.getMessage());
        }
    }
	
	/**
	 *   添加
	 *
	 * @param jobTypes
	 * @return
	 */
	@AutoLog(value = "工种信息-添加")
	@ApiOperation(value="工种信息-添加", notes="工种信息-添加")
    @RequiresPermissions("job:job_types:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody JobTypes jobTypes) {
		jobTypesService.addJobTypes(jobTypes);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param jobTypes
	 * @return
	 */
	@AutoLog(value = "工种信息-编辑")
	@ApiOperation(value="工种信息-编辑", notes="工种信息-编辑")
    @RequiresPermissions("job:job_types:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody JobTypes jobTypes) {
		jobTypesService.updateJobTypes(jobTypes);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "工种信息-通过id删除")
	@ApiOperation(value="工种信息-通过id删除", notes="工种信息-通过id删除")
    @RequiresPermissions("job:job_types:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		jobTypesService.deleteJobTypes(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "工种信息-批量删除")
	@ApiOperation(value="工种信息-批量删除", notes="工种信息-批量删除")
    @RequiresPermissions("job:job_types:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.jobTypesService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "工种信息-通过id查询")
	@ApiOperation(value="工种信息-通过id查询", notes="工种信息-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<JobTypes> queryById(@RequestParam(name="id",required=true) String id) {
		JobTypes jobTypes = jobTypesService.getById(id);
		if(jobTypes==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(jobTypes);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param jobTypes
    */
    @RequiresPermissions("job:job_types:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, JobTypes jobTypes) {
		return super.exportXls(request, jobTypes, JobTypes.class, "工种信息");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("job:job_types:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
		return super.importExcel(request, response, JobTypes.class);
    }

}
