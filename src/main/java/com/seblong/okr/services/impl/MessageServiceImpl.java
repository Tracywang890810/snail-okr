package com.seblong.okr.services.impl;

import com.seblong.okr.entities.Company;
import com.seblong.okr.repositories.CompanyRepository;
import com.seblong.okr.services.CorpService;
import com.seblong.okr.services.MessageService;
import com.seblong.okr.utils.HttpRequestUtil;
import com.seblong.okr.utils.HttpUtil;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class MessageServiceImpl implements MessageService {

    private static final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CorpService corpService;


    @Value("${snail.okr.wechat.url.send_message}")
    private String sendMessageUrl;

    /**
     * 给用户发送消息
     * @param receiveId  成员ID列表（消息接收者，多个接收者用‘|’分隔，最多支持1000个）。特殊情况：指定为@all，则向关注该企业应用的全部成员发送  必须
     * @param title  标题，不超过128个字节，超过会自动截断（支持id转译）必须
     * @param description  描述，不超过512个字节，超过会自动截断（支持id转译） 必须
     * @param url  点击后跳转的链接。 必须
     * @param cropId  企业id, 必须
     * @param agentId 企业安装的应用id, 必须
     * @param permanentCode 企业永久授权码, 必须
     * @param btntxt  按钮文字。 默认为“详情”， 不超过4个文字，超过自动截断。
     * @param enableIdTrans 表示是否开启id转译，0表示否，1表示是，默认0
     * @param enableDuplicate 表示是否开启重复消息检查，0表示否，1表示是，默认0
     * @param duplicateCheckInterval 表示是否重复消息检查的时间间隔，默认1800s，最大不超过4小时
     */
    @Override
    public void sendMessageToUser(String receiveId, String cropId, String permanentCode, int agentId, String title, String description, String url, String btntxt, Integer enableIdTrans, Integer enableDuplicate, Integer duplicateCheckInterval){
        String accessToken = corpService.getCorpToken(cropId, permanentCode);
        JSONObject params = new JSONObject();
//        params.put("access_token", accessToken);
        params.put("touser", receiveId);
        params.put("msgtype", "textcard");
        params.put("agentid", agentId);
        Map<String, Object> textCard = new HashMap<>(4);
        textCard.put("title", title);
        textCard.put("description", description);
        textCard.put("url", url);
        if(!StringUtils.isEmpty(btntxt)){
            textCard.put("btntxt", btntxt);
        }
        params.put("textcard", textCard);
        if(enableIdTrans != null){
            params.put("enable_id_trans", enableIdTrans);
        }
        if(enableDuplicate != null){
            params.put("enable_duplicate", enableDuplicate);
            params.put("duplicate_check_interval", duplicateCheckInterval);
        }
        String result = null;
        try {
            result = HttpRequestUtil.sendPost(sendMessageUrl + "?access_token=" + accessToken, params.toString());
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("给用户发送消息失败，errmsg：" + e.getMessage());
        }
        JSONObject json = new JSONObject(result);
        if(json.getInt("errcode") != 0){
            logger.error("给用户发送消息失败，errcode：" + json.getInt("errcode") + ", errmes:" + json.getString("errmsg"));
        }else {
            logger.info("给用户发送消息完成，receiveId:" + receiveId + ", title:" + title);
        }
    }
}
