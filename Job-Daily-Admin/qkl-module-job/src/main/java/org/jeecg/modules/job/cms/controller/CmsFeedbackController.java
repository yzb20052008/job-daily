package org.jeecg.modules.job.cms.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.job.cms.entity.CmsFeedback;
import org.jeecg.modules.job.cms.service.ICmsFeedbackService;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

 /**
 * @Description: 意见反馈
 * @Author: qingkonglan
 * @Date:   2022-08-21
 * @Version: V1.0
 */
@Api(tags="意见反馈")
@RestController
@RequestMapping("/cms/cmsFeedback")
@Slf4j
public class CmsFeedbackController extends JeecgController<CmsFeedback, ICmsFeedbackService> {
	@Autowired
	private ICmsFeedbackService cmsFeedbackService;
	
	/**
	 * 分页列表查询
	 *
	 * @param cmsFeedback
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "意见反馈-分页列表查询")
	@ApiOperation(value="意见反馈-分页列表查询", notes="意见反馈-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<CmsFeedback>> queryPageList(CmsFeedback cmsFeedback,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
//		QueryWrapper<CmsFeedback> queryWrapper = QueryGenerator.initQueryWrapper(cmsFeedback, req.getParameterMap());
		Page<CmsFeedback> page = new Page<CmsFeedback>(pageNo, pageSize);
		IPage<CmsFeedback> pageList = cmsFeedbackService.getFeedbackList(page,cmsFeedback);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param cmsFeedback
	 * @return
	 */
	@AutoLog(value = "意见反馈-添加")
	@ApiOperation(value="意见反馈-添加", notes="意见反馈-添加")
	//@RequiresPermissions("org.jeecg.modules:cms_feedback:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody CmsFeedback cmsFeedback) {
		cmsFeedbackService.save(cmsFeedback);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param cmsFeedback
	 * @return
	 */
	@AutoLog(value = "意见反馈-编辑")
	@ApiOperation(value="意见反馈-编辑", notes="意见反馈-编辑")
	//@RequiresPermissions("org.jeecg.modules:cms_feedback:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody CmsFeedback cmsFeedback) {
		cmsFeedbackService.updateById(cmsFeedback);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "意见反馈-通过id删除")
	@ApiOperation(value="意见反馈-通过id删除", notes="意见反馈-通过id删除")
	//@RequiresPermissions("org.jeecg.modules:cms_feedback:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		cmsFeedbackService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "意见反馈-批量删除")
	@ApiOperation(value="意见反馈-批量删除", notes="意见反馈-批量删除")
	//@RequiresPermissions("org.jeecg.modules:cms_feedback:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.cmsFeedbackService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "意见反馈-通过id查询")
	@ApiOperation(value="意见反馈-通过id查询", notes="意见反馈-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<CmsFeedback> queryById(@RequestParam(name="id",required=true) String id) {
		CmsFeedback cmsFeedback = cmsFeedbackService.getById(id);
		if(cmsFeedback==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(cmsFeedback);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param cmsFeedback
    */
    //@RequiresPermissions("org.jeecg.modules:cms_feedback:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, CmsFeedback cmsFeedback) {
        return super.exportXls(request, cmsFeedback, CmsFeedback.class, "意见反馈");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("cms_feedback:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, CmsFeedback.class);
    }

}
