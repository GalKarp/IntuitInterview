package com.intuit.interview.controllers;

import com.intuit.interview.dao.DataAggregationRepository;
import com.intuit.interview.models.DataAggregation;
import com.intuit.interview.services.DataAggregationService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@RestController
public class DataAggregationController {


    @Autowired
    private DataAggregationService dataAggregationService;

    @Autowired
    private DataAggregationRepository dataAggregationRepository;

    @ApiOperation(value = "Get all Red and Green product data")
    @GetMapping("/getRedGreenData")
    public ResponseEntity<List<DataAggregation>> getRedGreenData(){
        try {
            log.info("Going to retrieve list of Red and Green products from CRM");
            dataAggregationService.changeAggregationTime();
            return new ResponseEntity<>(dataAggregationService.onDemandAggregation(), HttpStatus.OK);
        }catch (Exception ex){
            log.error("Unable to fetch Blue product data",ex);
            return new ResponseEntity<>(new LinkedList<>(), HttpStatus.NOT_FOUND);
        }
    }

    @CacheEvict("dataAggregation")
    public void rateLimitEviction(){
        log.info("Rate limit refreshed, you can know call cached method again");
    }

    @CacheEvict("dataAggregation")
    @Scheduled(cron = "${refreshDataCron}")
    public void refreshIntervalEviction(){
        log.info("Going to refresh CRM data");
        dataAggregationRepository.deleteAllInBatch();
        dataAggregationService.onDemandAggregation();
    }
}
