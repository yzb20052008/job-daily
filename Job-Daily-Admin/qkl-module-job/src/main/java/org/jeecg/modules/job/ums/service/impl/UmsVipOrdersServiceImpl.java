package org.jeecg.modules.job.ums.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.job.constant.BizConstants;
import org.jeecg.modules.job.ums.entity.UmsVip;
import org.jeecg.modules.job.ums.entity.UmsVipOrders;
import org.jeecg.modules.job.ums.mapper.UmsVipMapper;
import org.jeecg.modules.job.ums.mapper.UmsVipOrdersMapper;
import org.jeecg.modules.job.ums.service.IUmsUserVipService;
import org.jeecg.modules.job.ums.service.IUmsVipOrdersService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @Description: VIP订单
 * @Author: qingkonglan
 * @Date:   2022-12-18
 * @Version: V1.0
 */
@Service
public class UmsVipOrdersServiceImpl extends ServiceImpl<UmsVipOrdersMapper, UmsVipOrders> implements IUmsVipOrdersService {

    @Resource
    private UmsVipMapper umsVipMapper;
    @Resource
    private ISysBaseAPI sysBaseAPI;
    @Resource
    private IUmsUserVipService umsUserVipService;
//    @Resource
//    private UmsIntegralLogMapper integralLogMapper;

    @Transactional
    @Override
    public boolean createVipOrder(UmsVipOrders vipOrders) {
        //生成订单
        UmsVip vip=umsVipMapper.selectById(vipOrders.getVipId());
        if (vip==null){
            throw new RuntimeException("参数异常");
        }
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //生成订单编号
        String no= DateUtils.formatDate(new Date(),"yyyyMMddHHmmss")+ RandomUtil.randomNumbers(5);
        vipOrders.setOrdersSn(no);
        vipOrders.setVipName(vip.getName());
        vipOrders.setMemberId(user.getId());
        vipOrders.setOrdersStatus(BizConstants.ORDER_STATUS_DEFAULT);
        vipOrders.setTotalAmount(vip.getPrice());
        vipOrders.setPayAmount(vip.getPrice());
        this.save(vipOrders);
        //积分支付
        if (vipOrders.getPayType().equals(BizConstants.PAY_TYPE_JF)){
            //判断积分是否足够支付
            if (user.getIntegral() < vip.getIntegral()){
                throw new RuntimeException("积分不足");
            }
            //扣除积分
            user.setIntegral(user.getIntegral()-vip.getIntegral());
            //更新会员信息
            sysBaseAPI.updateUserInfo(user);
            //更新vip信息
            umsUserVipService.addOrUpdateVip(vip,user.getId());
            vipOrders.setOrdersStatus(BizConstants.ORDER_STATUS_SUCCESS);
            this.updateById(vipOrders);

//            //积分记录
//            UmsIntegralLog log=new UmsIntegralLog();
//            log.setUserId(user.getId());
//            log.setIfAdd(0);
//            log.setIntegralResource(BizConstants.INTEGRAL_RESOURCE_BUYVIP);
//            log.setDataId(vipOrders.getId());
//            log.setIntegral(vip.getIntegral());
//            log.setRemark("积分购买VIP会员："+vip.getIntegral());
//            integralLogMapper.insert(log);
        }else{
            throw new RuntimeException("当前仅支持积分支付");
        }
        return true;
    }

    @Override
    public IPage<UmsVipOrders> getPageListForAdmin(Page<UmsVipOrders> page, UmsVipOrders paramCondition) {
        return baseMapper.getPageListForAdmin(page,paramCondition);
    }
}
