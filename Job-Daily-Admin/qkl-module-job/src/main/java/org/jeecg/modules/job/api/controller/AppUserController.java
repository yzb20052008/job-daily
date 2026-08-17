package org.jeecg.modules.job.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.base.service.BaseCommonService;
import org.jeecg.modules.job.api.model.UserLocation;
import org.jeecg.modules.job.api.model.UserModel;
import org.jeecg.modules.job.job.entity.JobCompany;
import org.jeecg.modules.job.job.entity.JobResume;
import org.jeecg.modules.job.job.entity.JobResumeIntention;
import org.jeecg.modules.job.job.service.IJobCompanyService;
import org.jeecg.modules.job.job.service.IJobResumeCertService;
import org.jeecg.modules.job.job.service.IJobResumeIntentionService;
import org.jeecg.modules.job.job.service.IJobResumeService;
import org.jeecg.modules.job.ums.entity.UmsMemberRole;
import org.jeecg.modules.job.ums.entity.UmsParamLimit;
import org.jeecg.modules.job.ums.entity.UmsRealnameAuth;
import org.jeecg.modules.job.ums.entity.UmsUserVip;
import org.jeecg.modules.job.ums.service.IUmsMemberRoleService;
import org.jeecg.modules.job.ums.service.IUmsParamLimitService;
import org.jeecg.modules.job.ums.service.IUmsRealnameAuthService;
import org.jeecg.modules.job.ums.service.IUmsUserVipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 */
@RestController
@RequestMapping("/api/user")
@Api(tags="移动端用户信息")
@Slf4j
public class AppUserController {
	@Autowired
	private ISysBaseAPI sysBaseApi;
	@Resource
	private BaseCommonService baseCommonService;
	@Resource
	private IUmsMemberRoleService memberRoleService;
	@Autowired
	private RedisUtil redisUtil;
	@Resource
	private IUmsUserVipService userVipService;
	@Resource
	private IUmsParamLimitService paramLimitService;
	@Resource
	private IUmsRealnameAuthService realnameAuthService;
	@Resource
	private IJobCompanyService companyService;
	@Resource
	private IJobResumeService resumeService;
	@Resource
	private IJobResumeIntentionService resumeIntentionService;


	/**
	 * 是否为调试模式
	 */
	@Value(value="${jeecg.devTest}")
	private boolean devTest;

	/**
	 * 根据id/phone/uuid/username等获取
	 *
	 * @return
	 */
	@ApiOperation("根据id等查询用户信息")
	@GetMapping("/getUserInfo")
	public Result<?> getUserInfo(HttpServletRequest request, UserModel userModel) {
		try {
			LoginUser sysUser=null;
			String username = JwtUtil.getUserNameByToken(request);
			if(StringUtils.isNotBlank(userModel.getId())){
				sysUser = sysBaseApi.getUserById(userModel.getId());
			}else if(StringUtils.isNotBlank(userModel.getPhone())){
				sysUser = sysBaseApi.getUserByName(userModel.getPhone());
			}else if(StringUtils.isNotBlank(userModel.getUuid())){
				sysUser = sysBaseApi.getUserByName(userModel.getUuid());
			}else if(StringUtils.isNotBlank(userModel.getUsername())){
				sysUser = sysBaseApi.getUserByName(userModel.getUsername());
			}else{
				log.debug(" ------ 通过令牌获取部分用户信息，当前用户： " + username);
				// 根据用户名查询用户信息
				sysUser = sysBaseApi.getUserByName(username);
			}
			if (sysUser!=null){
				sysUser.setPassword(null);
				//查看当前角色
				List<Map<String,String>> roles=sysBaseApi.getRoleMapByUserName(username);
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
			return Result.ok(sysUser);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Result.error(500, "查询失败:" + e.getMessage());
		}
	}

	/**
	 * 根据token获取用户信息
	 * @return
	 */
	@ApiOperation("根据token查询用户信息")
	@GetMapping("/getUserInfoByToken")
	public Result<?> getUserInfoByToken(HttpServletRequest request) {
		try {
			LoginUser sysUser;
			String username = JwtUtil.getUserNameByToken(request);
			log.debug(" ------ 通过令牌获取部分用户信息，当前用户： " + username);
			// 根据用户名查询用户信息
			sysUser = sysBaseApi.getUserByName(username);
			sysUser.setPassword(null);
			//关联企业
			UmsMemberRole memberRole=memberRoleService.getMemberRole(sysUser.getId());
			if (oConvertUtils.isNotEmpty(memberRole)){
//				RmsCompany company=companyService.getById(memberRole.getCompanyId());
//				if (oConvertUtils.isNotEmpty(company)){
//					sysUser.setCompany(company.getName());
//					sysUser.setCompanyId(company.getId());
//					sysUser.setPostName(memberRole.getPostName());
//				}
			}
			//查看当前角色
			List<Map<String,String>> roles=sysBaseApi.getRoleMapByUserName(username);
			if (roles.size()==1){
				sysUser.setMemberRoleName(roles.get(0).get("name"));
				sysUser.setMemberRole(roles.get(0).get("code"));
			}
			return Result.ok(sysUser);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Result.error(500, "查询失败:" + e.getMessage());
		}
	}



	/**
	 * 修改用户信息
	 * @param jsonObject
	 * @return
	 */
	@ApiOperation("修改用户信息")
	@PostMapping(value = "/updateUser")
	public Result<LoginUser> appEdit(HttpServletRequest request, @RequestBody JSONObject jsonObject) {
		Result<LoginUser> result = new Result<LoginUser>();
		try {
			String username = JwtUtil.getUserNameByToken(request);
			LoginUser sysUser = sysBaseApi.getUserByName(username);
			baseCommonService.addLog("移动端编辑用户，id： " +jsonObject.getString("id") , CommonConstant.LOG_TYPE_2, 2);
			String realname=jsonObject.getString("realname");
			String avatar=jsonObject.getString("avatar");
			String sex=jsonObject.getString("sex");
			String phone=jsonObject.getString("phone");
			String email=jsonObject.getString("email");
			Date birthday=jsonObject.getDate("birthday");
			String nickname=jsonObject.getString("nickname");
			String province=jsonObject.getString("province");
			String city=jsonObject.getString("city");
			String area=jsonObject.getString("area");
			String sign=jsonObject.getString("sign");
			String postName=jsonObject.getString("postName");//公司担任职位
			LoginUser userPhone = sysBaseApi.getUserByName(phone);
			if(sysUser==null) {
				result.error500("未找到对应用户!");
			}else {
				if(userPhone!=null){
					String userPhonename = userPhone.getUsername();
					if(!userPhonename.equals(username)){
						result.error500("手机号已存在!");
						return result;
					}
				}
				if(StringUtils.isNotBlank(realname)){
					sysUser.setRealname(realname);
				}
				if(StringUtils.isNotBlank(nickname)){
					sysUser.setNickname(nickname);
				}
				if(StringUtils.isNotBlank(avatar)){
					sysUser.setAvatar(avatar);
					//判断是否存在简历
					JobResume resume=resumeService.getResumeInfo(sysUser.getId());
					if (resume!=null){
						resume.setAvatar(avatar);
						resumeService.updateById(resume);
					}
				}
				if(StringUtils.isNotBlank(sex)){
					sysUser.setSex(Integer.parseInt(sex));
				}
				if(StringUtils.isNotBlank(phone)){
					sysUser.setPhone(phone);
				}
				if(StringUtils.isNotBlank(email)){
					sysUser.setEmail(email);
				}
				if(null != birthday){
					sysUser.setBirthday(birthday);
				}
				sysBaseApi.updateUserInfo(sysUser);
				//更新职位名称
				if(StringUtils.isNotBlank(postName)){
					UmsMemberRole role=memberRoleService.getMemberRole(sysUser.getId());
					role.setPostName(postName);
					memberRoleService.updateById(role);
				}
				result.setCode(200);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.error500("保存失败!");
		}
		return result;
	}


	/**
	 * 绑定手机号
	 * @param jsonObject
	 * @return
	 */
	@ApiOperation("绑定手机号")
	@PostMapping(value = "/bindPhone")
	public Result<LoginUser> bindPhone(HttpServletRequest request, @RequestBody JSONObject jsonObject) {
		Result<LoginUser> result = new Result<LoginUser>();
		try {
			String phone = jsonObject.getString("phone");
			String smscode = jsonObject.getString("code");
			if(StringUtils.isEmpty(phone) || StringUtils.isEmpty(phone)){
				result.setMessage("手机号+验证码不能为空");
				return result;
			}
			Object code = redisUtil.get(phone);
			if(devTest){
				//此处可添加指定验证码校验
			}else{
				if (!smscode.equals(code)) {
					result.setMessage("手机验证码错误");
					return result;
				}
			}
			String username = JwtUtil.getUserNameByToken(request);
			LoginUser sysUser = sysBaseApi.getUserByName(username);
			LoginUser userPhone = sysBaseApi.getUserByName(phone);
			if(sysUser==null) {
				result.error500("未找到对应用户!");
			}else {
				if(userPhone!=null){
					String userPhonename = userPhone.getUsername();
					if(!userPhonename.equals(username)){
						result.error500("手机号已存在!");
						return result;
					}
				}
				sysUser.setUsername(phone);
				sysUser.setPhone(phone);
				sysBaseApi.updateUserInfo(sysUser);
				result.setSuccess(true);
				result.setCode(200);
				result.setMessage("绑定成功");
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.error500("操作失败!");
		}
		return result;
	}

	/**
	 * 角色切换
	 */
	@ApiOperation(value = "角色切换", notes = "角色切换")
	@PostMapping(value = "/switchRole")
	public Result<?> switchRole(@RequestBody JSONObject jsonObject) throws Exception {
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		Map<String,Object> map=new HashMap<>();
		String role=jsonObject.getString("role");
		map.put("result",true);
		map.put("role",role);
		try{
			//切换角色
			user.setMemberRole(role);
			String roleId=sysBaseApi.getRoleIdByCode(role);
			if (oConvertUtils.isEmpty(roleId)){
				return Result.error("参数错误");
			}
			sysBaseApi.updateUserInfo(user,roleId,null,null);
			return Result.ok(map);
		}catch (Exception e){
			return Result.error(e.getMessage());
		}
	}


	/**
	 * 查询用户拥有的菜单权限
	 *
	 * @return
	 */
	@RequestMapping(value = "/getUserPermissionByToken", method = RequestMethod.GET)
	public Result<?> getUserPermissionByToken() {
		Result<Object> result = new Result<Object>();
		try {
			//直接获取当前用户不适用前端token
			LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
			if (oConvertUtils.isEmpty(loginUser)) {
				return Result.error("请登录系统！");
			}
			return  sysBaseApi.getUserPermissionByToken(loginUser.getUsername());
		} catch (Exception e) {
			result.error500("查询失败:" + e.getMessage());
			log.error(e.getMessage(), e);
			return result;
		}
	}

	/**
	 * 查询移动端角色列表
	 *
	 * @return
	 */
	@RequestMapping(value = "/getAppRoles", method = RequestMethod.GET)
	public Result<?> getAppRoles(HttpServletRequest request) {
		try {
			List<Map<String,String>> roles= sysBaseApi.getRoleByPlatform("mobile");
			return Result.ok(roles);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Result.error("查询失败:" + e.getMessage());
		}
	}

	/**
	 * 添加公司招聘人员
	 */
	@ApiOperation(value = "添加公司招聘人员", notes = "添加公司招聘人员")
	@PostMapping(value = "/addCompanyUser")
	public Result<?> addCompanyUser(@RequestBody JSONObject jsonObject) throws Exception {
		String phone=jsonObject.getString("phone");
		String name=jsonObject.getString("realname");
		String postName=jsonObject.getString("postName");
		String companyId=jsonObject.getString("companyId");
		String smscode = jsonObject.getString("code");
		if (oConvertUtils.isEmpty(phone) || oConvertUtils.isEmpty(name) || oConvertUtils.isEmpty(postName) || oConvertUtils.isEmpty(companyId)|| oConvertUtils.isEmpty(smscode)){
			return Result.error("参数错误");
		}
		Object code = redisUtil.get(phone);
		if(devTest){
			//此处可添加指定验证码校验
		}else{
			if (!smscode.equals(code)) {
				return  Result.error("手机验证码错误");
			}
		}
		try{
			memberRoleService.addCompanyUser(phone,name,postName,companyId);
			return Result.ok();
		}catch (Exception e){
			return Result.error(e.getMessage());
		}
	}


	/**
	 * 移除公司招聘人员
	 */
	@ApiOperation(value = "移除公司招聘人员", notes = "移除公司招聘人员")
	@PostMapping(value = "/delCompanyUser")
	public Result<?> delCompanyUser(@RequestBody JSONObject jsonObject) throws Exception {
		String id=jsonObject.getString("id");
		if (oConvertUtils.isEmpty(id)){
			return Result.error("参数错误");
		}
		try{
			memberRoleService.removeById(id);
			return Result.ok();
		}catch (Exception e){
			return Result.error(e.getMessage());
		}
	}

	/**
	 * 查询企业招聘人员
	 */
	@RequestMapping(value = "/getCompanyUserList",method = RequestMethod.GET)
	@ApiOperation(value = "查询投递列表", notes = "查询投递列表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "page", value = "页码", dataType = "Integer", paramType = "query"),
			@ApiImplicitParam(name = "limit", value = "每页数量", dataType = "Integer", paramType = "query")
	})
	public Result<Object> getCompanyUserList(UmsMemberRole param,
										  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
										  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize
	) {
		IPage<Map<String, Object>> pageInfo= memberRoleService.getMemeberRoleList(new Page<>(pageNo,pageSize),param);
		return Result.OK(pageInfo);
	}


	/**
	 * 查询用户参数额度
	 *
	 * @return
	 */
	@RequestMapping(value = "/getParamLimit", method = RequestMethod.GET)
	public Result<?> getParamLimit() {
		try {
			LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
			UmsParamLimit limit= paramLimitService.getParamLimit(loginUser.getId());
			return Result.ok(limit);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Result.error("查询失败:" + e.getMessage());
		}
	}

	/**
	 * 注销账号
	 */
	@ApiOperation(value = "注销账号", notes = "注销账号")
	@PostMapping(value = "/cancelAccount")
	public Result<?> cancelAccount(@RequestBody JSONObject jsonObject) throws Exception {
		String phone = jsonObject.getString("phone");
		String smscode = jsonObject.getString("code");
		if(StringUtils.isEmpty(phone) || StringUtils.isEmpty(phone)){
			return Result.error("手机号+验证码不能为空");
		}
		Object code = redisUtil.get(phone);
		if(devTest){
			//此处可添加指定验证码校验
		}else{
			if (!smscode.equals(code)) {
				return Result.error("手机验证码错误");
			}
		}
		try{
			LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
//			applyService.removeUserInfos(loginUser.getId());
			return Result.ok();
		}catch (Exception e){
			return Result.error(e.getMessage());
		}
	}


	/**
	 * 忘记密码
	 */
	@ApiOperation(value = "忘记密码", notes = "忘记密码")
	@PostMapping(value = "/resetPwd")
	public Result<?> resetPwd(@RequestBody JSONObject jsonObject) throws Exception {
		String phone = jsonObject.getString("phone");
		String smscode = jsonObject.getString("code");
		String password = jsonObject.getString("password");
		if(StringUtils.isEmpty(phone)){
			return Result.error("手机号不能为空");
		}else if(StringUtils.isEmpty(smscode)){
			return Result.error("验证码不能为空");
		}else if(StringUtils.isEmpty(password)){
			return Result.error("密码不能为空");
		}
		Object code = redisUtil.get(phone);
		if(devTest){
			//此处可添加指定验证码校验
		}else{
			if (!smscode.equals(code)) {
				return Result.error("手机验证码错误");
			}
		}
		LoginUser sysUser = sysBaseApi.getUserByPhone(phone);
		Result<JSONObject> result = (Result<JSONObject>) sysBaseApi.checkUserIsEffective(sysUser);
		if (!result.isSuccess()) {
			return result;
		}
		try{
			//重置密码
			sysBaseApi.changePassword(sysUser.getId(),phone,password);
			return Result.ok();
		}catch (Exception e){
			return Result.error(e.getMessage());
		}
	}

	/**
	 * 修改绑定手机
	 */
	@ApiOperation(value = "修改绑定手机", notes = "修改绑定手机")
	@PostMapping(value = "/updatePhone")
	public Result<?> updatePhone(@RequestBody JSONObject jsonObject) throws Exception {
		String phone = jsonObject.getString("phone");
		String smscode = jsonObject.getString("code");
		if(StringUtils.isEmpty(phone)){
			return Result.error("手机号不能为空");
		}else if(StringUtils.isEmpty(smscode)){
			return Result.error("验证码不能为空");
		}
		Object code = redisUtil.get(phone);
		if(devTest){
			//此处可添加指定验证码校验
		}else{
			if (!smscode.equals(code)) {
				return Result.error("手机验证码错误");
			}
		}
		LoginUser sysUser = sysBaseApi.getUserByPhone(phone);
		if (sysUser!=null){
			return Result.error("修改失败：号码已存在");
		}
		//判定当前账号
		LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		if (loginUser.getPhone().equals(phone)){
			return Result.error("修改失败：号码相同");
		}
		loginUser.setPhone(phone);
		try{
			sysBaseApi.updateUserInfo(loginUser);
			return Result.ok(true);
		}catch (Exception e){
			return Result.error(e.getMessage());
		}
	}


	/**
	 * 更新用户定位信息
	 * @param userLocation
	 * @return
	 */
	@ApiOperation("更新用户定位信息")
	@PostMapping(value = "/updateUserLocation")
	public Result<LoginUser> updateUserLocation(HttpServletRequest request, @RequestBody UserLocation userLocation) {
		Result<LoginUser> result = new Result<LoginUser>();
		try {
			String username = JwtUtil.getUserNameByToken(request);
			LoginUser sysUser = sysBaseApi.getUserByName(username);
			if(sysUser==null) {
				result.error500("未找到对应用户!");
			}else {
				//判断是否存在简历
				resumeService.createDefaultResume(sysUser,userLocation);
				result.setCode(200);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.error500("保存失败!");
		}
		return result;
	}

}