package org.jeecg.modules.job.utils;


import net.sf.json.JSONObject;
import org.jeecg.modules.job.api.util.WxUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WX_TemplateMsgUtil {

    private static Logger log = LoggerFactory.getLogger(WX_TemplateMsgUtil.class);

    /**
     * 发送小程序订阅消息
     *
     * @param touser    用户 OpenID
     * @param templatId 模板消息ID
     * @param page      URL置空，则在发送后，点击模板消息会进入一个空白页面（ios），或无法点击（android）。
     * @param data      详细内容
     * @return
     */
    public static boolean sendWxSubscribeMsg(String touser, String templatId, String page, JSONObject data, String token) {
        String tmpurl = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token=" + token;
        JSONObject json = new JSONObject();
        json.put("touser", touser);
        json.put("page", page);
        json.put("template_id", templatId);
        json.put("data", data);
        log.info("发送小程序订阅消息：" + json.toString());
        try {
            JSONObject resultJson = WX_HttpsUtil.httpsRequest(tmpurl, "POST", json.toString());
            log.info("发送小程序订阅消息返回信息：" + resultJson);
            String errmsg = (String) resultJson.get("errmsg");
            if (!"ok".equals(errmsg)) {  //如果为errmsg为ok，则代表发送成功，公众号推送信息给用户了。
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    /**
     * 发送公众号模板消息
     *
     * @param touser    用户 OpenID
     * @param templatId 模板消息ID
     * @param url      公众号模板消息所要跳转的url
     * @param miniprogram  公众号模板消息所要跳转的小程序，小程序的必须与公众号具有绑定关系
     * @param data  详细内容
     * @return
     */
    public static boolean sendWechatMsgToUser(String touser, String templatId, String url, String miniprogram, JSONObject data, String token) {
        String tmpurl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + token;
        JSONObject json = new JSONObject();
        json.put("touser", touser);
        json.put("template_id", templatId);
        json.put("url",url);
//        json.put("miniprogram",miniprogram);
        json.put("appid", WxUtil.getAppId());//跳转小程序appId
        json.put("pagepath","/pages/index/index");
        json.put("data",data);
        System.out.println("json==="+json.toString());
        try {
            JSONObject resultJson = WX_HttpsUtil.httpsRequest(tmpurl, "POST", json.toString());
            log.info("发送微信消息返回信息：" + resultJson);
            String errmsg = (String) resultJson.get("errmsg");
            if (!"ok".equals(errmsg)) {  //如果为errmsg为ok，则代表发送成功，公众号推送信息给用户了。
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 获取  access_token  临时测试用
     * @return access_token
     * @throws Exception
     */
    public static String getAccessToken(String appid, String appSecret) throws Exception {
        String key = "access_token:" + appid;
        try {
            JSONObject resultJson = WX_HttpsUtil.httpsRequest( "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appid + "&secret=" + appSecret, "POST", "");
            log.info("发送返回信息resultJson：" + resultJson.toString());
            String accessToken = (String) resultJson.get("access_token");
            log.info("发送返回信息accessToken：" + accessToken);
            return accessToken;
        } finally {
        }
    }

    public static void main(String[] args) {
        try {
            JSONObject data=new JSONObject();
            data.put("keyword1","2");
            data.put("keyword2","3");
            data.put("keyword3","4");
            JSONObject miniprogram=new JSONObject();
            miniprogram.put("appid","wx7d42768a670978dd");
            miniprogram.put("pagepath","pages/index/index");
            sendWechatMsgToUser("oDFis6UgkSZX7-ACW9x3ZYIaXzv0", "pecXcfsUSn5F3bmXryOxd4jJdwCs1KtrdKfHFJoqJ64","pages/index/index",miniprogram.toString(), data, getAccessToken("wx2e44a0988f1e973a","b7bd643e537c9ef4784dd6b36e9bacae"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
