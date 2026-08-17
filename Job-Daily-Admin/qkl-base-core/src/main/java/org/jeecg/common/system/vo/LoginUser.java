package org.jeecg.common.system.vo;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import org.jeecg.common.desensitization.annotation.SensitiveField;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 在线用户信息
 * </p>
 *
 * @Author scott
 * @since 2018-12-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class LoginUser {

	/**
	 * 登录人id
	 */
	@SensitiveField
	private String id;

	/**
	 * 登录人账号
	 */
	@SensitiveField
	private String username;

	/**
	 * 登录人名字
	 */
	@SensitiveField
	private String realname;

	/**
	 * 登录人密码
	 */
	@SensitiveField
	private String password;

	/**
	 * md5密码盐
	 */
	private String salt;

     /**
      * 当前登录部门code
      */
    private String orgCode;
	/**
	 * 头像
	 */
	@SensitiveField
	private String avatar;

	/**
	 * 生日
	 */
	@SensitiveField
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date birthday;

	/**
	 * 性别（1：男 2：女）
	 */
	private Integer sex;

	/**
	 * 电子邮件
	 */
	@SensitiveField
	private String email;

	/**
	 * 电话
	 */
	@SensitiveField
	private String phone;

	/**
	 * 状态(1：正常 2：冻结 ）
	 */
	private Integer status;
	
	private Integer delFlag;
	/**
     * 同步工作流引擎1同步0不同步
     */
    private Integer activitiSync;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 *  身份（1 普通员工 2 上级）
	 */
	private Integer userIdentity;

	/**
	 * 管理部门ids
	 */
	private String departIds;

	/**
	 * 职务，关联职务表
	 */
	@SensitiveField
	private String post;

	/**
	 * 座机号
	 */
	@SensitiveField
	private String telephone;

	/** 多租户ids临时用，不持久化数据库(数据库字段不存在) */
	private String relTenantIds;

	/**设备id uniapp推送用*/
	private String clientId;

	/**
	 * 第三方登录的唯一标识
	 */
	private String thirdId;

	/**
	 * 抖音登录的唯一标识
	 */
	private String ttId;

	/**
	 * 用户编码
	 */
	@SensitiveField
	private String userCode;

	/**
	 * 邀请码
	 */
	private String inviteCode;

	/**
	 * 登录人昵称
	 */
	@SensitiveField
	private String nickname;

	/**
	 * 会员等级
	 */
	private String vipLevel;
	/**
	 * 会员到期时间
	 */
	private Date vipEndTime;
	/**
	 * 可用积分
	 */
	private Integer integral;
	/**
	 * 历史积分
	 */
	private Integer totalIntegral;

	/**
	 * 当前角色
	 */
	private String memberRole;

	/**
	 * 当前角色名称
	 */
	private String memberRoleName;

	/**
	 * 公司ID
	 */
	private String companyId;
	/**
	 * 公司名称
	 */
	private String company;

	/**
	 * 所属平台：admin-管理后台，mobile-移动应用
	 */
	private String platform;

	private String referrer;//推荐用户id

	//登录token
	private String loginToken;

	//登录时间
	private Date loginTime;

	@TableField(exist = false)
	private String postName;
	//企业认证:-1-未实名，0-审核，1-通过，2-失败
	@TableField(exist = false)
	private int companyAuth;
	@TableField(exist = false)
	private boolean ifAddResume;
	@TableField(exist = false)
	private int companyRole;
	//简历完善度
	@TableField(exist = false)
	private int percentage;
	//实名认证:-1-未实名，0-审核，1-通过，2-失败
	@TableField(exist = false)
	private int realNameAuth;
}
