package com.tryvault.task.validator;

import com.tryvault.task.config.FundConfiguration;
import com.tryvault.task.entity.Fund;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class WeeklyVelocityLimitValidator extends AbstractVelocityLimitValidator {

    private static final Logger log = LoggerFactory.getLogger(WeeklyVelocityLimitValidator.class);

    private final FundConfiguration fundConfiguration;

    @Autowired
    public WeeklyVelocityLimitValidator(FundConfiguration fundConfiguration) {
        this.fundConfiguration = fundConfiguration;
    }

    /**
     * Validates the fund based on weekly Velocity limits.
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

        // Filter funds loaded in the same week as the given fund
        List<Fund> thisWeekLoadedFunds = existingFunds.stream()
                .filter(f -> areInSameWeek(f.getTime(), fund.getTime()))
                .toList();

        // Calculate the total load amount of funds processed this week
        var thisWeekFundsAmount = thisWeekLoadedFunds.stream().mapToDouble(Fund::getLoadAmount).sum();
        // Check if adding the load amount of the given fund exceeds the maximum weekly load amount
        if((thisWeekFundsAmount + fund.getLoadAmount()) > fundConfiguration.getMaxWeeklyLoadAmount()){
            log.warn("Validation failed: Fund {} exceeds maximum weekly fund load amount ({}).",
                    fund.getFundId().getId(), fundConfiguration.getMaxWeeklyLoadAmount());
            return false;
        }
        // Continue validation with the next validator in the chain
        return callNextValidator(existingFunds,fund);
    }

    private static boolean areInSameWeek(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        // Use the ISO week definition and English locale for consistent week calculations
        WeekFields weekFields = WeekFields.of(Locale.ENGLISH);

        // Extract week and year from each date time
        int week1 = dateTime1.get(weekFields.weekOfWeekBasedYear());
        int year1 = dateTime1.get(ChronoField.YEAR);

        int week2 = dateTime2.get(weekFields.weekOfWeekBasedYear());
        int year2 = dateTime2.get(ChronoField.YEAR);

        return week1 == week2 && year1 == year2;
    }
}
