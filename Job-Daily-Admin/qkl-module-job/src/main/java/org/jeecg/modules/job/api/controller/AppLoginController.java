package org.jeecg.modules.job.api.controller;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.exceptions.ClientException;
import com.google.gson.Gson;
import com.xkcoding.http.HttpUtil;
import com.xkcoding.http.support.HttpHeader;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CacheConstant;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.*;
import org.jeecg.common.util.sms.DySmsEnum;
import org.jeecg.common.util.sms.DySmsHelper;
import org.jeecg.modules.base.service.BaseCommonService;
import org.jeecg.modules.job.api.model.UserLocation;
import org.jeecg.modules.job.api.model.WxPhoneInfo;
import org.jeecg.modules.job.api.util.*;
import org.jeecg.modules.job.constant.BizConstants;
import org.jeecg.modules.job.integral.service.IIntegralLogService;
import org.jeecg.modules.job.job.entity.JobCompany;
import org.jeecg.modules.job.job.service.IJobCompanyService;
import org.jeecg.modules.job.job.service.IJobResumeService;
import org.jeecg.modules.job.ums.entity.UmsRealnameAuth;
import org.jeecg.modules.job.ums.service.IUmsRealnameAuthService;
import org.jeecg.modules.job.ums.service.IUmsReferrerLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 *
 */
@RestController
@RequestMapping("/api/login")
@Api(tags = "移动端用户登录")
@Slf4j
public class AppLoginController {
    @Autowired
    private ISysBaseAPI sysBaseApi;
    @Autowired
    private RedisUtil redisUtil;
    @Resource
    private BaseCommonService baseCommonService;
    @Resource
    private IUmsReferrerLogService referrerLogService;
    @Resource
    private IIntegralLogService integralLogService;
    @Resource
    private IUmsRealnameAuthService realnameAuthService;
    @Resource
    private IJobCompanyService companyService;
    @Resource
    private IJobResumeService resumeService;

    /**
     * 是否为调试模式
     */
    @Value(value="${jeecg.devTest}")
    private boolean devTest;

    /**
     * 退出登录
     *
     * @param request
     * @param response
     * @return
     */
    @ApiOperation("退出登录")
    @PostMapping(value = "/logout")
    public Result<Object> logout(HttpServletRequest request, HttpServletResponse response) {
        //用户退出逻辑
        String token = request.getHeader(CommonConstant.X_ACCESS_TOKEN);
        if (oConvertUtils.isEmpty(token)) {
            return Result.error("退出登录失败！");
        }
        String username = JwtUtil.getUsername(token);
        LoginUser sysUser = sysBaseApi.getUserByName(username);
        if (sysUser != null) {
            //update-begin--Author:wangshuai  Date:20200714  for：登出日志没有记录人员
            baseCommonService.addLog("用户名: " + sysUser.getRealname() + ",退出成功！", CommonConstant.LOG_TYPE_1, null, sysUser);
            //update-end--Author:wangshuai  Date:20200714  for：登出日志没有记录人员
            log.info(" 用户名:  " + sysUser.getRealname() + ",退出成功！ ");
            //清空用户登录Token缓存
            redisUtil.del(CommonConstant.PREFIX_USER_TOKEN + token);
            //清空用户登录Shiro权限缓存
            redisUtil.del(CommonConstant.PREFIX_USER_SHIRO_CACHE + sysUser.getId());
            //清空用户的缓存信息（包括部门信息），例如sys:cache:user::<username>
            redisUtil.del(String.format("%s::%s", CacheConstant.SYS_USERS_CACHE, sysUser.getUsername()));
            //调用shiro的logout
            SecurityUtils.getSubject().logout();
            return Result.ok("退出登录成功！");
        } else {
            return Result.error("Token无效!");
        }
    }

    /**
     * 短信验证码接口
     * @return
     */
    @ApiOperation("短信验证码接口")
    @GetMapping(value = "/getSmsCode")
    public Result<String> getSmsCode(@RequestParam(name="phone") String phone,@RequestParam(name="smsmode") String smsmode) {
        Result<String> result = new Result<String>();
        //短信模板方式  0 .登录模板、1.注册模板、2.忘记密码模板
        log.info(phone);
        if (oConvertUtils.isEmpty(phone)) {
            result.setMessage("手机号不允许为空！");
            result.setSuccess(false);
            result.setCode(500);
            return result;
        }
        //是否测试
        if(devTest){
            result.setResult("发送成功");
            result.setMessage("输入任意验证码即可");
            result.setSuccess(true);
            result.setCode(CommonConstant.SC_OK_200);
            return result;
        }
        Object object = redisUtil.get(phone);
        if (object != null) {
            result.setMessage("验证码10分钟内，仍然有效！");
            result.setSuccess(false);
            result.setCode(CommonConstant.SC_OK_200);
            return result;
        }
        //随机数
        String captcha = RandomUtil.randomNumbers(6);
        JSONObject obj = new JSONObject();
        obj.put("code", captcha);
        try {
            boolean b = true;
            /**
             * smsmode 短信模板方式  0 .登录模板、1.注册模板、2.忘记密码模板
             */
            if (CommonConstant.SMS_TPL_TYPE_0.equals(smsmode)) {
                //登录模板
				b = DySmsHelper.sendSms(phone, obj, DySmsEnum.LOGIN_TEMPLATE_CODE);
            } else if (CommonConstant.SMS_TPL_TYPE_1.equals(smsmode)) {
                //忘记密码模板
                b = DySmsHelper.sendSms(phone, obj, DySmsEnum.REGISTER_TEMPLATE_CODE);
            } else if (CommonConstant.SMS_TPL_TYPE_2.equals(smsmode)) {
                //忘记密码模板
                b = DySmsHelper.sendSms(phone, obj, DySmsEnum.FORGET_PASSWORD_TEMPLATE_CODE);
            }
            if (b == false) {
                result.setMessage("短信验证码发送失败,请稍后重试");
                result.setSuccess(false);
                return result;
            }
            //验证码10分钟内有效
            redisUtil.set(phone, captcha, 600);
            //update-begin--Author:scott  Date:20190812 for：issues#391
            //result.setResult(captcha);
            //update-end--Author:scott  Date:20190812 for：issues#391
            result.setResult("发送成功");
            result.setMessage("发送成功");
            result.setSuccess(true);
            result.setCode(CommonConstant.SC_OK_200);
        } catch (ClientException e) {
			log.error("操作异常", e);
			result.error500(" 短信接口未配置，请联系管理员！");
            return result;
        }
        return result;
    }

    /**
     * 短信验证码校验接口
     *
     * @param jsonObject
     * @return
     */
    @ApiOperation("短信验证码校验接口")
    @PostMapping(value = "/judgeSmsCode")
    public Result<String> judgeSmsCode(@RequestBody JSONObject jsonObject) {
        Result<String> result = new Result<String>();
        String phone = jsonObject.get("phone").toString();
        String smscode = jsonObject.get("code").toString();
        log.info(phone);
        if (oConvertUtils.isEmpty(phone)) {
            result.setMessage("手机号不允许为空！");
            result.setSuccess(false);
            return result;
        }
        //是否测试
        if(devTest){
            result.setResult("校验成功");
            result.setMessage("校验成功");
            result.setSuccess(true);
            result.setCode(CommonConstant.SC_OK_200);
            return result;
        }
        Object code = redisUtil.get(phone);
        if (!smscode.equals(code)) {
            result.setMessage("手机验证码错误");
            return result;
        }
        result.setResult("校验成功");
        result.setMessage("校验成功");
        result.setSuccess(true);
        result.setCode(CommonConstant.SC_OK_200);
        return result;
    }


    /**
     * 邮箱验证码接口
     *
     * @param jsonObject
     * @return
     */
    @ApiOperation("邮箱验证码接口")
    @PostMapping(value = "/getEmailCode")
    public Result<String> getEmailCode(@RequestBody JSONObject jsonObject) {
        Result<String> result = new Result<String>();
        String email = jsonObject.get("email").toString();
        if (oConvertUtils.isEmpty(email)) {
            result.setMessage("邮箱号为空！");
            result.setSuccess(false);
            return result;
        }
        //是否测试
        if(devTest){
            result.setResult("发送成功");
            result.setMessage("输入任意验证码即可");
            result.setSuccess(true);
            result.setCode(CommonConstant.SC_OK_200);
            return result;
        }
        Object object = redisUtil.get(email);
        if (object != null) {
            result.setMessage("验证码10分钟内，仍然有效！");
            result.setSuccess(false);
            result.setCode(CommonConstant.SC_OK_200);
            return result;
        }
        //随机数
        String captcha = RandomUtil.randomNumbers(6);
        JSONObject obj = new JSONObject();
        obj.put("code", captcha);
        try {
            boolean b = true;
            sysBaseApi.sendEmailMsg(email,"验证码","验证码为："+captcha);
            //验证码10分钟内有效
            redisUtil.set(email, captcha, 600);
            result.setResult("发送成功");
            result.setMessage("发送成功");
            result.setSuccess(true);
            result.setCode(CommonConstant.SC_OK_200);
        } catch (Exception e) {
			log.error("操作异常", e);
			result.error500("发送失败"+e.getMessage());
            return result;
        }
        return result;
    }


    /**
     * App账号密码登录
     *
     * @param jsonObject
     * @return
     * @throws Exception
     */
    @ApiOperation("app账号密码登录")
    @RequestMapping(value = "/mLogin", method = RequestMethod.POST)
    public Result<JSONObject> mLogin(@RequestBody JSONObject jsonObject) throws Exception {
        Result<JSONObject> result = new Result<JSONObject>();
        String phone = jsonObject.getString("phone");
        String password = jsonObject.getString("password");
        LoginUser sysUser=null;
        //判断是手机号还是邮箱号
        if (CheckUtils.isPhoneNumber(phone)){
            //手机号
            sysUser = sysBaseApi.getUserByPhone(phone);
        }else{
            //邮箱
            sysUser = sysBaseApi.getUserByEmail(phone);
        }
        //1. 校验用户是否有效
        result = (Result<JSONObject>) sysBaseApi.checkUserIsEffective(sysUser);
        if (!result.isSuccess()) {
            return result;
        }
        //2. 校验用户名或密码是否正确
        String userpassword = PasswordUtil.encrypt(sysUser.getPhone(), password, sysUser.getSalt());
        String syspassword = sysUser.getPassword();
        if (!syspassword.equals(userpassword)) {
            result.error500("账号或密码错误");
            return result;
        }
        //如果已存在，再强退
        String lastToken=sysUser.getLoginToken();
        if (oConvertUtils.isNotEmpty(lastToken)){
            redisUtil.del(CommonConstant.PREFIX_USER_TOKEN + lastToken);
            //清空用户登录Shiro权限缓存
            redisUtil.del(CommonConstant.PREFIX_USER_SHIRO_CACHE + sysUser.getId());
            //清空用户的缓存信息（包括部门信息），例如sys:cache:user::<username>
            redisUtil.del(String.format("%s::%s", CacheConstant.SYS_USERS_CACHE, sysUser.getUsername()));
        }
        JSONObject obj = new JSONObject();
        //用户登录信息
        obj.put("userInfo", sysUser);

        // 生成token
        String token = JwtUtil.sign(sysUser.getPhone(), syspassword);
        // 设置超时时间
        redisUtil.set(CommonConstant.PREFIX_USER_TOKEN + token, token);
        redisUtil.expire(CommonConstant.PREFIX_USER_TOKEN + token, JwtUtil.EXPIRE_TIME * 2 / 1000);

        sysUser.setLoginTime(new Date());
        sysUser.setLoginToken(token);
        sysBaseApi.updateUserInfo(sysUser);

        //token 信息
        obj.put("token", token);
        result.setResult(obj);
        result.setSuccess(true);
        result.setCode(200);
        result.setMessage("登录成功");
        baseCommonService.addLog("登录账号: " + phone + ",登录成功[移动端]！", CommonConstant.LOG_TYPE_1, null);
        return result;
    }

    /**
     * App手机号登录接口
     *
     * @param jsonObject
     * @return
     */
    @ApiOperation("App手机号登录接口")
    @PostMapping("/mPhoneLogin")
    public Result<JSONObject> mPhoneLogin(@RequestBody JSONObject jsonObject) {
        Result<JSONObject> result = new Result<JSONObject>();
        String phone = jsonObject.getString("phone");
        String smscode = jsonObject.getString("code");
        String referrer = jsonObject.getString("referrer");
        String inviteCode = jsonObject.getString("inviteCode");
        String role = jsonObject.getString("role");//角色编码
        Object code = redisUtil.get(phone);
        if(devTest){
            //此处可添加指定验证码校验
        }else{
            if (!smscode.equals(code)) {
			    result.setMessage("手机验证码错误");
			    result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
			    return result;
		    }
        }
        //校验用户有效性
        LoginUser sysUser = sysBaseApi.getUserByName(phone);
        result = (Result<JSONObject>) sysBaseApi.checkUserIsEffective(sysUser);
        if (!result.isSuccess()) {
            if (!result.getMessage().equals("该用户不存在，请注册")) {
                return result;
            } else {
                //注册新用户
                sysUser = new LoginUser();
                sysUser.setId(RandomUtil.randomNumbers(19));
                sysUser.setPhone(phone);
                sysUser.setUsername(phone);
                sysUser.setNickname("用户"+sysUser.getPhone().substring(7));
                sysUser.setRealname(sysUser.getNickname());
                sysUser.setStatus(1);
                sysUser.setDelFlag(CommonConstant.DEL_FLAG_0);
                sysUser.setSex(0);
                sysUser.setUserIdentity(3);//移动端
                String s = UUID.randomUUID().toString().replace("-", "");
//				sysUser.setUuid(s);
                String salt = oConvertUtils.randomGen(8);
                sysUser.setSalt(salt);
                String password = "qkl123456";//默认密码
                String passwordEncode = PasswordUtil.encrypt(sysUser.getUsername(), password, salt);
                sysUser.setPassword(passwordEncode);
                //生成用户编码
                sysUser.setUserCode(OrderUtils.getLocalTrmSeqNum());
                //生成inviteCode
                String mId = null;
                Boolean haveId= true;//默认有重复的推荐码
                while (haveId) {
                    mId=RandomUtil.randomNumbers(6);
                    //数据库查找
                    LoginUser tt = sysBaseApi.getUserByInviteCode(mId);
                    if (tt == null) {//没有查到数据，可以使用
                        haveId = false;
                    }
                }
                sysUser.setInviteCode(mId);
                //推荐用户-手动
                if (oConvertUtils.isNotEmpty(inviteCode)){
                    LoginUser uu=sysBaseApi.getUserByInviteCode(inviteCode);
                    sysUser.setReferrer(uu.getId());
                    referrerLogService.addReferrerLog(role,sysUser.getId(),uu.getId());
                }else{
                    //推荐用户-自动
                    if (oConvertUtils.isNotEmpty(referrer)){
                        sysUser.setReferrer(referrer);
                        referrerLogService.addReferrerLog(role,sysUser.getId(),referrer);
                    }
                }
                //查询角色id
                String roleId=sysBaseApi.getRoleIdByCode("member");//首次注册默认应聘者
                //当前角色,默认游客
                sysBaseApi.saveUser(sysUser,roleId,null,null);
                //账号更新积分
                integralLogService.addIntegralLog(sysUser.getId(), BizConstants.JF_REGISTER,"","新用户注册获得积分：");
            }
        }
        //如果已存在，再强退
        String lastToken=sysUser.getLoginToken();
        if (oConvertUtils.isNotEmpty(lastToken)){
            redisUtil.del(CommonConstant.PREFIX_USER_TOKEN + lastToken);
            //清空用户登录Shiro权限缓存
            redisUtil.del(CommonConstant.PREFIX_USER_SHIRO_CACHE + sysUser.getId());
            //清空用户的缓存信息（包括部门信息），例如sys:cache:user::<username>
            redisUtil.del(String.format("%s::%s", CacheConstant.SYS_USERS_CACHE, sysUser.getUsername()));
        }
        JSONObject obj = new JSONObject();
        //用户登录信息
        obj.put("userInfo", sysUser);
        // 生成token
        String token = JwtUtil.sign(sysUser.getUsername(), sysUser.getPassword());
        // 设置超时时间
        redisUtil.set(CommonConstant.PREFIX_USER_TOKEN + token, token);
        redisUtil.expire(CommonConstant.PREFIX_USER_TOKEN + token, JwtUtil.EXPIRE_TIME * 2 / 1000);

        sysUser.setLoginTime(new Date());
        sysUser.setLoginToken(token);
        sysBaseApi.updateUserInfo(sysUser);

        //token 信息
        obj.put("token", token);
        result.setResult(obj);
        result.setSuccess(true);
        result.setCode(200);
        result.setMessage("登录成功");
        baseCommonService.addLog("用户名: " + phone + ",登录成功[移动端]！", CommonConstant.LOG_TYPE_1, null);
        return result;
    }


    /**
     * 抖音小程序授权登录接口
     *
     * @param jsonObject
     * @return
     */
    @ApiOperation("小程序授权登录接口")
    @PostMapping("/mDyLogin")
    public Result<JSONObject> mDyLogin(@RequestBody JSONObject jsonObject) {
        Result<JSONObject> result = new Result<JSONObject>();
        try {
            String role = jsonObject.getString("role");//角色编码
            String code = jsonObject.getString("code");
            String encryptedData = jsonObject.getString("encryptedData");
            String iv = jsonObject.getString("iv");
            String referrer = jsonObject.getString("referrer");
            String inviteCode = jsonObject.getString("inviteCode");
            String sessionkey = ""; //会话秘钥
            String openid = ""; //用户唯一标识
            LoginUser sysUser =null;
            String url = "https://developer.toutiao.com/api/apps/v2/jscode2session";
            Map<String,String> params=new HashMap<>();
            params.put("appid",DyUtil.getAppId());
            params.put("secret",DyUtil.getAppSecret());
            params.put("code",code);
            params.put("anonymous_code","");
            HttpHeader header=new HttpHeader();
            header.add("Content-Type","application/json");
            Gson gson=new Gson();
            String jsons=gson.toJson(params);
            String re=HttpUtil.post(url,jsons);
            System.out.println(re + "响应报文");
            cn.hutool.json.JSONObject jsonObject1 = JSONUtil.parseObj(re);
            String data = jsonObject1.getStr("data");
            Map<String, String> map = new HashMap<>();
            map = gson.fromJson(data, map.getClass());
            sessionkey = map.get("session_key").toString(); //会话秘钥
            openid = map.get("openid").toString(); //用户唯一标识
            //根据openid判断用户是否存在
            sysUser = sysBaseApi.getUserByTtId(openid);
            if (!oConvertUtils.isEmpty(encryptedData)) {
                if (sysUser == null) {
                    //注册新用户
//                    if (oConvertUtils.isEmpty(role)){
//                        return Result.error("请选择角色");
//                    }
                    sysUser = new LoginUser();
                    sysUser.setId(RandomUtil.randomNumbers(19));
                    sysUser.setTtId(openid);
                    String avatar=jsonObject.get("avatarUrl").toString();
                    if (oConvertUtils.isNotEmpty(avatar)){
                        sysUser.setAvatar(avatar);
                    }else{
                    }
                    sysUser.setUsername(openid);
                    String nickname=jsonObject.get("nickName").toString();
                    if (oConvertUtils.isNotEmpty(nickname)){
                        sysUser.setNickname(nickname);
                        sysUser.setRealname(sysUser.getNickname());
                    }else{
//                        sysUser.setNickname(RandomName.randomName(true,3));
                        sysUser.setNickname("用户"+sysUser.getPhone().substring(7));
                        sysUser.setRealname(sysUser.getNickname());
                    }
                    sysUser.setStatus(1);
                    sysUser.setDelFlag(CommonConstant.DEL_FLAG_0);
                    sysUser.setUserIdentity(3);//移动端
                    String salt = oConvertUtils.randomGen(8);
                    sysUser.setSalt(salt);
                    String password = "qkl123456";//默认密码
                    String passwordEncode = PasswordUtil.encrypt(sysUser.getUsername(), password, salt);
                    sysUser.setPassword(passwordEncode);
                    //生成用户编码
                    sysUser.setUserCode(OrderUtils.getLocalTrmSeqNum());
                    //生成inviteCode
                    String mId = null;
                    Boolean haveId= true;//默认有重复的推荐码
                    while (haveId) {
                        mId=RandomUtil.randomNumbers(6);
                        //数据库查找
                        LoginUser tt = sysBaseApi.getUserByInviteCode(mId);
                        if (tt == null) {//没有查到数据，可以使用
                            haveId = false;
                        }
                    }
                    sysUser.setInviteCode(mId);
                    //推荐用户-手动
                    if (oConvertUtils.isNotEmpty(inviteCode)){
                        LoginUser uu=sysBaseApi.getUserByInviteCode(inviteCode);
                        sysUser.setReferrer(uu.getId());
                        referrerLogService.addReferrerLog(role,sysUser.getId(),uu.getId());
                    }else{
                        //推荐用户-自动
                        if (oConvertUtils.isNotEmpty(referrer)){
                            sysUser.setReferrer(referrer);
                            referrerLogService.addReferrerLog(role,sysUser.getId(),referrer);
                        }
                    }
                    //查询角色id
                    String roleId=sysBaseApi.getRoleIdByCode("member");//默认求职者
                    //当前角色
                    sysBaseApi.saveUser(sysUser,roleId,null,null);
                    //账号更新积分
                    integralLogService.addIntegralLog(sysUser.getId(), BizConstants.JF_REGISTER,"","新用户注册获得积分：");
                } else {
                    //用户存在，更新头像等信息
//                    sysUser.setAvatar(jsons.get("avatarUrl").toString());
//                    sysUser.setNickname(jsons.get("nickName").toString());
//                    sysUser.setSex((Integer) jsons.get("gender"));
//                    sysBaseApi.updateUserInfo(sysUser);
                }
            }else{
                if (sysUser == null) {
                    return result.error500("用户不存在");
                }
            }
            //如果已存在，再强退
            String lastToken=sysUser.getLoginToken();
            if (oConvertUtils.isNotEmpty(lastToken)){
                redisUtil.del(CommonConstant.PREFIX_USER_TOKEN + lastToken);
                //清空用户登录Shiro权限缓存
                redisUtil.del(CommonConstant.PREFIX_USER_SHIRO_CACHE + sysUser.getId());
                //清空用户的缓存信息（包括部门信息），例如sys:cache:user::<username>
                redisUtil.del(String.format("%s::%s", CacheConstant.SYS_USERS_CACHE, sysUser.getUsername()));
            }
            JSONObject obj = new JSONObject();
            //用户登录信息
            obj.put("userInfo", sysUser);
            // 生成token
            String token = JwtUtil.sign(sysUser.getUsername(), sysUser.getPassword());
            // 设置超时时间
            redisUtil.set(CommonConstant.PREFIX_USER_TOKEN + token, token);
            redisUtil.expire(CommonConstant.PREFIX_USER_TOKEN + token, JwtUtil.EXPIRE_TIME * 2 / 1000);

            sysUser.setLoginTime(new Date());
            sysUser.setLoginToken(token);
            sysBaseApi.updateUserInfo(sysUser);

            //token 信息
            obj.put("token", token);
            result.setResult(obj);
            result.setSuccess(true);
            result.setCode(200);
            result.setMessage("登录成功");
            baseCommonService.addLog("用户名: " + sysUser.getNickname() + ",登录成功[移动端]！", CommonConstant.LOG_TYPE_1, null);
        } catch (Exception e) {
			log.error("操作异常", e);
			System.err.println(e.getMessage());
        }
        return result;
    }


    /**
     * 小程序授权登录接口
     *
     * @param jsonObject
     * @return
     */
    @ApiOperation("小程序授权登录接口")
    @PostMapping("/mWxLogin")
    public Result<JSONObject> mWxLogin(@RequestBody JSONObject jsonObject) {
        Result<JSONObject> result = new Result<JSONObject>();
        try {
            String role = jsonObject.getString("role");//角色编码
            String code = jsonObject.getString("code");
            String encryptedData = jsonObject.getString("encryptedData");
            String iv = jsonObject.getString("iv");
            String referrer = jsonObject.getString("referrer");
            String inviteCode = jsonObject.getString("inviteCode");
            String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + WxUtil.getAppId() + "&secret=" + WxUtil.getAppSecret() + "&js_code=" + code + "&grant_type=authorization_code";
            JSONObject json = WxUtil.doGetJson(url); //发起请求拿到key和openid
            System.out.println("返回过来的json数据:" + json.toString());
            String  sessionkey = json.get("session_key").toString(); //会话秘钥
            String openid = json.get("openid").toString(); //用户唯一标识
            //根据openid判断用户是否存在
            LoginUser sysUser = sysBaseApi.getUserByThirdId(openid);
//            result = (Result<JSONObject>) sysBaseApi.checkUserIsEffective(sysUser);
            if (!oConvertUtils.isEmpty(encryptedData)) {
//                String decrypts = WxUtil.decrypt(encryptedData, sessionkey, iv, "utf-8");//解密
//                JSONObject jsons = JSONObject.parseObject(decrypts);
                if (sysUser == null) {
                    //注册新用户
//                    if (oConvertUtils.isEmpty(role)){
//                        return Result.error("请选择角色");
//                    }
                    sysUser = new LoginUser();
                    sysUser.setId(RandomUtil.randomNumbers(19));
                    sysUser.setThirdId(openid);
                    String avatar=jsonObject.get("avatarUrl").toString();
                    if (oConvertUtils.isNotEmpty(avatar)){
                        sysUser.setAvatar(avatar);
                    }else{
                    }
                    sysUser.setUsername(openid);
                    String nickname=jsonObject.get("nickName").toString();
                    if (oConvertUtils.isNotEmpty(nickname)){
                        sysUser.setNickname(nickname);
                        sysUser.setRealname(sysUser.getNickname());
                    }else{
//                        sysUser.setNickname(RandomName.randomName(true,3));
                        sysUser.setNickname("用户"+sysUser.getId().substring(7,11));
                        sysUser.setRealname(sysUser.getNickname());
                    }
                    sysUser.setStatus(1);
                    sysUser.setDelFlag(CommonConstant.DEL_FLAG_0);
                    sysUser.setUserIdentity(3);//移动端
                    String salt = oConvertUtils.randomGen(8);
                    sysUser.setSalt(salt);
                    String password = "qkl123456";//默认密码
                    String passwordEncode = PasswordUtil.encrypt(sysUser.getUsername(), password, salt);
                    sysUser.setPassword(passwordEncode);
                    //生成用户编码
                    sysUser.setUserCode(OrderUtils.getLocalTrmSeqNum());
                    //生成inviteCode
                    String mId = null;
                    Boolean haveId= true;//默认有重复的推荐码
                    while (haveId) {
                        mId=RandomUtil.randomNumbers(6);
                        //数据库查找
                        LoginUser tt = sysBaseApi.getUserByInviteCode(mId);
                        if (tt == null) {//没有查到数据，可以使用
                            haveId = false;
                        }
                    }
                    sysUser.setInviteCode(mId);
                    //推荐用户-手动
                    if (oConvertUtils.isNotEmpty(inviteCode)){
                        LoginUser uu=sysBaseApi.getUserByInviteCode(inviteCode);
                        sysUser.setReferrer(uu.getId());
                        referrerLogService.addReferrerLog(role,sysUser.getId(),uu.getId());
                    }else{
                        //推荐用户-自动
                        if (oConvertUtils.isNotEmpty(referrer)){
                            sysUser.setReferrer(referrer);
                            referrerLogService.addReferrerLog(role,sysUser.getId(),referrer);
                        }
                    }
                    //查询角色id
                    String roleId=sysBaseApi.getRoleIdByCode("member");//默认求职者
                    //当前角色
                    sysBaseApi.saveUser(sysUser,roleId,null,null);
                    //账号更新积分
                    integralLogService.addIntegralLog(sysUser.getId(), BizConstants.JF_REGISTER,"","新用户注册获得积分：");
                } else {
                    //用户存在，更新头像等信息
//                    sysUser.setAvatar(jsons.get("avatarUrl").toString());
//                    sysUser.setNickname(jsons.get("nickName").toString());
//                    sysUser.setSex((Integer) jsons.get("gender"));
//                    sysBaseApi.updateUserInfo(sysUser);
                }
            }else{
                if (sysUser == null) {
                    return result.error500("用户不存在");
                }
            }
            //如果已存在，再强退
            String lastToken=sysUser.getLoginToken();
            if (oConvertUtils.isNotEmpty(lastToken)){
                redisUtil.del(CommonConstant.PREFIX_USER_TOKEN + lastToken);
                //清空用户登录Shiro权限缓存
                redisUtil.del(CommonConstant.PREFIX_USER_SHIRO_CACHE + sysUser.getId());
                //清空用户的缓存信息（包括部门信息），例如sys:cache:user::<username>
                redisUtil.del(String.format("%s::%s", CacheConstant.SYS_USERS_CACHE, sysUser.getUsername()));
            }
            JSONObject obj = new JSONObject();
            //用户登录信息
            obj.put("userInfo", sysUser);
            // 生成token
            String token = JwtUtil.sign(sysUser.getUsername(), sysUser.getPassword());
            // 设置超时时间
            redisUtil.set(CommonConstant.PREFIX_USER_TOKEN + token, token);
            redisUtil.expire(CommonConstant.PREFIX_USER_TOKEN + token, JwtUtil.EXPIRE_TIME * 2 / 1000);

            sysUser.setLoginTime(new Date());
            sysUser.setLoginToken(token);
            sysBaseApi.updateUserInfo(sysUser);

            //token 信息
            obj.put("token", token);
            result.setResult(obj);
            result.setSuccess(true);
            result.setCode(200);
            result.setMessage("登录成功");
            baseCommonService.addLog("用户名: " + sysUser.getNickname() + ",登录成功[移动端]！", CommonConstant.LOG_TYPE_1, null);
        } catch (Exception e) {
			log.error("操作异常", e);
			System.err.println(e.getMessage());
        }
        return result;
    }



    @ResponseBody
    @RequestMapping(value = "/getOpenId", method = {RequestMethod.GET})
    @ApiOperation(value = "微信小程序获取OpenId", notes = "小程序获取OpenId")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "code", dataType = "String", paramType = "query"),
    })
    public Result getOpenId(String code) {
        System.out.println(code);
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + WxUtil.getAppId() + "&secret=" + WxUtil.getAppSecret() + "&js_code=" + code + "&grant_type=authorization_code";
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        try {
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String responseContent = EntityUtils.toString(entity, "UTF-8");
            System.out.println(responseContent + "响应报文");
            Map<String, String> res = new HashMap<>();
            Gson gson = new Gson();
            res = gson.fromJson(responseContent, res.getClass());
            String openid = res.get("openid");
            String session_key = res.get("session_key");
            System.out.println(res);
            System.out.println(openid + "获取到了openid");
            Map<String, Object> map = new HashMap<>();
            map.put("openId", openid);
            map.put("session_key", session_key);
            return Result.ok(map);
        } catch (Exception e) {
            System.out.println("请求错误");
            return Result.error(e.getMessage());
        }
    }

    @ResponseBody
    @RequestMapping(value = "/getDyOpenId", method = {RequestMethod.GET})
    @ApiOperation(value = "抖音小程序获取OpenId", notes = "抖音小程序获取OpenId")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "code", dataType = "String", paramType = "query"),
    })
    public Result getDyOpenId(String code) {
        try {
            System.out.println(code);
            String url = "https://developer.toutiao.com/api/apps/v2/jscode2session";
            Map<String,String> params=new HashMap<>();
            params.put("appid",DyUtil.getAppId());
            params.put("secret",DyUtil.getAppSecret());
            params.put("code",code);
            params.put("anonymous_code","");
            HttpHeader header=new HttpHeader();
            header.add("Content-Type","application/json");
            String json=new Gson().toJson(params);
            String result=HttpUtil.post(url,json);
            System.out.println(result + "响应报文");
            cn.hutool.json.JSONObject jsonObject = JSONUtil.parseObj(result);
            String data = jsonObject.getStr("data");
            Map<String, String> res = new HashMap<>();
            Gson gson = new Gson();
            res = gson.fromJson(data, res.getClass());
            System.out.println("res=="+res);
            String openid = res.get("openid");
            System.out.println("openid=="+openid);
            String unionid = res.get("unionid");
            System.out.println("unionid=="+unionid);
            String session_key = res.get("session_key");
            System.out.println("session_key=="+session_key);
            System.out.println(openid + "获取到了openid");
            Map<String, Object> map = new HashMap<>();
            map.put("openId", openid);
            map.put("session_key", session_key);
            return Result.ok(map);
        } catch (Exception e) {
            System.out.println("请求错误");
            return Result.error(e.getMessage());
        }
    }


    /**
     * 小程序绑定手机号
     *
     * @return
     */
    @PostMapping("bindPhoneForXcx")
    @ResponseBody
    public synchronized Result bindPhoneForWx(@RequestBody JSONObject jsonObject) {
        String openid = jsonObject.getString("openid");
        String encDataStr = jsonObject.getString("encDataStr");
        String keyStr = jsonObject.getString("keyStr");
        String ivStr = jsonObject.getString("ivStr");
        String nickName = jsonObject.getString("nickName");
        String avatar = jsonObject.getString("avatar");
        String role = jsonObject.getString("role");//角色编码
        String referrer = jsonObject.getString("referrer");
        String inviteCode = jsonObject.getString("inviteCode");
        Map<String, Object> map = new HashMap<>();
        //解析手机号
        WxPhoneInfo wxPhoneInfo = null;
        System.err.println("encDataStr=" + encDataStr + ",keyStr=" + keyStr + ",ivStr=" + ivStr);
        try {
            String result = AES.wxDecrypt(encDataStr, keyStr, ivStr);
            Gson gson = new Gson();
            wxPhoneInfo = gson.fromJson(result, WxPhoneInfo.class);
        } catch (Exception e) {
			log.error("操作异常", e);
			return Result.error("绑定手机号异常");
        }
        if (wxPhoneInfo == null) {
            return Result.error("绑定手机号异常");
        }
        //更新手机号
        LoginUser sysUser = new LoginUser();
        sysUser.setPhone(wxPhoneInfo.getPhoneNumber().trim());
        LoginUser u = sysBaseApi.getUserByPhone(sysUser.getPhone());
        if (u == null) {
            //注册新用户
//            if (oConvertUtils.isEmpty(role)){
//                return Result.error("请选择角色");
//            }
            sysUser.setId(RandomUtil.randomNumbers(19));
            sysUser.setThirdId(openid);
            if (oConvertUtils.isNotEmpty(avatar)){
                sysUser.setAvatar(avatar);
            }else{
//                sysUser.setAvatar("https://xxxx.oss-cn-shanghai.aliyuncs.com/common/head.png");//默认头像
            }
            sysUser.setUsername(openid);
            String nickname=jsonObject.get("nickName").toString();
            if (oConvertUtils.isNotEmpty(nickname)){
                sysUser.setNickname(nickname);
                sysUser.setRealname(sysUser.getNickname());
            }else{
//                        sysUser.setNickname(RandomName.randomName(true,3));
                sysUser.setNickname("用户"+sysUser.getPhone().substring(7));
                sysUser.setRealname(sysUser.getNickname());
            }
            sysUser.setStatus(1);
            sysUser.setDelFlag(CommonConstant.DEL_FLAG_0);
            sysUser.setUserIdentity(3);//移动端
            String salt = oConvertUtils.randomGen(8);
            sysUser.setSalt(salt);
            String password = "qkl123456";//默认密码
            String passwordEncode = PasswordUtil.encrypt(sysUser.getUsername(), password, salt);
            sysUser.setPassword(passwordEncode);
            //生成用户编码
            sysUser.setUserCode(OrderUtils.getLocalTrmSeqNum());
            //生成inviteCode
            String mId = null;
            Boolean haveId= true;//默认有重复的推荐码
            while (haveId) {
                mId=RandomUtil.randomNumbers(6);
                //数据库查找
                LoginUser tt = sysBaseApi.getUserByInviteCode(mId);
                if (tt == null) {//没有查到数据，可以使用
                    haveId = false;
                }
            }
            sysUser.setInviteCode(mId);

            //推荐用户-手动
            if (oConvertUtils.isNotEmpty(inviteCode)){
               LoginUser uu=sysBaseApi.getUserByInviteCode(inviteCode);
                sysUser.setReferrer(uu.getId());
                referrerLogService.addReferrerLog(role,sysUser.getId(),uu.getId());
            }else{
                //推荐用户-自动
                if (oConvertUtils.isNotEmpty(referrer)){
                    sysUser.setReferrer(referrer);
                    referrerLogService.addReferrerLog(role,sysUser.getId(),referrer);
                }
            }
            //查询角色id
            String roleId=sysBaseApi.getRoleIdByCode("member");//默认求职者
            //当前角色
            sysBaseApi.saveUser(sysUser,roleId,null,null);
            //账号更新积分
            integralLogService.addIntegralLog(sysUser.getId(), BizConstants.JF_REGISTER,"","新用户注册获得积分：");
        } else {
            //用户存在，更新头像等信息
//                    sysUser.setAvatar(jsons.get("avatarUrl").toString());
//                    sysUser.setNickname(jsons.get("nickName").toString());
//                    sysUser.setSex((Integer) jsons.get("gender"));
//                    sysBaseApi.updateUserInfo(sysUser);
            if (oConvertUtils.isNotEmpty(u.getThirdId())){
                return Result.error(sysUser.getPhone()+"已绑定其他微信账号");
            }else{
                //合并账号
                u.setThirdId(openid);
                sysBaseApi.updateUserInfo(u);
            }
            sysUser=u;
        }
        //如果已存在，再强退
        String lastToken=sysUser.getLoginToken();
        if (oConvertUtils.isNotEmpty(lastToken)){
            redisUtil.del(CommonConstant.PREFIX_USER_TOKEN + lastToken);
            //清空用户登录Shiro权限缓存
            redisUtil.del(CommonConstant.PREFIX_USER_SHIRO_CACHE + sysUser.getId());
            //清空用户的缓存信息（包括部门信息），例如sys:cache:user::<username>
            redisUtil.del(String.format("%s::%s", CacheConstant.SYS_USERS_CACHE, sysUser.getUsername()));
        }
        JSONObject obj = new JSONObject();
        //用户登录信息
        obj.put("userInfo", sysUser);
        // 生成token
        String token = JwtUtil.sign(sysUser.getUsername(), sysUser.getPassword());
        // 设置超时时间
        redisUtil.set(CommonConstant.PREFIX_USER_TOKEN + token, token);
        redisUtil.expire(CommonConstant.PREFIX_USER_TOKEN + token, JwtUtil.EXPIRE_TIME * 2 / 1000);

        sysUser.setLoginTime(new Date());
        sysUser.setLoginToken(token);
        sysBaseApi.updateUserInfo(sysUser);

        //token 信息
        obj.put("token", token);
        baseCommonService.addLog("用户名: " + sysUser.getNickname() + ",登录成功[移动端]！", CommonConstant.LOG_TYPE_1, null);
        return Result.ok(obj);
    }

    /**
     * 小程序授权登录方法
     * @return
     */
    @PostMapping(value = "xcxLogin")
    @ResponseBody
    public synchronized Result xcxUserLogin(@RequestBody JSONObject jsonObject) {
        String openid = jsonObject.getString("openid");
        String nickName = jsonObject.getString("nickName");
        String avatar = jsonObject.getString("avatar");
        System.out.println("openid=" + openid);
        LoginUser sysUser= sysBaseApi.getUserByThirdId(openid);
        if (sysUser == null) {
            return Result.ok("账号不存在");
        } else if (sysUser.getPhone() == null) {
            return Result.ok("未绑定手机号");
        } else {
            //如果已存在，再强退
            String lastToken=sysUser.getLoginToken();
            if (oConvertUtils.isNotEmpty(lastToken)){
                redisUtil.del(CommonConstant.PREFIX_USER_TOKEN + lastToken);
                //清空用户登录Shiro权限缓存
                redisUtil.del(CommonConstant.PREFIX_USER_SHIRO_CACHE + sysUser.getId());
                //清空用户的缓存信息（包括部门信息），例如sys:cache:user::<username>
                redisUtil.del(String.format("%s::%s", CacheConstant.SYS_USERS_CACHE, sysUser.getUsername()));
            }
            JSONObject obj = new JSONObject();
            //用户登录信息
            obj.put("userInfo", sysUser);
            // 生成token
            String token = JwtUtil.sign(sysUser.getUsername(), sysUser.getPassword());
            // 设置超时时间
            redisUtil.set(CommonConstant.PREFIX_USER_TOKEN + token, token);
            redisUtil.expire(CommonConstant.PREFIX_USER_TOKEN + token, JwtUtil.EXPIRE_TIME * 2 / 1000);

            sysUser.setLoginTime(new Date());
            sysUser.setLoginToken(token);
            sysBaseApi.updateUserInfo(sysUser);
            //token 信息
            obj.put("token", token);
            baseCommonService.addLog("用户名: " + sysUser.getNickname() + ",登录成功[移动端]！", CommonConstant.LOG_TYPE_1, null);
            return Result.ok(obj);
        }
    }

    /**
     * 小程序绑定手机号
     *
     * @return
     */
    @PostMapping("phoneLoginForWx")
    @ResponseBody
    public synchronized Result phoneLoginForWx(@RequestBody JSONObject jsonObject) {
        String encDataStr = jsonObject.getString("encDataStr");
        String code = jsonObject.getString("code");
        String ivStr = jsonObject.getString("ivStr");
        String role = jsonObject.getString("role");//角色编码
        String referrer = jsonObject.getString("referrer");
        String inviteCode = jsonObject.getString("inviteCode");
        //解析手机号
        WxPhoneInfo wxPhoneInfo = null;
        System.err.println("encDataStr=" + encDataStr + ",code=" + code + ",ivStr=" + ivStr);
        try {
            String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + WxUtil.getAppId() + "&secret=" + WxUtil.getAppSecret() + "&js_code=" + code + "&grant_type=authorization_code";
            HttpGet httpGet = new HttpGet(url);
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String responseContent = EntityUtils.toString(entity, "UTF-8");
            System.out.println(responseContent + "响应报文");
            Map<String, String> res = new HashMap<>();
            Gson gson = new Gson();
            res = gson.fromJson(responseContent, res.getClass());
            String openid = res.get("openid");
            String session_key = res.get("session_key");
            System.out.println(res);
            System.out.println(openid + "获取到了openid");
            String result = AES.wxDecrypt(encDataStr, session_key, ivStr);
            wxPhoneInfo = gson.fromJson(result, WxPhoneInfo.class);
        } catch (Exception e) {
			log.error("操作异常", e);
			return Result.error("登录异常");
        }
        if (wxPhoneInfo == null) {
            return Result.error("登录异常");
        }
        //更新手机号
        LoginUser sysUser = new LoginUser();
        sysUser.setPhone(wxPhoneInfo.getPhoneNumber().trim());
        LoginUser u = sysBaseApi.getUserByPhone(sysUser.getPhone());
        if (u == null) {
            //注册新用户
//            if (oConvertUtils.isEmpty(role)){
//                return Result.error("请选择角色");
//            }
            sysUser.setId(RandomUtil.randomNumbers(19));
            sysUser.setUsername(sysUser.getPhone());
//                        sysUser.setNickname(RandomName.randomName(true,3));
            sysUser.setNickname("用户"+sysUser.getPhone().substring(7));
            sysUser.setRealname(sysUser.getNickname());
            sysUser.setStatus(1);
            sysUser.setDelFlag(CommonConstant.DEL_FLAG_0);
            sysUser.setUserIdentity(3);//移动端
            String salt = oConvertUtils.randomGen(8);
            sysUser.setSalt(salt);
            String password = "qkl123456";//默认密码
            String passwordEncode = PasswordUtil.encrypt(sysUser.getUsername(), password, salt);
            sysUser.setPassword(passwordEncode);
            //生成用户编码
            sysUser.setUserCode(OrderUtils.getLocalTrmSeqNum());
            //生成inviteCode
            String mId = null;
            Boolean haveId= true;//默认有重复的推荐码
            while (haveId) {
                mId=RandomUtil.randomNumbers(6);
                //数据库查找
                LoginUser tt = sysBaseApi.getUserByInviteCode(mId);
                if (tt == null) {//没有查到数据，可以使用
                    haveId = false;
                }
            }
            sysUser.setInviteCode(mId);

            //推荐用户-手动
            if (oConvertUtils.isNotEmpty(inviteCode)){
                LoginUser uu=sysBaseApi.getUserByInviteCode(inviteCode);
                sysUser.setReferrer(uu.getId());
                referrerLogService.addReferrerLog(role,sysUser.getId(),uu.getId());
            }else{
                //推荐用户-自动
                if (oConvertUtils.isNotEmpty(referrer)){
                    sysUser.setReferrer(referrer);
                    referrerLogService.addReferrerLog(role,sysUser.getId(),referrer);
                }
            }
            //查询角色id
            String roleId=sysBaseApi.getRoleIdByCode("member");//默认求职者角色
            //当前角色
            sysBaseApi.saveUser(sysUser,roleId,null,null);
            //账号更新积分
            integralLogService.addIntegralLog(sysUser.getId(), BizConstants.JF_REGISTER,"","新用户注册获得积分：");
        } else {
            //用户存在,直接登录
            sysUser=u;
        }
        //如果已存在，再强退
        String lastToken=sysUser.getLoginToken();
        if (oConvertUtils.isNotEmpty(lastToken)){
            redisUtil.del(CommonConstant.PREFIX_USER_TOKEN + lastToken);
            //清空用户登录Shiro权限缓存
            redisUtil.del(CommonConstant.PREFIX_USER_SHIRO_CACHE + sysUser.getId());
            //清空用户的缓存信息（包括部门信息），例如sys:cache:user::<username>
            redisUtil.del(String.format("%s::%s", CacheConstant.SYS_USERS_CACHE, sysUser.getUsername()));
        }
        JSONObject obj = new JSONObject();
        //用户登录信息
        obj.put("userInfo", sysUser);
        // 生成token
        String token = JwtUtil.sign(sysUser.getUsername(), sysUser.getPassword());
        // 设置超时时间
        redisUtil.set(CommonConstant.PREFIX_USER_TOKEN + token, token);
        redisUtil.expire(CommonConstant.PREFIX_USER_TOKEN + token, JwtUtil.EXPIRE_TIME * 2 / 1000);

        sysUser.setLoginTime(new Date());
        sysUser.setLoginToken(token);
        sysBaseApi.updateUserInfo(sysUser);

        //token 信息
        obj.put("token", token);
        baseCommonService.addLog("用户名: " + sysUser.getNickname() + ",登录成功[移动端]！", CommonConstant.LOG_TYPE_1, null);
        return Result.ok(obj);
    }


    /**
     * 小程序授权手机号登录接口
     *
     * @param jsonObject
     * @return
     */
    @ApiOperation("小程序授权手机号登录接口")
    @PostMapping("/mPhoneAuthLogin")
    public Result<JSONObject> mPhoneAuthLogin(@RequestBody JSONObject jsonObject) {
        Result<JSONObject> result = new Result<JSONObject>();
        try {
            String code = jsonObject.getString("code");
            String encDataStr = jsonObject.getString("encryptedData");
            String ivStr = jsonObject.getString("iv");
            String referrer = jsonObject.getString("referrer");
            String inviteCode = jsonObject.getString("inviteCode");
            String role = jsonObject.getString("role");//角色编码
            String city=jsonObject.getString("city");
            String cityCode=jsonObject.getString("city");
            String pCity=jsonObject.getString("pcity");
            String pCityCode=jsonObject.getString("pcityCode");
            String address=jsonObject.getString("address");
            String latitude=jsonObject.getString("latitude");
            String longitude=jsonObject.getString("longitude");
            //解析手机号
            WxPhoneInfo wxPhoneInfo = null;
            String openid=null;
            System.err.println("encDataStr=" + encDataStr + ",code=" + code + ",ivStr=" + ivStr);
            try {
                String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + WxUtil.getAppId() + "&secret=" + WxUtil.getAppSecret() + "&js_code=" + code + "&grant_type=authorization_code";
                HttpGet httpGet = new HttpGet(url);
                CloseableHttpClient httpClient = HttpClientBuilder.create().build();
                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity entity = response.getEntity();
                String responseContent = EntityUtils.toString(entity, "UTF-8");
                System.out.println(responseContent + "响应报文");
                Map<String, String> res = new HashMap<>();
                Gson gson = new Gson();
                res = gson.fromJson(responseContent, res.getClass());
                openid = res.get("openid");
                String session_key = res.get("session_key");
                System.out.println(res);
                System.out.println(openid + "获取到了openid");
                String phoneRes = AES.wxDecrypt(encDataStr, session_key, ivStr);
                wxPhoneInfo = gson.fromJson(phoneRes, WxPhoneInfo.class);
            } catch (Exception e) {
			log.error("操作异常", e);
			return Result.error("登录异常");
            }
            if (wxPhoneInfo == null) {
                return Result.error("登录异常");
            }
            //根据openid判断用户是否存在
            LoginUser sysUser = sysBaseApi.getUserByThirdId(openid);
            //如果openid不存在，判断手机号
            if (sysUser==null){
                sysUser=sysBaseApi.getUserByPhone(wxPhoneInfo.getPhoneNumber());
                if (sysUser !=null){
                    sysUser.setThirdId(openid);
                }
            }
            if (!oConvertUtils.isEmpty(encDataStr)) {
                if (sysUser == null) {
                    //注册新用户
                    sysUser = new LoginUser();
                    sysUser.setId(RandomUtil.randomNumbers(19));
                    sysUser.setThirdId(openid);
                    sysUser.setUsername(wxPhoneInfo.getPhoneNumber());
                    sysUser.setPhone(wxPhoneInfo.getPhoneNumber());
                    sysUser.setNickname("用户"+sysUser.getPhone().substring(7,11));
                    sysUser.setRealname(sysUser.getNickname());
                    sysUser.setAvatar("");
                    sysUser.setStatus(1);
                    sysUser.setDelFlag(CommonConstant.DEL_FLAG_0);
                    sysUser.setUserIdentity(3);//移动端
                    String salt = oConvertUtils.randomGen(8);
                    sysUser.setSalt(salt);
                    String password = "qkl123456";//默认密码
                    String passwordEncode = PasswordUtil.encrypt(sysUser.getUsername(), password, salt);
                    sysUser.setPassword(passwordEncode);
                    //生成用户编码
                    sysUser.setUserCode(OrderUtils.getLocalTrmSeqNum());

                    //生成inviteCode
                    String mId = null;
                    Boolean haveId= true;//默认有重复的推荐码
                    while (haveId) {
                        mId=RandomUtil.randomNumbers(6);
                        //数据库查找
                        LoginUser tt = sysBaseApi.getUserByInviteCode(mId);
                        if (tt == null) {//没有查到数据，可以使用
                            haveId = false;
                        }
                    }
                    sysUser.setInviteCode(mId);
                    //推荐用户-手动
                    if (oConvertUtils.isNotEmpty(inviteCode)){
                        LoginUser uu=sysBaseApi.getUserByInviteCode(inviteCode);
                        sysUser.setReferrer(uu.getId());
                        referrerLogService.addReferrerLog(role,sysUser.getId(),uu.getId());
                    }else{
                        //推荐用户-自动
                        if (oConvertUtils.isNotEmpty(referrer)){
                            sysUser.setReferrer(referrer);
                            referrerLogService.addReferrerLog(role,sysUser.getId(),referrer);
                        }
                    }
                    //查询角色id
                    String roleId=sysBaseApi.getRoleIdByCode("member");//首次注册默认求职者
                    //当前角色,设置角色
                    sysBaseApi.saveUser(sysUser,roleId,null,null);
                    //账号更新积分
                    integralLogService.addIntegralLog(sysUser.getId(), BizConstants.JF_REGISTER,"","新用户注册获得积分：");

                    //判断是否存在简历
                    if (oConvertUtils.isNotEmpty(city)){
                        UserLocation location=new UserLocation();
                        location.setAddress(address);
                        location.setLatitude(latitude);
                        location.setLongitude(longitude);
                        location.setCity(city);
                        location.setPCity(pCity);
                        location.setCityCode(cityCode);
                        location.setPCityCode(pCityCode);
                        resumeService.createDefaultResume(sysUser,location);
                    }
                } else {
                    //用户存在，更新头像等信息
                    result = (Result<JSONObject>) sysBaseApi.checkUserIsEffective(sysUser);
                    if (!result.isSuccess()){
                        return result;
                    }
                    sysBaseApi.updateUserInfo(sysUser);
                }
            }else{
                if (sysUser == null) {
                    return result.error500("用户不存在");
                }
            }

            //如果已存在，再强退
            String lastToken=sysUser.getLoginToken();
            if (oConvertUtils.isNotEmpty(lastToken)){
                redisUtil.del(CommonConstant.PREFIX_USER_TOKEN + lastToken);
                //清空用户登录Shiro权限缓存
                redisUtil.del(CommonConstant.PREFIX_USER_SHIRO_CACHE + sysUser.getId());
                //清空用户的缓存信息（包括部门信息），例如sys:cache:user::<username>
                redisUtil.del(String.format("%s::%s", CacheConstant.SYS_USERS_CACHE, sysUser.getUsername()));
            }

            JSONObject obj = new JSONObject();
            //用户登录信息
            obj.put("userInfo", sysUser);
            // 生成token
            String token = JwtUtil.sign(sysUser.getUsername(), sysUser.getPassword());
            // 设置超时时间
            redisUtil.set(CommonConstant.PREFIX_USER_TOKEN + token, token);
            redisUtil.expire(CommonConstant.PREFIX_USER_TOKEN + token, JwtUtil.EXPIRE_TIME * 2000 / 1000);

            //token 信息
            obj.put("token", token);
            result.setResult(obj);
            result.setSuccess(true);
            result.setCode(200);
            result.setMessage("登录成功");
            baseCommonService.addLog("用户名: " + sysUser.getNickname() + ",登录成功[移动端]！", CommonConstant.LOG_TYPE_1, null);
        } catch (Exception e) {
			log.error("操作异常", e);
			System.err.println(e.getMessage());
        }
        return result;
    }


    /**
     * 小程序静默登录
     *
     * @param jsonObject
     * @return
     */
    @ApiOperation("小程序静默登录")
    @PostMapping("/mLoginByCode")
    public Result<JSONObject> mLoginByCode(@RequestBody JSONObject jsonObject) {
        Result<JSONObject> result = new Result<JSONObject>();
        try {
            String code = jsonObject.getString("code");
            String type = jsonObject.getString("type");
            LoginUser sysUser;
            String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + WxUtil.getAppId() + "&secret=" + WxUtil.getAppSecret() + "&js_code=" + code + "&grant_type=authorization_code";
            JSONObject json = AuthUtil.doGetJson(url); //发起请求拿到key和openid
            System.out.println("返回过来的json数据:" + json.toString());
            String openid = json.get("openid").toString(); //用户唯一标识
            //根据openid判断用户是否存在
            sysUser = sysBaseApi.getUserByThirdId(openid);
            result = (Result<JSONObject>) sysBaseApi.checkUserIsEffective(sysUser);
            if (!result.isSuccess()){
                return result;
            }
            if (oConvertUtils.isEmpty(sysUser)) {
                return result.error500("账号不存在");
            }
            JSONObject obj = new JSONObject();
            //更新用户信息
            if (sysUser!=null){
                //查看当前角色
                List<Map<String,String>> roles=sysBaseApi.getRoleMapByUserName(sysUser.getUsername());
                if (roles.size()==1){
                    sysUser.setMemberRoleName(roles.get(0).get("name"));
                    sysUser.setMemberRole(roles.get(0).get("code"));
                }
                //是否实名认证状态
                UmsRealnameAuth realnameAuth=realnameAuthService.getRealNameAuth(sysUser.getId());
                if (realnameAuth!=null){
                    sysUser.setRealNameAuth(realnameAuth.getAuthStatus());
                }else{
                    sysUser.setRealNameAuth(-1);
                }
                if ("company".equals(sysUser.getMemberRole())){
                    //企业认证状态
                    JobCompany companyAuth=companyService.getCompanyAuth(sysUser.getId());
                    if (companyAuth!=null){
                        sysUser.setCompanyAuth(companyAuth.getAuthStatus());
                        sysUser.setCompany(companyAuth.getRealName());
                    }else{
                        sysUser.setCompanyAuth(-1);
                    }
                }else if ("member".equals(sysUser.getMemberRole())) {
//					WmsResume resume = wmsResumeService.getOne(new QueryWrapper<>(new WmsResume().setMemberId(sysUser.getId())));
//					if (resume != null) {
//						sysUser.setPercentage(resume.getPercentage());
//					}else{
//						sysUser.setPercentage(0);
//					}
                }
            }
            //用户登录信息
            obj.put("userInfo", sysUser);
            // 生成token
            String token = JwtUtil.sign(sysUser.getUsername(), sysUser.getPassword());
            // 设置超时时间
            redisUtil.set(CommonConstant.PREFIX_USER_TOKEN + token, token);
            redisUtil.expire(CommonConstant.PREFIX_USER_TOKEN + token, JwtUtil.EXPIRE_TIME * 2000 / 1000);

            //token 信息
            obj.put("token", token);
            result.setResult(obj);
            result.setSuccess(true);
            result.setCode(200);
            result.setMessage("登录成功");
            baseCommonService.addLog("用户名: " + sysUser.getNickname() + ",登录成功[移动端]！", CommonConstant.LOG_TYPE_1, null);
        } catch (Exception e) {
			log.error("操作异常", e);
			System.err.println(e.getMessage());
        }
        return result;
    }



}
