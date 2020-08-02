package com.intuit.interview.projections;

import com.intuit.interview.models.DataAggregation;
import org.springframework.data.rest.core.config.Projection;

@Projection(types = DataAggregation.class)
public interface DataAggregationProjection {
     String getErrorCode();
     String getProvider();
     String getProductName();
     String getOpenCase();
     String getListOfCases();


}
