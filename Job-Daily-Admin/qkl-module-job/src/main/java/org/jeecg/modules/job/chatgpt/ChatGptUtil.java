package org.jeecg.modules.job.chatgpt;


import com.google.gson.Gson;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jeecg.modules.job.chatgpt.bean.ChatResponse;

import java.util.ArrayList;
import java.util.List;

public class ChatGptUtil {
    //    openai key
    private static final String TOKEN="sb-01d67c5df7576a7ceb175a21c50361789d34b18536d0c9cb";
    //URL地址
    private static final String BASE_URL="https://api.openai-sb.com/v1/completions";

    public ChatResponse getChatResponse(String ask, List<String> messages) throws Exception{
        RequestConfig build = RequestConfig.custom().build();
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(BASE_URL);
        post.addHeader("Content-Type", "application/json");
        post.addHeader("Authorization", "Bearer "+TOKEN);
        //将 build 配置设置到 post 请求中包括先前指定的代理设置。
        post.setConfig(build);
        String paramJson = "{\"model\": \"gpt-3.5-turbo-instruct\", \"prompt\": \""+ask+"\", \"temperature\": 0, \"max_tokens\": 1024}";
        StringEntity stringEntity = new StringEntity(paramJson, ContentType.create("text/json", "UTF-8"));
        post.setEntity(stringEntity);
        CloseableHttpResponse response = httpClient.execute(post);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String res = EntityUtils.toString(response.getEntity());
            //System.out.println(res);
            Gson gson = new Gson();
            ChatResponse result = gson.fromJson(res, ChatResponse.class);
            return result;
        } else {
            System.out.println(response.getStatusLine().getStatusCode());
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        ChatResponse response=new ChatGptUtil().getChatResponse("你是谁",new ArrayList<>());
        System.err.println(response.toString());
    }
}
