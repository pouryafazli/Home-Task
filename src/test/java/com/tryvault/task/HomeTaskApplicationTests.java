package com.tryvault.task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tryvault.task.Response.FundLoadResponse;
import com.tryvault.task.service.FundService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HomeTaskApplicationTests {

    @Autowired
    private FundService fundService;

    @Test
    public void integrationTest() {
        // Given
        var input = getFile("input.txt");

        // When
        var response = fundService.loadFunds(input);

        // Then
        assertResponsesMatchExpectedResults(response, getFile("output.txt"));
    }

    private void assertResponsesMatchExpectedResults(List<FundLoadResponse> actualResponses, File expectedFile) {
        var expectedResponses = extractsFunds(expectedFile);
        var customerToFundMap = expectedResponses.stream().collect(Collectors.groupingBy(FundLoadResponse::getCustomerId));

        actualResponses.forEach(actualResponse -> {
            var optionalResult = customerToFundMap.get(actualResponse.getCustomerId()).stream()
                    .filter(er -> er.getId().equals(actualResponse.getId()))
                    .findFirst();

            assertTrue(optionalResult.isPresent(), "Could not find the valid response for fund id " + actualResponse.getId());
            assertEquals(optionalResult.get().getCustomerId(), actualResponse.getCustomerId(),
                    "Customer id is not correct for fund id " + actualResponse.getId());
            assertEquals(optionalResult.get().isAccepted(), actualResponse.isAccepted(),
                    "Status is not correct for fund id " + actualResponse.getId());
        });
    }
    private File getFile(String filePath) {
        URL resource = getClass().getClassLoader().getResource(filePath);
        assertNotNull(resource, "File not found: " + filePath);
        return new File(resource.getFile());
    }

    private List<FundLoadResponse> extractsFunds(File input) {
        List<FundLoadResponse> funds = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(input))) {
            String line;
            while ((line = reader.readLine()) != null) {
                funds.add(populateFundObject(line));
            }
        } catch (IOException e) {
            fail("Exception while extracting funds: " + e.getMessage());
        }
        return funds;
    }

    private FundLoadResponse populateFundObject(String fundStr) {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        try {
            return objectMapper.readValue(fundStr, FundLoadResponse.class);
        } catch (JsonProcessingException e) {
            fail("Exception while populating fund object: " + e.getMessage());
            return null;
        }
    }
}
