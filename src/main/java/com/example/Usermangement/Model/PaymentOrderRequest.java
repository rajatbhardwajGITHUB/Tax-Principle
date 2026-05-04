package com.example.Usermangement.Model;

import jakarta.validation.constraints.NotNull;

public class PaymentOrderRequest {

    @NotNull
    private Long serviceId;

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }
}
