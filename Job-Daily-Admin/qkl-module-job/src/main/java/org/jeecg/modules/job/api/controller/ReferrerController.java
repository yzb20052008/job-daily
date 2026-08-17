package org.jeecg.modules.job.api.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.job.ums.entity.UmsReferrerLog;
import org.jeecg.modules.job.ums.service.IUmsReferrerLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 邀新管理
 */
@RestController
@RequestMapping("/api/referrer")
@Api(tags="移动端邀新管理")
@Slf4j
public class ReferrerController {
	@Autowired
	private ISysBaseAPI sysBaseApi;
	@Resource
	private IUmsReferrerLogService referrerLogService;

	/**
	 * 查询邀请列表
	 */
	@RequestMapping(value = "/getReferrerList",method = RequestMethod.GET)
	@ApiOperation(value = "查询邀请列表", notes = "查询邀请列表")
	public Result<Object> getReferrerList(UmsReferrerLog param,
										  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
										  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize
	) {
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		param.setReferrerId(user.getId());
		IPage<UmsReferrerLog> pageInfo= referrerLogService.getReferrerPageList(new Page<>(pageNo,pageSize),param);
		return Result.OK(pageInfo);
	}

	/**
	 * 查询推广统计信息
	 */
	@RequestMapping(value = "/getReferrerCount",method = RequestMethod.GET)
	@ApiOperation(value = "查询推广统计信息", notes = "查询推广统计信息")
	public Result<Object> getReferrerCount() {
		Map<String,Object> map=new HashMap<>();
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		//累计邀请人数
		UmsReferrerLog log1=referrerLogService.getReferrerCount(user.getId(),null,null);
		map.put("totalNumber",log1.getTotalNumber());
		map.put("totalIntegral",log1.getIntegralCount());
		//本月
		UmsReferrerLog log2=referrerLogService.getReferrerCount(user.getId(),DateUtils.getCurrentMonthFirst(),DateUtils.getCurrentMonthLast());
		map.put("currentNumber",log2.getTotalNumber());
		map.put("currentIntegral",log2.getIntegralCount());
		return Result.OK(map);
	}

}