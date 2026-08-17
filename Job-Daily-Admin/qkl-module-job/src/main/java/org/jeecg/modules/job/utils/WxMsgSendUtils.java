package org.jeecg.modules.job.utils;

import net.sf.json.JSONObject;

import java.math.BigDecimal;

/**
 * 微信模板消息推送
 */
public class WxMsgSendUtils {

    private static String TemplateId_Auth = "rORH6Ct2fOi5JMGsZABamh5LrB2w0ZmhWIA03T0IS8I";//认证结果通知
    private static String TemplateId_Withdraw = "ij_HvuqyZZ_lZnAlMpWv2zcUCm_Nwd4jTUlGPk1FEaM";//提现进度提醒
    private static String TemplateId_Agree = "M1CpyBY5jSPs_UvB4eSpnHzgQeaLIR-Im-8gEb1n4v8";//同意工作通知
    private static String TemplateId_UnAgree = "85msHoY9z5lBVsRX5hIpIyGBRHfht_ac9GDqCDOGIhM";//拒绝工作通知
    private static String TemplateId_ApplyResult="dKD4VQ-E9kChwTRI5nsteBGRIM9WYOWuT9acAPwGGGc";//录用结果通知
    private static String TemplateId_NewOrder="6wvo2jmjl9e5JrkcyTUBnTQZ_YYfVi75WXXr96mpL4E";//接单任务通知

    /**
     * 认证结果通知
     *
     * @param type 认证类型：如实名认证，企业认证
     * @param status 认证状态：如认证通过，认证未通过
     * @param reason 未通过原因或温馨提示内容
     * @param touser 接收用户openid
     * @param token
     * @return
     */
    public static boolean sendMsgForAuth(String type, String status, String reason, String touser, String token, String page) {
        JSONObject data = new JSONObject();
        data.put("thing1", getValue(type));
        data.put("phrase2", getValue(status));
        data.put("thing3", getValue(reason));
        System.out.println(data.toString());
        return WX_TemplateMsgUtil.sendWxSubscribeMsg(touser, TemplateId_Auth, page, data, token);
    }

    /**
     * 提现结果通知
     *
     * @param time 申请时间
     * @param money 提现金额
     * @param status 提现状态
     * @param note 备注信息
     * @param touser 接收用户openid
     * @param token
     * @return
     */
    public static boolean sendMsgForWithdraw(String time, String money, String status,String note, String touser, String token, String page) {
        JSONObject data = new JSONObject();
        data.put("time4", getValue(time));
        data.put("amount1", getValue(money));
        data.put("phrase3", getValue(status));
        data.put("thing5", getValue(note));
        System.out.println(data.toString());
        return WX_TemplateMsgUtil.sendWxSubscribeMsg(touser, TemplateId_Withdraw, page, data, token);
    }


    /**
     * 同意用工
     *
     * @param title 标题
     * @param content 说明
     * @param touser 接收用户openid
     * @param token
     * @return
     */
    public static boolean sendMsgForOrderAgree(String title, String content, String touser, String token, String page) {
        JSONObject data = new JSONObject();
        data.put("thing2", getValue(title));//工作标题
        data.put("thing3", getValue(content));//温馨提示
        System.out.println(data.toString());
        return WX_TemplateMsgUtil.sendWxSubscribeMsg(touser, TemplateId_Agree, page, data, token);
    }

    /**
     * 拒绝用工
     *
     * @param title  标题
     * @param content 说明
     * @param touser 接收用户openid
     * @param token
     * @return
     */
    public static boolean sendMsgForOrderUnAgree(String title, String content, String touser, String token, String page) {
        JSONObject data = new JSONObject();
        data.put("thing2", getValue(title));//工作标题
        data.put("thing3", getValue(content));//温馨提示
        System.out.println(data.toString());
        return WX_TemplateMsgUtil.sendWxSubscribeMsg(touser, TemplateId_UnAgree, page, data, token);
    }

    /**
     * 录用结果
     *
     * @param title  任务名称
     * @param result 录取结果
     * @param workTime 工作时间
     * @param workAddress 工作地点
     * @param content 说明
     * @param touser 接收用户openid
     * @param token
     * @return
     */
    public static boolean sendMsgForApplyResult(String title,String result,String workTime,String workAddress, String content, String touser, String token, String page) {
        JSONObject data = new JSONObject();
        data.put("thing1", getValue(title));//任务名称
        data.put("phrase3", getValue(result));//录用结果
        data.put("time10", getValue(workTime));//到岗时间
        data.put("thing6", getValue(workAddress));//工作地点
        data.put("thing4", getValue(content));//温馨提示
        System.out.println(data.toString());
        return WX_TemplateMsgUtil.sendWxSubscribeMsg(touser, TemplateId_ApplyResult, page, data, token);
    }

    /**
     * 新订单提醒
     *
     * @param title  任务名称
     * @param result 任务说明
     * @param content 温馨提示
     * @param touser 接收用户openid
     * @param token
     * @return
     */
    public static boolean sendMsgForNewOrderNotice(String title,String result, String content, String touser, String token, String page) {
        JSONObject data = new JSONObject();
        data.put("thing1", getValue(title));//任务名称
        data.put("thing4", getValue(result));//任务说明
        data.put("thing5", getValue(content));//温馨提示
        System.out.println(data.toString());
        return WX_TemplateMsgUtil.sendWxSubscribeMsg(touser, TemplateId_NewOrder, page, data, token);
    }

    private static String getValue(Object param) {
        JSONObject data = new JSONObject();
        data.put("value", param);
        return data.toString();
    }

}
