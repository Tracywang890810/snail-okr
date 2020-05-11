package com.seblong.okr.services.impl;

import com.seblong.okr.services.CompanyService;
import com.seblong.okr.utils.XmlUtil;
import com.seblong.okr.utils.wx.AesException;
import com.seblong.okr.utils.wx.WXBizMsgCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CompanyServiceImpl implements CompanyService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${snail.okr.wechat.suite_id}")
    private String suite_id;

    @Value("${snail.okr.wechat.suite_token}")
    private String token;

    @Value("${snail.okr.wechat.suite_encodingAesKey}")
    private String encodingAesKey;

    private final String SUITE_TICKET_KEY = "SUITE_TICKET_KEY";

    @Override
    public String getVerify(String msg_signature, String timestamp, String nonce, String echostr) throws AesException {
        WXBizMsgCrypt wxBizMsgCrypt = new WXBizMsgCrypt(token, encodingAesKey, suite_id);
        String result = wxBizMsgCrypt.VerifyURL(msg_signature, timestamp, nonce, echostr);
        return result;
    }

    @Override
    public String refreshData(String msg_signature, String timestamp, String nonce, String postData) throws AesException {
        WXBizMsgCrypt wxBizMsgCrypt = new WXBizMsgCrypt(token, encodingAesKey, suite_id);
        String result = wxBizMsgCrypt.DecryptMsg(msg_signature, timestamp, nonce, postData);
        Map<String, Object> dataMap = XmlUtil.getXmlBodyContext(result);
        if(dataMap.containsKey("InfoType")){
            if("suite_ticket".equals(dataMap.get("InfoType"))){
                redisTemplate.opsForValue().set(SUITE_TICKET_KEY, dataMap.get("SuiteTicket"));
            }
        }
        return "success";
    }
}
