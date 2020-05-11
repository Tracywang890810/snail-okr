package com.seblong.okr.controllers;

import com.seblong.okr.services.CompanyService;
import com.seblong.okr.utils.wx.AesException;
import com.seblong.okr.utils.wx.WXBizMsgCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/company", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public class APICompanyController {

    protected static final Logger logger = LoggerFactory.getLogger(APICompanyController.class);

    @Autowired
    private CompanyService companyService;

    /**
     *
     * @param msg_signature
     * @param timestamp
     * @param nonce
     * @param echostr
     * @param corpId
     * @param agentId
     * @param encrypt
     * @return
     */
    @RequestMapping(value = "/ticket", produces = MediaType.APPLICATION_JSON_VALUE)
    public String ticket(
            @RequestParam(value = "msg_signature") String msg_signature,
            @RequestParam(value = "timestamp") String timestamp,
            @RequestParam(value = "nonce") String nonce,
            @RequestParam(value = "echostr", required = false) String echostr,
            @RequestParam(value = "ToUserName", required = false) String corpId,
            @RequestParam(value = "AgentID", required = false) String agentId,
            @RequestParam(value = "Encrypt", required = false) String encrypt,
            @RequestBody(required = false) String postData
    ){
        logger.debug("msg_signature:" + msg_signature);
        logger.debug("timestamp:" + timestamp);
        logger.debug("nonce:" + nonce);
        logger.debug("echostr:" + echostr);
        logger.debug("ToUserName:" + corpId);
        logger.debug("AgentID:" + agentId);
        logger.debug("Encrypt:" + encrypt);
        try {

            if(!StringUtils.isEmpty(echostr)){
                String result = companyService.getVerify(msg_signature, timestamp, nonce, echostr);
                return result;
            }else {
                logger.info(postData);
                String result = companyService.refreshData(msg_signature, timestamp, nonce, postData);
                logger.info(result);
                return "success";
            }

        } catch (AesException e) {
            logger.error(e.getCode() + "====" + e.getMessage());
            e.printStackTrace();
            return e.getMessage();
        }
    }


}