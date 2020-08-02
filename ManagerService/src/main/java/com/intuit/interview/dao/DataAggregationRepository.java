package com.intuit.interview.dao;

import com.intuit.interview.models.DataAggregation;
import com.intuit.interview.projections.DataAggregationProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;


public interface DataAggregationRepository extends JpaRepository<DataAggregation, Long> {

    @Query(value = "Select CREATED_ERROR_CODE as ErrorCode , PROVIDER as Provider , group_concat(PRODUCT_NAME) as ProductName, COUNT( CASE WHEN status = 'Open' THEN 1 ELSE 0 END) as OpenCase, " +
            "group_concat(   DISTINCT CONCAT('{customerId:',CUSTOMERID,', ticket creation date:',TICKET_CREATION_DATE,'}') ) as listOfCases " +
            "from DATA_AGGREGATION " +
            "WHERE PRODUCT_NAME in :productList " +
            "AND STATUS = :caseStatus " +
            "AND PROVIDER = :provider " +
            "AND CREATED_ERROR_CODE = :errorCode " +
            "AND CUSTOMERID = :costumerId " +
            "AND TICKET_CREATION_DATE between :startDate and :endDate GROUP BY ( Provider , ErrorCode )",nativeQuery = true)
    List<DataAggregationProjection> aggregateData(List<String> productList,
                                                  String provider,
                                                  String errorCode,
                                                  String costumerId,
                                                  String caseStatus,
                                                  LocalDateTime startDate,
                                                  LocalDateTime endDate);
}