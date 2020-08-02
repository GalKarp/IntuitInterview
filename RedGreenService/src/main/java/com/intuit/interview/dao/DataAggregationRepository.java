package com.intuit.interview.dao;

import com.intuit.interview.models.DataAggregation;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DataAggregationRepository extends JpaRepository<DataAggregation, Long> {
}