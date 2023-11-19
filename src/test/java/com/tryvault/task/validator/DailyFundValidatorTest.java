package com.tryvault.task.validator;

import com.tryvault.task.config.FundConfiguration;
import com.tryvault.task.entity.Fund;
import com.tryvault.task.entity.FundId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class DailyFundValidatorTest {
    @Mock
    private FundConfiguration fundConfiguration;

    @InjectMocks
    DailyVelocityLimitValidator dailyFundValidator;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.openMocks(this);
        when(fundConfiguration.getMaxDailyLoad()).thenReturn(3);
        when(fundConfiguration.getMaxDailyLoadAmount()).thenReturn(5000);
    }

    /**
     * Test that a fund with a load amount within the maximum daily load amount is considered valid.
     */
    @Test
    void testMaximumDailyLoadFund() {
        Fund fund = getFund(3000);
        var result = dailyFundValidator.validate(new ArrayList<>(), fund);
        assertTrue(result);
    }

    /**
     * Test that adding a fund to existing funds would exceed the maximum number of daily loads, resulting in invalidation.
     */
    @Test
    void testExceedingMaximumDailyLoadFund() {
        Fund fund1 = getFund(30);
        Fund fund2 = getFund(40.4);
        Fund fund3 = getFund(34.1);
        Fund fund4 = getFund(25);
        var result = dailyFundValidator.validate(List.of(fund1,fund2,fund3), fund4);
        assertFalse(result);
    }

    /**
     * Test that adding a fund to existing funds would exceed the maximum daily load amount, resulting in invalidation.
     */
    @Test
    public void testExceedingMaximumDailyLoadFundAmount() {
        Fund fund1 = getFund(3000);
        Fund fund2 = getFund(500);
        Fund fund3 = getFund(4000);
        var result = dailyFundValidator.validate(List.of(fund1,fund2), fund3);
        assertFalse(result);
    }

    /**
     * Test that a fund with a load amount exceeding the maximum daily load amount is considered invalid.
     */
    @Test
    void testInvalidMaximumDailyLoadFund() {
        Fund fund = getFund(5001);
        fund.setTime(LocalDateTime.now());
        var result = dailyFundValidator.validate(new ArrayList<>(), fund);
        assertFalse(result);
    }


    /**
     * Helper method to create a Fund with a random FundId, load amount, and current timestamp.
     */
    private static Fund getFund(double loadAmount) {
        Fund fund = new Fund();
        fund.setFundId(new FundId(new Random().nextLong(),1L));
        fund.setLoadAmount(loadAmount);
        fund.setTime(LocalDateTime.now());
        return fund;
    }
}