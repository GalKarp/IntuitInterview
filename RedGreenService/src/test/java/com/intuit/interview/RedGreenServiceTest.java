package com.intuit.interview;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.interview.controllers.DataAggregationController;
import com.intuit.interview.dao.DataAggregationRepository;
import com.intuit.interview.models.DataAggregation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = RedGreenService.class)
@AutoConfigureMockMvc
public class RedGreenServiceTest {

	@Value( "${rateLimit}")
	private Integer rateLimit;

	@Autowired
	private MockMvc mvc;

	@Autowired
	private DataAggregationRepository dataAggregationRepository;

	@Autowired
	private DataAggregationController dataAggregationController;

	//Testing refresh data interval
	@Test
	public void refreshDataIntervalTest()
			throws Exception {

		DataAggregation dataAggregation = new DataAggregation();
			dataAggregation.setCaseID(12345);
			dataAggregation.setCreatedErrorCode(12345);
			dataAggregation.setCustomerID(12345);
			dataAggregation.setLastModifiedDate(LocalDateTime.now());
			dataAggregation.setProductName("YELLOW");
			dataAggregation.setProvider(12345);
			dataAggregation.setStatus("OPEN");
			dataAggregation.setTicketCreationDate(LocalDateTime.now());

		ObjectMapper mapper = new ObjectMapper();
		//Converting the Object to JSONString
		String jsonString = mapper.writeValueAsString(dataAggregation);
		assertFalse(checkingStringOnRedGreenData(jsonString));
		dataAggregationRepository.save(dataAggregation);
		assertFalse(checkingStringOnRedGreenData(jsonString));
		dataAggregationController.refreshIntervalEviction();
		assertFalse(checkingStringOnRedGreenData(jsonString));
		dataAggregationRepository.delete(dataAggregation);
	}

	private boolean checkingStringOnRedGreenData(String jsonString) throws Exception {
		return mvc.perform(get("/getRedGreenData")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andReturn().getResponse()
				.getContentAsString()
				.contains(jsonString);
	}

	//Testing rate limit interval
	@Test
	public void rateLimitIntervalTest()
			throws Exception {

		gettingRedGreenData();
		dataAggregationRepository.deleteAllInBatch();
		assertEquals(0, dataAggregationRepository.findAll().size());
		Thread.sleep(rateLimit*60*1000);
		gettingRedGreenData();
		assertTrue(dataAggregationRepository.findAll().size() > 0);

	}

	private void gettingRedGreenData() throws Exception {
		mvc.perform(get("/getRedGreenData")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
	}
}
