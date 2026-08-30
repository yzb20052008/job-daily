package org.jeecg.modules.job.job.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.job.api.vo.JobOrderVo;
import org.jeecg.modules.job.job.entity.JobCollect;
import org.jeecg.modules.job.job.entity.JobOrder;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * @Description: 订单信息
 * @Author: qingkonglan
 * @Date:   2024-07-30
 * @Version: V1.0
 */
public interface IJobOrderService extends IService<JobOrder> {

    /**
     * 根据id查询订单详情
     * @param id
     * @return
     */
    JobOrderVo getOrderDetail(String id);

    /**
     * 报名
     * @return
     */
    boolean doApply(String userId,String postId,Integer integral);

    /**
     * 生成订单
     * @return
     */
    boolean createOrder(String userId,String postId);

    /**
     * 更新订单状态
     * @param id
     * @param orderStatus
     * @return
     */
    boolean updateOrderStatus(String id,String orderStatus,String imgs);


    /**
     * 解析结算金额（老板申报，不强制等于单价×工时）
     */
    java.math.BigDecimal resolvePaySalaryAmount(String orderId, java.math.BigDecimal clientAmount);

    /**
     * 更新支付工资
     * @param money
     */
    void updatePayMoney(String orderId,String orderSn,String money,String payType);

    /**
     * 支付工资成功（兼容旧回调，不校验渠道金额）
     */
    void paySalarySuccess(String orderSn);

    /**
     * 支付工资成功（带渠道实付金额校验，幂等）
     * @param orderSn 商户订单号
     * @param paidAmount 渠道实付金额（元），可为 null 表示不校验
     * @return true-已处理或幂等成功；false-订单不存在/状态或金额异常
     */
    boolean paySalarySuccess(String orderSn, java.math.BigDecimal paidAmount);


    /**
     * 分页查询订单列表
     * @param page
     * @param params 参数信息
     * @return
     */
    IPage<Map<String,Object>> getOrderList(Page<JobOrder> page, JobOrder params);


    /**
     * 分页查询订单列表
     * @param page
     * @param params 参数信息
     * @return
     */
    IPage<Map<String,Object>> getOrderListForAdmin(Page<JobOrder> page, JobOrder params);


    /**
     * 分页查询订单列表
     * @param page
     * @param params 参数信息
     * @return
     */
    IPage<Map<String,Object>> getPostUserList(Page<JobOrder> page, JobOrder params);

    /**
     * 查询招工信息下的订单状态数量统计
     * @param postId 参数信息
     * @return
     */
    Map<String, Object> getOrderStatistics(String postId);

    /**
     * 自动结束订单
     */
    void autoFinishOrder();

}
