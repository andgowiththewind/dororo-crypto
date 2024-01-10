package com.dororo.future.dororocrypto.websocket;

import cn.hutool.core.lang.Console;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.dororo.future.dororocrypto.service.StatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 加解密WebSocket
 * <p>用于加解密过程中的进度通知</p>
 * <p>采用一对多的模式:一个`CryptoWebSocket`负责整个页面的多个更新;</p>
 *
 * @author Dororo
 * @date 2024-01-10 12:41
 */
@Slf4j
@Component
@ServerEndpoint("/cryptoWebSocket/{sessionId}") // 接口路径`ws://localhost:8080/cryptoWebSocket/{sessionId}`
public class CryptoWebSocket {
    /**
     * 一个页面维护一个WebSocket连接
     */
    private Session session;
    /**
     * 页面唯一标识
     */
    private String sessionId;
    /**
     * 会话池,用于存储所有的WebSocket连接,以sessionId为key,session为value
     */
    private static final Map<String, Session> SESSION_POOL = new ConcurrentHashMap<>();

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("sessionId") String sessionId) {
        this.session = session;
        this.sessionId = sessionId;
        SESSION_POOL.put(sessionId, session);
        log.debug("[CRYPTO WEBSOCKET]-连接加入:[ID={}],[当前在线连接数={}]", sessionId, SESSION_POOL.size());
    }

    @OnClose
    public void onClose(Session session) {
        // 从会话池中移除
        SESSION_POOL.remove(sessionId);
        log.debug("[CRYPTO WEBSOCKET]-连接关闭:[ID={}],[当前在线连接数={}]", sessionId, SESSION_POOL.size());
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.debug(StrUtil.format("[CRYPTO WEBSOCKET]连接异常:[ID={}]", sessionId), error);
    }

    /**
     * 接收客户端的消息
     */
    @OnMessage
    public void onMessage(Session session, String message) {
        log.debug("[CRYPTO WEBSOCKET]收到客户端[ID={}]的消息:{}", sessionId, message);

        StatService statService = SpringUtil.getBean(StatService.class);
        statService.onMessage(sessionId, message);
    }


    /**
     * 静态工具类方法：发送单点消息
     *
     * <p>加解密过程中也通过调静态方法实时向页面发生或广播信息;</p>
     *
     * @param sessionId 客户端的sessionId,根据这个sessionId在SESSION_POOL中找到对应的Session,然后发送消息
     * @param message   消息内容,JSON格式,如:{"status":1,"message":"加密中","progress":0.5},具体业务具体设计
     */
    public static void sendMessage(String sessionId, String message) {
        Session session = SESSION_POOL.get(sessionId);
        if (session != null) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                log.debug(StrUtil.format("[CRYPTO WEBSOCKET]发送消息异常:[ID={}]", sessionId), e);
            }
        }
    }

    /**
     * 发送多点消息
     */
    public static void sendMessage(Set<String> sessionIdSet, String message) {
        sessionIdSet.forEach(sessionId -> sendMessage(sessionId, message));
    }


    /**
     * 全局广播
     * <p>加解密过程中也通过调静态方法实时向页面发生或广播信息;</p>
     *
     * @param message 消息内容,JSON格式,如:{"status":1,"message":"加密中","progress":0.5},具体业务具体设计
     */
    public static void broadcast(String message) {
        if (SESSION_POOL.size() > 0) {
            SESSION_POOL.forEach((sessionId, session) -> {
                try {
                    session.getBasicRemote().sendText(message);
                } catch (Exception e) {
                    log.debug(StrUtil.format("[CRYPTO WEBSOCKET]广播消息异常:[ID={}]", sessionId), e);
                }
            });
        }
    }
}
