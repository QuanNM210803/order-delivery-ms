package com.odms.socket.controller;

import com.corundumstudio.socketio.SocketIOServer;
import com.odms.socket.entity.SocketSession;
import com.odms.socket.service.ISocketSessionService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import nmquan.commonlib.utils.ObjectMapperUtils;
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
        Object object = ObjectMapperUtils.convertToObject(message, Object.class);

        if(object instanceof Map<?,?> map){
            Boolean status = (Boolean) map.get("status");
            Long userId = Long.valueOf(map.get("userId").toString());
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
