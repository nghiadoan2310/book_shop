package com.java_project.product_service.service;

import com.java_project.product_service.dto.request.DistributorRequest;
import com.java_project.product_service.dto.response.DistributorResponse;
import com.java_project.product_service.mapper.DistributorMapper;
import com.java_project.product_service.repository.DistributorRepository;
import com.java_project.product_service.repository.httpClient.FileClient;
import com.java_project.product_service.utils.SlugUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DistributorService {
    DistributorRepository distributorRepository;
    FileClient fileClient;

    DistributorMapper distributorMapper;

    public DistributorResponse createDistributor(DistributorRequest request) {
        var distributor = distributorMapper.toDistributor(request);

        distributor.setImageFile(fileClient.uploadFile(request.getImageFile()).getResult().getUrl());
        distributor.setSlug(SlugUtils.toSlug(distributor.getName()));

        distributorRepository.save(distributor);

        return distributorMapper.toDistributorResponse(distributor);
    }
}
