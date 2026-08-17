package org.jeecg.modules.job.ums.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.OrderUtils;
import org.jeecg.common.util.PasswordUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.job.ums.entity.UmsMemberRole;
import org.jeecg.modules.job.ums.mapper.UmsMemberRoleMapper;
import org.jeecg.modules.job.ums.service.IUmsMemberRoleService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @Description: 会员公司关系
 * @Author: qingkonglan
 * @Date:   2022-12-18
 * @Version: V1.0
 */
@Service
public class UmsMemberRoleServiceImpl extends ServiceImpl<UmsMemberRoleMapper, UmsMemberRole> implements IUmsMemberRoleService {

    @Resource
    private ISysBaseAPI sysBaseAPI;


    @Override
    public boolean saveOrUpdateRole(UmsMemberRole role) {
//        UmsMemberRole result=this.getOne(new QueryWrapper<>(role));
        //直接删除原来的，重新添加
        this.remove(new LambdaQueryWrapper<UmsMemberRole>().eq(UmsMemberRole::getMemberId,role.getMemberId()));
        return this.save(role);
    }

    @Override
    public boolean addCompanyUser(String phone, String name, String postName,String companyId) {
        //判断用户是否已存在
        LoginUser user=sysBaseAPI.getUserByName(phone);
        UmsMemberRole memberRole=new UmsMemberRole();
        memberRole.setPostName(postName);
        memberRole.setCompanyId(companyId);
        memberRole.setMemberRole(0);//普通招聘者
        if (user!=null){
//            throw new RuntimeException("无法绑定已注册账号");
            //判断当前是否为企业角色
//            List<String> ids=sysBaseAPI.getRoleIdsByUsername(user.getId());
//            if (ids.size()>0 && !ids.get(0).equals("1690647456108986369")){
//                throw new RuntimeException("绑定账号非企业账号");
//            }
            //判断用户角色是否存在
            UmsMemberRole role=getOne(new QueryWrapper<>(new UmsMemberRole().setMemberId(user.getId())));
            if (role!=null){
                throw new RuntimeException("该手机号已绑公司,请先解绑");
            }
            //配置用户角色
            sysBaseAPI.updateUserInfo(user,"1690647456108986369",null,null);
        }else{
            //新增用户
            user=new LoginUser();
            user.setId(RandomUtil.randomNumbers(19));
            user.setPhone(phone);
            user.setNickname(name);
            user.setRealname(name);
            user.setUsername(phone);
            user.setStatus(1);
            user.setDelFlag(CommonConstant.DEL_FLAG_0);
            user.setSex(0);
            user.setUserIdentity(3);//移动端
            String salt = oConvertUtils.randomGen(8);
            user.setSalt(salt);
            String password = "qkl123456";//默认密码
            String passwordEncode = PasswordUtil.encrypt(user.getUsername(), password, salt);
            user.setPassword(passwordEncode);
            user.setUserCode(OrderUtils.getLocalTrmSeqNum());
            //生成inviteCode
            String mId = null;
            Boolean haveId= true;//默认有重复的推荐码
            while (haveId) {
                mId=RandomUtil.randomNumbers(6);
                //数据库查找
                LoginUser tt = sysBaseAPI.getUserByInviteCode(mId);
                if (tt == null) {//没有查到数据，可以使用
                    haveId = false;
                }
            }
            user.setInviteCode(mId);
            sysBaseAPI.saveUser(user,"1690647456108986369",null,null);

        }
        memberRole.setMemberId(user.getId());
        this.save(memberRole);
        return true;
    }

    @Override
    public UmsMemberRole getMemberRole(String memberId) {
        return this.getOne(new QueryWrapper<UmsMemberRole>().eq("member_id",memberId));
    }

    @Override
    public String getCompanyId(String memberId) {
        UmsMemberRole role=getMemberRole(memberId);
        if(role==null){
            return null;
        }
        return role.getCompanyId();
    }

    @Override
    public IPage<Map<String, Object>> getMemeberRoleList(Page<UmsMemberRole> page, UmsMemberRole paramCondition) {
        return this.baseMapper.getMemeberRoleList(page,paramCondition);
    }

    @Override
    public UmsMemberRole getCompanyManager(String companyId) {
        List<UmsMemberRole> list=this.list(new QueryWrapper<>(new UmsMemberRole().setCompanyId(companyId).setMemberRole(1)));
        if (list.size()>0){
            return list.get(0);
        }
        return null;
    }
}
