package com.example.Usermangement.Service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.example.Usermangement.Model.ServiceCreateRequest;
import com.example.Usermangement.Model.ServiceResponse;
import com.example.Usermangement.Model.ServiceUpdateRequest;


public interface ServiceCatalogService {

    ServiceResponse create(ServiceCreateRequest request);

    ServiceResponse update(Long id, ServiceUpdateRequest request);

    Page<ServiceResponse> listActive(Pageable pageable);

    ServiceResponse getById(Long id);

    ServiceResponse getByCode(String code);

    void setActive(Long id, boolean active);
    
}
