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
import org.jeecg.modules.job.constant.BizConstants;
import org.jeecg.modules.job.job.entity.JobPostContact;
import org.jeecg.modules.job.job.service.IJobPostContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Description: 通话记录管理
 * @Author: qingkonglan
 * @Date:   2024-08-22
 * @Version: V1.0
 */
@RestController
@RequestMapping("/api/contact")
@Api(tags="移动端收藏管理模块")
@Slf4j
public class ContactController {
	@Autowired
	private IJobPostContactService postContactService;

	/**
	 * 添加联系记录
	 *
	 * @param contact
	 * @return
	 */
	@ApiOperation("添加联系记录")
	@PostMapping(value = "/addContact")
	public Result<Object> addContact(@RequestBody JobPostContact contact) {
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		if (contact.getRoleCode().equals(BizConstants.ROLE_CODE_MEMBER)){
			//工人端
			contact.setUserId(user.getId());
			if(oConvertUtils.isEmpty(contact.getPostId()) || oConvertUtils.isEmpty(contact.getPostUserId())){
				return Result.error("参数异常");
			}
		}else{
			//老板端
			contact.setPostUserId(user.getId());
			if(oConvertUtils.isEmpty(contact.getUserId()) || oConvertUtils.isEmpty(contact.getRoleCode())){
				return Result.error("参数异常");
			}
		}
		try {
			boolean result=postContactService.addContact(contact);
			return Result.ok(result);
		} catch (Exception e) {
			log.error("操作异常", e);
			return Result.error(e.getMessage());
		}
	}

	/**
	 * 移除联系记录
	 *
	 * @param contact
	 * @return
	 */
	@ApiOperation("移除联系记录")
	@PostMapping(value = "/delContact")
	public Result<Object> delContact(@RequestBody JobPostContact contact) {
		if(oConvertUtils.isEmpty(contact.getId())){
			return Result.error("参数异常");
		}
		try {
			boolean result=postContactService.removeById(contact.getId());
			return Result.ok(result);
		} catch (Exception e) {
			log.error("操作异常", e);
			return Result.error(e.getMessage());
		}
	}

	/**
	 * 查询通话记录列表
	 */
	@RequestMapping(value = "/getContactList",method = RequestMethod.GET)
	@ApiOperation(value = "查询通话记录列表", notes = "查询通话记录列表")
	public Result<Object> getContactList(JobPostContact param,
										 @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
										 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize
	) {
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		if (BizConstants.ROLE_CODE_MEMBER.equals(param.getRoleCode())){
			param.setUserId(user.getId());
		}else{
			param.setPostUserId(user.getId());
		}
		IPage<Map<String,Object>> pageInfo= postContactService.getContactList(new Page<>(pageNo,pageSize),param);
		return Result.OK(pageInfo);
	}


	/**
	 * 提交合作意向
	 *
	 * @param contact
	 * @return
	 */
	@ApiOperation("提交合作意向")
	@PostMapping(value = "/updateAgreeState")
	public Result<Object> updateAgreeState(@RequestBody JobPostContact contact) {
		if(oConvertUtils.isEmpty(contact.getId()) || oConvertUtils.isEmpty(contact.getAgreeState())){
			return Result.error("参数异常");
		}
		try {
			LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
			boolean result=postContactService.updateAgreeState(contact.getId(),contact.getAgreeState(),user.getId());
			return Result.ok(result);
		} catch (Exception e) {
			log.error("操作异常", e);
			return Result.error(e.getMessage());
		}
	}

}