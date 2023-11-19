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

class WeeklyFundValidatorTest {

    @Mock
    private FundConfiguration fundConfiguration;

    @InjectMocks
    WeeklyVelocityLimitValidator weeklyFundValidator;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        when(fundConfiguration.getMaxWeeklyLoadAmount()).thenReturn(20000);
    }

    /**
     * Test that a fund with a load amount within the maximum weekly load amount is considered valid.
     */
    @Test
    void testMaximumWeeklyLoadFund() {
        Fund fund = getFund(3000);
        var result = weeklyFundValidator.validate(new ArrayList<>(), fund);
        assertTrue(result);
    }

    /**
     * Test that adding a fund to existing funds would not exceed the maximum number of weekly loads, resulting in validity.
     */
    @Test
    void testExceedingMaximumWeeklyLoadFund() {
        Fund fund1 = getFund(30);
        Fund fund2 = getFund(40.4);
        Fund fund3 = getFund(34.1);
        Fund fund4 = getFund(25);
        var result = weeklyFundValidator.validate(List.of(fund1, fund2, fund3), fund4);
        assertTrue(result);
    }

    /**
     * Test that adding a fund to existing funds would exceed the maximum weekly load amount, resulting in invalidation.
     */
    @Test
    public void testExceedingMaximumWeeklyLoadFundAmount() {
        Fund fund1 = getFund(15000);
        Fund fund2 = getFund(5000);
        Fund fund3 = getFund(4000);
        var result = weeklyFundValidator.validate(List.of(fund1, fund2), fund3);
        assertFalse(result);
    }

    /**
     * Test that a fund with a load amount exceeding the maximum weekly load amount is considered invalid.
     */
    @Test
    void testInvalidMaximumWeeklyLoadFund() {
        Fund fund = getFund(20001);
        fund.setTime(LocalDateTime.now());
        var result = weeklyFundValidator.validate(new ArrayList<>(), fund);
        assertFalse(result);
    }

    /**
     * Helper method to create a Fund with a random FundId, load amount, and current timestamp.
     */
    private static Fund getFund(double loadAmount) {
        Fund fund = new Fund();
        fund.setFundId(new FundId(new Random().nextLong(), 1L));
        fund.setLoadAmount(loadAmount);
        fund.setTime(LocalDateTime.now());
        return fund;
    }
}
