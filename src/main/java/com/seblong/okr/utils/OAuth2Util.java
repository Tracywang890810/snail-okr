package com.seblong.okr.utils;

import com.seblong.okr.entities.Employee;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OAuth2Util {

    /**
     * 根据URL规范，将上述参数分别进行UrlEncode，得到拼接的OAuth2链接为：
     * https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxCorpId&redirect_uri=http%3a%2f%2fapi.3dept.com%2fcgi-bin%2fquery%3faction%3dget&response_type=code&scope=snsapi_base&state=#wechat_redirect
     * @return
     */
    public static String getOAuth2Url(String authUrl, String appId, String redirectUri){
        String oauth2Url = authUrl + "?appid=" + appId + "&redirect_uri=" + URLEncoder.encode(redirectUri) + "&response_type=code&scope=snsapi_base&state=login#wechat_redirect";
        return oauth2Url;
    }

    /**
     * 获取企业微信access_token
     * @param restTemplate
     * @return
     */
    public static String getAccessToken(RestTemplate restTemplate, RedisTemplate redisTemplate, String appId, String secret){
        String accessToken = null;
        Object value = redisTemplate.opsForValue().get(appId);
        if(value != null){
            accessToken = String.valueOf(value);
        }else {
            String url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid="+appId+"&corpsecret=" + secret;
            String response = HttpUtil.get(restTemplate, url, String.class);
            Map<String, Object> rMap = new HashMap<>(4);
            JSONObject resObj = new JSONObject(response);
            int errCode = resObj.getInt("errcode");
            if(errCode == 0){
                accessToken = resObj.getString("access_token");
                redisTemplate.opsForValue().set(appId, accessToken, resObj.getInt("expires_in"), TimeUnit.SECONDS);
            }else {
                throw new RuntimeException(String.format("get wechat accessToken error, errorCode : %d, errMsg : %d", errCode, resObj.getString("errmsg")));
            }
        }
        return accessToken;
    }

    /**
     * 获取用户身份
     * @param restTemplate
     * @param accessToken
     * @param code
     * @return
     */
    public static String getUserId(RestTemplate restTemplate, RedisTemplate redisTemplate, String accessToken, String code, String appId, String secret){
        String url = "https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo?access_token=" + accessToken + "&code=" + code;
        String response = HttpUtil.get(restTemplate, url, String.class);
        JSONObject resObj = new JSONObject(response);
        int errCode = resObj.getInt("errcode");
        if(errCode == 0){
            if(resObj.has("UserId")) {
                return resObj.getString("UserId");
            }else {
                throw new RuntimeException(String.format("user not in group, OpenId : %d", resObj.getString("OpenId")));
            }
        }else if(errCode == 40014){
            return getUserId(restTemplate, redisTemplate, getAccessToken(restTemplate, redisTemplate, appId, secret), code, appId, secret);
        }else {
            throw new RuntimeException(String.format("get wechat userId error, errorCode : %d, errMsg : %d", errCode, resObj.getString("errmsg")));
        }
    }

    /**
     * 根据用户id获取用户信息
     * @param restTemplate
     * @param accessToken
     * @param userId
     * @return
     */
    public static Employee getUserInfo(RestTemplate restTemplate, RedisTemplate redisTemplate, String accessToken, String userId, String appId, String secret){
        String url = "https://qyapi.weixin.qq.com/cgi-bin/user/get?access_token=" + accessToken + "&userid=" + userId;
        String response = HttpUtil.get(restTemplate, url, String.class);
        JSONObject resObj = new JSONObject(response);
        int errCode = resObj.getInt("errcode");
        if(errCode == 0){
            Employee employee = new Employee();
            employee.setAddress(resObj.getString("address"));
            employee.setAvatar(resObj.getString("avatar"));
            employee.setEmail(resObj.getString("email"));
            employee.setEnable(resObj.getInt("enable"));
            employee.setGender(resObj.getString("gender"));
            employee.setMobile(resObj.getString("mobile"));
            employee.setName(resObj.getString("name"));
            employee.setPosition(resObj.getString("position"));
            employee.setStatus(resObj.getInt("status"));
            employee.setTelephone(resObj.getString("telephone"));
            employee.setThumb_avatar(resObj.getString("thumb_avatar"));
            employee.setUserId(resObj.getString("userId"));
            return employee;
        }else if(errCode == 40014){
            return getUserInfo(restTemplate, redisTemplate, getAccessToken(restTemplate, redisTemplate, appId, secret), userId, appId, secret);
        }
        return null;
    }
}
