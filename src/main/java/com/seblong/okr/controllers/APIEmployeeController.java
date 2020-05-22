package com.seblong.okr.controllers;

import com.seblong.okr.entities.Employee;
import com.seblong.okr.entities.Follow;
import com.seblong.okr.resource.StandardEntityResource;
import com.seblong.okr.resource.StandardListResource;
import com.seblong.okr.resource.StandardRestResource;
import com.seblong.okr.services.CorpService;
import com.seblong.okr.services.EmployeeService;
import com.seblong.okr.utils.wx.AesException;
import com.seblong.okr.utils.wx.WXBizMsgCrypt;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/employee", produces = {MediaType.APPLICATION_JSON_VALUE})
public class APIEmployeeController {

    protected static final Logger logger = LoggerFactory.getLogger(APIEmployeeController.class);

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private CorpService corpService;

    @RequestMapping(value = "/sync", produces = MediaType.APPLICATION_JSON_VALUE)
    public String sync(
            @RequestParam(value = "msg_signature") String msg_signature,
            @RequestParam(value = "timestamp") String timestamp,
            @RequestParam(value = "nonce") String nonce,
            @RequestParam(value = "echostr", required = false) String echostr,
            @RequestBody(required = false) String postData
    ){
        logger.info("msg_signature:" + msg_signature);
        logger.info("timestamp:" + timestamp);
        logger.info("nonce:" + nonce);
        logger.info("echostr:" + echostr);
        try {
            WXBizMsgCrypt wxBizMsgCrypt = new WXBizMsgCrypt("3Ad8eryw6j", "8XTWWruKyy4J9Dh6WRlOLvzbb5yBq7eWmdOyz9M2cAy", "ww8d0e994b898e7746");
            if(!StringUtils.isEmpty(echostr)){
                String result = wxBizMsgCrypt.VerifyURL(msg_signature, timestamp, nonce, echostr);
                logger.debug(result);
                return result;
            }else {
                String result = wxBizMsgCrypt.DecryptMsg(msg_signature, timestamp, nonce, postData);
                logger.info(result);
                return result;
            }

        } catch (AesException e) {
            logger.error(e.getCode() + "====" + e.getMessage());
            e.printStackTrace();
            return e.getMessage();
        }
    }

    /**
     * 获取网页授权链接
     *
     * @return
     */
    @ApiOperation(value = "获取企业微信用户授权链接")
    @GetMapping(value = "/oauth")
    public Map<String, Object> getOAuth2Url() {
        Map<String, Object> rMap = new HashMap<>(4);
        String oauthUrl = employeeService.getAuth2Url();
        rMap.put("status", 200);
        rMap.put("message", "OK");
        rMap.put("oauthUrl", oauthUrl);
        return rMap;
    }

    /**
     * 获取accessToken
     * @return
     */
    @GetMapping(value = "/accessToken")
    public Map<String, Object> getAccessToken(){
        Map<String, Object> rMap = new HashMap<>(4);
        String accessToken = corpService.refreshSuiteToken();
        rMap.put("accessToken", accessToken);
        rMap.put("status", 200);
        rMap.put("message", "OK");
        return rMap;
    }

    /**
     * 根据code或者cookie获取员工信息，相当于登录
     * cookie为员工id
     *
     * @param code
     * @param cookie
     * @return
     */
    @ApiOperation(value = "用户登录接口")
    @ApiImplicitParams(
            value = {@ApiImplicitParam(name = "code", value = "企业微信返回的code", required = false, dataType = "String", paramType = "query"),
                    @ApiImplicitParam(name = "cookie", value = "本地缓存的用户id", required = false, dataType = "String", paramType = "query")}
    )
    @ApiResponses(value = {
            @ApiResponse(code = 406, message = "oauth-error"),
            @ApiResponse(code = 200, message = "OK", response = Employee.class, responseContainer = "用户属性")
    })
    @PostMapping(value = "/login")
    public ResponseEntity<StandardRestResource> getEmployee(
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "cookie", required = false) String cookie) {
        System.out.println("code=" + code);
        System.out.println("cookie=" + cookie);
        Employee employee = employeeService.getEmployee(code, cookie);
        if(employee == null){
            return new ResponseEntity<StandardRestResource>(new StandardRestResource(406, "oauth-error"), HttpStatus.OK);
        }
        return new ResponseEntity<StandardRestResource>(new StandardEntityResource<Employee>(employee), HttpStatus.OK);
    }

    /**
     * 根据用户名前缀模糊查询用户
     * @param keyword
     * @return
     */
    @GetMapping(value = "/query")
    public ResponseEntity<StandardRestResource> query(@RequestParam(value = "keyword") String keyword, @RequestParam(value = "companyId") String companyId) {
        List<Employee> employeeList = employeeService.queryUser(keyword, companyId);
        if (employeeList == null) {
            employeeList = Collections.emptyList();
        }
        return new ResponseEntity<StandardRestResource>(new StandardListResource<Employee>(employeeList), HttpStatus.OK);
    }

    /**
     * 根据用户id获取用户信息
     * @param unique
     * @return
     */
    @GetMapping("/get")
    public ResponseEntity<StandardRestResource> find(
            @RequestParam(value = "unique") String unique){
        Employee employee = employeeService.findById(unique);
        if(employee == null){
            return new ResponseEntity<StandardRestResource>(new StandardRestResource(404, "employee-not-exists"), HttpStatus.OK);
        }else {
            return new ResponseEntity<StandardRestResource>(new StandardEntityResource<Employee>(employee), HttpStatus.OK);
        }
    }

    /**
     * 关注用户
     * @param fromId
     * @param targetId
     * @return
     */
    @PostMapping("/follow")
    public Map<String, Object> follow(
            @RequestParam(value = "from") String fromId,
            @RequestParam(value = "target") String targetId){
        Map<String, Object> rMap = new HashMap<>(4);
        Employee from = employeeService.findById(fromId);
        if(from == null){
            rMap.put("status", 404);
            rMap.put("message", "from-error");
            return rMap;
        }
        Employee target = employeeService.findById(targetId);
        if(target == null){
            rMap.put("status", 404);
            rMap.put("message", "target-error");
            return rMap;
        }
        Follow follow = employeeService.follow(from, target);
        rMap.put("status", 200);
        rMap.put("message", "OK");
        rMap.put("unique", follow.getId().toString());
        return rMap;
    }

    /**
     * 判断是否已关注
     * @param employeeId
     * @param target
     * @return
     */
    @GetMapping("/isfollow")
    public Map<String, Object> isFollow(
            @RequestParam(value = "employee") String employeeId,
            @RequestParam(value = "target") String target
    ){
        Map<String, Object> rMap = new HashMap<>(4);
        Follow follow = employeeService.getFollow(employeeId, target);
        rMap.put("status", 200);
        rMap.put("message", "OK");
        rMap.put("follow", follow != null);
        rMap.put("unique", follow == null ? null : follow.getId().toString());
        return rMap;
    }

    /**
     * 取消关注
     * @param unique
     * @return
     */
    @PostMapping("/unfollow")
    public Map<String, Object> unFollow(
            @RequestParam(value = "unique") String unique){
        Map<String, Object> rMap = new HashMap<>(4);
        employeeService.unFollow(unique);
        rMap.put("status", 200);
        rMap.put("message", "OK");
        return rMap;
    }

    /**
     * 获取用户关注
     * @param employeeId
     * @return
     */
    @GetMapping("/get/follow")
    public ResponseEntity<StandardRestResource> getFollows(
            @RequestParam(value = "employee") String employeeId
    ){
        List<Follow> follows = employeeService.getFollows(employeeId);
        if(follows == null){
            follows = Collections.emptyList();
        }else {
            follows.forEach(follow -> {
                Employee employee = employeeService.findById(follow.getTarget());
                follow.setAvatar(employee.getAvatar());
                follow.setName(employee.getName());
                follow.setThumb_avatar(employee.getThumb_avatar());
            });
        }
        return new ResponseEntity<StandardRestResource>(new StandardListResource<Follow>(follows), HttpStatus.OK);
    }
}
