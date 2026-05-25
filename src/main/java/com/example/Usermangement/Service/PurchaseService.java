package com.example.Usermangement.Service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.Usermangement.Enums.PurchaseStatus;
import com.example.Usermangement.Model.DashboardMetricsResponse;
import com.example.Usermangement.Model.PurchaseCreateRequest;
import com.example.Usermangement.Model.PurchaseEventResponse;
import com.example.Usermangement.Model.PurchaseResponse;
import com.example.Usermangement.Model.PurchaseStatusUpdateRequest;

public interface PurchaseService {

    PurchaseResponse create(String email, PurchaseCreateRequest request);

    PurchaseResponse createPaidPurchase(String email, Long serviceId, String paymentReference);

    Page<PurchaseResponse> listMy(
        String email,
        LocalDate from,
        LocalDate to,
        PurchaseStatus status,
        Long serviceId,
        Pageable pageable
    );

    PurchaseResponse getMyById(String email, Long purchaseId);

    List<PurchaseEventResponse> getMyEvents(String email, Long purchaseId);

    Page<PurchaseResponse> listAll(
        LocalDate from,
        LocalDate to,
        PurchaseStatus status,
        Long serviceId,
        Pageable pageable
    );

    PurchaseResponse getById(Long purchaseId);

    PurchaseResponse updateStatus(Long purchaseId, PurchaseStatusUpdateRequest request);

    DashboardMetricsResponse getAdminMetrics(LocalDate from, LocalDate to);
}
