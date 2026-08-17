package org.jeecg.modules.job.chatgpt.bean;

import lombok.Data;

import java.util.List;
@Data
public class ChatResponse {
    String id;
    String object;
    long created;
    String model;
    List<ChatChoice> choices;
    ChatUsage usage;
}


