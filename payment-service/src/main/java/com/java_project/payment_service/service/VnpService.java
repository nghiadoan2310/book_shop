package com.java_project.payment_service.service;

import com.java_project.payment_service.config.VnpConfig;
import com.java_project.payment_service.dto.response.VnpResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VnpService {
    VnpConfig vnpConfig;

    @NonFinal
    @Value("${vnp.secretKey}")
    String secretKey;

    @NonFinal
    @Value("${vnp.vnp_PayUrl}")
    String vnp_PayUrl;

    public VnpResponse createVnpPayment(HttpServletRequest request) {
        long amount = Integer.parseInt(request.getParameter("amount"))* 100L;
        String bankCode = request.getParameter("bankCode");

        Map<String, String> vnpParamsMap = vnpConfig.getVnpConfig();

        vnpParamsMap.put("vnp_Amount", String.valueOf(amount));

        if (bankCode != null && !bankCode.isEmpty()) {
            vnpParamsMap.put("vnp_BankCode", bankCode);
        }

        String locate = request.getParameter("language");
        if (locate != null && !locate.isEmpty()) {
            vnpParamsMap.put("vnp_Locale", locate);
        } else {
            vnpParamsMap.put("vnp_Locale", "vn");
        }

        vnpParamsMap.put("vnp_IpAddr", vnpConfig.getIpAddress(request));

        String queryUrl = vnpConfig.getVnpUrl(vnpParamsMap, true);
        String hashData = vnpConfig.getVnpUrl(vnpParamsMap, false);

        String vnp_SecureHash = vnpConfig.hmacSHA512(secretKey, hashData);
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String vnpUrl = vnp_PayUrl + "?" + queryUrl;

        return VnpResponse.builder()
                .code("ok")
                .message("success")
                .vnpUrl(vnpUrl)
                .build();
    }
}
