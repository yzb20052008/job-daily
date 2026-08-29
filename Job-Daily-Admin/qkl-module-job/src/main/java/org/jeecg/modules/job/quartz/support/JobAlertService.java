package org.jeecg.modules.job.quartz.support;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.job.base.entity.BaseConfig;
import org.jeecg.modules.job.base.service.IBaseConfigService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * 定时任务失败告警（日志 + 可选 Webhook）
 * <p>base_config.job_alert_webhook 配置企微/钉钉机器人地址；为空则仅打日志</p>
 */
@Slf4j
@Component
public class JobAlertService {

    public static final String CONFIG_WEBHOOK = "job_alert_webhook";

    @Resource
    private IBaseConfigService configService;

    public void alert(String jobName, Throwable error) {
        String msg = String.format("[定时任务失败] %s: %s", jobName,
                error == null ? "unknown" : error.getMessage());
        log.error(msg, error);
        String webhook = readWebhook();
        if (oConvertUtils.isEmpty(webhook)) {
            return;
        }
        try {
            postJson(webhook, "{\"msgtype\":\"text\",\"text\":{\"content\":\"" + escape(msg) + "\"}}");
        } catch (Exception e) {
            log.warn("任务告警 Webhook 发送失败 job={}", jobName, e);
        }
    }

    private String readWebhook() {
        try {
            BaseConfig cfg = configService.getConfigByCode(CONFIG_WEBHOOK);
            return cfg == null ? null : cfg.getConfigValue();
        } catch (Exception e) {
            return null;
        }
    }

    private void postJson(String url, String body) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setConnectTimeout(3000);
        conn.setReadTimeout(5000);
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.getBytes(StandardCharsets.UTF_8));
        }
        int code = conn.getResponseCode();
        if (code < 200 || code >= 300) {
            log.warn("Webhook 响应异常 code={} url={}", code, url);
        }
        conn.disconnect();
    }

    private String escape(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ");
    }
}
