package com.utils.wechat;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.myweb.vo.wechat.WeChatToken;
import com.myweb.vo.wechat.WeChatUserInfo;

@Component
public class WeChatUtil
{
    private static Logger log = LoggerFactory.getLogger(WeChatUtil.class);

    @Value("${tokenUrl}")
    public String TOKEN_URL;

    @Value("${userInfoUrl}")
    public String USER_INFO_URL;

    /**
     * 发送https请求
     * 
     * @param requestUrl 请求地址
     * @param requestMethod 请求方式（GET、POST）
     * @param outputStr 提交的数据
     * @return JSONObject(通过JSONObject.get(key)的方式获取json对象的属性值)
     */
    public JSONObject httpsRequest(String requestUrl, String requestMethod, String outputStr)
    {
        JSONObject jsonObject = null;
        try
        {
            // 创建SSLContext对象，并使用我们指定的信任管理器初始化
            TrustManager[] tm = {new BookStoreX509TrustManager()};
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, tm, new java.security.SecureRandom());
            // 从上述SSLContext对象中得到SSLSocketFactory对象
            SSLSocketFactory ssf = sslContext.getSocketFactory();

            URL url = new URL(requestUrl);
            HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
            conn.setSSLSocketFactory(ssf);

            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            // 设置请求方式（GET/POST）
            conn.setRequestMethod(requestMethod);

            // 当outputStr不为null时向输出流写数据
            if (null != outputStr)
            {
                OutputStream outputStream = conn.getOutputStream();
                // 注意编码格式
                outputStream.write(outputStr.getBytes("UTF-8"));
                outputStream.close();
            }

            // 从输入流读取返回内容
            InputStream inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            StringBuffer buffer = new StringBuffer();
            while ((str = bufferedReader.readLine()) != null)
            {
                buffer.append(str);
            }

            // 释放资源
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            inputStream = null;
            conn.disconnect();
            jsonObject = JSONObject.parseObject(buffer.toString());
        }
        catch (ConnectException ce)
        {
            log.error("连接超时：{}", ce);
        }
        catch (Exception e)
        {
            log.error("https请求异常：{}", e);
        }
        return jsonObject;
    }

    /**
     * 获取接口访问凭证
     * 
     * @param appid 凭证
     * @param appsecret 密钥
     * @return
     */
    public WeChatToken getToken(String appid, String appsecret)
    {
        WeChatToken token = null;
        String requestUrl = TOKEN_URL.replace("APPID", appid).replace("APPSECRET", appsecret);
        // 发起GET请求获取凭证
        JSONObject jsonObject = httpsRequest(requestUrl, "GET", null);

        if (null != jsonObject)
        {
            try
            {
                token = new WeChatToken();
                token.setAccessToken(jsonObject.getString("access_token"));
                token.setExpiresIn(jsonObject.getIntValue("expires_in"));
            }
            catch (JSONException e)
            {
                token = null;
                // 获取token失败
                log.error("获取token失败 errcode:{} errmsg:{}", jsonObject.getIntValue("errcode"),
                    jsonObject.getString("errmsg"));
            }
        }
        return token;
    }

    /**
     * URL编码（utf-8）
     * 
     * @param source
     * @return
     */
    public String urlEncodeUTF8(String source)
    {
        String result = source;
        try
        {
            result = java.net.URLEncoder.encode(source, "utf-8");
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 根据内容类型判断文件扩展名
     * 
     * @param contentType 内容类型
     * @return
     */
    public String getFileExt(String contentType)
    {
        String fileExt = "";
        if ("image/jpeg".equals(contentType))
            fileExt = ".jpg";
        else if ("audio/mpeg".equals(contentType))
            fileExt = ".mp3";
        else if ("audio/amr".equals(contentType))
            fileExt = ".amr";
        else if ("video/mp4".equals(contentType))
            fileExt = ".mp4";
        else if ("video/mpeg4".equals(contentType))
            fileExt = ".mp4";
        return fileExt;
    }

    /**
     * 获取用户信息
     * 
     * @param accessToken 接口访问凭证
     * @param openId 用户标识
     * @return WeixinUserInfo
     */
    public WeChatUserInfo getUserInfo(String accessToken, String openId)
    {
        WeChatUserInfo WeChatUserInfo = null;
        // 拼接请求地址
        String requestUrl =
            USER_INFO_URL.replace("ACCESS_TOKEN", accessToken).replace("OPENID", openId);
        // 获取用户信息
        JSONObject jsonObject = httpsRequest(requestUrl, "GET", null);

        if (null != jsonObject)
        {
            try
            {
                WeChatUserInfo = new WeChatUserInfo();
                // 用户的标识
                WeChatUserInfo.setOpenId(jsonObject.getString("openid"));
                // 关注状态（1是关注，0是未关注），未关注时获取不到其余信息
                WeChatUserInfo.setSubscribe(jsonObject.getIntValue("subscribe"));
                // 用户关注时间
                WeChatUserInfo.setSubscribeTime(jsonObject.getString("subscribe_time"));
                // 昵称
                WeChatUserInfo.setNickname(jsonObject.getString("nickname"));
                // 用户的性别（1是男性，2是女性，0是未知）
                WeChatUserInfo.setSex(jsonObject.getIntValue("sex"));
                // 用户所在国家
                WeChatUserInfo.setCountry(jsonObject.getString("country"));
                // 用户所在省份
                WeChatUserInfo.setProvince(jsonObject.getString("province"));
                // 用户所在城市
                WeChatUserInfo.setCity(jsonObject.getString("city"));
                // 用户的语言，简体中文为zh_CN
                WeChatUserInfo.setLanguage(jsonObject.getString("language"));
                // 用户头像
                WeChatUserInfo.setHeadImgUrl(jsonObject.getString("headimgurl"));
            }
            catch (Exception e)
            {
                if (0 == WeChatUserInfo.getSubscribe())
                {
                    log.error("用户{}已取消关注", WeChatUserInfo.getOpenId());
                }
                else
                {
                    int errorCode = jsonObject.getIntValue("errcode");
                    String errorMsg = jsonObject.getString("errmsg");
                    log.error("获取用户信息失败 errcode:{} errmsg:{}", errorCode, errorMsg);
                }
            }
        }
        return WeChatUserInfo;
    }
}
