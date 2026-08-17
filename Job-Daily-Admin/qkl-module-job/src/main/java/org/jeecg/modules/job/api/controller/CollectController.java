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
import org.jeecg.modules.job.job.entity.JobCollect;
import org.jeecg.modules.job.job.service.IJobCollectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Description: 收藏相关接口
 * @Author: qingkonglan
 * @Date:   2024-08-22
 * @Version: V1.0
 */
@RestController
@RequestMapping("/api/collect")
@Api(tags="移动端收藏管理模块")
@Slf4j
public class CollectController {
	@Autowired
	private IJobCollectService collectService;

	/**
	 * 添加/移除收藏
	 */
	@RequestMapping(value = "/updateCollect",method = RequestMethod.POST)
	@ApiOperation(value = "添加/移除收藏", notes = "添加/移除收藏")
	public Result<Object> updateCollect(@RequestBody JobCollect param) {
		try {
			LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
			param.setUserId(user.getId());
			collectService.updateCollect(param);
			return Result.OK(true);
		}catch (Exception e){
			return Result.error(e.getMessage());
		}
	}


	/**
	 * 移除收藏
	 */
	@RequestMapping(value = "/delCollect",method = RequestMethod.POST)
	@ApiOperation(value = "移除收藏", notes = "移除收藏")
	public Result<Object> delCollect(@RequestBody JobCollect param) {
		try {
			if(oConvertUtils.isEmpty(param.getId())){
				return Result.error("参数异常");
			}
			return Result.OK(collectService.removeById(param.getId()));
		}catch (Exception e){
			return Result.error(e.getMessage());
		}
	}

	/**
	 * 查询收藏列表
	 */
	@RequestMapping(value = "/getCollectList",method = RequestMethod.GET)
	@ApiOperation(value = "查询收藏列表", notes = "查询收藏列表")
	public Result<Object> getCollectList(JobCollect param,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize
	) {
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		param.setUserId(user.getId());
		IPage<Map<String,Object>> pageInfo= collectService.getCollectList(new Page<>(pageNo,pageSize),param);
		return Result.OK(pageInfo);
	}

	/**
	 * 查询统计数量
	 */
	@RequestMapping(value = "/getMyStatistics",method = RequestMethod.GET)
	@ApiOperation(value = "查询统计数量", notes = "查询统计数量")
	public Result<Object> getMyStatistics(@RequestParam(name="roleCode") String roleCode){
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		Map<String,Object> map=collectService.getMyStatistics(user.getId(),roleCode);
		return Result.OK(map);
	}

}