package com.intuit.interview.controllers;

import com.intuit.interview.CaseStatus;
import com.intuit.interview.Product;
import com.intuit.interview.dao.DataAggregationRepository;
import com.intuit.interview.projections.DataAggregationProjection;
import com.intuit.interview.services.DataAggregationService;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class DataAggregationController {

    @Value( "${coolDownTimeInMinutes}")
    private Integer coolDownTimeInMinutes;

    private LocalDateTime lastAggregationTime = LocalDateTime.now();
    private Boolean crmQueried = false;

    @Autowired
    private DataAggregationRepository dataAggregationRepository;

    @Autowired
    private DataAggregationService dataAggregationService;

    @GetMapping("/myAggregatedHub")
    public ResponseEntity<List<DataAggregationProjection>> getMyAggregatedHub(@ApiParam(value = "List of products, multi-select supported", required = true) @RequestParam List<Product> product,
                                                                              @ApiParam(value = "Provider", required = true)@RequestParam String provider,
                                                                              @ApiParam(value = "Error code", required = true)@RequestParam String errorCode,
                                                                              @ApiParam(value = "Customer ID", required = true)@RequestParam String costumerId,
                                                                              @ApiParam(value = "Case Status (Open / Closed )", required = true)@RequestParam CaseStatus caseStatus,
                                                                              @ApiParam(value = "Start date to search yyyy-MM-dd HH:mm:ss", required = true)@RequestParam String startDate,
                                                                              @ApiParam(value = "End date to search yyyy-MM-dd HH:mm:ss", required = true)@RequestParam String endDate) {

        long duration = Duration.between(lastAggregationTime, LocalDateTime.now()).toMinutes();
        if(crmQueried && duration < coolDownTimeInMinutes){
            log.info("Cool-down period reached, "+ (coolDownTimeInMinutes  - duration) + "minutes left" );
            return new ResponseEntity<>(new LinkedList<>(), HttpStatus.LOCKED);
        }else {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("[yyyy-MM-dd HH:mm:ss]");
                log.info("Going to fetch data from CRM tools");
                dataAggregationService.onDemandAggregation();
                List<DataAggregationProjection> objectList = dataAggregationRepository.aggregateData(product.stream().map(Enum::name).collect(Collectors.toList()),
                        provider,
                        errorCode,
                        costumerId,
                        caseStatus.name(),
                        LocalDateTime.parse(startDate, formatter),
                        LocalDateTime.parse(endDate, formatter));
                changeAggregationTime();
                return new ResponseEntity<>(objectList, HttpStatus.OK);
            }catch (Exception ex){
                log.error("Unable to fetch data");
                return new ResponseEntity<>(new LinkedList<>(), HttpStatus.NOT_FOUND);
            }
        }
    }

    private synchronized void changeAggregationTime() {
        crmQueried = true;
        lastAggregationTime = LocalDateTime.now();
    }
}
