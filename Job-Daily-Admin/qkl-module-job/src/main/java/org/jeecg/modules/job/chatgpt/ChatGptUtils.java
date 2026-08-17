package org.jeecg.modules.job.chatgpt;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import okhttp3.Credentials;

import java.net.Proxy;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.OpenAiApi;
import com.theokanning.openai.completion.chat.*;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import java.net.InetSocketAddress;
import java.time.temporal.ChronoUnit;

import static com.theokanning.openai.service.OpenAiService.*;

public class ChatGptUtils {
//    openai key
    private static final String TOKEN="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";//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
    //代理ip地址
    private static final String PROXY_HOST="45.77.197.39";//127.0.0.1
    //代理端口
    private static final int PROXY_PORT=12306;
    //代理用户名
    private static final String PROXY_USERNAME="gpt";
    //代理密码
    private static final String PROXY_PASSWORD="zhaoshixinet";

    public ChatCompletionResult getChatResponse(String ask,List<ChatMessage> messages){
        try{
//            System.setProperty("https.protocols", "TLSv1.2");
            ObjectMapper mapper = defaultObjectMapper();
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(PROXY_HOST, PROXY_PORT));
            OkHttpClient client = defaultClient(TOKEN, Duration.of(60, ChronoUnit.SECONDS))
                    .newBuilder()
                    .proxy(proxy)
                    .proxyAuthenticator((route, response) -> {
                        String credential = Credentials.basic(PROXY_USERNAME, PROXY_PASSWORD);
                        return response.request().newBuilder().header("Proxy-Authorization", credential).build();
                    })
//                    .sslSocketFactory(OkHttpUtil.getIgnoreInitedSslContext().getSocketFactory(), OkHttpUtil.IGNORE_SSL_TRUST_MANAGER_X509)
//                    .hostnameVerifier(OkHttpUtil.getIgnoreSslHostnameVerifier())
                    .build();
            Retrofit retrofit = defaultRetrofit(client, mapper);
            OpenAiApi api = retrofit.create(OpenAiApi.class);
            OpenAiService service = new OpenAiService(api);
//        OpenAiService service = new OpenAiService(TOKEN);
            final ChatMessage systemMessage = new ChatMessage(ChatMessageRole.USER.value(), ask);
            messages.add(systemMessage);
            System.out.println(messages.toString());
            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                    .builder()
                    .model("gpt-3.5-turbo")
                    .messages(messages)
                    .n(1)
                    .maxTokens(1000)
                    .logitBias(new HashMap<>())
                    .build();
            ChatCompletionResult result=service.createChatCompletion(chatCompletionRequest);
            System.out.println(result.toString());
            return result;
        }catch (Exception e){
            e.printStackTrace();
        }
       return null;
    }


    public static void main(String[] args) {
        List<ChatMessage> messages=new ArrayList<>();
//        ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(),"求职招聘");
//        messages.add(systemMessage);
//        systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(),"软件开发");
//        messages.add(systemMessage);
        ChatCompletionResult result=new ChatGptUtils().getChatResponse("您好",messages);
        if (result==null){
            try {
                Thread.sleep(1000);
                result=new ChatGptUtils().getChatResponse("您好",messages);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.err.println("res==="+result.getChoices().get(0).getMessage().getContent());
        System.err.println("ask-token-"+result.getUsage().getPromptTokens());
        System.err.println("res-token-"+result.getUsage().getCompletionTokens());
        System.err.println("total-token-"+result.getUsage().getTotalTokens());

//        ObjectMapper mapper = defaultObjectMapper();
//        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(PROXY_HOST, PROXY_PORT));
//        OkHttpClient client = defaultClient(TOKEN, Duration.of(60, ChronoUnit.SECONDS))
//                .newBuilder()
//                .proxy(proxy)
//                .build();
//        Retrofit retrofit = defaultRetrofit(client, mapper);
//        OpenAiApi api = retrofit.create(OpenAiApi.class);
//        OpenAiService service = new OpenAiService(api);
//        OpenAiService service = new OpenAiService(token);
//        CompletionRequest completionRequest = CompletionRequest.builder()
//                .prompt("Somebody once told me the world is gonna roll me")
//                .model("ada")
//                .echo(true)
//                .build();
//        service.createCompletion(completionRequest).getChoices().forEach(System.out::println);
        //文本补全
//        System.out.println("\nCreating completion...");
//        CompletionRequest completionRequest = CompletionRequest.builder()
//                .model("ada")
//                .prompt("你叫什么？")
//                .echo(true)
//                .user("testing")
//                .n(3)
//                .build();
//        service.createCompletion(completionRequest).getChoices().forEach(new Consumer<CompletionChoice>() {
//            @Override
//            public void accept(CompletionChoice completionChoice) {
//                System.out.println(completionChoice.getText());
//            }
//        });
        //图片生成
//        System.out.println("\nCreating Image...");
//        CreateImageRequest request = CreateImageRequest.builder()
//                .prompt("美女")
//                .build();
//        System.out.println("\nImage is located at:");
//        System.out.println(service.createImage(request).getData().get(0).getUrl());
        //对话
//        System.out.println("Creating chat completion...");
//        final List<ChatMessage> messages = new ArrayList<>();
//        final ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), "你叫什么？");
//        messages.add(systemMessage);
//        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
//                .builder()
//                .model("gpt-3.5-turbo")
//                .messages(messages)
//                .n(1)
//                .maxTokens(500)
//                .logitBias(new HashMap<>())
//                .build();
        //完整对话
//        service.createChatCompletion(chatCompletionRequest)
//                .getChoices().forEach(new Consumer<ChatCompletionChoice>() {
//            @Override
//            public void accept(ChatCompletionChoice chatCompletionChoice) {
//                System.out.println(chatCompletionChoice.getMessage());
//            }
//        });
//        ChatCompletionResult result=service.createChatCompletion(chatCompletionRequest);
//        System.out.println(result.toString());

        //流式对话（逐Token返回）
//        service.streamChatCompletion(chatCompletionRequest)
//                .doOnError(Throwable::printStackTrace)
//                .blockingForEach(System.out::println);
        //立即释放连接
//        service.shutdownExecutor();
    }


//    public static void main(String... args) {
//        String token = "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";
//        OpenAiService service = new OpenAiService(token, Duration.ofSeconds(30));
//
//        //列出所有模型实例
//        System.out.println(service.listModels());
//        //检索模型,得到模型实例，提供有关模型的基本信息，例如所有者和权限，应用场景等。
//        System.out.println(service.getModel("text-davinci-003"));


//        System.out.println("\nCreating completion...");
//        CompletionRequest completionRequest = CompletionRequest.builder()
//                .model("ada")
//                .prompt("Somebody once told me the world is gonna roll me")
//                .echo(true)
//                .user("testing")
//                .n(3)
//                .build();
//        service.createCompletion(completionRequest).getChoices().forEach(System.out::println);
//
//        System.out.println("\nCreating Image...");
//        CreateImageRequest request = CreateImageRequest.builder()
//                .prompt("A cow breakdancing with a turtle")
//                .build();
//
//        System.out.println("\nImage is located at:");
//        System.out.println(service.createImage(request).getData().get(0).getUrl());
//
//        System.out.println("Streaming chat completion...");
//        final List<ChatMessage> messages = new ArrayList<>();
//        final ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), "You are a dog and will speak as such.");
//        messages.add(systemMessage);
//        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
//                .builder()
//                .model("gpt-3.5-turbo")
//                .messages(messages)
//                .n(1)
//                .maxTokens(50)
//                .logitBias(new HashMap<>())
//                .build();
//
//        service.streamChatCompletion(chatCompletionRequest)
//                .doOnError(Throwable::printStackTrace)
//                .blockingForEach(System.out::println);
//
//        service.shutdownExecutor();
//    }




//    public static void main(String[] args) throws Exception {
//        HttpClient httpClient = HttpClientBuilder.create().build();
//        HttpPost request = new HttpPost("https://api.openai.com/v1/engines/davinci/jobs");
//        request.addHeader("Content-Type", "application/json");
//        request.addHeader("Authorization", "Bearer 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");
//
//        JSONObject requestBody = new JSONObject();
//        requestBody.put("prompt", "What is the capital of France?");
//        requestBody.put("max_tokens", 100);
//        requestBody.put("temperature", 0.5);
//
//        StringEntity requestEntity = new StringEntity(requestBody.toString());
//        request.setEntity(requestEntity);
//
//        HttpResponse response = httpClient.execute(request);
//        String responseString = EntityUtils.toString(response.getEntity());
//        JSONObject responseJson = new JSONObject(responseString);
//
//        System.out.println("Response: " + responseJson.getString("choices").split("\n")[0]);
//    }
}
