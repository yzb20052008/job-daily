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
import org.jeecg.modules.job.job.entity.JobPost;
import org.jeecg.modules.job.job.service.IJobPostService;
import org.jeecg.modules.job.ums.service.IUmsAccountService;
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
    private IIntegralGoodsEffectService goodsEffectService;
    @Resource
    private IUmsAccountService accountService;
    @Resource
    private IJobPostService postService;

    @Transactional
    @Override
    public boolean createJfOrder(String userId, String number, String goodsId, String amount, String dataId) {
        IntegralGoods goods = jfGoodsMapper.selectById(goodsId);
        if (goods == null) {
            throw new RuntimeException("参数异常");
        }
        // AI 卡已下线
        if (BizConstants.JF_CODE_AI.equals(goods.getCode()) || "ai".equals(goods.getCode())) {
            throw new RuntimeException("该道具已下线");
        }
        BigDecimal total = goods.getIntegral().multiply(new BigDecimal(number));
        if (total.compareTo(new BigDecimal(amount)) != 0) {
            throw new RuntimeException("支付金额异常，请重新提交");
        }
        LoginUser user = sysBaseAPI.getUserById(userId);
        if (user.getIntegral() < new BigDecimal(amount).intValue()) {
            throw new RuntimeException("积分不足");
        }
        IntegralGoodsOrder order = new IntegralGoodsOrder();
        String no = DateUtils.formatDate(new Date(), "yyyyMMddHHmmss") + RandomUtil.randomNumbers(5);
        order.setOrderSn(no);
        order.setAmount(new BigDecimal(amount));
        order.setPrice(goods.getIntegral());
        order.setNumber(Integer.valueOf(number));
        order.setGoodsId(goodsId);
        order.setPayType(BizConstants.PAY_TYPE_JF);
        order.setUserId(user.getId());
        order.setOrderStatus(BizConstants.ORDER_STATUS_SUCCESS.toString());
        this.save(order);

        user.setIntegral(user.getIntegral() - order.getAmount().intValue());
        sysBaseAPI.updateUserInfo(user);

        IntegralLog log = new IntegralLog();
        log.setUserId(user.getId());
        log.setIfAdd(0);
        log.setIntegralResource(BizConstants.INTEGRAL_RESOURCE_PLANER);
        log.setDataId(order.getId());
        log.setIntegral(order.getAmount());
        log.setRemark("购买积分道具-" + goods.getName() + "消费积分：" + order.getAmount().toString());
        integralLogMapper.insert(log);

        applyGoodsEffect(userId, goods, dataId);
        return true;
    }

    /**
     * 道具生效：刷新/置顶/加粗写回 JobPost
     */
    private void applyGoodsEffect(String userId, IntegralGoods goods, String dataId) {
        String code = goods.getCode();
        if (BizConstants.JF_CODE_REFRESH.equals(code)) {
            JobPost post = requireOwnPost(userId, dataId);
            postService.updateById(new JobPost().setId(post.getId()).setCreateTime(new Date()));
            return;
        }
        if (BizConstants.JF_CODE_TOPPING.equals(code) || BizConstants.JF_CODE_EYE.equals(code)) {
            JobPost post = requireOwnPost(userId, dataId);
            IntegralGoodsEffect effect = new IntegralGoodsEffect();
            effect.setDataId(post.getId());
            effect.setGoodsCode(code);
            effect.setGoodsId(goods.getId());
            effect.setStatus(1);
            effect.setStartTime(new Date());
            effect.setEndTime(DateUtil.offsetHour(effect.getStartTime(), goods.getPeriod()));
            effect.setUserId(userId);
            effect.setPeriod(goods.getPeriod());
            goodsEffectService.addOrUpdateEffect(effect);
            if (BizConstants.JF_CODE_TOPPING.equals(code)) {
                postService.updateById(new JobPost().setId(post.getId()).setIfTopping(1));
            } else {
                postService.updateById(new JobPost().setId(post.getId()).setIfBold(1));
            }
        }
    }

    private JobPost requireOwnPost(String userId, String dataId) {
        if (oConvertUtils.isEmpty(dataId)) {
            throw new RuntimeException("参数错误");
        }
        JobPost post = postService.getById(dataId);
        if (post == null) {
            throw new RuntimeException("岗位不存在");
        }
        if (!userId.equals(post.getUserId())) {
            throw new RuntimeException("只能对自己的岗位使用道具");
        }
        return post;
    }

    @Override
    public IPage<Map<String, Object>> getGoodsOrderList(Page<IntegralGoodsOrder> page, IntegralGoodsOrder params) {
        return baseMapper.getGoodsOrderList(page, params);
    }
}
