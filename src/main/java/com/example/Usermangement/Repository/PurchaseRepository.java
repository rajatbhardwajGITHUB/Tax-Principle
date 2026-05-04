package com.example.Usermangement.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.Usermangement.Bean.Purchase;
import com.example.Usermangement.Enums.PurchaseStatus;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    Optional<Purchase> findByIdAndUserId(Long id, Long userId);

    Optional<Purchase> findByPaymentReference(String paymentReference);

    boolean existsByPaymentReference(String paymentReference);

    @Query("""
        select p from Purchase p
        where p.user.id = :userId
          and (:fromDate is null or p.purchasedAt >= :fromDate)
          and (:toDate is null or p.purchasedAt <= :toDate)
          and (:status is null or p.status = :status)
          and (:serviceId is null or p.serviceItem.id = :serviceId)
        """)
    Page<Purchase> findMyPurchases(
        @Param("userId") Long userId,
        @Param("fromDate") LocalDateTime fromDate,
        @Param("toDate") LocalDateTime toDate,
        @Param("status") PurchaseStatus status,
        @Param("serviceId") Long serviceId,
        Pageable pageable
    );

    @Query("""
        select p from Purchase p
        where (:fromDate is null or p.purchasedAt >= :fromDate)
          and (:toDate is null or p.purchasedAt <= :toDate)
          and (:status is null or p.status = :status)
          and (:serviceId is null or p.serviceItem.id = :serviceId)
        """)
    Page<Purchase> findAllPurchases(
        @Param("fromDate") LocalDateTime fromDate,
        @Param("toDate") LocalDateTime toDate,
        @Param("status") PurchaseStatus status,
        @Param("serviceId") Long serviceId,
        Pageable pageable
    );

    @Query("""
        select count(p) from Purchase p
        where (:fromDate is null or p.purchasedAt >= :fromDate)
          and (:toDate is null or p.purchasedAt <= :toDate)
        """)
    long countAllByDateRange(
        @Param("fromDate") LocalDateTime fromDate,
        @Param("toDate") LocalDateTime toDate
    );

    @Query("""
        select count(p) from Purchase p
        where p.status = :status
          and (:fromDate is null or p.purchasedAt >= :fromDate)
          and (:toDate is null or p.purchasedAt <= :toDate)
        """)
    long countByStatusAndDateRange(
        @Param("status") PurchaseStatus status,
        @Param("fromDate") LocalDateTime fromDate,
        @Param("toDate") LocalDateTime toDate
    );

    @Query("""
        select coalesce(sum(p.amount), 0) from Purchase p
        where p.status = :status
          and (:fromDate is null or p.purchasedAt >= :fromDate)
          and (:toDate is null or p.purchasedAt <= :toDate)
        """)
    java.math.BigDecimal sumAmountByStatusAndDateRange(
        @Param("status") PurchaseStatus status,
        @Param("fromDate") LocalDateTime fromDate,
        @Param("toDate") LocalDateTime toDate
    );
}
