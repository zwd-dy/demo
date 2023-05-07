package com.tencent.wxcloudrun.dto;

import lombok.Data;

@Data
public class LoginDto {
    private String code;
    private String rawData;
    private String signature;
}
