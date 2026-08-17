package org.jeecg.modules.job.pay.haoda;


import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * SDK Client，已经实现了签名和验签。
 * <p>
 * 本质上是对{@link HttpClient}的包装，所以该对象应该被复用，而不是每次请求都创建新实例。
 * 另外需要注意，此类被设计为“实例一旦创建就无法修改”，所以如果需要实现动态修改配置，可以使用新配置创建新的实例，然后关闭旧实例。
 *
 * @author qingkonglan
 */
public class Client implements AutoCloseable {
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    /**
     * 请求头中的应用 Id.
     */
    public static final String APPID_HEADER = "X-Up-AppId";
    /**
     * 请求头中的时间戳.
     */
    private static final String TIMESTAMP_HEADER = "X-Timestamp";
    /**
     * 请求头中的签名算法.
     */
    public static final String SIGN_ALGORITHM_HEADER = "X-Sign-Type";
    /**
     * 请求头中的签名.
     */
    private static final String SIGN_HEADER = "X-Sign";

    /**
     * @see Config#getAppId()
     */
    private final String appId;
    /**
     * @see Config#getAppSecret()
     */
    private final String appSecret;
    /**
     * @see Config#getGatewayUrl()
     */
    private final String gatewayUrl;
    /**
     * @see Config#getSignAlgorithm()
     */
    private final String signAlgorithm;
    private final CloseableHttpClient httpClient;
    /**
     * 用于记录当前实例正在处理任务的个数.
     */
    private final AtomicInteger atomicInteger;

    public Client(Config config) {
        this.appId = config.getAppId();
        this.appSecret = config.getAppSecret();
        this.gatewayUrl = config.getGatewayUrl();
        this.signAlgorithm = config.getSignAlgorithm();
        this.httpClient = HttpClients.createDefault();
        this.atomicInteger = new AtomicInteger(0);
    }

    /**
     * 发起请求，并返回响应结果.
     *
     * @param path    接口请求路径.
     * @param body    发送给服务端的数据（HTTP Body）.
     * @param resType 调用者希望得到的类型，可以是常见的类型，如{@link byte[]}、{@link String}
     *                以及简单的 DTO（可以由 {@link JsonUtils}工具类反序列化得到的类型）.
     * @param <T>     调用者希望得到的类型.
     * @return 服务端响应的结果，如果服务端响应为空，则该值也可能为空.
     * @throws RequestException 当 HTTP 状态码不在 200 ~ 299 范围内时将抛出该异常.
     * @throws IOException      一般发生网络相关的问题时会抛出该异常.
     */
    public <T> @Nullable T execute(String path, Object body, Class<T> resType) throws IOException, RequestException {
        try {
            this.atomicInteger.incrementAndGet();

            final byte[] bodyBytes = toBytes(body);
            final String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
            // 计算请求签名
            final String sign = SignUtils.sign(this.signAlgorithm, this.appSecret, timestamp, bodyBytes);
            // 组装请求
            final HttpPost httpPost = buildPostJsonHttpPost(path, timestamp, sign, bodyBytes);

            // 使用http客户端组件执行请求得到响应
            try (CloseableHttpResponse response = this.httpClient.execute(httpPost)) {
                final int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode < 200 || statusCode > 299) {
                    throw new RequestException(statusCode, EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8));
                }

                final byte[] resBytes = EntityUtils.toByteArray(response.getEntity());
                // 验签（验签使用的算法需要使用响应头指定的算法）
                final String resAlgorithm = response.getFirstHeader(SIGN_ALGORITHM_HEADER).getValue();
                final String resTimestamp = response.getFirstHeader(TIMESTAMP_HEADER).getValue();
                final String resSign = response.getFirstHeader(SIGN_HEADER).getValue();
                SignUtils.verify(resAlgorithm, appSecret, resTimestamp, resBytes, resSign);
                if (resType.equals(byte[].class)) {
                    return (T) resBytes;
                }
                final String resStr = new String(resBytes, StandardCharsets.UTF_8);
                if (resType.equals(String.class)) {
                    //noinspection unchecked
                    return (T) resStr;
                }
                return JsonUtils.jsonToObject(resStr, new TypeReference<T>() {
                });
            } catch (IOException e) {
                throw new RuntimeException("请求 " + httpPost.getURI() + " 失败。", e);
            }
        } finally {
            this.atomicInteger.decrementAndGet();
        }
    }

    /**
     * 返回当前客户端是否空闲.
     * 一般用于动态修改配置后，在关闭客户端前检查该客户端是否正在处理任务，
     * 确保其处于空闲状态时才将其关闭，以避免关闭后正在使用到该客户端的任务出错.
     */
    public boolean isIdle() {
        return this.atomicInteger.get() == 0;
    }

    @Override
    public void close() throws Exception {
        this.httpClient.close();
    }

    private byte[] toBytes(Object obj) {
        if (obj == null) {
            return new byte[0];
        }
        if (obj instanceof byte[]) {
            return (byte[]) obj;
        }
        return JsonUtils.objectToJson(obj).getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 构建 HttpClient 请求对象.
     */
    private HttpPost buildPostJsonHttpPost(String path, String timestamp, String sign, byte[] bodyBytes) {
        final HttpPost httpPost = new HttpPost();
        httpPost.setURI(URI.create(this.gatewayUrl + path));
        httpPost.addHeader("Content-Type", "application/json");
        httpPost.addHeader(APPID_HEADER, appId);
        httpPost.addHeader(TIMESTAMP_HEADER, timestamp);
        httpPost.addHeader(SIGN_ALGORITHM_HEADER, this.signAlgorithm);
        httpPost.addHeader(SIGN_HEADER, sign);
        httpPost.setEntity(new ByteArrayEntity(bodyBytes));
        return httpPost;
    }
}
