package com.seblong.okr.services;

import com.seblong.okr.utils.wx.AesException;

public interface CorpService {
    String getVerify(String msg_signature, String timestamp, String nonce, String echostr) throws AesException;

    String refreshData(String msg_signature, String timestamp, String nonce, String postData) throws AesException;

    String refreshProviderAccessToken();

    String refreshSuiteToken();

    String getCorpToken(String authCorpId, String permanentCode);
}
