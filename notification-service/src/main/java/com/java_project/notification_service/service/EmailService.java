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
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailService {
    EmailClient emailClient;

    String apiKey = "your-brevo-apikey";

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
