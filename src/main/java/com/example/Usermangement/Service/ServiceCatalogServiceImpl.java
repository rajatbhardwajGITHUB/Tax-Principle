package com.example.Usermangement.Service;

import java.time.LocalDateTime;
import java.math.BigDecimal;

import com.example.Usermangement.Exceptions.ServiceNotFoundException;


import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.example.Usermangement.Bean.ServiceItem;
import com.example.Usermangement.Exceptions.ServiceAlreadyExistsException;
import com.example.Usermangement.Model.ServiceCreateRequest;
import com.example.Usermangement.Model.ServiceResponse;
import com.example.Usermangement.Model.ServiceUpdateRequest;
import com.example.Usermangement.Repository.ServiceItemRepository;

@Service
public class ServiceCatalogServiceImpl implements ServiceCatalogService{

    private final ServiceItemRepository repo;

    public ServiceCatalogServiceImpl(ServiceItemRepository repo){

        this.repo = repo;
    }

    @Override
    public ServiceResponse create(ServiceCreateRequest request) {
        // TODO Auto-generated method stub
        if(repo.findByCode(request.getCode()).isPresent()){
            throw new ServiceAlreadyExistsException("Service Already Exists");
        }

         LocalDateTime date =  LocalDateTime.now();

        ServiceItem item = new ServiceItem();
        item.setCode(request.getCode());
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setPrice(request.getPrice());
        item.setCreatedAt(date);
        item.setUpdatedAt(date);
        item.setActive(request.getActive());
        ServiceItem saved = repo.save(item);
        return map(saved);
    }

    @Override
    public ServiceResponse getById(Long id) {
        if(id == null){
            throw new IllegalArgumentException("Id cannot be null");
        }
        ServiceItem item = repo.findById(id).orElseThrow(() -> new ServiceNotFoundException("Service not found"));
        return map(item);
    }

    @Override
    public Page<ServiceResponse> listActive(Pageable pageable) {
        Page<ServiceItem> result = repo.findByActiveTrue(pageable);
        if(result.isEmpty()){
            throw new ServiceNotFoundException("No Service Present");
        }
        return result.map(this::map);
    }

    @Override
    public void setActive(Long id, boolean active) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        ServiceItem item = repo.findById(id)
                .orElseThrow(() -> new ServiceNotFoundException("Service not found"));
        item.setActive(active);
        item.setUpdatedAt(LocalDateTime.now());
        repo.save(item);
    }

    @Override
    public ServiceResponse update(Long id, ServiceUpdateRequest request) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        ServiceItem item = repo.findById(id)
                .orElseThrow(() -> new ServiceNotFoundException("Service not found"));
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setPrice(new BigDecimal(request.getPrice()));
        if (request.getActive() != null) {
            item.setActive(request.getActive());
        }
        item.setUpdatedAt(LocalDateTime.now());
        ServiceItem saved = repo.save(item);
        return map(saved);
    }

    @Override
    public ServiceResponse getByCode(String code){
        return null;
    }

    private ServiceResponse map(ServiceItem item) {
    ServiceResponse res = new ServiceResponse();
    res.setId(item.getId());

    res.setCode(item.getCode());
    res.setName(item.getName());
    res.setDescription(item.getDescription());
    res.setPrice(item.getPrice());
    res.setActive(item.getActive());
    return res;
}

@Override
public Page<ServiceResponse> listAll(Pageable pageable){
    return repo.findAll(pageable).map(this::map);
}

}
