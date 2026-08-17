package org.jeecg.modules.job.cms.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.job.api.util.WxUtil;
import org.jeecg.modules.job.cms.entity.CmsNotice;
import org.jeecg.modules.job.cms.entity.CmsNoticeRead;
import org.jeecg.modules.job.cms.mapper.CmsNoticeMapper;
import org.jeecg.modules.job.cms.service.ICmsNoticeReadService;
import org.jeecg.modules.job.cms.service.ICmsNoticeService;
import org.jeecg.modules.job.utils.WX_TemplateMsgUtil;
import org.jeecg.modules.job.utils.WxMsgSendUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @Description: 系统通知
 * @Author: qingkonglan
 * @Date:   2022-09-26
 * @Version: V1.0
 */
@Service
public class CmsNoticeServiceImpl extends ServiceImpl<CmsNoticeMapper, CmsNotice> implements ICmsNoticeService {

    @Resource
    private ICmsNoticeReadService readService;
    @Autowired
    private RedisUtil redisUtil;
    @Resource
    private ISysBaseAPI sysBaseAPI;

    @Override
    public boolean addNotice(CmsNotice notice) {
        if (oConvertUtils.isNotEmpty(notice.getUserId())){
            String[] userIds=notice.getUserId().split(",");
            CmsNotice cn=null;
            for (String userId:userIds){
                cn=new CmsNotice();
                cn.setTitle(notice.getTitle());
                cn.setContent(notice.getContent());
                cn.setSetTop(notice.getSetTop());
                cn.setStatus(notice.getStatus());
                cn.setExcerpt(notice.getExcerpt());
                cn.setBanner(notice.getBanner());
                cn.setAvatar(notice.getAvatar());
                cn.setUserId(userId);
                cn.setIfPublic(0);
                if(oConvertUtils.isNotEmpty(notice.getType())){
                    cn.setType(notice.getType());
                }else{
                    cn.setType(2);//平台私信
                }
                this.save(cn);
            }
        }else{
            notice.setType(0);
            return this.save(notice);
        }
        return true;
    }


    @Override
    public int getUnReadCount(String roleCode,String userId,Integer type) {
        return this.baseMapper.getUnReadCount(roleCode,userId,type);
    }

    @Transactional
    @Override
    public boolean setAllRead(String userId) {
        IPage<CmsNotice> pageList = baseMapper.getUnReadList(new Page<>(1, 1000),userId);
        pageList.getRecords().forEach(item->{
            //添加已读记录
            readService.save(new CmsNoticeRead().setNoticeId(item.getId()).setUserId(userId));
        });
        return true;
    }

    @Override
    public boolean addOrderNotice(String roleCode,String userId, String title, String content, String cover,String orderId,String dataId) {
        CmsNotice notice=new CmsNotice();
        notice.setRoleCode(roleCode);
        notice.setIfPublic(0);
        notice.setUserId(userId);
        notice.setType(1);
        notice.setDataId(dataId);
        notice.setOrderId(orderId);
        notice.setContent(content);
        notice.setTitle(title);
        if (!oConvertUtils.isEmpty(cover)){
            notice.setAvatar(cover);
        }
        return this.save(notice);
    }

    @Override
    public boolean addBalanceNotice(String userId, String title, String content, String dataId) {
        CmsNotice notice=new CmsNotice();
        notice.setIfPublic(0);
        notice.setUserId(userId);
        notice.setType(3);
        notice.setDataId(dataId);
        notice.setContent(content);
        notice.setTitle(title);
        return this.save(notice);
    }

    @Override
    public boolean addVipNotice(String userId, String content) {
        CmsNotice notice=new CmsNotice();
        notice.setIfPublic(0);
        notice.setUserId(userId);
        notice.setType(2);
        notice.setTitle("会员到期提醒");
        notice.setContent(content);
        return this.save(notice);
    }

    @Override
    public boolean addCodeNotice(String userId, String phone,String logId) {
        CmsNotice notice=new CmsNotice();
        notice.setIfPublic(0);
        notice.setUserId(userId);
        notice.setType(2);
        notice.setDataId(phone);
        notice.setExcerpt(logId);
        //随机数
        String captcha = RandomUtil.randomNumbers(6);
        //验证码60分钟内有效
        redisUtil.set(phone, captcha, 60*60);
        notice.setContent("平台验证码为：<font color=#dd524d font-weight=bold>"+captcha+"</font>,验证码60分钟内有效。");
        notice.setTitle("平台验证码");
        return this.save(notice);
    }

    @Override
    public boolean addPrivateNotice(String userId, String title, String content, String dataId) {
        CmsNotice notice=new CmsNotice();
        notice.setIfPublic(0);
        notice.setUserId(userId);
        notice.setType(2);
        notice.setTitle(title);
        notice.setContent(content);
        return this.save(notice);
    }
}
