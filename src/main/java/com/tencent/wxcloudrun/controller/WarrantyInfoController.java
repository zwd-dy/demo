package com.tencent.wxcloudrun.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicSessionCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.region.Region;
import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.model.WarrantyInfo;
import com.tencent.wxcloudrun.service.WarrantyInfoService;
import com.tencent.wxcloudrun.utils.ExcelUtil;
import com.tencent.wxcloudrun.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/warranty")
public class WarrantyInfoController {

    @Autowired
    private WarrantyInfoService warrantyInfoService;

    @PostMapping("/save")
    public ApiResponse save(@RequestBody WarrantyInfo info) {
        return warrantyInfoService.addInfo(info);
    }

    @GetMapping("/get")
    public ApiResponse get(String carNumber, String phone) {
        System.out.println("carNumber：" + carNumber);
        System.out.println("phone：" + phone);
        WarrantyInfo info = null;
        LambdaQueryWrapper<WarrantyInfo> query = Wrappers.lambdaQuery();

        if (!StringUtils.isEmpty(carNumber)) {
            query.eq(WarrantyInfo::getCarNumber, carNumber);
        }
        if (!StringUtils.isEmpty(phone)) {
//            query.or().eq(WarrantyInfo::getPhone,phone);
            query.and(i -> i.eq(WarrantyInfo::getPhone, phone));
        }

        try {
            info = warrantyInfoService.getOne(query);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (info == null) {
            return ApiResponse.error("质保信息不存在");
        }
        return ApiResponse.ok(info);
    }

    @GetMapping("/getExcel")
    public void getExcel() {
        String s = HttpClientUtil.doGet("http://api.weixin.qq.com/_/cos/getauth");
        System.out.println("对象存储："+s);
        JSONObject jsonObject = JSON.parseObject(s);
//        String secretId = jsonObject.getString("TmpSecretId");
//        String secretKey = jsonObject.getString("TmpSecretKey");
//        String token = jsonObject.getString("Token");

        String secretId = "AKIDUs-32vdL7iMvxml96ZaQmY3FEys_4-FoCPzqfLDx8NCyssfZP6upI8ld1zaIQydy";
        String secretKey = "vw6BEp7ouVpSGkICL0MxVqqpDigleDG8TLmf97Qod0M=";
        String token = "StTRv6zdmmgf7luHxXXueEzP7HUiqO1a7064e3ee4adf11066556e035528571d9UZZP1RLixTFtDIkRl879DzXjKKnDNahobdbB-DLuqcJZ_UdRcyasnkP62p3YZ4Zm2-36UwqANfQxd3lSHQgEpX03bwdwptYjU1QWSC9RtnngwEg4Q-wo9t57DWAPKsPu582-PSL3gYW2QUbMQdxWk5CrGRabYwGL_eetLgtBISoEQcOSNVjN98Z0RyDJ7MLZX3SE3E6tXaQnYJj796Wn0nBMI8rUTIjkH5zBSnak6DrxZt4y6ROJIVLXCOEM4kM4oxy3_C0F0cdsBUcX4Ll7piXDbz3EN9jpT4OFKqvZYTIP8RmF8uAPvV5VHcup3ryqZ7jTOLlnuYnQCN8_P_VboJ6k1fIhTBrAu0ZXXxKuPOOkkXHeFWMx2fLlWkUmAnkcic1rwqTIpfonzvhAzidSshoP1zbhspK2b4CDxiqxs2xTVWtjpO6V3zm3IyHI2IVOX0ACPqMs43iOHTf1-Lu5owc90G1r7Q_WkDqr0RaICgk";

        ClientConfig clientConfig = new ClientConfig(new Region("ap-shanghai"));
        COSCredentials cosCredentials = new BasicSessionCredentials(secretId, secretKey, token);
        COSClient cosClient = new COSClient(cosCredentials, clientConfig);

        List<WarrantyInfo> infoList = warrantyInfoService.list();
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        if (infoList != null || infoList.size() > 0) {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            String time = format.format(new Date());
            time = time.replaceAll("-", "").replaceAll(":", "").replaceAll(" ", "");
            String fileName = "质保数据" + time;
            String sheetName = "质保数据";
            try {
                ExcelUtil.writeExcel2(os, infoList, fileName, sheetName, new WarrantyInfo());

            } catch (Exception e) {
                e.printStackTrace();
            }

            byte[] buffer = os.toByteArray();
            InputStream sbs = new ByteArrayInputStream(buffer);
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(buffer.length);

//            //创建存储对象的请求
            PutObjectRequest putObjectRequest = new PutObjectRequest("7072-prod-0guxo16k879428da-1310128581", "excel.xlsx", sbs,objectMetadata);
            //执行上传
            cosClient.putObject(putObjectRequest);
        }


    }

}

