package org.jeecg.modules.job.pay.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 微信支付V3平台证书获取工具类（不依赖IJPay，纯Java实现）
 */
public class WxPayPlatformCertUtil {
    private static final Logger log = LoggerFactory.getLogger(WxPayPlatformCertUtil.class);

    // 微信V3接口相关常量
    private static final String WX_DOMAIN = "https://api.mch.weixin.qq.com";
    private static final String CERTIFICATES_PATH = "/v3/certificates"; // 获取平台证书接口
    private static final String SIGN_ALGORITHM = "SHA256withRSA"; // 签名算法（微信V3要求）
    private static final String AES_GCM_ALGORITHM = "AES/GCM/NoPadding"; // 解密算法
    private static final int GCM_IV_LENGTH = 12; // AES-GCM IV长度（12字节）
    private static final int GCM_TAG_LENGTH = 16; // AES-GCM 标签长度（16字节）
    private static final String AUTH_HEADER_PREFIX = "WECHATPAY2-SHA256-RSA2048 "; // 签名头前缀

    // 微信支付配置参数
    private final String mchId; // 商户号（10位）
    private final String apiV3Key; // APIv3密钥（32位）
    private final String mchPrivateKeyPath; // 商户私钥路径（PKCS#1/PKCS#8）
    private final String mchCertPath; // 商户证书路径（用于获取序列号）
    private final String platformCertSavePath; // 平台证书存储路径

    // 初始化BouncyCastleProvider（解析PKCS#1私钥+AES解密）
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static void main(String[] args) throws Exception {
        // 本地拉取平台证书时请从环境变量或 private 配置读取，禁止提交真实商户号/密钥
        throw new UnsupportedOperationException("请使用本地私有配置运行，勿在源码中硬编码商户凭证");
    }

    /**
     * 构造方法（初始化配置）
     *
     * @param mchId              商户号
     * @param apiV3Key           APIv3密钥（32位）
     * @param mchPrivateKeyPath  商户私钥绝对路径
     * @param mchCertPath        商户证书绝对路径
     * @param platformCertSavePath 平台证书存储绝对路径
     */
    public WxPayPlatformCertUtil(String mchId, String apiV3Key, String mchPrivateKeyPath, String mchCertPath, String platformCertSavePath) {
        this.mchId = Objects.requireNonNull(mchId, "商户号不能为空");
        this.apiV3Key = Objects.requireNonNull(apiV3Key, "APIv3密钥不能为空");
        this.mchPrivateKeyPath = Objects.requireNonNull(mchPrivateKeyPath, "商户私钥路径不能为空");
        this.mchCertPath = Objects.requireNonNull(mchCertPath, "商户证书路径不能为空");
        this.platformCertSavePath = Objects.requireNonNull(platformCertSavePath, "平台证书存储路径不能为空");

        // 校验APIv3密钥长度
        if (apiV3Key.length() != 32) {
            throw new IllegalArgumentException("APIv3密钥必须是32位字符串");
        }

        // 校验文件是否存在
        checkFileExists(mchPrivateKeyPath, "商户私钥文件不存在");
        checkFileExists(mchCertPath, "商户证书文件不存在");
    }

    /**
     * 核心方法：获取并保存平台证书
     *
     * @return 平台证书序列号
     * @throws Exception 异常
     */
    public String fetchAndSavePlatformCert() throws Exception {
        log.info("开始获取微信支付平台证书（不依赖IJPay）...");

        // 1. 生成签名所需参数
        String nonceStr = generateNonceStr(); // 随机串（32位）
        long timestamp = System.currentTimeMillis() / 1000; // 10位秒级时间戳
        String mchCertSerialNo = getCertificateSerialNumber(mchCertPath); // 商户证书序列号
        PrivateKey privateKey = loadPrivateKey(mchPrivateKeyPath); // 加载商户私钥

        // 2. 构造待签名字符串（微信V3签名规范）
        String signStr = buildSignString("GET", CERTIFICATES_PATH, timestamp, nonceStr, "");
        log.info("待签名字符串：\n{}", signStr);

        // 3. 生成RSA签名
        String signature = sign(signStr, privateKey);
        log.info("生成签名：{}", signature);

        // 4. 构造Authorization请求头
        String authHeader = buildAuthHeader(mchId, nonceStr, timestamp, mchCertSerialNo, signature);
        log.info("Authorization头：{}", authHeader);

        // 5. 发送HTTPS请求到微信接口
        String responseBody = sendHttpsRequest(WX_DOMAIN + CERTIFICATES_PATH, authHeader);
        log.info("接口响应体：{}", responseBody);

        // 6. 解析响应并解密证书
        String certSerialNo = parseAndDecryptCert(responseBody);

        // 7. 验签响应（可选，验证数据完整性）
        // 注意：首次获取证书时，需用微信根证书验签；后续可用已保存的平台证书验签
        // verifyResponseSignature(responseHeaders, responseBody);

        return certSerialNo;
    }

    /**
     * 构造微信V3待签名字符串
     * 格式：HTTP方法\n接口路径\n时间戳\n随机串\n请求体\n
     */
    private String buildSignString(String method, String path, long timestamp, String nonceStr, String body) {
        return String.format("%s\n%s\n%d\n%s\n%s\n",
                method.toUpperCase(), path, timestamp, nonceStr, body);
    }

    /**
     * 生成RSA2048签名（SHA256withRSA）
     */
    private String sign(String signStr, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance(SIGN_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(signStr.getBytes(StandardCharsets.UTF_8));
        byte[] signBytes = signature.sign();
        return Base64.getEncoder().encodeToString(signBytes);
    }

    /**
     * 构造Authorization请求头
     */
    private String buildAuthHeader(String mchId, String nonceStr, long timestamp, String serialNo, String signature) {
        return AUTH_HEADER_PREFIX + String.format(
                "mchid=\"%s\",nonce_str=\"%s\",timestamp=\"%d\",serial_no=\"%s\",signature=\"%s\"",
                mchId, nonceStr, timestamp, serialNo, signature
        );
    }

    /**
     * 发送HTTPS请求（GET）
     */
    private String sendHttpsRequest(String urlStr, String authHeader) throws Exception {
        URL url = new URL(urlStr);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

        // 配置请求头
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", authHeader);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(10000);

        // 发送请求并获取响应
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            String errorMsg = readInputStream(conn.getErrorStream());
            throw new RuntimeException("请求失败，状态码：" + responseCode + "，错误信息：" + errorMsg);
        }

        // 读取响应体
        return readInputStream(conn.getInputStream());
    }

    /**
     * 解析响应并解密平台证书
     */
    private String parseAndDecryptCert(String responseBody) throws Exception {
        JSONObject jsonObject = JSON.parseObject(responseBody);
        JSONArray dataArray = jsonObject.getJSONArray("data");
        if (dataArray == null || dataArray.isEmpty()) {
            throw new RuntimeException("未获取到平台证书数据");
        }

        // 取第一个证书（可扩展：按expire_time筛选未过期证书）
        JSONObject certObj = dataArray.getJSONObject(0);
        String certSerialNo = certObj.getString("serial_no");
        JSONObject encryptCertObj = certObj.getJSONObject("encrypt_certificate");

        String associatedData = encryptCertObj.getString("associated_data");
        String nonce = encryptCertObj.getString("nonce");
        String cipherText = encryptCertObj.getString("ciphertext");

        // 解密证书
        String platformCertPem = decryptAesGcm(associatedData, nonce, cipherText, apiV3Key);
        log.info("证书解密成功，PEM长度：{}", platformCertPem.length());

        // 保存到本地
        saveCertToLocal(platformCertPem);
        log.info("平台证书已保存到：{}", platformCertSavePath);

        return certSerialNo;
    }

    /**
     * AES-256-GCM解密（微信V3标准解密方式）
     */
    private String decryptAesGcm(String associatedData, String nonce, String cipherText, String apiV3Key) throws Exception {
        // 解码参数
        byte[] keyBytes = apiV3Key.getBytes(StandardCharsets.UTF_8);
        byte[] nonceBytes = nonce.getBytes(StandardCharsets.UTF_8);
        byte[] cipherTextBytes = Base64.getDecoder().decode(cipherText);

        // 初始化加密器
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
        GCMParameterSpec gcmParamSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, nonceBytes);

        Cipher cipher = Cipher.getInstance(AES_GCM_ALGORITHM, "BC");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, gcmParamSpec);

        // 设置关联数据（必须）
        cipher.updateAAD(associatedData.getBytes(StandardCharsets.UTF_8));

        // 解密
        byte[] plainBytes = cipher.doFinal(cipherTextBytes);
        return new String(plainBytes, StandardCharsets.UTF_8);
    }

    /**
     * 保存证书到本地文件
     */
    private void saveCertToLocal(String certPem) {
        // 保存证书
        cn.hutool.core.io.file.FileWriter writer = new cn.hutool.core.io.file.FileWriter(platformCertSavePath);
        writer.write(certPem);

    }

    /**
     * 加载商户私钥（兼容PKCS#1和PKCS#8格式）
     */
    private PrivateKey loadPrivateKey(String privateKeyPath) throws Exception {
        try (PEMParser pemParser = new PEMParser(new FileReader(privateKeyPath))) {
            Object obj = pemParser.readObject();
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");

            if (obj instanceof PrivateKeyInfo) {
                // PKCS#8格式（BEGIN PRIVATE KEY）
                return converter.getPrivateKey((PrivateKeyInfo) obj);
            } else {
                // PKCS#1格式（BEGIN RSA PRIVATE KEY）
                return converter.getPrivateKey(PrivateKeyInfo.getInstance(obj));
            }
        }
    }

    /**
     * 获取证书序列号（从PEM证书文件中提取）
     */
    private String getCertificateSerialNumber(String certPath) throws Exception {
        X509Certificate cert = loadX509Certificate(certPath);
        // 序列号转16进制字符串（微信要求的格式）
        return cert.getSerialNumber().toString(16).toUpperCase();
    }

    /**
     * 加载X509证书（商户证书/平台证书）
     */
    public X509Certificate loadX509Certificate(String certPath) throws Exception {
        try (InputStream in = new FileInputStream(certPath)) {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            return (X509Certificate) cf.generateCertificate(in);
        }
    }

    /**
     * 验签微信响应（需微信根证书或已保存的平台证书）
     *
     * @param responseHeaders 响应头（包含Wechatpay-Timestamp、Nonce、Signature等）
     * @param responseBody    响应体
     * @param cert            验签用证书（微信根证书或平台证书）
     * @return 验签结果
     */
    public boolean verifyResponseSignature(Map<String, String> responseHeaders, String responseBody, X509Certificate cert) throws Exception {
        String timestamp = responseHeaders.get("Wechatpay-Timestamp");
        String nonce = responseHeaders.get("Wechatpay-Nonce");
        String signature = responseHeaders.get("Wechatpay-Signature");
        String serialNo = responseHeaders.get("Wechatpay-Serial");

        if (timestamp == null || nonce == null || signature == null || serialNo == null) {
            log.error("响应头缺少验签参数");
            return false;
        }

        // 构造待验名字符串（与签名格式一致）
        String verifyStr = buildSignString("GET", CERTIFICATES_PATH, Long.parseLong(timestamp), nonce, responseBody);
        log.info("待验名字符串：\n{}", verifyStr);

        // 验签
        PublicKey publicKey = cert.getPublicKey();
        Signature sig = Signature.getInstance(SIGN_ALGORITHM);
        sig.initVerify(publicKey);
        sig.update(verifyStr.getBytes(StandardCharsets.UTF_8));
        boolean result = sig.verify(Base64.getDecoder().decode(signature));

        log.info("响应验签结果：{}，证书序列号：{}", result, serialNo);
        return result;
    }

    /**
     * 生成32位随机串
     */
    private String generateNonceStr() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(32);
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < 32; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /**
     * 读取输入流为字符串
     */
    private String readInputStream(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        in.close();
        return sb.toString();
    }

    /**
     * 校验文件是否存在
     */
    private void checkFileExists(String filePath, String errorMsg) {
        if (!new File(filePath).exists()) {
            throw new IllegalArgumentException(errorMsg + "，路径：" + filePath);
        }
    }

    /**
     * 读取本地平台证书
     */
    public X509Certificate getLocalPlatformCert() throws Exception {
        if (!new File(platformCertSavePath).exists()) {
            throw new RuntimeException("本地平台证书不存在，请先调用fetchAndSavePlatformCert()");
        }
        return loadX509Certificate(platformCertSavePath);
    }

    /**
     * 定时更新证书（配合定时任务使用）
     */
    public void scheduledUpdateCert() {
        try {
            log.info("开始定时更新微信平台证书...");
            String serialNo = fetchAndSavePlatformCert();
            log.info("定时更新成功，新序列号：{}", serialNo);
        } catch (Exception e) {
            log.error("定时更新平台证书失败", e);
        }
    }

    // ------------------------------ Getter方法 ------------------------------
    public String getMchId() {
        return mchId;
    }

    public String getPlatformCertSavePath() {
        return platformCertSavePath;
    }
}
