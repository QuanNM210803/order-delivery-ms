package com.odms.socket.service;

import com.odms.socket.entity.SocketSession;

import java.util.Map;

public interface ISocketSessionService {

    SocketSession createSocketSession(SocketSession socketSession);

    void deleteSocketSession(String socketSessionId);

    Map<String, SocketSession> getSocketSessionByUserId(Integer userId);
}
