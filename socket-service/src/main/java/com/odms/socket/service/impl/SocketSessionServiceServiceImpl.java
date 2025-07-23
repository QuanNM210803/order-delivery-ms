package com.odms.socket.service.impl;

import com.odms.socket.entity.SocketSession;
import com.odms.socket.repository.SocketSessionRepository;
import com.odms.socket.service.ISocketSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SocketSessionServiceServiceImpl implements ISocketSessionService {
    private final SocketSessionRepository socketSessionRepository;

    @Override
    public SocketSession createSocketSession(SocketSession socketSession) {
        return socketSessionRepository.save(socketSession);
    }

    @Override
    public void deleteSocketSession(String socketSessionId) {
        socketSessionRepository.deleteBySocketSessionId(socketSessionId);
    }

    @Override
    public Map<String, SocketSession> getSocketSessionByUserId(Integer userId) {
        return socketSessionRepository.findAllByUserId(userId)
                .stream()
                .collect(Collectors.toMap(SocketSession::getSocketSessionId, Function.identity()));
    }
}
