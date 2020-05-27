package com.seblong.okr.services.impl;

import com.seblong.okr.entities.Company;
import com.seblong.okr.entities.Employee;
import com.seblong.okr.repositories.CompanyRepository;
import com.seblong.okr.services.CompanyService;
import com.seblong.okr.services.CorpService;
import com.seblong.okr.services.OKRService;
import com.seblong.okr.utils.HttpRequestUtil;
import com.seblong.okr.utils.SecuritySHA1Utils;
import com.seblong.okr.utils.StringUtil;
import com.seblong.okr.utils.XmlUtil;
import com.seblong.okr.utils.wx.AesException;
import com.seblong.okr.utils.wx.WXBizMsgCrypt;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class CorpServiceImpl implements CorpService {

    protected static final Logger logger = LoggerFactory.getLogger(CorpServiceImpl.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private CompanyRepository companyRepo;

    @Value("${snail.okr.wechat.suite_id}")
    private String suite_id;

    @Value("${snail.okr.wechat.suite_secret}")
    private String suite_secret;

    @Value("${snail.okr.wechat.corpId}")
    private String corpId;

    @Value("${snail.okr.wechat.provider_secret}")
    private String providerSecret;

    @Value("${snail.okr.wechat.suite_token}")
    private String token;

    @Value("${snail.okr.wechat.suite_encodingAesKey}")
    private String encodingAesKey;

    private final String SUITE_TICKET_KEY = "SUITE_TICKET_KEY";

    private final String PROVIDER_ACCESS_TOKEN_KEY = "PROVIDER_ACCESS_TOKEN_KEY";

    private final String SUITE_TOKEN_KEY = "SUITE_ACCESS_TOKEN_KEY";

    private final String CORP_TOKEN_KEY = "CORP_ACCESS_TOKEN_KEY";

    private final String JSAPI_CORP_TICKET_KEY = "JSAPI_CORP_TICKET_KEY";

    private final String JSAPI_AGENT_TICKET_KEY = "JSAPI_AGENT_TICKET_KEY";

    /**
     * 获取服务商凭证URL
     */
    @Value("${snail.okr.wechat.url.providerToken}")
    private String provider_token_url;

    /**
     * 获取第三方应用凭证URL
     */
    @Value("${snail.okr.wechat.url.suite_token}")
    private String suite_token_url;

    /**
     * 获取企业永久授权码URL
     */
    @Value("${snail.okr.wechat.url.permanent_code}")
    private String permanent_code_url;

    /**
     * 获取企业授权信息URL
     */
    @Value("${snail.okr.wechat.url.auth_info}")
    private String auth_info_url;

    @Value("${snail.okr.wechat.url.corp_token}")
    private String corp_token_url;

    @Value("${snail.okr.wechat.url.js_api_corp_ticket}")
    private String js_api_corp_ticket_url;

    @Value("${snail.okr.wechat.url.js_api_agent_ticket}")
    private String js_api_agent_ticket_url;

    private WXBizMsgCrypt init(String receiveId) throws AesException {
        return new WXBizMsgCrypt(token, encodingAesKey, receiveId);
    }

    @Override
    public String getVerify(String msg_signature, String timestamp, String nonce, String echostr) throws AesException {
        WXBizMsgCrypt wxBizMsgCrypt = init(corpId);
        String result = wxBizMsgCrypt.VerifyURL(msg_signature, timestamp, nonce, echostr);
        return result;
    }

    @Override
    public String refreshData(String msg_signature, String timestamp, String nonce, String postData) throws AesException {
        Map<String, Object> postDataMap = XmlUtil.getXmlBodyContext(postData);
        String receiveId = postDataMap.get("ToUserName") + "";
        WXBizMsgCrypt wxBizMsgCrypt = init(StringUtils.isEmpty(receiveId) ? corpId : receiveId);
        logger.info(postData);
        String result = wxBizMsgCrypt.DecryptMsg(msg_signature, timestamp, nonce, postData);
        Map<String, Object> dataMap = XmlUtil.getXmlBodyContext(result);
        logger.info("解密后的数据：" + dataMap);
        if(dataMap.containsKey("InfoType")){
            String infoType = dataMap.get("InfoType").toString();
            if("suite_ticket".equals(infoType)){    //推送suite_ticket
                redisTemplate.opsForValue().set(SUITE_TICKET_KEY, dataMap.get("SuiteTicket"));
            }else if("create_auth".equals(infoType)){    //授权通知事件
                logger.info("收到授权通知，receiveId:" + receiveId);
                String response = getPermanentCode(dataMap.get("AuthCode").toString());
                if(StringUtils.isEmpty(response)){
                    return "fail";
                }
            }else if("change_auth".equals(infoType)){     //变更授权通知事件
                String authCorpId = dataMap.get("AuthCorpId").toString();
                Company company = companyRepo.findByCorpId(authCorpId);
                if(company != null){
                    getAuthInfo(authCorpId, company);
                }
            }else if("cancel_auth".equals(infoType)){       //取消授权通知事件，删除企业所有相关信息
                String authCorpId = dataMap.get("AuthCorpId").toString();
                Company company = companyRepo.findByCorpId(authCorpId);
                if(company != null){
                    companyService.cleanData(company.getId().toString());
                }
            }
        }
        return "success";
    }

    /**
     * 根据临时授权码获取企业永久授权码及企业授权信息
     * @param preAuthCode
     * @return
     */
    private String getPermanentCode(String preAuthCode){
//        Map<String, Object> params_getPreAuthCode = new HashMap<>(2);
//        params_getPreAuthCode.put("suite_access_token", refreshSuiteToken());
        String url = permanent_code_url + "?suite_access_token=%s";
        url = String.format(url, refreshSuiteToken());
        JSONObject body = new JSONObject();
        body.put("auth_code", preAuthCode);
        String result = null;
        try {
            result = HttpRequestUtil.sendPost(url, body.toString());
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("获取企业授权码错误 errmsg:" + e.getMessage());
        }
        logger.info(result);
        JSONObject json = new JSONObject(result);
        if(json.has("access_token")){
            String accessToken = json.getString("access_token");
            Long expires_in = json.getLong("expires_in");
            String permanent_code = json.getString("permanent_code");
            JSONObject authCorpInfo = json.getJSONObject("auth_corp_info");
            String corpId = authCorpInfo.getString("corpid");
            redisTemplate.opsForValue().set(CORP_TOKEN_KEY + "_" + corpId, accessToken, expires_in - 5*60, TimeUnit.SECONDS);
            String corp_name = authCorpInfo.getString("corp_name");
            String corp_type = authCorpInfo.getString("corp_type");
            String corp_square_logo_url = authCorpInfo.getString("corp_square_logo_url");
            String corp_full_name = authCorpInfo.getString("corp_full_name");
            JSONObject authInfo = json.getJSONObject("auth_info");
            JSONArray agents = authInfo.getJSONArray("agent");
            logger.info(agents.toString());
            JSONObject agent = agents.getJSONObject(0);
            logger.info(agent.toString());
            int agentId = agent.getInt("agentid");
            String agentName = agent.getString("name");
            String agentSquareLogoUrl = agent.has("square_logo_url") ? agent.getString("square_logo_url") : null;
            String agentRoundLogoUrl = agent.has("round_logo_url") ? agent.getString("round_logo_url") : null;
            JSONObject privilege = agent.getJSONObject("privilege");
            Integer level = privilege.getInt("level");
            JSONArray allowPartyArr =  privilege.getJSONArray("allow_party");
            List<Object> allowParty = allowPartyArr.toList();
            JSONArray allowUserArr = privilege.getJSONArray("allow_user");
            List<Object> allowUser = allowUserArr.toList();
            JSONArray allowTagArr = privilege.getJSONArray("allow_tag");
            List<Object> allowTag = allowTagArr.toList();
            JSONArray extraPartyArr = privilege.getJSONArray("extra_party");
            List<Object> extraParty = extraPartyArr.toList();
            JSONArray extraUserArr = privilege.getJSONArray("extra_user");
            List<Object> extraUser = extraUserArr.toList();
            JSONArray extraTagArr = privilege.getJSONArray("extra_tag");
            List<Object> extraTag = extraTagArr.toList();
            JSONObject authUserInfo = json.getJSONObject("auth_user_info");
            String authUserId = authUserInfo.getString("userid");
            String authUserName = authUserInfo.getString("name");
            String authUserAvatar = authUserInfo.getString("avatar");
            Company company = companyRepo.findByCorpId(corpId);
            if(company == null){
                company = new Company();
                company.setCorpId(corpId);
                company.setCorpSquareLogoUrl(corp_square_logo_url);
                company.setCreated(System.currentTimeMillis());
                company.setUpdated(System.currentTimeMillis());
                company.setEmployees(10);
                company.setEnd(System.currentTimeMillis() + 30L * 24 * 3600 * 1000);
                company.setFullName(corp_full_name);
                company.setName(corp_name);
                company.setPermanentCode(permanent_code);
                company.setStatus(0);
                company.setAgentId(agentId);
                company.setAgentName(agentName);
                company.setLevel(level);
                company.setAllowParty(allowParty);
                company.setAllowUser(allowUser);
                company.setAllowTag(allowTag);
                company.setExtraParty(extraParty);
                company.setExtraUser(extraUser);
                company.setExtraTag(extraTag);
                company.setAuthUserId(authUserId);
                company.setAuthUserName(authUserName);
                company.setAuthUserAvatar(authUserAvatar);
                company.setAgentRoundLogoUrl(agentRoundLogoUrl);
                company.setAgentSquareLogoUrl(agentSquareLogoUrl);
                companyRepo.save(company);
            }else {
                company.setPermanentCode(permanent_code);
                company.setCorpSquareLogoUrl(corp_square_logo_url);
                company.setUpdated(System.currentTimeMillis());
                company.setFullName(corp_full_name);
                company.setName(corp_name);
                company.setAgentId(agentId);
                company.setAgentName(agentName);
                company.setLevel(level);
                company.setAllowParty(allowParty);
                company.setAllowUser(allowUser);
                company.setAllowTag(allowTag);
                company.setExtraParty(extraParty);
                company.setExtraUser(extraUser);
                company.setExtraTag(extraTag);
                company.setAuthUserId(authUserId);
                company.setAuthUserName(authUserName);
                company.setAuthUserAvatar(authUserAvatar);
                company.setAgentRoundLogoUrl(agentRoundLogoUrl);
                company.setAgentSquareLogoUrl(agentSquareLogoUrl);
                companyRepo.save(company);
            }
            return permanent_code;
        } else {
            logger.error("获取企业授权码错误，errcode:" + json.get("errcode") + ", errmsg:" + json.get("errmsg"));
        }
        return null;
    }

    /**
     * 获取企业授权信息
     * @param corpId
     * @param company
     */
    private void getAuthInfo(String corpId, Company company){
        String url = auth_info_url + "?suite_access_token=%s";
        url = String.format(url, refreshSuiteToken());
        JSONObject params = new JSONObject();
//        params.put("suite_access_token", refreshSuiteToken());
        params.put("auth_corpid", corpId);
        params.put("permanent_code", company.getPermanentCode());
        String result = null;
        try {
            result = HttpRequestUtil.sendPost(url, params.toString());
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("获取企业授权信息错误 errmsg:" + e.getMessage());
            return;
        }
        JSONObject json = new JSONObject(result);
        if(json.has("errcode") && json.getInt("errcode") == 0){
            String permanent_code = json.getString("permanent_code");
            JSONObject authCorpInfo = json.getJSONObject("auth_corp_info");
            String corp_name = authCorpInfo.getString("corp_name");
            String corp_type = authCorpInfo.getString("corp_type");
            String corp_square_logo_url = authCorpInfo.getString("corp_square_logo_url");
            String corp_full_name = authCorpInfo.getString("corp_full_name");
            JSONObject authInfo = json.getJSONObject("auth_info");
            JSONArray agents = authInfo.getJSONArray("agent");
            JSONObject agent = agents.getJSONObject(0);
            int agentId = agent.getInt("agentid");
            String agentName = agent.getString("name");
            String agentSquareLogoUrl = agent.getString("square_logo_url");
            String agentRoundLogoUrl = agent.getString("roundLogoUrl");
            JSONObject privilege = agent.getJSONObject("privilege");
            Integer level = privilege.getInt("level");
            JSONArray allowPartyArr =  privilege.getJSONArray("allow_party");
            List<Object> allowParty = allowPartyArr.toList();
            JSONArray allowUserArr = privilege.getJSONArray("allow_user");
            List<Object> allowUser = allowUserArr.toList();
            JSONArray allowTagArr = privilege.getJSONArray("allow_tag");
            List<Object> allowTag = allowTagArr.toList();
            JSONArray extraPartyArr = privilege.getJSONArray("extra_party");
            List<Object> extraParty = extraPartyArr.toList();
            JSONArray extraUserArr = privilege.getJSONArray("extra_user");
            List<Object> extraUser = extraUserArr.toList();
            JSONArray extraTagArr = privilege.getJSONArray("extra_tag");
            List<Object> extraTag = extraTagArr.toList();
            company.setPermanentCode(permanent_code);
            company.setCorpSquareLogoUrl(corp_square_logo_url);
            company.setUpdated(System.currentTimeMillis());
            company.setFullName(corp_full_name);
            company.setName(corp_name);
            company.setAgentId(agentId);
            company.setAgentName(agentName);
            company.setAgentRoundLogoUrl(agentRoundLogoUrl);
            company.setAgentSquareLogoUrl(agentSquareLogoUrl);
            company.setLevel(level);
            company.setAllowParty(allowParty);
            company.setAllowUser(allowUser);
            company.setAllowTag(allowTag);
            company.setExtraParty(extraParty);
            company.setExtraUser(extraUser);
            company.setExtraTag(extraTag);
            companyRepo.save(company);
        }
    }

    /**
     * 获取或刷新服务商凭证
     * 开发者需要缓存provider_access_token，用于后续接口的调用（注意：不能频繁调用get_provider_token接口，否则会受到频率拦截）。当provider_access_token失效或过期时，需要重新获取。
     *
     * provider_access_token的有效期通过返回的expires_in来传达，正常情况下为7200秒（2小时），有效期内重复获取返回相同结果，过期后获取会返回新的provider_access_token。
     * provider_access_token至少保留512字节的存储空间。
     * 企业微信可能会出于运营需要，提前使provider_access_token失效，开发者应实现provider_access_token失效时重新获取的逻辑。
     * @return
     */
    @Override
    public String refreshProviderAccessToken(){
        Object accessTokenObj = redisTemplate.opsForValue().get(PROVIDER_ACCESS_TOKEN_KEY);
        if(accessTokenObj != null){
            return String.valueOf(accessTokenObj);
        }
        JSONObject params = new JSONObject();
        params.put("corpid", corpId);
        params.put("provider_secret", providerSecret);
        String result = null;
        try {
            result = HttpRequestUtil.sendPost(provider_token_url, params.toString());
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("获取服务商凭证错误 errmsg:" + e.getMessage());
        }
        JSONObject json = new JSONObject(result);
        if(json.has("errcode") && json.getInt("errcode") != 0){
            logger.error("获取服务商凭证出错：errcode：" + json.getInt("errcode") + "，errmsg：" + json.getString("errmsg"));
            return null;
        }
        redisTemplate.opsForValue().set(PROVIDER_ACCESS_TOKEN_KEY, json.getString("provider_access_token"), json.getInt("expires_in") - 5*60, TimeUnit.SECONDS);
        logger.info("获取服务商凭证完成：provider_access_token：" + json.getString("provider_access_token"));
        return json.getString("provider_access_token");
    }

    /**
     * 作废服务商凭证
     */
    @Override
    public void disableProviderAccessToken(){
        redisTemplate.delete(PROVIDER_ACCESS_TOKEN_KEY);
    }

    /**
     * 由于第三方服务商可能托管了大量的企业，其安全问题造成的影响会更加严重，故API中除了合法来源IP校验之外，还额外增加了suite_ticket作为安全凭证。
     * 获取suite_access_token时，需要suite_ticket参数。suite_ticket由企业微信后台定时推送给“指令回调URL”，每十分钟更新一次，见推送suite_ticket。
     * suite_ticket实际有效期为30分钟，可以容错连续两次获取suite_ticket失败的情况，但是请永远使用最新接收到的suite_ticket。
     * 通过本接口获取的suite_access_token有效期为2小时，开发者需要进行缓存，不可频繁获取。
     * @return
     */
    @Override
    public String refreshSuiteToken(){
        Object val = redisTemplate.opsForValue().get(SUITE_TOKEN_KEY + "_" + suite_id);
        if(val != null){
            return val.toString();
        }
        JSONObject params = new JSONObject();
        params.put("suite_id", suite_id);
        params.put("suite_secret", suite_secret);
        Object ticket = redisTemplate.opsForValue().get(SUITE_TICKET_KEY);
        if(ticket == null){
            logger.error("suite_ticket未获得到，需要手动刷新");
            return null;
        }
        params.put("suite_ticket", ticket.toString());
        String result = null;
        try {
            result = HttpRequestUtil.sendPost(suite_token_url, params.toString());
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("获取第三方应用凭证错误 errmsg:" + e.getMessage());
        }
        JSONObject json = new JSONObject(result);
        if(json.has("errcode") && json.getInt("errcode") != 0){
            logger.error("获取第三方应用凭证出错：errcode：" + json.getInt("errcode") + "，errmsg：" + json.getString("errmsg"));
            return null;
        }
        redisTemplate.opsForValue().set(SUITE_TOKEN_KEY + "_" + suite_id, json.getString("suite_access_token"), json.getInt("expires_in") - 5*60, TimeUnit.SECONDS);
        logger.info("获取第三方应用凭证完成：suite_access_token：" + json.getString("suite_access_token"));
        return json.getString("suite_access_token");
    }

    private void cleanSuiteToken(){
        redisTemplate.delete(SUITE_TOKEN_KEY + "_" + suite_id);
    }

    private void cleanCorpToken(String authCorpId){
        redisTemplate.delete(CORP_TOKEN_KEY + "_" + authCorpId);
    }

    /**
     * 第三方服务商在取得企业的永久授权码后，通过此接口可以获取到企业的access_token。
     * 获取后可通过通讯录、应用、消息等企业接口来运营这些应用。
     * @param authCorpId
     * @param permanentCode
     * @return
     */
    @Override
    public String getCorpToken(String authCorpId, String permanentCode){
        Object val = redisTemplate.opsForValue().get(CORP_TOKEN_KEY + "_" + authCorpId);
        if(val != null){
            return val.toString();
        }
        String url = corp_token_url + "?suite_access_token=%s";
        url = String.format(url, refreshSuiteToken());
        JSONObject params = new JSONObject();
        params.put("auth_corpid", authCorpId);
        params.put("permanent_code", permanentCode);
        String result = null;
        try {
            result = HttpRequestUtil.sendPost(url, params.toString());
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("获取企业凭证出错：errmsg：" + e.getMessage());
        }
        JSONObject json = new JSONObject(result);
        if(json.has("errcode") && json.getInt("errcode") != 0){
            logger.error("获取企业凭证出错：errcode：" + json.getInt("errcode") + "，errmsg：" + json.getString("errmsg"));
            return null;
        }
        redisTemplate.opsForValue().set(CORP_TOKEN_KEY + "_" + authCorpId, json.getString("access_token"), json.getInt("expires_in") - 5*60, TimeUnit.SECONDS);
        logger.info("获取企业凭证完成：corp_access_token：" + json.getString("access_token"));
        return json.getString("access_token");
    }

    @Override
    public String getJSAPICorpTicket(String authCorpId, String permanentCode){
        Object val = redisTemplate.opsForValue().get(JSAPI_CORP_TICKET_KEY + "_" + authCorpId);
        if(val != null){
            return val.toString();
        }
        String accessToken = getCorpToken(authCorpId, permanentCode);
        String result = null;
        try {
            result = HttpRequestUtil.sendGet(js_api_corp_ticket_url, "access_token=" + accessToken);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("获取JSAPI企业凭证出错：errmsg：" + e.getMessage());
        }
        JSONObject json = new JSONObject(result);
        if(!json.has("ticket")){
            logger.error("获取JSAPI企业凭证出错：errcode：" + json.getInt("errcode") + "，errmsg：" + json.getString("errmsg"));
            return null;
        }
        redisTemplate.opsForValue().set(JSAPI_CORP_TICKET_KEY + "_" + authCorpId, json.getString("ticket"), json.getInt("expires_in") - 5*60, TimeUnit.SECONDS);
        logger.info("获取JSAPI企业凭证完成：JSAPI_CORP_TICKET：" + json.getString("ticket"));
        return json.getString("ticket");
    }


    @Override
    public String getJSAPIAgentTicket(String authCorpId){
        Object val = redisTemplate.opsForValue().get(JSAPI_AGENT_TICKET_KEY + "_" + authCorpId);
        if(val != null){
            return val.toString();
        }
        Company company = companyRepo.findByCorpId(authCorpId);
        String accessToken = getCorpToken(authCorpId, company.getPermanentCode());
        logger.info(accessToken);
        String result = null;
        try {
            result = HttpRequestUtil.sendGet(js_api_agent_ticket_url, "access_token=" + accessToken + "&type=agent_config");

            JSONObject json = new JSONObject(result);
            if(!json.has("ticket")){
                cleanCorpToken(authCorpId);
                logger.error("获取JSAPI应用凭证出错：errcode：" + json.getInt("errcode") + "，errmsg：" + json.getString("errmsg"));
                result = HttpRequestUtil.sendGet(js_api_agent_ticket_url, "access_token=" + getCorpToken(authCorpId, company.getPermanentCode()) + "&type=agent_config");
                json = new JSONObject(result);
                redisTemplate.opsForValue().set(JSAPI_AGENT_TICKET_KEY + "_" + authCorpId, json.getString("ticket"), json.getInt("expires_in") - 5*60, TimeUnit.SECONDS);
                logger.info("获取JSAPI应用凭证完成：JSAPI_AGENT_TICKET：" + json.getString("ticket"));
                return json.getString("ticket");
            }
            redisTemplate.opsForValue().set(JSAPI_AGENT_TICKET_KEY + "_" + authCorpId, json.getString("ticket"), json.getInt("expires_in") - 5*60, TimeUnit.SECONDS);
            logger.info("获取JSAPI应用凭证完成：JSAPI_AGENT_TICKET：" + json.getString("ticket"));
            return json.getString("ticket");
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("获取JSAPI应用凭证出错：errmsg：" + e.getMessage());
            return null;
        }
    }

    @Override
    public Map<String, Object> jsSign(String url, String authCorpId, String permanentCode, long timestamp, String type){
        Map<String, Object> rMap = new HashMap<>(4);
        String noncestr = StringUtil.randomNumStr(16);
        rMap.put("noncestr", noncestr);
        String jsapi_ticket = "";
        if("suite".equals(type)){
            jsapi_ticket = getJSAPIAgentTicket(authCorpId);
            rMap.put("jsapi_ticket", jsapi_ticket);
        }else {
            jsapi_ticket = getJSAPICorpTicket(authCorpId, permanentCode);
            rMap.put("jsapi_ticket", jsapi_ticket);
        }
        rMap.put("url", url);
        rMap.put("timestamp", timestamp);
        String signStr = "jsapi_ticket=" + jsapi_ticket + "&noncestr=" + noncestr + "&timestamp=" + timestamp + "&url=" + url;
        try {
            String sign = SecuritySHA1Utils.shaEncode(signStr);
            rMap.put("sign", sign);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return rMap;
    }

}
