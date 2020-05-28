package com.seblong.okr.services.impl;

import com.seblong.okr.entities.Company;
import com.seblong.okr.entities.Employee;
import com.seblong.okr.entities.Follow;
import com.seblong.okr.repositories.CompanyRepository;
import com.seblong.okr.repositories.EmployeeRepository;
import com.seblong.okr.repositories.FollowRepository;
import com.seblong.okr.services.CorpService;
import com.seblong.okr.services.EmployeeService;
import com.seblong.okr.services.MessageService;
import com.seblong.okr.utils.HttpRequestUtil;
import com.seblong.okr.utils.HttpUtil;
import com.seblong.okr.utils.OAuth2Util;
import org.apache.http.client.utils.DateUtils;
import org.bson.types.ObjectId;
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

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepo;

    @Autowired
    private FollowRepository followRepo;

    @Autowired
    private CompanyRepository companyRepo;

    @Autowired
    private CorpService corpService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${snail.okr.wechat.suite_id}")
    private String appId;

    @Value("${snail.okr.wechat.url.auth}")
    private String authUrl;

    @Value("${snail.okr.wechat.url.search}")
    private String searchUrl;

    @Value("${snail.okr.wechat.url.redirect}")
    private String redirectUrl;

    /**
     * 获取访问用户idURL
     */
    @Value("${snail.okr.wechat.url.getuserinfo3rd}")
    private String getUserInfoUrl;

    /**
     * 获取访问用户敏感信息URL
     */
    @Value("${snail.okr.wechat.url.getuserdetail3rd}")
    private String getUserDetail3rdUrl;

    @Override
    public Employee getEmployee(String code, String cookie) {
        if(StringUtils.isEmpty(cookie)){
            return getEmployeeByCode(code);
        }else {
//            Object obj = redisTemplate.boundValueOps(cookie).get();
            Employee obj = findById(cookie);
            if(obj == null){
                return getEmployeeByCode(code);
            }else {
//                Employee employee = (Employee) redisTemplate.boundValueOps(cookie).get();
//                redisTemplate.expire(cookie, 24, TimeUnit.HOURS);
                return obj;
            }
        }
    }

    @Override
    public List<Employee> queryByName(String keyword) {
        return employeeRepo.queryByName(keyword);
    }

    @Override
    public Employee findById(String unique) {
        Optional<Employee> optional = employeeRepo.findById(new ObjectId(unique));
        return optional.orElse(null);
    }

    @Override
    public Follow follow(Employee from, Employee target) {
        Follow follow = followRepo.findByEmployeeAndTarget(from.getId().toString(), target.getId().toString());
        if(follow != null){
            return follow;
        }
        follow = new Follow(from.getId().toString(), target.getId().toString(), System.currentTimeMillis());
        follow = followRepo.save(follow);
        if(!follow.getTarget().equals(follow.getEmployee())){
            Company company = companyRepo.findById(new ObjectId(target.getCorpId())).orElse(null);
            String time = DateUtils.formatDate(new Date(), "yyyy年MM月dd日");
            String description = "<div class=\"gray\">" + time + "</div> <div class=\"highlight\">有新的用户关注了你</div>";
            messageService.sendMessageToUser(follow.getTarget(), company.getCorpId(), company.getPermanentCode(), company.getAgentId(), "关注通知", description, redirectUrl, null, null, null, null);
        }
        return follow;
    }

    @Override
    public void unFollow(String unique) {
        followRepo.deleteById(new ObjectId(unique));
    }

    @Override
    public List<Follow> getFollows(String employeeId) {
        return followRepo.findByEmployee(employeeId);
    }

    @Override
    public String getAuth2Url() {
        return OAuth2Util.getOAuth2Url(authUrl, appId, redirectUrl);
    }

    @Override
    public String getAccessToken(String corpId) {
        Company company = companyRepo.findByCorpId(corpId);
        return corpService.getCorpToken(corpId, company.getPermanentCode());
    }

    @Override
    public Follow getFollow(String employeeId, String target) {
        return followRepo.findByEmployeeAndTarget(employeeId, target);
    }

    @Override
    public List<Employee> queryUser(String keyword, String companyId) {
        String accessToken = corpService.refreshProviderAccessToken();
        String queryUrl = searchUrl + "?provider_access_token=%s";
        queryUrl = String.format(queryUrl, corpService.refreshProviderAccessToken());
        JSONObject params = new JSONObject();
        Company company = companyRepo.findById(new ObjectId(companyId)).orElse(null);
        params.put("provider_access_token", accessToken);
        params.put("auth_corpid", company.getCorpId());
        params.put("query_word", keyword);
        params.put("query_type", 1);
        params.put("agentid", company.getAgentId());
        params.put("offset", 0);
        params.put("limit", 200);
        String result = null;
        try {
            result = HttpRequestUtil.sendPost(queryUrl, params.toString());
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("用户搜索失败" + e.getMessage());
        }
        JSONObject json = new JSONObject(result);
        logger.info("用户搜索返回数据" + result);
        if(json.getInt("errcode") == 0){
            List<Employee> employeeList = new ArrayList<>();
            boolean isLast = json.getBoolean("is_last");
            JSONObject queryResult = json.getJSONObject("query_result");
            if(queryResult.has("user")){
                JSONObject user = queryResult.getJSONObject("user");
                JSONArray userIds = user.getJSONArray("userid");
                for (Object userId : userIds){
                    employeeList.add(employeeRepo.findByUserIdAndCorpId(userId.toString(), companyId));
                }
            }
            return employeeList;
        }else {
            corpService.disableProviderAccessToken();
            queryUser(keyword, companyId);
        }
        return null;
    }

    private Employee getEmployeeByCode(String code){
        String accessToken = corpService.refreshSuiteToken();
        Employee employee = getUserId(accessToken, code);
        if(employee == null){
            return null;
        }
        Company company = companyRepo.findById(new ObjectId(employee.getCorpId())).orElse(null);
        employee = getUserInfo3rd(corpService.getCorpToken(company.getCorpId(), company.getPermanentCode()), employee);
        employee = employeeRepo.save(employee);
        return employee;
    }

    /**
     * 获取用户身份
     * @param accessToken
     * @param code
     * @return
     */
    private Employee getUserId(String accessToken, String code){
        String url = getUserInfoUrl + "?suite_access_token=" + accessToken + "&code=" + code;
        String response = HttpUtil.get(restTemplate, url, String.class);
        JSONObject resObj = new JSONObject(response);
        System.out.println("code==" + code + "===result=" + resObj);
        int errCode = resObj.getInt("errcode");
        if(errCode == 0){
            if(resObj.has("UserId")) {
                String userId = resObj.getString("UserId");
                String corpId = resObj.getString("CorpId");
                Company company = companyRepo.findByCorpId(corpId);
                Employee employee = employeeRepo.findByUserIdAndCorpId(userId, company.getId().toString());
                if(employee == null){
                    employee = new Employee();
                    employee.setUserId(userId);
                    employee.setCorpId(company.getId().toString());
                    employee.setOpenId(resObj.getString("open_userid"));
                }
                employee = getUserInfo(accessToken, resObj.getString("user_ticket"), employee);
                return employee;
            }else {
                logger.error("解析用户id信息失败");
            }
        }else if(errCode == 40014){
            logger.error("accessToken过期，重新获取后再次尝试获取用户id");
            return getUserId(corpService.refreshSuiteToken(), code);
        }else {
            logger.error("获取用户id失败，errcode：" + errCode + "，errmsg：" + resObj.getString("errmsg"));
        }
        return null;
    }

    /**
     * 根据用户id获取用户信息
     * @param accessToken
     * @return
     */
    private Employee getUserInfo3rd(String accessToken, Employee employee){
        String url = getUserDetail3rdUrl + "?access_token=" + accessToken + "&userid=" + employee.getUserId();
        String response = HttpUtil.get(restTemplate, url, String.class);
        JSONObject resObj = new JSONObject(response);
        int errCode = resObj.getInt("errcode");
        if(errCode == 0){
            employee.setAddress(resObj.has("address") ? resObj.get("address")+"" : null);
//            employee.setAvatar(resObj.getString("avatar"));
//            employee.setEmail(resObj.getString("email"));
//            employee.setEnable(resObj.getInt("enable"));
            employee.setGender(resObj.getString("gender"));
//            employee.setMobile(resObj.getString("mobile"));
//            employee.setName(resObj.getString("name"));
            employee.setPosition(resObj.has("external_position") ? resObj.getString("external_position") : null);
            employee.setStatus(resObj.getInt("status"));
//            employee.setTelephone(resObj.getString("telephone"));
//            employee.setThumb_avatar(resObj.getString("thumb_avatar"));
            employee.setUserId(resObj.getString("userid"));
            employee.setMainDepartment(resObj.getInt("main_department"));
            return employee;
        }else if(errCode == 40014){
            logger.error("accessToken过期，重新获取后再次尝试获取用户信息");
            return getUserInfo3rd(corpService.refreshSuiteToken(), employee);
        }else {
            logger.error("获取用户信息失败，errcode：" + errCode + ", errmsg: " + resObj.getString("errmsg"));
        }
        return null;
    }

    /**
     * 根据用户id获取用户姓名和头像
     * @param accessToken
     * @param employee
     * @return
     */
    private Employee getUserInfo(String accessToken, String userTicket, Employee employee){
        String url = "https://qyapi.weixin.qq.com/cgi-bin/service/getuserdetail3rd?suite_access_token=" + accessToken;
        JSONObject parmas = new JSONObject();
        parmas.put("user_ticket", userTicket);
        String response = null;
        try {
            response = HttpRequestUtil.sendPost(url, parmas.toString());
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("获取用户敏感信息失败 errmsg:" + e.getMessage());
        }
        JSONObject resObj = new JSONObject(response);
        int errCode = resObj.getInt("errcode");
        if(errCode == 0){
            employee.setAvatar(resObj.has("avatar") ? resObj.getString("avatar") : null);
            employee.setGender(resObj.has("gender") ? resObj.getString("gender") : "0");
            employee.setName(resObj.has("name") ? resObj.getString("name") : null);
            return employee;
        }else {
            logger.info("获取用户敏感信息失败，errcode:" + errCode + ", errmsg:" + resObj.getString("errmsg"));
        }
        return null;
    }

    /**
     * 获取单点登录用户信息
     * @param code
     * @return
     */
    @Override
    public Employee getLoginInfo(String code){
        String url = "https://qyapi.weixin.qq.com/cgi-bin/service/get_login_info" + "?access_token=" + corpService.refreshProviderAccessToken();
        JSONObject params = new JSONObject();
        params.put("auth_code", code);
        String result = null;
        try {
            result = HttpRequestUtil.sendPost(url, params.toString());
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("获取单点登录用户信息失败" + e.getMessage());
        }
        JSONObject json = new JSONObject(result);
        if(json.has("user_info")){
            JSONObject userInfo = json.getJSONObject("user_info");
            JSONObject corpInfo = json.getJSONObject("corp_info");
            String corpId = corpInfo.getString("corpid");
            String userId = userInfo.getString("userid");
            Company company = companyRepo.findByCorpId(corpId);
            Employee employee = employeeRepo.findByUserIdAndCorpId(userId, company.getId().toString());
            if(employee == null){
                employee = new Employee();
                employee.setAvatar(userInfo.getString("avatar"));
                employee.setName(userInfo.getString("name"));
                employee.setCorpId(company.getId().toString());
                employee.setUserId(userId);
                employee = employeeRepo.save(employee);
            }
            return employee;
        }else {
            logger.error("获取单点登录用户信息失败, errmsg:" + json.toString());
            return null;
        }
    }

}
