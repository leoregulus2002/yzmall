package cn.yz.yzmall.websocket;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/websocket/{oid}")
public class WebSocketServer {

    private static ConcurrentHashMap<String, Session> sessionMap = new ConcurrentHashMap<>();
    @OnOpen
    public void open(@PathParam("oid")String orderId, Session session){
        sessionMap.put(orderId,session);
    }

    @OnClose
    public void close(@PathParam("oid")String orderId){
        sessionMap.remove(orderId);
    }

    public static void sendMessage(String orderId,String msg){
        Session session = sessionMap.get(orderId);
        try {
            session.getBasicRemote().sendText(msg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
