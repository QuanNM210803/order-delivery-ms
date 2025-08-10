package com.odms.socket.controller;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.odms.socket.entity.SocketSession;
import com.odms.socket.service.ISocketSessionService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nmquan.commonlib.model.JwtUser;
import nmquan.commonlib.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class SocketHandler {
    private final SocketIOServer server;

    private final ISocketSessionService socketSessionService;

    @Value("${jwt.secret-key}")
    private String SECRET_KEY;

    @OnConnect
    public void onConnect(SocketIOClient client) {
        try{
            String token = client.getHandshakeData().getSingleUrlParam("token");
            JwtUser jwtUser = JwtUtils.validate(token, SECRET_KEY);
            SocketSession socketSession = SocketSession.builder()
                    .socketSessionId(client.getSessionId().toString())
                    .userId(Long.valueOf(jwtUser.getUser().get("id").toString()))
                    .createdAt(Instant.now())
                    .build();
            socketSessionService.createSocketSession(socketSession);
            log.info("Client connected: {}", client.getSessionId());
        } catch (Exception e) {
            log.error("Authentication fail: {}", client.getSessionId());
            client.disconnect();
        }
    }

    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        log.info("Client disconnected: {}", client.getSessionId());
        socketSessionService.deleteSocketSession(client.getSessionId().toString());
    }

    @PostConstruct
    public void startServer() {
        server.start();
        server.addListeners(this);
        log.info("Socket.IO server started on port {}", server.getConfiguration().getPort());
    }

    @PreDestroy
    public void stopServer() {
        server.stop();
        log.info("Socket.IO server stopped");
    }
}
