package com.tryvault.task.Response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class FundLoadResponse {
    @JsonProperty
    private String id;
    @JsonProperty("customer_id")
    private String customerId;
    @JsonProperty
    private boolean accepted;


    @Override
    public String toString(){
        return "{\"id\":\"" + id + "\",\"customer_id\":\"" + customerId + "\",\"accepted\":" + accepted + "}";

    }
}
