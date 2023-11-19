package com.tryvault.task.validator;

import com.tryvault.task.entity.Fund;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FundValidationProcessor {

    private final VelocityLimitValidator velocityLimitValidator;

    @Autowired
    public FundValidationProcessor(DailyVelocityLimitValidator dailyVelocityLimitValidator,
                                   WeeklyVelocityLimitValidator weeklyVelocityLimitValidator) {

        // Creating a chain of fund validators to execute in a specific order
        // Setting the next validator for daily fund validation to be the weekly fund validator
        dailyVelocityLimitValidator.setNextValidator(weeklyVelocityLimitValidator);

        // Setting the entry point of the validation chain to be the daily fund validator
        velocityLimitValidator = dailyVelocityLimitValidator;
    }

    public boolean isValid(List<Fund> existingFunds, Fund fund) {
        return velocityLimitValidator.validate(existingFunds, fund);
    }
}
