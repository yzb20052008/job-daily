package org.jeecg.common.util.sms;

import org.apache.commons.lang3.StringUtils;

/**
 * @Description: 短信枚举类
 * @Author: qingkonglan
 */
public enum TsSmsEnum {

    /**登录短信模板编码*/
	LOGIN_TEMPLATE_CODE("1714203","赣州市社联","code"),
    /**忘记密码短信模板编码*/
	FORGET_PASSWORD_TEMPLATE_CODE("1714203","赣州市社联","code"),
    /**注册账号短信模板编码*/
	REGISTER_TEMPLATE_CODE("1714203","赣州市社联","code"),
	/**到期通知*/
	NOTICE_TEMPLATE_CODE("1714205","赣州市社联","title,days"),
	/**认证通过通知*/
	VERIFY_SUCCESS_CODE("2120171","赣州市社联","name"),
	/**认证失败通知*/
	VERIF_FAILURE_CODE("2106996","赣州市社联","name"),
	/**我的计划通知*/
	PLAN_NOTICE_TEMPLATE_CODE("SMS_201470515","赣州市社联","username,title,time");

	/**
	 * 短信模板编码
	 */
	private String templateCode;
	/**
	 * 签名
	 */
	private String signName;
	/**
	 * 短信模板必需的数据名称，多个key以逗号分隔，此处配置作为校验
	 */
	private String keys;

	private TsSmsEnum(String templateCode, String signName, String keys) {
		this.templateCode = templateCode;
		this.signName = signName;
		this.keys = keys;
	}
	
	public String getTemplateCode() {
		return templateCode;
	}
	
	public void setTemplateCode(String templateCode) {
		this.templateCode = templateCode;
	}
	
	public String getSignName() {
		return signName;
	}
	
	public void setSignName(String signName) {
		this.signName = signName;
	}
	
	public String getKeys() {
		return keys;
	}

	public void setKeys(String keys) {
		this.keys = keys;
	}

	public static TsSmsEnum toEnum(String templateCode) {
		if(StringUtils.isEmpty(templateCode)){
			return null;
		}
		for(TsSmsEnum item : TsSmsEnum.values()) {
			if(item.getTemplateCode().equals(templateCode)) {
				return item;
			}
		}
		return null;
	}
}

