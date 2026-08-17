package org.jeecg.modules.job.integral.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.job.constant.BizConstants;
import org.jeecg.modules.job.integral.entity.IntegralGoods;
import org.jeecg.modules.job.integral.entity.IntegralGoodsEffect;
import org.jeecg.modules.job.integral.entity.IntegralGoodsOrder;
import org.jeecg.modules.job.integral.entity.IntegralLog;
import org.jeecg.modules.job.integral.mapper.IntegralGoodsMapper;
import org.jeecg.modules.job.integral.mapper.IntegralGoodsOrderMapper;
import org.jeecg.modules.job.integral.mapper.IntegralLogMapper;
import org.jeecg.modules.job.integral.service.IIntegralGoodsEffectService;
import org.jeecg.modules.job.integral.service.IIntegralGoodsOrderService;
import org.jeecg.modules.job.ums.entity.UmsAccount;
import org.jeecg.modules.job.ums.service.IUmsAccountService;
import org.jeecg.modules.job.ums.service.IUmsParamLimitService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * @Description: 积分订单
 * @Author: qingkonglan
 * @Date:   2024-07-30
 * @Version: V1.0
 */
@Service
public class IntegralGoodsOrderServiceImpl extends ServiceImpl<IntegralGoodsOrderMapper, IntegralGoodsOrder> implements IIntegralGoodsOrderService {

    @Resource
    private IntegralGoodsMapper jfGoodsMapper;
    @Resource
    private ISysBaseAPI sysBaseAPI;
    @Resource
    private IntegralLogMapper integralLogMapper;
    @Resource
    private IUmsParamLimitService paramLimitService;
    @Resource
    private IIntegralGoodsEffectService goodsEffectService;
    @Resource
    private IUmsAccountService accountService;


    @Transactional
    @Override
    public boolean createJfOrder(String userId, String number, String goodsId, String amount, String dataId) {
        //判断金额是否匹配
        IntegralGoods goods=jfGoodsMapper.selectById(goodsId);
        if (goods==null){
            throw new RuntimeException("参数异常");
        }
        BigDecimal total=goods.getIntegral().multiply(new BigDecimal(number));
        if (total.compareTo(new BigDecimal(amount))!=0){
            throw new RuntimeException("支付金额异常，请重新提交");
        }
        LoginUser user = sysBaseAPI.getUserById(userId);
        //判断积分是否足够
        if (user.getIntegral() < new BigDecimal(amount).intValue()){
            throw new RuntimeException("积分不足");
        }
        //生成订单
        IntegralGoodsOrder order=new IntegralGoodsOrder();
        //生成订单编号
        String no= DateUtils.formatDate(new Date(),"yyyyMMddHHmmss")+ RandomUtil.randomNumbers(5);
        order.setOrderSn(no);
        order.setAmount(new BigDecimal(amount));
        order.setPrice(goods.getIntegral());
        order.setNumber(new Integer(number));
        order.setGoodsId(goodsId);
        order.setPayType(BizConstants.PAY_TYPE_JF);
        order.setUserId(user.getId());
        order.setOrderStatus(BizConstants.ORDER_STATUS_SUCCESS.toString());
        this.save(order);
        //扣除积分
        user.setIntegral(user.getIntegral()-order.getAmount().intValue());
        sysBaseAPI.updateUserInfo(user);
        //积分记录
        IntegralLog log=new IntegralLog();
        log.setUserId(user.getId());
        log.setIfAdd(0);
        log.setIntegralResource(BizConstants.INTEGRAL_RESOURCE_PLANER);
        log.setDataId(order.getId());
        log.setIntegral(order.getAmount());
        log.setRemark("购买积分道具-"+goods.getName()+"消费积分："+order.getAmount().toString());
        integralLogMapper.insert(log);

        //积分道具生效
        if (goods.getCode().equals(BizConstants.JF_CODE_AI)){
            //AI卡
            paramLimitService.addAi(userId,10);//10次卡
        }else if (goods.getCode().equals(BizConstants.JF_CODE_REFRESH)){//刷新卡
            //刷新提交时间
            if (oConvertUtils.isEmpty(dataId)){
                throw new RuntimeException("参数错误");
            }
            //刷新时间
//            UmsApply apply=applyService.getById(dataId);
//            if (apply!=null){
//                apply.setRefreshTime(new Date());
//                applyService.updateById(apply);
//            }else{
//                throw new RuntimeException("参数错误");
//            }
        }else if (goods.getCode().equals(BizConstants.JF_CODE_TOPPING) || goods.getCode().equals(BizConstants.JF_CODE_EYE)){//置顶卡/加粗
            if (oConvertUtils.isEmpty(dataId)){//岗位ID不能为空
                throw new RuntimeException("参数错误");
            }
            //
            IntegralGoodsEffect effect=new IntegralGoodsEffect();
            effect.setDataId(dataId);
            effect.setGoodsCode(goods.getCode());
            effect.setGoodsId(goods.getId());
            effect.setStatus(1);//启用
            effect.setStartTime(new Date());
            effect.setEndTime(DateUtil.offsetHour(effect.getStartTime(),goods.getPeriod()));
            effect.setUserId(userId);
            effect.setPeriod(goods.getPeriod());
            goodsEffectService.addOrUpdateEffect(effect);
            if (goods.getCode().equals(BizConstants.JF_CODE_TOPPING)){//置顶
                //岗位置顶
//                RmsCompanyPosition position=new RmsCompanyPosition().setId(dataId).setIfTopping(1);
//                positionService.updateById(position);
            }else if (goods.getCode().equals(BizConstants.JF_CODE_EYE)){//加粗
                //岗位加粗
//                RmsCompanyPosition position=new RmsCompanyPosition().setId(dataId).setIfBold(1);
//                positionService.updateById(position);
            }
        }else if (goods.getCode().equals(BizConstants.JF_CODE_PERSPECTIVE)){//透视卡

        }
        return true;
    }


    @Override
    public IPage<Map<String, Object>> getGoodsOrderList(Page<IntegralGoodsOrder> page, IntegralGoodsOrder params) {
        return baseMapper.getGoodsOrderList(page,params);
    }
}
