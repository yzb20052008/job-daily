package org.jeecg.modules.job.constant;

/**
 * 业务常量（兼容历史命名）。
 * <p>日结主链路请优先使用本接口中订单/岗位/资金/积分事件段；
 * 圈子/校园招聘遗留常量已标记 {@code @Deprecated}，勿再新增引用。</p>
 */
public interface BizConstants {

    // ========== 遗留：圈子角色（无引用，勿再用） ==========
    /** @deprecated 圈子遗留 */
    @Deprecated
    String MEMBER_ROLE_NORMAL  = "1";
    /** @deprecated 圈子遗留 */
    @Deprecated
    String MEMBER_ROLE_MANAGER  = "2";
    /** @deprecated 圈子遗留 */
    @Deprecated
    String MEMBER_ROLE_ADMIN  = "3";

    // ========== 遗留：申请/激活状态（旧招聘语义） ==========
    /** @deprecated 旧申请流 */
    @Deprecated
    Integer APPLY_STATUS_CANCEL  = 0;
    /** @deprecated 旧申请流 */
    @Deprecated
    Integer APPLY_STATUS_APPLY  = 1;
    /** @deprecated 旧申请流 */
    @Deprecated
    Integer APPLY_STATUS_AGREE  = 2;
    /** @deprecated 旧申请流 */
    @Deprecated
    Integer APPLY_STATUS_REFUSE  = 3;
    /** @deprecated 旧申请流 */
    @Deprecated
    Integer APPLY_STATUS_DELAY  = 4;

    /** @deprecated 旧激活态 */
    @Deprecated
    String ACTIVE_STATUS_DEFAULT="0";
    /** @deprecated 旧激活态 */
    @Deprecated
    String ACTIVE_STATUS_ACTIVE="1";
    /** @deprecated 旧激活态 */
    @Deprecated
    String ACTIVE_STATUS_INVALID="2";
    /** @deprecated 旧激活态 */
    @Deprecated
    String ACTIVE_STATUS_USED="3";


    /** 1-积分充值，2-积分转赠，3-卡券兑换，4-后台充值，5-购买会员*/
    /** 积分充值*/
    Integer INTEGRAL_RESOURCE_RECHARGE  = 1;
    /** 积分转赠*/
    Integer INTEGRAL_RESOURCE_TRANSFER  = 2;
    /** 卡券兑换 */
    Integer INTEGRAL_RESOURCE_EXCHANGE  = 3;
    /** 后台充值 */
    Integer INTEGRAL_RESOURCE_BACKGROUND  = 4;
    /** 购买会员 */
    Integer INTEGRAL_RESOURCE_BUYVIP  = 5;
    /** 咨询支付 */
    Integer INTEGRAL_RESOURCE_PLANER  = 6;


    /** 微信支付*/
    String PAY_TYPE_WX  = "wxPay";
    /** 支付宝支付*/
    String PAY_TYPE_ZFB  = "aliPay";
    /** 积分支付 */
    String PAY_TYPE_JF = "jfPay";
    /** 余额支付 */
    String PAY_TYPE_YE = "yePay";

    /**待支付*/
    Integer ORDER_STATUS_DEFAULT=0;
    /**支付成功*/
    Integer ORDER_STATUS_SUCCESS=1;
    /**支付失败*/
    Integer ORDER_STATUS_FAILURE=2;


    /** 账户类型：0-支付宝，1-微信，2-银联*/
    /**支付宝*/
    Integer ACCOUNT_TYPE_ZFB=0;
    /**微信*/
    Integer ACCOUNT_TYPE_WX=1;
    /**银联*/
    Integer ACCOUNT_TYPE_YL=2;


    /**Android*/
    String APP_TYPE_ANDROID="Android";
    /**IOS*/
    String APP_TYPE_IOS="IOS";


    /**
     * 城市缓存id
     */
    String CITY_REDIS_KEY = "bms:cache:city";
    /**
     * 职位缓存
     */
    String POST_REDIS_KEY = "bms:cache:post";

    /**
     * 工种缓存
     */
    String TYPES_REDIS_KEY = "post:cache:type";


    /**
     *账号注册
     */
    String JF_REGISTER= "jf_register";
    /**
     *每日签到
     */
    String JF_SIGN="jf_sign";
    /**
     *连续签到
     */
    String JF_SIGN_SEVEN="jf_sign_seven";
    /**
     *简历填写50%
     */
    String JF_RESUME_50="jf_resume_50";
    /**
     *简历填写80%
     */
    String JF_RESUME_80="jf_resume_80";
    /**
     *简历填写100%
     */
    String JF_RESUME_100= "jf_resume_100";
    /** 推荐企业注册（日结邀请仍可能使用） */
    String JF_REFER_COMPANY="jf_refer_company";
    /** @deprecated 校园招聘遗留 */
    @Deprecated
    String JF_REFER_SCHOOL="jf_refer_school";
    /** @deprecated 校园招聘遗留 */
    @Deprecated
    String JF_REFER_TEACHER="jf_refer_teacher";
    /** @deprecated 校园招聘遗留 */
    @Deprecated
    String JF_REFER_PLANER="jf_refer_planer";
    /** @deprecated 校园招聘遗留 */
    @Deprecated
    String JF_RESUME_APPLY_READ="jf_resume_apply_read";
    /** 推荐用户注册 */
    String  JF_REFER_USER="jf_refer_user";
    /** @deprecated 校园招聘遗留 */
    @Deprecated
    String JF_RESUME_APPLY_SUCCESS="jf_resume_apply_success";
    /** 发布新岗位（配置分值，发奖接线可选） */
    String JF_POST_ADD="jf_post_add";
    /** @deprecated HR 招聘遗留，未接线 */
    @Deprecated
    String JF_HR_CHAT="jf_hr_chat";
    /** @deprecated HR 招聘遗留，未接线 */
    @Deprecated
    String JF_HR_REPLY_1="jf_hr_reply_1";
    /** @deprecated HR 招聘遗留，未接线 */
    @Deprecated
    String JF_HR_REPLY_4="jf_hr_reply_4";
    /** @deprecated HR 招聘遗留，未接线 */
    @Deprecated
    String JF_HR_REPLY_24="jf_hr_reply_24";
    /** @deprecated HR 招聘遗留，未接线 */
    @Deprecated
    String  JF_HR_REPLY_72="jf_hr_reply_72";
    /** 企业认证通过（配置分值） */
    String JF_COMPANY_VERIFY="jf_company_verify";
    /** 实名认证通过 */
    String JF_USER_VERIFY="jf_user_verify";
    /**
     *每日积分阈值
     */
    String JF_DAY_MAX="jf_day_max";

    /**
     *实名认证人工自动
     */
    String REALNAME_AUTH="realname_auth";

    /**
     *企业认证人工自动
     */
    String COMPANY_AUTH="company_auth";

    /**
     *拨号消费积分
     */
    String JF_CALL="jf_call";

    /**
     * 打卡允许半径（米），与前端 clock_range 同源
     */
    String CLOCK_RANGE="clock_range";

    /**
     * 腾讯地图 WebService / JS Key（可下发前端渲染地图）
     */
    String MAP_KEY="map_key";

    /**
     * 腾讯地图签名密钥 SK（仅服务端使用，勿下发客户端）
     */
    String MAP_SK="map_sk";

    /**
     * 待评价超时自动完结（小时）
     */
    String EVALUATE_TIMEOUT_HOURS="evaluate_timeout_hours";

    /**
     * 待开工已过结束时间是否自动取消：1=开（默认），0=关
     */
    String ORDER_NO_START_AFTER_END="order_no_start_after_end";

    /**
     * 拨号记录确认时长
     */
    String CALL_ENSURE_TIME="call_ensure_time";

    /**
     * 积分充值比例
     */
    String JF_RECHARGE_RATIO="jf_recharge_ratio";


    //简历完善度进度
    //基本信息
    int RESUME_BASE_PERCENT=30;
    //个人优势
    int RESUME_SKILL=10;
    //求职状态
    int RESUME_STATE=10;
    //求职期望
    int RESUME_EXPECT=10;
    //工作经历
    int RESUME_WORK=10;
    //项目经历
    int RESUME_PRO=10;
    //教育经历
    int RESUME_EDU=10;
    //个人荣誉
    int RESUME_HONOR=10;

    //道具编码
    String JF_CODE_EYE="eye";//醒目卡
    String JF_CODE_TOPPING="topping";//置顶卡
    String JF_CODE_PERSPECTIVE="perspective";//透视卡
    String JF_CODE_REFRESH="refresh";//刷新卡
    /** AI卡（已下线，保留常量用于拦截购买） */
    String JF_CODE_AI="ai";

    /**待审核*/
    Integer AUTH_STATUS_DEFAULT=0;
    /**审核通过*/
    Integer AUTH_STATUS_SUCCESS=1;
    /**审核失败*/
    Integer AUTH_STATUS_FAILURE=2;

    /**默认*/
    Integer AGREE_STATUS_DEFAULT=0;
    /**合作达成*/
    Integer AGREE_STATUS_SUCCESS=1;
    /**合作未达成*/
    Integer AGREE_STATUS_FAILURE=2;
    /**超时*/
    Integer AGREE_STATUS_OVERTIME=3;

//    订单状态：0-待确认，1-待开工，2-工作中，3-待结算，4-待评价，5-已完成，6-已取消
    /**待确认*/
    String ORDER_STATUS_WAIT_ENSURE="0";
    /**待开工*/
    String ORDER_STATUS_WAIT_START="1";
    /**工作中*/
    String ORDER_STATUS_WORKING="2";
    /**待结算*/
    String ORDER_STATUS_WAIT_PAY="3";
    /**待评价*/
    String ORDER_STATUS_WAIT_COMMENT="4";
    /**已完成*/
    String ORDER_STATUS_FINISH="5";
    /**已取消*/
    String ORDER_STATUS_CANCEL="6";

    /**用户角色*/
    String ROLE_CODE_MEMBER="member";
    /**企业角色*/
    String ROLE_CODE_COMPANY="company";


//    招工状态：1-待审核，2-招工中，3-发布失败，4-已停招，5-已取消，6-已招满
    /**待审核*/
    String POST_STATUS_VERIFY="1";
    /**招工中*/
    String POST_STATUS_RUNNING="2";
    /**发布失败*/
    String POST_STATUS_FAILURE="3";
    /**已停招*/
    String POST_STATUS_STOP="4";
    /**已取消*/
    String POST_STATUS_CANCEL="5";
    /**已招满*/
    String POST_STATUS_FULL="6";


    /** 充值状态 0-未支付，1-支付成功，2-支付失败*/
    /** 未支付*/
    String RECHARGE_STATUS_DEFAULT  = "0";
    /** 支付成功*/
    String RECHARGE_STATUS_SUCCESS  = "1";
    /** 支付失败 */
    String RECHARGE_STATUS_FAILURE  = "2";


    /** 提现审核状态：0-待审核，1-审核通过(打款中/已到账看 transfer_status)，2-失败(含拒绝/转账失败解冻)*/
    Integer WITHDRAW_STATUS_DEFAULT  = 0;
    /** 审核通过（到账以 transfer_status=SUCCESS 为准）*/
    Integer WITHDRAW_STATUS_SUCCESS  = 1;
    /** 失败（审核拒绝或转账失败） */
    Integer WITHDRAW_STATUS_FAILURE  = 2;

    /** base_config：单次提现最低金额 */
    String WITHDRAW_MIN = "withdraw_min";
    /** base_config：单次提现最高金额 */
    String WITHDRAW_MAX = "withdraw_max";
    /** base_config：当日提现最高金额 */
    String WITHDRAW_DAY_MAX = "withdraw_day_max";


    /** 交易类型 1-工资收入，2-工资支出，3-充值积分，4-用户提现，5-余额充值*/
    /** 工资收入*/
    String TRADE_TYPE_SALARY_IN  = "1";
    /** 工资支出*/
    String TRADE_TYPE_SALARY_OUT  = "2";
    /** 充值积分 */
    String TRADE_TYPE_RECHARGE_INTEGRAL  = "3";
    /** 用户提现 */
    String TRADE_TYPE_WITHDRAW  = "4";
    /** 余额充值 */
    String TRADE_TYPE_RECHARGE_BALANCE = "5";


    /** 打卡类型 1-上班，2-下班/
    /** 上班*/
    Integer CLOCK_TYPE_ON  = 1;
    /** 下班 */
    Integer CLOCK_TYPE_OFF  = 2;


    /** 转账状态：ACCEPTED-已受理，PROCESSING-转账锁定资金中，WAIT_USER_CONFIRM-待收款用户确认，TRANSFERING-转账中，SUCCESS-转账成功，FAIL- 转账失败，CANCELING-转账撤销中，CANCELLED-转账撤销完成*/
    /** 已受理*/
    String TRANSFER_STATUS_ACCEPTED  = "ACCEPTED";
    /** 转账锁定资金中*/
    String TRANSFER_STATUS_PROCESSING  = "PROCESSING";
    /** 待收款用户确认 */
    String TRANSFER_STATUS_WAIT_USER_CONFIRM  = "WAIT_USER_CONFIRM";
    /** 转账中*/
    String TRANSFER_STATUS_TRANSFERING  = "TRANSFERING";
    /** 转账成功*/
    String TRANSFER_STATUS_SUCCESS  = "SUCCESS";
    /** 转账失败 */
    String TRANSFER_STATUS_FAIL  = "FAIL";
    /** 转账撤销中 */
    String TRANSFER_STATUS_CANCELING  = "CANCELING";
    /** 转账撤销完成 */
    String TRANSFER_STATUS_CANCELLED  = "CANCELLED";


}
