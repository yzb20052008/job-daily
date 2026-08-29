package org.jeecg.modules.job.api.controller;

import cn.hutool.core.date.DateUtil;
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
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.job.constant.BizConstants;
import org.jeecg.modules.job.integral.entity.IntegralLog;
import org.jeecg.modules.job.job.entity.JobPost;
import org.jeecg.modules.job.job.service.IJobPostService;
import org.jeecg.modules.job.job.vo.JobPostVo;
import org.jeecg.modules.job.ums.entity.UmsReferrerLog;
import org.jeecg.modules.job.ums.entity.UmsSign;
import org.jeecg.modules.job.ums.service.IUmsSignService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 招工信息管理模块
 */
@RestController
@RequestMapping("/api/post")
@Api(tags="移动端招工管理模块")
@Slf4j
public class PostController {

    @Resource
    private IJobPostService postService;

    /**
     * 查询招工列表
     */
    @RequestMapping(value = "/getPostList",method = RequestMethod.GET)
    @ApiOperation(value = "查询招工列表", notes = "查询招工列表")
    public Result<Object> getPostList(JobPost param,
                                      @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                      @RequestParam(name="pageSize", defaultValue="10") Integer pageSize
    ) {
        param.setPostStatus(BizConstants.POST_STATUS_RUNNING);
        IPage<Map<String,Object>> pageInfo= postService.getPostMapList(new Page<>(pageNo,pageSize),param);
        return Result.OK(pageInfo);
    }

    /**
     * 查询我的招工列表
     */
    @RequestMapping(value = "/getMyPostList",method = RequestMethod.GET)
    @ApiOperation(value = "查询我的招工列表", notes = "查询我的招工列表")
    public Result<Object> getMyPostList(JobPost param,
                                      @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                      @RequestParam(name="pageSize", defaultValue="10") Integer pageSize
    ) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        param.setUserId(user.getId());
        IPage<JobPost> pageInfo= postService.getMyPostList(new Page<>(pageNo,pageSize),param);
        return Result.OK(pageInfo);
    }

    /**
     * 查询招工基本信息
     */
    @ResponseBody
    @RequestMapping(value = "/getPostInfo", method = RequestMethod.GET)
    @ApiOperation(value = "查询招工基本信息", notes = "查询招工基本信息")
    public Result<Object> getPostInfo(@RequestParam("id") String id) {
        try {
            JobPost postVo=postService.getById(id);
            return Result.OK(postVo);
        } catch (Exception e) {
			log.error("操作异常", e);
			return Result.error(e.getMessage());
        }
    }

    /**
     * 查询招工详情
     */
    @ResponseBody
    @RequestMapping(value = "/getPostDetail", method = RequestMethod.GET)
    @ApiOperation(value = "查询招工详情", notes = "查询招工详情")
    public Result<Object> getPostDetail(@RequestParam("id") String id,@RequestParam(value = "userId",required = false) String userId) {
        try {
            JobPostVo postVo=postService.getPostDetail(id,userId);
            return Result.OK(postVo);
        } catch (Exception e) {
			log.error("操作异常", e);
			return Result.error(e.getMessage());
        }
    }

    @ApiOperation("新增招工信息")
    @PostMapping(value = "/addPost")
    public Result<Object> addPost(@RequestBody JobPost post) {
        try {
            LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            post.setUserId(user.getId());
            post.setPostStatus(BizConstants.POST_STATUS_RUNNING);//默认直接上线
            boolean result=postService.addPostInfo(post);
            return Result.ok(result);
        } catch (Exception e) {
            log.error("新增招工信息：%s", e.getMessage(), e);
            return Result.error("操作失败："+e.getMessage());
        }
    }

    @ApiOperation("编辑招工信息")
    @PostMapping(value = "/updatePost")
    public Result<Object> updatePost(@RequestBody JobPost post) {
        try {
            boolean result=postService.updatePostInfo(post);
            return Result.ok(result);
        } catch (Exception e) {
            log.error("编辑招工信息：%s", e.getMessage(), e);
            return Result.error("操作失败："+e.getMessage());
        }
    }

    @ApiOperation("修改招工状态")
    @PostMapping(value = "/updatePostStatus")
    public Result<Object> updatePostStatus(@RequestBody JSONObject jsonObject) {
        try {
            String id = jsonObject.getString("id");
            String postStatus = jsonObject.getString("postStatus");
            if (oConvertUtils.isEmpty(id) || oConvertUtils.isEmpty(postStatus)){
                return Result.error("参数错误");
            }
            boolean result=postService.updatePostStatus(id,postStatus);
            return Result.ok(result);
        } catch (Exception e) {
            log.error("修改招工状态：%s", e.getMessage(), e);
            return Result.error("操作失败："+e.getMessage());
        }
    }





}