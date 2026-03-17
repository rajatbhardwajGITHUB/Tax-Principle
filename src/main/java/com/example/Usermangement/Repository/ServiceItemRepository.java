package com.example.Usermangement.Repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Usermangement.Bean.ServiceItem;

public interface ServiceItemRepository extends JpaRepository<ServiceItem, Long>{

    Optional<ServiceItem> findByCode(String code);

    Page<ServiceItem> findByActiveTrue(Pageable pageable);

}
