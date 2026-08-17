package org.jeecg.modules.job.api.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value="用户信息", description="用户信息")
public class UserModel {
    /**
     * 用户id
     */
    private String id;

    /**
     * 用户电话
     */
    private String phone;

    /**
     * 用户电话
     */
    private String uuid;

    /**
     * 用户电话
     */
    private String username;

}
