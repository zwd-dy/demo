package com.tencent.wxcloudrun.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "warranty_info",excludeProperty = {"cellStyleMap"})
public class WarrantyInfo extends BaseRowModel  {
    @TableId(type = IdType.AUTO)
    @ExcelProperty(value = {"序号"}, index = 0)
    private String id;
    /**
     * 用户姓名
     */
    @ExcelProperty(value = {"用户姓名"}, index = 1)
    private String nickname;
    /**
     * 手机号
     */
    @ExcelProperty(value = {"手机号"}, index = 2)
    private String phone;
    /**
     * 车辆信息
     */
    private String carInfo;
    /**
     * 车牌号
     */
    @ExcelProperty(value = {"车牌号"}, index = 4)

    private String carNumber;
    /**
     * 前挡型号
     */
    @ExcelProperty(value = {"前挡型号"}, index = 5)
    private String modelFront;
    /**
     * 后档型号
     */
    @ExcelProperty(value = {"后档型号"}, index = 6)
    private String modelRear;
    /**
     * 侧挡型号
     */
    @ExcelProperty(value = {"侧挡型号"}, index = 7)
    private String modelSide;
    /**
     * 天窗型号
     */
    @ExcelProperty(value = {"天窗型号"}, index = 8)
    private String modelSkylight;
    /**
     * 质保年限
     */
    @ExcelProperty(value = {"质保年限"}, index = 9)
    private String period;
    /**
     * 施工日期
     */
    @ExcelProperty(value = {"施工日期"}, index = 10)
    private Long buildDate;
    /**
     * 安装技师
     */
    @ExcelProperty(value = {"安装技师"}, index = 11)
    private String technician;

}
