package org.jeecg.modules.job.api.util;

import cn.hutool.json.JSONUtil;
import com.aliyun.ocr20191230.models.RecognizeBankCardResponse;
import com.aliyun.ocr20191230.models.RecognizeBusinessLicenseResponse;
import com.aliyun.tea.TeaModel;
import com.google.gson.Gson;
import org.jeecg.modules.job.api.model.BusinessLicenseModel;
import org.jeecg.modules.job.api.model.BusinessLicenseResponse;

import java.io.InputStream;
import java.net.URL;

public class AliOcrUtil {

    /**
     * 使用AK&SK初始化账号Client
     * @param accessKeyId
     * @param accessKeySecret
     * @return Client
     * @throws Exception
     */
    // 这里只是以ocr为例，其他能力请使用相应类目的包下面的Client类
    public static com.aliyun.ocr20191230.Client createClient(String accessKeyId, String accessKeySecret) throws Exception {
        /*
          初始化配置对象com.aliyun.teaopenapi.models.Config
          Config对象存放AccessKeyId、AccessKeySecret、endpoint等配置
         */
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret);

        // 2、访问的域名。注意：这个地方需要求改为相应类目的域名，参考：https://help.aliyun.com/document_detail/143103.html
        config.endpoint = "ocr.cn-shanghai.aliyuncs.com";

        // 3、这里只是以ocr为例，其他能力请使用相应类目的包下面的Client类
        return new com.aliyun.ocr20191230.Client(config);
    }

    public static void main(String[] args) throws Exception {
        String imgUrl="https://xxx.oss-cn-shanghai.aliyuncs.com/kp/auth/1701861623303116.png";
        BusinessLicenseModel result = getOcrInfo(imgUrl,"xxxx","xxxx");
        System.out.println(result.toString());
    }

    public static BusinessLicenseModel getOcrInfo(String imgUrl,String key,String secret) throws Exception{
        com.aliyun.ocr20191230.Client client = AliOcrUtil.createClient(key, secret);
        // 场景一，使用本地文件
        // InputStream inputStream = new FileInputStream(new File("/tmp/RecognizeBusinessLicense.jpg"));
        // 场景二，使用任意可访问的url
        URL url = new URL(imgUrl);
        InputStream inputStream = url.openConnection().getInputStream();
        com.aliyun.ocr20191230.models.RecognizeBusinessLicenseAdvanceRequest recognizeBusinessLicenseAdvanceRequest = new com.aliyun.ocr20191230.models.RecognizeBusinessLicenseAdvanceRequest()
                .setImageURLObject(inputStream);
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        RecognizeBusinessLicenseResponse recognizeAccountPageResponse = client.recognizeBusinessLicenseAdvance(recognizeBusinessLicenseAdvanceRequest, runtime);
        //同OSS区域
//        com.aliyun.ocr20191230.models.RecognizeBusinessLicenseRequest recognizeBusinessLicenseRequest = new com.aliyun.ocr20191230.models.RecognizeBusinessLicenseRequest()
//                .setImageURL(imgUrl);
//        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
//        RecognizeBusinessLicenseResponse recognizeAccountPageResponse = client.recognizeBusinessLicenseWithOptions(recognizeBusinessLicenseRequest, runtime);
        // 获取整体结果
        if (recognizeAccountPageResponse.getStatusCode()==200){
            String json=com.aliyun.teautil.Common.toJSONString(TeaModel.buildMap(recognizeAccountPageResponse.getBody()));
            System.out.println(json);
            Gson gson = new Gson();
            BusinessLicenseResponse result = gson.fromJson(json, BusinessLicenseResponse.class);
            System.err.println("data========="+result.getData().toString());
            return result.getData();
        }else{
            System.err.println("xxxxxxxxxxxxxxxxxxxx");
            throw new  Exception("识别错误");
        }
    }

    public static void main2(String[] args_) throws Exception {
        // 创建AccessKey ID和AccessKey Secret，请参考https://help.aliyun.com/document_detail/175144.html。
        // 如果您使用的是RAM用户的AccessKey，还需要为子账号授予权限AliyunVIAPIFullAccess，请参考https://help.aliyun.com/document_detail/145025.html。
        // 从环境变量读取配置的AccessKey ID和AccessKey Secret。运行代码示例前必须先配置环境变量。
        String accessKeyId = System.getenv("ALIBABA_CLOUD_ACCESS_KEY_ID");
        String accessKeySecret = System.getenv("ALIBABA_CLOUD_ACCESS_KEY_SECRET");
        com.aliyun.ocr20191230.Client client = AliOcrUtil.createClient("xxxx", "xxxx");
        com.aliyun.ocr20191230.models.RecognizeBusinessLicenseRequest recognizeBusinessLicenseRequest = new com.aliyun.ocr20191230.models.RecognizeBusinessLicenseRequest()
                .setImageURL("https://xxx.oss-cn-shanghai.aliyuncs.com/kp/auth/1701861623303116.png");
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        try {
            RecognizeBusinessLicenseResponse recognizeAccountPageResponse = client.recognizeBusinessLicenseWithOptions(recognizeBusinessLicenseRequest, runtime);
            // 获取整体结果
            System.out.println(com.aliyun.teautil.Common.toJSONString(TeaModel.buildMap(recognizeAccountPageResponse)));
            // 获取单个字段
            System.out.println(recognizeAccountPageResponse.getBody());
        } catch (com.aliyun.tea.TeaException teaException) {
            // 获取整体报错信息
            System.out.println(com.aliyun.teautil.Common.toJSONString(teaException));
            // 获取单个字段
            System.out.println(teaException.getCode());
        }
    }

    public static void main3(String[] args) {
        String str="{\"RequestId\":\"FC88EE4A-DC20-57FF-8096-AF74F7916F8F\",\"Data\":{\"Address\":\"无锡市新吴区湘江路2-3-1908\",\"Stamp\":{\"Left\":0,\"Top\":0,\"Height\":0,\"Width\":0},\"ValidPeriod\":\"29991231\",\"Title\":{\"Left\":0,\"Top\":0,\"Height\":0,\"Width\":0},\"Capital\":\"100万元整\",\"LegalPerson\":\"芮莹\",\"EstablishDate\":\"20221108\",\"Name\":\"无锡道源景观工程服务有限公司\",\"RegisterNumber\":\"91320214MAC3EH9U7L\",\"QRCode\":{\"Left\":0,\"Top\":0,\"Height\":0,\"Width\":0},\"Type\":\"有限责任公司(自然人投资或控股)\",\"Angle\":0,\"Emblem\":{\"Left\":0,\"Top\":0,\"Height\":0,\"Width\":0},\"Business\":\"一般项目:园林绿化工程施工;土石方工程施工;对外承包工程;工程管理服务;工程造价咨询业务;专业设计服务;工程技术服务(规划管理、勘察、设计、监理除外);城市绿化管理;市政设施管理;花卉绿植租借与代管理;水污染治理;信息咨询服务(不含许可类信息咨询服务);信息技术咨询服务;礼品花卉销售;建筑装饰材料销售;建筑用钢筋产品销售;建筑材料销售;水泥制品制造;五金产品批发;五金产品零售;日用品批发;日用品销售;劳动保护用品销售;园艺产品销售(除依法须经批准的项目外,凭营业执照依法自主开展经营活动)\"}}";
        Gson gson = new Gson();
        BusinessLicenseResponse result = gson.fromJson(str, BusinessLicenseResponse.class);
        System.out.println(result.getData().toString());
    }

}
