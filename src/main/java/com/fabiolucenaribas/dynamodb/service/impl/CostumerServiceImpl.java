package com.fabiolucenaribas.dynamodb.service.impl;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.fabiolucenaribas.dynamodb.dto.CostumerDTO;
import com.fabiolucenaribas.dynamodb.model.Costumer;
import com.fabiolucenaribas.dynamodb.repository.CostumerRepository;
import com.fabiolucenaribas.dynamodb.service.CostumerService;
import com.fabiolucenaribas.dynamodb.service.SQSService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CostumerServiceImpl implements CostumerService {

    private final AmazonDynamoDB amazonDynamoDB;
    private final CostumerRepository costumerRepository;
    private final SQSService sqsService;

    public CostumerServiceImpl(AmazonDynamoDB amazonDynamoDB, SQSService sqsService, CostumerRepository costumerRepository) {
        this.amazonDynamoDB = amazonDynamoDB;
        this.sqsService = sqsService;
        this.costumerRepository = costumerRepository;
    }

    @Override
    public Costumer saveCostumer(CostumerDTO costumerDTO) {
        if(costumerRepository.findByCompanyDocumentNumber(costumerDTO.getCompanyDocumentNumber()).isPresent()) {
            throw new RuntimeException("There is already a customer with this document number");
        }
        return costumerRepository.save(costumerDTO.costumerDTOToCostumer());
    }

    @Override
    public List<Costumer> findAllCostumers() {
        return (List<Costumer>) costumerRepository.findAll();
    }

    @Override
    public List<Costumer> findByCompanyName(String companyName) {

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterConditionEntry("company_name", new Condition()
                        .withComparisonOperator(ComparisonOperator.EQ)
                        .withAttributeValueList(new AttributeValue(companyName)));

        DynamoDBMapper mapper = new DynamoDBMapper(amazonDynamoDB);

        //return costumerRepository.findByCompanyName(companyName);
        return mapper.scan(Costumer.class,scanExpression);
    }

    @Override
    public Costumer updateCostumer(String companyDocumentNumber, CostumerDTO costumerDTO) {
        Optional<Costumer> costumer =
                costumerRepository.findByCompanyDocumentNumber(companyDocumentNumber);

        if(costumer.isEmpty()) {
            throw new RuntimeException("There is no customer with this document number");
        }

        BeanUtils.copyProperties(costumerDTO, costumer.get(), "active", "id");

        return costumerRepository.save(costumer.get());
    }

    @Override
    public Costumer disableCostumer(String companyDocumentNumber) {
        Optional<Costumer> costumer =
                costumerRepository.findByCompanyDocumentNumber(companyDocumentNumber);

        if(costumer.isEmpty()) {
            throw new RuntimeException("There is no customer with this document number");
        }

        costumer.get().setActive(false);

        return costumerRepository.save(costumer.get());
    }

    @Override
    public void disableCostumerAsync(String companyDocumentNumber) {

        Optional<Costumer> costumer =
                costumerRepository.findByCompanyDocumentNumber(companyDocumentNumber);

        if(costumer.isEmpty()) {
            throw new RuntimeException("There is no customer with this document number");
        }

        sqsService.sendMessage(costumer.get().getId());
    }

}