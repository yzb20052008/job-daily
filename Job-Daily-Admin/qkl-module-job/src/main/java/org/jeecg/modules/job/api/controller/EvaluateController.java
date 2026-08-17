package org.jeecg.modules.job.api.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.job.constant.BizConstants;
import org.jeecg.modules.job.job.entity.JobCollect;
import org.jeecg.modules.job.job.entity.JobEvaluateLog;
import org.jeecg.modules.job.job.entity.JobPostContact;
import org.jeecg.modules.job.job.service.IJobCollectService;
import org.jeecg.modules.job.job.service.IJobEvaluateLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Description: 评价相关接口
 * @Author: qingkonglan
 * @Date:   2024-08-22
 * @Version: V1.0
 */
@RestController
@RequestMapping("/api/evaluate")
@Api(tags="移动端收藏管理模块")
@Slf4j
public class EvaluateController {
	@Autowired
	private IJobEvaluateLogService evaluateLogService;

	/**
	 * 添加评价信息
	 */
	@RequestMapping(value = "/addEvaluate",method = RequestMethod.POST)
	@ApiOperation(value = "添加评价信息", notes = "添加评价信息")
	public Result<Object> addEvaluate(@RequestBody JobEvaluateLog param) {
		try {
			LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
			if (BizConstants.ROLE_CODE_COMPANY.equals(param.getRoleCode())){
				param.setPostUserId(user.getId());
			}else{
				param.setUserId(user.getId());
			}
			evaluateLogService.addEvaluateLog(param);
			return Result.OK(true);
		}catch (Exception e){
			return Result.error(e.getMessage());
		}
	}

	/**
	 * 查询我的评价记录
	 */
	@RequestMapping(value = "/getMyEvaluateList",method = RequestMethod.GET)
	@ApiOperation(value = "查询我的评价记录", notes = "查询我的评价记录")
	public Result<Object> getMyEvaluateList(JobEvaluateLog param,
										 @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
										 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize
	) {
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		if (BizConstants.ROLE_CODE_MEMBER.equals(param.getRoleCode())){
			param.setUserId(user.getId());
		}else{
			param.setPostUserId(user.getId());
		}
		IPage<Map<String,Object>> pageInfo= evaluateLogService.getMyEvaluateList(new Page<>(pageNo,pageSize),param);
		return Result.OK(pageInfo);
	}


	/**
	 * 查询指定用户的评价记录
	 */
	@RequestMapping(value = "/getEvaluateList",method = RequestMethod.GET)
	@ApiOperation(value = "查询指定用户的评价记录", notes = "查询指定用户的评价记录")
	public Result<Object> getEvaluateList(JobEvaluateLog param,
											@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
											@RequestParam(name="pageSize", defaultValue="10") Integer pageSize
	) {
		IPage<Map<String,Object>> pageInfo= evaluateLogService.getEvaluateList(new Page<>(pageNo,pageSize),param);
		return Result.OK(pageInfo);
	}

}