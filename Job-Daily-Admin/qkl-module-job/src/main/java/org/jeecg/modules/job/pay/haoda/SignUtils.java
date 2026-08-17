package org.jeecg.modules.job.pay.haoda;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class SignUtils {
    /**
     * 签名。
     *
     * @param algorithm 签名算法
     * @param secret    应用秘钥
     * @param timestamp 时间戳
     * @param body      请求体
     * @return 签名字符串
     */
    public static String sign(String algorithm, String secret, String timestamp, byte[] body) {
        final MessageDigest messageDigest = getMessageDigest(algorithm);
        messageDigest.update(timestamp.getBytes());
        messageDigest.update(secret.getBytes());
        messageDigest.update(body);
        return new String(Base64.getEncoder().encode(messageDigest.digest()));
    }

    /**
     * 验签。
     * 如果无法验签通过则会抛出异常。
     *
     * @param algorithm 签名算法
     * @param secret    应用秘钥
     * @param timestamp 时间戳
     * @param body      响应体
     * @param sign      接收到的签名
     */
    public static void verify(String algorithm, String secret, String timestamp, byte[] body, String sign) {
        // 期望签名
        final String expectSign = sign(algorithm, secret, timestamp, body);
        if (!expectSign.equals(sign)) {
            throw new RuntimeException("签名验证未通过。期望签名: " + expectSign + " 实际签名: " + sign);
        }
    }

    private static MessageDigest getMessageDigest(String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("不支持的加密算法", e);
        }
    }

}
