package com.tencent.wxcloudrun.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.model.WarrantyInfo;
import com.tencent.wxcloudrun.service.WarrantyInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/warranty")
public class WarrantyInfoController {

    @Autowired
    private WarrantyInfoService warrantyInfoService;

    @PostMapping("/save")
    public ApiResponse save(@RequestBody WarrantyInfo info){
        return warrantyInfoService.addInfo(info);
    }

    @GetMapping("/get")
    public ApiResponse get(String carNumber,String phone){
        WarrantyInfo info = null;
        LambdaQueryWrapper<WarrantyInfo> query = Wrappers.lambdaQuery();

        if(!StringUtils.isEmpty(carNumber)){
            query.eq(WarrantyInfo::getNickname,carNumber);
        }
        if(!StringUtils.isEmpty(phone)){
//            query.or().eq(WarrantyInfo::getPhone,phone);
            query.and(i->i.eq(WarrantyInfo::getPhone,phone));
        }

        try {
            info = warrantyInfoService.getOne(query);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if(info==null){
            return ApiResponse.error("质保信息不存在");
        }
        return ApiResponse.ok(info);
    }
}
