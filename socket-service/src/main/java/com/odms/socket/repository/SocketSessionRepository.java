package com.odms.socket.repository;

import com.odms.socket.entity.SocketSession;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface SocketSessionRepository extends MongoRepository<SocketSession, String> {

    void deleteBySocketSessionId(String socketSessionId);

    List<SocketSession> findAllByUserId(Integer userId);
}
