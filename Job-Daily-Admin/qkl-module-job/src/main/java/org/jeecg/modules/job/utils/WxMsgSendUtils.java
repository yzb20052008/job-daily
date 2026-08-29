package org.jeecg.modules.job.utils;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.jeecg.common.util.SpringContextUtils;
import org.jeecg.modules.job.constant.MsgTemplateCodes;
import org.jeecg.modules.job.msg.service.IBizMsgTemplateService;

/**
 * 微信模板消息推送（模板ID优先读 biz_msg_template，失败回退内置默认）
 */
@Slf4j
public class WxMsgSendUtils {

    private static final String FALLBACK_AUTH = "rORH6Ct2fOi5JMGsZABamh5LrB2w0ZmhWIA03T0IS8I";
    private static final String FALLBACK_WITHDRAW = "ij_HvuqyZZ_lZnAlMpWv2zcUCm_Nwd4jTUlGPk1FEaM";
    private static final String FALLBACK_AGREE = "M1CpyBY5jSPs_UvB4eSpnHzgQeaLIR-Im-8gEb1n4v8";
    private static final String FALLBACK_UNAGREE = "85msHoY9z5lBVsRX5hIpIyGBRHfht_ac9GDqCDOGIhM";
    private static final String FALLBACK_APPLY_RESULT = "dKD4VQ-E9kChwTRI5nsteBGRIM9WYOWuT9acAPwGGGc";
    private static final String FALLBACK_NEW_ORDER = "6wvo2jmjl9e5JrkcyTUBnTQZ_YYfVi75WXXr96mpL4E";

    public static boolean sendMsgForAuth(String type, String status, String reason, String touser, String token, String page) {
        JSONObject data = new JSONObject();
        data.put("thing1", getValue(type));
        data.put("phrase2", getValue(status));
        data.put("thing3", getValue(reason));
        return WX_TemplateMsgUtil.sendWxSubscribeMsg(touser,
                resolve(MsgTemplateCodes.WX_AUTH, FALLBACK_AUTH), page, data, token);
    }

    public static boolean sendMsgForWithdraw(String time, String money, String status, String note, String touser, String token, String page) {
        JSONObject data = new JSONObject();
        data.put("time4", getValue(time));
        data.put("amount1", getValue(money));
        data.put("phrase3", getValue(status));
        data.put("thing5", getValue(note));
        return WX_TemplateMsgUtil.sendWxSubscribeMsg(touser,
                resolve(MsgTemplateCodes.WX_WITHDRAW, FALLBACK_WITHDRAW), page, data, token);
    }

    public static boolean sendMsgForOrderAgree(String title, String content, String touser, String token, String page) {
        JSONObject data = new JSONObject();
        data.put("thing2", getValue(title));
        data.put("thing3", getValue(content));
        return WX_TemplateMsgUtil.sendWxSubscribeMsg(touser,
                resolve(MsgTemplateCodes.WX_ORDER_AGREE, FALLBACK_AGREE), page, data, token);
    }

    public static boolean sendMsgForOrderUnAgree(String title, String content, String touser, String token, String page) {
        JSONObject data = new JSONObject();
        data.put("thing2", getValue(title));
        data.put("thing3", getValue(content));
        return WX_TemplateMsgUtil.sendWxSubscribeMsg(touser,
                resolve(MsgTemplateCodes.WX_ORDER_UNAGREE, FALLBACK_UNAGREE), page, data, token);
    }

    public static boolean sendMsgForApplyResult(String title, String result, String workTime, String workAddress,
                                                String content, String touser, String token, String page) {
        JSONObject data = new JSONObject();
        data.put("thing1", getValue(title));
        data.put("phrase3", getValue(result));
        data.put("time10", getValue(workTime));
        data.put("thing6", getValue(workAddress));
        data.put("thing4", getValue(content));
        return WX_TemplateMsgUtil.sendWxSubscribeMsg(touser,
                resolve(MsgTemplateCodes.WX_APPLY_RESULT, FALLBACK_APPLY_RESULT), page, data, token);
    }

    public static boolean sendMsgForNewOrderNotice(String title, String result, String content, String touser, String token, String page) {
        JSONObject data = new JSONObject();
        data.put("thing1", getValue(title));
        data.put("thing4", getValue(result));
        data.put("thing5", getValue(content));
        return WX_TemplateMsgUtil.sendWxSubscribeMsg(touser,
                resolve(MsgTemplateCodes.WX_NEW_ORDER, FALLBACK_NEW_ORDER), page, data, token);
    }

    private static String resolve(String code, String fallback) {
        try {
            IBizMsgTemplateService svc = SpringContextUtils.getBean(IBizMsgTemplateService.class);
            if (svc != null) {
                return svc.resolveWxTemplateId(code, fallback);
            }
        } catch (Exception e) {
            log.debug("消息模板解析回退内置ID code={}", code);
        }
        return fallback;
    }

    private static String getValue(Object param) {
        JSONObject data = new JSONObject();
        data.put("value", param);
        return data.toString();
    }
}
