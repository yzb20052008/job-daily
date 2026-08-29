package org.jeecg.modules.job.msg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.job.msg.entity.BizMsgTemplate;

import java.util.Map;

/**
 * 业务消息模板服务
 */
public interface IBizMsgTemplateService extends IService<BizMsgTemplate> {

    /**
     * 按编码查询启用模板
     */
    BizMsgTemplate getEnabledByCode(String templateCode);

    /**
     * 解析微信模板ID；库中无配置时返回 fallback
     */
    String resolveWxTemplateId(String templateCode, String fallback);

    /**
     * 渲染标题（占位符 {key}）
     */
    String renderTitle(String templateCode, Map<String, String> vars, String fallback);

    /**
     * 渲染内容
     */
    String renderContent(String templateCode, Map<String, String> vars, String fallback);
}
