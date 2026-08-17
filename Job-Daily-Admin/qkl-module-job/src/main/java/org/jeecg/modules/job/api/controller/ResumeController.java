package org.jeecg.modules.job.api.controller;

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
import org.jeecg.modules.job.job.entity.*;
import org.jeecg.modules.job.job.service.*;
import org.jeecg.modules.job.job.vo.JobResumeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Description: 简历相关接口
 * @Author: qingkonglan
 * @Date:   2024-08-22
 * @Version: V1.0
 */
@RestController
@RequestMapping("/api/resume")
@Api(tags="移动端简历管理模块")
@Slf4j
public class ResumeController {
	@Autowired
	private IJobResumeService resumeService;
	@Autowired
	private IJobResumeIntentionService intentionService;
	@Autowired
	private IJobResumeExpeService expService;
	@Autowired
	private IJobResumeCertService certService;


	/**
	 * 添加/更新简历
	 */
	@RequestMapping(value = "/updateResume",method = RequestMethod.POST)
	@ApiOperation(value = "添加/更新简历", notes = "添加/更新简历")
	public Result<Object> addResume(@RequestBody JobResume param) {
		try {
			LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
			param.setUserId(user.getId());
			resumeService.updateResume(param);
			return Result.OK(true);
		}catch (Exception e){
			return Result.error(e.getMessage());
		}
	}

	/**
	 * 添加/更新求职意向
	 */
	@RequestMapping(value = "/updateIntention",method = RequestMethod.POST)
	@ApiOperation(value = "添加/更新求职意向", notes = "添加/更新求职意向")
	public Result<Object> updateIntention(@RequestBody JobResumeIntention param) {
		try {
			LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
			param.setUserId(user.getId());
			intentionService.updateResumeIntention(param);
			return Result.OK(true);
		}catch (Exception e){
			return Result.error(e.getMessage());
		}
	}


	/**
	 * 添加工作经验
	 */
	@RequestMapping(value = "/addResumeExp",method = RequestMethod.POST)
	@ApiOperation(value = "添加工作经验", notes = "添加工作经验")
	public Result<Object> addResumeExp(@RequestBody JobResumeExpe param) {
		try {
			LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
			param.setUserId(user.getId());
			expService.addResumeExp(param);
			resumeService.updateResumePercentage(user.getId(),20);
			return Result.OK(true);
		}catch (Exception e){
			return Result.error(e.getMessage());
		}
	}


	/**
	 * 更新工作经验
	 */
	@RequestMapping(value = "/updateResumeExp",method = RequestMethod.POST)
	@ApiOperation(value = "更新工作经验", notes = "添更新工作经验")
	public Result<Object> updateResumeExp(@RequestBody JobResumeExpe param) {
		try {
			LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
			param.setUserId(user.getId());
			expService.updateResumeExp(param);
			return Result.OK(true);
		}catch (Exception e){
			return Result.error(e.getMessage());
		}
	}


	/**
	 * 添加技能证书
	 */
	@RequestMapping(value = "/addResumeCert",method = RequestMethod.POST)
	@ApiOperation(value = "添加技能证书", notes = "添加技能证书")
	public Result<Object> addResumeCert(@RequestBody JobResumeCert param) {
		try {
			LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
			param.setUserId(user.getId());
			certService.addResumeCert(param);
			resumeService.updateResumePercentage(user.getId(),20);
			return Result.OK(true);
		}catch (Exception e){
			return Result.error(e.getMessage());
		}
	}


	/**
	 * 更新技能证书
	 */
	@RequestMapping(value = "/updateResumeCert",method = RequestMethod.POST)
	@ApiOperation(value = "更新技能证书", notes = "更新技能证书")
	public Result<Object> updateResumeCert(@RequestBody JobResumeCert param) {
		try {
			LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
			param.setUserId(user.getId());
			certService.updateResumeCert(param);
			return Result.OK(true);
		}catch (Exception e){
			return Result.error(e.getMessage());
		}
	}

	/**
	 * 删除工作经验
	 */
	@RequestMapping(value = "/delResumeExp",method = RequestMethod.POST)
	@ApiOperation(value = "删除工作经验", notes = "删除工作经验")
	public Result<Object> delResumeExp(@RequestBody JobResumeExpe param) {
		try {
			if (oConvertUtils.isEmpty(param.getId())){
				return Result.error("参数错误");
			}
			return Result.OK(expService.removeById(param.getId()));
		}catch (Exception e){
			return Result.error(e.getMessage());
		}
	}

	/**
	 * 删除技能证书
	 */
	@RequestMapping(value = "/delResumeCert",method = RequestMethod.POST)
	@ApiOperation(value = "删除技能证书", notes = "删除技能证书")
	public Result<Object> delResumeCert(@RequestBody JobResumeCert param) {
		try {
			if (oConvertUtils.isEmpty(param.getId())){
				return Result.error("参数错误");
			}
			return Result.OK(certService.removeById(param.getId()));
		}catch (Exception e){
			return Result.error(e.getMessage());
		}
	}


	/**
	 * 查询简历信息(基础信息)
	 * @param userId 用户id，如果没有则查询自己的
	 */
	@RequestMapping(value = "/getResumeBase",method = RequestMethod.GET)
	@ApiOperation(value = "查询简历信息(基础信息)", notes = "查询简历信息(基础信息)")
	public Result<Object> getResumeBase(@RequestParam(name="userId", required = false) String userId) {
		try {
			if (oConvertUtils.isEmpty(userId)){
				LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
				userId=user.getId();
			}
			JobResume resumeVo=resumeService.getOne(new QueryWrapper<>(new JobResume().setUserId(userId)));
			return Result.OK(resumeVo);
		}catch (Exception e){
			return Result.error(e.getMessage());
		}
	}

	/**
	 * 查询简历信息
	 * @param userId 用户id，如果没有则查询自己的
	 */
	@RequestMapping(value = "/getResumeInfo",method = RequestMethod.GET)
	@ApiOperation(value = "查询简历信息", notes = "添加/更新简历")
	public Result<Object> getResumeInfo(@RequestParam(name="userId", required = false) String userId) {
		try {
			if (oConvertUtils.isEmpty(userId)){
				LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
				userId=user.getId();
			}
			JobResumeVo resumeVo=resumeService.getResumeInfo(userId);
			return Result.OK(resumeVo);
		}catch (Exception e){
			return Result.error(e.getMessage());
		}
	}



	/**
	 * 查询求职意向
	 * @param userId 用户id，如果没有则查询自己的
	 */
	@RequestMapping(value = "/getIntention",method = RequestMethod.GET)
	@ApiOperation(value = "查询求职意向", notes = "查询求职意向")
	public Result<Object> getIntention(@RequestParam(name="userId", required = false) String userId) {
		try {
			if (oConvertUtils.isEmpty(userId)){
				LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
				userId=user.getId();
			}
			JobResumeIntention intention=intentionService.getOne(new QueryWrapper<>(new JobResumeIntention().setUserId(userId)));
			return Result.OK(intention);
		}catch (Exception e){
			return Result.error(e.getMessage());
		}
	}


	/**
	 * 查询项目经验
	 * @param id 用户id，如果没有则查询自己的
	 */
	@RequestMapping(value = "/getResumeExp",method = RequestMethod.GET)
	@ApiOperation(value = "查询项目经验", notes = "查询项目经验")
	public Result<Object> getResumeExp(@RequestParam(name="id", required = true) String id) {
		try {
			JobResumeExpe resumeExpe=expService.getById(id);
			return Result.OK(resumeExpe);
		}catch (Exception e){
			return Result.error(e.getMessage());
		}
	}


	/**
	 * 查询项目技能证书
	 * @param id 用户id，如果没有则查询自己的
	 */
	@RequestMapping(value = "/getResumeCert",method = RequestMethod.GET)
	@ApiOperation(value = "查询项目技能证书", notes = "查询项目技能证书")
	public Result<Object> getResumeCert(@RequestParam(name="id", required = true) String id) {
		try {
			JobResumeCert resumeCert=certService.getById(id);
			return Result.OK(resumeCert);
		}catch (Exception e){
			return Result.error(e.getMessage());
		}
	}

	/**
	 * 查询简历列表
	 */
	@RequestMapping(value = "/getResumeList",method = RequestMethod.GET)
	@ApiOperation(value = "查询简历列表", notes = "查询简历列表")
	public Result<Object> getCollectList(JobResume param,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize
	) {
		IPage<Map<String,Object>> pageInfo= resumeService.getResumeList(new Page<>(pageNo,pageSize),param);
		return Result.OK(pageInfo);
	}



}