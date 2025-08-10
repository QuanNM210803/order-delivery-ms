package com.odms.socket.repository;

import com.odms.socket.entity.SocketSession;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SocketSessionRepository extends MongoRepository<SocketSession, String> {

    void deleteBySocketSessionId(String socketSessionId);

    List<SocketSession> findAllByUserId(Long userId);
}
