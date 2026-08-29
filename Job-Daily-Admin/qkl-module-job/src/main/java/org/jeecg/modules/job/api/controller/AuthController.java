package org.jeecg.modules.job.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.job.job.entity.JobCompany;
import org.jeecg.modules.job.job.entity.JobOrder;
import org.jeecg.modules.job.job.service.IJobCompanyService;
import org.jeecg.modules.job.job.service.IJobOrderService;
import org.jeecg.modules.job.ums.entity.UmsRealnameAuth;
import org.jeecg.modules.job.ums.service.IUmsRealnameAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 */
@RestController
@RequestMapping("/api/auth")
@Api(tags="移动端认证管理模块")
@Slf4j
public class AuthController {
	@Autowired
	private IUmsRealnameAuthService realnameAuthService;
	@Autowired
	private IJobCompanyService companyService;

	/**
	 * 实名认证
	 */
	@ApiOperation(value = "实名认证", notes = "实名认证")
	@PostMapping(value = "/addRealNameAuth")
	public Result<?> addRealNameAuth(@RequestBody UmsRealnameAuth realnameAuth){
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		realnameAuth.setUserId(user.getId());
		if (oConvertUtils.isEmpty(realnameAuth.getRealname()) || oConvertUtils.isEmpty(realnameAuth.getIdNo())){
			return Result.error("参数错误");
		}
		try{
			boolean result=realnameAuthService.addOrUpdateRealNameAuth(realnameAuth);
			if (result){
				return Result.ok(true);
			}
			return Result.error("提交失败");
		}catch (Exception e){
			return Result.error(e.getMessage());
		}
	}

	/**
	 * 查询认证详情
	 *
	 * @return
	 */
	@ApiOperation("查询认证详情")
	@GetMapping(value = "/getRealNameAuth")
	public Result<Object> getRealNameAuth() {
		try {
			LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
			UmsRealnameAuth auth=realnameAuthService.getRealNameAuth(user.getId());
			return Result.ok(auth);
		} catch (Exception e) {
			log.error("操作异常", e);
			return Result.error(e.getMessage());
		}
	}


	/**
	 * 企业认证
	 */
	@ApiOperation(value = "企业认证", notes = "企业认证")
	@PostMapping(value = "/addCompanyAuth")
	public Result<?> addCompanyAuth(@RequestBody JobCompany company){
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		company.setUserId(user.getId());
		if (oConvertUtils.isEmpty(company.getLegalPerson()) || oConvertUtils.isEmpty(company.getRealName())|| oConvertUtils.isEmpty(company.getIdentity())){
			return Result.error("参数错误");
		}
		try{
			boolean result=companyService.addOrUpdateCompanyAuth(company);
			if (result){
				return Result.ok(true);
			}
			return Result.error("提交失败");
		}catch (Exception e){
			return Result.error(e.getMessage());
		}
	}


	/**
	 * 查询企业认证详情
	 *
	 * @return
	 */
	@ApiOperation("查询企业认证详情")
	@GetMapping(value = "/getCompanyAuth")
	public Result<Object> getCompanyAuth() {
		try {
			LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
			JobCompany auth=companyService.getCompanyAuth(user.getId());
			return Result.ok(auth);
		} catch (Exception e) {
			log.error("操作异常", e);
			return Result.error(e.getMessage());
		}
	}


}