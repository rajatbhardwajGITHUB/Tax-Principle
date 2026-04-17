package com.example.Usermangement.Model;

import com.example.Usermangement.Enums.PurchaseStatus;

import jakarta.validation.constraints.NotNull;

public class PurchaseStatusUpdateRequest {

    @NotNull
    private PurchaseStatus status;

    private String message;

    public PurchaseStatus getStatus() {
        return status;
    }

    public void setStatus(PurchaseStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
