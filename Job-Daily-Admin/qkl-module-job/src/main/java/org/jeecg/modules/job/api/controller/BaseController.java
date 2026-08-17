package org.jeecg.modules.job.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.job.base.entity.BaseConfig;
import org.jeecg.modules.job.base.service.IBaseConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description: 基础配资管理模块
 * @Author: qingkonglan
 * @Date:   2024-08-22
 * @Version: V1.0
 */
@RestController
@RequestMapping("/api/base")
@Api(tags="移动端基础配资管理模块")
@Slf4j
public class BaseController {
	@Autowired
	private IBaseConfigService configService;

	/**
	 * 查询参数配置信息
	 */
	@RequestMapping(value = "/getBaseConfig",method = RequestMethod.GET)
	@ApiOperation(value = "查询参数配置信息", notes = "查询参数配置信息")
	public Result<Object> getBaseConfig(@RequestParam(name="code") String code) {
		if(oConvertUtils.isEmpty(code)){
			return Result.error("编码不能为空");
		}
		BaseConfig config=configService.getConfigByCode(code);
		return Result.OK(config);
	}


	/**
	 * 根据类型编码查询配置列表
	 */
	@RequestMapping(value = "/getConfigByGroupCode",method = RequestMethod.GET)
	@ApiOperation(value = "根据类型编码查询配置列表", notes = "根据类型编码查询配置列表")
	public Result<Object> getConfigByGroupCode(@RequestParam(name="groupCode") String groupCode) {
		if(oConvertUtils.isEmpty(groupCode)){
			return Result.error("编码不能为空");
		}
		List<BaseConfig> config=configService.getConfigByGroupCode(groupCode);
		return Result.OK(config);
	}
}