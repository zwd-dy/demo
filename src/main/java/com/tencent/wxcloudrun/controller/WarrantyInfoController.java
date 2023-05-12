package com.tencent.wxcloudrun.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.model.WarrantyInfo;
import com.tencent.wxcloudrun.service.WarrantyInfoService;
import com.tencent.wxcloudrun.utils.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
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
    public void getExcel(HttpServletResponse response) {
        List<WarrantyInfo> infoList = warrantyInfoService.list();
        if (infoList != null || infoList.size() > 0) {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            String time = format.format(new Date());
            time = time.replaceAll("-", "").replaceAll(":", "").replaceAll(" ", "");
            String fileName = "质保数据" + time;
            String sheetName = "质保数据";
            try {
                ExcelUtil.writeExcel(response, infoList, fileName, sheetName, new WarrantyInfo());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

}
