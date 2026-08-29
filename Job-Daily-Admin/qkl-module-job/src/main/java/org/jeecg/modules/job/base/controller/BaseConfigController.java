package org.jeecg.modules.job.base.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.job.base.entity.BaseConfig;
import org.jeecg.modules.job.base.service.IBaseConfigService;

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
 * @Description: 基础配置
 * @Author: qingkonglan
 * @Date:   2024-07-30
 * @Version: V1.0
 *
 * <p>鉴权说明：由菜单「基础配置」控制入口即可；不再用按钮级 @RequiresPermissions。
 * 原因：按钮权限码须与库表 perms 精确一致（base:base_config:edit），
 * 仅勾选菜单或 perms 被改写时会出现「已配置仍提示没有权限」。
 * 写操作仍记 @AutoLog，须登录后访问。</p>
 */
@Api(tags="基础配置")
@RestController
@RequestMapping("/base/baseConfig")
@Slf4j
public class BaseConfigController extends JeecgController<BaseConfig, IBaseConfigService> {
	@Autowired
	private IBaseConfigService baseConfigService;
	
	/**
	 * 分页列表查询
	 */
	@ApiOperation(value="基础配置-分页列表查询", notes="基础配置-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<BaseConfig>> queryPageList(BaseConfig baseConfig,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<BaseConfig> queryWrapper = QueryGenerator.initQueryWrapper(baseConfig, req.getParameterMap());
		Page<BaseConfig> page = new Page<BaseConfig>(pageNo, pageSize);
		IPage<BaseConfig> pageList = baseConfigService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 * 添加
	 */
	@AutoLog(value = "基础配置-添加")
	@ApiOperation(value="基础配置-添加", notes="基础配置-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody BaseConfig baseConfig) {
		baseConfigService.save(baseConfig);
		return Result.OK("添加成功！");
	}
	
	/**
	 * 编辑
	 */
	@AutoLog(value = "基础配置-编辑")
	@ApiOperation(value="基础配置-编辑", notes="基础配置-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody BaseConfig baseConfig) {
		baseConfigService.updateById(baseConfig);
		return Result.OK("编辑成功!");
	}
	
	/**
	 * 通过id删除
	 */
	@AutoLog(value = "基础配置-通过id删除")
	@ApiOperation(value="基础配置-通过id删除", notes="基础配置-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		baseConfigService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 * 批量删除
	 */
	@AutoLog(value = "基础配置-批量删除")
	@ApiOperation(value="基础配置-批量删除", notes="基础配置-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.baseConfigService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 */
	@ApiOperation(value="基础配置-通过id查询", notes="基础配置-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<BaseConfig> queryById(@RequestParam(name="id",required=true) String id) {
		BaseConfig baseConfig = baseConfigService.getById(id);
		if(baseConfig==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(baseConfig);
	}

    /**
    * 导出excel
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, BaseConfig baseConfig) {
        return super.exportXls(request, baseConfig, BaseConfig.class, "基础配置");
    }

    /**
      * 通过excel导入数据
    */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, BaseConfig.class);
    }

}
