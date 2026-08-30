package org.jeecg.modules.job.job.controller;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.job.constant.BizConstants;
import org.jeecg.modules.job.job.entity.JobPost;
import org.jeecg.modules.job.job.entity.JobTypes;
import org.jeecg.modules.job.job.service.IJobPostService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.job.job.service.IJobTypesService;
import org.jeecg.modules.job.map.support.TencentMapApiSupport;
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
 * @Description: 招工信息
 * @Author: qingkonglan
 * @Date:   2024-07-30
 * @Version: V1.0
 */
@Api(tags="招工信息")
@RestController
@RequestMapping("/job/jobPost")
@Slf4j
public class JobPostController extends JeecgController<JobPost, IJobPostService> {
	@Autowired
	private IJobPostService jobPostService;
	@Resource
	private IJobTypesService typesService;
	@Resource
	private TencentMapApiSupport tencentMapApiSupport;

	/**
	 * 分页列表查询
	 *
	 * @param jobPost
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "招工信息-分页列表查询")
	@ApiOperation(value="招工信息-分页列表查询", notes="招工信息-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<Map<String, Object>>> queryPageList(JobPost jobPost,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		Page<JobPost> page = new Page<JobPost>(pageNo, pageSize);
		IPage<Map<String, Object>> pageList = jobPostService.getPostMapListForAdmin(page, jobPost);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param jobPost
	 * @return
	 */
	@AutoLog(value = "招工信息-添加")
	@ApiOperation(value="招工信息-添加", notes="招工信息-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody JobPost jobPost) {
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		jobPost.setUserId(user.getId());
		jobPost.setPostSource(1);
		jobPost.setSexRequire("不限");
		jobPost.setPostStatus(BizConstants.POST_STATUS_RUNNING);
		//查询分类
		JobTypes types=typesService.getTypeNameCodes(jobPost.getTypeIds());
		jobPost.setTypeNames(types.getName());
		jobPost.setTypeCodes(types.getTypeCode());
		jobPostService.save(jobPost);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param jobPost
	 * @return
	 */
	@AutoLog(value = "招工信息-编辑")
	@ApiOperation(value="招工信息-编辑", notes="招工信息-编辑")
	@RequiresPermissions("job:job_post:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody JobPost jobPost) {
		//查询分类
		JobTypes types=typesService.getTypeNameCodes(jobPost.getTypeIds());
		jobPost.setTypeNames(types.getName());
		jobPost.setTypeCodes(types.getTypeCode());
		jobPostService.updateById(jobPost);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "招工信息-通过id删除")
	@ApiOperation(value="招工信息-通过id删除", notes="招工信息-通过id删除")
	@RequiresPermissions("job:job_post:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		jobPostService.deletePostInfo(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "招工信息-批量删除")
	@ApiOperation(value="招工信息-批量删除", notes="招工信息-批量删除")
	@RequiresPermissions("job:job_post:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.jobPostService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "招工信息-通过id查询")
	@ApiOperation(value="招工信息-通过id查询", notes="招工信息-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<JobPost> queryById(@RequestParam(name="id",required=true) String id) {
		JobPost jobPost = jobPostService.getPostDetail(id);
		if(jobPost==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(jobPost);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param jobPost
    */
    @RequiresPermissions("job:job_post:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, JobPost jobPost) {
        return super.exportXls(request, jobPost, JobPost.class, "招工信息");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("job:job_post:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, JobPost.class);
    }

	@ApiOperation(value = "地图Key", notes = "后台地图选点组件使用，仅下发 map_key")
	@GetMapping(value = "/mapConfig")
	public Result<Map<String, String>> mapConfig() {
		Map<String, String> data = new LinkedHashMap<>();
		try {
			data.put("mapKey", tencentMapApiSupport.getMapKey());
		} catch (IllegalStateException e) {
			data.put("mapKey", "");
		}
		return Result.OK(data);
	}

	@ApiOperation(value = "地点搜索", notes = "关键词 POI 搜索")
	@GetMapping(value = "/map/suggestion")
	public Result<Object> mapSuggestion(@RequestParam(name = "keyword") String keyword,
			@RequestParam(name = "latitude", required = false) String latitude,
			@RequestParam(name = "longitude", required = false) String longitude,
			@RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize) {
		return tencentMapApiSupport.suggestion(keyword, latitude, longitude, pageSize);
	}

	@ApiOperation(value = "逆地理编码", notes = "经纬度转地址")
	@GetMapping(value = "/map/reverseGeocoder")
	public Result<Object> mapReverseGeocoder(@RequestParam(name = "latitude") String latitude,
			@RequestParam(name = "longitude") String longitude) {
		return tencentMapApiSupport.reverseGeocoder(latitude, longitude);
	}

}
