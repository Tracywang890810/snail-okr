package com.seblong.okr.services.impl;

import com.seblong.okr.services.CorpService;
import com.seblong.okr.services.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MessageServiceImpl implements MessageService {

    private static final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CorpService corpService;

    @Value("${snail.okr.wechat.url.send_message}")
    private String sendMessageUrl;

    public void sendMessage(String receiveId, String content){

    }
}
