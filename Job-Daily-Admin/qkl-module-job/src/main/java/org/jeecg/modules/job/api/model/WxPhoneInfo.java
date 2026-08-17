package org.jeecg.modules.job.api.model;

/**
 * 解析微信授权获取手机号信息，解密后内容
 */
public class WxPhoneInfo {
    //{"phoneNumber":"15818613619","purePhoneNumber":"15818613619","countryCode":"86","watermark":{"timestamp":1551864698,"appid":"wx9cb7347b1c98a719"}}
    private String phoneNumber;
    private String purePhoneNumber;
    private String countryCode;
    private WaterMark watermark;


    class WaterMark{
        private long timestamp;
        private String appid;

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public String getAppid() {
            return appid;
        }

        public void setAppid(String appid) {
            this.appid = appid;
        }

        @Override
        public String toString() {
            return "WaterMark{" +
                    "timestamp=" + timestamp +
                    ", appid='" + appid + '\'' +
                    '}';
        }
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPurePhoneNumber() {
        return purePhoneNumber;
    }

    public void setPurePhoneNumber(String purePhoneNumber) {
        this.purePhoneNumber = purePhoneNumber;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public WaterMark getWatermark() {
        return watermark;
    }

    public void setWatermark(WaterMark watermark) {
        this.watermark = watermark;
    }

    @Override
    public String toString() {
        return "WxPhoneInfo{" +
                "phoneNumber='" + phoneNumber + '\'' +
                ", purePhoneNumber='" + purePhoneNumber + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", watermark=" + watermark +
                '}';
    }
}
