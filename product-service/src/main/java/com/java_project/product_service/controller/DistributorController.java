package com.java_project.product_service.controller;

import com.java_project.product_service.dto.ApiResponse;
import com.java_project.product_service.dto.request.DistributorRequest;
import com.java_project.product_service.dto.response.DistributorResponse;
import com.java_project.product_service.service.DistributorService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DistributorController {
    DistributorService distributorService;

    @PostMapping(value = "/create/distributor", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<DistributorResponse> createProduct(@ModelAttribute DistributorRequest request) {
        return ApiResponse.<DistributorResponse>builder()
                .result(distributorService.createDistributor(request))
                .build();
    }
}
