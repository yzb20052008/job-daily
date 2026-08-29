package org.jeecg.modules.job.msg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.job.msg.entity.BizMsgTemplate;
import org.jeecg.modules.job.msg.mapper.BizMsgTemplateMapper;
import org.jeecg.modules.job.msg.service.IBizMsgTemplateService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 业务消息模板服务实现
 */
@Service
public class BizMsgTemplateServiceImpl extends ServiceImpl<BizMsgTemplateMapper, BizMsgTemplate>
        implements IBizMsgTemplateService {

    @Override
    public BizMsgTemplate getEnabledByCode(String templateCode) {
        if (oConvertUtils.isEmpty(templateCode)) {
            return null;
        }
        return this.getOne(new QueryWrapper<BizMsgTemplate>()
                .eq("template_code", templateCode)
                .eq("status", "1")
                .last("LIMIT 1"));
    }

    @Override
    public String resolveWxTemplateId(String templateCode, String fallback) {
        BizMsgTemplate tpl = getEnabledByCode(templateCode);
        if (tpl != null && oConvertUtils.isNotEmpty(tpl.getWxTemplateId())) {
            return tpl.getWxTemplateId();
        }
        return fallback;
    }

    @Override
    public String renderTitle(String templateCode, Map<String, String> vars, String fallback) {
        BizMsgTemplate tpl = getEnabledByCode(templateCode);
        if (tpl == null || oConvertUtils.isEmpty(tpl.getTitle())) {
            return fill(fallback, vars);
        }
        return fill(tpl.getTitle(), vars);
    }

    @Override
    public String renderContent(String templateCode, Map<String, String> vars, String fallback) {
        BizMsgTemplate tpl = getEnabledByCode(templateCode);
        if (tpl == null || oConvertUtils.isEmpty(tpl.getContent())) {
            return fill(fallback, vars);
        }
        return fill(tpl.getContent(), vars);
    }

    private String fill(String text, Map<String, String> vars) {
        if (text == null) {
            return null;
        }
        if (vars == null || vars.isEmpty()) {
            return text;
        }
        String out = text;
        for (Map.Entry<String, String> e : vars.entrySet()) {
            out = out.replace("{" + e.getKey() + "}", e.getValue() == null ? "" : e.getValue());
        }
        return out;
    }
}
