package com.seblong.okr.utils;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.client.RestTemplate;

import com.seblong.okr.entities.Employee;

public class OAuth2Util {

    protected static final Logger logger = LoggerFactory.getLogger(OAuth2Util.class);

    /**
     * 根据URL规范，将上述参数分别进行UrlEncode，得到拼接的OAuth2链接为：
     * https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxCorpId&redirect_uri=http%3a%2f%2fapi.3dept.com%2fcgi-bin%2fquery%3faction%3dget&response_type=code&scope=snsapi_base&state=#wechat_redirect
     * @return
     */
    public static String getOAuth2Url(String authUrl, String appId, String redirectUri){
        @SuppressWarnings("deprecation")
		String oauth2Url = authUrl + "?appid=" + appId + "&redirect_uri=" + URLEncoder.encode(redirectUri) + "&response_type=code&scope=snsapi_privateinfo&state=login#wechat_redirect";
        return oauth2Url;
    }

    /**
     * 获取企业微信access_token
     * @param restTemplate
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked", "unused" })
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
                throw new RuntimeException(String.format("get wechat accessToken error, errorCode : %d, errMsg : %s", errCode, resObj.getString("errmsg")));
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
    @SuppressWarnings({ "rawtypes" })
    public static String getUserId(RestTemplate restTemplate, RedisTemplate redisTemplate, String accessToken, String code, String appId, String secret, String suiteTicketKey, String suiteTokenUrl, String suiteTokenKey){
        String url = "https://qyapi.weixin.qq.com/cgi-bin/service/getuserinfo3rd?suite_access_token=" + accessToken + "&code=" + code;
        String response = HttpUtil.get(restTemplate, url, String.class);
        JSONObject resObj = new JSONObject(response);
        System.out.println("code==" + code + "===result=" + resObj);
        int errCode = resObj.getInt("errcode");
        if(errCode == 0){
            if(resObj.has("UserId")) {
                return resObj.getString("UserId");
            }else {
                throw new RuntimeException(String.format("user not in group, OpenId : %s", resObj.getString("OpenId")));
            }
        }else if(errCode == 40014){
            return getUserId(restTemplate, redisTemplate, refreshSuiteToken(restTemplate, redisTemplate, appId, secret, suiteTicketKey, suiteTokenUrl, suiteTokenKey), code, appId, secret, suiteTicketKey, suiteTokenUrl, suiteTokenKey);
        }else {
            throw new RuntimeException(String.format("get wechat userId error, errorCode : %d, errMsg : %s", errCode, resObj.getString("errmsg")));
        }
    }

    /**
     * 由于第三方服务商可能托管了大量的企业，其安全问题造成的影响会更加严重，故API中除了合法来源IP校验之外，还额外增加了suite_ticket作为安全凭证。
     * 获取suite_access_token时，需要suite_ticket参数。suite_ticket由企业微信后台定时推送给“指令回调URL”，每十分钟更新一次，见推送suite_ticket。
     * suite_ticket实际有效期为30分钟，可以容错连续两次获取suite_ticket失败的情况，但是请永远使用最新接收到的suite_ticket。
     * 通过本接口获取的suite_access_token有效期为2小时，开发者需要进行缓存，不可频繁获取。
     * @return
     */
    public static String refreshSuiteToken(RestTemplate restTemplate, RedisTemplate redisTemplate, String appId, String secret, String suiteTicketKey, String suiteTokenUrl, String suiteTokenKey){
        Map<String, Object> params = new HashMap<>(4);
        params.put("suite_id", appId);
        params.put("suite_secret", secret);
        Object ticket = redisTemplate.opsForValue().get(suiteTicketKey);
        if(ticket == null){
            logger.error("suite_ticket未获得到，需要手动刷新");
            return null;
        }
        params.put("suite_ticket", ticket.toString());
        String result = HttpUtil.post(restTemplate, suiteTokenUrl, params, String.class);
        JSONObject json = new JSONObject(result);
        if(json.has("errcode") && json.getInt("errcode") != 0){
            logger.error("获取第三方应用凭证出错：errcode：" + json.getInt("errcode") + "，errmsg：" + json.getString("errmsg"));
            return null;
        }
        redisTemplate.opsForValue().set(suiteTokenKey, json.getString("suite_access_token"), json.getInt("expires_in") - 5, TimeUnit.SECONDS);
        logger.info("获取服务商凭证完成：suite_access_token：" + json.getString("suite_access_token"));
        return json.getString("suite_access_token");
    }

}
