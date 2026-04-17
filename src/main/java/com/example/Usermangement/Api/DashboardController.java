package com.example.Usermangement.Api;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Usermangement.Model.DashboardMetricsResponse;
import com.example.Usermangement.Model.PurchaseResponse;
import com.example.Usermangement.Service.PurchaseService;

@RestController
@RequestMapping
public class DashboardController {

    private final PurchaseService purchaseService;

    public DashboardController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @GetMapping("/dashboard/me/purchases")
    public ResponseEntity<Page<PurchaseResponse>> myDashboardPurchases(
        Authentication authentication,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
        Pageable pageable
    ) {
        return ResponseEntity.ok(
            purchaseService.listMy(authentication.getName(), from, to, null, null, pageable)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/dashboard/purchases")
    public ResponseEntity<Page<PurchaseResponse>> adminDashboardPurchases(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
        Pageable pageable
    ) {
        return ResponseEntity.ok(purchaseService.listAll(from, to, null, null, pageable));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/dashboard/metrics")
    public ResponseEntity<DashboardMetricsResponse> adminDashboardMetrics(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return ResponseEntity.ok(purchaseService.getAdminMetrics(from, to));
    }
}
