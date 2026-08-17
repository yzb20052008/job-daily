package org.jeecg.modules.job.pay.entity;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.jeecg.modules.job.pay.utils.TransferToUser;

import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(chain = true)
public class TransferToUserRequest {

    public String appid;

    public String out_bill_no;

    public String transfer_scene_id;

    public String openid;

    public String user_name;

    public Long transfer_amount;

    public String transfer_remark;

    public String notify_url;

    public String user_recv_perception;

    public List<TransferSceneReportInfo> transfer_scene_report_infos = new ArrayList<TransferSceneReportInfo>();
}
