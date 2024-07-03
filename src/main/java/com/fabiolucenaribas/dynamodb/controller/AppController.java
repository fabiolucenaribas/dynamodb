package com.fabiolucenaribas.dynamodb.controller;

import com.fabiolucenaribas.dynamodb.dto.CostumerDTO;
import com.fabiolucenaribas.dynamodb.model.Costumer;
import com.fabiolucenaribas.dynamodb.service.CostumerService;
import jakarta.validation.Valid;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1")
public class AppController {

    private final CostumerService costumerService;

    public AppController(CostumerService costumerService) {
        this.costumerService = costumerService;
    }

    @PostMapping("costumer")
    public ResponseEntity<Costumer> newCostumer(@Valid @RequestBody CostumerDTO costumerDTO) {
        return ResponseEntity.ok(costumerService.saveCostumer(costumerDTO));
    }

    @GetMapping("costumer")
    public ResponseEntity<List<Costumer>> findCostumerByName(@Param("companyName") String companyName) {
        return ResponseEntity.ok(costumerService.findByCompanyName(companyName));
    }

    @GetMapping("costumer/all")
    public ResponseEntity<List<Costumer>> allCostumers() {
        return ResponseEntity.ok(costumerService.findAllCostumers());
    }

    @PutMapping("costumer/{companyDocumentNumber}")
    public ResponseEntity<Costumer> updateCostumer(
            @PathVariable("companyDocumentNumber") String companyDocumentNumber,
            @Valid @RequestBody CostumerDTO costumerDTO
    ) {
        return ResponseEntity.ok(costumerService.updateCostumer(companyDocumentNumber, costumerDTO));
    }

    @DeleteMapping("costumer/{companyDocumentNumber}")
    public ResponseEntity<Costumer> disableCostumer(@PathVariable("companyDocumentNumber") String companyDocumentNumber) {
        return ResponseEntity.ok(costumerService.disableCostumer(companyDocumentNumber));
    }

    @DeleteMapping("costumer/{companyDocumentNumber}/async")
    public ResponseEntity<Void> disableCostumerAsync(@PathVariable("companyDocumentNumber") String companyDocumentNumber) {
        costumerService.disableCostumerAsync(companyDocumentNumber);
        return ResponseEntity.ok().build();
    }
}