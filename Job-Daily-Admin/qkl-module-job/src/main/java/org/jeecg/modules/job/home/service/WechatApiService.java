package org.jeecg.modules.job.home.service;


import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jeecg.modules.job.api.util.WxUtil;
import org.jeecg.modules.job.utils.JsonUtils;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 对接微信接口服务
 * Created by fei on 2017/4/24.
 */
@Service
public class WechatApiService {
    private static final String WECHAT_API = "https://api.weixin.qq.com/cgi-bin";
    private static final String WECHAT_API_TOKEN = WECHAT_API + "/token";
    private HttpClient httpclient = null;

    public WechatApiService() {
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(5000)
                .setSocketTimeout(20000)
                .setConnectionRequestTimeout(3000)
                .build();
        httpclient = HttpClients.custom().setDefaultRequestConfig(config).build();
    }

    /**
     * 获取  access_token
     * https://mp.weixin.qq.com/wiki?action=doc&id=mp1421140183
     *
     * @return access_token
     * @throws Exception
     */
    public String getAccessToken() throws Exception {
        HttpGet get = new HttpGet(WECHAT_API_TOKEN + "?grant_type=client_credential&appid=" + WxUtil.getAppId() + "&secret=" + WxUtil.getAppSecret());
        HttpResponse response = httpclient.execute(get);
        String text = EntityUtils.toString(response.getEntity());
        System.out.println(text);
        Map<String, Object> resultMap = JsonUtils.readJsonToMap(text);
        String accessToken = (String) resultMap.get("access_token");
        System.err.println("accessToken====" + accessToken);
        return accessToken;
    }

    /**
     * 纯文本拦截敏感词
     *
     * @param textConetnt
     * @return
     */
    public Boolean checkText(String textConetnt) {
        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            CloseableHttpResponse response = null;
            String accessToken = this.getAccessToken();
            HttpPost request = new HttpPost("https://api.weixin.qq.com/wxa/msg_sec_check?access_token=" + accessToken);
            request.addHeader("Content-Type", "application/json;charset=UTF-8");
            Map<String, String> paramMap = new HashMap<String, String>();
            paramMap.put("content", textConetnt);
            request.setEntity(new StringEntity(JSONObject.toJSONString(paramMap), ContentType.create("application/json", "utf-8")));
            response = httpclient.execute(request);
            HttpEntity httpEntity = response.getEntity();
            String result = EntityUtils.toString(httpEntity, "UTF-8");// 转成string
            System.out.println("result-----------" + result);
            JSONObject jso = JSONObject.parseObject(result);
            Object errcode = jso.get("errcode");
            int errCode = (int) errcode;
            if (errCode == 0) {
                return true;
            } else if (errCode == 87014) {
                System.out.println("内容违规-----------" + textConetnt);
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("----------------调用腾讯内容过滤系统出错------------------");
            return true;
        }
    }

    /**
     * 获取小程序二维码图片
     *
     * @return
     */
    public Map<String, Object> getCodeImg(Long id) {
        Map<String, Object> data = new HashMap<String, Object>();
        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            CloseableHttpResponse response = null;
            String accessToken = this.getAccessToken();
            HttpPost request = new HttpPost("https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=" + accessToken);
            request.addHeader("Content-Type", "application/json;charset=UTF-8");
            Map<String, String> paramMap = new HashMap<String, String>();
            paramMap.put("page", "pages/forum/forum");
            paramMap.put("scene", "postId="+id);
            request.setEntity(new StringEntity(JSONObject.toJSONString(paramMap), ContentType.create("application/json", "utf-8")));
            response = httpclient.execute(request);
            HttpEntity httpEntity = response.getEntity();
            InputStream contentStream = httpEntity.getContent();
            byte[] bytes = toByteArray(contentStream);
            contentStream.read(bytes);
            // 返回内容
            data.put("qrLength", bytes.length);
            data.put("qrBytes", bytes);
            data.put("qrBytesEncoder", Base64.getEncoder().encodeToString(bytes));
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("----------------调用腾讯内容过滤系统出错------------------");
            return data;
        }
    }

    /**
     * @param input
     * @return
     * @throws IOException
     */
    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output);
        return output.toByteArray();
    }

    /**
     * @param input
     * @param output
     * @return
     * @throws IOException
     */
    public static int copy(InputStream input, OutputStream output) throws IOException {
        long count = copyLarge(input, output);
        if (count > 2147483647L) {
            return -1;
        }
        return (int) count;
    }

    /**
     * @param input
     * @param output
     * @return
     * @throws IOException
     */
    public static long copyLarge(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[4096];
        long count = 0L;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    /**
     * @param imgPath
     * @param bytes
     * @return
     */
    public static int saveToImg(String imgPath, byte[] bytes) {
        int stateInt = 1;
        try {
            FileOutputStream fos = new FileOutputStream(imgPath);
            fos.write(bytes);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            stateInt = 0;
            e.printStackTrace();
        } finally {
        }
        return stateInt;
    }

    public static void main(String[] args) throws Exception {
        String appid = "wxb7e1271186379b78";
        String secret = "875a7ab86c5ca65f505501930bf55885";
        WechatApiService apiService = new WechatApiService();
//        String token=apiService.getAccessToken();
//        System.err.println("token===="+token);
//        String text="有违规文字内容测试特3456书yuuo莞6543李zxcz蒜7782法fgnv级";
//        String text="习近平";
        String text="13612365353";
//        String text="法轮功";
//        boolean result = apiService.checkText(text);
//        System.err.println("result====" + result);

        Map<String, Object> data =apiService.getCodeImg(2L);
        String qrBytesEncoder = (String) data.get("qrBytesEncoder");
        Integer qrLength = (Integer) data.get("qrLength");
        System.out.println(qrBytesEncoder);
        System.out.println(qrLength);
        byte[] qrBytes = (byte[]) data.get("qrBytes");
        saveToImg("d://qrcode.png",qrBytes);
    }

}
