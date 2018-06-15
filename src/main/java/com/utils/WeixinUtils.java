package com.utils;

import org.springframework.web.client.RestTemplate;

import java.util.ResourceBundle;

public class WeixinUtils {
    /**
     * 获取openId
     */
    public static String getOpenId(RestTemplate restTemplate, String code) {
        ResourceBundle resource = ResourceBundle.getBundle("weixinConfig");
        String requestUrl = "https://api.weixin.qq.com/sns/jscode2session?appid=APPID&secret=SECRET&js_code=JSCODE&grant_type=authorization_code";
        requestUrl = requestUrl.replace("APPID", resource.getString("appId"))
                .replace("SECRET", resource.getString("appSecret"))
                .replace("JSCODE", code)
                .replace("authorization_code", resource.getString("grant_type"));
        return restTemplate.getForObject(requestUrl, String.class);
    }

    /**
     * 得到微信用户信息
     */
    public String getUserInfo(RestTemplate restTemplate, String accessToken, String openId) {
        // 拼接请求地址
        String requestUrl = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID";
        requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken)
                .replace("OPENID", openId);
        // 获取用户信息
        return restTemplate.getForObject(requestUrl, String.class);
    }

}
