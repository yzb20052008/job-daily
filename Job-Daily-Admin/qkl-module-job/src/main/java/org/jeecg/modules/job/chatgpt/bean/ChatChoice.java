package org.jeecg.modules.job.chatgpt.bean;

import lombok.Data;

@Data
public class ChatChoice {

    Integer index;
    String logprobs;
    String text;
    String finish_reason;
}
