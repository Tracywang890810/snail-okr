package com.seblong.okr.controllers;

import com.seblong.okr.utils.wx.AesException;
import com.seblong.okr.utils.wx.WXBizMsgCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/employee", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public class APIEmployeeController {

    protected static final Logger logger = LoggerFactory.getLogger(APIEmployeeController.class);

    @RequestMapping(value = "/sync", produces = MediaType.APPLICATION_JSON_VALUE)
    public String sync(
            @RequestParam(value = "msg_signature") String msg_signature,
            @RequestParam(value = "timestamp") String timestamp,
            @RequestParam(value = "nonce") String nonce,
            @RequestParam(value = "echostr", required = false) String echostr,
            @RequestBody(required = false) String postData
    ){
        logger.debug("msg_signature:" + msg_signature);
        logger.debug("timestamp:" + timestamp);
        logger.debug("nonce:" + nonce);
        logger.debug("echostr:" + echostr);
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
}
