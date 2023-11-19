package com.tryvault.task.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tryvault.task.Response.FundLoadResponse;
import com.tryvault.task.entity.Fund;
import com.tryvault.task.entity.FundId;
import com.tryvault.task.repository.FundRepository;
import com.tryvault.task.validator.FundValidationProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FundService {
    private static final Logger log = LoggerFactory.getLogger(FundService.class);

    private final FundValidationProcessor validator;
    private final FundRepository fundRepository;

    @Autowired
    public FundService(FundRepository fundRepository, FundValidationProcessor validator) {
        this.fundRepository = fundRepository;
        this.validator = validator;
    }

    /**
     * Processes fund loading operations based on the input file, providing a list of FundLoadResponse objects.
     *
     * This method reads funds from the specified input file, processes each fund, and generates FundLoadResponse
     * objects indicating the acceptance or rejection of fund loading operations. The fund loading is subject to
     * certain constraints, including customer-specific limits and existing fund data.
     *
     * After processing, the valid funds are saved to the database.
     *
     * @param input The file containing fund loading information in JSON format.
     * @return A list of FundLoadResponse objects representing the results of the fund loading operations.
     */
    public List<FundLoadResponse> loadFunds(File input) {
        List<FundLoadResponse> responses = new ArrayList<>();

        var funds = extractsFunds(input);
        if (Objects.isNull(funds) || funds.isEmpty()) {
            log.error("input file is empty");
            return responses;
        }
        var customersExistingFund = getExistingFunds(funds);

        Map<Long, Set<Long>> seenIds = new HashMap<>();
        List<Fund> newFunds = new ArrayList<>();

        funds.forEach(newFund -> {
            var customerExistingFunds = customersExistingFund.get(newFund.getFundId().getCustomerId());
            // Check if the fund with the same customer ID and fund ID has not been seen before
            // (seenIds is a map to keep track of seen fund IDs for each customer)
            // If it hasn't been seen or the fund ID is not in the set, proceed with validation
            boolean isValid = (Objects.isNull(seenIds.get(newFund.getFundId().getCustomerId()))
                    || !seenIds.get(newFund.getFundId().getCustomerId()).contains(newFund.getFundId().getId()))
                    && validator.isValid(customerExistingFunds, newFund);
            log.info("Fund ID {} validation status: {}", newFund.getFundId().getId(), isValid ? "Accepted" : "Rejected");
            responses.add(populateResponse(isValid, newFund));
            seenIds.computeIfAbsent(newFund.getFundId().getCustomerId(), k -> new HashSet<>()).add(newFund.getFundId().getId());

            if (isValid) {
                customersExistingFund.computeIfAbsent(newFund.getFundId().getCustomerId(), k -> new ArrayList<>()).add(newFund);
                newFunds.add(newFund);
            }
        });

        log.info("{} funds are saving.", newFunds.size());
        fundRepository.saveAll(newFunds);
        log.info("{} funds saved successfully.", newFunds.size());
        return responses;
    }

    private Map<Long,List<Fund>> getExistingFunds(List<Fund> funds) {
        var customerIds = funds.stream().map(Fund::getFundId)
                .map(FundId::getCustomerId).collect(Collectors.toSet());
        // Assume the earliest date in the input file as the reference point.
        // To perform weekly limit validation, retrieve data from one week before the earliest date.
        var startDate = funds.get(0).getTime().minusWeeks(1);
        var customersExistingFund = fundRepository.findByCustomerIdsAndDate(customerIds, startDate);
        return customersExistingFund.stream().collect(Collectors.groupingBy(fund-> fund.getFundId().getCustomerId()));
    }

    /**
     * Creates a FundLoadResponse object based on the provided validity and Fund details.
     *
     * @param isValid The validity status of the fund load operation.
     * @param newFund The Fund object for which the response is being created.
     * @return A FundLoadResponse object representing the result of the fund load operation.
     */
    private FundLoadResponse populateResponse(boolean isValid, Fund newFund) {
        return new FundLoadResponse(newFund.getFundId().getId().toString(), newFund.getFundId().getCustomerId().toString(), isValid);
    }

    /**
     * Extracts Fund objects from the specified file.
     * This method reads each line from the input file, populates Fund objects, and returns a list
     * containing the extracted Fund objects.
     *
     * @param input The file containing data to extract Fund objects.
     * @return A list of Fund objects extracted from the input file.
     */
    private List<Fund> extractsFunds(File input) {
        List<Fund> funds = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(input))) {
            reader.lines().map(this::populateFundObject).filter(Objects::nonNull).forEach(funds::add);
        } catch (IOException e) {
            log.error("Unable to load input file. error: {}", e.getMessage(), e);
        }
        return funds;
    }

    /**
     * Populates a Fund object from the given JSON string.
     *
     * This method uses Jackson's ObjectMapper to deserialize the JSON string into a Fund object.
     * If the deserialization fails, a warning log is generated, and null is returned.
     *
     * @param fundStr The JSON string representing the Fund object.
     * @return A Fund object populated from the JSON string, or null if deserialization fails.
     */
    private Fund populateFundObject(String fundStr) {
        try {
            //removing $ from load_amount value to be able to convert it ot double
            fundStr = fundStr.replaceAll("\\$", "");
            return new ObjectMapper().registerModule(new JavaTimeModule()).readValue(fundStr, Fund.class);
        } catch (JsonProcessingException e) {
            log.warn("Unable to convert fund string to Fund object. error: {}", e.getMessage(), e);
        }
        return null;
    }
}
