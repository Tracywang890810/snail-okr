package com.seblong.okr.services;

import com.seblong.okr.utils.wx.AesException;

public interface CompanyService {
    String getVerify(String msg_signature, String timestamp, String nonce, String echostr) throws AesException;

    String refreshData(String msg_signature, String timestamp, String nonce, String postData) throws AesException;
}
