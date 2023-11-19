package com.tryvault.task.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class Fund {
    @JsonCreator
    public static Fund create(@JsonProperty("id") String id,
                              @JsonProperty("customer_id") String customerId,
                              @JsonProperty("load_amount") String loadAmount,
                              @JsonProperty("time") LocalDateTime time) {
        Fund fund = new Fund();
        fund.setFundId(new FundId(Long.parseLong(id), Long.parseLong(customerId)));
        fund.setLoadAmount(Double.parseDouble(loadAmount));
        fund.setTime(time);
        return fund;
    }
    @EmbeddedId
    private FundId fundId;

    @NotNull(message = "amount cannot be null")
    @PositiveOrZero(message = "amount must be a positive or zero value")
    @JsonProperty("load_amount")
    private double loadAmount;

    @NotNull(message = "time cannot be null")
    private LocalDateTime time;
}

