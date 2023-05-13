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
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.model.WarrantyInfo;
import com.tencent.wxcloudrun.service.WarrantyInfoService;
import com.tencent.wxcloudrun.utils.ExcelUtil;
import com.tencent.wxcloudrun.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/warranty")
public class WarrantyInfoController {

    @Autowired
    private WarrantyInfoService warrantyInfoService;
    @Value("${wx.regionName}")
    private String regionName;
    @Value("${wx.bucketName}")
    private String bucketName;

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
    public ApiResponse getExcel() {
        URL url = null;
        String s = HttpClientUtil.doGet("http://api.weixin.qq.com/_/cos/getauth");
        System.out.println( "对象存储："+s);
        JSONObject jsonObject = JSON.parseObject(s);
        String secretId = jsonObject.getString("TmpSecretId");
        String secretKey = jsonObject.getString("TmpSecretKey");
        String token = jsonObject.getString("Token");
//        String secretId = "AKIDxoI2UHo_zwks3RT-JT5MCiDAUXPRo1AUSxSzVM3XZqkRvTj9e87o8oWna_8OnYd7";
//        String secretKey = "le5ZtiKzW1Qm0XBDQ9qC77XryvrLBTN+qDBdSGL94PU=";
//        String token = "StTRv6zdmmgf7luHxXXueEzP7HUiqO1a382f157fe2a3d26348a03dc6a7775b54UZZP1RLixTFtDIkRl879D8VZq6PZ_W7GWF2hvOJvyfp9mqCZPgfBGNSf3IyRSLTPDEkmGqzhO_8oJk6e7t-fpmEoneje55-F2g-yZhInm1vVgSZAfezNZ4tsfIUBJKqKYLpuMZR6HYgz1yqvDVvfZJFgf1mfnrN4IEDM_mFDzpemfgLMKGEoN21tkPavvCAb7XgXSZzIL70EJzCq5Xqot3f7jfzLZbbtD7O0lchaOub8MGWhaKWCxjp9T_k1XuTDYvhPcIHZosse7meqaPWplffLCXCLrOl1kn2EMWPIvHMB43VWFZU4gkh7gXBWg9QQfuk9oJbQD0IPJ1_IJmTAs5w4bL76xC1FbH6o0th3UkfZG_VCbzeELAI22A8M7ds8WOB0ry4bFCLr0xbN488JMlrv3Q30XZ176R0WQVf-CWEis0hU3Aa9DxsLjJnL3iPMe7w8P_l9o-nmRLlcN9kVLtaRHWpyRLdIfYTNn29xESk";

        ClientConfig clientConfig = new ClientConfig(new Region(regionName));
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
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName+".xlsx", sbs,objectMetadata);
            //执行上传
            cosClient.putObject(putObjectRequest);
            url = cosClient.getObjectUrl(bucketName, fileName + ".xlsx");

        }
        return ApiResponse.ok(url);
    }

}

