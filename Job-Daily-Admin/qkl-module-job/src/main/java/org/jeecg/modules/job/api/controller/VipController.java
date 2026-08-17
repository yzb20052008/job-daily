package org.jeecg.modules.job.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.job.ums.entity.UmsPayType;
import org.jeecg.modules.job.ums.entity.UmsVip;
import org.jeecg.modules.job.ums.entity.UmsVipOrders;
import org.jeecg.modules.job.ums.service.IUmsPayTypeService;
import org.jeecg.modules.job.ums.service.IUmsVipOrdersService;
import org.jeecg.modules.job.ums.service.IUmsVipService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


/**
 */
@RestController
@RequestMapping("/api/vip")
@Api(tags="移动端VIP管理模块")
@Slf4j
public class VipController {

    @Resource
    private IUmsVipService vipService;
    @Resource
    private IUmsPayTypeService payTypeService;
    @Resource
    private IUmsVipOrdersService ordersService;

    /**
     * 查询VIP信息
     *
     * @return
     */
    @ApiOperation(value="查询VIP信息", notes="查询VIP信息")
    @GetMapping(value = "/getVipList")
    public Result<List<UmsVip>> getVipList(String roleCode) {
        List<UmsVip> list = vipService.list(new QueryWrapper<UmsVip>().eq("status",1).eq("role_code",roleCode).orderByAsc("sort"));
        return Result.OK(list);
    }

    /**
     * 查询支付方式
     *
     * @return
     */
    @ApiOperation(value="查询支付方式", notes="查询支付方式")
    @GetMapping(value = "/getPayTypeList")
    public Result<List<UmsPayType>> getPayTypeList() {
        List<UmsPayType> list = payTypeService.list(new QueryWrapper<UmsPayType>().eq("status",1).orderByAsc("sort"));
        return Result.OK(list);
    }

    /**
     * VIP购买
     *
     * @param orders
     * @return
     */
    @ApiOperation("VIP购买")
    @PostMapping(value = "/createVipOrder")
    public Result<Object> createVipOrder(@RequestBody UmsVipOrders orders) {
        try {
            boolean result=ordersService.createVipOrder(orders);
            return Result.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }










}