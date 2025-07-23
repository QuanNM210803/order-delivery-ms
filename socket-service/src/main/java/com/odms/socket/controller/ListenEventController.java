package com.odms.socket.controller;

import com.corundumstudio.socketio.SocketIOServer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.odms.socket.entity.SocketSession;
import com.odms.socket.service.ISocketSessionService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ListenEventController {
    private final SocketIOServer socketIOServer;
    private final ISocketSessionService socketSessionService;

    @SneakyThrows
    @KafkaListener(topics = "update-find-order-status-topic", groupId = "socket-service")
    void listenUpdateFindOrderStatusEvent(String message) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        Object object = objectMapper.readValue(message, Object.class);

        if(object instanceof Map<?,?> map){
            Boolean status = (Boolean) map.get("status");
            Integer userId = (Integer) map.get("userId");
            Map<String, SocketSession> socketSessionMap = socketSessionService.getSocketSessionByUserId(userId);

            socketIOServer.getAllClients().forEach(client -> {
                var socketSession = socketSessionMap.get(client.getSessionId().toString());
                if(Objects.nonNull(socketSession)){
                    Map<String, Object> dataClient = new HashMap<>();
                    dataClient.put("status", status);
                    client.sendEvent("updateFindOrderStatus", dataClient);
                }
            });
        }
    }
}
