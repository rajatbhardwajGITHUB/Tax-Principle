package com.example.Usermangement.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Usermangement.Bean.PurchaseEvent;

public interface PurchaseEventRepository extends JpaRepository<PurchaseEvent, Long> {

    List<PurchaseEvent> findByPurchaseIdOrderByCreatedAtDesc(Long purchaseId);
}
