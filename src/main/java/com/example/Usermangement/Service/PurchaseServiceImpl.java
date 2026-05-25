package com.example.Usermangement.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.Usermangement.Bean.Purchase;
import com.example.Usermangement.Bean.PurchaseEvent;
import com.example.Usermangement.Bean.ServiceItem;
import com.example.Usermangement.Bean.User;
import com.example.Usermangement.Enums.PurchaseStatus;
import com.example.Usermangement.Exceptions.DuplicatePaymentReferenceException;
import com.example.Usermangement.Exceptions.InactiveServicePurchaseException;
import com.example.Usermangement.Exceptions.PurchaseNotFoundException;
import com.example.Usermangement.Exceptions.ServiceNotFoundException;
import com.example.Usermangement.Exceptions.UserNotFoundException;
import com.example.Usermangement.Model.DashboardMetricsResponse;
import com.example.Usermangement.Model.PurchaseCreateRequest;
import com.example.Usermangement.Model.PurchaseEventResponse;
import com.example.Usermangement.Model.PurchaseResponse;
import com.example.Usermangement.Model.PurchaseStatusUpdateRequest;
import com.example.Usermangement.Repository.PurchaseEventRepository;
import com.example.Usermangement.Repository.PurchaseRepository;
import com.example.Usermangement.Repository.ServiceItemRepository;
import com.example.Usermangement.Repository.UserRepository;

@Service
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final PurchaseEventRepository purchaseEventRepository;
    private final UserRepository userRepository;
    private final ServiceItemRepository serviceItemRepository;

    public PurchaseServiceImpl(
        PurchaseRepository purchaseRepository,
        PurchaseEventRepository purchaseEventRepository,
        UserRepository userRepository,
        ServiceItemRepository serviceItemRepository
    ) {
        this.purchaseRepository = purchaseRepository;
        this.purchaseEventRepository = purchaseEventRepository;
        this.userRepository = userRepository;
        this.serviceItemRepository = serviceItemRepository;
    }

    @Override
    public PurchaseResponse create(String email, PurchaseCreateRequest request) {
        return createPurchase(email, request.getServiceId(), request.getPaymentReference(), PurchaseStatus.CREATED);
    }

    @Override
    public PurchaseResponse createPaidPurchase(String email, Long serviceId, String paymentReference) {
        return createPurchase(email, serviceId, paymentReference, PurchaseStatus.SUCCESS);
    }

    private PurchaseResponse createPurchase(String email, Long serviceId, String paymentReference, PurchaseStatus status) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("User not found"));

        ServiceItem service = serviceItemRepository.findById(serviceId)
            .orElseThrow(() -> new ServiceNotFoundException("Service not found"));

        if (!Boolean.TRUE.equals(service.getActive())) {
            throw new InactiveServicePurchaseException("Selected service is inactive");
        }

        if (purchaseRepository.existsByPaymentReference(paymentReference)) {
            throw new DuplicatePaymentReferenceException("Payment reference already exists");
        }

        Purchase purchase = new Purchase();
        purchase.setUser(user);
        purchase.setServiceItem(service);
        purchase.setAmount(service.getPrice());
        purchase.setStatus(status);
        purchase.setPaymentReference(paymentReference);
        purchase.setPurchasedAt(LocalDateTime.now());

        Purchase saved = purchaseRepository.save(purchase);
        recordEvent(saved, status == PurchaseStatus.SUCCESS ? "PURCHASE_PAID" : "PURCHASE_CREATED",
            status == PurchaseStatus.SUCCESS ? "Purchase completed successfully" : "Purchase created successfully");
        return map(saved);
    }

    @Override
    public Page<PurchaseResponse> listMy(
        String email,
        LocalDate from,
        LocalDate to,
        PurchaseStatus status,
        Long serviceId,
        Pageable pageable
    ) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("User not found"));

        Page<Purchase> result = purchaseRepository.findMyPurchases(
            user.getId(),
            toStartOfDay(from),
            toEndOfDay(to),
            status,
            serviceId,
            pageable
        );
        return result.map(this::map);
    }

    @Override
    public PurchaseResponse getMyById(String email, Long purchaseId) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("User not found"));

        Purchase purchase = purchaseRepository.findByIdAndUserId(purchaseId, user.getId())
            .orElseThrow(() -> new PurchaseNotFoundException("Purchase not found"));

        return map(purchase);
    }

    @Override
    public List<PurchaseEventResponse> getMyEvents(String email, Long purchaseId) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("User not found"));

        Purchase purchase = purchaseRepository.findByIdAndUserId(purchaseId, user.getId())
            .orElseThrow(() -> new PurchaseNotFoundException("Purchase not found"));

        return purchaseEventRepository.findByPurchaseIdOrderByCreatedAtDesc(purchase.getId())
            .stream()
            .map(this::mapEvent)
            .toList();
    }

    @Override
    public Page<PurchaseResponse> listAll(
        LocalDate from,
        LocalDate to,
        PurchaseStatus status,
        Long serviceId,
        Pageable pageable
    ) {
        Page<Purchase> result = purchaseRepository.findAllPurchases(
            toStartOfDay(from),
            toEndOfDay(to),
            status,
            serviceId,
            pageable
        );
        return result.map(this::map);
    }

    @Override
    public PurchaseResponse getById(Long purchaseId) {
        Purchase purchase = purchaseRepository.findById(purchaseId)
            .orElseThrow(() -> new PurchaseNotFoundException("Purchase not found"));
        return map(purchase);
    }

    @Override
    public PurchaseResponse updateStatus(Long purchaseId, PurchaseStatusUpdateRequest request) {
        Purchase purchase = purchaseRepository.findById(purchaseId)
            .orElseThrow(() -> new PurchaseNotFoundException("Purchase not found"));

        purchase.setStatus(request.getStatus());
        Purchase saved = purchaseRepository.save(purchase);
        String message = request.getMessage() == null || request.getMessage().isBlank()
            ? "Status updated to " + request.getStatus().name()
            : request.getMessage();
        recordEvent(saved, "PURCHASE_STATUS_UPDATED", message);
        return map(saved);
    }

    @Override
    public DashboardMetricsResponse getAdminMetrics(LocalDate from, LocalDate to) {
        LocalDateTime fromDate = toStartOfDay(from);
        LocalDateTime toDate = toEndOfDay(to);

        DashboardMetricsResponse response = new DashboardMetricsResponse();
        response.setTotalPurchases(purchaseRepository.countAllByDateRange(fromDate, toDate));
        response.setSuccessCount(
            purchaseRepository.countByStatusAndDateRange(PurchaseStatus.SUCCESS, fromDate, toDate)
        );

        BigDecimal totalRevenue = purchaseRepository.sumAmountByStatusAndDateRange(
            PurchaseStatus.SUCCESS,
            fromDate,
            toDate
        );
        response.setTotalRevenue(totalRevenue == null ? BigDecimal.ZERO : totalRevenue);
        return response;
    }

    private void recordEvent(Purchase purchase, String action, String message) {
        PurchaseEvent event = new PurchaseEvent();
        event.setPurchase(purchase);
        event.setStatus(purchase.getStatus());
        event.setAction(action);
        event.setMessage(message);
        event.setCreatedAt(LocalDateTime.now());
        purchaseEventRepository.save(event);
    }

    private PurchaseEventResponse mapEvent(PurchaseEvent event) {
        PurchaseEventResponse response = new PurchaseEventResponse();
        response.setId(event.getId());
        response.setStatus(event.getStatus().name());
        response.setAction(event.getAction());
        response.setMessage(event.getMessage());
        response.setCreatedAt(event.getCreatedAt());
        return response;
    }

    private PurchaseResponse map(Purchase purchase) {
        PurchaseResponse response = new PurchaseResponse();
        response.setPurchaseId(purchase.getId());
        response.setUserId(purchase.getUser().getId());
        response.setUserEmail(purchase.getUser().getEmail());
        response.setServiceId(purchase.getServiceItem().getId());
        response.setServiceCode(purchase.getServiceItem().getCode());
        response.setServiceName(purchase.getServiceItem().getName());
        response.setAmount(purchase.getAmount());
        response.setStatus(purchase.getStatus().name());
        response.setPaymentReference(purchase.getPaymentReference());
        response.setPurchasedAt(purchase.getPurchasedAt());
        return response;
    }

    private LocalDateTime toStartOfDay(LocalDate date) {
        return date == null ? null : date.atStartOfDay();
    }

    private LocalDateTime toEndOfDay(LocalDate date) {
        return date == null ? null : date.plusDays(1).atStartOfDay().minusNanos(1);
    }
}
