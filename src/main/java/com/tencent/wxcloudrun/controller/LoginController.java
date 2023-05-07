package com.tencent.wxcloudrun.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dto.LoginDto;
import com.tencent.wxcloudrun.model.User;
import com.tencent.wxcloudrun.service.UserService;
import com.tencent.wxcloudrun.utils.WechatUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wx")
public class LoginController {

    @Value("${wx.appid}")
    private String appid;
    @Value("${wx.appSecret}")
    private String appSecret;
    @Autowired
    private UserService userService;


    @PostMapping("/login")
    public ApiResponse login(@RequestBody LoginDto loginDto) {
        // 用户非敏感信息：rawData
        // 签名：signature
        JSONObject rawDataJson = JSON.parseObject(loginDto.getRawData());
        // 1.接收小程序发送的code
        // 2.开发者服务器 登录凭证校验接口 appid + appSecret + code
        JSONObject SessionKeyOpenId = WechatUtil.getSessionKeyOrOpenId(loginDto.getCode(),appid,appSecret);
        // 3.接收微信接口服务 获取返回的参数
        String openid = SessionKeyOpenId.getString("openid");
        String sessionKey = SessionKeyOpenId.getString("session_key");

        // 4.校验签名 小程序发送的签名signature与服务器端生成的签名signature2 = sha1(rawData + sessionKey)
        String signature2 = DigestUtils.sha1Hex(loginDto.getRawData() + sessionKey);
        if (!loginDto.getSignature().equals(signature2)) {
            return ApiResponse.error("签名校验失败");
        }
        // 5.根据返回的User实体类，判断用户是否是新用户，是的话，将用户信息存到数据库；
        LambdaQueryWrapper<User> lqw = Wrappers.lambdaQuery();
        lqw.eq(User::getOpenId, openid);
        User user = userService.getOne(lqw);
        if (user == null) {
            // 用户信息入库
            String nickName = rawDataJson.getString("nickName");
            String avatarUrl = rawDataJson.getString("avatarUrl");
            user = new User();
            user.setOpenId(openid);
            user.setAvatar(avatarUrl);
            user.setNickName(nickName);
            userService.save(user);
        }
        return ApiResponse.ok(user);
    }


}
