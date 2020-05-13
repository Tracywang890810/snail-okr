package com.seblong.okr.services;

public interface MessageService {
    void sendMessageToUser(String receiveId, String cropId, String permanentCode, int agentId, String title, String description, String url, String btntxt, Integer enableIdTrans, Integer enableDuplicate, Integer duplicateCheckInterval);
}
