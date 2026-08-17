package org.jeecg.modules.job.home.controller;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.job.home.service.IHomeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;


@Api(tags="首页统计")
@RestController
@RequestMapping("/home/homeController")
@Slf4j
public class HomeController{

    @Resource
    private IHomeService homeService;

    /**
     * 查询用户统计信息
     *
     * @return
     */
    @ApiOperation("查询用户统计信息")
    @GetMapping(value = "/getUserStatistics")
    public Result<Object> getUserStatistics() {
        try {
            String today = DateUtil.today();
            String tomorrow = DateUtil.formatDate(DateUtil.tomorrow());
            String yesterday = DateUtil.formatDate(DateUtil.yesterday());
            //用户总数
            Map<String,Object> userCount = homeService.getUserCountInfo(null, null,null);
            //今日新增用户数
            Map<String,Object> todayCount = homeService.getUserCountInfo(today, tomorrow,null);
            //昨日新增
            Map<String,Object> yesterdayCount = homeService.getUserCountInfo(yesterday, today,null);
            Map<String, Object> map = new HashMap<>();
            map.put("userCount", userCount);
            map.put("todayCount", todayCount);
            map.put("yesterdayCount", yesterdayCount);
            return Result.ok(map);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询其他统计信息
     *
     * @return
     */
    @ApiOperation("查询统计信息")
    @GetMapping(value = "/getComStatistics")
    public Result<Object> getComStatistics() {
        String orgCode=null;
        try {
            String today = DateUtil.today();
            String tomorrow = DateUtil.formatDate(DateUtil.tomorrow());
            String yesterday = DateUtil.formatDate(DateUtil.yesterday());
            //全部企业
            Map<String,Object> companyCount = this.homeService.getCompanyCountInfo(null,null,null);
            //今日企业
            Map<String,Object> todayCompanyCount = homeService.getCompanyCountInfo(today, tomorrow,null);
            //昨日企业信息
            Map<String,Object> yesterdayCompanyCount = homeService.getCompanyCountInfo(yesterday, today,null);
            Map<String, Object> map = new HashMap<>();
            map.put("companyCount", companyCount);
            map.put("todayCompanyCount", todayCompanyCount);
            map.put("yesterdayCompanyCount", yesterdayCompanyCount);

            //全部职位
            Map<String,Object> positionCount = this.homeService.getPositionCountInfo(null,null,null);
            //今日职位
            Map<String,Object> todayPositionCount = homeService.getPositionCountInfo(today, tomorrow,null);
            //昨日职位
            Map<String,Object> yesterdayPositionCount = homeService.getPositionCountInfo(yesterday, today,null);
            map.put("positionCount", positionCount);
            map.put("todayPositionCount", todayPositionCount);
            map.put("yesterdayPositionCount", yesterdayPositionCount);

            //全部订单
            Map<String,Object> orderCount = this.homeService.getOrderCountInfo(null,null,null);
            //今日订单
            Map<String,Object> todayOrderCount = homeService.getOrderCountInfo(today, tomorrow,null);
            //昨日订单
            Map<String,Object> yesterdayOrderCount = homeService.getOrderCountInfo(yesterday, today,null);
            map.put("orderCount", orderCount);
            map.put("todayOrderCount", todayOrderCount);
            map.put("yesterdayOrderCount", yesterdayOrderCount);

            return Result.ok(map);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }
}
