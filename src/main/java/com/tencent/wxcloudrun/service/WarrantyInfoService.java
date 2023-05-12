package com.tencent.wxcloudrun.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.model.WarrantyInfo;

public interface WarrantyInfoService extends IService<WarrantyInfo> {
    public ApiResponse addInfo(WarrantyInfo info);
}
