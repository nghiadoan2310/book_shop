package com.java_project.product_service.mapper;

import com.java_project.product_service.dto.request.DistributorRequest;
import com.java_project.product_service.dto.request.ProductRequest;
import com.java_project.product_service.dto.response.DistributorResponse;
import com.java_project.product_service.dto.response.ProductResponse;
import com.java_project.product_service.entity.Distributor;
import com.java_project.product_service.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DistributorMapper {
    @Mapping(target = "imageFile", ignore = true)
    Distributor toDistributor(DistributorRequest request);

    DistributorResponse toDistributorResponse(Distributor distributor);
}
