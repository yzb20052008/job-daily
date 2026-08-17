package org.jeecg.modules.job.api.controller;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.job.ums.entity.UmsPayType;
import org.jeecg.modules.job.ums.entity.UmsSign;
import org.jeecg.modules.job.ums.entity.UmsVip;
import org.jeecg.modules.job.ums.entity.UmsVipOrders;
import org.jeecg.modules.job.ums.service.IUmsPayTypeService;
import org.jeecg.modules.job.ums.service.IUmsSignService;
import org.jeecg.modules.job.ums.service.IUmsVipOrdersService;
import org.jeecg.modules.job.ums.service.IUmsVipService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;


/**
 * 签到管理模块
 */
@RestController
@RequestMapping("/api/sign")
@Api(tags="移动端签到管理模块")
@Slf4j
public class SignController {

    @Resource
    private IUmsSignService signService;

    @ApiOperation("查询签到情况")
    @GetMapping(value = "/getSignedList")
    public Result<Object> getSignedList(@RequestParam(value = "startDate", defaultValue = "") String startDate) {
        try {
            LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.ge("sign_date", startDate);
            queryWrapper.eq("user_id", user.getId());
            queryWrapper.orderByDesc("create_time");
            List<UmsSign> list = signService.list(queryWrapper);
            return Result.ok(list);
        } catch (Exception e) {
            log.error("查询签到情况：%s", e.getMessage(), e);
            return Result.error("查询失败："+e.getMessage());
        }
    }


    @ApiOperation("签到")
    @PostMapping(value = "/sign")
    public Result<Object> sign() {
        try {
            LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            return Result.ok(signService.addSign(user.getId()));
        } catch (Exception e) {
            log.error("签到：%s", e.getMessage(), e);
            return Result.error("签到失败："+e.getMessage());
        }
    }


    @ApiOperation("补签")
    @PostMapping(value = "/reSign")
    public Result<Object> reSign(@RequestBody UmsSign entity) {
        try {
            LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            return Result.ok(signService.addReSign(user.getId(), entity.getSignDate()));
        } catch (Exception e) {
            log.error("补签：%s", e.getMessage(), e);
            return Result.error("补签失败："+e.getMessage());
        }
    }


    @ApiOperation("查询今明两天签到情况")
    @GetMapping(value = "/getLastSign")
    public Result<Object> getLastSign() {
        try {
            LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            QueryWrapper queryWrapper = new QueryWrapper();
            //前一天
            String lastDay= DateUtils.formatDate(DateUtil.offsetDay(new Date(),-1),"yyyy-MM-dd");
            queryWrapper.ge("sign_date", lastDay);
            queryWrapper.eq("user_id", user.getId());
            queryWrapper.orderByDesc("sign_date");
            List<UmsSign> sign = signService.list(queryWrapper);
            return Result.ok(sign);
        } catch (Exception e) {
            log.error("查询签到情况：%s", e.getMessage(), e);
            return Result.error("查询失败："+e.getMessage());
        }
    }

    @ApiOperation("查询签到积分")
    @GetMapping(value = "/getSignIntegral")
    public Result<Object> getSignIntegral() {
        try {
            LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            return Result.ok(signService.getLeftReSignCount(user.getId()));
        } catch (Exception e) {
            log.error("查询签到情况：%s", e.getMessage(), e);
            return Result.error("查询失败："+e.getMessage());
        }
    }

}