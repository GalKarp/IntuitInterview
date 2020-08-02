package com.intuit.interview.models;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;


@Entity
@Data
public class DataAggregation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonProperty("CaseID")
    private Integer caseID;
    @JsonProperty("CustomerID")
    private Integer customerID;
    @JsonProperty("Provider")
    private Integer provider;
    @JsonProperty("CREATED_ERROR_CODE")
    private Integer createdErrorCode;
    @JsonProperty("STATUS")
    private String status;
    @JsonDeserialize(using = FixDateSerializer.class)
    @JsonProperty("TICKET_CREATION_DATE")
    private LocalDateTime ticketCreationDate;
    @JsonDeserialize(using = FixDateSerializer.class)
    @JsonProperty("LAST_MODIFIED_DATE")
    private LocalDateTime lastModifiedDate;
    @JsonProperty("PRODUCT_NAME")
    private String productName;

}