package org.jeecg.modules.job.chatgpt.bean;

import lombok.Data;

@Data
public class ChatUsage {
    long prompt_tokens;
    long completion_tokens;
    long total_tokens;
}
