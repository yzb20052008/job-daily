package org.jeecg.modules.job.job.support;

import org.jeecg.common.util.oConvertUtils;

/**
 * 打卡地理距离（米）。半径以 base_config.clock_range 为准，缺省用 DEFAULT_MAX_METERS。
 */
public final class ClockGeoHelper {

    /** 默认允许打卡半径（米），与历史后端一致；可被配置覆盖 */
    public static final double DEFAULT_MAX_METERS = 2000D;

    private ClockGeoHelper() {
    }

    public static Double distanceMeters(String lat1, String lng1, String lat2, String lng2) {
        if (oConvertUtils.isEmpty(lat1) || oConvertUtils.isEmpty(lng1)
                || oConvertUtils.isEmpty(lat2) || oConvertUtils.isEmpty(lng2)) {
            return null;
        }
        try {
            double la1 = Double.parseDouble(lat1);
            double lo1 = Double.parseDouble(lng1);
            double la2 = Double.parseDouble(lat2);
            double lo2 = Double.parseDouble(lng2);
            return haversineMeters(la1, lo1, la2, lo2);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /**
     * 解析配置半径，非法或空则回退默认值
     */
    public static double resolveMaxMeters(String configValue) {
        if (oConvertUtils.isEmpty(configValue)) {
            return DEFAULT_MAX_METERS;
        }
        try {
            double v = Double.parseDouble(configValue.trim());
            return v > 0 ? v : DEFAULT_MAX_METERS;
        } catch (NumberFormatException ex) {
            return DEFAULT_MAX_METERS;
        }
    }

    private static double haversineMeters(double lat1, double lon1, double lat2, double lon2) {
        double r = 6371000D;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return r * c;
    }
}
