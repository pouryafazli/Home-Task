package com.tryvault.task.validator;

import com.tryvault.task.config.FundConfiguration;
import com.tryvault.task.entity.Fund;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DailyVelocityLimitValidator extends AbstractVelocityLimitValidator {

    private static final Logger log = LoggerFactory.getLogger(DailyVelocityLimitValidator.class);

    private final FundConfiguration fundConfiguration;

    @Autowired
    public DailyVelocityLimitValidator(FundConfiguration fundConfiguration) {
        this.fundConfiguration = fundConfiguration;
    }

    /**
     * Validates the fund based on daily Velocity limits.
     *
     * @param existingFunds The list of existing funds.
     * @param fund The fund to be validated.
     * @return {@code true} if the fund is valid; otherwise, {@code false}.
     */
    @Override
    public boolean validate(List<Fund> existingFunds, Fund fund) {
        if (existingFunds == null) {
            existingFunds = new ArrayList<>();
        }

        // Filter funds created on the same day as the given fund
        List<Fund> todayFunds = existingFunds.stream()
                .filter(f -> isSameDay(fund.getTime(), f.getTime()))
                .toList();

        // Calculate the total load amount of funds processed today
        var todayProcessFundsAmount = todayFunds.stream().mapToDouble(Fund::getLoadAmount).sum();

        if ((todayProcessFundsAmount + fund.getLoadAmount()) > fundConfiguration.getMaxDailyLoadAmount()) {
            log.warn("Validation failed: Fund {} exceeds maximum daily fund load amount ({}).",
                    fund.getFundId().getId(), fundConfiguration.getMaxDailyLoadAmount());
            return false;
        }

        // Check if the number of funds processed today exceeds the maximum daily load limit
        if (todayFunds.size() >= fundConfiguration.getMaxDailyLoad()) {
            log.warn("Validation failed: Fund {} exceeds maximum daily fund loads ({}).",
                    fund.getFundId().getId(), fundConfiguration.getMaxDailyLoad());
            return false;
        }

        // Continue validation with the next validator in the chain
        return callNextValidator(existingFunds, fund);
    }

    private static boolean isSameDay(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        return dateTime1.toLocalDate().isEqual(dateTime2.toLocalDate());
    }
}
