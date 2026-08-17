package org.jeecg.modules.job.cms.controller;

import java.util.Arrays;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.job.cms.entity.CmsNotice;
import org.jeecg.modules.job.cms.service.ICmsNoticeService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
 * @Description: 系统通知
 * @Author: qingkonglan
 * @Date:   2022-09-26
 * @Version: V1.0
 */
@Api(tags="系统通知")
@RestController
@RequestMapping("/cms/cmsNotice")
@Slf4j
public class CmsNoticeController extends JeecgController<CmsNotice, ICmsNoticeService> {
	@Autowired
	private ICmsNoticeService cmsNoticeService;
	 @Resource
	 private ISysBaseAPI sysBaseAPI;

	/**
	 * 分页列表查询
	 *
	 * @param cmsNotice
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "系统通知-分页列表查询")
	@ApiOperation(value="系统通知-分页列表查询", notes="系统通知-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<CmsNotice>> queryPageList(CmsNotice cmsNotice,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<CmsNotice> queryWrapper = QueryGenerator.initQueryWrapper(cmsNotice, req.getParameterMap());
		Page<CmsNotice> page = new Page<CmsNotice>(pageNo, pageSize);
		queryWrapper.ne("type",1);
		queryWrapper.ne("type",3);
		IPage<CmsNotice> pageList = cmsNoticeService.page(page, queryWrapper);
		pageList.getRecords().forEach(item->{
			if (item.getType()==2 || item.getType()==4){
				LoginUser user=sysBaseAPI.getUserById(item.getUserId());
				item.setUserName(user.getNickname());
				item.setUserPhone(user.getPhone());
				item.setUserAvatar(user.getAvatar());
			}
		});
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param cmsNotice
	 * @return
	 */
	@AutoLog(value = "系统通知-添加")
	@ApiOperation(value="系统通知-添加", notes="系统通知-添加")
	//@RequiresPermissions("org.jeecg.modules:cms_notice:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody CmsNotice cmsNotice) {
		cmsNotice.setIfPublic(1);
		cmsNoticeService.addNotice(cmsNotice);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param cmsNotice
	 * @return
	 */
	@AutoLog(value = "系统通知-编辑")
	@ApiOperation(value="系统通知-编辑", notes="系统通知-编辑")
	//@RequiresPermissions("org.jeecg.modules:cms_notice:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody CmsNotice cmsNotice) {
		cmsNoticeService.updateById(cmsNotice);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "系统通知-通过id删除")
	@ApiOperation(value="系统通知-通过id删除", notes="系统通知-通过id删除")
	//@RequiresPermissions("org.jeecg.modules:cms_notice:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		cmsNoticeService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "系统通知-批量删除")
	@ApiOperation(value="系统通知-批量删除", notes="系统通知-批量删除")
	//@RequiresPermissions("org.jeecg.modules:cms_notice:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.cmsNoticeService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "系统通知-通过id查询")
	@ApiOperation(value="系统通知-通过id查询", notes="系统通知-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<CmsNotice> queryById(@RequestParam(name="id",required=true) String id) {
		CmsNotice cmsNotice = cmsNoticeService.getById(id);
		if(cmsNotice==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(cmsNotice);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param cmsNotice
    */
    //@RequiresPermissions("org.jeecg.modules:cms_notice:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, CmsNotice cmsNotice) {
        return super.exportXls(request, cmsNotice, CmsNotice.class, "系统通知");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("cms_notice:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, CmsNotice.class);
    }

}
