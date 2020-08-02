package com.intuit.interview.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.interview.controllers.DataAggregationController;
import com.intuit.interview.dao.DataAggregationRepository;
import com.intuit.interview.models.DataAggregation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DataAggregationService {

    private LocalDateTime lastAggregationTime = LocalDateTime.now();

    @Value( "${rateLimit}")
    private Integer rateLimit;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DataAggregationRepository dataAggregationRepository;

    @Autowired
    private DataAggregationController dataAggregationController;

    @Cacheable("dataAggregation")
    public List<DataAggregation> onDemandAggregation(){
        log.info("Going to fetch new data from CRM");
        List response = restTemplate.getForObject("https://fakebanky.herokuapp.com/fruit/banana", ArrayList.class);
        if( response != null && response.size() > 0){
            ObjectMapper objectMapper = new ObjectMapper();
            List<DataAggregation> dataAggregations = objectMapper.convertValue(response.get(0),new TypeReference<List<DataAggregation>>(){});
            List<DataAggregation> filteredData = dataAggregations.stream().filter(dataAggregation ->
                    dataAggregation.getProductName().equals("BLUE")).collect(Collectors.toList());
            //Will save data on blue table
            dataAggregationRepository.saveAll(filteredData);
            return filteredData;
        }
        return new LinkedList<>();
    }

    /**
     * Will check if query rate limit reached and if yes will clear cache
     */
    public synchronized void changeAggregationTime() {
        long duration = Duration.between(lastAggregationTime, LocalDateTime.now()).toMinutes();
        if(duration >= rateLimit) dataAggregationController.rateLimitEviction();
        lastAggregationTime = LocalDateTime.now();
    }
}
