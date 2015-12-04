/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.example.websocket;

import java.math.BigInteger;
import java.util.HashMap;

/**
 *
 * @author JAY
 */
public class SessionHandler {

    private final HashMap<String, BigInteger> sessionList;    // <sessionId,sessionKey>

    public SessionHandler() {
        sessionList = new HashMap<>();
    }

    public final BigInteger addSession(String sessionId, BigInteger sessionKey) {
        return sessionList.putIfAbsent(sessionId, sessionKey);
    }

    public final void removeSession(String sessionId) {
        sessionList.remove(sessionId);
    }

    public final BigInteger getUserName(String sessoinId) {
        return sessionList.getOrDefault(sessoinId, null);
    }

}
