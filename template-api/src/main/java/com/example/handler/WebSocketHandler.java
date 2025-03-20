package com.example.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {

    private static final String SESSION_PREFIX = "xinYang_user:";

    private final RedisTemplate<String, String> redisTemplate;

    private final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();


    @Autowired
    public WebSocketHandler(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String query = session.getUri().getQuery();
        log.info("WebSocket 连接是 ID: {}, URI: {}, Query: {}, 当前线程: {}", session.getId(), session.getUri(), query, Thread.currentThread().getName());

        if (query != null && query.contains("uuid=")) {
            String[] queryParams = query.split("=");
            if (queryParams.length > 1) {
                String uuid = queryParams[1];
                String sessionInfo = session.getId() + "," + session.getUri().toString();
                redisTemplate.opsForValue().set(SESSION_PREFIX + uuid, sessionInfo);
                sessionMap.put(session.getId(), session);
                log.info("WebSocket 是用户{}", uuid);
            } else {
                log.warn("Invalid query parameter format: {}", query);
            }
        } else {
            log.warn("Missing uuid parameter in query: {}", query);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 处理接收到的消息
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String query = session.getUri().getQuery();
        if (query != null && query.contains("uuid=")) {
            String[] queryParams = query.split("=");
            if (queryParams.length > 1) {
                String uuid = queryParams[1];
                redisTemplate.delete(SESSION_PREFIX + uuid);
                sessionMap.remove(session.getId());
                log.info("WebSocket 会话关闭，删除 Redis 中的数据: {}", uuid);
            }
        }
    }

    //参考
//    public void sendBookingMessage(String uuid, String message) throws JsonProcessingException {
//        sendMessage(uuid, message, "booking");
//    }


    //回复用户
    public void sendReplyMessage(String receiverId, String message, String messageType) {
        sendMessage(receiverId, message, messageType);
    }

        private void sendMessage(String uuid, String message, String messageType) {
        String sessionInfo = redisTemplate.opsForValue().get(SESSION_PREFIX + uuid);
        if (sessionInfo != null) {
            String[] parts = sessionInfo.split(",");
            String sessionId = parts[0];
            // 根据 sessionId 获取 WebSocketSession 并发送消息
            WebSocketSession session = getSessionById(sessionId);
            if (session != null && session.isOpen()) {
                try {
                    // 将消息类型和消息内容一起发送
                    String combinedMessage = String.format("{\"type\":\"%s\",\"message\":\"%s\"}", messageType, message);
                    session.sendMessage(new TextMessage(combinedMessage));
                    log.info("给 {} {} 消息 {} 发送成功了", uuid, messageType, message);
                } catch (IOException e) {
                    log.error("给 {} {} 消息发送失败了", uuid, messageType, e);
                }
            } else {
                log.warn("给 {} {} 消息发送失败了", uuid, messageType);
            }
        } else {
            log.info("用户{}未登录，把{}消息存到数据库中", uuid, messageType);

        }
    }

    private WebSocketSession getSessionById(String sessionId) {
        return sessionMap.get(sessionId);
    }



}
