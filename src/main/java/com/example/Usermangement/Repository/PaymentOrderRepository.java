package com.example.Usermangement.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Usermangement.Bean.PaymentOrder;

public interface PaymentOrderRepository extends JpaRepository<PaymentOrder, Long> {

    Optional<PaymentOrder> findByGatewayOrderIdAndUserId(String gatewayOrderId, Long userId);

    Optional<PaymentOrder> findByGatewayPaymentIdAndUserId(String gatewayPaymentId, Long userId);
}
