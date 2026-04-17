package com.example.Usermangement.Api;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Usermangement.Enums.PurchaseStatus;
import com.example.Usermangement.Model.PurchaseCreateRequest;
import com.example.Usermangement.Model.PurchaseEventResponse;
import com.example.Usermangement.Model.PurchaseResponse;
import com.example.Usermangement.Model.PurchaseStatusUpdateRequest;
import com.example.Usermangement.Service.PurchaseService;

import jakarta.validation.Valid;

@RestController
@RequestMapping
public class PurchaseController {

    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @PostMapping("/purchases")
    public ResponseEntity<PurchaseResponse> createPurchase(
        Authentication authentication,
        @Valid @RequestBody PurchaseCreateRequest request
    ) {
        return ResponseEntity.ok(purchaseService.create(authentication.getName(), request));
    }

    @GetMapping("/purchases/me")
    public ResponseEntity<Page<PurchaseResponse>> listMyPurchases(
        Authentication authentication,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
        @RequestParam(required = false) PurchaseStatus status,
        @RequestParam(required = false) Long serviceId,
        Pageable pageable
    ) {
        return ResponseEntity.ok(
            purchaseService.listMy(authentication.getName(), from, to, status, serviceId, pageable)
        );
    }

    @GetMapping("/purchases/me/{id}")
    public ResponseEntity<PurchaseResponse> getMyPurchase(
        Authentication authentication,
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(purchaseService.getMyById(authentication.getName(), id));
    }

    @GetMapping("/purchases/me/{id}/events")
    public ResponseEntity<List<PurchaseEventResponse>> getMyPurchaseEvents(
        Authentication authentication,
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(purchaseService.getMyEvents(authentication.getName(), id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/purchases")
    public ResponseEntity<Page<PurchaseResponse>> listAllPurchases(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
        @RequestParam(required = false) PurchaseStatus status,
        @RequestParam(required = false) Long serviceId,
        Pageable pageable
    ) {
        return ResponseEntity.ok(purchaseService.listAll(from, to, status, serviceId, pageable));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/purchases/{id}")
    public ResponseEntity<PurchaseResponse> getAnyPurchase(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseService.getById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @org.springframework.web.bind.annotation.PatchMapping("/admin/purchases/{id}/status")
    public ResponseEntity<PurchaseResponse> updatePurchaseStatus(
        @PathVariable Long id,
        @Valid @RequestBody PurchaseStatusUpdateRequest request
    ) {
        return ResponseEntity.ok(purchaseService.updateStatus(id, request));
    }
}
