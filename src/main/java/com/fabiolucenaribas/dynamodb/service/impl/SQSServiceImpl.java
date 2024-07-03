package com.fabiolucenaribas.dynamodb.service.impl;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fabiolucenaribas.dynamodb.service.SQSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SQSServiceImpl implements SQSService {

    @Autowired
    private AmazonSQS sqsClient;

    @Value("${sqs.queue}")
    private String queueUrl;

    @Override
    public void sendMessage(String msg) {
        SendMessageRequest send_msg_request = new SendMessageRequest()
                .withQueueUrl(queueUrl)
                .withMessageBody(msg)
                .withDelaySeconds(5);

        sqsClient.sendMessage(send_msg_request);
    }
}
