package com.fabiolucenaribas.dynamodb.service;

import com.fabiolucenaribas.dynamodb.dto.CostumerDTO;
import com.fabiolucenaribas.dynamodb.model.Costumer;

import java.util.List;

public interface CostumerService {
    Costumer saveCostumer(CostumerDTO costumerDTO);

    List<Costumer> findAllCostumers();

    List<Costumer> findByCompanyName(String companyName);

    Costumer updateCostumer(String companyDocumentNumber, CostumerDTO costumerDTO);

    Costumer disableCostumer(String companyDocumentNumber);

    void disableCostumerAsync(String companyDocumentNumber);
}