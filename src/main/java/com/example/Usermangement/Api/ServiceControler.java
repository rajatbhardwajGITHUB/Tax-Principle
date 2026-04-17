package com.example.Usermangement.Api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Usermangement.Repository.ServiceItemRepository;
import com.example.Usermangement.Service.ServiceCatalogService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.Usermangement.Model.ServiceResponse;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.Usermangement.Model.ServiceCreateRequest;

import org.springframework.web.bind.annotation.PutMapping;

import com.example.Usermangement.Model.ServiceUpdateRequest;

import jakarta.validation.Valid;



@RestController
@RequestMapping("/service")
public class ServiceControler {

    private final ServiceItemRepository serviceItemRepository;
    
    @Autowired
    private  ServiceCatalogService serviceCatalogService;

    public ServiceControler(ServiceItemRepository repo){
        this.serviceItemRepository = repo;
    }

    
    /**
     *  gets a service item list from the catalog
     * @param authentication
     * @param pageable
     * @return ServiceResponse
     * 
     * to call this api use the following format - 
     * /service/services?pages=0&size=5
     * page=0 => page 1
     * size=5 => 5 services per page
     */ 
    @GetMapping("/user/services")
    public ResponseEntity<Page<ServiceResponse>> getAllServices(Authentication authentication, Pageable pageable) {
        return ResponseEntity.ok(serviceCatalogService.listActive(pageable));
    }
    
    /**
     *  gets a single item based on the id
     * @param id
     * @return
     */
    @GetMapping("/user/services/{id}")
    public ResponseEntity<ServiceResponse> getService(@PathVariable Long id) {
        return ResponseEntity.ok(serviceCatalogService.getById(id));
    }

    @GetMapping("/admin/services")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ServiceResponse>> getAllAdminServices(Pageable pageable) {
        return ResponseEntity.ok(serviceCatalogService.listAll(pageable));
    }


    /**
     * 
     * @param requestList
     * @return
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/services")
    public ResponseEntity<ServiceResponse> postService(@Valid @RequestBody ServiceCreateRequest requestList) {
        return ResponseEntity.ok(serviceCatalogService.create(requestList));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/services/updateService/{id}")
    public ResponseEntity<ServiceResponse> updateService(@PathVariable Long id, @Valid @RequestBody ServiceUpdateRequest request) {
        return ResponseEntity.ok(serviceCatalogService.update(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/admin/services/updatedStatus/{id}")
    public ResponseEntity<Void> updatedServiceStatus(@PathVariable Long id, @RequestParam boolean status){
        serviceCatalogService.setActive(id, status);
        return ResponseEntity.noContent().build();
    }
    

}
