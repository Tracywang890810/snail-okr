package com.seblong.okr.controllers;

import com.seblong.okr.services.CorpService;
import com.seblong.okr.utils.wx.AesException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/corp", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public class APICorpController {

    protected static final Logger logger = LoggerFactory.getLogger(APICorpController.class);

    @Autowired
    private CorpService corpService;

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
        logger.info("msg_signature:" + msg_signature);
        logger.info("timestamp:" + timestamp);
        logger.info("nonce:" + nonce);
        logger.info("echostr:" + echostr);
        logger.info("ToUserName:" + corpId);
        logger.info("AgentID:" + agentId);
        logger.info("Encrypt:" + encrypt);
        try {

            if(!StringUtils.isEmpty(echostr)){
                String result = corpService.getVerify(msg_signature, timestamp, nonce, echostr);
                return result;
            }else {
                logger.info(postData);
                String result = corpService.refreshData(msg_signature, timestamp, nonce, postData);
                logger.info(result);
                return result;
            }

        } catch (AesException e) {
            logger.error(e.getCode() + "====" + e.getMessage());
            e.printStackTrace();
            return e.getMessage();
        }
    }
}
