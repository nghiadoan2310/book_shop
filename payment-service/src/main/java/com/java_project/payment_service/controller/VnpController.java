package com.java_project.payment_service.controller;

import com.java_project.payment_service.dto.response.VnpResponse;
import com.java_project.payment_service.service.VnpService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VnpController {
    VnpService vnpService;

    @GetMapping("/vnp-payment")
    VnpResponse vnpPayment(HttpServletRequest request) {
        return vnpService.createVnpPayment(request);
    }

    @GetMapping("/vnp-callback")
    VnpResponse payCallbackHandler(HttpServletRequest request) {
        String status = request.getParameter("vnp_TransactionStatus");
        if (status.equals("00")) {
            return VnpResponse.builder()
                    .code("00")
                    .message("Success")
                    .build();
        }

        return VnpResponse.builder()
                .code("99")
                .message("Failed")
                .build();
    }
}
