package org.jeecg.modules.job.api.util.Json;

import lombok.Data;

import java.util.List;
@Data
public class JsonVo {

    private String pid;
    private String code;
    private String name;
    private List<JsonVo> subLevelModelList;

}
