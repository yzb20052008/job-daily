package org.jeecg.modules.job.api.model;

import lombok.Data;

/**
 * 营业执照OCR返回值
 */
@Data
public class BusinessLicenseResponse {

    private String RequestId;
    private BusinessLicenseModel Data;
}
