package com.tencent.wxcloudrun.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("warranty_info")
public class WarrantyInfo {
    @TableId(type = IdType.AUTO)
    private String id;
    /**
     * 用户姓名
     */
    private String nickname;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 车辆信息
     */
    private String carInfo;
    /**
     * 车牌号
     */
    private String carNumber;
    /**
     * 前挡型号
     */
    private String modelFront;
    /**
     * 后档型号
     */
    private String modelRear;
    /**
     * 侧挡型号
     */
    private String modelSide;
    /**
     * 天窗型号
     */
    private String modelSkylight;
    /**
     * 质保年限
     */
    private String period;
    /**
     * 施工日期
     */
    private Long buildDate;
    /**
     * 安装技师
     */
    private String technician;

}
