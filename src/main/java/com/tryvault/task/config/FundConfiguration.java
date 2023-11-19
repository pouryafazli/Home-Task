package com.tryvault.task.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


@Configuration
@Getter
@Setter
public class FundConfiguration {

    @Value("${fund.maxDailyLoadAmount}")
    private int maxDailyLoadAmount;

    @Value("${fund.maxWeeklyLoadAmount}")
    private int maxWeeklyLoadAmount;

    @Value("${fund.maxDailyLoad}")
    private int maxDailyLoad;

}
