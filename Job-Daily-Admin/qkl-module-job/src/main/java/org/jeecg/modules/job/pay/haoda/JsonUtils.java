package org.jeecg.modules.job.pay.haoda;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.TextNode;

import java.io.IOException;

/**
 * Json 工具类，用于序列化和反序列化。
 * <p>
 * 此工具主要是{@link ObjectMapper}的包装，但将检查异常转换为运行时异常，
 * 且此工具为通用工具，如果项目中已经使用了其他工具，可以将其替换为其他工具以便统一。
 *
 * @author Laeni
 */
public class JsonUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        // 配置忽略未知属性
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 序列化空对象时不抛出异常
        MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // 指定序列化时的包含规则，NON_NULL表示序列化时忽略值为null的字段
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    /**
     * 序列化为字符串。
     *
     * @param obj 待序列化的对象
     * @return 序列化之后的字符串
     */
    public static String objectToJson(Object obj) {
        if (obj == null) {
            return "";
        }
        try {
            return obj instanceof String ? (String) obj : MAPPER.writeValueAsString(obj);
        } catch (Throwable e) {
            throw new RuntimeException(obj + "序列化失败", e);
        }
    }

    /**
     * json 字符串转成 Java 对象。
     * <p>例:
     * <code>
     *     jsonToObject("[{\"name\": \"张三\"}]" , new TypeReference&lt;List&lt;User&gt;&gt;{})
     * </code>
     *
     * @param jsonStr       待反序列化的字符串
     * @param typeReference 目标对象类型
     * @param <T>           目标对象类型
     * @return 目标对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T jsonToObject(String jsonStr, TypeReference<T> typeReference) {
        if (jsonStr == null) {
            return null;
        }
        if (jsonStr.isEmpty()) {
            if (typeReference.getType().equals(String.class)) {
                return (T) jsonStr;
            } else {
                return null;
            }
        }
        try {
            return (T) (typeReference.getType().equals(String.class) ? jsonStr : MAPPER.readValue(jsonStr, typeReference));
        } catch (IOException e) {
            throw new RuntimeException(jsonStr + " 反序列化失败", e);
        }
    }

    /**
     * json字符串转对象
     *
     * @param jsonStr 待反序列化的字符串
     * @param clazz   目标对象类型
     * @param <T>     目标对象类型
     * @return 目标对象
     */
    public static <T> T jsonToObject(String jsonStr, Class<T> clazz) {
        if (jsonStr == null) {
            return null;
        }
        if (jsonStr.isEmpty()) {
            if (clazz.equals(String.class)) {
                //noinspection unchecked
                return (T) jsonStr;
            } else {
                return null;
            }
        }
        try {
            return MAPPER.readValue(jsonStr, clazz);
        } catch (IOException e) {
            throw new RuntimeException(jsonStr + " 反序列化失败", e);
        }
    }

    /**
     * 树模型.
     * 参考: <a href="https://www.yiibai.com/jackson/jackson_tree_model.html#article-start">示例</a>
     *
     * @param jsonStr Json字符串
     * @return JsonNode树对象.类似Map
     */
    public static JsonNode jsonToObject(String jsonStr) {
        if (jsonStr == null) {
            return NullNode.getInstance();
        }
        try {
            return MAPPER.readTree(jsonStr);
        } catch (IOException e) {
            throw new RuntimeException("JsonUtils 反序列化对象失败,该字符串为:" + jsonStr, e);
        }
    }

    /**
     * 将Object转为class实例.
     * 功能上类似于将Object转为String,然后再将String转为Class实例,但此方法在大多数情况下应该更有效.
     */
    public static <T> T objectToObject(Object obj, Class<T> clazz) {
        return MAPPER.convertValue(obj, clazz);
    }

    public static ObjectMapper getMapper() {
        return MAPPER;
    }

    /**
     * 判断JsonNode节点是否为"空".
     * 空情况: 没有子节点 | null | ""
     *
     * @param node 节点
     */
    public static boolean isEmpty(JsonNode node) {
        if (!node.isEmpty()) {
            return false;
        }

        if (node instanceof NullNode || node instanceof MissingNode) {
            return true;
        }
        if (node instanceof TextNode) {
            return node.asText() == null || node.asText().isEmpty();
        }
        return false;
    }

}