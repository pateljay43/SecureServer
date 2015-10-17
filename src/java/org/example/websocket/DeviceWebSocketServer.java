package org.example.websocket;

import java.util.logging.Level;
import java.util.logging.Logger;
//import javax.faces.bean.ApplicationScoped;
//import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

//@ApplicationScoped
@ServerEndpoint("/actions")
public class DeviceWebSocketServer {

    @OnOpen
    public void open(Session session) {
        // handshake with android
    }

    @OnClose
    public void close(Session session) {
        // remove sessions
    }

    @OnError
    public void onError(Throwable error) {
        Logger.getLogger(DeviceWebSocketServer.class.getName()).log(Level.SEVERE, null, error);
    }

    @OnMessage
    public void handleMessage(String message, Session session) {
        // handle message sent and received to android
    }
}
