package org.jeecg.modules.job.api.model;

import lombok.Data;

@Data
public class AIPromptModel {
    //类型：1-自荐信，2-推荐信
    private int type;
    //求职者ID
    private String memberId;
    //推荐者ID
    private String refererId;
    //岗位ID
    private String positionId;
}
