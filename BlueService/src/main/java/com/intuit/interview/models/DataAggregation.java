package com.intuit.interview.models;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * DB model for storing Blue data from CRMs
 */
@Entity
@Data
public class DataAggregation {

    @Id
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