package com.tryvault.task.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class FundId implements Serializable {

    @NotNull(message = "id cannot be null")
    private Long id;

    @NotNull(message = "customerId cannot be null")
    @JsonProperty("customer_id")
    private Long customerId;

}
