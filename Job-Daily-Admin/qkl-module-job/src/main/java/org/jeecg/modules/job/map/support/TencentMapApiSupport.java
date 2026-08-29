package org.jeecg.modules.job.map.support;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.job.base.entity.BaseConfig;
import org.jeecg.modules.job.base.service.IBaseConfigService;
import org.jeecg.modules.job.constant.BizConstants;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 腾讯地图 WebService 封装（App/后台共用）
 * key/sk 从 base_config：map_key / map_sk
 */
@Slf4j
@Component
public class TencentMapApiSupport {

    private static final String QQ_MAP_BASE = "https://apis.map.qq.com";

    @Resource
    private IBaseConfigService configService;

    public String getMapKey() {
        BaseConfig keyConfig = configService.getConfigByCode(BizConstants.MAP_KEY);
        if (keyConfig == null || oConvertUtils.isEmpty(keyConfig.getConfigValue())) {
            throw new IllegalStateException("地图Key未配置，请在基础配置中设置 map_key");
        }
        return keyConfig.getConfigValue().trim();
    }

    public Result<Object> reverseGeocoder(String latitude, String longitude) {
        if (oConvertUtils.isEmpty(latitude) || oConvertUtils.isEmpty(longitude)) {
            return Result.error("经纬度参数不能为空");
        }
        try {
            Map<String, String> params = new LinkedHashMap<>();
            params.put("key", getMapKey());
            params.put("location", latitude + "," + longitude);
            return requestGeocoder(params);
        } catch (IllegalStateException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("逆地理编码异常", e);
            return Result.error("逆地理编码服务异常");
        }
    }

    public Result<Object> geocoder(String address) {
        if (oConvertUtils.isEmpty(address)) {
            return Result.error("地址参数不能为空");
        }
        try {
            Map<String, String> params = new LinkedHashMap<>();
            params.put("address", address.trim());
            params.put("key", getMapKey());
            return requestGeocoder(params);
        } catch (IllegalStateException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("地理编码异常", e);
            return Result.error("地理编码服务异常");
        }
    }

    public Result<Object> suggestion(String keyword, String latitude, String longitude, Integer pageSize) {
        if (oConvertUtils.isEmpty(keyword)) {
            return Result.error("搜索关键词不能为空");
        }
        try {
            Map<String, String> params = new LinkedHashMap<>();
            params.put("keyword", keyword.trim());
            params.put("key", getMapKey());
            params.put("page_index", "1");
            params.put("page_size", String.valueOf(pageSize != null && pageSize > 0 ? pageSize : 20));
            if (oConvertUtils.isNotEmpty(latitude) && oConvertUtils.isNotEmpty(longitude)) {
                params.put("location", latitude + "," + longitude);
            }
            return requestPlaceList("/ws/place/v1/suggestion", params, "地点搜索");
        } catch (IllegalStateException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("地点搜索异常", e);
            return Result.error("地点搜索异常");
        }
    }

    public Result<Object> nearby(String latitude, String longitude, Integer radius, Integer pageSize) {
        if (oConvertUtils.isEmpty(latitude) || oConvertUtils.isEmpty(longitude)) {
            return Result.error("经纬度参数不能为空");
        }
        try {
            int r = radius != null && radius > 0 ? radius : 1000;
            int size = pageSize != null && pageSize > 0 ? pageSize : 20;
            Map<String, String> params = new LinkedHashMap<>();
            params.put("boundary", "nearby(" + latitude + "," + longitude + "," + r + ")");
            params.put("key", getMapKey());
            params.put("orderby", "_distance");
            params.put("page_index", "1");
            params.put("page_size", String.valueOf(size));
            return requestPlaceList("/ws/place/v1/explore", params, "附近地点");
        } catch (IllegalStateException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("附近地点查询异常", e);
            return Result.error("附近地点查询异常");
        }
    }

    public Result<Object> ipLocation(HttpServletRequest request, String ip) {
        try {
            String clientIp = oConvertUtils.isNotEmpty(ip) ? ip.trim() : resolveClientIp(request);
            Map<String, String> params = new LinkedHashMap<>();
            params.put("key", getMapKey());
            if (oConvertUtils.isNotEmpty(clientIp) && !isPrivateOrLocalIp(clientIp)) {
                params.put("ip", clientIp);
            }
            String mapSk = getMapSk();
            String paramStr = buildParamString(params);
            String path = "/ws/location/v1/ip";
            String url = QQ_MAP_BASE + path + "?" + paramStr;
            if (oConvertUtils.isNotEmpty(mapSk)) {
                url += "&sig=" + md5(path + "?" + paramStr + mapSk);
            }
            JSONObject json = JSONObject.parseObject(doGet(url));
            if (json == null || json.getInteger("status") == null || json.getInteger("status") != 0) {
                log.warn("腾讯地图 IP 定位失败: ip={}, resp={}", clientIp, json);
                return Result.error(json != null ? json.getString("message") : "IP定位失败");
            }
            return Result.OK(json.get("result"));
        } catch (IllegalStateException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("IP 网络定位异常", e);
            return Result.error("IP定位服务异常");
        }
    }

    private Result<Object> requestGeocoder(Map<String, String> params) throws Exception {
        String mapSk = getMapSk();
        String paramStr = buildParamString(params);
        String path = "/ws/geocoder/v1/";
        String url = QQ_MAP_BASE + path + "?" + paramStr;
        if (oConvertUtils.isNotEmpty(mapSk)) {
            url += "&sig=" + md5(path + "?" + paramStr + mapSk);
        }
        JSONObject json = JSONObject.parseObject(doGet(url));
        if (json == null || json.getInteger("status") == null || json.getInteger("status") != 0) {
            log.error("腾讯地图 geocoder 失败: {}", json);
            return Result.error(json != null ? json.getString("message") : "地图服务失败");
        }
        return Result.OK(json.get("result"));
    }

    private Result<Object> requestPlaceList(String path, Map<String, String> params, String action) throws Exception {
        String mapSk = getMapSk();
        String paramStr = buildParamString(params);
        String url = QQ_MAP_BASE + path + "?" + paramStr;
        if (oConvertUtils.isNotEmpty(mapSk)) {
            url += "&sig=" + md5(path + "?" + paramStr + mapSk);
        }
        JSONObject json = JSONObject.parseObject(doGet(url));
        if (json == null || json.getInteger("status") == null || json.getInteger("status") != 0) {
            log.error("腾讯地图 {} 失败: {}", action, json);
            return Result.error(json != null ? json.getString("message") : action + "失败");
        }
        JSONArray data = json.getJSONArray("data");
        return Result.OK(data != null ? data : new JSONArray());
    }

    private String getMapSk() {
        BaseConfig skConfig = configService.getConfigByCode(BizConstants.MAP_SK);
        if (skConfig != null && oConvertUtils.isNotEmpty(skConfig.getConfigValue())) {
            return skConfig.getConfigValue().trim();
        }
        return "";
    }

    private String buildParamString(Map<String, String> params) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return sb.toString();
    }

    private String resolveClientIp(HttpServletRequest request) {
        if (request == null) {
            return "";
        }
        String xff = request.getHeader("X-Forwarded-For");
        if (oConvertUtils.isNotEmpty(xff)) {
            String first = xff.split(",")[0].trim();
            if (oConvertUtils.isNotEmpty(first)) {
                return first;
            }
        }
        String realIp = request.getHeader("X-Real-IP");
        if (oConvertUtils.isNotEmpty(realIp)) {
            return realIp.trim();
        }
        return request.getRemoteAddr();
    }

    private boolean isPrivateOrLocalIp(String ip) {
        if (oConvertUtils.isEmpty(ip)) {
            return true;
        }
        String v = ip.trim().toLowerCase();
        return "unknown".equals(v)
                || "127.0.0.1".equals(v)
                || "::1".equals(v)
                || "0:0:0:0:0:0:0:1".equals(v)
                || v.startsWith("10.")
                || v.startsWith("192.168.")
                || v.startsWith("169.254.")
                || v.matches("^172\\.(1[6-9]|2[0-9]|3[0-1])\\..*")
                || v.startsWith("fc")
                || v.startsWith("fd")
                || v.startsWith("fe80:");
    }

    private String doGet(String url) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(10000)
                .setSocketTimeout(10000)
                .build();
        httpGet.setConfig(requestConfig);
        httpGet.setHeader("Content-type", "application/json");
        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            return EntityUtils.toString(response.getEntity(), "UTF-8");
        }
    }

    private String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("MD5计算失败", e);
        }
    }
}
