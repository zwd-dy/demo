package com.tencent.wxcloudrun.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dao.WarrantyInfoMapper;
import com.tencent.wxcloudrun.model.WarrantyInfo;
import com.tencent.wxcloudrun.service.WarrantyInfoService;
import org.springframework.stereotype.Service;

@Service
public class WarrantyInfoServiceImpl extends ServiceImpl<WarrantyInfoMapper, WarrantyInfo> implements WarrantyInfoService {
    @Override
    public ApiResponse addInfo(WarrantyInfo info) {
        // 伪联合主键（用户姓名+手机号）判断是否存在
        LambdaQueryWrapper<WarrantyInfo> lqw = Wrappers.lambdaQuery();
        lqw
                .eq(WarrantyInfo::getNickname, info.getNickname()).or()
                .eq(WarrantyInfo::getPhone, info.getPhone());
        WarrantyInfo one = this.getOne(lqw);
        if(one != null){
            return ApiResponse.error("用户质保信息已存在，请勿重复添加");
        }

        boolean suc = this.save(info);
        if(!suc) return ApiResponse.error("服务器出错");
        return ApiResponse.ok(info);
    }
}
