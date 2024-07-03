package com.fabiolucenaribas.dynamodb.service;

public interface SQSService {
    void sendMessage(String msg);
}
