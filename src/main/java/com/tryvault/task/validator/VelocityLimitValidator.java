package com.tryvault.task.validator;

import com.tryvault.task.entity.Fund;

import java.util.List;

public interface VelocityLimitValidator {
    boolean validate(List<Fund> existingFunds, Fund fund);

    void setNextValidator(VelocityLimitValidator nextValidatornextValidator);
}
