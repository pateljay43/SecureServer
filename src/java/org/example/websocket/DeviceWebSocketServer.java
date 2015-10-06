package org.example.websocket;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ApplicationScoped
@ServerEndpoint("/actions")
public class DeviceWebSocketServer {

    @Inject
    private DeviceSessionHandler sessionHandler = new DeviceSessionHandler();

    @OnOpen
    public void open(Session session) {
        System.out.println("Connection Opened: ");
        System.out.println("\tSession Id: " + session.getId());
        System.out.println("\tProtocol: " + session.getNegotiatedSubprotocol());
        System.out.println("\tProtocol version: " + session.getProtocolVersion());
        System.out.println("\tQuery String: " + session.getQueryString());
        System.out.println("\tSession as String: " + session);
//        sessionHandler.addSession(session);
        try {
            String message = "123abc";
            session.getBasicRemote().sendText(message);
        } catch (IOException ex) {
            Logger.getLogger(DeviceWebSocketServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @OnClose
    public void close(Session session) {
        sessionHandler.removeSession(session);
    }

    @OnError
    public void onError(Throwable error) {
        Logger.getLogger(DeviceWebSocketServer.class.getName()).log(Level.SEVERE, null, error);
    }

    @OnMessage
    public void handleMessage(String message, Session session) {
        System.out.println("Server received: " + message);
//        try (JsonReader reader = Json.createReader(new StringReader(message))) {
//            JsonObject jsonMessage = reader.readObject();
//
//            if ("add".equals(jsonMessage.getString("action"))) {
//                Device device = new Device();
//                device.setName(jsonMessage.getString("name"));
//                device.setDescription(jsonMessage.getString("description"));
//                device.setType(jsonMessage.getString("type"));
//                device.setStatus("Off");
//                sessionHandler.addDevice(device);
//            }
//
//            if ("remove".equals(jsonMessage.getString("action"))) {
//                int id = (int) jsonMessage.getInt("id");
//                sessionHandler.removeDevice(id);
//            }
//
//            if ("toggle".equals(jsonMessage.getString("action"))) {
//                int id = (int) jsonMessage.getInt("id");
//                sessionHandler.toggleDevice(id);
//            }
//        }
    }
}
