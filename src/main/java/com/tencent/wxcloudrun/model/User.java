package com.tencent.wxcloudrun.model;

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
@TableName("wx_user")
public class User {
    @TableId(type = IdType.AUTO)
    private String id;
    @TableField("open_id")
    private String openId;
    private String avatar;
    private String nickname;
    private Boolean isAdmin;
}
