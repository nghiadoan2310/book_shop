package com.java_project.notification_service.service;

import com.java_project.notification_service.dto.request.EmailRequest;
import com.java_project.notification_service.dto.request.SendEmailRequest;
import com.java_project.notification_service.dto.request.Sender;
import com.java_project.notification_service.dto.response.EmailResponse;
import com.java_project.notification_service.exception.AppException;
import com.java_project.notification_service.exception.ErrorCode;
import com.java_project.notification_service.repository.httpClient.EmailClient;
import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailService {
    EmailClient emailClient;

    //String apiKey="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";
    @Value("${notification.email.brevo-apikey}")
    @NonFinal
    String apiKey;

    public EmailResponse sendEmail(SendEmailRequest request) {
        EmailRequest emailRequest = EmailRequest.builder()
                .sender(Sender.builder()
                        .name("bookShop")
                        .email("nghiadoan23102002@gmail.com")
                        .build())
                .to(List.of(request.getTo()))
                .subject(request.getSubject())
                .htmlContent(request.getHtmlContent())
                .build();

        try {
            return emailClient.sendEmail(apiKey, emailRequest);
        } catch (FeignException e){
            throw new AppException(ErrorCode.CANNOT_SEND_EMAIL);
        }
    }
}
