package com.seblong.okr.services;

import com.seblong.okr.utils.wx.AesException;

import java.util.Map;

public interface CorpService {
    String getVerify(String msg_signature, String timestamp, String nonce, String echostr) throws AesException;

    String refreshData(String msg_signature, String timestamp, String nonce, String postData) throws AesException;

    String refreshProviderAccessToken();

    void disableProviderAccessToken();

    String refreshSuiteToken();

    String getCorpToken(String authCorpId, String permanentCode);

    String getJSAPICorpTicket(String authCorpId, String permanentCode);

    String getJSAPIAgentTicket(String authCorpId);

    Map<String, Object> jsSign(String url, String authCorpId, String permanentCode, long timestamp, String type);
}
