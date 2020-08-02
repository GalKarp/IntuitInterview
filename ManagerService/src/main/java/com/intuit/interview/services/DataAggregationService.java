package com.intuit.interview.services;

import com.intuit.interview.dao.DataAggregationRepository;
import com.intuit.interview.models.DataAggregation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class DataAggregationService {

    List<String> urls = Arrays.asList("http://localhost:8081/getBlueData","http://localhost:8082/getRedGreenData");

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DataAggregationRepository dataAggregationRepository;


    /**
     * Will build aggregation DB from all services
     * @return
     */
    public void onDemandAggregation(){
        try {
            List<DataAggregation> dataAggregationList = new ArrayList<>();
            for (String url : urls) {
                ResponseEntity<DataAggregation[]> dataAggregationResponseEntity = restTemplate.getForEntity(url, DataAggregation[].class);
                if (dataAggregationResponseEntity.getStatusCode() == HttpStatus.OK) {
                    dataAggregationList.addAll(Arrays.asList(dataAggregationResponseEntity.getBody()));
                }
            }
            dataAggregationRepository.deleteAllInBatch();
            dataAggregationRepository.saveAll(dataAggregationList);
        }catch (Exception e){
            log.error("Unable to fetch data from service",e);
        }
    }
}
