package org.jeecg.modules.job.api.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckUtils {

    /**
     * 是否为邮箱地址
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    public static boolean isPhoneNumber(String number) {
        // 正则表达式匹配，手机号的格式可以根据需求进行修改
        String regex = "^(13\\d|14[5-9]|15[0-3,5-9]|16[6]|17[0-8]|18\\d|19[0-3,5-9])\\d{8}$";
        return number.matches(regex);
    }
}
