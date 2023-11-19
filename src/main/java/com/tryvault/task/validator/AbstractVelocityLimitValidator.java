package com.tryvault.task.validator;

import com.tryvault.task.entity.Fund;

import java.util.List;

public abstract class AbstractVelocityLimitValidator implements VelocityLimitValidator {
    private VelocityLimitValidator nextValidator;

    @Override
    public void setNextValidator(VelocityLimitValidator nextValidator) {
        this.nextValidator = nextValidator;
    }

    protected boolean callNextValidator(List<Fund> existingFunds, Fund fund) {
        if (nextValidator != null) {
            return nextValidator.validate(existingFunds, fund);
        }
        return true; // No more validator in the chain
    }
}
