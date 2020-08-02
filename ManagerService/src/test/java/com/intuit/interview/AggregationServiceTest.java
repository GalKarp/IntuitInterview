package com.intuit.interview;

import com.intuit.interview.controllers.DataAggregationController;
import com.intuit.interview.dao.DataAggregationRepository;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = AggregationService.class)
@AutoConfigureMockMvc
public class AggregationServiceTest {

	@Value( "${coolDownTimeInMinutes}")
	private Integer coolDownTimeInMinutes;

	@Autowired
	private MockMvc mvc;

	//Testing product search
	@Test
	@Order(1)
	public void productSearchTest()
			throws Exception {

		mvc.perform(get("/myAggregatedHub")
				.param("product","RED")
				.param("product","BLUE")
				.param("provider", "12" )
				.param("errorCode", "101")
				.param("costumerId", "831071")
				.param("caseStatus", "Closed")
				.param("startDate", "2017-03-17 03:41:00")
				.param("endDate", "2020-03-20 03:41:00")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(content().json("[\n" +
						"  {\n" +
						"    \"errorCode\": \"101\",\n" +
						"    \"openCase\": \"1\",\n" +
						"    \"listOfCases\": \"{customerId:831071, ticket creation date:2019-03-21 07:31:00}\",\n" +
						"    \"productName\": \"BLUE\",\n" +
						"    \"provider\": \"12\"\n" +
						"  }\n" +
						"]"));
	}

	//Testing rate limit interval
	@Test
	@Order(2)
	public void refreshIntervalTest() throws Exception {
		mvc.perform(get("/myAggregatedHub")
				.param("product","RED")
				.param("product","BLUE")
				.param("provider", "12" )
				.param("errorCode", "101")
				.param("costumerId", "831071")
				.param("caseStatus", "Closed")
				.param("startDate", "2017-03-17 03:41:00")
				.param("endDate", "2020-03-20 03:41:00")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isLocked())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(content().json("[]"));
		Thread.sleep(60*coolDownTimeInMinutes);
		mvc.perform(get("/myAggregatedHub")
				.param("product","RED")
				.param("product","BLUE")
				.param("provider", "12" )
				.param("errorCode", "101")
				.param("costumerId", "831071")
				.param("caseStatus", "Closed")
				.param("startDate", "2017-03-17 03:41:00")
				.param("endDate", "2020-03-20 03:41:00")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(content().json("[\n" +
						"  {\n" +
						"    \"errorCode\": \"101\",\n" +
						"    \"openCase\": \"1\",\n" +
						"    \"listOfCases\": \"{customerId:831071, ticket creation date:2019-03-21 07:31:00}\",\n" +
						"    \"productName\": \"BLUE\",\n" +
						"    \"provider\": \"12\"\n" +
						"  }\n" +
						"]"));
	}
}
