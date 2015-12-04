package org.example.websocket;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.json.JSONObject;

@ServerEndpoint("/actions")
public class DeviceWebSocketServer {

    private DatabaseHandler db;

    public DeviceWebSocketServer() {
//        db = new DatabaseHandler("root", "123456");
    }

    @OnOpen
    public void open(Session session) {
        // handshake with android
        System.out.println("Session: " + session.getId());
        System.out.println("Secure: " + session.isSecure());
        System.out.println("Protocol: " + session.getNegotiatedSubprotocol());
    }

    @OnClose
    public void close(Session session) {
        // remove sessions
        System.out.println("Closing: " + session.getId());
    }

    @OnError
    public void onError(Throwable error) {
        Logger.getLogger(DeviceWebSocketServer.class.getName()).log(Level.SEVERE, null, error);
    }

    @OnMessage
    public void handleMessage(String message, Session session) {
        if (true) {
            return;
        }
// handle message sent and received to android
        JSONObject json = new JSONObject(message);
        Iterator<String> jsonKeys = json.keySet().iterator();
        int limit = 3;
        while (limit-- != 0 && jsonKeys.hasNext()) {
            String command = jsonKeys.next();
            if (command.equals(Constants.verify)) {
                JSONObject data = json.getJSONObject(command);
                if (db.verify(data.getString(Constants.id), data.getString(Constants.hp))) {   // user verified
                    String pubkey = db.getRSAKeys()[0];
                    BigInteger[] dhB = db.generateDHSeckey(session.getId());    // [g,p,dhB]
                    JSONObject replyData = new JSONObject()
                            .put(Constants.publickey, pubkey)
                            .put(Constants.g, dhB[0])
                            .put(Constants.p, dhB[1])
                            .put(Constants.dh, dhB[2]);
                    reply(session, new JSONObject()
                            .put(Constants.verify, replyData)
                            .toString());
                } else {  // user not verified
                    close(session);
                }
            } else if (command.equals("get")) {

            } else if (command.equals("post")) {

            } else if (command.equals(Constants.dh)) {  // session key established
                db.generateDHkey(session.getId(),
                        new BigInteger(db.decryptRSA((json.get(Constants.dh).toString().getBytes())))
                );
            } else if (command.equals(Constants.sign_up)) {
                JSONObject data = json.getJSONObject(command);
                if (!db.addNewUser(data.getString(Constants.id), (String) data.get(Constants.hp))) {
                    reply(session, new JSONObject().put(Constants.error, "").toString());
//                    close(session);
                }
            }
        }
//        DatabaseHandler db = DatabaseHandler.getInstance("root", "123456");
//        while(jsonKeys.hasNext()){
//            String name = jsonKeys.next();
//            if(name.equals("GET")){     // get -> salt,server public key,
//                
//            }else if(name.equals("STORE")){     // store public key
//                JSONObject pubkey = new JSONObject(json.getString(name));
////                db.insertClientPublicKey(, name);
//            }else if(name.equals("LOGIN/REGISTER")){  // login verification OR register if not verified
//                
//            }
//            json.getString(name);
//        }
//        try {
//            
//            String pubkey = json.getString("publickey");
////            DatabaseHandler db = DatabaseHandler.getInstance("root", "123456");
//            
//        } catch (JSONException ex) {
//            ex.printStackTrace();
//            return;
//        }
//        System.out.println("From Session: " + session.getId());
//        System.out.println("\tMessage: " + message);
    }

    private void reply(Session session, String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException ex) {
            Logger.getLogger(DeviceWebSocketServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
