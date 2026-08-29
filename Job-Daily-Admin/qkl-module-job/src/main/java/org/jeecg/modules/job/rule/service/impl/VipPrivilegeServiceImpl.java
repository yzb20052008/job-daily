package org.jeecg.modules.job.rule.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.job.base.entity.BaseConfig;
import org.jeecg.modules.job.base.service.IBaseConfigService;
import org.jeecg.modules.job.constant.BizConstants;
import org.jeecg.modules.job.constant.PrivilegeCodes;
import org.jeecg.modules.job.job.entity.JobPostContact;
import org.jeecg.modules.job.job.mapper.JobPostContactMapper;
import org.jeecg.modules.job.rule.service.IVipPrivilegeService;
import org.jeecg.modules.job.ums.entity.UmsUserVip;
import org.jeecg.modules.job.ums.service.IUmsUserVipService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * VIP / 权益统一校验（规则读 base_config，可后台调）
 * <p>不依赖 ContactService，避免与拨号服务循环依赖</p>
 */
@Slf4j
@Service
public class VipPrivilegeServiceImpl implements IVipPrivilegeService {

    @Resource
    private IUmsUserVipService userVipService;
    @Resource
    private IBaseConfigService configService;
    @Resource
    private JobPostContactMapper contactMapper;

    @Override
    public boolean isVipActive(String userId, String roleCode) {
        if (oConvertUtils.isEmpty(userId) || oConvertUtils.isEmpty(roleCode)) {
            return false;
        }
        UmsUserVip vip = userVipService.getUserVip(userId, roleCode);
        if (vip == null || vip.getVipEndTime() == null) {
            return false;
        }
        return vip.getVipEndTime().after(new Date());
    }

    @Override
    public boolean isContactFree(String userId, String roleCode, String postId) {
        if (oConvertUtils.isEmpty(userId) || oConvertUtils.isEmpty(roleCode)) {
            return false;
        }
        if (isVipActive(userId, roleCode) && isFlagOn(PrivilegeCodes.VIP_CONTACT_FREE, true)) {
            return true;
        }
        return hasRecentContact(userId, roleCode, postId);
    }

    @Override
    public void assertCanPublishPost(String userId) {
        if (!isFlagOn(PrivilegeCodes.VIP_POST_REQUIRE, false)) {
            return;
        }
        if (!isVipActive(userId, BizConstants.ROLE_CODE_COMPANY)) {
            throw new RuntimeException("发岗需开通企业会员，请先购买 VIP");
        }
    }

    private boolean hasRecentContact(String userId, String roleCode, String postId) {
        if (oConvertUtils.isEmpty(postId)) {
            return false;
        }
        int minutes = 30;
        try {
            BaseConfig config = configService.getConfigByCode(BizConstants.CALL_ENSURE_TIME);
            if (config != null && oConvertUtils.isNotEmpty(config.getConfigValue())) {
                minutes = Integer.parseInt(config.getConfigValue().trim());
            }
        } catch (Exception e) {
            log.warn("读取拨号确认时长失败，使用默认 {} 分钟", minutes);
        }
        Date since = DateUtil.offsetMinute(new Date(), -minutes);
        QueryWrapper<JobPostContact> qw = new QueryWrapper<>();
        qw.eq("post_id", postId).eq("role_code", roleCode).ge("create_time", since);
        if (BizConstants.ROLE_CODE_COMPANY.equals(roleCode)) {
            qw.eq("post_user_id", userId);
        } else {
            qw.eq("user_id", userId);
        }
        qw.orderByDesc("create_time").last("LIMIT 1");
        List<JobPostContact> list = contactMapper.selectList(qw);
        return list != null && !list.isEmpty();
    }

    private boolean isFlagOn(String configCode, boolean defaultOn) {
        try {
            BaseConfig cfg = configService.getConfigByCode(configCode);
            if (cfg == null || oConvertUtils.isEmpty(cfg.getConfigValue())) {
                return defaultOn;
            }
            String v = cfg.getConfigValue().trim();
            return "1".equals(v) || "true".equalsIgnoreCase(v) || "Y".equalsIgnoreCase(v);
        } catch (Exception e) {
            log.warn("读取权益配置失败 code={}，使用默认值 {}", configCode, defaultOn);
            return defaultOn;
        }
    }
}
