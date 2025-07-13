package com.java_project.chat_service.controller;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.java_project.chat_service.dto.request.IntrospectRequest;
import com.java_project.chat_service.service.IdentityService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SocketHandler {
    SocketIOServer socketIOServer;
    IdentityService identityService;

    @OnConnect
    public void clientConnected(SocketIOClient client) {
        var token = client.getHandshakeData().getSingleUrlParam("token");

        var verifyToken = identityService.introspect(IntrospectRequest.builder()
                        .token(token)
                .build());

        if(ObjectUtils.isEmpty(verifyToken)) {
            log.info("Client connected: {}, {}", client.getSessionId(), token);
        } else {
            log.error("Client disconnected: {}, {}", client.getSessionId(), token);
            client.disconnect();
        }
    }

    @OnDisconnect
    public void clientDisconnected(SocketIOClient client) {
        log.error("Client disconnected: {}", client.getSessionId());
        client.disconnect();
    }

    @PostConstruct
    public void startServer() {
        socketIOServer.start();
        socketIOServer.addListeners(this);

        log.info("Socket server started");
    }

    @PreDestroy
    public void stopServer() {
        socketIOServer.stop();
        log.info("Socket server stop");
    }
}
