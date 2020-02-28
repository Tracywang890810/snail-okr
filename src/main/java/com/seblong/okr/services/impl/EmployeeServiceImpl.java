package com.seblong.okr.services.impl;

import com.seblong.okr.entities.Employee;
import com.seblong.okr.entities.Follow;
import com.seblong.okr.repositories.EmployeeRepository;
import com.seblong.okr.repositories.FollowRepository;
import com.seblong.okr.services.EmployeeService;
import com.seblong.okr.utils.OAuth2Util;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepo;

    @Autowired
    private FollowRepository followRepo;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${snail.okr.wechat.appid}")
    private String appId;

    @Value("${snail.okr.wechat.secret}")
    private String secret;

    @Value("${snail.okr.wechat.redirectUri}")
    private String redirectUri;

    @Value("${snail.okr.wechat.authUrl}")
    private String authUrl;

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
        return optional == null ? null : optional.get();
    }

    @Override
    public void follow(Employee from, Employee target) {
        Follow follow = followRepo.findByEmployeeAndTarget(from.getId().toString(), target.getId().toString());
        if(follow != null){
            return;
        }
        follow = new Follow(from.getId().toString(), target.getId().toString(), System.currentTimeMillis());
        follow = followRepo.save(follow);
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
        return OAuth2Util.getOAuth2Url(authUrl, appId, redirectUri);
    }

    @Override
    public String getAccessToken() {
        return OAuth2Util.getAccessToken(restTemplate, redisTemplate, appId, secret);
    }

    private Employee getEmployeeByCode(String code){
        String accessToken = OAuth2Util.getAccessToken(restTemplate, redisTemplate, appId, secret);
        String employeeId = OAuth2Util.getUserId(restTemplate, redisTemplate, accessToken, code, appId, secret);
        Employee employee = OAuth2Util.getUserInfo(restTemplate, redisTemplate, accessToken, employeeId, appId, secret);
        if(employee != null){
            Employee employeeO = employeeRepo.findByUserId(employeeId);
            if(employeeO != null){
                employeeO.setThumb_avatar(employee.getThumb_avatar());
                employeeO.setTelephone(employee.getTelephone());
                employeeO.setStatus(employee.getStatus());
                employeeO.setPosition(employee.getPosition());
                employeeO.setName(employee.getName());
                employeeO.setMobile(employee.getMobile());
                employeeO.setGender(employee.getGender());
                employeeO.setEnable(employee.getEnable());
                employeeO.setEmail(employee.getEmail());
                employeeO.setAvatar(employee.getAvatar());
                employeeO.setAddress(employee.getAddress());
                employeeO = employeeRepo.save(employeeO);
//                redisTemplate.boundValueOps(employeeO.getId().toString()).set(employeeO, 24, TimeUnit.HOURS);
                return employeeO;
            }else {
                employee = employeeRepo.save(employee);
//                redisTemplate.boundValueOps(employeeO.getId().toString()).set(employee, 24, TimeUnit.HOURS);
            }
        }
        return employee;
    }

}
